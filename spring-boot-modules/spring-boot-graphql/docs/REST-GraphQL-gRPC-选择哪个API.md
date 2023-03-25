## 1. 概述

多年来，REST一直是设计Web API的事实上的行业标准架构风格。然而，最近出现了GraphQL和gRPC，以解决REST的一些局限性。**这些API方法中的每一种都带来了巨大的好处和一些权衡**。

在本教程中，我们将首先介绍每种API设计方法。然后，我们将使用Spring Boot中的三种不同方法构建一个简单的服务。

最后，由于没有放之四海而皆准的方法，我们将了解如何在不同的应用程序层上混合使用不同的方法。

## 2. REST

Representational State Transfer(REST)是全球最常用的API架构风格。它是由Roy Fielding于2000年定义的。

### 2.1 架构风格

REST不是框架或库，而是**一种描述基于URL结构和HTTP协议的接口的架构风格**。它描述了用于客户端-服务器交互的无状态、可缓存、基于约定的体系结构。它使用URL来寻址适当的资源，并使用HTTP方法来表达要执行的操作：

-   GET用于获取现有资源或多个资源
-   POST用于创建新资源
-   PUT用于更新资源或创建资源(如果不存在)
-   DELETE用于删除资源
-   PATCH用于部分更新现有资源

REST可以用多种编程语言实现，并支持JSON和XML等多种数据格式。

### 2.2 示例服务

