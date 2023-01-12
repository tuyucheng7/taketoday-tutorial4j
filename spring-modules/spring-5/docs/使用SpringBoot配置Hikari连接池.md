## 1. 概述

[Hikari](https://github.com/brettwooldridge/HikariCP)是一个 提供连接池机制 的 JDBC DataSource实现。

与其他实现相比，它有望实现轻量级和[更好的性能](https://github.com/brettwooldridge/HikariCP#jmh-benchmarks-checkered_flag)。有关 Hikari 的介绍，请参阅[这篇文章](https://www.baeldung.com/hikaricp)。

本快速教程展示了我们如何配置Spring Boot2 或Spring Boot1 应用程序以使用 Hikari DataSource。

## 延伸阅读：

## [JPA 中的一对一关系](https://www.baeldung.com/jpa-one-to-one)

学习三种不同的方式来维护与 JPA 的一对一关系。

[阅读更多](https://www.baeldung.com/jpa-one-to-one)→

## [带休眠功能的 Spring Boot](https://www.baeldung.com/spring-boot-hibernate)

集成Spring Boot和 Hibernate/JPA 的快速、实用的介绍。

[阅读更多](https://www.baeldung.com/spring-boot-hibernate)→

## 2. 使用Spring Boot2.x 配置 Hikari

在Spring Boot2 中，Hikari 是默认的 DataSource 实现。

但是，要使用最新版本，我们需要在 pom.xml 中显式添加 Hikari 依赖项：

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>4.0.3</version>
</dependency>
```

这是Spring Boot1.x 的变化：

-   对 Hikari 的依赖现在自动包含在spring-boot-starter-data-jpa和spring-boot-starter-jdbc中。
-   自动确定DataSource实现的发现算法现在更喜欢 Hikari 而不是 TomcatJDBC(请参阅[参考手册](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/))。

所以，如果我们想在基于Spring Boot2.x 的应用程序中使用 Hikari，我们无事可做，除非我们想使用它的最新版本。


## 3. 调整 Hikari 配置参数

Hikari 相对于其他DataSource实现的优势之一是它提供了大量配置参数。

我们可以通过使用前缀spring.datasource.hikari并附加 Hikari 参数的名称来指定这些参数的值 ：

```xml
spring.datasource.hikari.connectionTimeout=30000 
spring.datasource.hikari.idleTimeout=600000 
spring.datasource.hikari.maxLifetime=1800000 
...
```

[Hikari GitHub 站点](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby) 和[Spring 文档](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#spring.datasource.hikari)中提供了所有 Hikari 参数的列表以及很好的解释。

## 4. 使用Spring Boot1.x 配置 Hikari

Spring Boot 1.x 默认使用[Tomcat JDBC 连接池](https://tomcat.apache.org/tomcat-8.5-doc/jdbc-pool.html) 。

一旦我们将spring-boot-starter-data-jpa 包含到我们的pom.xml中，我们将传递地包含对 Tomcat JDBC 实现的依赖项。在运行时，Spring Boot 会创建一个 Tomcat DataSource 供我们使用。

要将Spring Boot配置为使用 Hikari 连接池，我们有两个选择。

### 4.1. Maven 依赖

首先，我们需要在我们的pom.xml中包含对 Hikari 的依赖：

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>4.0.3</version>
</dependency>
```

最新版本可以在[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"com.zaxxer" AND a%3A"HikariCP")上找到。

### 4.2. 显式配置

告诉Spring Boot使用 Hikari 的最安全方法是显式配置 DataSource 实现。

为此，我们只需将属性spring.datasource.type 设置为我们要使用的DataSource实现的完全限定名称：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(
    properties = "spring.datasource.type=com.zaxxer.hikari.HikariDataSource"
)
public class HikariIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void hikariConnectionPoolIsConfigured() {
        assertEquals("com.zaxxer.hikari.HikariDataSource", dataSource.getClass().getName());
    }
}
```

### 4.3. 删除 Tomcat JDBC 依赖项

第二种选择是让Spring Boot自己找到 Hikari DataSource实现。

如果Spring Boot在类路径中找不到 Tomcat DataSource ，接下来它会自动寻找 Hikari DataSource。[参考手册](https://docs.spring.io/spring-boot/docs/1.5.15.RELEASE/reference/htmlsingle/#boot-features-connect-to-production-database) 中描述了发现算法。

要从类路径中删除 Tomcat 连接池，我们可以在我们的pom.xml中排除它：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.apache.tomcat</groupId>
            <artifactId>tomcat-jdbc</artifactId>
         </exclusion>
     </exclusions>
</dependency>
```

现在，上一节中的测试也可以在不设置spring.datasource.type属性的情况下运行。

## 5.总结

在本文中，我们在Spring Boot2.x 应用程序中配置了 Hikari DataSource实现。我们还学习了如何利用Spring Boot的自动配置。

我们还查看了使用Spring Boot1.x 时配置 Hikari 所需的更改。

此处提供了Spring Boot1.x 示例的代码，[此处提供](https://github.com/eugenp/tutorials/tree/master/spring-4)[了](https://github.com/eugenp/tutorials/tree/master/spring-5)Spring Boot 2.x 示例的代码 。