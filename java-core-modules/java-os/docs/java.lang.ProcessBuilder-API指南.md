## 1. 概述

Process [API](https://www.baeldung.com/java-process-api)提供了一种在Java中执行操作系统命令的强大方法。但是，它有几个选项可能会使其使用起来很麻烦。

在本教程中，我们将了解Java如何使用ProcessBuilder API 缓解这种情况。

## 2.ProcessBuilder API _

ProcessBuilder类提供了创建和配置操作系统进程的方法。每个ProcessBuilder实例都允许我们管理流程属性的集合。然后我们可以使用这些给定的属性启动一个新 进程。

以下是我们可以使用此 API 的一些常见场景：

-   查找当前的Java版本
-   为我们的环境设置自定义键值映射
-   更改运行 shell 命令的工作目录
-   将输入和输出流重定向到自定义替换
-   继承当前 JVM 进程的两个流
-   [从Java代码执行 shell 命令](https://www.baeldung.com/run-shell-command-in-java)

我们将在后面的部分中查看其中每一个的实际示例。

但在我们深入研究工作代码之前，让我们先看看这个 API 提供了什么样的功能。

### 2.1. 方法总结

在本节中，我们将退后一步，简要了解ProcessBuilder类中最重要的方法。当我们稍后深入研究一些真实示例时，这将对我们有所帮助：

-   ```java
    ProcessBuilder(String... command)
    ```

    要使用指定的操作系统程序和参数创建一个新的进程构建器，我们可以使用这个方便的构造函数。

-   ```java
    directory(File directory)
    ```

    我们可以通过调用directory方法并传递一个File对象来覆盖当前进程的默认工作目录。默认情况下，当前工作目录设置为user.dir系统属性返回的值。

-   ```java
    environment()
    ```

    如果我们想获取当前的环境变量，我们可以简单地调用环境方法。它使用System.getenv()但作为Map向我们返回当前进程环境的副本。

-   ```java
    inheritIO()
    ```

    如果我们想指定子进程标准 I/O 的源和目标应该与当前Java进程的源和目标相同，我们可以使用 inheritIO方法。

-   ```java
    redirectInput(File file), redirectOutput(File file), redirectError(File file)
    ```

    当我们想要将流程构建器的标准输入、输出和错误目标重定向到一个文件时，我们可以使用这三种类似的重定向方法。

-   ```java
    start()
    ```

    最后但同样重要的是，要使用我们配置的内容启动一个新进程，我们只需调用start()。

我们应该注意到这个类不是同步的。例如，如果我们有多个线程同时访问一个ProcessBuilder实例，那么同步必须在外部进行管理。

## 3.例子

现在我们对ProcessBuilder API 有了基本的了解，让我们逐步浏览一些示例。

### 3.1. 使用ProcessBuilder打印Java版本

在第一个示例中，我们将运行带有一个参数的java命令以获取版本。

```java
Process process = new ProcessBuilder("java", "-version").start();
```

首先，我们创建ProcessBuilder对象，将命令和参数值传递给构造函数。接下来，我们使用start()方法启动进程以获取Process对象。

现在让我们看看如何处理输出：

```java
List<String> results = readOutput(process.getInputStream());

assertThat("Results should not be empty", results, is(not(empty())));
assertThat("Results should contain java version: ", results, hasItem(containsString("java version")));

int exitCode = process.waitFor();
assertEquals("No errors should be detected", 0, exitCode);
```

在这里，我们正在读取流程输出并验证内容是否符合我们的预期。在最后一步，我们使用process.waitFor()等待进程完成。

流程完成后，返回值会告诉我们流程是否成功。

需要牢记的几个要点：

-   参数的顺序必须正确
-   此外，在这个例子中，使用默认的工作目录和环境
-   我们故意在读取输出之前不调用process.waitFor()因为输出缓冲区可能会停止进程
-   我们假设java命令可通过PATH变量使用

### 3.2. 使用修改后的环境启动进程

在下一个示例中，我们将了解如何修改工作环境。

但在我们这样做之前，让我们先看看我们可以在默认环境中找到的信息类型：

```java
ProcessBuilder processBuilder = new ProcessBuilder();        
Map<String, String> environment = processBuilder.environment();
environment.forEach((key, value) -> System.out.println(key + value));
```

这只是打印出默认提供的每个变量条目：

```bash
PATH/usr/bin:/bin:/usr/sbin:/sbin
SHELL/bin/bash
...
```

现在我们要向我们的ProcessBuilder对象添加一个新的环境变量并运行一个命令来输出它的值：

```java
environment.put("GREETING", "Hola Mundo");

processBuilder.command("/bin/bash", "-c", "echo $GREETING");
Process process = processBuilder.start();
```

让我们分解这些步骤来理解我们做了什么：

-   在我们的环境中添加一个名为“GREETING”的变量，其值为“Hola Mundo”，这是一个标准的 Map<String, String>
-   这次，我们没有使用构造函数，而是直接通过command(String ... command)方法设置命令和参数。
-   然后我们按照前面的例子开始我们的过程。

为了完成示例，我们验证输出包含我们的问候语：

```java
List<String> results = readOutput(process.getInputStream());
assertThat("Results should not be empty", results, is(not(empty())));
assertThat("Results should contain java version: ", results, hasItem(containsString("Hola Mundo")));
```

### 3.3. 使用修改后的工作目录启动进程

有时更改工作目录可能很有用。在我们的下一个示例中，我们将看到如何做到这一点：

```java
@Test
public void givenProcessBuilder_whenModifyWorkingDir_thenSuccess() 
  throws IOException, InterruptedException {
    ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", "ls");

    processBuilder.directory(new File("src"));
    Process process = processBuilder.start();

    List<String> results = readOutput(process.getInputStream());
    assertThat("Results should not be empty", results, is(not(empty())));
    assertThat("Results should contain directory listing: ", results, contains("main", "test"));

    int exitCode = process.waitFor();
    assertEquals("No errors should be detected", 0, exitCode);
}
```

在上面的示例中，我们使用便捷方法directory(File directory)将工作目录设置为项目的src目录。然后我们运行一个简单的目录列表命令并检查输出是否包含子目录 main和test。

### 3.4. 重定向标准输入和输出

在现实世界中，我们可能希望将运行进程的结果捕获到日志文件中以供进一步分析。幸运的是，正如我们将在本例中看到的， ProcessBuilder API 内置了对此的支持。

默认情况下，我们的进程从管道读取输入。我们可以通过Process.getOutputStream()返回的输出流访问这个管道。

然而，正如我们很快就会看到的，标准输出可能会被重定向到另一个源，例如使用 redirectOutput方法的文件。在这种情况下，getOutputStream()将返回一个ProcessBuilder.NullOutputStream。

让我们回到我们最初的例子来打印出Java的版本。但是这次让我们将输出重定向到日志文件而不是标准输出管道：

```java
ProcessBuilder processBuilder = new ProcessBuilder("java", "-version");

processBuilder.redirectErrorStream(true);
File log = folder.newFile("java-version.log");
processBuilder.redirectOutput(log);

Process process = processBuilder.start();
```

在上面的示例中，我们创建了一个名为 log 的新临时文件，并告诉我们的ProcessBuilder将输出重定向到该文件 destination。

在最后一个片段中，我们只是检查getInputStream()是否确实为null以及我们文件的内容是否符合预期：

```java
assertEquals("If redirected, should be -1 ", -1, process.getInputStream().read());
List<String> lines = Files.lines(log.toPath()).collect(Collectors.toList());
assertThat("Results should contain java version: ", lines, hasItem(containsString("java version")));
```

现在让我们看一下这个例子的一个细微变化。例如，当我们希望附加到日志文件而不是每次都创建一个新文件时：

```java
File log = tempFolder.newFile("java-version-append.log");
processBuilder.redirectErrorStream(true);
processBuilder.redirectOutput(Redirect.appendTo(log));
```

提及对redirectErrorStream(true) 的调用也很重要。 如果出现任何错误，错误输出将合并到正常流程输出文件中。

当然，我们可以为标准输出和标准错误输出指定单独的文件：

```java
File outputLog = tempFolder.newFile("standard-output.log");
File errorLog = tempFolder.newFile("error.log");

processBuilder.redirectOutput(Redirect.appendTo(outputLog));
processBuilder.redirectError(Redirect.appendTo(errorLog));
```

### 3.5. 继承当前进程的I/O

在倒数第二个示例中，我们将看到inheritIO()方法的作用。当我们想将子进程的 I/O 重定向到当前进程的标准 I/O 时，可以使用这个方法：

```java
@Test
public void givenProcessBuilder_whenInheritIO_thenSuccess() throws IOException, InterruptedException {
    ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", "echo hello");

    processBuilder.inheritIO();
    Process process = processBuilder.start();

    int exitCode = process.waitFor();
    assertEquals("No errors should be detected", 0, exitCode);
}
```

在上面的示例中，通过使用inheritIO()方法，我们在 IDE 的控制台中看到了一个简单命令的输出。

在下一节中，我们将看看在Java9 中对ProcessBuilder API 进行了哪些添加。

## 4.Java9 添加

Java 9 向ProcessBuilder API引入了管道的概念：

```java
public static List<Process> startPipeline(List<ProcessBuilder> builders)

```

使用startPipeline方法，我们可以传递一个ProcessBuilder对象列表。然后，此静态方法将为每个ProcessBuilder启动一个Process。因此，创建了一个由标准输出和标准输入流链接的进程管道。

例如，如果我们想运行这样的东西：

```bash
find . -name .java -type f | wc -l
```

我们要做的是为每个独立的命令创建一个流程构建器并将它们组合成一个管道：

```java
@Test
public void givenProcessBuilder_whenStartingPipeline_thenSuccess()
  throws IOException, InterruptedException {
    List builders = Arrays.asList(
      new ProcessBuilder("find", "src", "-name", ".java", "-type", "f"), 
      new ProcessBuilder("wc", "-l"));

    List processes = ProcessBuilder.startPipeline(builders);
    Process last = processes.get(processes.size() - 1);

    List output = readOutput(last.getInputStream());
    assertThat("Results should not be empty", output, is(not(empty())));
}
```

在此示例中，我们正在搜索src目录中的所有 java 文件，并将结果通过管道传输到另一个进程以对它们进行计数。

要了解Java9 中对 Process API 所做的其他改进，请查看我们关于[Java 9 Process API Improvements](https://www.baeldung.com/java-9-process-api)的精彩文章。

## 5.总结

总而言之，在本教程中，我们详细探讨了java.lang.ProcessBuilder API。

首先，我们首先解释了 API 可以做什么，并总结了最重要的方法。

接下来，我们看一些实际的例子。最后，我们查看了Java9 中向 API 引入了哪些新增功能。