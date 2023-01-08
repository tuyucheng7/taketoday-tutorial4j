## 1. 概述

在本教程中，我们将解释为什么 JMX 在启动时打开三个端口。此外，我们将展示如何在Java中启动 JMX。之后，我们将展示如何限制打开的端口数。

## 2.JMX定义

让我们首先定义什么是 JMX 框架。Java[管理扩展](https://www.baeldung.com/java-management-extensions)(JMX)框架为管理Java应用程序提供了一个可配置、可伸缩且可靠的基础结构。此外，它还为应用程序的实时管理定义了 MBean 的概念。该框架允许在本地或远程管理应用程序。

## 3. 在Java中启用 JMX

现在让我们看看如何启用 JMX。对于Java1.5 及之前的版本，有一个系统属性com.sun.management.jmxremote。使用该属性启动的应用程序允许从本地和远程连接[JConsole 。](https://www.baeldung.com/java-management-extensions#1-connecting-from-the-client-side)另一方面，在没有该属性的情况下启动应用程序时，从 JConsole 中是不可见的。

但是，从Java6 及以上版本开始，该参数就不再需要了。该应用程序在启动后自动可供管理。此外，默认配置会自动分配端口并仅在本地公开。

## 4.JMX 端口

在我们的示例中，我们将使用Java6 或更高版本。首先，让我们创建一个具有无限循环的类。该类什么都不做，但它允许我们检查打开了哪些端口：

```java
public class JMXConfiguration {

    public static void main(String[] args) {
        while (true) {
            // to ensure application does not terminate
        }
    }
}
```

现在，我们将编译类并启动它：

```bash
java jmx.cn.tuyucheng.taketoday.JMXConfiguration
```

之后，我们可以检查分配给进程的 pid 并检查进程打开的端口：

```bash
netstat -ao | grep <pid>
```

结果，我们将获得应用程序公开的端口列表：

```bash
Active Connections
Proto  Local Address          Foreign Address        State           PID
TCP    127.0.0.1:55846        wujek:55845            ESTABLISHED     2604
```

此外，在重启的情况下，端口将会改变。它是随机分配的。此功能从Java6 开始可用，它自动为JavaAttach API 公开应用程序。换句话说，它通过本地进程自动公开 JConsole 连接的应用程序。

现在让我们通过向 JVM 提供选项来启用远程连接：

```bash
-Dcom.sun.management.jmxremote=true
-Dcom.sun.management.jmxremote.port=1234
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false
```

端口号是我们必须提供的强制参数，以便为远程连接公开 JMX。我们仅出于测试目的禁用身份验证和 SSL。

现在，[netstat](https://www.baeldung.com/linux/find-process-using-port#netstat)命令返回：

```bash
Proto  Local Address    Foreign Address State       PID
TCP    0.0.0.0:1234     wujek:0         LISTENING   11088
TCP    0.0.0.0:58738    wujek:0         LISTENING   11088
TCP    0.0.0.0:58739    wujek:0         LISTENING   11088

```

如我们所见，该应用程序公开了三个端口。RMI/JMX 公开了两个端口。第三个是用于本地连接的随机端口。

## 5. 限制开放端口数

首先，我们可以使用-XX:+DisableAttachMechanism选项禁用从 JConsole 公开应用程序以进行本地连接：

```bash
java -XX:+DisableAttachMechanism jmx.cn.tuyucheng.taketoday.JMXConfiguration
```

之后，应用程序不会公开任何 JMX/RMI 端口。

此外，从 JDK 16 开始，我们可以设置本地端口号：

```bash
java 
  -Dcom.sun.management.jmxremote=true 
  -Dcom.sun.management.jmxremote.local.port=1235 
  jmx.cn.tuyucheng.taketoday.JMXConfiguration
```

现在让我们更改配置并使用远程端口。

还有一个附加选项-Dcom.sun.management.jmxremote.rmi.port=1234允许我们将 RMI 端口设置为与 JMX 端口相同的值。现在，完整的命令是：

```bash
java 
  -Dcom.sun.management.jmxremote=true 
  -Dcom.sun.management.jmxremote.port=1234 
  -Dcom.sun.management.jmxremote.rmi.port=1234 
  -Dcom.sun.management.jmxremote.local.port=1235 
  -Dcom.sun.management.jmxremote.authenticate=false 
  -Dcom.sun.management.jmxremote.ssl=false 
  jmx.cn.tuyucheng.taketoday.JMXConfiguration
```

之后，netstat命令返回：

```bash
Proto  Local Address    Foreign Address State       PID
TCP    0.0.0.0:1234     wujek:0         LISTENING   19504
TCP    0.0.0.0:1235     wujek:0         LISTENING   19504
```

也就是说，应用程序只暴露两个端口，一个用于JMX/RMI 远程连接，一个用于本地连接。得益于此，我们可以完全控制暴露的端口，避免与其他进程暴露的端口发生冲突。

但是，当我们启用远程连接并禁用附加机制时：

```bash
java 
  -XX:+DisableAttachMechanism 
  -Dcom.sun.management.jmxremote=true 
  -Dcom.sun.management.jmxremote.port=1234 
  -Dcom.sun.management.jmxremote.rmi.port=1234 
  -Dcom.sun.management.jmxremote.authenticate=false 
  -Dcom.sun.management.jmxremote.ssl=false 
  jmx.cn.tuyucheng.taketoday.JMXConfiguration
```

然后，应用程序仍然暴露两个端口：

```bash
Proto Local Address     Foreign Address     State       PID
TCP   0.0.0.0:1234      wujek:0             LISTENING   9856
TCP   0.0.0.0:60565     wujek:0             LISTENING   9856
```

## 六，总结

在这篇简短的文章中，我们解释了如何在Java中启动 JMX。然后，我们展示了 JMX 在启动时打开了哪些端口。最后，我们介绍了如何限制 JMX 打开的端口数。