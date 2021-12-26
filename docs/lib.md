# 工作原理

分两种状态:
- jar包中运行
- 非jar中运行

## 非jar中运行
    通过具体 `PackageMark` 子类包名反射获取具体库的路径, 然后包下的所有静态库都拷贝到系统临时目录下, 最后会将路径返回给上层应用

## jar中运行
    a. 获取jar所在路径, 然后通过JarFile类获取jar中所依赖的存放库文件jar中的所有库文件
    b. 拷贝到系统临时目录下, 并将目录返回给上层应用

# 如何使用

1. `LibHik.HCNetSDK.path`
```java
public interface HCNetSDK extends Library {
    HCNetSDK INSTANCE = (HCNetSDK) Native.loadLibrary(LibHik.HCNetSDK.path, HCNetSDK.class);
}
```

2. 引入依赖, 根据不同平台引入不同的依赖, 如果全引入, 会自动判断具体加载哪个系统的库文件
```xml
<dependency>
    <groupId>com.xxxx</groupId>
    <artifactId>lib-hik-win64</artifactId>
    <version>${project.version}</version>
</dependency>

<dependency>
    <groupId>com.xxx</groupId>
    <artifactId>lib-hik-linux64</artifactId>
    <version>${project.version}</version>
</dependency>
```

# 如何扩展自己的库

如果你想要加载其他库需要按照如下步骤操作
1. 新增两个module, 比如lib-demo-win64, lib-demo-linux64, 如果你需要其他平台的, 则需要再新增module, 但是我的代码中, 默认只判断win和linux系统, 因此如果想要支持其他系统, 需要修改 `LibPath#copy` 一开始的代码
2. 然后在这两个模块下新建包, eg: com.project.lib.demo.win64, com.project.lib.demo.linux64
3. 修改pom.xml
```xml
<dependencies>
    <dependency>
        <groupId>com.simplifydev</groupId>
        <artifactId>lib-common</artifactId>
        <version>${project.version}</version>
    </dependency>
</dependencies>
<build>
    <resources>
        <resource>
            <directory>src/main/java</directory>
            <targetPath>${project.build.directory}/classes</targetPath>
            <includes>
                <include>**/*.*</include>
                <include>**/*.*</include>
                <include>**/*.*</include>
            </includes>
            <filtering>false</filtering>
        </resource>
    </resources>
</build>
```

4. 分别在com.project.lib.demo.win64, com.project.lib.demo.linux64中新建, DemoWin64PackageMark, DemoLinux64PackageMark并实现`PackageMark`
    接口

5. 回到lib-common中的lib目录下, 在LibEnums中新增Demo相关配置
```java
public enum LibEnums {
    DEMO(
            "lib-demo-win64-v20210901.jar",
            "com.project.lib.demo.win64.DemoWin64PackageMark",
            "lib-hik-linux64-v20210901.jar",
            "com.project.lib.demo.linux64.DemoLinux64PackageMark"
    );
}
```

6. 新增LibDemo类
```java
/**
 * 内部类的名称要求和库的名称一样, 不限制大小写, 但如果有下划线等特殊符号必须指明
 * 比如 opencv_core 可以定义成类名有: Opencv_Core / OPENCV_CORE / opencv_core 等
 * 目前只支持加载包的根路径下库文件, 不支持加载二级以及以上的库文件
 */
public class LibDemo {
    public static class DemoSDK extends LibPath {
        public static String path = getPath(LibEnums.DEMO, DemoSDK.class);
    }
}

```

> 注意内部类名必须和静态库名称去掉lib以及后缀保持一样 (不限制大小写)
> eg: libhcnetsdk.so / HCNETSDK.dll  ===> 可以定义的类名 HCNETSDK / hcnetsdk
> eg: libhc_netsdk.so / HC_NETSDK.dll  ===> 可以定义的类名 HC_NETSDK / hc_netsdk

7. 在具体的sdk类中加载库
```java
public interface DemoSDK extends Library {
    DemoSDK INSTANCE = (DemoSDK) Native.loadLibrary(LibDemo.DemoSDK.path, DemoSDK.class);
}
```

