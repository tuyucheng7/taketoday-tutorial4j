## 1. 概述

在这个简短的教程中，我们将了解导致 Java 运行时错误java.lang.UnsupportedClassVersionError: Unsupported major.minor version的原因 以及如何修复它。

## 2. 查看错误

让我们从看一个示例错误开始：

```plaintext
Exception in thread "main" java.lang.UnsupportedClassVersionError: com/baeldung/MajorMinorApp 
  has been compiled by a more recent version of the Java Runtime (class file version 55.0), 
  this version of the Java Runtime only recognizes class file versions up to 52.0
```

这个错误告诉我们，我们的类是在比我们试图运行它的版本更高的 Java 版本上编译的。更具体地说，在这种情况下，我们使用 Java 11 编译我们的类并尝试使用 Java 8 运行它。

### 2.1. Java 版本号

作为参考，让我们快速浏览一下 Java 版本号。如果我们需要下载适当的 Java 版本，这将派上用场。

主要和次要版本号存储在类字节码的第六和第七字节处。

让我们看看主要版本号如何映射到 Java 版本：

-   45 = Java 1.1
-   46 = Java 1.2
-   47 = Java 1.3
-   48 = Java 1.4
-   49 = Java 5
-   50 = Java 6
-   51 = Java 7
-   52 = Java 8
-   53 = Java 9
-   54 = Java 10
-   55 = Java 11
-   56 = Java 12
-   57 = Java 13

## 3.通过命令行修复

现在让我们讨论如何在从命令行运行 Java 时解决此错误。

根据我们的情况，我们有两种方法可以解决此错误：为早期版本的 Java 编译我们的代码，或者在较新的 Java 版本上运行我们的代码。

最后的决定取决于我们的情况。如果我们需要使用已经在更高级别编译的第三方库，我们最好的选择可能是使用更新的 Java 版本运行我们的应用程序。如果我们正在打包一个应用程序以供分发，最好编译成旧版本。

### 3.1. JAVA_HOME环境变量

