## 1. 概述

在本教程中，我们将探讨如何将Java Record与JPA结合使用。我们将首先探讨为什么不能在实体中使用记录。

然后，我们将了解如何将记录与JPA一起使用。我们还将了解如何在Spring Boot应用程序中将记录与[Spring Data JPA一起使用](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)。

## 2. 记录与实体

[记录](https://www.baeldung.com/java-record-keyword)是不可变的，用于存储数据。**它们包含字段、全参数构造函数、getter、toString和equals/hashCode方法**。由于它们是不可变的，因此它们没有setter方法。由于其简洁的语法，它们通常用作Java应用程序中的数据传输对象(DTO)。

实体是映射到数据库表的类。它们用于表示数据库中的条目。它们的字段映射到数据库表中的列。

### 2.1 记录不能是实体

实体由JPA提供程序处理。**JPA提供程序负责创建数据库表，将实体映射到表，并将实体持久保存到数据库**。在Hibernate等流行的JPA提供程序中，实体是使用代理创建和管理的。

代理是在运行时生成并扩展实体类的类。**这些代理依赖于实体类具有无参数构造函数和setters。由于记录没有这些，因此它们不能用作实体**。

### 2.2 在JPA中使用记录的其他方式

由于在Java应用程序中使用记录的简便性和安全性，因此以其他方式将它们与JPA一起使用可能是有益的。

在JPA中，我们可以通过以下方式使用记录：

-   将查询结果转换为记录
-   使用记录作为DTO在层之间传输数据
-   将实体转换为记录

## 3. 项目设置

我们将使用Spring Boot创建一个使用JPA和Spring Data JPA的简单应用程序。然后我们将看看在与数据库交互时使用记录的几种方法。

### 3.1 Maven依赖

让我们首先将[Spring Data JPA](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa)依赖项添加到我们的项目中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>3.0.4</version>
</dependency>
```

除了Spring Data JPA，我们还需要配置一个数据库。我们可以使用任何SQL数据库。例如，我们可以使用[内存中的H2数据库](https://www.baeldung.com/spring-boot-h2-database)。

### 3.2 实体和记录

让我们创建一个用于与数据库交互的实体。我们将创建一个Book实体，它将映射到数据库中的book表：

```java
@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private String isbn;

    // constructors, getters, setters
}
```

我们还创建一个对应于Book实体的记录：

```java
public record BookRecord(Long id, String title, String author, String isbn) {
}
```

接下来，我们将介绍几种在应用程序中使用记录而不是实体的方法。

## 4. 在JPA中使用记录

JPA API提供了几种与可以使用记录的数据库进行交互的方法。

### 4.1 CriteriaBuilder

让我们首先看看如何通过CriteriaBuilder使用记录。我们将进行一个查询，返回数据库中的所有书籍：

```java
public class QueryService {
    @PersistenceContext
    private EntityManager entityManager;

