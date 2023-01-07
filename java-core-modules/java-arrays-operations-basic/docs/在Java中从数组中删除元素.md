## 1. 概述

在本快速教程中，我们将学习使用[Apache Commons Lang库](https://commons.apache.org/proper/commons-lang/)从Java中的数组中删除元素的各种方法。

## 2.专家

让我们将 commons-lang3 依赖项添加[到项目的](https://search.maven.org/search?q=g:org.apache.commons AND a:commons-lang3)pom.xml文件中：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

## 3.删除元素

在我们开始之前，让我们看看当我们从数组中删除一个元素而不使用Apache Commons Lang 库中的ArrayUtils类时会发生什么。

给定下面的数组，让我们删除索引 2 处的元素：

[![数组简单](https://www.baeldung.com/wp-content/uploads/2019/07/Array-simple-300x75.png)](https://www.baeldung.com/wp-content/uploads/2019/07/Array-simple-300x75.png)

一个简单的方法是将存储在索引 2 的值替换为存储在索引 3 的值，直到我们到达数组的末尾：

[![数组 rm](https://www.baeldung.com/wp-content/uploads/2019/07/Array-rm-e1561592406558.png)](https://www.baeldung.com/wp-content/uploads/2019/07/Array-rm-e1561592406558.png)

请注意，通过以上述方式删除元素，数组的大小将保持不变，并且存储在最后一个索引处的值将为空。由于数组在初始化期间分配了固定的内存大小，因此删除元素不会调整数组的大小。

现在让我们看看使用Apache Commons Lang 的ArrayUtils类的remove方法删除元素时的数组表示：

[![数组 rm 公共](https://www.baeldung.com/wp-content/uploads/2019/07/Array-rm-common-e1561592423646.png)](https://www.baeldung.com/wp-content/uploads/2019/07/Array-rm-common-e1561592423646.png)

我们可以看到，这里的数组大小在移除元素后调整为5。remove方法创建一个全新的数组并除要删除的值之外的所有值。

ArrayUtils类提供了两种从数组中删除元素的方法。接下来我们看看这些。

## 4. 使用索引作为输入

我们可以删除元素的第一种方法是使用ArrayUtils#remove通过其索引：

```java
public int[] removeAnElementWithAGivenIndex(int[] array, int index) {
      return ArrayUtils.remove(array, index);
}
```

另一个变体是removeAll方法，我们可以使用它从数组中删除多个元素，给定它们的索引：

```java
public int[] removeAllElementsWithGivenIndices(int[] array, int... indices) {
    return ArrayUtils.removeAll(array, indices);
}
```

## 5. 使用元素作为输入

或者，假设我们不知道要删除的内容的索引。在这种情况下，我们可以使用ArrayUtils#removeElement提供要删除的元素 ：

```java
public int[] removeFirstOccurrenceOfGivenElement(int[] array, int element) {
    return ArrayUtils.removeElement(array, element);
}
```

这是此方法ArrayUtils#removeElements的另一种有用的变体，以防我们要删除的元素不止一个：

```java
public int[] removeAllGivenElements(int[] array, int... elements) {
    return ArrayUtils.removeElements(array, elements);
}
```

有时，我们想要删除所有出现的给定元素。我们可以使用ArrayUtils#removeAllOccurences来做到这一点：

```java
public int[] removeAllOccurrencesOfAGivenElement(int[] array, int element) {
    return ArrayUtils.removeAllOccurences(array, element);
}
```

## 六，总结

在本文中，我们研究了使用[Apache Commons Lang](https://commons.apache.org/proper/commons-lang/)库从数组中删除一个或多个元素的各种方法。