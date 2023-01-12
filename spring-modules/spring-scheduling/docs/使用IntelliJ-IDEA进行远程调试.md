## 1. 简介

远程调试使开发人员能够诊断服务器或其他进程上的独特错误。它提供了追踪那些烦人的运行时错误并识别性能瓶颈和资源池的方法。

在本教程中，我们将了解如何使用 JetBrains IntelliJ IDEA 进行远程调试。让我们首先通过更改 JVM 来准备示例应用程序。

## 2.配置JVM

我们将使用[Spring 调度程序示例应用程序](https://github.com/eugenp/tutorials/tree/master/spring-scheduling)轻松连接定期计划的任务并为其添加断点。

此外，IntelliJ IDEA 提供我们的 JVM 参数作为配置的一部分：

```bash
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=:5005
```

### 2.1. JVM 参数

除了JavaDebug Wire Protocol (JDWP) 配置——jdwp=transport=dt_socket——我们还看到了server、suspend和address参数。

服务器参数将JVM 配置为调试器的目标。suspend参数告诉 JVM在启动之前等待调试器客户端连接。最后，地址参数使用通配符主机和声明的端口。

那么，让我们构建调度程序应用程序：

```bash
mvn clean package
```

现在让我们启动应用程序，包括-agentlib:jdwp参数：

```bash
java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=:5005 
  target/gs-scheduling-tasks-0.1.0.jar
```

打开任何终端并运行命令。随着我们的应用程序启动，现在让我们切换到 IntelliJ。

## 3.在IntelliJ IDEA中运行配置

接下来，在 IntelliJ 中，我们为远程调试创建一个新的运行配置：

[![img](https://www.baeldung.com/wp-content/uploads/2019/11/run_configuration.png)](https://www.baeldung.com/wp-content/uploads/2019/11/run_configuration.png)

现在我们的应用程序正在运行，让我们通过单击“调试”按钮启动远程调试会话。

## 4.远程调试

接下来，我们打开ScheduleTask文件并在此处显示的第 36 行放置一个断点：

```java
public void reportCurrentTime() {
  log.info("The time is now {}", dateFormat.format(new Date()));
}
```

由于任务每五秒执行一次，添加后很快就会停止。因此，我们现在可以单步执行整个应用程序。

对于应用启动问题，我们将suspend标志改为n ，并在Application的main方法中下断点。

### 4.1. 限制

在远程调试时，有时日志记录和输出会让我们感到困惑。日志不会发送到 IDE 控制台，因此可以使用外部日志文件并将其映射到 IDE 中以获得更强大的调试能力。

还要记住，虽然远程调试是一个非常强大的工具，但生产环境不是调试的合适目标。

## 5.总结

正如我们在本文中介绍的那样，使用 IntelliJ 进行远程调试只需几个简短的步骤即可轻松设置和使用。

我们研究了如何配置我们的应用程序 JVM 进行调试，以及我们开发人员工具箱中这个重要工具的一些限制。