## 1. 概述

在这个快速教程中，我们将熟悉Kotlin中的支持字段及其用例。

首先，我们将从Kotlin中的属性和自定义访问器的简短介绍开始，然后我们将在自定义访问器示例中使用支持字段。最后，我们将看到如何在字节码级别实现支持字段。

## 2. 支持字段

Kotlin支持声明可变或不可变属性，我们所要做的就是用[val或var](https://www.baeldung.com/kotlin-const-var-and-val-keywords)标记属性：

```kotlin
data class HttpResponse(val body: String, var headers: Map<String, String>)
```

在上面的示例中，Kotlin编译器为body属性生成一个getter，并为headers属性生成一个getter和setter。在底层，Kotlin将使用Java字段来存储属性值，**这些Java字段在Kotlin世界中被称为支持字段**。

然而，有时我们可能需要创建自定义访问器来控制它们的逻辑。例如，我们在这里为hasBody属性创建一个简单的getter：

```kotlin
data class HttpResponse(val body: String, var headers: Map<String, String>) {
    val hasBody: Boolean
        get() = body.isNotBlank()
}
```

如果body为空，则hasBody的getter返回true，由于Kotlin可以从body属性计算hasBody值，因此它不会为其生成支持字段。

现在假设当且仅当给定值介于100和599之间时，我们将设置statusCode属性。在我们的第一次尝试中，我们可能会尝试如下操作：

```kotlin
var statusCode: Int = 100
    set(value) {
        if (value in 100..599) statusCode = value
    }
```

每次我们设置statusCode属性时，Kotlin都会调用set(value)自定义访问器。当前的实现是一个无休止的递归调用，因为我们也在setter本身内部设置了这个字段。

**为了避免这种无休止的递归，我们可以使用Kotlin为该属性生成的支持字段**：

```kotlin
var statusCode: Int = 100
    set(value) {
        if (value in 100..599) field = value
    }
```

如上所示，当属性需要其支持字段时，Kotlin会自动提供它。此外，**我们可以通过field标识符引用自定义访问器中的支持字段**。

**简而言之，支持字段是存储属性值的位置**。在最后一个示例中，支持字段帮助我们避免了递归问题。

## 3. 字节码表示

**如果我们至少使用一个默认访问器或者我们在自定义访问器中引用field标识符，Kotlin将为属性生成一个支持字段**。默认访问器是使用val或var关键字生成的访问器。

为了验证这个说法，我们可以看一下生成的字节码。首先，让我们使用kotlinc编译Kotlin代码：

```bash
$ kotlinc BackingField.kt
```

现在，我们可以使用javap检查字节码：

```bash
$ javap -c -p cn.tuyucheng.taketoday.backingfield.HttpResponse 
Compiled from "BackingField.kt"
public final class com.baeldung.backingfield.HttpResponse {
    private int statusCode;
    private final java.lang.String body;
    private java.util.Map<java.lang.String, java.lang.String> headers;
    // truncated
```

如上所示，Kotlin为三个属性生成了支持字段：

-   body属性，因为它仅使用默认访问器
-   headers属性同理 
-   statusCode属性，因为它引用了field标识符

但是，由于hasBody属性不满足这两个条件中的任何一个，Kotlin没有为其生成支持字段。因此，在生成的字节码中没有这样一个字段的迹象。

## 4. 总结

在这个简短的教程中，我们快速回顾了Kotlin中的属性和自定义访问器。之后，我们熟悉了Kotlin中的支持字段及其用例，以及它们的字节码表示。