## 1. 概述

在本文中，我们将解释如何使用**Maven设置Spring Security**并回顾使用Spring Security依赖项的特定用例，你可以在[Maven Central](https://search.maven.org/search?q=g:org.springframework.security)上找到最新的Spring Security版本。

这是之前的[Spring与Maven](https://www.baeldung.com/spring-with-maven)文章的后续，因此对于非安全性Spring依赖项，这是开始的地方。

## 2. 使用Maven的Spring Security

### 2.1 spring-security-core

核心Spring Security支持**spring-security-core**包含身份验证和访问控制功能，对于使用Spring Security的所有项目，必须包含此依赖项。

此外，spring-security-core支持独立(非Web)应用程序、方法级安全性和JDBC：

```xml
<properties>
    <spring-security.version>5.3.4.RELEASE</spring-security.version>
    <spring.version>5.2.8.RELEASE</spring.version>
</properties>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-core</artifactId>
    <version>${spring-security.version}</version>
</dependency>
```

请注意，**Spring和Spring Security的发布时间表不同，因此版本号之间并不总是1:1匹配**。

如果你使用的是旧版本的Spring，同样非常重要的是要理解一个事实，即**Spring Security 4.1.x并不依赖于Spring 4.1.x版本！**例如，当[Spring Security 4.1.0](https://mvnrepository.com/artifact/org.springframework.security/spring-security-core/4.1.0.RELEASE)发布时，Spring核心框架已经是4.2.x，因此将该版本作为其编译依赖项包含在内。计划是在未来的版本中更紧密地对齐这些依赖项，有关更多详细信息，请参阅[此JIRA](https://jira.springsource.org/browse/SEC-2123)。但就目前而言，这具有实际意义，我们将在接下来研究。

### 2.2 spring-security-web

**要添加对Spring Security的Web支持，我们需要spring-security-web依赖项**：

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-web</artifactId>
    <version>${spring-security.version}</version>
</dependency>
```

这包含在Servlet环境中启用URL访问控制的过滤器和相关的网络安全基础设施。

### 2.3 Spring Security和旧的Spring Core依赖问题 

这个新的依赖关系也**给Maven依赖关系图带来了一个问题**，如上所述，Spring Security jar不依赖于最新的Spring核心jar(而是依赖于以前的版本)，这可能会导致这些较旧的依赖项位于类路径的顶部，而不是较新的5.x Spring工件。

要理解为什么会发生这种情况，我们需要看看Maven是如何解决冲突的。在版本冲突的情况下，Maven将选择最接近树根的jar。例如，spring-core由spring-orm(5.0.0.RELEASE版本)和spring-security-core(5.0.2.RELEASE版本)定义。因此，在这两种情况下，spring-jdbc都定义在距离我们项目的根pom深度为1的位置。正因为如此，在我们自己的pom中定义spring-orm和spring-security-core的顺序实际上很重要，第一个将优先考虑，因此我们**最终可能会在我们的类路径上得到任何一个版本**。

为了解决这个问题，我们必须**在我们自己的pom中显式定义一些Spring依赖项**，而不是依赖于隐式的Maven依赖项解析机制。这样做会将该特定依赖项置于距离我们的pom深度为0(如它在pom本身中定义的那样)的位置，因此它将优先。以下所有内容都属于同一类别，并且都需要直接明确定义，或者对于多模块项目，在父级的dependencyManagement元素中显式定义：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>${spring-version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>${spring-version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>${spring-version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-beans</artifactId>
    <version>${spring-version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aop</artifactId>
    <version>${spring-version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-tx</artifactId>
    <version>${spring-version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-expression</artifactId>
    <version>${spring-version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>${spring-version}</version>
</dependency>
```

### 2.4 spring-security-config和其他

要使用丰富的Spring Security XML命名空间和注解，我们需要spring-security-config依赖项：

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
    <version>${spring-security.version}</version>
</dependency>
```

最后，LDAP、ACL、CAS、OAuth和OpenID支持在Spring Security中有它们自己的依赖项：spring-security-ldap、spring-security-acl、spring-security-cas、spring-security-oauth和spring-security-openid。

## 3. 使用Spring Boot

使用Spring Boot时，[spring-boot-starter-security](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-security)启动器将自动包含所有依赖项，例如spring-security-core、spring-security-web和spring-security-config等：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
    <version>2.3.3.RELEASE</version>
</dependency>

```

由于Spring Boot将自动为我们管理所有依赖项，这也将摆脱前面提到的Spring Security和旧的Spring Core依赖项问题。

## 4. 使用快照和里程碑

Spring Security[里程碑](https://docs.spring.io/spring-security/site/docs/5.1.1.RELEASE/reference/html/get-spring-security.html)以及快照在Spring提供的自定义Maven仓库中可用，有关如何配置这些的更多详细信息，请参阅[如何使用快照和里程碑](https://www.baeldung.com/spring-with-maven#milestones)。

## 5. 总结

在这个快速教程中，我们讨论了将Spring Security与Maven一起使用的实用细节，这里介绍的Maven依赖项当然是一些主要的依赖项，还有其他几个可能值得一提但尚未完成的其他依赖项。尽管如此，这应该是在支持Maven的项目中使用Spring的良好起点。