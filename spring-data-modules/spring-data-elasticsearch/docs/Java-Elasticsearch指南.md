## 1. 概述

在本文中，我们将深入探讨与全文搜索引擎相关的一些关键概念，并特别关注Elasticsearch。

由于这是一篇面向Java的文章，我们不会提供有关如何设置Elasticsearch的详细分步教程并展示它在幕后如何工作。相反，我们将以Java客户端为目标，以及如何使用index、delete、get和search等主要功能。

## 2. 设置

为了简单起见，我们将为Elasticsearch实例使用Docker镜像，**任何监听端口9200的Elasticsearch实例都可以**。

我们首先启动我们的Elasticsearch实例：

```shell
docker run -d --name es762 -p 9200:9200 -e "discovery.type=single-node" elasticsearch:7.6.2
```

默认情况下，Elasticsearch在9200端口上监听即将到来的HTTP查询。我们可以通过在你喜欢的浏览器中打开[http://localhost:9200/](http://localhost:9200/) URL来验证它是否成功启动：

```json
{
    "name": "M4ojISw",
    "cluster_name": "docker-cluster",
    "cluster_uuid": "CNnjvDZzRqeVP-B04D3CmA",
    "version": {
        "number": "7.6.2",
        "build_flavor": "default",
        "build_type": "docker",
        "build_hash": "2f4c224",
        "build_date": "2020-03-18T23:22:18.622755Z",
        "build_snapshot": false,
        "lucene_version": "8.4.0",
        "minimum_wire_compatibility_version": "6.8.0",
        "minimum_index_compatibility_version": "6.8.0-beta1"
    },
    "tagline": "You Know, for Search"
}
```

## 3. Maven配置

现在我们已经启动并运行了基本的Elasticsearch集群，让我们直接跳转到Java客户端。首先，我们需要在pom.xml文件中声明以下[Maven依赖项](https://central.sonatype.com/artifact/org.elasticsearch/elasticsearch/8.6.2)：

```xml
<dependency>
    <groupId>org.elasticsearch</groupId>
    <artifactId>elasticsearch</artifactId>
    <version>7.6.2</version>
</dependency>
```

你始终可以使用之前提供的链接查看由Maven Central托管的最新版本。

## 4. Java API

在我们直接跳到如何使用主要Java API功能之前，我们需要启动RestHighLevelClient：

```java
ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo("localhost:9200").build();
RestHighLevelClient client = RestClients.create(clientConfiguration).rest();
```

### 4.1 索引文档

index()函数允许存储任意JSON文档并使其可搜索：

```java
@Test
public void givenJsonString_whenJavaObject_thenIndexDocument() {
    String jsonObject = "{\"age\":10,\"dateOfBirth\":1471466076564," +"\"fullName\":\"John Doe\"}";
    IndexRequest request = new IndexRequest("people");
    request.source(jsonObject, XContentType.JSON);
    
    IndexResponse response = client.index(request, RequestOptions.DEFAULT);
    String index = response.getIndex();
    long version = response.getVersion();
      
    assertEquals(Result.CREATED, response.getResult());
    assertEquals(1, version);
    assertEquals("people", index);
}
```

请注意，可以使用任何[JSON Java库](https://www.baeldung.com/java-json)来创建和处理你的文档。**如果你不熟悉其中的任何一个，你可以使用Elasticsearch助手来生成你自己的JSON文档**：

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
    assertEquals(Result.CREATED, response.getResult());
```

### 4.2 查询索引文档

现在我们已经索引了一个类型化的可搜索JSON文档，我们可以继续使用search()方法进行搜索：

```java
SearchRequest searchRequest = new SearchRequest();
SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
SearchHit[] searchHits = response.getHits().getHits();
List<Person> results = Arrays.stream(searchHits)
    .map(hit -> JSON.parseObject(hit.getSourceAsString(), Person.class))
    .collect(Collectors.toList());
```

**search()方法返回的结果称为Hits**，每个Hit指的是匹配搜索请求的JSON文档。

在这种情况下，results列表包含存储在集群中的所有数据。请注意，在此示例中，我们使用[FastJson](https://www.baeldung.com/fastjson)库将JSON字符串转换为Java对象。

我们可以通过添加额外的参数来增强请求，以便使用QueryBuilders方法自定义查询：

```java
SearchSourceBuilder builder = new SearchSourceBuilder()
    .postFilter(QueryBuilders.rangeQuery("age").from(5).to(15));

SearchRequest searchRequest = new SearchRequest();
searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
searchRequest.source(builder);

SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
```

### 4.3 检索和删除文档

get()和delete()方法允许使用其id从集群中获取或删除JSON文档：

```java
GetRequest getRequest = new GetRequest("people");
getRequest.id(id);

GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
// process fields
    
DeleteRequest deleteRequest = new DeleteRequest("people");
deleteRequest.id(id);

DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
```

语法非常简单，你只需要在对象ID旁边指定索引。

## 5. QueryBuilder示例

QueryBuilders类提供了多种用作动态匹配器的静态方法来查找集群中的特定条目。在使用search()方法在集群中查找特定的JSON文档时，我们可以使用查询构建器来自定义搜索结果。

下面是QueryBuilders API最常见用途的列表。

matchAllQuery()方法返回一个匹配集群中所有文档的QueryBuilder对象：

```java
QueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
```

rangeQuery()匹配字段值在特定范围内的文档：

```java
QueryBuilder matchDocumentsWithinRange = QueryBuilders
    .rangeQuery("price").from(15).to(100)
```

提供一个字段名-例如fullName，和相应的值-例如JohnDoe，matchQuery()方法匹配所有具有这些确切字段值的文档：

```java
QueryBuilder matchSpecificFieldQuery= QueryBuilders
    .matchQuery("fullName", "John Doe");
```

我们也可以使用multiMatchQuery()方法来构建匹配查询的多字段版本：

```java
QueryBuilder matchSpecificFieldQuery= QueryBuilders.matchQuery("Text I am looking for", "field_1", "field_2^3", "*_field_wildcard");
```

**我们可以使用插入符号(^)来提升特定字段**。

在我们的示例中，field_2的提升值设置为3，使其比其他字段更重要。请注意，可以使用通配符和正则表达式查询，但在性能方面，处理通配符时要注意内存消耗和响应时间延迟，因为像*_apples这样的东西可能会对性能产生巨大影响。

重要性系数用于对执行search()方法后返回的命中结果集进行排序。

如果你更熟悉Lucene查询语法，则可以使用simpleQueryStringQuery()方法自定义搜索查询：

```java
QueryBuilder simpleStringQuery = QueryBuilders
    .simpleQueryStringQuery("+John -Doe OR Janette");
```

正如你可能猜到的那样，**我们可以使用Lucene的查询解析器语法来构建简单但功能强大的查询**。以下是一些可与AND/OR/NOT运算符一起用于构建搜索查询的基本运算符：

-   必需的运算符(+)：要求特定文本片段存在于文档字段中的某处
-   禁止运算符(-)：排除所有包含在(-)符号后声明的关键字的文档

## 6. 总结

在这篇简短的文章中，我们了解了如何使用ElasticSearch的Java API来执行一些与全文搜索引擎相关的常见功能。