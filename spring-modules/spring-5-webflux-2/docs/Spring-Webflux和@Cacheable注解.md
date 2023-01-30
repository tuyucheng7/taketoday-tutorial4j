## 1. 简介

在本文中，我们将解释Spring WebFlux如何与@Cacheable注解进行交互。首先，我们将介绍一些常见问题以及如何避免这些问题。接下来，我们将介绍可用的解决方法。最后，与往常一样，我们将提供代码示例。

## 2. @Cacheable和Reactive类型

这个话题还是比较新的，在撰写本文时，@Cacheable和响应式框架之间还没有流畅的集成，**主要问题是没有非阻塞缓存实现(JSR-107缓存API是阻塞的)**，只有[Redis](https://www.baeldung.com/spring-data-redis-tutorial)提供了响应式驱动程序。

尽管我们在上一段中提到了问题，但我们仍然可以在我们的服务方法上使用@Cacheable，**这将导致缓存我们的包装器对象(Mono或Flux)但不会缓存我们方法的实际结果**。

### 2.1 项目设置

让我们用一个测试来说明这一点，在测试之前，我们需要设置我们的项目。我们将使用响应式[MongoDB](https://www.baeldung.com/spring-data-mongodb-tutorial)驱动程序创建一个简单的[Spring WebFlux](https://www.baeldung.com/spring-webflux)项目，我们将使用[Testcontainers](https://www.baeldung.com/spring-boot-testcontainers-integration-test)，而不是将MongoDB作为一个单独的进程运行。

我们的测试类将使用@SpringBootTest注解，并将包含：

```java
final static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

@DynamicPropertySource
static void mongoDbProperties(DynamicPropertyRegistry registry) {
    mongoDBContainer.start();
    registry.add("spring.data.mongodb.uri",  mongoDBContainer::getReplicaSetUrl);
}
```

这些代码将启动一个MongoDB实例并将URI传递给Spring Boot以自动配置Mongo Repository。

对于此测试，我们将使用save和getItem方法创建ItemService类：

```java
@Service
public class ItemService {

    private final ItemRepository repository;

    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }
    
    @Cacheable("items")
    public Mono<Item> getItem(String id){
        return repository.findById(id);
    }
    
    public Mono<Item> save(Item item){
        return repository.save(item);
    }
}
```

在application.properties中，我们为缓存和Repository设置日志记录器，以便我们可以监控测试中发生的事情：

```properties
logging.level.org.springframework.data.mongodb.core.ReactiveMongoTemplate=DEBUG
logging.level.org.springframework.cache=TRACE
```

### 2.2 初始测试

设置完成后，我们可以运行测试并分析结果：

```java
@Test
public void givenItem_whenGetItemIsCalled_thenMonoIsCached() {
    Mono<Item> glass = itemService.save(new Item("glass", 1.00));

    String id = glass.block().get_id();

    Mono<Item> mono = itemService.getItem(id);
    Item item = mono.block();

    assertThat(item).isNotNull();
    assertThat(item.getName()).isEqualTo("glass");
    assertThat(item.getPrice()).isEqualTo(1.00);

    Mono<Item> mono2 = itemService.getItem(id);
    Item item2 = mono2.block();

    assertThat(item2).isNotNull();
    assertThat(item2.getName()).isEqualTo("glass");
    assertThat(item2.getPrice()).isEqualTo(1.00);
}
```

在控制台中，我们可以看到这个输出(为简洁起见，只显示了必要的部分)：

```shell
Inserting Document containing fields: [name, price, _class] in collection: item...
Computed cache key '618817a52bffe4526c60f6c0' for operation Builder[public reactor.core.publisher.Mono...
No cache entry for key '618817a52bffe4526c60f6c0' in cache(s) [items]
Computed cache key '618817a52bffe4526c60f6c0' for operation Builder[public reactor.core.publisher.Mono...
findOne using query: { "_id" : "618817a52bffe4526c60f6c0"} fields: Document{{}} for class: class cn.tuyucheng.taketoday.caching.Item in collection: item...
findOne using query: { "_id" : { "$oid" : "618817a52bffe4526c60f6c0"}} fields: {} in db.collection: test.item
Computed cache key '618817a52bffe4526c60f6c0' for operation Builder[public reactor.core.publisher.Mono...
Cache entry for key '618817a52bffe4526c60f6c0' found in cache 'items'
findOne using query: { "_id" : { "$oid" : "618817a52bffe4526c60f6c0"}} fields: {} in db.collection: test.item
```

在第一行，我们看到了插入方法。之后，当调用getItem时，Spring会检查缓存中是否有此项，但没有找到，然后访问MongoDB来获取这条记录。在第二次getItem调用中，Spring再次检查缓存并找到该键的条目，但仍会转到MongoDB以获取该记录。

**发生这种情况是因为Spring缓存了getItem方法的结果，它是Mono包装器对象。但是，对于结果本身，它仍然需要从数据库中获取记录**。

在以下部分中，我们将提供此问题的解决方法。

## 3. 缓存Mono/Flux的结果

Mono和Flux有一个内置的缓存机制，我们可以在这种情况下使用它作为解决方法。正如我们之前所说，@Cacheable缓存包装器对象，并且使用内置缓存，我们可以创建对服务方法实际结果的引用：

```java
@Cacheable("items")
public Mono<Item> getItem_withCache(String id) {
    return repository.findById(id).cache();
}
```

让我们使用这个新的服务方法运行上一章的测试，输出将如下所示：

```shell
Inserting Document containing fields: [name, price, _class] in collection: item
Computed cache key '6189242609a72e0bacae1787' for operation Builder[public reactor.core.publisher.Mono...
No cache entry for key '6189242609a72e0bacae1787' in cache(s) [items]
Computed cache key '6189242609a72e0bacae1787' for operation Builder[public reactor.core.publisher.Mono...
findOne using query: { "_id" : "6189242609a72e0bacae1787"} fields: Document{{}} for class: class cn.tuyucheng.taketoday.caching.Item in collection: item
findOne using query: { "_id" : { "$oid" : "6189242609a72e0bacae1787"}} fields: {} in db.collection: test.item
Computed cache key '6189242609a72e0bacae1787' for operation Builder[public reactor.core.publisher.Mono...
Cache entry for key '6189242609a72e0bacae1787' found in cache 'items'
```

我们可以看到几乎相似的输出，只是这一次，当在缓存中找到一个元素时，没有额外的数据库查找。使用此解决方案，当我们的缓存过期时存在潜在问题。**由于我们使用的是缓存的缓存，因此我们需要在两个缓存上设置适当的过期时间。经验法则是Flux缓存TTL应该比@Cacheable长**。

## 4. 使用Reactor插件

Reactor 3插件允许我们通过CacheMono和CacheFlux类以流畅的方式使用不同的缓存实现。对于这个例子，我们将配置[Caffeine](https://www.baeldung.com/spring-boot-caffeine-cache)缓存：

```java
public ItemService(ItemRepository repository) {
    this.repository = repository;
    this.cache = Caffeine.newBuilder().build(this::getItem_withAddons);
}
```

在ItemService构造函数中，我们使用最小配置初始化Caffeine缓存，并在新的服务方法中使用该缓存：

```java
@Cacheable("items")
public Mono<Item> getItem_withAddons(String id) {
    return CacheMono.lookup(cache.asMap(), id)
        .onCacheMissResume(() -> repository.findById(id).cast(Object.class)).cast(Item.class);
}
```

因为CacheMono在内部使用Signal类，所以我们需要进行一些强制转换以返回适当的对象。

当我们重新运行之前的测试时，我们将获得与上一个示例类似的输出。

## 5. 总结

在本文中，我们介绍了Spring WebFlux如何与@Cacheable交互。此外，我们还描述了如何使用它们以及一些常见问题。