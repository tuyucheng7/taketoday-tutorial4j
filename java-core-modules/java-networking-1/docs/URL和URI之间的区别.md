## 1. 概述

在这篇简短的文章中，我们将了解 URI 和 URL 之间的主要区别，并通过实施示例来突出显示这些区别。

## 2.URI和URL

了解它们的定义后，它们之间的区别就很简单了：

-   统一资源标识符 (URI) - 允许完整标识任何抽象或物理资源的字符序列
-   统一资源定位符 (URL) - URI 的一个子集，除了标识资源可用的位置外，还描述了访问它的主要机制

现在我们可以得出总结，每个 URL 都是一个 URI，但反之则不然，我们稍后会看到。

### 2.1. 句法

每个 URI，无论它是否是 URL，都遵循特定的形式：

```plaintext
scheme:[//authority][/path][?query][#fragment]
```

其中各部分说明如下：

-   方案- 对于 URL，是用于访问资源的协议的名称，对于其他 URI，是指在该方案中分配标识符的规范的名称
-   authority - 可选部分，包括用户身份验证信息、主机和可选端口
-   路径- 它用于在其方案和权限范围内识别资源
-   查询- 与路径一起用于识别资源的附加数据。对于 URL，这是查询字符串
-   片段- 资源特定部分的可选标识符

为了轻松识别特定 URI 是否也是 URL，我们可以检查其 scheme。每个 URL 都必须以以下任何一种方案开头：ftp、http、https、 gopher、mailto、news、nntp、telnet、wais、file或prospero。如果不是以它开头，则它不是 URL。

现在我们知道了语法，让我们看一些例子。下面是一个 URI 列表，其中只有前三个是 URL：

```plaintext
ftp://ftp.is.co.za/rfc/rfc1808.txt
https://tools.ietf.org/html/rfc3986
mailto:john@doe.com

tel:+1-816-555-1212
urn:oasis:names:docbook:dtd:xml:4.1
urn:isbn:1234567890
```

## 3. URI 和 URLJavaAPI 差异

在本节中，我们将通过示例演示Java 提供的URI和URL类之间的主要区别。

### 3.1. 实例化

创建URI和URL实例非常相似，这两个类都提供了几个接受其大部分部分的构造函数，但是，只有URI类有一个构造函数来指定语法的所有部分：

```java
@Test
public void whenCreatingURIs_thenSameInfo() throws Exception {
    URI firstURI = new URI(
      "somescheme://theuser:thepassword@someauthority:80"
      + "/some/path?thequery#somefragment");
    
    URI secondURI = new URI(
      "somescheme", "theuser:thepassword", "someuthority", 80,
      "/some/path", "thequery", "somefragment");

    assertEquals(firstURI.getScheme(), secondURI.getScheme());
    assertEquals(firstURI.getPath(), secondURI.getPath());
}

@Test
public void whenCreatingURLs_thenSameInfo() throws Exception {
    URL firstURL = new URL(
      "http://theuser:thepassword@somehost:80"
      + "/path/to/file?thequery#somefragment");
    URL secondURL = new URL("http", "somehost", 80, "/path/to/file");

    assertEquals(firstURL.getHost(), secondURL.getHost());
    assertEquals(firstURL.getPath(), secondURL.getPath());
}
```

URI类还提供了一个实用方法来创建一个不会抛出已检查异常的新实例：

```java
@Test
public void whenCreatingURI_thenCorrect() {
    URI uri = URI.create("urn:isbn:1234567890");
    
    assertNotNull(uri);
}
```

URL类不提供这样的方法。

由于 URL 必须以前面提到的方案之一开头，因此尝试使用不同的方案创建对象将导致异常：

```java
@Test(expected = MalformedURLException.class)
public void whenCreatingURLs_thenException() throws Exception {
    URL theURL = new URL("otherprotocol://somehost/path/to/file");

    assertNotNull(theURL);
}
```

这两个类中还有其他构造函数，要全部发现它们，请参阅[URI](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/URI.html)和[URL](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/URL.html)文档。

### 3.2. 在 URI 和 URL 实例之间转换

URI 和 URL 之间的转换非常简单：

```java
@Test
public void givenObjects_whenConverting_thenCorrect()
  throws MalformedURLException, URISyntaxException {
    String aURIString = "http://somehost:80/path?thequery";
    URI uri = new URI(aURIString);
    URL url = new URL(aURIString);

    URL toURL = uri.toURL();
    URI toURI = url.toURI();

    assertNotNull(url);
    assertNotNull(uri);
    assertEquals(toURL.toString(), toURI.toString());
}
```

但是，尝试转换非 URL URI 会导致异常：

```java
@Test(expected = MalformedURLException.class)
public void givenURI_whenConvertingToURL_thenException()
  throws MalformedURLException, URISyntaxException {
    URI uri = new URI("somescheme://someauthority/path?thequery");

    URL url = uri.toURL();

    assertNotNull(url);
}
```

### 3.3. 打开远程连接

由于 URL 是对远程资源的有效引用，因此Java提供了打开与该资源的连接并获取其内容的方法：

```java
@Test
public void givenURL_whenGettingContents_thenCorrect()
  throws MalformedURLException, IOException {
    URL url = new URL("http://courses.baeldung.com");
    
    String contents = IOUtils.toString(url.openStream());

    assertTrue(contents.contains("<!DOCTYPE html>"));
}
```

需要注意的是， URL equals() 和 hashcode() 函数的实现可能会触发DNS命名服务解析 IP 地址。这是不一致的，并且会根据网络连接给出不同的结果，并且还需要很长时间才能运行。已知该实现与虚拟主机不兼容，不应使用。我们建议改用URI。

## 4 . 总结

在这篇快速文章中，我们提供了一些示例来演示Java中URI和URL之间的区别。

我们强调了创建两个对象的实例以及将一个对象转换为另一个对象时的差异。我们还展示了URL具有打开指向资源的远程连接的方法。