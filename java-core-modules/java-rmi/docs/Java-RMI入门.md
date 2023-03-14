## 1. 概述

当两个 JVM 需要通信时，Java RMI 是我们必须实现的一种选择。在本文中，我们将启动一个展示JavaRMI 技术的简单示例。

## 2. 创建服务器

创建 RMI 服务器需要两个步骤：

1.  创建定义客户端/服务器契约的接口。
2.  创建该接口的实现。

### 2.1. 定义合同

首先，让我们为远程对象创建接口。该接口扩展了java.rmi.Remote标记接口。

此外，接口中声明的每个方法都会抛出java.rmi。远程异常：

```java
public interface MessengerService extends Remote {
    String sendMessage(String clientMessage) throws RemoteException;
}
```

但是请注意，只要Java类型实现java.io.Serializable ， RMI就支持方法签名的完整Java规范。

我们将在以后的章节中看到客户端和服务器将如何使用此接口。

对于服务器，我们将创建实现，通常称为远程对象。

对于客户端，RMI 库将动态创建一个名为Stub的实现。

### 2.2. 执行

此外，让我们实现远程接口，再次称为远程对象：

```java
public class MessengerServiceImpl implements MessengerService { 
 
    @Override 
    public String sendMessage(String clientMessage) { 
        return "Client Message".equals(clientMessage) ? "Server Message" : null;
    }

    public String unexposedMethod() { / code / }
}
```

请注意，我们已经从方法定义中删除了throws RemoteException子句。

我们的远程对象抛出RemoteException是不常见的，因为此异常通常保留给 RMI 库以向客户端引发通信错误。

将其排除在外还有一个好处，即使我们的实现与 RMI 无关。

此外，在远程对象中定义的任何其他方法，而不是在接口中定义的方法，对客户端来说都是不可见的。

## 3.注册服务

创建远程实现后，我们需要将远程对象绑定到 RMI 注册表。

### 3.1. 创建存根

首先，我们需要创建远程对象的存根：

```java
MessengerService server = new MessengerServiceImpl();
MessengerService stub = (MessengerService) UnicastRemoteObject
  .exportObject((MessengerService) server, 0);
```

我们使用静态UnicastRemoteObject.exportObject方法来创建存根实现。存根是通过底层 RMI 协议与服务器通信的神奇之处。

exportObject的第一个参数是远程服务器对象。

第二个参数是exportObject用于将远程对象导出到注册表的端口。

给出零值表示我们不关心exportObject使用哪个端口，这是典型的，因此是动态选择的。

不幸的是，不推荐使用没有端口号的exportObject()方法。

### 3.2. 创建注册表

我们可以在我们的服务器本地建立一个注册中心，或者作为一个单独的独立服务。

为简单起见，我们将创建一个本地服务器：

```java
Registry registry = LocateRegistry.createRegistry(1099);
```

这将创建一个注册表，存根可以由服务器绑定到该注册表并由客户端发现。

此外，我们还使用了createRegistry方法，因为我们正在创建服务器本地的注册表。

默认情况下，RMI 注册表在端口 1099 上运行。相反，也可以在createRegistry工厂方法中指定不同的端口。

但在独立的情况下，我们会调用getRegistry，将主机名和端口号作为参数传递。

### 3.3. 绑定存根

因此，让我们将存根绑定到注册表。RMI 注册表是一种命名工具，如 JNDI 等。我们可以在这里遵循类似的模式，将我们的存根绑定到一个唯一的键：

```java
registry.rebind("MessengerService", stub);

```

因此，远程对象现在可供任何可以找到注册表的客户端使用。

## 4.创建客户端

最后，让我们编写客户端来调用远程方法。

为此，我们将首先找到 RMI 注册表。此外，我们将使用有界唯一键查找远程对象存根。

最后，我们将调用sendMessage方法：

```java
Registry registry = LocateRegistry.getRegistry();
MessengerService server = (MessengerService) registry
  .lookup("MessengerService");
String responseMessage = server.sendMessage("Client Message");
String expectedMessage = "Server Message";
 
assertEquals(expectedMessage, responseMessage);
```

因为我们在本地机器和默认端口 1099 上运行 RMI 注册表，所以我们不向 getRegistry 传递任何参数。

事实上，如果注册表位于不同的主机或不同的端口上，我们可以提供这些参数。

一旦我们使用注册表查找存根对象，我们就可以调用远程服务器上的方法。

## 5.总结

在本教程中，我们简要介绍了JavaRMI 以及它如何成为客户端-服务器应用程序的基础。请继续关注有关 RMI 的一些独特功能的其他帖子！