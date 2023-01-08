## 1. 概述

在本文中，我们将学习如何从Java应用程序执行 shell 命令。

首先，我们将使用 . Runtime类提供的exec()方法。然后，我们将了解更可定制的ProcessBuilder 。

## 2. 操作系统依赖

Shell 命令依赖于操作系统，因为它们的行为因系统而异。因此，在我们创建任何进程来运行我们的 shell 命令之前，我们需要了解运行 JVM 的操作系统。

此外，在 Windows 上，shell 通常称为cmd.exe。相反，在 Linux 和 macOS 上，shell 命令是使用/bin/sh运行的。为了在这些不同的机器上兼容，我们可以通过编程方式附加cmd.exe(如果在 Windows 机器上)或 /bin/ sh否则。例如，我们可以通过读取System类的“os.name”属性来检查运行代码的机器是否是 Windows 机器：

```java
boolean isWindows = System.getProperty("os.name")
  .toLowerCase().startsWith("windows");
```

## 3. 输入输出

通常，我们需要连接流程的输入和输出流。详细来说，InputStream充当标准输入，OutputStream充当流程的标准输出。我们必须始终使用输出流。否则，我们的流程将不会返回并将永远挂起。

让我们实现一个名为StreamGobbler 的常用类，它使用一个InputStream：

```java
private static class StreamGobbler implements Runnable {
    private InputStream inputStream;
    private Consumer<String> consumer;

    public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
        this.inputStream = inputStream;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        new BufferedReader(new InputStreamReader(inputStream)).lines()
          .forEach(consumer);
    }
}
```

此类实现了Runnable接口，这意味着任何[Executor](https://www.baeldung.com/java-executor-service-tutorial)都可以执行它。

## 4.运行时.exec()

接下来，我们将使用.exec()方法生成一个新进程，并使用之前创建的StreamGobler。

例如，我们可以列出用户主目录中的所有目录，然后将其打印到控制台：

```java
String homeDirectory = System.getProperty("user.home");
Process process;
if (isWindows) {
    process = Runtime.getRuntime()
      .exec(String.format("cmd.exe /c dir %s", homeDirectory));
} else {
    process = Runtime.getRuntime()
      .exec(String.format("/bin/sh -c ls %s", homeDirectory));
}
StreamGobbler streamGobbler = 
  new StreamGobbler(process.getInputStream(), System.out::println);
Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);

int exitCode = process.waitFor();
assert exitCode == 0;

future.get(); // waits for streamGobbler to finish
```

在这里，我们使用.newSingleThreadExecutor()创建了一个新的子进程，然后使用.submit()运行包含 shell 命令的进程。此外，.submit()返回一个[Future](https://www.baeldung.com/guava-futures-listenablefuture#1-future)对象，我们利用它来检查过程的结果。另外，请确保在返回的对象上调用.get()方法以等待计算完成。

注意：JDK 18 弃用了Runtime类中的.exec(String command)。

### 4.1. 手柄管

目前，无法使用.exec()处理管道。幸运的是，管道是 shell 的一个特性。因此，我们可以在要使用管道的地方创建整个命令并将其传递给.exec()：

```java
if (IS_WINDOWS) {
    process = Runtime.getRuntime()
        .exec(String.format("cmd.exe /c dir %s | findstr "Desktop"", homeDirectory));
} else {
    process = Runtime.getRuntime()
        .exec(String.format("/bin/sh -c ls %s | grep "Desktop"", homeDirectory));
}
```

在这里，我们列出了用户家中的所有目录并搜索“桌面”文件夹。

## 5.进程生成器

或者，我们可以使用 ProcessBuilder [，](https://www.baeldung.com/java-lang-processbuilder-api)它比运行时方法更受欢迎，因为我们可以自定义它而不是仅仅运行一个字符串命令。

简而言之，通过这种方法，我们能够：

-   更改我们的 shell 命令正在运行的工作目录。目录()
-   通过向.environment()提供键值映射来更改环境变量
-   以自定义方式重定向输入和输出流
-   使用.inheritIO()将它们都继承到当前 JVM 进程的流中

同样，我们可以运行与上一个示例相同的 shell 命令：

```java
ProcessBuilder builder = new ProcessBuilder();
if (isWindows) {
    builder.command("cmd.exe", "/c", "dir");
} else {
    builder.command("sh", "-c", "ls");
}
builder.directory(new File(System.getProperty("user.home")));
Process process = builder.start();
StreamGobbler streamGobbler = 
  new StreamGobbler(process.getInputStream(), System.out::println);
Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);
int exitCode = process.waitFor();
assert exitCode == 0;
future.get(10, TimeUnit.SECONDS)
```

## 六，总结

正如我们在本快速教程中所见，我们可以通过两种不同的方式在Java中执行 shell 命令。

通常，如果我们计划自定义生成进程的执行，例如，更改其工作目录，我们应该考虑使用ProcessBuilder。