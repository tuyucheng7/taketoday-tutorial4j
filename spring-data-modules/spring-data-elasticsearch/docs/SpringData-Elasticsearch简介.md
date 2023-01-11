## **一、概述**

在本教程中，我们将以代码为中心和实用的方式**探索 Spring Data Elasticsearch 的基础知识。**

我们将学习如何使用 Spring Data Elasticsearch 在 Spring 应用程序中索引、搜索和查询 Elasticsearch。Spring Data Elasticseach 是一个实现 Spring Data 的 Spring 模块，因此提供了一种与流行的开源、基于 Lucene 的搜索引擎进行交互的方式。

**虽然 Elasticsearch 可以在没有几乎没有定义的模式的情况下工作，但设计一个模式并创建映射以指定我们在某些领域期望的数据类型仍然是一种常见的做法**。当一个文档被索引时，它的字段会根据它们的类型进行处理。例如，文本字段将根据映射规则进行标记和过滤。我们还可以创建自己的过滤器和分词器。

为了简单起见，我们将为 Elasticsearch 实例使用 docker 镜像，尽管**任何侦听端口 9200 的 Elasticsearch 实例都可以**。

我们将从启动 Elasticsearch 实例开始：

```bash
docker run -d --name es762 -p 9200:9200 -e "discovery.type=single-node" elasticsearch:7.6.2复制
```

## **2. 弹簧数据**

Spring Data 有助于避免样板代码。例如，如果我们定义一个存储库接口，它扩展了Spring Data Elasticsearch 提供的*ElasticsearchRepository接口**，*那么对应文档类的 CRUD 操作将默认可用。

此外，方法实现将通过以预定义格式声明名称的方法简单地为我们生成。无需编写存储库接口的实现。

