## 1. 概述

从版本7开始，Java支持在一个catch块中[捕获多个异常](https://www.baeldung.com/java-exceptions)，例如：

```java
//Javacode
try {
    // ...
} catch (Exception1 | Excpetion2 ex) {
    // Perform some common operations with ex
}
```

但是，**直到当前版本(1.7.0)，Kotlin才支持此功能**。因此，在本教程中，我们将探讨如何在Kotlin中实现“多捕获”功能。

## 2. 创建示例

为了演示本教程中的解决方案，让我们首先创建一个示例函数来生成异常。

### 2.1 KeyService和异常

假设我们需要创建一个KeyService来存储字符串密钥，此外，我们对保存一个“Key”有几个要求：

-   密钥的长度必须为6
-   密钥中只允许使用数字
-   同一个密钥只能存储一次

因此，让我们创建四个异常：

```kotlin
class KeyTooLongException(message: String = "Key-length must be six.") : Exception(message)
class KeyTooShortException(message: String = "Key-length must be six.") : Exception(message)
class InvalidKeyException(message: String = "Key should only contain digits.") : Exception(message)
class KeyAlreadyExistsException(message: String = "Key exists already.") : Exception(message)

```

接下来，让我们创建KeyService来检查和存储给定的密钥：

```kotlin
object KeyService {
    private val keyStore = mutableSetOf<String>()
    fun clearStore() = keyStore.clear()
    fun saveSixDigitsKey(digits: String) {
        when {
            digits.length < 6 -> throw KeyTooShortException()
            digits.length > 6 -> throw KeyTooLongException()
            digits.matches(Regex("""\d{6}""")).not() -> throw InvalidKeyException()
            digits in keyStore -> throw KeyAlreadyExistsException()
            else -> keyStore += digits
        }
    }
}
```

如上面的代码所示，我们在[when{}](https://www.baeldung.com/kotlin/when)块中验证并保存输入密钥。

为简单起见，我们将KeyService设为一个[对象](https://www.baeldung.com/kotlin/objects)。此外，在验证之后，我们将密钥存储在[Set](https://www.baeldung.com/kotlin/collections-api#2-sethttps://www.baeldung.com/kotlin/collections-api#2-set)中。

### 2.2 SaveKeyResult枚举

由于我们将解决在save()函数中处理多个异常的不同解决方案，让我们创建一个[枚举](https://www.baeldung.com/kotlin/enum)来表示save函数的结果：

```kotlin
enum class SaveKeyResult {
    SUCCESS, FAILED, SKIPPED_EXISTED_KEY
}
```

上面的枚举具有三个常量：

-   SUCCESS：给定密钥有效且已成功存储
-   FAILED：当抛出KeyTooShortException、KeyTooLongException或InvalidKeyException时
-   SKIPPED_EXISTED_KEY：密钥有效，但已存在于Set中。换句话说，我们希望在抛出KeyAlreadyExistsException时得到这个结果

所以接下来，我们将实现save()函数的不同方法来调用KeyService.saveSixDigitsKey()。当然，**我们的save()函数会处理异常并返回相应的SaveKeyResult**，为了区分每个save()函数，我们将在每个解决方案的函数名称后添加一个索引后缀，比如save1()、save2()等。

为简单起见，我们将使用单元测试断言来验证我们的解决方案是否按预期工作。此外，我们将使用相同的输入数据来测试每种方法，因此，我们需要在每次测试开始前清空keystore集合：

```kotlin
@BeforeEach
fun cleanup() {
    KeyService.clearStore()
}
```

接下来，让我们看看如何在Kotlin中捕获多个异常。

## 3. 多个catch块

**最直接的解决方案是捕获每个异常并处理所需的操作**，例如相应地返回SaveKeyResult实例：

```kotlin
fun save1(theKey: String): SaveKeyResult {
    return try {
        KeyService.saveSixDigitsKey(theKey)
        SUCCESS
    } catch (ex: KeyTooShortException) {
        FAILED
    } catch (ex: KeyTooLongException) {
        FAILED
    } catch (ex: InvalidKeyException) {
        FAILED
    } catch (ex: KeyAlreadyExistsException) {
        SKIPPED_EXISTED_KEY
    }
}
```

上面的代码非常容易理解。接下来，让我们使用不同类型的输入对其进行测试：

```kotlin
assertEquals(FAILED, save1("42"))
assertEquals(FAILED, save1("1234567"))
assertEquals(FAILED, save1("kotlin"))
assertEquals(SUCCESS, save1("123456"))
assertEquals(SKIPPED_EXISTED_KEY, save1("123456"))
```

正如上面的断言所示，我们已经涵盖了save1()执行的所有情况，如果我们运行测试，它就会通过。所以，这个解决方案有效。

这种方法非常简单，但是，缺点也很明显。我们在多个catch块中有重复项，**我们需要为每个异常情况创建一个catch块**。

接下来，让我们看看是否可以对save1()进行一些改进。

## 4. 在catch块中使用when

减少重复代码的一种想法是只有一个catch块来捕获异常，然后，**在catch块中，我们使用when{}来检查异常类型并执行所需的操作**：

```kotlin
fun save2(theKey: String): SaveKeyResult {
    return try {
        KeyService.saveSixDigitsKey(theKey)
        SUCCESS
    } catch (ex: Exception) {
        when (ex) {
            is KeyTooLongException,
            is KeyTooShortException,
            is InvalidKeyException -> FAILED
            is KeyAlreadyExistsException -> SKIPPED_EXISTED_KEY
            else -> throw ex
        }
    }
}
```

如上面的代码所示，我们捕获通用的异常类型并在when{}块中检查具体的异常类型以模拟捕获多个异常。

接下来，让我们使用相同的测试输入来测试save2()函数：

```kotlin
assertEquals(FAILED, save2("42"))
assertEquals(FAILED, save2("1234567"))
assertEquals(FAILED, save2("kotlin"))
assertEquals(SUCCESS, save2("123456"))
assertEquals(SKIPPED_EXISTED_KEY, save2("123456"))
```

同样，如果我们执行测试，测试就会通过。

## 5. 创建multiCatch()函数

Kotlin有一个很好的特性：[扩展函数](https://www.baeldung.com/kotlin/extension-methods)。那么接下来，让我们看看是否可以使用扩展函数来解决这个问题。

首先，让我们看一下常规的try-catch结构：

```kotlin
try {
    DO_SOMETHING
} catch (ex: Exception) {
    // ...
}
```

**我们可以将DO_SOMETHING块视为一个不带参数并返回某种类型(R)的函数：() -> R**；然后，我们可以扩展() ->R函数类型来处理多个异常：

```kotlin
inline fun <R> (() -> R).multiCatch(vararg exceptions: KClass<out Throwable>, thenDo: () -> R): R {
    return try {
        this()
    } catch (ex: Exception) {
        if (ex::class in exceptions) thenDo() else throw ex
    }
}
```

在这里，multiCatch()函数有两个参数；第一个是[可变参数](https://www.baeldung.com/java-varargs)，包含“多重异常”的类型，如果定义的异常中的任何异常发生，将执行一个函数，[thenDo()函数](https://www.baeldung.com/kotlin/interfaces-higher-order-functions)是第二个参数。

正如我们在上面的代码中看到的，我们将try-catch块包装在multiCatch()函数中。此外，值得一提的是，**我们已将multiCatch()函数声明为**[内联](https://www.baeldung.com/kotlin/inline-functions)**函数以获得更好的性能**。

接下来让我们看看如何使用save3()中的multiCatch()函数：

```kotlin
fun save3(theKey: String): SaveKeyResult {
    return try {
        {
            KeyService.saveSixDigitsKey(theKey)
            SUCCESS
        }.multiCatch(
            KeyTooShortException::class,
            KeyTooLongException::class,
            InvalidKeyException::class
        ) { FAILED }
    } catch (ex: KeyAlreadyExistsException) {
        SKIPPED_EXISTED_KEY
    }
}
```

正如我们所看到的，**我们将KeyService.saveSixDigitsKey(theKey)和SUCCESS包装在{ ... } 中，使其成为() -> SaveKeyResult函数**。然后，我们可以调用预定义的multiCatch()函数，第一个参数是三种异常类型，第二个参数非常简单：一个返回FAILED的函数。

除了这三个异常之外，我们还需要处理其他异常类型；因此，我们有一个外部try-catch块来处理KeyAlreadyExistsException异常。

接下来，让我们测试save3()是否按预期工作：

```kotlin
assertEquals(FAILED, save3("42"))
assertEquals(FAILED, save3("1234567"))
assertEquals(FAILED, save3("kotlin"))
assertEquals(SUCCESS, save3("123456"))
assertEquals(SKIPPED_EXISTED_KEY, save3("123456"))
```

如果我们运行，测试就会通过。

该解决方案定义了一个有意义的multiCatch()函数，因此更易于阅读，但是，缺点也很明显。首先，multiCatch()函数返回一个R对象而不是this(()->R函数)，因此，**它只能处理一组“multiCatch – required”异常**。另一个不便之处就像我们在示例中看到的那样-如果我们需要捕获其他异常类型，我们仍然需要一个外部try-catch结构。

接下来，让我们看看我们是否可以保持可读性并解决save3()带来的不便。

## 6. 扩展Result类

Kotlin引入了[Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/)类来封装一些处理的结果，在本节中，我们将通过扩展Result类来实现捕获多个异常。

### 6.1 runCatching()和Result<T\>简介

当我们执行一些操作时，**我们可以用标准的**[runCatching()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/run-catching.html)**函数包装这些操作以获得一个Result<T\>对象作为结果**，例如：

```kotlin
runCatching {
    KeyService.saveSixDigitsKey(theKey)
    SUCCESS
}
```

上面的代码将返回一个Result<SaveKeyResult\>对象，如果成功执行了saveSixDigitsKey()函数，则Result对象的isSuccess属性为true，并且结果包含值SUCCESS。Kotlin提供了几种方法来获取包装在Result对象中的值，例如：[getOrThrow()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/get-or-throw.html)、[getOrDefault()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/get-or-default.html)和[getOrElse()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/get-or-else.html)。

否则，如果在执行过程中抛出异常，则Result对象的isFailure属性将被设置为true，并且结果对象包含异常。

**在失败的情况下，Result<T\>类允许我们通过调用**[recoverCatching()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/recover-catching.html)**函数将异常转换为另一个对象作为结果的值**：

```kotlin
inline fun <R, T : R> Result<T>.recoverCatching(
    transform: (exception: Throwable) -> R
): Result<R>
```

那么接下来，让我们看看这些是如何帮助我们处理多重异常的。

### 6.2 扩展Result<T\>类

现在我们了解了recoverCatching()和Result<T\>的工作原理，我们可以创建Result<T\>类的扩展来处理多个异常。

接下来，让我们看一下实现，然后了解它是如何工作的：

```kotlin
inline fun <R, T : R> Result<T>.onException(
    vararg exceptions: KClass<out Throwable>,
    transform: (exception: Throwable) -> T
) = recoverCatching { ex ->
    if (ex::class in exceptions) {
        transform(ex)
    } else throw ex
}
```

正如我们所看到的，我们已经为Result类创建了一个扩展onException()。onException()函数有两个参数，第一个是可变参数，一组异常类型；第二个参数是transform()函数。

onException()函数基于recoverCatching()，因此，**只有当结果的isFailure为true时，我们的异常处理才会执行。此外，transform()函数定义了在抛出某些异常时我们要做什么**。

那么接下来，让我们使用声明的onException()函数创建save4()：

```kotlin
fun save4(theKey: String): SaveKeyResult {
    return runCatching {
        KeyService.saveSixDigitsKey(theKey)
        SUCCESS
    }.onException(
        KeyTooShortException::class,
        KeyTooLongException::class,
        InvalidKeyException::class
    ) {
        FAILED
    }.onException(KeyAlreadyExistsException::class) {
        SKIPPED_EXISTED_KEY
    }.getOrThrow()
}
```

这一次，正如我们所见，因为我们使用了runCatching()，所以我们不再有任何try-catch结构。此外，**如果我们需要处理多个“multiCatch”组，我们可以简单地添加更多.onException{ ... }块**。 

最后，让我们测试一下我们的save4()是否按预期工作：

```kotlin
assertEquals(FAILED, save4("1234567"))
assertEquals(FAILED, save4("42"))
assertEquals(FAILED, save4("kotlin"))
assertEquals(SUCCESS, save4("123456"))
assertEquals(SKIPPED_EXISTED_KEY, save4("123456"))
```

当我们运行测试时，它通过了。

## 7. 总结

在本文中，我们学习了如何在Kotlin中捕获多个异常。