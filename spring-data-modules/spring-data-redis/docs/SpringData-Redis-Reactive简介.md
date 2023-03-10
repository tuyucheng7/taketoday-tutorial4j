## 1. 概述

在本教程中，**我们将学习如何使用Spring Data的ReactiveRedisTemplate配置和实现Redis操作**。

我们将介绍ReactiveRedisTemplate的基本用法，例如如何在Redis中存储和检索对象。我们将看看如何使用ReactiveRedisConnection执行Redis命令。

要了解基础知识，请查看我们的[Spring Data Redis简介](https://www.baeldung.com/spring-data-redis-tutorial)。

## 2. 设置

要在我们的代码中使用ReactiveRedisTemplate，首先，我们需要添加对[Spring Boot的Redis Reactive](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-data-redis-reactive/3.0.3)模块的依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
</dependency>
```

## 3. 配置

然后，我们需要与我们的Redis服务器建立连接。如果想连接到localhost:6379的Redis服务器，我们不需要添加任何配置代码。

但是，**如果我们的服务器是远程的或位于不同的端口上，我们可以在LettuceConnectionFactory构造函数中提供主机名和端口**：

```java
@Bean
public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
    return new LettuceConnectionFactory(host, port);
}
```

## 4. 列表操作

Redis列表是按插入顺序排序的字符串列表。我们可以通过从左侧或右侧push或pop元素来添加或移除列表中的元素。

### 4.1 字符串模板

要使用列表，我们需要一个**ReactiveStringRedisTemplate实例来获取对RedisListOperations的引用**：

```java
@Autowired
private ReactiveStringRedisTemplate redisTemplate;
private ReactiveListOperations<String, String> reactiveListOps;

@Before
public void setup() {
    reactiveListOps = redisTemplate.opsForList();
}
```

### 4.2 LPUSH和LPOP

现在我们有了ReactiveListOperations的实例，让我们对一个以demo_list作为列表标识符的列表执行LPUSH操作。

之后，我们将在列表上执行LPOP，然后验证弹出的元素：

```java
@Test
public void givenListAndValues_whenLeftPushAndLeftPop_thenLeftPushAndLeftPop() {
    Mono<Long> lPush = reactiveListOps.leftPushAll(LIST_NAME, "first", "second")
        .log("Pushed");
    StepVerifier.create(lPush)
        .expectNext(2L)
        .verifyComplete();
    Mono<String> lPop = reactiveListOps.leftPop(LIST_NAME)
        .log("Popped");
    StepVerifier.create(lPop)
        .expectNext("second")
        .verifyComplete();
}
```

注意，在测试响应式组件时，我们可以使用StepVerifier来阻塞任务的完成。

## 5. 值运算符

我们可能还想使用自定义对象，而不仅仅是字符串。

那么，让我们对Employee对象做一些类似的操作来演示我们对POJO的操作：

```java
public class Employee implements Serializable {
    private String id;
    private String name;
    private String department;
    // ... getters and setters
    // ... hashCode and equals
}
```

### 5.1 Employee Template

我们需要创建ReactiveRedisTemplate的第二个实例。我们仍将使用String作为我们的键，但这次值将是Employee：

```java
@Bean
public ReactiveRedisTemplate<String, Employee> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
    StringRedisSerializer keySerializer = new StringRedisSerializer();
    Jackson2JsonRedisSerializer<Employee> valueSerializer = new Jackson2JsonRedisSerializer<>(Employee.class);
    RedisSerializationContext.RedisSerializationContextBuilder<String, Employee> builder = RedisSerializationContext.newSerializationContext(keySerializer);
    RedisSerializationContext<String, Employee> context = builder.value(valueSerializer).build();
    return new ReactiveRedisTemplate<>(factory, context);
}
```

为了正确地序列化一个自定义对象，我们需要指导Spring如何去做。在这里，我们通过**为value配置Jackson2JsonRedisSerializer来告诉模板使用Jackson库**。由于key只是一个字符串，我们可以为此使用StringRedisSerializer。

然后我们使用这个序列化上下文和我们的连接工厂来像以前一样创建一个模板。

接下来，我们将创建ReactiveValueOperations的实例，就像我们之前对ReactiveListOperations所做的那样：

```java
@Autowired
private ReactiveRedisTemplate<String, Employee> redisTemplate;
private ReactiveValueOperations<String, Employee> reactiveValueOps;

