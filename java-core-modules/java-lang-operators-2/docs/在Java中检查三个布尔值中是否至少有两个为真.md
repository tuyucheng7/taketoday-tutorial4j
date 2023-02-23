## 1. 概述

布尔值是Java的[原语](https://www.baeldung.com/java-primitives)之一。这是一种非常简单的数据类型，只有两个值：true和false。

在本教程中，我们将研究一个问题：检查给定的三个布尔值中是否至少有两个为真。

## 2. 问题简介

问题很简单。我们将获得三个布尔值。如果至少有两个为真，我们的方法应该返回真。

解决问题对我们来说不是挑战。但是，在本教程中，我们将探索一些不错的解决方案。此外，我们将讨论是否可以轻松扩展每种方法来解决一般问题：给定n个布尔值，检查它们中是否至少有x个为真。

我们将通过单元测试验证每种方法。因此，让我们首先创建一个Map对象来保存测试用例和预期结果：

```java
static final Map<boolean[], Boolean> TEST_CASES_AND_EXPECTED = ImmutableMap.of(
    new boolean[]{true, true, true}, true,
    new boolean[]{true, true, false}, true,
    new boolean[]{true, false, false}, false,
    new boolean[]{false, false, false}, false
);
```

如上面的代码所示，TEST_CASES_AND_EXPECTED地图包含四种场景及其预期结果。稍后，我们将遍历此映射对象并将每个布尔数组作为参数传递给每个方法，并验证该方法是否返回预期值。

接下来，让我们看看如何解决这个问题。

## 3. 循环三个布尔值

解决问题最直接的想法可能是遍历三个给定的布尔值并计算真值。

一旦计数器大于或等于2，我们将停止检查并返回true。否则，三个布尔值中true的数量小于2。因此，我们返回false：

```java
public static boolean twoOrMoreAreTrueByLoop(boolean a, boolean b, boolean c) {
    int count = 0;
    for (boolean i : new Boolean[] { a, b, c }) {
        count += i ? 1 : 0;
        if (count >= 2) {
            return true;
        }
    }
    return false;
}
```

接下来，让我们使用我们的TEST_CASES_AND_EXPECTED映射来测试此方法是否有效：

```java
TEST_CASES_AND_EXPECTED.forEach((array, expected) -> 
    assertThat(ThreeBooleans.twoOrMoreAreTrueByLoop(array[0], array[1], array[2])).isEqualTo(expected));
```

如果我们运行这个测试，不出所料，它通过了。

这种方法很容易理解。此外，假设我们将方法的参数更改为布尔数组(或Collection)和int x。在这种情况下，它可以很容易地扩展为解决问题的通用解决方案：给定n个布尔值，检查其中是否至少有x个为真：

```java
public static boolean xOrMoreAreTrueByLoop(boolean[] booleans, int x) {
    int count = 0;
    for (boolean i : booleans) { 
        count += i ? 1 : 0;
        if (count >= x) {
            return true;
        }
    }
    return false;
}
```

## 4. 将布尔值转换为数字

同样，我们可以将三个布尔值转换为数字并计算它们的总和并检查它是否为2或更大：

```java
public static boolean twoOrMoreAreTrueBySum(boolean a, boolean b, boolean c) {
    return (a ? 1 : 0) + (b ? 1 : 0) + (c ? 1 : 0) >= 2;
}
```

让我们执行测试以确保它按预期工作：

```java
TEST_CASES_AND_EXPECTED.forEach((array, expected) -> 
    assertThat(ThreeBooleans.twoOrMoreAreTrueBySum(array[0], array[1], array[2])).isEqualTo(expected));
```

我们还可以将这种方法转化为通用解决方案，以检查n个布尔值中的至少x个真值：

```java
public static boolean xOrMoreAreTrueBySum(Boolean[] booleans, int x) {
    return Arrays.stream(booleans)
      .mapToInt(b -> Boolean.TRUE.equals(b) ? 1 : 0)
      .sum() >= x;
}
```

我们使用[StreamAPI](https://www.baeldung.com/java-8-streams)将每个boolean转换为int并在上面的代码中计算总和。

## 5. 使用逻辑运算符

我们通过将布尔值转换为整数解决了这个问题。或者，我们可以使用[逻辑运算](https://www.baeldung.com/java-operators#logical-operators)来确定三个布尔值中是否至少有两个为真。

我们可以对每两个布尔值执行逻辑与(&&)运算。因此，我们将对给定的三个布尔值执行三个AND运算。如果三个布尔值中有两个为true，则至少有一个逻辑AND运算结果为true：

```java
public static boolean twoOrMoreAreTrueByOpeators(boolean a, boolean b, boolean c) {
    return (a && b) || (a && c) || (b && c);
}
```

接下来，如果我们使用TEST_CASES_AND_EXPECTED映射测试此方法，它也会通过：

```java
TEST_CASES_AND_EXPECTED.forEach((array, expected) -> 
    assertThat(ThreeBooleans.twoOrMoreAreTrueByOpeators(array[0], array[1], array[2])).isEqualTo(expected));
```

现在，让我们考虑一下是否可以将这种方法扩展到一般情况。它只在x为2时起作用。另外，如果n足够大，我们可能需要建立一个很长的逻辑运算链。

因此，它不适用于一般问题。

## 6. 使用卡诺图

[卡诺图](https://en.wikipedia.org/wiki/Karnaugh_map)是一种简化[布尔代数](https://www.baeldung.com/cs/boolean-algebra-basic-laws)表达式的方法。另外，我们可以写出卡诺图的表达式。因此，有时，它可以帮助我们解决复杂的布尔代数问题。

接下来，让我们看看如何使用卡诺图来解决这个问题。鉴于我们有三个布尔值A、B和C，我们可以构建卡诺图：

```bash
      | C | !C
------|---|----
 A  B | 1 | 1 
 A !B | 1 | 0
!A !B | 0 | 0
!A  B | 1 | 0
```

在上表中，A、B和C表示它们的真实值。相反，!A、!B和!C表示它们的假值。

因此，正如我们所见，该表涵盖了给定三个布尔值的所有可能组合。此外，我们可以找到至少两个布尔值为真的所有组合情况。对于这些情况，我们在表中写入了“1”。因此，有两个包含这些的组：第一行(组1)和第一列(组2)。

因此，产生1的最终布尔代数表达式将是：(获得group1中所有元素的表达式)||(获取group2中所有的表达式)。

接下来，让我们分而治之。

- 第1组(第一行)：A和B都为真。无论C有什么价值，我们都会有一个。因此，我们有：A&&B
- 第2组(第一列)：首先，C始终为真。而且，A和B中必须至少有一个为真。因此，我们得到：C&&(A||B)

最后，让我们结合两组并得到解决方案：

```java
public static boolean twoorMoreAreTrueByKarnaughMap(boolean a, boolean b, boolean c) {
    return (c && (a || b)) || (a && b);
}
```

现在，让我们测试该方法是否按预期工作：

```java
TEST_CASES_AND_EXPECTED.forEach((array, expected) -> 
    assertThat(ThreeBooleans.twoorMoreAreTrueByKarnaughMap(array[0], array[1], array[2])).isEqualTo(expected));
```

如果我们执行测试，它就会通过。也就是说，该方法完成了工作。

但是，如果我们尝试使用这种方法来解决一般问题，那么当n很大时，生成表可能会很困难。

因此，尽管卡诺图擅长解决复杂的布尔代数问题，但并不适用于一些动态的、一般性的任务。

## 7. 使用异或运算符

最后，让我们看看另一种有趣的方法。

在这个问题中，我们得到了三个布尔值。此外，我们知道布尔值只能有两个不同的值：true和false。

因此，让我们首先从三个布尔值中取出任意两个布尔值，比如a和b。然后，我们检查表达式a!=b的结果：

-a!=b为真——a或b为真。所以，如果c为真，那么我们有两个真。否则，我们在三个布尔值中有两个false。也就是说，c的值就是答案。
-a!=b为假——a和b具有相同的值。因为我们只有三个布尔值，所以a(或b)值就是答案。

因此，我们可以得出解决方案：a!=b?丙：一个。而且，a!=b校验实际上是一个[XOR操作](https://www.baeldung.com/java-xor-operator)。因此，解决方案可以很简单：

```java
public static boolean twoOrMoreAreTrueByXor(boolean a, boolean b, boolean c) {
    return a ^ b ? c : a;
}
```

当我们使用TEST_CASES_AND_EXPECTED映射测试该方法时，测试将通过：

```java
TEST_CASES_AND_EXPECTED.forEach((array, expected) -> 
    assertThat(ThreeBooleans.twoOrMoreAreTrueByXor(array[0], array[1], array[2])).isEqualTo(expected));
```

这个解决方案非常紧凑和棘手。但是，我们不能扩展它来解决一般问题。

## 8. 总结

在本文中，我们探索了几种不同的方法来检查三个给定的布尔值中是否至少有两个为真。

此外，我们还讨论了可以轻松扩展哪种方法来解决更普遍的问题：检查n个布尔值中是否至少有x个为真。