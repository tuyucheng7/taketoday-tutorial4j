## 1. 概述

在本文中，我们将重点关注网络接口以及如何在Java中以编程方式访问它们。

简而言之，网络接口是设备与其任何网络连接之间的互连点。

在日常用语中，我们通过术语网络接口卡 (NIC) 来指代它们——但它们不一定都是硬件形式。

例如，流行的本地主机 IP 127.0.0.1是我们在测试 Web 和网络应用程序时经常使用的环回接口——它不是直接的硬件接口。

当然，系统往往会有多个活跃的网络连接，比如有线以太网、WIFI、蓝牙等。

在Java中，我们可以用来直接与它们交互的主要 API 是java.net.NetworkInterface类。因此，为了快速开始，让我们导入完整的包：

```java
import java.net.;
```

## 2. 为什么访问网络接口？

大多数Java程序可能不会直接与它们交互；然而，当我们确实需要这种低级别访问时，会有一些特殊情况。

其中最突出的是系统有多个卡，你希望自由选择特定的接口来使用带有 socket 的接口。在这种情况下，我们通常知道名称但不一定知道 IP 地址。

通常，当我们想要建立到特定服务器地址的套接字连接时：

```java
Socket socket = new Socket();
socket.connect(new InetSocketAddress(address, port));
```

这样，系统将选择一个合适的本地地址，绑定到它并通过其网络接口与服务器通信。但是，这种方法不允许我们选择自己的。

我们将在这里做一个假设；我们不知道地址，但我们知道名字。仅出于演示目的，假设我们希望通过环回接口进行连接，按照惯例，它的名称是lo，至少在 Linux 和 Windows 系统上是这样，在 OSX 上它是lo0：

```java
NetworkInterface nif = NetworkInterface.getByName("lo");
Enumeration<InetAddress> nifAddresses = nif.getInetAddresses();

Socket socket = new Socket();
socket.bind(new InetSocketAddress(nifAddresses.nextElement(), 0));
socket.connect(new InetSocketAddress(address, port));
```

所以我们首先检索附加到lo 的网络接口，检索附加到它的地址，创建一个套接字，将它绑定到我们在编译时甚至不知道的任何枚举地址，然后连接。

NetworkInterface对象包含名称和分配给它的一组 IP 地址。因此，绑定到这些地址中的任何一个都将保证通过此接口进行通信。

这并没有真正说明 API 有什么特别之处。我们知道，如果我们希望我们的本地地址是 localhost，那么只要添加绑定代码，第一个片段就足够了。

此外，由于 localhost 有一个众所周知的地址127.0.0.1并且我们可以轻松地将套接字绑定到它，因此我们永远不必真正完成所有几个步骤。

但是，在你的情况下，lo可能代表其他接口，例如蓝牙 – net1、无线网络 – net0或以太网 – eth0。在这种情况下，你不会在编译时知道 IP 地址。

## 3. 检索网络接口

在本节中，我们将探讨用于检索可用接口的其他可用 API。在上一节中，我们只看到了其中一种方法；getByName ()静态方法。

值得注意的是NetworkInterface类没有任何公共构造函数，因此我们当然无法创建新实例。相反，我们将使用可用的 API 来检索一个。

到目前为止我们看到的 API 用于按指定名称搜索网络接口：

```java
@Test
public void givenName_whenReturnsNetworkInterface_thenCorrect() {
    NetworkInterface nif = NetworkInterface.getByName("lo");

    assertNotNull(nif);
}
```

如果没有名称，则返回null ：

```java
@Test
public void givenInExistentName_whenReturnsNull_thenCorrect() {
    NetworkInterface nif = NetworkInterface.getByName("inexistent_name");

    assertNull(nif);
}
```

第二个API是getByInetAddress()，它也需要我们提供一个已知的参数，这次我们可以提供IP地址：

```java
@Test
public void givenIP_whenReturnsNetworkInterface_thenCorrect() {
    byte[] ip = new byte[] { 127, 0, 0, 1 };

    NetworkInterface nif = NetworkInterface.getByInetAddress(
      InetAddress.getByAddress(ip));

    assertNotNull(nif);
}
```

或主机名称：

```java
@Test
public void givenHostName_whenReturnsNetworkInterface_thenCorrect()  {
    NetworkInterface nif = NetworkInterface.getByInetAddress(
      InetAddress.getByName("localhost"));

    assertNotNull(nif);
}
```

或者，如果你特定于本地主机：

