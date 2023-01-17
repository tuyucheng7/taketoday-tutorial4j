## 1. 简介

在本文中，我们将探索[AWS AppSync](https://docs.aws.amazon.com/appsync/latest/devguide/welcome.html)与 Spring Boot。AWS AppSync 是一种完全托管的企业级[GraphQL](https://www.baeldung.com/graphql)服务，具有实时数据同步和离线编程功能。

## 2. 设置 AWS AppSync

首先，我们需要有一个活跃的[AWS 账户](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc)。完成后，我们可以从 AWS 控制台搜索 AppSync。然后我们将单击[AppSync 入门](https://docs.aws.amazon.com/appsync/latest/devguide/welcome.html) 链接。

## 2.1. 创建 AppSync API

按照[快速入门说明](https://docs.aws.amazon.com/appsync/latest/devguide/quickstart-launch-a-sample-schema.html)创建我们的 API，我们将使用Event App示例项目。然后点击开始命名并创建应用程序：

[![aws 应用程序同步](https://www.baeldung.com/wp-content/uploads/2020/05/aws_appsync.jpg)](https://www.baeldung.com/wp-content/uploads/2020/05/aws_appsync.jpg)

这会将我们带到我们的 AppSync 应用程序控制台。现在让我们来看看我们的 GraphQL 模型。

### 2.2. GraphQL 事件模型

GraphQL 使用模式来定义客户端可用的数据以及如何与 GraphQL 服务器交互。该模式包含查询、变更和各种已声明的类型。

为简单起见，让我们看一下默认的 AWS AppSync GraphQL 架构的一部分，即我们的事件模型：

```javascript
type Event {
  id: ID!
  name: String
  where: String
  when: String
  description: String
  # Paginate through all comments belonging to an individual post.
  comments(limit: Int, nextToken: String): CommentConnection
}
```

Event是一个声明的类型，带有一些String字段和一个CommentConnection类型。请注意ID字段上的感叹号。这意味着它是必填/非空字段。

这应该足以理解我们模式的基础知识。但是，有关更多信息，请访问[GraphQL](https://graphql.org/)站点。

## 3. 弹簧靴

现在我们已经在 AWS 端设置了所有内容，让我们看看我们的Spring Boot客户端应用程序。

### 3.1. Maven 依赖项

要访问我们的 API，我们将使用Spring BootStarter WebFlux 库来访问WebClient，这是 Spring 对RestTemplate的新替代方案：

```xml
    <dependency> 
      <groupId>org.springframework.boot</groupId> 
      <artifactId>spring-boot-starter-webflux</artifactId> 
    </dependency>
```

查看[我们关于](https://www.baeldung.com/spring-5-webclient)WebClient 的文章以获取更多信息。

### 3.2. GraphQL 客户端

要向我们的 API 发出请求，我们首先使用WebClient构建器创建我们的RequestBodySpec ，提供 AWS AppSync API URL 和 API 密钥：

```java
WebClient.RequestBodySpec requestBodySpec = WebClient
    .builder()
    .baseUrl(apiUrl)
    .defaultHeader("x-api-key", apiKey)
    .build()
    .method(HttpMethod.POST)
    .uri("/graphql");
```

不要忘记 API 密钥标头x-api-key。API 密钥向我们的 AppSync 应用程序进行身份验证。

## 4. 使用 GraphQL 类型

### 4.1. 查询

设置我们的查询涉及将其添加到消息正文中的查询元素：

```java
Map<String, Object> requestBody = new HashMap<>();
requestBody.put("query", "query ListEvents {" 
  + " listEvents {"
  + "   items {"
  + "     id"
  + "     name"
  + "     where"
  + "     when"
  + "     description"
  + "   }"
  + " }"
  + "}");
```

使用我们的requestBody， 让我们调用我们的WebClient来检索响应主体：

```java
WebClient.ResponseSpec response = requestBodySpec
    .body(BodyInserters.fromValue(requestBody))
    .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
    .acceptCharset(StandardCharsets.UTF_8)
    .retrieve();

```

最后，我们可以将正文作为String获取：

```java
String bodyString = response.bodyToMono(String.class).block();
assertNotNull(bodyString);
assertTrue(bodyString.contains("My First Event"));
```

### 4.2. 突变

GraphQL 允许通过使用突变来更新和删除数据。突变根据需要修改服务器端数据，并遵循与查询类似的语法。

让我们添加一个带有添加突变查询的新事件：

```java
String queryString = "mutation add {"
  + "    createEvent("
  + "        name:"My added GraphQL event""
  + "        where:"Day 2""
  + "        when:"Saturday night""
  + "        description:"Studying GraphQL""
  + "    ){"
  + "        id"
  + "        name"
  + "        description"
  + "    }"
  + "}";
 
requestBody.put("query", queryString);
```

AppSync 和 GraphQL 的最大优势之一是，一个端点 URL 提供了整个模式中的所有 CRUD 功能。

我们可以重复使用相同的 WebClient来添加、更新和删除数据。我们将根据查询或突变中的回调简单地获得新的响应。

```java
assertNotNull(bodyString);
assertTrue(bodyString.contains("My added GraphQL event"));
assertFalse(bodyString.contains("where"));
```

## 5.总结

在本文中，我们了解了使用 AWS AppSync 设置 GraphQL 应用程序并使用Spring Boot客户端访问它的速度有多快。

AppSync 通过单一端点为开发人员提供强大的 GraphQL API。有关更多信息，请查看我们关于创建[GraphQLSpring Boot服务器](https://www.baeldung.com/spring-graphql)的教程。