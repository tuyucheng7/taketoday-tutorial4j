## 1. 概述

在本文中，我们将介绍[ConcurrentModificationException](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ConcurrentModificationException.html)类。

首先，我们将解释它是如何工作的，然后通过使用触发它的测试来证明它。

最后，我们将通过实际示例尝试一些解决方法。

## 2. 引发ConcurrentModificationException

本质上，ConcurrentModificationException用于在我们**正在迭代的内容被修改时快速失败**，让我们通过一个简单的测试来证明这一点：

```java
@Test(expected = ConcurrentModificationException.class)
public void givenIterating_whenRemoving_thenThrowException() {
    List<Integer> integers = newArrayList(1, 2, 3);

    for (Integer integer : integers) {
        integers.remove(1);
    }
}
```

如我们所见，在完成迭代之前，我们试图删除一个元素。这就是触发异常的原因。

## 3. 解决方案

有时，我们实际上可能希望在迭代时从集合中删除元素。如果是这种情况，那么有一些解决方案。

### 3.1 直接使用迭代器

for-each循环在幕后使用迭代器，但不那么冗长。但是，如果我们重构之前的测试以使用迭代器，我们将可以访问其他方法，例如remove()。让我们尝试使用此方法来修改我们的集合：

```java
@Test
public void givenIterating_whenUsingIteratorRemove_thenNoError() {
    List<Integer> integers = newArrayList(1, 2, 3);

    for (Iterator<Integer> iterator = integers.iterator(); iterator.hasNext(); ) {
        Integer integer = iterator.next();
        if (integer == 2) {
            iterator.remove();
        }
    }

    assertThat(integers).containsExactly(1, 3);
}
```

现在我们会注意到没有发生异常。原因是remove()方法不会导致ConcurrentModificationException，迭代时调用是安全的。

### 3.2 迭代期间不删除

如果我们想保留我们的for-each循环，那也不是不可以，只是我们需要等到迭代之后再移除元素。让我们通过在迭代时将要删除的内容添加到toRemove集合来实现这一点：

```java
@Test
public void givenIterating_whenUsingRemovalList_thenNoError() {
    List<Integer> integers = newArrayList(1, 2, 3);
    List<Integer> toRemove = newArrayList();

    for (Integer integer : integers) {
        if (integer == 2) {
            toRemove.add(integer);
        }
    }
    integers.removeAll(toRemove);

    assertThat(integers).containsExactly(1, 3);
}
```

这是解决问题的另一种有效方法。

### 3.3 removeIf()

Java 8向Collection接口添加了removeIf()方法。这意味着如果我们使用它，我们可以使用函数式编程的方式再次实现相同的结果：

```java
@Test
public void whenUsingRemoveIf_thenRemoveElements() {
    Collection<Integer> integers = newArrayList(1, 2, 3);

    integers.removeIf(i -> i == 2);

    assertThat(integers).containsExactly(1, 3);
}
```

这种声明式风格为我们提供了最少的冗长。但是，根据用例，我们可能会发现其他方法更方便。

### 3.4 使用Stream过滤

当深入到函数式/声明式编程的世界时，我们可以忘记改变集合，相反，我们可以专注于应该实际处理的元素：

```java
@Test
public void whenUsingStream_thenRemoveElements() {
    Collection<Integer> integers = newArrayList(1, 2, 3);

    List<String> collected = integers
        .stream()
        .filter(i -> i != 2)
        .map(Object::toString)
        .collect(toList());

    assertThat(collected).containsExactly("1", "3");
}
```

通过提供一个谓词来确定要包含而不是排除的元素，我们已经完成了与前面示例相反的操作。优点是我们可以在删除的同时将其他函数链接在一起。在示例中，我们使用了函数map()，但如果需要，可以使用更多的操作。

## 4. 总结

在本文中，我们展示了在迭代时从集合中删除元素时可能遇到的问题，还提供了一些解决该问题的解决方案。