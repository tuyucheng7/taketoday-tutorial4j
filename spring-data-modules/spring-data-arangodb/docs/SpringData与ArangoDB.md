## 1. 概述

在本教程中，我们将学习如何使用[Spring Data](https://www.baeldung.com/spring-data)模块和[ArangoDB](https://www.arangodb.com/)数据库。ArangoDB 是一个免费开源的多模型数据库系统。它支持键值、文档和图形数据模型，具有一个数据库核心和统一的查询语言：[AQL(ArangoDB 查询语言)。](https://www.arangodb.com/docs/stable/aql/)

我们将介绍所需的配置、基本的 CRUD 操作、自定义查询和实体关系。

## 2.ArangoDB 设置

要安装 ArangoDB，我们首先需要从ArangoDB 官方网站的[下载页面下载软件包。](https://www.arangodb.com/download/)

出于本教程的目的，我们将安装 ArangoDB 的社区版。可以在[此处](https://www.arangodb.com/docs/stable/installation.html)找到详细的安装步骤。

默认安装包含一个名为 的数据库`_system`和一个 `root`可以访问所有数据库的用户。

根据软件包的不同，安装程序会在安装过程中要求输入 root 密码或设置一个随机密码。

使用默认配置，我们将看到 ArangoDB 服务器在`8529`端口上运行。

设置完成后，我们可以使用可在 上访问的 Web 界面与服务器进行交互`http://localhost:8529`。我们将在本教程后面的部分使用此主机和端口进行 Spring Data 配置。

我们也可以选择使用`arangosh`同步 shell 与服务器进行交互。

让我们开始`arangosh`创建一个名为的新数据库`baeldung-database`和一个`baeldung` 有权访问这个新创建的数据库的用户。

```shell
arangosh> db._createDatabase("baeldung-database", {}, [{ username: "baeldung", passwd: "password", active: true}]);
```

## 3.依赖关系

要在我们的应用程序中使用 Spring Data 和 ArangoDB，我们需要[以下依赖](https://search.maven.org/search?q=a:arangodb-spring-data)项：

```xml
<dependency>
    <groupId>com.arangodb</groupId>
    <artifactId>arangodb-spring-data</artifactId>
    <version>3.5.0</version>
</dependency>
```

## 4.配置

在我们开始处理数据之前，我们需要建立到ArangoDB的连接。我们应该通过创建一个实现ArangoConfiguration接口的配置类来做到这一点：

```java
@Configuration
public class ArangoDbConfiguration implements ArangoConfiguration {}
```

在里面我们需要实现两个方法。第一个应该创建ArangoDB.Builder对象，它将生成一个到我们数据库的接口：

```java
@Override
public ArangoDB.Builder arango() {
    return new ArangoDB.Builder()
      .host("127.0.0.1", 8529)
      .user("baeldung").password("password"); }
```

创建连接需要四个参数：主机、端口、用户名和密码。

或者，我们可以跳过在配置类中设置这些参数：

```java
@Override
public ArangoDB.Builder arango() {
    return new ArangoDB.Builder();
}
```

因为我们可以将它们存储在arango.properties资源文件中：

```plaintext
arangodb.host=127.0.0.1
arangodb.port=8529
arangodb.user=baeldung
arangodb.password=password
```

这是 Arango 寻找的默认位置。可以通过将InputStream传递给自定义属性文件来覆盖它：

```java
InputStream in = MyClass.class.getResourceAsStream("my.properties");
ArangoDB.Builder arango = new ArangoDB.Builder()
  .loadProperties(in);
```

我们必须实现的第二种方法是简单地提供我们在应用程序中需要的数据库名称：

```java
@Override
public String database() {
    return "baeldung-database";
}
```

此外，配置类需要 @EnableArangoRepositories 注解，告诉 Spring Data 在哪里寻找 ArangoDB 存储库：

```java
@EnableArangoRepositories(basePackages = {"com.baeldung"})
```

## 5. 数据模型

下一步，我们将创建一个数据模型。对于这篇文章，我们将使用带有name、author和publishDate字段的文章表示：

```java
@Document("articles")
public class Article {

    @Id
    private String id;

    @ArangoId
    private String arangoId;

    private String name;
    private String author;
    private ZonedDateTime publishDate;

    // constructors
}
```

ArangoDB实体必须具有 将集合名称作为参数的@Document注解。默认情况下，它是一个 decapitalize 类名。

接下来，我们有两个 id 字段。一个带有 Spring 的 @Id 注解，第二个带有 Arango 的 @ArangoId注解。第一个存储生成的实体 ID。第二个将相同的 id 螺母存储在数据库中的适当位置。在我们的例子中，这些值相应地可以是1 和 articles/1。

现在，当我们定义了实体后，我们可以创建一个用于数据访问的存储库接口：

```java
@Repository
public interface ArticleRepository extends ArangoRepository<Article, String> {}
```

它应该使用两个通用参数扩展ArangoRepository接口。在我们的例子中，它是一个 id 类型为String的Article类。

## 6.增删改查操作

最后，我们可以创建一些具体的数据。

作为起点，我们需要对文章存储库的依赖：

```java
@Autowired
ArticleRepository articleRepository;
```

以及Article类的一个简单实例：

```java
Article newArticle = new Article(
  "ArangoDb with Spring Data",
  "Baeldung Writer",
  ZonedDateTime.now()
);
```

现在，如果我们想将这篇文章存储在我们的数据库中，我们应该简单地调用 save 方法：

```java
Article savedArticle = articleRepository.save(newArticle);
```

之后，我们可以确保生成了 id 和 arangoId 字段：

```java
assertNotNull(savedArticle.getId());
assertNotNull(savedArticle.getArangoId());
```

要从数据库中获取文章，我们需要先获取它的 ID：

```java
String articleId = savedArticle.getId();
```

然后简单地调用findById方法：

```java
Optional<Article> articleOpt = articleRepository.findById(articleId);
assertTrue(articleOpt.isPresent());
```

有了文章实体，我们可以改变它的属性：

```java
Article article = articleOpt.get();
article.setName("New Article Name");
articleRepository.save(article);
```

最后，再次调用save方法来更新数据库条目。它不会创建新条目，因为 id 已分配给实体。

删除条目也是一个简单的操作。我们只需调用存储库的删除方法：

```java
articleRepository.delete(article)
```

也可以通过 id 删除它：

```java
articleRepository.deleteById(articleId)
```

## 7.自定义查询

使用 Spring Data和ArangoDB，我们可以使用[派生存储库](https://www.baeldung.com/spring-data-derived-queries)并通过方法名称简单地定义查询：

```java
@Repository
public interface ArticleRepository extends ArangoRepository<Article, String> {
    Iterable<Article> findByAuthor(String author);
}
```

第二种选择是使用[AQL(ArangoDb 查询语言)](https://www.arangodb.com/docs/stable/aql/)。这是一种自定义语法语言，我们可以使用[@Query注解](https://www.baeldung.com/spring-data-jpa-query)来应用它。

现在，让我们看一下基本的 AQL 查询，该查询将查找具有给定作者的所有文章并按发布日期对它们进行排序：

```java
@Query("FOR a IN articles FILTER a.author == @author SORT a.publishDate ASC RETURN a")
Iterable<Article> getByAuthor(@Param("author") String author);
```

## 8.关系

ArangoDB提供了在实体之间创建关系的可能性。

例如，让我们在Author类和它的文章之间创建一个关系。

为此，我们需要使用@Relations注解定义一个新的集合属性，该属性将包含指向给定作者撰写的每篇文章的链接：

```java
@Relations(edges = ArticleLink.class, lazy = true)
private Collection<Article> articles;
```

正如我们所见，ArangoDB 中的关系是通过一个用@Edge 注解的单独类定义的：

```java
@Edge
public class ArticleLink {

    @From
    private Article article;

    @To
    private Author author;

    // constructor, getters and setters
}
```

它带有两个用 @From 和@To注解的字段。它们定义传入和传出关系。

## 9.总结

在本教程中，我们学习了如何配置 ArangoDB并将其与 Spring Data 一起使用。我们介绍了基本的 CRUD 操作、自定义查询和实体关系。