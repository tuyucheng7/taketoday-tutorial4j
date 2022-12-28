## 1. 概述

在本快速教程中，我们将熟悉Kotlin中的类型别名及其用例。在此过程中，我们还将看到此功能如何在字节码级别表现出来。

## 2. 类型别名

**在Kotlin中，可以使用类型别名为现有类型声明替代名称**，让我们看一下基本语法：

```kotlin
typealias <NewName> = <ExistingType>
```

在某些情况下，这些别名可能很有用。例如，**我们可以使用它们将更多上下文附加到其他通用类型**：

```kotlin
typealias CreditCard = String
fun linkCard(card: CreditCard) {
    // omitted
}
```

如上所示，通过为String定义类型别名，我们向调用者传达了更多信息和上下文。

使用类型别名很简单：

```kotlin
val cc: CreditCard = "1234****"
linkCard(cc)
```

在这里，我们将String字面值分配给CreditCard类型的变量。此外，我们也可以对CreditCard实例执行字符串操作：

```kotlin
val other = cc.toUpperCase()
```

除此之外，我们可以在任何需要CreditCard实例的地方使用String：

```kotlin
linkCard("1234")
```

尽管linkCard函数需要一个CreditCard实例，但我们将一个String实例传递给它。

类型别名还有其他用例，例如，**我们可以使用它们为函数字面值创建一个具有更有意义名称的新类型**：

```kotlin
class HttpRequest
class HttpResponse
typealias RequestHandler = (HttpRequest) -> HttpResponse
```

在这里，我们将只使用RequestHandler类型，而不是将Web控制器定义为HttpRequest -> HttpResponse的函数。

下面是Java 8中一个更熟悉的示例-声明泛型类型别名：

```kotlin
typealias Predicate<T> = (T) -> Boolean
```

此外，**我们可以使用类型别名来缩短一些长数据类型名称**：

```kotlin
typealias Completed = CompletableFuture<Map<String, Pair<String, Int>>>
```

## 3. 字节码表示

**类型别名只是源代码的产物，因此，他们不会在运行时引入任何新类型**。例如，在我们使用CreditCard实例的每个地方，Kotlin编译器都会将其转换为String。

为了验证这种转换是否发生，我们将查看生成的字节码。

首先，让我们通过kotlinc编译Kotlin代码：

```bash
$ kotlinc TypeAlias.kt
```

然后，我们可以通过javap查看生成的字节码：

```bash
$ javap -c -p cn.tuyucheng.taketoday.alias.TypeAliasKt
Compiled from "TypeAlias.kt"
public final class cn.tuyucheng.taketoday.alias.TypeAliasKt {
    public static final void linkCard(java.lang.String);
    // truncated
}
```

如我们所见，linkCard方法接收一个String而不是声明的类型别名。因此，如前所述，**Kotlin编译器会在编译时擦除类型别名，并在运行时使用底层类型**。

## 4. 总结

在这个简短的教程中，我们了解了如何以及何时在Kotlin中使用类型别名。然后，我们查看了这个特性的字节码表示形式。