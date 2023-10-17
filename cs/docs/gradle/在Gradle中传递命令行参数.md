## 1. 概述

有时，我们想从[Gradle](https://www.baeldung.com/gradle)执行各种需要输入参数的程序。

在本快速教程中，我们将了解如何从 Gradle 传递命令行参数。

## 2. 输入参数的类型

当我们想从 Gradle CLI 传递输入参数时，我们有两种选择：

-   )使用-D)标志设置系统属性
-   )使用-P)标志设置项目属性

一般来说，))除非我们想在 JVM 中自定义设置，否则我们应该使用项目属性))。

尽管可以劫持系统属性来传递我们的输入，但我们应该避免这样做。

让我们看看这些属性的作用。首先，我们配置我们的)build.gradle)：

```groovy
apply plugin: "java"
description = "Gradle Command Line Arguments examples"

task propertyTypes(){
    doLast{
        if (project.hasProperty("args")) {
            println "Our input argument with project property ["+project.getProperty("args")+"]"
        }
        println "Our input argument with system property ["+System.getProperty("args")+"]"
    }
}

```

请注意，我们在任务中以不同的方式阅读它们。

我们这样做是因为 )))项目。)如果我们的属性未定义，)getProperty())将抛出)MissingPropertyException)))。

与项目属性不同，)System.getProperty())在属性未定义的情况下返回)空值。)

接下来，让我们运行任务并查看其输出：

```shell
$ ./gradlew propertyTypes -Dargs=lorem -Pargs=ipsum

> Task :cmd-line-args:propertyTypes
Our input argument with project property [ipsum]
Our input argument with system property [lorem]

```

## 3.传递命令行参数

到目前为止，我们已经看到了如何读取属性。实际上，我们需要将这些属性作为参数发送给我们选择的程序。

### 3.1. 将参数传递给Java应用程序

在之前的教程中，我们解释[了如何从 Gradle 运行Java主类](https://www.baeldung.com/gradle-run-java-main)。让我们以此为基础，看看我们如何也可以传递参数。

首先，让我们在)))build.gradle)))))中使用应用程序插件))：

```groovy
apply plugin: "java"
apply plugin: "application"
description = "Gradle Command Line Arguments examples"
 
// previous declarations
 
ext.javaMainClass = "com.baeldung.cmd.MainClass"
 
application {
    mainClassName = javaMainClass
}

```

现在，让我们来看看我们的主类：

```java
public class MainClass {
    public static void main(String[] args) {
        System.out.println("Gradle command line arguments example");
        for (String arg : args) {
            System.out.println("Got argument [" + arg + "]");
        }
    }
}

```

接下来，让我们用一些参数运行它：

```shell
$ ./gradlew :cmd-line-args:run --args="lorem ipsum dolor"

> Task :cmd-line-args:run
Gradle command line arguments example
Got argument [lorem]
Got argument [ipsum]
Got argument [dolor]
```

在这里，我们不使用属性来传递参数。相反，))我们在那里传递)–args)标志和相应的输入))。

这是应用程序插件提供的一个很好的包装器。但是，))这仅在 Gradle 4.9 之后可用))。

让我们看看))使用)JavaExec)任务))会是什么样子。

首先，我们需要在)build.gradle)中定义它：

```groovy
ext.javaMainClass = "com.baeldung.cmd.MainClass"
if (project.hasProperty("args")) {
    ext.cmdargs = project.getProperty("args")
} else { 
    ext.cmdargs = ""
}
task cmdLineJavaExec(type: JavaExec) {
    group = "Execution"
    description = "Run the main class with JavaExecTask"
    classpath = sourceSets.main.runtimeClasspath
    main = javaMainClass
    args cmdargs.split()
}

```

让我们仔细看看我们做了什么。我们首先))从项目属性中读取参数))。

由于这包含所有参数作为一个字符串，因此我们))使用)split)方法获取一个参数数组))。

接下来，我们))将此数组传递给)JavaExec)任务的)args)属性))。

让我们看看运行此任务时会发生什么，使用)-P)选项传递项目属性：

```shell
$ ./gradlew cmdLineJavaExec -Pargs="lorem ipsum dolor"

> Task :cmd-line-args:cmdLineJavaExec
Gradle command line arguments example
Got argument [lorem]
Got argument [ipsum]
Got argument [dolor]

```

### 3.2. 将参数传递给其他应用程序

在某些情况下，我们可能希望将一些参数从 Gradle 传递给第三方应用程序。

幸运的是，我们可以))使用更通用的)Exec)任务))来做到这一点：

```groovy
if (project.hasProperty("args")) {
    ext.cmdargs = project.getProperty("args")
} else { 
    ext.cmdargs = "ls"
}
 
task cmdLineExec(type: Exec) {
    group = "Execution"
    description = "Run an external program with ExecTask"
    commandLine cmdargs.split()
}

```

在这里，我们))使用任务的)commandLine)属性来传递可执行文件和任何参数))。同样，我们根据空格拆分输入。

让我们看看如何为)ls)命令运行它：

```shell
$ ./gradlew cmdLineExec -Pargs="ls -ll"

> Task :cmd-line-args:cmdLineExec
total 4
drwxr-xr-x 1 user 1049089    0 Sep  1 17:59 bin
drwxr-xr-x 1 user 1049089    0 Sep  1 18:30 build
-rw-r--r-- 1 user 1049089 1016 Sep  3 15:32 build.gradle
drwxr-xr-x 1 user 1049089    0 Sep  1 17:52 src
```

如果我们不想在任务中对可执行文件进行硬编码，这将非常有用。

## 4. 总结

在本快速教程中，我们了解了如何从 Gradle 传递输入参数。

首先，我们解释了可以使用的属性类型。虽然我们可以使用系统属性来传递输入参数，但我们应该更喜欢项目属性。

然后，我们探索了将命令行参数传递给Java或外部应用程序的不同方法。