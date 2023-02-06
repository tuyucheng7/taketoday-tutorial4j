## 1. 概述

在本教程中，我们将看到**预编译正则表达式模式的好处**以及**Java 8和11中引入的新方法**。

这将不是一个正则表达式操作方法，但我们为此目的提供了一个出色的[Java正则表达式API指南](https://www.baeldung.com/regular-expressions-java)。

## 2. 好处

重用不可避免地会带来性能提升，因为我们不需要一次又一次地创建和重新创建相同对象的实例。因此，我们可以假设重用和性能通常是联系在一起的。

让我们来看看这个原则，因为它与Pattern#compile相关。**我们将使用一个简单的基准测试**：

1.  我们有一个列表，其中包含从1到5,000,000的5,000,000个数字
2.  我们的正则表达式将匹配偶数

因此，让我们使用以下Java正则表达式来测试解析这些数字：

-   String.matches(regex)
-   Pattern.matches(regex, charSequence)
-   Pattern.compile(regex).matcher(charSequence).matches()
-   预编译的正则表达式，多次调用preCompiledPattern.matcher(value).matches()
-   带有一个Matcher实例和多次调用matcherFromPreCompiledPattern.reset(value).matches()的预编译正则表达式

实际上，如果我们看一下String#matches的实现：

```java
public boolean matches(String regex) {
    return Pattern.matches(regex, this);
}
```

在Pattern#matches中：

```java
public static boolean matches(String regex, CharSequence input) {
    Pattern p = compile(regex);
    Matcher m = p.matcher(input);
    return m.matches();
}
```

然后，我们可以想象前三个表达式的执行方式相似。这是因为第一个表达式调用第二个，第二个调用第三个。

第二点是这些方法不会重用创建的Pattern和Matcher实例。而且，正如我们将在基准测试中看到的那样，这会将性能降低六倍：

```java
@Benchmark
public void matcherFromPreCompiledPatternResetMatches(Blackhole bh) {
    for (String value : values) {
        bh.consume(matcherFromPreCompiledPattern.reset(value).matches());
    }
}

@Benchmark
public void preCompiledPatternMatcherMatches(Blackhole bh) {
    for (String value : values) {
        bh.consume(preCompiledPattern.matcher(value).matches());
    }
}

@Benchmark
public void patternCompileMatcherMatches(Blackhole bh) {
    for (String value : values) {
        bh.consume(Pattern.compile(PATTERN).matcher(value).matches());
    }
}

@Benchmark
public void patternMatches(Blackhole bh) {
    for (String value : values) {
        bh.consume(Pattern.matches(PATTERN, value));
    }
}

@Benchmark
public void stringMatchs(Blackhole bh) {
    Instant start = Instant.now();
    for (String value : values) {
        bh.consume(value.matches(PATTERN));
    }
}
```

从基准测试结果来看，毫无疑问，预编译的Pattern和重用的Matcher是赢家，结果快了六倍以上：

```shell
Benchmark                                                               Mode  Cnt     Score     Error  Units
PatternPerformanceComparison.matcherFromPreCompiledPatternResetMatches  avgt   20   278.732 ±  22.960  ms/op
PatternPerformanceComparison.preCompiledPatternMatcherMatches           avgt   20   500.393 ±  34.182  ms/op
PatternPerformanceComparison.stringMatchs                               avgt   20  1433.099 ±  73.687  ms/op
PatternPerformanceComparison.patternCompileMatcherMatches               avgt   20  1774.429 ± 174.955  ms/op
PatternPerformanceComparison.patternMatches                             avgt   20  1792.874 ± 130.213  ms/op
```

**除了性能时间之外，我们还有创建的对象数量**：

-   前三种形式：
    -   创建了5,000,000个Pattern实例
    -   创建了5,000,000个Matcher实例
-   preCompiledPattern.matcher(value).matches()
    -   创建了1个Pattern实例
    -   创建了5,000,000个Matcher实例
-   matcherFromPreCompiledPattern.reset(value).matches()
    -   创建了1个Pattern实例
    -   创建了1个Matcher实例

因此，不要将我们的正则表达式委托给String#matches或Pattern#matches，因为它们总是会创建Pattern和Matcher实例。我们应该预编译我们的正则表达式以获得性能并创建更少的对象。

要了解更多关于正则表达式性能的信息，请查看我们的[Java正则表达式性能概述](https://www.baeldung.com/java-regex-performance)。

## 3. 新方法

由于引入了函数式接口和流，重用变得更加容易。

**Pattern类已在新的Java版本中发展**，以提供与流和lambda的集成。

### 3.1 Java 8

**Java 8引入了两种新方法：splitAsStream和asPredicate**。

让我们看看splitAsStream的一些代码，该代码围绕模式的匹配从给定的输入序列创建一个流：

```java
@Test
public void givenPreCompiledPattern_whenCallSplitAsStream_thenReturnArraySplitByThePattern() {
    Pattern splitPreCompiledPattern = Pattern.compile("__");
    Stream<String> textSplitAsStream = splitPreCompiledPattern.splitAsStream("My_Name__is__Fabio_Silva");
    String[] textSplit = textSplitAsStream.toArray(String[]::new);

    assertEquals("My_Name", textSplit[0]);
    assertEquals("is", textSplit[1]);
    assertEquals("Fabio_Silva", textSplit[2]);
}
```

asPredicate方法创建一个谓词，其行为就像它从输入序列创建一个匹配器，然后调用find：

```java
string -> matcher(string).find();
```

让我们创建一个模式来匹配列表中的名称，该列表中的名字和姓氏至少各有三个字母：

```java
@Test
public void givenPreCompiledPattern_whenCallAsPredicate_thenReturnPredicateToFindPatternInTheList() {
    List<String> namesToValidate = Arrays.asList("Fabio Silva", "Mr. Silva");
    Pattern firstLastNamePreCompiledPattern = Pattern.compile("[a-zA-Z]{3,} [a-zA-Z]{3,}");
    
    Predicate<String> patternsAsPredicate = firstLastNamePreCompiledPattern.asPredicate();
    List<String> validNames = namesToValidate.stream()
        .filter(patternsAsPredicate)
        .collect(Collectors.toList());

    assertEquals(1,validNames.size());
    assertTrue(validNames.contains("Fabio Silva"));
}
```

### 3.2 Java 11

**Java 11引入了asMatchPredicate方法**，该方法创建一个谓词，该谓词的行为就像它从输入序列创建一个匹配器然后调用匹配项：

```java
string -> matcher(string).matches();
```

让我们创建一个模式来匹配列表中的名称，该列表只有名字和姓氏，每个名字至少三个字母：

```java
@Test
public void givenPreCompiledPattern_whenCallAsMatchPredicate_thenReturnMatchPredicateToMatchesPattern() {
    List<String> namesToValidate = Arrays.asList("Fabio Silva", "Fabio Luis Silva");
    Pattern firstLastNamePreCompiledPattern = Pattern.compile("[a-zA-Z]{3,} [a-zA-Z]{3,}");
        
    Predicate<String> patternAsMatchPredicate = firstLastNamePreCompiledPattern.asMatchPredicate();
    List<String> validatedNames = namesToValidate.stream()
        .filter(patternAsMatchPredicate)
        .collect(Collectors.toList());

    assertTrue(validatedNames.contains("Fabio Silva"));
    assertFalse(validatedNames.contains("Fabio Luis Silva"));
}
```

## 4. 总结

在本教程中，我们看到**使用预编译模式为我们带来了卓越的性能**。

我们还了解了JDK 8和JDK 11中引入的三种新方法，它们使我们的生活更轻松。

这些示例的代码可在GitHub上的[java-11-1](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/core-java-modules/core-java-11)中获得，用于JDK 11片段，而[java-regex-1](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/core-java-modules/core-java-regex)则用于其他片段。