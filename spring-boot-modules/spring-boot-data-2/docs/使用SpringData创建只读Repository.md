## 1. 概述

在这个简短的教程中，**我们将讨论如何创建一个只读的[Spring Data Repository](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)**。

有时需要从数据库中读取数据，而不必对其进行修改。在这种情况下，创建一个只读的Repository接口是完美的。

它可以提供读取数据的能力，同时不会有任何人更改数据的风险。

## 2. 扩展Repository

我们首先需要包含[spring-boot-starter-data-jpa](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa)依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

此依赖项中包含Spring Data最常用的CrudRepository接口，该接口提供了大多数应用程序所需的所有基本CRUD操作(创建、读取、更新、删除)的方法。但是，它包含了几种修改数据的方法，而我们需要一个只具有读取数据能力的Repository。

CrudRepository实际上扩展了另一个名为Repository的接口。我们还可以扩展此接口以满足我们的需要。

让我们创建一个扩展Repository的新接口：

```java
@NoRepositoryBean
public interface ReadOnlyRepository<T, ID> extends Repository<T, ID> {

    Optional<T> findById(ID id);

    List<T> findAll();
}
```

在这里，我们只定义了两个只读方法。**此Repository访问的实体将不会受到任何修改**。

同样重要的是要注意，我们必须使用@NoRepositoryBean注解告诉Spring我们希望这个Repository保持通用。**这使我们能够为任意数量的不同实体重用只读Repository**。

接下来，我们将了解如何将实体绑定到新的ReadOnlyRepository。

## 3. 扩展ReadOnlyRepository

假设我们有一个想要访问的简单Book实体：

```java
@Entity
public class Book {
    @Id
    @GeneratedValue
    private Long id;
    private String author;
    private String title;

    // getters and setters
}
```

现在我们可以创建一个继承自ReadOnlyRepository的Repository接口：

```java
public interface BookReadOnlyRepository extends ReadOnlyRepository<Book, Long> {
    List<Book> findByAuthor(String author);

    List<Book> findByTitle(String title);
}
```

除了它继承的两个方法之外，我们还添加了两个特定于Book实体的只读方法：findByAuthor()和findByTitle()。总的来说，这个Repository可以访问四种只读方法。

最后，让我们编写一个测试来确保BookReadOnlyRepository的功能正确：

```java
@SpringBootTest(classes = ReadOnlyRepositoryApplication.class)
class ReadOnlyRepositoryUnitTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookReadOnlyRepository bookReadOnlyRepository;

    @Test
    void givenBooks_whenUsingReadOnlyRepository_thenGetThem() {
        Book aChristmasCarolCharlesDickens = new Book();
        aChristmasCarolCharlesDickens.setTitle("A Christmas Carol");
        aChristmasCarolCharlesDickens.setAuthor("Charles Dickens");
        bookRepository.save(aChristmasCarolCharlesDickens);

        Book greatExpectationsCharlesDickens = new Book();
        greatExpectationsCharlesDickens.setTitle("Great Expectations");
        greatExpectationsCharlesDickens.setAuthor("Charles Dickens");
        bookRepository.save(greatExpectationsCharlesDickens);

        Book greatExpectationsKathyAcker = new Book();
        greatExpectationsKathyAcker.setTitle("Great Expectations");
        greatExpectationsKathyAcker.setAuthor("Kathy Acker");
        bookRepository.save(greatExpectationsKathyAcker);

        List<Book> charlesDickensBooks = bookReadOnlyRepository.findByAuthor("Charles Dickens");
        Assertions.assertEquals(2, charlesDickensBooks.size());

        List<Book> greatExpectationsBooks = bookReadOnlyRepository.findByTitle("Great Expectations");
        Assertions.assertEquals(2, greatExpectationsBooks.size());

        List<Book> allBooks = bookReadOnlyRepository.findAll();
        Assertions.assertEquals(3, allBooks.size());

        Long bookId = allBooks.get(0).getId();
        Book book = bookReadOnlyRepository.findById(bookId).orElseThrow(NoSuchElementException::new);
        Assertions.assertNotNull(book);
    }
}
```

为了先保存一些Book实体数据，我们创建了一个BookRepository，它在测试范围内扩展了CrudRepository。此Repository在我们的项目中是不重要的，但对于此测试是必需的。

```java
public interface BookRepository extends CrudRepository<Book, Long> {
}
```

我们能够测试所有四种只读方法，并且现在还可以为其他实体重用ReadOnlyRepository接口。

## 4. 总结

在本文中，我们学习了如何扩展Spring Data的Repository接口以创建可重用的只读Repository。之后，我们将它绑定到一个简单的Book实体并编写了一个测试，证明它的功能符合我们的预期。