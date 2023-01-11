## **一、概述**

在本文中，我们将深入探讨与全文搜索引擎相关的一些关键概念，并特别关注 Elasticsearch。

由于这是一篇面向 Java 的文章，我们不会提供有关如何设置 Elasticsearch 的详细分步教程并展示它在幕后如何工作。相反，我们将以 Java 客户端为目标，以及如何使用*index*、*delete*、*get*和*search*等主要功能。

## **2.设置**

为了简单起见，我们将为 Elasticsearch 实例使用 docker 镜像，尽管**任何侦听端口 9200 的 Elasticsearch 实例都可以**。

我们首先启动我们的 Elasticsearch 实例：

```bash
docker run -d --name es762 -p 9200:9200 -e "discovery.type=single-node" elasticsearch:7.6.2复制
```

默认情况下，Elasticsearch 在 9200 端口上侦听即将到来的 HTTP 查询。*我们可以通过在您喜欢的浏览器中打开http://localhost:9200/* URL来验证它是否成功启动：

```javascript
{
  "name" : "M4ojISw",
  "cluster_name" : "docker-cluster",
  "cluster_uuid" : "CNnjvDZzRqeVP-B04D3CmA",
  "version" : {
    "number" : "7.6.2",
    "build_flavor" : "default",
    "build_type" : "docker",
    "build_hash" : "2f4c224",
    "build_date" : "2020-03-18T23:22:18.622755Z",
    "build_snapshot" : false,
    "lucene_version" : "8.4.0",
    "minimum_wire_compatibility_version" : "6.8.0",
    "minimum_index_compatibility_version" : "6.8.0-beta1"
  },
  "tagline" : "You Know, for Search"
}复制
```

## **3.Maven配置**

