## 1. 概述

在本教程中，我们将讨论不同类型的[JPA](https://www.baeldung.com/jpa-hibernate-difference)查询。此外，我们将重点比较它们之间的差异并扩展每个人的优缺点。

## 2.设置

首先，让我们定义我们将用于本文中所有示例的UserEntity类：

```java
@Table(name = "users")
@Entity
public class UserEntity {

    @Id
    private Long id;
    private String name;
    //Standard constructor, getters and setters.

}
```

共有三种基本类型的 JPA 查询：

-   Query，用Java持久性查询语言 (JPQL) 语法编写
-   NativeQuery，以纯 SQL 语法编写
-   Criteria API 查询，通过不同的方法以编程方式构建

让我们探索它们。

## 3.查询

Query在语法上类似于 SQL，它通常用于执行 CRUD 操作：

```java
public UserEntity getUserByIdWithPlainQuery(Long id) {
    Query jpqlQuery = getEntityManager().createQuery("SELECT u FROM UserEntity u WHERE u.id=:id");
    jpqlQuery.setParameter("id", id);
    return (UserEntity) jpqlQuery.getSingleResult();
}
```

此查询从用户表中检索匹配的记录，并将其映射到UserEntity对象。

还有两个额外的查询子类型：

-   类型查询
-   命名查询

让我们看看他们的行动。

### 3.1. 类型查询

我们需要注意前面示例中的return语句。JPA 无法推断查询结果类型是什么，因此我们必须强制转换。

但是，JPA 提供了一种称为 TypedQuery的特殊查询子类型。 如果我们事先知道我们的查询结果类型，这总是首选。此外，它使我们的代码更加可靠且更易于测试。

与我们的第一个示例相比，让我们看一下TypedQuery替代方案：

```java
public UserEntity getUserByIdWithTypedQuery(Long id) {
    TypedQuery<UserEntity> typedQuery
      = getEntityManager().createQuery("SELECT u FROM UserEntity u WHERE u.id=:id", UserEntity.class);
    typedQuery.setParameter("id", id);
    return typedQuery.getSingleResult();
}
```

通过这种方式，我们可以免费获得更强的类型，避免可能出现的强制转换异常。

### 3.2. 命名查询

虽然我们可以在特定方法上动态定义Query，但它们最终会成长为难以维护的代码库。如果我们可以将一般使用查询保存在一个集中的、易于阅读的地方会怎样？

JPA 还通过另一种称为 [NamedQuery](https://www.baeldung.com/hibernate-named-query)的Query子类型让我们了解了这一点。

我们可以在orm.xml或属性文件中定义NamedQueries 。

此外，我们可以在Entity类本身上定义NamedQuery，提供一种集中、快速和简单的方法来读取和查找Entity的相关查询。

所有NamedQueries都必须有一个唯一的名称。

让我们看看如何将NamedQuery添加到我们的UserEntity类：

```java
@Table(name = "users")
@Entity
@NamedQuery(name = "UserEntity.findByUserId", query = "SELECT u FROM UserEntity u WHERE u.id=:userId")
public class UserEntity {

    @Id
    private Long id;
    private String name;
    //Standard constructor, getters and setters.

}
```

如果我们使用版本 8 之前的 Java，则必须将 @NamedQuery 注解分组在 @NamedQueries 注解中。从Java8 开始，我们可以简单地在我们的实体类中重复@NamedQuery注解。

使用NamedQuery非常简单：

```java
public UserEntity getUserByIdWithNamedQuery(Long id) {
    Query namedQuery = getEntityManager().createNamedQuery("UserEntity.findByUserId");
    namedQuery.setParameter("userId", id);
    return (UserEntity) namedQuery.getSingleResult();
}
```

## 4.原生查询

NativeQuery只是一个 SQL 查询。这些使我们能够释放数据库的全部功能，因为我们可以使用 JPQL 限制语法中不可用的专有功能。

这是有代价的。我们失去了使用NativeQuery的应用程序的数据库可移植性，因为我们的 JPA 提供程序无法再从数据库实现或供应商那里抽象出特定的细节。

让我们看看如何使用NativeQuery来产生与前面示例相同的结果：

```java
public UserEntity getUserByIdWithNativeQuery(Long id) {
    Query nativeQuery
      = getEntityManager().createNativeQuery("SELECT  FROM users WHERE id=:userId", UserEntity.class);
    nativeQuery.setParameter("userId", id);
    return (UserEntity) nativeQuery.getSingleResult();
}
```

我们必须始终考虑NativeQuery是否是唯一的选择。大多数时候，一个好的 JPQL查询可以满足我们的需求，最重要的是，保持对实际数据库实现的抽象级别。

使用NativeQuery并不一定意味着将我们锁定到一个特定的数据库供应商。毕竟，如果我们的查询不使用专有 SQL 命令并且仅使用标准 SQL 语法，那么切换提供程序应该不是问题。

## 5. Query、NamedQuery和NativeQuery

到目前为止，我们已经了解了Query、NamedQuery和NativeQuery。

现在，让我们快速回顾一下它们并总结一下它们的优缺点。

### 5.1. 询问

我们可以使用entityManager.createQuery(queryString)创建一个查询。

接下来，让我们探讨一下Query的优缺点：

优点：

-   当我们使用EntityManager创建查询时，我们可以构建动态查询字符串
-   查询是用 JPQL 编写的，因此它们是可移植的

缺点：

-   [对于动态查询，根据查询计划缓存](https://www.baeldung.com/hibernate-query-plan-cache)的不同，可能会多次编译成原生SQL语句
-   查询可能分散到各种Java类中，并且它们与Java代码混合在一起。因此，如果项目包含许多查询，则可能难以维护

### 5.2. 命名查询

一旦定义了NamedQuery ，我们就可以使用EntityManager 引用它：

```java
entityManager.createNamedQuery(queryName);
```

现在，让我们看看NamedQueries的优点和缺点：

优点：

-   加载持久性单元时编译和验证NamedQueries 。也就是说，它们只被编译一次
-   我们可以集中NamedQueries以使其更易于维护——例如，在orm.xml中、在属性文件中或在@Entity类中

缺点：

-   NamedQueries始终是静态的
-   可以在 Spring Data JPA 存储库中引用NamedQueries 。但是不支持[动态排序](https://www.baeldung.com/spring-data-sorting#sorting-with-spring-data)

### 5.3. 本机查询

我们可以 使用EntityManager创建一个NativeQuery：

```java
entityManager.createNativeQuery(sqlStmt);
```

根据结果映射，我们还可以将第二个参数传递给方法，例如我们在前面的示例中看到的实体类。

NativeQueries也有利有弊。让我们快速看看它们：

优点：

-   随着我们的查询变得复杂，有时 JPA 生成的 SQL 语句并不是最优化的。在这种情况下，我们可以使用NativeQueries 来提高查询效率
-   NativeQueries允许我们使用数据库供应商特定的功能。有时，这些特性可以为我们的查询提供更好的性能

缺点：

-   特定于供应商的功能可以带来便利和更好的性能，但我们为此付出了代价，失去了从一个数据库到另一个数据库的可移植性

## 6.条件API查询

[Criteria API 查询](https://www.baeldung.com/hibernate-criteria-queries)是以编程方式构建的、类型安全的查询——在语法上有点类似于 JPQL 查询：

```java
public UserEntity getUserByIdWithCriteriaQuery(Long id) {
    CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<UserEntity> criteriaQuery = criteriaBuilder.createQuery(UserEntity.class);
    Root<UserEntity> userRoot = criteriaQuery.from(UserEntity.class);
    UserEntity queryResult = getEntityManager().createQuery(criteriaQuery.select(userRoot)
      .where(criteriaBuilder.equal(userRoot.get("id"), id)))
      .getSingleResult();
    return queryResult;
}
```

直接使用Criteria API 查询可能会让人望而生畏，但当我们需要添加动态查询元素或与[JPA元模型结合使用时，它们可能是一个不错的选择。](https://www.baeldung.com/hibernate-criteria-queries-metamodel)

## 七. 总结

在这篇简短的文章中，我们了解了什么是 JPA 查询及其用法。

JPA 查询是从数据访问层抽象业务逻辑的好方法，因为我们可以依赖 JPQL 语法并让我们选择的 JPA 提供程序处理查询转换。