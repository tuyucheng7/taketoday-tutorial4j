## 1. 概述

在本快速教程中，我们将研究初始化数组的不同方法，以及它们之间的细微差别。

## 延伸阅读：

## [Java 中的数组：参考指南](https://www.baeldung.com/java-arrays-guide)

理解和使用Java数组的简单而完整的参考指南。

[阅读更多](https://www.baeldung.com/java-arrays-guide)→

## [Java中的数组操作](https://www.baeldung.com/java-common-array-operations)

了解我们如何处理Java中的常见数组操作。

[阅读更多](https://www.baeldung.com/java-common-array-operations)→

## [一行中的Java列表初始化](https://www.baeldung.com/java-init-list-one-line)

在本快速教程中，我们将研究如何使用单行代码初始化 List。

[阅读更多](https://www.baeldung.com/java-init-list-one-line)→

## 2.一次一个元素

让我们从一个简单的、基于循环的方法开始：

```java
for (int i = 0; i < array.length; i++) {
    array[i] = i + 2;
}
```

我们还将看到如何一次初始化一个元素的多维数组：

```java
for (int i = 0; i < 2; i++) {
    for (int j = 0; j < 5; j++) {
        array[i][j] = j + 1;
    }
}
```

## 三、申报时

现在让我们在声明时初始化一个数组：

```java
String array[] = new String[] { 
  "Toyota", "Mercedes", "BMW", "Volkswagen", "Skoda" };
```

在实例化数组时，我们不必指定其类型：

```java
int array[] = { 1, 2, 3, 4, 5 };
```

请注意，使用此方法无法在声明后初始化数组；尝试这样做将导致编译错误。

## 4. 使用Arrays.fill()

java.util.Arrays类有几个名为fill()的方法，它们接受不同类型的参数并用相同的值填充整个数组：

```java
long array[] = new long[5];
Arrays.fill(array, 30);
```

该方法还有几个备选方案，它们将数组的范围设置为特定值：

```java
int array[] = new int[5];
Arrays.fill(array, 0, 3, -50);
```

请注意，该方法接受数组、第一个元素的索引、元素数和值。

## 5. 使用Arrays.copyOf()

Arrays.copyOf()方法通过另一个数组来创建一个新数组。该方法有许多重载，它们接受不同类型的参数。

让我们看一个简单的例子：

```java
int array[] = { 1, 2, 3, 4, 5 };
int[] copy = Arrays.copyOf(array, 5);
```

这里有几点说明：

-   该方法接受源数组和要创建的副本的长度。
-   如果长度大于要的数组的长度，那么额外的元素将使用它们的默认值进行初始化。
-   如果源数组尚未初始化，则抛出NullPointerException 。
-   最后，如果源数组长度为负，则抛出NegativeArraySizeException 。

## 6. 使用Arrays.setAll()

Arrays.setAll()方法使用生成器函数设置数组的所有元素：

```java
int[] array = new int[20];
Arrays.setAll(array, p -> p > 9 ? 0 : p);

// [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
```

如果生成器函数为 null，则抛出NullPointerException 。

## 7. 使用ArrayUtils.clone()

最后，让我们利用Apache Commons Lang 3 中的ArrayUtils.clone() API，它通过创建另一个数组的直接副本来初始化一个数组：

```java
char[] array = new char[] {'a', 'b', 'c'};
char[] copy = ArrayUtils.clone(array);
```

请注意，此方法已为所有原始类型重载。

## 八、总结

在这篇简短的文章中，我们探讨了在Java中初始化数组的不同方法。