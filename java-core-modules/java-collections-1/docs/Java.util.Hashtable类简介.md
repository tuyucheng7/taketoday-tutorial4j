## 1. 概述

**[Hashtable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Hashtable.html)是Java中最古老的哈希表数据结构实现**。HashMap是JDK 1.2中引入的第二种实现。

这两个类都提供相似的功能，但也存在细微差别，我们将在本教程中探讨这些差别。

## 2. 何时使用Hashtable

假设我们有一本字典，其中每个单词都有其定义。此外，我们需要快速从字典中获取、插入和删除单词。

因此，Hashtable(或HashMap)是有意义的。单词将是Hashtable中的键，因为它们应该是唯一的。另一方面，定义将是值。

## 3. 使用示例

让我们继续字典示例。我们将Word建模为键：

```java
public class Word {
    private String name;

    public Word(String name) {
        this.name = name;
    }
    
    // ...
}
```

假设值是String。现在我们可以创建一个哈希表：

```java
Hashtable<Word, String> table = new Hashtable<>();
```

首先，让我们添加一个条目：

```java
Word word = new Word("cat");
table.put(word, "an animal");
```

另外，要获取条目：

```java
String definition = table.get(word);
```

最后，让我们删除一个条目：

```java
definition = table.remove(word);
```

类中还有很多方法，稍后我们将介绍其中的一些方法。

但首先，让我们谈谈对键对象的一些要求。

## 4. hashCode()的重要性

