## 1. 概述

在本教程中，我们将以代码为中心和实用的方式**探索Spring Data Elasticsearch的基础知识**。

我们将学习如何使用Spring Data Elasticsearch在Spring应用程序中索引、搜索和查询Elasticsearch。Spring Data Elasticsearch是一个实现Spring Data的Spring模块，因此提供了一种与流行的开源、基于Lucene的搜索引擎进行交互的方式。

**虽然Elasticsearch可以在几乎没有定义的模式的情况下工作，但设计一个模式并创建映射以指定我们在某些字段期望的数据类型仍然是一种常见的做法**。当一个文档被索引时，它的字段会根据它们的类型进行处理。例如，文本字段将根据映射规则进行标记和过滤。我们还可以创建自己的过滤器和分词器。

为了简单起见，我们将为Elasticsearch实例使用Docker镜像，使用**任何监听端口9200的Elasticsearch实例都可以**。

我们将从启动我们的Elasticsearch实例开始：

```bash
docker run -d --name es762 -p 9200:9200 -e "discovery.type=single-node" elasticsearch:7.6.2
```

## 2. Spring Data

Spring Data有助于避免样板代码。例如，如果我们定义一个Repository接口来扩展Spring Data Elasticsearch提供的ElasticsearchRepository接口，那么对应文档类的CRUD操作将默认可用。

此外，方法实现将通过以预定义格式声明名称的方法简单地为我们生成。无需编写Repository接口的实现。

[Spring Data](https://www.baeldung.com/spring-data)上的指南提供了开始该主题的基础知识。

### 2.1 Maven依赖

Spring Data Elasticsearch为搜索引擎提供了Java API。为了使用它，我们需要向pom.xml添加一个新的依赖项：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-elasticsearch</artifactId>
    <version>4.0.0.RELEASE</version>
</dependency>
```

### 2.2 定义Repository接口

为了定义新的Repository，我们将扩展提供的Repository接口之一，用我们的实际文档和主键类型替换泛型类型。

需要注意的是，ElasticsearchRepository是从PagingAndSortingRepository扩展而来的。这允许对分页和排序的内置支持。

在我们的示例中，我们将在自定义搜索方法中使用分页功能：

```java
public interface ArticleRepository extends ElasticsearchRepository<Article, String> {

    Page<Article> findByAuthorsName(String name, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"authors.name\": \"?0\"}}]}}")
    Page<Article> findByAuthorsNameUsingCustomQuery(String name, Pageable pageable);
}
```

使用findByAuthorsName方法，Repository代理将根据方法名称创建一个实现。解析算法会判断需要访问authors属性，然后搜索每个项的name属性。

第二种方法findByAuthorsNameUsingCustomQuery使用自定义Elasticsearch布尔查询，该查询使用@Query注解定义，这需要作者姓名与提供的name参数之间的严格匹配。

### 2.3 Java配置

在我们的Java应用程序中配置Elasticsearch时，我们需要定义我们如何连接到Elasticsearch实例。为此，我们将扩展AbstractElasticsearchConfiguration类：

```java
@Configuration
@EnableElasticsearchRepositories(basePackages = "cn.tuyucheng.taketoday.spring.data.es.repository")
@ComponentScan(basePackages = { "cn.tuyucheng.taketoday.spring.data.es.service" })
public class Config extends AbstractElasticsearchConfiguration {

    @Bean
    @Override
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
              .connectedTo("localhost:9200")
              .build();

        return RestClients.create(clientConfiguration)
              .rest();
    }
}
```

我们使用标准的支持Spring风格注解。@EnableElasticsearchRepositories将使Spring Data Elasticsearch扫描提供的Spring Data Repository包。

为了与我们的Elasticsearch服务器通信，我们将使用一个简单的[RestHighLevelClient](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high.html)。虽然Elasticsearch提供了多种类型的客户端，但使用RestHighLevelClient是一种面向未来的与服务器通信的好方法。

在我们的服务器上执行操作所需的ElasticsearchOperations bean已经由基类提供。

## 3. 映射

我们使用映射来为我们的文档定义模式。通过为我们的文档定义模式，我们可以保护它们免受意外结果的影响，例如映射到不需要的类型。

我们的实体是一个简单的文档Article，其中id是String类型。我们还将指定此类文档必须存储在article类型中名为blog的索引中。

```java
@Document(indexName = "blog", type = "article")
public class Article {

    @Id
    private String id;

    private String title;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<Author> authors;

    // standard getters and setters
}
```

索引可以有多种类型，我们可以使用它们来实现层次结构。

我们将authors字段标记为FieldType.Nested。这允许我们单独定义Author类，但当文章在Elasticsearch中建立索引时，作者的各个实例仍然嵌入在Article文档中。

## 4. 索引文档

Spring Data Elasticsearch一般会根据项目中的实体自动创建索引。但是，我们也可以通过操作模板以编程方式创建索引：

```java
elasticsearchTemplate.indexOps(Article.class).create();
```

然后我们可以将文档添加到索引中：

```java
Article article = new Article("Spring Data Elasticsearch");
article.setAuthors(asList(new Author("John Smith"), new Author("John Doe")));
articleRepository.save(article);
```

## 5. 查询

### 5.1 基于方法名称的查询

当我们使用基于名称的查询方法时，我们编写定义我们要执行的查询的方法。在设置期间，Spring Data将解析方法签名并相应地创建查询：

```java
String nameToFind = "John Smith";
Page<Article> articleByAuthorName = articleRepository.findByAuthorsName(nameToFind, PageRequest.of(0, 10));
```

通过使用PageRequest对象调用findByAuthorsName，我们将获得结果的第1页(页码从0开始)，该页最多包含10篇文章。page对象还提供查询的总命中数，以及其他方便的分页信息。

### 5.2 自定义查询

有几种方法可以为Spring Data Elasticsearch Repository定义自定义查询。一种方法是使用@Query注解，如2.2节中所示。

另一种选择是使用查询构建器来创建我们的自定义查询。

如果我们想搜索标题中包含“data”一词的文章，我们只需创建一个带有标题过滤器的NativeSearchQueryBuilder：

```java
Query searchQuery = new NativeSearchQueryBuilder()
    .withFilter(regexpQuery("title", ".*data.*"))
    .build();
SearchHits<Article> articles = elasticsearchTemplate.search(searchQuery, Article.class, IndexCoordinates.of("blog");
```

## 6. 更新和删除

为了更新文档，我们必须首先检索它：

```java
String articleTitle = "Spring Data Elasticsearch";
Query searchQuery = new NativeSearchQueryBuilder()
    .withQuery(matchQuery("title", articleTitle).minimumShouldMatch("75%"))
    .build();

SearchHits<Article> articles = elasticsearchTemplate.search(searchQuery, Article.class, IndexCoordinates.of("blog");
Article article = articles.getSearchHit(0).getContent();
```

然后我们可以通过使用评估器编辑对象的内容来更改文档：

```java
article.setTitle("Getting started with Search Engines");
articleRepository.save(article);
```

至于删除，有几种选择。我们可以使用delete方法检索文档并删除它：

```java
articleRepository.delete(article);
```

一旦我们知道它，我们也可以通过id删除它：

```java
articleRepository.deleteById("article_id");
```

也可以创建自定义deleteBy查询并使用Elasticsearch提供的批量删除功能：

```java
articleRepository.deleteByTitle("title");
```

## 7. 总结

在本文中，我们探讨了如何连接和使用Spring Data Elasticsearch。我们讨论了如何查询、更新和删除文档。最后，我们学习了如果Spring Data Elasticsearch提供的内容不符合我们的需求，如何创建自定义查询。