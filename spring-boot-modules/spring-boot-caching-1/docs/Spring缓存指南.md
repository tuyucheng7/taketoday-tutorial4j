## 1. 概述

在本教程中，我们将学习如何**在Spring中使用缓存抽象**，从总体上提高系统的性能。

我们为一些真实的方法示例启用简单的缓存，介绍如何通过智能缓存管理实际提高这些方法调用的性能。

## 2. 快速入门

Spring提供的核心缓存抽象位于spring-context模块中，所以在使用Maven时，我们的pom.xml应该包含以下依赖：

```xml

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.3.13</version>
</dependency>
```

此外，还有一个名为spring-context-support的模块，它构建于spring-context模块之上，
并提供了一些由EhCache或Caffeine等支持的CacheManager。
如果我们想将它们用作缓存存储，那么我们需要使用spring-context-support模块：

```xml

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context-support</artifactId>
    <version>5.3.13</version>
</dependency>
```

由于spring-context-support模块依赖于spring-context模块，因此不需要为spring-context单独声明依赖。

如果我们使用Spring Boot，那么我们可以简单添加spring-boot-starter-cache依赖项：

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
    <version>2.6.1</version>
</dependency>
```

该starter包含了spring-context-support模块。

## 3. 启用缓存

为了启用缓存，我们可以使用Spring注解，就像在Spring中启用任何其他配置级功能一样。

我们可以简单地通过将@EnableCaching注解添加到任何配置类来启用缓存功能：

```java

@Configuration
@EnableCaching
public class CachingConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("addresses");
    }
}
```

当然，我们也可以使用**XML配置启用缓存管理**：

```xml

<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:cache="http://www.springframework.org/schema/cache"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans 
       http://www.springframework.org/schema/beans/spring-beans-4.2.xsd
        http://www.springframework.org/schema/cache 
        http://www.springframework.org/schema/cache/spring-cache-4.2.xsd">

    <cache:annotation-driven/>

    <bean id="cacheManager" class="org.springframework.cache.support.SimpleCacheManager">
        <property name="caches">
            <set>
                <bean class="org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean" name="addresses"/>
            </set>
        </property>
    </bean>
</beans>
```

**注意：启用缓存后，作为最小配置，我们必须注册一个cacheManager**。

### 3.1 使用Spring Boot

当使用Spring Boot时，只要类路径存在缓存的依赖项并且指定了@EnableCaching注解，就会注册上述示例相同的ConcurrentMapCacheManager。
因此不需要单独的bean声明。

此外，我们可以使用一个或多个CacheManagerCustomizer<T\> bean自定义自动配置的CacheManager：

```java

@Component
public class SimpleCacheCustomizer implements CacheManagerCustomizer<ConcurrentMapCacheManager> {
    static final String USER_CACHE = "users";
    static final String TRANSACTIONS_CACHE = "transactions";
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCacheCustomizer.class);

    @Override
    public void customize(ConcurrentMapCacheManager cacheManager) {
        LOGGER.info("Customizing Cache Manager");
        cacheManager.setCacheNames(asList(USER_CACHE, TRANSACTIONS_CACHE));
    }
}
```

CacheAutoConfiguration类自动获取这些CacheManagerCustomizer，并在当前CacheManager完成初始化之前将其应用于当前CacheManager。

## 4. 注解方式使用缓存

一旦我们启用了缓存，下一步就是将缓存行为绑定到带有声明性注解的方法。

### 4.1 @Cacheable

为方法启用缓存行为的最简单方法是使用@Cacheable对其进行标注，并在参数中指定需要使用的缓存名称：

```java
public abstract class AbstractService {

    @Cacheable(value = "addresses")
    public String getAddress(final Customer customer) {
        return customer.getAddress();
    }
}
```

getAddress()方法调用会首先检查缓存addresses中内容，然后再实际调用该方法，然后缓存结果。

虽然在大多数情况下，一个缓存就足够了，但Spring框架还支持将多个缓存作为参数传递：

```java
public abstract class AbstractService {

    @Cacheable({"addresses", "directory"})
    public String getAddress(final Customer customer) {
        return customer.getAddress();
    }
}
```

在这种情况下，如果任何缓存包含所需的结果，则返回结果并且不调用该方法。

### 4.2 @CacheEvict

现在，将所有方法设为@Cacheable会有什么问题？

问题在于大小。**我们不想用我们不经常需要的值填充缓存**，这样的话缓存可能会变得非常大，非常快的增长，此时可以删除大量过时或未使用的数据。

我们可以使用@CacheEvict注解来指示删除一个或多个值，以便可以再次将新值加载到缓存中：

```java
public abstract class AbstractService {

    @CacheEvict(value = "addresses", allEntries = true)
    public String getAddress(final Customer customer) {
        return customer.getAddress();
    }
}
```

在这里，我们指定要清楚的缓存和allEntries属性为true；这将清除缓存addresses中的所有条目。

### 4.3 @CachePut

虽然@CacheEvict通过删除过时和未使用的条目来减少在大型缓存中查找条目的开销，但我们希望**避免从缓存中驱逐过多的数据**。

相反，每当我们更改条目时，我们都会选择性地更新它们。

使用@CachePut注解，我们可以在不干扰方法执行的情况下更新缓存的内容。也就是说，该方法将始终被执行并缓存结果：

```java
public abstract class AbstractService {

