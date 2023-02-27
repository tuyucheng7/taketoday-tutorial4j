## 1. 概述

在本教程中，我们说明如何使用Spring Cache创建自定义KeyGenerator。

## 2. KeyGenerator

KeyGenerator负责为缓存中的每个数据项生成每个key，用于在检索时查找数据项。

Spring的默认实现是SimpleKeyGenerator，它使用提供的方法参数来生成key。
这意味着如果我们有两个方法使用相同的缓存名称和一组参数类型集，那么它很可能会导致冲突。

这也意味着缓存数据会被另一个方法覆盖。

## 3. 自定义KeyGenerator

KeyGenerator只需要实现一个方法，以下是KeyGenerator接口的定义：

```java

@FunctionalInterface
public interface KeyGenerator {
    Object generate(Object target, Method method, Object... params);
}
```

如果没有正确实现或使用它，可能会导致覆盖缓存数据。

以下是我们的自定义实现：

```java
public class CustomKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        return target.getClass().getSimpleName() + "_" + method.getName() + "_" + StringUtils.arrayToDelimitedString(params, "_");
    }
}
```

之后，我们有两种可能的使用方式；第一种是在ApplicationConfig中声明一个bean。

重要的是要注意该类必须从CachingConfigurerSupport继承或实现CacheConfigurer：

```java

@EnableCaching
@Configuration
public class ApplicationConfig extends CachingConfigurerSupport {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        Cache booksCache = new ConcurrentMapCache("books");
        cacheManager.setCaches(Arrays.asList(booksCache));
        return cacheManager;
    }

    @Bean("customKeyGenerator")
    public KeyGenerator keyGenerator() {
        return new CustomKeyGenerator();
    }
}
```

第二种方法是将其仅用于特定方法：

```java

@Component
public class BookService {

    @Cacheable(value = "books", keyGenerator = "customKeyGenerator")
    public List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book(1, "The Counterfeiters", "André Gide"));
        books.add(new Book(2, "Peer Gynt and Hedda Gabler", "Henrik Ibsen"));
        return books;
    }
}
```

## 4. 总结

在本文中，我们介绍了一种实现自定义Spring Cache中KeyGenerator的方法。