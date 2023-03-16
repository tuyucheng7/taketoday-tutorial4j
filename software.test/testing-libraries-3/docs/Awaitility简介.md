## 1. 概述

异步系统的一个常见问题是很难为它们编写可读的测试，这些测试专注于业务逻辑并且不受同步、超时和并发控制的污染。

在本文中，我们将介绍[Awaitility](http://www.awaitility.org/)-**一个为异步系统测试提供简单的领域特定语言(DSL)的库**。

通过Awaitility，**我们可以用易于阅读的DSL表达我们对系统的期望**。

## 2. 依赖

我们需要将Awaitility依赖项添加到我们的pom.xml中。

对于大多数用例来说，awaitility库就足够了。如果我们想使用基于代理的条件，我们还需要提供awaitility-proxy库：

```xml
<dependency>
    <groupId>org.awaitility</groupId>
    <artifactId>awaitility</artifactId>
    <version>3.0.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.awaitility</groupId>
    <artifactId>awaitility-proxy</artifactId>
    <version>3.0.0</version>
    <scope>test</scope>
</dependency>
```

你可以在Maven Central上找到最新版本的[awaitility](https://central.sonatype.com/artifact/org.awaitility/awaitility/4.2.0)和[awaitility-proxy](https://central.sonatype.com/artifact/org.awaitility/awaitility-proxy/3.1.6)库。

## 3. 创建异步服务

让我们编写一个简单的异步服务并对其进行测试：

```java
public class AsyncService {
    private final int DELAY = 1000;
    private final int INIT_DELAY = 2000;

    private AtomicLong value = new AtomicLong(0);
    private Executor executor = Executors.newFixedThreadPool(4);
    private volatile boolean initialized = false;

    void initialize() {
        executor.execute(() -> {
            sleep(INIT_DELAY);
            initialized = true;
        });
    }

    boolean isInitialized() {
        return initialized;
    }

    void addValue(long val) {
        throwIfNotInitialized();
        executor.execute(() -> {
            sleep(DELAY);
            value.addAndGet(val);
        });
    }

    public long getValue() {
        throwIfNotInitialized();
        return value.longValue();
    }

    private void sleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
        }
    }

    private void throwIfNotInitialized() {
        if (!initialized) {
            throw new IllegalStateException("Service is not initialized");
        }
    }
}
```

## 4. Awaitility测试

现在，让我们创建测试类：

```java
public class AsyncServiceLongRunningManualTest {
    private AsyncService asyncService;

    @Before
    public void setUp() {
        asyncService = new AsyncService();
    }

    //...
}
```

我们的测试检查我们的服务初始化是否发生在调用initialize方法后指定的超时期限(默认10秒)内。

此测试用例仅等待服务初始化状态更改，或者如果状态未发生更改则抛出ConditionTimeoutException。

状态由Callable获取，该Callable在指定的初始延迟(默认100毫秒)后以定义的时间间隔(默认100毫秒)轮询我们的服务。在这里，我们使用超时、间隔和延迟的默认设置：

```java
asyncService.initialize();
await()
    .until(asyncService::isInitialized);
```

在这里，我们使用await-Awaitility类的静态方法之一。它返回一个ConditionFactory类的实例。为了提高可读性，我们还可以使用其他方法，例如given。

可以使用Awaitility类中的静态方法更改默认时间参数：

```java
Awaitility.setDefaultPollInterval(10, TimeUnit.MILLISECONDS);
Awaitility.setDefaultPollDelay(Duration.ZERO);
Awaitility.setDefaultTimeout(Duration.ONE_MINUTE);
```

在这里我们可以看到Duration类的使用，它为最常用的时间段提供了有用的常量。

我们还可以**为每个await调用提供自定义计时值**。在这里，我们期望初始化最多在5秒后发生，并且至少在100毫秒后发生，轮询间隔为100毫秒：

```java
asyncService.initialize();
await()
    .atLeast(Duration.ONE_HUNDRED_MILLISECONDS)
    .atMost(Duration.FIVE_SECONDS)
  .with()
    .pollInterval(Duration.ONE_HUNDRED_MILLISECONDS)
    .until(asyncService::isInitialized);
```

值得一提的是，ConditionFactory包含其他方法，例如with、then、and、given。这些方法不执行任何操作，只是返回this，但它们可能有助于提高测试条件的可读性。

## 5. 使用匹配器

Awaitility还允许使用Hamcrest匹配器来检查表达式的结果。例如，我们可以在调用addValue方法后检查我们的long值是否按预期更改：

```java
asyncService.initialize();
await()
    .until(asyncService::isInitialized);
long value = 5;
asyncService.addValue(value);
await()
    .until(asyncService::getValue, equalTo(value));
```

请注意，在此示例中，我们使用第一个await调用来等待服务初始化。否则，getValue方法将抛出IllegalStateException。

## 6. 忽略异常

有时，我们会遇到一个方法在异步作业完成之前抛出异常的情况。在我们的服务中，它可以是在初始化服务之前对getValue方法的调用。

Awaitility提供了忽略此异常而不会使测试失败的可能性。

例如，让我们在初始化后检查getValue结果是否等于0，忽略IllegalStateException：

```java
asyncService.initialize();
given().ignoreException(IllegalStateException.class)
    .await().atMost(Duration.FIVE_SECONDS)
    .atLeast(Duration.FIVE_HUNDRED_MILLISECONDS)
    .until(asyncService::getValue, equalTo(0L));
```

## 7. 使用代理

如第2节所述，我们需要包含awaitility-proxy以使用基于代理的条件。代理的想法是在不实现Callable或Lambda表达式的情况下为条件提供真正的方法调用。

让我们使用AwaitilityClassProxy.to静态方法来检查AsyncService是否已初始化：

```java
asyncService.initialize();
await()
    .untilCall(to(asyncService).isInitialized(), equalTo(true));
```

## 8. 访问字段

Awaitility甚至可以访问私有字段以对其执行断言。在下面的示例中，我们可以看到另一种获取服务初始化状态的方法：

```java
asyncService.initialize();
await()
  .until(fieldIn(asyncService)
  .ofType(boolean.class)
  .andWithName("initialized"), equalTo(true));
```

## 9. 总结

在这个快速教程中，我们介绍了Awaitility库，熟悉了它用于测试异步系统的基本DSL，并看到了一些使该库灵活且易于在实际项目中使用的高级功能。