@Before
public void setup() {
    reactiveValueOps = redisTemplate.opsForValue();
}
```

### 5.2 保存和检索操作

现在我们有了ReactiveValueOperations的实例，让我们用它来存储Employee的实例：

```java
@Test
public void givenEmployee_whenSet_thenSet() {
    Mono<Boolean> result = reactiveValueOps.set("123", new Employee("123", "Bill", "Accounts"));
    StepVerifier.create(result)
        .expectNext(true)
        .verifyComplete();
}
```

然后我们可以从Redis中获取相同的对象：

```java
@Test
public void givenEmployeeId_whenGet_thenReturnsEmployee() {
    Mono<Employee> fetchedEmployee = reactiveValueOps.get("123");
    StepVerifier.create(fetchedEmployee)
        .expectNext(new Employee("123", "Bill", "Accounts"))
        .verifyComplete();
}
```

### 5.3 具有到期时间的操作

我们经常希望**将值放入自然过期的缓存中**，我们可以使用相同的set操作来做到这一点：

```java
@Test
public void givenEmployee_whenSetWithExpiry_thenSetsWithExpiryTime() throws InterruptedException {
    Mono<Boolean> result = reactiveValueOps.set("129", 
        new Employee("129", "John", "Programming"), 
        Duration.ofSeconds(1));
    StepVerifier.create(result)
        .expectNext(true)
        .verifyComplete();
    Thread.sleep(2000L); 
    Mono<Employee> fetchedEmployee = reactiveValueOps.get("129");
    StepVerifier.create(fetchedEmployee)
        .expectNextCount(0L)
        .verifyComplete();
}
```

请注意，此测试会自己进行一些阻塞以等待缓存key过期。

## 6. Redis命令

Redis命令基本上是Redis客户端可以在服务器上调用的方法。Redis支持数十种命令，其中一些我们已经看到过，例如LPUSH和LPOP。

**Operations API是围绕Redis命令集的更高级别的抽象**。

但是，**如果我们想更直接地使用Redis命令原语，那么Spring Data Redis Reactive也为我们提供了一个Commands API**。

因此，让我们通过Commands API的视角来了解String和Key命令。

### 6.1 String和Key命令

要执行Redis命令操作，我们将获取ReactiveKeyCommands和ReactiveStringCommands的实例。

我们可以从我们的ReactiveRedisConnectionFactory实例中获取它们：

```java
@Bean
public ReactiveKeyCommands keyCommands(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
    return reactiveRedisConnectionFactory.getReactiveConnection().keyCommands();
}

@Bean
public ReactiveStringCommands stringCommands(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
    return reactiveRedisConnectionFactory.getReactiveConnection().stringCommands();
}
```

### 6.2 Set和Get操作

我们可以使用ReactiveStringCommands通过单次调用存储多个key，**基本上是多次调用SET命令**。

然后，我们可以通过ReactiveKeyCommands检索这些key，**调用KEYS命令**：

```java
@Test
public void givenFluxOfKeys_whenPerformOperations_thenPerformOperations() {
    Flux<SetCommand> keys = Flux.just("key1", "key2", "key3", "key4");
        .map(String::getBytes)
        .map(ByteBuffer::wrap)
        .map(key -> SetCommand.set(key).value(key));
    StepVerifier.create(stringCommands.set(keys))
        .expectNextCount(4L)
        .verifyComplete();
    Mono<Long> keyCount = keyCommands.keys(ByteBuffer.wrap("key*".getBytes()))
        .flatMapMany(Flux::fromIterable)
        .count();
    StepVerifier.create(keyCount)
        .expectNext(4L)
        .verifyComplete();
}
```

请注意，如前所述，此API的级别要低得多。例如，**我们不处理高级对象，而是使用ByteBuffer发送字节流**。此外，我们使用了更多的Redis原语，如SET和SCAN。

最后，String和Key Commands只是[Spring Data Redis](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/connection/package-summary.html)响应式公开的众多命令接口中的两个。

## 7. 总结

在本教程中，我们介绍了使用Spring Data的Reactive Redis模板的基础知识以及将其与我们的应用程序集成的各种方式。