## 1. 概述

在这个教程中，我们将介绍在Spring Boot中禁用数据库自动配置的两种不同方法，这在编写测试时可以派上用场。

我们以Redis、MongoDB和Spring Data JPA的为例。先从基于注解的方法开始，然后我们介绍基于属性文件的方法。

## 2. 使用注解

让我们从MongoDB示例开始。我们需要做的就是排除的特定的自动配置类：

```java

@SpringBootApplication(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class
})
public class SpringDataMongoDB {

    public static void main(String[] args) {
        SpringApplication.run(SpringDataMongoDB.class, args);
    }
}
```

同样，我们可以禁用Redis的自动配置：

```java

@SpringBootApplication(exclude = {
        RedisAutoConfiguration.class,
        RedisRepositoriesAutoConfiguration.class
})
public class SpringDataRedis {

    public static void main(String[] args) {
        SpringApplication.run(SpringDataRedis.class, args);
    }
}
```

最后，也可以禁用Spring Data JPA的自动配置：

```java

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class SpringDataJPA {

    public static void main(String[] args) {
        SpringApplication.run(SpringDataJPA.class, args);
    }
}
```

## 3. 使用属性文件

我们还可以使用属性文件禁用自动配置。

对于MongoDB：

```properties
spring.autoconfigure.exclude=\
  org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration, \
  org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
```

对于Redis：

```properties
spring.autoconfigure.exclude=\
  org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration, \
  org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration
```

以及Spring Data JPA：

```properties
spring.autoconfigure.exclude=\
  org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration, \
  org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration, \
  org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
```

## 4. 测试

为了看到效果，我们可以检查自动配置类的Spring bean在我们的应用程序上下文中是否存在。

对于MongoDB，我们可以检查MongoTemplate bean是否存在：

```java

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringDataMongoDB.class)
class SpringDataMongoDBIntegrationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void givenAutoConfigDisabled_whenStarting_thenNoAutoconfiguredBeansInContext() {
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(MongoTemplate.class));
    }
}
```

对于JPA，我们检查Datasource bean是否存在：

```java

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringDataJPA.class)
class SpringDataJPAIntegrationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void givenAutoConfigDisabled_whenStarting_thenNoAutoconfiguredBeansInContext() {
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(DataSource.class));
    }
}
```

最后，对于Redis，我们可以检查RedisTemplate bean是否存在：

```java

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringDataRedis.class)
class SpringDataRedisIntegrationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void givenAutoConfigDisabled_whenStarting_thenNoAutoconfiguredBeansInContext() {
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(RedisTemplate.class));
    }
}
```

## 5. 总结

在这篇简短的文章中，我们学习了如何禁用不同数据库的Spring Boot自动配置。