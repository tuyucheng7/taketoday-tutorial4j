## 1. 概述

**[GraphQL](GraphQL简介.md)是一个相对较新的概念，用于构建Web服务作为REST的替代方案**，最近出现了一些用于创建和调用GraphQL服务的Java库。

在本教程中，我们将了解GraphQL模式、查询和突变，我们介绍如何使用纯Java创建和Mock一个简单的GraphQL服务器。然后，我们将探讨如何使用众所周知的HTTP库调用GraphQL服务。

最后，我们还将探索用于调用GraphQL服务的可用第三方库。

## 2. GraphQL

**GraphQL是一种用于Web服务的查询语言，也是一种使用类型系统执行查询的服务器端运行时**。

GraphQL服务器使用GraphQL模式指定API功能，这允许GraphQL客户端准确指定要从API检索的数据，这可能包括单个请求中的子资源和多个查询。

### 2.1 GraphQL模式

GraphQL服务器使用一组类型定义服务，这些类型描述了可以使用该服务查询的一组可能的数据。

GraphQL服务可以用任何语言编写，但是，需要使用称为GraphQL模式语言的DSL来定义GraphQL模式。

在我们的示例GraphQL模式中，我们将定义两种类型(Book和Author)和一个查询操作来获取所有书籍(allBooks)：

```graphql
type Book {
    title: String!
    author: Author
}

type Author {
    name: String!
    surname: String!
}

type Query {
    allBooks: [Book]
}

schema {
    query: Query
}
```

Query类型很特殊，因为它定义了GraphQL查询的入口点。

### 2.2 查询和突变

GraphQL服务是**通过定义类型和字段，以及为不同字段提供函数来创建的**。

在最简单的形式中，GraphQL是关于请求对象上的特定字段。例如，我们可能会查询以获取所有书名：

```graphql
{
    "allBooks" {
        "title"
    }
}
```

尽管看起来很相似，但这不是JSON。它是一种特殊的GraphQL查询格式，支持参数、别名、变量等。

GraphQL服务将使用JSON格式的响应来响应上述查询，如下所示：

```json
{
    "data": {
        "allBooks": [
            {
                "title": "Title 1"
            },
            {
                "title": "Title 2"
            }
        ]
    }
}
```

在本教程中，我们将重点介绍如何使用查询获取数据。然而，重要的是要提到GraphQL中的另一个特殊概念-突变。

任何可能导致修改的操作都使用突变类型发送。

## 3. GraphQL服务器

