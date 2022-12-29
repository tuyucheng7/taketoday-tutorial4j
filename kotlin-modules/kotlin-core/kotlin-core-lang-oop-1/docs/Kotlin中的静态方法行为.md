## 1. 概述

[Kotlin语言](https://www.baeldung.com/kotlin)与Java的不同之处之一在于Kotlin不包含我们熟悉的[static关键字](https://www.baeldung.com/kotlin-static)。

在本快速教程中，我们将看到几种在Kotlin中实现Java静态方法行为的方法。

## 2. 包级函数

让我们从创建一个LoggingUtils.kt文件开始，在该文件中，我们将创建一个名为debug的非常简单的方法。因为我们不太关心我们方法中的功能，因此我们只打印一条简单的消息。

由于我们在类之外定义我们的方法，所以它代表一个包级函数：

```kotlin
fun debug(debugMessage: String) {
    println("[DEBUG] $debugMessage")
}
```

我们还将在反编译代码中看到，我们的debug方法现在被声明为static：

```java
public final class LoggingUtilsKt {
    public static final void debug(@NotNull String debugMessage) {
        Intrinsics.checkParameterIsNotNull(debugMessage, "debugMessage");
        String var1 = "[DEBUG] " + debugMessage;
        System.out.println(var1);
    }
}
```

## 3. 伴生对象

Kotlin允许我们创建一个类的所有实例共有的对象-伴生对象，我们可以通过添加关键字companion来创建对象的单例实例。

让我们在伴生对象中定义我们的debug方法：

```kotlin
class ConsoleUtils {
    companion object {
        fun debug(debugMessage: String) {
            println("[DEBUG] $debugMessage")
        }
    }
}
```

我们的反编译代码向我们展示了我们可以通过Companion对象访问debug方法：

```java
public final class ConsoleUtils {
    public static final ConsoleUtils.Companion Companion
          = new ConsoleUtils.Companion((DefaultConstructorMarker) null);

    public static final class Companion {
        public final void debug(@NotNull String debugMessage) {
            Intrinsics.checkParameterIsNotNull(debugMessage, "debugMessage");
            String var2 = "[DEBUG] " + debugMessage;
            System.out.println(var2);
        }

        private Companion() {}

        public Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}
```

为了避免通过通用名称Companion调用生成的实例，我们还可以提供一个自定义名称。

最后，要再次使debug方法静态化，我们应该使用@JvmStatic注解：

```kotlin
class ConsoleUtils {
    companion object {
        @JvmStatic
        fun debug(debugMessage : String) {
            println("[DEBUG] $debugMessage")
        }
    }
}
```

通过使用它，我们最终会在反编译代码中得到一个实际的static final void的debug方法：

```java
public final class ConsoleUtils {
    public static final ConsoleUtils.Companion Companion
          = new ConsoleUtils.Companion((DefaultConstructorMarker) null);

    @JvmStatic
    public static final void debug(@NotNull String debugMessage) {
        Companion.debug(debugMessage);
    }

    public static final class Companion {
        @JvmStatic
        public final void debug(@NotNull String debugMessage) {
            Intrinsics.checkParameterIsNotNull(debugMessage, "debugMessage");
            String var2 = "[DEBUG] " + debugMessage;
            System.out.println(var2);
        }

        private Companion() {}

        public Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}
```

现在，我们将能够通过ConsoleUtils类直接访问这个新方法。

## 4. 总结

在这个简短的教程中，我们了解了如何在Kotlin中复制Java静态方法的行为，我们使用了包级函数和伴生对象。