## 1. 概述

当我们要在Java中进行十进制数计算时，可以考虑使用[BigDecimal](https://www.baeldung.com/java-bigdecimal-biginteger)类。

在这个简短的教程中，我们将探讨如何检查BigDecimal对象的值是否为零。

## 二、问题简介

这个问题其实很简单。假设我们有一个非空的BigDecimal对象。我们想知道它的值是否等于零。

眼尖的人可能已经意识到“其值是否 为零”这一需求已经暗示了解决方案：使用[equals()](https://www.baeldung.com/java-equals-method-operator-difference)方法。此外， BigDecimal类提供了一个方便的ZERO常量对象来指示零值。

事实上，这个问题听起来很简单。我们可以简单地检查BigDecimal.ZERO.equals(givenBdNumber)来决定givenBdNumber对象的值是否为零。但是，如果我们不知道BigDeicmal的比较复杂性，我们可能会掉入一个常见的陷阱。

接下来，让我们仔细看看它并解决它的正确方法。

## 3. BigDecimal比较的常见陷阱：使用equals 方法

首先，让我们创建一个值为零的BigDecimal对象：

```java
BigDecimal BD1 = new BigDecimal("0");
```

现在，让我们使用equals方法检查BD1的值是否为零 。为简单起见，让我们在单元测试方法中执行此操作：

```java
assertThat(BigDecimal.ZERO.equals(BD1)).isTrue();
```

如果我们运行测试，它就会通过。到目前为止，一切都很好。我们可能认为这是解决方案。接下来，让我们创建另一个BigDecimal对象：

```java
BigDecimal BD2 = new BigDecimal("0.0000");
```

显然，BD2对象的值为零，尽管我们是用一个标度为四的字符串构造它的。众所周知，0.0000在数值上等同于0。

现在，让我们再次使用equals方法测试 BD2 ：

```java
assertThat(BigDecimal.ZERO.equals(BD2)).isTrue();
```

这一次，如果我们运行该方法，令人惊讶的是，测试将失败。

这是因为BigDecimal对象具有 value 和 scale 属性。此外，只有当两个BigDecimal对象的 value 和 scale 都相等时， equals 方法才认为它们相等。也就是说，如果我们将它们与equals进行比较， BigDecimal 42 不等于 42.0 。

另一方面，BigDecimal.ZERO常量的值为零，小数位数也为零。因此，当我们检查“ 0 等于 0.0000 ”时，equals方法返回false。

因此，我们需要找到一种只比较两个BigDecimal对象的值而忽略它们的比例的方法。

接下来，让我们看看解决问题的几种方法。

## 4. 使用compareTo方法

BigDecimal类实现了[Comparable](https://www.baeldung.com/java-comparator-comparable)接口。因此，我们可以使用compareTo方法来比较两个BigDecimal对象的值。

此外，compareTo方法的 Javadoc 明确指出：

>   此方法认为值相等但比例不同(如 2.0 和 2.00)的两个BigDecimal对象相等。

因此，我们可以检查BigDecimal.ZERO.compareTo(givenBdNumber) == 0 来确定 givenBdNumber 的值为零。

接下来，让我们测试此方法是否可以正确判断BigDecimal对象BD1和BD2是否为零：

```java
assertThat(BigDecimal.ZERO.compareTo(BD1)).isSameAs(0);
assertThat(BigDecimal.ZERO.compareTo(BD2)).isSameAs(0);
```

当我们运行测试时，它通过了。所以，我们已经使用compareTo方法解决了这个问题。

## 5.使用signum方法

BigDeicmal类提供了[signum](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/math/BigDecimal.html#signum())方法来判断给定的BigDecimal对象的值是负数 (-1)、零 (0) 还是正数 (1)。signum方法将 忽略 scale 属性。

因此，我们可以通过检查(givenBdNumber.signum() == 0)来解决问题。

同样，让我们编写一个测试来验证这种方法是否适用于这两个示例：

```java
assertThat(BD1.signum()).isSameAs(0);
assertThat(BD2.signum()).isSameAs(0);
```

如果我们运行上面的测试，它就会通过。

## 六，总结

在这篇简短的文章中，我们介绍了两种检查BigDecimal对象的值是否为零的正确方法：compareTo方法或signum方法。