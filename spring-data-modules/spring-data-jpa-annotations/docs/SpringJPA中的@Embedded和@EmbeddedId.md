## 1. 概述

在本教程中，我们将介绍如何使用@EmbeddedId注解和“findBy”方法来查询基于复合主键的JPA实体。

因此，**我们将使用@EmbeddeId和@Embeddable注解来表示JPA实体中的复合主键**。我们还需要使用Spring JpaRepository来实现我们的目标。

我们将专注于通过部分主键查询对象。

## 2. 需要@Embeddable和@EmbeddedId

在软件中，当我们需要有一个复合主键来定义表中的记录时，我们会遇到许多用例。**复合主键是使用多个列来唯一标识表中一行的键**。

我们通过在类上使用@Embeddable注解来表示Spring Data中的复合主键。然后通过在@Embeddable类型的字段上使用@EmbeddedId标注，将该键作为复合主键嵌入到表的相应实体类中。

## 3. 示例

考虑一个book表，其中book记录有一个由author和name组成的复合主键。有时，我们可能希望通过复合主键的一部分来查找书籍。例如，用户可能只想搜索特定作者的书籍。我们将学习如何使用JPA执行此操作。

我们的应用程序将包含一个@Embeddable BookId和带有@EmbeddedId BookId属性的@Entity Book。

### 3.1 @Embeddable

让我们在本节中定义BookId类。作者和名称将指定一个唯一的BookId-该类是可序列化的，并实现了equals和hashCode方法：

```java
@Embeddable
public class BookId implements Serializable {

    private String author;
    private String name;

    // standard getters and setters
}
```

### 3.2 @Entity和@EmbeddedId

我们的Book实体有@EmbeddedId BookId和其他与Book相关的字段。**@EmbeddedId BookId告诉JPA这是Book实体的一个复合主键**：

```java
@Entity
public class Book {

    @EmbeddedId
    private BookId id;
    private String genre;
    private Integer price;

    // standard getters and setters
}
```

### 3.3 JPA Repository和命名方法

让我们通过使用实体Book和BookId扩展JpaRepository来快速定义我们的JPA Repository接口。**注意接口的泛型指定，在这里主键的类型为BookId**：

```java
@Repository
public interface BookRepository extends JpaRepository<Book, BookId> {

    List<Book> findByIdName(String name);

    List<Book> findByIdAuthor(String author);
}
```

我们使用id变量的一部分字段名来派生我们的Spring Data查询方法。因此，JPA将部分主键查询解释为：

```java
findByIdName -> directive "findBy" field "id.name"
findByIdAuthor -> directive "findBy" field "id.author"
```

## 4. 总结

JPA可用于有效地映射复合主键并通过派生查询来查询它们。

在本文中，我们看到了一个运行部分id字段搜索的小示例。我们查看了@Embeddable注解来表示复合主键和@EmbeddedId注解以在实体中插入复合键。

最后，我们看到了如何**使用JpaRepository findBy派生方法**来搜索部分id字段。