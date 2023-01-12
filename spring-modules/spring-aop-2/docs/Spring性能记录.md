## 1. 概述

在本教程中，我们介绍Spring框架为性能监控提供的几种基本方法。

## 2. PerformanceMonitorInterceptor

一个简单的解决方案来获得方法执行时间的基本监控功能，我们可以利用Spring AOP(面向方面编程)中的PerformanceMonitorInterceptor类。

Spring AOP允许在应用程序中定义横切关注点，即拦截一个或多个方法执行的代码，以便添加额外的功能。

PerformanceMonitorInterceptor类是一个拦截器，可以与要同时执行的任何自定义方法关联。此类使用StopWatch实例来确定方法运行的开始和结束时间。

让我们创建一个简单的Person类和一个PersonService类，其中包含我们将监控的两个方法：

```java

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Person {
    private String lastName;
    private String firstName;
    private LocalDate dateOfBirth;
}
```

```java
public class PersonService {

    public String getFullName(Person person) {
        return person.getLastName() + " " + person.getFirstName();
    }

    public int getAge(Person person) {
        Period p = Period.between(person.getDateOfBirth(), LocalDate.now());
        return p.getYears();
    }
}
```

为了使用Spring监控拦截器，我们需要定义一个切入点和Advisor：

```java

@Configuration
@EnableAspectJAutoProxy
public class AopConfiguration {

    @Pointcut("execution(public String cn.tuyucheng.taketoday.performancemonitor.PersonService.getFullName(..))")
    public void monitor() {
    }

    @Bean
    public Advisor performanceMonitorAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("cn.tuyucheng.taketoday.performancemonitor.AopConfiguration.monitor()");
        return new DefaultPointcutAdvisor(pointcut, performanceMonitorInterceptor());
    }

    @Bean
    public PerformanceMonitorInterceptor performanceMonitorInterceptor() {
        return new PerformanceMonitorInterceptor(true);
    }

    @Bean
    public Person person() {
        return new Person("John", "Smith", LocalDate.of(1980, Month.JANUARY, 12));
    }

    @Bean
    public PersonService personService() {
        return new PersonService();
    }
}
```

切入点包含一个表达式，用于标识我们想要被拦截的方法，在我们的例子中是PersonService类中的getFullName()方法。

在配置了performanceMonitorInterceptor() bean之后，我们需要将拦截器与切入点关联起来，这是通过Advisor实现的，如上面的示例所示。

最后，@EnableAspectJAutoProxy注解启用了对bean的AspectJ支持。
简单地说，AspectJ是一个第三方库，通过@Pointcut等方便的注解使Spring AOP的使用变得更容易。

**创建配置后，我们需要将拦截器类的日志级别设置为TRACE，因为这是它记录消息的级别**。

例如，使用log4j，我们可以通过log4j.properties文件来实现：

```properties
log4j.logger.org.springframework.aop.interceptor.PerformanceMonitorInterceptor=TRACE, stdout
```

如果使用logback，可以通过logback.xml文件配置：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!--....-->
    <logger name="org.springframework.aop.interceptor.PerformanceMonitorInterceptor" level="TRACE"/>
    <!--...-->
</configuration>
```

对于getFullName()方法的每次执行，我们都会在控制台中看到TRACE级别的日志记录：

```text
22:36:09.141 [main] TRACE [c.t.t.p.PersonService] >>> StopWatch 'cn.tuyucheng.taketoday.performancemonitor.PersonService.getFullName': running time = 11496200 ns 
```

## 3. 自定义性能监控拦截器

如果我们想更多地控制性能监控的完成方式，我们可以实现自己的自定义拦截器。

为此，我们可以继承AbstractMonitoringInterceptor类并重写invokeUnderTrace()方法来记录方法的开始、结束和持续时间，并在方法执行持续时间超过10毫秒时给出警告：

```java
public class MyPerformanceMonitorInterceptor extends AbstractMonitoringInterceptor {

    public MyPerformanceMonitorInterceptor() {
    }

    public MyPerformanceMonitorInterceptor(boolean useDynamicLogger) {
        setUseDynamicLogger(useDynamicLogger);
    }

    @Override
    protected Object invokeUnderTrace(MethodInvocation invocation, Log log) throws Throwable {

        String name = createInvocationTraceName(invocation);
        long start = System.currentTimeMillis();
        log.info("Method " + name + " execution started at:" + new Date());
        try {
            return invocation.proceed();
        } finally {
            long end = System.currentTimeMillis();
            long time = end - start;
            log.info("Method " + name + " execution lasted:" + time + " ms");
            log.info("Method " + name + " execution ended at:" + new Date());

            if (time > 10) {
                log.warn("Method execution longer than 10 ms!");
            }

        }
    }
}
```

然后与之前一样，我们需要将自定义拦截器关联到一个或多个方法。

让我们为PersonService的getAge()方法定义一个切入点，并将其与我们创建的拦截器相关联：

```java
public class AopConfiguration {

    @Pointcut("execution(public int cn.tuyucheng.taketoday.performancemonitor.PersonService.getAge(..))")
    public void myMonitor() {
    }

    @Bean
    public Advisor myPerformanceMonitorAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("cn.tuyucheng.taketoday.performancemonitor.AopConfiguration.myMonitor()");
        return new DefaultPointcutAdvisor(pointcut, myPerformanceMonitorInterceptor());
    }

    @Bean
    public MyPerformanceMonitorInterceptor myPerformanceMonitorInterceptor() {
        return new MyPerformanceMonitorInterceptor(true);
    }
}
```

我们将自定义拦截器的日志级别设置为INFO：

```properties
log4j.logger.cn.tuyucheng.taketoday.performancemonitor.MyPerformanceMonitorInterceptor=INFO, stdout
```

对于logback.xml文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!--...-->
    <logger name="cn.tuyucheng.taketoday.performancemonitor.MyPerformanceMonitorInterceptor" level="INFO"/>
    <!--...-->
</configuration>
```

getAge()方法的执行生成的日志如下：

```text
22:43:55.785 [main] INFO  [c.t.t.p.PersonService] >>> Method cn.tuyucheng.taketoday.performancemonitor.PersonService.getAge execution started at:Sat Oct 22 22:43:55 CST 2022 
22:43:55.795 [main] INFO  [c.t.t.p.PersonService] >>> Method cn.tuyucheng.taketoday.performancemonitor.PersonService.getAge execution lasted:15 ms 
22:43:55.796 [main] INFO  [c.t.t.p.PersonService] >>> Method cn.tuyucheng.taketoday.performancemonitor.PersonService.getAge execution ended at:Sat Oct 22 22:43:55 CST 2022 
22:43:55.796 [main] WARN  [c.t.t.p.PersonService] >>> Method execution longer than 10 ms! 
```

## 4. 总结

在这个教程中，我们介绍了Spring中的简单性能监控。