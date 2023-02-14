## 1. 概述

在本快速教程中，我们将探讨在Spring Boot中禁用数据库自动配置的两种不同方法。这在[测试](https://www.baeldung.com/spring-boot-exclude-auto-configuration-test)时可以派上用场。

我们将举例说明Redis、MongoDB和Spring Data JPA。

我们将从查看基于注解的方法开始，然后我们将查看属性文件方法。

## 2. 使用注解

让我们从[MongoDB](https://www.baeldung.com/spring-data-mongodb-tutorial)示例开始。我们需要做的就是排除的特定的自动配置类：

```java
@SpringBootApplication(exclude = {
      MongoAutoConfiguration.class,
      MongoDataAutoConfiguration.class
})
```

同样，我们可以禁用[Redis](https://www.baeldung.com/spring-data-redis-tutorial)的自动配置：

```java
@SpringBootApplication(exclude = {
      RedisAutoConfiguration.class,
      RedisRepositoriesAutoConfiguration.class
})
```

最后，也可以禁用[Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)的自动配置：

```java
@SpringBootApplication(exclude = {
      DataSourceAutoConfiguration.class,
      DataSourceTransactionManagerAutoConfiguration.class,
      HibernateJpaAutoConfiguration.class
})
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

为了进行测试，我们将检查我们的[应用程序上下文](https://www.baeldung.com/spring-web-contexts)中是否不存在自动配置类的Spring bean。

我们将从MongoDB的测试开始。我们将验证MongoTemplate bean是否不存在：

```java
@Autowired
private ApplicationContext context;

@Test
void givenAutoConfigDisabled_whenStarting_thenNoAutoconfiguredBeansInContext() {
    assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(MongoTemplate.class));
}
```

对于JPA，我们检查Datasource bean是否不存在：

```java
@Autowired
private ApplicationContext context;

@Test
void givenAutoConfigDisabled_whenStarting_thenNoAutoconfiguredBeansInContext() {
    assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(DataSource.class));
}
```

最后，对于Redis，我们可以检查RedisTemplate bean是否不存在：

```java
@Autowired
private ApplicationContext context;

@Test
void givenAutoConfigDisabled_whenStarting_thenNoAutoconfiguredBeansInContext() {
    assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(RedisTemplate.class));
}
```

## 5. 总结

在这篇简短的文章中，我们学习了如何为不同的数据库禁用Spring Boot自动配置。