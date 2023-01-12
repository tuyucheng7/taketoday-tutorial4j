## 1. 概述

在本文中，我们将介绍Spring框架中不同类型的bean作用域。

bean的作用域定义了该bean在我们使用它的上下文中的生命周期和可见性。

最新版本的Spring框架定义了6种类型的作用域：

+ singleton
+ prototype
+ session
+ request
+ application
+ websocket

后面提到的四个作用域，request、session、application和websocket，仅在web应用程序中可用。

## 2. Singleton Scope

当我们使用单例作用域定义bean时，容器会创建该bean的单个实例；对该bean名称的所有请求都将返回相同的对象，该对象被缓存。
对对象的任何修改都将反映在对bean的所有引用中。如果未指定其他作用域，则此作用域是默认值。

让我们创建一个Person实体来举例说明作用域的概念：

```java
public class Person {
    private String name;
    // standard constructor, getters and setters
}
```

然后，我们使用@Scope注解定义具有单例作用域的bean：

```java

@Configuration
public class ScopesConfig {

    @Bean
    @Scope("singleton")
    public Person personSingleton() {
        return new Person();
    }
}
```

我们还可以通过以下方式使用常量而不是显示的String值：

```
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
```

现在我们可以编写一个测试，显示引用同一个bean的两个对象将具有相同的值，即使它们中只有一个改变了它们的状态，
因为它们都引用同一个bean实例：

```java
class ScopesIntegrationTest {
    private static final String NAME = "John Smith";

    @Test
    void givenSingletonScope_whenSetName_thenEqualNames() {
        final AbstractApplicationContext applicationContext = new ClassPathXmlApplicationContext("scopes.xml");

        final Person personSingletonA = (Person) applicationContext.getBean("personSingleton");
        final Person personSingletonB = (Person) applicationContext.getBean("personSingleton");
        personSingletonA.setName(NAME);
        assertEquals(NAME, personSingletonB.getName());

        applicationContext.close();
    }
}
```

此示例中的scopes.xml文件应包含所用bean的xml定义：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="personSingleton" class="cn.tuyucheng.taketoday.scopes.Person" scope="singleton"/>
</beans>
```

## 3. Prototype Scope

具有prototype作用域的bean将在每次从容器请求时返回不同的实例。它是通过在bean定义中将@Scope注解的value设置为prototype来定义的：

```java

@Configuration
public class ScopesConfig {

    @Bean
    @Scope("prototype")
    public Person personPrototype() {
        return new Person();
    }
}
```

我们也可以使用一个常量，就像我们对单例作用域所做的那样：

```
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
```

我们现在编写一个与之前类似的测试，显示两个对象在原型作用域内请求相同的bean名称。
它们将具有不同的状态，因为它们不再引用同一个bean实例：

```java
class ScopesIntegrationTest {
    private static final String NAME = "John Smith";
    private static final String NAME_OTHER = "Anna Jones";

    @Test
    void givenPrototypeScope_whenSetNames_thenDifferentNames() {
        final AbstractApplicationContext applicationContext = new ClassPathXmlApplicationContext("scopes.xml");

        final Person personPrototypeA = (Person) applicationContext.getBean("personPrototype");
        final Person personPrototypeB = (Person) applicationContext.getBean("personPrototype");

        personPrototypeA.setName(NAME);
        personPrototypeB.setName(NAME_OTHER);
        assertEquals(NAME, personPrototypeA.getName());
        assertEquals(NAME_OTHER, personPrototypeB.getName());

        applicationContext.close();
    }
}
```

scopes.xml文件类似于上一节中介绍的文件，同时为具有原型作用域的bean添加xml定义：

```
<bean id="personPrototype" class="cn.tuyucheng.taketoday.scopes.Person" scope="prototype"/>
```

## 4. Web应用作用域

如前所述，有四个额外作用域仅在Web应用程序上下文中可用。我们在实践中很少使用这些。

request作用域为单个HTTP请求创建一个bean实例，而session作用域为一个HTTP会话创建一个bean实例。

Application作用域为ServletContext的生命周期创建bean实例，而websocket作用域为特定的WebSocket会话创建bean示例。

让我们创建一个用于实例化bean的类：

```java
public class HelloMessageGenerator {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}
```

### 4.1 Request Scope

我们可以使用@Scope注解定义具有request作用域的bean：

```java

@Configuration
public class ScopesConfig {

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public HelloMessageGenerator requestScopedBean() {
        return new HelloMessageGenerator();
    }
}
```

proxyMode属性是必需的，因为在实例化web应用程序上下文时，没有有效的请求。
Spring创建一个代理作为依赖注入，并在请求中需要它时实例化目标bean。

我们还可以使用@RequestScope组合注解作为上述定义的快捷方式：

```java

