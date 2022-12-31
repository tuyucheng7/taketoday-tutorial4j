## 1. 概述

在本教程中，我们将了解Kotlin中的显式API模式是什么，以及当它在严格模式或警告模式下启用时对我们的代码有何不同。我们将了解如何使用Gradle KTS、Gradle Groovy和命令行编译器方法将它添加到我们的项目中。

## 2. 什么是显式API模式？

**Kotlin提供显式API设置主要是为库作者提供工具，使库代码更安全、更清晰、更一致**。 

在这种模式下，Kotlin编译器会进行额外的检查以确保更明确的公共API定义。测试文件被忽略，因为它们不是公共API的一部分；**显式API模式仅适用于生产代码**。

### 2.1 可见性修饰符

在Kotlin中，任何给定函数或变量的默认[可见性修饰符](https://www.baeldung.com/kotlin/visibility-modifiers)都是public，通常，这不会产生太大影响。大多数时候，我们可以自由地重构我们的代码，而无需考虑下游依赖。

**显式API需要显式设置公共API，以免声明无意中变成公共API**。

为了演示这个概念，让我们编写一个简单的类，一个求和计算器：

```kotlin
class Calculator {
    fun sum(a: Int, b: Int) = a + b  
}
```

如果我们启用了显式API模式，编译器会抱怨Calculator类和sum函数都没有显式定义其可见性：

```bash
Visibility must be specified in explicitAPImode
```

让我们通过向两者添加public修饰符来解决这个问题：

```kotlin
public class Calculator {
    public fun sum(a: Int, b: Int) = a + b
}
```

### 2.2 显式类型说明

通常利用Kotlin的编译器来推断变量或函数的结果类型，一般而言，这是编写代码时的一项很棒的技术。然而，当转向更显式的API时，推断的类型是危险的，因为我们可以在没有通知的情况下更改类型，这绝不能是无意的。

**显式API要求公共API显式定义返回类型，以免无意中更改任何类型**。

让我们用sum()函数来演示这个概念。当我们写依赖编译器来推断类型的时候，我们可能会写这样的代码：

```kotlin
public fun sum(a: Int, b: Int) = a + b
```

编译器隐式发现类型为Int，为了使sum()函数显式符合API标准，我们必须显式添加类型：

```kotlin
public fun sum(a: Int, b: Int): Int = a + b
```

如果没有添加类型，编译器当然会报错：

```bash
Return type must be specified in explicitAPImode
```

此规则适用于公共API的任何部分。

保持类型明确将有助于我们API下游的用户，例如，在进行[类型检查或转换](https://www.baeldung.com/kotlin/type-checks-casts)时。

### 2.3 规则的例外情况

当启用显式API模式时，允许一些规则例外以避免重复代码：

-   主要构造函数
-   数据类的属性
-   属性getter和setter
-   覆盖方法

## 3. 显式API模式

**可能的显式API模式只有两种：Warning和Strict**。 

两种模式运行相同的逻辑，但警告不会使构建崩溃(它只会产生警告)。

另一方面，严格模式会产生错误并因此导致构建崩溃。

## 4. 开启显式API模式

要启用显式API模式，我们必须向编译器传递一个标志。在使用Gradle时，我们可以同时使用Kotlin和Groovy，我们也可以直接在命令行编译运行。让我们来看看所有这些选项：

### 4.1 在Gradle中使用KTS

在我们项目的build.gradle.kts中，我们必须将设置添加到kotlin配置块中。

以下是我们如何启用严格模式：

```kotlin
kotlin {
    explicitApi()
    // or
    explicitApi = ExplicitApiMode.Strict
}
```

现在，让我们看看如何启用警告模式：

```kotlin
kotlin {
    explicitApiWarning()
    // or
    explicitApi = ExplicitApiMode.Warning
}
```

### 4.2 在Gradle中使用Groovy

在我们项目的build.gradle中，我们必须将相同的设置添加到kotlin配置块中。

让我们启用严格模式：

```kotlin
kotlin {
    explicitApi()
    // or
    explicitApi = 'strict'
}
```

以下是我们如何启用警告模式：

```kotlin
kotlin {
    explicitApiWarning()
    // or
    explicitApi = 'warning'
}
```

### 4.3 在命令行编译器中

最后，我们可以直接使用CLI：

```bash
$ kotlinc MyKotlinFile.kt -Xexplicit-api=strict
```

在这里，我们将explicit-api标志添加到编译器，其值为strict；警告模式也可以这样做：

```bash
$ kotlinc MyKotlinFile.kt -Xexplicit-api=warning
```

## 5. 总结

在本文中，我们了解了Kotlin中的显式API模式，我们看到了它如何帮助库维护者保持他们的公共API不易出错。

我们还查看了显式API模式强加的规则，并学习了如何使用Gradle KTS、Gradle Groovy和命令行编译器在我们的项目中启用它。