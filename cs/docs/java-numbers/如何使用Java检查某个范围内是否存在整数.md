## 1. 概述

在本教程中，我们将了解一些检查给定范围内是否存在整数的方法。我们将使用运算符和几个实用程序类来做到这一点。

## 2.范围类型

在我们使用任何这些方法之前，我们必须清楚我们在谈论什么[样的范围。](https://en.wikipedia.org/wiki/Interval_(mathematics)#Terminology)在本教程中，我们将重点关注这四种有界范围类型：

-   封闭范围——包括其下限和上限
-   开放范围——排除其下限和上限
-   左开右闭范围——包括其上限并排除其下限
-   左闭右开范围——包括其下限并排除其上限

例如，假设我们想知道整数 20 是否出现在这两个范围内：R1 = [10, 2o)，左闭右开范围，R2 = (10, 20]，左开右开范围封闭范围。由于R1不包含其上限，因此整数 20 仅存在于R2中。

## 3. 使用<和<=运算符

我们的目标是确定一个数字是否在给定的下限和上限之间。我们将从使用基本Java运算符检查这一点开始。

让我们定义一个类来检查所有四种范围：

```java
public class IntRangeOperators {

    public static boolean isInClosedRange(Integer number, Integer lowerBound, Integer upperBound) {
        return (lowerBound <= number && number <= upperBound);
    }

    public static boolean isInOpenRange(Integer number, Integer lowerBound, Integer upperBound) {
        return (lowerBound < number && number < upperBound);
    }

    public static boolean isInOpenClosedRange(Integer number, Integer lowerBound, Integer upperBound) {
        return (lowerBound < number && number <= upperBound);
    }

    public static boolean isInClosedOpenRange(Integer number, Integer lowerBound, Integer upperBound) {
        return (lowerBound <= number && number < upperBound);
    }
}
```

在这里，通过更改运算符以包含或排除边界，我们可以将区间调整为开放、封闭或半开放。

让我们测试我们的静态isInOpenClosedRange()方法。我们将通过传入 10 作为下限和 20 作为上限来指定左开右闭范围(10,20]) ：

```java
assertTrue(IntRangeClassic.isInOpenClosedRange(20, 10, 20));

assertFalse(IntRangeClassic.isInOpenClosedRange(10, 10, 20));
```

在我们的第一个测试中，我们成功地验证了整数 20 存在于(10,20]范围内，包括其上限。然后我们确认整数 10 不存在于同一范围内，不包括其下限。

## 4. 使用范围类

作为使用Java运算符的替代方法，我们还可以使用表示范围的实用程序类。使用预定义类的主要好处是范围类为上述部分或所有范围类型提供开箱即用的实现。

此外，我们可以使用定义的边界配置范围对象，并在其他方法或类中重用该对象。通过一次定义范围，如果我们需要对整个代码库中的同一范围进行多次检查，我们的代码就不容易出错。

另一方面，我们将在下面看到的两个范围类位于外部库中，在我们使用它们之前必须将它们导入到我们的项目中。

### 4.1. 使用 java.time.temporal.ValueRange

不需要导入外部库的范围类是java.time.temporal.ValueRange，在 JDK 1.8 中引入：

```java
public class IntRangeValueRange {

    public boolean isInClosedRange(Integer number, Integer lowerBound, Integer upperBound) {
        final ValueRange range = ValueRange.of(lowerBound, upperBound);
        return range.isValidIntValue(number);
    }

    public boolean isInOpenRange(Integer number, Integer lowerBound, Integer upperBound) {
        final ValueRange range = ValueRange.of(lowerBound + 1, upperBound - 1);
        return range.isValidIntValue(number);
    }

    public boolean isInOpenClosedRange(Integer number, Integer lowerBound, Integer upperBound) {
        final ValueRange range = ValueRange.of(lowerBound + 1, upperBound);
        return range.isValidIntValue(number);
    }

    public boolean isInClosedOpenRange(Integer number, Integer lowerBound, Integer upperBound) {
        final ValueRange range = ValueRange.of(lowerBound, upperBound - 1);
        return range.isValidIntValue(number);
    }
}

```

