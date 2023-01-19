## 一、概述

多年来，REST 一直是设计 Web API 的事实上的行业标准架构风格。然而，最近出现了 GraphQL 和 gRPC，以解决 REST 的一些局限性。这些**API 方法中的每一种都带来了巨大的好处和一些权衡**。

在本教程中，我们将首先了解每种 API 设计方法。然后，我们将使用 Spring Boot 中的三种不同方法构建一个简单的服务。接下来，我们将通过查看在决定一个之前应该考虑的几个标准来比较它们。

最后，由于没有放之四海而皆准的方法，我们将了解如何在不同的应用程序层上混合使用不同的方法。

## 2. 休息

Representational State Transfer (REST) 是全球最常用的 API 架构风格。它是由 Roy Fielding 于 2000 年定义的。

### 2.1. 建筑风格

REST 不是框架或库，而是**描述基于 URL 结构和 HTTP 协议的接口的架构风格**。它描述了用于客户端-服务器交互的无状态、可缓存、基于约定的体系结构。它使用 URL 来寻址适当的资源，并使用 HTTP 方法来表达要采取的操作：

-   GET 用于获取现有资源或多个资源
-   POST用于创建新资源
-   PUT 用于更新资源或在资源不存在时创建它
-   DELETE 用于删除资源
-   PATCH 用于部分更新现有资源

REST 可以用多种编程语言实现，支持 JSON 和 XML 等多种数据格式。

### 2.2. 示例服务

