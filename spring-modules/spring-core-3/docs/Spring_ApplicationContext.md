## 1. 概述

在本教程中，我们将详细介绍Spring的ApplicationContext接口。

## 2. ApplicationContext接口

Spring框架的主要特性之一是IoC(控制反转)容器。Spring IoC容器负责管理应用程序的对象。它使用依赖注入来实现控制反转。

BeanFactory和ApplicationContext接口代表Spring IoC容器。
这里，BeanFactory是访问Spring容器的顶层接口。它提供了管理bean的基本功能。

另一方面，ApplicationContext是BeanFactory的子接口。因此，它提供了BeanFactory之外的额外功能。

此外，**它提供了更多企业级应用特定的功能**。
ApplicationContext的重要特性是**消息解析、支持国际化、发布事件和应用层特定的上下文**。
这就是我们使用它作为默认Spring容器的原因。

## 3. 什么是Spring Bean？

在我们深入介绍ApplicationContext容器之前，了解Spring bean很重要。
**在Spring中，bean是Spring容器实例化、组装和管理的对象**。

那么我们应该将应用程序的所有对象都配置为Spring bean吗？作为最佳实践，我们不应该这样做。

一般来说，根据Spring文档，我们应该为Service层对象、数据访问对象(DAO)、表示对象、
基础设施对象(如Hibernate SessionFactories、JMS队列等)定义bean。

此外，通常情况下，我们不应该在容器中配置细粒度的域对象。创建和加载域对象通常是DAO和业务逻辑的职责。

现在，让我们定义一个简单的Java类，我们在本文中将其用作Spring bean：

```java
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    // getters and setters ...
}
```

## 4. 在容器中配置bean

众所周知，ApplicationContext的主要工作是管理bean。

因此，应用程序必须向ApplicationContext容器提供bean的配置。
Spring bean配置由一个或多个bean definitions组成。此外，Spring支持不同的配置bean的方式。

### 4.1 基于Java的配置

首先，我们从基于Java的配置开始，因为它是最流行且最受青睐的bean配置方式。它从Spring 3.0开始提供。

Java配置通常使用@Configuration类中的@Bean注解方法。
方法上的@Bean注解表明该方法创建了一个Spring bean。此外，使用@Configuration注解的类表明它包含Spring bean配置。

现在让我们创建一个配置类来将我们的AccountService类定义为Spring bean：

```java

@Configuration
public class AccountConfig {

    @Bean
    public AccountService accountService() {
        return new AccountService(accountRepository());
    }

    @Bean
    public AccountRepository accountRepository() {
        return new AccountRepository();
    }
}
```

### 4.2 基于注解的配置

Spring 2.5引入了基于注解的配置，作为在Java中启用bean配置的第一步。

在这种方法中，我们首先通过XML配置启用基于注解的配置。然后我们在Java类、方法、构造函数或字段上使用一组注解来配置bean。
这些注解包含@Component、@Controller、@Service、@Repository、@Autowired和@Qualifier。

值得注意的是，我们也将这些注解与基于Java的配置一起使用。另外值得一提的是，Spring在每个版本中都不断地为这些注解添加更多功能。

现在让我们看一个简单的配置示例。

首先，我们将创建XML配置user-bean-config.xml以启用注解功能：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans 
	  http://www.springframework.org/schema/beans/spring-beans.xsd 
	  http://www.springframework.org/schema/context 
	  http://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>
    <context:component-scan base-package="cn.tuyucheng.taketoday.applicationcontext"/>
</beans>
```

在这里，**annotation-config标签启用基于注解的映射**。component-scan标签告诉Spring在哪里寻找带注解的类。

其次，我们将创建UserService类并使用@Component注解将其定义为Spring bean：

```java

@Component
public class UserService {
    // user service code
}
```

然后我们将编写一个简单的测试用例来测试这个配置：

```java
class ApplicationContextUnitTest {

    @Test
    void givenClasspathXmlAppContext_whenAnnotationConfig_thenMappingSuccess() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationcontext/user-bean-config.xml");
        UserService userService = context.getBean(UserService.class);
        assertNotNull(userService);
        context.close();
    }
}
```

### 4.3 基于XML的配置

最后，让我们看看基于XML的配置。这是在Spring中配置bean的传统方式。

显然，在这种方法中，我们**在一个XML配置文件中进行所有bean映射**。

因此，让我们创建一个XML配置文件account-bean-config.xml，并为我们的AccountService类定义bean：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans 
	  http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="accountService" class="cn.tuyucheng.taketoday.applicationcontext.AccountService">
        <constructor-arg name="accountRepository" ref="accountRepository"/>
    </bean>

    <bean id="accountRepository" class="cn.tuyucheng.taketoday.applicationcontext.AccountRepository"/>
</beans>
```

## 5. ApplicationContext的类型

Spring提供了适合不同需求的不同类型的ApplicationContext容器。
这些是ApplicationContext接口的实现。因此，让我们来看看ApplicationContext的一些常见类型。

### 5.1 AnnotationConfigApplicationContext

首先，让我们看一下AnnotationConfigApplicationContext类，它是在Spring 3.0中引入的。
它可以将带有@Configuration、@Component和JSR-330元数据注解的类作为输入参数。

因此，让我们来看一个简单的示例，将AnnotationConfigApplicationContext容器与基于Java的配置一起使用：

