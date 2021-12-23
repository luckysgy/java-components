package com.concise.component.util.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 文件分解工具类
 * 文件合并工具类 {@link FileUnifierUtils}
 * @author shenguangyang
 * @date 2021-10-16 15:42
 */
public class FileSplitterUtils {
    private static final Logger log = LoggerFactory.getLogger(FileSplitterUtils.class);
    
    private final String srcFile, msDstDir, msOutFileBaseName;
    private int partSize = 1024 * 1024 * 10;
    private int miMaxThrPoolSize = 2;
    /**
     * 是否运行小于分片大小
     * 如果为false, 最后一片会与前一片合并在一起, 否则不合并
     */
    private boolean isAllowLtPartSize = true;
    private ThreadPoolExecutor moThrPool;

    private FileInputStream moInput;
    private FileChannel moInChannel;

    /**
     * Constructor
     *
     * @param srcFile 源文件
     * @param dstDir 目标文件
     * @param psOutFileBaseName the output files will be named by
     */
    public FileSplitterUtils(String srcFile, String dstDir, String psOutFileBaseName, boolean isAllowLtPartSize) {
        this.isAllowLtPartSize = isAllowLtPartSize;
        this.srcFile = srcFile;
        msDstDir = dstDir;
        msOutFileBaseName = psOutFileBaseName;
    }


    public FileSplitterUtils(String srcFile, String dstDir, String psOutFileBaseName, int partSize, boolean isAllowLtPartSize) {
        this.isAllowLtPartSize = isAllowLtPartSize;
        this.srcFile = srcFile;
        this.msDstDir = dstDir;
        this.msOutFileBaseName = psOutFileBaseName;
        this.partSize = partSize;
    }

    public FileSplitterUtils(String srcFile, String dstDir, String psOutFileBaseName, int piPartSize, int piMaxThrPool, boolean isAllowLtPartSize) {
        this.isAllowLtPartSize = isAllowLtPartSize;
        this.srcFile = srcFile;
        msDstDir = dstDir;
        msOutFileBaseName = psOutFileBaseName;
        partSize = piPartSize;
        miMaxThrPoolSize = piMaxThrPool;
    }

    /**
     * Judge if the splitter is working
     */
    public boolean isBusy() {
        return moThrPool != null && !moThrPool.isTerminated();
    }

    /**
     * Start splitting
     *
     * @return key 索引: 从1开始, value: 文件路径
     * @throws IOException
     */
    public Map<Integer, String> start() throws IOException {
        File loFile = new File(srcFile);
        if (!loFile.exists()) {
            throw new IOException("Src-File not found:" + srcFile);
        }

        File dstDirFile = new File(this.msDstDir);
        if (!dstDirFile.exists()) {
            dstDirFile.mkdir();
        }

        long liFileLen = loFile.length();
        long liPartCnt = liFileLen / partSize + (liFileLen % partSize == 0 ? 0 : 1);
        // 最后一片的长度
        long lastPartLen = liFileLen % partSize;
        // 最后一片的前一片索引
        long lastPartPreIndex = liPartCnt - 2;
        if (liPartCnt > Integer.MAX_VALUE) {
            throw new IOException("Src-File too large");
        }

        moInput = new FileInputStream(srcFile);
        moInChannel = moInput.getChannel();
        moThrPool = new ThreadPoolExecutor(miMaxThrPoolSize, miMaxThrPoolSize, 100, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        Map<Integer, String> outFiles = new HashMap<>(32);
        for (int i = 0; i < (int) liPartCnt; i++) {
            if (msOutFileBaseName == null || "".equals(msOutFileBaseName)) {
                outFiles.put(i + 1, msDstDir + "/" + (i + 1) + ".part");
            } else {
                outFiles.put(i + 1, msDstDir + "/" + msOutFileBaseName + "." + (i + 1) + ".part");
            }
            if (!isAllowLtPartSize && (i == lastPartPreIndex)) {
                moThrPool.execute(new SplitThread(i, lastPartLen + partSize));
                break;
            } else {
                moThrPool.execute(new SplitThread(i, i == liPartCnt - 1 ? lastPartLen : partSize));
            }

        }
        moThrPool.shutdown();
        // Monitor
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (moThrPool.isTerminated()) {
                        if (moInChannel != null) {
                            try {
                                moInChannel.close();
                            } catch (IOException ex) {
                                log.error(ex.getMessage());
                            }
                        }
                        if (moInput != null) {
                            try {
                                moInput.close();
                            } catch (IOException ex) {
                                log.error(ex.getMessage());
                            }
                        }
                        break;
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException ex) {
                        log.error(ex.getMessage());
                    }
                }
            }
        }.start();
        return outFiles;
    }

    /**
     * Splitting Thread
     */
    private class SplitThread implements Runnable {

        private final int miPartIndex;
        private final long miSize;

        public SplitThread(int piPartIndex, long piSize) {
            miPartIndex = piPartIndex;
            miSize = piSize;
        }

        @Override
        public void run() {
            String lsDstPartFile;
            if (msOutFileBaseName == null || "".equals(msOutFileBaseName)) {
                lsDstPartFile = msDstDir + "/" + (miPartIndex + 1) + ".part";
            } else {
                lsDstPartFile = msDstDir + "/" + msOutFileBaseName + "." + (miPartIndex + 1) + ".part";
            }
            log.info("FileSplitter: splitting " + lsDstPartFile);
            long liStartTime = System.currentTimeMillis();

            // output vars
            File loDstFile = new File(lsDstPartFile);
            RandomAccessFile output = null;
            FileChannel outChannel = null;

            try {
                // Input Map
                MappedByteBuffer inputBuffer = moInChannel.map(FileChannel.MapMode.READ_ONLY, (long) partSize * miPartIndex, miSize);

                // Output Map
                if (!loDstFile.exists()) {
                    loDstFile.createNewFile();
                }
                output = new RandomAccessFile(loDstFile, "rw");
                outChannel = output.getChannel();
                MappedByteBuffer outputBuffer = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, miSize);

                byte liByte;
                while (inputBuffer.hasRemaining()) {
                    liByte = inputBuffer.get();
                    outputBuffer.put(liByte);
                }
            } catch (IOException ex) {
                log.info("part_index:" + miPartIndex + " boom");
                log.error(ex.getMessage());
            } finally {
                // close output
                if (outChannel != null) {
                    try {
                        outChannel.close();
                    } catch (IOException ex) {
                        log.error(ex.getMessage());
                    }
                }
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException ex) {
                        log.error(ex.getMessage());
                    }
                }
            }

            log.info("Splitting Task " + lsDstPartFile + " Terminated. Spend " + (System.currentTimeMillis() - liStartTime) + " milsecs");
        }
    }

}
