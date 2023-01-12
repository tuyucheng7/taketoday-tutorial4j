## 1. 概述

在本教程中，我们将了解如何向[Jsoup](https://www.baeldung.com/java-with-jsoup)添加代理支持。

## 2. 使用代理的常见原因

我们可能希望对Jsoup[使用代理有两个主要原因。](https://www.baeldung.com/java-connect-via-proxy-server)

### 2.1. 组织代理背后的用法

组织使用代理控制 Internet 访问是很常见的。如果我们尝试通过代理本地网络访问 Jsoup，我们将得到一个异常：

```java
java.net.SocketTimeoutException: connect timed out
```

当我们看到这个错误时，我们需要先为 Jsoup 设置代理，然后再尝试访问网络外的任何 URL。

### 2.2. 防止 IP 封锁

使用 Jsoup 代理的另一个常见原因是防止网站阻止 IP 地址。

换句话说，使用代理(或多个轮换代理)可以让我们更可靠地解析 HTML，减少我们的代码因 IP 地址被阻止或禁止而停止工作的可能性。

## 3.设置

使用 Maven 时，我们需要将[Jsoup 依赖](https://search.maven.org/classic/#search|ga|1|g%3A"org.jsoup" AND a%3A"jsoup")项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.13.1</version>
</dependency>
```

在 Gradle 中，我们必须在build.gradle中声明我们的依赖：

```groovy
compile 'org.jsoup:jsoup:1.13.1'
```

## 4.通过主机和端口属性添加代理支持

向 Jsoup 添加代理支持非常简单。我们需要做的就是在构建Connection对象时调用[proxy(String, int)](https://jsoup.org/apidocs/org/jsoup/Connection.html#proxy(java.lang.String,int))方法：

```java
Jsoup.connect("https://spring.io/blog")
  .proxy("127.0.0.1", 1080)
  .get();
```

在这里，我们设置用于此请求的 HTTP 代理，第一个参数表示代理主机名，第二个参数表示代理端口。

## 5.通过代理对象添加代理支持

或者，要使用Proxy类将代理添加到 Jsoup ，我们调用Connection对象的[proxy(java.net.Proxy)](https://jsoup.org/apidocs/org/jsoup/Connection.html#proxy(java.net.Proxy))方法：

```java
Proxy proxy = new Proxy(Proxy.Type.HTTP, 
  new InetSocketAddress("127.0.0.1", 1080));

Jsoup.connect("https://spring.io/blog")
  .proxy(proxy)
  .get();
```

此方法采用一个由代理类型(通常是 HTTP 类型)和[InetSocketAddress](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/InetSocketAddress.html)(分别包装代理的主机名和端口的类)组成的Proxy对象。

## 六. 总结

在本教程中，我们探索了两种向 Jsoup 添加代理支持的不同方法。

首先，我们了解了如何使用采用主机和端口属性的 Jsoup 方法来完成此操作。其次，我们学习了如何使用代理对象作为参数来获得相同的结果。