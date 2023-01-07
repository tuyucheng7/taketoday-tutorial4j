## 1. 概述

在本快速教程中，我们将了解在 Java中获取[Iterable大小的各种方法。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Iterable.html)

## 2.可迭代和迭代器

Iterable是Java中集合类的主要接口之一。

[Collection](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collection.html)接口扩展了Iterable ，因此Collection的所有子类也实现了Iterable。

Iterable只有一种生成[Iterator](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Iterator.html)的方法：

```java
public interface Iterable<T> {
    public Iterator<T> iterator();    
}
```

然后可以使用此Iterator来迭代Iterable中的元素。

## 3. 使用 CoreJava的可迭代大小

### 3.1. for-each循环

所有实现Iterable的类都有资格使用Java中的[for-each](https://docs.oracle.com/javase/8/docs/technotes/guides/language/foreach.html)循环。

这允许我们循环遍历Iterable中的元素，同时递增计数器以获得其大小：

```java
int counter = 0;
for (Object i : data) {
    counter++;
}
return counter;
```

### 3.2. 集合.size()

在大多数情况下，Iterable将是Collection 的实例， 例如List或Set。

在这种情况下，我们可以检查Iterable的类型并调用它的size()方法来获取元素的数量。

```java
if (data instanceof Collection) {
    return ((Collection<?>) data).size();
}
```

调用size()通常比遍历整个集合要快得多。

这是一个显示上述两种解决方案组合的示例： 

```java
public static int size(Iterable data) {

    if (data instanceof Collection) {
        return ((Collection<?>) data).size();
    }
    int counter = 0;
    for (Object i : data) {
        counter++;
    }
    return counter;
}
```

### 3.3. 流.count()

如果我们使用Java8，我们可以 从Iterable创建一个[Stream 。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Stream.html)

然后可以使用流对象来获取Iterable中元素的计数。

```java
return StreamSupport.stream(data.spliterator(), false).count();
```

## 4. 使用第三方库的可迭代大小

### 4.1. IterableUtils#size()

Apache Commons Collections 库有一个很好的IterableUtils[类](https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/IterableUtils.html)，它为Iterable实例提供静态实用方法。

在开始之前，我们需要从[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.commons" AND a%3A"commons-collections4")导入最新的依赖项：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.1</version>
</dependency>
```

我们可以在Iterable对象上调用IterableUtils的size()方法 来获取它的大小。

```java
return IterableUtils.size(data);
```

### 4.2. 迭代#size()

同样， Google Guava 库也在其[Iterables](https://google.github.io/guava/releases/snapshot/api/docs/com/google/common/collect/Iterables.html#size(java.lang.Iterable))类中提供了一组静态实用方法来对Iterable实例进行操作。

在开始之前，我们需要从[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")导入最新的依赖项：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

调用Iterables类的静态 size()方法可以 得到元素的数量。

```java
return Iterables.size(data);
```

在幕后，IterableUtils和Iterables都使用 3.1 和 3.2 中描述的方法的组合来确定大小。

## 5.总结

在本文中，我们研究了在 Java中获取Iterable大小的不同方法。