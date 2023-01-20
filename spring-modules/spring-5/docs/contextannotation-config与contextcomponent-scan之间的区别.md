## 1. 概述

在本教程中，我们将了解Spring的两个主要XML配置元素之间的区别：\<context:annotation-config\>和\<context:component-scan\>。

## 2. Bean定义

众所周知，Spring为我们提供了两种方式来定义我们的[bean](https://www.baeldung.com/spring-bean)和依赖项：[XML配置](https://www.baeldung.com/spring-xml-injection)和Java注解。我们还可以将Spring的注解分为两类：[依赖注入注解](https://www.baeldung.com/spring-core-annotations)和[bean注解](https://www.baeldung.com/spring-core-annotations)。

在注解之前，我们必须在XML配置文件中手动定义所有bean和依赖项。现在感谢Spring的注解，**它可以自动为我们发现并注入我们所有的bean和依赖项**。因此，我们至少可以消除bean和依赖项所需的XML。

但是，我们应该记住，**除非我们激活它们，否则注解是没有用的。为了激活它们，我们可以在我们的XML文件顶部添加\<context:annotation-config\>或\<context:component-scan\>**。

在本节中，我们将了解\<context:annotation-config\>和\<context:component-scan\>在激活注解的方式方面有何不同。

## 3. 通过\<context:annotation-config\>激活注解

\<context:annotation-config\>注解主要用于激活依赖注入注解，[@Autowired](https://www.baeldung.com/spring-autowire)、[@Qualifier](https://www.baeldung.com/spring-core-annotations)、[@PostConstruct](https://www.baeldung.com/spring-postconstruct-predestroy)、[@PreDestroy](https://www.baeldung.com/spring-postconstruct-predestroy)和[@Resource](https://www.baeldung.com/spring-annotations-resource-inject-autowire)是\<context:annotation-config\>可以解决的一些问题。

下面做一个简单的例子，看看\<context:annotation-config\>是如何为我们简化XML配置的。

首先，让我们创建一个带有依赖字段的类：

```java
public class UserService {
    @Autowired
    private AccountService accountService;
}
```

```java
public class AccountService {}
```

现在，让我们定义我们的bean。

```xml
<bean id="accountService" class="AccountService"></bean>

<bean id="userService" class="UserService"></bean>
```

在继续之前，让我们指出我们仍然需要在XML中声明bean，这是因为**\<context:annotation-config\>只为已经在应用程序上下文中注册的bean激活注解。**

从这里可以看出，我们使用@Autowired注解了accountService字段，**@Autowired告诉Spring这个字段是一个需要被匹配的bean自动注入的依赖**。

如果我们不使用@Autowired，那么我们需要手动设置accountService依赖：

```xml
<bean id="userService" class="UserService">
	<property name="accountService" ref="accountService"/>
</bean>
```

现在，我们可以在单元测试中引用我们的beans和依赖项：

```java
@Test
void givenContextAnnotationConfig_whenDependenciesAnnotated_thenNoXMLNeeded() {
    ApplicationContext context = new ClassPathXmlApplicationContext("classpath:annotationconfigvscomponentscan-beans.xml");

    UserService userService = context.getBean(UserService.class);
    AccountService accountService = context.getBean(AccountService.class);

    assertNotNull(userService);
    assertNotNull(accountService);
    assertNotNull(userService.getAccountService());
}
```

嗯，这里有问题。看起来Spring没有注入accountService，即使我们用@Autowired注解标注它也是如此，看起来@Autowired未激活。为了解决这个问题，我们只需在XML文件顶部添加以下行：

```xml
<context:annotation-config/>
```

## 4. 通过\<context:component-scan\>激活注解

与\<context:annotation-config\>类似，\<context:component-scan\>也可以识别和处理依赖注入注解。此外，**\<context:component-scan\>可识别\<context:annotation-config\>未检测到的bean注解**。

基本上，**\<context:component-scan\>通过包扫描来检测注解**。换句话说，它告诉Spring需要扫描哪些包以查找带注解的bean或组件。

[@Component](https://www.baeldung.com/spring-component-repository-service)、[@Repository](https://www.baeldung.com/spring-component-repository-service)、[@Service](https://www.baeldung.com/spring-component-repository-service)、[@Controller](https://www.baeldung.com/spring-controller-vs-restcontroller)、[@RestController](https://www.baeldung.com/spring-controller-vs-restcontroller)和[@Configuration](https://www.baeldung.com/spring-mvc-tutorial)是\<context:component-scan\>可以检测到的几个。

现在让我们看看如何简化前面的示例：

```java
@Component
public class UserService {
    @Autowired
    private AccountService accountService;
}
```

```java
@Component
public class AccountService {}
```

在这里，**@Component注解将我们的类标记为beans**。现在，我们可以从XML文件中清除所有bean定义。当然，我们需要将\<context:component-scan\>放在它上面：

```xml
<context:component-scan
	base-package="cn.tuyucheng.taketoday.annotationconfigvscomponentscan.components"/>
```

最后，请注意，Spring将在base-package属性指示的包下查找带注解的bean和依赖项。

## 5. 总结

在本教程中，我们查看了\<context:annotation-config\>和\<context:component-scan\>之间的区别。