## 1. 概述

在本教程中，我们将学习如何在Java中声明多行字符串。

现在Java15 已经发布，我们可以使用称为文本块的新原生功能。

如果我们不能使用此功能，我们还会查看其他方法。

## 2.文本块

我们可以通过使用“”” (三个双引号)声明字符串来使用文本块：

```java
public String textBlocks() {
    return """
        Get busy living
        or
        get busy dying.
        --Stephen King""";
}
```

这是迄今为止声明多行字符串最方便的方法。事实上，正如我们的[专门文章中所述，](https://www.baeldung.com/java-text-blocks)我们不必处理行分隔符或缩进空格。

[此功能在Java15 中可用，如果我们启用预览功能](https://www.baeldung.com/java-preview-features)，Java 13 和 14 也可用。

在以下部分中，如果我们使用以前版本的Java或文本块不适用，我们将回顾其他适用的方法。

## 3.获取行分隔符

每个操作系统都有自己定义和识别新行的方式。

在Java中，获取操作系统行分隔符非常容易：

```java
String newLine = System.getProperty("line.separator");
```

我们将在以下部分中使用此newLine来创建多行字符串。

## 4. 字符串连接

字符串连接是一种简单的本机方法，可用于创建多行字符串：

```java
public String stringConcatenation() {
    return "Get busy living"
            .concat(newLine)
            .concat("or")
            .concat(newLine)
            .concat("get busy dying.")
            .concat(newLine)
            .concat("--Stephen King");
}
```

使用 + 运算符是实现相同目的的另一种方法。

Java 编译器以相同的方式翻译concat()和 + 运算符：

```java
public String stringConcatenation() {
    return "Get busy living"
            + newLine
            + "or"
            + newLine
            + "get busy dying."
            + newLine
            + "--Stephen King";
}
```

## 5. 字符串连接

Java 8 引入了[String#join](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#join(java.lang.CharSequence,java.lang.CharSequence...))，它接受一个分隔符和一些字符串作为参数。

它返回一个最终字符串，所有输入字符串都用分隔符连接在一起：

```java
public String stringJoin() {
    return String.join(newLine,
                       "Get busy living",
                       "or",
                       "get busy dying.",
                       "--Stephen King");
}
```

## 6. 字符串生成器

StringBuilder是构建String的辅助类。[StringBuilder](https://www.baeldung.com/java-string-builder-string-buffer)是在Java1.5 中作为StringBuffer的替代品引入的。

这是在循环中构建巨大字符串的不错选择：

```java
public String stringBuilder() {
    return new StringBuilder()
            .append("Get busy living")
            .append(newLine)
            .append("or")
            .append(newLine)
            .append("get busy dying.")
            .append(newLine)
            .append("--Stephen King")
            .toString();
}
```

## 7. 字符串编写器

StringWriter是我们可以用来创建多行字符串的另一种方法。我们在这里不需要newLine因为我们使用PrintWriter。

println函数自动添加新行：

```java
public String stringWriter() {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    printWriter.println("Get busy living");
    printWriter.println("or");
    printWriter.println("get busy dying.");
    printWriter.println("--Stephen King");
    return stringWriter.toString();
}
```

## 8.番石榴细木工

仅仅为了像这样的简单任务而使用外部库没有多大意义。但是，如果项目已经将库用于其他目的，我们可以利用它。

例如，Google 的 Guava 库非常受欢迎。

[Guava 有一个Joiner类](https://www.baeldung.com/guava-joiner-and-splitter-tutorial)，可以构建多行字符串：

```java
public String guavaJoiner() {
    return Joiner.on(newLine).join(ImmutableList.of("Get busy living",
        "or",
        "get busy dying.",
        "--Stephen King"));
}
```

## 9. 从文件加载

Java 完全按原样读取文件。这意味着如果我们在文本文件中有一个多行字符串，那么当我们读取该文件时将得到相同的字符串。在 Java中有很多[读取文件](https://www.baeldung.com/java-read-file)的方法。

将长字符串与代码分开实际上是一种很好的做法：

```java
public String loadFromFile() throws IOException {
    return new String(Files.readAllBytes(Paths.get("src/main/resources/stephenking.txt")));
}
```

## 10. 使用 IDE 功能

许多现代 IDE 支持多行/粘贴。Eclipse 和 IntelliJ IDEA 是此类 IDE 的示例。我们可以简单地多行字符串并将其粘贴到这些 IDE 中的两个双引号内。

显然，此方法不适用于在运行时创建字符串，但它是获取多行字符串的一种快速简便的方法。

## 11.总结

在本文中，我们学习了几种在Java中构建多行字符串的方法。

好消息是Java15 通过Text Blocks原生支持多行字符串。

回顾的所有其他方法都可以在Java15 或任何以前的版本中使用。