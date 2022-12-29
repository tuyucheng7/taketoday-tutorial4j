## 1. 简介

**Kotlin在定义扩展后引入了扩展函数的概念，这是一种使用新功能扩展现有类的便捷方式，无需使用继承或任何形式的装饰器模式**。我们基本上可以使用它，因为它是原始API的一部分。

这对于使我们的代码易于阅读和维护非常有用，因为即使我们无法访问源代码，我们也能够添加特定于我们需求的方法，并使它们看起来像是原始代码的一部分。

例如，我们可能需要对String进行XML转义，在标准Java代码中，我们需要编写一个可以执行此操作并调用它的方法：

```java
String escaped = escapeStringForXml(input);
```

虽然是用Kotlin编写的，但该片段可以替换为：

```kotlin
val escaped = input.escapeForXml()
```

这不仅更易于阅读，而且IDE将能够将该方法作为自动完成选项提供，就像它是String类上的标准方法一样。

## 2. 标准库扩展函数

Kotlin标准库附带了一些开箱即用的扩展函数。

### 2.1 上下文调整扩展函数

**存在一些通用扩展，并且可以应用于我们应用程序中的所有类型**。这些可用于确保代码在适当的上下文中运行，并且在某些情况下可用于确保变量不为空。

事实证明，我们很可能在没有意识到这一点的情况下利用了扩展。

最流行的函数之一可能是let()函数，它可以在Kotlin中的任何类型上调用，让我们将一个函数传递给它，该函数将在初始值上执行：

```kotlin
val name = "Tuyucheng"
val uppercase = name
    .let { n -> n.toUpperCase() }
```

它类似于Optional或Stream类中的map()方法，在这种情况下，我们传递一个函数来表示将给定字符串转换为其大写表示形式的操作。

**变量name称为调用的接收者**，因为它是扩展函数所操作的变量。

这对安全调用运算符非常有效：

```kotlin
val name = maybeGetName()
val uppercase = name?.let { n -> n.toUpperCase() }
```

**在这种情况下，仅当变量name为非空时，才会计算传递给let()的块**。这意味着在块内部，值n保证是非空的。

let()的其他替代方法也很有用，具体取决于我们的需要。

**run()扩展与let()的工作方式相同，但在被调用块中提供了一个接收器作为this值**：

```kotlin
val name = "Tuyucheng"
val uppercase = name.run { toUpperCase() }
```

**apply()与run( )的工作方式相同，但它返回一个接收器而不是从提供的块中返回值**。

让我们利用apply()来链接相关调用：

```kotlin
val languages = mutableListOf<String>()
languages.apply {
    add("Java")
    add("Kotlin")
    add("Groovy")
    add("Python")
}.apply {
    remove("Python")
}
```

请注意我们的代码如何变得更加简洁和富有表现力，而不必显式使用this或it。

**also()扩展的工作方式与let()类似，但它以与apply()相同的方式返回接收者**：

```kotlin
val languages = mutableListOf<String>()
languages.also { list ->
    list.add("Java")
    list.add("Kotlin")
    list.add("Groovy")
}
```

**takeIf()扩展提供了一个作用于接收者的谓词，如果这个谓词返回true，则它返回接收者，否则返回null**，这类似于常见的map()和filter()方法的组合：

```kotlin
val language = getLanguageUsed()
val coolLanguage = language.takeIf { l -> l == "Kotlin" }
```

**takeUnless()扩展与takeIf()相同，但具有相反的谓词逻辑**。

```kotlin
val language = getLanguageUsed()
val oldLanguage = language.takeUnless { l -> l == "Kotlin" }
```

### 2.2 集合的扩展函数

**Kotlin在标准的Java Collections基础上增加了大量的扩展函数，可以让我们的代码更易于使用**。

这些方法位于_Collections.kt、_Ranges.kt和_Sequences.kt以及_Arrays.kt中，用于将等效方法应用于数组。(请记住，在Kotlin中，数组可以被视为与集合相同)

这些扩展方法太多了，无法在这里讨论，因此请浏览这些文件以查看可用的内容。

**除了Collections之外，Kotlin还向String类添加了大量扩展函数-在_Strings.kt中定义**，这些允许我们将字符串视为字符集合。

所有这些扩展方法协同工作，使我们能够编写更清晰、更易于维护的代码，而不管我们使用的是哪种集合。

## 3. 编写我们的扩展函数

那么，如果我们需要用新功能扩展一个类-无论是从Java或Kotlin标准库，还是从我们正在使用的依赖库？

**扩展函数的编写方式与任何其他函数一样**，但接收器类作为函数名称的一部分提供，并以点分隔。

例如：

```kotlin
fun String.escapeForXml(): String {
    // ....
}
```

