## 1. 概述

通常，我们使用@BeforeEach、@AfterEach、@BeforeAll和@AfterAll等JUnit注解来编排测试的生命周期，但有时这还不够-尤其是在我们使用Spring框架时。

这就是Spring TestExecutionListener派上用场的地方。

在本教程中，**我们将了解TestExecutionListener提供的作用、Spring提供的默认监听器以及如何实现自定义TestExecutionListener**。

## 2. TestExecutionListener接口

首先，下面是TestExecutionListener接口的简单定义：

```java
public interface TestExecutionListener {
    default void beforeTestClass(TestContext testContext) throws Exception {
    }

    default void prepareTestInstance(TestContext testContext) throws Exception {
    }

    default void beforeTestMethod(TestContext testContext) throws Exception {
    }

    default void afterTestMethod(TestContext testContext) throws Exception {
    }

    default void afterTestClass(TestContext testContext) throws Exception {
    }
}
```

该接口的实现可以在不同的测试执行阶段接收事件。因此，接口中的每个方法都接收一个TestContext对象作为参数。

此TestContext对象包含Spring上下文以及目标测试类和方法的信息。此信息可用于更改测试的行为或扩展其功能。

现在，让我们快速浏览一下这些方法：

+ afterTestClass：在类中的**所有测试都执行后**的后置处理
+ afterTestExecution：在提供的测试上下文中**执行测试方法后立即执行**的后置处理
+ afterTestMethod：在执行底层测试框架的**生命周期after回调后**的后置处理
+ beforeTestClass：在类中的**所有测试都执行前**的前置处理
+ beforeTestExecution：在提供的测试上下文中**执行测试方法前立即执行**的前置处理
+ beforeTestMethod：在执行底层测试框架的**生命周期before回调前**的前置处理
+ prepareTestInstance：准备所提供测试上下文的测试实例

值得注意的是，此接口为所有方法提供了空的默认实现。因此，具体的实现可以选择只覆盖那些自己需要的方法。

## 3. Spring默认的TestExecutionListeners

默认情况下，Spring提供了一些开箱即用的TestExecutionListener实现。

让我们快速看一下其中的每一个：

+ ServletTestExecutionListener：为WebApplicationContext配置Servlet API Mock
+ DirtiesContextBeforeModesTestExecutionListener：处理“before”模式的@DirtiesContext注解
+ DependencyInjectionTestExecutionListener：为测试实例提供依赖注入
+ DirtiesContextTestExecutionListener：处理“after”模式的@DirtiesContext注解
+ TransactionalTestExecutionListener：提供具有默认回滚语义的事务测试执行
+ SqlScriptsTestExecutionListener：运行使用@Sql注解配置的SQL脚本

这些监听器完全按照列出的顺序预先注册。当我们创建一个自定义的TestExecutionListener时，我们将看到更多关于顺序的信息。

## 4. 使用自定义TestExecutionListener

现在，让我们定义一个自己的TestExecutionListener实现：

```java
public class CustomTestExecutionListener implements TestExecutionListener, Ordered {
    private static final Logger logger = LoggerFactory.getLogger(CustomTestExecutionListener.class);

    public void beforeTestClass(TestContext testContext) {
        logger.info("beforeTestClass : {}", testContext.getTestClass());
    }

    public void prepareTestInstance(TestContext testContext) {
        logger.info("prepareTestInstance : {}", testContext.getTestClass());
    }

    public void beforeTestMethod(TestContext testContext) {
        logger.info("beforeTestMethod : {}", testContext.getTestMethod());
    }

    public void afterTestMethod(TestContext testContext) {
        logger.info("afterTestMethod : {}", testContext.getTestMethod());
    }

    public void afterTestClass(TestContext testContext) {
        logger.info("afterTestClass : {}", testContext.getTestClass());
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
```

为简单起见，这个类所做的只是记录一些TestContext信息。

### 4.1 使用@TestExecutionListeners注册自定义监听器

现在，让我们在测试类中使用这个监听器。为此，我们需要使用@TestExecutionListeners注解注册它：

```java
@ExtendWith(SpringExtension.class)
@TestExecutionListeners(value = {CustomTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
@ContextConfiguration(classes = AdditionService.class)
class TestExecutionListenersWithoutMergeModeUnitTest {
    // ...
}
```

