## 1. 概述

在本教程中，我们将了解如何在 Java 中将int转换为char并返回。

## 2. Java 中的字符

我们将简要讨论字符的表示方式，以便更好地理解我们将在本文后面看到的代码。

在内部，**Java 将每个字符 存储为 16 位 Unicode 编码值**：

| 特点 |       2字节       | 十进制(以 10 为底) | 十六进制(基数 16) |
| :--: | :---------------: | :------------------: | :-----------------: |
| 一种 | 00000000 01000001 |          65          |         41          |
| 一种 | 00000000 01100001 |          61          |         97          |
|  1   | 00000000 00110001 |          49          |         31          |
|  从  | 00000000 01011010 |          90          |         5A          |

我们可以通过将char值转换为 int来轻松地检查这一点 ：

```java
assertEquals(65, (int)'A');
```

[ASCII 码](https://www.baeldung.com/cs/ascii-code)是 Unicode 编码的一个子集，主要代表英文字母表。

## 3. int转char

假设我们有一个值为 7 的int变量，我们想将其转换为对应的char类型“ 7 ”。我们有几个选项可以做到这一点。

**简单地将它转换为char是行不通的，因为这会将它转换为二进制表示的字符0111****，在 UTF-16 中是U+0007或字符['BELL'](https://www.fileformat.info/info/unicode/char/0007/index.htm)。

### 3.1. 抵消“0”

UTF-16 中的字符按顺序表示。所以我们可以将“ 0 ”字符偏移7以获得“ 7 ”字符：

```java
@Test
public void givenAnInt_whenAdding0_thenExpectedCharType() {
    int num = 7;

    char c = (char)('0' + num);

    assertEquals('7', c);
}
```

### 3.2. 使用Character.forDigit()方法

添加“ 0 ”有效，但似乎有点老套。幸运的是，我们有一种更简洁的方法来使用 [Character.forDigit()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Character.html#forDigit(int,int))方法：

```java
@Test
public void givenAnInt_whenUsingForDigit_thenExpectedCharType() {
    int num = 7;

    char c = Character.forDigit(num , 10);

    assertEquals('7', c);
}
```

我们可以注意到forDigit ()方法接受第二个参数 radix，它表示我们要转换的数字的基本表示形式。在我们的例子中，它是10。

### 3.3. 使用Integer.toString()方法

我们可以使用包装器类Integer，它具有[toString()](https://www.baeldung.com/java-tostring-valueof)方法，可将给定的int转换为其String表示形式。当然，这可以用于将多位数字转换为String。但是，我们也可以通过链接 charAt [()](https://www.baeldung.com/java-convert-string-to-char)方法并选择第一个char来使用它将单个数字转换为char：

```java
@Test
public void givenAnInt_whenUsingToString_thenExpectedCharType() {
    int num = 7;

    char c = Integer.toString(num).charAt(0);

    assertEquals('7', c);
}
```

## 4. 将char转换为int

之前，我们看到了如何将int转换为char。让我们看看如何获取char的**int值。正如我们可能预料的那样，**将*****char*****转换为*****int*****是行不通的，因为这为我们提供了字符的 UTF-16 编码的十进制表示：**

```java
@Test
public void givenAChar_whenCastingFromCharToInt_thenExpectedUnicodeRepresentation() {

    char c = '7';

    assertEquals(55, (int) c); 
}
```

### 4.1. 减去'0'

如果当我们添加 '0' 时我们得到char，然后相反地通过减去 '0' 十进制值，我们应该得到int：

```java
@Test
public void givenAChar_whenSubtracting0_thenExpectedNumericType() {

    char c = '7';

    int n = c - '0';

    assertEquals(7, n);
}
```

这确实有效，但有更好、更简单的方法来做到这一点。

### 4.2. 使用Character.getNumericValue()方法

Character类再次提供了另一个辅助方法[getNumericValue()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Character.html#getNumericValue(char))，它基本上按照它说的做：

```java
@Test
public void givenAChar_whenUsingGetNumericValue_thenExpectedNumericType() {

    char c = '7';

    int n = Character.getNumericValue(c);

    assertEquals(7, n);
}
```

### 4.3. 使用Integer.parseInt()

我们可以使用[Integer.parseInt()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Integer.html#parseInt(java.lang.String))将String转换为数字表示形式。和以前一样，虽然我们可以使用它来将具有多个数字的整个数字转换为int表示形式，但我们也可以将它用于单个数字：

```java
@Test
public void givenAChar_whenUsingParseInt_thenExpectedNumericType() {

    char c = '7';

    int n = Integer.parseInt(String.valueOf(c));

    assertEquals(7, n);
}
```

确实，语法有点繁琐，主要是因为它涉及到多次转换，但它按预期工作。

## 5.总结

在本文中，我们了解了字符在 Java 中是如何在内部表示的，以及我们如何将int转换为char并返回。