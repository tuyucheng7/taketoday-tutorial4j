## 1. 概述

在本教程中，我们将深入了解Process API。

要更深入地了解如何使用Process执行 shell 命令，我们可以[在此处](https://www.baeldung.com/run-shell-command-in-java)参考我们之前的教程 。

它所指的进程是一个正在执行的应用程序。Process类提供了与这些进程交互的方法，包括提取输出、执行输入、监视生命周期、检查退出状态以及销毁(杀死)它。

## 2. 使用进程类编译运行Java程序

让我们看一个在Process API的帮助下编译和运行另一个Java程序的例子：

```java
@Test
public void whenExecutedFromAnotherProgram_thenSourceProgramOutput3() throws IOException {
 
    Process process = Runtime.getRuntime()
      .exec("javac -cp src srcmainjavacombaeldungjava9processOutputStreamExample.java");
    process = Runtime.getRuntime() 
      .exec("java -cp src/main/java process.java9.cn.tuyucheng.taketoday.OutputStreamExample");
    BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
    int value = Integer.parseInt(output.readLine());
 
    assertEquals(3, value);
}
```

因此，在现有Java代码中执行Java代码的应用实际上是无限的。

## 3.创建过程

我们的Java应用程序可以调用在我们的计算机系统中运行的任何受操作系统限制的应用程序。

因此我们可以执行应用程序。让我们看看我们可以使用 Process API 运行哪些不同的用例。

ProcessBuilder类允许我们在应用程序中创建子流程。

让我们看一个打开基于 Windows 的记事本应用程序的演示：

```java
ProcessBuilder builder = new ProcessBuilder("notepad.exe");
Process process = builder.start();
```

## 4.销毁进程

Process也为我们提供了销毁子流程或进程的方法。虽然，应用程序如何被杀死是平台相关的。

让我们看看可能的不同用例。

### 4.1. 通过引用销毁进程

假设我们正在使用 Windows 操作系统并想要生成记事本应用程序并销毁它。

和以前一样，我们可以使用ProcessBuilder类和start()方法创建记事本应用程序的实例。

然后我们可以在Process对象上调用 destroy() 方法。

### 4.2. 通过 ID 销毁进程

我们还可以终止在我们的操作系统中运行的进程，这些进程可能不是由我们的应用程序创建的。

执行此操作时应谨慎，因为我们可能会在不知不觉中破坏可能使操作系统不稳定的关键进程。

我们首先需要通过查看任务管理器，找出当前运行进程的进程ID，找出pid。

让我们看一个例子：

```java
long pid = / PID to kill /;
Optional<ProcessHandle> optionalProcessHandle = ProcessHandle.of(pid);
optionalProcessHandle.ifPresent(processHandle -> processHandle.destroy());

```

### 4.3. 强行销毁进程

在执行 destroy ()方法时，子进程将被终止，正如我们在本文前面看到的那样。

在destroy()不起作用的情况下，我们可以选择destroyForcibly()。

我们应该始终首先从destroy()方法开始。之后，我们可以通过执行isAlive()来快速检查子流程是否存在。

如果它返回 true 则执行destroyForcibly()：

```java
ProcessBuilder builder = new ProcessBuilder("notepad.exe");
Process process = builder.start();
process.destroy();
if (process.isAlive()) {
    process.destroyForcibly();
}
```

## 5.等待进程完成

我们还有两个重载方法，通过它们我们可以确保我们可以等待一个过程的完成。

### 5.1. 等待() 

该方法执行时，会将当前执行进程线程置于阻塞等待状态，除非子进程终止。

让我们看一下这个例子：

```java
ProcessBuilder builder = new ProcessBuilder("notepad.exe");
Process process = builder.start();
assertThat(process.waitFor() >= 0);

```

从上面的例子我们可以看出当前线程要继续执行它会一直等待子进程线程结束。一旦子进程结束，当前线程将继续执行。

### 5.2. waitfor(long timeOut, TimeUnit 时间) 

该方法执行时，会将当前执行进程线程置于阻塞等待状态，除非子进程终止或超时。

让我们看一下这个例子：

```java
ProcessBuilder builder = new ProcessBuilder("notepad.exe");
Process process = builder.start();
assertFalse(process.waitFor(1, TimeUnit.SECONDS));
```

从上面的例子我们可以看出当前线程要继续执行它会一直等待子进程线程结束或者指定的时间间隔已经过去。

执行此方法时，如果子进程已经退出，它将返回一个布尔值 true，如果在子进程退出之前等待时间已经过去，它将返回一个布尔值 false。

## 6.退出值() 

运行此方法时，当前线程不会等待子进程终止或销毁，但是，如果子进程未终止，它将抛出IllegalThreadStateException 。

另一种方法是，如果子进程已成功终止，那么它将导致进程的退出值。

它可以是任何可能的正整数。

让我们看一个示例，当子进程已成功终止时，exitValue()方法返回一个正整数：

```java
@Test
public void 
  givenSubProcess_whenCurrentThreadWillNotWaitIndefinitelyforSubProcessToEnd_thenProcessExitValueReturnsGrt0() 
  throws IOException {
    ProcessBuilder builder = new ProcessBuilder("notepad.exe");
    Process process = builder.start();
    assertThat(process.exitValue() >= 0);
}
```

## 7.活着() 

当我们想要执行业务处理时，无论进程是否存在，都是主观的。

我们可以执行快速检查以查找返回布尔值的进程是否处于活动状态。

让我们看一个简单的例子：

```java
ProcessBuilder builder = new ProcessBuilder("notepad.exe");
Process process = builder.start();
Thread.sleep(10000);
process.destroy();
assertTrue(process.isAlive());
```

## 8. 处理流程流

默认情况下，创建的子流程没有终端或控制台。它的所有标准 I/O(即 stdin、stdout、stderr)操作都将发送到父进程。因此，父进程可以使用这些流向子进程提供输入并从中获取输出。

因此，这给了我们很大的灵活性，因为它让我们可以控制子流程的输入/输出。

### 8.1. 获取错误流() 

有趣的是，我们可以把子流程产生的错误取出来进行业务处理。

之后，我们可以根据我们的要求进行具体的业务处理检查。

让我们看一个例子：

```java
@Test
public void givenSubProcess_whenEncounterError_thenErrorStreamNotNull() throws IOException {
    Process process = Runtime.getRuntime().exec(
      "javac -cp src srcmainjavacombaeldungjava9processProcessCompilationError.java");
    BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    String errorString = error.readLine();
    assertNotNull(errorString);
}
```

### 8.2. 获取输入流() 

我们还可以获取子进程生成的输出并在父进程中使用，从而允许进程之间共享信息：

```java
@Test
public void givenSourceProgram_whenReadingInputStream_thenFirstLineEquals3() throws IOException {
    Process process = Runtime.getRuntime().exec(
      "javac -cp src srcmainjavacombaeldungjava9processOutputStreamExample.java");
    process = Runtime.getRuntime()
      .exec("java -cp  src/main/java process.java9.cn.tuyucheng.taketoday.OutputStreamExample");
    BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
    int value = Integer.parseInt(output.readLine());
 
    assertEquals(3, value);
}
```

### 8.3. 获取tOutputStream() 

我们可以将输入从父进程发送到子进程：

```java
Writer w = new OutputStreamWriter(process.getOutputStream(), "UTF-8");
w.write("send to childn");
```

### 8.4. 过滤流程流

这是与选择性运行进程交互的完全有效的用例。

Process为我们提供了根据特定谓词有选择地过滤正在运行的进程的工具。

之后我们就可以对这个选择性的流程集进行业务操作了：

```java
@Test
public void givenRunningProcesses_whenFilterOnProcessIdRange_thenGetSelectedProcessPid() {
    assertThat(((int) ProcessHandle.allProcesses()
      .filter(ph -> (ph.pid() > 10000 && ph.pid() < 50000))
      .count()) > 0);
}
```

## 9.总结

Process是操作系统级交互的强大类。触发终端命令以及启动、监视和终止应用程序。

[有关Java9 Process API 的更多信息，请在此处](https://www.baeldung.com/java-9-process-api)查看我们的文章。