让我们使用上面定义的模式在Java中创建一个简单的GraphQL服务器，我们将**使用[GraphQL Java](https://www.graphql-java.com/)库来实现我们的GraphQL服务器**。

我们从定义GraphQL查询开始，并实现示例GraphQL模式中指定的allBooks方法：

```java
public class GraphQLQuery implements GraphQLQueryResolver {

    private BookRepository repository;

    public GraphQLQuery(BookRepository repository) {
        this.repository = repository;
    }

    public List<Book> allBooks() {
        return repository.getAllBooks();
    }
}
```

接下来，为了公开我们的GraphQL端点，我们创建一个Web Servlet：

```java
@WebServlet(urlPatterns = "/graphql")
public class GraphQLEndpoint extends HttpServlet {

    private SimpleGraphQLHttpServlet graphQLServlet;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        graphQLServlet.service(req, resp);
    }

    @Override
    public void init() {
        GraphQLSchema schema = SchemaParser.newParser()
              .resolvers(new GraphQLQuery(new BookRepository()))
              .file("schema.graphqls")
              .build()
              .makeExecutableSchema();
        graphQLServlet = SimpleGraphQLHttpServlet
              .newBuilder(schema)
              .build();
    }
}
```

在Servlet的init方法中，我们将解析位于resources文件夹中的GraphQL模式。最后，使用解析后的模式，我们可以创建SimpleGraphQLHttpServlet的实例。

我们将使用[maven-war-plugin](https://mvnrepository.com/artifact/org.apache.maven.plugins/maven-war-plugin)来打包我们的应用程序，并使用[jetty-maven-plugin](https://mvnrepository.com/artifact/org.eclipse.jetty/jetty-maven-plugin)来运行它：

```bash
mvn jetty:run
```

现在我们准备好通过发送请求来运行和测试我们的GraphQL服务：

```bash
http://localhost:8080/graphql?query={allBooks{title}}
```

## 4. HTTP客户端

与REST服务一样，GraphQL服务通过HTTP协议公开。因此，**我们可以使用任何Java HTTP客户端来调用GraphQL服务**。

### 4.1 发送请求

让我们尝试向我们在上一节中创建的GraphQL服务发送请求：

```java
public static HttpResponse callGraphQLService(String url, String query) throws URISyntaxException, IOException {
    HttpClient client = HttpClientBuilder.create().build();
    HttpGet request = new HttpGet(url);
    URI uri = new URIBuilder(request.getURI())
      .addParameter("query", query)
      .build();
    request.setURI(uri);
    return client.execute(request);
}
```

在我们的示例中，我们使用了[Apache HttpClient]()，但是可以使用任何Java HTTP客户端。

### 4.2 解析响应

接下来，让我们解析来自GraphQL服务的响应。**GraphQL服务发送JSON格式的响应，与REST服务相同**：

```java
HttpResponse httpResponse = callGraphQLService(serviceUrl, "{allBooks{title}}");
String actualResponse = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8.name());
Response parsedResponse = objectMapper.readValue(actualResponse, Response.class);
assertThat(parsedResponse.getData().getAllBooks()).hasSize(2);
```

在我们的示例中，我们使用了常用的[Jackson]()库中的[ObjectMapper]()。但是，我们也可以使用任何Java库进行JSON序列化/反序列化。

### 4.3 Mock响应

与通过HTTP公开的任何其他服务一样，**我们可以Mock GraphQL服务器响应以进行测试**。

我们可以使用[MockServer]()库来stubbing外部GraphQL HTTP服务：

```java
String requestQuery = "{allBooks{title}}";
String responseJson = "{"data":{"allBooks":[{"title":"Title 1"},{"title":"Title 2"}]}}";

new MockServerClient(SERVER_ADDRESS, serverPort)
      .when(
            request()
             .withPath(PATH)
        .withQueryStringParameter("query", requestQuery),
      exactly(1)
    )
    .respond(
      response()
        .withStatusCode(HttpStatusCode.OK_200.code())
        .withBody(responseJson)
    );
```

我们的示例Mock服务器将接受GraphQL查询作为参数，并在正文中使用JSON响应进行响应。

## 5. 外部库

最近出现了一些允许更简单的GraphQL服务调用的Java GraphQL库。

### 5.1 American Express Nodes

**Nodes是来自American Express的GraphQL客户端，设计用于从标准模型定义构建查询**。要开始使用它，我们应该首先添加所需的[依赖项](https://jitpack.io/p/americanexpress/nodes)：

```xml
<dependency>
    <groupId>com.github.americanexpress.nodes</groupId>
    <artifactId>nodes</artifactId>
    <version>0.5.0</version>>
</dependency>
```

该库目前托管在JitPack上，我们也应该将其添加到我们的Maven安装仓库中：

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

解决依赖关系后，我们就可以使用GraphQLTemplate构建查询并调用我们的GraphQL服务：

```java
public static GraphQLResponseEntity<Data> callGraphQLService(String url, String query) throws IOException {
    GraphQLTemplate graphQLTemplate = new GraphQLTemplate();

    GraphQLRequestEntity requestEntity = GraphQLRequestEntity.Builder()
      .url(StringUtils.join(url, "?query=", query))
      .request(Data.class)
      .build();

    return graphQLTemplate.query(requestEntity, Data.class);
}
```

节点将使用我们指定的类解析来自GraphQL服务的响应：

```java
GraphQLResponseEntity<Data> responseEntity = callGraphQLService(serviceUrl, "{allBooks{title}}");
assertThat(responseEntity.getResponse().getAllBooks()).hasSize(2);
```

我们应该注意到，Nodes仍然需要我们构建自己的DTO类来解析响应。

### 5.2 GraphQL Java生成器

**GraphQL [Java Generator](https://github.com/graphql-java-generator/graphql-maven-plugin-project)库利用基于GraphQL模式生成Java代码的能力，这种方法类似于SOAP服务中使用的WSDL代码生成器**。

要开始使用它，我们应该首先添加所需的[依赖项](https://search.maven.org/search?q=com.graphql-java-generator)：

```xml
<dependency>
    <groupId>com.graphql-java-generator</groupId>
    <artifactId>graphql-java-runtime</artifactId>
    <version>1.18</version>
</dependency>
```

接下来，我们可以配置graphql-maven-plugin来执行generateClientCode目标：

```xml
<plugin>
    <groupId>com.graphql-java-generator</groupId>
    <artifactId>graphql-maven-plugin</artifactId>
    <version>1.18</version>
    <executions>
        <execution>
            <goals>
                <goal>generateClientCode</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <packageName>cn.tuyucheng.taketoday.graphql.generated</packageName>
        <copyRuntimeSources>false</copyRuntimeSources>
        <generateDeprecatedRequestResponse>false</generateDeprecatedRequestResponse>
        <separateUtilityClasses>true</separateUtilityClasses>
    </configuration>
</plugin>
```

一旦我们运行Maven构建命令，该插件将生成调用我们的GraphQL服务所需的DTO和实用程序类。

生成的QueryExecutor组件将包含调用我们的GraphQL服务和解析其响应的方法：

```java
public List<Book> allBooks(String queryResponseDef, Object... paramsAndValues) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
    logger.debug("Executing query 'allBooks': {} ", queryResponseDef);
    ObjectResponse objectResponse = getAllBooksResponseBuilder()
      .withQueryResponseDef(queryResponseDef).build();
    return allBooksWithBindValues(objectResponse, graphqlClientUtils.generatesBindVariableValuesMap(paramsAndValues));
}
```

但是，它是为与[Spring]()框架一起使用而构建的。

## 6. 总结

在本文中，我们探讨了如何从Java应用程序调用GraphQL服务，我们学习了如何创建和Mock一个简单的GraphQL服务器。接下来，我们了解了如何使用标准HTTP库从GraphQL服务器发送请求和检索响应，并看到了如何将GraphQL服务响应从JSON解析为Java对象。

最后，我们介绍两个可用的第3方库来调用GraphQL服务，Nodes和GraphQLJavaGenerator。