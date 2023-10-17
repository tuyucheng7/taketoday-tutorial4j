## 1. 概述

在本快速教程中，我们将展示模式匹配引擎的工作原理。我们还将介绍在Java中优化正则表达式的不同方法。

有关正则表达式使用的介绍，请参阅[此处的这篇文章](https://www.baeldung.com/regular-expressions-java)。

## 2. 模式匹配引擎

java.util.regex 包使用一种称为 非确定性有限自动机(NFA)的模式匹配引擎。它被认为是不确定的 ，因为在尝试匹配给定字符串上的正则表达式时，输入中的每个字符可能会针对正则表达式的不同部分进行多次检查。

在后台，上面提到的引擎使用回溯。这种通用算法会尝试穷尽所有可能性，直到它宣告失败。考虑以下示例以更好地理解NFA：

```java
"tra(vel|ce|de)m"
```

输入字符串 “ travel ”，引擎首先会寻找“ tra ”并立即找到它。

之后，它将尝试从第四个字符开始匹配“ vel ”。这将匹配，因此它将继续并尝试匹配“ m ”。

那将不匹配，因此，它将返回到第四个字符并搜索“ ce ”。同样，这不会匹配，所以它会再次回到第四个位置并尝试使用“ de ”。该字符串也不匹配，因此它将返回到输入字符串中的第二个字符并尝试搜索另一个“ tra ”。

对于最后一次失败，算法将返回失败。

对于最后一个简单的示例，引擎在尝试将输入字符串与正则表达式匹配时不得不多次回溯。因此，尽量减少它所做的回溯量很重要。

## 3.优化正则表达式的方法

### 3.1. 避免重新编译

Java 中的正则表达式被编译成一个内部数据结构。这个编译是一个耗时的过程。

每次我们调用 String.matches(String regex) 方法时，都会重新编译指定的正则表达式：

```java
if (input.matches(regexPattern)) {
    // do something
}
```

正如我们所见，每次评估条件时，都会编译正则表达式。

为了优化，可以先编译模式，然后创建一个Matcher来查找值中的巧合：

```java
Pattern pattern = Pattern.compile(regexPattern);
for(String value : values) {
    Matcher matcher = pattern.matcher(value);
    if (matcher.matches()) {
        // do something
    }
}
```

上述优化的替代方法是使用相同的 Matcher 实例及其reset()方法：

```java
Pattern pattern = Pattern.compile(regexPattern);
Matcher matcher = pattern.matcher("");
for(String value : values) {
    matcher.reset(value);
    if (matcher.matches()) {
      // do something
    }
}
```

由于 Matcher不是线程安全的，我们必须谨慎使用这种变体。在多线程场景中它可能很危险。

总而言之，在我们确定在任何时间点只有一个Matcher用户的每种情况下 ，都可以通过reset重用它。对于其余部分，重用预编译就足够了。

### 3.2. 使用交替

正如我们刚刚在上一节中检查的那样，交替使用不当可能会对性能造成损害。将更可能发生的选项放在前面很重要，这样可以更快地匹配它们。

此外，我们必须提取它们之间的共同模式。放置是不一样的：

```java
(travel | trade | trace)
```

比：

```java
tra(vel | de | ce)
```

后者速度更快，因为NFA将尝试匹配“ tra ”，如果找不到，则不会尝试任何替代方案。

### 3.3. 捕获组

每次我们捕获组时，我们都会受到小时间的惩罚。

如果我们不需要捕获组内的文本，我们应该考虑使用非捕获组。不要使用“ (M) ”，请使用“ (?:M) ”。

## 4。总结

在这篇简短的文章中，我们简要回顾了NFA 的工作原理。然后，我们继续探索如何通过预编译我们的模式并重用 Matcher 来优化正则表达式的性能。

最后，我们指出了在处理交替和分组时要牢记的几个注意事项。