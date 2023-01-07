## 1. 概述

在本快速教程中，我们将探讨在JavaMap中查找最大值的各种方法。我们还将看到Java8中的新特性如何简化此操作。

在开始之前，让我们简要回顾一下Java中如何[比较对象](https://www.baeldung.com/java-comparator-comparable)。

通常，对象可以通过从Comparable接口实现方法compareTo()来表达自然排序。但是，可以通过Comparator对象采用自然排序以外的排序。随着我们的继续，我们将看到更多细节。

## 2.Java 8之前

让我们先开始探讨如何在没有Java8 功能的情况下找到最高价值。

### 2.1. 使用简单迭代

使用迭代，我们可以简单地遍历 Map的所有条目以选择最高值，将当前最高值存储在变量中：

```java
public <K, V extends Comparable<V>> V maxUsingIteration(Map<K, V> map) {
    Map.Entry<K, V> maxEntry = null;
    for (Map.Entry<K, V> entry : map.entrySet()) {
        if (maxEntry == null || entry.getValue()
            .compareTo(maxEntry.getValue()) > 0) {
            maxEntry = entry;
        }
    }
    return maxEntry.getValue();
}
```

在这里，我们还利用Java泛型来构建可应用于不同类型的方法。

### 2.2. 使用 Collections.max()

现在让我们看看Collections类中 的实用方法max()如何让我们免于自己编写大量这样的代码：

```java
public <K, V extends Comparable<V>> V maxUsingCollectionsMax(Map<K, V> map) {
    Entry<K, V> maxEntry = Collections.max(map.entrySet(), new Comparator<Entry<K, V>>() {
        public int compare(Entry<K, V> e1, Entry<K, V> e2) {
            return e1.getValue()
                .compareTo(e2.getValue());
        }
    });
    return maxEntry.getValue();
}
```

在这个例子中，我们将一个Comparator对象传递给max() ，它可以通过compareTo()来利用 Entry值的自然排序，或者完全实现不同的排序。

## 3.Java 8之后

Java 8 的特性可以简化我们上面以多种方式从Map获取最大值的尝试。

### 3.1. 将 Collections.max() 与 Lambda 表达式结合使用

让我们首先探索 lambda 表达式如何简化对Collections.max()的调用：

```java
public <K, V extends Comparable<V>> V maxUsingCollectionsMaxAndLambda(Map<K, V> map) {
    Entry<K, V> maxEntry = Collections.max(map.entrySet(), (Entry<K, V> e1, Entry<K, V> e2) -> e1.getValue()
        .compareTo(e2.getValue()));
    return maxEntry.getValue();
}
```

正如我们在这里看到的，lambda 表达式使我们免于定义成熟的功能接口，并提供了一种定义逻辑的简洁方法。要阅读有关 lambda 表达式的更多信息，另请查看[我们之前的文章](https://www.baeldung.com/java-8-lambda-expressions-tips)。

### 3.2. 使用 流

Stream API 是Java 8的另一个新增功能，它在很大程度上简化了集合的使用：

```java
public <K, V extends Comparable<V>> V maxUsingStreamAndLambda(Map<K, V> map) {
    Optional<Entry<K, V>> maxEntry = map.entrySet()
        .stream()
        .max((Entry<K, V> e1, Entry<K, V> e2) -> e1.getValue()
            .compareTo(e2.getValue())
        );
    
    return maxEntry.get().getValue();
}
```

这个 API 提供了很多数据处理查询，比如对集合的 map-reduce转换。在这里，我们对Map Entry 流使用了max() ，这是缩减操作的特例。有关Stream API 的 更多详细信息，请参见[此处](https://www.baeldung.com/java-8-streams-introduction)。

我们还在这里使用 Optional API，它是Java8 中添加的一个容器对象，它可能包含也可能不包含非空值。 可以在 [此处获得有关](https://www.baeldung.com/java-optional)Optional的更多详细信息。

### 3.3. 将 Stream与方法引用一起使用

最后，让我们看看方法引用如何进一步简化我们对 lambda 表达式的使用：

```java
public <K, V extends Comparable<V>> V maxUsingStreamAndMethodReference(Map<K, V> map) {
    Optional<Entry<K, V>> maxEntry = map.entrySet()
        .stream()
        .max(Comparator.comparing(Map.Entry::getValue));
    return maxEntry.get()
        .getValue();
}
```

在 lambda 表达式仅调用现有方法的情况下，方法引用允许我们直接使用方法名称来执行此操作。有关方法参考 的更多详细信息，请查看[之前的这篇文章](https://www.baeldung.com/java-8-double-colon-operator)。

## 4。总结

在本文中，我们看到了多种在Java Map中查找最高值的方法，其中一些使用了作为Java8 的一部分添加的功能。