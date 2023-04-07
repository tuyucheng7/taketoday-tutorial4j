## **一、概述**

Spring 带有一组***@Enable\*注释，使开发人员可以更轻松地配置 Spring 应用程序**。这些注释与***@Configuration\*****注释****结合使用**。

在本文中，我们将研究这些注解：

-   *@EnableWebMvc*
-   *@启用缓存*
-   *@EnableScheduling*
-   *@EnableAsync*
-   *@EnableWebSocket*
-   *@EnableJpaRepositories*
-   *@EnableTransactionManagement*
-   *@EnableJpaAuditing*

## **2. \*@EnableWebMvc\***

@EnableWebMvc注释用于**在应用程序中启用 Spring MVC** *，并通过从**WebMvcConfigurationSupport*导入 Spring MVC 配置来工作。

具有类似功能的 XML 等价物是*<mvc:annotation-driven/>。*

可以通过实现*WebMvcConfigurer 的**@Configuration*类来自定义配置：

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
}复制
```

## **3. \*@启用缓存\***

@EnableCaching注释在应用程序中**启用注释驱动的缓存管理***功能*，并允许我们在应用程序中**使用*****@Cacheable\*****和*****@CacheEvict\*****注释。**

具有类似功能的 XML 等价物是*<cache:\*>*命名空间：

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
}复制
```

此注释还具有以下选项：

-   ***mode*** — 指示应如何应用缓存建议
-   ***order*** — 指示在特定连接点应用时执行缓存顾问的顺序
-   ***proxyTargetClass*** — 指示是否要创建基于子类 (CGLIB) 的代理而不是基于标准 Java 接口的代理

这个配置也可以通过实现*CachingConfigurerSupport类的**@Configuration*类来定制：

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
}复制
```

有关使用 Spring 缓存的更多信息，您可以参考这篇[文章](https://www.baeldung.com/spring-cache-tutorial)。

## **4. \*@EnableScheduling\***

@EnableScheduling注解启用计划任务功能**，**并允许我们在应用程序中**使用*****@Scheduled\****注解。*具有类似功能的 XML 等价物是使用*scheduler属性的**<task:\*>*命名空间。

这个配置也可以通过实现*SchedulingConfigurer类的**@Configuration*类来定制：

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
}复制
```

更多使用Spring调度，可以参考这篇[文章](https://www.baeldung.com/spring-scheduled-tasks)。

## **5. \*@EnableAsync\***

@EnableAsync注释在我们的应用程序*中***启用异步处理**。具有类似功能的 XML 等价物是使用*executor属性的**<task:\*>*命名空间。

```java
@Configuration
@EnableAync
public class AsyncConfig { ... }复制
```

有关使用 Spring async 的更多信息，您可以参考这篇[文章](https://www.baeldung.com/spring-async)。

## **6. \*@EnableWebSocket\***

@EnableWebSocket注解用于配置网络套接字请求*的***处理**。*自定义可以通过实现WebSocketConfigurer*类来完成：

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
}复制
```

更多关于Spring Websockets的使用，可以参考这篇[文章](https://www.baeldung.com/websockets-spring)。

## **7. \*@EnableJpaRepositories\***

@EnableJpaRepositories注释通过扫描存储库的*注释*配置类的包来**启用 Spring Data JPA 存储库。**

```java
@Configuration
@EnableJpaRepositories
public class JpaConfig { ... }复制
```

此注释可用的一些选项是：

-   ***value*** — *basePackages()*属性
-   ***basePackages*** — 用于扫描注释组件的基础包
-   ***enableDefaultTransactions*** — 配置是否为 Spring Data JPA 存储库启用默认事务
-   ***entityManagerFactoryRef*** — 配置要使用的*EntityManagerFactory* bean 定义的名称

## **8. \*@EnableTransactionManagement\***

@EnableTransactionManagement注解**启用 Spring 的注解驱动的事务管理***能力*。XML 等效项是*<tx:\*>*命名空间。

```java
@Configuration
@EnableTransactionManagement
public class JpaConfig { ... }复制
```

有关使用 Spring Transaction Management 的更多信息，您可以参考这篇[文章](https://www.baeldung.com/transaction-configuration-with-jpa-and-spring)。

## **9. \*@EnableJpaAuditing\***

@EnableJpaAuditing批注**启用对 JPA 实体的***审计*。

```java
@Configuration
@EnableJpaAuditing
public class JpaConfig {

    @Bean
    public AuditorAware<AuditableUser> auditorProvider() {
        return new AuditorAwareImpl();
    }
}复制
```

有关使用 Spring Web Sockets 的更多信息，您可以参考这篇[文章](https://www.baeldung.com/database-auditing-jpa)。

## **10.结论**

在这篇快速文章中，我们了解了一些*@Enable* Spring 注释以及如何使用它们来帮助我们配置 Spring 应用程序。