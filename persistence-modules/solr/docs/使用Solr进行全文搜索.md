## 1. 概述

在本文中，我们将探索[Apache Solr](https://lucene.apache.org/solr/)搜索引擎中的一个基本概念——全文搜索。

Apache Solr 是一个开源框架，旨在处理数以百万计的文档。我们将通过使用Java库[SolrJ](https://cwiki.apache.org/confluence/display/solr/Solrj)的示例来了解它的核心功能。

## 2.Maven配置

鉴于 Solr 是开源的——我们可以简单地下载二进制文件并独立于我们的应用程序启动服务器。

为了与服务器通信，我们将为 SolrJ 客户端定义 Maven 依赖项：

```xml
<dependency>
    <groupId>org.apache.solr</groupId>
    <artifactId>solr-solrj</artifactId>
    <version>6.4.2</version>
</dependency>
```

[你可以在此处](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.solr" AND a%3A"solr-solrj")找到最新的依赖项。

## 3.索引数据

为了索引和搜索数据，我们需要创建一个核心；我们将创建一个命名项来索引我们的数据。

在我们这样做之前，我们需要在服务器上对数据进行索引，以便它可以搜索。

我们可以通过多种不同的方式对数据进行索引。我们可以使用数据导入处理程序直接从关系数据库导入数据，使用 Apache Tika 使用 Solr Cell 上传数据或使用索引处理程序上传 XML/XSLT、JSON 和 CSV 数据。

### 3.1. 索引 Solr 文档

我们可以通过创建SolrInputDocument将数据索引到核心中。首先，我们需要用我们的数据填充文档，然后只调用 SolrJ 的 API 来索引文档：

```java
SolrInputDocument doc = new SolrInputDocument();
doc.addField("id", id);
doc.addField("description", description);
doc.addField("category", category);
doc.addField("price", price);
solrClient.add(doc);
solrClient.commit();
```

请注意，id对于不同的项目自然应该是唯一的。拥有已索引文档的id将更新该文档。

### 3.2. 索引 Bean

SolrJ 提供了用于索引Javabean 的 API。要索引一个 bean，我们需要使用@Field注解对其进行注解：

```java
public class Item {

    @Field
    private String id;

    @Field
    private String description;

    @Field
    private String category;

    @Field
    private float price;
}
```

一旦我们有了 bean，索引就很简单了：

```java
solrClient.addBean(item); 
solrClient.commit();
```

## 4.Solr 查询

搜索是 Solr 最强大的功能。一旦我们在我们的存储库中索引了文档，我们就可以搜索关键字、短语、日期范围等。结果按相关性(分数)排序。

### 4.1. 基本查询

服务器公开用于搜索操作的 API。我们可以调用/select或/query请求处理程序。

让我们做一个简单的搜索：

```java
SolrQuery query = new SolrQuery();
query.setQuery("brand1");
query.setStart(0);
query.setRows(10);

QueryResponse response = solrClient.query(query);
List<Item> items = response.getBeans(Item.class);
```

SolrJ 将在其对服务器的请求中内部使用主要查询参数q 。返回的记录数将为 10，在未指定start和rows时从零开始索引。

上面的搜索查询将查找在其任何索引字段中包含完整单词“brand1”的任何文档。请注意，简单搜索不区分大小写。

让我们看另一个例子。我们要搜索任何包含“rand”的单词，它以任意数量的字符开头，仅以一个字符结尾。我们可以使用通配符和? 在我们的查询中：

```java
query.setQuery("rand?");
```

Solr 查询还支持像 SQL 中的布尔运算符：

```java
query.setQuery("brand1 AND (Washing OR Refrigerator)");
```

所有布尔运算符必须全部大写；查询解析器支持的是AND、OR 、 NOT、+和 – 。

更重要的是，如果我们想搜索特定字段而不是所有索引字段，我们可以在查询中指定这些：

```java
query.setQuery("description:Brand AND category:Washing");
```

### 4.2. 短语查询

到目前为止，我们的代码在索引字段中查找关键字。我们还可以对索引字段进行短语搜索：

```java
query.setQuery("Washing Machine");
```

当我们有一个像“ Washing Machine ”这样的短语时，Solr 的标准查询解析器将它解析为“ Washing OR Machine ”。要搜索整个短语，我们只能在双引号内添加表达式：

```java
query.setQuery(""Washing Machine"");
```

我们可以使用邻近搜索来查找特定距离内的单词。如果我们想找到至少相隔两个单词的单词，我们可以使用以下查询：

```java
query.setQuery(""Washing equipment"~2");
```

### 4.3. 范围查询

范围查询允许获取其字段在特定范围之间的文档。

假设我们要查找价格在 100 到 300 之间的商品：

```java
query.setQuery("price:[100 TO 300]");
```

上面的查询将查找价格在 100 到 300 之间(含)的所有元素。我们可以使用“ } ”和“ { ”来排除端点：

```java
query.setQuery("price:{100 TO 300]");
```

### 4.4. 过滤查询

过滤器查询可用于限制可返回结果的超集。过滤查询不影响分数：

```java
SolrQuery query = new SolrQuery();
query.setQuery("price:[100 TO 300]");
query.addFilterQuery("description:Brand1","category:Home Appliances");
```

通常，过滤器查询将包含常用查询。由于它们通常是可重复使用的，因此会缓存它们以提高搜索效率。

## 5. 分面搜索

分面有助于将搜索结果安排到组计数中。我们可以对字段、查询或范围进行分面。

### 5.1. 场面

例如，我们想要获取搜索结果中类别的聚合计数。我们可以在查询中添加类别字段：

```java
query.addFacetField("category");

QueryResponse response = solrClient.query(query);
List<Count> facetResults = response.getFacetField("category").getValues();
```

facetResults将包含结果中每个类别的计数。

### 5.2. 查询分面

当我们想要返回子查询的计数时，查询分面非常有用：

```java
query.addFacetQuery("Washing OR Refrigerator");
query.addFacetQuery("Brand2");

QueryResponse response = solrClient.query(query);
Map<String,Integer> facetQueryMap = response.getFacetQuery();
```

因此，facetQueryMap将具有 facet 查询的计数。

### 5.3. 范围刻面

范围分面用于获取搜索结果中的范围计数。以下查询将返回 100 到 251 之间价格范围的计数，间隔为 25：

```java
query.addNumericRangeFacet("price", 100, 275, 25);

QueryResponse response = solrClient.query(query);
List<RangeFacet> rangeFacets =  response.getFacetRanges().get(0).getCounts();
```

除了数字范围之外，Solr 还支持日期范围、区间分面和数据透视分面。

## 6.点击突出显示

我们可能希望搜索查询中的关键字在结果中突出显示。这对于更好地了解结果非常有帮助。让我们索引一些文档并定义要突出显示的关键字：

```java
itemSearchService.index("hm0001", "Brand1 Washing Machine", "Home Appliances", 100f);
itemSearchService.index("hm0002", "Brand1 Refrigerator", "Home Appliances", 300f);
itemSearchService.index("hm0003", "Brand2 Ceiling Fan", "Home Appliances", 200f);
itemSearchService.index("hm0004", "Brand2 Dishwasher", "Washing equipments", 250f);

SolrQuery query = new SolrQuery();
query.setQuery("Appliances");
query.setHighlight(true);
query.addHighlightField("category");
QueryResponse response = solrClient.query(query);

Map<String, Map<String, List<String>>> hitHighlightedMap = response.getHighlighting();
Map<String, List<String>> highlightedFieldMap = hitHighlightedMap.get("hm0001");
List<String> highlightedList = highlightedFieldMap.get("category");
String highLightedText = highlightedList.get(0);
```

我们将获得highLightedText作为“Home <em>Appliances</em>”。请注意，搜索关键字Appliances带有<em>标记。Solr 使用的默认高亮标记是<em>，但我们可以通过设置pre和post标记来更改它：

```java
query.setHighlightSimplePre("<strong>");
query.setHighlightSimplePost("</strong>");
```

## 7. 搜索建议

建议是 Solr 支持的重要功能之一。如果查询中的关键字包含拼写错误，或者如果我们想建议自动完成搜索关键字，我们可以使用建议功能。

### 7.1. 拼写检查

标准搜索处理程序不包括拼写检查组件；它必须手动配置。有三种方法可以做到这一点。你可以在官方[wiki 页面](https://cwiki.apache.org/confluence/display/solr/Spell+Checking)中找到配置详细信息。在我们的示例中，我们将使用IndexBasedSpellChecker，它使用索引数据进行关键字拼写检查。

让我们搜索一个拼写错误的关键字：

```java
query.setQuery("hme");
query.set("spellcheck", "on");
QueryResponse response = solrClient.query(query);

SpellCheckResponse spellCheckResponse = response.getSpellCheckResponse();
Suggestion suggestion = spellCheckResponse.getSuggestions().get(0);
List<String> alternatives = suggestion.getAlternatives();
String alternative = alternatives.get(0);
```

我们的关键字“hme”的预期替代项应该是“home”，因为我们的索引包含术语“home”。请注意，必须在执行搜索之前激活拼写检查。

### 7.2. 自动提示条款

我们可能希望获得不完整关键字的建议，以协助搜索。Solr 的建议组件必须手动配置。你可以在其官方[wiki 页面](https://cwiki.apache.org/confluence/display/solr/Suggester)中找到配置详细信息。

我们配置了一个名为/suggest的请求处理程序来处理建议。让我们得到关键字“Hom”的建议：

```java
SolrQuery query = new SolrQuery();
query.setRequestHandler("/suggest");
query.set("suggest", "true");
query.set("suggest.build", "true");
query.set("suggest.dictionary", "mySuggester");
query.set("suggest.q", "Hom");
QueryResponse response = solrClient.query(query);
        
SuggesterResponse suggesterResponse = response.getSuggesterResponse();
Map<String,List<String>> suggestedTerms = suggesterResponse.getSuggestedTerms();
List<String> suggestions = suggestedTerms.get("mySuggester");
```

列表建议应包含所有单词和短语。请注意，我们在配置中配置了一个名为mySuggester的建议器。

## 八. 总结

本文是对 Solr 的搜索引擎功能和特性的快速介绍。

我们触及了许多功能，但这些当然只是触及了我们可以使用高级和成熟的搜索服务器(如 Solr)可以做的事情的皮毛。