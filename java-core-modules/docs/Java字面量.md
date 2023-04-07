## 一、概述

在使用 Java 编程语言时，我们将使用大量文字。

在本教程中，我们将了解所有类型的文字以及如何使用它们。

## 2. 什么是 Java 字面量？

**Java 文字是我们在代码中指定为常量的任何值。**它可以是任何类型 - *integer*、*float*、*double*、*long*、*String*、*char*或*boolean 。*

在下面的示例中，数字 *1*和字符串*literal_string*是文字。

```java
int x = 1;
String s = "literal_string";复制
```

使用文字时，充分理解[Java 原语](https://www.baeldung.com/java-primitives)同样重要。

## 3. 文字类型

让我们通过一些例子来看看不同类型的文字。

### 3.1. 整数文字

对于整数（*int、long、short、byte*），我们可以用不同的方式指定它们。

首先，我们可以使用**十进制文字（以 10 为底）**。这些是最常用的，因为它们是我们每天使用的数字。

```java
int x = 1;复制
```

其次，我们可以指定**八进制形式（基数 8）**的整数文字。在这种形式中，他们必须以***0*****开头** *。*

```java
int x = 05;复制
```

**第三，整数文字可以以十六进制形式（以 16 为底）**使用。他们必须以***0x\*** **或** ***0X*****开头***。*

```java
int x = 0x12ef;复制
```

最后，我们有**二进制形式 (base 2)**。这种形式是在 Java 1.7 中引入的，这些文字必须**以 \*0b\* 或** ***0B**开头。*

```java
int x = 0b1101;复制
```

在上面的示例中，只要字面值不超过类型限制，我们就可以将*int更改为任何整数类型。*

默认情况下，这些文字被视为*int*。对于*long*、*short*或*byte*，编译器检查该值是否超出类型的限制，如果为真，则将它们视为该类型文字。

我们可以通过对 long 文字使用*l*或*L来覆盖默认的**int文字。*仅当文字值高于*int* 限制时，我们才需要使用它。

### 3.2. 浮点文字

默认情况下，浮点文字被认为*是双精度的*。我们还可以使用*d*或*D*指定文字是*双精度*数。这不是强制性的，但这是一个很好的做法。

我们可以**使用*****f\*****或*****F\*****指定我们想要一个\*浮点数\***。这对于*float*类型的文字是强制性的。

浮点文字只能以十进制形式（基数 10）指定：

```java
double d = 123.456;
float f = 123.456;
float f2 = 123.456d;
float f3 = 123.456f;复制
```

由于类型不匹配，第二个和第三个示例将无法编译。

### 3.3. 字符文字

对于*char*数据类型，文字可以有几种方式。

单引号字符很常见，对特殊字符特别有用。

```java
char c = 'a';
char c2 = '\n';复制
```

我们可以**在单引号之间只使用一个字符或一个特殊字符**。

用于字符的整数值被转换为该字符的 Unicode 值：

```java
char c = 65;复制
```

我们可以用与整数文字相同的方式来指定它。

最后，我们可以使用 Unicode 表示：

```java
char c= '\u0041';复制
```

最后两个示例中的表达式计算字符*A。*

### 3.4. 字符串文字

双引号之间的任何文本都是*字符串*文字：

```java
String s = "string_literal";复制
```

*字符串*文字只能在一行中。为了拥有多行*String*，我们可以使用将在编译时执行的表达式：

```java
String multiLineText = "When we want some text that is on more than one line,\n"
+ "then we can use expressions to add text to a new line.\n";复制
```

## 4. 短字面量和字节字面量

上面，我们看到了如何声明每种类型的文字。*对于除byte 和 short*之外的所有类型，我们不需要创建变量。我们可以简单地使用文字值。

例如，当将参数传递给方法时，我们可以传递文字：

```java
static void literals(int i, long l, double d, float f, char c, String s) {
    // do something
}
//Call literals method
literals(1, 123L, 1.0D, 1.0F, 'a', "a");复制
```

有用于指定文字类型的符号，因此它们不用作默认类型。在上面的示例中，*F*是唯一的强制符号。

*令人惊讶的是，字节*和*短*类型会出现问题：

```java
static void shortAndByteLiterals(short s, byte b) {
    // do something
}
//Call the method
shortAndByteLiterals(1, 0); // won't compile复制
```

尽管存在此限制，但仍有 2 种解决方案。

第一个解决方案是使用我们之前声明的一些变量：

```java
short s = 1;
byte b = 1;
shortAndByteLiterals(s, b);复制
```

另一种选择是转换文字值：

```java
shortAndByteLiterals((short) 1, (byte) 0);复制
```

这是使编译器使用适当类型所必需的。

## 5.结论

在本文中，我们研究了在 Java 中指定和使用文字的不同方式。