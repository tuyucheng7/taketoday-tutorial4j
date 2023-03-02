## 1. 概述

在本文中，我们将讨论Spring org.springframework.beans.factory.BeanDefinitionStoreException–当bean定义无效时，这通常是BeanFactory的责任，该bean的加载有问题。本文将讨论此异常的最常见原因以及每个原因的解决方案。

## 2. 原因：java.io.FileNotFoundException

BeanDefinitionStoreException可能是由底层IOException引起的多种可能原因：

### 2.1 IOException从ServletContext资源解析XML文档

这通常发生在SpringWeb应用程序中，当在SpringMVC的web.xml中设置DispatcherServlet时：

```xml
<servlet>  
   <servlet-name>mvc</servlet-name>  
   <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>  
</servlet>
```

默认情况下，Spring将在Web应用程序的/WEB-INF目录中查找名为springMvcServlet-servlet.xml的文件。

如果此文件不存在，则会抛出以下异常：

```bash
org.springframework.beans.factory.BeanDefinitionStoreException: 
Ioexception Parsing Xml Document from Servletcontext Resource [/WEB-INF/mvc-servlet.xml]; 
nested exception is java.io.FileNotFoundException: 
Could not open ServletContext resource [/WEB-INF/mvc-servlet.xml]
```

解决方案当然是确保mvc-servlet.xml文件确实存在于/WEB-INF下；如果没有，则可以创建一个示例：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans 
   xmlns="http://www.springframework.org/schema/beans"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xsi:schemaLocation="
      http://www.springframework.org/schema/beans 
      http://www.springframework.org/schema/beans/spring-beans-3.2.xsd" >

</beans>
```

### 2.2 IOException从类路径资源解析XML文档

当应用程序中的某些内容指向不存在的XML资源或未放置在应有的位置时，通常会发生这种情况。

指向这样的资源可能以多种方式发生。

使用例如Java配置，这可能看起来像：

```java
@Configuration
@ImportResource("beans.xml")
public class SpringConfig {...}
```

在XML中，这将是：

```xml
<import resource="beans.xml"/>
```

或者甚至通过手动创建Spring XML上下文：

```java
ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
```

如果文件不存在，所有这些都会导致相同的异常：

```bash
org.springframework.beans.factory.BeanDefinitionStoreException: 
Ioexception Parsing Xml Document from Servletcontext Resource [/beans.xml]; 
nested exception is java.io.FileNotFoundException: 
Could not open ServletContext resource [/beans.xml]
```

解决方案是创建文件并将其放在项目的/src/main/resources目录下——这样，该文件将存在于类路径中，并且将被Spring找到和使用。

## 3. 原因：无法解析占位符

当Spring尝试解析属性但由于多种可能的原因之一而无法解析时，会发生此错误。

但首先，该属性的用法——这可能会在XML中使用：

```xml
... value="${some.property}" ...
```

该属性也可以用在Java代码中：

```java
@Value("${some.property}")
private String someProperty;
```

首先要检查的是属性的名称是否与属性定义相匹配；在此示例中，我们需要定义以下属性：

```properties
some.property=someValue
```

然后，我们需要检查属性文件在Spring中的定义位置——这在我的[Properties with Spring Tutorial](https://www.baeldung.com/properties-with-spring)中有详细描述。一个好的最佳实践是将所有属性文件放在应用程序的/src/main/resources目录下，并通过以下方式加载它们：

```java
"classpath:app.properties"
```

从显而易见的地方继续-Spring无法解析属性的另一个可能原因是Spring上下文中可能有多个PropertyPlaceholderConfigurer bean(或多个属性占位符元素)

如果是这种情况，那么解决方案要么将它们折叠成一个，要么在父上下文中使用ignoreUnresolvablePlaceholders配置一个。

## 4. 原因：java.lang.NoSuchMethodError

这个错误有多种形式-其中一种比较常见的是：

```bash
org.springframework.beans.factory.BeanDefinitionStoreException:
Unexpected exception parsing XML document from ServletContext resource [/WEB-INF/mvc-servlet.xml];
nested exception is java.lang.NoSuchMethodError:
org.springframework.beans.MutablePropertyValues.add (Ljava/lang/String;Ljava/lang/Object;)
Lorg/springframework/beans/MutablePropertyValues;
```

当类路径上有多个Spring版本时，通常会发生这种情况。在项目类路径中不小心有一个旧版本的Spring比人们想象的更常见——我在[Spring Security with Maven文章](https://www.baeldung.com/spring-security-with-maven#maven_problem)中描述了这个问题和解决方案。

简而言之，这个错误的解决方案很简单——检查类路径上的所有Spring jar并确保它们都具有相同的版本——并且那个版本是3.0或更高版本。

同样，异常并不局限于MutablePropertyValues bean-同样的问题还有其他几种形式，由相同的版本不一致引起：

```bash
org.springframework.beans.factory.BeanDefinitionStoreException:
Unexpected exception parsing XML document from class path resource [/WEB-INF/mvc-servlet.xml];
- nested exception is java.lang.NoSuchMethodError:
org.springframework.util.ReflectionUtils.makeAccessible(Ljava/lang/reflect/Constructor;)V
```

## 5. 原因：java.lang.NoClassDefFoundError

与Maven和现有Spring依赖项类似的常见问题是：

```bash
org.springframework.beans.factory.BeanDefinitionStoreException:
Unexpected exception parsing XML document from ServletContext resource [/WEB-INF/mvc-servlet.xml];
nested exception is java.lang.NoClassDefFoundError: 
org/springframework/transaction/interceptor/TransactionInterceptor
```

当在XML配置中配置事务功能时会发生这种情况：

```xml
<tx:annotation-driven/>
```

NoClassDefFoundError意味着类路径中不存在Spring Transactional支持(即spring-tx)。

解决方案很简单——需要在Maven pom中定义spring-tx：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-tx</artifactId>
    <version>4.1.0.RELEASE</version>
</dependency>
```

当然，这不仅限于事务功能-如果缺少AOP，也会抛出类似的错误：

```bash
Exception in thread "main" org.springframework.beans.factory.BeanDefinitionStoreException: 
Unexpected exception parsing XML document from class path resource [/WEB-INF/mvc-servlet.xml]; 
nested exception is java.lang.NoClassDefFoundError: 
org/aopalliance/aop/Advice
```

现在需要的jar是：spring-aop(和隐含的aopalliance)：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aop</artifactId>
    <version>4.1.0.RELEASE</version>
</dependency>
```

## 6. 总结

在这篇文章的最后，我们应该有一个清晰的地图来导航可能导致Bean定义存储异常的各种原因和问题，并很好地掌握如何解决所有这些问题。