```java
@Test
public void givenLocalHost_whenReturnsNetworkInterface_thenCorrect() {
    NetworkInterface nif = NetworkInterface.getByInetAddress(
      InetAddress.getLocalHost());

    assertNotNull(nif);
}
```

另一种选择也是显式使用环回接口：

```java
@Test
public void givenLoopBack_whenReturnsNetworkInterface_thenCorrect() {
    NetworkInterface nif = NetworkInterface.getByInetAddress(
      InetAddress.getLoopbackAddress());

    assertNotNull(nif);
}
```

第三种方法自Java7 以来才可用，它是通过其索引获取网络接口：

```java
NetworkInterface nif = NetworkInterface.getByIndex(int index);
```

最后一种方法涉及使用getNetworkInterfaces API。它返回系统中所有可用网络接口的枚举。我们需要在循环中检索返回的对象，标准习惯用法使用List：

```java
Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

for (NetworkInterface nif: Collections.list(nets)) {
    //do something with the network interface
}
```

## 4. 网络接口参数

检索对象后，我们可以从中获得很多有价值的信息。其中最有用的一项是分配给它的 IP 地址列表。

我们可以使用两个 API 获取 IP 地址。第一个 API 是getInetAddresses()。它返回一个InetAddress实例的枚举，我们可以按我们认为合适的方式对其进行处理：

```java
@Test
public void givenInterface_whenReturnsInetAddresses_thenCorrect()  {
    NetworkInterface nif = NetworkInterface.getByName("lo");
    Enumeration<InetAddress> addressEnum = nif.getInetAddresses();
    InetAddress address = addressEnum.nextElement();

    assertEquals("127.0.0.1", address.getHostAddress());
}
```

第二个 API 是getInterfaceAddresses()。它返回比InetAddress实例更强大的InterfaceAddress实例列表。例如，除了 IP 地址，你可能还对广播地址感兴趣：

```java
@Test
public void givenInterface_whenReturnsInterfaceAddresses_thenCorrect() {
    NetworkInterface nif = NetworkInterface.getByName("lo");
    List<InterfaceAddress> addressEnum = nif.getInterfaceAddresses();
    InterfaceAddress address = addressEnum.get(0);

    InetAddress localAddress=address.getAddress();
    InetAddress broadCastAddress = address.getBroadcast();

    assertEquals("127.0.0.1", localAddress.getHostAddress());
    assertEquals("127.255.255.255",broadCastAddress.getHostAddress());
}
```

除了分配给它的名称和 IP 地址之外，我们还可以访问有关接口的网络参数。要检查它是否已启动并正在运行：

```java
@Test
public void givenInterface_whenChecksIfUp_thenCorrect() {
    NetworkInterface nif = NetworkInterface.getByName("lo");

    assertTrue(nif.isUp());
}
```

检查它是否是环回接口：

```java
@Test
public void givenInterface_whenChecksIfLoopback_thenCorrect() {
    NetworkInterface nif = NetworkInterface.getByName("lo");

    assertTrue(nif.isLoopback());
}
```

要检查它是否代表点对点网络连接：

```java
@Test
public void givenInterface_whenChecksIfPointToPoint_thenCorrect() {
    NetworkInterface nif = NetworkInterface.getByName("lo");

    assertFalse(nif.isPointToPoint());
}
```

或者如果它是一个虚拟接口：

```java
@Test
public void givenInterface_whenChecksIfVirtual_thenCorrect() {
    NetworkInterface nif = NetworkInterface.getByName("lo");
    assertFalse(nif.isVirtual());
}
```

检查是否支持多播：

```java
@Test
public void givenInterface_whenChecksMulticastSupport_thenCorrect() {
    NetworkInterface nif = NetworkInterface.getByName("lo");

    assertTrue(nif.supportsMulticast());
}
```

或者检索其物理地址，通常称为 MAC 地址：

```java
@Test
public void givenInterface_whenGetsMacAddress_thenCorrect() {
    NetworkInterface nif = NetworkInterface.getByName("lo");
    byte[] bytes = nif.getHardwareAddress();

    assertNotNull(bytes);
}
```

另一个参数是最大传输单元，它定义了可以通过该接口传输的最大数据包大小：

```java
@Test
public void givenInterface_whenGetsMTU_thenCorrect() {
    NetworkInterface nif = NetworkInterface.getByName("net0");
    int mtu = nif.getMTU();

    assertEquals(1500, mtu);
}
```

## 5.总结

在本文中，我们展示了网络接口、如何以编程方式访问它们以及我们为什么需要访问它们。