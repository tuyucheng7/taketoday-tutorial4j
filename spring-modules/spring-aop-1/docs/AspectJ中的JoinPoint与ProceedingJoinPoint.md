## 1. 概述

在这个简短的教程中，我们介绍AspectJ中JoinPoint和ProceedingJoinPoint接口之间的区别。

## 2. JoinPoint

JoinPoint是一个AspectJ的接口，**它提供对给定连接点可用状态的反射访问，例如方法参数、返回值或抛出的异常**。
它还提供有关方法本身的所有静态信息(如方法名，返回值类型等)。

我们可以将它与@Before、@After、@AfterThrowing和@AfterReturning通知一起使用。
这些切入点将分别在方法执行前、执行后、返回值后、抛出异常之后或仅在方法返回值之后执行。

为了更好地理解，让我们看一个基本的例子。
首先，我们需要声明一个切入点，我们将定义为ArticleService类中getArticleList()方法的每次执行：

```java

@Service
public class ArticleService {

    public List<String> getArticleList() {
        return Arrays.asList("Article 1", "Article 2");
    }

    public List<String> getArticleList(String startsWithFilter) {
        if (StringUtils.isBlank(startsWithFilter))
            throw new IllegalArgumentException("startsWithFilter can't be blank");
        return getArticleList().stream().filter(a -> a.startsWith(startsWithFilter)).collect(toList());
    }
}

@Aspect
@Component
public class JoinPointBeforeAspect {
    private static final Logger log = Logger.getLogger(JoinPointBeforeAspect.class.getName());

    @Pointcut("execution(* cn.tuyucheng.taketoday.joinpoint.ArticleService.getArticleList(..))")
    public void articleListPointcut() {
    }
}
```

接下来，我们可以定义通知，在我们的示例中，我们将使用@Before通知：

```java
public class JoinPointBeforeAspect {

    @Before("articleListPointcut()")
    public void beforeAdvice(JoinPoint joinPoint) {
        log.info(format("Method %s executed with %s arguments",
                joinPoint.getStaticPart().getSignature(),
                Arrays.toString(joinPoint.getArgs())
        ));
    }
}
```

在上面的例子中，我们使用@Before通知来记录方法执行及其参数。一个类似的用例是记录我们代码中发生的异常：

```java

@Aspect
@Component
public class JoinPointAfterThrowingAspect {
    private static final java.util.logging.Logger log = Logger.getLogger(JoinPointAfterThrowingAspect.class.getName());

    @Pointcut("execution(* cn.tuyucheng.taketoday.joinpoint.ArticleService.getArticleList(..))")
    public void articleListPointcut() {
    }

    @AfterThrowing(pointcut = "articleListPointcut()", throwing = "e")
    public void logExceptions(JoinPoint jp, Exception e) {
        log.severe(e.getMessage());
    }
}
```

通过使用@AfterThrowing通知，我们确保仅在发生异常时才进行日志记录。

## 3. ProceedingJoinPoint

ProceedingJoinPoint是JoinPoint的扩展，它公开了额外的proceed()方法。
**当proceed()方法被调用时，代码执行会跳转到下一个通知或目标方法，它使我们能够控制代码流并决定是否继续进行进一步的调用**。

它只能与@Around通知一起使用，围绕整个方法调用：

```java

@Aspect
@Component
public class JoinPointAroundCacheAspect {

    public final static Map<Object, Object> CACHE = new HashMap<>();

    @Pointcut("execution(* cn.tuyucheng.taketoday.joinpoint.ArticleService.getArticleList(..))")
    public void articleListPointcut() {
    }

    @Around("articleListPointcut()")
    public Object aroundAdviceCache(ProceedingJoinPoint pjp) throws Throwable {
        Object articles = CACHE.get(pjp.getArgs());
        if (articles == null) {
            articles = pjp.proceed(pjp.getArgs());
            CACHE.put(pjp.getArgs(), articles);
        }
        return articles;
    }
}
```

在上面的示例中，我们举例演示了@Around通知最常用的用法之一。
**只有当CACHE中没有返回结果时，才会调用实际的方法，这正是Spring中@Cache注解的实现原理**。

我们还可以使用ProceedingJoinPoint和@Around通知实现重试操作，以防出现任何异常：

```java

@Aspect
@Component
public class JoinPointAroundExceptionAspect {
    private static final java.util.logging.Logger log = Logger.getLogger(JoinPointAroundExceptionAspect.class.getName());

    @Pointcut("execution(* cn.tuyucheng.taketoday.joinpoint.ArticleService.getArticleList(..))")
    public void articleListPointcut() {
    }

    @Around("articleListPointcut()")
    public Object aroundAdviceException(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return pjp.proceed(pjp.getArgs());
        } catch (Throwable e) {
            log.severe(e.getMessage());
            log.info("Retrying operation");
            return pjp.proceed(pjp.getArgs());
        }
    }
}
```

例如，此解决方案可用于在网络中断的情况下重试HTTP调用。

## 4. 总结

在本文中，我们介绍了AspectJ中的JoinPoint和ProceedingJoinPoint之间的区别。