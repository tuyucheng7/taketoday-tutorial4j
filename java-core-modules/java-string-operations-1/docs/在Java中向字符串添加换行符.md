## 1. 概述

字符串格式化和生成文本输出经常在编程过程中出现。在许多情况下，需要向字符串添加新行以格式化输出。

让我们讨论如何使用换行符。

## 延伸阅读：

## [在Java中检查空字符串或空白字符串](https://www.baeldung.com/java-blank-empty-strings)

查看Java中的一些简单方法来测试字符串是否为空。

[阅读更多](https://www.baeldung.com/java-blank-empty-strings)→

## [检查字符串是否包含子字符串](https://www.baeldung.com/java-string-contains-substring)

使用性能基准探索在字符串中搜索子字符串的各种方法

[阅读更多](https://www.baeldung.com/java-string-contains-substring)→

## 2. 在字符串中添加换行符

操作系统有特殊字符表示新行的开始。例如，在 Linux 中，新行用“ n”表示，也称为 换行符。在 Windows 中，新行使用“ rn”表示，有时称为 回车 换行或CRLF。

在Java中添加新行就像在字符串末尾包含“ n”、“ r” 或“  r n” 一样简单。

### 2.1. 使用 CRLF 换行符

对于此示例，我们要使用两行文本创建一个段落。具体来说，我们希望line2出现在line1之后的新行中。

对于基于 Unix/Linux/New Mac 的操作系统，我们可以使用“ n”：

```java
String line1 = "Humpty Dumpty sat on a wall.";
String line2 = "Humpty Dumpty had a great fall.";
String rhyme = line1 + "n" + line2;
```

如果我们在基于 Windows 的操作系统上，我们可以使用“ rn”：

```java
rhyme = line1 + "rn" + line2;
```

对于旧的基于 Mac 的操作系统，我们可以使用“ r”：

```java
rhyme = line1 + "r" + line2;
```

我们已经演示了三种添加新行的方法，但不幸的是，它们依赖于平台。

### 2.2. 使用平台无关的行分隔符

当我们希望我们的代码独立于平台时，我们可以使用系统定义的常量。

例如，使用 System.lineSeparator()给出行分隔符：

```java
rhyme = line1 + System.lineSeparator() + line2;
```

或者我们也可以使用 System.getProperty(“line.separator”)：

```java
rhyme = line1 + System.getProperty("line.separator") + line2;
```

### 2.3. 使用与平台无关的换行符

尽管行分隔符提供了平台独立性，但它们迫使我们连接我们的字符串。

如果我们使用[System.out.printf](https://www.baeldung.com/java-printstream-printf)或 [String.format](https://www.baeldung.com/string/format)之类的东西，那么平台无关的换行符%n可以直接在字符串中使用：

```java
rhyme = "Humpty Dumpty sat on a wall.%nHumpty Dumpty had a great fall.";
```

这与在我们的字符串中包含System.lineSeparator()相同，但我们不需要将字符串分成多个部分。

## 3. 在 HTML 页面中添加换行符

假设我们正在创建一个字符串，它是 HTML 页面的一部分。在这种情况下，我们可以添加一个 HTML 中断标记 <br>。

我们还可以使用 Unicode 字符“ ” (回车)和“ ” (换行)。 尽管这些角色可以工作，但它们在所有平台上的工作方式并不完全像我们期望的那样。相反，最好使用<br>来换行。

此外，我们可以在某些 HTML 元素中使用“n”来换行。

总的来说，这是在 HTML 中换行的三种方法。我们可以根据我们使用的 HTML 标签来决定使用哪一个。

### 3.1. HTML 中断标记

我们可以使用 HTML break 标签<br>来换行：

```java
rhyme = line1 + "<br>" + line2;
```

用于换行的<br>标签适用于几乎所有 HTML 元素，如<body>、<p>、<pre>等。但是，请注意它不适用于<textarea>标签。

### 3.2. 换行符

如果文本包含在<pre>或<textarea>标记中，我们可以使用'n'来换行：

```java
rhyme = line1 + "n" + line2;
```

### 3.3. Unicode 字符

最后，我们可以使用 Unicode 字符“ ” (回车)和“ ” (换行)换行。例如，在<textarea>标签中，我们可以使用以下任意一种：

```java
rhyme = line1 + "
" + line2;
rhyme = line1 + "
" + line2;

```

对于 <pre>标签，下面两行都有效：

```java
rhyme = line1 + "
" + line2;
rhyme = line1 + "

" + line2;

```

## 4. n和r的区别

r 和n分别是用 ASCII 值 13 (CR) 和 10 (LF) 表示的字符。它们 都代表两条线之间的中断，但操作系统使用它们的方式不同。 

在 Windows 上，两个字符的序列用于开始一个新行，CR 紧跟 LF。相反，在类 Unix 系统上，只使用 LF。

在编写Java应用程序时，我们必须注意使用的换行符，因为应用程序的行为会因运行的操作系统而异。

最安全和最交叉兼容的选择是使用System.lineSeparator()。这样，我们就不必考虑操作系统了。

## 5.总结

在本文中，我们讨论了如何在Java中向字符串添加换行符。

我们还了解了如何使用System.lineSeparator()和System.getProperty(“line.separator”)为换行编写平台独立代码。

最后，我们总结了如何在生成 HTML 页面时添加新行。