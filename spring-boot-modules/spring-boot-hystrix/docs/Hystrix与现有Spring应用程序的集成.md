## 1. 概述

在[上一篇文章](https://www.baeldung.com/introduction-to-hystrix)中，我们了解了Hystrix的基础知识以及它如何帮助构建容错和弹性应用程序。

**有许多现有的Spring应用程序调用外部系统，这些应用程序可以从Hystrix中受益**。不幸的是，可能无法重写这些应用程序以集成Hystrix，但是在[Spring AOP](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/aop.html)的帮助下，可以采用非侵入式的方式集成Hystrix。

在本文中，我们将研究如何将Hystrix与现有的Spring应用程序集成。

## 2. 集成Hystrix到Spring应用程序

### 2.1 现有应用程序

让我们看一下应用程序的现有客户端调用程序，它调用我们在上一篇文章中创建的RemoteServiceTestSimulator：

```java
@Component("springClient")
public class SpringExistingClient {

    @Value("${remoteservice.timeout}")
    private int remoteServiceDelay;

    public String invokeRemoteServiceWithOutHystrix() throws InterruptedException {
        return new RemoteServiceTestSimulator(remoteServiceDelay).execute();
    }
}
```

正如我们在上面的代码片段中看到的，invokeRemoteServiceWithOutHystrix方法负责调用RemoteServiceTestSimulator远程服务。当然，现实世界的应用不会这么简单。

### 2.2 创建环绕通知

为了演示如何集成Hystrix，我们将使用此客户端作为示例。

为此，**我们将定义一个@Around通知，它将在执行invokeRemoteService时启动**：

```java
@Around("@annotation(cn.tuyucheng.taketoday.hystrix.HystrixCircuitBreaker)")
public Object circuitBreakerAround(ProceedingJoinPoint aJoinPoint) {
    return new RemoteServiceCommand(config, aJoinPoint).execute();
}
```

上面的通知被设计为一个环绕通知，在用@HystrixCircuitBreaker标注的切入点处执行。

现在让我们看看HystrixCircuitBreaker注解的定义：

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HystrixCircuitBreaker {}
```

### 2.3 Hystrix逻辑

现在让我们看一下RemoteServiceCommand。它在示例代码中以静态内部类的形式实现，从而封装Hystrix的调用逻辑：

```java
private static class RemoteServiceCommand extends HystrixCommand<String> {
    private ProceedingJoinPoint joinPoint;

    RemoteServiceCommand(Setter config, ProceedingJoinPoint joinPoint) {
        super(config);
        this.joinPoint = joinPoint;
    }

    @Override
    protected String run() throws Exception {
        try {
            return (String) joinPoint.proceed();
        } catch (Throwable th) {
            throw new Exception(th);
        }
    }
}
```

可以在[这里](https://github.com/tu-yucheng/taketoday-tutorial4j/blob/master/spring-boot-modules/spring-boot-hystrix/src/main/java/cn/tuyucheng/taketoday/hystrix/HystrixAspect.java)查看Aspect组件的整个实现。

### 2.4 使用@HystrixCircuitBreaker注解

定义切面后，我们可以使用@HystrixCircuitBreaker标注我们的客户端方法，如下所示，每次调用标注的方法时都会触发Hystrix：

```java
@HystrixCircuitBreaker
public String invokeRemoteServiceWithHystrix() throws InterruptedException{
    return new RemoteServiceTestSimulator(remoteServiceDelay).execute();
}
```

下面的集成测试将演示Hystrix路由和非Hystrix路由之间的区别。

### 2.5 测试集成

为了演示的目的，我们定义了两种方法执行路由，一种有Hystrix，另一种没有。

```java
public class SpringAndHystrixIntegrationTest {

    @Autowired
    private HystrixController hystrixController;

    @Test(expected = HystrixRuntimeException.class)
    public void givenTimeOutOf15000_whenClientCalledWithHystrix_thenExpectHystrixRuntimeException() throws InterruptedException {
        hystrixController.withHystrix();
    }

    @Test
    public void givenTimeOutOf15000_whenClientCalledWithOutHystrix_thenExpectSuccess() throws InterruptedException {
        assertThat(hystrixController.withOutHystrix(), equalTo("Success"));
    }
}
```

当测试执行时，可以看到没有Hystrix的方法调用将等待远程服务的整个执行时间，而Hystrix路由将短路并在定义的超时后抛出HystrixRuntimeException，在我们的例子中是10秒。

## 3. 总结

我们可以为想要使用不同配置进行的每个远程服务调用创建一个切面。在下一篇文章中，我们将从项目开始着手集成Hystrix。