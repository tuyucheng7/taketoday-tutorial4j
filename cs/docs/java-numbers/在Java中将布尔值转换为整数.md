## 1. 概述

在本教程中，我们将学习如何将布尔值转换为int值。首先，我们将了解Java如何处理这两种[原始数据类型](https://www.baeldung.com/java-primitives)；然后，我们将探讨将布尔值转换为整数的多种方法。

## 2.数据类型

在Java中，整数可以用int 原始数据类型或 Integer[包装类](https://www.baeldung.com/java-wrapper-classes)来表示。原始数据类型是一个 32 位有符号整数，由二进制[补](https://www.baeldung.com/cs/two-complement)码编码方法表示。Integer 类用作包装器，允许你执行无符号整数运算，以及将整数(原始)值视为与[Generics](https://www.baeldung.com/java-generics)一起使用的对象。

另一方面，布尔 值在内存中没有特定的大小，但它默认为操作系统和[Java 虚拟机 (JVM)](https://www.baeldung.com/jvm-vs-jre-vs-jdk)。类似地，与Java中的所有原始数据类型一样，boolean 具有 Boolean 包装类，它允许 boolean 值表现得像对象。

我们可以利用两种数据类型(boolean和int)的原始值和包装类来执行数据转换。假设true 和 false布尔值分别代表 1 和 0，我们有多种方法可以进行转换。

## 3. 原始boolean到int

要将原始 布尔值 转换为 int，我们评估表达式的条件以确定我们要返回的整数：

```java
public int booleanPrimitiveToInt(boolean foo) {
    int bar = 0;
    if (foo) {
        bar = 1;
    }
    return bar;
}
```

我们可以使用三元运算符来简化这个函数：

```java
public int booleanPrimitiveToIntTernary(boolean foo) {
    return (foo) ? 1 : 0;
}
```

这种方法使用原始数据类型(boolean和int)进行转换。结果，当布尔表达式为真时，我们得到 1。否则，该方法返回 0。

## 4.包装类

使用 Boolean 包装器类，我们有两种方法来进行转换：

-   我们可以利用 Boolean 类中的 静态 方法。
-   我们可以直接从布尔对象调用方法。

### 4.1. 静态方法

Boolean 类有一个 比较 方法，我们可以按如下方式使用：

```java
public static int booleanObjectToInt(boolean foo) {
    return Boolean.compare(foo, false);
}
```

回想一下， 如果两个参数具有相同的值，则静态 比较 方法返回 0 。换句话说，当 foo 为假时，比较结果将是 0。否则， 当第一个参数为 真且第二个参数为假时，函数返回1 。

同样，我们可以使用相同的静态方法，将第二个参数更改为true：

```java
public static int booleanObjectToIntInverse(boolean foo) { 
    return Boolean.compare(foo, true) + 1;
}
```

这一次，如果 foo为真， compare方法计算两个相同值的参数，结果为0。但是，将 1 添加到结果将返回来自真实布尔变量的预期整数值。

### 4.2. 布尔类对象

Boolean 类对象具有 我们可以使用的compareTo等函数：

```java
public static int booleanObjectMethodToInt(Boolean foo) {
    return foo.compareTo(false);
}
```

使用方法 booleanObjectMethodToInt，我们可以像使用静态方法一样将布尔值转换为整数。同样，你可以通过将参数更改为true并将结果加1来应用反向版本。

## 5.阿帕奇公地

[Apache Commons](https://search.maven.org/search?q=g:org.apache.commons AND a:commons-lang3)是一个流行的Java开源库，它提供实用程序类，例如BooleanUtils。我们可以将库添加为 Maven 中的依赖项，如下所示：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>

```

一旦库在我们的 pom.xml 文件中，我们就可以使用 BooleanUtils 类将布尔值转换为整数：

```java
public static int booleanUtilsToInt(Boolean foo) {
    return BooleanUtils.toInteger(foo);
}
```

与示例方法booleanPrimitiveToIntTernary一样，在内部， toInteger 方法执行相同的三元运算符来进行转换。

## 六，总结

在本教程中，我们学习了如何将布尔值转换为整数值。假设 true 转换为 1 而 false 转换为 0，我们探索了不同的实现来实现这种转换。