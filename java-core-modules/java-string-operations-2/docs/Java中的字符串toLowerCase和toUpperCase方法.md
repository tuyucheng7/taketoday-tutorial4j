## 1. 概述

在本教程中，我们将介绍Java [String](https://www.baeldung.com/java-string)类中包含的toUpperCase和toLowerCase方法。

我们将从创建一个名为name的字符串开始：

```java
String name = "John Doe";
```

## 2.转换为大写

要基于name创建一个新的大写字符串，我们调用 toUpperCase 方法：

```java
String uppercaseName = name.toUpperCase();
```

这导致 uppercaseName的值为“JOHN DOE”：

```java
assertEquals("JOHN DOE", uppercaseName);
```

请注意，字符串在 Java中是不可变的，调用toUpperCase 会创建一个新的 字符串。换句话说，调用 toUpperCase时name没有改变。

## 3.转换为小写

类似地，我们通过调用 toLowerCase基于 名称创建一个新的小写字符串：

```java
String lowercaseName = name.toLowerCase();
```

这导致 lowercaseName的值为“john doe”：

```java
assertEquals("john doe", lowercaseName);
```

与toUpperCase一样， toLowerCase 不会更改name的值。

## 4. 使用语言环境更改大小写

此外，通过为toUpperCase 和 toLowerCase方法提供[Locale](https://www.baeldung.com/java-8-localization#localization) ，我们可以使用特定于语言环境的规则更改String的大小写。

例如，我们可以提供Locale以大写土耳其语i (Unicode 0069 ) ：

```java
Locale TURKISH = new Locale("tr");
System.out.println("u0069".toUpperCase());
System.out.println("u0069".toUpperCase(TURKISH));
```

因此，这会导致大写I和带点的大写I：

```plaintext
I
İ
```

我们可以使用以下断言来验证这一点：

```java
assertEquals("u0049", "u0069".toUpperCase());
assertEquals("u0130", "u0069".toUpperCase(TURKISH));
```

同样，我们可以 使用土耳其语 I (Unicode 0049 ) 对toLowerCase做同样的事情：

```java
System.out.println("u0049".toLowerCase());
System.out.println("u0049".toLowerCase(TURKISH));
```

因此，这会导致小写i和小写无点 i：

```plaintext
i
ı
```

我们可以使用以下断言来验证这一点：

```java
assertEquals("u0069", "u0049".toLowerCase());
assertEquals("u0131", "u0049".toLowerCase(TURKISH));
```

## 5.总结

总之，Java String 类包括 用于更改String大小写的toUpperCase和 toLowerCase方法 。如果需要，可以提供 Locale以在更改String 的大小写时提供特定于区域设置的规则。