**[\*我们可以通过使用@RestController\*](https://www.baeldung.com/spring-controller-vs-restcontroller)****注释****定义控制器类来**在 Spring 中构建 REST 服务。*[接下来，我们通过@GetMapping](https://www.baeldung.com/spring-new-requestmapping-shortcuts)*注解定义一个对应HTTP方法的函数，例如GET 。最后，在注释参数中，我们提供了一个应该触发该方法的资源路径：

```java
@GetMapping("/rest/books")
public List<Book> books() {
    return booksService.getBooks();
}复制
```

[*MockMvc*](https://www.baeldung.com/integration-testing-in-spring) 提供了对 Spring 中 REST 服务集成测试的支持。它封装了所有 Web 应用程序 bean 并使它们可用于测试：

```java
this.mockMvc.perform(get("/rest/books"))
  .andDo(print())
  .andExpect(status().isOk())
  .andExpect(content().json(expectedJson));复制
```

由于它们基于 HTTP，因此可以在浏览器中或使用[Postman](https://www.baeldung.com/postman-testing-collections)或[CURL](https://www.baeldung.com/curl-rest)等工具测试 REST 服务：

```bash
$ curl http://localhost:8082/rest/books复制
```

### 2.3. 优点和缺点

REST 的最大优势在于它是**技术界最成熟的 API 架构风格**。由于它的流行，许多开发人员已经熟悉 REST 并且发现它很容易使用。然而，由于其灵活性，REST 在开发人员中可能有不同的解释。

鉴于每个资源通常都位于一个唯一的 URL 后面，因此很容易监控和限制 API 的速率。REST 还通过利用 HTTP 使缓存变得简单。通过缓存 HTTP 响应，我们的客户端和服务器不需要不断地相互交互。

REST 容易出现获取不足和过度获取的情况。例如，要获取嵌套实体，我们可能需要发出多个请求。另一方面，在 REST API 中，通常不可能只获取特定的实体数据。客户端始终接收请求端点配置为返回的所有数据。

## 3. 图表QL

GraphQL 是 Facebook 开发的用于 API 的开源查询语言。

### 3.1. 建筑风格

GraphQL 提供了**一种用于开发 API 的查询语言，以及用于完成这些查询的框架**。它不依赖于 HTTP 方法来处理数据，并且主要只使用 POST。相比之下，GraphQL 使用查询、变更和订阅：

-   查询用于从服务器请求数据
-   突变用于修改服务器上的数据
-   订阅用于在数据更改时获取实时更新

GraphQL 是客户端驱动的，因为它使客户端能够准确定义特定用例所需的数据。然后在一次往返中从服务器检索请求的数据。

### 3.2. 示例服务

在 GraphQL 中，**数据用定义对象、它们的字段和类型的模式表示**。因此，我们将从为我们的示例服务定义一个 GraphQL 模式开始：

```json
type Author {
    firstName: String!
    lastName: String!
}

type Book {
    title: String!
    year: Int!
    author: Author!
}

type Query {
    books: [Book]
}
复制
```

*我们可以使用@RestController*类注释在 Spring 中构建类似于 REST 服务的GraphQL 服务。接下来，我们用[*@QueryMapping*](https://www.baeldung.com/spring-graphql)注释我们的函数，将其标记为 GraphQL 数据获取组件：

```java
@QueryMapping
public List<Book> books() {
    return booksService.getBooks();
}复制
```

*HttpGraphQlTester*提供对 Spring 中 GraphQL 服务集成测试的支持。它封装了所有 Web 应用程序 bean 并使它们可用于测试：

```java
this.graphQlTester.document(document)
  .execute()
  .path("books")
  .matchesJson(expectedJson);复制
```

可以使用 Postman 或 CURL 等工具测试 GraphQL 服务。但是，它们需要在 POST 正文中指定查询：

```bash
$ curl -X POST -H "Content-Type: application/json" -d "{\"query\":\"query{books{title}}\"}" http://localhost:8082/graphql复制
```

### 3.3. 优点和缺点

GraphQL 对其客户端非常灵活，因为它只**允许获取和传送请求的数据**。由于不会通过网络发送不必要的数据，因此 GraphQL 可以带来更好的性能。

与 REST 的模糊性相比，它使用了更严格的规范。此外，GraphQL 提供了用于调试目的的详细错误描述，并自动生成有关 API 更改的文档。

由于每个查询可能不同，GraphQL 打破了中间代理缓存，使缓存实现更加困难。此外，由于 GraphQL 查询可能会执行大型且复杂的服务器端操作，因此查询通常会受到复杂性的限制，以避免服务器过载。

## 4.gRPC

RPC代表远程过程调用，[gRPC](https://www.baeldung.com/grpc-introduction)是谷歌创建的一个高性能、开源的RPC框架。

### 4.1. 建筑风格

gRPC 框架基于远程过程调用的客户端-服务器模型。**客户端应用程序可以直接调用服务器应用程序上的方法，就**好像它是本地对象一样。这是一种基于契约的严格方法，其中客户端和服务器都需要访问相同的模式定义。

在 gRPC 中，一种称为协议缓冲区语言的 DSL 定义了请求和响应类型。Protocol Buffer 编译器然后生成服务器和客户端代码工件。我们可以使用自定义业务逻辑扩展生成的服务器代码并提供响应数据。

该框架支持多种类型的客户端-服务器交互：

-   传统的请求-响应交互
-   服务器流，来自客户端的一个请求可能会产生多个响应
-   客户端流，其中来自客户端的多个请求导致单个响应

客户端和服务器使用紧凑的二进制格式通过 HTTP/2 进行通信，这使得 gRPC 消息的编码和解码非常高效。

### 4.2. 示例服务

与 GraphQL 类似，**我们首先定义一个模式，该模式定义服务、请求和响应，包括它们的字段和类型**：

```bash
message BooksRequest {}

message AuthorProto {
    string firstName = 1;
    string lastName = 2;
}

message BookProto {
    string title = 1;
    AuthorProto author = 2;
    int32 year = 3;
}

message BooksResponse {
    repeated BookProto book = 1;
}

service BooksService {
    rpc books(BooksRequest) returns (BooksResponse);
}复制
```

然后，我们需要将我们的协议缓冲区文件传递给协议缓冲区编译器以生成所需的代码。[*我们可以选择使用预编译的二进制文件之一手动执行此操作，或者使用protobuf-maven-plugin*](https://search.maven.org/classic/#search|ga|1|g%3A"org.xolstice.maven.plugins" AND a%3A"protobuf-maven-plugin")使其成为构建过程的一部分：

```xml
<plugin>
    <groupId>org.xolstice.maven.plugins</groupId>
    <artifactId>protobuf-maven-plugin</artifactId>
    <version>${protobuf-plugin.version}</version>
    <configuration>
        <protocArtifact>com.google.protobuf:protoc:${protobuf.version}:exe:${os.detected.classifier}</protocArtifact>
        <pluginId>grpc-java</pluginId>
        <pluginArtifact>io.grpc:protoc-gen-grpc-java:${grpc.version}:exe:${os.detected.classifier}</pluginArtifact>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
                <goal>compile-custom</goal>
            </goals>
        </execution>
    </executions>
</plugin>复制
```

现在，我们可以扩展生成的*BooksServiceImplBase*类，使用*@GrpcService*注释对其进行注释并覆盖*books*方法：

```java
@Override
public void books(BooksRequest request, StreamObserver<BooksResponse> responseObserver) {
    List<Book> books = booksService.getBooks();
    BooksResponse.Builder responseBuilder = BooksResponse.newBuilder();
    books.forEach(book -> responseBuilder.addBook(GrpcBooksMapper.mapBookToProto(book)));

    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
}复制
```

可以在 Spring 中对 gRPC 服务进行集成测试，但还不如 REST 和 GraphQL 成熟：

```java
BooksRequest request = BooksRequest.newBuilder().build();
BooksResponse response = booksServiceGrpc.books(request);

List<Book> books = response.getBookList().stream()
  .map(GrpcBooksMapper::mapProtoToBook)
  .collect(Collectors.toList());     

JSONAssert.assertEquals(objectMapper.writeValueAsString(books), expectedJson, true);复制
```

为了使这个集成测试工作，我们需要用以下注释我们的测试类：

-   *@SpringBootTest* 配置客户端连接到所谓的 gRPC *“进程中”*测试服务器
-   *@SpringJUnitConfig*准备并提供应用程序 bean
-   *@DirtiesContext*确保服务器在每次测试后正确关闭

Postman 最近添加了对测试 gRPC 服务的支持。[*与 CURL 类似，一个名为grpcurl* ](https://github.com/fullstorydev/grpcurl)的命令行工具使我们能够与 gRPC 服务器进行交互：

```bash
$ grpcurl --plaintext localhost:9090 com.baeldung.chooseapi.BooksService/books复制
```

此工具使用 JSON 编码使协议缓冲区编码更加人性化以用于测试目的。

### 4.3. 优点和缺点

gRPC 最大的优势是性能，这是通过**紧凑的数据格式、快速的消息编码和解码以及 HTTP/2 的使用来实现的**。此外，它的代码生成功能支持多种编程语言，帮助我们节省编写样板代码的时间。

通过要求 HTTP 2 和 TLS/SSL，gRPC 提供了更好的安全默认值和对流的内置支持。接口契约的语言不可知定义支持用不同编程语言编写的服务之间的通信。

然而，目前，gRPC 在开发者社区中远不如 REST 流行。它的数据格式对人类来说是不可读的，因此需要额外的工具来分析有效载荷和执行调试。此外，HTTP/2 仅在最新版本的现代浏览器中通过 TLS 得到支持。

## 5.选择哪个API

现在我们已经熟悉了所有三种 API 设计方法，让我们看看在决定采用哪种方法之前应该考虑的几个标准。

### 5.1. 数据格式

就请求和响应数据格式而言，REST 是最灵活的方法。我们可以**实现 REST 服务来支持一种或多种数据格式**，如 JSON 和 XML。

另一方面，GraphQL 定义了自己的查询语言，需要在请求数据时使用。GraphQL 服务以 JSON 格式响应。尽管可以将响应转换为另一种格式，但这并不常见并且可能会影响性能。

gRPC 框架使用协议缓冲区，一种自定义二进制格式。它对人类来说是不可读的，但它也是使 gRPC 如此高效的主要原因之一。尽管有多种编程语言支持，但无法自定义格式。

### 5.2. 数据获取

**GraphQL 是从服务器获取数据的最有效的 API 方法**。由于它允许客户端选择要获取的数据，因此通常不会通过网络发送额外的数据。

REST 和 gRPC 都不支持这种高级客户端查询。因此，除非在服务器上开发和部署新的端点或过滤器，否则服务器可能会返回额外的数据。

### 5.3. 浏览器支持

**所有现代浏览器都支持 REST 和 GraphQL API**。通常，JavaScript 客户端代码用于通过 HTTP 请求从浏览器发送到服务器 API。

浏览器对 gRPC API 的支持并不是开箱即用的。但是，可以使用 gRPC 的 Web 扩展。它基于 HTTP 1.1.，但不提供所有 gRPC 功能。与 Java 客户端类似，用于 Web 的 gRPC 需要浏览器客户端代码从协议缓冲区模式生成 gRPC 客户端。

### 5.4. 代码生成

GraphQL 需要将额外的[库](https://www.baeldung.com/spring-graphql)添加到像 Spring 这样的核心框架中。这些库增加了对 GraphQL 模式处理、基于注释的编程和 GraphQL 请求的服务器处理的支持。从 GraphQL 模式生成代码是可能的，但不是必需的。**可以使用与架构中定义的 GraphQL 类型匹配的任何自定义 POJO**。

gRPC 框架还需要将额外的[库](https://www.baeldung.com/grpc-introduction)添加到核心框架，以及强制代码生成步骤。protocol buffer 编译器生成我们可以扩展的服务器和客户端样板代码。如果我们使用自定义 POJO，则需要将它们映射到自动生成的协议缓冲区类型。

REST 是一种架构风格，可以使用任何编程语言和各种 HTTP 库来实现。它不使用预定义模式，也不需要任何代码生成。也就是说，使用 Swagger 或[OpenAPI](https://www.baeldung.com/java-openapi-generator-server)允许我们定义模式并根据需要生成代码。

### 5.5. 响应时间

由于其优化的二进制格式，**与 REST 和 GraphQL 相比，gRPC 的响应时间明显更快**。此外，负载平衡可用于所有三种方法，以在多个服务器之间平均分配客户端请求。

但是，此外，gRPC 默认使用 HTTP 2.0，这使得 gRPC 中的延迟低于 REST 和 GraphQL API。使用 HTTP 2.0，多个客户端可以同时发送多个请求，而无需建立新的 TCP 连接。大多数性能测试报告说 gRPC 比 REST 快大约 5 到 10 倍。

### 5.6. 缓存

使用 REST 缓存请求和响应**既简单又成熟，因为它允许在 HTTP 级别缓存数据**。每个 GET 请求都会公开应用程序资源，这些资源很容易被浏览器、代理服务器或[CDN](https://www.baeldung.com/cs/cdn)缓存。

由于 GraphQL 默认使用 POST 方法，并且每次查询可能不同，这使得缓存实现更加困难。当客户端和服务器在地理上彼此远离时尤其如此。此问题的可能解决方法是通过 GET 进行查询，并使用预先计算并存储在服务器上的持久查询。一些 GraphQL 中间件服务也提供缓存。

目前，gRPC 默认不支持缓存请求和响应。但是，可以实现将缓存响应的自定义中间件层。

### 5.7. 预期用途

REST 非常适合**域，可以很容易地描述为一组资源** 而不是操作。使用 HTTP 方法可以在这些资源上启用标准的 CRUD 操作。通过依赖 HTTP 语义，它对其调用者来说很直观，使其非常适合面向公众的界面。对 REST 的良好缓存支持使其适用于具有稳定使用模式和地理分布用户的 API。

GraphQL 非常适合**多个客户端需要不同数据集**的公共 API 。因此，GraphQL 客户端可以通过标准化的查询语言指定他们想要的确切数据。对于聚合来自多个来源的数据然后将其提供给多个客户端的 API，它也是一个不错的选择。

gRPC 框架非常适合开发**微服务之间频繁交互**的内部 API 。它通常用于从不同物联网设备等低级代理收集数据。但是，其有限的浏览器支持使其难以在面向客户的 Web 应用程序中使用。

## 6.混搭

三种 API 架构风格各有优势。但是，没有一种放之四海而皆准的方法，我们选择哪种方法将取决于我们的用例。

我们不必每次都做出单一选择。我们还可以**在我们的解决方案架构中混合搭配不同的风格**：

[![使用 REST、GraphQL 和 gRPC 的示例架构](https://www.baeldung.com/wp-content/uploads/2022/12/choose-api2.png)](https://www.baeldung.com/wp-content/uploads/2022/12/choose-api2.png)

在上面的示例架构图中，我们演示了如何在不同的应用程序层中应用不同的 API 样式。

## 七、结论

在本文中，我们探索了**三种用于设计 Web API 的流行架构风格：REST、GraphQL 和 gRPC**。我们研究了每种不同风格的用例并描述了它的好处和权衡。

我们探讨了如何在 Spring Boot 中使用所有三种不同的方法构建一个简单的服务。此外，我们通过查看在决定方法之前应考虑的几个标准来比较它们。最后，由于没有放之四海而皆准的方法，我们看到了如何在不同的应用程序层中混合和匹配不同的方法。