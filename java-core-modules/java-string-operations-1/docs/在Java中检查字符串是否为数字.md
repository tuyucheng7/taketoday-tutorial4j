## 1. 概述

通常在对String进行操作时，我们需要弄清楚String是否是有效数字。

在本教程中，我们将探索多种方法来检测给定的String是否为数字，首先使用纯 Java，然后使用正则表达式，最后使用外部库。

讨论完各种实现后，我们将使用基准来了解哪些方法是最佳的。

## 延伸阅读：

## [Java 字符串转换](https://www.baeldung.com/java-string-conversions)

快速实用的示例侧重于将 String 对象转换为Java中的不同数据类型。

[阅读更多](https://www.baeldung.com/java-string-conversions)→

## [Java 正则表达式 API 指南](https://www.baeldung.com/regular-expressions-java)

Java 中正则表达式 API 的实用指南。

[阅读更多](https://www.baeldung.com/regular-expressions-java)→

## [了解Java中的 NumberFormatException](https://www.baeldung.com/java-number-format-exception)

了解Java中 NumberFormatException 的各种原因以及避免它的一些最佳实践。

[阅读更多](https://www.baeldung.com/java-number-format-exception)→

## 2.先决条件

在我们进入主要内容之前，让我们先了解一些先决条件。

在本文的后半部分，我们将使用 Apache Commons 外部库将其依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

这个库的最新版本可以在[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.commons" AND a%3A"commons-lang3")上找到。

## 3. 使用纯 Java

检查String是否为数字的最简单和最可靠的方法可能是使用Java的内置方法对其进行解析：

1.  Integer.parseInt(字符串)
2.  Float.parseFloat(字符串)
3.  Double.parseDouble(字符串)
4.  Long.parseLong(字符串)
5.  新的大整数(字符串)

如果这些方法没有抛出任何[NumberFormatException](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/NumberFormatException.html)，则意味着解析成功并且String是数字：

```java
public static boolean isNumeric(String strNum) {
    if (strNum == null) {
        return false;
    }
    try {
        double d = Double.parseDouble(strNum);
    } catch (NumberFormatException nfe) {
        return false;
    }
    return true;
}
```

让我们看看这个方法的实际效果：

```java
assertThat(isNumeric("22")).isTrue();
assertThat(isNumeric("5.05")).isTrue();
assertThat(isNumeric("-200")).isTrue(); 
assertThat(isNumeric("10.0d")).isTrue();
assertThat(isNumeric("   22   ")).isTrue();
 
assertThat(isNumeric(null)).isFalse();
assertThat(isNumeric("")).isFalse();
assertThat(isNumeric("abc")).isFalse();
```

在我们的isNumeric()方法中，我们只是检查Double类型的值；但是，我们也可以修改此方法以使用我们之前使用的任何解析方法来检查Integer、Float、Long和大数字。

这些方法也在 [Java String Conversions](https://www.baeldung.com/java-string-conversions)一文中讨论。

## 4.使用正则表达式

现在让我们使用正则表达式-?d+(.d+)? 匹配由正整数或负整数和浮点数组成的数字字符串。

不用说，我们绝对可以修改这个正则表达式来识别和处理范围广泛的规则。在这里，我们将保持简单。

让我们分解这个正则表达式，看看它是如何工作的：

-   -？ – 这部分标识给定数字是否为负数，破折号“ - ”按字面意思搜索破折号，问号“ ？ ” ” 将它的存在标记为可选的
-   d+ – 搜索一位或多位数字
-   (.d+)? – 这部分正则表达式用于识别浮点数。在这里，我们搜索一个或多个数字后跟一个句点。最后的问号表示这个完整的组是可选的。

正则表达式是一个非常广泛的话题。要获得简要概述，请查看我们关于[Java 正则表达式 API](https://baeldung.com/regular-expressions-java)的教程。

现在，让我们使用上面的正则表达式创建一个方法：

```java
private Pattern pattern = Pattern.compile("-?d+(.d+)?");

public boolean isNumeric(String strNum) {
    if (strNum == null) {
        return false; 
    }
    return pattern.matcher(strNum).matches();
}
```

现在让我们看一下上述方法的一些断言：

```java
assertThat(isNumeric("22")).isTrue();
assertThat(isNumeric("5.05")).isTrue();
assertThat(isNumeric("-200")).isTrue();

assertThat(isNumeric(null)).isFalse();
assertThat(isNumeric("abc")).isFalse();
```

##  5. 使用 Apache Commons

在本节中，我们将讨论 Apache Commons 库中可用的各种方法。

### 5.1. NumberUtils.isCreatable(字符串) 

[Apache Commons 的NumberUtils](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/math/NumberUtils.html)提供了一个静态方法[NumberUtils.isCreatable(String)](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/math/NumberUtils.html#isCreatable-java.lang.String-)，它检查一个String是否是一个有效的Java数字。

此方法接受：

1.  以 0x 或 0X 开头的十六进制数
2.  以前导 0 开头的八进制数
3.  科学计数法(例如 1.05e-10)
4.  标有类型限定符的数字(例如 1L 或 2.2d)

如果提供的字符串为null或empty/blank，则它不被视为数字，该方法将返回false。

让我们使用此方法运行一些测试：

```java
assertThat(NumberUtils.isCreatable("22")).isTrue();
assertThat(NumberUtils.isCreatable("5.05")).isTrue();
assertThat(NumberUtils.isCreatable("-200")).isTrue();
assertThat(NumberUtils.isCreatable("10.0d")).isTrue();
assertThat(NumberUtils.isCreatable("1000L")).isTrue();
assertThat(NumberUtils.isCreatable("0xFF")).isTrue();
assertThat(NumberUtils.isCreatable("07")).isTrue();
assertThat(NumberUtils.isCreatable("2.99e+8")).isTrue();
 
assertThat(NumberUtils.isCreatable(null)).isFalse();
assertThat(NumberUtils.isCreatable("")).isFalse();
assertThat(NumberUtils.isCreatable("abc")).isFalse();
assertThat(NumberUtils.isCreatable(" 22 ")).isFalse();
assertThat(NumberUtils.isCreatable("09")).isFalse();
```

请注意，我们分别在第 6、7 和 8 行中获得了十六进制数、八进制数和科学记数法的真断言。

另外，在第 14 行，字符串“09”返回false，因为前面的“0”表示这是一个八进制数，而“09”不是有效的八进制数。

对于使用此方法返回true的每个输入，我们可以使用[NumberUtils.createNumber(String)](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/math/NumberUtils.html#createNumber-java.lang.String-)，这将为我们提供有效数字。

### 5.2. NumberUtils.isParsable(字符串) 

NumberUtils.isParsable [(String)](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/math/NumberUtils.html#isParsable-java.lang.String-)方法检查给定的String是否可解析。

可解析的数字是那些被任何解析方法成功解析的数字，如Integer.parseInt(String)、Long.parseLong(String)、Float.parseFloat(String)或Double.parseDouble(String)。

与NumberUtils.isCreatable()不同，此方法不接受十六进制数字、科学记数法或以任何类型的限定符(如'f'、'F'、'd'、'D'、'l'或'L ' 结尾的字符串'。

让我们看一些肯定：

```java
assertThat(NumberUtils.isParsable("22")).isTrue();
assertThat(NumberUtils.isParsable("-23")).isTrue();
assertThat(NumberUtils.isParsable("2.2")).isTrue();
assertThat(NumberUtils.isParsable("09")).isTrue();

assertThat(NumberUtils.isParsable(null)).isFalse();
assertThat(NumberUtils.isParsable("")).isFalse();
assertThat(NumberUtils.isParsable("6.2f")).isFalse();
assertThat(NumberUtils.isParsable("9.8d")).isFalse();
assertThat(NumberUtils.isParsable("22L")).isFalse();
assertThat(NumberUtils.isParsable("0xFF")).isFalse();
assertThat(NumberUtils.isParsable("2.99e+8")).isFalse();
```

在第 4 行，与NumberUtils.isCreatable()不同，以字符串“0”开头的数字不被视为八进制数，而是普通的十进制数，因此它返回 true。

我们可以使用此方法来替代我们在第 3 节中所做的操作，我们在第 3 节中尝试解析数字并检查错误。

### 5.3. StringUtils.isNumeric(CharSequence ) 

[StringUtils.isNumeric(CharSequence)](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/StringUtils.html#isNumeric-java.lang.CharSequence-)方法严格检查 Unicode 数字。这表示：

1.  来自任何语言的 Unicode 数字的任何数字都是可接受的
2.  由于小数点不被视为 Unicode 数字，因此无效
3.  前导标志(正面或负面)也不可接受

现在让我们看看这个方法的实际效果：

```java
assertThat(StringUtils.isNumeric("123")).isTrue();
assertThat(StringUtils.isNumeric("١٢٣")).isTrue();
assertThat(StringUtils.isNumeric("१२३")).isTrue();
 
assertThat(StringUtils.isNumeric(null)).isFalse();
assertThat(StringUtils.isNumeric("")).isFalse();
assertThat(StringUtils.isNumeric("  ")).isFalse();
assertThat(StringUtils.isNumeric("12 3")).isFalse();
assertThat(StringUtils.isNumeric("ab2c")).isFalse();
assertThat(StringUtils.isNumeric("12.3")).isFalse();
assertThat(StringUtils.isNumeric("-123")).isFalse();
```

请注意，第 2 行和第 3 行中的输入参数分别表示阿拉伯语和梵文的数字123。由于它们是有效的 Unicode 数字，因此此方法对它们返回true。

### 5.4. StringUtils.isNumericSpace(CharSequence)

StringUtils.isNumericSpace ( [CharSequence)](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/StringUtils.html#isNumericSpace-java.lang.CharSequence-)严格检查 Unicode 数字和/或空格。这与StringUtils.isNumeric()相同，只是它也接受空格，不仅前导空格和尾随空格，而且如果它们位于数字之间：

```java
assertThat(StringUtils.isNumericSpace("123")).isTrue();
assertThat(StringUtils.isNumericSpace("١٢٣")).isTrue();
assertThat(StringUtils.isNumericSpace("")).isTrue();
assertThat(StringUtils.isNumericSpace("  ")).isTrue();
assertThat(StringUtils.isNumericSpace("12 3")).isTrue();
 
assertThat(StringUtils.isNumericSpace(null)).isFalse();
assertThat(StringUtils.isNumericSpace("ab2c")).isFalse();
assertThat(StringUtils.isNumericSpace("12.3")).isFalse();
assertThat(StringUtils.isNumericSpace("-123")).isFalse();
```

## 6. 基准

在结束本文之前，让我们通过一些基准测试结果来帮助我们分析上述哪种方法最适合我们的用例。

### 6.1. 简单基准

首先，我们采用一种简单的方法。我们选择一个字符串值——我们使用Integer.MAX_VALUE进行测试。然后将针对我们所有的实现对该值进行测试：

```bash
Benchmark                                     Mode  Cnt    Score   Error  Units
Benchmarking.usingCoreJava                    avgt   20   57.241 ± 0.792  ns/op
Benchmarking.usingNumberUtils_isCreatable     avgt   20   26.711 ± 1.110  ns/op
Benchmarking.usingNumberUtils_isParsable      avgt   20   46.577 ± 1.973  ns/op
Benchmarking.usingRegularExpressions          avgt   20  101.580 ± 4.244  ns/op
Benchmarking.usingStringUtils_isNumeric       avgt   20   35.885 ± 1.691  ns/op
Benchmarking.usingStringUtils_isNumericSpace  avgt   20   31.979 ± 1.393  ns/op
```

正如我们所见，成本最高的操作是正则表达式。之后是我们基于Java的核心解决方案。

此外，请注意使用 Apache Commons 库的操作大体上是相同的。

### 6.2. 增强基准

让我们使用一组更多样化的测试来获得更具代表性的基准：

-   95 个值是数字(0-94 和Integer.MAX_VALUE)
-   3 包含数字但格式仍然错误 —“ x0 ”、“ 0..005 ”和“ –11 ”
-   1 仅包含文本
-   1 为空

执行相同的测试后，我们将看到结果：

```bash
Benchmark                                     Mode  Cnt      Score     Error  Units
Benchmarking.usingCoreJava                    avgt   20  10162.872 ± 798.387  ns/op
Benchmarking.usingNumberUtils_isCreatable     avgt   20   1703.243 ± 108.244  ns/op
Benchmarking.usingNumberUtils_isParsable      avgt   20   1589.915 ± 203.052  ns/op
Benchmarking.usingRegularExpressions          avgt   20   7168.761 ± 344.597  ns/op
Benchmarking.usingStringUtils_isNumeric       avgt   20   1071.753 ±   8.657  ns/op
Benchmarking.usingStringUtils_isNumericSpace  avgt   20   1157.722 ±  24.139  ns/op
```

最重要的区别是我们的两个测试，正则表达式解决方案和基于Java的核心解决方案，交换了位置。

从这个结果中，我们了解到NumberFormatException的抛出和处理(仅在 5% 的情况下发生)对整体性能的影响相对较大。因此我们可以得出总结，最佳解决方案取决于我们的预期输入。

此外，我们可以安全地得出总结，我们应该使用 Commons 库中的方法或类似实现的方法以获得最佳性能。

## 七、总结

在本文中，我们探讨了查找字符串是否为数字的不同方法。我们研究了两种解决方案——内置方法和外部库。

与往常一样，上面给出的所有示例和代码片段的实现，包括用于执行基准测试的代码，都可以[在 GitHub 上找到](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-string-operations)。