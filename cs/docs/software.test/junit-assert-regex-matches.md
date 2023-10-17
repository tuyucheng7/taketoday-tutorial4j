## 1. 概述

[JUnit](https://www.baeldung.com/junit)成为许多开发人员对Java代码执行单元测试的首选。在现实场景中，一种常见的测试需求是验证给定字符串是否符合特定的[正则表达式 (regex)](https://www.baeldung.com/regular-expressions-java)模式。

在本教程中，我们将探索在JUnit中断言正则表达式匹配的几种方法，使我们能够有效地测试字符串模式。

## 2.问题介绍

问题非常简单：我们需要一种自然而有效的方法来确认输入字符串与特定的正则表达式模式对齐。理想情况下，我们还应该有一种可靠的方法来断言相反的情况，即输入字符串与正则表达式模式不匹配。

让我们首先探索广泛使用的[JUnit 5框架](https://www.baeldung.com/junit-5)，并了解如何使用其标准功能对正则表达式模式匹配执行断言。此外，我们将讨论使用JUnit 5进行此类断言时的潜在陷阱。

除了JUnit 5之外，还有与JUnit 5无缝集成的方便且补充的测试和断言库。在本教程中，我们将重点关注其中两个流行的外部库。我们将研究如何在这些库的上下文中断言正则表达式模式匹配，从而扩展我们的工具包以进行高效测试。

## 3. 使用标准JUnit 5方法

JUnit 5在org.junit.jupiter.api.Assertions包中提供了一系列常用的断言，例如[assertSame()](https://www.baeldung.com/junit-assertions#junit5-same)、[assertEquals()](https://www.baeldung.com/junit-assertions#junit5-assertEquals)等。

然而，JUnit 5缺乏用于正则表达式模式验证的专用断言方法，例如“ assertMatches() ”。由于String.matches()方法返回一个boolean，我们可以使用assertTrue()方法来确保字符串与正则表达式模式匹配：

```java
assertTrue("Java at Baeldung".matches(".* at Baeldung$"));
```

毫不奇怪，如果我们想断言字符串与正则表达式模式不匹配，我们可以使用assertFalse()断言：

```java
assertFalse("something else".matches(".* at Baeldung$"));
```

## 4. 为什么我们不应该使用assertLinesMatch()方法进行正则表达式匹配测试

虽然我们之前提到JUnit 5缺乏专用的正则表达式模式断言方法，但有些人可能不同意这一说法。JUnit 5确实提供了[assertLinesMatch()](https://www.baeldung.com/junit-assertions#junit5-assertLinesMatch)方法，它可以验证文本行内的正则表达式模式匹配，例如：

```java
assertLinesMatch(List.of(".* at Baeldung$"), List.of("Kotlin at Baeldung"));
```

如上面的示例所示，由于该方法接受两个字符串列表(预期列表和实际列表)，因此我们将正则表达式模式和输入字符串包装在列表中，并且测试通过。

但是，值得注意的是，使用assertLinesMatch()方法测试正则表达式匹配并不安全。一个例子可以快速展示它：

```java
assertFalse(".* at Baeldung$".matches(".* at Baeldung$"));
assertLinesMatch(List.of(".* at Baeldung$"), List.of(".* at Baeldung$"));
```

在上面的示例中，我们的输入字符串和正则表达式模式是相同的：“ . at Baeldung$ ”。显然，输入与模式不匹配，因为输入字符串以“ $ ”字符而不是“ Baeldung ”结尾。因此，assertFalse ()断言通过。

然后，我们将相同的输入和正则表达式模式传递给assertLinesMatch()方法，断言已经通过了！这是因为assertLinesMatch() 通过三个步骤验证预期列表和实际列表中的每一对：

1. 如果预期字符串等于实际字符串，则继续下一对
2. 否则，将预期字符串视为正则表达式模式并检查actualString.matches(expectedString)。 如果结果为真，则继续下一对
3. 否则，如果预期字符串是快进标记，则相应地应用快进实际行并从第一步开始

我们不会深入探讨assertLinesMatches()方法的用法。如上面的步骤所示，正则表达式模式测试在步骤 2 中执行。在我们的示例中，作为输入的实际字符串和预期字符串(正则表达式模式)是 equal 的。因此，步骤1通过了。也就是说，断言在没有应用正则表达式匹配检查的情况下就通过了。

因此，assertLinesMatch ()方法不是验证正则表达式模式匹配的正确断言方法。

## 5.使用AssertJ的matches()方法

借助流行的[AssertJ](https://www.baeldung.com/introduction-to-assertj)库，我们可以快速编写流畅的断言。AssertJ 提供matches()方法来测试正则表达式模式：

```java
// assertThat() below is imported from org.assertj.core.api.Assertions
assertThat("Linux at Baeldung").matches(".* at Baeldung$");
```

顾名思义，doesNotMatch()方法允许我们执行否定测试场景：

```java
assertThat("something unrelated").doesNotMatch(".* at Baeldung$");
```

## 6. 使用Hamcrest的matchesPattern()方法

同样，[Hamcrest](https://www.baeldung.com/java-junit-hamcrest-guide)是另一个广泛使用的测试框架。此外，它还提供matchesPattern()方法用于正则表达式匹配测试：

```java
// assertThat() below is imported from org.hamcrest.MatcherAssert
assertThat("Computer science at Baeldung", matchesPattern(".* at Baeldung$"));
```

要执行负正则表达式匹配测试，我们可以使用not()来否定matchesPattern()创建的Matcher：

```java
assertThat("something unrelated", not(matchesPattern(".* at Baeldung$")));
```

## 七、总结

在本文中，我们探索了断言正则表达式模式匹配的不同方法。

两个常用的库 AssertJ 和Hamcrest提供专用的正则表达式匹配测试方法。另一方面，如果我们希望最大限度地减少外部依赖，JUnit 5的assertTrue()与String.matches()方法相结合也可以实现相同的目标。

此外，我们还讨论了不应使用JUnit 5的assertLinesMatch() 进行正则表达式模式匹配测试。