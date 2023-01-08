## 1. 概述

[Stream API](https://www.baeldung.com/java-8-streams-introduction)提供了丰富的中间函数、缩减函数和终端函数，它们还支持并行化。

更具体地说，缩减流操作允许我们通过对序列中的元素重复应用组合操作，从一系列元素中生成一个结果。

在本教程中，我们将研究通用[Stream.reduce()](https://docs.oracle.com/javase/tutorial/collections/streams/reduction.html)操作并在一些具体用例中查看它。

## 延伸阅读：

## [使用Java流对数字求和](https://www.baeldung.com/java-stream-sum)

使用JavaStream API 对数字求和的快速实用指南。

[阅读更多](https://www.baeldung.com/java-stream-sum)→

## [Java 8 流简介](https://www.baeldung.com/java-8-streams-introduction)

对Java8 Streams 的快速实用介绍。

[阅读更多](https://www.baeldung.com/java-8-streams-introduction)→

## [Java BiFunction 接口指南](https://www.baeldung.com/java-bifunction-interface)

了解带有两个参数的Java功能接口的一些常见模式。

[阅读更多](https://www.baeldung.com/java-bifunction-interface)→

## 2. 关键概念：Identity、Accumulator 和 Combiner

在我们深入研究使用Stream.reduce()操作之前，让我们将操作的参与者元素分解为单独的块。这样，我们将更容易理解每个人所扮演的角色。

-   Identity – 一个元素，它是缩减操作的初始值，如果流为空则为默认结果
-   累加器——一个有两个参数的函数：归约运算的部分结果和流的下一个元素
-   Combiner——当归约被并行化或当累加器参数的类型与累加器实现的类型不匹配时，用于合并归约操作的部分结果的函数

## 3. 使用Stream.reduce()

为了更好地理解 identity、accumulator 和 combiner 元素的功能，让我们看一些基本示例：

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
int result = numbers
  .stream()
  .reduce(0, (subtotal, element) -> subtotal + element);
assertThat(result).isEqualTo(21);
```

在这种情况下，整数值0 是标识。它存储归约操作的初始值，以及整数值流为空时的默认结果。

同样，lambda 表达式：

```java
subtotal, element -> subtotal + element
```

是累加器 ，因为它采用整数值的部分和以及流中的下一个元素。

为了使代码更加简洁，我们可以使用方法引用而不是 lambda 表达式：

```java
int result = numbers.stream().reduce(0, Integer::sum);
assertThat(result).isEqualTo(21);
```

当然，我们可以对包含其他类型元素的流使用reduce()操作。

例如，我们可以对String元素数组使用reduce()并将它们连接成一个结果：

```java
List<String> letters = Arrays.asList("a", "b", "c", "d", "e");
String result = letters
  .stream()
  .reduce("", (partialString, element) -> partialString + element);
assertThat(result).isEqualTo("abcde");
```

同样，我们可以切换到使用方法引用的版本：

```java
String result = letters.stream().reduce("", String::concat);
assertThat(result).isEqualTo("abcde");
```

让我们使用reduce()操作来连接字母数组的大写元素：

```java
String result = letters
  .stream()
  .reduce(
    "", (partialString, element) -> partialString.toUpperCase() + element.toUpperCase());
assertThat(result).isEqualTo("ABCDE");
```

此外，我们可以在并行流中使用reduce() (稍后会详细介绍)：

```java
List<Integer> ages = Arrays.asList(25, 30, 45, 28, 32);
int computedAges = ages.parallelStream().reduce(0, (a, b) -> a + b, Integer::sum);
```

当流并行执行时，Java 运行时会将流拆分为多个子流。在这种情况下，我们需要使用一个函数将子流的结果合并为一个。 这就是组合器的作用——在上面的代码片段中，它是Integer::sum方法引用。

有趣的是，这段代码无法编译：

```java
List<User> users = Arrays.asList(new User("John", 30), new User("Julie", 35));
int computedAges = 
  users.stream().reduce(0, (partialAgeResult, user) -> partialAgeResult + user.getAge());

```

在这种情况下，我们有一个User对象流，累加器参数的类型是Integer和User。但是，累加器实现是整数之和，因此编译器无法推断用户参数的类型。

我们可以通过使用组合器来解决这个问题：

```java
int result = users.stream()
  .reduce(0, (partialAgeResult, user) -> partialAgeResult + user.getAge(), Integer::sum);
assertThat(result).isEqualTo(65);
```

简单来说，如果我们使用顺序流并且累加器参数的类型与其实现的类型相匹配，我们就不需要使用组合器。

## 4. 并行缩减

正如我们之前了解到的，我们可以在并行流上使用reduce() 。

当我们使用并行流时，我们应该确保reduce()或在流上执行的任何其他聚合操作是：

-   associative：结果不受操作数顺序的影响
-   非干扰：操作不影响数据源
-   无状态和确定性：操作没有状态并为给定输入产生相同的输出

我们应该满足所有这些条件，以防止不可预测的结果。

正如预期的那样，对并行流执行的操作(包括reduce() )是并行执行的，因此可以利用多核硬件架构。

出于显而易见的原因，并行流比顺序流的性能要高得多。即便如此，如果应用于流的操作成本不高，或者流中的元素数量很少，那么它们就有些矫枉过正了。

当然，当我们需要处理大型流并执行昂贵的聚合操作时，并行流是正确的方法。

让我们创建一个简单的[JMH](https://www.baeldung.com/java-microbenchmark-harness) (Java Microbenchmark Harness)基准测试，并比较在顺序流和并行流上使用reduce()操作时各自的执行时间：

```java
@State(Scope.Thread)
private final List<User> userList = createUsers();

@Benchmark
public Integer executeReduceOnParallelizedStream() {
    return this.userList
      .parallelStream()
      .reduce(
        0, (partialAgeResult, user) -> partialAgeResult + user.getAge(), Integer::sum);
}

@Benchmark
public Integer executeReduceOnSequentialStream() {
    return this.userList
      .stream()
      .reduce(
        0, (partialAgeResult, user) -> partialAgeResult + user.getAge(), Integer::sum);
}

```

在上面的 JMH 基准测试中，我们比较了执行平均时间。我们只是创建一个包含大量User对象的List 。接下来，我们在顺序流和并行流上调用reduce()并检查后者的执行速度是否比前者快(每次操作以秒为单位)。

这些是我们的基准测试结果：

```plaintext
Benchmark                                                   Mode  Cnt  Score    Error  Units
JMHStreamReduceBenchMark.executeReduceOnParallelizedStream  avgt    5  0,007 ±  0,001   s/op
JMHStreamReduceBenchMark.executeReduceOnSequentialStream    avgt    5  0,010 ±  0,001   s/op
```

## 5. 减少时抛出和处理异常

在上面的示例中，reduce()操作不会抛出任何异常。但它当然可能。

例如，假设我们需要将流的所有元素除以提供的因子，然后对它们求和：

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
int divider = 2;
int result = numbers.stream().reduce(0, a / divider + b / divider);

```

只要divider变量不为零，这就会起作用。但如果它为零，reduce()将抛出 [ArithmeticException](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ArithmeticException.html)异常：除以零。

我们可以很容易地捕获异常并用它做一些有用的事情，比如记录它、从中恢复等等，这取决于用例，通过使用[try/catch](https://www.baeldung.com/java-exceptions)块：

```java
public static int divideListElements(List<Integer> values, int divider) {
    return values.stream()
      .reduce(0, (a, b) -> {
          try {
              return a / divider + b / divider;
          } catch (ArithmeticException e) {
              LOGGER.log(Level.INFO, "Arithmetic Exception: Division by Zero");
          }
          return 0;
      });
}
```

虽然这种方法可行，但我们用try/catch块污染了 lambda 表达式。我们不再拥有以前的干净单线。

为了解决这个问题，我们可以使用[提取函数重构技术](https://refactoring.com/catalog/extractFunction.html) 并将try/catch块提取到一个单独的方法中：

```java
private static int divide(int value, int factor) {
    int result = 0;
    try {
        result = value / factor;
    } catch (ArithmeticException e) {
        LOGGER.log(Level.INFO, "Arithmetic Exception: Division by Zero");
    }
    return result
}

```

现在divideListElements()方法的实现再次变得干净和流线型：

```java
public static int divideListElements(List<Integer> values, int divider) {
    return values.stream().reduce(0, (a, b) -> divide(a, divider) + divide(b, divider));
}

```

假设divideListElements()是一个由抽象NumberUtils类实现的实用方法，我们可以创建一个单元测试来检查divideListElements()方法的行为：

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
assertThat(NumberUtils.divideListElements(numbers, 1)).isEqualTo(21);

```

当提供的整数值列表包含 0时，让我们也测试divideListElements()方法：

```java
List<Integer> numbers = Arrays.asList(0, 1, 2, 3, 4, 5, 6);
assertThat(NumberUtils.divideListElements(numbers, 1)).isEqualTo(21);

```

最后，让我们测试一下分频器也为 0 时的方法实现：

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
assertThat(NumberUtils.divideListElements(numbers, 0)).isEqualTo(0);
```

## 6. 复杂的自定义对象

我们还可以将 Stream.reduce() 与包含非原始字段的自定义对象一起使用。为此，我们需要为数据类型提供相关的身份、累加器和组合器。

假设我们的用户 是评论网站的一部分。我们的每个User都可以拥有一个Rating ，这是许多Review的平均值 。

首先，让我们从Review 对象开始。

每个Review都应该包含一个简单的评论和分数：

```java
public class Review {

    private int points;
    private String review;

    // constructor, getters and setters
}
```

接下来，我们需要定义我们的 评级，它将把我们的评论与分数字段放在一起。随着我们添加更多评论，此字段将相应增加或减少：

```java
public class Rating {

    double points;
    List<Review> reviews = new ArrayList<>();

    public void add(Review review) {
        reviews.add(review);
        computeRating();
    }

    private double computeRating() {
        double totalPoints = 
          reviews.stream().map(Review::getPoints).reduce(0, Integer::sum);
        this.points = totalPoints / reviews.size();
        return this.points;
    }

    public static Rating average(Rating r1, Rating r2) {
        Rating combined = new Rating();
        combined.reviews = new ArrayList<>(r1.reviews);
        combined.reviews.addAll(r2.reviews);
        combined.computeRating();
        return combined;
    }

}
```

我们还添加了一个 平均 函数来计算基于两个输入Rating的平均值。这将非常适合我们的 组合器 和 累加器 组件。

接下来，让我们定义一个 User列表，每个列表都有自己的评论集：

```java
User john = new User("John", 30);
john.getRating().add(new Review(5, ""));
john.getRating().add(new Review(3, "not bad"));
User julie = new User("Julie", 35);
john.getRating().add(new Review(4, "great!"));
john.getRating().add(new Review(2, "terrible experience"));
john.getRating().add(new Review(4, ""));
List<User> users = Arrays.asList(john, julie);

```

既然 John 和 Julie 已经被计算在内，让我们使用 Stream.reduce()来计算两个用户的平均评分。

作为 身份，如果我们的输入列表为空，让我们返回一个新的评级：

```java
Rating averageRating = users.stream()
  .reduce(new Rating(), 
    (rating, user) -> Rating.average(rating, user.getRating()), 
    Rating::average);
```

如果我们计算一下，我们应该会发现平均分是 3.6：

```java
assertThat(averageRating.getPoints()).isEqualTo(3.6);
```

## 七、总结

在本文中，我们学习了如何使用Stream.reduce()操作。

此外，我们还学习了如何对顺序流和并行流执行缩减，以及如何在缩减时处理异常。