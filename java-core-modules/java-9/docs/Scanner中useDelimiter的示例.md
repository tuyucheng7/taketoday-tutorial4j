## 1. 概述

在本教程中，我们将了解如何使用Scanner类的useDelimiter方法。

## 2. java.util.Scanner简介

Scanner API提供了一个简单的文本扫描器。默认情况下，Scanner使用空格作为分隔符将其输入拆分为字符串。让我们编写一个函数：

-   将输入传递给Scanner
-   遍历Scanner以收集列表中的字符串

以下是基本实现：

```java
public static List<String> baseScanner(String input) {
    try (Scanner scan = new Scanner(input)) {
        List<String> result = new ArrayList<String>();
        scan.forEachRemaining(result::add);
        return result;
    }
}
```

请注意，在这段代码中，我们使用了try-with-resources来创建Scanner，因为Scanner类实现了AutoCloseable接口。该代码块负责自动关闭Scanner资源。在Java 7之前，我们不能使用try-with-resources，因此必须手动处理它。

我们还可以注意到，为了迭代Scanner元素，我们使用了forEachRemaining方法。这个方法是在Java 8中引入的。Scanner实现了Iterator，如果我们使用的是较旧的Java版本，我们就必须利用它来遍历元素。

正如我们所说，Scanner默认使用空格来解析其输入。例如，使用以下输入调用我们的baseScanner方法：“Welcome to Tuyucheng”，应该返回一个包含以下有序元素的集合：“Welcome”、“to”、“Tuyucheng”。

让我们编写一个测试来检查我们的方法是否符合预期：

```java
@Test
void whenBaseScanner_ThenWhitespacesAreUsedAsDelimiters() {
    assertEquals(List.of("Welcome", "to", "Tuyucheng"), baseScanner("Welcome to Tuyucheng"));
}
```

## 3. 使用自定义分隔符

现在让我们将Scanner设置为使用自定义分隔符。我们传入一个字符串，Scanner将使用该字符串来中断输入：

```java
public static List<String> scannerWithDelimiter(String input, String delimiter) {
    try (Scanner scan = new Scanner(input)) {
        scan.useDelimiter(delimiter); 
        List<String> result = new ArrayList<String>();
        scan.forEachRemaining(result::add);
        return result;
    }
}
```

让我们评论几个例子：

-   我们可以使用单个字符作为分隔符：如果需要，必须对字符进行转义。例如，如果我们想模仿基本行为并使用空格作为分隔符，我们将使用“\\\\s”
    
-   我们可以使用任何单词/短语作为分隔符
-   我们可以使用多个可能的字符作为分隔符：为此，我们必须用“|”分隔它们。例如，如果我们想在每个空格和每个换行符之间拆分输入，我们将使用以下分隔符：“n|\\\s”
-   简而言之，我们可以使用任何类型的正则表达式作为分隔符：例如，“a+”是一个有效的分隔符

下面的测试用例演示了以上的第一种情况：

```java
@Test
void givenSimpleCharacterDelimiter_whenScannerWithDelimiter_ThenInputIsCorrectlyParsed() {
    assertEquals(List.of("Welcome", "to", "Tuyucheng"), scannerWithDelimiter("Welcome to Tuyucheng", "s"));
}
```

实际上，useDelimiter方法会将其输入转换为封装在Pattern对象中的正则表达式。或者，我们也可以自己处理Pattern的实例化。为此，我们需要使用重写的useDelimiter(Pattern pattern)，如下所示：

```java
public static List<String> scannerWithDelimiterUsingPattern(String input, Pattern delimiter) {
    try (Scanner scan = new Scanner(input)) {
        scan.useDelimiter(delimiter); 
        List<String> result = new ArrayList<String>();
        scan.forEachRemaining(result::add);
        return result;
    }
}
```

要实例化Pattern，我们可以使用compile方法：

```java
@Test
void givenStringDelimiter_whenScannerWithDelimiterUsingPattern_ThenInputIsCorrectlyParsed() {
    assertEquals(List.of("Welcome", "to", "Tuyucheng"), DelimiterDemo.scannerWithDelimiterUsingPattern("Welcome to Tuyucheng", Pattern.compile("s")));
}
```

## 4. 总结

在本文中，我们演示了几个可用于调用useDelimiter函数的模式示例。默认情况下，Scanner使用空白分隔符，并且我们指出使用任何类型的正则表达式作为分隔符。