我们可以通过**使用[@RestController](https://www.baeldung.com/spring-controller-vs-restcontroller)注解定义控制器类来在Spring中构建REST服务**。接下来，我们通过[@GetMapping](https://www.baeldung.com/spring-new-requestmapping-shortcuts)注解定义一个对应HTTP方法的函数，例如GET。最后，在注解参数中，我们提供了一个应该触发该方法的资源路径：

```java
@GetMapping("/rest/books")
public List<Book> books() {
    return booksService.getBooks();
}
```

[MockMvc](https://www.baeldung.com/integration-testing-in-spring)提供了对Spring中REST服务集成测试的支持。它封装了所有Web应用程序bean并使它们可用于测试：

```java
this.mockMvc.perform(get("/rest/books"))
    .andDo(print())
    .andExpect(status().isOk())
    .andExpect(content().json(expectedJson));
```

由于它们基于HTTP，因此可以在浏览器中或使用[Postman](https://www.baeldung.com/postman-testing-collections)或[CURL](https://www.baeldung.com/curl-rest)等工具测试REST服务：

```shell
$ curl http://localhost:8082/rest/books
```

### 2.3 优点和缺点

REST的最大优势在于**它是技术界最成熟的API架构风格**。由于它的受欢迎程度，许多开发人员已经熟悉REST并且发现它很容易使用。但是，由于其灵活性，开发人员对REST的解释可能有所不同。

鉴于每个资源通常都位于一个唯一的URL后面，因此很容易监控和限制API的速率。REST还通过利用HTTP使缓存变得简单。通过缓存HTTP响应，我们的客户端和服务器不需要不断地相互交互。

REST容易出现获取不足和过度获取的情况。例如，要获取嵌套实体，我们可能需要发出多个请求。另一方面，在REST API中，通常不可能只获取特定的实体数据。客户端始终接收请求端点配置为返回的所有数据。

## 3. GraphQL

GraphQL是由Facebook开发的用于API的开源查询语言。

### 3.1 架构风格

**GraphQL提供了一种用于开发API的查询语言，以及用于完成这些查询的框架**。它不依赖于HTTP方法来处理数据，并且主要只使用POST。相比之下，GraphQL使用查询、突变和订阅：

-   查询用于从服务器请求数据
-   突变用于修改服务器上的数据
-   订阅用于在数据更改时获取实时更新

GraphQL是客户端驱动的，因为它使客户端能够准确定义特定用例所需的数据。然后在一次往返中从服务器检索请求的数据。

### 3.2 示例服务

在GraphQL中，**数据由定义对象、其字段和类型的模式表示**。因此，我们将首先为我们的示例服务定义一个GraphQL模式：

```graphql
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
```

我们可以使用@RestController注解在Spring中构建类似于REST服务的GraphQL服务。接下来，我们用[@QueryMapping](https://www.baeldung.com/spring-graphql)标注我们的函数，将其标记为GraphQL数据获取组件：

```java
@QueryMapping
public List<Book> books() {
    return booksService.getBooks();
}
```

HttpGraphQlTester提供对Spring中GraphQL服务集成测试的支持。它封装了所有Web应用程序bean并使它们可用于测试：

```java
this.graphQlTester.document(document)
    .execute()
    .path("books")
    .matchesJson(expectedJson);
```

可以使用Postman或CURL等工具测试GraphQL服务。但是，它们需要在POST正文中指定查询：

```shell
$ curl -X POST -H "Content-Type: application/json" -d "{\"query\":\"query{books{title}}\"}" http://localhost:8082/graphql
```

### 3.3 优点和缺点

GraphQL对其客户端非常灵活，因为它**只允许获取和传送请求的数据**。由于不会通过网络发送不必要的数据，因此GraphQL可以带来更好的性能。

与REST的模糊性相比，它使用了更严格的规范。此外，GraphQL还提供了详细的错误描述用于调试目的，并自动生成有关API更改的文档。

由于每个查询可能不同，因此GraphQL打破了中间代理缓存，使缓存实现更加困难。此外，由于GraphQL查询可能会执行大型且复杂的服务器端操作，因此查询通常会受到复杂性的限制，以避免服务器过载。

## 4. gRPC

RPC代表远程过程调用，[gRPC](https://www.baeldung.com/grpc-introduction)是Google创建的一个高性能、开源的RPC框架。

### 4.1 架构风格

gRPC框架基于远程过程调用的客户端-服务器模型。**客户端应用程序可以直接调用服务器应用程序上的方法**，就好像它是本地对象一样。这是一种基于契约的严格方法，其中客户端和服务器都需要访问相同的模式定义。

在gRPC中，一种称为协议缓冲区(Protocol Buffer)语言的DSL定义了请求和响应类型。然后，Protocol Buffer编译器生成服务器和客户端代码工件。我们可以使用自定义业务逻辑扩展生成的服务器代码并提供响应数据。

该框架支持多种类型的客户端-服务器交互：

-   传统的请求-响应交互
-   服务器流式处理，来自客户端的一个请求可能会产生多个响应
-   客户端流式处理，其中来自客户端的多个请求导致单个响应

客户端和服务器使用紧凑的二进制格式通过HTTP/2进行通信，这使得gRPC消息的编码和解码非常高效。

### 4.2 示例服务

与GraphQL类似，**我们首先定义一个模式，该模式定义服务、请求和响应，包括它们的字段和类型**：

```protobuf
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
}
```

然后，我们需要将我们的协议缓冲区文件传递给协议缓冲区编译器以生成所需的代码。我们可以选择使用预编译的二进制文件之一手动执行此操作，或者使用[protobuf-maven-plugin](https://central.sonatype.com/artifact/org.xolstice.maven.plugins/protobuf-maven-plugin/0.6.1)使其成为构建过程的一部分：

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
</plugin>
```

现在，我们可以扩展生成的BooksServiceImplBase类，使用@GrpcService注解对其进行标注并覆盖books方法：

```java
@Override
public void books(BooksRequest request, StreamObserver<BooksResponse> responseObserver) {
    List<Book> books = booksService.getBooks();
    BooksResponse.Builder responseBuilder = BooksResponse.newBuilder();
    books.forEach(book -> responseBuilder.addBook(GrpcBooksMapper.mapBookToProto(book)));

    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
}
```

在Spring中对gRPC服务进行集成测试是可能的，但还没有REST和GraphQL那么成熟：

```java
BooksRequest request = BooksRequest.newBuilder().build();
BooksResponse response = booksServiceGrpc.books(request);

List<Book> books = response.getBookList().stream()
    .map(GrpcBooksMapper::mapProtoToBook)
    .collect(Collectors.toList());     

JSONAssert.assertEquals(objectMapper.writeValueAsString(books), expectedJson, true);
```

为了使这个集成测试工作，我们需要用以下注解标注我们的测试类：

-   @SpringBootTest将客户端配置为连接到所谓的gRPC “in process”测试服务器
-   @SpringJUnitConfig准备并提供应用程序bean
-   @DirtiesContext确保服务器在每次测试后正确关闭

Postman最近添加了对测试gRPC服务的支持。与CURL类似，一个名为[grpcurl](https://github.com/fullstorydev/grpcurl)的命令行工具使我们能够与gRPC服务器进行交互：

```shell
$ grpcurl --plaintext localhost:9090 cn.tuyucheng.taketoday.chooseapi.BooksService/books
```

此工具使用JSON编码使协议缓冲区编码对测试目的更人性化。

### 4.3 优点和缺点

gRPC最大的优势是性能，这是通过**紧凑的数据格式、快速的消息编码和解码以及HTTP/2**的使用来实现的。此外，它的代码生成功能支持多种编程语言，帮助我们节省编写样板代码的时间。

通过要求HTTP2和TLS/SSL，gRPC提供了更好的安全默认值和对流式处理的内置支持。接口契约的与语言无关的定义支持用不同编程语言编写的服务之间的通信。

但是，目前，gRPC在开发者社区中远不如REST流行。它的数据格式对人类来说是不可读的，因此需要额外的工具来分析有效负载和执行调试。此外，HTTP/2仅在最新版本的现代浏览器中通过TLS得到支持。

## 5. 选择哪个API

现在我们已经熟悉了所有三种API设计方法，让我们看看在决定采用哪种方法之前应该考虑的几个标准。

### 5.1 数据格式

就请求和响应数据格式而言，REST是最灵活的方法。**我们可以实现REST服务来支持一种或多种数据格式，如JSON和XML**。

另一方面，GraphQL定义了自己的查询语言，需要在请求数据时使用。GraphQL服务以JSON格式响应。尽管可以将响应转换为另一种格式，但这并不常见并且可能会影响性能。

gRPC框架使用协议缓冲区(一种自定义二进制格式)。它对人类来说是不可读的，但它也是使gRPC如此高效的主要原因之一。尽管支持多种编程语言，但无法自定义格式。

### 5.2 数据获取

**GraphQL是从服务器获取数据的最有效的API方法**。由于它允许客户端选择要获取的数据，因此通常不会通过网络发送额外的数据。

REST和gRPC都不支持这种高级客户端查询。因此，除非在服务器上开发和部署新的端点或过滤器，否则服务器可能会返回额外的数据。

### 5.3 浏览器支持

**所有现代浏览器都支持REST和GraphQL API**。通常，JavaScript客户端代码用于通过HTTP请求从浏览器发送到服务器API。

浏览器对gRPC API的支持并不是开箱即用的。但是，可以使用gRPC的Web扩展。它基于HTTP 1.1.，但不提供所有gRPC功能。与Java客户端类似，用于Web的gRPC需要浏览器客户端代码从协议缓冲区模式生成gRPC客户端。

### 5.4 代码生成

GraphQL需要将额外的[库](https://www.baeldung.com/spring-graphql)添加到像Spring这样的核心框架中。这些库增加了对GraphQL模式处理、基于注解的编程和GraphQL请求的服务器处理的支持。从GraphQL模式生成代码是可能的，但不是必需的。**可以使用与模式中定义的GraphQL类型匹配的任何自定义POJO**。

gRPC框架也需要将额外的[库](https://www.baeldung.com/grpc-introduction)添加到核心框架，以及强制代码生成步骤。协议缓冲区编译器生成我们可以扩展的服务器和客户端样板代码。如果我们使用自定义POJO，则需要将它们映射到自动生成的协议缓冲区类型。

REST是一种可以使用任何编程语言和各种HTTP库实现的架构风格。它不使用预定义模式，也不需要任何代码生成。也就是说，使用Swagger或[OpenAPI](https://www.baeldung.com/java-openapi-generator-server)允许我们定义模式并根据需要生成代码。

### 5.5 响应时间

由于其优化的二进制格式，**与REST和GraphQL相比，gRPC的响应时间明显更快**。此外，负载平衡可用于所有三种方法，以在多个服务器之间平均分配客户端请求。

但是，此外，gRPC默认使用HTTP 2.0，这使得gRPC中的延迟低于REST和GraphQL API。使用HTTP 2.0，多个客户端可以同时发送多个请求，而无需建立新的TCP连接。大多数性能测试报告称，gRPC比REST快大约5到10倍。

### 5.6 缓存

**使用REST缓存请求和响应既简单又成熟，因为它允许在HTTP级别缓存数据**。每个GET请求都会公开应用程序资源，这些资源很容易被浏览器、代理服务器或[CDN](https://www.baeldung.com/cs/cdn)缓存。

由于GraphQL默认使用POST方法，并且每次查询可能不同，这使得缓存实现更加困难。当客户端和服务器在地理上彼此远离时尤其如此。此问题的可能解决方法是通过GET进行查询，并使用预先计算并存储在服务器上的持久查询。一些GraphQL中间件服务也提供缓存。

目前，默认情况下，gRPC不支持缓存请求和响应。但是，可以实现将缓存响应的自定义中间件层。

### 5.7 预期用途

**REST非常适合域，可以很容易地描述为一组资源而不是操作**。使用HTTP方法可以在这些资源上启用标准的CRUD操作。通过依赖HTTP语义，它对其调用者来说很直观，使其非常适合面向公众的界面。对REST的良好缓存支持使其适用于具有稳定使用模式和地理分布用户的API。

GraphQL非常适合**多个客户端需要不同数据集的公共API**。因此，GraphQL客户端可以通过标准化的查询语言指定他们想要的确切数据。对于聚合来自多个来源的数据然后将其提供给多个客户端的API，它也是一个不错的选择。

gRPC框架非常适合**开发微服务之间频繁交互的内部API**。它通常用于从不同物联网设备等低级代理收集数据。但是，其有限的浏览器支持使其难以在面向客户的Web应用程序中使用。

## 6. 混搭

三种API架构风格各有优势。但是，没有一种放之四海而皆准的方法，我们选择哪种方法将取决于我们的用例。

我们不必每次都做出单一选择。我们还可以**在我们的解决方案架构中混合搭配不同的风格**：

<img src="../assets/img_3.png">

在上面的示例架构图中，我们演示了如何在不同的应用程序层中应用不同的API样式。

## 7. 总结

在本文中，我们探索了三种用于设计Web API的流行架构风格：REST、GraphQL和gRPC。我们研究了每种不同风格的用例并描述了它的好处和权衡。

我们探讨了如何在Spring Boot中使用所有三种不同的方法构建一个简单的服务。此外，我们通过查看在决定方法之前应考虑的几个标准来比较它们。最后，由于没有放之四海而皆准的方法，我们看到了如何在不同的应用程序层中混合和匹配不同的方法。