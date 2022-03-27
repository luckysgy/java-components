package com.concise.component.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A wrapper for ProcessBuilder that can be overridden easily for frameworks like Gradle that don't support it well.
 *
 * @author shenguangyang
 */
public class CommandExecutor {
    private static final Logger log = LoggerFactory.getLogger(CommandExecutor.class);

    /**
     * Executes a command with {@link ProcessBuilder}, but also logs the call
     * and redirects its input and output to our process.
     *
     * 解决乱码问题:
     * win10: java -jar -Dfile.encoding=UTF-8
     * linux: java -jar -Dfile.encoding=gbk
     *
     * @param cmd to have {@link ProcessBuilder} execute
     *        linux: String[] cmd = {"/bin/bash", "-c", "sh build.sh && cd ../cpp-base && sh build.sh"};
     *        win10: String[] cmd = {"cmd.exe", "/c", "ping www.baidu.com"};
     *               cmd /c dir 是执行完dir命令后关闭命令窗口。
     *               cmd /k dir 是执行完dir命令后不关闭命令窗口。
     *               cmd /c start dir 会打开一个新窗口后执行dir指令，原窗口会关闭。
     *               cmd /k start dir 会打开一个新窗口后执行dir指令，原窗口不会关闭。
     * @param workingDirectory to pass to {@link ProcessBuilder#directory()}
     *        可以理解为: 进入到系统的指定目录下执行cmd命令
     * @param environmentVariables to put in {@link ProcessBuilder#environment()}
     * @return the exit value of the command
     */
    public boolean executeCommand(String[] cmd, File workingDirectory,
                              Map<String,String> environmentVariables) throws Exception {
        List<String> command = new ArrayList<>(Arrays.asList(cmd));
        boolean windows = OSInfo.isWindows();
        for (int i = 0; i < command.size(); i++) {
            String arg = command.get(i);
            if (arg == null) {
                arg = "";
            }
            if (arg.trim().isEmpty() && windows) {
                // seems to be the only way to pass empty arguments on Windows?
                arg = "\"\"";
            }
            command.set(i, arg);
        }

        StringBuilder text = new StringBuilder();
        for (String s : command) {
            boolean hasSpaces = s.indexOf(" ") > 0 || s.isEmpty();
            if (hasSpaces) {
                text.append(windows ? "\"" : "'");
            }
            text.append(s);
            if (hasSpaces) {
                text.append(windows ? "\"" : "'");
            }
            text.append(" ");
        }
        log.info(text.toString());

        Process process = null;
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            if (workingDirectory != null) {
                pb.directory(workingDirectory);
            }
            if (environmentVariables != null) {
                for (Map.Entry<String,String> e : environmentVariables.entrySet()) {
                    if (e.getKey() != null && e.getValue() != null) {
                        pb.environment().put(e.getKey(), e.getValue());
                    }
                }
            }
            process = pb.inheritIO().start();
            return process.waitFor() == 0;
        } finally {
            // 销毁子进程
            if (process != null) {
                process.destroy();
            }
        }
    }
}