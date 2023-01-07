## 1. 概述

在本快速教程中，我们将了解两种 看似做同样事情但实际上并非如此的 Collection方法： clear()和 removeAll()。

我们将首先看到方法定义，然后在简短示例中使用它们。

## 2. 集合.clear()

我们将首先深入了解 Collection.clear()方法。让我们检查[该方法的 Javadoc](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collection.html#clear())。根据它，clear() 的目的是从列表中删除每个元素。

所以，基本上，对任何列表调用 clear()都会导致列表变空。

## 3. 集合.removeAll()

我们现在来看看 Collection.removeAll()的[Javadoc](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collection.html#removeAll(java.util.Collection)) 。 我们可以看到该方法将 Collection作为参数。它的目的是移除列表和集合之间的所有公共元素。

因此，当在集合上调用它时，它将从传递的参数中删除所有元素，这些元素也在我们调用removeAll()的集合中。

## 4.例子

现在让我们看一些代码来了解这些方法的作用。我们将首先创建一个名为 ClearVsRemoveAllUnitTest的测试类。

之后，我们将为 Collection.clear()创建第一个测试。

我们将用一些数字 初始化一个整数集合，并对其调用clear() ，这样列表中就没有元素了：

```java
@Test
void whenClear_thenListBecomesEmpty() {
    Collection<Integer> collection = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

    collection.clear();

    assertTrue(collection.isEmpty());
}
```

正如我们所见， 调用clear()后集合为空。

让我们用两个集合创建第二个测试，一个包含从 1 到 5 的数字，另一个包含从 3 到 7 的数字。之后，我们将在第一个集合上调用 removeAll() 并将第二个集合作为参数。

我们希望只有数字 1 和 2 保留在第一个集合中(而第二个没有变化)：

```java
@Test
void whenRemoveAll_thenFirstListMissElementsFromSecondList() {
    Collection<Integer> firstCollection = new ArrayList<>(
      Arrays.asList(1, 2, 3, 4, 5));
    Collection<Integer> secondCollection = new ArrayList<>(
      Arrays.asList(3, 4, 5, 6, 7));

    firstCollection.removeAll(secondCollection);

    assertEquals(
      Arrays.asList(1, 2), 
      firstCollection);
    assertEquals(
      Arrays.asList(3, 4, 5, 6, 7), 
      secondCollection);
}
```

我们的期望得到满足。第一个集合中只剩下数字 1 和 2，第二个没有改变。

## 5.总结

在本文中，我们了解了 Collection.clear()和 Collection.removeAll()的用途。

尽管我们一开始可能会这么想，但他们并没有做同样的事情。clear()删除集合中的每个元素，而removeAll()只删除与另一个 Collection中匹配的元素。