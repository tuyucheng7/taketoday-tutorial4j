## 1. 概述

在这个简短的教程中，我们将重点关注 [Hashtable](https://www.baeldung.com/java-hash-table)和 [HashMap](https://www.baeldung.com/java-hashmap)之间的核心区别。

## 2. Java中的Hashtable和HashMap

Hashtable和HashMap 非常相似——都是实现了 Map接口的集合。

此外， put()、get()、remove()和 containsKey() 方法提供恒定时间性能 O(1)。在内部，这些方法基于使用存储桶存储数据的散列的一般概念。

两个类都没有维护元素的插入顺序。换句话说，当我们遍历值时，添加的第一项可能不是第一项。

但它们也有一些差异，使得在某些情况下一个比另一个更好。让我们仔细看看这些差异。

## 3. Hashtable和HashMap的区别

### 3.1. 同步化

首先，Hashtable是线程安全的，可以在应用程序的多个线程之间共享。

另一方面，HashMap是不同步的，如果没有额外的同步代码，不能被多线程访问。我们可以使用 Collections.synchronizedMap()来制作HashMap的线程安全版本。我们也可以只创建自定义锁代码或使用同步关键字使代码线程安全。

HashMap不是同步的，因此它比Hashtable更快并且使用更少的内存。通常，在单线程应用程序中，非同步对象比同步对象更快。

### 3.2. 空值

另一个区别是空值 处理。HashMap 允许添加一个 以null为键的条目以及多个以null为值的条目。相比之下， Hashtable根本 不允许null。让我们看一个null和HashMap的例子：

```java
HashMap<String, String> map = new HashMap<String, String>();
map.put(null, "value");
map.put("key1", null);
map.put("key2", null);
```

这将导致：

```java
assertEquals(3, map.size());
```

接下来，让我们看看Hashtable有何不同：

```java
Hashtable<String, String> table = new Hashtable<String, String>();
table.put("key", null);
```

这会导致NullPointerException。添加一个以null作为键的对象也会导致NullPointerException：

```java
table.put(null, "value");
```

### 3.3. 元素迭代

HashMap使用 [Iterator ](https://www.baeldung.com/java-iterator)来迭代值，而 Hashtable具有相同的Enumerator。Iterator是Enumerator的继承者，消除了它的一些缺点。例如， 迭代器有一个remove()方法来从底层集合中移除元素。

Iterator 是一个[快速失败的迭代](https://www.baeldung.com/java-fail-safe-vs-fail-fast-iterator)器。换句话说， 当底层集合在迭代时被修改时，它会抛出一个[ConcurrentModificationException 。 ](https://www.baeldung.com/java-concurrentmodificationexception)让我们看一下快速失败的例子：

```java
HashMap<String, String> map = new HashMap<String, String>();
map.put("key1", "value1");
map.put("key2", "value2");

Iterator<String> iterator = map.keySet().iterator();
while(iterator.hasNext()){ 
    iterator.next();
    map.put("key4", "value4");
}
```

这将引发ConcurrentModificationException异常，因为我们在迭代集合时调用put() 。

## 4. 何时选择HashMap而不是 Hashtable

我们应该将 HashMap 用于非同步或单线程应用程序。

值得一提的是，从 JDK 1.8 开始， Hashtable 就被弃用了。然而， [ConcurrentHashMap](https://www.baeldung.com/java-concurrent-map) 是一个很好的Hashtable 替代品。我们应该考虑 在多线程的应用程序中使用ConcurrentHashMap 。

## 5.总结

在本文中，我们说明了HashMap和Hashtable之间的区别以及在需要选择时要注意的事项。