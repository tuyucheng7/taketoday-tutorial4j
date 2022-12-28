## 1. 概述

在这个快速教程中，我们将学习如何将可变数量的参数传递给Kotlin中的函数。此外，我们还将了解Kotlin如何使我们能够将数组转换为可变参数。最后，我们将看看所有这些是如何在字节码级别表示的。

## 2. 可变参数

要将可变数量的参数传递给函数，我们应该使用vararg参数声明该函数：

```kotlin
fun sum(vararg xs: Int): Int = xs.sum()
```

**这意味着sum()函数可以接收零个或多个整数，例如**：

```kotlin
val zeroNumbers = sum()
assertEquals(0, zeroNumbers)

assertEquals(2, sum(2))
assertEquals(12, sum(2, 4, 6))
```

如上所示，我们可以将零个或多个参数传递给vararg参数。

**在函数体内部，我们可以将vararg参数视为一个数组**：

```kotlin
fun <T> printAll(vararg ts: T) {
    ts.forEach { println(it) }
}
```

对于引用类型的可变参数，可变参数将被视为该引用类型的数组。例如，在上面的示例中，ts参数将作为函数体中的Array<T\>进行访问。在以下示例中，类似地，它将是一个Array<String\>：

```kotlin
fun printStrings(vararg vs: String) {
    vs.forEach { println(it) }
}
```

**然而，对于原始类型，vararg参数的作用类似于*Array专用数组类型**。例如，在sum()函数中，vararg参数是函数体中的[IntArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int-array/)。

## 3. 限制

**每个函数最多可以有一个可变参数**，如果我们声明一个带有多个可变参数的函数，Kotlin编译器将失败并报错：

```shell
Kotlin: Multiple vararg-parameters are prohibited
```

**与许多其他编程语言不同，没有必要将vararg参数声明为最后一个参数**：

```kotlin
fun createUser(vararg roles: String, username: String, age: Int) {
    // omitted
}
```

但是，当vararg不是最后一个声明的参数时，我们应该按名称传递其他参数以避免歧义：

```kotlin
createUser("admin", "user", username = "me", age = 42)
```

否则，编译器将失败并显示错误。

## 4. 扩展运算符

**有时我们在Kotlin中有一个现有的数组实例，我们想将它传递给一个接收可变参数的函数**，在这些情况下，要将数组分解为可变参数，我们可以使用扩展运算符：

```kotlin
val numbers = intArrayOf(1, 2, 3, 4)
val summation = sum(*numbers)
assertEquals(10, summation)
```

**numbers数组变量前面的“*”是扩展运算符**。

## 5. 字节码表示

**在底层，Kotlin将其可变参数转换为Java可变参数**，为了验证这一点，让我们通过kotlinc编译Kotlin源代码：

```bash
$ kotlinc Vararg.kt
```

之后，我们可以通过javap查看生成的字节码：

```shell
$ javap -c -p -v cn.tuyucheng.taketoday.varargs.VarargKt
    public static final int sum(int...);
        descriptor: ([I)I
        flags: (0x0099) ACC_PUBLIC, ACC_STATIC, ACC_FINAL, ACC_VARARGS
    // truncated
```

如上所示，vararg参数在Java中定义为可变参数，此外，ACC_VARARGS标志指定函数接收可变长度参数。

此外，在使用扩展运算符时：

```kotlin
val numbers = intArrayOf(1, 2)
sum(*numbers)
```

**Kotlin将使用**[Arrays.copyOf(array, length)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Arrays.html#copyOf(int[],int))**方法首先创建扩展数组的副本**，然后它将新数组作为可变参数传递：

```shell
12: aload_0 // loads the int[]
13: dup
14: arraylength
15: invokestatic  #71   // Method java/util/Arrays.copyOf:([II)[I
18: invokestatic  #72   // Method sum:([I)I
```

索引13和14将两个参数加载到操作数堆栈中，然后将它们传递给Arrays.copyOf(int[], int)方法。基本上，它将原始数组中的所有元素复制到一个新数组，并将复制的元素传递给sum()函数。

在Java中，[可变参数必须声明为最后一个参数](https://www.baeldung.com/java-varargs#rules)。因此，当我们在Kotlin中除最后一个以外的任何位置声明一个vararg参数时：

```kotlin
fun createUser(vararg roles: String, username: String, age: Int) {
    // omitted
}
```

**编译器将该特定的可变参数翻译成一个简单的数组**：

```shell
public static final void createUser(java.lang.String[], java.lang.String, int);
    descriptor: ([Ljava/lang/String;Ljava/lang/String;I)V
    flags: (0x0019) ACC_PUBLIC, ACC_STATIC, ACC_FINAL
```

正如我们所看到的，Kotlin编译器将“vararg roles: String”翻译成“String[]”，而不是预期的“String ...”结构。

## 6. 总结

在这个简短的教程中，我们了解了可变参数在Kotlin中的工作原理。此外，我们还了解了如何使用扩展运算符将Kotlin现有的数组类型转换为可变参数。