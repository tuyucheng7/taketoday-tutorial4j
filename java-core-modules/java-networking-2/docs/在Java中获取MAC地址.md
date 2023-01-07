## 1. 概述

在本教程中，我们将使用Java获取本地计算机的 MAC 地址。

MAC 地址是物理网络接口卡的唯一标识符。

我们将仅介绍 MAC 地址，但有关网络接口的更一般概述，请参阅[使用Java中的网络接口](https://www.baeldung.com/java-network-interfaces)。

## 2.例子

在下面的示例中，我们将使用java.net.NetworkInterface 和java.net.InetAddress API。

### 2.1. 机器本地主机

首先，让我们获取机器本地主机的 MAC 地址：

```java
InetAddress localHost = InetAddress.getLocalHost();
NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
byte[] hardwareAddress = ni.getHardwareAddress();

```

由于 NetworkInterface #getHardwareAddress返回一个字节数组， 我们可以格式化结果：

```java
String[] hexadecimal = new String[hardwareAddress.length];
for (int i = 0; i < hardwareAddress.length; i++) {
    hexadecimal[i] = String.format("%02X", hardwareAddress[i]);
}
String macAddress = String.join("-", hexadecimal);
```

请注意我们如何使用String#format将数组中的每个字节格式化为十六进制数。

之后，我们可以用“-”(破折号)连接所有格式化元素。

### 2.2. 本地IP

其次，让我们获取给定本地 IP 地址的 MAC 地址：

```java
InetAddress localIP = InetAddress.getByName("192.168.1.108");
NetworkInterface ni = NetworkInterface.getByInetAddress(localIP);
byte[] macAddress = ni.getHardwareAddress();
```

再次注意我们如何获得 MAC 地址的字节数组。

### 2.3. 所有网络接口

最后，让我们获取机器上所有网络接口的 MAC 地址：

```java
Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
while (networkInterfaces.hasMoreElements()) {
    NetworkInterface ni = networkInterfaces.nextElement();
    byte[] hardwareAddress = ni.getHardwareAddress();
    if (hardwareAddress != null) {
        String[] hexadecimalFormat = new String[hardwareAddress.length];
        for (int i = 0; i < hardwareAddress.length; i++) {
            hexadecimalFormat[i] = String.format("%02X", hardwareAddress[i]);
        }
        System.out.println(String.join("-", hexadecimalFormat));
    }
}
```

由于 getNetworkInterfaces返回物理接口和虚拟接口，我们需要过滤掉虚拟接口。

例如，我们可以通过对 getHardwareAddress进行空检查来做到这一点。

## 3.总结

在本快速教程中，我们探讨了获取本地计算机 MAC 地址的不同方法。