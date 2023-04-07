## 一、概述

Java 为我们提供了一系列方法来重新排列[*ArrayList*](https://www.baeldung.com/java-arraylist)中的项目。在本教程中，我们将研究其中的三个。

## 2. 移动物品

最手动的方法，也是给我们最多控制的方法，是将项目直接移动到新位置。**我们可以通过首先使用\*ArrayList.remove() 来完成此操作，\*它返回已删除的项目。***然后我们可以使用ArrayList.add()*在我们选择的位置重新插入该项目：

```java
@Test
void givenAList_whenManuallyReordering_thenOneItemMovesPosition() {
    ArrayList<String> arrayList = new ArrayList<>(Arrays.asList("one", "two", "three", "four", "five"));

    String removed = arrayList.remove(3);
    arrayList.add(2, removed);

    ArrayList<String> expectedResult = new ArrayList<>(Arrays.asList("one", "two", "four", "three", "five"));
    assertEquals(expectedResult, arrayList);
}复制
```

在底层，*ArrayList*使用数组。这意味着移除和插入项目会因为移动所有其他项目而产生很大的开销。出于这个原因，我们应该尽可能避免这种方法，并使用下面两种方法之一，它们都将 ArrayList 保持*在*其原始长度。

## 3.交换两个项目

**我们可以使用\*Collections.swap()交换\**ArrayList\*中两个项目的位置。**swap *()*方法采用三个参数，首先是要调整的*ArrayList*，然后是要交换的两个项目的位置：

```java
@Test
public void givenAList_whenUsingSwap_thenItemsSwapPositions() {
    ArrayList<String> arrayList = new ArrayList<>(Arrays.asList("one", "two", "three", "four", "five"));

    Collections.swap(arrayList, 1, 3);

    ArrayList<String> expectedResult = new ArrayList<>(Arrays.asList("one", "four", "three", "two", "five"));
    assertEquals(expectedResult, arrayList);
}复制
```

在这里，我们交换了位置 1 和 3 中的项目，并确认列表看起来符合我们的预期。

## 4.轮换完整列表

**最后，我们还可以对列表应用旋转，将所有元素移动给定的距离。**距离没有限制。因此，如果我们愿意，我们可以将所有内容循环多次。正距离将根据我们的观点将项目向右或向下旋转列表：

```java
@Test
void givenAList_whenUsingRotateWithPositiveDistance_thenItemsMoveToTheRight() {
    ArrayList<String> arrayList = new ArrayList<>(Arrays.asList("one", "two", "three", "four", "five"));

    Collections.rotate(arrayList, 2);

    ArrayList<String> expectedResult = new ArrayList<>(Arrays.asList("four", "five", "one", "two", "three"));
    assertEquals(expectedResult, arrayList);
}复制
```

这里的项目都向右移动了两个位置，一旦到达终点就循环回到起点。或者，如果需要，我们也可以向左旋转负距离：

```java
@Test
void givenAList_whenUsingRotateWithNegativeDistance_thenItemsMoveToTheLeft() {
    ArrayList<String> arrayList = new ArrayList<>(Arrays.asList("one", "two", "three", "four", "five"));

    Collections.rotate(arrayList, -2);

    ArrayList<String> expectedResult = new ArrayList<>(Arrays.asList("three", "four", "five", "one", "two"));
    assertEquals(expectedResult, arrayList);
}复制
```

## 5.结论

*在本文中，我们了解了 Java 为重新排序ArrayList*提供的三个选项。出于性能原因，我们应该尽可能使用*swap()*或*rotate() 。*如果我们需要更多控制或者如果只有一个项目在移动，那么我们学习了如何使用*remove()*和*add()*手动将项目移动到我们需要的任何位置。