## **一、概述**

**标记是一种常见的设计模式，它允许我们对数据模型中的项目进行分类和过滤。**

在本文中，我们将使用 Spring 和 Elasticsearch 实现标记。我们将同时使用 Spring Data 和 Elasticsearch API。

首先，我们不会介绍获取 Elasticsearch 和 Spring Data 的基础知识——您可以[在此处](https://www.baeldung.com/spring-data-elasticsearch-tutorial)探索这些内容。

## **2. 添加标签**

**标记的最简单实现是字符串数组。**我们可以通过向我们的数据模型添加一个新字段来实现这一点，如下所示：

```java
@Document(indexName = "blog", type = "article")
public class Article {

    // ...

    @Field(type = Keyword)
    private String[] tags;

    // ...
}复制
```

注意*关键字* 字段类型的使用。我们只希望我们的标签完全匹配来过滤结果。这允许我们使用类似但独立的标签，如*elasticsearchIsAwesome*和*elasticsearchIsTerrible*。

分析的字段将返回部分命中，这在这种情况下是错误的行为。

## **3.构建查询**

标签允许我们以有趣的方式操纵我们的查询。我们可以像任何其他字段一样搜索它们，或者我们可以使用它们来过滤*match_all*查询的结果。我们还可以将它们与其他查询一起使用以收紧我们的结果。

### **3.1. 搜索标签**

我们在模型上创建的新*标签*字段就像我们索引中的所有其他字段一样。我们可以搜索任何具有特定标签的实体，如下所示：

```java
@Query("{\"bool\": {\"must\": [{\"match\": {\"tags\": \"?0\"}}]}}")
Page<Article> findByTagUsingDeclaredQuery(String tag, Pageable pageable);复制
```

此示例使用 Spring Data Repository 来构造我们的查询，但我们可以同样快速地使用[Rest 模板](https://docs.spring.io/spring/docs/3.0.x/javadoc-api/org/springframework/web/client/RestTemplate.html)手动查询 Elasticsearch 集群。

同样，我们可以使用 Elasticsearch API：

```java
boolQuery().must(termQuery("tags", "elasticsearch"));复制
```

假设我们在索引中使用以下文档：

```javascript
[
    {
        "id": 1,
        "title": "Spring Data Elasticsearch",
        "authors": [ { "name": "John Doe" }, { "name": "John Smith" } ],
        "tags": [ "elasticsearch", "spring data" ]
    },
    {
        "id": 2,
        "title": "Search engines",
        "authors": [ { "name": "John Doe" } ],
        "tags": [ "search engines", "tutorial" ]
    },
    {
        "id": 3,
        "title": "Second Article About Elasticsearch",
        "authors": [ { "name": "John Smith" } ],
        "tags": [ "elasticsearch", "spring data" ]
    },
    {
        "id": 4,
        "title": "Elasticsearch Tutorial",
        "authors": [ { "name": "John Doe" } ],
        "tags": [ "elasticsearch" ]
    },
]复制
```

现在我们可以使用这个查询：

```java
Page<Article> articleByTags 
  = articleService.findByTagUsingDeclaredQuery("elasticsearch", PageRequest.of(0, 10));

// articleByTags will contain 3 articles [ 1, 3, 4]
assertThat(articleByTags, containsInAnyOrder(
 hasProperty("id", is(1)),
 hasProperty("id", is(3)),
 hasProperty("id", is(4)))
);复制
```

### **3.2. 过滤所有文件**

一个常见的设计模式是在 UI 中创建一个*过滤列表视图*，显示所有实体，但也允许用户根据不同的标准进行过滤。

假设我们想要返回所有被用户选择的标签过滤的文章：

```java
@Query("{\"bool\": {\"must\": " +
  "{\"match_all\": {}}, \"filter\": {\"term\": {\"tags\": \"?0\" }}}}")
Page<Article> findByFilteredTagQuery(String tag, Pageable pageable);复制
```

再一次，我们使用 Spring Data 来构造我们声明的查询。

因此，我们使用的查询被分成两部分。评分查询是第一项，在本例中为*match_all*。接下来是过滤器查询，它告诉 Elasticsearch 要丢弃哪些结果。

以下是我们如何使用此查询：

```java
Page<Article> articleByTags =
  articleService.findByFilteredTagQuery("elasticsearch", PageRequest.of(0, 10));

// articleByTags will contain 3 articles [ 1, 3, 4]
assertThat(articleByTags, containsInAnyOrder(
  hasProperty("id", is(1)),
  hasProperty("id", is(3)),
  hasProperty("id", is(4)))
);复制
```

重要的是要认识到，虽然这会返回与我们上面的示例相同的结果，但此查询的性能会更好。

### **3.3. 过滤查询**

有时搜索返回的结果太多而无法使用。在那种情况下，最好公开一种可以重新运行相同搜索的过滤机制，只是缩小结果范围。

这是一个示例，我们将作者撰写的文章缩小到仅具有特定标签的文章：

```java
@Query("{\"bool\": {\"must\": " + 
  "{\"match\": {\"authors.name\": \"?0\"}}, " +
  "\"filter\": {\"term\": {\"tags\": \"?1\" }}}}")
Page<Article> findByAuthorsNameAndFilteredTagQuery(
  String name, String tag, Pageable pageable);复制
```

同样，Spring Data 正在为我们完成所有工作。

让我们也看看如何自己构建这个查询：

```java
QueryBuilder builder = boolQuery().must(
  nestedQuery("authors", boolQuery().must(termQuery("authors.name", "doe")), ScoreMode.None))
  .filter(termQuery("tags", "elasticsearch"));复制
```

当然，我们可以使用相同的技术来过滤文档中的任何其他字段。但是标签特别适合这个用例。

以下是如何使用上述查询：

```java
SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(builder)
  .build();
List<Article> articles = 
  elasticsearchTemplate.queryForList(searchQuery, Article.class);

// articles contains [ 1, 4 ]
assertThat(articleByTags, containsInAnyOrder(
 hasProperty("id", is(1)),
 hasProperty("id", is(4)))
);复制
```

## **4.过滤上下文**

当我们构建查询时，我们需要区分查询上下文和过滤上下文。Elasticsearch 中的每个查询都有一个查询上下文，因此我们应该习惯于看到它们。

并非每种查询类型都支持过滤器上下文。因此，如果我们想过滤标签，我们需要知道我们可以使用哪些查询类型。

***bool\*查询有两种访问 Filter Context 的方法**。第一个参数***filter\***是我们上面使用的参数。我们还可以使用***must_not\***参数来激活上下文。

**我们可以过滤的下一个查询类型是\*constant_score\***。当你想用过滤器的结果替换查询上下文并为每个结果分配相同的分数时，这很有用。

**我们可以根据标签过滤的最后一种查询类型是\*过滤器聚合\***。这使我们能够根据过滤器的结果创建聚合组。换句话说，我们可以在聚合结果中按标签对所有文章进行分组。

## **5. 高级标记**

到目前为止，我们只讨论了使用最基本实现的标记。下一个合乎逻辑的步骤是创建本身就是*键值对的*标签。这将使我们的查询和过滤器变得更加有趣。

例如，我们可以将标签字段更改为：

```java
@Field(type = Nested)
private List<Tag> tags;复制
```

然后我们只需更改过滤器以使用*nestedQuery*类型。

一旦我们了解了如何使用*键值对*，这就是使用复杂对象作为我们的标签的一小步。没有多少实现需要一个完整的对象作为标记，但很高兴知道我们有这个选项，如果我们需要的话。

## **六，结论**

在本文中，我们介绍了使用 Elasticsearch 实现标记的基础知识。