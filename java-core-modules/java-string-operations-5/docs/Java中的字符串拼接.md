## 1. 概述

Java 中的字符串连接是最常见的操作之一。在本教程中，我们将介绍一些字符串连接方法。但是，我们将重点描述如何使用concat()和“ + ”运算符方法。最后，我们将讨论如何根据我们需要做的事情来选择正确的。

## 2.连接方法

通常，在Java中有不同的方法来连接两个或多个字符串。此外，我们将查看一些示例，并对每个示例进行描述。

### 2.1. 使用“ + ”运算符

Java中 最常见的串联方法之一是使用“ + ”运算符。

与其他方法相比，“ + ”运算符为字符串连接提供了更大的灵活性。首先，它不会为空值抛出任何异常。其次，它将 null 转换为其字符串表示形式。此外，我们可以用它来连接两个以上的字符串。

让我们看一个代码示例：

```java
@Test
void whenUsingPlusOperatorANull_thenAssertEquals() {
    String stringOne = "Hello ";
    String stringTwo = null;
    assertEquals("Hello null", stringOne + stringTwo);
}
```

编译器在内部将“ + ”运算符转换为StringBuilder(或StringBuffer)类及其append()方法。

由于“ + ”运算符默默地将参数转换为字符串(使用对象的toString()方法)，我们避免了 NullPointerException。但是，我们需要考虑我们的最终字符串结果是否适用于字符串主体中的“null”。

### 2.2. 使用concat() 方法

String类中的concat()方法在当前字符串的末尾附加一个指定的字符串，并返回新的组合字符串。由于String类是不可变的，因此原始String不会更改。

让我们测试一下这个行为：

```java
@Test
void whenUsingConcat_thenAssertEquals() {
    String stringOne = "Hello";
    String stringTwo = " World";
    assertEquals("Hello World", stringOne.concat(stringTwo));
}
```

在前面的示例中，stringOne变量是基本字符串。使用concat()方法，stringTwo附加在stringOne的末尾。concat()操作是不可变的，所以我们需要一个明确的赋值。下一个示例说明了这种情况：

```java
@Test
void whenUsingConcatWithOutAssignment_thenAssertNotEquals() {
    String stringOne = "Hello";
    String stringTwo = " World";
    stringOne.concat(stringTwo);
    assertNotEquals("Hello World", stringOne); // we get only Hello
}
```

此外，为了在这种情况下获得最终的连接字符串，我们需要将concat()结果分配给一个变量：

```java
stringOne = stringOne.concat(stringTwo);
assertEquals("Hello World", stringOne);
```

concat()的另一个有用的特性是当我们需要连接多个String对象时。这种方法允许它。此外，我们还可以追加空格和特殊字符：

```java
@Test
void whenUsingConcatToMultipleStringConcatenation_thenAssertEquals() {
    String stringOne = "Hello";
    String stringTwo = "World";
    String stringThree = ", in Jav";
    stringOne = stringOne.concat(" ").concat(stringTwo).concat(stringThree).concat("@");
    assertEquals("Hello World, in Jav@", stringOne);
}
```

空值呢？当前字符串和要追加的字符串都不能为空值。否则，concat()方法会抛出 NullPointerException：

```java
@Test
void whenUsingConcatAppendANull_thenAssertEquals() {
    String stringOne = "Hello";
    String stringTwo = null;
    assertThrows(NullPointerException.class, () -> stringOne.concat(stringTwo));
}
```

### 2.3. StringBuilder类 

首先，我们有[StringBuilder](https://www.baeldung.com/java-string-builder-string-buffer)类。此类提供append()方法来执行连接操作。下一个例子向我们展示了它是如何工作的：

```java
@Test
void whenUsingStringBuilder_thenAssertEquals() {
    StringBuilder builderOne = new StringBuilder("Hello");
    StringBuilder builderTwo = new StringBuilder(" World");
    StringBuilder builder = builderOne.append(builderTwo);
    assertEquals("Hello World", builder.toString());
}
```

另一方面，类似的串联方法是[StringBuffer类](https://www.baeldung.com/java-string-builder-string-buffer)。与非同步(即非线程安全)的StringBuilder相反， StringBuffer是同步的(即线程安全)。但它的性能比StringBuilder差。它有一个append()方法，就像StringBuilder一样。

### 2.4. String format()方法

执行字符串连接的另一种方法是使用 String 类中的format()方法。使用像%s这样的格式说明符，我们可以通过字符串值或对象连接多个字符串：

```java
@Test
void whenUsingStringFormat_thenAssertEquals() {
    String stringOne = "Hello";
    String stringTwo = " World";
    assertEquals("Hello World", String.format("%s%s", stringOne, stringTwo));
}
```

### 2.5.Java8 及更高版本中的连接方法

String类中的join()方法，对于Java8 及以上版本，可以进行字符串连接。在这种情况下，此方法将第一个参数作为将要连接的字符串之间使用的定界符：

```java
@Test
void whenUsingStringJoin_thenAssertEquals() {
    String stringOne = "Hello";
    String stringTwo = " World";
    assertEquals("Hello World", String.join("", stringOne, stringTwo));
}
```

从Java8 开始，添加了[StringJoiner](https://www.baeldung.com/java-string-joiner)类。此类使用定界符、前缀和后缀连接String 。以下代码片段是其使用示例：

```java
@Test
void whenUsingStringJoiner_thenAssertEquals() {
    StringJoiner joiner = new StringJoiner(", ");
    joiner.add("Hello");
    joiner.add("World");
    assertEquals("Hello, World", joiner.toString());
}
```

此外，在Java8 中，通过添加[Stream API](https://www.baeldung.com/java-8-streams)，我们可以找到[Collectors](https://www.baeldung.com/java-8-collectors)。Collectors类有joining()方法。此方法的工作方式类似于String类中的join()方法。它用于收藏。以下示例代码片段向我们展示了它是如何工作的：

```java
@Test
void whenUsingCollectors_thenAssertEquals() {
    List<String> words = Arrays.asList("Hello", "World");
    String collect = words.stream().collect(Collectors.joining(", "));
    assertEquals("Hello, World", collect);
}
```

## 3.选择方法

最后，如果我们需要在concat()方法和“ + ”运算符之间进行选择，我们需要考虑一些方面。

首先，concat()方法只接受字符串。同时，“ + ”运算符接受任何类型并将其转换为字符串。另一方面，concat()方法会在空值上引发NullPointerExeption ，而“ + ”运算符则不然。

此外，两者之间存在[性能](https://www.baeldung.com/java-string-performance)差异。concat()方法比“+”运算符执行得更好。无论字符串的长度如何，后者总是创建一个新字符串。此外，我们需要考虑到concat()方法仅在要追加的字符串的长度大于 0 时才创建一个新字符串。否则，它会返回相同的对象。

## 4。总结

在本文中，我们快速概述了Java中的字符串连接。此外，我们详细讨论了使用concat()和“ + ”运算符来执行字符串连接。最后，我们对concat()方法和“ + ”运算符进行了比较分析，以及我们如何在不同的上下文中选择其中之一。