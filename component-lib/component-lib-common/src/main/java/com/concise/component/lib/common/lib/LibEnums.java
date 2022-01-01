package com.concise.component.lib.common.lib;

import com.concise.component.lib.common.VersionEnvironmentPostProcessor;
import sun.tools.jar.resources.jar;

/**
 * @author shenguangyang
 * @date 2021-12-05 8:31
 */
public enum LibEnums {
    HIK(
            "component-lib-hik-win64-" + VersionEnvironmentPostProcessor.projectVersion + ".jar",
            "com.concise.component.lib.hik.win64.HikWin64PackageMark",
            "component-lib-hik-linux64-" + VersionEnvironmentPostProcessor.projectVersion + ".jar",
            "com.concise.component.lib.hik.linux64.HikLinux64PackageMark"
    );

    private final String win64JarName;
    private final String win64PackageMark;
    private final String linux64JarName;
    private final String linux64PackageMark;

    LibEnums(String win64JarName, String win64PackageMark, String linux64JarName, String linux64PackageMark) {
        this.win64JarName = win64JarName;
        this.win64PackageMark = win64PackageMark;
        this.linux64JarName = linux64JarName;
        this.linux64PackageMark = linux64PackageMark;
    }

    public String getWin64JarName() {
        return win64JarName;
    }

    public String getWin64PackageMark() {
        return win64PackageMark;
    }

    public String getLinux64JarName() {
        return linux64JarName;
    }

    public String getLinux64PackageMark() {
        return linux64PackageMark;
    }
}
