## 1. 概述

在本教程中，我们将提供 [Functional Java](http://www.functionaljava.org/)库的快速概述以及一些示例。

## 2. 函数式Java库

FunctionalJava库是一个开源库，旨在促进Java中的函数式编程。[该库提供了许多在函数](https://www.baeldung.com/cs/functional-programming)式编程中常用的基本和高级编程抽象。

该库的大部分功能都围绕着F接口。此F接口对接受类型A的输入并返回类型B的输出的函数建模。所有这些都建立在Java自己的类型系统之上。

## 3.Maven依赖

首先，我们需要将所需的[依赖](https://search.maven.org/search?q=g:org.functionaljava)项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.functionaljava</groupId>
    <artifactId>functionaljava</artifactId>
    <version>4.8.1</version>
</dependency>
<dependency>
    <groupId>org.functionaljava</groupId>
    <artifactId>functionaljava-java8</artifactId>
    <version>4.8.1</version>
</dependency>
<dependency>
    <groupId>org.functionaljava</groupId>
    <artifactId>functionaljava-quickcheck</artifactId>
    <version>4.8.1</version>
</dependency>
<dependency>
    <groupId>org.functionaljava</groupId>
    <artifactId>functionaljava-java-core</artifactId>
    <version>4.8.1</version>
</dependency>
```

## 4. 定义函数

让我们首先创建一个函数，稍后我们可以在我们的示例中使用它。

如果没有 Functional Java，基本的乘法方法将类似于：

```java
public static final Integer timesTwoRegular(Integer i) {
    return i  2;
}
```

使用 FunctionalJava库，我们可以更优雅地定义此功能：

```java
public static final F<Integer, Integer> timesTwo = i -> i  2;
```

上面，我们看到了一个F接口的例子，它接受一个Integer作为输入并返回该Integer乘以 2 作为它的输出。

下面是另一个将Integer作为输入的基本函数示例，但在这种情况下，返回一个布尔值以指示输入是偶数还是奇数：

```java
public static final F<Integer, Boolean> isEven = i -> i % 2 == 0;
```

## 5.应用函数

现在我们已经有了我们的函数，让我们将它们应用到数据集。

FunctionalJava库提供了一组常用的类型来管理列表、集合、数组和映射等数据。要认识到的关键是这些数据类型是不可变的。

此外，该库还提供了 方便的函数，可以在需要时与标准JavaCollections 类进行相互转换。

在下面的示例中，我们将定义一个整数列表并将我们的timesTwo函数应用于它。我们还将使用同一函数的内联定义来调用map 。当然，我们希望结果是一样的：

```java
public void multiplyNumbers_givenIntList_returnTrue() {
    List<Integer> fList = List.list(1, 2, 3, 4);
    List<Integer> fList1 = fList.map(timesTwo);
    List<Integer> fList2 = fList.map(i -> i  2);

    assertTrue(fList1.equals(fList2));
}
```

正如我们所见， map返回一个相同大小的列表，其中每个元素的值都是应用了函数的输入列表的值。输入列表本身不会改变。

这是一个使用我们的isEven函数的类似示例：

```java
public void calculateEvenNumbers_givenIntList_returnTrue() {
    List<Integer> fList = List.list(3, 4, 5, 6);
    List<Boolean> evenList = fList.map(isEven);
    List<Boolean> evenListTrueResult = List.list(false, true, false, true);

    assertTrue(evenList.equals(evenListTrueResult));
}
```

由于map方法返回一个列表，我们可以对其输出应用另一个函数。 我们调用地图函数的顺序改变了我们的结果输出：

```java
public void applyMultipleFunctions_givenIntList_returnFalse() {
    List<Integer> fList = List.list(1, 2, 3, 4);
    List<Integer> fList1 = fList.map(timesTwo).map(plusOne);
    List<Integer> fList2 = fList.map(plusOne).map(timesTwo);

    assertFalse(fList1.equals(fList2));
}
```

上述列表的输出将是：

```java
List(3,5,7,9)
List(4,6,8,10)
```

## 6. 使用函数过滤

函数式编程中另一个经常使用的操作是获取输入并根据某些条件过滤掉数据。正如可能已经猜到的那样，这些过滤条件以函数的形式提供。此函数将需要返回一个布尔值以指示数据是否需要包含在输出中。

现在，让我们使用isEven函数使用filter方法从输入数组中过滤掉奇数：

```java
public void filterList_givenIntList_returnResult() {
    Array<Integer> array = Array.array(3, 4, 5, 6);
    Array<Integer> filteredArray = array.filter(isEven);
    Array<Integer> result = Array.array(4, 6);

    assertTrue(filteredArray.equals(result));
}
```

一个有趣的观察是，在此示例中，我们使用数组而不是前面示例中使用的列表 ，并且我们的函数运行良好。由于函数被抽象和执行的方式，它们不需要知道使用什么方法来收集输入和输出。

在这个例子中，我们也使用了自己的isEven函数，但是 FunctionalJava自己的Integer 类也有用于[基本数值比较](http://www.functionaljava.org/javadoc/4.8.1/functionaljava/fj/function/Integers.html)的标准函数。

## 7. 使用函数应用布尔逻辑

在函数式编程中，我们经常使用诸如“仅当所有元素都满足某个条件时才执行此操作”或“仅当至少一个元素满足某个条件时才执行此操作”之类的逻辑。

FunctionalJava库通过[exists](http://www.functionaljava.org/javadoc/4.4/functionaljava/fj/data/Option.html#exists-fj.F-) 和[forall](http://www.functionaljava.org/javadoc/4.4/functionaljava/fj/data/Option.html#forall-fj.F-)方法为我们提供了此逻辑的快捷方式：

```java
public void checkForLowerCase_givenStringArray_returnResult() {
    Array<String> array = Array.array("Welcome", "To", "baeldung");
    assertTrue(array.exists(s -> List.fromString(s).forall(Characters.isLowerCase)));

    Array<String> array2 = Array.array("Welcome", "To", "Baeldung");
    assertFalse(array2.exists(s -> List.fromString(s).forall(Characters.isLowerCase)));

    assertFalse(array.forall(s -> List.fromString(s).forall(Characters.isLowerCase)));
}
```

在上面的示例中，我们使用了一个字符串数组作为输入。调用 fromString 函数会将数组中的每个字符串转换为字符列表。对于这些列表中的每一个，我们都应用了forall(Characters.isLowerCase)。

正如可能猜到的那样，Characters.isLowerCase是一个函数，如果字符是小写则返回 true。因此，将forall(Characters.isLowerCase)应用于字符列表只会在整个列表由小写字符组成时返回true，这反过来表明原始字符串全部为小写。

在前两个测试中，我们使用exists因为我们只想知道是否至少有一个字符串是小写的。第三个测试使用forall来验证是否所有的字符串都是小写的。

## 8. 使用函数处理可选值

在代码中处理可选值通常需要== null或isNotBlank检查。Java 8 现在提供了Optional类来更优雅地处理这些检查，而 FunctionalJava库提供了一个类似的结构来通过其[Option](http://www.functionaljava.org/javadoc/4.8.1/functionaljava/fj/data/Option.html)类优雅地处理丢失的数据：

```java
public void checkOptions_givenOptions_returnResult() {
    Option<Integer> n1 = Option.some(1);
    Option<Integer> n2 = Option.some(2);
    Option<Integer> n3 = Option.none();

    F<Integer, Option<Integer>> function = i -> i % 2 == 0 ? Option.some(i + 100) : Option.none();

    Option<Integer> result1 = n1.bind(function);
    Option<Integer> result2 = n2.bind(function);
    Option<Integer> result3 = n3.bind(function);

    assertEquals(Option.none(), result1);
    assertEquals(Option.some(102), result2);
    assertEquals(Option.none(), result3);
}
```

## 9. 使用函数约简集合

最后，我们将研究减少集合的功能。“减少一组”是“将其汇总为一个值”的一种奇特说法。

FunctionalJava库将此功能称为折叠。

需要指定一个函数来表示折叠元素是什么意思。这方面的一个例子是 Integers.add 函数，用于显示需要添加的数组或列表中的整数。

根据函数在折叠时的作用，结果可能会有所不同，具体取决于是从右侧还是左侧开始折叠。这就是 FunctionalJava库提供两个版本的原因：

```java
public void foldLeft_givenArray_returnResult() {
    Array<Integer> intArray = Array.array(17, 44, 67, 2, 22, 80, 1, 27);

    int sumAll = intArray.foldLeft(Integers.add, 0);
    assertEquals(260, sumAll);

    int sumEven = intArray.filter(isEven).foldLeft(Integers.add, 0);
    assertEquals(148, sumEven);
}
```

第一个foldLeft只是将所有整数相加。而第二个将首先应用过滤器，然后添加剩余的整数。

## 10.总结

本文只是对 FunctionalJava库的简短介绍。