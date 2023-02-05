## 1. 简介

Java 8引入了[函数式编程](https://www.baeldung.com/java-8-functional-interfaces)，允许我们通过传入函数来参数化通用方法。

我们可能最熟悉的是单参数Java 8函数式接口，例如Function、Predicate和Consumer。

**在本教程中，我们介绍使用两个参数的函数式接口**。此类函数称为二进制函数，并在Java中使用BiFunction函数接口表示。

## 2. 单参数函数

让我们快速回顾一下如何使用单参数或一元函数，就像我们在流中所做的那样：

```java
List<String> mapped = Stream.of("hello", "world")
    .map(word -> word + "!")
    .collect(Collectors.toList());

assertThat(mapped).containsExactly("hello!", "world!");
```

如我们所见，map方法使用Function作为参数，Function接收单个参数并允许我们对该值执行操作，返回一个新值。

## 3. 双参数操作

**Java Stream库为我们提供了一个reduce函数，它允许我们组合流的元素**。我们需要表达我们到目前为止积累的值是如何通过添加下一个项目来转换的。

reduce方法使用函数接口BinaryOperator<T\>，该接口接收两个相同类型的对象作为输入。

假设我们想通过将新元素放在前面并使用破折号分隔符来连接流中的所有元素。我们将在以下部分中介绍实现此功能的几种方法。

### 3.1 使用Lambda

BiFunction的lambda实现以两个参数为前缀，用括号括起来：

```java
String result = Stream.of("hello", "world")
    .reduce("", (a, b) -> b + "-" + a);

assertThat(result).isEqualTo("world-hello-");
```

如我们所见，a和b这两个值是String。我们编写了一个lambda，将它们组合起来以产生所需的输出，其中第二个字符串在前，中间有一个破折号。

我们应该注意，reduce还需要一个起始值，在本例中是空字符串。因此，随着流中的第一个值与上面的代码连接在一起，我们最后以一个尾随破折号结束。

另外，我们应该注意，Java的类型推断允许我们在大多数情况下省略参数的类型。在上下文中无法明确lambda类型的情况下，我们可以为参数指定类型：

```java
String result = Stream.of("hello", "world")
    .reduce("", (String a, String b) -> b + "-" + a);
```

### 3.2 使用Function

如果我们想让上面的算法不在末尾加上破折号怎么办？**我们可以在lambda中编写更多代码，但这可能会变得混乱**。让我们提取为一个方法：

```java
private String combineWithoutTrailingDash(String a, String b) {
	if (a.isEmpty()) {
		return b;
	}
	return b + "-" + a;
}
```

然后调用它：

```java
String result = Stream.of("hello", "world")
    .reduce("", (a, b) -> combineWithoutTrailingDash(a, b));

assertThat(result).isEqualTo("world-hello");
```

如我们所见，lambda调用了我们的方法，这比将更复杂的实现内联更容易阅读。

### 3.3 使用方法引用

像Intellij IDEA这样的IDE会自动提示我们可以将上面的lambda转换为方法引用，因为它通常更易于阅读。

因此，以上的例子可以使用方法引用简写：

```java
String result = Stream.of("hello", "world")
    .reduce("", this::combineWithoutTrailingDash);

assertThat(result).isEqualTo("world-hello");
```

**方法引用通常使函数代码更加能表达出所做的事情**。

## 4. 使用BiFunction

到目前为止，我们已经演示了如何使用两个参数类型相同的函数。**BiFunction接口允许我们使用不同类型的参数**，并提供第三种类型的返回值。

假设我们正在编写一个算法，通过对每一对元素执行操作，将两个大小相等的列表合并为第三个列表：

```java
List<String> list1 = Arrays.asList("a", "b", "c");
List<Integer> list2 = Arrays.asList(1, 2, 3);

List<String> result = new ArrayList<>();
for (int i = 0; i < list1.size(); i++) {
	result.add(list1.get(i) + list2.get(i));
}

assertThat(result).containsExactly("a1", "b2", "c3");
```

### 4.1 泛化函数

**我们可以使用BiFunction作为组合器(combiner)来泛化这个专门的函数**：

```java
private static <T, U, R> List<R> listCombiner(List<T> list1, List<U> list2, BiFunction<T, U, R> combiner) {
    
    List<R> result = new ArrayList<>();
    for (int i = 0; i < list1.size(); i++) {
        result.add(combiner.apply(list1.get(i), list2.get(i)));
    }
    return result;
}
```

在上面的BiFunction中，共有三种类型的参数：T表示第一个列表中的元素类型，U表示第二个列表中的元素类型，然后R表示组合函数返回的任何类型。

**我们使用提供给这个函数的BiFunction通过调用它的apply方法来获取结果**。

### 4.2 调用泛型函数

我们的组合器是一个BiFunction，它允许我们注入算法，无论输入和输出的类型如何。让我们尝试一下：

```java
List<String> list1 = Arrays.asList("a", "b", "c");
List<Integer> list2 = Arrays.asList(1, 2, 3);

List<String> result = listCombiner(list1, list2, (letter, number) -> letter + number);

assertThat(result).containsExactly("a1", "b2", "c3");
```

我们也可以将其用于完全不同类型的输入和输出。

让我们注入一个算法来确定第一个列表中的值是否大于第二个列表中的值，并生成一个布尔结果：

```java
List<Double> list1 = Arrays.asList(1.0d, 2.1d, 3.3d);
List<Float> list2 = Arrays.asList(0.1f, 0.2f, 4f);

List<Boolean> result = listCombiner(list1, list2, (doubleNumber, floatNumber) -> doubleNumber > floatNumber);

assertThat(result).containsExactly(true, true, false);
```

### 4.3 BiFunction方法引用

我们也可以用提取的方法和方法引用重写上面的代码：

```java
List<Double> list1 = Arrays.asList(1.0d, 2.1d, 3.3d);
List<Float> list2 = Arrays.asList(0.1f, 0.2f, 4f);

List<Boolean> result = listCombiner(list1, list2, this::firstIsGreaterThanSecond);

assertThat(result).containsExactly(true, true, false);

private boolean firstIsGreaterThanSecond(Double a, Float b) {
    return a > b;
}
```

这样的代码更易于阅读，因为方法firstIsGreaterThanSecond将注入的算法描述为方法引用。

### 4.4 BiFunction方法引用使用this

假设我们想使用上述基于BiFunction的算法来确定两个列表是否相等：

```java
List<Float> list1 = Arrays.asList(0.1f, 0.2f, 4f);
List<Float> list2 = Arrays.asList(0.1f, 0.2f, 4f);

List<Boolean> result = listCombiner(list1, list2, (float1, float2) -> float1.equals(float2));

assertThat(result).containsExactly(true, true, true);
```

我们实际上可以简化解决方案：

```java
List<Boolean> result = listCombiner(list1, list2, Float::equals);
```

这是因为Float中的equals函数与BiFunction具有相同的签名。它接收this的隐式第一个参数，即Float类型的对象。Object类型的第二个参数other是要比较的值。

## 5. 组合BiFunction

如果我们可以使用方法引用来执行与我们的数字列表比较示例相同的操作会怎样？

```java
List<Double> list1 = Arrays.asList(1.0d, 2.1d, 3.3d);
List<Double> list2 = Arrays.asList(0.1d, 0.2d, 4d);

List<Integer> result = listCombiner(list1, list2, Double::compareTo);

assertThat(result).containsExactly(1, 1, -1);
```

这与我们的示例很接近，但返回一个Integer，而不是原始的Boolean。这是因为Double中的compareTo方法返回Integer。

我们可以通过使用andThen来添加我们需要的额外行为来组成一个函数，这会产生一个BiFunction，该函数首先对两个输入执行一件事，然后执行另一个操作。

接下来，让我们创建一个函数来将我们的方法引用Double::compareTo强制转换为BiFunction：

```java
private static <T, U, R> BiFunction<T, U, R> asBiFunction(BiFunction<T, U, R> function) {
    return function;
}
```

**lambda或方法引用仅在通过方法调用转换后才成为BiFunction**。我们可以使用这个辅助函数将我们的lambda显式转换为BiFunction对象。

现在，我们可以使用andThen在第一个函数之上添加行为：

```java
List<Double> list1 = Arrays.asList(1.0d, 2.1d, 3.3d);
List<Double> list2 = Arrays.asList(0.1d, 0.2d, 4d);

List<Boolean> result = listCombiner(list1, list2, asBiFunction(Double::compareTo).andThen(i -> i > 0));

assertThat(result).containsExactly(true, true, false);
```

## 6. 总结

在本教程中，我们根据提供的Java Streams API和我们自定义的函数介绍了BiFunction和BinaryOperator。我们已经研究了如何使用lambda和方法引用来传递BiFunction，并且我们介绍了如何组合函数。

Java库仅提供单参数和双参数函数接口。对于需要更多参数的情况，请参阅我们关于[柯里化](https://www.baeldung.com/java-currying)的文章以获得更多想法。