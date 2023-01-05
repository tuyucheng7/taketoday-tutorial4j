## 1. 概述

在本文中，我们将研究属性测试的概念及其在vavr-test库中的实现。

基于属性的测试(PBT)允许我们指定程序关于它应该遵守的不变量的高级行为。

## 2. 什么是属性测试？

属性是不变量与输入值生成器的组合。对于每个生成的值，不变量被视为谓词并检查它是否为该值产生true或false。

一旦有一个值产生false，就说该属性被伪造了，检查就中止了。如果在特定数量的样本数据后无法使属性无效，则假定满足该属性。

由于这种行为，如果不满足条件而不做不必要的工作，我们的测试就会快速失败。

## 3. Maven依赖

首先，我们需要向[vavr-test](https://search.maven.org/search?q=a:vavr-test)库添加一个Maven依赖项：

```xml
<dependency>
    <groupId>io.vavr</groupId>
    <artifactId>vavr-test</artifactId>
    <version>${vavr.test.version}</version>
</dependency>

<properties>
    <vavr.test.version>2.0.5</vavr.test.version> 
</properties>
```

## 4. 编写基于属性的测试

让我们考虑一个返回字符串流的函数。它是一个从0向上的无限流，它根据简单规则将数字映射到字符串。我们在这里使用一个有趣的Vavr功能，称为[模式匹配](https://www.baeldung.com/vavr-pattern-matching)：

```java
private static Predicate<Integer> divisibleByTwo = i -> i % 2 == 0;
private static Predicate<Integer> divisibleByFive = i -> i % 5 == 0;

private Stream<String> stringsSupplier() {
    return Stream.from(0).map(i -> Match(i).of(
        Case($(divisibleByFive.and(divisibleByTwo)), "DividedByTwoAndFiveWithoutRemainder"),
        Case($(divisibleByFive), "DividedByFiveWithoutRemainder"),
        Case($(divisibleByTwo), "DividedByTwoWithoutRemainder"),
        Case($(), "")));
}
```

为这种方法编写单元测试很容易出错，因为我们很可能会忘记一些边缘情况，基本上不会涵盖所有可能的场景。

幸运的是，我们可以编写一个基于属性的测试来覆盖我们所有的边缘情况。首先，我们需要定义哪种类型的数字应该作为我们测试的输入：

```java
Arbitrary<Integer> multiplesOf2 = Arbitrary.integer()
  	.filter(i -> i > 0)
  	.filter(i -> i % 2 == 0 && i % 5 != 0);
```

我们指定输入数字需要满足两个条件-它需要大于零，并且需要可以被2无余数整除但不能被5整除。

接下来，我们需要定义一个条件来检查被测试的函数是否为给定参数返回正确的值：

```java
CheckedFunction1<Integer, Boolean> mustEquals
  	= i -> stringsSupplier().get(i).equals("DividedByTwoWithoutRemainder");
```

要开始基于属性的测试，我们需要使用[Property](https://static.javadoc.io/io.javaslang/javaslang-test/2.0.4/javaslang/test/Property.Property5.html)类：

```java
CheckResult result = Property
    .def("Every second element must equal to DividedByTwoWithoutRemainder")
    .forAll(multiplesOf2)
    .suchThat(mustEquals)
    .check(10_000, 100);

result.assertIsSatisfied();
```

我们指定，对于所有2的倍数的任意整数，必须满足mustEquals谓词。check()方法采用生成的输入的大小和此测试将运行的次数。

我们可以快速编写另一个测试来验证stringsSupplier()函数是否为每个可被2和5整除而没有余数的输入数字返回DividedByTwoAndFiveWithoutRemainder字符串。

Arbitrary供应商和CheckedFunction需要更改：

```java
Arbitrary<Integer> multiplesOf5 = Arbitrary.integer()
  	.filter(i -> i > 0)
  	.filter(i -> i % 5 == 0 && i % 2 == 0);

CheckedFunction1<Integer, Boolean> mustEquals
  	= i -> stringsSupplier().get(i).endsWith("DividedByTwoAndFiveWithoutRemainder");
```

然后我们可以运行基于属性的测试一千次迭代：

```java
Property.def("Every fifth element must equal to DividedByTwoAndFiveWithoutRemainder")
    .forAll(multiplesOf5)
    .suchThat(mustEquals)
    .check(10_000, 1_000)
    .assertIsSatisfied();
```

## 5. 总结

在这篇简短的文章中，我们了解了基于属性的测试的概念。

我们使用vavr-test库创建了测试；我们使用Arbitrary、CheckedFunction和Property类来使用vavr-test定义基于属性的测试。