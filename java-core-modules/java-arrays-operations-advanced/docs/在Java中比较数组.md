## 1. 概述

在本教程中，我们将了解在Java中比较数组的不同方法。我们将介绍常规方法，我们还将看到一些使用lambda 表达式的示例。

## 2.比较数组

我们将比较Java中的数组，正如我们所知，这些是对象。因此，让我们刷新一些基本概念：

-   对象有引用和值
-   两个相等的引用应该指向相同的值
-   两个不同的值应该有不同的引用
-   两个相等的值不一定具有相同的引用
-   原始值仅按值进行比较
-   字符串文字仅按值进行比较

### 2.1. 比较对象引用

如果我们有两个指向同一个数组的引用，我们应该总是在与==运算符的相等比较中得到true 的 结果。

让我们看一个例子：

```java
String[] planes1 = new String[] { "A320", "B738", "A321", "A319", "B77W", "B737", "A333", "A332" };
String[] planes2 = planes1;
```

首先，我们创建了一个由planes1引用的平面模型数组。然后我们创建planes2，它引用planes1。通过这样做，我们在 memory 中创建了对同一个数组的两个引用 。因此， “planes1 == planes2” 表达式将返回 true。

对于数组， equals() 方法与 == 运算符相同。因此，planes1.equals(planes2) 返回 true ，因为两个引用都指向同一个对象。一般来说， 当且仅当表达式“ array1 == array2”返回true时，array1.eqauls(array2) 才会 返回 true。

让我们断言这两个引用是否相同：

```java
assertThat(planes1).isSameAs(planes2);
```

现在让我们确保planes1引用的值实际上与planes2引用的值相同。因此，我们可以更改planes2引用的数组，并检查更改是否对planes1引用的数组有任何影响：

```java
planes2[0] = "747";
```

为了最终看到这个工作，让我们做出断言：

```java
assertThat(planes1).isSameAs(planes2);
assertThat(planes2[0]).isEqualTo("747");
assertThat(planes1[0]).isEqualTo("747");
```

通过这个单元测试，我们能够通过引用比较两个数组。

但是，我们只证明了一个引用一旦赋值给另一个引用，就会引用相同的值。

我们现在将创建两个具有相同值的不同数组：

```java
String[] planes1 = new String[] { "A320", "B738", "A321", "A319", "B77W", "B737", "A333", "A332" };
String[] planes2 = new String[] { "A320", "B738", "A321", "A319", "B77W", "B737", "A333", "A332" };
```

由于它们是不同的对象，我们可以肯定地知道它们是不一样的。因此，我们可以比较它们：

```java
assertThat(planes1).isNotSameAs(planes2);
```

总而言之，在这种情况下，我们在内存中有两个数组，它们以完全相同的顺序包含相同的字符串值。但是，不仅引用的数组内容不同，引用本身也不同。

### 2.2. 比较数组长度

可以比较数组的长度，而不管它们的元素类型，或者它们的值是否被填充。

让我们创建两个数组：

```java
final String[] planes1 = new String[] { "A320", "B738", "A321", "A319", "B77W", "B737", "A333", "A332" };
final Integer[] quantities = new Integer[] { 10, 12, 34, 45, 12, 43, 5, 2 };
```

这是两个具有不同元素类型的不同数组。在此数据集中，我们正在注册，例如，仓库中存储了每种型号的飞机数量。现在让我们对它们运行单元测试：

```java
assertThat(planes1).hasSize(8);
assertThat(quantities).hasSize(8);
```

有了这个，我们已经证明两个数组都有八个元素，并且length属性为每个数组返回正确数量的元素。

### 2.3. 比较数组与Arrays.equals

到目前为止，我们只根据对象标识比较数组。另一方面， 为了检查两个数组的内容是否相等，Java 提供了Arrays.equals静态方法。此方法将遍历数组，每个位置并行，并 为每对元素应用 == 运算符。

让我们以完全相同的顺序创建两个具有相同字符串文字的不同数组：

```java
String[] planes1 = new String[] { "A320", "B738", "A321", "A319", "B77W", "B737", "A333", "A332" };
String[] planes2 = new String[] { "A320", "B738", "A321", "A319", "B77W", "B737", "A333", "A332" };
```

