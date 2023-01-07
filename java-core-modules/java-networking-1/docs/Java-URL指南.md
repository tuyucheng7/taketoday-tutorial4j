## 1. 概述

在本文中，我们将探索Java网络编程的低级操作。我们将深入研究 URL。

URL 是对网络资源的引用或地址。简单地说，通过网络通信的Java代码可以使用java.net.URL类来表示资源的地址。

Java 平台附带内置网络支持，捆绑在java.net包中：

```java
import java.net.;
```

## 2. 创建网址

让我们首先创建一个java.net.URL对象，方法是使用它的构造函数并传入一个表示资源的人类可读地址的字符串：

```java
URL url = new URL("/a-guide-to-java-sockets");
```

我们刚刚创建了一个绝对 URL 对象。该地址包含到达所需资源所需的所有部分。

我们还可以创建一个相对 URL；假设我们有代表 Baeldung 主页的 URL 对象：

```java
URL home = new URL("http://baeldung.com");
```

接下来，让我们创建一个指向我们已知资源的新 URL；我们将使用另一个构造函数，它接受一个现有的 URL 和一个相对于该 URL 的资源名称：

```java
URL url = new URL(home, "a-guide-to-java-sockets");
```

我们现在已经创建了一个新的 URL 对象url相对于home；因此相对 URL 仅在基本 URL 的上下文中有效。

我们可以在测试中看到这一点：

```java
@Test
public void givenBaseUrl_whenCreatesRelativeUrl_thenCorrect() {
    URL baseUrl = new URL("http://baeldung.com");
    URL relativeUrl = new URL(baseUrl, "a-guide-to-java-sockets");
    
    assertEquals("http://baeldung.com/a-guide-to-java-sockets", 
      relativeUrl.toString());
}
```

但是，如果检测到相对 URL 在其组成部分中是绝对的，则忽略baseURL ：

```java
@Test
public void givenAbsoluteUrl_whenIgnoresBaseUrl_thenCorrect() {
    URL baseUrl = new URL("http://baeldung.com");
    URL relativeUrl = new URL(
      baseUrl, "/a-guide-to-java-sockets");
    
    assertEquals("http://baeldung.com/a-guide-to-java-sockets", 
      relativeUrl.toString());
}
```

最后，我们可以通过调用另一个接受 URL 字符串的组成部分的构造函数来创建 URL。我们将在介绍 URL 组件后的下一节中介绍这一点。

## 3. URL 组件

一个 URL 由几个部分组成——我们将在本节中探讨这些部分。

让我们首先看一下协议标识符和资源之间的分隔——这两个组件由一个冒号和两个正斜杠分隔，即：//。

如果我们有一个 URL，例如http://baeldung.com那么分隔符之前的部分http是协议标识符，而后面的部分是资源名称baeldung.com。

让我们看一下URL对象公开的 API。

### 3.1. 议定书

要检索协议——我们使用getProtocol()方法：

```java
@Test
public void givenUrl_whenCanIdentifyProtocol_thenCorrect(){
    URL url = new URL("http://baeldung.com");
    
    assertEquals("http", url.getProtocol());
}
```

### 3.2. 港口

要获取端口——我们使用getPort()方法：

```java
@Test
public void givenUrl_whenGetsDefaultPort_thenCorrect(){
    URL url = new URL("http://baeldung.com");
    
    assertEquals(-1, url.getPort());
    assertEquals(80, url.getDefaultPort());
}
```

请注意，此方法检索显式定义的端口。如果没有显式定义端口，它将返回 -1。

并且因为 HTTP 通信默认使用端口 80——没有定义端口。

这是一个我们确实有一个明确定义的端口的例子：

```java
@Test
public void givenUrl_whenGetsPort_thenCorrect(){
    URL url = new URL("http://baeldung.com:8090");
    
    assertEquals(8090, url.getPort());
}
```

### 3.3. 主人

主机是资源名称的一部分，紧跟在://分隔符之后，以域名扩展名结束，在我们的例子中是.com。

我们调用getHost()方法来检索主机名：

```java
@Test
public void givenUrl_whenCanGetHost_thenCorrect(){
    URL url = new URL("http://baeldung.com");
    
    assertEquals("baeldung.com", url.getHost());
}
```

### 3.4. 文件名

URL 中主机名之后的任何内容都称为资源的文件名。它可以包含路径和查询参数，也可以只包含一个文件名：

```java
@Test
public void givenUrl_whenCanGetFileName_thenCorrect1() {
    URL url = new URL("http://baeldung.com/guidelines.txt");
    
    assertEquals("/guidelines.txt", url.getFile());
}
```

假设 Baeldung 在 URL /articles?topic=java&version=8下有 java 8 文章。主机名之后的所有内容都是文件名：

```java
@Test
public void givenUrl_whenCanGetFileName_thenCorrect2() {
    URL url = new URL("http://baeldung.com/articles?topic=java&version=8");
    
    assertEquals("/articles?topic=java&version=8", url.getFile());
}
```

### 3.5. 路径参数

我们也可以只检查路径参数，在我们的例子中是/articles：

```java
@Test
public void givenUrl_whenCanGetPathParams_thenCorrect() {
    URL url = new URL("http://baeldung.com/articles?topic=java&version=8");
    
    assertEquals("/articles", url.getPath());
}
```

### 3.6. 查询参数

同样，我们可以检查查询参数topic =java&version=8：

```java
@Test
public void givenUrl_whenCanGetQueryParams_thenCorrect() {
    URL url = new URL("http://baeldung.com/articles?topic=java<em>&version=8</em>");
    
    assertEquals("topic=java<em>&version=8</em>", url.getQuery());
}
```

## 4. 使用组件创建 URL

由于我们现在已经查看了不同的 URL 组件及其在形成资源的完整地址中的位置，我们可以查看另一种通过传入组件部分来创建 URL 对象的方法。

第一个构造函数分别采用协议、主机名和文件名：

```java
@Test
public void givenUrlComponents_whenConstructsCompleteUrl_thenCorrect() {
    String protocol = "http";
    String host = "baeldung.com";
    String file = "/guidelines.txt";
    URL url = new URL(protocol, host, file);
    
    assertEquals("http://baeldung.com/guidelines.txt", url.toString());
}
```

请记住 filename 在这种情况下的含义，以下测试应该会更清楚：

```java
@Test
public void givenUrlComponents_whenConstructsCompleteUrl_thenCorrect2() {
    String protocol = "http";
    String host = "baeldung.com";
    String file = "/articles?topic=java&version=8";
    URL url = new URL(protocol, host, file);
    
    assertEquals("http://baeldung.com/articles?topic=java&version=8", url.toString());
}
```

第二个构造函数分别采用协议、主机名、端口号和文件名：

```java
@Test
public void givenUrlComponentsWithPort_whenConstructsCompleteUrl_
  thenCorrect() {
    String protocol = "http";
    String host = "baeldung.com";
    int port = 9000;
    String file = "/guidelines.txt";
    URL url = new URL(protocol, host, port, file);
    
    assertEquals(
      "http://baeldung.com:9000/guidelines.txt", url.toString());
}
```

## 5.总结

在本教程中，我们介绍了URL类并展示了如何在Java中使用它以编程方式访问网络资源。