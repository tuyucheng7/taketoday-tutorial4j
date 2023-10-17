## 1. 概述

[JGroups](http://jgroups.org/)是一个用于可靠消息交换的Java API，它具有一个简单的接口，可提供：

-   灵活的协议栈，包括TCP和UDP
-   大型消息的分片和重组
-   可靠的单播和多播
-   故障检测
-   流量控制

以及许多其他功能。

在本教程中，我们将创建一个简单的应用程序，用于在应用程序之间交换String消息并在新应用程序加入网络时为其提供共享状态。

## 2. 设置

### 2.1 Maven依赖

我们需要向我们的pom.xml添加一个依赖项：

```xml
<dependency>
    <groupId>org.jgroups</groupId>
    <artifactId>jgroups</artifactId>
    <version>4.0.10.Final</version>
</dependency>
```

### 2.2 联网

默认情况下，JGroups将尝试使用[IPV6]()。根据我们的系统配置，这可能会导致应用程序无法通信。

为了避免这种情况，我们在此处运行应用程序时将java.net.preferIPv4Stack属性设置为true：

```shell
java -Djava.net.preferIPv4Stack=true cn.tuyucheng.taketoday.jgroups.JGroupsMessenger
```

## 3. JChannels

我们与JGroups网络的连接是JChannel。该通道加入集群并发送和接收消息，以及有关网络状态的信息。

### 3.1 创建Channel

我们创建一个带有配置文件路径的JChannel。如果我们省略文件名，它将在当前工作目录中查找udp.xml。

我们将使用明确命名的配置文件创建一个通道：

```java
JChannel channel = new JChannel("src/main/resources/udp.xml");
```

JGroups配置可能非常复杂，但默认的UDP和TCP配置对于大多数应用程序来说已经足够了。我们已将UDP文件包含在我们的代码中，并将在本教程中使用它。

有关配置传输的更多信息，请参阅[此处](http://jgroups.org/manual4/index.html#_transport_protocols)的JGroups手册。

### 3.2 连接Channel

创建通道后，我们需要加入集群。**集群是一组交换消息的节点**。

加入集群需要集群名称：

```java
channel.connect("Tuyucheng");
```

如果集群不存在，第一个尝试加入集群的节点将创建它，我们会在下面看到这个过程的实际效果。

### 3.3 命名Channel

节点由名称标识，以便对等方可以发送定向消息并接收有关谁进入和离开集群的通知。JGroups将自动分配一个名称，或者我们可以设置自己的名称：

```java
channel.name("user1");
```

我们将在下面使用这些名称来跟踪节点何时进入和离开集群。

### 3.4 关闭Channel

**如果我们希望对等方及时收到我们已退出的通知，通道清理是必不可少的**。

我们用它的close方法关闭一个JChannel：

```java
channel.close()
```

## 4. 集群视图变化

创建JChannel后，我们现在可以查看集群中对等点的状态并与它们交换消息。

**JGroups在View类中维护集群状态**。每个通道都有一个网络视图。当视图改变时，它通过viewAccepted()回调传递。

对于本教程，我们将扩展ReceiverAdaptor API类，该类实现应用程序所需的所有接口方法。**这是实现回调的推荐方式**。

下面我们将viewAccepted添加到我们的应用程序中：

```java
public void viewAccepted(View newView) {

    private View lastView;

    if (lastView == null) {
        System.out.println("Received initial view:");
        newView.forEach(System.out::println);
    } else {
        System.out.println("Received new view.");

        List<Address> newMembers = View.newMembers(lastView, newView);
        System.out.println("New members: ");
        newMembers.forEach(System.out::println);

        List<Address> exMembers = View.leftMembers(lastView, newView);
        System.out.println("Exited members:");
        exMembers.forEach(System.out::println);
    }
    lastView = newView;
}

```

每个视图都包含一个Address对象集合，代表集群的每个成员。JGroups提供了将一个视图与另一个视图进行比较的便捷方法，我们用它来检测集群的新成员或退出成员。

## 5. 发送消息

JGroups中的消息处理非常简单，消息包含一个字节数组和对应于发送方和接收方的Address对象。

对于本教程，我们使用从命令行读取的字符串，但很容易看出应用程序如何交换其他数据类型。

### 5.1 广播消息

Message是用一个目标和一个字节数组创建的；JChannel为我们设置了发送者。**如果目标为null，则整个集群都会收到消息**。

我们将从命令行接收文本并将其发送到集群：

```java
System.out.print("Enter a message: ");
String line = in.readLine().toLowerCase();
Message message = new Message(null, line.getBytes());
channel.send(message);
```

如果我们运行程序的多个实例并发送此消息(在我们实现下面的receive()方法之后)，则所有实例都会收到它，包括发送者。

### 5.2 阻塞我们的消息

如果我们不想看到我们的消息，我们可以为此设置一个属性：

```java
channel.setDiscardOwnMessages(true);
```

当我们运行前面的测试时，消息发送者不会收到它的广播消息。

### 5.3 私信

发送直接消息需要有效的地址。如果我们按名称引用节点，我们需要一种方法来查找Address。幸运的是，我们有视图。

当前视图始终可以从JChannel获得：

```java
private Optional<address> getAddress(String name) { 
    View view = channel.view(); 
    return view.getMembers().stream()
        .filter(address -> name.equals(address.toString()))
        .findAny(); 
}
```

地址名称可通过类的toString()方法获得，因此我们只需在集群成员集合中搜索我们想要的名称。

因此，我们可以从控制台接收一个名字，找到相关的目的地，并发送一条直接消息：

```java
Address destination = null;
System.out.print("Enter a destination: ");
String destinationName = in.readLine().toLowerCase();
destination = getAddress(destinationName)
        .orElseThrow(() -> new Exception("Destination not found"); 
Message message = new Message(destination, "Hi there!"); 
channel.send(message);
```

## 6. 接收消息

我们可以发送消息，现在让我们尝试接收消息。

我们重写ReceiverAdaptor的空receive方法：

```java
public void receive(Message message) {
    String line = Message received from: " 
      + message.getSrc() 
      + " to: " + message.getDest() 
      + " -> " + message.getObject();
    System.out.println(line);
}
```

因为我们知道消息包含一个字符串，所以我们可以安全地将getObject()传递给System.out。

## 7. 状态交换

当节点进入网络时，它可能需要检索有关集群的状态信息。JGroups为此提供了一种状态转移机制。

当一个节点加入集群时，它只需调用getState()。集群通常从组中最早的成员(协调者)那里检索状态。

让我们向我们的应用程序添加一个广播消息计数器，我们将添加一个新的成员变量并在receive()中递增它：

```java
private Integer messageCount = 0;

public void receive(Message message) {
    String line = "Message received from: " 
      + message.getSrc() 
      + " to: " + message.getDest() 
      + " -> " + message.getObject();
    System.out.println(line);

    if (message.getDest() == null) {
        messageCount++;
        System.out.println("Message count: " + messageCount);
    }
}

```

我们检查一个空目的地，因为如果我们计算直接消息，每个节点将有一个不同的数字。

接下来，我们重写ReceiverAdaptor中的另外两个方法：

```java
public void setState(InputStream input) {
    try {
        messageCount = Util.objectFromStream(new DataInputStream(input));
    } catch (Exception e) {
        System.out.println("Error deserialing state!");
    }
    System.out.println(messageCount + " is the current messagecount.");
}

public void getState(OutputStream output) throws Exception {
    Util.objectToStream(messageCount, new DataOutputStream(output));
}

```

与消息类似，JGroups以字节数组的形式传输状态。

JGroups向协调器提供一个InputStream来写入状态，并为新节点提供一个OutputStream来读取。API提供了用于序列化和反序列化数据的便利类。

请注意，在生产代码中，对状态信息的访问必须是线程安全的。

最后，在我们连接到集群后，我们将对getState()的调用添加到我们的启动中：

```java
channel.connect(clusterName);
channel.getState(null, 0);
```

getState()接收请求状态的目的地和以毫秒为单位的超时。空目标表示协调器，0表示不超时。

当我们使用一对节点运行这个应用程序并交换广播消息时，我们看到messageCount增加。

然后，如果我们添加第三个客户端或停止并启动其中一个，我们将看到新连接的节点打印正确的messageCount。

## 8. 总结

在本教程中，我们使用JGroups创建了一个用于交换消息的应用程序。我们使用API来监控哪些节点连接到集群和离开集群，并在新节点加入时将集群状态传输到新节点。