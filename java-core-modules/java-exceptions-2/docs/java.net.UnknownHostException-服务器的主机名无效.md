## 1. 概述

在本教程中，我们将通过示例了解UnknownHostException的原因。我们还将讨论防止和处理异常的可能方法。

## 2. 什么时候抛出异常？

UnknownHostException表示无法确定主机名的 IP 地址。 它可能是由于主机名中的拼写错误而发生的：

```java
String hostname = "http://locaihost";
URL url = new URL(hostname);
HttpURLConnection con = (HttpURLConnection) url.openConnection();
con.getResponseCode();
```

上面的代码抛出UnknownHostException，因为拼写错误的locaihost没有指向任何 IP 地址。

UnknownHostException的另一个可能原因是 DNS 传播延迟或 DNS 配置错误。

新的 DNS 条目最多可能需要 48 小时才能传播到整个 Internet。

## 3.如何预防？

首先防止异常发生比事后处理要好。一些防止异常的技巧是：

1.  仔细检查主机名：确保没有拼写错误，并删除所有空格。
2.  检查系统的 DNS 设置：确保 DNS 服务器已启动且可访问，如果主机名是新的，请等待 DNS 服务器赶上。

## 4.如何处理？

UnknownHostException扩展了IOException，这是一个[已检查的异常](https://www.baeldung.com/java-checked-unchecked-exceptions#checked)。与任何其他已检查的异常类似，我们必须抛出它或用try-catch块包围它。

让我们处理示例中的异常：

```java
try {
    con.getResponseCode();
} catch (UnknownHostException e) {
    con.disconnect();
}
```

发生UnknownHostException时关闭连接是一个好习惯。大量浪费性的打开连接会导致应用程序内存不足。

## 5.总结

在本文中，我们了解了导致UnknownHostException的原因、如何防止它以及如何处理它。