@Configuration
public class ScopesConfig {

    @Bean
    @RequestScope
    public HelloMessageGenerator requestScopedBean() {
        return new HelloMessageGenerator();
    }
}
```

接下来，我们可以定义一个控制器，该控制器具有对requestScopedBean的引用。我们需要访问同一个请求两次以测试特定于Web的作用域。

如果我们在每次运行请求时都显示该消息，我们可以看到该值被重置为null，即使它后来在方法中被更改。这是因为每个请求都返回了不同的bean实例。

```java

@Controller
public class ScopesController {

    @Resource(name = "requestScopedBean")
    HelloMessageGenerator requestScopedBean;

    @RequestMapping("/scopes/request")
    public String getRequestScopeMessage(final Model model) {
        model.addAttribute("previousMessage", requestScopedBean.getMessage());
        requestScopedBean.setMessage("Request Scope Message!");
        model.addAttribute("currentMessage", requestScopedBean.getMessage());
        return "scopesExample";
    }
}
```

### 4.2 Session Scope

我们可以用类似的方式定义Session作用域的bean：

```java

@Configuration
public class ScopesConfig {

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public HelloMessageGenerator sessionScopedBean() {
        return new HelloMessageGenerator();
    }
}
```

我们也可以使用一个专用的组合注解来简化bean定义：

```java

@Configuration
public class ScopesConfig {

    @Bean
    @SessionScope
    public HelloMessageGenerator sessionScopedBean() {
        return new HelloMessageGenerator();
    }
}
```

接下来我们定义一个引用sessionScopedBean的控制器。同样，我们需要运行两个请求以显示message字段的值对于会话是相同的。

在这种情况下，当第一次发出请求时，message为空。但是，一旦更改，该值将保留给后续请求，因为为整个会话返回相同的bean实例。

```java

@Controller
public class ScopesController {
    public static final Logger LOG = LoggerFactory.getLogger(ScopesController.class);

    @Resource(name = "sessionScopedBean")
    HelloMessageGenerator sessionScopedBean;

    @RequestMapping("/scopes/session")
    public String getSessionScopeMessage(final Model model) {
        model.addAttribute("previousMessage", sessionScopedBean.getMessage());
        sessionScopedBean.setMessage("Session Scope Message!");
        model.addAttribute("currentMessage", sessionScopedBean.getMessage());
        return "scopesExample";
    }
}
```

### 4.3 Application Scope

Application作用域为ServletContext的生命周期创建bean实例。

这类似于Singleton作用域，但在bean的作用域方面有一个非常重要的区别。

当bean是Application作用域时，同一个bean实例在同一个ServletContext中运行的多个基于servlet的应用程序之间共享，
而Singleton作用域bean的作用域仅限于单个应用程序上下文。

让我们创建具有Application作用域的bean：

```java

@Configuration
public class ScopesConfig {

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_APPLICATION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public HelloMessageGenerator applicationScopedBean() {
        return new HelloMessageGenerator();
    }
}
```

类似于requestsession作用域，我们可以使用更简单的方式：

```java

@Configuration
public class ScopesConfig {

    @Bean
    @ApplicationScope
    public HelloMessageGenerator applicationScopedBean() {
        return new HelloMessageGenerator();
    }
}
```

现在让我们创建一个引用这个bean的控制器：

```java

@Controller
public class ScopesController {
    public static final Logger LOG = LoggerFactory.getLogger(ScopesController.class);

    @Resource(name = "applicationScopedBean")
    HelloMessageGenerator applicationScopedBean;

    @RequestMapping("/scopes/application")
    public String getApplicationScopeMessage(final Model model) {
        model.addAttribute("previousMessage", applicationScopedBean.getMessage());
        applicationScopedBean.setMessage("Application Scope Message!");
        model.addAttribute("currentMessage", applicationScopedBean.getMessage());
        return "scopesExample";
    }
}
```

在这种情况下，一旦在applicationScopedBean中设置了message的值，那么将为所有后续请求、会话，
甚至访问该bean的不同servlet应用程序保留该message值，前提是该bean运行在相同的ServletContext中。

### 4.4 WebSocket Scope

最后，让我们使用websocket作用域创建bean：

```java

@Configuration
public class ScopesConfig {

    @Bean
    @Scope(scopeName = "websocket", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public HelloMessageGenerator websocketScopedBean() {
        return new HelloMessageGenerator();
    }
}
```

首次访问时，WebSocket作用域的bean存储在WebSocket会话属性中。每当在整个WebSocket会话期间访问该bean时，都会返回该bean的相同实例。

我们也可以说它表现出单例行为，但仅限于WebSocket会话。

## 5. 总结

在本文中，我们介绍了Spring提供的不同bean作用域以及它们的用途。