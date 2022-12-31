## 1. 概述

在这篇文章中，我们将讨论Kotlin中受检异常的缺失及其影响。在此过程中，我们将学习@Throws注解如何促进从Java调用Kotlin方法和函数，我们还将了解何时应该以及何时不应该在我们的项目中使用此注解。

## 2. 没有受检异常

**在Kotlin中，没有**[受检异常](https://www.baeldung.com/java-checked-unchecked-exceptions)**之类的东西**，这与Java不同，因此，我们不必在Kotlin中使用throws子句来声明Java的受检异常。事实上，Kotlin甚至根本没有throws子句：

```kotlin
fun throwJavaUnchecked() {
    throw IllegalArgumentException()
}

fun throwJavaChecked() {
    throw IOException()
}
```

在上面的示例中，我们从Java标准库中抛出一个受检和非受检的异常，从这个例子中可以明显看出，Kotlin不关心Java世界中的异常类型，并且对所有异常都一视同仁。

**由于Kotlin中没有受检异常，我们可以很容易地将它们扔到不同的函数中，甚至无需在方法签名中声明它们**。

## 3. Java互操作性

众所周知，Kotlin最引人注目的特性之一是它与Java的出色互操作性。现在，鉴于Kotlin没有受检异常这一事实，让我们从Java类中调用上述函数：

```java
public class Caller {

    public static void main(String[] args) {
        unchecked();
    }

    public static void unchecked() {
        try {
            ThrowsKt.throwJavaUnchecked();
        } catch (IllegalArgumentException e) {
            System.out.println("Caught something!");
        }
    }
}
```

在这里，我们从Java调用throwJavaUnchecked()函数(在throws.kt文件中定义，因此名称是ThrowsKt)并捕获IllegalArgumentException异常，由于此异常在Java中是非受检的异常，因此代码编译并运行得非常好，因此，如果我们编译并运行它，它会捕获异常并打印预期的输出。

另一方面，如果我们在调用throwJavaChecked()函数时尝试捕获受检的IOException，代码甚至无法编译：

```java
public static void checked() {
    try {
        ThrowsKt.throwJavaChecked();
    } catch (IOException e) {
        System.out.println("Won't even compile");
    }
}
```

在这里，Java编译器将失败并显示错误消息：

```bash
java: exception java.io.IOException is never thrown in body of corresponding try statement
```

错误消息很明显：**它告诉我们被调用函数(在本例中为throwJavaChecked)没有声明IOException异常，但调用方方法正在捕获它**。在Java中，如果不使用throws子句声明它们，我们就无法捕获受检的异常。

现在，让我们看看Kotlin对这个互操作性问题的回答。

## 4. @Throws注解

为了解决这个Java互操作性问题，Kotlin提供了[@Throws注解](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-throws/)，**如果我们用@Throws注解标注Kotlin方法或函数，Kotlin将在其签名中使用throws子句编译该方法或函数**：

```kotlin
@Throws(IOException::class)
fun throwJavaChecked() {
    throw IOException()
}
```

如上所示，我们声明throwJavaChecked()函数将抛出IOException，显然，**此信息仅对Java客户端有益，对纯Kotlin代码库没有任何价值**。

现在在Java客户端中，当调用这个函数时，我们应该捕获IOException：

```java
try {
    ThrowsKt.throwJavaChecked();
} catch (IOException e) {
    System.out.println("It works this time!");
}
```

或者在throws子句中声明：

```java
public static void checked() throws IOException {
    ThrowsKt.throwJavaChecked();
}
```

因此，将此注解放在Kotlin函数之上在某种程度上类似于使用throws子句定义Java方法。请注意，从Kotlin 1.4开始，@Throws注解有一个[类型别名](https://www.baeldung.com/kotlin/type-aliases)：

```kotlin
@SinceKotlin("1.4")
public actual typealias Throws = kotlin.jvm.Throws
```

因此，如果我们使用的是旧版本，我们应该适当地导入注解：

```kotlin
import kotlin.jvm.Throws

@Throws(IOException::class)
fun throwJavaChecked() {
    throw IOException()
}
```

### 4.1 字节码表示

现在我们知道了如何使用注解，让我们看看它在幕后是什么样子的。首先，我们应该使用kotlinc编译Kotlin文件：

```bash
>> kotlinc throws.kt
```

然后，我们可以使用[javap](https://www.baeldung.com/java-class-view-bytecode)工具查看生成的字节码：

```bash
>> javap -c -p cn.tuyucheng.taketoday.throwsannotation.ThrowsKt
// omitted
public static final void throwJavaChecked() throws java.io.IOException;
```

如上所示，Kotlin编译器在字节码中发出一个throws子句，这是使用@Throws注解的效果。为了进行比较，另一个函数的字节码类似于：

```bash
public static final void throwJavaUnchecked();
```

显然，这里没有throws子句的迹象。

## 5. 总结

在这篇文章中，我们了解到Kotlin中没有受检异常这样的东西，此外，当从Java客户端调用Kotlin函数时，我们使用@Throws注解来促进使用受检的异常。

到目前为止，我们还应该知道，将此注解用于纯Kotlin项目没有意义，因为它的唯一目的是提供更好的Java互操作性。