**要用作Hashtable中的键，该对象不得违反[hashCode()协定](https://www.baeldung.com/java-hashcode)**。简而言之，相等的对象必须返回相同的哈希码。为了理解为什么，让我们看看哈希表是如何组织的。

哈希表使用数组。数组中的每个位置都是一个“桶”，可以为空或包含一个或多个键值对。计算每对的索引。

但是为什么不按顺序存储元素，将新元素添加到数组的末尾呢？

关键是通过索引查找元素比通过比较顺序遍历元素要快得多。因此，我们需要一个将键映射到索引的函数。

### 4.1 直接地址表

这种映射最简单的例子是直接地址表。这里的键用作索引：

```plaintext
index(k)=k,
where k is a key
```

键是唯一的，即每个桶包含一对键值对。当整数键的可能范围相当小时，此技术适用于整数键。

但是我们这里有两个问题：

-   首先，我们的键不是整数，而是Word对象
-   其次，如果它们是整数，没有人会保证它们很小。想象一下键是1、2和1000000。我们将有一个大小为1000000的大数组，只有三个元素，其余的将是浪费的空间

hashCode()方法解决了第一个问题。

Hashtable中的数据操作逻辑解决了第二个问题。

让我们深入讨论一下。

### 4.2 hashCode()方法

任何Java对象都继承返回int值的hashCode()方法。该值是根据对象的内部内存地址计算得出的。默认情况下，hashCode()为不同的对象返回不同的整数。

因此，**可以使用hashCode()将任何键对象转换为整数**。但是这个整数可能很大。

### 4.3 缩小范围

get()、put()和remove()方法包含解决第二个问题的代码-减少可能的整数范围。

该公式计算键的索引：

```java
int index = (hash & 0x7FFFFFFF) % tab.length;
```

其中tab.length是数组大小，hash是键的hashCode()方法返回的数字。

正如我们所看到的，**索引是hash跟数组长度相除的余数**。请注意，相同的哈希码会生成相同的索引。

### 4.4 碰撞

此外，**即使是不同的哈希码也可以产生相同的索引**，我们称之为碰撞。为了解决冲突，Hashtable存储了键值对的LinkedList。

这种数据结构称为带链表的哈希表。

### 4.5 负载因子

很容易猜到**碰撞会减慢对元素的操作**。要获得一个条目，仅仅知道它的索引是不够的，我们还需要遍历列表并对每个条目进行比较。

因此，减少碰撞次数非常重要。数组越大，发生碰撞的几率就越小。**负载因子决定了数组大小和性能之间的平衡**。默认情况下，它是0.75，这意味着当75%的存储桶变为非空时，数组大小会加倍。该操作由rehash()方法执行。

### 4.6 覆盖equals()和hashCode()

当我们将一个条目放入哈希表并从中取出时，我们希望该值不仅可以使用相同的键实例获得，还可以使用相等的键获得：

```java
Word word = new Word("cat");
table.put(word, "an animal");
String extracted = table.get(new Word("cat"));
```

**要设置相等规则，我们重写键的equals()方法**：

```java
public boolean equals(Object o) {
    if (o == this)
        return true;
    if (!(o instanceof Word))
        return false;

    Word word = (Word) o;
    return word.getName().equals(this.name);
}
```

但是，如果我们在重写equals()时不重写hashCode()，那么两个相等的键可能最终会出现在不同的桶中，因为Hashtable使用其哈希码计算键的索引。

让我们仔细看看上面的例子。如果我们不覆盖hashCode()会发生什么？

-   这里涉及到Word的两个实例-第一个用于放置条目，第二个用于获取条目。虽然这些实例是相等的，但它们的hashCode()方法返回不同的数字
-   每个键的索引由4.3节中的公式计算得出。根据这个公式，不同的哈希码可能会产生不同的索引
-   这意味着我们将条目放入一个桶中，然后尝试将其从另一个桶中取出。这样的逻辑破坏了哈希表

**相等的键必须返回相等的哈希码，这就是我们重写hashCode()方法的原因**：

```java
public int hashCode() {
    return name.hashCode();
}
```

请注意，**还建议使不相等的键返回不同的哈希码**，否则它们最终会在同一个桶中。这将影响性能，因此失去Hashtable的一些优势。

另外请注意，我们不关心String、Integer、Long或其他包装类型的键。equal()和hashCode()方法都已在包装类中被覆盖。

## 5. 迭代Hashtable

有几种方法可以迭代Hashtable。在本节中，我们将讨论它们并解释其中的一些含义。

### 5.1 快速失败：迭代

快速失败迭代意味着如果在创建Iterator之后修改Hashtable，则将抛出ConcurrentModificationException。让我们来演示一下。

首先，我们将创建一个哈希表并向其中添加条目：

```java
Hashtable<Word, String> table = new Hashtable<Word, String>();
table.put(new Word("cat"), "an animal");
table.put(new Word("dog"), "another animal");
```

其次，我们将创建一个迭代器：

```java
Iterator<Word> it = table.keySet().iterator();
```

第三，我们将修改哈希表：

```java
table.remove(new Word("dog"));
```

现在，如果我们尝试遍历哈希表，我们将得到一个ConcurrentModificationException：

```java
while (it.hasNext()) {
    Word key = it.next();
}
```

```shell
java.util.ConcurrentModificationException
	at java.util.Hashtable$Enumerator.next(Hashtable.java:1378)
```

ConcurrentModificationException有助于发现错误，从而避免不可预知的行为，例如，当一个线程遍历哈希表时，另一个线程同时尝试修改它。

### 5.2 不快速失败：枚举

哈希表中的枚举不是快速失败的。让我们看一个例子。

首先，让我们创建一个哈希表并向其中添加条目：

```java
Hashtable<Word, String> table = new Hashtable<Word, String>();
table.put(new Word("1"), "one");
table.put(new Word("2"), "two");
```

其次，让我们创建一个Enumeration：

```java
Enumeration<Word> enumKey = table.keys();
```

第三，让我们修改哈希表：

```java
table.remove(new Word("1"));
```

现在，如果我们遍历哈希表，它不会抛出异常：

```java
while (enumKey.hasMoreElements()) {
    Word key = enumKey.nextElement();
}
```

### 5.3 不可预测的迭代顺序

另外请注意，哈希表中的迭代顺序是不可预测的，并且与添加条目的顺序不匹配。

这是可以理解的，因为它使用键的哈希码计算每个索引。此外，还会不时进行rehash，重新排列数据结构的顺序。

因此，让我们添加一些条目并检查输出：

```java
Hashtable<Word, String> table = new Hashtable<Word, String>();
    table.put(new Word("1"), "one");
    table.put(new Word("2"), "two");
    // ...
    table.put(new Word("8"), "eight");

    Iterator<Map.Entry<Word, String>> it = table.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry<Word, String> entry = it.next();
        // ...
    }
}
```

```shell
five
four
three
two
one
eight
seven
```

## 6. Hashtable与HashMap

Hashtable和HashMap提供非常相似的功能。

它们都提供：

-   快速失败迭代
-   不可预测的迭代顺序

但也有一些区别：

-   HashMap不提供任何枚举，而Hashtable不提供快速失败枚举
-   Hashtable不允许空键和空值，而HashMap允许一个空键和任意数量的空值
-   Hashtable的方法是同步的，而HashMap的方法不是

## 7. Java 8中的Hashtable API

Java 8引入了有助于使我们的代码更简洁的新方法。特别是，我们可以摆脱一些if块。让我们来演示一下。

### 7.1 getOrDefault()

假设我们需要获取单词“dog”的定义并将其分配给变量(如果它在哈希表中)。否则，将“not found”分配给变量。

在Java 8之前：

```java
Word key = new Word("dog");
String definition;

if (table.containsKey(key)) {
     definition = table.get(key);
} else {
     definition = "not found";
}
```

Java 8之后：

```java
definition = table.getOrDefault(key, "not found");
```

### 7.2 putIfAbsent()

假设我们只需要在字典中没有“cat”这个单词的情况下放入它。

在Java 8之前：

```java
if (!table.containsKey(new Word("cat"))) {
    table.put(new Word("cat"), definition);
}
```

Java 8之后：

```java
table.putIfAbsent(new Word("cat"), definition);
```

### 7.3 remove()

假设我们需要删除“cat”这个词，但前提是它的定义是“an animal”。

在Java 8之前：

```java
if (table.get(new Word("cat")).equals("an animal")) {
    table.remove(new Word("cat"));
}
```

Java 8之后：

```java
boolean result = table.remove(new Word("cat"), "an animal");
```

最后，旧的remove()方法返回值，而新方法返回boolean。

### 7.4 replace()

假设我们需要替换“cat”的定义，但前提是它的旧定义是“a small domesticated carnivorous mammal”。

在Java 8之前：

```java
if (table.containsKey(new Word("cat")) 
    && table.get(new Word("cat")).equals("a small domesticated carnivorous mammal")) {
    table.put(new Word("cat"), definition);
}
```

Java 8之后：

```java
table.replace(new Word("cat"), "a small domesticated carnivorous mammal", definition);
```

### 7.5 computeIfAbsent()

此方法类似于putIfAbsent()。但是putIfAbsent()直接接收值，而computeIfAbsent()接收一个映射函数。它仅在检查键后才计算值，这样效率更高，尤其是在值难以获取的情况下。

```java
table.computeIfAbsent(new Word("cat"), key -> "an animal");
```

因此，上面一行等同于：

```java
if (!table.containsKey(cat)) {
    String definition = "an animal"; // note that calculations take place inside if block
    table.put(new Word("cat"), definition);
}
```

### 7.6 computeIfPresent()

此方法类似于replace()方法。但是，replace()直接接收值，而computeIfPresent()采用映射函数。它计算if块内部的值，这就是它更高效的原因。

假设我们需要更改定义：

```java
table.computeIfPresent(cat, (key, value) -> key.getName() + " - " + value);
```

因此，上面的行等效于：

```java
if (table.containsKey(cat)) {
    String concatination=cat.getName() + " - " + table.get(cat);
    table.put(cat, concatination);
}
```

### 7.7 compute()

现在我们将解决另一个任务。假设我们有一个String数组，其中的元素不是唯一的。另外，让我们计算一下我们可以在数组中获得多少个字符串。这是数组：

```java
String[] animals = { "cat", "dog", "dog", "cat", "bird", "mouse", "mouse" };
```

此外，我们要创建一个哈希表，其中包含一个动物作为键，它的出现次数作为值。

这是一个解决方案：

```java
Hashtable<String, Integer> table = new Hashtable<String, Integer>();

for (String animal : animals) {
    table.compute(animal, 
        (key, value) -> (value == null ? 1 : value + 1));
}
```

最后，让我们确保哈希表包含两只猫、两只狗、一只鸟和两只老鼠：

```java
assertThat(table.values(), hasItems(2, 2, 2, 1));
```

### 7.8 merge()

还有另一种方法可以解决上述任务：

```java
for (String animal : animals) {
    table.merge(animal, 1, (oldValue, value) -> (oldValue + value));
}
```

第二个参数1是映射到键的值(如果键尚未在哈希表中)。如果键已经在哈希表中，则我们将其计算为oldValue + 1。

### 7.9 foreach()

这是一种遍历条目的新方法。让我们打印所有条目：

```java
table.forEach((k, v) -> System.out.println(k.getName() + " - " + v)
```

### 7.10 replaceAll()

此外，我们可以在不迭代的情况下替换所有值：

```java
table.replaceAll((k, v) -> k.getName() + " - " + v);
```

## 8. 总结

在本文中，我们描述了哈希表结构的用途，并展示了如何将直接地址表结构复杂化以获得它。

此外，我们还介绍了哈希表中的碰撞以及加载因子是什么。此外，我们还了解了为什么要为键对象覆盖equals()和hashCode()。

最后，我们讨论了Hashtable的属性和Java 8特定的API。