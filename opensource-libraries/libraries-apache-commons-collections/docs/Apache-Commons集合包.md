## 1. 简介

在这篇简短的文章中，我们将重点介绍如何使用 Apache 的Bag集合。

## 延伸阅读：

## [Apache Commons BeanUtils](https://www.baeldung.com/apache-commons-beanutils)

了解如何使用 Apache Commons BeanUtils 进行常见的 bean 操作。

[阅读更多](https://www.baeldung.com/apache-commons-beanutils)→

## [阿帕奇公共 IO](https://www.baeldung.com/apache-commons-io)

用于Java的 Apache Commons IO 开源库的快速实用指南，涵盖其许多广为人知的功能。

[阅读更多](https://www.baeldung.com/apache-commons-io)→

## [Apache Commons 文本介绍](https://www.baeldung.com/java-apache-commons-text)

了解如何使用 Apache Commons Text 进行常见的字符串操作。

[阅读更多](https://www.baeldung.com/java-apache-commons-text)→

## 2.Maven依赖

在开始之前，我们需要从[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.commons" AND a%3A"commons-collections4")导入最新的依赖项：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.1</version>
</dependency>
```

## 3. 手袋与收藏品

简单地说，Bag是一个允许存储多个项目及其重复计数的集合：

```java
public void whenAdded_thenCountIsKept() {
    Bag<Integer> bag = new HashBag<>(
      Arrays.asList(1, 2, 3, 3, 3, 1, 4));
        
    assertThat(2, equalTo(bag.getCount(1)));
}

```

### 3.1. 违反托收合同

在阅读Bag的 API 文档时，我们可能会注意到某些方法被标记为违反标准Java的 Collection 契约。

例如，当我们使用Java集合中的add() API 时，即使该项目已经在集合中，我们也会收到true ：

```java
Collection<Integer> collection = new ArrayList<>();
collection.add(1);
assertThat(collection.add(1), is(true));
```

当我们添加集合中已有的元素时，来自Bag实现的相同 API将返回false ：

```java
Bag<Integer> bag = new HashBag<>();
bag.add(1);
 
assertThat(bag.add(1), is(not(true)));
```

为了解决这些问题，Apache Collections 的库提供了一个名为CollectionBag 的装饰器。我们可以使用它来使我们的包集合符合JavaCollection契约：

```java
public void whenBagAddAPILikeCollectionAPI_thenTrue() {
    Bag<Integer> bag = CollectionBag.collectionBag(new HashBag<>());
    bag.add(1);

    assertThat(bag.add(1), is((true)));
}
```

## 4. 包实现

现在让我们探索Bag接口的各种实现——在 Apache 的集合库中。

### 4.1. 哈希包

我们可以添加一个元素并指示 API 该元素在我们的 bag 集合中应具有的副本数：

```java
public void givenAdd_whenCountOfElementsDefined_thenCountAreAdded() {
    Bag<Integer> bag = new HashBag<>();
	
    bag.add(1, 5); // adding 1 five times
 
    assertThat(5, equalTo(bag.getCount(1)));
}
```

我们还可以从包中删除特定数量的副本或元素的每个实例：

```java
public void givenMultipleCopies_whenRemove_allAreRemoved() {
    Bag<Integer> bag = new HashBag<>(
      Arrays.asList(1, 2, 3, 3, 3, 1, 4));

    bag.remove(3, 1); // remove one element, two still remain
    assertThat(2, equalTo(bag.getCount(3)));
	
    bag.remove(1); // remove all
    assertThat(0, equalTo(bag.getCount(1)));
}
```

### 4.2. 树袋

TreeBag实现与任何其他树一样工作，另外还维护Bag语义。

我们可以自然地使用TreeBag对整数数组进行排序，然后查询每个元素在集合中的实例数：

```java
public void givenTree_whenDuplicateElementsAdded_thenSort() {
    TreeBag<Integer> bag = new TreeBag<>(Arrays.asList(7, 5,
      1, 7, 2, 3, 3, 3, 1, 4, 7));
    
    assertThat(bag.first(), equalTo(1));
    assertThat(bag.getCount(bag.first()), equalTo(2));
    assertThat(bag.last(), equalTo(7));
    assertThat(bag.getCount(bag.last()), equalTo(3));
}
```

TreeBag实现了一个SortedBag接口，该接口的所有实现都可以使用装饰器CollectionSortedBag来遵守JavaCollections 契约：

```java
public void whenTreeAddAPILikeCollectionAPI_thenTrue() {
    SortedBag<Integer> bag 
      = CollectionSortedBag.collectionSortedBag(new TreeBag<>());

    bag.add(1);
 
    assertThat(bag.add(1), is((true)));
}
```

### 4.3. 同步分拣袋

Bag的另一个广泛使用的实现是SynchronizedSortedBag。准确地说，这是SortedBag实现的同步装饰器。

我们可以将此装饰器与上一节中的TreeBag(SortedBag 的实现)一起使用，以同步对包的访问：

```java
public void givenSortedBag_whenDuplicateElementsAdded_thenSort() {
    SynchronizedSortedBag<Integer> bag = SynchronizedSortedBag
      .synchronizedSortedBag(new TreeBag<>(
        Arrays.asList(7, 5, 1, 7, 2, 3, 3, 3, 1, 4, 7)));
    
    assertThat(bag.first(), equalTo(1));
    assertThat(bag.getCount(bag.first()), equalTo(2));
    assertThat(bag.last(), equalTo(7));
    assertThat(bag.getCount(bag.last()), equalTo(3));
}
```

我们可以使用 API 的组合——Collections.synchronizedSortedMap ()和TreeMap——来模拟我们在这里使用SynchronizedSortedBag所做的事情。

## 5.总结

在这个简短的教程中，我们了解了Bag接口及其各种实现。