## 1. 简介

在这个简短的教程中，**我们将学习如何使用Spring Data JPA @Query注解创建更新查询**。我们将通过使用@Modifying注解来实现这一点。

首先，作为回顾目的，我们可以阅读[如何使用Spring Data JPA进行查询](https://www.baeldung.com/spring-data-jpa-query)。之后，我们将深入探讨@Query和@Modifying注解的使用。最后，我们将讨论在使用修改查询时如何管理持久性上下文的状态。

## 2. 在Spring Data JPA中查询

首先，让我们回顾一下**Spring Data JPA为查询数据库中的数据提供的三种机制**：

-   查询方法
-   @Query注解
-   自定义Repository实现

让我们创建一个User类和一个匹配的Spring Data JPA Repository来说明这些机制：

```java
@Entity
@Table(name = "users", schema = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private LocalDate creationDate;
    private LocalDate lastLoginDate;
    private boolean active;
    private String email;
}
```

```java
public interface UserRepository extends JpaRepository<User, Integer> {}
```

查询方法机制允许我们通过从方法名称派生查询来操作数据：

```java
List<User> findAllByName(String name);
void deleteAllByCreationDateAfter(LocalDate date);
```

在此示例中，我们定义了一个按姓名检索用户的查询，以及一个删除创建日期在特定日期之后的用户的查询。

至于@Query注解，**它为我们提供了在@Query注解中编写特定JPQL或SQL查询的机会**：

```java
@Query("select u from User u where u.email like '%@gmail.com'")
List<User> findUsersWithGmailAddress();
```

在此代码片段中，我们定义了一个查询检索具有@gmail.com电子邮件地址的用户。

第一种机制使我们能够检索或删除数据。至于第二种机制，它允许我们执行几乎任何查询。但是，**对于更新查询，我们必须添加@Modifying注解**。这将是本教程的主题。

## 3. 使用@Modifying注解

**[@Modifying](https://docs.spring.io/spring-data/jpa/docs/current/api/org/springframework/data/jpa/repository/Modifying.html)注解用于增强@Query注解，以便我们不仅可以执行SELECT查询，还可以执行INSERT、UPDATE、DELETE甚至DDL查询**。

首先，让我们看一个@Modifying UPDATE查询的例子：

```java
@Modifying
@Query("update User u set u.active = false where u.lastLoginDate < :date")
void deactivateUsersNotLoggedInSince(@Param("date") LocalDate date);
```

在这里，我们将停用自给定日期以来未登录的用户。

让我们尝试另一个，我们将删除停用的用户：

```java
@Modifying
@Query("delete User u where u.active = false")
int deleteDeactivatedUsers();
```

如我们所见，此方法返回一个整数。**这是Spring Data JPA @Modifying查询的一个功能，它为我们提供了更新实体的数量**。

我们应该注意，使用@Query执行删除查询与Spring Data JPA的deleteBy名称派生查询方法不同。后者首先从数据库中获取实体，然后逐个删除它们。这意味着将在这些实体上调用生命周期方法@PreRemove。但是，对于前者，只对数据库执行单个查询。

最后，让我们使用DDL查询将已删除的列添加到我们的USERS表中：

```java
@Modifying
@Query(value = "alter table USERS.USERS add column deleted int(1) not null default 0", nativeQuery = true)
void addDeletedColumn();
```

不幸的是，使用修改查询会使底层的持久性上下文过时。但是，可以管理这种情况。这是下一节的主题。

### 3.1 不使用@Modifying注解的结果

让我们看看当我们不在删除查询上添加@Modifying注解时会发生什么。

为此，我们需要创建另一种方法：

```java
@Query("delete User u where u.active = false")
int deleteDeactivatedUsersWithNoModifyingAnnotation();
```

请注意我们没有添加@Modifying注解。

当我们执行上面的方法时，我们得到一个InvalidDataAccessApiUsage异常：

```shell
org.springframework.dao.InvalidDataAccessApiUsageException: org.hibernate.hql.internal.QueryExecutionRequestException: 
Not supported for DML operations [delete cn.tuyucheng.taketoday.boot.domain.User u where u.active = false]
(...)
```

错误信息描述很清楚：DML操作不支持该查询。

## 4. 管理持久性上下文

**如果我们的修改查询更改了持久性上下文中包含的实体，那么这个上下文就会过时**。处理这种情况的一种方法是[清除持久性上下文](https://docs.oracle.com/javaee/7/api/javax/persistence/EntityManager.html#clear--)。通过这样做，我们确保持久性上下文下次将从数据库中获取实体。

但是，我们不必显式调用EntityManager上的clear()方法。我们可以只使用@Modifying注解中的[clearAutomatically](https://codingexplained.com/coding/java/spring-framework/updating-entities-with-update-query-spring-data-jpa)属性：

```java
@Modifying(clearAutomatically = true)
```

**这样，我们确保在执行查询后清除持久性上下文**。

但是，如果我们的持久性上下文包含未刷新的更改，则清除它意味着删除未保存的更改。幸运的是，在这种情况下我们可以使用注解的另一个属性flushAutomatically：

```java
@Modifying(flushAutomatically = true)
```

**现在，在执行查询之前，实体管理器将被刷新**。

## 5. 总结

关于@Modifying注解的这篇短文章到此结束。我们学习了如何使用@Modifying注解来执行更新查询，例如INSERT、UPDATE、DELETE，甚至DDL。之后，我们讨论了如何使用clearAutomatically和flushAutomatically属性来管理持久化上下文的状态。