## 1. 概述

在本教程中，我们探讨@Qualifier注解的作用，它解决了哪些问题，以及如何使用它。

并解释它与@Primary注解以及按名称自动注入的区别。

## 2. @Autowire需要消歧

@Autowired注解是一种在Spring中显式注入依赖bean的方法。
尽管它很有用，但在某些用例中，仅使用此注解不足以让Spring理解要注入哪个bean。

默认情况下，Spring按类型解析自动装配。

**如果容器中有多个相同类型的bean可用，框架将抛出NoUniqueBeanDefinitionException**，这表明有多个bean可用于自动注入。

让我们想象这样一种情况，在给定的实例中，存在两个可能的Spring候选对象可作为bean注入：

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

@Component
public class FooService {

    @Autowired
    private Formatter formatter;
}
```

如果我们尝试将FooService加载到上下文中，Spring框架将抛出NoUniqueBeanDefinitionException。
这是因为Spring不知道要注入哪个具体的Formatter bean。
为了避免这个问题，有几种解决方案；@Qualifier注解就是其中之一。

## 3. @Qualifier

通过使用@Qualifier注解，我们可以消除需要注入哪个bean的问题。

让我们重新回顾之前的例子，看看如何通过使用@Qualifier注解来指示我们想要注入哪个bean：

```java
public class FooService {

    @Autowired
    @Qualifier("fooFormatter")
    private Formatter formatter;
}
```

通过使用@Qualifier注解以及我们要使用的具体实现的名称(在本例中为Foo)，我们可以避免当Spring发现多个相同类型的bean时产生歧义。

我们需要知道的是，@Qualifier中指定的名称是该bean在@Component注解中声明的名称。

我们也可以在Formatter实现类上使用@Qualifier注解，而不是在它们的@Component注解中指定名称，这可以实现相同的效果：

```java

@Component
@Qualifier("fooFormatter")
public class FooFormatter implements Formatter {
    //...
}

@Component
@Qualifier("barFormatter")
public class BarFormatter implements Formatter {
    //...
}
```

## 4. @Qualifier与@Primary

还有另一个名为@Primary的注解，当依赖注入存在歧义时，我们可以使用它来决定注入哪个bean。

**当存在多个相同类型的bean时，此注解定义了一个首选项**。除非另有说明，否则将使用与@Primary注解关联的bean。

让我们看一个例子：

```java

@Configuration
public class Config {

    @Bean
    public Employee johnEmployee() {
        return new Employee("John");
    }

    @Bean
    @Primary
    public Employee tonyEmployee() {
        return new Employee("Tony");
    }
}
```

在本例中，这两个方法都返回相同的Employee类型，而Spring将注入的bean是tonyEmployee方法返回的bean。
这是因为它包含@Primary注解。**当我们要指定默认注入某个类型的哪个bean时，此注解很有用**。

如果我们在某个注入点需要另一个bean，我们需要明确指定它，这可以通过@Qualifier注解来实现。
例如，我们可以通过使用@Qualifier注解来指定我们想要使用johnEmployee方法返回的bean。

值得注意的是，如果@Qualifier和@Primary注解都存在，那么@Qualifier注解将具有优先权。
基本上，@Primary定义了一个默认值，而@Qualifier更具体。

让我们看看使用@Primary注解的另一种方式：

```java

@Component
@Primary
public class FooFormatter implements Formatter {
    //...
}

@Component
public class BarFormatter implements Formatter {
    //...
}
```

在这种情况下，@Primary注解被放置在一个实现类中，并将消除场景的歧义。

## 5. @Qualifier与按名称自动装配

自动装配时在多个bean之间进行选择的另一种方法是使用要注入的字段的名称。
这是默认设置，以防Spring没有其他提示。让我们看一个基于我们最初示例的代码：

```java
public class FooService {

    @Autowired
    private Formatter fooFormatter;
}
```

在这种情况下，Spring将确定要注入的bean是FooFormatter类型的bean，因为字段名称与我们在该bean的@Component注解中使用的值匹配。

## 6. 总结

在本文中，我们描述了需要明确注入哪些bean的场景。
特别是，我们介绍了@Qualifier注解，并将其与确定需要使用哪些bean的其他类似方法进行了比较。