正如我们在上面看到的，我们通过将 lowerBound和 upperBound传递 给静态of()方法来创建ValueRange对象。然后，我们使用每个对象的isValidIntValue()方法检查每个范围内是否存在数字。

我们应该注意，ValueRange仅支持开箱即用的封闭范围检查。因此，我们必须通过递增lowerBound来验证左开范围，通过递减upperBound来验证右开范围，就像我们上面所做的那样。

### 4.2. 使用 Apache Commons

让我们继续讨论我们可以从第三方库中使用的一些范围类。首先，我们将[Apache Commons](https://search.maven.org/search?q=g:org.apache.commons a:commons-lang3)依赖项添加到我们的项目中：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

在这里，我们实现与以前相同的行为，但使用 Apache Commons Range类：

```java
public class IntRangeApacheCommons {

    public boolean isInClosedRange(Integer number, Integer lowerBound, Integer upperBound) {
        final Range<Integer> range = Range.between(lowerBound, upperBound);
        return range.contains(number);
    }

    public boolean isInOpenRange(Integer number, Integer lowerBound, Integer upperBound) {
        final Range<Integer> range = Range.between(lowerBound + 1, upperBound - 1);
        return range.contains(number);
    }

    public boolean isInOpenClosedRange(Integer number, Integer lowerBound, Integer upperBound) {
        final Range<Integer> range = Range.between(lowerBound + 1, upperBound);
        return range.contains(number);
    }

    public boolean isInClosedOpenRange(Integer number, Integer lowerBound, Integer upperBound) {
        final Range<Integer> range = Range.between(lowerBound, upperBound - 1);
        return range.contains(number);
    }
}
```

与ValueRange的of()方法一样，我们将lowerBound和upperBound传递给 Range的静态 between()方法以创建Range对象。然后我们使用contains()方法检查每个对象的范围内是否存在数字。

Apache Commons Range类也只支持闭区间，但我们只是像调整ValueRange一样再次调整lowerBound和upperBound。

此外，作为泛型类，Range不仅可以用于Integer ，还可以用于实现Comparable 的任何其他类型。

### 4.3. 使用谷歌番石榴

最后，让我们将[Google Guava](https://search.maven.org/search?q=g:com.google.guava a:guava)依赖项添加到我们的项目中：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.1-jre</version>
</dependency>
```

我们可以使用 Guava 的Range类来重新实现与之前相同的行为：

```java
public class IntRangeGoogleGuava {

    public boolean isInClosedRange(Integer number, Integer lowerBound, Integer upperBound) {
        final Range<Integer> range = Range.closed(lowerBound, upperBound);
        return range.contains(number);
    }

    public boolean isInOpenRange(Integer number, Integer lowerBound, Integer upperBound) {
        final Range<Integer> range = Range.open(lowerBound, upperBound);
        return range.contains(number);
    }

    public boolean isInOpenClosedRange(Integer number, Integer lowerBound, Integer upperBound) {
        final Range<Integer> range = Range.openClosed(lowerBound, upperBound);
        return range.contains(number);
    }

    public boolean isInClosedOpenRange(Integer number, Integer lowerBound, Integer upperBound) {
        final Range<Integer> range = Range.closedOpen(lowerBound, upperBound);
        return range.contains(number);
    }
}
```

我们可以在上面看到 Guava 的Range类有四个单独的方法来创建我们之前讨论的每个范围类型。也就是说，与我们目前看到的其他范围类不同， Guava 的Range类原生支持开放和半开放范围。例如，要指定排除其上限的半开区间，我们将lowerBound和upperBound传递给静态closedOpen()方法。对于排除其下限的半开区间，我们使用openClosed()。然后我们使用contains()方法检查每个范围内是否存在数字。

## 5.总结

在本文中，我们学习了如何使用基本运算符和范围类来检查整数是否在给定范围内。我们还探讨了各种方法的优缺点。