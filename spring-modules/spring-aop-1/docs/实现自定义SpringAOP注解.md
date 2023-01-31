## 1. 概述

在本文中，我们演示使用Spring中的AOP支持来实现自定义AOP注解。

首先，我们给出AOP的高层次概念，解释它是什么以及它的优点。在此之后，逐步实现我们的注解，建立对AOP概念的更深入理解。

## 2. 什么是AOP注解

简单地说，AOP代表面向切面编程。从本质上讲，**它是一种在不修改代码的情况下向现有代码添加行为(功能)的方法**。

关于AOP的详细介绍，有AOP[切入点](https://www.baeldung.com/spring-aop-pointcut-tutorial)和[通知](https://www.baeldung.com/spring-aop-advice-tutorial)的文章，本文假设我们已经有了基本的知识。

我们将在本文中实现的AOP类型是注解驱动的，如果使用过Spring的[@Transactional](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/annotation/Transactional.html)注解，你应该对此很熟悉：

```java
@Transactional
public void orderGoods(Order order) {
   // A series of database calls to be performed in a transaction
}
```

**这里的关键是非侵入性**。通过使用注解元数据，我们的核心业务逻辑不会被事务代码污染，这使得推理、重构和隔离测试变得更容易。

有时，开发Spring应用程序的人可以将其视为“Spring魔法”，而无需详细考虑它是如何工作的。实际上，发生的事情并不是特别复杂。但是，完成本文中的步骤后，我们将能够创建自己的自定义注解，以便理解和利用 AOP。

## 3. Maven依赖

首先，让我们添加我们的[Maven](https://search.maven.org/search?q=a:spring-boot-starter-aop)依赖项。

对于这个例子，我们将使用Spring Boot，因为它的约定优于配置方法可以让我们尽快启动并运行：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.2.2.RELEASE</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>
</dependencies>
```

请注意，我们已经包含了AOP Starter，它引入了我们开始实现切面所需的库。

## 4. 创建自定义注解

我们要创建的注解将用于记录方法执行所需的时间：

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogExecutionTime {

}
```

尽管该注解的定义相当简单，但值得注意的是两个元注解的作用。

@Target注解表示我们的@LogExecutionTime注解可以用在哪些地方，这里我们使用的是ElementType.Method，意味着它只能用于方法上。如果我们尝试在其他任何地方使用此注解，那么我们的代码将无法编译。因为我们的注解只是用于记录方法执行时间，因此无需太多配置。

而[@Retention](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/annotation/Retention.html)只是说明注解在运行时是否对JVM可用，默认情况下它不是，所以Spring AOP将无法看到注解，因此我们在这里重新配置它的值。

## 5. 创建切面

现在我们有了注释，让我们创建切面。切面只是将封装我们的横切关注点的模块，在我们的例子中是方法执行时间日志记录。它只是一个用[@Aspect](https://www.eclipse.org/aspectj/doc/released/aspectj5rt-api/org/aspectj/lang/annotation/Aspect.html?is-external=true)标注的类：

```java
@Aspect
@Component
public class ExampleAspect {

}
```

我们还添加了[@Component](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/stereotype/Component.html)注解，因为我们的切面类也需要是一个Spring bean才能被检测到。本质上，这只是一个普通类，我们可以在其中实现我们希望自定义注解注入的逻辑。

## 6. 创建切入点和通知

现在，让我们创建我们的切入点和通知。这将是一个存在于我们切面类的带注解的方法：

```java
@Around("@annotation(cn.tuyucheng.taketoday.LogExecutionTime)")
public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    return joinPoint.proceed();
}
```

从技术上讲，这还并没有改变程序的任何行为，但是仍然有很多事情需要分析。

首先，我们用[@Around](https://www.eclipse.org/aspectj/doc/released/aspectj5rt-api/org/aspectj/lang/annotation/Around.html?is-external=true)标注了我们的方法。这是我们的通知，Around通知意味着我们在方法执行之前和之后添加额外的代码。还有其他类型的通知，例如@Before和@After，但它们不在本文讨论范围之内。

接下来，我们的@Around注解有一个切入点参数，切入点只是表明将这个通知应用于任何带有@LogExecutionTime注解的方法。

logExecutionTime()方法本身就是我们的通知，只有一个参数[ProceedingJoinPoint](https://www.eclipse.org/aspectj/doc/next/runtime-api/org/aspectj/lang/ProceedingJoinPoint.html)。在我们的例子中，这将是一个使用@LogExecutionTime注解标注的执行方法。

最后，当我们的带有@LogExecutionTime注解的方法最终被调用时，会发生的是我们的通知将首先被调用，然后由我们的通知决定下一步该做什么。在我们的例子中，通知除了调用proceed()之外什么都不做，它只是调用原始的带@LogExecutionTime注解的方法。

## 7. 记录我们的执行时间

现在有了最基本的骨架，我们需要做的就是在我们的通知中添加一些额外的逻辑。除了调用原始方法之外，我们需要记录方法的执行时间，让我们将这个额外的行为添加到我们的通知中：

```java
@Around("@annotation(cn.tuyucheng.taketoday.LogExecutionTime)")
public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    final long start = System.currentTimeMillis();

    final Object proceed = joinPoint.proceed();

    final long executionTime = System.currentTimeMillis() - start;
    System.out.println(joinPoint.getSignature() + " executed in " + executionTime + "ms");
    return proceed;
}
```

同样，我们在这里没有做任何特别复杂的事情，我们只是记录了当前时间，执行了目标方法，然后将方法执行消耗的时间打印到控制台。我们还记录了方法签名，这是通过ProceedingJoinPoint实例实现的。如果愿意的话，我们还可以访问其他信息，例如方法参数。

现在，让我们尝试用@LogExecutionTime标注一个方法，然后执行它来看看会发生什么。请注意，这必须是一个Spring Bean才能正常工作：

```java
@Component
public class Service {

    @LogExecutionTime
    public void serve() throws InterruptedException {
        Thread.sleep(2000);
    }
}
```

执行后，我们应该会看到控制台打印了以下内容：

```shell
void cn.tuyucheng.taketoday.Service.serve() executed in 2004ms
```

## 8. 总结

在本文中，我们通过Spring AOP支持创建我们的自定义注解，我们可以将其应用于Spring Bean以在运行时向它们注入额外的行为。