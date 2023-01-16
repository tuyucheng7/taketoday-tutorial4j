## 1. 简介

[排列](https://www.baeldung.com/cs/array-generate-all-permutations)是集合中元素的重新排列。换句话说，它是集合顺序的所有可能变化。

在本教程中，我们将学习如何使用第三方库[在Java中轻松创建排列。](https://www.baeldung.com/java-array-permutations)更具体地说，我们将处理字符串中的排列。

## 2.排列

有时我们需要检查 String 值的所有可能排列。通常用于令人难以置信的在线编码练习，较少用于日常工作任务。例如，一个字符串“abc”将有六种不同的方式来排列里面的字符：“abc”、“acb”、“cab”、“bac”、“bca”、“cba”。

一些定义明确的算法可以帮助我们为特定字符串值创建所有可能的排列。比如最著名的就是Heap的算法。但是，它非常复杂且不直观。最重要的是，递归方法使事情变得更糟。

## 3.优雅的解决方案

实现用于生成排列的算法将需要编写自定义逻辑。在实现中很容易出错，并且随着时间的推移很难测试它是否正常工作。另外，重写以前写的东西是没有意义的。

此外，使用String值时，如果不小心操作，可能会通过创建太多实例来淹没String池。

以下是目前提供此类功能的库：

-   阿帕奇公地
-   番石榴
-   组合数学库

让我们尝试使用这些库查找 String 值的所有排列。我们将 关注这些库是否允许延迟遍历排列以及它们如何处理输入值中的重复项。

我们将在下面的示例中使用Helper.toCharacterList方法。此方法封装了将String转换为List of Characters的复杂性：

```java
static List<Character> toCharacterList(final String string) {
    return string.chars().mapToObj(s -> ((char) s)).collect(Collectors.toList());
}
```

此外，我们将使用辅助方法将字符列表 转换 为 字符串：

```java
static String toString(Collection<Character> collection) {
    return collection.stream().map(s -> s.toString()).collect(Collectors.joining());
}
```

## 4.阿帕奇公地

首先，让我们将 Maven 依赖项[commons-collections4](https://search.maven.org/search?q=g:org.apache.commons AND a:commons-collections4)添加到项目中：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.4</version>
</dependency>
```

总的来说，Apache 提供了一个简单的 API。CollectionUtils急切地创建排列，所以我们在处理长字符串值时应该小心：

```java
public List<String> eagerPermutationWithRepetitions(final String string) {
    final List<Character> characters = Helper.toCharacterList(string);
    return CollectionUtils.permutations(characters)
        .stream()
        .map(Helper::toString)
        .collect(Collectors.toList());
}
```

同时，为了让它以惰性方式工作，我们应该使用PermutationIterator：

```java
public List<String> lazyPermutationWithoutRepetitions(final String string) {
    final List<Character> characters = Helper.toCharacterList(string);
    final PermutationIterator<Character> permutationIterator = new PermutationIterator<>(characters);
    final List<String> result = new ArrayList<>();
    while (permutationIterator.hasNext()) {
        result.add(Helper.toString(permutationIterator.next()));
    }
    return result;
}
```

该库不处理重复项，因此字符串“aaaaaa”将产生 720 个排列，这通常是不可取的。 此外，PermutationIterator 没有获取排列数的方法。在这种情况下，我们应该根据输入大小分别计算它们。

## 5.番石榴

首先，让我们将 [Guava 库](https://search.maven.org/search?q=g:com.google.guava AND a:guava)的 Maven 依赖项添加 到项目中：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

Guava 允许使用Collections2创建排列。API 易于使用：

```java
public List<String> permutationWithRepetitions(final String string) {
    final List<Character> characters = Helper.toCharacterList(string);
    return Collections2.permutations(characters).stream()
        .map(Helper::toString)
        .collect(Collectors.toList());
}
```

Collections2.permutations的结果是一个PermutationCollection，它允许轻松访问排列。所有的排列都是懒惰地创建的。

此外，此类还提供了一个 API，用于创建不重复的排列：

```java
public List<String> permutationWithoutRepetitions(final String string) {
    final List<Character> characters = Helper.toCharacterList(string);
    return Collections2.orderedPermutations(characters).stream()
        .map(Helper::toString)
        .collect(Collectors.toList());
}
```

但是，这些方法的问题在于它们使用[@Beta注解进行注解](https://guava.dev/releases/18.0/api/docs/com/google/common/annotations/Beta.html)，这不能保证此 API 在未来的版本中不会更改。

## 6. 组合数学库

要在项目中使用它，让我们添加 [combinatoricslib3](https://search.maven.org/search?q=g:com.github.dpaukov AND a:combinatoricslib3) Maven 依赖项：

```xml
<dependency>
    <groupId>com.github.dpaukov</groupId>
    <artifactId>combinatoricslib3</artifactId>
    <version>3.3.3</version>
</dependency>
```

尽管这是一个小型库，但它提供了许多组合工具，包括排列。API 本身非常直观并且使用Java流。让我们从特定字符串或字符列表创建排列：

```java
public List<String> permutationWithoutRepetitions(final String string) {
    List<Character> chars = Helper.toCharacterList(string);
    return Generator.permutation(chars)
      .simple()
      .stream()
      .map(Helper::toString)
      .collect(Collectors.toList());
}
```

上面的代码创建了一个生成器，它将提供字符串的排列。排列将被延迟检索。因此，我们只创建了一个生成器并计算了预期的排列数。

同时，有了这个库，我们可以识别重复的策略。如果我们使用一个字符串“aaaaaa”作为例子，我们只会得到一个而不是 720 个相同的排列。

```java
public List<String> permutationWithRepetitions(final String string) {
    List<Character> chars = Helper.toCharacterList(string);
    return Generator.permutation(chars)
      .simple(TreatDuplicatesAs.IDENTICAL)
      .stream()
      .map(Helper::toString)
      .collect(Collectors.toList());
}
```

TreatDuplicatesAs允许我们定义我们希望如何处理重复项。

## 七. 总结

有很多方法可以处理组合学和排列，尤其是。所有这些库都可以为此提供很大帮助。值得尝试所有这些并决定哪一个适合你的需要。尽管许多人有时会敦促编写所有代码，但将时间浪费在已经存在并提供良好功能的东西上是没有意义的。