## 1. 概述

在本教程中，我们将探索列出范围内数字序列的不同方法。

## 2. 列出范围内的数字

### 2.1. 传统的 for循环

我们可以使用传统的 for循环来生成指定范围内的数字：

```java
public List<Integer> getNumbersInRange(int start, int end) {
    List<Integer> result = new ArrayList<>();
    for (int i = start; i < end; i++) {
        result.add(i);
    }
    return result;
}
```

上面的代码将生成一个列表，其中包含从开始(包括)到结束(不包括)的数字。

### 2.2. JDK 8 IntStream.范围

JDK 8 中引入的IntStream可用于生成给定范围内的数字，从而减少对for循环的需要：

```java
public List<Integer> getNumbersUsingIntStreamRange(int start, int end) {
    return IntStream.range(start, end)
      .boxed()
      .collect(Collectors.toList());
}
```

### 2.3. IntStream.rangeClosed

在上一节中，结束是排他性的。要获取包含末尾的范围内的数字，可以使用IntStream.rangeClosed：

```java
public List<Integer> getNumbersUsingIntStreamRangeClosed(int start, int end) {
    return IntStream.rangeClosed(start, end)
      .boxed()
      .collect(Collectors.toList());
}
```

### 2.4. IntStream.迭代

前面的部分使用范围来获取数字序列。当我们知道一个序列中需要多少个数字时，我们可以使用IntStream.iterate：

```java
public List<Integer> getNumbersUsingIntStreamIterate(int start, int limit) {
    return IntStream.iterate(start, i -> i + 1)
      .limit(limit)
      .boxed()
      .collect(Collectors.toList());
}
```

此处，limit参数限制了要迭代的元素数量。

## 3.总结

在本文中，我们看到了在一个范围内生成数字的不同方法。