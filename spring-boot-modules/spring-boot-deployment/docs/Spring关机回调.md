## 1. 概述

在本教程中，我们将学习在Spring中使用关闭回调的不同方法。

使用关闭回调的主要优点是它使我们能够控制应用程序的优雅退出。

## 2. 关机回调方法

Spring支持组件级别和上下文级别的关机回调，我们可以使用以下方法创建这些回调：

-   @PreDestroy
-   DisposableBean接口
-   Bean销毁方法
-   全局ServletContextListener

### 2.1 使用@PreDestroy

让我们创建一个使用@PreDestroy的bean：

```java
@Component
public class Bean1 {

    @PreDestroy
    public void destroy() {
        System.out.println("Callback triggered - @PreDestroy.");
    }
}
```

在bean初始化期间，Spring将注册所有带有@PreDestroy注解的bean方法，并在应用程序关闭时调用它们。

### 2.2 使用DisposableBean接口

我们的第二个bean将实现DisposableBean接口来注册关闭回调：

```java
@Component
public class Bean2 implements DisposableBean {

    @Override
    public void destroy() throws Exception {
        System.out.println("Callback triggered - DisposableBean.");
    }
}
```

### 2.3 声明一个Bean销毁方法

对于这种方法，首先我们创建一个带有自定义销毁方法的类：

```java
public class Bean3 {

    public void destroy() {
        System.out.println("Callback triggered - bean destroy method.");
    }
}
```

然后，我们创建初始化bean的配置类，并将其destroy()方法标记为我们的关闭回调：

```java
@Configuration
public class ShutdownHookConfiguration {

    @Bean(destroyMethod = "destroy")
    public Bean3 initializeBean3() {
        return new Bean3();
    }
}
```

注册destroy方法的XML方式是：

```xml
<bean class="cn.tuyucheng.taketoday.shutdownhooks.config.Bean3" destroy-method="destroy">
```

### 2.4 使用全局ServletContextListener

与在bean级别注册回调的其他三种方法不同，**ServletContextListener在上下文级别注册回调**。

为此，让我们创建一个自定义上下文监听器：

```java
public class ExampleServletContextListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("Callback triggered - ContextListener.");
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // Triggers when context initializes
    }
}
```

我们需要将其注册到配置类中的ServletListenerRegistrationBean中：

```java
@Bean
ServletListenerRegistrationBean<ServletContextListener> servletListener() {
    ServletListenerRegistrationBean<ServletContextListener> srb = new ServletListenerRegistrationBean<>();
    srb.setListener(new ExampleServletContextListener());
    return srb;
}
```

## 3. 总结

我们了解了Spring提供的在bean级别和上下文级别注册关闭回调的不同方式，这些可用于优雅地关闭应用程序并有效地释放已使用的资源。