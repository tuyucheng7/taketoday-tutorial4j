## 1. 概述

Vavr库，以前称为Javaslang，是Java的函数库。在本文中，我们将探索其强大的集合API。

要获得有关此库的更多信息，请阅读[这篇文章](https://www.baeldung.com/vavr)。

## 2. 持久化集合

修改后的持久集合会在保留当前版本的同时生成集合的新版本。

维护同一集合的多个版本可能会导致CPU和内存使用效率低下。然而，Vavr集合库通过在集合的不同版本之间共享数据结构来克服这个问题。

这与Java的Collections实用程序类中的unmodifiableCollection()根本不同，后者仅提供一个底层集合的包装器。

尝试修改此类集合会导致UnsupportedOperationException而不是创建新版本。此外，底层集合通过其直接引用仍然是可变的。

## 3. Traversable

Traversable是所有Vavr集合的基本类型——这个接口定义了在所有数据结构之间共享的方法。

它提供了一些有用的默认方法，例如size()、get()、filter()、isEmpty()和其他由子接口继承的方法。

让我们进一步探索集合库。

## 4. Seq

我们将从Seq开始。

Seq接口表示顺序数据结构。它是List、Stream、Queue、Array、Vector和CharSeq的父接口。所有这些数据结构都有自己独特的属性，我们将在下面进行探讨。

### 4.1 List

List是一个急切求值的元素序列，扩展了LinearSeq接口。

持久列表是从头部和尾部递归形成的：

-   Head：第一个元素
-   Tail：包含剩余元素的列表(该列表也是由头部和尾部形成的)

List API中有静态工厂方法可用于创建List。我们可以使用静态of()方法从一个或多个对象创建List的实例。

我们还可以使用static empty()创建一个空列表和ofAll()从Iterable类型创建一个列表：

```java
List<String> list = List.of("Java", "PHP", "Jquery", "JavaScript", "JShell", "JAVA");
```

让我们看一些关于如何操作列表的例子。

我们可以使用drop()及其变体来删除前N个元素：

```java
List list1 = list.drop(2);                                      
assertFalse(list1.contains("Java") && list1.contains("PHP"));   
                                                                
List list2 = list.dropRight(2);                                 
assertFalse(list2.contains("JAVA") && list2.contains("JShell"));
                                                                
List list3 = list.dropUntil(s -> s.contains("Shell"));          
assertEquals(list3.size(), 2);                                  
                                                                
List list4 = list.dropWhile(s -> s.length() > 0);               
assertTrue(list4.isEmpty());
```

drop(int n)从第一个元素开始从列表中删除n个元素，而dropRight()从列表中的最后一个元素开始执行相同的操作。

dropUntil()会继续从列表中删除元素，直到谓词的计算结果为真，而dropWhile()会在谓词为真时继续删除元素。

还有dropRightWhile()和dropRightUntil()开始从右边移除元素。

接下来，take(int n)用于从列表中获取元素。它从列表中获取n个元素，然后停止。还有一个takeRight(int n)开始从列表末尾获取元素：

```java
List list5 = list.take(1);                       
assertEquals(list5.single(), "Java");            
                                                 
List list6 = list.takeRight(1);                  
assertEquals(list6.single(), "JAVA");            
                                                 
List list7 = list.takeUntil(s -> s.length() > 6);
assertEquals(list7.size(), 3);
```

最后，takeUntil()继续从列表中获取元素，直到谓词为真。还有一个takeWhile()变体也接受谓词参数。

此外，API中还有其他有用的方法，例如实际上返回非重复元素列表的distinct()以及接受Comparator以确定相等性的distinctBy()。

非常有趣的是，还有intersperse()在列表的每个元素之间插入一个元素。它对于String操作非常方便：

```java
List list8 = list
	.distinctBy((s1, s2) -> s1.startsWith(s2.charAt(0) + "") ? 0 : 1);
assertEquals(list8.size(), 2);

String words = List.of("Boys", "Girls")
  	.intersperse("and")
  	.reduce((s1, s2) -> s1.concat( " " + s2 ))
  	.trim();  
assertEquals(words, "Boys and Girls");
```

想要将列表分成几类？好吧，也有一个API：

```java
Iterator<List<String>> iterator = list.grouped(2);
assertEquals(iterator.head().size(), 2);

Map<Boolean, List<String>> map = list.groupBy(e -> e.startsWith("J"));
assertEquals(map.size(), 2);
assertEquals(map.get(false).get().size(), 1);
assertEquals(map.get(true).get().size(), 5);
```

group(int n)将List分成n个元素的组。groupBy()接受一个包含划分列表逻辑的Function并返回一个包含两个条目的Map-true和false。

真正的键映射到满足函数中指定条件的元素列表；false键映射到不存在的元素列表。

正如预期的那样，当改变List时，原始List实际上并没有被修改。相反，总是返回新版本的列表。

我们还可以使用堆栈语义与List交互-元素的后进先出(LIFO)检索。就此而言，有用于操作堆栈的API方法，例如peek()、pop()和push()：

```java
List<Integer> intList = List.empty();

List<Integer> intList1 = intList.pushAll(List.rangeClosed(5,10));

assertEquals(intList1.peek(), Integer.valueOf(10));

List intList2 = intList1.pop();
assertEquals(intList2.size(), (intList1.size() - 1) );
```

pushAll()函数用于将一系列整数插入堆栈，而peek()用于获取堆栈的头部。还有peekOption()可以将结果包装在Option对象中。

List接口中还有其他有趣且非常有用的方法，这些方法在[Java文档](https://static.javadoc.io/io.vavr/vavr/0.9.0/io/vavr/collection/List.html)中有详细的记录。

### 4.2 Queue

不可变队列存储允许先进先出(FIFO)检索的元素。

一个Queue内部由两个链表组成，一个front List和一个rear List。前面的List包含出队的元素，后面的List包含入队的元素。

这允许在O(1)中执行入队和出队操作。当前面的List用完元素时，前后List的交换，后面的List颠倒过来。

让我们创建一个队列：

```java
Queue<Integer> queue = Queue.of(1, 2);
Queue<Integer> secondQueue = queue.enqueueAll(List.of(4,5));

assertEquals(3, queue.size());
assertEquals(5, secondQueue.size());

Tuple2<Integer, Queue<Integer>> result = secondQueue.dequeue();
assertEquals(Integer.valueOf(1), result._1);

Queue<Integer> tailQueue = result._2;
assertFalse(tailQueue.contains(secondQueue.get(0)));
```

dequeue函数从Queue中删除头部元素并返回Tuple2<T, Q\>。该元组包含已作为第一个条目删除的头元素，以及作为第二个条目的队列中的剩余元素。

我们可以使用combination(n)来获取Queue中元素的所有可能的N组合：

```java
Queue<Queue<Integer>> queue1 = queue.combinations(2);
assertEquals(queue1.get(2).toCharSeq(), CharSeq.of("23"));
```

同样，我们可以看到原始Queue在对元素进行入队/出队时没有被修改。

### 4.3 Stream

Stream是惰性链表的实现，与java.util.stream有很大不同。与java.util.stream不同，VavrStream存储数据并延迟评估下一个元素。

假设我们有一个整数流：

```java
Stream<Integer> s = Stream.of(2, 1, 3, 4);
```

将s.toString()的结果打印到控制台只会显示Stream(2, ?)。这意味着只有Stream的头部被评估，而尾部没有被评估。

调用s.get(3)并随后显示s.tail()的结果返回Stream(1, 3, 4, ?)。相反，如果不先调用s.get(3)，这会导致Stream计算最后一个元素s.tail()的结果只会是Stream(1, ?)，这意味着只评估了尾部的第一个元素。

此行为可以提高性能，并使使用Stream表示(理论上)无限长的序列成为可能。

VavrStream是不可变的，可能是Empty或Cons。Cons由一个head元素和一个延迟计算的尾部Stream组成。与List不同，对于Stream，只有头元素保留在内存中。尾部元素按需计算。

让我们创建一个包含10个正整数的Stream并计算偶数的总和：

```java
Stream<Integer> intStream = Stream.iterate(0, i -> i + 1)
  	.take(10);

assertEquals(10, intStream.size());

long evenSum = intStream.filter(i -> i % 2 == 0)
  	.sum()
  	.longValue();

assertEquals(20, evenSum);

```

与Java 8 Stream API不同，Vavr的Stream是一种用于存储元素序列的数据结构。

因此，它具有get()、append()、insert()等方法来操作其元素。drop()、distinct()和之前考虑的一些其他方法也可用。

最后，让我们快速演示Stream中的tabulate()。此方法返回一个长度为n的Stream，其中包含作为应用函数结果的元素：

```java
Stream<Integer> s1 = Stream.tabulate(5, (i)-> i + 1);
assertEquals(s1.get(2).intValue(), 3);
```

我们还可以使用zip()生成Tuple2<Integer, Integer\>的Stream，其中包含通过组合两个Stream形成的元素：

```java
Stream<Integer> s = Stream.of(2,1,3,4);

Stream<Tuple2<Integer, Integer>> s2 = s.zip(List.of(7,8,9));
Tuple2<Integer, Integer> t1 = s2.get(0);
 
assertEquals(t1._1().intValue(), 2);
assertEquals(t1._2().intValue(), 7);
```

### 4.4 Array

数组是一个不可变的、有索引的序列，允许高效的随机访问。它由Java对象数组支持。本质上，它是T类型对象数组的Traversable包装器。

我们可以使用静态方法of()来实例化一个数组。我们还可以使用静态range()和rangeBy()方法生成范围元素。rangeBy()有第三个参数让我们定义步骤。

range()和rangeBy()方法只会生成从起始值到结束值减一的元素。如果我们需要包含结束值，我们可以使用rangeClosed()或rangeClosedBy()：

```java
Array<Integer> rArray = Array.range(1, 5);
assertFalse(rArray.contains(5));

Array<Integer> rArray2 = Array.rangeClosed(1, 5);
assertTrue(rArray2.contains(5));

Array<Integer> rArray3 = Array.rangeClosedBy(1,6,2);
assertEquals(rArray3.size(), 3);
```

让我们按索引操作元素：

```java
Array<Integer> intArray = Array.of(1, 2, 3);
Array<Integer> newArray = intArray.removeAt(1);

assertEquals(3, intArray.size());
assertEquals(2, newArray.size());
assertEquals(3, newArray.get(1).intValue());

Array<Integer> array2 = intArray.replace(1, 5);
assertEquals(array2.get(0).intValue(), 5);
```

### 4.5 Vector

Vector是一种介于Array和List之间的类型，它提供了另一种索引元素序列，允许在恒定时间内进行随机访问和修改：

```java
Vector<Integer> intVector = Vector.range(1, 5);
Vector<Integer> newVector = intVector.replace(2, 6);

assertEquals(4, intVector.size());
assertEquals(4, newVector.size());

assertEquals(2, intVector.get(1).intValue());
assertEquals(6, newVector.get(1).intValue());
```

### 4.6 CharSeq

CharSeq是一个集合对象，用于表示一个原始字符序列。它本质上是一个添加了集合操作的String包装器。

要创建一个CharSeq：

```java
CharSeq chars = CharSeq.of("vavr");
CharSeq newChars = chars.replace('v', 'V');

assertEquals(4, chars.size());
assertEquals(4, newChars.size());

assertEquals('v', chars.charAt(0));
assertEquals('V', newChars.charAt(0));
assertEquals("Vavr", newChars.mkString());
```

## 5. Set

在本节中，我们将详细介绍集合库中的各种Set实现。Set数据结构的独特之处在于它不允许重复值。

然而，Set有不同的实现——HashSet是最基本的实现。TreeSet不允许重复元素，可以排序。LinkedHashSet维护其元素的插入顺序。

让我们一一仔细看看这些实现。

### 5.1 HashSet

HashSet具有用于创建新实例的静态工厂方法，我们之前在本文中探讨过其中的一些方法-比如of()、ofAll()和range()方法的变体。

我们可以使用diff()方法获取两个集合之间的差异。此外，union()和intersect()方法返回两个集合的并集和交集：

```java
HashSet<Integer> set0 = HashSet.rangeClosed(1,5);
HashSet<Integer> set1 = HashSet.rangeClosed(3, 6);

assertEquals(set0.union(set1), HashSet.rangeClosed(1,6));
assertEquals(set0.diff(set1), HashSet.rangeClosed(1,2));
assertEquals(set0.intersect(set1), HashSet.rangeClosed(3,5));
```

我们还可以执行基本操作，例如添加和删除元素：

```java
HashSet<String> set = HashSet.of("Red", "Green", "Blue");
HashSet<String> newSet = set.add("Yellow");

assertEquals(3, set.size());
assertEquals(4, newSet.size());
assertTrue(newSet.contains("Yellow"));
```

HashSet实现由Hash array mapped trie(HAMT)支持，与[普通](https://en.wikipedia.org/wiki/Hash_array_mapped_trie)HashTable相比，它拥有卓越的性能，其结构使其适合支持持久集合。

### 5.2 TreeSet

不可变的TreeSet是SortedSet接口的实现。它存储一组已排序的元素，并使用二叉搜索树实现。它的所有操作都在O(log n)时间内运行。

默认情况下，TreeSet的元素按其自然顺序排序。

让我们使用自然排序顺序创建一个SortedSet：

```java
SortedSet<String> set = TreeSet.of("Red", "Green", "Blue");
assertEquals("Blue", set.head());

SortedSet<Integer> intSet = TreeSet.of(1,2,3);
assertEquals(2, intSet.average().get().intValue());
```

要以自定义方式对元素进行排序，请在创建TreeSet时传递一个Comparator实例。我们还可以从集合元素生成一个字符串：

```java
SortedSet<String> reversedSet
  = TreeSet.of(Comparator.reverseOrder(), "Green", "Red", "Blue");
assertEquals("Red", reversedSet.head());

String str = reversedSet.mkString(" and ");
assertEquals("Red and Green and Blue", str);
```

### 5.3 BitSet

Vavr集合还包含一个不可变的BitSet实现，BitSet接口扩展了SortedSet接口，可以使用BitSet.Builder中的静态方法实例化BitSet。

与Set数据结构的其他实现一样，BitSet不允许将重复的条目添加到集合中。

它从Traversable接口继承了操作方法。请注意，它与标准Java库中的java.util.BitSet不同。BitSet数据不能包含字符串值。

让我们看看如何使用工厂方法of()创建一个BitSet实例：

```java
BitSet<Integer> bitSet = BitSet.of(1,2,3,4,5,6,7,8);
BitSet<Integer> bitSet1 = bitSet.takeUntil(i -> i > 4);
assertEquals(bitSet1.size(), 4);
```

我们使用takeUntil()来选择BitSet 的前四个元素。该操作返回了一个新实例。请注意，takeUntil()是在Traversable接口中定义的，该接口是BitSet的父接口。

上面演示的在Traversable接口中定义的其他方法和操作也适用于BitSet。

## 6. Map

Map是一种键值数据结构。Vavr的Map是不可变的，并且具有HashMap、TreeMap和LinkedHashMap的实现。

通常，map结构不允许重复的键-尽管可能有重复的值映射到不同的键。

### 6.1 HashMap

HashMap是不可变Map接口的实现，它使用键的哈希码存储键值对。

Vavr的Map使用Tuple2来表示键值对，而不是传统的Entry类型：

```java
Map<Integer, List<Integer>> map = List.rangeClosed(0, 10)
  	.groupBy(i -> i % 2);
        
assertEquals(2, map.size());
assertEquals(6, map.get(0).get().size());
assertEquals(5, map.get(1).get().size());
```

与HashSet类似，HashMap实现由Hash array mapped trie(HAMT)支持，导致几乎所有操作的时间恒定。

我们可以使用 filterKeys()方法按键过滤映射条目，或使用filterValues()方法按值过滤映射条目。两种方法都接受Predicate作为参数：

```java
Map<String, String> map1 = HashMap.of("key1", "val1", "key2", "val2", "key3", "val3");
        
Map<String, String> fMap = map1.filterKeys(k -> k.contains("1") || k.contains("2"));
assertFalse(fMap.containsKey("key3"));
        
Map<String, String> fMap2 = map1.filterValues(v -> v.contains("3"));
assertEquals(fMap2.size(), 1);
assertTrue(fMap2.containsValue("val3"));
```

我们还可以使用map()方法转换Map条目。例如，让我们将map1转换为Map<String, Integer>：

```java
Map<String, Integer> map2 = map1.map(
  	(k, v) -> Tuple.of(k, Integer.valueOf(v.charAt(v.length() - 1) + "")));
assertEquals(map2.get("key1").get().intValue(), 1);
```

### 6.2 TreeMap

不可变的TreeMap是SortedMap接口的实现。与TreeSet类似，Comparator实例用于自定义对TreeMap的元素进行排序。

让我们演示SortedMap的创建：

```java
SortedMap<Integer, String> map = TreeMap.of(3, "Three", 2, "Two", 4, "Four", 1, "One");

assertEquals(1, map.keySet().toJavaArray()[0]);
assertEquals("Four", map.get(4).get());
```

默认情况下，TreeMap的条目按键的自然顺序排序。但是，我们可以指定一个将用于排序的比较器：

```java
TreeMap<Integer, String> treeMap2 = TreeMap.of(Comparator.reverseOrder(), 3,"three", 6, "six", 1, "one");
assertEquals(treeMap2.keySet().mkString(), "631");
```

与TreeSet一样，TreeMap实现也使用树建模，因此其操作时间为O(log n)。map.get(key)返回一个Option，它在Map中的指定键处包装一个值。

## 7. 与Java的互操作性

集合 API 可与Java的集合框架完全互操作。让我们看看这在实践中是如何完成的。

### 7.1 Java到Vavr的转换

Vavr中的每个集合实现都有一个采用java.util.Iterable的静态工厂方法All()。这允许我们从Java集合中创建一个Vavr集合。同样，All()的另一个工厂方法直接采用Java Stream。

将JavaList转换为不可变List：

```java
java.util.List<Integer> javaList = java.util.Arrays.asList(1, 2, 3, 4);
List<Integer> vavrList = List.ofAll(javaList);

java.util.stream.Stream<Integer> javaStream = javaList.stream();
Set<Integer> vavrSet = HashSet.ofAll(javaStream);
```

另一个有用的函数是collector()，它可以与Stream.collect()结合使用来获取Vavr集合：

```java
List<Integer> vavrList = IntStream.range(1, 10)
    .boxed()
    .filter(i -> i % 2 == 0)
    .collect(List.collector());

assertEquals(4, vavrList.size());
assertEquals(2, vavrList.head().intValue());
```

### 7.2 Vavr到Java的转换

值接口有很多方法可以将Vavr类型转换为Java类型，这些方法的格式为toJavaXXX()。

让我们举几个例子：

```java
Integer[] array = List.of(1, 2, 3)
  	.toJavaArray(Integer.class);
assertEquals(3, array.length);

java.util.Map<String, Integer> map = List.of("1", "2", "3")
  	.toJavaMap(i -> Tuple.of(i, Integer.valueOf(i)));
assertEquals(2, map.get("2").intValue());
```

我们还可以使用Java 8收集器从Vavr集合中收集元素：

```java
java.util.Set<Integer> javaSet = List.of(1, 2, 3)
  	.collect(Collectors.toSet());
        
assertEquals(3, javaSet.size());
assertEquals(1, javaSet.toArray()[0]);
```

### 7.3 Java集合视图

或者，该库提供所谓的集合视图，在转换为Java集合时性能更好。上一节中的转换方法遍历所有元素以构建Java集合。

另一方面，视图实现标准Java接口并将方法调用委托给底层Vavr集合。

在撰写本文时，仅支持列表视图。每个顺序集合都有两种方法，一种用于创建不可变视图，另一种用于创建可变视图。

在不可变视图上调用mutator方法会导致UnsupportedOperationException。

让我们看一个例子：

```java
@Test(expected = UnsupportedOperationException.class)
public void givenVavrList_whenViewConverted_thenException() {
    java.util.List<Integer> javaList = List.of(1, 2, 3)
      	.asJava();
    
    assertEquals(3, javaList.get(2).intValue());
    javaList.add(4);
}
```

要创建不可变视图：

```java
java.util.List<Integer> javaList = List.of(1, 2, 3)
  	.asJavaMutable();
javaList.add(4);

assertEquals(4, javaList.get(3).intValue());
```

## 8. 总结

在本教程中，我们了解了Vavr的集合API提供的各种功能数据结构。在Vavr的[集合JavaDoc](https://static.javadoc.io/io.vavr/vavr/0.9.0/io/vavr/collection/package-frame.html)和[用户指南](http://www.vavr.io/vavr-docs/)中可以找到更多有用和高效的API方法。

最后，重要的是要注意该库还定义了Try、Option、Either和Future，它们扩展了Value接口并因此实现了Java的Iterable接口。这意味着它们在某些情况下可以表现为一个集合。