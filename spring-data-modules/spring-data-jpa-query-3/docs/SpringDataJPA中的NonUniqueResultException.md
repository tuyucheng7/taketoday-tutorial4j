## 1. 简介

[Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)提供了一个简单且一致的接口，用于访问存储在各种关系型数据库中的数据，使开发人员更容易编写与数据库无关的代码。它还消除了对大量样板代码的需求，使开发人员能够专注于构建其应用程序的业务逻辑。

但是，我们仍然需要确保正确的返回类型，否则会引发异常。在本教程中，我们将重点关注NonUniqueResultException。我们将了解导致它的原因以及如何在遇到它时修复我们的代码。

## 2. NonUniqueResultException

**当预期查询方法返回单个结果但找到多个结果时，Spring Data JPA框架会抛出NonUniqueResultException运行时异常**。当使用Spring Data JPA的查询方法之一(例如findById()、findOne()或不返回Collection的自定义方法)执行查询时，可能会发生这种情况。

当抛出NonUniqueResultException时，这意味着正在使用的方法旨在返回单个结果，但它找到了多个结果。这可能是由于查询不正确或数据库中的数据不一致。

## 3. 示例

让我们使用文章[使用Spring Data JPA按日期和时间查询实体](https://www.baeldung.com/spring-data-jpa-query-by-date)中提到的实体：

```java
@Entity
public class Article {

    @Id
    @GeneratedValue
    private Integer id;

    @Temporal(TemporalType.DATE)
    private Date publicationDate;

    @Temporal(TemporalType.TIME)
    private Date publicationTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDateTime;
}
```

现在，让我们创建我们的ArticleRepository并添加两个方法：

```java
public interface ArticleRepository extends JpaRepository<Article, Integer> {

    List<Article> findAllByPublicationTimeBetween(Date publicationTimeStart, Date publicationTimeEnd);

    Article findByPublicationTimeBetween(Date publicationTimeStart, Date publicationTimeEnd);
}
```

这两种方法之间的唯一区别是findAllByPublicationTimeBetween()使用List<Article\>作为返回类型，而findByPublicationTimeBetween()使用单个Article作为返回类型。

当我们执行第一个方法findAllByPublicationTimeBetween时，我们总是会得到一个集合。根据我们数据库中的数据，我们可能得到一个空列表或一个包含一个或多个文章实例的列表。

第二种方法findByPublicationTimeBetween在技术上也可以工作，因为数据库恰好包含零个或单个匹配的Article。如果给定查询没有单个条目，该方法将返回null。另一方面，如果有一个对应的Article，它将返回单个Article。

但是，如果有多个Article与findByPublicationTimeBetween的查询相匹配，该方法将抛出NonUniqueResultException，然后将其包装在IncorrectResultSizeDataAccessException中。

当这样的异常可能在运行时随机发生时，这表明数据库设计或我们的方法实现有问题。在下一节中，我们将了解如何避免此错误。

## 4. 避免NonUniqueResultException的技巧

为了避免NonUniqueResultException，仔细设计数据库查询并正确使用Spring Data JPA的查询方法非常重要。在设计查询时，确保它始终返回预期数量的结果非常重要。我们可以通过仔细指定我们的查询条件来实现这一点，例如使用唯一键或其他标识信息。

在设计查询方法时，我们应该遵循一些基本规则来避免NonUniqueResultExceptions：

-   **如果可能返回多个值，我们应该使用List或Set作为返回类型**。
-   如果我们可以**通过数据库设计确保只有一个返回值**，我们就只使用一个单一的返回值。当我们查找唯一键(如Id、UUID)时，情况总是如此，或者根据数据库设计，它也可能是保证唯一的电子邮件或电话号码。
-   确保只有一个返回值的另一种方法是**将[返回限制](https://www.baeldung.com/jpa-limit-query-results)为单个元素**。这可能很有用，例如，如果我们总是想要最新的Article。

## 5. 总结

NonUniqueResultException是使用Spring Data JPA时需要理解和避免的重要异常。它发生在查询预期返回单个结果但找到多个结果时。我们可以通过确保我们的JpaRepository方法返回正确数量的元素并相应地指定正确的返回类型来防止这种情况。

通过理解并正确避免NonUniqueResultException，我们可以确保我们的应用程序能够一致且可靠地访问数据库中的数据。