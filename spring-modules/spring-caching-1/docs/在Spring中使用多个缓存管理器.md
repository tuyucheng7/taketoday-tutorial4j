## 1. 概述

在本教程中，我们介绍如何在Spring应用程序中配置多个缓存管理器。

## 2. 缓存

Spring将缓存应用于方法，这样我们的应用程序就不会为相同的输入多次执行同一方法。

在Spring应用程序中实现缓存非常容易，这可以通过在我们的配置类中添加@EnableCaching注解来完成：

```java

@Configuration
@EnableCaching
public class MultipleCacheManagerConfig extends CachingConfigurerSupport {

}
```

然后我们可以通过在方法上添加@Cacheable注解来开始缓存方法的输出：

```java

@Component
public class CustomerDetailBO {

    @Autowired
    private CustomerDetailRepository customerDetailRepository;

    @Cacheable(cacheNames = "customers")
    public Customer getCustomerDetail(Integer customerId) {
        return customerDetailRepository.getCustomerDetail(customerId);
    }
}
```

一旦我们添加了上述配置，Spring Boot本身就会为我们创建一个缓存管理器。

**默认情况下，如果我们没有明确指定任何其他缓存，它会使用ConcurrentHashMap作为底层缓存**。

## 3. 配置多个缓存管理器

在某些情况下，我们可能需要在应用程序中使用多个缓存管理器。因此，让我们通过一个示例来看看如何在Spring Boot应用程序中做到这一点。

**在我们的例子中，我们将使用一个CaffeineCacheManager和一个简单的ConcurrentMapCacheManager**。

CaffeineCacheManager由spring-boot-starter-cache提供。
如果类路径存在Caffeine依赖，它将由Spring自动配置，这是一个用Java 8编写的缓存库。

ConcurrentMapCacheManager使用ConcurrentHashMap实现缓存。

我们可以通过以下方式做到这一点。

### 3.1 使用@Primary

我们可以在配置类中创建两个CacheManager bean，然后，我们可以将一个bean标记为@Primary：

```java

@Configuration
@EnableCaching
public class MultipleCacheManagerConfig {

    @Bean
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("customers", "orders");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(200)
                .maximumSize(500)
                .weakKeys()
                .recordStats());
        return cacheManager;
    }

    @Bean
    public CacheManager alternateCacheManager() {
        return new ConcurrentMapCacheManager("customerOrders", "orderprice");
    }
}
```

现在，Spring Boot将使用CaffeineCacheManager作为所有方法的默认缓存管理器，直到我们显式指定cacheManager参数为“alternateCacheManager”：

```java

@Component
public class CustomerDetailBO {

    @Autowired
    private CustomerDetailRepository customerDetailRepository;

    @Cacheable(cacheNames = "customers")
    public Customer getCustomerDetail(Integer customerId) {
        return customerDetailRepository.getCustomerDetail(customerId);
    }

    @Cacheable(cacheNames = "customerOrders", cacheManager = "alternateCacheManager")
    public List<Order> getCustomerOrders(Integer customerId) {
        return customerDetailRepository.getCustomerOrders(customerId);
    }
}
```

在上面的示例中，我们的应用程序将CaffeineCacheManager用于getCustomerDetail()方法。
对于getCustomerOrders()方法，它将使用alternateCacheManager作为缓存管理器。

### 3.2 继承CachingConfigurerSupport

另一种方法是继承CachingConfigurerSupport类并重写cacheManager()方法，此方法返回一个bean，它将成为我们应用程序的默认缓存管理器：

```java

@Configuration
@EnableCaching
public class MultipleCacheManagerConfig extends CachingConfigurerSupport {

    @Bean
    @Override
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("customers", "orders");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(200)
                .maximumSize(500)
                .weakKeys()
                .recordStats());
        return cacheManager;
    }

    @Bean
    public CacheManager alternateCacheManager() {
        return new ConcurrentMapCacheManager("customerOrders", "orderprice");
    }
}
```

请注意，我们仍然可以创建另一个名为alternateCacheManager的bean，
我们可以通过显式指定方法将这个alternateCacheManager用做缓存管理器，就像我们在上一个示例中所做的那样。

### 3.3 使用CacheResolver

我们可以实现CacheResolver接口并创建一个自定义的CacheResolver：

```java
public class MultipleCacheResolver implements CacheResolver {

    private static final String ORDER_CACHE = "orders";
    private static final String ORDER_PRICE_CACHE = "orderprice";

    private final CacheManager simpleCacheManager;
    private final CacheManager caffeineCacheManager;

    public MultipleCacheResolver(CacheManager simpleCacheManager, CacheManager caffeineCacheManager) {
        this.simpleCacheManager = simpleCacheManager;
        this.caffeineCacheManager = caffeineCacheManager;
    }

    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        Collection<Cache> caches = new ArrayList<>();
        if ("getOrderDetail".equals(context.getMethod().getName()))
            caches.add(caffeineCacheManager.getCache(ORDER_CACHE));
        else
            caches.add(simpleCacheManager.getCache(ORDER_PRICE_CACHE));
        return caches;
    }
}
```

在这种情况下，我们必须重写CacheResolver接口的resolveCaches()方法。

在我们的示例中，我们根据方法名称选择缓存管理器。在此之后，我们需要创建一个自定义CacheResolver bean：

```java

@Configuration
@EnableCaching
public class MultipleCacheManagerConfig extends CachingConfigurerSupport {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("customers", "orders");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(200)
                .maximumSize(500)
                .weakKeys()
                .recordStats());
        return cacheManager;
    }

    @Bean
    public CacheManager alternateCacheManager() {
        return new ConcurrentMapCacheManager("customerOrders", "orderprice");
    }

    @Bean
    public CacheResolver cacheResolver() {
        return new MultipleCacheResolver(alternateCacheManager(), cacheManager());
    }
}
```

现在我们可以使用自定义的CacheResolver为我们的方法解析缓存管理器：

```java

@Component
public class OrderDetailBO {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Cacheable(cacheNames = "orders", cacheResolver = "cacheResolver")
    public Order getOrderDetail(Integer orderId) {
        return orderDetailRepository.getOrderDetail(orderId);
    }

    @Cacheable(cacheNames = "orderprice", cacheResolver = "cacheResolver")
    public double getOrderPrice(Integer orderId) {
        return orderDetailRepository.getOrderPrice(orderId);
    }
}
```

在这里，我们在cacheResolver参数中传递了CacheResolver bean的名称。

## 4. 总结

在本文中，我们了解了如何在Spring Boot应用程序中启用缓存。然后，我们介绍了三种在应用程序中使用多个缓存管理器的方法。