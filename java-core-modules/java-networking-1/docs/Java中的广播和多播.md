## 1. 概述

在本文中，我们描述了如何在Java中处理one-to-all(广播)和one-to-many(多播)通信。本文概述的[广播和多播概念](https://www.baeldung.com/cs/multicast-vs-broadcast-anycast-unicast)基于UDP协议。

我们首先快速回顾一下数据报和广播，以及它是如何在Java中实现的。我们还研究了广播的缺点，并提出多播作为广播的替代方案。

最后，我们通过讨论[IPv4和IPv6](https://www.baeldung.com/cs/ipv4-vs-ipv6)中对这两种寻址方法的支持来得出总结。

## 2. 数据报回顾

根据数据报的[官方定义](https://docs.oracle.com/javase/tutorial/networking/datagrams/)，“数据报是通过网络发送的独立的、自包含的消息，其到达、到达时间和内容都没有保证”。

在Java中，java.net包公开了可用于通过UDP协议进行通信的DatagramPacket和DatagramSocket类。UDP通常用于低延迟比保证交付更重要的场景，例如音频/视频流、网络发现等。

要了解有关Java中的UDP和数据报的更多信息，请参阅[Java中的UDP指南](https://www.baeldung.com/udp-in-java)。

## 3. 广播

广播是一种一对一的通信，即旨在将数据报发送到网络中的所有节点。**与点对点通信的情况不同，我们不必知道目标主机的IP地址**。相反，使用广播地址。

根据IPv4协议，广播地址是一个逻辑地址，连接到网络的设备可以在该地址上接收数据包。在我们的示例中，我们使用特定的IP地址255.255.255.255，这是本地网络的广播地址。

根据定义，将本地网络连接到其他网络的路由器不会转发发送到此默认广播地址的数据包。稍后我们还将展示如何遍历所有NetworkInterfaces并将数据包发送到它们各自的广播地址。

首先，我们演示如何广播消息。为此，我们需要在套接字上调用setBroadcast()方法，让它知道要广播数据包：

```java
public class BroadcastingClient {
    private static DatagramSocket socket = null;

    public static void main(String[] args) throws IOException {
        broadcast("Hello", InetAddress.getByName("255.255.255.255"));
    }

    public static void broadcast(String broadcastMessage, InetAddress address) throws IOException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);

        byte[] buffer = broadcastMessage.getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);
        socket.send(packet);
        socket.close();
    }
}
```

下一个片段展示了如何遍历所有NetworkInterfaces以找到它们的广播地址：

```java
List<InetAddress> listAllBroadcastAddresses() throws SocketException {
    List<InetAddress> broadcastList = new ArrayList<>();
    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
    while (interfaces.hasMoreElements()) {
        NetworkInterface networkInterface = interfaces.nextElement();

        if (networkInterface.isLoopback() || !networkInterface.isUp()) {
            continue;
        }

        networkInterface.getInterfaceAddresses().stream() 
            .map(a -> a.getBroadcast())
            .filter(Objects::nonNull)
            .forEach(broadcastList::add);
    }
    return broadcastList;
}
```

一旦我们有了广播地址列表，我们就可以为每个地址执行上面显示的broadcast()方法中的代码。

接收方**无需特殊代码即可接收广播消息**。我们可以重用接收普通UDP数据报的相同代码。[Java中的UDP指南](https://www.baeldung.com/udp-in-java)包含有关此主题的更多详细信息。

## 4. 多播

广播是低效的，因为数据包被发送到网络中的所有节点，无论它们是否有兴趣接收通信。这可能是一种资源浪费。

多播解决了这个问题，只向感兴趣的消费者发送数据包。**多播基于组成员概念**，其中多播地址代表每个组。

在IPv4中，224.0.0.0到239.255.255.255之间的任何地址都可以用作多播地址。只有订阅组的那些节点才能接收传送到该组的数据包。

在Java中，MulticastSocket用于接收发送到多播IP的数据包。以下示例演示了MulticastSocket的用法：

```java
public class MulticastReceiver extends Thread {
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];

    public void run() {
        socket = new MulticastSocket(4446);
        InetAddress group = InetAddress.getByName("230.0.0.0");
        socket.joinGroup(group);
        while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
            if ("end".equals(received)) {
                break;
            }
        }
        socket.leaveGroup(group);
        socket.close();
    }
}
```

将MulticastSocket绑定到端口后，我们调用joinGroup()方法，并将多播IP作为参数。这是能够接收发布到该组的数据包所必需的。leaveGroup()方法可用于离开组。

以下示例显示如何发布到多播IP：

```java
public class MulticastPublisher {
    private DatagramSocket socket;
    private InetAddress group;
    private byte[] buf;

    public void multicast(String multicastMessage) throws IOException {
        socket = new DatagramSocket();
        group = InetAddress.getByName("230.0.0.0");
        buf = multicastMessage.getBytes();

        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4446);
        socket.send(packet);
        socket.close();
    }
}
```

## 5. 广播和IPv6

IPv4支持三种类型的寻址：单播、广播和组播。从理论上讲，广播是一种一对多的通信，即从设备发送的数据包有可能到达整个互联网。

由于显而易见的原因这是不受欢迎的，因此IPv4广播的范围大大缩小了。多播也是广播的更好替代方案，它出现得晚得多，因此在采用方面滞后。

**在IPv6中，多播支持已成为强制性的，并且没有明确的广播概念**。多播已得到扩展和改进，因此所有广播功能现在都可以通过某种形式的多播来实现。

在IPv6中，地址最左边的位用于确定其类型。对于组播地址，前8位全为1，即FF00::/8。此外，第113-116位表示地址的范围，可以是以下4种之一：Global、Site-local、Link-local、Node-local。

除了单播和组播之外，IPv6还支持任播，一个数据包可以发送给组内的任何成员，但不必发送给所有成员。

## 6. 总结

在本文中，我们探讨了使用UDP协议的一对多和一对多类型通信的概念。我们看到了如何在Java中实现这些概念的示例。

最后，我们还研究了IPv4和IPv6支持。