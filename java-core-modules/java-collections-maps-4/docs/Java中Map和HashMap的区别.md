## 1. 概述

Map 和 [HashMap](https://www.baeldung.com/java-hashmap)的区别在于第一个是接口，第二个是实现。然而，在本文中，我们将深入挖掘并解释接口为何有用。此外，我们还将学习如何使用接口使代码更灵活，以及为什么我们对同一接口有不同的实现。

## 2.接口的目的

接口是仅定义行为的契约。每个实现特定接口的类都应该履行这个契约。为了更好地理解它，我们可以举一个现实生活中的例子。想象一辆汽车。每个人的脑海里都会有不同的形象。汽车一词暗示了一些品质和行为。任何具有这些品质的物体都可以称为汽车。这就是为什么我们每个人都想象着一辆不同的汽车。

接口工作相同。地图 是一种抽象，它定义了某些品质和行为。只有具备所有这些品质的类才能成为Map。

## 3.不同的实现

出于同样的原因，我们有不同的车型，我们有不同的Map接口实现。所有的实现都有不同的目的。不可能找到总体上最好的实现。对于某些目的只有最好的实现。虽然跑车速度快，看起来很酷，但它并不是家庭野餐或去家具店旅行的最佳选择。

[HashMap](https://www.baeldung.com/java-hashmap)是Map接口的最简单实现，并提供基本功能。大多数情况下，此实现涵盖了所有需求。另外两个广泛使用的实现是[TreeMap](https://www.baeldung.com/java-treemap)和 [LinkedHashMap](https://www.baeldung.com/java-linked-hashmap)提供额外的功能。

这是一个更详细但不完整的层次结构：

[![地图层级](https://www.baeldung.com/wp-content/uploads/2022/02/Map.png)](https://www.baeldung.com/wp-content/uploads/2022/02/Map.png)

## 4. 编程实现

想象一下，我们想在控制台中[打印](https://www.baeldung.com/java-iterate-map)[HashMap的键和值：](https://www.baeldung.com/java-hashmap) 

```java
public class HashMapPrinter {

    public void printMap(final HashMap<?, ?> map) {
        for (final Entry<?, ?> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }
}
```

这是一个完成这项工作的小班。然而，它包含一个问题。它将只能与[HashMap](https://www.baeldung.com/java-hashmap)一起使用。 因此，任何尝试传递到方法[TreeMap 或什 至HashMap](https://www.baeldung.com/java-treemap-vs-hashmap)中，被Map 引用都将导致编译错误：

```java
public class Main {
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        HashMap<String, String> hashMap = new HashMap<>();
        TreeMap<String, String> treeMap = new TreeMap<>();

        HashMapPrinter hashMapPrinter = new HashMapPrinter();
        hashMapPrinter.printMap(hashMap);
//        hashMapPrinter.printMap(treeMap); Compile time error
//        hashMapPrinter.printMap(map); Compile time error
    }
}
```

让我们试着理解为什么会这样。在这两种情况下，编译器都无法确定在此方法内部不会调用[HashMap](https://www.baeldung.com/java-hashmap) 特定方法。

[TreeMap](https://www.baeldung.com/java-treemap) 位于 Map 实现的不同分支上(没有双关语意)，因此它可能缺少[HashMap](https://www.baeldung.com/java-hashmap)中定义的一些方法。 

在第二种情况下，尽管实际底层对象是[HashMap](https://www.baeldung.com/java-hashmap)类型，但它由Map接口引用。因此，该对象将只能公开Map中定义的方法，而不能公开[HashMap](https://www.baeldung.com/java-hashmap)中定义的方法。

因此，即使我们的HashMapPrinter是一个非常简单的类，它也太具体了。使用这种方法，需要我们为每个Map 实现创建一个特定的打印机。

## 5.接口编程

初学者常常对“针对接口编程”或“针对接口编写代码”的表达含义感到困惑。让我们考虑以下示例，这将使它更清楚一些。我们将参数的类型更改为最通用的类型，即Map：

```java
public class MapPrinter {
    
    public void printMap(final Map<?, ?> map) {
        for (final Entry<?, ?> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }
}
```

如我们所见，实际实现保持不变，而唯一的变化是参数的类型。这表明该方法没有使用[HashMap](https://www.baeldung.com/java-hashmap)的任何特定方法。所有需要的功能都已在Map接口中定义，即方法[entrySet()](https://www.baeldung.com/java-map-entries-methods)。

结果，这个微小的变化造成了巨大的差异。现在，此类可以与任何Map实现一起使用：

```java
public class Main {
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        HashMap<String, String> hashMap = new HashMap<>();
        TreeMap<String, String> treeMap = new TreeMap<>();

        MapPrinter mapPrinter = new MapPrinter();
        mapPrinter.printMap(hashMap);
        mapPrinter.printMap(treeMap);
        mapPrinter.printMap(map);
    }
}

```

接口编码帮助我们创建了一个通用类，它可以与Map接口的任何实现一起工作。这种方法可以消除代码重复并确保我们的类和方法具有明确定义的目的。

## 6. 在哪里使用接口

总的来说，参数应该是最通用的类型。我们在前面的示例中看到了方法签名的简单更改如何改进我们的代码。我们应该采用相同方法的另一个地方是构造函数：

```java
public class MapReporter {

    private final Map<?, ?> map;

    public MapReporter(final Map<?, ?> map) {
        this.map = map;
    }

    public void printMap() {
        for (final Entry<?, ?> entry : this.map.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }
}

```

此类可以与 Map 的任何实现一起使用， 只是因为我们在构造函数中使用了正确的类型。

## 七、总结

总而言之，在本教程中我们讨论了为什么接口是抽象和定义契约的好方法。尽可能使用最通用的类型将使代码易于重用和阅读。同时，这种方法减少了代码量，这始终是简化代码库的好方法。