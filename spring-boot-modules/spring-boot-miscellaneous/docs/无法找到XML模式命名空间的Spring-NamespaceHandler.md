## 1. 问题

本文将讨论Spring中最常见的配置问题之一——找不到Spring命名空间之一的命名空间处理程序。大多数时候，这意味着类路径中缺少一个特定的Springjar——所以让我们来看看这些缺少的模式可能是什么，以及每个模式缺少的依赖项是什么。

## 延伸阅读：

## [Spring 中基于XML的注入](https://www.baeldung.com/spring-xml-injection)

了解如何使用Spring执行基于XML的注入。

[阅读更多](https://www.baeldung.com/spring-xml-injection)→

## [web.xml 与Spring的初始化程序](https://www.baeldung.com/spring-xml-vs-java-config)

Spring 中XML和Java配置的快速实用指南。

[阅读更多](https://www.baeldung.com/spring-xml-vs-java-config)→

## [顶级Spring框架面试问题](https://www.baeldung.com/spring-interview-questions)

快速讨论在求职面试中可能出现的有关Spring框架的常见问题。

[阅读更多](https://www.baeldung.com/spring-interview-questions)→

## 2. http://www.springframework.org/schema/security

[安全命名空间](http://static.springsource.org/spring-security/site/docs/3.1.x/reference/ns-config.html)不可用是迄今为止在实践中遇到的最广泛的问题：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans:beans xmlns="http://www.springframework.org/schema/security" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xmlns:beans="http://www.springframework.org/schema/beans"
    xsi:schemaLocation="
        http://www.springframework.org/schema/security 
        http://www.springframework.org/schema/security/spring-security-3.2.xsd
        http://www.springframework.org/schema/beans 
        http://www.springframework.org/schema/beans/spring-beans-4.1.xsd">

</beans:beans>
```

这导致以下异常：

```bash
org.springframework.beans.factory.parsing.BeanDefinitionParsingException: 
Configuration problem: 
Unable to locate Spring NamespaceHandler for XML schema namespace 
[http://www.springframework.org/schema/security]
Offending resource: class path resource [securityConfig.xml]
```

解决方案很简单——项目的类路径中缺少spring-security-config依赖项：

```xml
<dependency> 
   <groupId>org.springframework.security</groupId>
   <artifactId>spring-security-config</artifactId>
   <version>3.2.5.RELEASE</version>
</dependency>
```

这会将正确的命名空间处理程序(在本例中为SecurityNamespaceHandler )放在类路径上，并准备好解析安全命名空间中的元素。

[完整的Spring Security设置的完整Maven配置可以在我之前的Maven教程](https://www.baeldung.com/spring-security-with-maven)中找到。

## 3. http://www.springframework.org/schema/aop

在类路径上没有必要的 aop spring 库的情况下使用aop命名空间时会出现同样的问题：

```bash
<beans 
    xmlns="http://www.springframework.org/schema/beans" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xmlns:aop="http://www.springframework.org/schema/aop"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans 
        http://www.springframework.org/schema/beans/spring-beans-4.1.xsd
        http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop-4.1.xsd">

</beans>
```

确切的例外：

```bash
org.springframework.beans.factory.parsing.BeanDefinitionParsingException: 
Configuration problem: 
Unable to locate Spring NamespaceHandler for XML schema namespace 
[http://www.springframework.org/schema/aop]
Offending resource: ServletContext resource [/WEB-INF/webConfig.xml]
```

解决方案类似——需要将spring-aop jar 添加到项目的类路径中：

```xml
<dependency>
   <groupId>org.springframework</groupId>
   <artifactId>spring-aop</artifactId>
   <version>4.1.0.RELEASE</version>
</dependency>
```

在这种情况下，AopNamespaceHandler将在添加新依赖项后出现在类路径中。

## 4. http://www.springframework.org/schema/tx

使用事务命名空间——一个用于配置事务语义的小但非常有用的命名空间：

```xml
<beans 
    xmlns="http://www.springframework.org/schema/beans" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xmlns:tx="http://www.springframework.org/schema/tx"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans 
        http://www.springframework.org/schema/beans/spring-beans-4.1.xsd
        http://www.springframework.org/schema/tx
        http://www.springframework.org/schema/tx/spring-tx-4.1.xsd">

</beans>
```

如果正确的 jar 不在类路径中，也会导致异常：

```bash
org.springframework.beans.factory.parsing.BeanDefinitionParsingException: 
Configuration problem: 
Unable to locate Spring NamespaceHandler for XML schema namespace
[http://www.springframework.org/schema/tx]
Offending resource: class path resource [daoConfig.xml]
```

这里缺少的依赖项是spring-tx：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-tx</artifactId>
    <version>4.1.0.RELEASE</version>
</dependency>
```

现在，正确的NamspaceHandler——即TxNamespaceHandler——将出现在类路径上，允许使用XML和注解进行声明式事务管理。

## 5. http://www.springframework.org/schema/mvc

前进到mvc命名空间：

```xml
<beans 
    xmlns="http://www.springframework.org/schema/beans" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xmlns:tx="http://www.springframework.org/schema/mvc"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans 
        http://www.springframework.org/schema/beans/spring-beans-4.1.xsd
        http://www.springframework.org/schema/mvc
        http://www.springframework.org/schema/mvc/spring-mvc-4.1.xsd">

</beans>
```

缺少依赖项将导致以下异常：

```bash
org.springframework.beans.factory.parsing.BeanDefinitionParsingException: 
Configuration problem: 
Unable to locate Spring NamespaceHandler for XML schema namespace
[http://www.springframework.org/schema/mvc]
Offending resource: class path resource [webConfig.xml]
```

在这种情况下，缺少的依赖项是spring-mvc：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>4.1.0.RELEASE</version>
</dependency>
```

将其添加到pom.xml会将MvcNamespaceHandler添加到类路径——允许项目使用命名空间配置 MVC 语义。

## 六，总结

最后，如果你使用 Eclipse 来管理 Web 服务器和部署——请确保[项目的 Deployment Assembly 部分已正确配置](http://stackoverflow.com/questions/4777026/classnotfoundexception-dispatcherservlet-when-launching-tomcat-maven-dependenci/4777496#4777496)——即Maven依赖项实际上包含在部署时的类路径中。

本教程讨论了“无法定位XML模式名称空间的SpringNamespaceHandler”问题的常见疑点，并为每次出现的问题提供了解决方案。