[Spring Data](https://www.baeldung.com/spring-data)上的 Baeldung 指南提供了开始该主题的基础知识。

### **2.1. Maven 依赖**

Spring Data Elasticsearch 为搜索引擎提供了 Java API。为了使用它，我们需要向*pom.xml*添加一个新的依赖项：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-elasticsearch</artifactId>
    <version>4.0.0.RELEASE</version>
</dependency>复制
```

### **2.2. 定义存储库接口**

为了定义新的存储库，我们将扩展提供的存储库接口之一，用我们的实际文档和主键类型替换通用类型。

请务必注意，*ElasticsearchRepository*是从*PagingAndSortingRepository 扩展而来的。*这允许对分页和排序的内置支持。

在我们的示例中，我们将在自定义搜索方法中使用分页功能：

```java
public interface ArticleRepository extends ElasticsearchRepository<Article, String> {

    Page<Article> findByAuthorsName(String name, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"authors.name\": \"?0\"}}]}}")
    Page<Article> findByAuthorsNameUsingCustomQuery(String name, Pageable pageable);
}复制
```

使用*findByAuthorsName*方法，存储库代理将根据方法名称创建一个实现。解析算法会判断需要访问*authors*属性，然后搜索每一项的*name属性。*

第二种方法*findByAuthorsNameUsingCustomQuery*使用自定义 Elasticsearch 布尔查询，该查询使用*@Query*注释定义，这需要作者姓名与提供的*名称*参数之间的严格匹配。

### **2.3. Java配置**

在我们的 Java 应用程序中配置 Elasticsearch 时，我们需要定义我们如何连接到 Elasticsearch 实例。为此，我们将使用Elasticsearch 依赖项提供的*RestHighLevelClient ：*

```java
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.baeldung.spring.data.es.repository")
@ComponentScan(basePackages = { "com.baeldung.spring.data.es.service" })
public class Config {

    @Bean
    public RestHighLevelClient client() {
        ClientConfiguration clientConfiguration 
            = ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .build();

        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(client());
    }
}复制
```

我们正在使用标准的支持 Spring 的样式注释。*@EnableElasticsearchRepositories*将使 Spring Data Elasticsearch 扫描提供的 Spring Data 存储库包。

为了与我们的 Elasticsearch 服务器通信，我们将使用一个简单的*[RestHighLevelClient](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high.html)**。*虽然 Elasticsearch 提供了多种类型的客户端，但使用 *RestHighLevelClient*是一种面向未来的与服务器通信的好方法。

最后，我们将设置一个*ElasticsearchOperations* bean 来在我们的服务器上执行操作。在这种情况下，我们实例化一个*ElasticsearchRestTemplate*。

## **3.映射**

我们使用映射来为我们的文档定义模式。通过为我们的文档定义模式，我们可以保护它们免受意外结果的影响，例如映射到不需要的类型。

我们的实体是一个简单的文档*Article，*其中*id*是*String*类型。我们还将指定此类文档必须存储在文章类型中名为blog*的*索引*中。*

```java
@Document(indexName = "blog", type = "article")
public class Article {

    @Id
    private String id;
    
    private String title;
    
    @Field(type = FieldType.Nested, includeInParent = true)
    private List<Author> authors;
    
    // standard getters and setters
}复制
```

索引可以有多种类型，我们可以使用它们来实现层次结构。

我们将*作者*字段标记为*FieldType.Nested*。这允许我们单独定义*Author*类，但当文章在 Elasticsearch 中建立索引时，作者的各个实例仍然嵌入在*Article文档中。*

## **4.索引文档**

Spring Data Elasticsearch 一般会根据项目中的实体自动创建索引。但是，我们也可以通过客户端模板以编程方式创建索引：

```java
elasticsearchTemplate.indexOps(Article.class).create();复制
```

然后我们可以将文档添加到索引中：

```java
Article article = new Article("Spring Data Elasticsearch");
article.setAuthors(asList(new Author("John Smith"), new Author("John Doe")));
articleRepository.save(article);复制
```

## **5.查询**

### **5.1. 基于方法名称的查询**

当我们使用基于名称的查询方法时，我们编写了定义我们要执行的查询的方法。在设置期间，Spring Data 将解析方法签名并相应地创建查询：

```java
String nameToFind = "John Smith";
Page<Article> articleByAuthorName
  = articleRepository.findByAuthorsName(nameToFind, PageRequest.of(0, 10));复制
```

通过使用*PageRequest*对象调用*findByAuthorsName*，我们将获得结果的第一页（页码从零开始），该页最多包含 10 篇文章。page 对象还提供查询的总命中数，以及其他方便的分页信息。

### **5.2. 自定义查询**

有几种方法可以为 Spring Data Elasticsearch 存储库定义自定义查询。一种方法是使用*@Query*注释，如 2.2 节中所示。

另一种选择是使用查询构建器来创建我们的自定义查询。

如果我们想搜索标题中包含“*数据*”一词的文章，我们只需创建一个带有标题过滤器的*NativeSearchQueryBuilder* *：*

```java
Query searchQuery = new NativeSearchQueryBuilder()
   .withFilter(regexpQuery("title", ".*data.*"))
   .build();
SearchHits<Article> articles = 
   elasticsearchTemplate.search(searchQuery, Article.class, IndexCoordinates.of("blog");复制
```

## **6.更新和删除**

为了更新文档，我们必须首先检索它：

```java
String articleTitle = "Spring Data Elasticsearch";
Query searchQuery = new NativeSearchQueryBuilder()
  .withQuery(matchQuery("title", articleTitle).minimumShouldMatch("75%"))
  .build();

SearchHits<Article> articles = 
   elasticsearchTemplate.search(searchQuery, Article.class, IndexCoordinates.of("blog");
Article article = articles.getSearchHit(0).getContent();复制
```

然后我们可以通过使用评估器编辑对象的内容来更改文档：

```java
article.setTitle("Getting started with Search Engines");
articleRepository.save(article);复制
```

至于删除，有几种选择。我们可以使用*delete*方法检索文档并删除它：

```java
articleRepository.delete(article);复制
```

一旦我们知道它，我们也可以通过*id删除它：*

```java
articleRepository.deleteById("article_id");复制
```

也可以创建自定义*deleteBy*查询并使用 Elasticsearch 提供的批量删除功能：

```java
articleRepository.deleteByTitle("title");复制
```

## **七、结论**

在本文中，我们探讨了如何连接和使用 Spring Data Elasticsearch。我们讨论了如何查询、更新和删除文档。最后，我们学习了如果 Spring Data Elasticsearch 提供的内容不符合我们的需求，如何创建自定义查询。