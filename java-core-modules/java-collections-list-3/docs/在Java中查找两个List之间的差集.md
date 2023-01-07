## 1. 概述

查找相同数据类型的对象集合之间的差异是一项常见的编程任务。例如，假设我们有一个申请考试的学生列表，以及另一个通过考试的学生列表。这两个列表之间的差异会告诉我们没有通过考试的学生。

在Java中，没有明确的方法可以找到List API 中两个列表之间的差异，尽管有一些辅助方法很接近。

在本快速教程中，我们将学习如何找出两个列表之间的差异。我们将尝试几种不同的方法，包括普通Java(有和没有Streams)和第三方库，例如Guava和Apache Commons Collections。

## 2. 测试设置

让我们从定义两个列表开始，我们将使用它们来测试我们的示例：

```java
public class FindDifferencesBetweenListsUnitTest {

    private static final List listOne = Arrays.asList("Jack", "Tom", "Sam", "John", "James", "Jack");
    private static final List listTwo = Arrays.asList("Jack", "Daniel", "Sam", "Alan", "James", "George");

}
```

## 3. 使用 Java列表API

我们可以创建一个列表的副本，然后 使用List方法removeAll()删除与另一个列表共有的所有元素：

```java
List<String> differences = new ArrayList<>(listOne);
differences.removeAll(listTwo);
assertEquals(2, differences.size());
assertThat(differences).containsExactly("Tom", "John");
```

让我们反过来找出不同之处：

```java
List<String> differences = new ArrayList<>(listTwo);
differences.removeAll(listOne);
assertEquals(3, differences.size());
assertThat(differences).containsExactly("Daniel", "Alan", "George");
```

我们还应该注意，如果我们想找到两个列表之间的共同元素，List还包含一个retainAll方法。

## 4. 使用流 API

Java [Stream](https://www.baeldung.com/java-streams)可用于对集合中的数据执行顺序操作，其中包括过滤列表之间的差异：

```java
List<String> differences = listOne.stream()
            .filter(element -> !listTwo.contains(element))
            .collect(Collectors.toList());
assertEquals(2, differences.size());
assertThat(differences).containsExactly("Tom", "John");
```

与我们的第一个示例一样，我们可以切换列表的顺序以从第二个列表中找到不同的元素：

```java
List<String> differences = listTwo.stream()
            .filter(element -> !listOne.contains(element))
            .collect(Collectors.toList());
assertEquals(3, differences.size());
assertThat(differences).containsExactly("Daniel", "Alan", "George");
```

我们应该注意到List的重复调用。contains()对于较大的列表来说可能是一项代价高昂的操作。

## 5. 使用第三方库

### 5.1. 使用谷歌番石榴

Guava包含一个方便的Sets。difference method，但要使用它，我们需要先将我们的List转换为Set：

```java
List<String> differences = new ArrayList<>(Sets.difference(Sets.newHashSet(listOne), Sets.newHashSet(listTwo)));
assertEquals(2, differences.size());
assertThat(differences).containsExactlyInAnyOrder("Tom", "John");
```

我们应该注意到，将List转换为Set会产生和重新排序的效果。

### 5.2. 使用 Apache Commons 集合

Apache Commons Collections中的CollectionUtils类包含一个removeAll方法。

此方法与List相同。removeAll，同时还为结果创建一个新集合：

```java
List<String> differences = new ArrayList<>((CollectionUtils.removeAll(listOne, listTwo)));
assertEquals(2, differences.size());
assertThat(differences).containsExactly("Tom", "John");
```

## 6. 处理重复值

现在让我们看看当两个列表包含重复值时找出差异。

为此，我们需要从第一个列表中删除重复元素，精确地删除它们在第二个列表中包含的次数。

在我们的示例中，值“Jack”在第一个列表中出现了两次，而在第二个列表中只出现了一次：

```java
List<String> differences = new ArrayList<>(listOne);
listTwo.forEach(differences::remove);
assertThat(differences).containsExactly("Tom", "John", "Jack");
```

我们还可以使用Apache Commons Collections的subtract 方法来实现这一点：

```java
List<String> differences = new ArrayList<>(CollectionUtils.subtract(listOne, listTwo));
assertEquals(3, differences.size());
assertThat(differences).containsExactly("Tom", "John", "Jack");
```

## 七、总结

在本文中，我们探索了几种查找列表之间差异的方法。我们介绍了一个基本的Java解决方案、一个使用Streams API 的解决方案，以及使用第三方库(如Google Guava和Apache Commons Collections)的解决方案。

我们还讨论了如何处理重复值。