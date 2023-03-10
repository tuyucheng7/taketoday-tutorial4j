## 1. 概述

**标签是一种常见的设计模式，它允许我们对数据模型中的项目进行分类和过滤**。

在本文中，我们将使用Spring和Elasticsearch实现标签。我们将同时使用Spring Data和Elasticsearch API。

首先，我们不会介绍获取Elasticsearch和Spring Data的基础知识-你可以在[此处](https://www.baeldung.com/spring-data-elasticsearch-tutorial)探索这些内容。

## 2. 添加标签

**标签的最简单实现是字符串数组**。我们可以通过向我们的数据模型添加一个新字段来实现这一点，如下所示：

```java
@Document(indexName = "blog", type = "article")
public class Article {

    // ...

    @Field(type = Keyword)
    private String[] tags;

    // ...
}
```

注意Keyword字段类型的使用。我们只希望我们的标签完全匹配来过滤结果。这允许我们使用类似但独立的标签，如elasticsearchIsAwesome和elasticsearchIsTerrible。

分析的字段将返回部分命中，这在这种情况下是错误的行为。

## 3. 构建查询

标签允许我们以有趣的方式操纵我们的查询。我们可以像任何其他字段一样搜索它们，或者我们可以使用它们来过滤match_all查询的结果。我们还可以将它们与其他查询一起使用以紧凑我们的结果。

### 3.1 搜索标签

我们在模型上创建的新标签字段就像我们索引中的所有其他字段一样。我们可以搜索任何具有特定标签的实体，如下所示：

```java
@Query("{\"bool\": {\"must\": [{\"match\": {\"tags\": \"?0\"}}]}}")
Page<Article> findByTagUsingDeclaredQuery(String tag, Pageable pageable);
```

此示例使用Spring Data Repository来构造我们的查询，但我们也可以同样快速地使用[RestTemplate](https://docs.spring.io/spring/docs/3.0.x/javadoc-api/org/springframework/web/client/RestTemplate.html)手动查询Elasticsearch集群。

同样，我们可以使用Elasticsearch API：

```java
boolQuery().must(termQuery("tags", "elasticsearch"));
```

假设我们在索引中使用以下文档：

```json
[
    {
        "id": 1,
        "title": "Spring Data Elasticsearch",
        "authors": [
            {
                "name": "John Doe"
            },
            {
                "name": "John Smith"
            }
        ],
        "tags": [
            "elasticsearch",
            "spring data"
        ]
    },
    {
        "id": 2,
        "title": "Search engines",
        "authors": [
            {
                "name": "John Doe"
            }
        ],
        "tags": [
            "search engines",
            "tutorial"
        ]
    },
    {
        "id": 3,
        "title": "Second Article About Elasticsearch",
        "authors": [
            {
                "name": "John Smith"
            }
        ],
        "tags": [
            "elasticsearch",
            "spring data"
        ]
    },
    {
        "id": 4,
        "title": "Elasticsearch Tutorial",
        "authors": [
            {
                "name": "John Doe"
            }
        ],
        "tags": [
            "elasticsearch"
        ]
    }
]
```

现在我们可以使用这个查询：

```java
Page<Article> articleByTags = articleService.findByTagUsingDeclaredQuery("elasticsearch", PageRequest.of(0, 10));

// articleByTags will contain 3 articles [ 1, 3, 4]
assertThat(articleByTags, containsInAnyOrder(
    hasProperty("id", is(1)),
    hasProperty("id", is(3)),
    hasProperty("id", is(4)))
);
```

### 3.2 过滤所有文档

一个常见的设计模式是在UI中创建一个过滤列表视图，显示所有实体，但也允许用户根据不同的标准进行过滤。

假设我们想要返回所有被用户选择的标签过滤的文章：

```java
@Query("{\"bool\": {\"must\": " +  "{\"match_all\": {}}, \"filter\": {\"term\": {\"tags\": \"?0\" }}}}")
Page<Article> findByFilteredTagQuery(String tag, Pageable pageable);
```

再一次，我们使用Spring Data来构造我们声明的查询。

因此，我们使用的查询被分成两部分。评分查询是第一项，在本例中为match_all。接下来是过滤器查询，它告诉Elasticsearch要丢弃哪些结果。

以下是我们如何使用此查询：

```java
Page<Article> articleByTags = articleService.findByFilteredTagQuery("elasticsearch", PageRequest.of(0, 10));

// articleByTags will contain 3 articles [ 1, 3, 4]
assertThat(articleByTags, containsInAnyOrder(
    hasProperty("id", is(1)),
    hasProperty("id", is(3)),
    hasProperty("id", is(4)))
);
```

重要的是要认识到，虽然这会返回与我们上面的示例相同的结果，但此查询的性能会更好。

### 3.3 过滤查询

有时搜索返回的结果太多而无法使用。在那种情况下，最好公开一种可以重新运行相同搜索的过滤机制，只是缩小结果范围。

这是一个示例，我们将作者撰写的文章缩小到仅具有特定标签的文章：

```java
@Query("{\"bool\": {\"must\": " + 
    "{\"match\": {\"authors.name\": \"?0\"}}, " +
    "\"filter\": {\"term\": {\"tags\": \"?1\" }}}}")
Page<Article> findByAuthorsNameAndFilteredTagQuery(String name, String tag, Pageable pageable);
```

同样，Spring Data正在为我们完成所有工作。

让我们也看看如何自己构建这个查询：

```java
QueryBuilder builder = boolQuery().must(
    nestedQuery("authors", boolQuery().must(termQuery("authors.name", "doe")), ScoreMode.None))
    .filter(termQuery("tags", "elasticsearch"));
```

当然，我们可以使用相同的技术来过滤文档中的任何其他字段。但是标签特别适合这个用例。

以下是如何使用上述查询：

```java
SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(builder)
    .build();
List<Article> articles = elasticsearchTemplate.queryForList(searchQuery, Article.class);

// articles contains [ 1, 4 ]
assertThat(articleByTags, containsInAnyOrder(
    hasProperty("id", is(1)),
    hasProperty("id", is(4)))
);
```

## 4. 过滤上下文

当我们构建查询时，我们需要区分查询上下文和过滤上下文。Elasticsearch中的每个查询都有一个查询上下文，因此我们应该习惯于看到它们。

并非每种查询类型都支持过滤器上下文。因此，如果我们想过滤标签，我们需要知道我们可以使用哪些查询类型。

**bool查询有两种访问过滤器上下文的方法**。第一个参数filter是我们上面使用的参数。我们还可以使用must_not参数来激活上下文。

**我们可以过滤的下一个查询类型是constant_score**。当你想用过滤器的结果替换查询上下文并为每个结果分配相同的分数时，这很有用。

**我们可以根据标签过滤的最后一种查询类型是filter aggregation**。这使我们能够根据过滤器的结果创建聚合组。换句话说，我们可以在聚合结果中按标签对所有文章进行分组。

## 5. 高级标签

到目前为止，我们只讨论了使用最基本实现的标签。下一个合乎逻辑的步骤是创建本身就是键值对的标签。这将使我们的查询和过滤器变得更加有趣。

例如，我们可以将tags字段更改为：

```java
@Field(type = Nested)
private List<Tag> tags;
```

然后我们只需更改过滤器以使用nestedQuery类型。

一旦我们理解了如何使用键值对，这就是使用复杂对象作为我们的标签的一小步。没有多少实现需要一个完整的对象作为标签，但很高兴知道我们有这个选项，如果我们需要的话。

## 6. 总结

在本文中，我们介绍了使用Elasticsearch实现标签的基础知识。