## 1. 简介

在本文中，我们将介绍 Guava [CacheLoader](https://google.github.io/guava/releases/21.0/api/docs/com/google/common/cache/CacheLoader.html)。

在进一步阅读之前，建议先对[LoadingCache](https://google.github.io/guava/releases/21.0/api/docs/com/google/common/cache/LoadingCache.html)类有一个基本的了解。这是因为CacheLoader专门与它一起工作。

本质上，CacheLoader是一个函数，用于在 Guava [LoadingCache](https://google.github.io/guava/releases/21.0/api/docs/com/google/common/cache/LoadingCache.html)中找不到值时计算值。

## 2. 将CacheLoader与LoadingCache 一起使用

当LoadingCache 缓存未命中，或者需要刷新缓存时，CacheLoader将用于计算值。这有助于将我们的缓存逻辑封装在一个地方，使我们的代码更具凝聚力。

### 2.1. Maven 依赖

首先，让我们添加我们的 Maven 依赖项：

```xml
<dependency>
  <groupId>com.google.guava</groupId>
  <artifactId>guava</artifactId>
  <version>31.0.1-jre</version>
</dependency>
```

[你可以在Maven 存储库](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")中找到最新版本。

### 2.2. 计算和缓存值

现在，让我们看看如何使用CacheLoader实例化LoadingCache：

```java
LoadingCache<String, String> loadingCache = CacheBuilder.newBuilder()
  .build(new CacheLoader<String, String>() {
    @Override
    public String load(final String s) throws Exception {
      return slowMethod(s);
    }
});
```

本质上，LoadingCache会在需要计算尚未缓存的值时调用我们的内联CacheLoader 。让我们尝试计算当我们多次从缓存中检索某些内容时调用slowMethod()的次数：

```java
String value = loadingCache.get("key");
value = loadingCache.get("key");

assertThat(callCount).isEqualTo(1);
assertThat(value).isEqualTo("expectedValue");

```

正如我们所见，它只被调用了一次。第一次该值未被缓存，因为它尚未被计算。第二次，它是从上一次调用中缓存的，所以我们可以避免再次调用我们的slowMethod()的开销。

### 2.3. 刷新缓存

缓存的另一个常见问题是刷新缓存。尽管最困难的方面是知道何时刷新缓存，但另一个方面是知道如何刷新。

使用CacheLoader解决这个问题很简单。LoadingCache将简单地为每个需要刷新的值调用它。让我们通过测试来尝试一下：

```java
String value = loadingCache.get("key");
loadingCache.refresh("key");

assertThat(callCount).isEqualTo(2);
assertThat(value).isEqualTo("key");
```

与我们随后对get() 的调用不同，refresh()将强制再次调用CacheLoader，确保我们的值是最新的。

## 3.总结

在本文中，我们解释了CacheLoader如何使用LoadingCache来计算缓存未命中以及缓存刷新的值。[这篇关于番石榴缓存](https://www.baeldung.com/guava-cache)的更深入的文章也值得一看。