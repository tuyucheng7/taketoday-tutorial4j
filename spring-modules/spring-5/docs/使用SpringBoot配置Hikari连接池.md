## 1. 概述

[Hikari](https://github.com/brettwooldridge/HikariCP)是一个提供连接池机制的JDBC DataSource实现。

与其他实现相比，它有望实现轻量级和[更好的性能](https://github.com/brettwooldridge/HikariCP#jmh-benchmarks-checkered_flag)。有关Hikari的介绍，请参阅[这篇文章](https://www.baeldung.com/hikaricp)。

本快速教程展示了我们如何配置Spring Boot 2或Spring Boot 1应用程序以使用Hikari DataSource。

## 2. 使用Spring Boot 2.x配置Hikari

在Spring Boot 2中，Hikari是默认的DataSource实现。

但是，要使用最新版本，我们需要在pom.xml中显式添加Hikari依赖项：

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>4.0.3</version>
</dependency>
```

这是Spring Boot 1.x的变化：

-   对Hikari的依赖现在自动包含在spring-boot-starter-data-jpa和spring-boot-starter-jdbc中。
-   自动确定DataSource实现的发现算法现在更喜欢Hikari而不是TomcatJDBC(请参阅[参考手册](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/))。

**所以，如果我们想在基于Spring Boot 2.x的应用程序中使用Hikari，我们无事可做，除非我们想使用它的最新版本**。

## 3. 调整Hikari配置参数

Hikari相对于其他DataSource实现的优势之一是它提供了大量配置参数。

我们可以通过使用前缀spring.datasource.hikari并附加Hikari参数的名称来指定这些参数的值：

```properties
spring.datasource.hikari.connectionTimeout=30000
spring.datasource.hikari.idleTimeout=600000
spring.datasource.hikari.maxLifetime=1800000
#...
```

[Hikari GitHub站点](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby)和[Spring文档](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#spring.datasource.hikari)中提供了所有Hikari参数的列表以及很好的解释。

## 4. 使用Spring Boot 1.x配置Hikari

Spring Boot 1.x默认使用[Tomcat JDBC连接池](https://tomcat.apache.org/tomcat-8.5-doc/jdbc-pool.html)。

一旦我们将spring-boot-starter-data-jpa包含到我们的pom.xml中，我们将传递地包含对Tomcat JDBC实现的依赖项。在运行时，Spring Boot会创建一个Tomcat DataSource供我们使用。

要将Spring Boot配置为使用Hikari连接池，我们有两个选择。

### 4.1 Maven依赖

首先，我们需要在我们的pom.xml中包含对Hikari的依赖：

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>4.0.3</version>
</dependency>
```

最新版本可以在[Maven Central](https://search.maven.org/search?q=a:HikariCP)上找到。

### 4.2 显式配置

告诉Spring Boot使用Hikari的最安全方法是显式配置DataSource实现。

为此，我们只需**将属性spring.datasource.type设置为我们要使用的DataSource实现的完全限定名称**：

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

### 4.3 删除Tomcat JDBC依赖项

第二种选择是让Spring Boot自己找到Hikari DataSource实现。

**如果Spring Boot在类路径中找不到Tomcat DataSource，接下来它会自动寻找Hikari DataSource**。[参考手册](https://docs.spring.io/spring-boot/docs/1.5.15.RELEASE/reference/htmlsingle/#boot-features-connect-to-production-database)中描述了发现算法。

要从类路径中删除Tomcat连接池，我们可以在我们的pom.xml中排除它：

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

## 5. 总结

在本文中，我们在Spring Boot 2.x应用程序中配置了Hikari DataSource实现，我们还学习了如何利用Spring Boot的自动配置。

我们还查看了使用Spring Boot 1.x时配置Hikari所需的更改。

[此处](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/spring-4)提供了Spring Boot 1.x示例的代码，[此处](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/spring-5)提供了Spring Boot 2.x示例的代码。