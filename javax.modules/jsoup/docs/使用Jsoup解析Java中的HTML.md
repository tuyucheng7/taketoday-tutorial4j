## 1. 概述

[Jsoup](https://jsoup.org/)是一个开源Java库，主要用于从 HTML 中提取数据。它还允许操作和输出 HTML。它拥有稳定的开发线、出色的文档以及流畅灵活的 API。Jsoup 也可用于解析和构建 XML。

在本教程中，我们将使用[Spring 博客](https://spring.io/blog)来说明一个抓取练习，该练习演示了 jsoup 的几个特性：

-   加载：获取 HTML 并将其解析为文档
-   过滤：将需要的数据选到Elements中并遍历
-   Extracting：获取节点的属性、文本、HTML
-   修改：添加/编辑/删除节点和编辑它们的属性

## 2.Maven依赖

要在的项目中使用 jsoup 库，请将依赖项添加到的pom.xml：

```xml
<dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.10.2</version>
</dependency>
```

可以在 Maven 中央存储库中找到最新版本的[jsoup 。](https://search.maven.org/classic/#search|ga|1|g%3A"org.jsoup" AND a%3A"jsoup")

## 3. Jsoup 概览

Jsoup 加载页面 HTML 并构建相应的 DOM 树。这棵树的工作方式与浏览器中的 DOM 相同，提供类似于 jQuery 和普通 JavaScript 的方法来选择、遍历、操作文本/HTML/属性以及添加/删除元素。

如果熟悉客户端选择器和 DOM 遍历/操作，会发现 jsoup 非常熟悉。检查打印页面段落的难易程度：

```java
Document doc = Jsoup.connect("http://example.com").get();
doc.select("p").forEach(System.out::println);
```

请记住，jsoup 仅解释 HTML — 它不解释 JavaScript。因此，在支持 JavaScript 的浏览器中加载页面后通常发生的 DOM 更改将不会在 jsoup 中看到。

## 4.加载

加载阶段包括获取 HTML 并将其解析为[Document](https://jsoup.org/apidocs/org/jsoup/nodes/Document.html)。Jsoup 保证像现代浏览器一样解析任何 HTML，从最无效的到完全有效的 HTML。它可以通过加载String、InputStream、File或 URL 来实现。

让我们从 Spring 博客 URL加载一个文档：

```java
String blogUrl = "https://spring.io/blog";
Document doc = Jsoup.connect(blogUrl).get();
```

注意get方法，它代表一个 HTTP GET 调用。还可以使用post方法执行 HTTP POST (或者可以使用接收 HTTP方法类型作为参数的方法)。

如果需要检测异常状态码(如404)，应该捕获HttpStatusException异常：

```java
try {
   Document doc404 = Jsoup.connect("https://spring.io/will-not-be-found").get();
} catch (HttpStatusException ex) {
   //...
}
```

有时，连接需要更加定制化。Jsoup.connect(…)返回一个Connection，它允许设置用户代理、引荐来源网址、连接超时、cookie、发布数据和标头等：

```java
Connection connection = Jsoup.connect(blogUrl);
connection.userAgent("Mozilla");
connection.timeout(5000);
connection.cookie("cookiename", "val234");
connection.cookie("cookiename", "val234");
connection.referrer("http://google.com");
connection.header("headersecurity", "xyz123");
Document docCustomConn = connection.get();
```

由于连接遵循流畅的接口，可以在调用所需的 HTTP 方法之前链接这些方法：

```java
Document docCustomConn = Jsoup.connect(blogUrl)
  .userAgent("Mozilla")
  .timeout(5000)
  .cookie("cookiename", "val234")
  .cookie("anothercookie", "ilovejsoup")
  .referrer("http://google.com")
  .header("headersecurity", "xyz123")
  .get();
```

[可以通过浏览相应的 Javadoc](https://jsoup.org/apidocs/org/jsoup/Connection.html)了解有关连接设置的更多信息。

## 5.过滤

现在我们已将 HTML 转换为Document，是时候浏览它并找到我们要查找的内容了。这是与 jQuery/JavaScript 的相似之处更为明显，因为它的选择器和遍历方法相似。

### 5.1. 选择

Document select方法接收表示选择器的字符串，使用与[CSS 或 JavaScript 中](https://api.jquery.com/category/selectors/)相同的选择器语法，并检索匹配的Elements列表。此列表可以为空但不能为null。

让我们看一下使用select方法的一些选择：

```java
Elements links = doc.select("a");
Elements sections = doc.select("section");
Elements logo = doc.select(".spring-logo--container");
Elements pagination = doc.select("#pagination_control");
Elements divsDescendant = doc.select("header div");
Elements divsDirect = doc.select("header > div");
```

还可以使用受浏览器 DOM 启发的更明确的方法，而不是通用的select：

```java
Element pag = doc.getElementById("pagination_control");
Elements desktopOnly = doc.getElementsByClass("desktopOnly");
```

由于Element是Document的超类，可以在[Document](https://jsoup.org/apidocs/org/jsoup/nodes/Document.html)和[Element](https://jsoup.org/apidocs/org/jsoup/nodes/Element.html) Javadocs中了解有关使用选择方法的更多信息。

### 5.2. 遍历

遍历意味着在 DOM 树中导航。Jsoup 提供了对Document、一组Elements或特定Element进行操作的方法，使可以导航到节点的父节点、兄弟节点或子节点。

此外，可以跳转到一组Elements中的第一个、最后一个和第 n 个(使用基于 0 的索引)元素：

```java
Element firstSection = sections.first();
Element lastSection = sections.last();
Element secondSection = sections.get(2);
Elements allParents = firstSection.parents();
Element parent = firstSection.parent();
Elements children = firstSection.children();
Elements siblings = firstSection.siblingElements();
```

还可以遍历选择。事实上，任何类型的Elements都可以迭代：

```java
sections.forEach(el -> System.out.println("section: " + el));
```

可以将选择限制为先前的选择(子选择)：

```java
Elements sectionParagraphs = firstSection.select(".paragraph");
```

## 6.提取

我们现在知道如何访问特定元素，所以是时候获取它们的内容了——即它们的属性、HTML 或子文本。

看一下这个示例，它从博客中选择第一篇文章并获取其日期、第一部分文本，最后是其内部和外部 HTML：

```java
Element firstArticle = doc.select("article").first();
Element timeElement = firstArticle.select("time").first();
String dateTimeOfFirstArticle = timeElement.attr("datetime");
Element sectionDiv = firstArticle.select("section div").first();
String sectionDivText = sectionDiv.text();
String articleHtml = firstArticle.html();
String outerHtml = firstArticle.outerHtml();
```

以下是选择和使用选择器时要牢记的一些提示：

-   依赖浏览器的“查看源代码”功能，而不仅仅是页面 DOM，因为它可能已经更改(在浏览器控制台选择可能会产生与 jsoup 不同的结果)
-   [了解的选择器](https://jsoup.org/apidocs/index.html?org/jsoup/select/Selector.html)，因为它们有很多，至少以前见过它们总是好的；掌握选择器需要时间
-   [使用选择器的游乐场](https://try.jsoup.org/)来试验它们(在此处粘贴示例 HTML)
-   减少对页面变化的依赖：以最小和最不妥协的选择器为目标(例如，更喜欢基于 id. 的选择器)

## 7.修改

修改包括设置元素的属性、文本和 HTML，以及添加和删除元素。它是针对之前由 jsoup 生成的 DOM 树完成的——文档。

### 7.1. 设置属性和内部文本/HTML

与在 jQuery 中一样，设置属性、文本和 HTML 的方法具有相同的名称，但也接收要设置的值：

-   attr() – 设置属性值(如果属性不存在则创建它)
-   text() – 设置元素内部文本，替换内容
-   html() – 设置元素内部 HTML，替换内容

让我们看一下这些方法的一个简单示例：

```java
timeElement.attr("datetime", "2016-12-16 15:19:54.3");
sectionDiv.text("foo bar");
firstArticle.select("h2").html("<div><span></span></div>");

```

### 7.2. 创建和附加元素

要添加新元素，需要先通过实例化Element来构建它。构建Element后，可以使用appendChild方法将其附加到另一个Element。新创建和附加的Element将被插入到调用appendChild的元素的末尾：

```java
Element link = new Element(Tag.valueOf("a"), "")
  .text("Checkout this amazing website!")
  .attr("href", "http://baeldung.com")
  .attr("target", "_blank");
firstArticle.appendChild(link);
```

### 7.3. 删除元素

要删除元素，需要先选择它们并运行remove方法。

例如，让我们从Document中删除所有包含“ navbar-link”类的<li>标签，以及第一篇文章中的所有图像：

```java
doc.select("li.navbar-link").remove();
firstArticle.select("img").remove();
```

### 7.4. 将修改后的文档转换为 HTML

最后，由于我们正在更改Document，我们可能想要检查我们的工作。

为此，我们可以使用提供的方法通过选择、遍历和提取来探索文档DOM 树，或者我们可以使用html()方法简单地将其 HTML 提取为字符串：

```java
String docHtml = doc.html();
```

String输出是一个整洁的 HTML 。

## 八. 总结

Jsoup 是一个很棒的库，可以抓取任何页面。如果使用Java并且不需要基于浏览器的抓取，那么它是一个需要考虑的库。它熟悉且易于使用，因为它利用了在前端开发方面可能拥有的知识，并遵循良好的实践和设计模式。

[可以通过研究jsoup API](https://jsoup.org/apidocs/)和阅读[jsoup cookbook](https://jsoup.org/cookbook/)了解有关使用 jsoup 抓取网页的更多信息。