现在，让我们断言它们是相等的：

```java
assertThat(Arrays.equals(planes1, planes2)).isTrue();
```

如果我们改变第二个数组的值的顺序：

```java
String[] planes1 = new String[] { "A320", "B738", "A321", "A319", "B77W", "B737", "A333", "A332" };
String[] planes2 = new String[] { "B738", "A320", "A321", "A319", "B77W", "B737", "A333", "A332" };

```

我们会得到不同的结果：

```java
assertThat(Arrays.equals(planes1, planes2)).isFalse();
```

### 2.4. 比较数组与Arrays.deepEquals

如果我们在Java中使用简单类型，则使用==运算符很容易。这些可以是基本类型或 字符串 文字。Object数组之间的比较可能更复杂。这背后的原因在我们的[Arrays.deepEquals](https://www.baeldung.com/java-arrays-deepequals) 文章中得到了充分解释。让我们看一个例子。

首先，让我们从Plane 类开始：

```java
public class Plane {
    private final String name;
    private final String model;

    // getters and setters
}
```

然后，让我们实现hashCode 和 equals方法：

```java
@Override
public boolean equals(Object o) {
    if (this == o)
        return true;
    if (o == null || getClass() != o.getClass())
        return false;
    Plane plane = (Plane) o;
    return Objects.equals(name, plane.name) && Objects.equals(model, plane.model);
}

@Override
public int hashCode() {
    return Objects.hash(name, model);
}
```

其次，让我们创建以下双元素数组：

```java
Plane[][] planes1 
  = new Plane[][] { new Plane[]{new Plane("Plane 1", "A320")}, new Plane[]{new Plane("Plane 2", "B738") }};
Plane[][] planes2 
  = new Plane[][] { new Plane[]{new Plane("Plane 1", "A320")}, new Plane[]{new Plane("Plane 2", "B738") }};

```

现在让我们看看它们是否是真实的、深度相等的数组：

```java
assertThat(Arrays.deepEquals(planes1, planes2)).isTrue();
```

为了确保我们的比较按预期进行，现在让我们更改最后一个数组的顺序：

```java
Plane[][] planes1 
  = new Plane[][] { new Plane[]{new Plane("Plane 1", "A320")}, new Plane[]{new Plane("Plane 2", "B738") }};
Plane[][] planes2 
  = new Plane[][] { new Plane[]{new Plane("Plane 2", "B738")}, new Plane[]{new Plane("Plane 1", "A320") }};
```

最后，让我们测试一下它们是否确实不再相等：

```java
assertThat(Arrays.deepEquals(planes1, planes2)).isFalse();
```

### 2.5. 比较具有不同元素顺序的数组

为了检查数组是否相等，不管元素的顺序如何，我们需要定义是什么让我们的Plane的一个实例独一无二。对于我们的案例，不同的名称或型号足以确定一个平面与另一个平面不同。我们已经通过实现hashCode和 equals 方法来建立它。这意味着在我们可以比较我们的数组之前，我们应该对它们进行排序。为此，我们需要一个[Comparator](https://www.baeldung.com/java-comparator-comparable)：

```java
Comparator<Plane> planeComparator = (o1, o2) -> {
    if (o1.getName().equals(o2.getName())) {
        return o2.getModel().compareTo(o1.getModel());
    }
    return o2.getName().compareTo(o1.getName());
};
```

在此Comparator中，我们优先考虑名称。如果名称相同，我们通过查看模型来解决歧义。我们使用String类型的 compareTo 方法来比较字符串。

无论排序顺序如何，我们都希望能够找到数组是否相等。为此，让我们现在对数组进行排序：

```java
Arrays.sort(planes1[0], planeComparator);
Arrays.sort(planes2[0], planeComparator);
```

最后，让我们测试一下：

```java
assertThat(Arrays.deepEquals(planes1, planes2)).isTrue();
```

首先按相同顺序对数组进行排序后，我们允许deepEquals方法查找这两个数组是否相等。

## 3.总结

在本教程中，我们看到了比较数组的不同方法。其次，我们看到了比较引用和值之间的区别。此外，我们还研究了如何深入比较数组。最后，我们看到了普通比较和分别使用 equals 和 deepEquals 的深度比较之间的区别。