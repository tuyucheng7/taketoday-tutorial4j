## 一、概述

在某些应用程序中，我们可能需要从互联网上下载一个网页并将其内容提取为字符串。一个流行的用例是网络抓取或内容解析。

在本教程中，我们将使用[Jsoup](https://www.baeldung.com/java-with-jsoup)和*HttpURLConnection*下载示例网页。

## *2. 使用HttpURLConnection*下载网页

*[HttpURLConnection](https://www.baeldung.com/java-http-request)* 是 URLConnection 的子类*。* **它有助于连接到使用 HTTP作为其协议的统一资源定位器 ( [URL](https://www.baeldung.com/java-url-vs-uri#uri-and-url)** ) 。该类包含处理[HTTP](https://www.baeldung.com/java-9-http-client) 请求的不同方法。

让我们使用*HttpURLConnection下载*[示例网页](https://example.com/)：

```java
@Test
void givenURLConnection_whenRetrieveWebpage_thenWebpageIsNotNullAndContainsHtmlTag() throws IOException {
    
    URL url = new URL("https://example.com");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseBuilder.append(line);
        }
    
        assertNotNull(responseBuilder);
        assertTrue(responseBuilder.toString()
          .contains("<html>"));
    }
}复制
```

在这里，我们创建一个表示网页地址的*URL 对象。**接下来，我们创建一个HttpURLConnection* 实例并调用*URL对象的**openConnection()*方法。这将打开到网页的连接。此外，我们将请求方法设置为 GET 以获取网页内容。

*然后，我们创建一个新的BufferedReader* 和*InputStreamReader* 实例来帮助从网页中读取数据。InputStreamReader类有助于将原始字节转换为*BufferedReader*可以读取的*字符*。

最后，我们通过从*BufferedReader*读取并将行连接在一起，将网页转换为字符串*。*我们使用*StringBuilder*对象来有效地连接这些行。

## 3. 使用 Jsoup 下载网页

Jsoup 是一个流行的开源 Java 库，用于处理 HTML。它有助于获取 URL 并提取其数据。**它的主要优势之一是使用 HTML DOM 方法和 CSS 选择器从 URL 中抓取 HTML**。

要开始使用 Jsoup，我们需要将其依赖项添加到我们的依赖项管理器中。让我们将[Jsoup](https://search.maven.org/search?q=a:jsoup AND g:org.jsoup)依赖项添加到*pom.xml*：

```xml
<dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.15.4</version>
</dependency>复制
```

以下是使用 Jsoup 下载网页的示例：

```java
@Test
void givenJsoup_whenRetrievingWebpage_thenWebpageDocumentIsNotNullAndContainsHtmlTag() throws IOException {
        
    Document document = Jsoup.connect("https://www.example.com").get();
    String webpage = document.html();
        
    assertNotNull(webpage);
    assertTrue(webpage.contains("<html>"));
}复制
```

*在此示例中，我们创建了一个Document* 实例，并使用 Jsoup.connect() 建立了到示例站点的连接*。Jsoup.connect()*有助于建立到 URL 的连接并将其内容检索为*Document*对象。

接下来，我们调用*get()* 方法，该方法向指定的 URL 发送 GET 请求。它将响应返回为*Document*。

最后，我们将提取出来的内容存入一个*String*类型的可变*网页*中。我们通过在*Document对象上调用**html()*方法来完成此操作。

## 4。结论

在本文中，我们学习了两种用 Java 下载网页的方法。我们使用了*HttpURLConnection*类和 Jsoup 来下载网页的内容。这两种方法都可以使用，但 Jsoup 似乎更容易使用。