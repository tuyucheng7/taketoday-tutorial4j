## 1. 概述

简单地说，NaN是一个数字数据类型值，代表“不是数字”。

在本快速教程中，我们将解释Java中的NaN值以及可以产生或涉及该值的各种操作。

## 2. NaN是什么？

NaN通常表示无效操作的结果。例如，尝试将零除以零就是这样一种操作。

我们还使用NaN表示不可表示的值。-1 的平方根就是这样一种情况，因为我们只能用复数来描述值 ( i )。

[IEEE 浮点运算标准 (IEEE 754)](https://en.wikipedia.org/wiki/IEEE_754)定义了NaN值。在Java中，浮点类型float和double实现了这个标准。

Java 将float和double类型的NaN常量定义为[Float](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Float.html#NaN)[ .NaN](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Float.html#NaN)和[Double.NaN](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Double.html#NaN)：

“一个包含双精度类型的非数字 (NaN) 值的常量。它相当于 Double.longBitsToDouble(0x7ff8000000000000L) 返回的值。”

和：

“一个常量，包含一个浮点类型的非数字 (NaN) 值。它相当于 Float.intBitsToFloat(0x7fc00000) 返回的值。”

对于Java中的其他数字数据类型，我们没有这种类型的常量。

## 3. 与NaN的比较

在Java中编写方法时，我们应该检查输入是否有效并在预期范围内。在大多数情况下， NaN值不是有效输入。因此，我们应该验证输入值不是NaN值，并对这些输入值进行适当的处理。

NaN不能与任何浮点类型值进行比较。这意味着对于涉及NaN的所有比较操作，我们都会得到false(除了“!=”，我们得到true)。

当且仅当x为NaN时，“ x != x”为真：

```java
System.out.println("NaN == 1 = " + (NAN == 1));
System.out.println("NaN > 1 = " + (NAN > 1));
System.out.println("NaN < 1 = " + (NAN < 1));
System.out.println("NaN != 1 = " + (NAN != 1));
System.out.println("NaN == NaN = " + (NAN == NAN));
System.out.println("NaN > NaN = " + (NAN > NAN));
System.out.println("NaN < NaN = " + (NAN < NAN));
System.out.println("NaN != NaN = " + (NAN != NAN));

```

让我们看看上面代码的运行结果：

```plaintext
NaN == 1 = false
NaN > 1 = false
NaN < 1 = false
NaN != 1 = true
NaN == NaN = false
NaN > NaN = false
NaN < NaN = false
NaN != NaN = true

```

因此，我们不能通过使用“==”或“!=”与NaN进行比较来检查NaN 。事实上，我们应该很少使用float或double类型的“==”或“!=”运算符。

相反，我们可以使用表达式“ x ! = x” 。此表达式仅对NAN 返回 true。

我们还可以使用Float.isNaN和Double.isNaN方法来检查这些值。这是首选方法，因为它更具可读性和易懂性：

```java
double x = 1;
System.out.println(x + " is NaN = " + (x != x));
System.out.println(x + " is NaN = " + (Double.isNaN(x)));
        
x = Double.NaN;
System.out.println(x + " is NaN = " + (x != x));
System.out.println(x + " is NaN = " + (Double.isNaN(x)));

```

运行这段代码我们会得到如下结果：

```plaintext
1.0 is NaN = false
1.0 is NaN = false
NaN is NaN = true
NaN is NaN = true
```

## 4. 产生NaN 的操作

在进行涉及float和double类型的操作时，我们需要注意NaN值。

一些浮点方法和操作产生NaN值而不是抛出异常。我们可能需要显式处理此类结果。

导致非数字值的常见情况是数学上未定义的数字运算：

```java
double ZERO = 0;
System.out.println("ZERO / ZERO = " + (ZERO / ZERO));
System.out.println("INFINITY - INFINITY = " + 
  (Double.POSITIVE_INFINITY - Double.POSITIVE_INFINITY));
System.out.println("INFINITY  ZERO = " + (Double.POSITIVE_INFINITY  ZERO));

```

这些示例产生以下输出：

```plaintext
ZERO / ZERO = NaN
INFINITY - INFINITY = NaN
INFINITY  ZERO = NaN

```

没有实数结果的数值运算也会产生NaN：

```java
System.out.println("SQUARE ROOT OF -1 = " + Math.sqrt(-1));
System.out.println("LOG OF -1 = " +  Math.log(-1));

```

这些语句将导致：

```plaintext
SQUARE ROOT OF -1 = NaN
LOG OF -1 = NaN

```

所有以NaN作为操作数的数值运算都会产生NaN作为结果：

```java
System.out.println("2 + NaN = " +  (2 + Double.NaN));
System.out.println("2 - NaN = " +  (2 - Double.NaN));
System.out.println("2  NaN = " +  (2  Double.NaN));
System.out.println("2 / NaN = " +  (2 / Double.NaN));

```

上面的结果是：

```plaintext
2 + NaN = NaN
2 - NaN = NaN
2  NaN = NaN
2 / NaN = NaN

```

最后，我们不能将null赋值给double或float类型的变量。相反，我们可以显式地将NaN分配给此类变量以指示缺失值或未知值：

```java
double maxValue = Double.NaN;
```

## 5.总结

在本文中，我们讨论了NaN以及涉及它的各种操作。我们还讨论了在Java中显式进行浮点计算时处理NaN的需要。