## 1. 概述

Kotlin是一种[空安全编程语言](https://www.baeldung.com/kotlin-null-safety)，从某种意义上说，它区分可为null和不可为null的数据类型。

在这个快速教程中，我们将了解在Kotlin中将空值存储到非空数据类型时会发生什么。

## 2. 在非可空类型中存储空值

Kotlin编译器将在编译时强制执行数据类型的空安全，因此如果我们写这样的东西：

```kotlin
val name: String = null
```

然后，编译器将在编译时抱怨错误：

```shell
Kotlin: Null can not be a value of a non-null type String
```

**尽管Kotlin编译器在编译时强制执行null安全，但有时我们可能还需要在运行时采取另一种安全措施**。简而言之，有时将null值存储到不可为null的变量中在编译时并不明显。因此，在这些情况下，编译器需要运行时的帮助。

为了使事情更具体，让我们考虑一个简单的数据模型类：

```kotlin
data class User(val id: Long, val username: String)
```

如果我们尝试通过反射将username字段设置为null：

```kotlin
val constructor = User::class.java.constructors[0]
val createInstance = { constructor.newInstance(42L, null) }
```

然后Kotlin将抛出异常，因为username字段设置为null：

```kotlin
val exception = assertThrows { createInstance() }
assertThat(exception.cause).isInstanceOf(NullPointerException::class.java)
assertThat(exception.cause?.message).startsWith("Parameter specified as non-null is null")
```

如果我们不断言异常详细信息，运行时将打印以下堆栈跟踪：

```shell
java.lang.NullPointerException: Parameter specified as non-null is null: method User.<init>, parameter username
// truncated
```

堆栈跟踪很明显：来自User类构造函数([<init\>](https://www.baeldung.com/jvm-init-clinit-methods)是构造函数)中名为username的非空属性设置为null，然而，在Kotlin 1.4之前，同样的错误表现为IllegalArgumentException。

最重要的是，**当我们在运行时将非null属性设置为null时，Kotlin会使用这种特定的消息模式抛出此异常**。

## 3. 总结

在这个简短的教程中，我们了解了Kotlin如何在编译时和运行时强制执行空安全。