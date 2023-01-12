## 1. 概述

在本教程中，我们将了解 Spring 的两个主要 XML 配置元素之间的区别： <context:annotation-config> 和<context:component-scan>。

## 2. Bean 定义

众所周知，Spring 为我们提供了两种方式来定义我们的[bean](https://www.baeldung.com/spring-bean)和依赖项：[XML 配置](https://www.baeldung.com/spring-xml-injection)和Java注解。我们还可以将 Spring 的注解分为两类：[依赖注入注解](https://www.baeldung.com/spring-core-annotations)和[bean 注解](https://www.baeldung.com/spring-core-annotations)。

在注解之前，我们必须在 XML 配置文件中手动定义所有 bean 和依赖项。现在感谢 Spring 的注解，它可以自动为我们发现并连接我们所有的 bean 和依赖项。因此，我们至少可以消除 bean 和依赖项所需的 XML。

但是，我们应该记住，除非我们激活它们，否则注解是没有用的。为了激活它们，我们可以在我们的 XML 文件顶部添加<context:annotation-config>或<context:component-scan> 。

在本节中，我们将了解<context:annotation-config>和 <context:component-scan>在激活注解的方式方面有何不同。

## 3.通过< context:annotation-config>激活注解

< context:annotation-config>注解主要用于激活依赖注入注解。 [@Autowired](https://www.baeldung.com/spring-autowire)、[@Qualifier](https://www.baeldung.com/spring-core-annotations)、 [@PostConstruct](https://www.baeldung.com/spring-postconstruct-predestroy)、[@PreDestroy](https://www.baeldung.com/spring-postconstruct-predestroy)和[@Resource](https://www.baeldung.com/spring-annotations-resource-inject-autowire)是 <context:annotation-config>可以解决的一些问题。

下面做一个简单的例子，看看<context:annotation-config>是如何为我们简化XML配置的。

首先，让我们创建一个带有依赖字段的类：

```java
public class UserService {
    @Autowired
    private AccountService accountService;
}
public class AccountService {}
```

现在，让我们定义我们的bean。

```xml
<bean id="accountService" class="AccountService"></bean>

<bean id="userService" class="UserService"></bean>
```

在继续之前，让我们指出我们仍然需要在 XML 中声明 bean。这是因为 <context:annotation-config> 只为已经在应用程序上下文中注册的 bean 激活注解。

从这里可以看出，我们使用@Autowired注解了accountService字段 。@Autowired告诉 Spring 这个字段是一个需要被匹配的 bean 自动连接的依赖。

如果我们不使用 @Autowired，那么我们需要手动设置accountService依赖：

```xml
<bean id="userService" class="UserService">
    <property name="accountService" ref="accountService"></property>
</bean>
```

现在，我们可以在单元测试中引用我们的 beans 和依赖项：

```java
@Test
public void givenContextAnnotationConfig_whenDependenciesAnnotated_thenNoXMLNeeded() {
    ApplicationContext context
      = new ClassPathXmlApplicationContext("classpath:annotationconfigvscomponentscan-beans.xml");

    UserService userService = context.getBean(UserService.class);
    AccountService accountService = context.getBean(AccountService.class);

    Assert.assertNotNull(userService);
    Assert.assertNotNull(accountService);
    Assert.assertNotNull(userService.getAccountService());
}
```

嗯，这里有问题。看起来 Spring 没有连接accountService ，即使我们用@Autowired注解它也是如此。看起来@Autowired 未激活。为了解决这个问题，我们只需在 XML 文件顶部添加以下行：

```xml
<context:annotation-config/>
```

## 4. Annotation Activation by < context:component-scan>

与<context:annotation-config>类似，<context:component-scan>也可以识别和处理依赖注入注解。此外，<context:component-scan> 可识别<context:annotation-config>未检测到的 bean 注解。

基本上，<context:component-scan> 通过包扫描来检测注解。换句话说，它告诉 Spring 需要扫描哪些包以查找带注解的 bean 或组件。

@Component 、@Repository、@Service、@Controller、@RestController 和[@Configuration](https://www.baeldung.com/spring-component-repository-service)是[<](https://www.baeldung.com/spring-controller-vs-restcontroller) context[ : ](https://www.baeldung.com/spring-component-repository-service)[component](https://www.baeldung.com/spring-controller-vs-restcontroller) - [ scan](https://www.baeldung.com/spring-component-repository-service) >[可以](https://www.baeldung.com/spring-mvc-tutorial)检测 到 的几个。

现在让我们看看如何简化前面的示例：

```java
@Component
public class UserService {
    @Autowired
    private AccountService accountService;
}


@Component
public class AccountService {}
```

在这里， @Component注解将我们的类标记为 beans。现在，我们可以从 XML 文件中清除所有 bean 定义。当然，我们需要将<context:component-scan>放在它上面：

```xml
<context:component-scan
  base-package="com.baeldung.annotationconfigvscomponentscan.components" />
```

最后，请注意，Spring 将在 base-package属性指示的包下查找带注解的 bean 和依赖项。

## 5.总结

在本教程中，我们查看了<context:annotation-config>和<context:component-scan>之间的区别。