    public List<BookRecord> findAllBooks() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BookRecord> query = cb.createQuery(BookRecord.class);
        Root<Book> root = query.from(Book.class);
        query.select(cb.construct(BookRecord.class, root.get("id"), root.get("title"), root.get("author"), root.get("isbn")));
        return entityManager.createQuery(query).getResultList();
    }
}
```

在上面的代码中，我们使用CriteriaBuilder创建一个返回BookRecord的CriteriaQuery。

让我们看一下上面代码中的一些步骤：

-   我们使用CriteriaBuilder.createQuery()方法创建一个CriteriaQuery。**我们将要返回的记录的类作为参数传递**
-   然后我们使用CriteriaQuery.from()方法创建一个Root。我们将实体类作为参数传递。这就是我们**指定要查询的表的方式**
-   然后，我们使用CriteriaQuery.select()方法来指定一个select子句。**我们使用CriteriaBuilder.construct()方法将查询结果转换为记录。我们将记录的Class和我们要传递给记录构造函数的实体的字段作为参数**
-   最后，我们使用EntityManager.createQuery()方法从CriteriaQuery创建一个TypedQuery。然后我们使用TypedQuery.getResultList()方法来获取查询的结果

这将创建一个选select查询来获取数据库中的所有书籍。然后它将使用construct()方法将每个结果转换为BookRecord，并在我们调用getResultList()方法时返回记录列表而不是实体列表。

这样，我们可以使用实体类来创建查询，而对应用程序的其余部分使用记录。

### 4.2 TypedQuery

与CriteriaBuilder类似，我们可以使用类型化查询来返回记录而不是实体。让我们在QueryService中添加一个方法，以使用类型化查询获取单本书作为记录：

```java
public BookRecord findBookById(Long id) {
    TypedQuery<BookRecord> query = entityManager
        .createQuery("SELECT new cn.tuyucheng.taketoday.jpa.records.BookRecord(b.id, b.title, b.author, b.isbn) FROM Book b WHERE b.id = :id", BookRecord.class);
    query.setParameter("id", id);
    return query.getSingleResult();
}
```

**TypedQuery允许我们将查询结果转换为任何类型，只要该类型具有采用与查询结果相同数量的参数的构造函数即可**。

在上面的代码中，我们使用EntityManager.createQuery()方法创建一个TypedQuery。我们将查询字符串和记录的Class作为参数传递。然后，我们使用TypedQuery.setParameter()方法来设置查询的参数。最后，我们使用TypedQuery.getSingleResult()方法获取查询结果，这将是一个BookRecord对象。

### 4.3 原生查询

我们还可以使用原生查询来获取查询结果作为记录。但是，**原生查询不允许我们将结果转换为任何类型。相反，我们需要使用映射将结果转换为记录**。首先，让我们在实体中定义一个映射：

```java
@SqlResultSetMapping(
      name = "BookRecordMapping",
      classes = @ConstructorResult(
            targetClass = BookRecord.class,
            columns = {
                  @ColumnResult(name = "id", type = Long.class),
                  @ColumnResult(name = "title", type = String.class),
                  @ColumnResult(name = "author", type = String.class),
                  @ColumnResult(name = "isbn", type = String.class)
            }
      )
)
@Entity
@Table(name = "book")
public class Book {
    // ...
}
```

映射将按以下方式工作：

-   @SqlResultSetMapping注解的name属性指定映射的名称
-   @ConstructorResult注解指定我们要使用记录的构造函数来转换结果
-   @ConstructorResult注解的targetClass属性指定记录的类
-   @ColumnResult注解指定列名和列的类型。这些列值将传递给记录的构造函数

然后，我们可以在原生查询中使用此映射来将结果作为记录获取：

```java
public List<BookRecord> findAllBooksUsingMapping() {
    Query query = entityManager.createNativeQuery("SELECT * FROM book", "BookRecordMapping");
    return query.getResultList();
}
```

这将创建一个返回数据库中所有书籍的原生查询。当我们调用getResultList()方法时，它将使用映射将结果转换为BookRecord并返回记录列表而不是实体列表。

## 5. 将记录与Spring Data JPA结合使用

Spring Data JPA对JPA API进行了一些改进，它使我们能够以几种方式将记录与Spring Data JPA Repository一起使用。

### 5.1 从实体到记录的自动映射

Spring Data Repository允许我们使用记录作为Repository中方法的返回类型，这将自动将实体映射到记录。这只有在记录具有与实体完全相同的字段时才有可能。让我们看一个例子：

```java
public interface BookRepository extends JpaRepository<Book, Long> {
    List<BookRecord> findBookByAuthor(String author);
}
```

由于BookRecord与Book实体具有相同的字段，因此当我们调用findBookByAuthor()方法时，Spring Data JPA会自动将实体映射到记录并返回记录列表而不是实体列表。

### 5.2 将记录与@Query一起使用

与TypedQuery类似，我们可以在Spring Data JPA Repository中使用带有@Query注解的记录。让我们看一个例子：

```java
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT new cn.tuyucheng.taketoday.jpa.records.BookRecord(b.id, b.title, b.author, b.isbn) FROM Book b WHERE b.id = :id")
    BookRecord findBookById(@Param("id") Long id);
}
```

当我们调用findBookById()方法时，Spring Data JPA会自动将查询结果转换为BookRecord并返回单个记录而不是实体。

### 5.3 自定义Repository实现

如果自动映射不是一种选择，我们还可以定义一个自定义Repository实现，允许我们定义自己的映射。让我们首先创建一个CustomBookRecord类，该类将用作Repository中方法的返回类型：

```java
public record CustomBookRecord(Long id, String title) {
}
```

请注意，CustomBookRecord类没有与Book实体相同的字段，它只有id和title字段。

然后，我们可以创建一个将使用CustomBookRecord类的自定义Repository实现：

```java
public interface CustomBookRepository {
    List<CustomBookRecord> findAllBooks();
}
```

在Repository的实现中，我们可以定义用于将查询结果映射到CustomBookRecord类的方法：

```java
@Repository
public class CustomBookRepositoryImpl implements CustomBookRepository {
    private final JdbcTemplate jdbcTemplate;

    public CustomBookRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CustomBookRecord> findAllBooks() {
        return jdbcTemplate.query("SELECT id, title FROM book", (rs, rowNum) -> new CustomBookRecord(rs.getLong("id"), rs.getString("title")));
    }
}
```

在上面的代码中，我们使用JdbcTemplate.query()方法执行查询，并使用Lambda表达式将结果映射到CustomBookRecord中，该表达式是RowMapper接口的实现。

## 6. 总结

在本文中，我们研究了如何将记录与JPA和Spring Data JPA一起使用。我们介绍了如何使用CriteriaBuilder、TypedQuery和原生查询将记录与JPA API一起使用。我们还了解了如何使用自动映射、自定义查询和自定义Repository实现将记录与Spring Data JPA Repository一起使用。