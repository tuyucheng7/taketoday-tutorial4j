## 1. 概述

在本教程中，我们将说明Guava的collect包中的许多有用功能之一：如何将 Function 应用于 Guava Set并获取Map。

我们将讨论两种方法——创建不可变地图和基于内置 guava 操作的实时地图，然后实现自定义实时地图实现。

## 2.设置

首先，我们将Guava库添加为pom.xml 中的依赖项：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

快速说明 - 你可以在此处查看是否有[更新的版本](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")。

## 3. 映射函数

让我们首先定义我们将应用于集合元素的函数：

```java
    Function<Integer, String> function = new Function<Integer, String>() {
        @Override
        public String apply(Integer from) {
            return Integer.toBinaryString(from.intValue());
        }
    };
```

该函数只是将Integer的值转换为其二进制String表示形式。

## 4. 番石榴toMap()

Guava 提供了一个与Map实例相关的静态实用程序类。其中，它有两个操作可用于通过应用定义的 Guava函数将Set转换为Map。

以下代码片段显示了创建一个不可变的Map：

```java
Map<Integer, String> immutableMap = Maps.toMap(set, function);
```

以下测试断言集合已正确转换：

```java
@Test
public void givenStringSetAndSimpleMap_whenMapsToElementLength_thenCorrect() {
    Set set = new TreeSet(Arrays.asList(32, 64, 128));
    Map<Integer, String> immutableMap = Maps.toMap(set, function);
    assertTrue(immutableMap.get(32).equals("100000")
      && immutableMap.get(64).equals("1000000")
      && immutableMap.get(128).equals("10000000"));
}

```

创建的地图的问题在于，如果将元素添加到源集中，则派生地图不会更新。

## 4.番石榴asMap()

如果我们使用前面的示例并使用Maps.asMap方法创建地图：

```java
Map<Integer, String> liveMap = Maps.asMap(set, function);
```

我们将获得实时地图视图——这意味着对原始 Set 的更改也将反映在地图中：

```java
@Test
public void givenStringSet_whenMapsToElementLength_thenCorrect() {
    Set<Integer> set = new TreeSet<Integer>(Arrays.asList(32, 64, 128));
    Map<Integer, String> liveMap = Maps.asMap(set, function);
    assertTrue(liveMap.get(32).equals("100000")
            && liveMap.get(64).equals("1000000")
            && liveMap.get(128).equals("10000000"));
    
    set.add(256);
    assertTrue(liveMap.get(256).equals("100000000") && liveMap.size() == 4);
}

```

请注意，尽管我们通过集合添加了一个元素并在地图中查找它，但测试断言正确。

## 5. 构建自定义实时地图

当我们谈论Set的Map View 时，我们基本上是在使用Guava Function扩展Set的功能。

在实时Map视图中，对Set的更改应该实时更新Map EntrySet 。我们将创建我们自己的通用Map，子类化AbstractMap<K,V >，如下所示：

```java
public class GuavaMapFromSet<K, V> extends AbstractMap<K, V> {
    public GuavaMapFromSet(Set<K> keys, 
        Function<? super K, ? extends V> function) { 
    }
}
```

值得注意的是，AbstractMap的所有子类的主要契约是实现entrySet方法，正如我们所做的那样。然后，我们将在以下小节中查看代码的 2 个关键部分。

### 5.1. 条目

Map中的另一个属性是entries，代表我们的EntrySet：

```java
private Set<Entry<K, V>> entries;
```

条目字段将始终使用构造函数中的输入Set进行初始化：

```java
public GuavaMapFromSet(Set<K> keys,Function<? super K, ? extends V> function) {
    this.entries=keys;
}
```

这里有一个简短的说明——为了保持实时视图，我们将在输入Set中为后续Map的EntrySet使用相同的迭代器。

在履行AbstractMap<K,V >的契约时，我们实现了entrySet方法，然后我们在该方法中返回条目：

```java
@Override
public Set<java.util.Map.Entry<K, V>> entrySet() {
    return this.entries;
}
```

### 5.2. 缓存

此Map存储通过将Function应用于Set 获得的值：

```java
private WeakHashMap<K, V> cache;
```

## 6.集合迭代器

我们将为后续Map的EntrySet使用输入Set的迭代器。为此，我们使用自定义的EntrySet和自定义的Entry类。

### 6.1. 入门级_

首先，让我们看看Map中的单个条目会是什么样子：

```java
private class SingleEntry implements Entry<K, V> {
    private K key;
    public SingleEntry( K key) {
        this.key = key;
    }
    @Override
    public K getKey() {
        return this.key;
    }
    @Override
    public V getValue() {
        V value = GuavaMapFromSet.this.cache.get(this.key);
  if (value == null) {
      value = GuavaMapFromSet.this.function.apply(this.key);
      GuavaMapFromSet.this.cache.put(this.key, value);
  }
  return value;
    }
    @Override
    public V setValue( V value) {
        throw new UnsupportedOperationException();
    }
}
```

显然，在此代码中，我们不允许从地图视图修改Set ，因为对setValue的调用会抛出UnsupportedOperationException。

密切关注getValue——这是我们实时取景功能的关键。我们检查Map中的缓存以获取当前键(Set元素)。

如果我们找到键，我们返回它，否则我们将我们的函数应用于当前键并获得一个值，然后将它存储在缓存中。

这样，只要Set有一个新元素，地图就会是最新的，因为新值是动态计算的。

### 6.2. 条目集

我们现在将实现EntrySet：

```java
private class MyEntrySet extends AbstractSet<Entry<K, V>> {
    private Set<K> keys;
    public MyEntrySet(Set<K> keys) {
        this.keys = keys;
    }
    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new LiveViewIterator();
    }
    @Override
    public int size() {
        return this.keys.size();
    }
}
```

我们已经通过覆盖迭代器和大小方法实现了扩展AbstractSet的契约。但还有更多。

请记住，此EntrySet的实例将构成我们地图视图中的条目。通常，地图的EntrySet只是为每次迭代返回一个完整的Entry 。

但是，在我们的例子中，我们需要使用输入Set中的迭代器来维护我们的实时视图。我们很清楚它只会返回Set的元素，所以我们还需要一个自定义迭代器。

### 6.3. 迭代器

这是上面EntrySet的迭代器的实现：

```java
public class LiveViewIterator implements Iterator<Entry<K, V>> {
    private Iterator<K> inner;
    
    public LiveViewIterator () {
        this.inner = MyEntrySet.this.keys.iterator();
    }
    
    @Override
    public boolean hasNext() {
        return this.inner.hasNext();
    }
    @Override
    public Map.Entry<K, V> next() {
        K key = this.inner.next();
        return new SingleEntry(key);
    }
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
```

LiveViewIterator必须驻留在MyEntrySet类中，这样，我们就可以在初始化时共享Set的迭代器。

当使用迭代器遍历GuavaMapFromSet的条目时，对next的调用只是从Set的迭代器中检索键并构造一个SingleEntry。

## 7. 把它们放在一起

将我们在本教程中介绍的内容拼接在一起后，让我们替换之前示例中的liveMap变量，并将其替换为我们的自定义地图：

```java
@Test
public void givenIntSet_whenMapsToElementBinaryValue_thenCorrect() {
    Set<Integer> set = new TreeSet<>(Arrays.asList(32, 64, 128));
    Map<Integer, String> customMap = new GuavaMapFromSet<Integer, String>(set, function);
    
    assertTrue(customMap.get(32).equals("100000")
      && customMap.get(64).equals("1000000")
      && customMap.get(128).equals("10000000"));
}
```

更改输入Set的内容，我们将看到Map实时更新：

```java
@Test
public void givenStringSet_whenMapsToElementLength_thenCorrect() {
    Set<Integer> set = new TreeSet<Integer>(Arrays.asList(32, 64, 128));
    Map<Integer, String> customMap = Maps.asMap(set, function);
    
    assertTrue(customMap.get(32).equals("100000")
      && customMap.get(64).equals("1000000")
      && customMap.get(128).equals("10000000"));
    
    set.add(256);
    assertTrue(customMap.get(256).equals("100000000") && customMap.size() == 4);
}
```

## 八. 总结

在本教程中，我们研究了利用Guava操作并通过应用Function从Set获取Map视图的不同方法。