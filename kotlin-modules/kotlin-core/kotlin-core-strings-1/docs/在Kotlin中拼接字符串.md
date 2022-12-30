## 1. 简介

在这个简短的教程中，我们将研究在Kotlin中拼接字符串的不同方法。

## 2. 使用plus()方法

Kotlin的String类包含一个plus()方法：

```kotlin
operator fun plus(other: Any?): String (source)
```

**它返回一个字符串，该字符串是通过将引用字符串与作为参数传递的字符串拼接起来而获得的**。

例如：

```kotlin
@Test
fun givenTwoStrings_concatenateWithPlusMethod_thenEquals() {
    val a = "Hello"
    val b = "Tuyucheng"
    val c = a.plus(" ").plus(b)

    assertEquals("Hello Tuyucheng", c)
}
```

此外，重要的是要认识到，如果传入的对象不是String，则将使用该对象的字符串表示形式。

## 3. 使用+运算符

在Kotlin中拼接字符串的最简单方法是使用+运算符。结果，我们**得到了一个由运算符左右两边的String组成的新String对象**：

```kotlin
@Test
fun givenTwoStrings_concatenateWithPlusOperator_thenEquals() {
    val a = "Hello"
    val b = "Tuyucheng"
    val c = a + " " + b

    assertEquals("Hello Tuyucheng", c)
}
```

另一个关键点是，在Kotlin中，由于[运算符重载](https://kotlinlang.org/docs/reference/operator-overloading.html)，+运算符被解析为plus()方法。

通常，这是拼接少量字符串的常用方法。

## 4. 使用StringBuilder

正如我们所知，String对象是不可变的，每次使用+运算符或plus()方法进行拼接时，我们都会得到一个新的String对象。相反，为了避免不必要的字符串对象创建，我们可以使用StringBuilder。

因此，**StringBuilder会创建一个包含最终字符串的内部缓冲区**。

因此，StringBuilder在拼接大量字符串时效率更高。

下面是一个使用StringBuilder的字符串拼接示例：

```kotlin
@Test
fun givenTwoStrings_concatenateWithStringBuilder_thenEquals() {
    val builder = StringBuilder()
    builder.append("Hello")
        .append(" ")
        .append("Tuyucheng")

    assertEquals("Hello Tuyucheng", builder.toString())
}
```

最后，我们可以使用StringBuffer代替StringBuilder进行线程安全的拼接。

## 5. 使用字符串模板

Kotlin还有一个称为字符串模板的特性，**字符串模板包含被评估以构建String的表达式**。

字符串模板表达式以美元符号($)开头，后跟变量的名称。

下面是使用模板拼接字符串的示例：

```kotlin
@Test
fun givenTwoStrings_concatenateWithTemplates_thenEquals() {
    val a = "Hello"
    val b = "Tuyucheng"
    val c = "$a $b"

    assertEquals("Hello Tuyucheng", c)
}
```

Kotlin编译器会将这段代码翻译成：

```java
new StringBuilder().append(a).append(" ").append(b).toString()
```

最后这个过程就是**字符串插值**。

## 6. 总结

在本文中，我们学习了几种在Kotlin中拼接字符串对象的方法。