让我们从检查[JAVA_HOME](https://www.baeldung.com/find-java-home)变量的设置方式开始。当我们从命令行运行javac时，这将告诉我们正在使用哪个 JDK ：

```shell
echo %JAVA_HOME%
C:AppsJavajdk8-x64
```

如果我们准备好完全迁移到更新的[JDK](https://www.baeldung.com/jvm-vs-jre-vs-jdk)，我们可以下载更新的版本并确保我们的PATH和[JAVA_HOME环境变量设置正确](https://www.baeldung.com/java-home-on-windows-7-8-10-mac-os-x-linux)。

### 3.2. 运行新的 JRE

回到我们的示例，让我们看看如何通过在更高版本的 Java 上运行来解决错误。假设我们在C:Appsjdk-11.0.2中有 Java 11 JRE，我们可以使用与它打包的java命令运行我们的代码：

```shell
C:Appsjdk-11.0.2binjava com.baeldung.MajorMinorApp
Hello World!
```

### 3.3. 使用较旧的 JDK 进行编译

如果我们正在编写一个应用程序，我们希望它可以运行到特定版本的 Java，我们需要编译该版本的代码。

我们可以通过以下三种方式之一来做到这一点：使用旧的 JDK 来编译我们的代码；使用javac命令的-bootclasspath、-source和-target选项(JDK 8 及更早版本)；或使用–release选项(JDK 9 及更新版本)。

让我们从使用较旧的 JDK 开始，类似于我们使用较新的 JRE 来运行代码的方式：

```shell
C:AppsJavajdk1.8.0_31binjavac com/baeldung/MajorMinorApp.java
```

可以只使用-source和-target，但它可能仍会创建与旧版 Java 不兼容的类文件。

为了确保兼容性，我们可以将 -bootclasspath指向目标 JRE的rt.jar：

```bash
javac -bootclasspath "C:AppsJavajdk1.8.0_31jrelibrt.jar" 
  -source 1.8 -target 1.8 com/baeldung/MajorMinorApp.java
```

以上主要适用于 JDK 8 及更低版本。在 JDK 9 中，添加了 –release参数以替换-source和-target。–release选项支持目标6、7、8、9、10和 11。

让我们使用 –release来定位 Java 8：

```shell
javac --release 8 com/baeldung/MajorMinorApp.java
```

现在我们可以在 Java 8 或更高版本的 JRE 上运行我们的代码。

## 4.Eclipse集成开发环境

现在我们了解了错误和纠正它的一般方法，让我们利用我们所学的知识，看看在 Eclipse IDE 中工作时如何应用它。

### 4.1. 更改 JRE

假设我们已经为[Eclipse 配置了不同版本的 Java](https://www.baeldung.com/eclipse-change-java-version)，让我们更改项目的 JRE。

让我们转到我们的Project properties，然后是Java Build Path，然后是Libraries选项卡。在那里，我们将选择 JRE 并单击编辑：

[![项目属性库](https://www.baeldung.com/wp-content/uploads/2019/03/BAEL-2308_ProjectPropertiesLib.jpg)](https://www.baeldung.com/wp-content/uploads/2019/03/BAEL-2308_ProjectPropertiesLib.jpg)

现在让我们选择Alternate JRE并指向我们的 Java 11 安装：

[![项目编辑JRE11](https://www.baeldung.com/wp-content/uploads/2019/03/BAEL-2308_ProjectEditJRE11.jpg)](https://www.baeldung.com/wp-content/uploads/2019/03/BAEL-2308_ProjectEditJRE11.jpg)

此时，我们的应用程序将针对 Java 11 运行。

### 4.2. 更改编译器级别

现在让我们看看如何将目标更改为较低级别的 Java。

首先，让我们回到我们的项目属性，然后是Java Compiler，然后检查Enable project specific settings：

[![BAEL 2308_ProjectPropertiesCompilerLevel](https://www.baeldung.com/wp-content/uploads/2019/03/BAEL-2308_ProjectPropertiesCompilerLevel.jpg)](https://www.baeldung.com/wp-content/uploads/2019/03/BAEL-2308_ProjectPropertiesCompilerLevel.jpg)

在这里我们可以将我们的项目设置为针对早期版本的 Java 进行编译并自定义其他合规性设置：

[![BAEL 2308_ProjectPropertiesCompilerLevel_dropdown](https://www.baeldung.com/wp-content/uploads/2019/03/BAEL-2308_ProjectPropertiesCompilerLevel_dropdown.jpg)](https://www.baeldung.com/wp-content/uploads/2019/03/BAEL-2308_ProjectPropertiesCompilerLevel_dropdown.jpg)

## 5.理解这个想法

我们还可以控制在 IntelliJ IDEA 中用于编译和运行的 Java 版本。

### 5.1. 添加 JDK

在此之前，我们将了解如何添加其他 JDK。让我们去文件 - >项目结构 - >平台设置 - > SDKs：

[![BAEL 2308_IDEA_AddSDK1](https://www.baeldung.com/wp-content/uploads/2019/03/BAEL-2308_IDEA_AddSDK1.jpg)](https://www.baeldung.com/wp-content/uploads/2019/03/IDEA_AddSDK1.jpg)

让我们单击中间列中的加号图标， 从下拉列表中选择 JDK，然后选择我们的 JDK 位置：

[![IDEA_AddSDK2_2](https://www.baeldung.com/wp-content/uploads/2019/03/BAEL-2308_IDEA_AddSDK2_2.jpg)](https://www.baeldung.com/wp-content/uploads/2019/03/BAEL-2308_IDEA_AddSDK2_2.jpg)

### 5.2. 更改 JRE

首先，我们将了解如何使用 IDEA 在较新的 JRE 上运行我们的项目。

让我们转到运行 -> 编辑配置...并将我们的JRE更改为 11：

[![IDEA_ChangeJRE](https://www.baeldung.com/wp-content/uploads/2019/03/BAEL-2308_IDEA_ChangeJRE.jpg)](https://www.baeldung.com/wp-content/uploads/2019/03/BAEL-2308_IDEA_ChangeJRE.jpg)

现在，当我们运行我们的项目时，它将使用 Java 11 JRE 运行。

### 5.3. 更改编译器级别

如果我们分发我们的应用程序以在较低的 JRE 上运行，我们需要调整我们的编译器级别以针对旧版本的 Java。

让我们转到文件 -> 项目结构... -> 项目设置 -> 项目并更改我们的项目 SDK和项目语言级别：

[![IDEA_ChangeTargetLevel](https://www.baeldung.com/wp-content/uploads/2019/03/BAEL-2308_IDEA_ChangeTargetLevel.jpg)](https://www.baeldung.com/wp-content/uploads/2019/03/BAEL-2308_IDEA_ChangeTargetLevel.jpg)

我们现在可以构建我们的项目，生成的类文件将在 Java 8 及更高版本上运行。

## 6.专家

[当我们在Maven](https://www.baeldung.com/maven)中构建和打包文件时，我们可以控制目标[Java 的版本。](https://www.baeldung.com/maven-java-version)

使用 Java 8 或更早版本时，我们为编译器插件设置源和目标。

让我们使用编译器插件属性设置源和目标：

```xml
<properties>
    <maven.compiler.target>1.8</maven.compiler.target>
    <maven.compiler.source>1.8</maven.compiler.source>
</properties>
```

或者，我们可以在编译器插件中设置源和目标：

```xml
<plugins>
    <plugin>    
        <artifactId>maven-compiler-plugin</artifactId>
        <configuration>
            <source>1.8</source>
            <target>1.8</target>
        </configuration>
    </plugin>
</plugins>
```

通过在 Java 9 中添加的–release选项，我们也可以使用 Maven 对其进行配置。

让我们使用编译器插件属性来设置发布：

```xml
<properties>
    <maven.compiler.release>8</maven.compiler.release>
</properties>
```

或者我们可以直接配置编译器插件：

```xml
<plugins>
    <plugin>    
        <artifactId>maven-compiler-plugin</artifactId>
        <configuration>
            <release>8</release>
        </configuration>
    </plugin>
</plugins>
```

## 七、总结

在本文中，我们了解了导致java.lang.UnsupportedClassVersionError: Unsupported major.minor version错误消息的原因，以及如何修复它。