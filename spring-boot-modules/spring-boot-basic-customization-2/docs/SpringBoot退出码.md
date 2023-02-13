## 1. 概述

每个应用程序在退出时都会返回一个退出代码；此代码可以是任何整数值，包括负值。

在这个快速教程中，我们将了解如何从Spring Boot应用程序返回退出代码。

## 2. Spring Boot和退出码

如果在启动时发生异常，Spring Boot应用程序将以代码1退出。否则，在正常退出时，它会提供0作为退出码。

Spring向JVM注册关闭钩子以确保ApplicationContext在退出时正常关闭。除此之外，Spring还提供了org.springframework.boot.ExitCodeGenerator接口。该接口可以返回调用System.exit()时的具体代码。

## 3. 实现退出码

Spring Boot提供了四种允许我们使用退出代码的方法。

ExitCodeGenerator接口和ExitCodeExceptionMapper允许我们指定自定义退出码，而ExitCodeEvent允许我们在退出时读取退出码。此外，异常甚至可以实现ExitCodeGenerator接口。

### 3.1 ExitCodeGenerator

让我们创建一个实现ExitCodeGenerator接口的类，我们必须实现返回整数值的方法getExitCode()：

```java
@SpringBootApplication
public class ExitCodeGeneratorDemoApplication implements ExitCodeGenerator {

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(ExitCodeGeneratorDemoApplication.class, args)));
    }

    @Override
    public int getExitCode() {
        return 42;
    }
}
```

在这里，ExitCodeGeneratorDemoApplication类实现了ExitCodeGenerator接口。此外，**我们使用SpringApplication.exit()包装了对SpringApplication.run()的调用**。

当应用程序退出时，退出码现在为42。

### 3.2 ExitCodeExceptionMapper

现在让我们了解如何**根据运行时异常返回退出码**。为此，我们实现了一个CommandLineRunner，它总是抛出NumberFormatException，然后注册一个ExitCodeExceptionMapper类型的bean：

```java
@SpringBootApplication
public class ExitCodeExceptionMapperDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExitCodeExceptionMapperDemoApplication.class, args);
    }

    @Bean
    CommandLineRunner createException() {
        return args -> Integer.parseInt("test");
    }

    @Bean
    ExitCodeExceptionMapper exitCodeToExceptionMapper() {
        return exception -> {
            // set exit code based on the exception type
            if (exception.getCause() instanceof NumberFormatException) {
                return 80;
            }
            return 1;
        };
    }
}
```

在ExitCodeExceptionMapper bean中，我们只是将异常映射到某个退出码。

### 3.3 ExitCodeEvent

接下来，我们可以捕获一个ExitCodeEvent事件来读取我们应用程序的退出码。为此，我们只需**注册一个订阅ExitCodeEvents的事件监听器**(在此示例中名为DemoListener)：

```java
@SpringBootApplication
public class ExitCodeEventDemoApplication {

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(ExitCodeEventDemoApplication.class, args)));
    }

    @Bean
    DemoListener demoListenerBean() {
        return new DemoListener();
    }

    private static class DemoListener {
        @EventListener
        public void exitEvent(ExitCodeEvent event) {
            System.out.println("Exit code: " + event.getExitCode());
        }
    }
}
```

现在，当应用程序退出时，方法exitEvent()将被调用，我们可以从事件中读取退出码。

### 3.4 退出码与异常

异常也可以实现ExitCodeGenerator接口。当抛出此类异常时，Spring Boot返回由已实现的getExitCode()方法提供的退出代码。例如：

```java
public class FailedToStartException extends RuntimeException implements ExitCodeGenerator {

    @Override
    public int getExitCode() {
        return 127;
    }
}
```

如果我们抛出一个FailedToStartException的实例，Spring Boot会将此异常检测为ExitCodeGenerator并报告127作为退出码。

## 4. 总结

在本文中，我们介绍了Spring Boot提供的多种选项来处理退出码。

对于任何应用程序来说，在退出时返回正确的错误代码是非常重要的。退出码确定退出发生时应用程序的状态。除此之外，它还有助于故障排除。