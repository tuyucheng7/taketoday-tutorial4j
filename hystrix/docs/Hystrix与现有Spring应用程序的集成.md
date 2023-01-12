## 1. 概述

在[上一篇文章中](https://www.baeldung.com/introduction-to-hystrix)，我们了解了 Hystrix 的基础知识以及它如何帮助构建容错和弹性应用程序。

有许多现有的 Spring 应用程序调用外部系统，这些应用程序将从 Hystrix 中受益。不幸的是，可能无法重写这些应用程序以集成 Hystrix，但是在[Spring AOP](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/aop.html)的帮助下，可以采用非侵入式的方式集成 Hystrix 。

在本文中，我们将研究如何将 Hystrix 与现有的 Spring 应用程序集成。

## 2. Hystrix 到 Spring 应用程序

### 2.1. 现有应用

让我们看一下应用程序的现有客户端调用程序，它调用我们在上一篇文章中创建的RemoteServiceTestSimulator ：

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

正如我们在上面的代码片段中看到的，invokeRemoteServiceWithOutHystrix方法负责调用RemoteServiceTestSimulator远程服务。当然，真实世界的应用不会这么简单。

### 2.2. 创建环绕建议

为了演示如何集成 Hystrix，我们将使用此客户端作为示例。

为此，我们将定义一个Around建议，它将在执行invokeRemoteService时启动：

```java
@Around("@annotation(com.baeldung.hystrix.HystrixCircuitBreaker)")
public Object circuitBreakerAround(ProceedingJoinPoint aJoinPoint) {
    return new RemoteServiceCommand(config, aJoinPoint).execute();
}
```

上面的建议被设计为一个Around建议，在用@HystrixCircuitBreaker注解的切入点处执行。

现在让我们看看HystrixCircuitBreaker注解的定义：

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HystrixCircuitBreaker {}
```

### 2.3. Hystrix 逻辑

现在让我们看一下RemoteServiceCommand。在示例代码中以静态内部类的形式实现，从而封装Hystrix的调用逻辑：

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

可以在[这里看到](https://github.com/eugenp/tutorials/blob/master/hystrix/src/main/java/com/baeldung/hystrix/HystrixAspect.java)Aspect组件的整个实现。

### 2.4. 使用@HystrixCircuitBreaker注解

定义方面后，我们可以使用@HystrixCircuitBreaker注解我们的客户端方法，如下所示，每次调用注解的方法时都会触发 Hystrix：

```java
@HystrixCircuitBreaker
public String invokeRemoteServiceWithHystrix() throws InterruptedException{
    return new RemoteServiceTestSimulator(remoteServiceDelay).execute();
}
```

下面的集成测试将演示 Hystrix 路由和非 Hystrix 路由之间的区别。

### 2.5. 测试集成

为了演示的目的，我们定义了两种方法执行路由，一种有Hystrix，一种没有。

```java
public class SpringAndHystrixIntegrationTest {

    @Autowired
    private HystrixController hystrixController;

    @Test(expected = HystrixRuntimeException.class)
    public void givenTimeOutOf15000_whenClientCalledWithHystrix_thenExpectHystrixRuntimeException()
      throws InterruptedException {
        hystrixController.withHystrix();
    }

    @Test
    public void givenTimeOutOf15000_whenClientCalledWithOutHystrix_thenExpectSuccess()
      throws InterruptedException {
        assertThat(hystrixController.withOutHystrix(), equalTo("Success"));
    }
}

```

当测试执行时，可以看到没有 Hystrix 的方法调用将等待远程服务的整个执行时间，而 Hystrix 路由将短路并在定义的超时后抛出HystrixRuntimeException，在我们的例子中是 10 秒。

## 3.总结

我们可以为我们想要使用不同配置进行的每个远程服务调用创建一个方面。在下一篇文章中，我们将从项目开始着手集成 Hystrix。