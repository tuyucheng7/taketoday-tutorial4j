## 1. 概述

在本快速教程中，我们将讨论在使用大多数List实现的 某些 API 时可能发生的常见异常– UnsupportedOperationException。

java.util.List比普通的rray 可以支持的功能更多。例如，仅通过一个内置方法调用，就可以检查特定元素是否在结构内部。这通常就是为什么我们有时需要将数组转换为List或Collection的原因。

有关核心JavaList实现(ArrayList )的介绍，请参阅[本文](https://www.baeldung.com/java-arraylist)。

## 2. 不支持的操作异常

发生此错误的常见方式是当我们使用 java.util.Arrays 中的asList()方法时 ：

```java
public static List asList(T... a)
```

它返回：

-   根据给定数组的大小固定大小的列表
-   与原始数组中的元素类型相同的元素， 它必须是一个对象
-    与原始数组中的元素顺序相同
-   可序列化并实现 [RandomAccess的列表](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/RandomAccess.html)

由于 T 是一个[可变参数](https://www.baeldung.com/java-varargs)，我们可以直接传递一个数组或项目作为参数，该方法将创建一个固定大小的初始化列表：

```java
List<String> flowers = Arrays.asList("Ageratum", "Allium", "Poppy", "Catmint");
```

我们也可以传递一个实际的 数组：

```java
String[] flowers = { "Ageratum", "Allium", "Poppy", "Catmint" };
List<String> flowerList = Arrays.asList(flowers);
```

由于返回的List是一个固定大小的List，我们不能添加/删除元素。

尝试添加更多元素会导致UnsupportedOperationException：

```java
String[] flowers = { "Ageratum", "Allium", "Poppy", "Catmint" }; 
List<String> flowerList = Arrays.asList(flowers); 
flowerList.add("Celosia");
```

此异常的根源是返回的对象未实现 add() 操作，因为它与 java.util.ArrayList 不同。

它是一个 ArrayList，来自 java.util.Arrays。

获得相同异常的另一种方法是尝试从获得的列表中删除一个元素。

另一方面，有一些方法可以 在我们需要的时候获得一个可变的列表。

其中之一是直接根据asList()的结果创建ArrayList或任何类型的列表：

```java
String[] flowers = { "Ageratum", "Allium", "Poppy", "Catmint" }; 
List<String> flowerList = new ArrayList<>(Arrays.asList(flowers));
```

## 3.总结

总之，重要的是要了解向列表中添加更多元素不仅会对不可变列表造成问题。