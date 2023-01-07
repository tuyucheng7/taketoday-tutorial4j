## 1. 概述

在本教程中，我们将说明如何将多个集合串联成一个逻辑集合。

我们将探索五种不同的方法——两种使用Java8，一种使用 Guava，一种使用 Apache Commons Collections，一种仅使用标准Java7 SDK。

在接下来的示例中，让我们考虑以下集合：

```java
Collection<String> collectionA = Arrays.asList("S", "T");
Collection<String> collectionB = Arrays.asList("U", "V");
```

## 2. 使用Java8 Stream API

Java API 中的Stream接口提供了有用的方法，可以更轻松地处理集合。让我们看一下它的两个方法——concat()和flatMap() ——用于组合集合。

一旦获得Stream，就可以对其执行聚合操作。

### 2.1. 使用concat()方法 

静态方法concat()通过创建一个延迟连接的Stream来在逻辑上组合两个Streams ，其元素是第一个Stream的所有元素，然后是第二个Stream的所有元素。

在下面的示例中，让我们使用concat()方法组合collectionA和collectionB ：

```java
Stream<String> combinedStream = Stream.concat(
  collectionA.stream(),
  collectionB.stream());
```

如果你需要合并两个以上的Streams，你可以从原始调用中再次调用concat()方法：

```java
Stream<String> combinedStream = Stream.concat(
  Stream.concat(collectionA.stream(), collectionB.stream()), 
  collectionC.stream());
```

重要的是要注意Java8 Streams不可重用，因此在将它们分配给变量时应考虑到这一点。

### 2.2. 使用flatMap()方法 

flatMap()方法在用映射 Stream的内容替换此Stream的每个元素后返回一个Stream ，映射Stream是通过将提供的映射函数应用于每个元素而生成的。

下面的示例演示了使用flatMap()方法合并集合。最初，你得到一个Stream，其元素是两个集合，然后在将Stream收集到合并列表之前展平该Stream ：

```java
Stream<String> combinedStream = Stream.of(collectionA, collectionB)
  .flatMap(Collection::stream);
Collection<String> collectionCombined = 
  combinedStream.collect(Collectors.toList());
```

## 3.使用番石榴

Google 的 Guava 库提供了几种操作集合的便捷方法，可以与Java6 或更高版本一起使用。

### 3.1. 使用Iterables.concat()方法

Iterables.concat()方法是用于合并集合的 Guava 便捷方法之一：

```java
Iterable<String> combinedIterables = Iterables.unmodifiableIterable(
  Iterables.concat(collectionA, collectionA));
```

返回的Iterable可以转换为集合：

```java
Collection<String> collectionCombined = Lists.newArrayList(combinedIterables);
```

### 3.2. Maven 依赖

将以下依赖项添加到你的 Maven pom.xml文件以在你的项目中包含 Guava 库：

```java
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

[你可以在Maven 中央](https://search.maven.org/classic/#search|ga|1|g%3A"com.google.guava" AND a%3A"guava")存储库中找到最新版本的 Guava 库。

## 4. 使用 Apache Commons 集合

Apache Commons Collections 是另一个有助于处理各种集合的实用程序库。该库提供了两种可用于组合集合的实用方法。在本节中，让我们了解这些方法的工作原理。

### 4.1. 使用IterableUtils.chainedIterable()方法

IterableUtils类为Iterable实例提供实用方法和装饰器。它提供了chainedIterable()方法，可用于将多个Iterable组合成一个。

```java
Iterable<String> combinedIterables = IterableUtils.chainedIterable(
  collectionA, collectionB);
```

### 4.2. 使用CollectionUtils.union()方法

Collection实例的实用方法和装饰器由CollectionUtils类提供。此类的union()方法返回一个Collection，其中包含给定Iterable实例的联合。

```java
Iterable<String> combinedIterables = CollectionUtils.union(
  collectionA, collectionB);
```

在union()方法的情况下，返回集合中每个元素的基数将等于两个给定Iterables中该元素的基数的最大值。这意味着组合集合仅包含第一个集合中的元素和第二个集合中第一个集合中不存在的元素。

### 4.3. Maven 依赖

将以下依赖项添加到你的 Maven pom.xml文件以在你的项目中包含 Apache Commons Collections 库：

```java
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.1</version>
</dependency>
```

[你可以在Maven 中央](https://search.maven.org/classic/#search|ga|1|a%3A"commons-collections4")存储库中找到最新版本的 Apache Commons 库。

## 5. 使用Java7

如果你仍在使用Java7 并希望避免第三方库(例如 Guava)，你可以使用addAll()方法来组合来自多个集合的元素，或者你可以编写自己的实用方法来组合Iterables。

### 5.1. 使用addAll()方法

当然，组合集合的最简单解决方案是使用addAll()方法，如以下List示例所示，但值得注意的是，此方法创建了一个新集合，其中包含对前两个集合中相同对象的附加引用：

```java
List<String> listC = new ArrayList<>();
listC.addAll(listA);
listC.addAll(listB);
```

### 5.2. 编写自定义concat()方法

下面的示例定义了一个concat()方法，它接受两个Iterables并返回一个合并的Iterable对象：

```java
public static <E> Iterable<E> concat(
  Iterable<? extends E> i1,
  Iterable<? extends E> i2) {
        return new Iterable<E>() {
            public Iterator<E> iterator() {
                return new Iterator<E>() {
                    Iterator<? extends E> listIterator = i1.iterator();
                    Boolean checkedHasNext;
                    E nextValue;
                    private boolean startTheSecond;

                    void theNext() {
                        if (listIterator.hasNext()) {
                            checkedHasNext = true;
                            nextValue = listIterator.next();
                        } else if (startTheSecond)
                            checkedHasNext = false;
                        else {
                            startTheSecond = true;
                            listIterator = i2.iterator();
                            theNext();
                        }
                    }

                    public boolean hasNext() {
                        if (checkedHasNext == null)
                            theNext();
                        return checkedHasNext;
                    }

                    public E next() {
                        if (!hasNext())
                            throw new NoSuchElementException();
                        checkedHasNext = null;
                        return nextValue;
                    }

                    public void remove() {
                        listIterator.remove();
                    }
                };
            }
        };
    }
```

可以通过将两个集合作为参数传递来调用concat ()方法：

```java
Iterable<String> combinedIterables = concat(collectionA, collectionB);
Collection<String> collectionCombined = makeListFromIterable(combinedIterables);
```

如果你需要将Iterable作为List使用，你还可以使用makeListFromIterable()方法，该方法使用Iterable的成员创建一个List：

```java
public static <E> List<E> makeListFromIterable(Iterable<E> iter) {
    List<E> list = new ArrayList<E>();
    for (E item : iter) {
        list.add(item);
    }
    return list;
}
```

## 六，总结

本文讨论了在Java中逻辑组合两个集合的几种不同方法，而无需创建对它们包含的对象的额外引用。