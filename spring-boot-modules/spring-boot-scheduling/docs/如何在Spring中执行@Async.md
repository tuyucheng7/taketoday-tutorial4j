## 1. 概述

在本教程中，我们将探讨Spring中的**异步执行支持和@Async注解**。

简单的说，用@Async标注bean的一个方法，就会让它在一个**单独的线程中执行**。换句话说，调用者不会等待被调用方法的完成。

Spring中一个有趣的方面是框架中的事件支持在必要时[也支持异步处理](https://www.baeldung.com/spring-events)。

### 延伸阅读

### [Spring Events](https://www.baeldung.com/spring-events)

Spring中事件的基础-创建一个简单的自定义事件，发布它并在监听器中处理它。

[阅读更多](https://www.baeldung.com/spring-events)→

### [使用@Async进行Spring Security上下文传播](https://www.baeldung.com/spring-security-async-principal-propagation)

使用@Async注解时传播Spring Security上下文的简短示例

[阅读更多](https://www.baeldung.com/spring-security-async-principal-propagation)→

### [Servlet 3异步支持与Spring MVC和Spring Security](https://www.baeldung.com/spring-mvc-async-security)

快速介绍Spring Security对Spring MVC中异步请求的支持。

[阅读更多](https://www.baeldung.com/spring-mvc-async-security)→

## 2. 启用异步支持 

让我们从**使用Java配置启用异步处理**开始。

为此，我们将@EnableAsync注解添加到配置类中：

```java
@Configuration
@EnableAsync
public class SpringAsyncConfig {
    // ...
}
```

启用注解就足够了。但是也有一些简单的配置选项：

-   **annotation**-默认情况下，@EnableAsync检测Spring的@Async注解和EJB 3.1 javax.ejb.Asynchronous。我们也可以使用此选项来检测其他用户定义的注解类型。
-   **mode**表示应该使用的通知类型-基于JDK代理或AspectJ编织。
-   **proxyTargetClass**表示应该使用的代理类型-CGLIB或JDK。仅当模式设置为AdviceMode.PROXY时，此属性才有效。
-   **order**设置应用AsyncAnnotationBeanPostProcessor的顺序。默认情况下，它最后运行，以便它可以考虑所有现有代理。

我们还可以使用task命名空间启用**XML配置**的异步处理：

```xml
<task:executor id="myexecutor" pool-size="5"  />
<task:annotation-driven executor="myexecutor"/>
```

## 3. @Async注解

首先，让我们回顾一下规则。@Async有两个限制：

-   它必须仅应用于公共方法。
-   自调用(从同一个类中调用异步方法)将不起作用。

原因很简单：**该方法需要公开**(public)以便可以被代理。**而自调用是行不通的**，因为它绕过了代理，直接调用了底层方法。

### 3.1 具有Void返回类型的方法

这是将具有void返回类型的方法配置为异步运行的简单方法：

```java
@Async
public void asyncMethodWithVoidReturnType() {
    System.out.println("Execute method asynchronously. " + Thread.currentThread().getName());
}
```

### 3.2 具有返回类型的方法

我们还可以通过将实际返回包装在Future中，将@Async应用于具有返回类型的方法：

```java
@Async
public Future<String> asyncMethodWithReturnType() {
    System.out.println("Execute method asynchronously - " + Thread.currentThread().getName());
    try {
        Thread.sleep(5000);
        return new AsyncResult<String>("hello world !!!!");
    } catch (InterruptedException e) {
        //
    }

    return null;
}
```

Spring还提供了一个实现Future的AsyncResult类。我们可以使用它来跟踪异步方法执行的结果。

现在让我们调用上述方法并使用Future对象检索异步进程的结果。

```java
public void testAsyncAnnotationForMethodsWithReturnType() throws InterruptedException, ExecutionException {
    System.out.println("Invoking an asynchronous method. " + Thread.currentThread().getName());
    Future<String> future = asyncAnnotationExample.asyncMethodWithReturnType();

    while (true) {
        if (future.isDone()) {
            System.out.println("Result from asynchronous process - " + future.get());
            break;
        }
        System.out.println("Continue doing something else. ");
        Thread.sleep(1000);
    }
}
```

## 4. Executor

默认情况下，Spring使用SimpleAsyncTaskExecutor实际异步运行这些方法。但是我们可以在两个级别覆盖默认值：应用程序级别或单个方法级别。

### 4.1 在方法级别覆盖执行器

我们需要在配置类中声明所需的执行器：

```java
@Configuration
@EnableAsync
public class SpringAsyncConfig {

    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        return new ThreadPoolTaskExecutor();
    }
}
```

然后我们应该在@Async中提供执行器名称作为属性：

```java
@Async("threadPoolTaskExecutor")
public void asyncMethodWithConfiguredExecutor() {
    System.out.println("Execute method with configured executor - " + Thread.currentThread().getName());
}
```

### 4.2 在应用程序级别覆盖执行器

配置类应该实现AsyncConfigurer接口。因此，它必须实现getAsyncExecutor()方法。在这里，我们将返回整个应用程序的执行器。现在，这将成为运行带有@Async注解的方法的默认执行程序：

```java
@Configuration
@EnableAsync
public class SpringAsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        return new ThreadPoolTaskExecutor();
    }
}
```

## 5. 异常处理

当方法返回类型是Future时，异常处理很容易。Future.get()方法将抛出异常。

**但是如果返回类型是void，异常将不会传播到调用线程**。因此，我们需要添加额外的配置来处理异常。

我们将通过实现AsyncUncaughtExceptionHandler接口来创建自定义异步异常处理程序。当存在任何未捕获的异步异常时调用handleUncaughtException()方法：

```java
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
        System.out.println("Exception message - " + throwable.getMessage());
        System.out.println("Method name - " + method.getName());
        for (Object param : obj) {
            System.out.println("Parameter value - " + param);
        }
    }
}
```

在上一节中，我们查看了配置类实现的AsyncConfigurer接口。作为其中的一部分，我们还需要覆盖getAsyncUncaughtExceptionHandler()方法以返回我们自定义的异步异常处理程序：

```java
@Override
public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return new CustomAsyncExceptionHandler();
}
```

## 6. 总结

在本文中，我们研究了使用Spring运行异步代码。

我们从非常基本的配置和注解开始，以使其正常工作。但我们也研究了更高级的配置，例如提供我们自己的执行器或异常处理策略。