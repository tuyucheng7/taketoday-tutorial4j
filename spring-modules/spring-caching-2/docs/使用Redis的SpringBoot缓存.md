## 1. 概述

在这个简短的教程中，我们介绍如何将Redis配置为Spring Boot缓存的数据存储。

## 2. 依赖

首先，我们需要添加spring-boot-starter-cache和spring-boot-starter-data-redis依赖：

```text
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
    <version>2.6.1</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <version>2.6.1</version>
</dependency>
```

## 3. 配置

通过添加上述依赖和@EnableCaching注解，Spring Boot将使用默认缓存配置自动配置RedisCacheManager。
但是，在缓存管理器初始化之前，我们可以通过几种方式修改此配置。

首先，让我们创建一个RedisCacheConfiguration bean：

```java

@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60))
                .disableCachingNullValues()
                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
}
```

这使我们**可以更好地控制默认配置**。例如，我们可以设置所需的存活时间(TTL)值，并为动态缓存创建自定义默认序列化策略。

为了完全控制缓存设置，让我们注册自己的RedisCacheManagerBuilderCustomizer bean：

```java

@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder
                .withCacheConfiguration("itemCache", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration("customerCache", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)));
    }
}
```

在这里，我们使用RedisCacheManagerBuilder和RedisCacheConfiguration分别为itemCache和customerCache配置了10分钟和5分钟的TTL值。
这有助于在每个缓存的基础上进一步微调缓存行为，包括空值、key前缀和二进制序列化。

值得一提的是，Redis实例的默认连接详细信息是localhost:6379，你可以通过Redis的配置属性自定义这些值。

## 4. 案例

在我们的示例中，我们有一个ItemService组件，它从数据库中检索Item信息。
实际上，对数据库的调用就意味着高昂的操作，因此很适合将缓存用于这种情况。

首先，我们使用嵌入式Redis服务器为此组件创建集成测试：

```java

@Import({CacheConfig.class, ItemService.class})
@ExtendWith(SpringExtension.class)
@ImportAutoConfiguration(classes = {CacheAutoConfiguration.class, RedisAutoConfiguration.class})
@EnableCaching
class ItemServiceCachingIntegrationTest {

    private static final String AN_ID = "id-1";
    private static final String A_DESCRIPTION = "an item";

    @MockBean
    private ItemRepository mockItemRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void givenRedisCaching_whenFindItemById_thenItemReturnedFromCache() {
        Item anItem = new Item(AN_ID, A_DESCRIPTION);
        given(mockItemRepository.findById(AN_ID)).willReturn(Optional.of(anItem));

        Item itemCacheMiss = itemService.getItemForId(AN_ID);
        Item itemCacheHit = itemService.getItemForId(AN_ID);

        assertThat(itemCacheMiss).isEqualTo(anItem);
        assertThat(itemCacheHit).isEqualTo(anItem);

        verify(mockItemRepository, times(1)).findById(AN_ID);
        assertThat(itemFromCache()).isEqualTo(anItem);
    }

    private Object itemFromCache() {
        return cacheManager.getCache("itemCache").get(AN_ID).get();
    }

    @TestConfiguration
    static class EmbeddedRedisConfiguration {
        private final RedisServer redisServer;

        public EmbeddedRedisConfiguration() {
            this.redisServer = new RedisServer();
        }

        @PostConstruct
        public void startRedis() {
            redisServer.start();
        }

        @PreDestroy
        public void stopRedis() {
            this.redisServer.stop();
        }
    }
}
```

在这里，我们为缓存行为创建一个测试切片，并调用getItemForId两次。
第一次调用应该从Repository中获取Item，但第二次调用应该从缓存中返回Item而不调用Repository。

最后，让我们使用Spring的@Cacheable注解启用缓存行为：

```java

@Service
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Cacheable(value = "itemCache")
    public Item getItemForId(String id) {
        return itemRepository.findById(id).orElseThrow(RuntimeException::new);
    }
}
```

上面的@Cacheable(value = "itemCache")配置应用缓存逻辑，同时依赖于我们之前配置的Redis缓存基础配置。

## 5. 总结

在本文中，我们了解了如何使用Redis用于Spring缓存。

我们首先描述了如何以最少的配置自动配置Redis缓存，然后我们研究了如何通过注册配置bean来进一步自定义缓存行为。

最后，我们通过一个实际案例在实践中演示这种缓存。