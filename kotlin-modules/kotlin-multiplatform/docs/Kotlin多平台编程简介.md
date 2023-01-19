## 一、简介

在本教程中，我们将了解 Kotlin 中的多平台编程。我们将开发一个面向 JVM、JS 和 Native 等多个平台的简单应用程序。

这也将帮助我们了解多平台编程的好处以及我们可以有效应用它们的不同用例。

## 2. 什么是多平台编程？

很多时候，我们编写的程序部分并不依赖于我们运行它们的平台。例如，我们调用 REST API 来获取一些数据并在返回结果之前应用一些额外的处理。当我们以后端为目标时，我们几乎必须在 Java 中此代码，当我们以 Web 为目标时，我们必须在 JS 中此代码，而当我们以移动平台为目标时，我们必须在 Android 或 iOS 中此代码。

如果我们可以只编写一次代码并针对多个平台，那不是很好吗？嗯，这是多平台编程的基本承诺！[Kotlin 多平台编程](https://kotlinlang.org/docs/reference/multiplatform.html)非常适合实现这一承诺。我们将在本教程中看到如何。

当我们用 Java 或 C 等高级语言编写程序时，我们必须编译它才能在 Windows 或 Linux 等平台上运行。然而，对于 Java，它被编译成一种称为字节码的中间格式。这赋予了 Java 著名的标语“一次编写，随处运行”。但是，我们仍然需要一个特定于本机平台的 JVM 来运行此字节码。

Kotlin multiplatform 将这个概念提升了一个档次，并承诺可以在 JVM、JS 甚至 Native 平台等多个平台上直接运行相同的代码。它不依赖于在目标平台上运行的虚拟机。这使得多平台编程成为 Kotlin 语言的主要优势之一。

此外，这大大减少了为不同平台编写和维护相同代码所需的工作量。

## 3. Kotlin 是如何支持的？

在继续了解多平台编程的魔力之前，我们应该花一些时间了解 Kotlin 实际上是如何支持它的。本节将介绍 Kotlin 提供的一些工具和技术，以支持并简化多平台编程的使用。

### 3.1. 涵盖基础知识

当我们可以获取一些 Java 代码并在任何 JVM 上运行它时，其背后的力量来自将代码转换为字节码的 Java 编译器。这就是使不同的语言能够针对相同的 JVM 的原因。例如，我们可以在同一个 JVM 平台上运行 Kotlin、Groovy 或 Scala 代码。它们每个都带有一个能够生成兼容字节码的编译器。

因此，我们没有理由不能提出可以采用相同代码并生成不同平台可以理解的格式的编译器。当然，这说起来容易做起来难，在某些情况下可能无法实现。在某些情况下，这可能不是编译器，而是转译器，它们不过是源到源的翻译器。

然而，这就是 Kotlin 支持多平台编程的方式。为了使通用代码在不同平台上工作，Kotlin提供了特定于平台的编译器和库，例如 Kotlin/JVM、Kotlin/JS 和 Kotlin/Native：

![Kotlin 多平台](https://www.baeldung.com/wp-content/uploads/sites/5/2021/01/Kotlin-Multiplatform.jpg)

在这里，我们在通用 Kotlin 中创建应用程序的可重用部分，并且凭借多平台编程支持，它可以在所有目标平台上运行。例如，调用 REST API 并获取一些数据可能是成为通用部分的一个很好的候选者。

### 3.2. 在平台之间重用源代码

Kotlin 多平台在层次结构中组织源代码，使依赖关系明确并在源集之间重用代码。默认情况下，所有特定于平台的源集都依赖于通用源集：

![Kotlin 多平台共享所有](https://www.baeldung.com/wp-content/uploads/sites/5/2021/01/Kotlin-Multiplatform-Shared-All-1024x305.jpg)

通用代码可以依赖于 Kotlin 为执行 HTTP 调用、执行数据序列化和管理并发性等典型任务而提供的许多库。此外，特定于平台的 Kotlin 版本提供了我们可以使用的库，可以利用目标平台的特定于平台的功能。

因此，我们决定保留可重用的业务逻辑，并开发应用程序的某些部分，例如利用本机功能的用户界面。此外，Kotlin 多平台编程允许我们在所有平台之间共享代码或[更有选择性地共享它们](https://kotlinlang.org/docs/reference/mpp-share-on-platforms.html#share-code-on-similar-platforms)：

![Kotlin 多平台共享层次结构](https://www.baeldung.com/wp-content/uploads/sites/5/2021/01/Kotlin-Multiplatform-Shared-Hierarchy-1024x448.jpg)

例如，在上图中，我们有跨所有平台共享的通用代码，但我们也有一些仅在Linux、Windows 和 macOS 等原生平台之间共享的通用本机代码。

### 3.3. 开发特定于平台的 API

到目前为止，我们已经了解了 Kotlin 多平台编程如何允许我们跨平台特定源集重用公共代码。但是，在某些情况下，可能需要共同定义和访问特定于平台的 API。这对于某些常见和可重用任务是专门的并且更有效地利用特定于平台的功能的领域特别有用。

Kotlin Multiplatform 提供了[预期](https://kotlinlang.org/docs/reference/mpp-connect-to-apis.html)[和](https://kotlinlang.org/docs/reference/mpp-connect-to-apis.html)[实际](https://kotlinlang.org/docs/reference/mpp-connect-to-apis.html)[声明的](https://kotlinlang.org/docs/reference/mpp-connect-to-apis.html)[机制](https://kotlinlang.org/docs/reference/mpp-connect-to-apis.html)来实现这一目标。例如，公共源集可以按预期声明一个函数，而特定于平台的源集将需要提供具有实际声明的相应函数：

![Kotlin Multiplatform 平台特定 API](https://www.baeldung.com/wp-content/uploads/sites/5/2021/01/Kotlin-Multiplatform-Platform-Specific-APIs.jpg)

在这里，正如我们所见，我们正在使用在公共源集中按预期声明的函数。公共代码不关心它是如何实现的。到目前为止，目标提供了此功能的特定于平台的实现。

我们可以将这些声明用于函数、类、接口、枚举、属性和注解。

### 3.4. 工具支持

由于 Kotlin 来自 JetBrains，JetBrains 也是开发用户友好的 IDE(如[IntelliJ IDEA](https://www.jetbrains.com/idea/) )的先驱，因此期待对多平台编程的集成支持是公平的。事实上，IntelliJ IDEA 提供了几个用于在 Kotlin 中创建多平台项目的项目模板。这使得创建多平台项目的整个过程非常无缝和快速。

当我们使用项目模板创建多平台项目时，它会自动应用[kotlin-multiplatform Gradle 插件](https://plugins.gradle.org/plugin/org.jetbrains.kotlin.multiplatform)：

```kotlin
plugins {
    kotlin("multiplatform") version "1.4.0"
}
```

这个kotlin-multiplatform 插件配置项目以创建在多个平台上工作的应用程序或库。像往常一样，配置进入文件build.gradle或build.gradle.kt，具体取决于我们选择的 DSL：

```kotlin
kotlin {
    jvm {
        withJava()
    }
    js {
        browser {
            binaries.executable()
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                .....
            }
        }
        val commonTest by getting {
            dependencies {
                .....
            }
        }
        val jvmMain by getting {
            dependencies {
                .....
            }
        }
        val jsMain by getting {
            dependencies {
                .....
            }
        }
    }
}
```

正如我们在配置中看到的那样，我们在顶部有一个“kotlin”扩展，其中包括目标、源集和依赖项的配置。

此外，每个目标可以有一个或多个编译。Kotlin 多平台项目使用编译来生成工件。对于每个目标，默认编译包括 JVM、JS 和本机目标的“主”和“测试”编译。

## 4. 多平台编程实践

本节将把我们到目前为止学到的一些理论付诸实践。我们将使用一些共享代码开发一个简单的计算器应用程序，我们将在 JVM、JS 和 Native 等多个目标平台上重复使用这些代码。

### 4.1. 创建多平台项目

我们将使用 IDEA 项目模板之一在 Kotlin 中生成多平台库的框架。我们来看看IntelliJ IDEA Community Edition中的项目模板选择向导：

![Kotlin 多平台 IDEA 向导](https://www.baeldung.com/wp-content/uploads/sites/5/2021/01/Kotlin-Multiplatform-IDEA-Wizard.jpg)

请注意，我们可以轻松地为其他类型的多平台项目创建框架，例如应用程序、移动库、移动应用程序、本地应用程序，甚至是全栈应用程序。使用上面的向导创建 Kotlin 多平台库项目后，它会为我们提供代码库的默认配置设置和结构：

![Kotlin 多平台项目结构](https://www.baeldung.com/wp-content/uploads/sites/5/2021/01/Kotlin-Multiplatform-Project-Structure-388x1024.jpg)

正如我们在上面看到的，该向导默认为 JVM、JS 和 Native 的公共代码和目标生成配置和结构。当然，我们可以手动删除目标或使用受支持平台的目标预设添加其他目标。

此外，我们可以根据需要更改默认目标 Gradle 配置。稍后，我们将看到如何更改这些配置以支持 JavaScript 模块中的前端应用程序和 Native 模块中的命令行应用程序。

### 4.2. 编写通用代码

一旦我们设置了项目结构，就可以编写一些我们稍后将在目标平台中使用的通用代码。公共代码位于我们之前创建的项目结构中的目录commonMain和commonTest中。

我们将编写一个简单的例程来模拟commonMain中的计算器：

```kotlin
fun add(num1: Double, num2: Double): Double {
    val sum = num1 + num2
    writeLogMessage("The sum of $num1 & $num2 is $sum", LogLevel.DEBUG)
    return sum
}

fun subtract(num1: Double, num2: Double): Double {
    val diff = num1 - num2
    writeLogMessage("The difference of $num1 & $num2 is $diff", LogLevel.DEBUG)
    return diff
}

fun multiply(num1: Double, num2: Double): Double {
    val product = num1  num2
    writeLogMessage("The product of $num1 & $num2 is $product", LogLevel.DEBUG)
    return product
}

fun divide(num1: Double, num2: Double): Double {
    val division = num1 / num2
    writeLogMessage("The division of $num1 & $num2 is $division", LogLevel.DEBUG)
    return division
}
```

正如我们所见，这些是简单的 Kotlin 函数，但它们可以跨多个平台重用。因此，我们从仅在一个地方声明和维护它们中受益匪浅。

这里唯一有趣的是函数writeLogMessage，我们还没有定义它。让我们看看如何定义它：

```kotlin
enum class LogLevel {
    DEBUG, WARN, ERROR
}

internal expect fun writeLogMessage(message: String, logLevel: LogLevel)
```

所以，在这里我们用关键字expect声明了函数writeLogMessage。正如我们之前所讨论的，这会强制多平台项目寻找使用关键字actual声明的函数的特定于平台的实现。这些声明必须具有相同的名称并出现在同一个包中。

现在，为什么我们要使用方法writeLogMessage来这样做？基本原理基本上是可能有一些例程具有特定于平台的依赖性。例如，使用一些特定于平台的功能可以更有效地编写日志。

此处的示例仅用于演示，不一定提供使用expect和actual声明的有效案例。然而，我们应该非常谨慎并谨慎使用expect和actual声明。我们的努力应该是在公共模块本身中实现尽可能多的功能。

### 4.3. 为通用代码编写测试

让我们为简单的计算器函数编写一些测试：

```kotlin
@Test
fun testAdd() {
    assertEquals(4.0, add(2.0, 2.0))
}

@Test
fun testSubtract() {
    assertEquals(0.0, subtract(2.0, 2.0))
}

@Test
fun testMultiply() {
    assertEquals(4.0, multiply(2.0, 2.0))
}

@Test
fun testDivide() {
    assertEquals(1.0, divide(2.0, 2.0))
}

```

这里没有什么特别的，这些都是纯粹、简单的单元测试，可以很好地完成它们的工作。然而，有趣的是，当我们想要运行它们时，我们会在 IntelliJ IDEA 中看到一个新窗口，要求我们选择一个目标：

![Kotlin 多平台运行单元测试](https://www.baeldung.com/wp-content/uploads/sites/5/2021/01/Kotlin-Multiplatform-Running-Unit-Tests.jpg)

这是可以理解的，因为我们必须定义要在哪个目标中运行我们的测试。我们可以选择多个目标来一次在所有目标中运行我们的测试。

## 5. 使用 Kotlin/JVM 以 JVM 为目标

当 Kotlin 开始时，它主要是为 Java 虚拟机 (JVM) 作为目标平台而设计的。它试图解决当时 Java 版本的一些典型挑战。然而，作为一种编程语言，Kotlin 从未绑定到 JVM，并且始终打算在多个平台上运行。

那么，既然我们已经在这里使用 Java 一段时间了，为什么要切换到 Kotlin 进行服务器端编程呢？虽然最近版本中的 Java 一直在努力弥合差距，但它仍然无法提供我们直接从 Kotlin 获得的一些好处。Kotlin非常适合编写简洁而富有表现力的代码，并具有一些很酷的功能，例如使用协程的结构化并发。

而且，Kotlin 提供[了 Java 平台特有的功能和注解，](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/)以及与 Java良好的[互操作性](https://kotlinlang.org/docs/reference/java-interop.html)。因此，我们可以根据需要使用多少。在本节中，我们将了解 Kotlin 如何使用 Kotlin/JVM 以 JVM 为目标。

### 5.1. Kotlin/JVM 编译器

Kotlin 自带一个编译器，可以将 Kotlin 源文件编译成 Java 类文件，我们可以在任何 JVM 上运行。通常，当我们构建像多平台库这样的 Kotlin 项目时，它会自动使用此编译器来生成类文件。不过，我们也可以使用kotlinc和kotlinc-jvm等命令行工具将 Kotlin 源文件编译成类文件。

可以将 Java 代码与 Kotlin 混合使用，但只有在绝对必要时才应该这样做。在多平台项目的 JVM 模块中，我们可以编写一个利用一些 Java 库的 Kotlin 源代码，或者如果更有意义的话，甚至可以编写一个 Java 源代码。

但是，Kotlin 编译器仅编译 Kotlin 源文件并在需要时从源目录加载任何 Java 引用。因此，我们还需要 Java 编译器来编译我们可能拥有的 Java 源文件：

![Kotlin 多平台 JVM 编译](https://www.baeldung.com/wp-content/uploads/sites/5/2021/01/Kotlin-Multiplatform-JVM-Compilation.jpg)

为了让多平台项目同时拥有 Java 和 Kotlin 源文件，我们需要在 Gradle 配置中进行必要的更改：

```kotlin
jvm {
    withJava()
}
```

也可以为特定版本的 JVM 生成类文件。默认情况下，它针对 Java 版本 1.6。我们可以通过在 Gradle 配置中配置它来定位像 1.8 这样的 Java 版本：

```kotlin
jvm {
    compilations.all {
        kotlinOptions.jvmTarget = "1.8"
    }
}
```

### 5.2. 在 Kotlin/JVM 中开发和重用代码

我们将在项目的jvmMain和jvmTest目录中添加特定于 JVM 平台的代码。我们必须在 JVM 目标中提供的第一件事是方法writeLogMessage的实现：

```kotlin
internal actual fun writeLogMessage(message: String, logLevel: LogLevel) {
    println("Running in JVM: [$logLevel]: $message")
}
```

这里没有什么特别之处可以利用 JVM 平台的优势。但是，请注意我们已经用声明actual标记了这个函数。

我们将为这个简单的应用程序编写一个 Java 源代码，以演示它如何与 Kotlin 共存。我们将编写一个简单的例程，利用常见的简单操作提供更多的数学运算：

```java
public static Double square(Double number) {
    return CalculatorKt.multiply(number, number);
}
```

这是一个简单的 Java 方法，它使用公共模块中的一个函数来工作。请注意我们如何在 Kotlin 中访问add函数作为 Java 中的静态方法。请注意，我们从未创建一个类来包装我们的函数add。但是Kotlin/JVM 编译器通过文件名生成一个类，并将函数添加为静态方法。

## 6. 使用 Kotlin/JS 定位 JavaScript

接下来，我们将看到如何使用[Kotlin/JS](https://kotlinlang.org/docs/reference/js-overview.html)从 Kotlin 多平台项目中将 JavaScript 平台作为目标。在我们进一步深入之前，让我们花一些时间了解将 JavaScript 作为目标的好处。JavaScript在编写前端应用程序方面相当流行。部分原因是大多数流行的网络浏览器很早就支持 JavaScript。

[Angular](https://angular.io/)、[React](https://reactjs.org/)和[Vue](https://vuejs.org/)等更复杂的库和框架的出现使开发前端应用程序变得更简单、更直观。此外，[Node.js](https://nodejs.org/en/)的加入使 JavaScript 也成为服务器端实现的流行选择，至少对于某些用例而言是这样。显然，将 JavaScript 作为目标对 Kotlin 开发人员来说意义重大。

JetBrains为 React、Mocha 和 styled-components 等流行的 JavaScript 库维护了几个 Kotlin 包装器。这些为 Kotlin 开发人员提供了方便的抽象，以便在 Kotlin 中编写类型安全的前端应用程序。此外，Gradle 插件提供了许多基本功能，例如使用[webpack控制和捆绑应用程序以及使用](https://webpack.js.org/)[yarn](https://yarnpkg.com/)从 npm 添加 JavaScript 依赖项。

### 6.1. 科特林/JS 编译器

Kotlin 版本附带的 Kotlin/JS 编译器将 Kotlin 源代码转换为 JavaScript 源代码。我们可以在任何 JavaScript 引擎(例如 Web 浏览器或 Node.js 附带的引擎)上执行生成的 JavaScript 源代码。因此，我们也可以将 Kotlin/JS 编译器称为转译器。

当前的 Kotlin/JS 编译器以[ECMAScript 5](https://www.ecma-international.org/ecma-262/5.1/) (ES5) 为目标并作为 JavaScript 的标准：

![Kotlin 多平台 JS 编译器](https://www.baeldung.com/wp-content/uploads/sites/5/2021/01/Kotlin-Multiplatform-JS-Compiler.jpg)

Kotlin 的最新版本带有一个基于中间表示 (IR) 的 Kotlin/JS 和 Kotlin/JVM 的备用编译器后端。从 Kotlin 1.4.0 开始，这仅作为 alpha 版本提供。这是为了给 Kotlin/JVM、Kotlin/JS 和 Kotlin/Native[提供一个基于 IR 的统一后端。](https://kotlinlang.org/docs/reference/whatsnew14.html#unified-backends-and-extensibility)

基本上，IR 编译器不是直接从 Kotlin 源代码生成 JavaScript 代码，而是首先将 Kotlin 源代码转换为中间表示 (IR)。然后 IR 编译器将这些中间表示进一步编译成目标表示，如 JavaScript 源代码。

这允许 IR 编译器执行积极的优化和其他默认编译器难以做到的事情。例如，对于 Kotlin/JS，它通过消除死代码生成更轻量级的可执行文件，并且能够生成 TypeScript 文件以实现更好的互操作性。

我们可以通过对 Gradle 配置进行更简单的更改，从默认编译器切换到 IR 编译器：

```kotlin
kotlin {
    js(IR) {
    }
}
```

正如我们之前所见，Kotlin/JS 项目可以针对两种不同的执行环境。其中包括用于在浏览器中编写客户端脚本的 Web 浏览器和用于在浏览器外编写服务器端脚本的 Node.js。

为 Kotlin/JS 选择执行环境同样是 Gradle 配置中的一个简单更改：

```kotlin
kotlin {
    js {
        browser {
        }   
    }
}
```

值得庆幸的是，Gradle 插件会自动配置其任务以使用选定的环境，使我们无需任何额外配置即可构建、运行和测试 Kotlin/JS 项目。

### 6.2. 在 Kotlin/JS 中开发和重用代码

对于我们的简单应用程序，我们将使用 React 为我们的计算器开发一个基本的前端。JavaScript 目标的代码将驻留在目录jsMain和jsTest中。

和以前一样，我们首先要添加的是函数writeLogMessage的实现，用声明actual标记它：

```kotlin
internal actual fun writeLogMessage(message: String, logLevel: LogLevel) {
    when (logLevel) {
        LogLevel.DEBUG -> console.log("Running in JS: $message")
        LogLevel.WARN -> console.warn("Running in JS: $message")
        LogLevel.ERROR -> console.error("Running in JS: $message")
    }
}
```

接下来，我们必须在 Gradle 配置中添加必要的前端依赖项：

```kotlin
val jsMain by getting {
    dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.2")
        implementation("org.jetbrains:kotlin-react:16.13.1-pre.110-kotlin-1.4.10")
        implementation("org.jetbrains:kotlin-react-dom:16.13.1-pre.110-kotlin-1.4.10")
        implementation("org.jetbrains:kotlin-styled:1.0.0-pre.110-kotlin-1.4.10")
    }
}
```

这些基本上是允许我们在 Kotlin 中编写 React 代码的 Kotlin 包装器。

好吧，我们在 React 中首先需要的是一个简单的 HTML 文件来锚定 React 可以定位的根元素：

```xml
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>JS Client</title>
</head>
<body>
<script src="kotlin-multiplatform.js"></script>
<div id="root"></div>
</body>
</html>
```

接下来，我们将编写一个简单的函数来在这个根元素上呈现我们的 React 应用程序：

```kotlin
fun main() {
    window.onload = {
        render(document.getElementById("root")) {
            child(Calculator::class) {
                attrs {
                    value = "0"
                }
            }
        }
    }
}
```

由于我们会将属性传递给我们的组件并且我们希望它保持状态，因此我们需要在 Kotlin 中定义它们：

```kotlin
external interface CalculatorProps : RProps {
    var value: String
}

data class CalculatorState(val value: String) : RState
```

最后，我们必须编写Calculator类来定义我们要加载的组件：

```kotlin
@JsExport
class Calculator(props: CalculatorProps) : RComponent<CalculatorProps, CalculatorState>(props) {

    init {
        state = CalculatorState(props.value)
    }

    override fun RBuilder.render() {
        styledLabel {
            css {
            }
            + "Enter a Number: "
        }
        styledInput {
            css {
            }
            attrs {
                type = InputType.number
                value = state.value
                onChangeFunction = { event ->
                    setState(
                        CalculatorState(value = (event.target as HTMLInputElement).value)
                    )
                }
            }
        }
        styledDiv {
            css {
            }
            +"Square of the Input: ${
                multiply(state.value.toDouble(), state.value.toDouble())}"
        }
    }
}
```

这是一个简单的 React 组件，它定义输入并使用我们输入的值更新本地状态。此外，它使用我们在公共模块中的函数乘积来计算平方并将其返回到组件中。

Gradle 插件附带了[webpack-dev-server](https://webpack.js.org/configuration/dev-server/)的支持，可以为生成的 JavaScript 工件提供服务。我们可以运行以下任务来运行 Kotlin/JS 项目：

```powershell
gradlew jsRun
```

然后我们可以使用任何浏览器访问我们的简单应用程序：

![Kotlin 多平台 React 接口 1](https://www.baeldung.com/wp-content/uploads/sites/5/2021/01/Kotlin-Multiplatform-React-Interface-1.jpg)

我们特意省略了样式，但它完全支持 CSS，我们可以根据需要创建任意花哨的界面。我们将在 Gradle 配置中做一个简单的更改以启用 webpack 的 CSS 和样式加载器：

```kotlin
browser {
    commonWebpackConfig {
        cssSupport.enabled = true
    }
}
```

## 7. 以 Kotlin/Native 为目标

原生平台可能是支持 Kotlin 的最多样化和最复杂的平台。Kotlin/Native 尝试 将 Kotlin 源代码直接编译为特定于受支持目标平台的本机二进制文件。但是，这样做有什么好处呢？

想象一下，我们正在开发一个我们打算在 Linux、Windows 或 macOS 上运行的桌面应用程序。当然，一种方法是针对每个平台单独开发它们，但不难理解这里的浪费精力。我们也可以开发它运行在像 JVM 这样的虚拟机上，但是我们需要这样的机器在所有原生平台上都可用。

如果我们可以只编写一次应用程序并生成特定于平台的二进制文件以在任何地方运行它们而无需任何依赖，那不是很好吗？这就是[Kotlin/Native](https://kotlinlang.org/docs/reference/native-overview.html)承诺为多个原生平台提供的功能。

此外，它还可以应用于许多其他用例，例如为多个平台开发移动应用程序。[在Android](https://www.android.com/intl/en_in/)和[iOS](https://www.apple.com/in/ios/ios-14/)上运行的单个移动应用程序可以为我们节省大量学习多个特定于平台的库并随着时间的推移维护它们的精力。

### 7.1. Kotlin/本机编译器

Kotlin/Native 为Kotlin/Native 编译器和 Kotlin 标准库的本地实现提供了一个基于[LLVM的后端。](https://llvm.org/)Kotlin/Native 编译器本身被称为 Konan。LLVM 基本上是一个编译器基础设施，我们可以用它来为任何编程语言开发前端，为任何指令集架构开发后端。

它提供了一种可移植的高级汇编语言，针对各种转换进行了优化，作为独立于语言的中间表示。最初是为 C 和 C++ 实现的，如今有多种语言具有支持 LLVM 的编译器，包括 Kotlin：

![Kotlin 多平台原生编译](https://www.baeldung.com/wp-content/uploads/sites/5/2021/01/Kotlin-Multiplatform-Native-Compilation-1024x382.jpg)

Kotlin/Native 支持的平台有很多，我们可以通过 Gradle 配置方便的选择：

-   Linux(x86_64、arm32、arm64、MIPS、MIPS 小端)
-   Windows (mingw x86_64, x86)
-   安卓(arm32、arm64、x86、x86_64)
-   iOS(arm32、arm64、模拟器 x86_64)
-   macOS (x86_64)
-   tvOS(arm64、x86_64)
-   watchOS(arm32、arm64、x86)
-   网络组装 (wasm32)

现在，我们应该注意到在我们的 Gradle 配置中，检查主机操作系统是否支持它：

```kotlin
kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }
}
```

使用 IntelliJ IDEA 项目模板开发多平台项目时，默认情况下会处理此问题。

### 7.2. 在 Kotlin/Native 中开发和重用代码

正如我们之前看到的，开发多平台移动应用程序可能是 Kotlin/Native 的主要用例之一。然而，它更详细地涵盖了开发和测试 Android 或 iOS 实际应用程序的细微差别。因此，我们将我们的应用程序限制为适用于 Windows 目标平台的简单命令行应用程序。

早些时候，我们开发了一个用户界面来利用通用模块中的简单操作来计算数字的平方。扩展它并创建一个命令行应用程序来执行相同的操作是合乎逻辑的。

按照惯例，Native 目标的代码将位于目录nativeMain和nativeTest中。首先，让我们为 Native 平台的函数writeLogMessage添加一个实现，用声明actual标记它：

```kotlin
internal actual fun writeLogMessage(message: String, logLevel: LogLevel) {
    println("Running in Native: [$logLevel]: $message")
}
```

接下来，我们需要在 Gradle 配置中为我们的应用程序定义一个入口点：

```kotlin
nativeTarget.apply {
    binaries {
        executable {
            entryPoint = "com.baeldung.kotlin.multiplatform.main"
        }
    }
}
```

最后，我们需要定义main函数，它将实际驱动我们的命令行应用程序：

```kotlin
fun main() {
    println("Enter a Number:")
    val number = readLine()!!.toInt()
    println("Square of the Input: ${multiply(number, number)}")
}
```

这里没有什么特别的事情发生，因为我们正在利用我们之前在通用模块中定义的功能产品。

我们可以通过在 Windows 的命令提示符下执行生成的可执行文件来实际看到这一点：

![Kotlin 多平台原生 CLI-1](https://www.baeldung.com/wp-content/uploads/sites/5/2021/01/Kotlin-Multiplatform-Native-CLI-1.jpg)

同样，如果我们在 Linux 或 macOS 等不同平台上构建此应用程序，它将为我们可以在没有任何其他依赖项的情况下本地运行的那些平台生成可执行文件。

## 八、总结

在本教程中，我们了解了多平台编程的基础知识以及 Kotlin 如何支持它。我们使用 IntelliJ IDEA 项目模板开发了一个简单的多平台项目。这使我们能够在 Kotlin 中创建一个通用模块。

此外，我们在 Kotlin/JS 中重用了这个通用模块的代码来开发基于 React 的用户界面，并在 Kotlin/Native 中重用了用于开发 Windows 命令行应用程序的代码。