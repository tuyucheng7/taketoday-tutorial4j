## 1. 概述

本教程将重点**介绍将Spring Data JPA引入Spring项目**，并全面配置持久层。有关使用基于Java的配置和项目的基本Maven POM设置Spring上下文的分步介绍，请参阅[本文](https://www.baeldung.com/bootstraping-a-web-application-with-spring-and-java-based-configuration)。

## 延伸阅读

### [使用Spring的JPA指南](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)

使用Spring设置JPA-如何设置EntityManager工厂和使用原始JPA API。

[阅读更多](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)→

### [Spring Data中的CrudRepository、JpaRepository和PagingAndSortingRepository](https://www.baeldung.com/spring-data-repositories)

了解Spring Data提供的不同风格的Repository。

[阅读更多](https://www.baeldung.com/spring-data-repositories)→

### [使用Spring和Java泛型简化DAO](https://www.baeldung.com/simplifying-the-data-access-layer-with-spring-and-java-generics)

通过使用单一的、通用的DAO简化数据访问层，这可以实现优雅的数据访问，没有不必要的混乱。

[阅读更多](https://www.baeldung.com/simplifying-the-data-access-layer-with-spring-and-java-generics)→

## 2. Spring Data Generated DAO-不再有DAO实现

正如我们在之前的文章中讨论的那样，[DAO层](https://www.baeldung.com/simplifying-the-data-access-layer-with-spring-and-java-generics)通常包含大量可以而且应该简化的样板代码。这种简化的优点有很多：减少我们需要定义和维护的工件数量、数据访问模式的一致性以及配置的一致性。

Spring Data将这种简化更进一步，**使得完全删除DAO实现成为可能**。DAO的接口现在是我们需要显式定义的唯一组件。

为了开始利用JPA的Spring Data编程模型，DAO接口需要扩展JPA特定的Repository接口JpaRepository。这将使Spring Data能够找到此接口并自动为其创建一个实现。

通过扩展接口，我们获得了标准DAO中可用的标准数据访问最相关的CRUD方法。

## 3. 自定义访问方式和查询

如前所述，**通过实现Repository接口之一，DAO已经定义和实现了一些基本的CRUD方法(和查询)**。

为了定义更具体的访问方法，Spring JPA支持相当多的选项：

-   只需在接口中**定义一个新方法**
-   使用@Query注解提供实际的**JPQL查询**
-   在Spring Data中使用更高级的**Specifications和Querydsl支持**
-   通过JPA命名查询定义**自定义查询**

[第三种方法](http://spring.io/blog/2011/04/26/advanced-spring-data-jpa-specifications-and-querydsl/)，Specifications和Querydsl支持，类似于JPA Criteria，但是使用了更灵活方便的API。这使得整个操作更具可读性和可重用性。当处理大量固定查询时，此API的优势将更加明显，因为我们可以通过较少数量的可重用块来更简洁地表达这些查询。

最后一种方法的缺点是它要么涉及XML，要么给域类增加查询负担。

### 3.1 自动自定义查询

当Spring Data创建一个新的Repository实现时，它会分析接口定义的所有方法，并尝试**从方法名称自动生成查询**。虽然这有一些限制，但它是一种非常强大和优雅的方式，可以轻松定义新的自定义访问方法。

让我们看一个例子：如果实体有name字段(以及Java Bean标准的getName和setName方法)，**我们将在DAO接口中定义findByName方法**。这将自动生成正确的查询：

```java
public interface FooDAO extends JpaRepository<Foo, Long> {
    Foo findByName(String name);
}
```

这是一个相对简单的例子。查询语句的创建机制支持更大的[关键字集](https://docs.spring.io/spring-data/data-jpa/docs/current/reference/html/#jpa.query-methods.query-creation)。

如果解析器无法将属性与域对象字段匹配，我们将得到以下异常：

```shell
java.lang.IllegalArgumentException: No property nam found for type class cn.tuyucheng.taketoday.spring.data.persistence.model.Foo
```

### 3.2 手动自定义查询

现在让我们看一下通过@Query注解定义的自定义查询：

```java
@Query("SELECT f FROM Foo f WHERE LOWER(f.name) = LOWER(:name)")
Foo retrieveByName(@Param("name") String name);
```

要对查询的创建进行更细粒度的控制(例如使用命名参数或修改现有查询)，该[参考资料](https://docs.spring.io/spring-data/data-jpa/docs/current/reference/html/#jpa.named-parameters)是一个很好的起点。

## 4. 事务配置

Spring管理的DAO的实际实现确实是隐藏的，因为我们不直接使用它。但是，它是一个足够简单的实现，**SimpleJpaRepository，它使用注解定义事务语义**。

更明确地说，这在类级别使用只读的@Transactional注解，然后为非只读方法覆盖该注解。其余的事务语义是默认的，但这些可以很容易地根据方法手动覆盖。

### 4.1 异常转换仍然存在

现在的问题是：由于Spring Data JPA不依赖于旧的ORM模板(JpaTemplate、HibernateTemplate)，并且自Spring 5以来它们已被删除，我们是否仍要将JPA异常转换为Spring的DataAccessException层次结构？

答案当然是。**通过在DAO上使用@Repository注解仍然可以启用异常转换**。此注解使Spring bean后处理器能够向所有@Repository bean提供在容器中找到的所有PersistenceExceptionTranslator实例的通知，并像以前一样提供异常转换。

让我们通过集成测试来验证异常转换：

```java
@Test
final void whenInvalidEntityIsCreated_thenDataException() {
	assertThrows(DataIntegrityViolationException.class, () -> service.create(new Foo()));
}
```

请记住，**异常转换是通过代理完成的**。为了让Spring能够围绕DAO类创建代理，这些类不能声明为final。

## 5. Spring Data JPA Repository配置

要激活Spring JPA Repository支持，我们可以使用@EnableJpaRepositories注解并指定包含DAO接口的包：

```java
@EnableJpaRepositories(basePackages = "cn.tuyucheng.taketoday.spring.data.persistence.repository")
public class PersistenceConfig {
    // ...
}
```

我们可以对XML配置执行相同的操作：

```xml
<jpa:repositories base-package="cn.tuyucheng.taketoday.spring.data.persistence.repository" />
```

## 6. Java或XML配置

在上一篇文章中，我们已经非常详细地讨论了如何[在Spring中配置JPA](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)。Spring Data还利用了Spring对JPA @PersistenceContext注解的支持。它使用该注解将EntityManager注入到负责创建实际DAO实现JpaRepositoryFactoryBean的Spring工厂bean。

除了已经讨论过的配置之外，如果我们使用XML，我们还需要包含Spring Data XML配置：

```java
@Configuration
@EnableTransactionManagement
@ImportResource("classpath*:*springDataConfig.xml")
public class PersistenceJPAConfig {
    // ...
}
```

## 7. Maven依赖

除了JPA的Maven配置，就像在[之前的文章](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)中一样，我们将添加[spring-data-jpa](https://central.sonatype.com/artifact/org.springframework.data/spring-data-jpa/3.0.3)依赖项：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-jpa</artifactId>
    <version>2.4.0</version>
</dependency>
```

## 8. 使用Spring Boot

**我们还可以使用[spring-boot-starter-data-jpa](https://search.maven.org/search?q=a:spring-boot-starter-data-jpa)依赖项，它会自动为我们配置数据源**。

我们需要确保我们要使用的数据库存在于类路径中。在我们的示例中，我们添加了H2内存数据库：

```xml
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-data-jpa</artifactId>
   <version>2.7.2</version>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.200</version>
</dependency>
```

因此，仅通过添加这些依赖项，我们的应用程序就能够启动并运行了，我们可以将其用于其他数据库操作。

**标准Spring应用程序的显式配置现在作为Spring Boot自动配置的一部分包含在内**。

当然，我们可以通过添加自定义显式配置来修改自动配置。

Spring Boot提供了一种使用application.properties文件中的属性来执行此操作的简单方法。让我们看一个更改连接URL和凭据的示例：

```properties
spring.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=sa
```

## 9. Spring Data JPA的有用工具

所有主要的Java IDE都支持Spring Data JPA。让我们看看Eclipse和IntelliJ IDEA有哪些有用的工具。

**如果你使用Eclipse作为你的IDE，你可以安装[Dali Java Persistence Tools](https://www.eclipse.org/webtools/dali/downloads.php)插件**。这为JPA实体提供了ER图、用于初始化模式的DDL生成以及基本的逆向工程功能。此外，你还可以使用Eclipse Spring Tool Suite(STS)。它有助于验证Spring Data JPA Repository中的查询方法名称。

如果你使用IntelliJ IDEA，则有两种选择。

IntelliJ IDEA Ultimate(旗舰版)支持ER图、用于测试JPQL语句的JPA控制台和有价值的检查。但是这些功能在社区版中不可用。

**为了提高IntelliJ的生产力，你可以安装[JPA Buddy](https://plugins.jetbrains.com/plugin/15075-jpa-buddy)插件**。它提供了许多功能，包括生成JPA实体、Spring Data JPA Repository、DTO、初始化DDL脚本、Flyway版本化迁移、Liquibase变更日志等。此外，JPA Buddy还提供逆向工程的高级工具。

最后，JPA Buddy插件同样适用于社区版和旗舰版。

## 10. 总结

在本文中，我们介绍了使用Spring 5、JPA 2和Spring Data JPA(Spring Data伞形项目的一部分)使用XML和基于Java的配置来配置和实现持久层。

我们讨论了定义更高级的自定义查询、事务语义以及使用新的JPA名称空间进行配置的方法。最终结果是使用Spring对数据访问进行了全新且优雅的处理，几乎没有实际的实现工作。