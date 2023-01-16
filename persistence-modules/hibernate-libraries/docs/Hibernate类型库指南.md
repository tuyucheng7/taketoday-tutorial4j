## 1. 概述

在本教程中，我们将了解 Hibernate 类型。这个库为我们提供了一些在核心 Hibernate ORM 中不是原生的类型。

## 2.依赖关系

要启用 Hibernate Types，我们只需添加[适当的hibernate-types依赖项](https://search.maven.org/artifact/com.vladmihalcea/hibernate-types-52)：

```xml
<dependency>
    <groupId>com.vladmihalcea</groupId>
    <artifactId>hibernate-types-52</artifactId>
    <version>2.9.7</version>
</dependency>

```

这将适用于 Hibernate 版本5.4、5.3和5.2。

如果Hibernate版本比较旧，上面的artifactId值会不一样。对于5.1和5.0版本，我们可以使用hibernate-types-51。同样，4.3版需要hibernate-types-43，而4.2和 4.1 版需要hibernate-types-4。

本教程中的示例需要一个数据库。我们使用 Docker 提供了一个数据库容器。因此，我们需要一个[Docker](https://www.docker.com/get-started)的工作副本。

因此，要运行和创建我们的数据库，我们只需要执行：

```bash
$ ./create-database.sh
```

## 3. 支持的数据库

我们可以将我们的类型用于 Oracle、SQL Server、PostgreSQL 和 MySQL 数据库。因此，Java中的类型到数据库列类型的映射会根据我们使用的数据库而有所不同。在我们的例子中，我们将使用 MySQL 并将 JsonBinaryType 映射到JSON 列类型。

[可以在 Hibernate Types 存储库中](https://github.com/vladmihalcea/hibernate-types)找到有关支持的映射的文档。

## 4. 数据模型

本教程的数据模型将允许我们存储有关专辑和歌曲的信息。一张专辑有封面和一首或多首歌曲。一首歌有一个艺术家和长度。封面艺术有两个图像 URL 和一个 UPC 代码。最后，艺术家有名字、国家和音乐流派。

过去，我们会创建表格来表示我们模型中的所有数据。但是，现在我们有了可用的类型，我们可以很容易地将一些数据存储为 JSON。

对于本教程，我们只会为专辑和歌曲创建表格：

```java
public class Album extends BaseEntity {
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private CoverArt coverArt;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Song> songs;

   // other class members
}
public class Song extends BaseEntity {

    private Long length = 0L;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private Artist artist;

    // other class members
}
```

使用JsonStringType，我们将在这些表中将封面艺术和艺术家表示为 JSON 列：

```java
public class Artist implements Serializable {
 
    private String name;
    private String country;
    private String genre;

    // other class members
}
public class CoverArt implements Serializable {

    private String frontCoverArtUrl;
    private String backCoverArtUrl;
    private String upcCode;

    // other class members
}
```

请务必注意，Artist和CoverArt类是 POJO 而不是实体。此外，它们是我们数据库实体类的成员，使用@Type(type = “json”)注解定义。

### 4.1. 存储 JSON 类型

我们定义了专辑和歌曲模型以包含数据库将存储为 JSON 的成员。这是由于使用了提供的json类型。为了让我们可以使用该类型，我们必须使用类型定义来定义它：

```java
@TypeDefs({
  @TypeDef(name = "json", typeClass = JsonStringType.class),
  @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class BaseEntity {
  // class members
}
```

JsonStringType和JsonBinaryType的@Type使类型json和jsonb可用。

最新的 MySQL 版本支持 JSON 作为列类型。因此，JDBC 处理从中读取的任何 JSON 或保存到具有这些类型之一的列的任何对象作为String。这意味着要正确映射到列，我们必须在类型定义中使用JsonStringType。

### 4.2. 休眠

最终，我们的类型将使用 JDBC 和 Hibernate 自动转换为 SQL。所以，现在我们可以创建一些歌曲对象，一个专辑对象并将它们保存到数据库中。随后，Hibernate 生成以下 SQL 语句：

```sql
insert into song (name, artist, length, id) values ('A Happy Song', '{"name":"Superstar","country":"England","genre":"Pop"}', 240, 3);
insert into song (name, artist, length, id) values ('A Sad Song', '{"name":"Superstar","country":"England","genre":"Pop"}', 120, 4);
insert into song (name, artist, length, id) values ('A New Song', '{"name":"Newcomer","country":"Jamaica","genre":"Reggae"}', 300, 6)
insert into album (name, cover_art, id) values ('Album 0', '{"frontCoverArtUrl":"http://fakeurl-0","backCoverArtUrl":"http://fakeurl-1","upcCode":"b2b9b193-ee04-4cdc-be8f-3a276769ab5b"}', 7)

```

正如预期的那样，我们的json类型Java对象全部由 Hibernate 翻译并作为格式良好的 JSON 存储在我们的数据库中。

## 5. 存储通用类型

除了支持基于 JSON 的列外，该库还添加了一些泛型类型：java.time包中的YearMonth、Year和Month。

现在，我们可以映射这些 Hibernate 或 JPA 本身不支持的类型。此外，我们现在能够将它们存储为Integer、String或Date列。

例如，假设我们要将歌曲的录制日期添加到我们的歌曲模型中，并将其作为整数存储在我们的数据库中。我们可以在Song实体类定义中使用YearMonthIntegerType ：

```java
@TypeDef(
  typeClass = YearMonthIntegerType.class,
  defaultForType = YearMonth.class
)
public class Song extends BaseEntity {
    @Column(
      name = "recorded_on",
      columnDefinition = "mediumint"
    )
    private YearMonth recordedOn = YearMonth.now();

    // other class members  
}

```

我们的recordedOn属性值被转换为我们提供的typeClass。因此，预定义的转换器会将值作为Integer保存在我们的数据库中。

## 6. 其他实用类

Hibernate Types 有一些辅助类，可以进一步改善开发人员在使用 Hibernate 时的体验。

CamelCaseToSnakeCaseNamingStrategy将Java类中的 驼峰式属性映射到数据库中的蛇形列。

ClassImportIntegrator允许在 JPA 构造函数参数中使用简单的JavaDTO 类名称值。

还有ListResultTransformer和MapResultTransformer类提供 JPA 使用的结果对象的更清晰的实现。此外，它们支持使用 lambda 并提供与旧 JPA 版本的向后兼容性。

## 七. 总结

在本教程中，我们介绍了 Hibernate TypesJava库以及它添加到 Hibernate 和 JPA 的新类型。我们还研究了该库提供的一些实用程序和通用类型。