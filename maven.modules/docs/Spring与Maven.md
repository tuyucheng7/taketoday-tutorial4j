## 1. 概述

本教程说明了如何**通过Maven设置Spring依赖项**，可以在[Maven Central](https://search.maven.org/search?q=org.springframework)上找到最新的Spring版本。

## 2. Maven的基本Spring依赖

Spring被设计为高度模块化的，使用Spring框架的一部分不应该也不需要另一部分。例如，基本的Spring Context可以没有Persistence或MVC Spring库。

让我们从一个基本的Maven设置开始，它只使用**spring-context依赖**：

```xml
<properties>
    <org.springframework.version>5.2.8.RELEASE</org.springframework.version>
</properties>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>${org.springframework.version}</version>
    <scope>runtime</scope>
</dependency>
```

这种依赖关系spring-context定义了实际的Spring依赖注入容器，并且具有少量的依赖关系：spring-core、spring-expression、spring-aop和spring-beans。这些通过启用对一些**核心Spring技术**的支持来增强容器：核心Spring实用程序、[Spring表达式语言](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#expressions)(SpEL)、[面向切面编程](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#aop)支持和[Java Beans机制](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#beans-definition)。

请注意，我们在**runtime范围**内定义依赖关系，这将确保在任何Spring特定的API上都没有编译时依赖关系。对于更高级的用例，runtime范围可能会从某些选定的Spring依赖项中移除，但对于更简单的项目，**无需针对Spring进行编译以充分利用该框架**。

另外请注意，JDK 8是Spring 5.2所需的最低Java版本，它还支持JDK 11作为当前的LTS分支和JDK 13作为最新的OpenJDK版本。

## 3. 使用Maven的Spring持久化

现在让我们看看**持久层Spring依赖项**，主要是spring-orm：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-orm</artifactId>
    <version>${org.springframework.version}</version>
</dependency>
```

这附带了Hibernate和JPA支持，例如HibernateTemplate和JpaTemplate，以及一些额外的、与持久层相关的依赖项：spring-jdbc和spring-tx。

JDBC数据访问库定义了[Spring JDBC支持](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/data-access.html#jdbc)以及JdbcTemplate，而spring-tx代表了极其灵活的[事务管理抽象](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/data-access.html#transaction)。

## 4. 使用Maven的Spring MVC

要使用Spring Web和Servlet支持，除了上面的核心依赖项之外，还需要在pom中包含两个依赖项：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>${org.springframework.version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>${org.springframework.version}</version>
</dependency>
```

spring-web依赖项包含适用于Servlet和Portlet环境的通用Web特定实用程序，而**spring-webmvc启用对Servlet环境的MVC支持**。

由于spring-webmvc具有spring-web作为依赖项，因此在使用spring-webmvc时不需要显式定义spring-web。

从Spring 5.0开始，**为了支持响应式堆栈(reactive-stack)web框架，我们可以添加对**[Spring WebFlux](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/web-reactive.html#spring-webflux)**的依赖**：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webflux</artifactId>
    <version>${org.springframework.version}</version>
</dependency>
```

## 5. 使用Maven的Spring Security

Spring Security Maven依赖项在[Spring Security与Maven](https://www.baeldung.com/spring-security-with-maven)一文中进行了深入讨论。

## 6. 使用Maven的Spring测试

可以通过以下依赖项将Spring测试框架包含在项目中：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-test</artifactId>
    <version>${spring.version}</version>
    <scope>test</scope>
</dependency>
```

使用Spring 5，我们也可以执行[并发测试](https://www.baeldung.com/spring-5-concurrent-tests)。

## 7. 使用里程碑

Spring的发布(release)版本托管在Maven Central上，但是，如果项目需要使用里程碑(milestone)版本，则需要在pom中添加自定义Spring仓库：

```xml
<repositories>
    <repository>
        <id>repository.springframework.maven.milestone</id>
        <name>Spring Framework Maven Milestone Repository</name>
        <url>http://repo.spring.io/milestone/</url>
    </repository>
</repositories>
```

一旦定义了这个仓库，项目就可以定义依赖关系，例如：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>5.3.0-M1</version>
</dependency>
```

## 8. 使用快照

与里程碑类似，快照(snapshot)托管在自定义仓库中：

```xml
<repositories>
    <repository>
        <id>repository.springframework.maven.snapshot</id>
        <name>Spring Framework Maven Snapshot Repository</name>
        <url>http://repo.spring.io/snapshot/</url>
    </repository>
</repositories>
```

在pom.xml中启用SNAPSHOT仓库后，可以引用以下依赖项：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>4.0.3.BUILD-SNAPSHOT</version>
</dependency>
```

以及对于5.x：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>5.3.0-SNAPSHOT</version>
</dependency>
```

## 9. 总结

本文讨论将Spring与Maven结合使用的实际细节，这里介绍的Maven依赖项当然是一些主要的依赖项，其他几个可能值得一提但尚未完成。尽管如此，这应该是在项目中使用Spring的一个良好起点。