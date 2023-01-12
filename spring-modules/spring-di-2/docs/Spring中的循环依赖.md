## 1. 概述

当一个bean A依赖另一个bean B并且bean B也依赖于bean A时，就会发生循环依赖：

```text
Bean A → Bean B → Bean A
```

当然，可能会隐含更多的bean：

```text
Bean A → Bean B → Bean C → Bean D → Bean E → Bean A
```

## 2. Spring中会发生什么

当Spring上下文加载所有bean时，它会尝试按照bean所需的顺序创建bean。

假设我们没有产生循环依赖。例如，我们的bean创建顺序如下：

```text
Bean A → Bean B → Bean C
```

Spring将创建bean C，然后创建bean B(并将bean C注入其中)，然后创建bean A(并将bean B注入其中)。

但是对于循环依赖，Spring无法决定应该首先创建哪个bean，因为它们相互依赖。
在这些情况下，Spring将在加载上下文时引发BeanCurrentlyInCreationException。

当使用**构造注入时**，循环依赖可能会在Spring中发生。
如果我们使用其他类型的注入，我们不应该会有这个问题，因为依赖项只会在需要时注入，而不是在上下文加载时注入。

## 3. 一个简单的例子

让我们定义两个相互依赖的bean(通过构造注入)：

```java

@Component
public class CircularDependencyA {

    private CircularDependencyB circB;

    @Autowired
    public CircularDependencyA(CircularDependencyB circB) {
        this.circB = circB;
    }
}
```

```java

@Component
public class CircularDependencyB {

    private CircularDependencyA circA;

    @Autowired
    public CircularDependencyB(CircularDependencyA circA) {
        this.circA = circA;
    }
}
```

现在我们可以为测试编写一个配置类TestConfig，它指定要扫描组件的basePackages。

假设我们的bean定义在包“cn.tuyucheng.taketoday.circulardependency”中：

```java

@Configuration
@ComponentScan(basePackages = {"cn.tuyucheng.taketoday.circulardependency"})
public class TestConfig {

}
```

最后，我们可以编写一个JUnit测试来测试循环依赖。

测试方法可以保留为空，因为Spring会在上下文加载期间检测到循环依赖：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
class CircularDependencyIntegrationTest {

    @Test
    void givenCircularDependency_whenConstructorInjection_thenItFails() {
        // Empty test; we just want the context to load
    }
}
```

如果我们尝试运行这个测试，会得到以下异常：

```text
Caused by: org.springframework.beans.factory.BeanCurrentlyInCreationException: 
Error creating bean with name 'circularDependencyA': 
Requested bean is currently in creation: Is there an unresolvable circular reference?
```

## 4. 解决办法

### 4.1 重新设计

当我们碰到循环依赖时，很可能是因为我们的设计问题，并且职责没有很好地分离。
我们应该尝试正确地重新设计组件，以便它们的层次结构设计得更好，并且不会发生循环依赖。

但是，有许多可能的原因，我们可能无法重新设计，例如遗留代码、已经测试且无法修改的代码、没有足够的时间或资源进行完整的重新设计等。
如果我们不能重新设计组件，我们可以尝试一些变通方法。

### 4.2 使用@Lazy注解

打破循环的一种简单方法是告诉Spring惰性地初始化其中一个bean。
因此，它不会完全初始化bean，而是创建一个代理将其注入另一个bean，注入的bean只有在第一次需要时才会完全创建。

要使用这种方法打破循环，我们可以更改CircularDependencyA：

```java

@Component
public class CircularDependencyA {

    private CircularDependencyB circB;

    @Autowired
    public CircularDependencyA(@Lazy CircularDependencyB circB) {
        this.circB = circB;
    }
}
```

如果我们现在运行测试，会看到这次不会出现错误。

### 4.3 使用Setter/字段注入

最常用的解决方法之一，也是Spring文档所建议的，是使用setter注入。

简单地说，我们可以通过改变bean的注入方式来解决这个问题 - 使用setter注入(或字段注入)而不是构造注入。
这样，Spring会创建bean，但直到需要使用它们时才会注入依赖项。

因此，让我们将类更改为使用setter注入，并向CircularDependencyB添加另一个字段(message)：

```java

