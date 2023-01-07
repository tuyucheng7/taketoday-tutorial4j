## 1. 概述

在本教程中，我们将了解如何在Java程序中使用[curl工具。](https://curl.haxx.se/)

Curl是一种网络工具，用于使用 HTTP、FTP、TELNET 和 SCP 等协议在服务器和curl客户端之间传输数据 。

## 二、curl的基本使用

我们可以使用ProcessBuilder从Java执行curl命令——一个用于构建[Process](https://www.baeldung.com/java-process-api)类 实例的辅助类。

让我们看一个直接向操作系统发送命令的例子：

```java
String command =
  "curl -X GET https://postman-echo.com/get?foo1=bar1&foo2=bar2";
ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));

```

 首先，我们在将命令变量传递给ProcessBuilder构造函数之前创建 命令变量。

这里值得注意的是，如果 curl可执行文件不在我们的系统路径中，我们必须在我们的命令字符串中提供它的完整路径。

然后我们可以为ProcessBuilder 设置工作目录 并启动进程：

```java
processBuilder.directory(new File("/home/"));
Process process = processBuilder.start();

```

从这里开始，我们可以通过从Process实例访问它来获取InputStream：

```java
InputStream inputStream = process.getInputStream();

```

处理完成后，我们可以通过以下方式获取退出代码：

```java
int exitCode = process.exitValue();

```

如果我们需要运行其他命令，我们可以通过在String数组中传递新命令和参数来重用ProcessBuilder实例：

```java
processBuilder.command(
  new String[]{"curl", "-X", "GET", "https://postman-echo.com?foo=bar"});

```

最后，要终止每个Process实例，我们应该使用：

```java
process.destroy();

```

## 3. ProcessBuilder的简单替代品 

作为使用 ProcessBuilder 类的替代方法，我们可以使用 Runtime.getRuntime()来获取Process类的实例。

让我们看看另一个示例curl命令——这次使用POST请求：

```bash
curl -X POST https://postman-echo.com/post --data foo1=bar1&foo2=bar2
```

现在，让我们使用Runtime.getRuntime() 方法执行命令 ：

```java
String command = "curl -X POST https://postman-echo.com/post --data foo1=bar1&foo2=bar2";
Process process = Runtime.getRuntime().exec(command);

```

首先，我们再次创建Process类的实例 ，但这次使用 Runtime.getRuntime()。我们可以通过调用 getInputStream()方法获得一个InputStream ，就像我们之前的例子一样：

```java
process.getInputStream();
```

当不再需要该实例时，我们应该通过调用 destroy() 方法释放系统资源。

## 4。总结

在本文中，我们展示了在Java中使用curl的两种方式。