## 1. 概述

在本快速教程中，我们将了解Java XOR运算符。我们将讨论一些关于XOR操作的理论，然后我们将了解如何在Java中实现它们。

## 2. 异或运算符

让我们首先提醒一下XOR操作的语义。XOR逻辑运算，异或，采用两个布尔操作数，当且仅当操作数不同时才返回true。相反，如果两个操作数具有相同的值，则返回false。

因此，例如，当我们必须检查两个不能同时为真的条件时，可以使用XOR运算符。

让我们考虑两个条件，A和B。下表显示了A XOR B的可能值：

[![异或运算符表](https://www.baeldung.com/wp-content/uploads/2019/08/xor_operator_table-1-1024x299.png)](https://www.baeldung.com/wp-content/uploads/2019/08/xor_operator_table-1.png)

A XOR B运算等效于(A AND !B)OR(!A AND B)。为清楚起见包含了括号，但括号是可选的，因为AND运算符优先于OR运算符。

## 3. 如何用Java实现？

现在让我们看看如何在Java中表达异或运算。当然，我们可以选择使用&&和||运算符，但这可能有点冗长，正如我们将要看到的那样。

想象一个具有两个布尔属性的Car类：diesel和manual。现在假设我们想判断这辆车是柴油车还是手动车，但不是两者都是。

让我们使用&&和||检查一下运算符：

```java
Car car = Car.dieselAndManualCar();
boolean dieselXorManual = (car.isDiesel() && !car.isManual()) || (!car.isDiesel() && car.isManual());
```

这有点长，特别是考虑到我们有一个替代方案，即由^符号表示的JavaXOR运算符。它是一个[按位运算符](https://www.baeldung.com/java-bitwise-operators)，这意味着它是一个运算符，比较两个值的匹配位以返回结果。在XOR的情况下，如果相同位置的两个位具有相同的值，则结果位将为0。否则，它将为1。

因此，我们可以直接使用^运算符，而不是我们繁琐的XOR实现：

```java
Car car = Car.dieselAndManualCar();
boolean dieselXorManual = car.isDiesel() ^ car.isManual();
```

正如我们所看到的，^运算符使我们能够更简洁地表达XOR运算。

最后，值得一提的是，与其他按位运算符一样，XOR运算符适用于所有基本类型。例如，让我们考虑两个整数1和3，它们的二进制表示分别为00000001和000000011。在它们之间使用XOR运算符将得到整数2：

```java
assertThat(1 ^ 3).isEqualTo(2);
```

这两个数字只有第二位不同；因此，该位的XOR运算符的结果将为1。所有其他位都相同，因此它们的按位XOR结果为0，最终值为00000010，即整数2的二进制表示。

## 4. 总结

在本文中，我们了解了Java XOR运算符。我们演示了它如何提供一种简洁的方式来表达XOR运算。