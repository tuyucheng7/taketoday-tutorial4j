## 1. 概述

Scanner类是一个方便的工具，可以[使用](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Scanner.html)正则表达式解析原始类型和字符串，并在Java5 中引入到java.util包中。

在这个简短的教程中，我们将讨论它的hasNext()和hasNextLine()方法。尽管这两种方法乍一看可能非常相似，但它们实际上进行的检查却截然不同。

[你还可以在此处的快速指南中](https://www.baeldung.com/java-scanner)阅读有关多功能 Scanner 类的更多信息。

## 2. hasNext()

### 2.1. 基本用法

hasNext [()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Scanner.html#hasNext())方法检查Scanner在其输入中是否有另一个标记。扫描仪使用分隔符模式将其输入分解为标记，默认情况下匹配[空格](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Character.html#isWhitespace(char))。也就是说，hasNext()检查输入并在它有另一个非空白字符时返回true 。

我们还应该注意有关默认定界符的一些细节：

-   空白不仅包括空格字符，还包括制表符空格 ( t )、换行符 ( n )，甚至[更多字符](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Character.html#isWhitespace(char))
-   连续的空白字符被视为单个分隔符
-   不打印输入末尾的空行——也就是说，hasNext()对空行返回false

让我们看一下hasNext()如何使用默认定界符的示例。首先，我们将准备一个输入字符串来帮助我们探索 S canner的解析结果：

```java
String INPUT = new StringBuilder()
    .append("magictprojectn")
    .append("     database: oraclen")
    .append("dependencies:n")
    .append("spring:foo:barn")
    .append("n")  // Note that the input ends with a blank line
    .toString();
```

接下来，让我们解析输入并打印结果：

```java
Scanner scanner = new Scanner(INPUT);
while (scanner.hasNext()) {
    log.info(scanner.next());
}
log.info("--------OUTPUT--END---------")

```

如果我们运行上面的代码，我们将看到控制台输出：

```java
[DEMO]magic
[DEMO]project
[DEMO]database:
[DEMO]oracle
[DEMO]dependencies:
[DEMO]spring:foo:bar
[DEMO]--------OUTPUT--END---------

```

### 2.2. 使用自定义分隔符

到目前为止，我们已经了解了带有默认定界符的hasNext() 。Scanner类提供了一个[useDelimiter(String pattern)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Scanner.html#useDelimiter(java.lang.String))方法，允许我们更改分隔符。更改分隔符后，hasNext()方法将使用新分隔符而不是默认分隔符进行检查。

让我们看另一个 hasNext() 和 next() 如何使用自定义分隔符的示例。我们将重用上一个示例的输入。

在扫描器解析出与字符串“ dependencies: ”匹配的标记后，我们将分隔符更改为冒号(:)，以便我们可以解析和提取依赖项的每个值：

```java
while (scanner.hasNext()) {
    String token = scanner.next();
    if ("dependencies:".equals(token)) {
        scanner.useDelimiter(":");
    }
    log.info(token);
}
log.info("--------OUTPUT--END---------");
```

让我们看看结果输出：

```plaintext
[DEMO]magic
[DEMO]project
[DEMO]database:
[DEMO]oracle
[DEMO]dependencies:
[DEMO]
spring
[DEMO]foo
[DEMO]bar


[DEMO]--------OUTPUT--END---------
```

伟大的！我们已经成功提取了“ dependencies ”中的值，但是，有一些意想不到的换行问题。我们将在下一节中看到如何避免这些。

### 2.3. 以正则表达式作为分隔符

让我们回顾一下上一节中的输出。首先，我们注意到在“ spring ”之前有一个换行符(n ) 。在获取“dependencies: ”标记后，我们将分隔符更改为“ : ”。“ dependencies: ”之后的换行符现在成为下一个标记的一部分。因此，hasNext() 返回true并打印出换行符。

同理，“ hibernate ”之后的换行和最后一个空行成为最后一个token的一部分，所以两个空行和“ hibernate ”一起打印出来。

如果我们可以将冒号和空格都作为分隔符，那么“依赖”值将被正确解析，我们的问题将得到解决。为此，让我们更改useDelimiter(“:”)调用：

```java
scanner.useDelimiter(":|s+");

```

这里的“ :|s+ ”是一个匹配单个“:”或一个或多个空白字符的正则表达式。通过此修复，输出变为：

```plaintext
[DEMO]magic
[DEMO]project
[DEMO]database:
[DEMO]oracle
[DEMO]dependencies:
[DEMO]spring
[DEMO]foo
[DEMO]bar
[DEMO]--------OUTPUT--END---------
```

## 3. hasNextLine()

hasNextLine [()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Scanner.html#hasNextLine())方法检查Scanner对象的输入中是否还有另一行，无论该行是否为空。

让我们再次采用相同的输入。这一次，我们将使用hasNextLine()和 nextLine()方法在输入的每一行前面添加行号：

```java
int i = 0;
while (scanner.hasNextLine()) {
    log.info(String.format("%d|%s", ++i, scanner.nextLine()));
}
log.info("--------OUTPUT--END---------");
```

现在，让我们看看我们的输出：

```plaintext
[DEMO]1|magic	project
[DEMO]2|     database: oracle
[DEMO]3|dependencies:
[DEMO]4|spring:foo:bar
[DEMO]5|
[DEMO]--------OUTPUT--END---------
```

正如我们所料，行号被打印出来，最后一个空白行也在那里。

## 4。总结

在本文中，我们了解到Scanner的hasNextLine()方法检查输入中是否还有另一行，无论该行是否为空，而hasNext()使用定界符检查另一个标记。