## 1. 简介

在之前的教程中，我们看到了如何在任何Java版本中使用[多行字符串](https://www.baeldung.com/java-multiline-string)。

在本教程中，我们将详细了解**如何使用Java 15文本块功能**最有效地声明多行字符串。

## 2. 用法

从Java 15开始，文本块已成为标准功能提供。对于Java 13和14，我们需要将其作为[预览功能](https://openjdk.java.net/jeps/355)启用。

**文本块以“”“(三个双引号)开头，后跟可选的空格和换行符**。最简单的示例如下所示：

```java
String example = """
     Example text""";
```

请注意，文本块的结果类型仍然是String。文本块只是为我们提供了另一种在源代码中编写字符串文本的方法。

**在文本块内部，我们可以自由地使用换行符和引号，而无需转义换行符**。它允许我们以更优雅和可读的方式包含HTML、JSON、SQL或任何我们需要的文字片段。

**在生成的字符串中，不包括(基本)缩进和第一个换行符**。我们将在下一节中介绍缩进的处理。

## 3. 缩进

**幸运的是，在使用文本块时，我们仍然可以正确地缩进代码**。为了实现这一点，缩进的一部分被视为源代码，而缩进的另一部分被视为文本块的一部分。为使其工作，编译器会检查所有非空行中的最小缩进。接下来，编译器将整个文本块向左移动。

下面是包含一些HTML的文本块：

```java
public String getBlockOfHtml() {
    return """
            <html>

                <body>
                    <span>example text</span>
                </body>
            </html>""";
}
```

在这种情况下，最小缩进为12个空格。因此，<html\>左侧和所有后续行的所有12个空格都被删除，下面是一个测试例子：

```java
@Test
void givenAnOldStyleMultilineString_whenComparing_thenEqualsTextBlock() {
    String expected = "<html>\n"
      + "\n" 
      + "    <body>\n"
      + "        <span>example text</span>\n"
      + "    </body>\n"
      + "</html>";
    assertThat(subject.getBlockOfHtml()).isEqualTo(expected);
}

@Test
void givenAnOldStyleString_whenComparing_thenEqualsTextBlock() {
    String expected = "<html>\n\n    <body>\n        <span>example text</span>\n    </body>\n</html>";
    assertThat(subject.getBlockOfHtml())
       .isEqualTo(expected);
}
```

**当我们需要显式缩进时**，我们可以对非空行(或最后一行)使用较少的缩进：

```java
public String getNonStandardIndent() {
    return """
                Indent
            """;
}

@Test
void givenAnIndentedString_thenMatchesIndentedOldStyle() {
    assertThat(subject.getNonStandardIndent())
            .isEqualTo("    Indent\n");
}
```

此外，我们还可以在文本块内使用转义，我们将在下一节中看到。

## 4. 转义

### 4.1 转义双引号

**在文本块内，不必转义双引号**。我们甚至可以通过转义其中一个来在我们的文本块中再次使用三个双引号：

```java
public String getTextWithEscapes() {
    return """
            "fun" with
            whitespace
            and other escapes \"""
            """;
}
```

这是唯一必须转义双引号的情况。在其他情况下，这被认为是一种不好的做法。

### 4.2 转义行终止符

通常，不必在文本块内转义换行符。

**但是，请注意，即使源文件具有Windows行结尾(\r\n)，文本块也只会以换行符(\\n)终止**。如果我们需要回车符(\r)，我们必须明确地将它们添加到文本块中：

```java
public String getTextWithCarriageReturns() {
	return """
		separated with
		carriage returns""";
}

@Test
void givenATextWithCarriageReturns_thenItContainsBoth() {
    assertThat(subject.getTextWithCarriageReturns())
        .isEqualTo("separated with\r\ncarriage returns");
}
```

有时，我们的源代码中可能有很长的文本行，我们希望以可读的方式对其进行格式化。Java 14预览版添加了一个允许我们执行此操作的功能，**我们可以转义换行符以使其被忽略**：

```java
public String getIgnoredNewLines() {
    return """
            This is a long test which looks to \
            have a newline but actually does not""";
}
```

实际上这个String字面值等于一个普通的不间断字符串：

```java
@Test
void givenAStringWithEscapedNewLines_thenTheResultHasNoNewLines() {
    String expected = "This is a long test which looks to have a newline but actually does not";
    assertThat(subject.getIgnoredNewLines())
        .isEqualTo(expected);
}
```

### 4.3 转义空格

**编译器忽略文本块中的所有尾随空格**。但是，自Java 14预览版以来，我们可以使用新的转义序列\s转义空格。编译器还将保留此转义空格前面的所有空格。

让我们仔细看看转义空格的影响：

```java
public String getEscapedSpaces() {
    return """
            line 1·······
            line 2·······\s
            """;
}

@Test
void givenAStringWithEscapesSpaces_thenTheResultHasLinesEndingWithSpaces() {
    String expected = "line 1\nline 2        \n";
    assertThat(subject.getEscapedSpaces())
            .isEqualTo(expected);
}
```

**注意：**上例中的空格已替换为“·”符号以使其可见。

编译器将删除第一行中的空格，但是，第二行以转义空格结束，因此保留了所有空格。

## 5. 格式化

为了帮助进行变量替换，添加了一个新方法，该方法允许直接在字符串字面值上调用[String.format方法](https://www.baeldung.com/string/format)：

```java
public String getFormattedText(String parameter) {
    return """
            Some parameter: %s
            """.formatted(parameter);
}
```

## 6. 总结

在这个简短的教程中，我们了解了Java的文本块功能。它可能不会为Java带来多大的影响，但它可以帮助我们编写更好、更易读的代码，这通常是一件好事。