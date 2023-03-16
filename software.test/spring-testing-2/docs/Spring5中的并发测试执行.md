## 1. 概述

从JUnit 4开始，测试可以并行运行以加快大型测试套件的速度。问题在于Spring 5之前的Spring TestContext框架不完全支持并发测试执行。

在这篇快速文章中，我们将展示如何**使用Spring 5在Spring项目中并发运行我们的测试**。

## 2. Maven设置

提醒一下，要并行运行JUnit测试，我们需要配置[maven-surefire-plugin](https://central.sonatype.com/artifact/org.apache.maven.plugins/maven-surefire-plugin/3.0.0)以启用该功能：

```xml
<build>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>2.22.2</version>
        <configuration>
            <parallel>methods</parallel>
            <useUnlimitedThreads>true</useUnlimitedThreads>
        </configuration>
    </plugin>
</build>
```

你可以查看[参考文档](http://maven.apache.org/surefire/maven-surefire-plugin/examples/fork-options-and-parallel-execution.html)以获取有关并行测试执行的更详细配置。

## 3. 并行测试

以下示例测试在Spring 5之前的版本并行运行时将失败。

但是，它会在Spring 5中顺利运行：

```java
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Spring5JUnit4ConcurrentIntegrationTest.SimpleConfiguration.class)
public class Spring5JUnit4ConcurrentIntegrationTest implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;
    private boolean beanInitialized = false;

    @Override
    public final void afterPropertiesSet() throws Exception {
        this.beanInitialized = true;
    }

    @Override
    public final void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public final void verifyApplicationContextSet() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        assertNotNull("The application context should have been set due to ApplicationContextAware semantics.", this.applicationContext);
    }

    @Test
    public final void verifyBeanInitialized() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        assertTrue("This test bean should have been initialized due to InitializingBean semantics.", this.beanInitialized);
    }

    @Configuration
    public static class SimpleConfiguration {
    }
}
```

按顺序运行时，上述测试大约需要6秒才能通过。通过并发执行，它只需要大约4.5秒-这对于我们在更大的套件中可以节省的时间来说是非常典型的。

## 4. 原理

框架的早期版本不支持并行运行测试的主要原因是由于TestContextManager对TestContext的管理。

在Spring 5中，[TestContextManager](http://docs.spring.io/spring/docs/5.0.0.M5/javadoc-api/org/springframework/test/context/TestContextManager.html)使用ThreadLocal [TestContext](http://docs.spring.io/spring/docs/5.0.0.M5/javadoc-api/org/springframework/test/context/TestContext.html)来确保每个线程中对TestContext的操作不会相互干扰。因此，大多数方法级别和类级别的并发测试都保证了线程安全：

```java
public class TestContextManager {

    // ...
    private final TestContext testContext;

    private final ThreadLocal<TestContext> testContextHolder = new ThreadLocal<TestContext>() {
        protected TestContext initialValue() {
            return copyTestContext(TestContextManager.this.testContext);
        }
    };

    public final TestContext getTestContext() {
        return this.testContextHolder.get();
    }

    // ...
}
```

注意，并发支持并不适用于所有类型的测试；**我们需要排除以下测试**：

+ 更改外部共享状态，例如缓存、数据库、消息队列等中的状态。
+ 需要特定的执行顺序，例如，使用JUnit的[@FixMethodOrder](http://junit.org/junit4/javadoc/4.12/org/junit/FixMethodOrder.html)的测试。
+ 修改ApplicationContext，一般用[@DirtiesContext](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/test/annotation/DirtiesContext.html)标记。

## 5. 总结

在本快速教程中，我们展示了一个使用Spring 5并行运行测试的基本示例。