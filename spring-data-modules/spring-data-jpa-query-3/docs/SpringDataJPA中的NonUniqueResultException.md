## **一、简介**

[Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)提供了一个简单且一致的接口，用于访问存储在各种关系数据库中的数据，使开发人员更容易编写与数据库无关的代码。它还消除了对大量样板代码的需求，使开发人员能够专注于构建其应用程序的业务逻辑。

但是，我们仍然需要确保正确的返回类型，否则会抛出*Exception 。*在本教程中，我们将重点关注*NonUniqueResultException*。我们将了解导致它的原因以及遇到它时如何修复我们的代码。

## **2.\*非唯一结果异常\***

**当预期查询方法返回单个结果但发现多个结果时，** Spring Data JPA 框架会抛出*NonUniqueResultException*运行时异常 。当使用 Spring Data JPA 的查询方法之一（例如*findById()*、 *findOne()*或不返回 collection 的自定义方法执行查询时，可能会发生这种情况。

当抛出*NonUniqueResultException*时，这意味着正在使用的方法旨在返回单个结果，但它发现了多个结果。这可能是由于查询不正确或数据库中的数据不一致。

## **3.例子**

让我们使用我们从文章[Query Entities by Dates and Times with Spring Data JPA中了解到的](https://www.baeldung.com/spring-data-jpa-query-by-date)*实体*：

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
}复制
```

现在，让我们创建我们的 *ArticleRepository*并添加两个方法：

```java
public interface ArticleRepository extends JpaRepository<Article, Integer> {

    List<Article> findAllByPublicationTimeBetween(Date publicationTimeStart, Date publicationTimeEnd);

    Article findByPublicationTimeBetween(Date publicationTimeStart, Date publicationTimeEnd);
}
复制
```

这两种方法之间的唯一区别是*findAllByPublicationTimeBetween()*具有*List* < *Article>*作为返回类型，而*findByPublicationTimeBetween()*具有单个*Article*作为返回类型。

当我们执行第一个方法*findAllByPublicationTimeBetween*时，我们总是会得到一个集合。根据我们数据库中的数据，我们可以获得一个空*列表*或一个包含一个或多个*文章实例的**列表*。

第二种方法*findByPublicationTimeBetween*在技术上也可以工作，因为数据库恰好包含零个或一个匹配的*Article*。如果给定查询没有单个条目，该方法将返回*null*。另一方面，如果有一个对应的*Article*，它将返回单个*Article*。

但是，如果有多个*Article与**findByPublicationTimeBetween*的查询相匹配，该方法将抛出*NonUniqueResultException*，然后将其包装在*IncorrectResultSizeDataAccessException*中。

当这样的异常在运行时随机发生时，表明数据库设计或我们的方法实现有问题。在下一节中，我们将了解如何避免此错误。

## **4. 避免\*NonUniqueResultException 的技巧\***

为避免*NonUniqueResultException*，仔细设计数据库查询并正确使用 Spring Data JPA 的查询方法非常重要。在设计查询时，确保它始终返回预期数量的结果非常重要。我们可以通过仔细指定我们的查询条件来实现这一点，例如使用唯一键或其他识别信息。

在设计查询方法时，我们应该遵循一些基本规则来避免*NonUniqueResultExceptions ：*

-   **如果可能返回多个值，我们应该使用*****List\*或\*Set\***作为返回类型。
-   如果我们可以**通过数据库设计确保只有一个返回值，我们就只使用一个单一的返回值**。当我们寻找唯一键（如*Id*、*UUID ）*时，情况总是如此，或者根据数据库设计，它也可能是保证唯一的电子邮件或电话号码。
-   确保只有一个返回值的另一种方法是将**[返回限制](https://www.baeldung.com/jpa-limit-query-results)为单个元素**。这可能很有用，例如，如果我们总是想要最新的*Article*。

## **5.结论** 

NonUniqueResultException是使用 Spring Data JPA*时*需要理解和避免的重要异常。它发生在查询预期返回单个结果但找到多个结果时。我们可以通过确保我们的*JpaRepository*方法返回正确数量的元素并相应地指定正确的返回类型来防止这种情况。

通过理解并正确避免*NonUniqueResultException*，我们可以确保我们的应用程序能够一致且可靠地访问数据库中的数据。