## 1. 概述

[jlink ](https://docs.oracle.com/javase/9/tools/jlink.htm#JSWOR-GUID-CECAC52B-CFEE-46CB-8166-F17A8E9280E9)是一种生成自定义Java运行时映像的工具，该映像仅包含给定应用程序所需的平台模块。

这样的运行时映像的行为与 JRE 完全相同，但仅包含我们选择的模块和它们运行所需的依赖项。[JEP 220](https://openjdk.java.net/jeps/220)中引入了模块化运行时映像的概念。

在本教程中，我们将学习如何使用jlink创建自定义 JRE ，我们还将在 JRE 中运行并测试我们的模块是否正常运行。

## 2.需要创建自定义JRE

让我们通过一个示例来了解自定义运行时映像背后的动机。

我们将创建一个简单的模块化应用程序。要了解有关创建模块化应用程序的更多信息，请参阅[我们关于模块化的文章。](https://www.baeldung.com/java-9-modularity)

首先，让我们创建一个HelloWorld类和一个相应的模块：

```java
public class HelloWorld {
    private static final Logger LOG = Logger.getLogger(HelloWorld.class.getName());
    public static void main(String[] args) {
        LOG.info("Hello World!");
    }
}
module jlinkModule {
    requires java.logging;
}
```

要运行这个程序，我们只需要HelloWorld、 String、Logger和Object类。

即使这个程序只需要运行四个类，JRE 中的所有预定义类也会被执行，即使我们的程序不需要它们。

因此，要运行一个小程序，我们必须维护一个完整的JRE，这简直是在浪费内存。

因此，定制的 JRE 是运行我们的示例的最佳选择。

使用jlink，我们可以创建自己的小型 JRE，其中仅包含我们想要使用的相关类，而不会浪费内存，因此，我们将看到性能提高。

## 3. 构建自定义Java运行时映像

我们将执行一系列简单的步骤来创建自定义 JRE 映像。

### 3.1。编译模块

首先，让我们从命令行编译上面提到的程序：

```java
javac -d out module-info.java
javac -d out --module-path out combaeldungjlinkHelloWorld.java
```

现在，让我们运行程序：

```java
java --module-path out --module jlinkModule/com.baeldung.jlink.HelloWorld
```

输出将是：

```java
Mar 13, 2019 10:15:40 AM com.baeldung.jlink.HelloWorld main
INFO: Hello World!
```

### 3.2. 使用jdeps列出依赖模块

为了使用jlink，我们需要知道应用程序使用的 JDK 模块列表，并且我们应该包含在我们的自定义 JRE 中。

让我们使用[jdeps](https://docs.oracle.com/javase/9/tools/jdeps.htm#JSWOR690) 命令来获取应用程序中使用的依赖模块：

```java
jdeps --module-path out -s --module jlinkModule
```

输出将是：

```java
jlinkModule -> java.base
jlinkModule -> java.logging
```

这是有道理的，因为java.base是Java代码库所需的最小模块，而java.logging由我们程序中的记录器使用。

### 3.3. 使用jlink创建自定义 JRE

要为基于模块的应用程序创建自定义 JRE，我们可以使用jlink命令。这是它的基本语法：

```java
jlink [options] –module-path modulepath
  –add-modules module [, module…]
  --output <target-directory>
```

现在，让我们使用Java11为我们的程序创建一个自定义 JRE ：

```java
jlink --module-path "%JAVA_HOME%jmods";out
  --add-modules jlinkModule
  --output customjre
```

这里，–add-modules参数后面的值告诉jlink哪个模块包含在 JRE 中。

最后，–output参数旁边的customjre定义了应该生成我们的自定义 JRE 的目标目录。

请注意，我们使用 Windows shell 来执行本教程中的所有命令。Linux 和 Mac 用户可能需要稍微调整它们。

### 3.4. 使用生成的图像运行应用程序

现在，我们有了 jlink 创建的自定义JRE。

为了测试我们的 JRE，让我们尝试通过在customjre目录的bin文件夹中导航并运行以下命令来运行我们的模块：

```java
java --module jlinkModule/com.baeldung.jlink.HelloWorld
```

同样，我们使用的 Windows shell 在进入 PATH 之前会在当前目录中查找任何可执行文件。 当我们在 Linux 或 Mac 上时，我们需要特别注意实际运行我们的自定义 JRE，而不是针对 PATH 解析的 java 。

## 4. 使用启动器脚本创建自定义 JRE

或者，我们还可以使用可执行启动器脚本创建自定义 JRE 。

为此，我们需要运行带有额外–launcher参数的jlink命令，以使用我们的模块和主类创建启动器：

```java
jlink --launcher customjrelauncher=jlinkModule/com.baeldung.jlink.HelloWorld
  --module-path "%JAVA_HOME%jmods";out
  --add-modules jlinkModule
  --output customjre
```

这将在我们的customjre/bin目录中生成两个脚本：customjrelauncher.bat和customjrelauncher 。

让我们运行脚本：

```java
customjrelauncher.bat
```

输出将是：

```java
Mar 18, 2019 12:34:21 AM com.baeldung.jlink.HelloWorld main
INFO: Hello World!
```

## 5. 总结

在本教程中，我们学习了如何使用jlink创建一个自定义的模块化 JRE，该 JRE只包含我们模块所需的最少文件。我们还研究了如何使用可以轻松执行和交付的启动器脚本创建自定义 JRE。

自定义、模块化的Java运行时映像非常强大。创建自定义 JRE 的目标很明确：它们可以节省内存、提高性能以及增强安全性和可维护性。轻量级自定义 JRE 还使我们能够为小型设备创建可扩展的应用程序。