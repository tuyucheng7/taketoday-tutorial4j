## 1. 概述

在本教程中，我们将学习如何在 Java中使用IdentityHashMap类。我们还将研究它与一般的HashMap类有何不同。虽然此类实现了Map接口，但它违反了Map接口的约定。

有关更详细的文档，我们可以参考[IdenityHashMap](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/IdentityHashMap.html) java 文档页面。有关通用HashMap类的更多详细信息，我们可以阅读[A Guide toJavaHashMap](https://www.baeldung.com/java-hashmap)。

## 2.关于IdentityHashMap类

该类实现了Map接口。Map接口要求在键比较时使用equals ()方法。但是，IdentityHashMap类违反了该约定。相反，它 在关键搜索操作上使用引用相等性 (==)。

在搜索操作期间，HashMap使用hashCode()方法进行散列，而IdentityHashMap使用System.identityHashCode()方法。它还使用哈希表的线性探测技术进行搜索操作。

引用相等性、System.identityHashCode()和线性探测技术的使用使IdentityHashMap类具有更好的性能。

## 3. 使用IdentityHashMap类

对象构造和方法签名与HashMap 相同，但由于引用相等而导致行为不同。

### 3.1. 创建IdentityHashMap对象

我们可以使用默认构造函数创建它：

```java
IdentityHashMap<String, String> identityHashMap = new IdentityHashMap<>();
```

或者它可以使用初始预期容量创建：

```java
IdentityHashMap<Book, String> identityHashMap = new IdentityHashMap<>(10);
```

如果我们没有像上面那样指定初始 expectedCapcity参数，它将使用 21 作为默认容量。

我们也可以使用另一个地图对象来创建它：

```java
IdentityHashMap<String, String> identityHashMap = new IdentityHashMap<>(otherMap);
```

在这种情况下，它使用otherMap的条目初始化创建的identityHashMap。

### 3.2. 添加、检索、更新和删除条目

put()方法用于添加条目：

```java
identityHashMap.put("title", "Harry Potter and the Goblet of Fire");
identityHashMap.put("author", "J. K. Rowling");
identityHashMap.put("language", "English");
identityHashMap.put("genre", "Fantasy");
```

我们还可以使用putAll()方法添加来自其他地图的所有条目：

```java
identityHashMap.putAll(otherMap);
```

要检索值，我们使用get()方法：

```java
String value = identityHashMap.get(key);
```

要更新键的值，我们使用put()方法：

```java
String oldTitle = identityHashMap.put("title", "Harry Potter and the Deathly Hallows");
assertEquals("Harry Potter and the Goblet of Fire", oldTitle);
```

在上面的代码片段中，put()方法在更新后返回旧值。第二条语句确保oldTitle匹配较早的“标题”值。

我们可以使用remove()方法来删除一个元素：

```java
identityHashMap.remove("title");
```

### 3.3. 遍历所有条目

我们可以使用entitySet()方法遍历所有条目：

```java
Set<Map.Entry<String, String>> entries = identityHashMap.entrySet();
for (Map.Entry<String, String> entry: entries) {
    System.out.println(entry.getKey() + ": " + entry.getValue());
}
```

我们还可以使用keySet()方法遍历所有条目：

```java
for (String key: identityHashMap.keySet()) {
    System.out.println(key + ": " + identityHashMap.get(key));
}
```

这些迭代器使用[快速失败](https://www.baeldung.com/java-fail-safe-vs-fail-fast-iterator)机制。如果地图在迭代时被修改，它会抛出一个ConcurrentModificationException。

### 3.4. 其他方法

我们也有不同的可用方法，它们的工作方式与其他Map对象类似：

-   clear()：删除所有条目
-   containsKey()：查找键是否存在于地图中。只有引用是等同的
-   containsValue()：查找地图中是否存在该值。只有引用是等同的
-   keySet()：返回一个基于身份的密钥集
-   size()：返回条目数
-   values()：返回值的集合

### 3.5. 支持空键和空值

IdentityHashMap允许键和值都为null ：

```java
IdentityHashMap<String, String> identityHashMap = new IdentityHashMap<>();
identityHashMap.put(null, "Null Key Accepted");
identityHashMap.put("Null Value Accepted", null);
assertEquals("Null Key Accepted", identityHashMap.get(null));
assertEquals(null, identityHashMap.get("Null Value Accepted"));
```

上面的代码片段确保null作为键和值。

### 3.6. 与IdentityHashMap并发

IdentityHashMap不是线程安全的，与HashMap相同。因此，如果我们有多个线程并行访问/修改IdentityHashMap条目，我们应该将它们转换为同步映射。

我们可以使用Collections类获取同步地图：

```java
Map<String, String> synchronizedMap = Collections.synchronizedMap(new IdentityHashMap<String, String>());
```

## 4. 引用等式的使用示例

IdentityHashMap在equals()方法上使用引用相等性 (==)来搜索/存储/访问键对象。

使用四个属性创建的IdentityHashMap ：

```java
IdentityHashMap<String, String> identityHashMap = new IdentityHashMap<>();
identityHashMap.put("title", "Harry Potter and the Goblet of Fire");
identityHashMap.put("author", "J. K. Rowling");
identityHashMap.put("language", "English");
identityHashMap.put("genre", "Fantasy");

```

使用相同属性创建的另一个HashMap ：

```java
HashMap<String, String> hashMap = new HashMap<>(identityHashMap);
hashMap.put(new String("genre"), "Drama");
assertEquals(4, hashMap.size());

```

当使用新的字符串对象“ genre”作为键时，HashMap将其等同于现有键并更新值。因此，哈希映射的大小保持为 4。

以下代码片段显示了IdentityHashMap的行为有何不同：

```java
identityHashMap.put(new String("genre"), "Drama");
assertEquals(5, identityHashMap.size());

```

IdentityHashMap将新的“流派”字符串对象视为新键。因此，它断言大小为 5。两个不同的“流派”对象用作两个键，“戏剧”和“奇幻”作为值。

## 5. 可变键

IdentityHashMap允许可变键。这是该类的另一个有用的特性。

这里我们将一个简单的Book类作为可变对象：

```java
class Book {
    String title;
    int year;
    
    // other methods including equals, hashCode and toString
}
```

首先，创建Book类的两个可变对象：

```java
Book book1 = new Book("A Passage to India", 1924);
Book book2 = new Book("Invisible Man", 1953);

```

以下代码显示了HashMap的可变键用法：

```java
HashMap<Book, String> hashMap = new HashMap<>(10);
hashMap.put(book1, "A great work of fiction");
hashMap.put(book2, "won the US National Book Award");
book2.year = 1952;
assertEquals(null, hashMap.get(book2));
```

尽管book2条目存在于HashMap中，但它无法检索其值。因为它已经被修改并且equals()方法现在不等同于修改后的对象。这就是为什么一般的Map对象要求不可变对象作为键。

下面的代码片段使用与IdentityHashMap相同的可变键：

```applescript
IdentityHashMap<Book, String> identityHashMap = new IdentityHashMap<>(10);
identityHashMap.put(book1, "A great work of fiction");
identityHashMap.put(book2, "won the US National Book Award");
book2.year = 1951;
assertEquals("won the US National Book Award", identityHashMap.get(book2));
```

有趣的是，IdentityHashMap即使在键对象被修改时也能够检索值。在上面的代码中，assertEquals确保再次检索相同的文本。这是可能的，因为引用相等。

## 6.一些用例

由于其特性，IdentiyHashMap有别于其他Map对象。但是，它不用于一般用途，因此我们在使用此类时需要谨慎。

它有助于构建特定的框架，包括：

-   维护一组可变对象的代理对象
-   基于对象引用构建快速缓存
-   使用引用保存对象的内存图

## 七、总结

在本文中，我们了解了如何使用IdentityHashMap、它与一般HashMap的区别以及一些用例。