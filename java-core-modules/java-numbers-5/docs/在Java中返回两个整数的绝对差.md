## 一、概述

在本教程中，我们将探索如何获得两个给定整数之间的绝对差。

## 2. 使用*Math.abs()*方法

问题很简单。让我们通过几个例子快速理解它：

-   *num1=3, num2=4* : *absDiff=1*
-   *num1=3, num2=-4* : *absDiff=7*
-   *num1=-3, num2=-4* : *absDiff=1*

查看上面的示例，**给定两个整数\*num1\*和\*num2，结果是\**(num1 – num2)\*的绝对值。**此外，Java 标准库提供了返回绝对值的[*Math.abs()方法。*](https://www.baeldung.com/java-lang-math#abs)因此，我们可以很容易地将计算转化为Java代码：

```java
int absDiff(int num1, int num2) {
    int result = Math.abs(num1 - num2);
    System.out.println("Absolute diff: " + result);
    return result;
}复制
```

如我们所见，*absDiff()*方法返回结果。此外，它还会打印结果。

为简单起见，让我们使用单元测试断言来验证该方法是否按预期工作：

```java
int diff1 = absDiff(100, -200);
assertEquals(300, diff1);
                               
int diff2 = absDiff(100, 200);
assertEquals(100, diff2);复制
```

当我们运行它时，测试通过了。所以，我们已经解决了这个问题。然而，这个实现中隐藏了一个潜在的问题。那么，接下来，让我们来了解一下。

## 3. 溢出/下溢问题

我们知道Java的*int*是一个带符号的32位整数。换句话说，**Java 整数的范围是\*-2147483648\*到\*2147483647\***。

现在，让我们重新审视我们的实现。我们有计算：*num1 – num2*。因此，结果可能会超过*Integer.MAX_VALUE*，例如，当我们将*Integer.MAX_VALUE* 和*-200*传递给*absDiff()*方法时。接下来，让我们用这两个整数调用方法，看看会发生什么。

首先，让我们手动计算一下，看看预期结果是什么：

```bash
Result = Integer.MAX_VALUE - (-200)
       = 2147483647 + 200
       = 2147483847复制
```

然而，如果我们调用我们的*absDiff()*方法，不会发生异常，我们会看到输出：

```bash
Absolute diff: 2147483449复制
```

显然，结果是不正确的。这是因为**发生了****整数[溢出](https://www.baeldung.com/java-overflow-underflow#overflow-underflow)**。当然，计算结果也可以小于*Integer.MIN_VALUE*，例如*absDiff(Integer.MIN_VALUE, 200)。*在这种情况下，我们称之为下溢。

接下来，让我们看看如何解决上溢/下溢问题。

## 4.使用不同的数据类型

在 Java 中，存在范围大于*int*的数字类型，例如 *long*或[*BigInteger*](https://www.baeldung.com/java-biginteger)。为了避免上溢/下溢陷阱，我们可以**在执行计算之前****将\*int参数转换为其中一种类型。\***让我们以 *long*为例，并在新方法中实现逻辑：

```java
long absDiffAsLong(int num1, int num2) {
    return Math.abs((long) num1 - num2);
}复制
```

现在，让我们测试当我们将*Integer.MAX_VALUE*和*-200*传递给它时它是否给我们预期的结果：

```java
long diff = absDiffAsLong(Integer.MAX_VALUE, -200);
assertEquals(Integer.MAX_VALUE + 200L, diff);复制
```

如果我们运行测试，它就会通过。因此，这种方法解决了上溢/下溢问题。但是，值得一提的是返回类型是*long*而不是 *int*。

*如果调用者期望结果是long*也没关系。否则，如果需要*int*，我们必须[将*long*值转换为 *int*](https://www.baeldung.com/java-convert-long-to-int)。这很不方便。此外，如果我们不正确地执行*long*到*int* 转换，也可能发生上溢/下溢。

接下来，让我们看看是否可以将结果保留为*int*并避免上溢/下溢问题。

## 5. 发生上溢/下溢时抛出异常

假设我们的程序需要绝对差结果作为*int*。那么该方法的理想行为是**返回一个\*int\*并在发生上溢/下溢时引发异常**。

但是，正如我们之前所见，在常规*(num1 – num2)*计算期间**发生溢出时，Java 不会抛出异常。**这使得当我们检测到我们的程序没有产生预期结果时很难找到真正的原因。

从 Java 8 开始，Java 的 Math 引入了新的[**exact()*](https://www.baeldung.com/java-8-math)方法。当发生上溢/下溢时，这些方法会抛出*ArithmeticException 。*那么接下来，让我们用*subtractExact()*替换我们方法中的*(num1 – num2* ) ：

```java
int absDiff2(int num1, int num2) {
    return Math.abs(Math.subtractExact(num1, num2));
}复制
```

最后，以下测试告诉*absDiff2()*按预期工作：

```java
int diff1 = absDiff2(100, -200);
assertEquals(300, diff1);

int diff2 = absDiff2(100, 200);
assertEquals(100, diff2);

//overflow -> exception
assertThrows(ArithmeticException.class, () -> absDiff2(Integer.MAX_VALUE, -200));复制
```

## 六，结论

在本文中，我们探索了计算两个整数之间的绝对差。此外，我们还讨论了上溢/下溢问题。