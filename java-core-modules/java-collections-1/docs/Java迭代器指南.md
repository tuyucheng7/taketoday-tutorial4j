## 1.概述

迭代器是我们可以遍历集合的众多方法之一，作为每个选项，它都有其优点和缺点。

它最初是在Java1.2中作为枚举的替代品引入的，并且：

-   引入了改进的方法名称
-   [使得从我们正在迭代的集合中删除元素成为可能](https://www.baeldung.com/java-concurrentmodificationexception)
-   不保证迭代顺序

在本教程中，我们将回顾简单的Iterator接口以了解如何使用它的不同方法。

我们还将检查更强大的ListIterator扩展，它添加了一些有趣的功能。

## 2.迭代器接口

首先，我们需要从Collection中获取一个Iterator；这是通过调用iterator()方法完成的。

为简单起见，我们将从列表中获取Iterator实例：

```java
List<String> items = ...
Iterator<String> iter = items.iterator();
```

Iterator接口有3个核心方法：

### 2.1.下一个()

hasNext()方法可用于检查是否至少还有一个元素要迭代。

它旨在用作while循环中的条件：

```java
while (iter.hasNext()) {
    // ...
}
```

### 2.2.下一个()

next()方法可用于跨过下一个元素并获取它：

```java
String next = iter.next();
```

在尝试调用next()之前使用hasNext()是一种很好的做法。

集合的迭代器不保证以任何特定顺序迭代，[除非特定实现提供它。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collection.html#iterator())

### 2.3.去掉()

最后，如果我们想从集合中移除当前元素，我们可以使用remove：

```java
iter.remove();
```

这是在遍历集合时移除元素的安全方法，不会出现[ConcurrentModificationException风险。](https://www.baeldung.com/java-concurrentmodificationexception)

### 2.4.完整迭代器示例

现在我们可以将它们全部结合起来，看看我们如何将这三种方法一起用于集合过滤：

```java
while (iter.hasNext()) {
    String next = iter.next();
    System.out.println(next);
 
    if( "TWO".equals(next)) {
        iter.remove();				
    }
}
```

这就是我们通常使用Iterator的方式，我们提前检查是否有另一个元素，我们检索它，然后对其执行一些操作。

### 2.5.使用Lambda表达式迭代

正如我们在前面的例子中看到的，当我们只想遍历所有元素并对它们做一些事情时，使用Iterator是非常冗长的。

从Java8开始，我们有了forEachRemaining方法，它允许使用lambda表达式来处理剩余元素：

```java
iter.forEachRemaining(System.out::println);
```

## 3.ListIterator接口

ListIterator是一个扩展，它添加了用于遍历列表的新功能：

```java
ListIterator<String> listIterator = items.listIterator(items.size());
```

请注意我们如何提供起始位置，在本例中是列表的末尾。

### 3.1.hasPrevious()和previous()

ListIterator可用于向后遍历，因此它提供了hasNext()和next()的等价物：

```java
while(listIterator.hasPrevious()) {
    String previous = listIterator.previous();
}
```

### 3.2.nextIndex()和previousIndex()

此外，我们可以遍历索引而不是实际元素：

```java
String nextWithIndex = items.get(listIterator.nextIndex());
String previousWithIndex = items.get(listIterator.previousIndex());
```

如果我们需要知道当前正在修改的对象的索引，或者如果我们想保留已删除元素的记录，这将非常有用。

### 3.3.添加()

add方法，顾名思义，允许我们在next()返回的项目之前和previous()返回的项目之后添加一个元素：

```java
listIterator.add("FOUR");
```

### 3.4.放()

最后一个值得一提的方法是set()，它让我们替换在调用next()或previous()时返回的元素：

```java
String next = listIterator.next();
if( "ONE".equals(next)) {
    listIterator.set("SWAPPED");
}
```

重要的是要注意，只有在没有事先调用add()或remove()的情况下才能执行此操作。

### 3.5.完整的ListIterator示例

我们现在可以将它们全部组合成一个完整的示例：

```java
ListIterator<String> listIterator = items.listIterator();
while(listIterator.hasNext()) {
    String nextWithIndex = items.get(listIterator.nextIndex());		
    String next = listIterator.next();
    if("REPLACE ME".equals(next)) {
        listIterator.set("REPLACED");
    }
}
listIterator.add("NEW");
while(listIterator.hasPrevious()) {
    String previousWithIndex
     = items.get(listIterator.previousIndex());
    String previous = listIterator.previous();
    System.out.println(previous);
}
```

在此示例中，我们首先从List获取ListIterator，然后我们可以通过索引(不会增加迭代器的内部当前元素)或调用next获取下一个元素。

然后我们可以用set替换一个特定的项目并用add插入一个新的项目。

到达迭代结束后，我们可以返回修改其他元素或简单地从下到上打印它们。

## 4。总结

Iterator接口允许我们在遍历集合的同时对其进行修改，这对于简单的for/while语句来说更加困难。反过来，这为我们提供了一个很好的模式，我们可以在许多方法中使用它，这些方法只需要集合处理，同时保持良好的内聚性和低耦合性。