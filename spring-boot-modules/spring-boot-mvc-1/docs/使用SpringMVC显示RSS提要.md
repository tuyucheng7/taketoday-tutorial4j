## 1. 概述

本快速教程将展示如何使用Spring MVC和AbstractRssFeedView类构建一个简单的RSS提要。

之后，我们还将实现一个简单的REST API，通过网络公开我们的提要。

## 2. RSS Feed

在深入介绍实现细节之前，让我们快速回顾一下RSS是什么以及它是如何工作的。

简单的说，RSS是一种网络提要，它可以轻松地允许用户跟踪来自网站的更新。此外，**RSS提要基于一个XML文件，该文件汇总了网站的内容**。然后，新闻聚合器可以订阅一个或多个提要，并通过定期检查XML是否已更改来显示更新。

## 3. 依赖

首先，由于**Spring的RSS支持基于ROME框架**，我们需要在实际使用它之前将其作为依赖项添加到我们的POM文件中：

```xml
<dependency>
    <groupId>com.rometools</groupId>
    <artifactId>rome</artifactId>
    <version>1.10.0</version>
</dependency>
```

## 4. 提要实现

接下来，我们将构建实际的提要，为了做到这一点，**我们需要扩展AbstractRssFeedView类并实现它的两个方法**。

**第一个方法接收一个Channel对象作为输入参数，并将使用提要的元数据填充它**。

**另一个方法将返回代表提要内容的Item集合**：

```java
@Component
public class RssFeedView extends AbstractRssFeedView {

    @Override
    protected void buildFeedMetadata(Map<String, Object> model,
                                     Channel feed, HttpServletRequest request) {
        feed.setTitle("Baeldung RSS Feed");
        feed.setDescription("Learn how to program in Java");
        feed.setLink("http://www.baeldung.com");
    }

    @Override
    protected List<Item> buildFeedItems(Map<String, Object> model,
                                        HttpServletRequest request, HttpServletResponse response) {
        Item entryOne = new Item();
        entryOne.setTitle("JUnit 5 @Test Annotation");
        entryOne.setAuthor("donatohan.rimenti@gmail.com");
        entryOne.setLink("http://www.baeldung.com/junit-5-test-annotation");
        entryOne.setPubDate(Date.from(Instant.parse("2017-12-19T00:00:00Z")));
        return Arrays.asList(entryOne);
    }
}
```

## 5. 公开提要

最后，我们将构建一个简单的REST服务，以**使我们的提要在Web上可用**，该服务将返回我们刚刚创建的视图对象：

```java
@RestController
public class RssFeedController {

    @Autowired
    private RssFeedView view;

    @GetMapping("/rss")
    public View getFeed() {
        return view;
    }
}
```

此外，由于我们使用Spring Boot来启动我们的应用程序，因此我们将实现一个简单的启动器类：

```java
@SpringBootApplication
public class RssFeedApplication {

    public static void main(final String[] args) {
        SpringApplication.run(RssFeedApplication.class, args);
    }
}
```

运行我们的应用程序后，当对我们的服务发起请求时，我们将看到以下RSS提要：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0">
    <channel>
        <title>Baeldung RSS Feed</title>
        <link>http://www.baeldung.com</link>
        <description>Learn how to program in Java</description>
        <item>
            <title>JUnit 5 @Test Annotation</title>
            <link>http://www.baeldung.com/junit-5-test-annotation</link>
            <pubDate>Tue, 19 Dec 2017 00:00:00 GMT</pubDate>
            <author>donatohan.rimenti@gmail.com</author>
        </item>
        <item>
            <title>Creating and Configuring Jetty 9 Server in Java</title>
            <link>http://www.baeldung.com/jetty-java-programmatic</link>
            <pubDate>Tue, 23 Jan 2018 00:00:00 GMT</pubDate>
            <author>donatohan.rimenti@gmail.com</author>
        </item>
        <item>
            <title>Flyweight Pattern in Java</title>
            <link>http://www.baeldung.com/java-flyweight</link>
            <pubDate>Thu, 01 Feb 2018 00:00:00 GMT</pubDate>
            <author>donatohan.rimenti@gmail.com</author>
        </item>
        <item>
            <title>Multi-Swarm Optimization Algorithm in Java</title>
            <link>http://www.baeldung.com/java-multi-swarm-algorithm</link>
            <pubDate>Fri, 09 Mar 2018 00:00:00 GMT</pubDate>
            <author>donatohan.rimenti@gmail.com</author>
        </item>
        <item>
            <title>A Simple Tagging Implementation with MongoDB</title>
            <link>http://www.baeldung.com/mongodb-tagging</link>
            <pubDate>Tue, 27 Mar 2018 00:00:00 GMT</pubDate>
            <author>donatohan.rimenti@gmail.com</author>
        </item>
        <item>
            <title>Double-Checked Locking with Singleton</title>
            <link>http://www.baeldung.com/java-singleton-double-checked-locking</link>
            <pubDate>Mon, 23 Apr 2018 00:00:00 GMT</pubDate>
            <author>donatohan.rimenti@gmail.com</author>
        </item>
        <item>
            <title>Introduction to Dagger 2</title>
            <link>http://www.baeldung.com/dagger-2</link>
            <pubDate>Sat, 30 Jun 2018 00:00:00 GMT</pubDate>
            <author>donatohan.rimenti@gmail.com</author>
        </item>
    </channel>
</rss>
```

## 6. 总结

本文介绍了如何使用Spring和ROME构建一个简单的RSS提要，并通过使用Web服务使其可供消费者使用。