```java
class ApplicationContextUnitTest {

    @Test
    void givenAnnotationConfigAppContext_whenSpringConfig_thenMappingSuccess() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AccountConfig.class);
        AccountService accountService = context.getBean(AccountService.class);
        assertNotNull(accountService);
        assertNotNull(accountService.getAccountRepository());
        context.close();
    }
}
```

### 5.2 AnnotationConfigWebApplicationContext

AnnotationConfigWebApplicationContext是**AnnotationConfigApplicationContext的基于Web的变体**。

当我们在web.xml文件中配置Spring的ContextLoaderListener servlet监听器或Spring MVC DispatcherServlet时，我们可能会使用这个类。

此外，从Spring 3.0开始，我们还可以通过编程方式配置此应用程序上下文容器。我们需要做的就是实现WebApplicationInitializer接口：

```java
public class MyWebApplicationInitializer implements WebApplicationInitializer {

    public void onStartup(ServletContext container) throws ServletException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AccountConfig.class);
        context.setServletContext(container);
        ServletRegistration.Dynamic servlet = container.addServlet("dispatcher", new DispatcherServlet(context));
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");
    }
}
```

### 5.3 XmlWebApplicationContext

如果我们**在Web应用程序中使用基于XML的配置**，我们可以使用XmlWebApplicationContext类。

事实上，配置这个容器就像AnnotationConfigWebApplicationContext类，
也就是说我们可以在web.xml中配置它，或者实现WebApplicationInitializer接口：

```java
public class MyXmlWebApplicationInitializer implements WebApplicationInitializer {

    public void onStartup(ServletContext container) throws ServletException {
        XmlWebApplicationContext context = new XmlWebApplicationContext();
        context.setConfigLocation("/WEB-INF/spring/applicationContext.xml");
        context.setServletContext(container);
        ServletRegistration.Dynamic servlet = container.addServlet("dispatcher", new DispatcherServlet(context));
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");
    }
}
```

### 5.4 FileSystemXMLApplicationContext

我们**使用FileSystemXMLApplicationContext类从文件系统或URL加载基于XML的Spring配置文件**。
当我们需要以编程方式加载ApplicationContext时，此类很有用。通常，测试工具和独立应用程序是这方面的可能用例。

例如，让我们看看如何创建这个Spring容器并为基于XML的配置加载bean：

```java
class ApplicationContextUnitTest {

    @Test
    void givenFileXmlAppContext_whenXMLConfig_thenMappingSuccess() {
        String path = "D:/java-workspace/intellij-workspace/java-development-practice/spring-framework-core-4/src/test/resources/applicationcontext/user-bean-config.xml";
        FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(path);
        AccountService accountService = context.getBean("accountService", AccountService.class);
        assertNotNull(accountService);
        assertNotNull(accountService.getAccountRepository());
        context.close();
    }
}
```

### 5.5 ClassPathXmlApplicationContext

如果我们想从类路径加载XML配置文件，我们可以使用ClassPathXmlApplicationContext类。
与FileSystemXMLApplicationContext类似，它对于测试工具以及嵌入在JAR中的应用程序上下文很有用。

以下是使用这个类的例子：

```java
class ApplicationContextUnitTest {

    @Test
    void givenClasspathXmlAppContext_whenXMLConfig_thenMappingSuccess() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationcontext/account-bean-config.xml");
        AccountService accountService = context.getBean("accountService", AccountService.class);
        assertNotNull(accountService);
        assertNotNull(accountService.getAccountRepository());
        context.close();
    }
}
```

## 6. ApplicationContext的其他功能

### 6.1 消息解析

ApplicationContext接口**通过继承MessageSource接口**来**支持消息解析**和国际化。
此外，Spring提供了两个MessageSource实现，分别是ResourceBundleMessageSource和StaticMessageSource。

我们可以使用StaticMessageSource以编程方式将消息添加到源；但是，它支持基本的国际化，并且更适合测试而不是生产使用。

另一方面，**ResourceBundleMessageSource是MessageSource最常见的实现**。它依赖于底层JDK的ResourceBundle实现。
它还使用MessageFormat提供的JDK标准消息解析。

现在让我们看看如何使用MessageSource从属性文件中读取消息。

首先，我们在类路径下创建一个messages.properties文件：

```properties
account.name=TestAccount
```

其次，我们在AccountConfig类中添加一个bean定义：

```java

@Configuration
public class AccountConfig {

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("config/messages");
        return messageSource;
    }
}
```

第三，我们在AccountService中注入MessageSource：

```java
public class AccountService {

    @Autowired
    private MessageSource messageSource;
}
```

最后，我们可以在AccountService中的任何位置使用MessageSource#getMessage方法来读取消息：

```java
import java.util.Locale;

public class AccountService {

    @Autowired
    private MessageSource messageSource;

    public String getAccountName() {
        return messageSource.getMessage("account.name", null, Locale.US);
    }
}
```

Spring还提供了ReloadableResourceBundleMessageSource类，它允许从任何Spring资源位置读取文件，并支持bundle属性文件的热加载。

### 6.2 事件处理

ApplicationContext**在ApplicationEvent类和ApplicationListener接口的帮助下**支持事件处理。
它支持ContextStartedEvent、ContextStoppedEvent、ContextClosedEvent和RequestHandledEvent等内置事件。
此外，它还支持业务用例的自定义事件。

## 7. 总结

在本文中，我们讨论了Spring中ApplicationContext容器的各个方面。
我们还探讨了如何在ApplicationContext中配置Spring bean的不同示例。
最后，我们学习了如何创建和使用不同类型的ApplicationContext。