## 1. 概述

三元条件运算符?:允许我们在Java中定义表达式。它是同样返回一个值的if-else语句的精简形式。

在本教程中，我们将学习何时以及如何使用三元结构。我们将从查看其语法开始，然后探索其用法。

### 延伸阅读

### [Java中的控制结构](https://www.baeldung.com/java-control-structures)

了解你可以在Java中使用的控制结构。

[阅读更多](https://www.baeldung.com/java-control-structures)→

### [Java中的If-Else语句](https://www.baeldung.com/java-if-else)

了解如何在Java中使用if-else语句。

[阅读更多](https://www.baeldung.com/java-if-else)→

### [如何在Java 8 Streams中使用if/else逻辑](https://www.baeldung.com/java-8-streams-if-else-logic)

了解如何将if/else逻辑应用于Java8Streams。

[阅读更多](https://www.baeldung.com/java-8-streams-if-else-logic)→

## 2. 语法

Java中的三元运算符?:是唯一接受三个操作数的运算符：

```java
booleanExpression ? expression1 : expression2
```

第一个操作数必须是布尔表达式，第二个和第三个操作数可以是任何返回值的表达式。如果第一个操作数的计算结果为true，则三元构造返回expression1作为输出，否则返回expression2。

## 3. 三元运算符示例

让我们考虑一下这个if-else结构：

```java
int num = 8;
String msg = "";
if(num > 10) {
    msg = "Number is greater than 10";
}
else {
    msg = "Number is less than or equal to 10";
}
```

在这里，我们根据num的条件评估为msg分配了一个值。

通过用三元结构轻松替换if-else语句，我们可以使这段代码更具可读性和安全性：

```java
final String msg = num > 10 
  ? "Number is greater than 10" 
  : "Number is less than or equal to 10";
```

## 4. 表达式计算

当使用Java三元结构时，在运行时只计算右侧表达式之一，即expression1或expression2。

我们可以通过编写一个简单的JUnit测试用例来测试它：

```java
@Test
public void whenConditionIsTrue_thenOnlyFirstExpressionIsEvaluated() {
    int exp1 = 0, exp2 = 0;
    int result = 12 > 10 ? ++exp1 : ++exp2;
    
    assertThat(exp1).isEqualTo(1);
    assertThat(exp2).isEqualTo(0);
    assertThat(result).isEqualTo(1);
}
```

我们的布尔表达式12 > 10始终计算为true，因此exp2的值保持原样。

同样，让我们考虑一下假条件会发生什么：

```java
@Test
public void whenConditionIsFalse_thenOnlySecondExpressionIsEvaluated() {
    int exp1 = 0, exp2 = 0;
    int result = 8 > 10 ? ++exp1 : ++exp2;

    assertThat(exp1).isEqualTo(0);
    assertThat(exp2).isEqualTo(1);
    assertThat(result).isEqualTo(1);
}
```

exp1的值保持不变，exp2的值增加1。

## 5. 嵌套三元运算符

我们可以将三元运算符嵌套到我们选择的任意数量的级别。

所以，这个结构在Java中是有效的：

```java
String msg = num > 10 ? "Number is greater than 10" : 
  num > 5 ? "Number is greater than 5" : "Number is less than equal to 5";
```

为了提高上面代码的可读性，我们可以在必要的地方使用大括号()：

```java
String msg = num > 10 ? "Number is greater than 10" 
  : (num > 5 ? "Number is greater than 5" : "Number is less than equal to 5");
```

但是，请注意，不建议在现实世界中使用这种深度嵌套的三元结构。这是因为它使代码的可读性和维护难度降低。

## 6. 总结

在这篇快速文章中，我们了解了Java中的三元运算符。不可能用三元运算符替换每个if-else结构。但它在某些情况下是一个很好的工具，可以使我们的代码更短、更易读。