    @CachePut(value = "addresses")
    public String getAddress(final Customer customer) {
        return customer.getAddress();
    }
}
```

@Cacheable和@CachePut之间的区别**在于@Cacheable将跳过运行该方法，而@CachePut将实际运行该方法，然后将其结果放入缓存中**。

### 4.4 @Caching

如果我们想使用多个相同类型的注解来缓存一个方法怎么办？让我们看一个使用不正确的例子：

```java
public abstract class AbstractService {

    @CacheEvict("addresses")
    @CacheEvict(value = "directory", key = customer.name)
    public String getAddress(Customer customer) {
        // ...
    }
}
```

上述代码编译不通过，因为Java不允许为给定方法声明同一类型的多个注解。

正确的做法是：

```java
public abstract class AbstractService {

    @Caching(evict = {
            @CacheEvict("addresses"),
            @CacheEvict(value = "directory", key = "#customer.name")
    })
    public String getAddress(Customer customer) {
        // ...
    }
}
```

如上面的代码片段所示，我们可以**用@Caching将多个缓存注解进行分组**，并使用它来实现我们自己自定义的缓存逻辑。

### 4.5 @CacheConfig

使用@CacheConfig注解，我们**可以将一些缓存配置简化到类级别的单个位置**，这样我们就不必多次声明：

```java

@CacheConfig(cacheNames = {"addresses"})
public class CustomerDataService {

    @Cacheable
    public String getAddress(Customer customer) {
        // ...
    }
}
```

## 5. 条件缓存

有时，缓存可能在所有情况下都不适用于方法。

对于下面的方法会每次执行以及缓存结果：

```java
public abstract class AbstractService {

    @CachePut(value = "addresses")
    public String getAddress(Customer customer) {
        // ...
    }
}
```

### 5.1 condition参数

如果我们想要更多地控制注解何时处于启用状态，
我们可以使用@CachePut注解的condition参数，该参数接收SpEL表达式并确保根据对该表达式的求值来缓存结果：

```java
public abstract class AbstractService {

    @CachePut(value = "addresses", condition = "#customer.name=='Tom'")
    public String getAddress(Customer customer) {
        // ...
    }
}
```

### 5.2 unless参数

我们还可以**使用unless参数，根据方法的输出而不是输入的参数来控制缓存**：

```java
public abstract class AbstractService {

    @CachePut(value = "addresses", unless = "#result.length()<64")
    public String getAddress(Customer customer) {
        // ...
    }
}
```

上述注解将缓存地址，除非它们的长度小于64个字符。

重要的是要知道condition和unless参数可以与所有缓存注解一起使用。

这种条件缓存对于管理大型结果非常有效，它还可用于根据输入参数自定义行为，而不是对所有操作强制执行通用行为。

## 6. 基于XML的声明式缓存

如果我们无法访问应用程序的源代码，或者想要在外部注入缓存行为，我们还可以使用基于XML的声明式缓存。

下面是我们的XML配置：

```xml

<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:cache="http://www.springframework.org/schema/cache"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.2.xsd
        http://www.springframework.org/schema/cache http://www.springframework.org/schema/cache/spring-cache-4.2.xsd  
        http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-4.2.xsd">

    <cache:annotation-driven/>
    <!-- <context:annotation-config/> -->

    <!-- the service that you wish to make cacheable. -->
    <bean id="customerDataService" class="cn.tuyucheng.taketoday.caching.example.CustomerDataService"/>

    <bean id="cacheManager" class="org.springframework.cache.support.SimpleCacheManager">
        <property name="caches">
            <set>
                <bean class="org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean" name="directory"/>
                <bean class="org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean" name="addresses"/>
            </set>
        </property>
    </bean>

    <!-- define caching behavior -->
    <cache:advice id="cachingBehavior" cache-manager="cacheManager">
        <cache:caching cache="addresses">
            <cache:cacheable method="getAddress" key="#customer.name"/>
        </cache:caching>
    </cache:advice>

    <!-- apply the behavior to all the implementations of CustomerDataService  -->
    <aop:config>
        <aop:advisor advice-ref="cachingBehavior"
                     pointcut="execution(* cn.tuyucheng.taketoday.caching.example.CustomerDataService.*(..))"/>
    </aop:config>
</beans>
```

## 7. 基于Java的缓存

下面是等效的Java配置：

```java

@Configuration
@EnableCaching
@ComponentScan("cn.tuyucheng.taketoday.caching.example")
public class CachingConfig {

    @Bean
    public CacheManager cacheManager() {
        final SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
                new ConcurrentMapCache("directory"),
                new ConcurrentMapCache("address"))
        );
        return cacheManager;
    }
}
```

这是我们的CustomerDataService：

```java

@Component
public class CustomerDataService {

    @Cacheable(value = "addresses", key = "#customer.name")
    public String getAddress(Customer customer) {
        return customer.getAddress();
    }
}
```

## 8. 总结

在本文中，我们介绍了Spring中缓存的基础知识，以及如何通过注解充分利用该抽象。