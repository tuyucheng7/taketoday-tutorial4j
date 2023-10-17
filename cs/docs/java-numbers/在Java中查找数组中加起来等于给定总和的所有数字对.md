## 1. 概述

在本快速教程中，我们将展示如何实现一种算法来查找数组中所有数字对的总和等于给定数字。我们将重点关注解决该问题的两种方法。

在第一种方法中，我们会找到所有这样的对，而不考虑唯一性。在第二个中，我们将只找到唯一的数字组合，删除冗余对。

对于每种方法，我们将提供两种实现方式——一种使用 for 循环的传统实现方式，另一种使用Java8 Stream API。

## 2.返回所有匹配对

我们将遍历一个整数数组，使用强力嵌套循环方法找到所有对( i和 j)总和为给定数字(sum )。该算法的运行时复杂度为O(n 2 )。

对于我们的演示，我们将使用以下输入数组查找和等于6的所有数字对：

```java
int[] input = { 2, 4, 3, 3 };
Copy
```

在这种方法中，我们的算法应该返回：

```plaintext
{2,4}, {4,2}, {3,3}, {3,3}
```

在每个算法中，当我们找到一对目标数字总和为目标数字时，我们将使用实用方法addPairs(i, j)收集这对数字。

我们可能想到的第一种实现解决方案的方法是使用传统 的 for 循环：

```java
for (int i = 0; i < input.length; i++) {
    for (int j = 0; j < input.length; j++) {
        if (j != i && (input[i] + input[j]) == sum) {
            addPairs(input[i], sum-input[i]));
        }
    }
}Copy
```

这可能有点简陋，所以让我们也使用Java8 Stream API 编写一个实现。

在这里，我们使用 IntStream.range 方法来生成连续的数字流。然后，我们根据条件过滤它们：数字 1 + 数字 2 = sum：

```java
IntStream.range(0,  input.length)
    .forEach(i -> IntStream.range(0,  input.length)
        .filter(j -> i != j && input[i] + input[j] == sum)
        .forEach(j -> addPairs(input[i], input[j]))
);
Copy
```

## 3.返回所有唯一匹配对

对于这个例子，我们必须开发一种更智能的算法，它只返回唯一的数字组合，忽略冗余对。

为实现这一点，我们将每个元素添加到哈希映射(不排序)，首先检查是否已显示该对。如果不是，我们将检索并将其标记为所示(将值字段设置为null)。

因此，使用与之前相同的输入数组，目标总和为6，我们的算法应该只返回不同的数字组合：

```plaintext
{2,4}, {3,3}
```

如果我们使用传统 的 for 循环，我们将有：

```java
Map<Integer, Integer> pairs = new HashMap();
for (int i : input) {
    if (pairs.containsKey(i)) {
        if (pairs.get(i) != null) {            
            addPairs(i, sum-i);
        }                
        pairs.put(sum - i, null);
    } else if (!pairs.containsValue(i)) {        
        pairs.put(sum-i, i);
    }
}Copy
```

请注意，此实现改进了之前的复杂性，因为我们只使用一个 for 循环，所以我们将拥有O(n)。

现在让我们使用Java8 和 Stream API 来解决这个问题：

```java
Map<Integer, Integer> pairs = new HashMap();
IntStream.range(0, input.length).forEach(i -> {
    if (pairs.containsKey(input[i])) {
        if (pairs.get(input[i]) != null) {
            addPairs(input[i], sum - input[i]);
        }
        pairs.put(sum - input[i], null);
    } else if (!pairs.containsValue(input[i])) {
        pairs.put(sum - input[i], input[i]);
    }
});
```

## 4。总结

在本文中，我们解释了几种不同的方法来查找在Java中求和给定数字的所有对。我们看到了两种不同的解决方案，每一种都使用两种Java核心方法。