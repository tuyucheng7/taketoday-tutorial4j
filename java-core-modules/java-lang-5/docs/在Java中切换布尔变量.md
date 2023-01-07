## 1. 概述

Boolean 是Java中的一种基本数据类型。通常，它只能有两个值，true或false。

在本教程中，我们将讨论如何切换给定的布尔变量。

## 二、问题简介

这个问题非常简单。简单地说，我们想要反转布尔变量的值。例如，true在切换后变为 false 。

但是，我们应该注意到Java中有两种“不同”的布尔类型，[原始](https://www.baeldung.com/java-primitives-vs-objects) 布尔值和[装箱](https://www.baeldung.com/java-wrapper-classes) 布尔值。因此，理想的切换方法应该适用于这两种类型。

在本教程中，我们将介绍如何实现这种方法。

此外，为简单起见，我们将使用单元测试断言来验证我们的实现是否按预期工作。

那么接下来，让我们从切换原始布尔变量开始，因为这是我们最终的toggle()方法的基础。

## 3.切换原始布尔变量

切换原始布尔变量的最直接方法 是使用[NOT 运算符](https://www.baeldung.com/java-operators#3-the-logical-complement-operator)( ! )。

让我们创建一个测试看看它是如何工作的：

```java
boolean b = true;
b = !b;
assertFalse(b);

b = !b;
assertTrue(b);

```

如果我们运行这个测试，它就会通过。因此，每次我们对布尔变量执行 NOT 运算符时，它的值都会被反转。

或者，[XOR 运算符](https://www.baeldung.com/java-operators#3-the-bitwise-xor-operator)( ^ ) 也可以反转布尔值。在考虑实现之前，让我们快速了解 XOR 运算符的工作原理。

给定两个布尔变量b1和b2，仅当b1和b2具有不同的值时b1 ^ b2才为真，例如：

-   真 ^ 真 = 假
-   假 ^ 假 = 假
-   真 ^ 假 = 真

因此，我们可以利用 XOR 的特性，执行b ^ true来反转 b的值：

-   b = true -> b ^ true 变为true ^ true = false
-   b = false -> b ^ true变为false ^ true = true

现在我们了解了 XOR 的逻辑，将其转换为Java代码对我们来说并不是一项具有挑战性的任务：

```java
boolean b = true;
b ^= true;
assertFalse(b);

b ^= true;
assertTrue(b);

```

毫不奇怪，当我们运行它时，测试通过了。

## 4. 创建toggle()方法

我们已经看到原始布尔变量只能有两个值：true和false。但是，与原始boolean不同，装箱的Boolean变量可以容纳 null。

当我们对布尔变量 执行 NOT 或 XOR 运算时，Java 会自动将布尔值拆箱为布尔值。但是如果我们没有正确处理null情况，我们将遇到NullPointerException：

```java
assertThatThrownBy(() -> {
    Boolean b = null;
    b = !b;
}).isInstanceOf(NullPointerException.class);

```

如果我们执行上面的测试，它就会通过。不幸的是，这意味着当我们执行!b时会发生NullPointerException。

那么接下来，让我们创建 null-safe toggle()方法来处理Boolean和boolean变量：

```java
static Boolean toggle(Boolean b) {
    if (b == null){
        return b;
    }
    return !b;
}

```

在这里，我们首先执行空值检查，然后使用 NOT 运算符反转值。当然，如果我们愿意，在空检查之后，我们也可以使用 XOR 方法来反转 b的值。

最后，让我们创建一个测试来验证我们的 toggle()方法是否适用于所有情况：

```java
// boxed Boolean
Boolean b = true;
b = ToggleBoolean.toggle(b);
assertFalse(b);

b = ToggleBoolean.toggle(b);
assertTrue(b);

b = null;
b = ToggleBoolean.toggle(b);
assertNull(b);

// primitive boolean
boolean bb = true;
bb = ToggleBoolean.toggle(bb);
assertFalse(bb);
bb = ToggleBoolean.toggle(bb);
assertTrue(bb);

```

如上面的测试所示，我们使用布尔变量和布尔变量测试了toggle()方法。此外，我们已经测试了布尔变量b=null的场景。

当我们执行它时，测试通过了。因此，我们的 toggle()方法按预期工作。

## 5.总结

在本文中，我们学习了如何构建空安全方法来切换给定的布尔/布尔变量。