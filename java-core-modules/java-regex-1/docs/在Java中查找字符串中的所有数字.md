## 1. 概述

有时我们需要在字符串中查找数字或完整数字。我们可以使用正则表达式或某些库函数来做到这一点。

在本文中，我们将使用正则表达式来查找和提取字符串中的数字。我们还将介绍一些计算数字的方法。

## 2.计算数字

让我们从计算在字符串中找到的数字开始。

### 2.1. 使用正则表达式

我们可以使用[Java 正则表达式](https://www.baeldung.com/regular-expressions-java)来计算一个数字的[匹配次数。](https://www.baeldung.com/java-count-regex-matches)

在正则表达式中，“ d ”匹配“任何单个数字”。让我们使用这个表达式来计算字符串中的数字：

```java
int countDigits(String stringToSearch) {
    Pattern digitRegex = Pattern.compile("d");
    Matcher countEmailMatcher = digitRegex.matcher(stringToSearch);

    int count = 0;
    while (countEmailMatcher.find()) {
        count++;
    }

    return count;
}
```

一旦我们为正则表达式定义了一个匹配器，我们就可以在循环中使用它来查找和计算所有匹配项。让我们测试一下：

```java
int count = countDigits("64x6xxxxx453xxxxx9xx038x68xxxxxx95786xxx7986");

assertThat(count, equalTo(21));
```

### 2.2. 使用 Google Guava CharMatcher

要使用[Guava](https://search.maven.org/classic/#search|ga|1|g%3A"com.google.guava" AND a%3A"guava")，我们首先需要添加 Maven 依赖：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

Guava 提供了[CharMatcher.inRange](https://guava.dev/releases/30.0-jre/api/docs/com/google/common/base/CharMatcher.html#inRange(char,char))方法来计算数字：

```java
int count = CharMatcher.inRange('0', '9')
  .countIn("64x6xxxxx453xxxxx9xx038x68xxxxxx95786xxx7986");

assertThat(count, equalTo(21));
```

## 3. 查找数字

计算数字需要捕获有效数字表达式的所有数字的模式。

### 3.1. 寻找整数

要构造一个表达式来识别整数，我们必须考虑它们可以是正数或负数，并且由一个或多个数字的序列组成。我们还注意到负整数前面有一个负号。

因此，我们可以通过将正则表达式扩展为“ -?d+ ”来找到整数。此模式的意思是“一个可选的减号，后跟一个或多个数字”。

让我们创建一个使用此正则表达式在字符串中查找整数的示例方法：

```java
List<String> findIntegers(String stringToSearch) {
    Pattern integerPattern = Pattern.compile("-?d+");
    Matcher matcher = integerPattern.matcher(stringToSearch);

    List<String> integerList = new ArrayList<>();
    while (matcher.find()) {
        integerList.add(matcher.group());
    }

    return integerList;
}
```

一旦我们在正则表达式上创建了一个匹配器，我们就可以在循环中使用它来查找字符串中的所有整数。我们在每场比赛中调用group以获取所有整数。

让我们测试一下findIntegers：

```java
List<String> integersFound = 
  findIntegers("646xxxx4-53xxx34xxxxxxxxx-35x45x9xx3868xxxxxx-95786xxx79-86");

assertThat(integersFound)
  .containsExactly("646", "4", "-53", "34", "-35", "45", "9", "3868", "-95786", "79", "-86");

```

### 3.2. 查找小数

要创建一个[查找十进制数的](https://www.baeldung.com/java-check-string-number#regex)正则表达式，我们需要考虑编写它们时使用的字符模式。

如果十进制数是负数，则它以负号开头。随后是一个或多个数字和一个可选的小数部分。这个小数部分以小数点开始，后面是一个或多个数字的另一个序列。

我们可以使用正则表达式“-?d+(.d+)? “：

```java
List<String> findDecimalNums(String stringToSearch) {
    Pattern decimalNumPattern = Pattern.compile("-?d+(.d+)?");
    Matcher matcher = decimalNumPattern.matcher(stringToSearch);

    List<String> decimalNumList = new ArrayList<>();
    while (matcher.find()) {
        decimalNumList.add(matcher.group());
    }

    return decimalNumList;
}
```

现在我们将测试findDecimalNums：

```java
List<String> decimalNumsFound = 
  findDecimalNums("x7854.455xxxxxxxxxxxx-3x-553.00x53xxxxxxxxxxxxx3456xxxxxxxx3567.4xxxxx");

assertThat(decimalNumsFound)
  .containsExactly("7854.455", "-3", "-553.00", "53", "3456", "3567.4");

```

## 4.将找到的字符串转换为数值

我们可能还希望将找到的数字转换为它们的Java类型。

让我们使用[Stream](https://www.baeldung.com/java-streams)[映射](https://www.baeldung.com/java-streams)将我们的整数转换为Long：

```java
LongStream integerValuesFound = findIntegers("x7854x455xxxxxxxxxxxx-3xxxxxx34x56")
  .stream()
  .mapToLong(Long::valueOf);
        
assertThat(integerValuesFound)
  .containsExactly(7854L, 455L, -3L, 34L, 56L);
```

接下来，我们将以相同的方式将小数转换为Double ：

```java
DoubleStream decimalNumValuesFound = findDecimalNums("x7854.455xxxxxxxxxxxx-3xxxxxx34.56")
  .stream()
  .mapToDouble(Double::valueOf);

assertThat(decimalNumValuesFound)
  .containsExactly(7854.455, -3.0, 34.56);
```

## 5.寻找其他类型的数字

数字可以用其他格式表示，我们可以通过调整正则表达式来检测。

### 5.1. 科学计数法

让我们找到一些使用科学记数法格式化的数字：

```java
String strToSearch = "xx1.25E-3xxx2e109xxx-70.96E+105xxxx-8.7312E-102xx919.3822e+31xxx";

Matcher matcher = Pattern.compile("-?d+(.d+)?[eE][+-]?d+")
  .matcher(strToSearch);

// loop over the matcher

assertThat(sciNotationNums)
  .containsExactly("1.25E-3", "2e109", "-70.96E+105", "-8.7312E-102", "919.3822e+31");

```

### 5.2. 十六进制

现在我们将在字符串中查找十六进制数：

```java
String strToSearch = "xaF851Bxxx-3f6Cxx-2Ad9eExx70ae19xxx";

Matcher matcher = Pattern.compile("-?[0-9a-fA-F]+")
  .matcher(strToSearch);

// loop over the matcher

assertThat(hexNums)
  .containsExactly("aF851B", "-3f6C", "-2Ad9eE", "70ae19");

```

## 六，总结

在本文中，我们首先讨论了如何使用正则表达式和来自 Google Guava 的CharMatcher类对字符串中的数字进行计数。

然后，我们探索了使用正则表达式来查找整数和小数。

最后，我们介绍了查找其他格式的数字，例如科学记数法和十六进制。