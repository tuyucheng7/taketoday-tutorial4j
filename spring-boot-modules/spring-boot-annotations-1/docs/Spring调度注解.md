## 1. 概述

当我们需要多个线程执行时，我们可以使用org.springframework.scheduling.annotation包中的注解。

在这个快速教程中，我们将介绍Spring中的调度注解。

## 2. @EnableAsync

通过这个注解，我们可以在Spring中启用异步功能。

我们必须将它与@Configuration一起使用：

```java
@Configuration
@EnableAsync
class VehicleFactoryConfig {
}
```

现在，我们启用了异步调用，我们可以使用@Async来定义支持异步的方法。

## 3. @EnableScheduling

使用此注解，我们可以在应用程序中启用调度功能。

我们也必须将它与@Configuration结合使用：

```java
@Configuration
@EnableScheduling
class VehicleFactoryConfig {
}
```

因此，我们现在可以使用@Scheduled定期运行方法。

## 4. @Async

**我们可以定义想要在不同线程上执行的方法，从而异步运行它们**。

为此，我们可以使用@Async标注该方法：

```java

@Service
public class VehicleService {

    @Async
    public void repairCar() {
    }
}
```

如果我们将此注解应用于一个类上，那么类中的所有方法都将被异步调用。

请注意，我们需要使用@EnableAsync或XML配置启用此注解的异步调用。

有关@Async的更多信息可以在[这篇文章]()中找到。

## 5. @Scheduled

**如果我们需要一个方法定期执行，我们可以使用这个注解**：

```java
@Component
public class Driver {

    @Scheduled(fixedRate = 10000)
    public void checkVehicle() {
    }
}
```

我们可以使用它以**固定的时间间隔**执行一个方法，或者也可以使用类似cron表达式对其进行调整。

@Scheduled利用了Java 8的重复注解特性，这意味着我们可以用它多次标记一个方法：

```java
@Component
public class Driver {

    @Scheduled(fixedRate = 10000)
    @Scheduled(cron = "0 * * * * MON-FRI")
    public void checkVehicle() {
    }
}
```

请注意，使用@Scheduled注解标注的方法应该具有void返回类型。

此外，我们必须通过@EnableScheduling或XML配置启用该注解才能使用。

有关调度的更多信息，请阅读[本文]()。

## 6. @Schedules

我们可以使用这个注解来指定多个@Scheduled规则：

```java
@Component
public class Driver {

    @Schedules({
            @Scheduled(fixedRate = 10000),
            @Scheduled(cron = "0 * * * * MON-FRI")
    })
    void checkVehicle() {
        // ...
    }
}
```

请注意，从Java 8开始，我们可以通过上述重复注解功能实现相同的功能。

## 7. 总结

在本文中，我们介绍了最常见的Spring调度注解。