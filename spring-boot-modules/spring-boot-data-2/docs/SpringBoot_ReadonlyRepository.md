## 1. 概述

在这个简短的教程中，我们将讨论如何创建一个**只读的Spring Data Repository**。

我们有时只需要从数据库中读取数据，而不必对其进行修改。在这种情况下，创建一个只读的Repository接口是可行的。

它提供了读取数据的能力，同时不会有任何人更改数据的风险。

## 2. 继承Repository

我们首先需要包含spring-boot-starter-data-jpa依赖项：

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

此依赖项中包含Spring Data最常用的CrudRepository接口，
该接口提供了大多数应用程序所需的所有基本CRUD操作(创建、读取、更新、删除)的方法。
但是，它包含了几种修改数据的方法，而我们需要一个只具有读取数据能力的Repository。

CrudRepository实际上继承了另一个名为Repository的接口，我们还可以继承这个接口以满足我们的需要。

让我们创建一个继承Repository的新接口：

```java

@NoRepositoryBean
public interface ReadOnlyRepository<T, ID> extends Repository<T, ID> {

    Optional<T> findById(ID id);

    List<T> findAll();
}
```

在这里，我们只定义了两个只读方法。**此Repository访问的实体将不会受到任何修改**。

还需要注意的是，我们必须使用@NoRepositoryBean注解告诉Spring我们希望这个Repository保持通用。
**这允许我们可以根据需要为任意多个不同实体重用我们的只读Repository**。

接下来，我们将了解如何将实体绑定到新的ReadOnlyRepository。

## 3. 继承ReadOnlyRepository

假设我们有一个简单的Book实体类：

```java

@Setter
@Getter
@Entity
public class Book {
    @Id
    @GeneratedValue
    private Long id;
    private String author;
    private String title;
}
```

现在我们可以创建一个从ReadOnlyRepository继承的Repository接口：

```java
public interface BookReadOnlyRepository extends ReadOnlyRepository<Book, Long> {
    List<Book> findByAuthor(String author);

    List<Book> findByTitle(String title);
}
```

除了继承的两个方法之外，我们还添加了另外两个特定于Book实体的只读方法：findByAuthor()和findByTitle()。
总的来说，这个Repository包含四个只读方法。

最后，我们编写一个测试来确保BookReadOnlyRepository的功能正确：

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

为了先保存一些Book实体数据，我们创建了一个BookRepository，它在测试范围内继承CrudRepository。
此Repository在我们的项目中是不重要的，但在本测试中是必需的。

```java
public interface BookRepository extends BookReadOnlyRepository, CrudRepository<Book, Long> {

}
```

我们能够测试所有四个只读方法，并且现在还可以为其他实体重用ReadOnlyRepository接口。

## 4. 总结

在本文中，我们介绍了如何继承Spring Data的Repository接口以创建可重用的只读Repository。
之后，我们将它绑定到一个简单的Book实体并编写了一个测试，证明它的功能与我们预期的确实一样。