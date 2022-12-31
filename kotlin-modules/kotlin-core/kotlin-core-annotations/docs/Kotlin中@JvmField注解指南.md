## 1. 概述

在本教程中，我们将探索[Kotlin](https://kotlinlang.org/)中的@JvmField注解。

Kotlin有其处理类和属性的方法，这与Java中使用的方法不同，@JvmField注解使得两种语言之间的兼容性成为可能。

## 2. 字段声明

**默认情况下，Kotlin类不公开字段，而是公开属性**。

该语言自动为属性提供支持字段，这些属性将以字段的形式存储它们的值：

```kotlin
class CompanionSample {
    var quantity = 0
    set(value) {
        if(value >= 0) field = value
    }
}
```

这是一个简单的示例，但通过在IntelliJ中使用Kotlin的反编译器(Tools > Kotlin > Show Kotlin Decompiler)，它将向我们展示它在Java中的样子：

```java
public class JvmSample {
   private int quantity;

   // custom getter

   public final void setQuantity(int value) {
      if (value >= 0) {
         this.quantity = value;
      }
   }
}
```

然而，这并不意味着我们根本不能有字段，在某些情况下，这是必要的。在这种情况下，我们可以利用@JvmField注解，它指示编译器不为该属性生成getter和setter并将其作为简单的Java字段公开。

让我们看一下Kotlin示例：

```kotlin
class KotlinJvmSample {
    @JvmField
    val example = "Hello!"
}
```

以及它的Java反编译副本，这确实证明了该字段是以标准Java方式公开的：

```java
public class KotlinJvmSample {
    @NotNull
    public final String example = "Hello!";
}
```

## 3. 静态变量

注解派上用场的另一个实例是在名称对象或伴生对象中声明的属性具有静态支持字段：

```java
public class Sample {
    public static final int MAX_LIMIT = 20;
}
```

```kotlin
class Sample {
    companion object {
        @JvmField val MAX_LIMIT = 20
    }
}
```

## 4. 使用异常

到目前为止，我们已经讨论了可以使用注解的情况，但是有一些限制。

以下是一些我们不能使用注解的情况：

-   私有属性
-   带有open、override、const修饰符的属性
-   委托属性

## 5. 总结

在这篇简短的文章中，我们探讨了使用Kotlin的@JvmField注解的不同方式。