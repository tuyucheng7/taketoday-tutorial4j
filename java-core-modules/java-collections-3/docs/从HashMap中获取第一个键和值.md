## 1. 概述

在本教程中，我们将讨论如何在不知道键的情况下从[HashMap](https://www.baeldung.com/java-hashmap)中获取第一个键值对。

首先，我们将使用迭代器，然后使用流来获取第一个条目。最后，我们将讨论HashMap在获取第一个表项时出现的一个问题，以及如何解决这个问题。

## 2. 使用迭代器

假设我们有以下HashMap<Integer, String>：

```java
Map<Integer, String> hashMap = new HashMap<>();
hashMap.put(5, "A");
hashMap.put(1, "B");
hashMap.put(2, "C");
```

在此示例中，我们将使用迭代器获取第一个键值对。因此，让我们在HashMap的条目集上创建一个迭代器，并调用next()方法来检索第一个条目：

```java
Iterator<Map.Entry<Integer, String>> iterator = hashMap.entrySet().iterator();

Map.Entry<Integer, String> actualValue = iterator.next();
Map.Entry<Integer, String> expectedValue = new AbstractMap.SimpleEntry<Integer, String>(1, "B");

assertEquals(expectedValue, actualValue);
```

## 3. 使用Java流

另一种方法是使用JavaStream API。让我们在条目集上创建一个流并调用findFirst()方法来获取它的第一个条目：

```java
Map.Entry<Integer, String> actualValue = hashMap.entrySet()
  .stream()
  .findFirst()
  .get();

Map.Entry<Integer, String> expectedValue = new AbstractMap.SimpleEntry<Integer, String>(1, "B");

assertEquals(expectedValue, actualValue);
```

## 4. 广告订单问题

为了提出这个问题，让我们记住我们是如何创建hashMap的，将5=A对作为第一个条目插入，然后是 1=B，最后是2=C。让我们通过打印HashMap的内容来检查这一点：

```java
System.out.println(hashMap);
{1=B, 2=C, 5=A}
```

正如我们所看到的，排序是不一样的。HashMap 类实现不保证插入顺序。

现在让我们再向 hashMap添加一个元素：

```java
hashMap.put(0, "D");

Iterator<Map.Entry<Integer, String>> iterator = hashMap.entrySet().iterator();

Map.Entry<Integer, String> actualValue = iterator.next();
Map.Entry<Integer, String> expectedValue = new AbstractMap.SimpleEntry<Integer, String>(0, "D");

assertEquals(expectedValue, actualValue);
```

如我们所见，第一个条目再次发生变化(在本例中为0=D)。这也证明HashMap不保证插入顺序。

所以，如果我们想保留顺序，我们应该改用[LinkedHashMap](https://www.baeldung.com/java-linked-hashmap)：

```java
Map<Integer, String> linkedHashMap = new LinkedHashMap<>();
linkedHashMap.put(5, "A");
linkedHashMap.put(1, "B");
linkedHashMap.put(2, "C");
linkedHashMap.put(0, "D");

Iterator<Map.Entry<Integer, String>> iterator = linkedHashMap.entrySet().iterator();
Map.Entry<Integer, String> actualValue = iterator.next();
Map.Entry<Integer, String> expectedValue = new AbstractMap.SimpleEntry<Integer, String>(5, "A");

assertEquals(expectedValue, actualValue);
```

## 5.总结

在这篇简短的文章中，我们讨论了从HashMap获取第一个条目的不同方法。

需要注意的最重要的一点是HashMap实现不保证任何插入顺序。因此，如果我们有兴趣保留插入顺序，我们应该使用LinkedHashMap。