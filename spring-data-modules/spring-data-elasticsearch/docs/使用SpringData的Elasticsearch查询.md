## 1. 概述

在[上一篇文章](https://www.baeldung.com/spring-data-elasticsearch-tutorial)中，我们演示了如何为一个项目配置和使用Spring Data Elasticsearch。在本文中，我们将研究Elasticsearch提供的几种查询类型，我们还将讨论字段分析器及其对搜索结果的影响。

## 2. 分析器

默认情况下，所有存储的字符串字段都由分析器处理。分析器由一个分词器和多个分词过滤器组成，通常在一个或多个字符过滤器之前。

默认分析器按常用单词分隔符(例如空格或标点符号)拆分字符串，并将每个标记都设为小写。它还会忽略常见的英语单词。

Elasticsearch还可以配置为同时将一个字段视为已分析和未分析。

例如，在Article类中，假设我们将title字段存储为标准分析字段。带有后缀verbatim的相同字段将存储为未分析字段：

```java
@MultiField(
    mainField = @Field(type = Text, fielddata = true),
    otherFields = {
        @InnerField(suffix = "verbatim", type = Keyword)
    }
)
private String title;
```

在这里，我们应用@MultiField注解来告诉Spring Data我们希望以多种方式对该字段进行索引。主字段将使用名称title，并将根据上述规则进行分析。

但我们还提供了第二个注解@InnerField，它描述了title字段的附加索引。我们使用FieldType.keyword来表示我们不想在执行字段的附加索引时使用分析器，并且应该使用带有后缀verbatim的嵌套字段存储此值。

### 2.1 分析字段

让我们看一个例子。假设将标题为“Spring Data Elasticsearch”的文章添加到我们的索引中。默认分析器将在空格字符处分解字符串并生成小写标记：“spring”、“data”和“elasticsearch”。

现在我们可以使用这些术语的任意组合来匹配文档：

```java
NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
    .withQuery(matchQuery("title", "elasticsearch data"))
    .build();
```

### 2.2 非分析字段

未分析的字段不会标记化，因此只能在使用match或term查询时作为一个整体进行匹配：

```java
NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
    .withQuery(matchQuery("title.verbatim", "Second Article About Elasticsearch"))
    .build();
```

使用匹配查询，我们可能只按完整标题进行搜索，这也是区分大小写的。

## 3. 匹配查询

**匹配查询**接收文本、数字和日期。

有三种类型的“匹配”查询：

- **boolean**
- **phrase**
- **phrase_prefix**

在本节中，我们将探讨布尔匹配查询。

### 3.1 与布尔运算符匹配

boolean是匹配查询的默认类型；你可以指定要使用的布尔运算符(or是默认值)：

```java
NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
    .withQuery(matchQuery("title","Search engines").operator(Operator.AND))
    .build();
SearchHits<Article> articles = elasticsearchTemplate()
    .search(searchQuery, Article.class, IndexCoordinates.of("blog"));
```

此查询将通过使用and运算符从标题中指定两个术语来返回标题为“Search engines”的文章。但是，如果我们在只有一个术语匹配时使用默认(or)运算符进行搜索，会发生什么情况？

```java
NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
    .withQuery(matchQuery("title", "Engines Solutions"))
    .build();
SearchHits<Article> articles = elasticsearchTemplate()
    .search(searchQuery, Article.class, IndexCoordinates.of("blog"));
assertEquals(1, articles.getTotalHits());
assertEquals("Search engines", articles.getSearchHit(0).getContent().getTitle());
```

“Search engines”文章仍然匹配，但它的分数较低，因为并非所有术语都匹配。

每个匹配术语的分数之和加起来就是每个结果文档的总分。

在某些情况下，包含查询中输入的稀有术语的文档比包含多个常用术语的文档具有更高的排名。

### 3.2 模糊性

当用户在单词中输入拼写错误时，仍然可以通过指定模糊参数将其与搜索匹配，这允许不精确匹配。

对于字符串字段，模糊性意味着编辑距离：需要对一个字符串进行单个字符更改以使其与另一个字符串相同的次数。

```java
NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
    .withQuery(matchQuery("title", "spring date elasticsearch")
    .operator(Operator.AND)
    .fuzziness(Fuzziness.ONE)
    .prefixLength(3))
    .build();
```

prefix_length参数用于提高性能。在这种情况下，我们要求前三个字符必须完全匹配，这样可以减少可能的组合数量。

## 5. 短语搜索

短语搜索更严格，尽管你可以使用slop参数控制它。此参数告诉短语查询在仍将文档视为匹配项时允许的术语相距多远。

换句话说，它表示为了使查询和文档匹配而需要移动术语的次数：

```java
NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
    .withQuery(matchPhraseQuery("title", "spring elasticsearch").slop(1))
    .build();
```

在这里，查询将匹配标题为“Spring Data Elasticsearch”的文档，因为我们将slop设置为1。

## 6. 多匹配查询

当你想在多个字段中搜索时，你可以使用QueryBuilders#multiMatchQuery()指定所有要匹配的字段：

```java
NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
    .withQuery(multiMatchQuery("tutorial")
        .field("title")
        .field("tags")
        .type(MultiMatchQueryBuilder.Type.BEST_FIELDS))
    .build();
```

在这里，我们在title和tags字段中搜索匹配项。

请注意，这里我们使用“BEST_FIELDS”评分策略。它将字段中的最高分数作为文档分数。

## 7. 聚合

在我们的Article类中，我们还定义了一个未分析的tags字段。我们可以使用聚合轻松创建标签云。

请记住，由于该字段是未分析的，因此标签不会被标记化：

```java
TermsAggregationBuilder aggregation = AggregationBuilders.terms("top_tags")
    .field("tags")
    .order(Terms.Order.count(false));
SearchSourceBuilder builder = new SearchSourceBuilder().aggregation(aggregation);

SearchRequest searchRequest = new SearchRequest().indices("blog").types("article").source(builder);
SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

Map<String, Aggregation> results = response.getAggregations().asMap();
StringTerms topTags = (StringTerms) results.get("top_tags");

List<String> keys = topTags.getBuckets()
    .stream()
    .map(b -> b.getKeyAsString())
    .collect(toList());
assertEquals(asList("elasticsearch", "spring data", "search engines", "tutorial"), keys);
```

## 8. 总结

在本文中，我们讨论了分析字段和非分析字段之间的区别，以及这种区别如何影响搜索。

我们还了解了Elasticsearch提供的几种查询类型，例如匹配查询、短语匹配查询、全文搜索查询和布尔查询。

Elasticsearch提供了许多其他类型的查询，例如地理查询、脚本查询和复合查询。你可以在[Elasticsearch文档](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl.html)中阅读它们并探索Spring Data Elasticsearch API以便在你的代码中使用这些查询。