## 1. 概述

[Apache DeltaSpike](https://deltaspike.apache.org/)是一个为Java项目提供CDI 扩展集合的项目；它需要 CDI 实现在运行时可用。

当然，它可以与 CDI 的不同实现一起工作——JBoss Weld 或 OpenWebBeans。它还在许多应用程序服务器上进行了测试。

在本教程中，我们将重点关注最著名和最有用的模块之一——数据模块。

## 2. DeltaSpike 数据模块设置

Apache DeltaSpike 数据模块用于简化存储库模式的实施。它允许通过为查询创建和执行提供集中逻辑来减少样板代码。

[它与Spring Data](https://www.baeldung.com/spring-data)项目非常相似。要查询数据库，我们需要定义一个方法声明(没有实现)，它遵循定义的命名约定或包含@Query注解。实施将由 CDI 扩展为我们完成。

在接下来的小节中，我们将介绍如何在我们的应用程序中设置 Apache DeltaSpike 数据模块。

### 2.1. 必需的依赖项

要在应用程序中使用 Apache DeltaSpike 数据模块，我们需要设置所需的依赖项。

当 Maven 是我们的构建工具时，我们必须使用：

```xml
<dependency>
    <groupId>org.apache.deltaspike.modules</groupId>
    <artifactId>deltaspike-data-module-api</artifactId>
    <version>1.8.2</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>org.apache.deltaspike.modules</groupId>
    <artifactId>deltaspike-data-module-impl</artifactId>
    <version>1.8.2</version>
    <scope>runtime</scope>
</dependency>
```

当我们使用 Gradle 时：

```groovy
runtime 'org.apache.deltaspike.modules:deltaspike-data-module-impl'
compile 'org.apache.deltaspike.modules:deltaspike-data-module-api'

```

Apache DeltaSpike 数据模块工件在 Maven Central 上可用：

-   [deltaspike-data-module-impl](https://search.maven.org/classic/#search|ga|1|a%3A"deltaspike-data-module-impl")
-   [deltaspike-数据-模块-api](https://search.maven.org/classic/#search|ga|1|a%3A"deltaspike-data-module-api")

要使用数据模块运行应用程序，我们还需要在运行时可用的 JPA 和 CDI 实现。

尽管可以在JavaSE 应用程序中运行 Apache DeltaSpike，但在大多数情况下，它将部署在应用程序服务器(例如 Wildfly 或 WebSphere)上。

应用服务器有完整的 Jakarta EE 支持，所以我们不需要做更多的事情。对于JavaSE 应用程序，我们必须提供这些实现(例如，通过向 Hibernate 和 JBoss Weld 添加依赖项)。

接下来，我们还将介绍EntityManager所需的配置。

### 2.2. 实体管理器配置

Data 模块需要 通过 CDI 注入EntityManager。

我们可以通过使用 CDI 生产者来实现这一点：

```java
public class EntityManagerProducer {

    @PersistenceContext(unitName = "primary")
    private EntityManager entityManager;

    @ApplicationScoped
    @Produces
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
```

上面的代码假定我们在persistence.xml文件中定义了名称为primary的持久性单元。

让我们看下面的定义示例：

```java
<persistence-unit name="primary" transaction-type="JTA">
   <jta-data-source>java:jboss/datasources/baeldung-jee7-seedDS</jta-data-source>
   <properties>
      <property name="hibernate.hbm2ddl.auto" value="create-drop" />
      <property name="hibernate.show_sql" value="false" />
   </properties>
</persistence-unit>
```

我们示例中的持久性单元使用 JTA 事务类型，这意味着我们必须提供我们将要使用的事务策略。

### 2.3. 交易策略

如果我们为数据源使用 JTA 事务类型，那么我们必须定义将在 Apache DeltaSpike 存储库中使用的事务策略。我们可以在apache-deltaspike.properties文件中(在META-INF目录下)完成它：

```java
globalAlternatives.org.apache.deltaspike.jpa.spi.transaction.TransactionStrategy=org.apache.deltaspike.jpa.impl.transaction.ContainerManagedTransactionStrategy
```

我们可以定义四种交易策略：

-   BeanManagedUserTransactionStrategy
-   ResourceLocalTransactionStrategy
-   ContainerManagedTransactionStrategy
-   环境感知交易策略

它们都实现了 org.apache.deltaspike.jpa.spi.transaction.TransactionStrategy。

这是我们的数据模块所需配置的最后一部分。

接下来，我们将展示如何实现存储库模式类。

## 3. 存储类

当我们使用 Apache DeltaSpike 数据模块时，任何抽象类或接口都可以成为存储库类。

我们所要做的就是 添加一个 带有 forEntity属性的@Repository注解 ，该属性定义了我们的存储库应该处理的 JPA 实体：

```java
@Entity
public class User {
    // ...
}  

@Repository(forEntity = User.class) 
public interface SimpleUserRepository { 
    // ... 
}
```

或者使用抽象类：

```java
@Repository(forEntity = User.class)
public abstract class SimpleUserRepository { 
    // ... 
}

```

数据模块发现带有此类注解的类(或接口)，并且它将处理其中的方法。

定义要执行的查询的可能性很小。我们将在接下来的部分中一一介绍。

## 4.从方法名查询

定义查询的第一种可能性是使用遵循已定义命名约定的方法名称。

它看起来像下面这样：

```java
(Entity|Optional<Entity>|List<Entity>|Stream<Entity>) (prefix)(Property[Comparator]){Operator Property [Comparator]}

```

接下来，我们将重点关注此定义的每个部分。

### 4.1. 返回类型

返回类型主要定义我们的查询可能返回多少个对象。我们不能将单个实体类型定义为返回值，以防我们的查询可能返回多个结果。

如果有多个具有给定名称的用户，以下方法将抛出异常：

```java
public abstract User findByFirstName(String firstName);
```

反之则不然——我们可以将返回值定义为Collection，即使结果只是单个实体。

```java
public abstract Collection<User> findAnyByFirstName(String firstName);
```

如果我们将返回值定义为Collection，建议一个值作为返回类型(例如findAny )的方法名称前缀将被取消。

上面的查询将返回所有名字匹配的用户，即使方法名称前缀暗示了不同的东西。

应该避免这种组合(Collection返回类型和暗示一个单一值返回的前缀)，因为代码变得不直观且难以理解。

下一节将显示有关方法名称前缀的更多详细信息。

### 4.2. 查询方法的前缀

前缀定义了我们要对存储库执行的操作。最有用的一个是找到符合给定搜索条件的实体。

此操作有许多前缀，如findBy、findAny、findAll。 详细列表请查看 Apache DeltaSpike 官方[文档](https://deltaspike.apache.org/documentation/data.html#UsingMethodExpressions)：

```java
public abstract User findAnyByLastName(String lastName);
```

但是，还有其他方法模板用于计算和删除实体。我们可以计算表中的所有行：

```java
public abstract int count();
```

此外， 存在删除方法模板，我们可以将其添加到我们的存储库中：

```java
public abstract void remove(User user);
```

下一版本的 Apache DeltaSpike 1.9.0 将添加对countBy 和removeBy方法前缀的支持。

下一节将展示我们如何向查询添加更多属性。

### 4.3. 查询多个属性

在查询中，我们可以结合使用许多属性 和运算符。

```java
public abstract Collection<User> findByFirstNameAndLastName(
  String firstName, String lastName);
public abstract Collection<User> findByFirstNameOrLastName(
  String firstName, String lastName);

```

我们可以根据需要组合任意多的属性。搜索嵌套属性也可用，我们将在接下来展示。

### 4.4. 使用嵌套属性查询

查询也可以使用嵌套属性。

在以下示例中，用户实体具有地址类型的地址属性，地址实体具有城市属性：

```java
@Entity
public class Address {
private String city;
    // ...
}
@Entity
public class User {
    @OneToOne 
    private Address address;
    // ...
}
public abstract Collection<User> findByAddress_city(String city);
```

### 4.5. 查询中的顺序

DeltaSpike 允许我们定义返回结果的顺序。我们可以同时定义升序和降序：

```java
public abstract List<User> findAllOrderByFirstNameAsc();
```

如上所示，我们要做的就是在方法名称中添加一部分，其中包含我们要排序的属性名称和排序方向的简称。

我们可以轻松组合许多订单：

```java
public abstract List<User> findAllOrderByFirstNameAscLastNameDesc();

```

接下来，我们将展示如何限制查询结果的大小。

### 4.6. 限制查询结果大小和分页

在某些情况下，我们想要从整个结果中检索几行第一行。这就是所谓的查询限制。使用 Data 模块也很简单：

```java
public abstract Collection<User> findTop2OrderByFirstNameAsc();
public abstract Collection<User> findFirst2OrderByFirstNameAsc();
```

First和top可以互换使用。

然后，我们可以通过提供两个附加参数来启用查询分页：@FirstResult和@MaxResult：

```java
public abstract Collection<User> findAllOrderByFirstNameAsc(@FirstResult int start, @MaxResults int size);
```

我们已经在存储库中定义了很多方法。其中一些是通用的，应定义一次并由每个存储库使用。

Apache DeltaSpike 提供了一些基本类型，我们可以使用它们来获得很多开箱即用的方法。

在下一节中，我们将重点介绍如何执行此操作。

## 5. 基本存储库类型

要获得一些基本的存储库方法，我们的存储库应该扩展 Apache DeltaSpike 提供的基本类型。其中有一些像EntityRepository、FullEntityRepository等：

```java
@Repository
public interface UserRepository 
  extends FullEntityRepository<User, Long> {
    // ...
}
```

或者使用抽象类：

```java
@Repository
public abstract class UserRepository extends AbstractEntityRepository<User, Long> {
    // ...
}

```

上面的实现为我们提供了很多方法而无需编写额外的代码行，因此我们得到了我们想要的——我们大量减少了样板代码。

如果我们使用基本存储库类型，则无需将额外的forEntity 属性值传递给我们的@Repository注解。

当我们为我们的存储库使用抽象类而不是接口时，我们获得了创建自定义查询的额外可能性。

抽象基础存储库类，例如 AbstractEntityRepository使我们能够访问字段(通过 getter)或我们可以用来创建查询的实用方法：

```java
public List<User> findByFirstName(String firstName) {
    return typedQuery("select u from User u where u.firstName = ?1")
      .setParameter(1, firstName)
      .getResultList();
}

```

在上面的示例中，我们使用了typedQuery 实用方法来创建自定义实现。

创建查询的最后一种可能性是使用我们接下来将展示的@Query注解。

## 6. @Query注解

要执行的 SQL查询也可以用@Query注解定义。它与 Spring 解决方案非常相似。我们必须向以 SQL 查询作为值的方法添加注解。

默认情况下，这是一个 JPQL 查询：

```java
@Query("select u from User u where u.firstName = ?1")
public abstract Collection<User> findUsersWithFirstName(String firstName);

```

如上例所示，我们可以轻松地通过索引将参数传递给查询。

如果我们想通过原生 SQL 而不是 JPQL 传递查询，我们需要定义额外的查询属性 – isNative和 true 值：

```java
@Query(value = "select  from User where firstName = ?1", isNative = true)
public abstract Collection<User> findUsersWithFirstNameNative(String firstName);
```

## 七. 总结

在本文中，我们介绍了 Apache DeltaSpike 的基本定义，并重点介绍了令人兴奋的部分——数据模块。它与 Spring Data Project 非常相似。

我们探索了如何实现存储库模式。我们还介绍了如何定义要执行的查询的三种可能性。