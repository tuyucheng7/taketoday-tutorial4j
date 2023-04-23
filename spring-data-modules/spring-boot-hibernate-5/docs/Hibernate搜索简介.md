## 1. 概述

在本文中，我们将讨论 Hibernate Search 的基础知识、如何配置它，并且我们将实现一些简单的查询。

## 2. Hibernate Search 基础知识

每当我们必须实现全文搜索功能时，使用我们已经精通的工具总是一个加号。

如果我们已经在为 ORM 使用 Hibernate 和 JPA，那么我们距离 Hibernate Search 仅一步之遥。

Hibernate Search 集成了 Apache Lucene，这是一个用Java编写的高性能和可扩展的全文搜索引擎库。这结合了 Lucene 的强大功能与 Hibernate 和 JPA 的简单性。

简单地说，我们只需要向我们的域类添加一些额外的注解，该工具就会处理诸如数据库/索引同步之类的事情。

Hibernate Search 还提供了 Elasticsearch 集成；然而，由于它仍处于试验阶段，我们将在这里重点介绍 Lucene。

## 三、配置

### 3.1. Maven 依赖项

在开始之前，我们首先需要将必要的[依赖项](https://search.maven.org/classic/#search|gav|1|g%3A"org.hibernate" AND a%3A"hibernate-search-orm")添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-search-orm</artifactId>
    <version>5.8.2.Final</version>
</dependency>
```

为了简单起见，我们将使用[H2](https://search.maven.org/classic/#search|ga|1|g%3A"com.h2database" AND a%3A"h2")作为我们的数据库：

```xml
<dependency>
    <groupId>com.h2database</groupId> 
    <artifactId>h2</artifactId>
    <version>1.4.196</version>
</dependency>
```

### 3.2. 配置

我们还必须指定 Lucene 应该在哪里存储索引。

这可以通过属性hibernate.search.default.directory_provider来完成。

我们将选择filesystem，这是我们用例最直接的选项。[官方文档](https://docs.jboss.org/hibernate/stable/search/reference/en-US/html_single/#search-configuration-directory)中列出了更多选项。Filesystem-master / filesystem-slave和infinispan对于集群应用程序来说是值得注意的，其中索引必须在节点之间同步。

我们还必须定义一个默认的基本目录，用于存储索引：

```plaintext
hibernate.search.default.directory_provider = filesystem
hibernate.search.default.indexBase = /data/index/default
```

## 4.模型类

配置完成后，我们现在可以指定我们的模型了。

在 JPA 注解@Entity和@Table之上，我们必须添加一个@Indexed注解。它告诉 Hibernate Search 实体Product应该被索引。

之后，我们必须通过添加@Field注解将所需的属性定义为可搜索的：

```java
@Entity
@Indexed
@Table(name = "product")
public class Product {

    @Id
    private int id;

    @Field(termVector = TermVector.YES)
    private String productName;

    @Field(termVector = TermVector.YES)
    private String description;

    @Field
    private int memory;

    // getters, setters, and constructors
}
```

稍后的“More Like This”查询将需要termVector = TermVector.YES属性。

## 5. 构建 Lucene 索引

在开始实际查询之前，我们必须先触发 Lucene 来构建索引：

```java
FullTextEntityManager fullTextEntityManager 
  = Search.getFullTextEntityManager(entityManager);
fullTextEntityManager.createIndexer().startAndWait();
```

在这个初始构建之后，Hibernate Search 将负责保持索引是最新的。IE。我们可以像往常一样通过EntityManager创建、操作和删除实体。

注意：我们必须确保实体在被 Lucene 发现和索引之前完全提交给数据库(顺便说一句，这也是我们[示例代码测试用例](https://github.com/eugenp/tutorials/tree/master/persistence-modules/spring-hibernate-5)中初始测试数据导入在专用 JUnit 中的原因测试用例，用@Commit注解)。

## 6. 构建和执行查询

现在，我们已准备好创建我们的第一个查询。

在下一节中，我们将展示准备和执行查询的一般工作流程。

之后，我们将为最重要的查询类型创建一些示例查询。

### 6.1. 创建和执行查询的一般工作流程

准备和执行查询一般包括四个步骤：

在第 1 步中，我们必须获得一个 JPA FullTextEntityManager并从中获得一个QueryBuilder：

```java
FullTextEntityManager fullTextEntityManager 
  = Search.getFullTextEntityManager(entityManager);

QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory() 
  .buildQueryBuilder()
  .forEntity(Product.class)
  .get();
```

在第 2 步中，我们将通过 Hibernate 查询 DSL 创建一个 Lucene 查询：

```java
org.apache.lucene.search.Query query = queryBuilder
  .keyword()
  .onField("productName")
  .matching("iphone")
  .createQuery();
```

在第 3 步中，我们将 Lucene 查询包装到 Hibernate 查询中：

```java
org.hibernate.search.jpa.FullTextQuery jpaQuery
  = fullTextEntityManager.createFullTextQuery(query, Product.class);
```

最后，在第 4 步中，我们将执行查询：

```java
List<Product> results = jpaQuery.getResultList();
```

注意：默认情况下，Lucene 按相关性对结果进行排序。

对于所有查询类型，步骤 1、3 和 4 都是相同的。

下面，我们将重点介绍步骤 2，即如何创建不同类型的查询。

### 6.2. 关键词查询

最基本的用例是搜索特定单词。

这是我们在上一节中实际做的：

```java
Query keywordQuery = queryBuilder
  .keyword()
  .onField("productName")
  .matching("iphone")
  .createQuery();
```

在这里，keyword()指定我们正在寻找一个特定的单词，onField()告诉 Lucene 去哪里找，matching()告诉 Lucene要找什么。

### 6.3. 模糊查询

模糊查询的工作方式类似于关键字查询，只是我们可以定义一个“模糊度”的限制，超过该限制，Lucene 将接受两个术语作为匹配项。

通过withEditDistanceUpTo()，我们可以定义一个术语与另一个术语的偏差程度。它可以设置为 0、1 和 2，其中默认值为 2(注意：此限制来自 Lucene 的实现)。

通过withPrefixLength()，我们可以定义前缀的长度，该长度将被模糊性忽略：

```java
Query fuzzyQuery = queryBuilder
  .keyword()
  .fuzzy()
  .withEditDistanceUpTo(2)
  .withPrefixLength(0)
  .onField("productName")
  .matching("iPhaen")
  .createQuery();
```

### 6.4. 通配符查询

Hibernate Search 还使我们能够执行通配符查询，即单词的一部分未知的查询。

为此，我们可以使用“ ？” 对于单个字符，“ ”对于任何字符序列：

```java
Query wildcardQuery = queryBuilder
  .keyword()
  .wildcard()
  .onField("productName")
  .matching("Z")
  .createQuery();
```

### 6.5. 短语查询

如果我们要搜索多个词，我们可以使用短语查询。如有必要，我们可以使用phrase()和withSlop()寻找精确或近似的句子。slop 因子定义了句子中允许的其他单词的数量：

```java
Query phraseQuery = queryBuilder
  .phrase()
  .withSlop(1)
  .onField("description")
  .sentence("with wireless charging")
  .createQuery();
```

### 6.6. 简单查询字符串查询

对于之前的查询类型，我们必须明确指定查询类型。

如果我们想给用户更多的权力，我们可以使用简单的查询字符串查询：通过它，他可以在运行时定义自己的查询。

支持以下查询类型：

-   布尔值(并使用“+”，或使用“|”，不使用“-”)
-   前缀(前缀)
-   短语(“某个短语”)
-   优先级(使用括号)
-   模糊(模糊~2)
-   用于短语查询的 near 运算符(“一些短语”~3)

以下示例将组合模糊查询、短语查询和布尔查询：

```java
Query simpleQueryStringQuery = queryBuilder
  .simpleQueryString()
  .onFields("productName", "description")
  .matching("Aple~2 + "iPhone X" + (256 | 128)")
  .createQuery();
```

### 6.7. 范围查询

范围查询搜索 给定边界之间的值。这可以应用于数字、日期、时间戳和字符串：

```java
Query rangeQuery = queryBuilder
  .range()
  .onField("memory")
  .from(64).to(256)
  .createQuery();
```

### 6.8. 更多类似这样的查询

我们的最后一个查询类型是“更像这样”——查询。为此，我们提供一个实体，Hibernate Search 返回一个包含相似实体的列表，每个实体都有一个相似度分数。

如前所述，我们的模型类中的termVector = TermVector.YES属性对于这种情况是必需的：它告诉 Lucene 在索引期间存储每个术语的频率。

基于此，将在查询执行时计算相似度：

```java
Query moreLikeThisQuery = queryBuilder
  .moreLikeThis()
  .comparingField("productName").boostedTo(10f)
  .andField("description").boostedTo(1f)
  .toEntity(entity)
  .createQuery();
List<Object[]> results = (List<Object[]>) fullTextEntityManager
  .createFullTextQuery(moreLikeThisQuery, Product.class)
  .setProjection(ProjectionConstants.THIS, ProjectionConstants.SCORE)
  .getResultList();
```

### 6.9. 搜索多个字段

到目前为止，我们只使用onField()创建了用于搜索一个属性的查询。

根据用例，我们还可以搜索两个或多个属性：

```java
Query luceneQuery = queryBuilder
  .keyword()
  .onFields("productName", "description")
  .matching(text)
  .createQuery();
```

此外，我们可以指定要单独搜索的每个属性，例如，如果我们想为一个属性定义提升：

```java
Query moreLikeThisQuery = queryBuilder
  .moreLikeThis()
  .comparingField("productName").boostedTo(10f)
  .andField("description").boostedTo(1f)
  .toEntity(entity)
  .createQuery();
```

### 6.10. 组合查询

最后，Hibernate Search 还支持使用各种策略组合查询：

-   SHOULD：查询应该包含子查询的匹配元素
-   MUST：查询必须包含子查询的匹配元素
-   MUST NOT：查询不能包含子查询的匹配元素

聚合类似于布尔值AND、OR和NOT。但是，名称不同是为了强调它们对相关性也有影响。

例如，两个查询之间的SHOULD类似于布尔值OR：如果两个查询之一匹配，则返回该匹配。

但是，如果两个查询都匹配，则与只有一个查询匹配相比，该匹配将具有更高的相关性：

```java
Query combinedQuery = queryBuilder
  .bool()
  .must(queryBuilder.keyword()
    .onField("productName").matching("apple")
    .createQuery())
  .must(queryBuilder.range()
    .onField("memory").from(64).to(256)
    .createQuery())
  .should(queryBuilder.phrase()
    .onField("description").sentence("face id")
    .createQuery())
  .must(queryBuilder.keyword()
    .onField("productName").matching("samsung")
    .createQuery())
  .not()
  .createQuery();
```

## 七. 总结

在本文中，我们讨论了 Hibernate Search 的基础知识并展示了如何实现最重要的查询类型。更多高级主题可以在[官方文档](https://docs.jboss.org/hibernate/stable/search/reference/en-US/html_single/)中找到。