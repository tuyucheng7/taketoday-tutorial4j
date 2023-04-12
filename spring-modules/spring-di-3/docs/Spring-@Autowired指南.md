## 1. 概述

从Spring 2.5开始，Spring引入了注解驱动的依赖注入。这个特性的主要注解是@Autowired。
**它允许Spring解析依赖bean并将其注入到我们的bean中**。

在本文中，我们将首先了解如何启用自动装配以及自动装配bean的各种方法。
之后，我们将介绍**使用@Qualifier注解解决bean冲突**，以及潜在的异常情况。

## 2. 启用@Autowired注解

Spring框架支持自动依赖注入。
换句话说，**通过在Spring配置文件中声明所有bean依赖项，Spring容器可以自动装配协作bean之间的关系**。
这称为**Spring bean自动装配**。

要在我们的应用程序中使用基于Java的配置，我们需要启用注解驱动注入来加载我们的Spring配置：

```java

@Configuration
@ComponentScan("cn.tuyucheng.taketoday.autowire.sample")
public class AppConfig {

}
```

或者，使用Spring XML文件配置时可以声明<context:annotation-config\>。

此外，**Spring Boot引入了@SpringBootApplication注解**。
这个注解等效于@Configuration、@EnableAutoConfiguration和@ComponentScan这三个注解。

让我们在应用程序的主类上使用此注解：

```java

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```

**当我们运行这个Spring Boot应用程序时，它会自动扫描当前包及其子包中的bean**。
然后在Spring的应用程序上下文中注册它们，并允许我们使用@Autowired注入bean。

## 3. @Autowired的使用

启用注解驱动后，**我们可以在成员变量、setter方法和构造方法上使用@Autowired注解**。

### 3.1 在属性上使用@Autowired

让我们看看如何在属性上使用@Autowired注解，这消除了对getter和setter方法的需要。

首先，让我们定义一个fooFormatter bean：

```java
public interface Formatter {
    String format();
}

@Component("fooFormatter")
public class FooFormatter {

    public String format() {
        return "foo";
    }
}
```

然后，我们将在字段定义上使用@Autowired将此bean注入进FooService bean：

```java

@Component
public class FooService {
    @Autowired
    private FooFormatter fooFormatter;
}
```

因此，Spring在创建FooService类型的bean时会自动注入fooFormatter。

### 3.2 在setter方法上使用@Autowired

现在，让我们尝试在setter方法上使用@Autowired注解。

在以下示例中，创建FooService时使用FooFormatter的实例调用setFooFormatter()方法：

```java
public class FooService {
    private FooFormatter fooFormatter;

    @Autowired
    public void setFooFormatter(FooFormatter fooFormatter) {
        this.fooFormatter = fooFormatter;
    }
}
```

### 3.3 在构造方法上使用@Autowired

最后，让我们在构造函数上使用@Autowired。

Spring将FooFormatter的实例作为FooService构造函数的参数注入：

```java
public class FooService {
    private FooFormatter fooFormatter;

    @Autowired
    public FooService(FooFormatter fooFormatter) {
        this.fooFormatter = fooFormatter;
    }
}
```

## 4. @Autowired和可选依赖项

当构建bean时，@Autowired依赖项应该可用。否则，**如果Spring无法解析用于注入的bean，它将抛出异常**。

因此，它会以异常的形式阻止Spring容器成功启动：

```
Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: 
No qualifying bean of type [cn.tuyucheng.taketoday.autowired.sample.FooDAO] found for dependency: 
expected at least 1 bean which qualifies as autowire candidate for this dependency. 
Dependency annotations: 
{@org.springframework.beans.factory.annotation.Autowired(required=true)}
```

要解决此问题，我们可以将required属性设置为false：

```java
public class FooService {
    @Autowired(required = false)
    private FooDAO dataAccessor;
}
```

## 5. 自动注入消歧

默认情况下，Spring按类型解析@Autowired。**如果容器中有多个相同类型的bean可用，框架将抛出一个致命异常**。

为了解决这个冲突，我们需要明确地告诉Spring我们要注入哪个bean。

### 5.1 使用@Qualifier自动注入

例如，让我们看看如何使用@Qualifier注解来指定所需注入的bean。

首先，我们将定义2个Formatter类型的bean：

```java

@Component("fooFormatter")
public class FooFormatter implements Formatter {
    public String format() {
        return "foo";
    }
}

@Component("barFormatter")
public class BarFormatter implements Formatter {
    public String format() {
        return "bar";
    }
}
```

现在，我们尝试将Formatter bean注入进FooService类：

```java
public class FooService {
    @Autowired
    private Formatter formatter;
}
```

在我们的例子中，Spring容器中有两个具体的Formatter实现可用。
因此，**在构造FooService时，Spring将抛出NoUniqueBeanDefinitionException异常**：

```
Caused by: org.springframework.beans.factory.NoUniqueBeanDefinitionException: 
No qualifying bean of type [cn.tuyucheng.taketoday.autowire.sample.Formatter] is defined: 
expected single matching bean but found 2: barFormatter,fooFormatter
```

**我们可以通过使用@Qualifier注解指定需要注入的bean的名称来避免这种情况**：

```java
public class FooService {
    @Autowired
    @Qualifier("fooFormatter")
    private Formatter formatter;
}
```

当有多个相同类型的bean时，最好**使用@Qualifier注解来避免这种情况**。

请注意，@Qualifier注解value属性的值需要与我们在FooFormatter实现类中@Component注解声明的名称匹配。

### 5.2 使用自定义Qualifier自动注入

Spring还允许我们创建自己的**自定义@Qualifier注解**。为此，我们需要提供带有@Qualifier注解定义的注解：

```java

@Qualifier
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FormatterType {
    String value();
}
```

然后，我们可以在实现类上使用@FormatterType来指定自定义值：

```java

@FormatterType("Foo")
@Component
public class FooFormatter implements Formatter {
    public String format() {
        return "foo";
    }
}

@FormatterType("Bar")
@Component
public class BarFormatter implements Formatter {
    public String format() {
        return "bar";
    }
}
```

最后，我们的自定义@FormatterType注解可用于自动注入：

```java

@Component
public class FooService {
    @Autowired
    @FormatterType("Foo")
    private Formatter formatter;
}
```

**@Target元注解中指定的值限制了@FormatterType可以使用的位置**，在我们的示例中可以在字段、方法、类和参数上使用。

### 5.3 按名称自动注入

**Spring使用bean的名称作为默认限定符值**。它将检查容器并查找具有确切名称的bean作为属性来自动装配它。

在下面的例子中，Spring将字段名称fooFormatter与名为fooFormatter的bean相匹配。
因此，它在构建FooService时会注入FooFormatter类型的bean：

```java
public class FooService {
    @Autowired
    private Formatter fooFormatter;
}
```

## 6. 总结

在本文中，我们讨论了自动注入及其不同的使用方法。我们还说明了解决由缺少bean或不明确bean注入引起的两种常见自动注入异常的方法。