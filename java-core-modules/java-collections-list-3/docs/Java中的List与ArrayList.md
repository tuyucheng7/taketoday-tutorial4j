## 1. 概述

在本文中，我们将研究使用[List](https://www.baeldung.com/tag/java-list)和[ArrayList](https://www.baeldung.com/java-arraylist)类型之间的区别。

首先，我们将看到一个使用ArrayList的示例实现。然后，我们将切换到List界面并比较差异。

## 2.使用数组列表

ArrayList是Java中最常用的List实现之一。它建立在数组之上，当我们添加/删除元素时，它可以动态增长和收缩。当我们知道它会变大时，最好用初始容量初始化一个列表：

```java
ArrayList<String> list = new ArrayList<>(25);
```

通过使用ArrayList作为引用类型，我们可以使用ArrayList API 中不在List API 中的方法——例如，ensureCapacity、trimToSize或removeRange。

### 2.1. 快速示例

让我们编写一个基本的乘客处理应用程序：

```java
public class ArrayListDemo {
    private ArrayList<Passenger> passengers = new ArrayList<>(20);

    public ArrayList<Passenger> addPassenger(Passenger passenger) {
        passengers.add(passenger);
        return passengers;
    }
    
    public ArrayList<Passenger> getPassengersBySource(String source) {
        return new ArrayList<Passenger>(passengers.stream()
            .filter(it -> it.getSource().equals(source))
            .collect(Collectors.toList()));
    }
    
    // Few other functions to remove passenger, get by destination, ... 
}
```

在这里，我们使用ArrayList类型来存储和返回乘客列表。由于最大乘客数为 20，因此列表的初始容量设置为此。

### 2.2. 可变大小数据的问题

只要我们不需要更改 我们正在使用的List的类型，上面的实现就可以正常工作。在我们的示例中，我们选择了 ArrayList并且觉得它满足了我们的需求。

但是，让我们假设随着应用程序的成熟，乘客的数量显然会发生很大变化。例如，如果只有五名预订的乘客，初始容量为 20，则[内存浪费](https://www.baeldung.com/java-list-capacity-array-size#2-building-small-multiple-arraylists)为 75%。假设我们决定切换到内存效率更高的List。

### 2.3. 更改实施类型

Java 提供了另一种称为[LinkedList](https://www.baeldung.com/java-linkedlist)的List实现 来存储可变大小的数据。LinkedList使用链接节点的集合来存储和检索元素。 如果我们决定将基本实现从ArrayList更改为LinkedList会怎样： 

```java
private LinkedList<Passenger> passengers = new LinkedList<>();
```

此更改会影响应用程序的更多部分，因为演示应用程序中的所有函数都希望使用ArrayList类型。

## 3.切换到列表

让我们看看如何使用List接口类型来处理这种情况：

```java
private List<Passenger> passengers = new ArrayList<>(20);
```

在这里，我们使用List接口作为引用类型，而不是更具体的ArrayList类型。我们可以将相同的原则应用于所有函数调用和返回类型。例如：

```java
public List<Passenger> getPassengersBySource(String source) {
    return passengers.stream()
        .filter(it -> it.getSource().equals(source))
        .collect(Collectors.toList());
}
```

现在，让我们考虑相同的问题陈述并将基本实现更改为LinkedList类型。ArrayList和LinkedList类都是List接口的实现。因此，我们现在可以安全地更改基本实现，而不会对应用程序的其他部分造成任何干扰。该类仍然像以前一样编译和工作。

## 4.比较方法

如果我们在整个程序中使用具体的列表类型，那么我们所有的代码都不必要地与该列表类型耦合。这使得将来更改列表类型变得更加困难。

此外，Java 中可用的实用程序类返回抽象类型而不是具体类型。例如，下面的实用函数返回List类型：

```java
Collections.singletonList(...), Collections.unmodifiableList(...)
Arrays.asList(...), ArrayList.sublist(...)
```

具体来说，ArrayList.sublist 返回的是List类型，即使原始对象是ArrayList类型。因此，List API 中的方法不保证返回相同类型的列表。

## 5.总结

在本文中，我们研究了使用List与ArrayList类型的差异和最佳实践。

我们看到引用特定类型如何使应用程序容易在以后的某个时间点发生更改。具体来说，当底层实现发生变化时，它会影响应用程序的其他层。因此，使用最抽象的类型(顶级类/接口)通常优于使用特定的引用类型。