## 一、扫描仪概述

在本快速教程中，我们将说明如何使用Java Scanner类——读取输入、查找和跳过具有不同分隔符的模式。

## 2.扫描文件

首先 - 让我们看看如何使用Scanner读取文件。

在下面的示例中——我们将一个包含“ Hello world ”的文件读入令牌中：

```java
@Test
public void whenReadFileWithScanner_thenCorrect() throws IOException{
    Scanner scanner = new Scanner(new File("test.txt"));

    assertTrue(scanner.hasNext());
    assertEquals("Hello", scanner.next());
    assertEquals("world", scanner.next());

    scanner.close();
}
```

请注意，next()方法在此处返回下一个String标记。

另外，请注意我们在使用完扫描仪后如何关闭它。

## 3.将InputStream转换为String

接下来 – 让我们看看如何使用Scanner将InputStream转换为String：

```java
@Test
public void whenConvertInputStreamToString_thenConverted()
  throws IOException {
    String expectedValue = "Hello world";
    FileInputStream inputStream 
      = new FileInputStream("test.txt");
    
    Scanner scanner = new Scanner(inputStream);
    scanner.useDelimiter("A");

    String result = scanner.next();
    assertEquals(expectedValue, result);

    scanner.close();
}
```

与前面的示例类似，我们使用扫描仪对从开头到下一个正则表达式“A”的整个流进行标记化——这与完整输入匹配。

## 4. Scanner与BufferedReader

现在——让我们讨论一下Scanner和BufferedReader之间的区别——我们通常使用：

-   BufferedReader当我们想将输入读入行时
-   扫描器将输入读入令牌

在下面的示例中——我们使用BufferedReader将文件读入行中：

```java
@Test
public void whenReadUsingBufferedReader_thenCorrect() 
  throws IOException {
    String firstLine = "Hello world";
    String secondLine = "Hi, John";
    BufferedReader reader 
      = new BufferedReader(new FileReader("test.txt"));

    String result = reader.readLine();
    assertEquals(firstLine, result);

    result = reader.readLine();
    assertEquals(secondLine, result);

    reader.close();
}
```

现在，让我们使用Scanner将同一文件读入令牌：

```java
@Test
public void whenReadUsingScanner_thenCorrect() 
  throws IOException {
    String firstLine = "Hello world";
    FileInputStream inputStream 
      = new FileInputStream("test.txt");
    Scanner scanner = new Scanner(inputStream);

    String result = scanner.nextLine();
    assertEquals(firstLine, result);

    scanner.useDelimiter(", ");
    assertEquals("Hi", scanner.next());
    assertEquals("John", scanner.next());

    scanner.close();
}
```

请注意我们是如何使用Scanner nextLine() API来读取整行的。

## 5. 使用新的扫描器 (System.in)从控制台扫描输入

接下来——让我们看看如何使用Scanner实例从控制台读取输入：

```java
@Test
public void whenReadingInputFromConsole_thenCorrect() {
    String input = "Hello";
    InputStream stdin = System.in;
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    Scanner scanner = new Scanner(System.in);

    String result = scanner.next();
    assertEquals(input, result);

    System.setIn(stdin);
    scanner.close();
}
```

请注意，我们使用System.setIn(...)来模拟来自控制台的一些输入。

### 5.1. 下一行() API

此方法仅返回当前行的字符串：

```java
scanner.nextLine();
```

这将读取当前行的内容并返回它，除了末尾的任何行分隔符 - 在本例中 - 换行符。

读取内容后，Scanner将其位置设置为下一行的开头。要记住的重点是 nextLine() API 使用行分隔符并将Scanner的位置移动到下一行。

所以下次如果我们通过Scanner阅读，我们将从下一行的开头开始阅读。

### 5.2. 下一个Int() API

此方法将输入的下一个标记扫描为int：

```java
scanner.nextInt();
```

API 读取接下来可用的整数令牌。

在这种情况下，如果下一个标记是整数并且在整数之后有行分隔符，请始终记住nextInt() 不会消耗行分隔符。相反，扫描仪的位置将是行分隔符本身。

因此，如果我们有一系列操作，其中第一个操作是scanner.nextInt()，然后是 scanner.nextLine()并且作为输入，如果我们提供一个整数并按换行符，那么这两个操作都会被执行。

nextInt () API 将使用整数，nextLine() API 将使用行分隔符并将Scanner放置到下一行的开头。

## 6. 验证输入

现在 - 让我们看看如何使用Scanner验证输入。在以下示例中——我们使用Scanner方法hasNextInt()来检查输入是否为整数值：

```java
@Test
public void whenValidateInputUsingScanner_thenValidated() 
  throws IOException {
    String input = "2000";
    InputStream stdin = System.in;
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    Scanner scanner = new Scanner(System.in);

    boolean isIntInput = scanner.hasNextInt();
    assertTrue(isIntInput);

    System.setIn(stdin);
    scanner.close();
}
```

## 7.扫描字符串

接下来——让我们看看如何使用Scanner扫描字符串：

```java
@Test
public void whenScanString_thenCorrect() 
  throws IOException {
    String input = "Hello 1 F 3.5";
    Scanner scanner = new Scanner(input);

    assertEquals("Hello", scanner.next());
    assertEquals(1, scanner.nextInt());
    assertEquals(15, scanner.nextInt(16));
    assertEquals(3.5, scanner.nextDouble(), 0.00000001);

    scanner.close();
}
```

