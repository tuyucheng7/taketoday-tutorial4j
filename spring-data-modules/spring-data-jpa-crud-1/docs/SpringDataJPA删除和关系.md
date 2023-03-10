## 1. 概述

在本教程中，我们将了解如何在[Spring Data JPA](https://spring.io/projects/spring-data-jpa)中完成删除操作。

## 2. 简单实体

正如我们从[Spring Data JPA参考文档](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories)中了解到的，Repository接口为我们提供了一些对实体的基本支持。

假设我们有一个实体，例如Book：

```java
@Entity
public class Book {

    @Id
    @GeneratedValue
    private Long id;
    private String title;

    // standard constructors ...

    // standard getters and setters ...
}
```

然后我们可以扩展Spring Data JPA的CrudRepository接口，让我们可以访问Book上的CRUD操作：

```java
@Repository
public interface BookRepository extends CrudRepository<Book, Long> {
}
```

## 3. Repository的删除方法

其中，CrudRepository包含两种删除方法：deleteById和deleteAll。

让我们直接从BookRepository测试这些方法：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class})
class DeleteFromRepositoryUnitTest {

    @Autowired
    private BookRepository repository;

    Book book1;
    Book book2;

    @BeforeEach
    void setup() {
        book1 = new Book("The Hobbit");
        book2 = new Book("All Quiet on the Western Front");

        repository.saveAll(Arrays.asList(book1, book2));
    }

    @AfterEach
    void teardown() {
        repository.deleteAll();
    }

    @Test
    void whenDeleteByIdFromRepository_thenDeletingShouldBeSuccessful() {
        repository.deleteById(book1.getId());

        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void whenDeleteAllFromRepository_thenRepositoryShouldBeEmpty() {
        repository.deleteAll();

        assertThat(repository.count()).isEqualTo(0);
    }
}
```

即使我们使用的是CrudRepository，但请注意，对于其他Spring Data JPA接口(例如JpaRepositories或PagingAndSortingRepository)，也存在这些相同的方法。

## 4. 派生删除方法

我们还可以派生用于删除实体的查询方法。这些方法有一套编写规则，但我们只关注最简单的示例。

**派生的删除方法必须以deleteBy开头，后跟选择条件的名称。这些条件必须在方法调用中提供**。

假设我们想按标题删除Book对象。根据命名约定，我们将以deleteBy开头并将title列为我们的标准：

```java
@Repository
public interface BookRepository extends CrudRepository<Book, Long> {

    long deleteByTitle(String title);
}
```

long类型的返回值表示该方法删除了多少条记录。

让我们编写一个测试并确保它是正确的：

```java
@Test
@Transactional
void whenDeleteFromDerivedQuery_thenDeletingShouldBeSuccessful() {
    long deletedRecords = bookRepository.deleteByTitle("The Hobbit");
    assertThat(deletedRecords).isEqualTo(1);
}
```

**在JPA中持久化和删除对象需要一个事务。这就是为什么在使用这些[派生的删除方法](https://www.baeldung.com/spring-data-jpa-deleteby)时应该使用@Transactional注解，以确保在事务中运行**。这在[Spring与ORM文档](https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#orm)中有详细说明。

## 5. 自定义删除查询

派生查询的方法名称可能会很长，并且仅限于单个表。

当我们需要做更复杂的查询时，我们可以一起使用@Query和[@Modifying](https://www.baeldung.com/spring-data-jpa-modifying-annotation)注解来编写自定义查询。

让我们使用这些注解编写之前派生方法的等效代码：

```java
@Modifying
@Query("delete from Book b where b.title=:title")
void deleteBooks(@Param("title") String title);
```

同样，我们可以通过一个简单的测试来验证它是否有效：

```java
@Test
@Transactional
void whenDeleteFromCustomQuery_thenDeletingShouldBeSuccessful() {
    bookRepository.deleteBooks("The Hobbit");
    assertThat(bookRepository.count()).isEqualTo(1);
}
```

上面介绍的两种解决方案都是相似的，并且实现了相同的结果。但是，它们采取的方法略有不同。

**@Query注解方法针对数据库创建单个JPQL查询。相比之下，deleteBy方法执行读取查询，然后逐个删除每一项**。

## 6. 包含映射关系的删除

现在让我们看看当我们**与其他实体建立关系**时会发生什么。

假设我们有一个Category实体，它与Book实体具有一对多的关联关系：

```java
@Entity
public class Category {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books;

    // standard constructors ...

    // standard getters and setters ...
}
```

CategoryRepository可以只是一个扩展CrudRepository的空接口：

```java
@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
}
```

我们还应该修改Book实体以反映这种关联：

```java
@Entity
public class Book {

    @Id
    @GeneratedValue
    private Long id;
    private String title;

    @ManyToOne
    private Category category;
    // setters getters and constructors ...
}
```

现在让我们添加两个Category，并将它们与当前拥有的Book相关联。

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DeleteApplication.class)
class DeleteInRelationshipsUnitTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setup() {
        Book book1 = new Book("The Hobbit");
        Category category1 = new Category("Cat1", book1);
        categoryRepository.save(category1);

        Book book2 = new Book("All Quiet on the Western Front");
        Category category2 = new Category("Cat2", book2);
        categoryRepository.save(category2);
    }

    @AfterEach
    void teardown() {
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
    }
}
```

**现在如果我们尝试删除类别，书籍也将被删除**：

```java
@Test
void whenDeletingCategories_thenBooksShouldAlsoDeleted() {
    categoryRepository.deleteAll();
    assertThat(bookRepository.count()).isEqualTo(0);
    assertThat(categoryRepository.count()).isEqualTo(0);
}
```

**不过，这不是双向的**，这意味着如果我们删除书籍，类别仍然存在：

```java
@Test
void whenDeletingBooks_thenCategoriesShouldNotAlsoDeleted() {
    bookRepository.deleteAll();
    assertThat(bookRepository.count()).isEqualTo(0);
    assertThat(categoryRepository.count()).isEqualTo(2);
}
```

我们可以通过更改关系的属性(例如[CascadeType](https://www.baeldung.com/delete-with-hibernate))来改变这种行为。

## 7. 总结

在本文中，我们看到了在Spring Data JPA中删除实体的不同方法。

我们查看了CrudRepository提供的删除方法以及我们的派生查询或使用@Query注解的自定义查询。

我们还看到了删除是如何在关联实体中完成的。