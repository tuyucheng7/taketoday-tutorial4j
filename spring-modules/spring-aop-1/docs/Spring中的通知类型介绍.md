## 1. 概述

在本文中，我们介绍可以在Spring中创建的不同的AOP通知类型。

**通知是切面在特定连接点采取的操作，不同类型的通知包括“around”、“before”和“after”通知**。
切面的主要目的是支持横切关注点，例如日志记录、分析、缓存和事务管理。

如果你想更深入地了解切入点表达式，请查看[之前](Spring切入点表达式介绍.md)的文章。

## 2. 启用通知

使用Spring，你可以使用AspectJ注解声明通知，但你必须首先将@EnableAspectJAutoProxy注解应用于你的配置类，
此注解启用处理带有@Aspect注解的组件支持。

```java

@Configuration
@EnableAspectJAutoProxy
public class AopConfiguration {
    // ...
}
```

### 2.1 Spring Boot

在Spring Boot项目中，我们不必显式地使用@EnableAspectJAutoProxy注解。
如果类路径上包含对应的AOP依赖，则有一个专用的AopAutoConfiguration自动启用Spring的AOP支持。

## 3. Before通知

**顾名思义，这个通知在连接点之前执行**，除非抛出异常，否则它不会阻止目标方法的继续执行。

考虑以下切面，它在调用实际方法之前简单地记录方法名称：

```java

@Component
@Aspect
public class LoggingAspect {
    private static final Logger LOGGER = Logger.getLogger(LoggingAspect.class.getName());

    private final ThreadLocal<SimpleDateFormat> sdf = ThreadLocal.withInitial(() -> new SimpleDateFormat("[yyyy-MM-dd hh:mm:ss:SSS]"));

    @Pointcut("within(cn.tuyucheng.taketoday..*) && execution(* cn.tuyucheng.taketoday.pointcutadvice.dao.FooDao.*(..))")
    public void repositoryMethods() {
    }

    @Before("repositoryMethods()")
    public void logMethodCall(JoinPoint jp) {
        String methodName = jp.getSignature().getName();
        LOGGER.info(sdf.get().format(new Date()) + methodName);
    }
}
```

logMethodCall通知将在repositoryMethods切入点定义的任何Repository方法之前执行。

## 4. After通知

**使用@After注解声明的After通知在匹配的方法执行后执行，无论是否引发异常**。

在某些方面，它类似于finally语句块，如果你需要仅在正常执行后触发通知，则应使用@AfterReturning注解声明的after-returning通知。
如果你希望仅在目标方法抛出异常时触发你的通知，你应该使用after-throwing通知，通过使用@AfterThrowing注解声明。

假设我们希望在创建Foo的新实例时通知某些应用程序组件，我们可以从FooDao发布一个事件，但这违反了单一责任原则。

```java
public class FooDao extends ApplicationEvent {

    public FooDao(Object source) {
        super(source);
    }
}
```

相反，我们可以通过定义以下切面来实现这一点：

```java

@Component
@Aspect
public class PublishingAspect {
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Pointcut("within(cn.tuyucheng.taketoday..*) && execution(* cn.tuyucheng.taketoday.pointcutadvice.dao.FooDao.*(..))")
    public void repositoryMethods() {
    }

    @Pointcut("within(cn.tuyucheng.taketoday..*) && execution(* cn.tuyucheng.taketoday.pointcutadvice.dao.FooDao.create*(Long,..))")
    public void firstLongParamMethods() {
    }

    @Pointcut("repositoryMethods() && firstLongParamMethods()")
    public void entityCreationMethods() {
    }

    @AfterReturning(value = "entityCreationMethods()", returning = "entity")
    public void logMethodCall(JoinPoint jp, Object entity) throws Throwable {
        eventPublisher.publishEvent(new FooCreationEvent(entity));
    }
}
```

请注意，首先通过使用@AfterReturning注解，我们可以访问目标方法的返回值。
其次，通过声明JoinPoint类型的参数，我们可以访问目标方法调用的参数。

接下来我们创建一个监听器，它简单地记录事件：

```java

@Component
public class FooCreationEventListener implements ApplicationListener<FooCreationEvent> {
    private static final Logger LOGGER = Logger.getLogger(FooCreationEventListener.class.getName());

    @Override
    public void onApplicationEvent(FooCreationEvent event) {
        LOGGER.info("Created foo instance: " + event.getSource().toString());
    }
}
```

## 5. Around通知

**环绕通知包围一个连接点(方法调用)**。

这是功能最强大的通知类型，**环绕通知可以在方法调用之前和之后执行自定义行为**。
它还有权决定是继续执行连接点方法还是通过提供自己的返回值或抛出异常来缩短通知的方法执行。

为了演示它的使用，假设我们要计算一个方法的执行时间，为此我们需要创建一个切面：

```java

@Aspect
@Component
public class PerformanceAspect {

    private static final Logger logger = Logger.getLogger(PerformanceAspect.class.getName());

    @Pointcut("within(cn.tuyucheng.taketoday..*) && execution(* cn.tuyucheng.taketoday.pointcutadvice.dao.FooDao.*(..))")
    public void repositoryClassMethods() {
    }

    @Around("repositoryClassMethods()")
    public Object measureMethodExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.nanoTime();
        Object retval = pjp.proceed();
        long end = System.nanoTime();
        String methodName = pjp.getSignature().getName();
        logger.info("Execution of " + methodName + " took " + TimeUnit.NANOSECONDS.toMillis(end - start) + " ms");
        return retval;
    }
}
```

当执行与repositoryClassMethods切入点表达式匹配的任何连接点时，将触发此通知。

该通知**接收ProceedingJointPoint类型的一个参数，该参数使我们有机会在目标方法调用之前采取行动。
在这种情况下，我们只需保存方法开始执行的时间**。

其次，通知方法的返回类型是Object，因为目标方法可以返回任何类型的结果，如果目标方法返回类型为void，则返回null。
在目标方法调用之后，我们可以计算时间，记录下来，并将目标方法的结果值返回给调用者。

## 6. 总结

在本文中，我们介绍了Spring中不同类型的通知及其声明和实现。
我们使用基于模式的方法和使用AspectJ注解来定义切面，并提供了实际的代码案例片段。