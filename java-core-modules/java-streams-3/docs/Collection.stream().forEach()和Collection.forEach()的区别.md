## 1. 概述

在Java中有几种迭代集合的选项。在这个简短的教程中，我们将研究两种外观相似的方法—— Collection.stream().forEach()和Collection.forEach()。

在大多数情况下，两者都会产生相同的结果，但我们会看到一些细微的差别。

## 2. 一个简单的列表

首先，让我们创建一个列表来迭代：

```java
List<String> list = Arrays.asList("A", "B", "C", "D");
```

最直接的方法是使用增强的 for 循环：

```java
for(String s : list) {
    //do something with s
}

```

如果我们想使用函数式风格的 Java，我们也可以使用forEach()。

我们可以直接在集合上这样做：

```java
Consumer<String> consumer = s -> { System.out::println }; 
list.forEach(consumer);

```

或者我们可以在集合的流上调用forEach() ：

```java
list.stream().forEach(consumer);

```

两个版本都将遍历列表并打印所有元素：

```plaintext
ABCD ABCD
```

在这个简单的例子中，我们使用哪个forEach()并没有什么区别。

## 3.执行顺序

Collection.forEach()使用集合的迭代器(如果指定了一个)，因此定义了项目的处理顺序。相比之下，Collection.stream().forEach()的处理顺序是未定义的。

在大多数情况下，我们选择两者中的哪一个并不重要。

### 3.1. 并行流

并行流允许我们在多个线程中执行流，在这种情况下，执行顺序是不确定的。Java 只要求所有线程在调用任何终端操作(例如Collectors.toList() )之前完成。

让我们看一个示例，我们首先在集合上直接调用forEach()，然后在并行流上调用：

```java
list.forEach(System.out::print);
System.out.print(" ");
list.parallelStream().forEach(System.out::print);

```

如果我们多次运行代码，我们会看到list.forEach()按插入顺序处理项目，而 list.parallelStream().forEach()在每次运行时产生不同的结果。

这是一种可能的输出：

```java
ABCD CDBA
```

这是另一个：

```java
ABCD DBCA
```

### 3.2. 自定义迭代器

让我们定义一个带有自定义迭代器的列表，以相反的顺序迭代集合：

```java
class ReverseList extends ArrayList<String> {

    @Override
    public Iterator<String> iterator() {

        int startIndex = this.size() - 1;
        List<String> list = this;

        Iterator<String> it = new Iterator<String>() {

            private int currentIndex = startIndex;

            @Override
            public boolean hasNext() {
                return currentIndex >= 0;
            }

            @Override
            public String next() {
                String next = list.get(currentIndex);
                currentIndex--;
                return next;
             }

             @Override
             public void remove() {
                 throw new UnsupportedOperationException();
             }
         };
         return it;
    }
}

```

然后我们将直接在集合上使用forEach()再次遍历列表，然后在流上：

```java
List<String> myList = new ReverseList();
myList.addAll(list);

myList.forEach(System.out::print); 
System.out.print(" "); 
myList.stream().forEach(System.out::print);

```

我们得到不同的结果：

```java
DCBA ABCD

```

不同结果的原因是直接在列表上使用的forEach()使用了自定义迭代器，而stream().forEach()只是简单地从列表中一个一个地取出元素，而忽略了迭代器。

## 4. 馆藏修改

许多集合(例如ArrayList或HashSet)在遍历它们时不应在结构上进行修改。如果在迭代期间删除或添加元素，我们将得到一个ConcurrentModification异常。

此外，集合被设计为快速失败，这意味着一旦有修改就会抛出异常。

类似地，当我们在流管道执行期间添加或删除元素时，我们将得到一个ConcurrentModification异常。但是，稍后将抛出异常。

两个forEach()方法之间的另一个细微差别是Java明确允许使用迭代器修改元素。相反，流应该是无干扰的。

让我们更详细地看一下删除和修改元素。

### 4.1. 删除元素

让我们定义一个删除列表最后一个元素(“D”)的操作：

```java
Consumer<String> removeElement = s -> {
    System.out.println(s + " " + list.size());
    if (s != null && s.equals("A")) {
        list.remove("D");
    }
};
```

当我们遍历列表时，在打印第一个元素(“A”)后删除最后一个元素：

```java
list.forEach(removeElement);
```

由于forEach()是快速失败的，我们停止迭代并在处理下一个元素之前看到异常：

```java
A 4
Exception in thread "main" java.util.ConcurrentModificationException
	at java.util.ArrayList.forEach(ArrayList.java:1252)
	at ReverseList.main(ReverseList.java:1)
```

让我们看看如果我们改用stream().forEach()会发生什么：

```java
list.stream().forEach(removeElement);
```

在这里，我们在看到异常之前继续遍历整个列表：

```java
A 4
B 3
C 3
null 3
Exception in thread "main" java.util.ConcurrentModificationException
	at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1380)
	at java.util.stream.ReferencePipeline$Head.forEach(ReferencePipeline.java:580)
	at ReverseList.main(ReverseList.java:1)
```

但是，Java 根本不保证会抛出[ConcurrentModificationException](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ConcurrentModificationException.html)。这意味着我们永远不应该编写依赖于此异常的程序。

### 4.2. 改变元素

我们可以在遍历列表时更改元素：

```java
list.forEach(e -> {
    list.set(3, "E");
});
```

但是，尽管使用Collection.forEach()或stream().forEach()执行此操作没有问题，但Java 要求对流的操作是无干扰的。这意味着在流管道执行期间不应修改元素。

这背后的原因是流应该促进并行执行。在这里，修改流的元素可能会导致意外行为。

## 5.总结

在本文中，我们看到了一些示例，这些示例显示了Collection.forEach()和Collection.stream().forEach()之间的细微差别。

重要的是要注意上面显示的所有示例都是微不足道的，并且仅用于比较迭代集合的两种方式。我们不应该编写其正确性依赖于所示行为的代码。

如果我们不需要流，而只想迭代一个集合，那么首选应该是直接在集合上使用forEach() 。