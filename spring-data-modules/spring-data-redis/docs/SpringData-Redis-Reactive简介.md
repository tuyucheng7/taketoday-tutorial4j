## **一、简介**

在本教程中，**我们将学习如何使用 Spring Data 的\*ReactiveRedisTemplate 配置和实现 Redis 操作。\*** 

我们将回顾*ReactiveRedisTemplate*的基本用法，例如如何在 Redis 中存储和检索对象。我们将看看如何使用 *ReactiveRedisConnection*执行 Redis 命令。

要了解基础知识，请查看我们 [的 Spring Data Redis 简介](https://www.baeldung.com/spring-data-redis-tutorial)。

## **2.设置**

要在我们的代码中使用 *ReactiveRedisTemplate* ，首先，我们需要添加[对 Spring Boot 的 Redis Reactive](https://search.maven.org/classic/#search|ga|1|a%3A"spring-boot-starter-data-redis-reactive")模块的依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
</dependency>复制
```

## **三、配置**

然后*，* 我们需要与我们的 Redis 服务器建立连接。*如果想连接到localhost:6379*的 Redis 服务器，我们不需要添加任何配置代码 。

但是，**如果我们的服务器是远程的或在不同的端口上，我们可以在\*LettuceConnectionFactory\* 构造函数中提供主机名和端口：**

```java
@Bean
public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
    return new LettuceConnectionFactory(host, port);
}复制
```

## **4.列表操作**

Redis 列表是按插入顺序排序的字符串列表。我们可以通过从左侧或右侧压入或弹出元素来添加或移除列表中的元素。

### **4.1. 字符串模板**

要使用列表，我们需要一个 ***ReactiveStringRedisTemplate\*实例 来获取对\*RedisListOperations\***的引用：

```java
@Autowired
private ReactiveStringRedisTemplate redisTemplate;
private ReactiveListOperations<String, String> reactiveListOps;
@Before
public void setup() {
    reactiveListOps = redisTemplate.opsForList();
}复制
```

### **4.2. LPUSH 和 LPOP**

现在我们有了 *ReactiveListOperations 的实例，*让我们用 *demo_list*作为列表的标识符对列表执行 LPUSH 操作。

之后，我们将在列表上执行 LPOP，然后验证弹出的元素：

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
}复制
```

注意，在测试响应式组件时，我们可以使用 *StepVerifier*来阻塞任务的完成。

## **5.价值运营**

我们可能还想使用自定义对象，而不仅仅是字符串。

*那么，让我们对Employee*对象做一些类似的操作来演示我们对 POJO 的操作：

```java
public class Employee implements Serializable {
    private String id;
    private String name;
    private String department;
    // ... getters and setters
    // ... hashCode and equals
}复制
```

### **5.1. 员工模板**

我们需要创建 *ReactiveRedisTemplate 的第二个实例。*我们仍将使用*String* 作为我们的键，但这次值将是*Employee*：

```java
@Bean
public ReactiveRedisTemplate<String, Employee> reactiveRedisTemplate(
  ReactiveRedisConnectionFactory factory) {
    StringRedisSerializer keySerializer = new StringRedisSerializer();
    Jackson2JsonRedisSerializer<Employee> valueSerializer =
      new Jackson2JsonRedisSerializer<>(Employee.class);
    RedisSerializationContext.RedisSerializationContextBuilder<String, Employee> builder =
      RedisSerializationContext.newSerializationContext(keySerializer);
    RedisSerializationContext<String, Employee> context = 
      builder.value(valueSerializer).build();
    return new ReactiveRedisTemplate<>(factory, context);
}复制
```

为了正确地序列化一个自定义对象，我们需要指导 Spring 如何去做。 **在这里，我们通过为 value配置*****Jackson2JsonRedisSerializer***来告诉模板**使用 Jackson 库** 。由于键只是一个字符串，我们可以为此使用*StringRedisSerializer* 。

然后我们使用这个序列化上下文和我们的连接工厂来像以前一样创建一个模板。

接下来，我们将创建 ReactiveValueOperations 的实例，*就像*我们之前对 *ReactiveListOperations*所做的那样：

```java
@Autowired
private ReactiveRedisTemplate<String, Employee> redisTemplate;
private ReactiveValueOperations<String, Employee> reactiveValueOps;
@Before
public void setup() {
    reactiveValueOps = redisTemplate.opsForValue();
}复制
```

### **5.2. 保存和检索操作**

现在我们有了 *ReactiveValueOperations 的实例，* 让我们用它来存储*Employee*的实例：

```java
@Test
public void givenEmployee_whenSet_thenSet() {
    Mono<Boolean> result = reactiveValueOps.set("123", 
      new Employee("123", "Bill", "Accounts"));
    StepVerifier.create(result)
      .expectNext(true)
      .verifyComplete();
}复制
```

然后我们可以从 Redis 中获取相同的对象：

```java
@Test
public void givenEmployeeId_whenGet_thenReturnsEmployee() {
    Mono<Employee> fetchedEmployee = reactiveValueOps.get("123");
    StepVerifier.create(fetchedEmployee)
      .expectNext(new Employee("123", "Bill", "Accounts"))
      .verifyComplete();
}复制
```

### **5.3. 有到期时间的操作**

我们经常希望**将值放入自然会过期的缓存中**，我们可以使用相同的*设置* 操作来做到这一点：

```java
@Test
public void givenEmployee_whenSetWithExpiry_thenSetsWithExpiryTime() 
  throws InterruptedException {
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
}复制
```

请注意，此测试会自己进行一些阻塞以等待缓存键过期。

## **6.Redis命令**

Redis 命令基本上是 Redis 客户端可以在服务器上调用的方法。Redis 支持数十种命令，其中一些我们已经见过，例如 LPUSH 和 LPOP。

***Operations\* API 是围绕 Redis 命令集的更高级别的抽象。**

但是，**如果我们想更直接地使用 Redis 命令原语，那么 Spring Data Redis Reactive 也为我们提供了一个\*命令\* API。**

因此，让我们通过*Commands* API 的视角来了解 String 和 Key 命令。

### **6.1. 字符串和键盘命令**

要执行 Redis 命令操作，我们将获得 *ReactiveKeyCommands*和 *ReactiveStringCommands 的实例。*

*我们可以从我们的ReactiveRedisConnectionFactory*实例中获取它们：

```java
@Bean
public ReactiveKeyCommands keyCommands(ReactiveRedisConnectionFactory 
  reactiveRedisConnectionFactory) {
    return reactiveRedisConnectionFactory.getReactiveConnection().keyCommands();
}
@Bean
public ReactiveStringCommands stringCommands(ReactiveRedisConnectionFactory 
  reactiveRedisConnectionFactory) {
    return reactiveRedisConnectionFactory.getReactiveConnection().stringCommands();
}复制
```

### **6.2. 设置和获取操作**

我们可以使用*ReactiveStringCommands*通过一次调用存储多个键，**基本上是多次调用 SET 命令**。

*然后，我们可以通过ReactiveKeyCommands*检索这些键 ，**调用 KEYS 命令**：

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
}复制
```

请注意，如前所述，此 API 的级别要低得多。例如，**我们不处理高级对象，而是使用\*ByteBuffer\***发送字节流。此外，我们使用了更多的 Redis 原语，如 SET 和 SCAN。

最后，String 和 Key Commands 只是[Spring Data Redis](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/connection/package-summary.html)响应式公开的众多命令接口中的两个。

## **七、结论**

在本教程中，我们介绍了使用 Spring Data 的 Reactive Redis 模板的基础知识以及将其与我们的应用程序集成的各种方式。