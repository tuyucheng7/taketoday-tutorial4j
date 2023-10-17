## 1. 概述

在本快速教程中，我们将简要介绍[java.util.Scanner](https://www.baeldung.com/java-scanner)类的nextLine()方法，当然重点是学习如何在实践中使用它。

## 2.扫描仪.nextLine()

java.util.Scanner类的nextLine ()方法从当前位置开始扫描，直到找到行分隔符。该方法返回从当前位置到行尾的字符串。

因此，在操作之后，扫描器的位置被设置为分隔符后面的下一行的开头。

该方法将搜索输入数据以查找行分隔符。如果不存在行分隔符，它可能会扫描所有输入数据以搜索要跳过的行。

nextLine()方法的签名是：

```java
public String nextLine()
```

该方法不带参数。它返回当前行，不包括末尾的任何行分隔符。

我们来看看它的用法：

```java
try (Scanner scanner = new Scanner("ScannernTestn")) {
    assertEquals("Scanner", scanner.nextLine());
    assertEquals("Test", scanner.nextLine());
}
```

正如我们所见，该方法从当前扫描仪位置返回输入，直到找到行分隔符：

```java
try (Scanner scanner = new Scanner("Scannern")) {
    scanner.useDelimiter("");
    scanner.next();
    assertEquals("canner", scanner.nextLine());
}
```

在上面的示例中，对next()的调用返回'S'并将扫描仪位置前进到指向'c'。

因此，当我们调用nextLine()方法时，它会从当前扫描仪位置返回输入，直到找到行分隔符。

nextLine ()方法抛出两种类型的已检查异常。

首先，当找不到行分隔符时，它会抛出NoSuchElementException：

```java
@Test(expected = NoSuchElementException.class)
public void whenReadingLines_thenThrowNoSuchElementException() {
    try (Scanner scanner = new Scanner("")) {
        scanner.nextLine();
    }
}
```

其次，如果扫描器关闭，它会抛出IllegalStateException ：

```java
@Test(expected = IllegalStateException.class)
public void whenReadingLines_thenThrowIllegalStateException() {
    Scanner scanner = new Scanner("");
    scanner.close();
    scanner.nextLine();
}
```

## 3.总结

在这篇切题的文章中，我们研究了Java 的Scanner类的nextLine()方法。

此外，我们还研究了它在一个简单的Java程序中的用法。最后，我们查看了方法抛出的异常以及说明它的示例代码。