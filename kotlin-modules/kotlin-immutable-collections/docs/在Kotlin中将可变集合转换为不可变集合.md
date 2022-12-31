## 1. 概述

有时我们希望将内部状态保留在可变集合中，但公开不可变变体，以防止接口滥用。在本文中，我们将研究**将可变集合更改为不可变集合的不同方法**。

## 2. 内置不可变集合

其中一种方法是使用Kotlin的默认可变和不可变集合，作为示例，让我们看一下MutableList和List。首先，我们将创建一个可变列表并向其中添加一个元素：

```kotlin
val mutableList = mutableListOf<String>()

mutableList.add("Hello")

// Prints "Hello"
println(mutableList.joinToString())
```

我们可以从我们的可变列表中创建一个不可变列表：

```kotlin
val immutableList: List<String> = mutableList.toList()
```

但是如果我们尝试修改它，我们会得到一个错误：

```kotlin
// Throws an error
immutableList[0] = "World"
```

我们甚至可以制作不可变列表的可变副本，然后修改副本：

```kotlin
val backToMutableList = immutableList.toMutableList()

backToMutableList[0] = "World"

// Prints "World"
println(backToMutableList.joinToString())

// Prints "Hello"
println(mutableList.joinToString())
```

默认方法应该足以满足日常使用，**但在某些情况下制作完整副本是不可接受的(例如，当性能是关键时)**，在这种情况下，我们可以尝试转换为不可变类型。这里的问题是，可以允许特别固执的用户修改基础数据。

使用前面显示的mutableList，而不是使用toList，让我们简单地转换它：

```kotlin
// Just casting
val immutableList: List<String> = mutableList
```

同样，我们无法修改它，因为我们转换为的类型不会公开所需的操作：

```kotlin
// Throws an error
immutableList[0] = "World"
```

但这就是顽固用户的用武之地，由于我们只做了一个简单的转换，用户可以将它转换回MutableList：

```kotlin
// Unsafe casting
val backToMutableList = immutableList as MutableList<String>
```

然后修改原来的集合：

```kotlin
backToMutableList[0] = "World"

// Prints "World"
println(backToMutableList.joinToString())
 
// Prints "World"
println(mutableList.joinToString())
```

正如我们所看到的，“just casting”方法会导致严重的误用。但是，如果出于某种原因，我们不想使用内置方法集合，那么我们有什么选择呢？**内置的收集方法不够用怎么办**？

## 3. 当内置函数不够时：委托方法

我们可以做的一件事是**将所需的集合包装成另一种类型**，由于Kotlin的[委托功能](https://www.baeldung.com/kotlin/delegation-pattern)，这很容易实现。首先，定义我们要使用的集合的不可变版本：

```kotlin
class ImmutableList<T>(private val protectedList: List<T>): List<T> by protectedList
```

一旦我们有了这样的包装器，我们就可以用它来纠正我们在前面的例子中遇到的问题。再一次，让我们使用mutableList：

```kotlin
// Prints "Hello"
println(mutableList.joinToString())
```

但不是使用toList或转换，而是将其包装在我们的ImmutableList中：

```kotlin
// Wrap - no copy!
val immutableList = ImmutableList(mutableList)
```

和以前一样，我们无法访问其他方法，并且由于我们的包装，我们无法将immutableList转换为MutableList：

```kotlin
// Error - Immutable List does not have addition methods
immutableList[0] = "World"

// Error - Cannot cast to Mutable list
val backToMutable = immutableList as MutableList<String>
```

这样，我们的问题就解决了：我们可以使用我们的包装器来禁用接口用户将我们的集合转换为MutableList的可能性，而无需Kotlin的toList所做的事情。更好的是，我们根本不必自己实现这样的包装器。已经有人为我们做到了。

### 3.1 Klutter实现

**流行的Kotlin实用程序库**[Klutter](https://github.com/kohesive/klutter)**已经提供了不可变集合的实现**，它使用前面描述的委派方法，但以一种功能更完整的方式。

为了使用这个解决方案，让我们将Klutter作为依赖项添加到我们的项目中：

```xml
<dependency>
    <groupId>uy.kohesive.klutter</groupId>
    <artifactId>klutter-core</artifactId>
    <version>3.0.0</version>
</dependency>
```

然后，我们就可以使用Klutter来获得各种不可变的集合，假设我们有一个Map或一个Set：

```kotlin
val map = mutableMapOf<String, String>()
val set = mutableSetOf<String>()
```

我们可以使用提供的扩展函数来我们的集合并将其包装在委托中：

```kotlin
map.toImmutable()
```

或者只是将集合包装在委托中：

```kotlin
set.asReadOnly()
```

Klutter实现这种方法的方式还有很多，但那可能是另一篇文章。目前，我们可以直接看一下[Klutter的源代码](https://github.com/kohesive/klutter/blob/f93e32817aac31aa7b4b40ee6ed69c885b65e44f/core/src/main/kotlin/uy/klutter/core/collections/Immutable.kt)。

## 4. 总结

在本文中，我们了解了在Kotlin中实现不可变集合的多种方法。