## 1. 概述

Spring 5支持在应用程序上下文中注册函数bean。

简而言之，**这可以通过在GenericApplicationContext类中定义的新registerBean()方法的重载版本来完成**。

让我们看一下这个功能的几个例子。

## 2. Maven依赖

设置Spring 5项目的最快方法是通过将spring-boot-starter-parent依赖项添加到pom.xml来使用Spring Boot：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.2</version>
</parent>
```

我们的示例还需要[spring-boot-starter-web](https://search.maven.org/search?q=a:spring-boot-starter-web)和spring-boot-starter-test，以便在JUnit测试中使用Web应用程序上下文：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

当然，为了使用新的函数方式注册bean，Spring Boot不是必需的，我们也可以直接添加spring-core依赖：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>5.3.3</version>
</dependency>
```

## 3. 函数Bean注册

**registerBean() API可以接收两种类型的函数接口作为参数**：

-   用于创建对象的**Supplier参数**
-   一个**BeanDefinitionCustomizer可变参数**，可用于提供一个或多个lambda表达式来自定义BeanDefinition；这个接口只有一个customize()方法

首先，让我们创建一个非常简单的类定义，我们将使用它来创建bean：

```java
public class MyService {
    public int getRandomNumber() {
        return new Random().nextInt(9);
    }
}
```

我们还添加一个@SpringBootApplication类，我们可以使用它来运行JUnit测试：

```java
@SpringBootApplication
public class Spring5Application {
    public static void main(String[] args) {
        SpringApplication.run(Spring5Application.class, args);
    }
}
```

接下来，我们可以使用@SpringBootTest注解来设置我们的测试类，以创建一个GenericWebApplicationContext实例：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Spring5Application.class)
public class BeanRegistrationIntegrationTest {
    @Autowired
    private GenericWebApplicationContext context;
   
    // ...
}
```

我们在示例中使用了GenericWebApplicationContext类型，但任何类型的应用程序上下文都可以以相同的方式用于注册bean。

让我们看看如何**使用lambda表达式注册一个bean来创建实例**：

```java
context.registerBean(MyService.class, () -> new MyService());
```

让我们验证我们现在可以检索bean并使用它：

```java
MyService myService = (MyService) context.getBean("cn.tuyucheng.taketoday.functional.MyService"); 
 
assertTrue(myService.getRandomNumber() < 10);
```

我们可以在这个例子中看到，如果没有明确定义bean名称，它将由类的小写名称确定。上面的相同方法也可以与显式bean名称一起使用：

```java
context.registerBean("mySecondService", MyService.class, () -> new MyService());
```

接下来，让我们看看如何**通过添加lambda表达式来自定义bean来注册它**：

```java
context.registerBean("myCallbackService", MyService.class, 
  () -> new MyService(), bd -> bd.setAutowireCandidate(false));
```

这个参数是一个回调，我们可以使用它来设置bean属性，例如autowire-candidate标志或primary标志。

## 4. 总结

在本快速教程中，我们了解了如何使用注册bean的函数式方法。