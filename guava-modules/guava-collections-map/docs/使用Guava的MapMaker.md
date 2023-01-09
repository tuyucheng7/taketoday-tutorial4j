## 1. 简介

MapMaker是 Guava 中的一个构建器类，可以轻松创建线程安全的地图。

Java 已经支持WeakHashMap对键使用[弱引用](https://www.baeldung.com/java-weak-reference)。但是，没有开箱即用的解决方案来使用相同的值。幸运的是，MapMaker提供了简单的构建器方法来对键和值使用WeakReference。

在本教程中，让我们看看MapMaker如何使创建多个地图和使用弱引用变得容易。

## 2.Maven依赖

首先，让我们添加[Google Guava](https://www.baeldung.com/whats-new-in-guava-19)依赖项，它在[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")上可用：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

## 3. 缓存示例

让我们考虑一个服务器为用户维护几个缓存的简单场景：一个会话缓存和一个配置文件缓存。

会话缓存是短暂的，其条目在用户不再活动后变为无效。因此缓存可以在用户对象被垃圾回收后删除用户的条目。

但是，配置文件缓存可以具有更长的生存时间 (TTL)。仅当用户更新其个人资料时，个人资料缓存中的条目才会失效。

在这种情况下，只有当配置文件对象被垃圾回收时，缓存才能删除该条目。

### 3.1. 数据结构

让我们创建类来表示这些实体。

我们将从用户开始：

```java
public class User {
    private long id;
    private String name;

    public User(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
```

然后会话：

```java
public class Session {
    private long id;

    public Session(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}

```

最后是简介：

```java
public class Profile {
    private long id;
    private String type;

    public Profile(long id, String type) {
        this.id = id;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return type;
    }

}
```

### 3.2. 创建缓存

让我们使用makeMap方法为会话缓存创建一个ConcurrentMap实例：

```java
ConcurrentMap<User, Session> sessionCache = new MapMaker().makeMap();
```

返回的映射不允许键和值都为空值。

现在，让我们为配置文件缓存创建另一个ConcurrentMap实例：

```java
ConcurrentMap<User, Profile> profileCache = new MapMaker().makeMap();
```

请注意，我们没有指定缓存的初始容量。因此，MapMaker默认创建容量为 16 的地图。

如果我们愿意，我们可以使用initialCapacity 方法修改容量：

```java
ConcurrentMap<User, Profile> profileCache = new MapMaker().initialCapacity(100).makeMap();
```

### 3.3. 更改并发级别

MapMaker将并发级别的默认值设置为 4。但是，sessionCache需要在没有任何线程争用的情况下支持更多的并发更新。

在这里，concurrencyLevel构建器方法来拯救：

```java
ConcurrentMap<User, Session> sessionCache = new MapMaker().concurrencyLevel(10).makeMap();
```

### 3.4. 使用弱引用

我们上面创建的映射对键和值都使用了强引用。因此，即使键和值被垃圾回收，条目也会保留在映射中。我们应该改用弱引用。

键(用户对象)被垃圾回收后，sessionCache条目无效。因此，让我们对键使用弱引用：

```java
ConcurrentMap<User, Session> sessionCache = new MapMaker().weakKeys().makeMap();
```

对于profileCache，我们可以对值使用弱引用：

```java
ConcurrentMap<User, Profile> profileCache = new MapMaker().weakValues().makeMap();
```

当这些引用被垃圾收集时，Guava 保证这些条目 不会包含在地图上的任何后续读取或写入操作中。但是，size()方法有时可能不一致并且可能包含这些条目。

## 4. MapMaker内部结构

如果未启用弱引用，MapMaker默认创建一个[ConcurrentHashMap](https://www.baeldung.com/java-concurrent-map#concurrenthashmap)。相等性检查通过通常的 equals 方法进行。

如果我们启用弱引用，则MapMaker会在内部创建一个由一组哈希表表示的自定义地图。它还具有与ConcurrentHashMap相似的性能特征。

但是，与WeakHashMap的一个重要区别是相等性检查是通过身份(== 和identityHashCode)比较进行的。

## 5.总结

在这篇简短的文章中，我们学习了如何使用MapMaker类创建线程安全的地图。我们还看到了如何自定义地图以使用弱引用。