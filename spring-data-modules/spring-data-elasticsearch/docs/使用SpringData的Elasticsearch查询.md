## **一、简介**

在[上一篇文章中](https://www.baeldung.com/spring-data-elasticsearch-tutorial)，我们演示了如何为一个项目配置和使用Spring Data Elasticsearch。在本文中，我们将研究 Elasticsearch 提供的几种查询类型，我们还将讨论字段分析器及其对搜索结果的影响。

## **2.分析仪**

默认情况下，所有存储的字符串字段都由分析器处理。分析器由一个分词器和多个分词过滤器组成，通常在一个或多个字符过滤器之前。

默认分析器按常用单词分隔符（例如空格或标点符号）拆分字符串，并将每个标记都设为小写。它还会忽略常见的英语单词。

Elasticsearch 还可以配置为同时将一个字段视为已分析和未分析。

例如，在*文章*类中，假设我们将标题字段存储为标准分析字段。带有后缀*verbatim*的相同字段将存储为未分析字段：

```java
@MultiField(
  mainField = @Field(type = Text, fielddata = true),
  otherFields = {
      @InnerField(suffix = "verbatim", type = Keyword)
  }
)
private String title;复制
```

在这里，我们应用*@MultiField*注释来告诉 Spring Data 我们希望以多种方式对该字段进行索引。主字段将使用名称*标题*，并将根据上述规则进行分析。

但我们还提供了第二个注释*@InnerField*，它描述了*标题*字段的附加索引。我们使用*FieldType.keyword* 来表示我们不想在执行字段的附加索引时使用分析器，并且应该使用带有后缀*verbatim*的嵌套字段存储此值。

### **2.1. 分析字段**

让我们看一个例子。假设将标题为“Spring Data Elasticsearch”的文章添加到我们的索引中。默认分析器将在空格字符处分解字符串并生成小写标记：“ *spring* ”、“ *data”*和“ *elasticsearch* ”。

现在我们可以使用这些术语的任意组合来匹配文档：

```java
NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
  .withQuery(matchQuery("title", "elasticsearch data"))
  .build();复制
```

### **2.2. 非分析字段**

未分析的字段未标记化，因此只能在使用 match 或 term 查询时作为一个整体进行匹配：

```java
NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
  .withQuery(matchQuery("title.verbatim", "Second Article About Elasticsearch"))
  .build();复制
```

使用匹配查询，我们可能只搜索全名，这也是区分大小写的。

## **3.匹配查询**

**匹配查询**接受文本、数字和日期。

有三种类型的“匹配”查询：

-   ***布尔值***
-   ***短语***和
-   ***短语前缀***

在本节中，我们将探索*布尔*匹配查询。

### **3.1. 与布尔运算符匹配**

*boolean*是匹配查询的默认类型；您可以指定要使用的布尔运算符（*or*是默认值）：

```java
NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
  .withQuery(matchQuery("title","Search engines").operator(Operator.AND))
  .build();
SearchHits<Article> articles = elasticsearchTemplate()
  .search(searchQuery, Article.class, IndexCoordinates.of("blog"));复制
```

*此查询将通过使用和*运算符从标题中指定两个术语来返回标题为“搜索引擎”的文章。但是，如果我们在只有一个术语匹配时使用默认 (*或*) 运算符进行搜索，会发生什么情况？

```java
NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
  .withQuery(matchQuery("title", "Engines Solutions"))
  .build();
SearchHits<Article> articles = elasticsearchTemplate()
  .search(searchQuery, Article.class, IndexCoordinates.of("blog"));
assertEquals(1, articles.getTotalHits());
assertEquals("Search engines", articles.getSearchHit(0).getContent().getTitle());复制
```

“*搜索引擎*”文章仍然匹配，但它的分数较低，因为并非所有术语都匹配。

每个匹配项的分数之和加起来就是每个结果文档的总分。

在某些情况下，包含查询中输入的稀有术语的文档比包含多个常用术语的文档具有更高的排名。

### **3.2. 模糊性**

当用户在单词中输入错误时，仍然可以通过指定*模糊*参数将其与搜索匹配，这允许不精确匹配。

对于字符串字段，*模糊*性意味着编辑距离：需要对一个字符串进行单个字符更改以使其与另一个字符串相同的次数。

```java
NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
  .withQuery(matchQuery("title", "spring date elasticsearch")
  .operator(Operator.AND)
  .fuzziness(Fuzziness.ONE)
  .prefixLength(3))
  .build();复制
```

prefix_length参数*用于*提高性能。在这种情况下，我们要求前三个字符必须完全匹配，这样可以减少可能的组合数量。

## **5.短语搜索**

阶段搜索更严格，尽管您可以使用*slop*参数控制它。此参数告诉短语查询允许术语相隔多远，同时仍将文档视为匹配项。

换句话说，它表示为了使查询和文档匹配而需要移动术语的次数：

```java
NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
  .withQuery(matchPhraseQuery("title", "spring elasticsearch").slop(1))
  .build();复制
```

此处查询将匹配标题为“ *Spring Data Elasticsearch* ”的文档，因为我们将斜率设置为 1。

## **6. 多匹配查询**

当你想在多个字段中搜索时，你可以使用*QueryBuilders#multiMatchQuery()*指定所有要匹配的字段：

```java
NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
  .withQuery(multiMatchQuery("tutorial")
    .field("title")
    .field("tags")
    .type(MultiMatchQueryBuilder.Type.BEST_FIELDS))
  .build();复制
```

在这里，我们搜索*title*和*tags*字段以进行匹配。

请注意，这里我们使用“最佳字段”评分策略。它将字段中的最大分数作为文档分数。

## **7.聚合**

在我们的*Article*类中，我们还定义了一个未分析的*标签*字段。我们可以使用聚合轻松创建标签云。

请记住，因为该字段是未分析的，所以标签不会被标记化：

```java
TermsAggregationBuilder aggregation = AggregationBuilders.terms("top_tags")
  .field("tags")
  .order(Terms.Order.count(false));
SearchSourceBuilder builder = new SearchSourceBuilder().aggregation(aggregation);

SearchRequest searchRequest = 
  new SearchRequest().indices("blog").types("article").source(builder);
SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

Map<String, Aggregation> results = response.getAggregations().asMap();
StringTerms topTags = (StringTerms) results.get("top_tags");

List<String> keys = topTags.getBuckets()
  .stream()
  .map(b -> b.getKeyAsString())
  .collect(toList());
assertEquals(asList("elasticsearch", "spring data", "search engines", "tutorial"), keys);复制
```

## **8.总结**

在本文中，我们讨论了分析字段和非分析字段之间的区别，以及这种区别如何影响搜索。

我们还了解了Elasticsearch提供的几种查询类型，例如匹配查询、词组匹配查询、全文搜索查询和布尔查询。

Elasticsearch 提供了许多其他类型的查询，例如地理查询、脚本查询和复合查询。[您可以在Elasticsearch 文档](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl.html)中阅读它们并探索 Spring Data Elasticsearch API 以便在您的代码中使用这些查询。