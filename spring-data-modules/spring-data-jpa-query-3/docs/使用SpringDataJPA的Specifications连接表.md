## 1. 概述

在这个简短的教程中，我们将讨论[Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa) Specification的一个高级功能，它允许我们在创建查询时连接表。

让首先我们简要回顾一下JPA Specifications及其用法。

## 2. JPA Specification

**Spring Data JPA引入了Specification接口，允许我们使用可重用组件创建动态查询**。

对于本文中的代码示例，我们将使用Author和Book类：

```java
@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Book> books;

    // getters and setters
}
```

为了为Author实体创建动态查询，我们可以使用Specification接口的实现：

```java
public class AuthorSpecifications {

    public static Specification<Author> hasFirstNameLike(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("firstName"), "%" + name + "%");
    }

    public static Specification<Author> hasLastName(String name) {
        return (root, query, cb) -> cb.equal(root.<String>get("lastName"), name);
    }
}
```

最后，我们需要让AuthorRepository扩展JpaSpecificationExecutor：

```java
@Repository
public interface AuthorsRepository extends JpaRepository<Author, Long>, JpaSpecificationExecutor<Author> {
}
```

因此，我们现在可以将这两个Specification链接在一起并使用它们创建查询：

```java
@Test
void whenFindByLastNameAndFirstNameLike_thenOneAuthorIsReturned() {
    Specification<Author> specification = hasLastName("Martin").and(hasFirstNameLike("Robert"));

    List<Author> authors = repository.findAll(specification);

    assertThat(authors).hasSize(1);
}
```

## 3. 使用JPA Specification连接表

我们可以从我们的数据模型中观察到Author实体与Book实体共享一对多关系：

```java
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    // getters and setters
}
```

[Criteria Query API](https://www.baeldung.com/spring-data-criteria-queries)允许我们在创建Specification时连接两个表。因此，我们将能够在我们的查询中包含来自Book实体的字段：

```java
public static Specification<Author> hasBookWithTitle(String bookTitle) {
    return (root, query, criteriaBuilder) -> {
        Join<Book, Author> authorsBook = root.join("books");
        return criteriaBuilder.equal(authorsBook.get("title"), bookTitle);
    };
}
```

现在让我们将这个新Specification与之前创建的Specification组合起来：

```java
@Test
void whenSearchingByBookTitleAndAuthorName_thenOneAuthorIsReturned() {
    Specification<Author> specification = hasLastName("Martin").and(hasBookWithTitle("Clean Code"));

    List<Author> authors = repository.findAll(specification);

    assertThat(authors).hasSize(1);
}
```

最后，以下是生成的SQL语句，注意观察其中的JOIN子句：

```sql
select author0_.id         as id1_1_,
       author0_.first_name as first_na2_1_,
       author0_.last_name  as last_nam3_1_
from author author0_
         inner join author_books books1_ on author0_.id = books1_.author_id
         inner join book book2_ on books1_.books_id = book2_.id
where author0_.last_name = ?
  and book2_.title = ?
```

## 4. 总结

在本文中，我们学习了如何使用JPA Specifications根据表的关联实体之一查询表。

Spring Data JPA的Specifications为创建查询提供了一种流式、动态和可重用的方式。