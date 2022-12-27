## 1. 概述

Spring Data Redis提供了一种与Redis实例集成的简单方法，但是，**在某些情况下，使用嵌入式服务器比创建具有真实服务器的环境更方便**。

因此，我们将学习如何设置和使用嵌入式Redis服务器。

## 2. 依赖项

首先我们需要添加必要的依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>it.ozimov</groupId>
    <artifactId>embedded-redis</artifactId>
    <version>0.7.2</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

spring-boot-starter-test依赖项包含运行集成测试所需的一切。此外，embedded-redis包含我们将使用的嵌入式服务器。

## 3. 配置

添加依赖项后，我们应该定义Redis服务器和我们的应用程序之间的连接设置。

让我们首先创建一个将保存属性的类：

```java
@Configuration
public class RedisProperties {
    private final int redisPort;
    private final String redisHost;

    public RedisProperties(@Value("${spring.redis.port}") final int redisPort,
                           @Value("${spring.redis.host}") final String redisHost) {
        this.redisPort = redisPort;
        this.redisHost = redisHost;
    }
}
```

接下来，我们应该创建一个使用我们属性类来定义连接配置的配置类：

```java
@Configuration
@EnableRedisRepositories
public class RedisConfiguration {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(final RedisProperties redisProperties) {
        return new LettuceConnectionFactory(redisProperties.getRedisHost(), redisProperties.getRedisPort());
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(final LettuceConnectionFactory connectionFactory) {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
```

配置非常简单。此外，它允许我们在不同的端口上运行嵌入式服务器。

## 4. 嵌入式Redis服务器

现在，我们将配置嵌入式服务器并在我们的测试中使用它。

首先，让我们在测试资源目录(src/test/resources)中创建一个application.properties文件：

```properties
spring.redis.host=localhost
spring.redis.port=6370
```

之后，我们将创建一个带有@TestConfiguration注解的类：

```java
@TestConfiguration
public class TestRedisConfiguration {
    private final RedisServer redisServer;

    public TestRedisConfiguration(final RedisProperties redisProperties) {
        this.redisServer = new RedisServer(redisProperties.getRedisPort());
    }

    @PostConstruct
    public void postConstruct() {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
    }
}
```

**服务器将在上下文启动后启动，并在我们的属性文件中定义的主机和端口上启动**，我们现在可以在不停止实际Redis服务器的情况下运行测试。

理想情况下，我们希望在任何随机可用的端口上启动它，但嵌入式Redis还没有此功能。不过我们可以通过ServerSocket API获取随机端口。

此外，一旦上下文被销毁，服务器将停止。

服务器也可以提供我们自己的可执行文件：

```java
this.redisServer = new RedisServer("/path/redis", redisProperties.getRedisPort());
```

此外，可执行文件可以按操作系统定义：

```java
RedisExecProvider customProvider = RedisExecProvider.defaultProvider()
      .override(OS.UNIX, "/path/unix/redis")
      .override(OS.Windows, Architecture.x86_64, "/path/windows/redis")
      .override(OS.MAC_OS_X, Architecture.x86_64, "/path/macosx/redis")
  
this.redisServer = new RedisServer(customProvider, redisProperties.getRedisPort());
```

最后，让我们创建一个将使用我们的TestRedisConfiguration类的测试：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestRedisConfiguration.class)
class UserRepositoryIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldSaveUser_toRedis() {
        final UUID id = UUID.randomUUID();
        final User user = new User(id, "name");
        final User saved = userRepository.save(user);
        Arrays.stream(applicationContext.getBeanDefinitionNames()).forEach(System.out::println);
        
        assertNotNull(saved);
    }
}
```

运行此测试，user已保存到我们的嵌入式Redis服务器。

此外，我们必须手动将TestRedisConfiguration添加到SpringBootTest中。正如我们之前所说，服务器在测试之前已经启动并在测试之后停止。

## 5. 总结

嵌入式Redis服务器是在测试环境中替换实际服务器的完美工具，我们演示了如何配置它以及如何在我们的测试中使用它。