现在我们已经启动并运行了基本的 Elasticsearch 集群，让我们直接跳转到 Java 客户端。首先，我们需要在*pom.xml*文件中声明以下[Maven 依赖项：](https://search.maven.org/classic/#search|ga|1|g%3A"org.elasticsearch" AND a%3A"elasticsearch")

```xml
<dependency>
    <groupId>org.elasticsearch</groupId>
    <artifactId>elasticsearch</artifactId>
    <version>7.6.2</version>
</dependency>复制
```

您始终可以使用之前提供的链接查看由 Maven Central 托管的最新版本。

## **4.Java API**

在我们直接跳到如何使用主要 Java API 功能之前，我们需要启动*RestHighLevelClient* *：*

```java
ClientConfiguration clientConfiguration =
    ClientConfiguration.builder().connectedTo("localhost:9200").build();
RestHighLevelClient client = RestClients.create(clientConfiguration).rest();复制
```

### **4.1. 索引文档**

*index()*函数允许存储任意 JSON 文档并使其可搜索：

```java
@Test
public void givenJsonString_whenJavaObject_thenIndexDocument() {
  String jsonObject = "{\"age\":10,\"dateOfBirth\":1471466076564,"
    +"\"fullName\":\"John Doe\"}";
  IndexRequest request = new IndexRequest("people");
  request.source(jsonObject, XContentType.JSON);
  
  IndexResponse response = client.index(request, RequestOptions.DEFAULT);
  String index = response.getIndex();
  long version = response.getVersion();
    
  assertEquals(Result.CREATED, response.getResult());
  assertEquals(1, version);
  assertEquals("people", index);
}复制
```

请注意，可以使用**[任何 JSON Java 库](https://www.baeldung.com/java-json)**来创建和处理您的文档。**如果您不熟悉其中的任何一个，您可以使用 Elasticsearch 助手来生成您自己的 JSON 文档**：

```java
XContentBuilder builder = XContentFactory.jsonBuilder()
  .startObject()
  .field("fullName", "Test")
  .field("dateOfBirth", new Date())
  .field("age", "10")
  .endObject();

  IndexRequest indexRequest = new IndexRequest("people");
  indexRequest.source(builder);

  IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
  assertEquals(Result.CREATED, response.getResult());复制
```

### **4.2. 查询索引文档**

现在我们已经索引了一个类型化的可搜索 JSON 文档，我们可以继续使用*search()*方法进行搜索：

```java
SearchRequest searchRequest = new SearchRequest();
SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
SearchHit[] searchHits = response.getHits().getHits();
List<Person> results = 
  Arrays.stream(searchHits)
    .map(hit -> JSON.parseObject(hit.getSourceAsString(), Person.class))
    .collect(Collectors.toList());复制
```

***search()\*方法返回的结果称为\*Hits\***，每个*Hit*指的是匹配搜索请求的 JSON 文档。

在这种情况下，*结果*列表包含存储在集群中的所有数据。请注意，在此示例中，我们使用[FastJson](https://www.baeldung.com/fastjson)库将 JSON*字符串*转换为 Java 对象。

我们可以通过添加额外的参数来增强请求，以便使用*QueryBuilders*方法自定义查询：

```java
SearchSourceBuilder builder = new SearchSourceBuilder()
  .postFilter(QueryBuilders.rangeQuery("age").from(5).to(15));

SearchRequest searchRequest = new SearchRequest();
searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
searchRequest.source(builder);

SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);复制
```

### **4.3. 检索和删除文件**

*get()*和 delete *()*方法允许使用其id 从集群中获取或删除 JSON 文档：

```java
GetRequest getRequest = new GetRequest("people");
getRequest.id(id);

GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
// process fields
    
DeleteRequest deleteRequest = new DeleteRequest("people");
deleteRequest.id(id);

DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);复制
```

语法非常简单，您只需在对象 ID 旁边指定索引。

## **5. \*QueryBuilder\*示例**

QueryBuilders类提供了多种*用作*动态匹配器的静态方法来查找集群中的特定条目。在使用*search()*方法在集群中查找特定的 JSON 文档时，我们可以使用查询构建器来自定义搜索结果。

*下面是QueryBuilders* API最常见用途的列表。

matchAllQuery *()*方法返回一个匹配集群中所有文档的*QueryBuilder*对象：

```java
QueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();复制
```

rangeQuery *()*匹配字段值在特定范围内的文档：

```java
QueryBuilder matchDocumentsWithinRange = QueryBuilders
  .rangeQuery("price").from(15).to(100)复制
```

提供一个字段名——例如*fullName*和相应的值——例如*John Doe*，*matchQuery()*方法匹配所有具有这些确切字段值的文档：

```java
QueryBuilder matchSpecificFieldQuery= QueryBuilders
  .matchQuery("fullName", "John Doe");复制
```

我们也可以使用*multiMatchQuery()*方法来构建匹配查询的多字段版本：

```java
QueryBuilder matchSpecificFieldQuery= QueryBuilders.matchQuery(
  "Text I am looking for", "field_1", "field_2^3", "*_field_wildcard");复制
```

**我们可以使用插入符号 (^) 来提升特定字段**。

在我们的示例中，*field_2*的提升值设置为 3，使其比其他字段更重要。请注意，可以使用通配符和正则表达式查询，但在性能方面，处理通配符时要注意内存消耗和响应时间延迟，因为像 *_apples 这样的东西可能会对性能产生巨大影响。

重要性系数用于对执行 s *earch()*方法后返回的命中结果集进行排序。

如果您更熟悉 Lucene 查询语法，则可以使用*simpleQueryStringQuery()*方法自定义搜索查询：

```java
QueryBuilder simpleStringQuery = QueryBuilders
  .simpleQueryStringQuery("+John -Doe OR Janette");复制
```

正如您可能猜到的那样，**我们可以使用 Lucene 的查询解析器语法来构建简单但功能强大的查询**。以下是一些可与*AND/OR/NOT*运算符一起用于构建搜索查询的基本运算符：

-   必需的运算符 ( *+* )：要求特定文本片段存在于文档字段中的某处。
-   禁止运算符（*-*）：排除所有包含在（*-*）符号后声明的关键字的文档。

## **六，结论**

在这篇简短的文章中，我们了解了如何使用 ElasticSearch 的 Java API 来执行一些与全文搜索引擎相关的常见功能。