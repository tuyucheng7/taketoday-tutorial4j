## 1. 简介

在本教程中，我们介绍使用Gradle执行Java main方法的不同方法。

## 2.Javamain方法

我们可以通过多种方式使用Gradle运行Java main方法，让我们通过一个输出简单“Goodbye cruel world ...”语句的MainClass来演示这些用法：

```java
public class MainClass {
    
    public static void main(String[] args) {
        System.out.println("Goodbye cruel world ...");
    }
}
```

## 3. 使用application插件运行

application插件是一个Gradle核心插件，它定义了一组随时可用的任务，可帮助我们打包和分发应用程序。

首先我们在build.gradle文件中添加以下内容：

```groovy
plugins {
    id "application"
}

apply plugin: "java"

ext {
    javaMainClass = "cn.tuyucheng.taketoday.gradle.exec.MainClass"
}

application {
    mainClassName = javaMainClass
}
```

该插件会自动生成一个名为run的任务，只需要我们将其指向主类。第12行的闭包正是这样做的，它允许我们触发任务：

```bash
$ gradle run

> Task :java-exec:run
Goodbye cruel world ...

BUILD SUCCESSFUL in 7s
2 actionable tasks: 1 executed, 1 up-to-date
```

## 4. 使用JavaExec任务运行

接下来，让我们在JavaExec任务类型的帮助下实现一个用于运行main方法的自定义任务：

```groovy
task runWithJavaExec(type: JavaExec) {
    group = "Execution"
    description = "Run the main class with JavaExecTask"
    classpath = sourceSets.main.runtimeClasspath
    main = javaMainClass
}
```

我们需要在第5行定义主类，另外还要指定类路径。类路径是根据构建输出的默认属性计算的，并且包含编译类实际放置的正确路径。请注意，))在每种情况下，我们都需要使用主类的全限定名称，包括包))。

让我们使用JavaExec运行我们的示例：

```bash
$ gradle runWithJavaExec

> Task :java-exec:runWithJavaExec
Goodbye cruel world ...

BUILD SUCCESSFUL in 885ms
2 actionable tasks: 1 executed, 1 up-to-date
```

## 5. 使用Exec任务运行

最后，我们可以使用基本的Exec任务类型来执行我们的主类。由于此方法为我们提供了以多种方式配置执行的可能性，所以让我们实现三个自定义任务并分别进行介绍。

### 5.1 从编译的构建输出运行

首先，我们创建一个自定义Exec任务，其行为类似于JavaExec：

```groovy
task runWithExec(type: Exec) {
    dependsOn build
    group = "Execution"
    description = "Run the main class with ExecTask"
    commandLine "java", "-classpath", sourceSets.main.runtimeClasspath.getAsPath(), javaMainClass
}
```

我们可以运行任何可执行文件(在本例中为java)并传递运行所需的参数。

我们在第5行配置了类路径并指向我们的主类，并在第2行配置为依赖于build任务。这是必要的，因为我们只能在编译主类之后运行它：

```bash
$ gradle runWithExec

> Task :java-exec:runWithExec
Goodbye cruel world ...

BUILD SUCCESSFUL in 1s
6 actionable tasks: 5 executed, 1 up-to-date
```

### 5.2 从输出Jar运行

第二种方式依赖于我们应用的jar包：

```groovy
task runWithExecJarOnClassPath(type: Exec) {
    dependsOn jar
    group = "Execution"
    description = "Run the mainClass from the output jar in classpath with ExecTask"
    commandLine "java", "-classpath", jar.archiveFile.get(), javaMainClass
}

```

注意第2行对jar任务的依赖生命以及第5行对java可执行文件的第二个参数。))我们使用的是普通jar，因此我们需要使用第四个参数指定入口点))：

```bash
$ gradle runWithExecJarOnClassPath

> Task :java-exec:runWithExecJarOnClassPath
Goodbye cruel world ...

BUILD SUCCESSFUL in 800ms
3 actionable tasks: 1 executed, 2 up-to-date
```

### 5.3 从可执行输出Jar运行

第三种方式也依赖于jar包，但是我们通过manifest属性来定义入口点：

```groovy
jar {
    manifest {
        attributes("Main-Class": javaMainClass)
    }
}

task runWithExecJarExecutable(type: Exec) {
    dependsOn jar
    group = "Execution"
    description = "Run the output executable jar with ExecTask"
    commandLine "java", "-jar", jar.archiveFile.get()
}
```

在这里，))我们不再需要指定类路径))，我们可以简单地运行jar：

```bash
 gradle runWithExecJarExecutable

> Task :java-exec:runWithExecJarExecutable
Goodbye cruel world ...

BUILD SUCCESSFUL in 780ms
3 actionable tasks: 1 executed, 2 up-to-date
```

## 6. 总结

在本文中，我们介绍了使用Gradle运行Java main方法的各种方法。

开箱即用，application插件提供了一个最低限度的可配置任务来运行我们的方法。JavaExec任务类型允许我们在不指定任何插件的情况下运行main方法。最后，通用Exec任务类型可以与java可执行文件进行各种组合以实现相同的结果，但需要依赖于其他任务。