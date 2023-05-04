## 1. 概述

Spring 带有一组@Enable注解，使开发人员可以更轻松地配置Spring应用程序。这些注解与@Configuration注解结合使用。

在本文中，我们将研究这些注解：

-   @EnableWebMvc
-   @启用缓存
-   @EnableScheduling
-   @EnableAsync
-   @EnableWebSocket
-   @EnableJpaRepositories
-   @EnableTransactionManagement
-   @EnableJpaAuditing

## 2. @EnableWebMvc

@EnableWebMvc注解用于在应用程序中启用Spring MVC，并通过从WebMvcConfigurationSupport导入Spring MVC配置来工作。

具有类似功能的XML等价物是<mvc:annotation-driven\/>。

可以通过实现WebMvcConfigurer 的@Configuration类来自定义配置：

```java
@Configuration
@EnableWebMvc
public class SpringMvcConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(
      List<HttpMessageConverter<?>> converters) {
 
        converters.add(new MyHttpMessageConverter());
    }
 
    // ...
}
```

## 3. @启用缓存

@EnableCaching注解在应用程序中启用注解驱动的缓存管理功能，并允许我们在应用程序中使用@Cacheable和@CacheEvict注解。

具有类似功能的XML等价物是<cache:>命名空间：

```java
@Configuration
@EnableCaching
public class CacheConfig {
 
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(
          Arrays.asList(new ConcurrentMapCache("default")));
        return cacheManager;
    }
}
```

此注解还具有以下选项：

-   mode — 指示应如何应用缓存建议
-   order — 指示在特定连接点应用时执行缓存顾问的顺序
-   proxyTargetClass — 指示是否要创建基于子类 (CGLIB) 的代理而不是基于标准Java接口的代理

这个配置也可以通过实现CachingConfigurerSupport类的@Configuration类来定制：

```java
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    @Bean
    @Override
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(
          Arrays.asList(new ConcurrentMapCache("default")));
        return cacheManager;
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new MyKeyGenerator();
    }
}
```

有关使用Spring缓存的更多信息，你可以参考这篇[文章](https://www.baeldung.com/spring-cache-tutorial)。

## 4. @EnableScheduling

@EnableScheduling注解启用计划任务功能，并允许我们在应用程序中使用@Scheduled注解。具有类似功能的XML等价物是使用scheduler属性的<task:\>命名空间。

这个配置也可以通过实现SchedulingConfigurer类的@Configuration类来定制：

```java
@Configuration
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(
      ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }

    @Bean(destroyMethod = "shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(100);
    }
}
```

更多使用Spring调度，可以参考这篇[文章](https://www.baeldung.com/spring-scheduled-tasks)。

## 5. @EnableAsync

@EnableAsync注解在我们的应用程序中启用异步处理。具有类似功能的XML等价物是使用executor属性的<task:\>命名空间。

```java
@Configuration
@EnableAync
public class AsyncConfig { ... }
```

有关使用Spring async的更多信息，你可以参考这篇[文章](https://www.baeldung.com/spring-async)。

## 6. @EnableWebSocket

@EnableWebSocket注解用于配置网络套接字请求的处理。自定义可以通过实现WebSocketConfigurer类来完成：

```java
@Configuration
@EnableWebSocket
public class MyConfiguration implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(echoWebSocketHandler(), "/echo").withSockJS();
    }

    @Bean
    public WebSocketHandler echoWebSocketHandler() {
        return new EchoWebSocketHandler();
    }
}
```

更多关于Spring Websockets的使用，可以参考这篇[文章](https://www.baeldung.com/websockets-spring)。

## 7. @EnableJpaRepositories

@EnableJpaRepositories注解通过扫描存储库的注解配置类的包来启用Spring Data JPA存储库。

```java
@Configuration
@EnableJpaRepositories
public class JpaConfig { ... }
```

此注解可用的一些选项是：

-   value — basePackages()属性
-   basePackages — 用于扫描注解组件的基础包
-   enableDefaultTransactions — 配置是否为Spring Data JPA存储库启用默认事务
-   entityManagerFactoryRef — 配置要使用的EntityManagerFactory bean 定义的名称

## 8. @EnableTransactionManagement

@EnableTransactionManagement注解启用Spring的注解驱动的事务管理能力。XML等效项是<tx:>命名空间。

```java
@Configuration
@EnableTransactionManagement
public class JpaConfig { ... }
```

有关使用SpringTransaction Management 的更多信息，你可以参考这篇[文章](https://www.baeldung.com/transaction-configuration-with-jpa-and-spring)。

## 9. @EnableJpaAuditing

@EnableJpaAuditing批注启用对 JPA 实体的审计。

```java
@Configuration
@EnableJpaAuditing
public class JpaConfig {

    @Bean
    public AuditorAware<AuditableUser> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
```

有关使用Spring Web Sockets的更多信息，你可以参考这篇[文章](https://www.baeldung.com/database-auditing-jpa)。

## 10.总结

在这篇快速文章中，我们了解了一些@EnableSpring注解以及如何使用它们来帮助我们配置Spring应用程序。