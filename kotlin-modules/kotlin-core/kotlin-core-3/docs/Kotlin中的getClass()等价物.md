## 1. 概述

在Java中，当我们要[检索Class<T\>](https://www.baeldung.com/java-reflection)类型的标记时，我们将在对象上使用[getClass()](https://docs.oracle.com/javase/tutorial/reflect/class/classNew.html)方法。在这个简短的教程中，我们将了解如何在Kotlin中执行此操作。

## 2. getClass()等价物

从Kotlin 1.1开始，我们可以使用[类引用](https://kotlinlang.org/docs/reflection.html#class-references)语法在Kotlin中检索KClass<T\>令牌：

```kotlin
val aString = "42"
val stringType = String::class
assertEquals(stringType, aString::class)
```

如上所示，“::”引用可用于类类型和实例。在Kotlin 1.1之前，我们应该使用[javaClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/java-class.html)扩展属性：

```kotlin
val aString = "42"
val type = aString.javaClass.kotlin
assertEquals("String", type.simpleName)
```

## 3. 总结

在本教程中，我们学习了如何在Kotlin中检索KClass<T|>令牌。