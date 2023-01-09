## 1. 简介

这本食谱说明了如何使用 Guava 风格的 Ordering 和 Comparators。它延续了我在[上一篇关于 Guava 集合的帖子](https://www.baeldung.com/guava-collections)中开始的食谱和示例焦点格式。

## 2.食谱

处理集合中的空值

空值优先

```java
List<Integer> toSort = Arrays.asList(3, 5, 4, null, 1, 2);
Collections.sort(toSort, Ordering.natural().nullsFirst());
assertThat(toSort.get(0), nullValue());
```

最后为空

```java
List<Integer> toSort = Arrays.asList(3, 5, 4, null, 1, 2);
Collections.sort(toSort, Ordering.natural().nullsLast());
assertThat(toSort.get(toSort.size() - 1), nullValue());
```

自然排序

```java
List<Integer> toSort = Arrays.asList(3, 5, 4, 1, 2);
Collections.sort(toSort, Ordering.natural());

assertTrue(Ordering.natural().isOrdered(toSort));
```

链接 2 个排序

```java
List<Integer> toSort = Arrays.asList(3, 5, 4, 1, 2);
Collections.sort(toSort, Ordering.natural().reverse());
```

反向排序

```java
List<Integer> toSort = Arrays.asList(3, 5, 4, null, 1, 2);
Collections.sort(toSort, Ordering.natural().nullsLast().reverse());
assertThat(toSort.get(0), nullValue());
```

自定义顺序 – 按长度排列的字符串

```java
private class OrderingByLenght extends Ordering<String> {
    @Override
    public int compare(String s1, String s2) {
        return Ints.compare(s1.length(), s2.length());
    }
}
List<String> toSort = Arrays.asList("zz", "aa", "b", "ccc");
Ordering<String> byLength = new OrderingByLenght();
Collections.sort(toSort, byLength);

Ordering<String> expectedOrder = Ordering.explicit(Lists.newArrayList("b", "zz", "aa", "ccc"));
assertTrue(expectedOrder.isOrdered(toSort))
```

检查显式顺序

```java
List<String> toSort = Arrays.asList("zz", "aa", "b", "ccc");
Ordering<String> byLength = new OrderingByLenght();
Collections.sort(toSort, byLength);

Ordering<String> expectedOrder = Ordering.explicit(Lists.newArrayList("b", "zz", "aa", "ccc"));
assertTrue(expectedOrder.isOrdered(toSort));
```

检查字符串顺序

```java
List<Integer> toSort = Arrays.asList(3, 5, 4, 2, 1, 2);
Collections.sort(toSort, Ordering.natural());

assertFalse(Ordering.natural().isStrictlyOrdered(toSort));
```

二次排序

```java
List<String> toSort = Arrays.asList("zz", "aa", "b", "ccc");
Ordering<String> byLength = new OrderingByLenght();
Collections.sort(toSort, byLength.compound(Ordering.natural()));

Ordering<String> expectedOrder = Ordering.explicit(Lists.newArrayList("b", "aa", "zz", "ccc"));
assertTrue(expectedOrder.isOrdered(toSort));
```

复杂的自定义排序示例——带链接


```java
List<String> toSort = Arrays.asList("zz", "aa", null, "b", "ccc");
Collections.sort(toSort, 
    new OrderingByLenght().reverse().compound(Ordering.natural()).nullsLast());
System.out.println(toSort);
```

使用toString表示排序

```java
List<Integer> toSort = Arrays.asList(1, 2, 11);
Collections.sort(toSort, Ordering.usingToString());

Ordering<Integer> expectedOrder = Ordering.explicit(Lists.newArrayList(1, 11, 2));
assertTrue(expectedOrder.isOrdered(toSort));
```

排序，然后查找(二分查找)

```java
List<Integer> toSort = Arrays.asList(1, 2, 11);
Collections.sort(toSort, Ordering.usingToString());
int found = Ordering.usingToString().binarySearch(toSort, 2);
System.out.println(found);
```

无需排序即可找到最小值/最大值(更快)

```java
List<Integer> toSort = Arrays.asList(2, 1, 11, 100, 8, 14);
int found = Ordering.usingToString().min(toSort);
assertThat(found, equalTo(1));
```

从排序中创建列表的排序副本

```java
List<String> toSort = Arrays.asList("aa", "b", "ccc");
List<String> sortedCopy = new OrderingByLenght().sortedCopy(toSort);

Ordering<String> expectedOrder = Ordering.explicit(Lists.newArrayList("b", "aa", "ccc"));
assertFalse(expectedOrder.isOrdered(toSort));
assertTrue(expectedOrder.isOrdered(sortedCopy));
```

创建一个排序的部分副本——最少的元素

```java
List<Integer> toSort = Arrays.asList(2, 1, 11, 100, 8, 14);
List<Integer> leastOf = Ordering.natural().leastOf(toSort, 3);
List<Integer> expected = Lists.newArrayList(1, 2, 8);
assertThat(expected, equalTo(leastOf));
```

通过中介功能订购

```java
List<Integer> toSort = Arrays.asList(2, 1, 11, 100, 8, 14);
Ordering<Object> ordering = Ordering.natural().onResultOf(Functions.toStringFunction());
List<Integer> sortedCopy = ordering.sortedCopy(toSort);

List<Integer> expected = Lists.newArrayList(1, 100, 11, 14, 2, 8);
assertThat(expected, equalTo(sortedCopy));
```

–注意：排序逻辑将首先通过函数运行数字 – 将它们转换为字符串 – 然后按照字符串的自然顺序进行排序

## 3. 更多番石榴食谱

Guava 是一个全面且非常有用的库——这里有一些以食谱形式介绍的 API：

-   #### [番石榴功能食谱](https://www.baeldung.com/guava-functions-predicates)

-   #### [番石榴系列食谱](https://www.baeldung.com/guava-collections)

享受。

## 4. 总结

这种实验形式——食谱——有一个明确的重点——简单和速度，所以大多数食谱除了代码示例本身之外没有额外的解释。

正如我之前提到的——这是一份动态文档——欢迎在评论中添加新的示例和用例，我会在遇到它们时继续添加自己的示例和用例。