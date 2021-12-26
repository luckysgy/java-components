package com.concise.createproject;
import cn.hutool.core.util.ReUtil;
import com.concise.component.core.utils.ReflectUtils;
import com.concise.component.util.file.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 作用: 将components 作为子模块添加到project-start工程中
 * 然后，执行当前类可以生成 {@link CreateProjectConfig} 中配置的目标工程
 * @author shenguangyang
 * @date 2021/7/8 20:27
 */
public class CreateProject {

    // 组件工程包名
    private static final String componentPackageName = "com.concise";
    // project-start工程包名
    private static final String projectStartPackageName = "com.projectstart";
    // project-start 工程的工程名字简写 相当于每个模块的前缀
    private static final String projectStartNameShort = "projectstart";
    // project-start 工程的工程名 = 顶级模块名称
    private static final String projectStartName = "project-start";
    // 拷贝时候需要排除的文件
    private static final List<String> copyExcludeFiles = new ArrayList<String>() {{
        // 拷贝文件时候会排除当前目录下的 **/target
        add("**" + File.separator + "target");
        add("**" + File.separator + "node_modules");
        add("logs");
        add(".git");
    }};

    private static String packageNameToPath(String packageName) {
        return packageName.replace(".", File.separator);
    }

    /**
     * 获取排除路径的正则表达式
     * @return
     */
    private static List<String> getExcludeFilePathReg() throws IOException {
        List<String> regFilePaths = new ArrayList<>();
        File directory = new File("");
        String currentProjectRootPath = directory.getCanonicalPath();

        for (String excludeFile : copyExcludeFiles) {
            String path = currentProjectRootPath + File.separator + excludeFile;
            path = path.replace("\\","\\\\");
            String regPath = ReflectUtils.getRegPath(path);
            regFilePaths.add(regPath);
        }
        return regFilePaths;
    }

    public static void main(String[] args) throws IOException {
        String componentPackagePath = packageNameToPath(componentPackageName);
        String projectStartPackagePath = packageNameToPath(projectStartPackageName);
        String newPackagePath = packageNameToPath(CreateProjectConfig.packageName);
        // 参数为空 获取当前项目的根路径
        File directory = new File("");
        String courseFile = directory.getCanonicalPath();
        String rootPath = courseFile.substring(0,courseFile.lastIndexOf(File.separator));
        String newProjectPath = rootPath + File.separator + CreateProjectConfig.projectName;
        System.out.println(courseFile);
        System.out.println(rootPath);
        System.out.println(newProjectPath);

        List<String> allFile = FileUtils.getAllFile(courseFile, true, getExcludeFilePathReg());
        for (String filePath : allFile) {
            String projectFilePath =
                    filePath.substring(filePath.lastIndexOf(projectStartName + "\\") + projectStartName.length() + 1);
            File sourceFile = new File(filePath);

            String newPath = "";
            if (CreateProjectConfig.isUpdateComponentPackageName) {
                newPath = (newProjectPath + File.separator + projectFilePath)
                        .replace(projectStartName, CreateProjectConfig.projectName)
                        .replace(componentPackagePath,newPackagePath)
                        .replace(projectStartPackagePath,newPackagePath)
                        .replace(projectStartNameShort, CreateProjectConfig.projectNameShort);
            } else {
                newPath = (newProjectPath + File.separator + projectFilePath)
                        .replace(projectStartName, CreateProjectConfig.projectName)
                        .replace(projectStartPackagePath,newPackagePath)
                        .replace(projectStartNameShort, CreateProjectConfig.projectNameShort);
            }

            File targetFile = new File(newPath);
            if (sourceFile.isDirectory()) {
                if (!targetFile.exists()) {
                    if (!targetFile.mkdirs()) {
                        System.out.println("路径创建失败, " + targetFile);
                    }
                }
                continue;
            }
            org.apache.commons.io.FileUtils.copyFile(sourceFile, targetFile);
        }

        /*
         * 替换包名
         */
        List<String> newProjectFiles = FileUtils.getAllFile(newProjectPath, true, null);
        for (String newProjectFile : newProjectFiles) {
            File file = new File(newProjectFile);
            if (file.isFile()) {
                if (isBinary(file)) {
                    System.out.println("检查到二进制文件, 不做任何处理, path = " + newProjectFile);
                    continue;
                }
            }
            FileUtils.replaceContent(newProjectFile, projectStartName, CreateProjectConfig.projectName);
            if (CreateProjectConfig.isUpdateComponentPackageName) {
                FileUtils.replaceContent(newProjectFile, componentPackageName, CreateProjectConfig.packageName);
            }
            FileUtils.replaceContent(newProjectFile, projectStartPackageName, CreateProjectConfig.packageName);
            FileUtils.replaceContent(newProjectFile, projectStartNameShort, CreateProjectConfig.projectNameShort);
        }
    }


    public static boolean isBinary(File file) {
        boolean isBinary = false;
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            long len = file.length();
            for (int j = 0; j < (int) len; j++) {
                int t = fin.read();
                if (t < 32 && t != 9 && t != 10 && t != 13) {
                    isBinary = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isBinary;
    }
}
