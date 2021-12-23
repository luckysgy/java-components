package com.concise.component.util.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 文件合并工具类
 * 文件分解工具类 {@link FileSplitterUtils}
 * @author shenguangyang
 * @date 2021-10-16 16:18
 */
public class FileUnifierUtils {
    private static final Logger log = LoggerFactory.getLogger(FileUnifierUtils.class);
    private final String[] srcFiles;
    private final String dstFile;

    private int maxThreadPoolSize = 10;
    private ThreadPoolExecutor moThrPool;
    private RandomAccessFile moOutput;
    private FileChannel moOutputChannel;

    /**
     * @param srcFiles 源文件
     * @param dstFile 目标文件
     */
    public FileUnifierUtils(String[] srcFiles, String dstFile) {
        this.srcFiles = srcFiles;
        this.dstFile = dstFile;
    }

    public FileUnifierUtils(String[] srcFiles, String dstFile, int maxThreadPoolSize) {
        this.srcFiles = srcFiles;
        this.dstFile = dstFile;
        this.maxThreadPoolSize = maxThreadPoolSize;
    }

    public boolean isBusy() {
        return moThrPool != null && !moThrPool.isTerminated();
    }

    /**
     * Start unifying
     *
     * @throws IOException
     */
    public void start() throws IOException {
        File loOutFile = new File(dstFile);
        if (!loOutFile.exists()) {
            loOutFile.createNewFile();
        }
        moOutput = new RandomAccessFile(loOutFile, "rw");
        moThrPool = new ThreadPoolExecutor(maxThreadPoolSize, maxThreadPoolSize, 100, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

        // get total size
        long[][] laOffsetSizes = new long[srcFiles.length][2];
        long liOffset = 0;
        for (int i = 0; i < srcFiles.length; i++) {
            String lsSrcFile = srcFiles[i];
            File loFile = new File(lsSrcFile);
            if (!loFile.exists()) {
                throw new IOException("Src-File not exist:" + lsSrcFile);
            }
            laOffsetSizes[i][0] = liOffset;
            laOffsetSizes[i][1] = loFile.length();
            liOffset += loFile.length();
        }
        moOutput.setLength(liOffset);
        moOutputChannel = moOutput.getChannel();
        for (int i = 0; i < srcFiles.length; i++) {
            moThrPool.execute(new UnifyThread(srcFiles[i], laOffsetSizes[i][0], laOffsetSizes[i][1]));
        }
        moThrPool.shutdown();
        // Start monitor Thread
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (moThrPool.isTerminated()) {
                        if (moOutputChannel != null) {
                            try {
                                moOutputChannel.close();
                            } catch (IOException ex) {
                                log.error(ex.getMessage());
                            }
                        }
                        if (moOutput != null) {
                            try {
                                moOutput.close();
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
    }

    /**
     * Unifying thread
     */
    private class UnifyThread implements Runnable {

        private final String msSrcFile;
        private final long miOffset, miSize;

        public UnifyThread(String psSrcFile, long piOffset, long piSize) {
            msSrcFile = psSrcFile;
            miOffset = piOffset;
            miSize = piSize;
        }

        @Override
        public void run() {
            File loSrcFile = new File(msSrcFile);
            FileInputStream loInput = null;
            FileChannel loInChannel = null;
            try {
                System.out.println("FileUnifier: start unifying " + msSrcFile);
                long liStartTime = System.currentTimeMillis();
                loInput = new FileInputStream(loSrcFile);
                loInChannel = loInput.getChannel();
                MappedByteBuffer loInBuf = loInChannel.map(FileChannel.MapMode.READ_ONLY, 0, miSize);
                MappedByteBuffer loOutBuf = moOutputChannel.map(FileChannel.MapMode.READ_WRITE, miOffset, miSize);
                while (loInBuf.hasRemaining()) {
                    loOutBuf.put(loInBuf.get());
                }
                System.out.println("FileUnifier: unify " + msSrcFile + " complete. Spend " + (System.currentTimeMillis() - liStartTime) + "milsecs");
            } catch (IOException ex) {
                log.error(ex.getMessage());
            } finally {
                // Close input
                if (loInChannel != null) {
                    try {
                        loInChannel.close();
                    } catch (IOException ex) {
                        log.error(ex.getMessage());
                    }
                }
                if (loInput != null) {
                    try {
                        loInput.close();
                    } catch (IOException ex) {
                        log.error(ex.getMessage());
                    }
                }
            }
        }

    }
}