重要的是要注意，**使用@TestExecutionListeners注解会取消注册所有默认的监听器**。因此，我们显式添加了DependencyInjectionTestExecutionListener，以便我们可以在测试类中使用自动注入。

如果我们需要任何其他默认监听器，则必须指定它们中的每一个。但是，我们也可以使用该注解的mergeMode属性：

```java
@TestExecutionListeners(value = CustomTestExecutionListener.class, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
```

这里，MERGE_WITH_DEFAULTS表示自己声明的监听器应该与默认监听器合并。

现在，当我们运行上述测试时，监听器将记录它接收到的每个事件：

```shell
15:33:19.186 [main] INFO org.springframework.test.context.support.DefaultTestContextBootstrapper - Using TestExecutionListeners: [cn.tuyucheng.taketoday.testexecutionlisteners.CustomTestExecutionListener@e350b40, org.springframework.test.context.support.DependencyInjectionTestExecutionListener@41a0aa7d]
15:33:19.187 [main] INFO cn.tuyucheng.taketoday.testexecutionlisteners.CustomTestExecutionListener - beforeTestClass : class cn.tuyucheng.taketoday.testexecutionlisteners.TestExecutionListenersWithoutMergeModeUnitTest
15:33:19.199 [main] INFO cn.tuyucheng.taketoday.testexecutionlisteners.CustomTestExecutionListener - prepareTestInstance : class cn.tuyucheng.taketoday.testexecutionlisteners.TestExecutionListenersWithoutMergeModeUnitTest
...
15:33:19.407 [main] INFO cn.tuyucheng.taketoday.testexecutionlisteners.CustomTestExecutionListener - beforeTestMethod : void cn.tuyucheng.taketoday.testexecutionlisteners.TestExecutionListenersWithoutMergeModeUnitTest.whenValidNumbersPassed_thenReturnSum()
15:33:19.434 [main] INFO cn.tuyucheng.taketoday.testexecutionlisteners.CustomTestExecutionListener - afterTestMethod : void cn.tuyucheng.taketoday.testexecutionlisteners.TestExecutionListenersWithoutMergeModeUnitTest.whenValidNumbersPassed_thenReturnSum()
15:33:19.445 [main] INFO cn.tuyucheng.taketoday.testexecutionlisteners.CustomTestExecutionListener - afterTestClass : class cn.tuyucheng.taketoday.testexecutionlisteners.TestExecutionListenersWithoutMergeModeUnitTest
```

### 4.2 自动扫描默认TestExecutionListener实现

如果只在有限数量的测试类中使用，那么使用@TestExecutionListener注册监听器是合适的。但是，将它添加到包含大量测试类的测试套件中可能会变得很麻烦。

我们可以通过利用SpringFactoriesLoader机制提供的自动发现TestExecutionListener实现的支持来解决这个问题。

spring-test模块在其META-INF/spring.factories属性文件中的org.springframework.test.context.TestExecutionListener键下声明所有核心默认监听器。**类似地，我们可以通过在我们自己的META-INF/spring.factories属性文件中使用上述键来注册我们的自定义监听器**：

```properties
org.springframework.test.context.TestExecutionListener=cn.tuyucheng.taketoday.testexecutionlisteners.CustomTestExecutionListener
```

### 4.3 排序默认的TestExecutionListener实现

当Spring通过SpringFactoriesLoader机制扫描默认的TestExecutionListener实现时，它将使用Spring的AnnotationAwareOrderComparator对它们进行排序。这遵循Spring的Ordered接口和用于排序的@Order注解。

请注意，Spring提供的所有默认TestExecutionListener实现都使用适当的值实现了Ordered接口的getOrder方法。因此，我们必须确保我们的自定义TestExecutionListener实现以正确的顺序注册。所以，我们需要在自定义监听器中同样实现Ordered接口：

```java
public class CustomTestExecutionListener implements TestExecutionListener, Ordered {

    // ...

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
```

或者我们也可以在自定义监听器类上使用@Order注解来代替。

## 5. 总结

在本文中，我们了解了如何实现自定义TestExecutionListener。我们还介绍了Spring框架提供的一些默认监听器。