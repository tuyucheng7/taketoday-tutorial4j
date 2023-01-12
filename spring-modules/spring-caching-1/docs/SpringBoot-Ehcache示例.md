## 1. 概述

让我们看一个在Spring Boot中使用Ehcache的例子，我们使用Ehcache 3.x版本，因为它提供了JSR-107缓存管理器的实现。

该示例是一个简单的REST服务，用于生成一个数字的平方。

## 2. 依赖

```text
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.6.1</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
    <version>2.6.1</version></dependency>
<dependency>
    <groupId>javax.cache</groupId>
    <artifactId>cache-api</artifactId>
    <version>1.1.1</version>
</dependency>
<dependency>
    <groupId>org.ehcache</groupId>
    <artifactId>ehcache</artifactId>
    <version>3.8.1</version>
</dependency>     
```

## 3. 案例

首先创建一个简单的REST控制器，它调用一个Service来对一个数字求平方并将结果作为JSON字符串返回：

```java

@RestController
@RequestMapping(path = "/number", produces = MediaType.APPLICATION_JSON_VALUE)
public class NumberController {

    public static final Logger LOGGER = LoggerFactory.getLogger(NumberController.class);

    @Autowired
    private NumberService numberService;

    @GetMapping(path = "/square/{number}")
    public String getThing(@PathVariable Long number) {
        LOGGER.info("call numberService to square {}", number);
        return String.format("{\"square\": %s}", numberService.square(number));
    }
}
```

对于Service类，我们使用@Cacheable注解标注Service方法，以便Spring将处理缓存。
**通过这个注解，Spring将创建NumberService的代理，来拦截对square方法的调用并调用Ehcache**。

我们需要提供要使用的缓存的名称和可选的key，还可以添加一个条件来限制缓存的内容：

```java

@Service
public class NumberService {
    public static final Logger LOGGER = LoggerFactory.getLogger(NumberService.class);

    @Cacheable(value = "squareCache", key = "#number", condition = "#number > 10")
    public BigDecimal square(Long number) {
        BigDecimal square = BigDecimal.valueOf(number).multiply(BigDecimal.valueOf(number));
        LOGGER.info("square of {} is {}", number, square);
        return square;
    }
}
```

最后，下面是Spring Boot的应用程序入口类：

```java

@SpringBootApplication
public class SpringBootEhcacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootEhcacheApplication.class, args);
    }
}
```

## 4. 缓存配置

我们需要将Spring的@EnableCaching注解添加到Spring配置类中，以便启用Spring注解驱动的缓存管理。

让我们创建一个CacheConfig类：

```java

@Configuration
@EnableCaching
public class CacheConfig {

}
```

**Spring的自动配置会找到Ehcache的JSR-107实现，但是，默认情况下不会创建缓存**。

因为Spring和Ehcache都不查找默认的ehcache.xml文件，我们通过添加以下属性来告诉Spring在哪里可以找到它：

```properties
spring.cache.jcache.config=classpath:ehcache.xml
```

下面是包含名称为“squareCache”缓存配置的ehcache.xml文件：

```xml

<config xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns="http://www.ehcache.org/v3"
        xsi:schemaLocation="http://www.ehcache.org/v3 http://www.ehcache.org/schema/ehcache-core-3.0.xsd">

    <cache alias="squareCache">
        <key-type>java.lang.Long</key-type>
        <value-type>java.math.BigDecimal</value-type>
        <expiry>
            <ttl unit="seconds">30</ttl>
        </expiry>

        <listeners>
            <listener>
                <class>cn.tuyucheng.taketoday.cachetest.config.CacheEventLogger</class>
                <event-firing-mode>ASYNCHRONOUS</event-firing-mode>
                <event-ordering-mode>UNORDERED</event-ordering-mode>
                <events-to-fire-on>CREATED</events-to-fire-on>
                <events-to-fire-on>EXPIRED</events-to-fire-on>
            </listener>
        </listeners>

        <resources>
            <heap unit="entries">2</heap>
            <offheap unit="MB">10</offheap>
        </resources>
    </cache>
</config>
```

我们还要添加缓存事件监听器，它记录`CREATED`和`EXPIRED`缓存事件：

```java
public class CacheEventLogger implements CacheEventListener<Object, Object> {

    public static final Logger LOGGER = LoggerFactory.getLogger(CacheEventLogger.class);

    @Override
    public void onEvent(CacheEvent<?, ?> cacheEvent) {
        LOGGER.info("Cache event {} for item with key {}. Old value = {}, New value = {}",
                cacheEvent.getType(), cacheEvent.getKey(), cacheEvent.getOldValue(), cacheEvent.getNewValue());
    }
}
```

## 5. 测试

我们可以使用Maven通过运行mvn spring-boot:run来启动这个应用程序。

然后打开浏览器并访问端口8080上的REST服务，
如果我们访问http://localhost:8080/number/square/12，那么返回的结果为{“square”:144}，在日志中我们可以看到：

```text
[http-nio-8080-exec-1] INFO  [c.t.t.c.rest.NumberController] >>> call numberService to square 12 
[http-nio-8080-exec-1] INFO  [c.t.t.c.service.NumberService] >>> square of 12 is 144 
[Ehcache [_default_]-0] INFO  [c.t.t.c.config.CacheEventLogger] >>> Cache event CREATED for item with key 12. Old value = null, New value = 144 
```

我们可以看到来自NumberService中square方法的日志消息，以及来自CacheEventLogger的`CREATED`事件。
**如果我们刷新浏览器，此时控制台只会输出以下日志**：

```text
[http-nio-8080-exec-5] INFO  [c.t.t.c.rest.NumberController] >>> call numberService to square 12 
```

NumberService的square方法中的日志消息没有被调用，这表明这次我们使用的是缓存的值。

**如果我们等待30秒让缓存项过期并再次刷新浏览器，我们可以看到一个`EXPIRED`事件**，并将值重新添加回缓存中：

```text
[http-nio-8080-exec-10] INFO  [c.t.t.c.rest.NumberController] >>> call numberService to square 12 
[http-nio-8080-exec-10] INFO  [c.t.t.c.service.NumberService] >>> square of 12 is 144 
[Ehcache [_default_]-4] INFO  [c.t.t.c.config.CacheEventLogger] >>> Cache event EXPIRED for item with key 12. Old value = 144, New value = null 
[Ehcache [_default_]-4] INFO  [c.t.t.c.config.CacheEventLogger] >>> Cache event CREATED for item with key 12. Old value = null, New value = 144
```

如果我们在浏览器中访问http://localhost:8080/number/square/3，我们会得到正确的答案9，但该值没有被缓存。

这是因为我们在@Cacheable注解上配置的condition属性仅缓存大于10的数字的值。

## 6. 总结

在这个教程中，我们演示了如何在Spring Boot中使用Ehcache。