@Component
public class CircularDependencyA {

    private CircularDependencyB circB;

    @Autowired
    public void setCircB(CircularDependencyB circB) {
        this.circB = circB;
    }

    public CircularDependencyB getCircB() {
        return circB;
    }
}
```

```java

@Component
public class CircularDependencyB {

    private CircularDependencyA circA;

    private String message = "Hi!";

    @Autowired
    public void setCircA(CircularDependencyA circA) {
        this.circA = circA;
    }

    public String getMessage() {
        return message;
    }
}
```

现在我们需要对单元测试进行一些更改：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
class CircularDependencyIntegrationTest {

    @Autowired
    ApplicationContext context;

    @Bean
    public CircularDependencyA getCircularDependencyA() {
        return new CircularDependencyA();
    }

    @Bean
    public CircularDependencyB getCircularDependencyB() {
        return new CircularDependencyB();
    }

    @Test
    void givenCircularDependency_whenSetterInjection_thenItWorks() {
        final CircularDependencyA circA = context.getBean(CircularDependencyA.class);
        assertEquals("Hi!", circA.getCircB().getMessage());
    }
}
```

@Bean注解告诉Spring框架必须使用这些方法来获取要注入的bean。

使用@Test注解，测试将从上下文中获取CircularDependencyA bean，
并断言其CircularDependencyB已正确注入，检查其message属性的值。

### 4.4 使用@PostConstruct

打破循环的另一种方法是在其中一个bean上使用@Autowired注入依赖项，然后使用带有@PostConstruct注解的方法来设置另一个依赖项。

例如下面这样：

```java

@Component
public class CircularDependencyA {

    @Autowired
    private CircularDependencyB circB;

    @PostConstruct
    public void init() {
        circB.setCircA(this);
    }

    public CircularDependencyB getCircB() {
        return circB;
    }
}
```

```java

@Component
public class CircularDependencyB {
    private CircularDependencyA circA;

    private String message = "Hi!";

    public void setCircA(CircularDependencyA circA) {
        this.circA = circA;
    }

    public String getMessage() {
        return message;
    }
}
```

我们可以运行之前的测试，可以看到循环依赖异常仍然没有发生，并且依赖被正确注入。

### 4.5 实现ApplicationContextAware和InitializingBean

如果其中一个bean实现了ApplicationContextAware，则该bean可以访问Spring上下文并可以从中获取另一个bean。

通过实现InitializingBean，我们表明该bean必须在其所有属性设置后执行一些操作。
在这种情况下，我们希望手动设置我们的依赖关系。以下是我们的代码：

```java

@Component
public class CircularDependencyA implements ApplicationContextAware, InitializingBean {

    private CircularDependencyB circB;

    private ApplicationContext context;

    public CircularDependencyB getCircB() {
        return circB;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        circB = context.getBean(CircularDependencyB.class);
    }

    @Override
    public void setApplicationContext(final ApplicationContext ctx) throws BeansException {
        context = ctx;
    }
}
```

```java

@Component
public class CircularDependencyB {
    private CircularDependencyA circA;
    private String message = "Hi!";

    @Autowired
    public void setCircA(CircularDependencyA circA) {
        this.circA = circA;
    }

    public String getMessage() {
        return message;
    }
}
```

同样，我们可以运行之前的测试，并看到没有抛出异常并且测试按预期执行。

## 5. 总结

在Spring中有很多方法可以解决循环依赖。

我们应该首先考虑重新设计我们的bean，这样就不会导致循环依赖。因为循环依赖通常意味着可以将代码结构进行改进。

但是如果我们在项目中不可避免循环依赖，我们可以参考上面提到的一些解决方法。

首选方法是使用setter注入，但是还有其他替代方案，通常基于阻止Spring管理bean的初始化，以及使用不同的策略自己完成这一点。