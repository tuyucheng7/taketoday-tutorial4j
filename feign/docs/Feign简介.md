## 1. 概述

在本教程中，我们将介绍[Feign——](https://github.com/OpenFeign/feign)一个由 Netflix 开发的声明式 HTTP 客户端。

Feign 旨在简化 HTTP API 客户端。简而言之，开发人员只需要声明和注解一个接口，而实际的实现是在运行时提供的。

## 2.例子

在本教程中，我们将使用公开 REST API 端点的示例[书店应用程序。](https://github.com/Baeldung/spring-hypermedia-api)

我们可以轻松克隆项目并在本地运行它：

```bash
mvn install spring-boot:run
```

## 3.设置

首先，让我们添加所需的依赖项：

```xml
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-okhttp</artifactId>
    <version>10.11</version>
</dependency>
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-gson</artifactId>
    <version>10.11</version>
</dependency>
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-slf4j</artifactId>
    <version>10.11</version>
</dependency>
```

除了[feign-core](https://search.maven.org/classic/#search|gav|1|g%3A"io.github.openfeign" AND a%3A"feign-core")依赖项(它也被引入)，我们将使用一些插件，特别是[feign-okhttp](https://search.maven.org/classic/#search|gav|1|g%3A"io.github.openfeign" AND a%3A"feign-okhttp")用于在内部使用 Square 的[OkHttp](https://square.github.io/okhttp/)客户端发出请求，[feign-gson](https://search.maven.org/classic/#search|gav|1|g%3A"io.github.openfeign" AND a%3A"feign-gson")用于使用谷歌的 GSON 作为 JSON 处理器和[feign-slf4j](https://search.maven.org/classic/#search|gav|1|g%3A"io.github.openfeign" AND a%3A"feign-slf4j")使用Simple Logging Facade来记录请求。

要实际获得一些日志输出，我们需要在类路径上使用我们最喜欢的支持 SLF4J 的记录器实现。

在我们继续创建我们的客户端界面之前，首先我们将设置一个Book模型来保存数据：

```java
public class Book {
    private String isbn;
    private String author;
    private String title;
    private String synopsis;
    private String language;

    // standard constructor, getters and setters
}
```

注意： JSON 处理器至少需要一个“无参数构造函数”。

事实上，我们的 REST 提供程序是一个[超媒体驱动的 API](https://www.baeldung.com/spring-hateoas-tutorial)，因此我们还需要一个简单的包装类：

```java
public class BookResource {
    private Book book;

    // standard constructor, getters and setters
}
```

注意：我们将保持BookResource简单，因为我们的示例 Feign 客户端不会受益于超媒体功能！

## 4.服务器端

要了解如何定义 Feign 客户端，我们将首先研究我们的 REST 提供程序支持的一些方法和响应。

让我们尝试使用一个简单的 curl shell 命令来列出所有书籍。

我们需要记住在所有调用前加上/api前缀，这是应用程序的 servlet 上下文：

```bash
curl http://localhost:8081/api/books
```

结果，我们将获得一个以 JSON 表示的完整图书存储库：

```javascript
[
  {
    "book": {
      "isbn": "1447264533",
      "author": "Margaret Mitchell",
      "title": "Gone with the Wind",
      "synopsis": null,
      "language": null
    },
    "links": [
      {
        "rel": "self",
        "href": "http://localhost:8081/api/books/1447264533"
      }
    ]
  },

  ...

  {
    "book": {
      "isbn": "0451524934",
      "author": "George Orwell",
      "title": "1984",
      "synopsis": null,
      "language": null
    },
    "links": [
      {
        "rel": "self",
        "href": "http://localhost:8081/api/books/0451524934"
      }
    ]
  }
]
```

我们还可以通过将 ISBN 附加到 get 请求来查询单个图书资源：

```bash
curl http://localhost:8081/api/books/1447264533
```

## 5. 假装客户

最后，让我们定义我们的 Feign 客户端。

我们将使用@RequestLine注解来指定 HTTP 谓词和路径部分作为参数。

参数将使用@Param注解建模：

```java
public interface BookClient {
    @RequestLine("GET /{isbn}")
    BookResource findByIsbn(@Param("isbn") String isbn);

    @RequestLine("GET")
    List<BookResource> findAll();

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    void create(Book book);
}
```

注意： Feign 客户端只能用于使用基于文本的 HTTP API，这意味着它们无法处理二进制数据，例如文件上传或下载。

就这样！现在我们将使用Feign.builder()来配置我们基于接口的客户端。

实际实现将在运行时提供：

```java
BookClient bookClient = Feign.builder()
  .client(new OkHttpClient())
  .encoder(new GsonEncoder())
  .decoder(new GsonDecoder())
  .logger(new Slf4jLogger(BookClient.class))
  .logLevel(Logger.Level.FULL)
  .target(BookClient.class, "http://localhost:8081/api/books");
```

Feign 支持各种插件，例如 JSON/XML 编码器和解码器或用于发出请求的底层 HTTP 客户端。

## 6. 单元测试

让我们创建三个测试用例来测试我们的客户端。

请注意，我们对org.hamcrest.CoreMatchers.和org.junit.Assert.使用静态导入：

```java
@Test
public void givenBookClient_shouldRunSuccessfully() throws Exception {
   List<Book> books = bookClient.findAll().stream()
     .map(BookResource::getBook)
     .collect(Collectors.toList());

   assertTrue(books.size() > 2);
}

@Test
public void givenBookClient_shouldFindOneBook() throws Exception {
    Book book = bookClient.findByIsbn("0151072558").getBook();
    assertThat(book.getAuthor(), containsString("Orwell"));
}

@Test
public void givenBookClient_shouldPostBook() throws Exception {
    String isbn = UUID.randomUUID().toString();
    Book book = new Book(isbn, "Me", "It's me!", null, null);
    bookClient.create(book);
    book = bookClient.findByIsbn(isbn).getBook();

    assertThat(book.getAuthor(), is("Me"));
}

```

## 7. 进一步阅读

如果我们在服务不可用的情况下需要某种回退，我们可以将[HystrixFeign添加到类路径并使用](https://search.maven.org/classic/#search|gav|1|g%3A"io.github.openfeign" AND a%3A"feign-hystrix")HystrixFeign.builder()构建我们的客户端。

查看[这个专门的教程系列](https://www.baeldung.com/introduction-to-hystrix)以了解有关 Hystrix 的更多信息。

此外，如果我们想将 Spring Cloud Netflix Hystrix 与 Feign 集成，这里有一篇专门的[文章](https://www.baeldung.com/spring-cloud-netflix-hystrix)。

此外，还可以向我们的客户端添加客户端负载平衡和/或服务发现。

我们可以通过将[Ribbon](https://search.maven.org/classic/#search|gav|1|g%3A"io.github.openfeign" AND a%3A"feign-ribbon")添加到我们的类路径并使用构建器来实现这一点：

```java
BookClient bookClient = Feign.builder()
  .client(RibbonClient.create())
  .target(BookClient.class, "http://localhost:8081/api/books");
```

对于服务发现，我们必须在启用 Spring Cloud Netflix Eureka 的情况下构建我们的服务。然后我们简单的集成Spring Cloud Netflix Feign。结果，我们免费获得了 Ribbon 负载均衡。可以在[此处](https://www.baeldung.com/spring-cloud-netflix-eureka)找到有关此的更多信息。

## 八. 总结

在本文中，我们解释了如何使用 Feign 构建声明式 HTTP 客户端以使用基于文本的 API。