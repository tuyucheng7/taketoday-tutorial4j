## 1. 概述

Spring Data JPA提供了许多处理实体的方法，包括[查询方法](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)和自定义[JPQL查询](https://www.baeldung.com/spring-data-jpa-query)。但有时，我们需要一种更具编程性的方法，例如[Criteria API](https://www.baeldung.com/hibernate-criteria-queries)或[QueryDSL](https://www.baeldung.com/querydsl-with-jpa-tutorial)。

**Criteria API提供了一种编程方式来创建类型化查询**，这有助于我们避免语法错误。此外，当我们将它与元模型API一起使用时，它会进行编译时检查以确认我们是否使用了正确的字段名称和类型。

但是，它也有缺点；我们必须用样板代码编写冗长的逻辑。

**在本教程中，我们将学习如何使用Criteria查询来实现我们的自定义DAO逻辑。我们还将说明Spring如何帮助减少样板代码**。

## 2. 示例应用程序

为了示例的简单起见，我们将以多种方式实现相同的查询：按作者姓名和包含String的标题查找书籍。

下面是Book实体：

```java
@Entity
class Book {

    @Id
    Long id;
    String title;
    String author;

    // getters and setters
}
```

因为我们想让事情简单化，所以我们不会在本教程中使用Metamodel API。

## 3. Repository

众所周知，在Spring组件模型中，**我们应该将数据访问逻辑放在@Repository bean中**。当然，这个逻辑可以使用任何实现，比如Criteria API。

为此，我们只需要一个EntityManager实例，我们可以自动注入它：

```java
@Repository
public class BookRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Book> findBooksByAuthorNameAndTitle(String authorName, String title) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);

        Root<Book> book = cq.from(Book.class);
        Predicate authorNamePredicate = cb.equal(book.get("author"), authorName);
        Predicate titlePredicate = cb.like(book.get("title"), "%" + title + "%");
        cq.where(authorNamePredicate, titlePredicate);

        TypedQuery<Book> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
}
```

上面的代码遵循标准的Criteria API工作流：

+ 首先，我们得到一个CriteriaBuilder引用，我们可以使用它来创建查询的不同部分。
+ 使用CriteriaBuilder，我们创建了一个CriteriaQuery<Book\>，它描述了我们想要在查询中执行的操作。它还声明结果中行的类型。
+ 使用CriteriaQuery<Book\>，我们声明查询的起点(Book实体)，并将其存储在book变量中以备后用。
+ 接下来，使用CriteriaBuilder，我们针对Book实体创建Predicate。请注意，这些Predicate还没有任何效果。
+ 我们将这两个谓词应用于我们的CriteriaQuery，CriteriaQuery.where(Predicate...)将其参数组合成一个逻辑与。这就是我们将这些谓词与查询联系起来的地方。
+ 之后，我们从CriteriaQuery创建一个TypedQuery<Book\>实例。
+ 最后，我们返回所有匹配的Book实体。

请注意，由于我们使用@Repository标注了BookRepository类，因此Spring为此类**启用了异常转换**。

## 4. 使用自定义方法扩展Repository

拥有[自动自定义查询](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa#customquery)是一个强大的Spring Data特性。但是，有时我们需要更复杂的逻辑，这是我们无法使用自动查询方法创建的。

我们可以在单独的Repository类中实现这些查询(如上一节中所示)。

或者，**如果我们希望@Repository接口具有带有自定义实现的方法，我们可以使用[组合Repository](https://www.baeldung.com/spring-data-composable-repositories)**。

自定义接口如下所示：

```java
public interface BookRepositoryCustom {
    List<Book> findBooksByAuthorNameAndTitle(String authorName, String title);
}
```

以及@Repository接口:

```java
interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {
}
```

我们还必须修改我们之前的Repository类来实现BookRepositoryCustom，并将其重命名为BookRepositoryImpl：

```java
@Repository
public class BookRepositoryImpl implements BookRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Book> findBooksByAuthorNameAndTitle(String authorName, String title) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);

        Root<Book> book = cq.from(Book.class);
        Predicate authorNamePredicate = cb.equal(book.get("author"), authorName);
        Predicate titlePredicate = cb.like(book.get("title"), "%" + title + "%");
        cq.where(authorNamePredicate, titlePredicate);

        TypedQuery<Book> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
}
```

当我们将BookRepository声明为依赖bean时，Spring会找到BookRepositoryImpl并在我们调用自定义方法时使用它。

假设我们要选择在查询中使用哪些谓词。例如，当我们不想按作者和书名查找书籍时，我们只需要匹配作者即可。

有多种方法可以做到这一点，例如仅当传递的参数不为null时才应用谓词：

```java
@Override
List<Book> findBooksByAuthorNameAndTitle(String authorName, String title) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Book> cq = cb.createQuery(Book.class);

    Root<Book> book = cq.from(Book.class);
    List<Predicate> predicates = new ArrayList<>();
    
    if (authorName != null) {
        predicates.add(cb.equal(book.get("author"), authorName));
    }
    if (title != null) {
        predicates.add(cb.like(book.get("title"), "%" + title + "%"));
    }
    cq.where(predicates.toArray(new Predicate[0]));

    return em.createQuery(cq).getResultList();
}
```

但是，这种方法**使代码难以维护**，特别是如果我们有很多谓词并希望将它们设为可选时。

将这些谓词外部化将是一个实用的解决方案。使用JPA Specifications，我们可以做到这一点。

## 5. 使用JPA Specifications

Spring Data引入了org.springframework.data.jpa.domain.Specification接口来封装单个谓词：

```java
interface Specification<T> {
    Predicate toPredicate(Root<T> root, CriteriaQuery query, CriteriaBuilder cb);
}
```

我们可以提供创建Specification实例的方法：

```java
interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom, JpaSpecificationExecutor<Book> {
    static Specification<Book> hasAuthor(String author) {
        return (book, cq, cb) -> cb.equal(book.get("author"), author);
    }

    static Specification<Book> titleContains(String title) {
        return (book, cq, cb) -> cb.like(book.get("title"), "%" + title + "%");
    }
}
```

要使用它们，我们需要我们的Repository接口来扩展org.springframework.data.jpa.repository.JpaSpecificationExecutor<T\>：

```java
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
}
```

该接口**声明了使用Specification的便捷方法**。例如，现在我们可以使用以下代码找到具有指定作者的所有Book实例：

```java
bookRepository.findAll(hasAuthor(author));
```

不幸的是，我们没有任何方法可以传递多个Specification参数。相反，我们使用org.springframework.data.jpa.domain.Specification接口中的工具方法。

例如，我们可以将两个Specification实例使用and组合：

```java
bookRepository.findAll(where(hasAuthor(author)).and(titleContains(title)));
```

在上面的示例中，where()是Specification类的静态方法。

**这样我们就可以使我们的查询模块化。此外，我们不必编写Criteria API样板代码，因为Spring为我们提供了它**。

请注意，这并不意味着我们不再需要编写标准样板；这种方法只能处理我们看到的工作流，即选择满足所提供条件的实体。

**查询可能有许多它不支持的结构，例如分组，返回与我们选择的类不同的类或子查询**。

## 6. 总结

在本文中，我们讨论了在Spring应用程序中使用Criteria查询的三种方法：

+ 创建Repository类是最直接和灵活的方式。
+ 扩展@Repository接口以与自动查询无缝集成。
+ 在Specification实例中使用谓词使简单的案例更清晰、更简洁。