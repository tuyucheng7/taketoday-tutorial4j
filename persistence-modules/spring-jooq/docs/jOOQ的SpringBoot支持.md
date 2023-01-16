## 1. 概述

本教程是[jOOQ 与 Spring 简介](https://www.baeldung.com/jooq-with-spring)文章的后续，涵盖了在Spring Boot应用程序中使用 jOOQ 的方法。

如果你还没有完成该教程，请查看它并按照有关 Maven 依赖项的第 2 部分和有关代码生成的第 3 部分中的说明进行操作。这将为表示示例数据库中表的Java类生成源代码，包括Author、Book和AuthorBook。

## 2.Maven配置

除了前面教程中的依赖项和插件之外，Maven POM 文件中还需要包含其他几个组件，以使 jOOQ 与Spring Boot一起工作。

### 2.1. 依赖管理

使用Spring Boot最常见的方法是通过在父元素中声明它来继承spring-boot-starter-parent项目。然而，这种方法并不总是适用，因为它强加了一个继承链，这在很多情况下可能不是用户想要的。

本教程使用另一种方法：将依赖管理委托给 Spring Boot。要实现它，只需将以下dependencyManagement元素添加到 POM 文件中：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>2.2.6.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 2.2. 依赖关系

为了让Spring Boot控制 jOOQ，需要声明对spring-boot-starter-jooq工件的依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jooq</artifactId>
    <version>2.2.6.RELEASE</version>
</dependency>
```

请注意，本文重点关注 jOOQ 的开源发行版。如果你想使用商业发行版，请查看官方博客上的[Guide to Using jOOQ's Commercial Distributions withSpring Boot。](https://blog.jooq.org/2019/06/26/how-to-use-jooqs-commercial-distributions-with-spring-boot/)

## 3. Spring Boot配置

### 3.1. 初始启动配置

在我们获得 jOOQ 支持之前，我们将开始使用Spring Boot做准备。

首先，我们将利用 Boot 中的持久性支持和改进以及标准application.properties文件中的数据访问信息。这样，我们可以跳过定义 bean 并通过单独的属性文件使它们可配置。

我们将在此处添加 URL 和凭据来定义我们的嵌入式 H2 数据库：

```plaintext
spring.datasource.url=jdbc:h2:~/jooq
spring.datasource.username=sa
spring.datasource.password=
```

我们还将定义一个简单的启动应用程序：

```java
@SpringBootApplication
@EnableTransactionManagement
public class Application {
    
}
```

我们将保留这个简单而空的，我们将在另一个配置类 – InitialConfiguration中定义所有其他 bean 声明。

### 3.2. Bean配置

现在让我们定义这个InitialConfiguration类：

```java
@Configuration
public class InitialConfiguration {
    // Other declarations
}
```

Spring Boot 已经根据application.properties文件中设置的属性自动生成并配置了dataSource bean ，所以我们不需要手动注册。下面的代码让自动配置的DataSource bean 被注入到一个字段中，并展示了这个 bean 是如何使用的：

```java
@Autowired
private DataSource dataSource;

@Bean
public DataSourceConnectionProvider connectionProvider() {
    return new DataSourceConnectionProvider
      (new TransactionAwareDataSourceProxy(dataSource));
}
```

由于名为transactionManager的 bean也已由Spring Boot自动创建和配置，因此我们无需像上一教程那样声明任何其他DataSourceTransactionManager类型的 bean 即可利用 Spring 事务支持。

DSLContext bean 的创建方式与前面教程的PersistenceContext类中的方式相同：

```java
@Bean
public DefaultDSLContext dsl() {
    return new DefaultDSLContext(configuration());
}
```

最后，需要向DSLContext提供配置实现。由于Spring Boot能够通过类路径上 H2 工件的存在来识别正在使用的 SQL 方言，因此不再需要方言配置：

```java
public DefaultConfiguration configuration() {
    DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
    jooqConfiguration.set(connectionProvider());
    jooqConfiguration
      .set(new DefaultExecuteListenerProvider(exceptionTransformer()));

    return jooqConfiguration;
}
```

## 4. 将Spring Boot与 jOOQ 结合使用

为了更容易理解Spring Boot对 jOOQ 的支持，重用了本教程前传中的测试用例，并对其类级注解进行了一些更改：

```java
@SpringApplicationConfiguration(Application.class)
@Transactional("transactionManager")
@RunWith(SpringJUnit4ClassRunner.class)
public class SpringBootTest {
    // Other declarations
}
```

很明显，Spring Boot没有采用@ContextConfiguration注解，而是使用@SpringApplicationConfiguration来利用SpringApplicationContextLoader上下文加载器来测试应用程序。

插入、更新、删除数据的测试方法与上一篇教程完全相同。请查看有关将 jOOQ 与 Spring 结合使用的文章的第 5 节以获取更多信息。所有测试都应该使用新配置成功执行，证明 jOOQ 完全受Spring Boot支持。

## 5.总结

本教程深入探讨了 jOOQ 与 Spring 的结合使用。它介绍了Spring Boot应用程序利用 jOOQ 以类型安全的方式与数据库交互的方法。