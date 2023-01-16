## 1. 概述

在本教程中，我们将了解如何使用[Morphia](https://morphia.dev/)，它是Java中 MongoDB 的对象文档映射器 (ODM)。

在此过程中，我们还将了解什么是 ODM 以及它如何促进使用 MongoDB。

## 2. 什么是ODM？

对于那些不熟悉这个领域的人来说，[MongoDB](https://www.mongodb.com/)是一个面向文档的数据库，它天生就是为分布式而构建的。面向文档的数据库，简单来说就是管理文档，文档就是一种无模式的半结构化数据组织方式。它们属于范围更广、定义松散的 NoSQL 数据库，以明显不同于传统的 SQL 数据库组织而得名。

MongoDB[为几乎所有流行的编程语言(如 Java)提供驱动程序](https://www.baeldung.com/java-mongodb)。这些驱动程序为使用 MongoDB 提供了一个抽象层，这样我们就不会直接使用 Wire Protocol。将此视为 Oracle 为其关系数据库提供 JDBC 驱动程序的实现。

然而，如果我们回忆起直接使用 JDBC 的日子，我们就会意识到它会变得多么混乱——尤其是在面向对象的范例中。幸运的是，我们有像 Hibernate 这样的对象关系映射 (ORM) 框架来拯救我们。对于 MongoDB 来说并没有太大的不同。

虽然我们当然可以使用低级驱动程序，但它需要更多的样板文件才能完成任务。在这里，我们有一个与 ORM 类似的概念，称为 Object Document Mapper (ODM)。Morphia 恰好填补了Java编程语言的空间，并在 MongoDB 的Java驱动程序之上工作。

## 3.设置依赖

我们已经看到足够多的理论让我们进入一些代码。对于我们的例子，我们将建立一个图书馆模型，看看我们如何使用 Morphia 在 MongoDB 中管理它。

但在我们开始之前，我们需要设置一些依赖项。

### 3.1. 数据库

我们需要有一个正在运行的 MongoDB 实例才能使用。有几种方法可以获得它，最简单的是在我们的本地机器上[下载并安装社区版。](https://docs.mongodb.com/v3.2/administration/install-community/)

我们应该保留所有默认配置，包括 MongoDB 运行的端口。

### 3.2. 吗啡

[我们可以从Maven Central](https://search.maven.org/search?q=g:dev.morphia.morphia AND a:core)下载 Morphia 的预构建 JAR，并在我们的Java项目中使用它们。

然而，最简单的方法是使用像 Maven 这样的依赖管理工具：

```xml
<dependency>
    <groupId>dev.morphia.morphia</groupId>
    <artifactId>core</artifactId>
    <version>1.5.3</version>
</dependency>
```

## 4.如何使用Morphia连接？

现在我们已经安装并运行了 MongoDB，并在我们的Java项目中设置了 Morphia，我们准备好使用 Morphia 连接到 MongoDB。

让我们看看如何实现：

```java
Morphia morphia = new Morphia();
morphia.mapPackage("com.baeldung.morphia");
Datastore datastore = morphia.createDatastore(new MongoClient(), "library");
datastore.ensureIndexes();
```

差不多就是这样！让我们更好地理解这一点。我们的映射操作需要两件事：

1.  映射器：负责将我们的JavaPOJO 映射到 MongoDB 集合。在我们上面的代码片段中，Morphia是负责的类。注意我们是如何配置包的，它应该在哪里寻找我们的 POJO。
2.  连接：这是到 MongoDB 数据库的连接，映射器可以在该数据库上执行不同的操作。Datastore类将MongoClient实例(来自JavaMongoDB 驱动程序)和 MongoDB 数据库的名称作为参数，返回一个活动连接以使用.

所以，我们都准备好使用这个数据存储并与我们的实体一起工作。

## 5.如何使用实体？

在我们可以使用我们刚创建的Datastore之前，我们需要定义一些要使用的域实体。

### 5.1. 简单实体

让我们首先定义一个具有一些属性的简单Book实体：

```java
@Entity("Books")
public class Book {
    @Id
    private String isbn;
    private String title;
    private String author;
    @Property("price")
    private double cost;
    // constructors, getters, setters and hashCode, equals, toString implementations
}
```

这里有一些有趣的事情需要注意：

-   注意注解@Entity，它使这个 POJO 符合Morphia的ODM 映射
-   默认情况下，Morphia 通过其类名将实体映射到 MongoDB 中的集合，但我们可以显式覆盖它(就像我们在这里为实体Book所做的那样 )
-   默认情况下，Morphia 通过变量名称将实体中的变量映射到 MongoDB 集合中的键，但我们可以再次覆盖它(就像我们在这里对可变成本所做的那样 )
-   最后，我们需要在实体中标记一个变量作为主键，通过注解@Id(就像我们在这里为我们的书使用 ISBN 一样)

### 5.2. 有关系的实体

然而，在现实世界中，实体并不像看起来那么简单，并且彼此之间有着复杂的关系。例如，我们的简单实体Book可以有一个Publisher并且可以引用其他配套书籍。我们如何为它们建模？

MongoDB 提供了两种建立关系的机制——引用和嵌入。顾名思义，通过引用，MongoDB 将相关数据作为单独的文档存储在相同或不同的集合中，并仅使用其 id 来引用它。

相反，通过嵌入，MongoDB 将关系存储或嵌入父文档本身。

让我们看看如何使用它们。让我们从在我们的Book中嵌入Publisher开始：

```java
@Embedded
private Publisher publisher;
```

很简单。现在让我们继续添加对其他书籍的引用：

```java
@Reference
private List<Book> companionBooks;
```

就是这样——Morphia 为 MongoDB 支持的模型关系提供了方便的注解。然而，引用与嵌入的选择应该从数据模型的复杂性、冗余性和一致性等方面考虑。

该练习类似于关系数据库中的规范化。

现在，我们已准备好使用Datastore对Book执行一些操作。

## 6.一些基本操作

让我们看看如何使用 Morphia 进行一些基本操作。

### 6.1. 节省

让我们从最简单的操作开始，在我们的 MongoDB 数据库库中创建一个Book实例：

```java
Publisher publisher = new Publisher(new ObjectId(), "Awsome Publisher");

Book book = new Book("9781565927186", "Learning Java", "Tom Kirkman", 3.95, publisher);
Book companionBook = new Book("9789332575103", "Java Performance Companion", 
  "Tom Kirkman", 1.95, publisher);

book.addCompanionBooks(companionBook);

datastore.save(companionBook);
datastore.save(book);
```

这足以让 Morphia 在我们的 MongoDB 数据库中创建一个集合，如果它不存在，则执行 upsert 操作。

### 6.2. 询问

让我们看看我们是否能够查询我们刚刚在 MongoDB 中创建的书：

```java
List<Book> books = datastore.createQuery(Book.class)
  .field("title")
  .contains("Learning Java")
  .find()
  .toList();

assertEquals(1, books.size());

assertEquals(book, books.get(0));
```

在 Morphia 中查询文档首先使用Datastore创建查询，然后以声明方式添加过滤器，这让那些喜欢函数式编程的人很高兴！

Morphia 支持使用过滤器和运算符进行更[复杂的查询构造](https://morphia.dev/morphia/2.1/querying.html)。此外，Morphia 允许对查询结果进行限制、跳过和排序。

更重要的是，如果需要，Morphia 允许我们使用 MongoDB 的Java驱动程序编写的原始查询以获得更多控制。

### 6.3. 更新

尽管如果主键匹配，保存操作可以处理更新，但 Morphia 提供了有选择地更新文档的方法：

```java
Query<Book> query = datastore.createQuery(Book.class)
  .field("title")
  .contains("Learning Java");

UpdateOperations<Book> updates = datastore.createUpdateOperations(Book.class)
  .inc("price", 1);

datastore.update(query, updates);

List<Book> books = datastore.createQuery(Book.class)
  .field("title")
  .contains("Learning Java")
  .find()
  .toList();

assertEquals(4.95, books.get(0).getCost());
```

在这里，我们正在构建一个查询和一个更新操作，以将查询返回的所有书籍的价格增加一倍。

### 6.4. 删除

最后，必须删除已创建的！同样，对于 Morphia，它非常直观：

```java
Query<Book> query = datastore.createQuery(Book.class)
  .field("title")
  .contains("Learning Java");

datastore.delete(query);

List<Book> books = datastore.createQuery(Book.class)
  .field("title")
  .contains("Learning Java")
  .find()
  .toList();

assertEquals(0, books.size());
```

我们创建查询与以前非常相似，并在Datastore上运行删除操作。

## 7.高级用法

MongoDB 有一些高级操作，如聚合、索引等。虽然不可能使用 Morphia 执行所有这些操作，但确实有可能实现其中的一部分。遗憾的是，对于其他人，我们将不得不退回到 MongoDB 的Java驱动程序。

让我们关注一些我们可以通过 Morphia 执行的高级操作。

### 7.1. 聚合

MongoDB 中的聚合允许我们在管道中定义一系列操作，这些操作可以对一组文档进行操作并产生聚合输出。

Morphia 有一个 API 来支持这样的聚合管道。

假设我们希望以这样一种方式聚合我们的图书馆数据，即我们将所有书籍按作者分组：

```java
Iterator<Author> iterator = datastore.createAggregation(Book.class)
  .group("author", grouping("books", push("title")))
  .out(Author.class);
```

那么，这是如何工作的呢？我们首先使用相同的旧Datastore创建聚合管道。我们必须提供我们希望对其执行聚合操作的实体，例如，此处为Book。

接下来，我们要按“作者”对文档进行分组，并将它们的“标题”聚合到一个名为“书籍”的键下。最后，我们在这里与 ODM 合作。因此，我们必须定义一个实体来收集我们的聚合数据——在我们的例子中，它是Author。

当然，我们必须定义一个名为Author的实体和一个名为 books 的变量：

```java
@Entity
public class Author {
    @Id
    private String name;
    private List<String> books;
    // other necessary getters and setters
}
```

当然，这只是 MongoDB 提供的一个非常强大的结构的冰山一角，[可以进一步探索细节](https://docs.mongodb.com/manual/core/aggregation-pipeline/)。

### 7.2. 投影

MongoDB 中的投影允许我们在查询中仅选择要从文档中获取的字段。如果文档结构复杂且繁重，当我们只需要几个字段时，这将非常有用。

假设我们只需要在查询中获取带有标题的书籍：

```java
List<Book> books = datastore.createQuery(Book.class)
  .field("title")
  .contains("Learning Java")
  .project("title", true)
  .find()
  .toList();
 
assertEquals("Learning Java", books.get(0).getTitle());
assertNull(books.get(0).getAuthor());
```

在这里，正如我们所见，我们只在结果中取回标题，而不是作者和其他字段。然而，我们在使用预测输出保存回 MongoDB 时应该小心。这可能会导致数据丢失！

### 7.3. 索引

索引在数据库查询优化中扮演着非常重要的角色——关系型和许多非关系型数据库。

MongoDB在集合级别定义索引，默认情况下在主键上创建唯一索引。此外，MongoDB 允许在文档中的任何字段或子字段上创建索引。我们应该根据我们希望创建的查询选择在键上创建索引。

例如，在我们的示例中，我们可能希望在Book的“title”字段上创建一个索引，因为我们经常最终查询它：

```java
@Indexes({
  @Index(
    fields = @Field("title"),
    options = @IndexOptions(name = "book_title")
  )
})
public class Book {
    // ...
    @Property
    private String title;
    // ...
}
```

当然，我们可以传递额外的索引选项来定制所创建索引的细微差别。请注意，该字段应由 @Property 注解以在索引中使用。

此外，除了类级索引外，Morphia 还具有一个注解来定义字段级索引。

### 7.4. 架构验证

我们有一个选项可以为MongoDB 在执行更新或插入操作时可以使用的集合提供数据验证规则。Morphia 通过他们的 API 支持这一点。

假设我们不想插入一本没有有效价格的书。我们可以利用模式验证来实现这一点：

```java
@Validation("{ price : { $gt : 0 } }")
public class Book {
    // ...
    @Property("price")
    private double cost;
    // ...
}
```

MongoDB提供[了一组丰富的验证](https://docs.mongodb.com/manual/core/schema-validation/index.html)，可以在这里使用。

## 8. 替代 MongoDB ODM

Morphia 不是唯一可用的JavaMongoDB ODM。我们可以考虑在我们的应用程序中使用其他几个。此处无法讨论与 Morphia 的比较，但了解我们的选择总是有用的：

-   [Spring Data](https://www.baeldung.com/spring-data-mongodb-guide)：提供了一个基于 Spring 的编程模型来使用 MongoDB
-   [MongoJack](https://mongojack.org/)：提供从 JSON 到 MongoDB 对象的直接映射

这不是用于Java的 MongoDB ODM 的完整列表，但是有一些有趣的替代品可用！

## 9.总结

在本文中，我们了解了 MongoDB 的基本细节以及使用 ODM 从Java等编程语言连接和操作 MongoDB。我们进一步探讨了 Morphia 作为Java的 MongoDB ODM 及其具有的各种功能。