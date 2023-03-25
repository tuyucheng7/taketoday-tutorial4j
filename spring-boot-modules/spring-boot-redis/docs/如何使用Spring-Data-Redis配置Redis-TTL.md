## 1. 简介

在这个快速教程中，我们将了解如何在Spring Data Redis中配置key过期。

## 2. 设置

让我们创建一个基于Spring Boot的API来管理由Redis支持的持久化Session资源。我们需要四个主要步骤来做到这一点。有关更详细的设置，请查看我们关于[Spring Data Redis](https://www.baeldung.com/spring-data-redis-tutorial)的指南。

### 2.1 依赖

首先，让我们将以下依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <version>3.0.4</version>
</dependency>
```

[spring-boot-starter-data-redis](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-data-redis/3.0.4)将传递添加spring-data-redis和lettuce-core。

### 2.2 Redis配置

其次，让我们添加RedisTemplate配置：

```java
@Configuration
public class RedisConfiguration {

    @Bean
    public RedisTemplate<String, Session> getRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Session> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        return redisTemplate;
    }
}
```

### 2.3 模型

第三，让我们创建Session模型：

```java
@RedisHash
public class Session {
    @Id
    private String id;
    private Long expirationInSeconds;
}
```

### 2.4 Redis Repository

最后，让我们创建SessionRepository，它提供与Redis的交互来管理我们的Session实体：

```java
public interface SessionRepository extends CrudRepository<Session, String> {}
```

## 3. 方法

我们将介绍设置TTL的三种方法。

### 3.1 使用@RedisHash

让我们从最简单的开始。@RedisHash允许我们为其timeToLive属性提供一个值：

```java
@RedisHash(timeToLive = 60L)
public class Session {
    @Id
    private String id;
    private Long expirationInSeconds;
}
```

**它以秒为单位获取值**。在上面的示例中，我们将过期时间设置为60秒。**当我们想为所有Session对象提供一个常量值TTL时，这种方法很有用。指定默认TTL也很有用**。

### 3.2 使用@TimeToLive

在前面的方法中，我们可以为所有Session对象设置相同的常量TTL。使用@TimeToLive，我们可以标注任何数字属性或返回数值以设置TTL的方法：

```java
@RedisHash(timeToLive = 60L)
public class Session {
    @Id
    private String id;
    @TimeToLive
    private Long expirationInSeconds;
}
```

这允许我们为每个Session对象动态设置TTL。**就像之前的方法一样，TTL以秒为单位。@TimeToLive的值取代了@RedisHash(timeToLive)**。

### 3.3 使用KeyspaceSettings

继续下一个方法，KeyspaceSettings设置TTL。Keyspace定义用于为Redis哈希创建实际键的前缀。现在让我们为Session类定义我们的KeyspaceSettings：

```java
@Configuration
@EnableRedisRepositories(keyspaceConfiguration = RedisConfiguration.MyKeyspaceConfiguration.class)
public class RedisConfiguration {

    // Other configurations omitted

    public static class MyKeyspaceConfiguration extends KeyspaceConfiguration {

        @Override
        protected Iterable<KeyspaceSettings> initialConfiguration() {
            KeyspaceSettings keyspaceSettings = new KeyspaceSettings(Session.class, "session");
            keyspaceSettings.setTimeToLive(60L);
            return Collections.singleton(keyspaceSettings);
        }
    }
}
```

就像之前的方法一样，我们以秒为单位指定了TTL。这种方法与@RedisHash非常相似。此外，如果我们不想使用@EnableRepositories，我们可以通过编程方式设置它：

```java
@Configuration
public class RedisConfiguration {

    // Other configurations omitted

    @Bean
    public RedisMappingContext keyValueMappingContext() {
        return new RedisMappingContext(new MappingConfiguration(new IndexConfiguration(), new MyKeyspaceConfiguration()));
    }

    public static class MyKeyspaceConfiguration extends KeyspaceConfiguration {

        @Override
        protected Iterable<KeyspaceSettings> initialConfiguration() {
            KeyspaceSettings keyspaceSettings = new KeyspaceSettings(Session.class, "session");
            keyspaceSettings.setTimeToLive(60L);
            return Collections.singleton(keyspaceSettings);
        }
    }
}
```

## 4. 比较

最后，让我们比较一下我们看到的三种方法，并确定何时使用哪一种。

@RedisHash和KeyspaceSettings都允许我们为所有实体实例(Session)一次性设置TTL。另一方面，@TimeToLive允许我们为每个实体实例动态设置TTL。

KeyspaceSettings提供了一种在多个实体的配置中的一个位置设置默认TTL的方法。另一方面，对于@TimeToLive和@RedisHash这将需要在每个实体类文件中发生。

@TimeToLive的优先级最高，其次是@RedisHash，最后是KeyspaceSettings。

## 5. Redis Key过期事件

通过所有这些在Redis中设置key过期的方法，我们可能还会对此类事件发生的时间感兴趣。为此，我们可以使用RedisKeyExpiredEvent。让我们设置一个EventListener来捕获RedisKeyExpiredEvent：

```java
@Configuration
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
@Slf4j
public class RedisConfiguration {
    // Other configurations omitted

    @Component
    public static class SessionExpiredEventListener {
        @EventListener
        public void handleRedisKeyExpiredEvent(RedisKeyExpiredEvent<Session> event) {
            Session expiredSession = (Session) event.getValue();
            assert expiredSession != null;
            log.info("Session with key={} has expired", expiredSession.getId());
        }
    }
}
```

我们现在能够知道Session何时过期，并能够在需要时采取一些行动：

```bash
2023-02-10T15:13:38.626+05:30  INFO 16874 --- [enerContainer-1] c.t.t.s.config.RedisConfiguration: Session with key=edbd98e9-7b50-45d8-9cf4-9c621899e213 has expired
```

## 6. 总结

在本文中，我们研究了通过Spring Data Redis设置Redis TTL的各种方法。最后，我们查看了RedisKeyExpiredEvent以及如何处理过期事件。