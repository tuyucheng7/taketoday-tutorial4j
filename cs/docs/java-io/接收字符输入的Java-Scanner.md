## 1. 概述

在本教程中，我们将了解如何从[Scanner](https://www.baeldung.com/java-scanner)类中获取字符输入。

## 2.扫描字符

Java [Scanner](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Scanner.html)不提供任何类似于nextInt()、nextLine()等的字符输入方法。

我们可以通过多种方式使用Scanner进行字符输入。

让我们首先创建一个输入字符串：

```java
String input = new StringBuilder().append("abcn")
  .append("mnon")
  .append("xyzn")
  .toString();
```

## 3. 使用 next()

让我们看看如何使用Scanner的next()方法和String类的charAt()方法将字符作为输入：

```java
@Test
public void givenInputSource_whenScanCharUsingNext_thenOneCharIsRead() {
    Scanner sc = new Scanner(input);
    char c = sc.next().charAt(0);
    assertEquals('a', c);
}
```

Java Scanner 的next ()方法返回一个 String 对象。我们在这里使用String类的charAt()方法从字符串对象中获取字符。

## 4. 使用 findInLine()

此方法将字符串模式作为输入，我们将传递“.”。(点)仅匹配单个字符。但是，这将以字符串形式返回单个字符，因此我们将使用charAt()方法来获取字符：

```java
@Test
public void givenInputSource_whenScanCharUsingFindInLine_thenOneCharIsRead() {
    Scanner sc = new Scanner(input);
    char c = sc.findInLine(".").charAt(0);
    assertEquals('a', c);
}
```

## 5. 使用 useDelimeter()

此方法也仅扫描一个字符，但作为类似于findInLine() API的字符串对象。我们可以类似地使用charAt()方法来获取字符值：

```java
@Test
public void givenInputSource_whenScanCharUsingUseDelimiter_thenOneCharIsRead() {
    Scanner sc = new Scanner(input);
    char c = sc.useDelimiter("").next().charAt(0);
    assertEquals('a', c);
}
```

## 六，总结

在本教程中，我们学习了如何使用JavaScanner获取字符输入。