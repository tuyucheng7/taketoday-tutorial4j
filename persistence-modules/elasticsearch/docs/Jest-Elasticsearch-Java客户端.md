## 1. 简介

任何使用过[Elasticsearch](https://www.baeldung.com/elasticsearch-java)的人都知道，使用他们的[RESTful 搜索 API](https://www.baeldung.com/elasticsearch-full-text-search-rest-api)构建查询可能既乏味又容易出错。

在本教程中，我们将了解[Jest](https://github.com/searchbox-io/Jest)，它是 Elasticsearch 的 HTTPJava客户端。Elasticsearch 提供了自己的原生Java客户端，而Jest 则提供了更流畅的 API 和更易于使用的界面。

## 2.Maven依赖

我们需要做的第一件事是将[Jest 库](https://search.maven.org/search?q=g:io.searchbox a:jest)导入到我们的 POM 中：

```xml
<dependency>
    <groupId>io.searchbox</groupId>
    <artifactId>jest</artifactId>
    <version>6.3.1</version>
</dependency>
```

Jest 的版本控制遵循主要 Elasticsearch 产品的版本控制。这有助于确保客户端和服务器之间的兼容性。

通过包含 Jest 依赖项，相应的[Elasticsearch 库](https://search.maven.org/search?q=g:org.elasticsearch a:elasticsearch)将作为传递依赖项包含在内。

## 3. 使用 Jest 客户端

在本节中，我们将了解如何使用 Jest 客户端通过 Elasticsearch 执行常见任务。

要使用 Jest 客户端，我们只需使用JestClientFactory创建一个JestClient对象。这些对象的创建成本很高并且是线程安全的，因此我们将创建一个可以在整个应用程序中共享的单例实例：

```java
public JestClient jestClient() {
    JestClientFactory factory = new JestClientFactory();
    factory.setHttpClientConfig(
      new HttpClientConfig.Builder("http://localhost:9200")
        .multiThreaded(true)
        .defaultMaxTotalConnectionPerRoute(2)
        .maxTotalConnection(10)
        .build());
    return factory.getObject();
}
```

这将创建一个连接到本地运行的 Elasticsearch 客户端的 Jest 客户端。虽然这个连接示例很简单，但Jest 还完全支持代理、SSL、身份验证，甚至节点发现。

JestClient类是通用的，只有少数公共方法。我们将使用的主要一个是execute，它采用Action接口的一个实例。Jest 客户端提供了几个构建器类来帮助创建与 Elasticsearch 交互的不同操作。

所有 Jest 调用的结果都是JestResult的一个实例。我们可以通过调用isSucceeded检查是否成功。对于不成功的操作，我们可以调用getErrorMessage来获取更多详细信息：

```java
JestResult jestResult = jestClient.execute(new Delete.Builder("1").index("employees").build());

if (jestResult.isSucceeded()) {
    System.out.println("Success!");
}
else {
    System.out.println("Error: " + jestResult.getErrorMessage());
}
```

### 3.1. 管理指数

要检查索引是否存在，我们使用IndicesExists操作：

```java
JestResult result = jestClient.execute(new IndicesExists.Builder("employees").build())

```

要创建索引，我们使用CreateIndex操作：

```java
jestClient.execute(new CreateIndex.Builder("employees").build());
```

这将创建一个具有默认设置的索引。我们可以在索引创建期间覆盖特定设置：

```java
Map<String, Object> settings = new HashMap<>();
settings.put("number_of_shards", 11);
settings.put("number_of_replicas", 2);
jestClient.execute(new CreateIndex.Builder("employees").settings(settings).build());
```

使用ModifyAliases操作创建或更改别名也很简单：

```java
jestClient.execute(new ModifyAliases.Builder(
  new AddAliasMapping.Builder("employees", "e").build()).build());
jestClient.execute(new ModifyAliases.Builder(
  new RemoveAliasMapping.Builder("employees", "e").build()).build());
```

### 3.2. 创建文件

Jest 客户端使用Index操作类可以轻松索引或创建新文档。Elasticsearch 中的文档只是 JSON 数据，有多种方法可以将 JSON 数据传递给 Jest 客户端进行索引。

对于此示例，让我们使用一个虚构的 Employee 文档：

```plaintext
{
    "name": "Michael Pratt",
    "title": "Java Developer",
    "skills": ["java", "spring", "elasticsearch"],
    "yearsOfService": 2
}
```

表示 JSON 文档的第一种方法是使用JavaString。虽然我们可以手动创建 JSON 字符串，但我们必须注意正确的格式、大括号和转义引号字符。

因此，使用诸如[Jackson](https://www.baeldung.com/jackson-object-mapper-tutorial)之类的 JSON 库来构建我们的 JSON 结构然后转换为String会更容易：

```java
ObjectMapper mapper = new ObjectMapper();
JsonNode employeeJsonNode = mapper.createObjectNode()
  .put("name", "Michael Pratt")
  .put("title", "Java Developer")
  .put("yearsOfService", 2)
  .set("skills", mapper.createArrayNode()
    .add("java")
    .add("spring")
    .add("elasticsearch"));
jestClient.execute(new Index.Builder(employeeJsonNode.toString()).index("employees").build());
```

我们还可以使用JavaMap来表示 JSON 数据并将其传递给Index操作：

```java
Map<String, Object> employeeHashMap = new LinkedHashMap<>();
employeeHashMap.put("name", "Michael Pratt");
employeeHashMap.put("title", "Java Developer");
employeeHashMap.put("yearsOfService", 2);
employeeHashMap.put("skills", Arrays.asList("java", "spring", "elasticsearch"));
jestClient.execute(new Index.Builder(employeeHashMap).index("employees").build());
```

最后，Jest 客户端可以接受代表要索引的文档的任何 POJO。假设我们有一个Employee类：

```java
public class Employee {
    String name;
    String title;
    List<String> skills;
    int yearsOfService;
}
```

我们可以将此类的实例直接传递给索引构建器：

```java
Employee employee = new Employee();
employee.setName("Michael Pratt");
employee.setTitle("Java Developer");
employee.setYearsOfService(2);
employee.setSkills(Arrays.asList("java", "spring", "elasticsearch"));
jestClient.execute(new Index.Builder(employee).index("employees").build());
```

### 3.3. 阅读文件

使用 Jest 客户端从 Elasticsearch 访问文档有两种主要方式。首先，如果我们知道文档 ID，我们可以使用Get操作直接访问它：

```java
jestClient.execute(new Get.Builder("employees", "17").build());
```

要访问返回的文档，我们必须调用各种getSource方法之一。我们可以将结果作为原始 JSON 获取，也可以将其反序列化回 DTO：

```java
Employee getResult = jestClient.execute(new Get.Builder("employees", "1").build())
    .getSourceAsObject(Employee.class);
```

访问文档的另一种方法是使用搜索查询，它是在 Jest 中通过Search操作实现的。

Jest 客户端支持完整的 Elasticsearch 查询 DSL。就像索引操作一样，查询被表示为 JSON 文档，并且有多种执行搜索的方法。

首先，我们可以传递一个代表搜索查询的 JSON 字符串。提醒一下，我们必须注意确保字符串被正确转义并且是有效的 JSON：

```java
String search = "{" +
  "  "query": {" +
  "    "bool": {" +
  "      "must": [" +
  "        { "match": { "name":   "Michael Pratt" }}" +
  "      ]" +
  "    }" +
  "  }" +
  "}";
jestClient.execute(new Search.Builder(search).build());
```

与上面的索引操作一样，我们可以使用诸如 Jackson 之类的库来构建我们的 JSON 查询字符串。

此外，我们还可以使用原生的 Elasticsearch 查询操作 API。这样做的一个缺点是我们的应用程序必须依赖于完整的[Elasticsearch 库](https://search.maven.org/search?q=g:org.elasticsearch a:elasticsearch)。

通过Search操作，可以使用getSource方法访问匹配的文档。但是，Jest 还提供了Hit类，它包装了匹配的文档并提供了有关结果的元数据。使用Hit类，我们可以访问每个结果的额外元数据：分数、路由和解释结果，仅举几例：

```java
List<SearchResult.Hit<Employee, Void>> searchResults = 
  jestClient.execute(new Search.Builder(search).build())
    .getHits(Employee.class);
searchResults.forEach(hit -> {
    System.out.println(String.format("Document %s has score %s", hit.id, hit.score));
});
```

### 3.4. 更新文件

Jest 提供了一个简单的Update操作来更新文档：

```java
employee.setYearOfService(3);
jestClient.execute(new Update.Builder(employee).index("employees").id("1").build());
```

它接受与我们之前看到的Index操作相同的 JSON 表示，从而可以轻松地在两个操作之间共享代码。

### 3.5. 删除文件

使用Delete操作从索引中删除文档。它只需要一个索引名称和文档 ID：

```java
jestClient.execute(new Delete.Builder("17")
  .index("employees")
  .build());
```

## 4.批量操作

Jest 客户端还支持批量操作。这意味着我们可以通过同时发送多个操作来节省时间和带宽。

使用批量操作，我们可以将任意数量的请求组合成一个调用。我们甚至可以将不同类型的请求组合在一起：

```java
jestClient.execute(new Bulk.Builder()
  .defaultIndex("employees")
  .addAction(new Index.Builder(employeeObject1).build())
  .addAction(new Index.Builder(employeeObject2).build())
  .addAction(new Delete.Builder("17").build())
  .build());
```

## 5.异步操作

Jest 客户端还支持异步操作，这意味着我们可以使用非阻塞 I/O 执行上述任何操作。

要异步调用操作，只需使用客户端的executeAsync方法即可：

```java
jestClient.executeAsync(
  new Index.Builder(employeeObject1).build(),
  new JestResultHandler<JestResult>() {
      @Override public void completed(JestResult result) {
          // handle result
      }
      @Override public void failed(Exception ex) {
          // handle exception
      }
  });
```

请注意，除了操作(在本例中为索引)之外，异步流还需要JestResultHandler。Jest 客户端将在操作完成时调用此对象。该接口有两种方法——完成和失败——分别允许处理操作的成功或失败。

## 六. 总结

在本教程中，我们简要介绍了 Jest 客户端，它是 Elasticsearch 的 RESTfulJava客户端。

虽然我们只介绍了它的一小部分功能，但很明显 Jest 是一个强大的 Elasticsearch 客户端。其流畅的构建器类和 RESTful 接口使其易于学习，并且其对 Elasticsearch 接口的全面支持使其成为本机客户端的有力替代品。