这将定义一个名为escapeForXml的新函数作为String类的扩展，允许我们按上述方式调用它。

**在这个函数内部，我们可以使用this访问接收者，就像我们在String类本身中编写this一样**：

```kotlin
fun String.escapeForXml() : String {
    return this
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
}
```

### 3.1 编写泛型扩展函数

如果我们想编写一个一般适用于多种类型的扩展函数怎么办？我们可以扩展Any类型，这相当于Java中的Object类，但还有更好的方法。

**扩展函数可以应用于泛型接收器以及具体接收器**：

```kotlin
fun <T> T.concatAsString(b: T): String {
    return this.toString() + b.toString()
}
```

这可以应用于满足泛型要求的任何类型，并且在函数内部这个值是类型安全的。

例如，使用上面的例子：

```kotlin
5.concatAsString(10) // compiles
"5".concatAsString("10") // compiles
5.concatAsString("10") // doesn't compile
```

### 3.2 编写中缀扩展函数

中缀方法对于编写DSL风格的代码很有用，因为它们允许在没有句点或括号的情况下调用方法：

```kotlin
infix fun Number.toPowerOf(exponent: Number): Double {
    return Math.pow(this.toDouble(), exponent.toDouble())
}
```

现在，我们可以像调用任何其他中缀方法一样调用它：

```kotlin
3 toPowerOf 2 // 9
9 toPowerOf 0.5 // 3
```

### 3.3 编写运算符扩展函数

我们还可以编写一个运算符函数作为扩展。

**运算符方法允许我们利用运算符简写而不是完整的方法名称**，例如，可以使用+运算符调用plus运算符方法：

```kotlin
operator fun List<Int>.times(by: Int): List<Int> {
    return this.map { it * by }
}
```

同样，这与任何其他运算符方法的工作方式相同：

```kotlin
listOf(1, 2, 3) * 4 // [4, 8, 12]
```

## 4. 从Java调用Kotlin扩展函数

现在让我们看看Java如何与Kotlin扩展函数一起运行。

**一般来说，我们在Kotlin中定义的每一个扩展函数都可以在Java中使用**，不过，我们应该记住，中缀方法仍然需要使用点和括号来调用。与运算符扩展相同-我们不能只使用加号字符(+)，这些设施仅在Kotlin中可用，毕竟Java中从不支持这种语法。

但是，我们不能在Java中调用一些标准的Kotlin库方法，比如let或apply，因为它们用@InlineOnly标记。

### 4.1 自定义扩展函数在Java中的可见性

让我们使用之前定义的扩展函数之一String.escapeXml()，我们包含扩展方法的文件称为StringUtil.kt。

现在，当我们需要从Java调用扩展方法时，我们需要使用类名StringUtilKt，**请注意，我们必须添加Kt后缀**：

```java
String xml = "<a>hi</a>";

String escapedXml = StringUtilKt.escapeForXml(xml);

assertEquals("&lt;a&gt;hi&lt;/a&gt;", escapedXml);
```

请注意第一个escapeForXml参数，此附加参数是扩展函数接收器类型，**具有顶层扩展函数的Kotlin是一个带有静态方法的纯Java类**，这就是为什么它需要以某种方式传递原始字符串。

当然，就像在Java中一样，我们可以使用静态导入：

```java
import static cn.tuyucheng.taketoday.kotlin.StringUtilKt.*;
```

### 4.2 调用内置的Kotlin扩展函数

Kotlin通过提供许多内置的扩展函数来帮助我们更轻松、更快速地编写代码。例如，有String.capitalize()方法，可以直接从Java中调用：

```java
String name = "john";

String capitalizedName = StringsKt.capitalize(name);

assertEquals("John", capitalizedName);
```

但是，**我们不能从Java中调用带有@InlineOnly标记的扩展方法**，例如：

```kotlin
inline fun <T, R> T.let(block: (T) -> R): R
```

### 4.3 重命名生成的Java静态类

我们已经知道Kotlin扩展函数是一种静态Java方法，让我们使用注解@file:JvmName(name: String)重命名生成的Java类。

这必须添加到文件的顶部：

```kotlin
@file:JvmName("Strings")
package cn.tuyucheng.taketoday.kotlin

fun String.escapeForXml() : String {
    return this
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
}
```

现在，当我们想要调用扩展方法时，我们只需要添加Strings类名：

```java
Strings.escapeForXml(xml);
```

此外，我们仍然可以添加静态导入：

```java
import static cn.tuyucheng.taketoday.kotlin.Strings.*;
```

## 5. 总结

扩展函数是扩展系统中已经存在的类型的有用工具，要么是因为它们没有我们需要的功能，要么只是为了使某些特定区域的代码更易于管理。

我们在这里看到了一些可以在系统中使用的扩展函数，此外，我们探索了扩展函数的各种可能性。