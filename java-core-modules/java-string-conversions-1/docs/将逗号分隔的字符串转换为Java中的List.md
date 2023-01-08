## 1. 概述

在本教程中，我们将着眼于将逗号分隔的字符串转换为字符串列表。此外，我们会将逗号分隔的整数字符串转换为整数列表。

## 2.依赖关系

我们将用于转换的一些方法需要 [Apache Commons Lang 3](https://search.maven.org/search?q=g:org.apache.commons AND a:commons-lang3)和[Guava](https://search.maven.org/search?q=a:guava AND g:com.google.guava)库。所以，让我们将它们添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

## 3. 定义我们的例子

在我们开始之前，让我们定义两个我们将在示例中使用的输入字符串。第一个字符串 countries 包含多个以逗号分隔的字符串，第二个字符串 ranks包含以逗号分隔的数字：

```java
String countries = "Russia,Germany,England,France,Italy";
String ranks = "1,2,3,4,5,6,7";
```

并且，在本教程中，我们会将上述字符串转换为字符串和整数列表，我们将存储在：

```java
List<String> convertedCountriesList;
List<Integer> convertedRankList;
```

最后，在我们执行转换后，预期的输出将是：

```java
List<String> expectedCountriesList = Arrays.asList("Russia", "Germany", "England", "France", "Italy");
List<Integer> expectedRanksList = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
```

## 4.核心Java

在我们的第一个解决方案中，我们将使用核心Java将字符串转换为字符串和整数列表。

首先，我们将使用 字符串类实用方法split 将字符串拆分[为字符串数组。](https://www.baeldung.com/java-split-string)然后，我们将在新的字符串数组上使用Arrays.asList将其转换为字符串列表：

```java
List<String> convertedCountriesList = Arrays.asList(countries.split(",", -1));
```

现在让我们把我们的数字串变成一个整数列表。

我们将使用split方法将数字字符串转换为字符串数组。然后，我们会将新数组中的每个字符串转换为整数并将其添加到我们的列表中：

```java
String[] convertedRankArray = ranks.split(",");
List<Integer> convertedRankList = new ArrayList<Integer>();
for (String number : convertedRankArray) {
    convertedRankList.add(Integer.parseInt(number.trim()));
}
```

在这两种情况下，我们都使用[String](https://www.baeldung.com/string/split)[类中的](https://www.baeldung.com/string/split)[split实用程序方法](https://www.baeldung.com/string/split)将以逗号分隔的字符串拆分为字符串数组。

请注意，用于转换我们的国家/地区字符串的[重载split方法](https://www.baeldung.com/string/split)包含第二个参数limit，我们为其提供的值为 -1。这指定应尽可能多地应用分隔符模式。

我们用来拆分整数字符串 ( ranks )的split方法使用零作为限制，因此它忽略了空字符串，而用于 countries字符串的拆分方法在返回的数组中保留了空字符串。

## 5.Java流

现在，我们将使用[Java Stream API](https://www.baeldung.com/java-streams)实现相同的转换。

首先，我们将使用[String](https://www.baeldung.com/string/split)[类中的](https://www.baeldung.com/string/split)[split](https://www.baeldung.com/string/split)[方法 将我们的](https://www.baeldung.com/string/split)国家/地区字符串转换为字符串数组。然后，我们将使用 Stream类将数组转换为字符串列表：

```java
List<String> convertedCountriesList = Stream.of(countries.split(",", -1))
  .collect(Collectors.toList());
```

让我们看看如何使用Stream将我们的数字字符串转换为整数列表。

同样，我们将首先使用split方法将数字字符串转换为字符串数组，然后使用 Stream 类中的of ()方法将结果数组转换为字符串流。

然后，我们将使用map(String :: trim) 修剪流中每个字符串的前导和尾随空格。

接下来，我们将在我们的流上应用 map(Integer::parseInt)以将我们Stream中的每个字符串转换为Integer。

最后，我们将 在Stream上调用collect(Collectors.toList())以将其转换为整数列表：

```java
List<Integer> convertedRankList = Stream.of(ranks.split(","))
  .map(String::trim)
  .map(Integer::parseInt)
  .collect(Collectors.toList());
```

## 6.Apache Commons 语言

在此解决方案中，我们将使用[Apache Commons Lang3](https://www.baeldung.com/java-commons-lang-3)库来执行我们的转换。Apache Commons Lang3 提供了几个辅助函数来操作核心Java类。

首先，我们将使用StringUtils.splitPreserveAllTokens将字符串拆分为字符串数组 。 然后，我们将使用 Arrays.asList方法将新的字符串数组转换为列表：

```java
List<String> convertedCountriesList = Arrays.asList(StringUtils.splitPreserveAllTokens(countries, ","));
```

现在让我们将数字字符串转换为整数列表。

我们将再次使用 StringUtils.split方法从我们的字符串创建一个字符串数组。然后，我们将使用Integer.parseInt 将新数组中的每个字符串转换为一个整数，并将转换后的整数添加到我们的列表中：

```java
String[] convertedRankArray = StringUtils.split(ranks, ",");
List<Integer> convertedRankList = new ArrayList<Integer>();
for (String number : convertedRankArray) {
    convertedRankList.add(Integer.parseInt(number.trim()));
}

```

在此示例中，我们使用splitPreserveAllTokens方法拆分我们的 国家/ 地区字符串，而我们使用split方法拆分我们的 等级字符串。

尽管这两个函数都将字符串拆分为数组，但 splitPreserveAllTokens 会 保留所有标记，包括由相邻分隔符创建的空字符串，而split方法会忽略空字符串。

因此，如果我们有要包含在列表中的空字符串，那么我们应该使用 splitPreserveAllTokens 而不是split。

## 7.番石榴

最后，我们将使用 [Guava](https://www.baeldung.com/category/guava/) 库将我们的字符串转换为适当的列表。

要转换我们的国家/ 地区字符串，我们将首先使用逗号作为参数调用Splitter.on来指定我们的字符串应该根据哪个字符进行拆分。

然后，我们将在Splitter实例上使用trimResults 方法。这将忽略创建的子字符串中的所有前导和尾随空格。

最后，我们将使用splitToList 方法拆分输入字符串并将其转换为列表：

```java
List<String> convertedCountriesList = Splitter.on(",")
  .trimResults()
  .splitToList(countries);
```

现在，让我们将数字字符串转换为整数列表。

 我们将再次使用与上面相同的过程将数字字符串转换为字符串列表。

然后，我们将使用Lists。transform方法，它接受我们的字符串列表作为第一个参数， Function 接口的实现作为第二个参数。

Function 接口实现将列表中的每个字符串转换为整数：

```java
List<Integer> convertedRankList = Lists.transform(Splitter.on(",")
  .trimResults()
  .splitToList(ranks), new Function<String, Integer>() {
      @Override
      public Integer apply(String input) {
          return Integer.parseInt(input.trim());
      }
  });

```

## 八、总结

在本文中，我们将逗号分隔的字符串转换为字符串列表和整数列表。但是，我们可以按照类似的过程将String转换为任何原始数据类型的列表。