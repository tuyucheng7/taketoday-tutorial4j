## 1. 概述

[运算符](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/opsummary.html)在Java语言中用于对数据和变量进行操作。

在本教程中，我们将探讨按位运算符及其在Java中的工作方式。

## 2. 位运算符

按位运算符处理[二进制数字](https://medium.com/coderscorner/number-systems-decimal-binary-octal-and-hexadecimal-5e567e55ab28)或输入值的位。我们可以将这些应用于整数类型-long、int、short、char和byte。

在探索不同的位运算符之前，让我们先了解它们的工作原理。

按位运算符处理十进制数的二进制等价物，并根据给定的运算符逐位对它们执行运算：

- 首先，操作数被转换为它们的二进制表示
- 接下来，将运算符应用于每个二进制数并计算结果
- 最后，将结果转换回其十进制表示形式

让我们通过一个例子来理解；让我们取两个整数：

```java
int value1 = 6;
int value2 = 5;
```

接下来，让我们对这些数字应用按位或运算符：

```java
int result = 6 | 5;
```

要执行此操作，首先将计算这些数字的二进制表示形式：

```java
Binary number of value1 = 0110
Binary number of value2 = 0101
```

然后操作将应用于每个位。结果返回一个新的二进制数：

```java
0110
0101
-----
0111
```

最后，结果0111将被转换回等于7的十进制：

```java
result : 7
```

按位运算符进一步分为按位逻辑运算符和按位移位运算符。现在让我们来看看每种类型。

## 3. 按位逻辑运算符

按位逻辑运算符是AND(&)、OR(|)、XOR(^)和NOT(~)。

### 3.1 按位或(|)

OR运算符比较两个整数的每个二进制数字，如果其中一个为1，则返回1。

这类似于||与布尔值一起使用的逻辑运算符。当比较两个布尔值时，如果其中一个为真，则结果为真。同样，当其中一个为1时，输出为1。

我们在上一节中看到了此运算符的示例：

```java
@Test
public void givenTwoIntegers_whenOrOperator_thenNewDecimalNumber() {
    int value1 = 6;
    int value2 = 5;
    int result = value1 | value2;
    assertEquals(7, result);
}
```

让我们看看这个操作的二进制表示：

```java
0110
0101
-----
0111
```

在这里，我们可以看到使用OR，0和0将得到0，而任何至少包含1的组合将得到1。

### 3.2 按位与(&)

AND运算符比较两个整数的每个二进制数字，如果都为1，则返回1，否则返回0。

这类似于带有布尔值的&&运算符。当两个布尔值都为真时，&&操作的结果为真。

让我们使用与上面相同的示例，除了现在使用&运算符而不是|操作员：

```java
@Test
public void givenTwoIntegers_whenAndOperator_thenNewDecimalNumber() {
    int value1 = 6;
    int value2 = 5;
    int result = value1 & value2;
    assertEquals(4, result);
}
```

让我们也看看这个操作的二进制表示：

```java
0110
0101
-----
0100
```

0100是十进制的4，因此，结果是：

```java
result : 4
```

### 3.3 按位异或(^)

XOR运算符比较两个整数的每个二进制数字，如果比较的两个位不同则返回1。这意味着如果两个整数的位都是1或0，则结果将为0；否则否则，结果将为1：

```java
@Test
public void givenTwoIntegers_whenXorOperator_thenNewDecimalNumber() {
    int value1 = 6;
    int value2 = 5;
    int result = value1 ^ value2;
    assertEquals(3, result);
}
```

和二进制表示：

```java
0110
0101
-----
0011
```

0011十进制为3，因此，结果为：

```java
result : 3
```

### 3.4 按位补码(~)

按位取反或补码运算符仅表示对输入值的每一位求反。它只需要一个整数，它等同于!操作员。

此运算符更改整数的每个二进制位，这意味着所有0变为1，所有1变为0。运算符对布尔值的工作方式类似：它将布尔值从true反转为false，反之亦然。

现在让我们通过一个例子来了解如何找到十进制数的补码。

让我们做value1=6的补码：

```java
@Test
public void givenOneInteger_whenNotOperator_thenNewDecimalNumber() {
    int value1 = 6;
    int result = ~value1;
    assertEquals(-7, result);
}
```

二进制值是：

```java
value1 = 0000 0110
```

通过应用补码运算符，结果将是：

```shell
0000 0110 -> 1111 1001
```

这是十进制数6的补码。由于第一个(最左边的)位在二进制中为1，这意味着存储的数字的符号为负。

现在，由于数字存储为2的补码，首先我们需要找到它的2的补码，然后将所得二进制数转换为十进制数：

```shell
1111 1001 -> 0000 0110 + 1 -> 0000 0111
```

最后，00000111是十进制的7。由于如上所述符号位为1，因此得到的答案是：

```java
result : -7
```

### 3.5 按位运算符表

让我们在比较表中总结到目前为止我们看到的运算符的结果：

```shell
A	B	A|B	A&B	A^B	~A
0	0	0	0	0	1
1	0	1	0	1	0
0	1	1	0	1	1
1	1	1	1	0	0
```

## 4. 移位运算符

二进制移位运算符根据移位运算符将输入值的所有位向左或向右移动。

让我们看看这些运算符的语法：

```java
value <operator> <number_of_times>
```

表达式的左侧是移位的整数，表达式的右侧表示必须移位的次数。

按位移位运算符进一步分为按位左移和按位右移运算符。

### 4.1 有符号左移[<<]

左移运算符将位向左移动操作数右侧指定的次数。左移后，右边的空位用0补上。

另一个需要注意的重点是，将一个数字移位1相当于将其乘以2，或者通常，将数字左移n个位置相当于乘以2^n。

让我们将值12作为输入值。

现在，我们将它向左移动2个位置(12<<2)，看看最终结果是什么。

12的二进制等价为00001100，左移2位后为00110000，相当于十进制的48：

```java
@Test
public void givenOnePositiveInteger_whenLeftShiftOperator_thenNewDecimalNumber() {
    int value = 12;
    int leftShift = value << 2;
    assertEquals(48, leftShift);
}
```

对于负值，这同样适用：

```java
@Test
public void givenOneNegativeInteger_whenLeftShiftOperator_thenNewDecimalNumber() {
    int value = -12;
    int leftShift = value << 2;
    assertEquals(-48, leftShift);
}
```

### 4.2 有符号右移[>>]

右移运算符将所有位右移。根据输入的数字填充左侧的空白区域：

- 当输入数字为负数时，最左边的位为1，则空位将用1填充
- 当输入数字为正数时，最左边的位为0，则空位将用0填充

让我们继续使用12作为输入的示例。

现在，我们将它向右移动2个位置(12>>2)，看看最终结果是什么。

输入的数是正数，所以右移2位后结果为0011，十进制为3：

```java
@Test
public void givenOnePositiveInteger_whenSignedRightShiftOperator_thenNewDecimalNumber() {
    int value = 12;
    int rightShift = value >> 2;
    assertEquals(3, rightShift);
}
```

另外，对于负值：

```java
@Test
public void givenOneNegativeInteger_whenSignedRightShiftOperator_thenNewDecimalNumber() {
    int value = -12;
    int rightShift = value >> 2;
    assertEquals(-3, rightShift);
}
```

### 4.3 无符号右移[>>>]

该运算符与带符号的右移运算符非常相似。唯一的区别是无论数字是正数还是负数，左边的空白处都用0填充。因此，结果将始终为正整数。

让我们右移相同的值12：

```java
@Test
public void givenOnePositiveInteger_whenUnsignedRightShiftOperator_thenNewDecimalNumber() {
    int value = 12;
    int unsignedRightShift = value >>> 2;
    assertEquals(3, unsignedRightShift);
}
```

现在，负值：

```java
@Test
public void givenOneNegativeInteger_whenUnsignedRightShiftOperator_thenNewDecimalNumber() {
    int value = -12;
    int unsignedRightShift = value >>> 2;
    assertEquals(1073741821, unsignedRightShift);
}
```

## 5. 位运算符和逻辑运算符的区别

我们在这里讨论的按位运算符与更常见的逻辑运算符之间存在一些差异。

首先，逻辑运算符处理布尔表达式并返回布尔值(true或false)，而位运算符处理整数值(long、int、short、char和byte)的二进制数字并返回整数。

此外，逻辑运算符始终对第一个布尔表达式求值，并且根据其结果和使用的运算符，可能会也可能不会对第二个布尔表达式求值。另一方面，按位运算符总是计算两个操作数。

最后，逻辑运算符用于根据多种条件做出决策，而位运算符则作用于位并执行逐位运算。

## 6. 用例

按位运算符的一些潜在用例是：

- 通信堆栈，其中附加到数据的标头中的各个位表示重要信息
- 在嵌入式系统中仅设置/清除/切换特定寄存器的一位而不修改其余位
- 使用XOR运算符加密数据以解决安全问题
- 在数据压缩中，通过将数据从一种表示形式转换为另一种表示形式，以减少使用的空间量

## 7. 总结

在本教程中，我们了解了按位运算符的类型以及它们与逻辑运算符的区别。我们还看到了它们的一些潜在用例。