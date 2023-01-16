## 1. 概述

Spring Boot 的主要吸引力之一是它经常将第三方配置减少到几个属性。

在本教程中，我们将了解Spring Boot 如何简化 Redis 的使用。

## 2. 为什么选择 Redis？

[Redis](https://redis.io/)是最流行的内存数据结构存储之一。因此，它可以用作数据库、缓存和消息代理。

在性能方面，它以[响应速度快](https://redis.io/topics/benchmarks)着称。因此，它每秒可以处理数十万次操作，并且易于扩展。

而且，它与Spring Boot应用程序搭配得很好。例如，我们可以将其用作微服务架构中的缓存。我们也可以将其用作 NoSQL 数据库。

## 3.运行Redis

首先，让我们使用他们的[官方 Docker 镜像](https://hub.docker.com/_/redis/)创建一个 Redis 实例。

```bash
$ docker run -p 16379:6379 -d redis:6.0 redis-server --requirepass "mypass"
```

上面，我们刚刚在端口16379上启动了一个 Redis 实例，密码为mypass。

## 4. 开胃菜

Spring 为我们使用[Spring Data Redis](https://www.baeldung.com/spring-data-redis-tutorial)将Spring Boot应用程序与 Redis 连接提供了强大的支持。

所以，接下来，让我们确保我们的pom.xml中有[spring-boot-starter-data-redis](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-data-redis)依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <version>2.7.2</version>    
</dependency>
```

## 5.生菜

接下来，让我们配置客户端。

我们将使用的JavaRedis 客户端是[Lettuce](https://www.baeldung.com/java-redis-lettuce)，因为Spring Boot默认使用它。但是，我们也可以使用 [Jedis](https://www.baeldung.com/jedis-java-redis-client-library)。

无论哪种方式，结果都是 RedisTemplate的一个实例：

```java
@Bean
public RedisTemplate<Long, Book> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<Long, Book> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    // Add some specific configuration here. Key serializers, etc.
    return template;
}
```

## 6.属性

当我们使用 Lettuce 时，我们不需要配置RedisConnectionFactory。Spring Boot 为我们做了这件事。

那么，我们剩下的就是在我们的application.properties文件中指定一些属性：

```plaintext
spring.redis.database=0
spring.redis.host=localhost
spring.redis.port=16379
spring.redis.password=mypass
spring.redis.timeout=60000
```

分别：

-   数据库 设置连接工厂使用的数据库索引
-   host是服务器主机所在的地方
-   port表示服务器监听的端口
-   password是服务器的登录密码，和
-   timeout建立连接超时

当然，还有很多其他的属性我们可以配置。Spring Boot 文档中提供了配置属性的[完整列表。](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#data-properties)

## 7.演示

最后，让我们尝试在我们的应用程序中使用它。如果我们想象一个Book类和一个BookRepository，我们可以创建和检索Book s，使用我们的[RedisTemplate](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/RedisTemplate.html)与 Redis 作为我们的后端进行交互：

```java
@Autowired
private RedisTemplate<Long, Book> redisTemplate;

public void save(Book book) {
    redisTemplate.opsForValue().set(book.getId(), book);
}

public Book findById(Long id) {
    return redisTemplate.opsForValue().get(id);
}
```

默认情况下，Lettuce 会为我们管理序列化和反序列化，所以此时没有什么可做的。但是，很高兴知道这也可以配置。

另一个重要的特性是由于RedisTemplate 是线程安全的，所以它可以在[多线程环境](https://www.baeldung.com/java-thread-safety)中正常工作。

## 八. 总结

在本文中，我们将Spring Boot配置为通过 Lettuce 与 Redis 通信。而且，我们通过一个启动器、一个@Bean配置和一些属性来实现它。

最后，我们使用 RedisTemplate让 Redis 充当简单的后端。