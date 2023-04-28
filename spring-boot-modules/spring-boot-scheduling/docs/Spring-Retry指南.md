## 1. 概述

Spring Retry提供了自动重新调用失败操作的能力。这在错误可能是暂时的(如暂时的网络故障)的情况下很有帮助。

在本教程中，我们将看到使用[Spring Retry](https://github.com/spring-projects/spring-retry)的各种方式：注解、RetryTemplate和回调。

### 延伸阅读

### [使用指数退避和抖动进行更好的重试](https://www.baeldung.com/resilience4j-backoff-jitter)

了解如何使用Resilience4j中的退避和抖动更好地控制应用程序重试。

[阅读更多](https://www.baeldung.com/resilience4j-backoff-jitter)→

### [Resilience4j指南](https://www.baeldung.com/resilience4j)

了解如何使用Resilience4j库中最有用的模块来构建弹性系统。

[阅读更多](https://www.baeldung.com/resilience4j)→

### [在Spring Batch中配置重试逻辑](https://www.baeldung.com/spring-batch-retry-logic)

Spring Batch允许我们在任务上设置重试策略，以便在出现错误时自动重复执行。这里我们看看如何配置它。

[阅读更多](https://www.baeldung.com/spring-batch-retry-logic)→

## 2. Maven依赖

让我们首先**将spring-retry依赖项添加到我们的pom.xml文件中**：

```xml
<dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
    <version>1.2.5.RELEASE</version>
</dependency>
```

我们还需要将Spring AOP添加到我们的项目中：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aspects</artifactId>
    <version>5.2.8.RELEASE</version>
</dependency>
```

查看Maven Central以获得最新版本的[spring-retry](https://search.maven.org/search?q=spring-retry)和[spring-aspects](https://search.maven.org/search?q=a:spring-aspects)依赖项。

## 3. 启用Spring Retry

要在应用程序中启用Spring Retry，**我们需要将@EnableRetry注解添加到我们的@Configuration类**：

```java
@Configuration
@EnableRetry
public class AppConfig {
    // ...
}
```

## 4. 使用Spring Retry

### 4.1 @Retryable不使用@Recover

**我们可以使用@Retryable注解为方法添加重试功能**：

```java
@Service
public interface MyService {

    @Retryable(value = RuntimeException.class)
    void retryService(String sql);
}
```

这里，在抛出RuntimeException时尝试重试。

根据@Retryable的默认行为，**重试最多可能发生3次，两次重试之间有1秒的延迟**。

### 4.2 @Retryable和@Recover

**现在让我们使用@Recover注解添加一个recover方法**：

```java
@Service
public interface MyService {

    @Retryable(value = SQLException.class)
    void retryServiceWithRecovery(String sql) throws SQLException;

    @Recover
    void recover(SQLException e, String sql);
}
```

这里，在抛出SQLException时尝试重试。**当@Retryable方法因指定异常而失败时，@Recover注解定义了一个单独的恢复方法**。

因此，如果retryServiceWithRecovery方法在三次尝试后仍然抛出SqlException，则将调用recover()方法。

恢复处理程序应该具有Throwable类型的第一个参数(可选)和相同的返回类型。以下参数以相同顺序从失败方法的参数列表中填充。

### 4.3 自定义@Retryable的行为

为了自定义重试的行为，**我们可以使用参数maxAttempts和backoff**：

```java
@Service
public interface MyService {

    @Retryable(value = SQLException.class, maxAttempts = 2, backoff = @Backoff(delay = 100))
    void retryServiceWithCustomization(String sql) throws SQLException;
}
```

最多将有两次尝试和100毫秒的延迟。

### 4.4 使用Spring属性

我们还可以在@Retryable注解中使用属性。

为了演示这一点，**我们将了解如何将delay和maxAttempts的值外部化到属性文件中**。

首先，让我们在名为retryConfig.properties的文件中定义属性：

```properties
retry.maxAttempts=2
retry.maxDelay=100
```

然后我们指示我们的@Configuration类加载这个文件：

```java
// ...
@PropertySource("classpath:retryConfig.properties")
public class AppConfig {
    // ...
}
```

最后，**我们可以在@Retryable定义中注入retry.maxAttempts和retry.maxDelay的值**：

```java
@Service
public interface MyService {

    @Retryable(value = SQLException.class, maxAttemptsExpression = "${retry.maxAttempts}",
          backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    void retryServiceWithExternalConfiguration(String sql) throws SQLException;
}
```

请注意，我们现在**使用maxAttemptsExpression和delayExpression而不是maxAttempts和delay**。

## 5. RetryTemplate

### 5.1 RetryOperations

Spring Retry提供了RetryOperations接口，该接口提供了一组execute()方法：

```java
public interface RetryOperations {

    <T> T execute(RetryCallback<T> retryCallback) throws Exception;

    // ...
}
```

RetryCallback是execute()的一个参数，它是一个允许插入需要在失败时重试的业务逻辑的接口：

```java
public interface RetryCallback<T> {

    T doWithRetry(RetryContext context) throws Throwable;
}
```

### 5.2 RetryTemplate配置

RetryTemplate是RetryOperations的实现。

让我们在@Configuration类中配置一个RetryTemplate bean：

```java
@Configuration
public class AppConfig {

    //...

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(2000l);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(2);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }
}
```

RetryPolicy确定何时应重试操作。

SimpleRetryPolicy用于重试固定次数。另一方面，BackOffPolicy用于控制重试之间的退避。

最后，FixedBackOffPolicy在继续之前暂停一段固定的时间。

### 5.3 使用RetryTemplate

要使用重试处理运行代码，我们可以调用retryTemplate.execute()方法：


```java
retryTemplate.execute(new RetryCallback<Void, RuntimeException>() {
    @Override
    public Void doWithRetry(RetryContext arg0) {
        myService.templateRetryService();
        // ...
    }
});
```

我们可以使用lambda表达式代替匿名类：


```java
retryTemplate.execute(arg0 -> {
    myService.templateRetryService();
    return null;
});
```

## 6. 监听器

监听器在重试时提供额外的回调。我们可以将这些用于不同重试的各种横切关注点。

### 6.1 添加回调

回调在RetryListener接口中提供：

```java
public class DefaultListenerSupport extends RetryListenerSupport {

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        logger.info("onClose");
        // ...
        super.close(context, callback, throwable);
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        logger.info("onError"); 
        // ...
        super.onError(context, callback, throwable);
    }

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        logger.info("onOpen");
        // ...
        return super.open(context, callback);
    }
}
```

open和close回调出现在整个重试之前和之后，而onError适用于各个RetryCallback调用。

### 6.2 注册监听器

接下来，我们将监听器(DefaultListenerSupport)注册到我们的RetryTemplate bean：

```java
@Configuration
public class AppConfig {
    // ...

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        // ...
        retryTemplate.registerListener(new DefaultListenerSupport());
        return retryTemplate;
    }
}
```

## 7. 测试结果

为了完成我们的示例，让我们验证结果：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
      classes = AppConfig.class,
      loader = AnnotationConfigContextLoader.class)
public class SpringRetryIntegrationTest {

    @Autowired
    private MyService myService;

    @Autowired
    private RetryTemplate retryTemplate;

    @Test(expected = RuntimeException.class)
    public void givenTemplateRetryService_whenCallWithException_thenRetry() {
        retryTemplate.execute(arg0 -> {
            myService.templateRetryService();
            return null;
        });
    }
}
```

从测试日志中可以看出，我们已经正确配置了RetryTemplate和RetryListener：

```shell
2020-01-09 20:04:10 [main] INFO  c.t.t.s.DefaultListenerSupport - onOpen 
2020-01-09 20:04:10 [main] INFO  c.t.t.springretry.MyServiceImpl
- throw RuntimeException in method templateRetryService() 
2020-01-09 20:04:10 [main] INFO  c.t.t.s.DefaultListenerSupport - onError 
2020-01-09 20:04:12 [main] INFO  c.t.t.springretry.MyServiceImpl
- throw RuntimeException in method templateRetryService() 
2020-01-09 20:04:12 [main] INFO  c.t.t.s.DefaultListenerSupport - onError 
2020-01-09 20:04:12 [main] INFO  c.t.t.s.DefaultListenerSupport - onClose
```

## 8. 总结

在本文中，我们了解了如何使用注解、RetryTemplate和回调监听器来使用Spring Retry。