注意：方法nextInt(16)将下一个标记读取为十六进制整数值。

## 8.寻找模式

现在 - 让我们看看如何使用Scanner查找模式。

在以下示例中——我们使用findInLine()在整个输入中搜索与给定模式匹配的标记：

```java
@Test
public void whenFindPatternUsingScanner_thenFound() throws IOException {
    String expectedValue = "world";
    FileInputStream inputStream = new FileInputStream("test.txt");
    Scanner scanner = new Scanner(inputStream);

    String result = scanner.findInLine("wo..d");
    assertEquals(expectedValue, result);

    scanner.close();
}
```

我们还可以使用findWithinHorizon()在特定域中搜索模式，如下例所示：

```java
@Test
public void whenFindPatternInHorizon_thenFound() 
  throws IOException {
    String expectedValue = "world";
    FileInputStream inputStream = new FileInputStream("test.txt");
    Scanner scanner = new Scanner(inputStream);

    String result = scanner.findWithinHorizon("wo..d", 5);
    assertNull(result);

    result = scanner.findWithinHorizon("wo..d", 100);
    assertEquals(expectedValue, result);

    scanner.close();
}
```

请注意，搜索范围只是执行搜索的字符数。

## 9. 跳过模式

接下来——让我们看看如何在Scanner中跳过一个Pattern。我们可以在使用Scanner读取输入时跳过与特定模式匹配的标记。

在以下示例中——我们使用Scanner方法skip()跳过“ Hello ”标记：

```java
@Test
public void whenSkipPatternUsingScanner_thenSkipped() 
  throws IOException {
    FileInputStream inputStream = new FileInputStream("test.txt");
    Scanner scanner = new Scanner(inputStream);

    scanner.skip(".e.lo");

    assertEquals("world", scanner.next());

    scanner.close();
}
```

## 10. 更改扫描仪定界符

最后 - 让我们看看如何更改扫描仪定界符。在下面的示例中——我们将默认的Scanner分隔符更改为“ o ”：

```java
@Test
public void whenChangeScannerDelimiter_thenChanged() 
  throws IOException {
    String expectedValue = "Hello world";
    String[] splited = expectedValue.split("o");

    FileInputStream inputStream = new FileInputStream("test.txt");
    Scanner scanner = new Scanner(inputStream);
    scanner.useDelimiter("o");

    assertEquals(splited[0], scanner.next());
    assertEquals(splited[1], scanner.next());
    assertEquals(splited[2], scanner.next());

    scanner.close();
}
```

我们也可以使用多个定界符。在下面的示例中——我们同时使用逗号“ , ”和破折号“ - ”作为分隔符来扫描包含“ John,Adam-Tom ”的文件：

```java
@Test
public void whenReadWithScannerTwoDelimiters_thenCorrect() 
  throws IOException {
    Scanner scanner = new Scanner(new File("test.txt"));
    scanner.useDelimiter(",|-");

    assertEquals("John", scanner.next());
    assertEquals("Adam", scanner.next());
    assertEquals("Tom", scanner.next());

    scanner.close();
}
```

注意：默认的Scanner分隔符是空格。

## 11. 处理NoSuchElementException

在深入细节之前，让我们试着理解异常的真正含义。

根据文档，NoSuchElementException通常在请求的元素不存在时发生。

基本上，Scanner在读取数据或获取下一个元素失败时会抛出此异常。

### 11.1. 重现异常

现在我们知道是什么导致Scanner抛出NoSuchElementException，让我们看看如何使用实际示例重现它。

为了演示真实世界的用例，我们将使用Scanner类的两个不同实例从FileInputStream读取数据两次：

```java
public void givenClosingScanner_whenReading_thenThrowException() throws IOException {
    final FileInputStream inputStream = new FileInputStream("src/test/resources/test_read.in");

    final Scanner scanner = new Scanner(inputStream);
    scanner.next();
    scanner.close();

    final Scanner scanner2 = new Scanner(inputStream);
    scanner2.next();
    scanner2.close();
}
```

接下来，让我们运行我们的测试用例，看看会发生什么。事实上，查看日志，测试用例失败并出现NoSuchElementException：

```bash
java.util.NoSuchElementException
    at java.util.Scanner.throwFor(Scanner.java:862)
    at java.util.Scanner.next(Scanner.java:1371)
    at scanner.cn.tuyucheng.taketoday.JavaScannerUnitTest.givenClosingScanner_whenReading_thenThrowException(JavaScannerUnitTest.java:195)
...

```

### 11.2. 调查原因

简而言之，当Scanner关闭时，如果源实现了Closeable接口，它也会关闭它的源。

基本上，当我们在测试用例中调用scanner.close()时，我们实际上也关闭了inputStream。因此，第二个Scanner未能读取数据，因为源未打开。

### 11.3. 修复异常

只有一种方法可以修复异常——避免使用多个Scanner操作同一个数据源。

因此，一个好的解决方案是使用单个Scanner实例来读取数据并在最后关闭它。

## 12.总结

在本教程中，我们介绍了多个使用Java扫描器的真实示例。

我们学习了如何使用Scanner从文件、控制台或字符串读取输入。我们还学习了如何使用Scanner查找和跳过模式，以及如何更改Scanner定界符。

最后，我们解释了如何处理NoSuchElementException异常。