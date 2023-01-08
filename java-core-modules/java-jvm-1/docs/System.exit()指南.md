## 1. 概述

在本教程中，我们将了解System.exit在Java中的含义。

我们将了解它的用途、使用地点和使用方法。我们还将看到使用不同的状态代码调用它有什么不同。

## 2.什么是System.exit？

System.exit是一个void方法。它需要一个退出代码，并将其传递给调用脚本或程序。

以零代码退出意味着正常退出：

```java
System.exit(0);
```

我们可以将任何整数作为参数传递给该方法。非零状态码被认为是异常退出。

调用System.exit方法终止当前运行的 JVM 并退出程序。此方法不会正常返回。

这意味着System.exit之后的后续代码实际上是不可访问的 ，而且编译器不知道它。

```java
System.exit(0);
System.out.println("This line is unreachable");
```

使用System.exit(0)关闭程序不是一个好主意。它为我们提供了从main方法退出的相同结果，并停止执行后续行，调用System.exit的线程也会阻塞，直到 JVM 终止。如果关闭挂钩向该线程提交任务，则会导致死锁。

## 3. 我们为什么需要它？

System.exit的典型用例是出现异常情况，我们需要立即退出程序。

此外，如果我们必须从 main 方法以外的地方终止程序，System.exit是实现它的一种方法。

## 4. 我们什么时候需要它？

脚本依赖于它调用的命令的退出代码是很常见的。如果这样的命令是Java应用程序，那么System.exit可以方便地发送此退出代码。

例如，我们可以返回异常退出代码，而不是抛出异常，然后调用脚本可以解释该异常退出代码。

或者，我们可以使用System.exit来调用我们已注册的任何关闭挂钩。可以设置这些挂钩来清理持有的资源并从其他非[守护](https://www.baeldung.com/java-daemon-thread)线程安全退出。

## 5. 一个简单的例子

在这个例子中，我们尝试读取一个文件，如果它存在，我们从中打印一行。如果该文件不存在，我们从 catch 块中使用System.exit退出程序。

```java
try {
    BufferedReader br = new BufferedReader(new FileReader("file.txt"));
    System.out.println(br.readLine());
    br.close();
} catch (IOException e) {
    System.exit(2);
} finally {
    System.out.println("Exiting the program");
}
```

在这里，我们必须注意，如果找不到文件，则不会执行 finally 块。因为catch块上的System.exit退出JVM，不允许finally块执行。

## 6. 选择状态码

我们可以传递任何整数作为状态代码，但是，一般做法是状态代码为 0 的System.exit是正常的，其他是异常退出。

请注意，这只是一种“良好做法”，并不是编译器会关心的严格规则。

此外，值得注意的是，当我们从命令行调用Java程序时，会考虑状态代码。

在下面的示例中，当我们尝试执行SystemExitExample.class 时，如果它通过使用非零状态代码调用System.exit退出 JVM，则不会打印以下回显。

```java
java SystemExitExample && echo "I will not be printed"
```

为了使我们的程序能够与其他标准工具进行通信，我们可以考虑遵循相关系统用于通信的标准代码。

Linux 文档项目准备的[具有特殊含义的退出代码文档提供了一个保留代码列表。](https://tldp.org/LDP/abs/html/exitcodes.html)它还建议针对特定情况使用哪些代码。

## 七、总结

在本教程中，我们讨论了System.exit在何时使用以及如何使用它的工作原理。

[在使用应用程序服务器和其他常规应用程序时，使用异常处理](https://www.baeldung.com/java-exceptions)或简单的返回语句退出程序是一种很好的做法。System.exit方法的使用更适合基于脚本的应用程序或解释状态代码的任何地方。