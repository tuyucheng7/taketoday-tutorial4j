## 1. 概述

在本教程中，我们将介绍Spring中的组件扫描。在使用Spring时，我们可以使用注解标注我们的类，以便将它们变成Spring bean。此外，**我们可以告诉Spring在哪里搜索这些带注解的类**，因为并非所有这些类都必须在此特定运行中成为beans。

当然组件扫描有一些默认值，但是我们也可以自定义包以进行搜索。

首先，让我们看一下默认设置。

## 2. 不带参数的@ComponentScan

### 2.1 在Spring应用程序中使用@ComponentScan

在Spring中，**我们使用@ComponentScan注解和@Configuration注解来指定我们想要扫描的包**，不带参数的@ComponentScan告诉Spring扫描当前包及其所有子包。

假设我们在cn.tuyucheng.taketoday.componentscan.springapp包中有以下@Configuration类：

```java
@Configuration
@ComponentScan
public class SpringComponentScanApp {
    private static ApplicationContext applicationContext;

    @Bean
    public ExampleBean exampleBean() {
        return new ExampleBean();
    }

    public static void main(String[] args) {
        applicationContext = new AnnotationConfigApplicationContext(SpringComponentScanApp.class);

        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            System.out.println(beanName);
        }
    }
}
```

此外，我们在cn.tuyucheng.taketoday.componentscan.springapp.animals包中有Cat和Dog组件：

```java
package cn.tuyucheng.taketoday.componentscan.springapp.animals;
// ...
@Component
public class Cat {}
```

```java
package cn.tuyucheng.taketoday.componentscan.springapp.animals;
// ...
@Component
public class Dog {}
```

最后，我们在cn.tuyucheng.taketoday.componentscan.springapp.flowers包中有Rose组件：

```java
package cn.tuyucheng.taketoday.componentscan.springapp.flowers;
// ...
@Component
public class Rose {}
```

main()方法的输出将包含cn.tuyucheng.taketoday.componentscan.springapp包及其子包的所有beans：

```shell
springComponentScanApp
cat
dog
rose
exampleBean
```

请注意，主应用程序类也是一个bean，因为它用@Configuration标注，这是一个@Component。

我们还应该注意，主应用程序类和配置类不一定相同。如果它们不同，我们把主应用程序类放在哪里都没有关系，**只有配置类的位置很重要，因为组件扫描默认情况下从它的包开始**。

最后请注意，在我们的示例中，@ComponentScan等效于：

```java
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.componentscan.springapp")
```

basePackages参数是一个包或一组用于扫描的包。

### 2.2 在Spring Boot应用程序中使用@ComponentScan

Spring Boot的诀窍在于许多事情都是隐式发生的，我们使用@SpringBootApplication注解，但它是三个注解的组合：

```java
@Configuration
@EnableAutoConfiguration
@ComponentScan
```

让我们在cn.tuyucheng.taketoday.componentscan.springbootapp包中创建一个类似的结构，这次的主应用程序将是：

```java
package cn.tuyucheng.taketoday.componentscan.springbootapp;
// ...
@SpringBootApplication
public class SpringBootComponentScanApp {
    private static ApplicationContext applicationContext;

    @Bean
    public ExampleBean exampleBean() {
        return new ExampleBean();
    }

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(SpringBootComponentScanApp.class, args);
        checkBeansPresence("cat", "dog", "rose", "exampleBean", "springBootComponentScanApp");

    }

    private static void checkBeansPresence(String... beans) {
        for (String beanName : beans) {
            System.out.println("Is " + beanName + " in ApplicationContext: " + applicationContext.containsBean(beanName));
        }
    }
}
```

所有其他包和类都保持不变，我们只需将它们复制到同级的cn.tuyucheng.taketoday.componentscan.springbootapp包中。

Spring Boot扫描包的方式与我们之前的示例类似，让我们检查一下输出：

```shell
Is cat in ApplicationContext: true
Is dog in ApplicationContext: true
Is rose in ApplicationContext: true
Is exampleBean in ApplicationContext: true
Is springBootComponentScanApp in ApplicationContext: true
```

我们只是在第二个示例中检查bean是否存在(而不是打印出所有bean)的原因是输出太长。

这是因为隐式的@EnableAutoConfiguration注解，它使Spring Boot自动创建许多bean，依赖于pom.xml文件中的依赖项。

## 3. @ComponentScan带参数

现在让我们自定义扫描路径，例如，假设我们要排除Rose bean。

### 3.1 特定包的@ComponentScan

我们可以通过几种不同的方式来做到这一点，首先，我们可以更改基础包：

```java
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.componentscan.springapp.animals")
@Configuration
public class SpringComponentScanApp {
    // ...
}
```

现在输出将是：

```shell
springComponentScanApp
cat
dog
exampleBean
```

让我们看看这背后是什么：

-   springComponentScanApp的创建是因为它是作为参数传递给AnnotationConfigApplicationContext的配置
-   exampleBean是配置里面配置的bean
-   cat和dog在指定的cn.tuyucheng.taketoday.componentscan.springapp.animals包中

上面列出的所有自定义也适用于SpringBoot，我们可以将@ComponentScan与@SpringBootApplication一起使用，结果是一样的：

```java
@SpringBootApplication
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.componentscan.springbootapp.animals")
```

### 3.2 @ComponentScan有多个包

Spring提供了一种方便的方式来指定多个包名，为此，我们需要使用字符串数组。

数组的每个字符串表示一个包名称：

```java
@ComponentScan(basePackages = {"cn.tuyucheng.taketoday.componentscan.springapp.animals", "cn.tuyucheng.taketoday.componentscan.springapp.flowers"})
```

或者，从Spring 4.1.1开始，**我们可以使用逗号、分号或空格来分隔包列表**：

```java
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.componentscan.springapp.animals;cn.tuyucheng.taketoday.componentscan.springapp.flowers")
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.componentscan.springapp.animals,cn.tuyucheng.taketoday.componentscan.springapp.flowers")
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.componentscan.springapp.animals cn.tuyucheng.taketoday.componentscan.springapp.flowers")
```

### 3.3 @ComponentScan带排除项

另一种方法是使用过滤器，指定要排除的类的模式：

```java
@ComponentScan(excludeFilters = @ComponentScan.Filter(type=FilterType.REGEX, 
      pattern="cn\\.tuyucheng\\.taketoday\\.componentscan\\.springapp\\.flowers\\..*"))
```

我们还可以选择不同的过滤器类型，因为**注解支持多种灵活的选项来[过滤]()扫描的类**：

```java
@ComponentScan(excludeFilters = 
  @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = Rose.class))
```

## 4. 默认包

我们应该避免将@Configuration类放在[默认包中](https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-structuring-your-code.html)(即根本不指定包)。如果我们这样做，Spring会扫描类路径中所有jar中的所有类，这会导致错误并且应用程序可能无法启动。

## 5. 总结

在本文中，我们了解了Spring默认扫描哪些包以及如何自定义这些路径。