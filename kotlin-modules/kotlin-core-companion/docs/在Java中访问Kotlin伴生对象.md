## 1. 概述

在本教程中，我们将学习Kotlin的[伴生对象](https://www.baeldung.com/kotlin/objects#what-is-a-companion-object)以及如何使用一些简单示例从Java访问它们，对于每个示例，我们还将查看反编译的Kotlin字节码(编译器生成的等效Java代码)。如果使用IntelliJ，我们可以从**Tools → Kotlin → Show Kotlin Bytecode → Decompile**访问生成的代码，初读本文时跳过反编译代码也无妨。

## 2. 声明伴生对象

**在Kotlin中，我们可以使用对象声明轻松创建线程安全的单例**，如果我们在类中声明该对象，我们可以选择将其标记为伴生对象。在Java中，伴生对象的成员可以作为类的静态成员来访问，**将对象标记为伴生对象允许我们在调用其成员时省略对象的名称**。

让我们看一个简单的例子，它展示了如何在Kotlin中声明伴生对象：

```kotlin
class MyClass {
    companion object {
        val age: Int = 22
    }
}
```

在上面的示例中，我们声明了一个伴生对象，请注意，我们省略了它的名称。

## 3. 从Java访问伴生对象的属性

由于Kotlin与Java是100%可互操作的，因此还有一项规定可以将伴生对象的属性作为Java类的静态字段进行访问。

### 3.1 @JvmField注解

**我们可以使用@JvmField注解在Kotlin类中标记一个属性，让Kotlin编译器知道我们想要将其公开为静态字段**：

```kotlin
class FieldSample {
    companion object{
        @JvmField
        val age : Int = 22
    }
}
```

现在，让我们**从Java类访问它**：

```java
public class Main {
    public static void main(String[] args) {
        System.out.println(FieldSample.age);
    }
} 
```

在上面的示例中，我们使用@JvmField注解标记了age属性，然后，我们可以直接在我们的Java类中访问它。请注意，如果我们只观察Java代码，那么就好像我们正在访问FieldSample的静态字段一样。

让我们看看FieldSample类的反编译Kotlin字节码：

```java
public final class FieldSample {
    @JvmField
    public static int age = 22;
    @NotNull
    public static final FieldSample.Companion Companion = new FieldSample.Companion((DefaultConstructorMarker)null);

    public static final class Companion {
        private Companion() {
        }
        // ...
    }
}
```

从上面的代码片段我们可以看出，**Kotlin编译器为标有@JvmField注解的属性生成了一个静态字段**。

### 3.2 特例-lateinit修饰符

定义为lateinit的变量是特殊的，因为当定义为伴生对象的一部分时，将为它们中的每一个创建一个静态支持字段，此静态支持字段与lateinit变量具有相同的可见性。所以，如果我们定义了一个公共的lateinit变量，我们就可以在我们的Java代码中访问它。

让我们快速编写一个声明私有和公共lateinit变量的Kotlin类：

```kotlin
class LateInitSample {
    companion object {
        private lateinit var password: String
        lateinit var userName: String

        fun setData(pair: Pair<String, String>) {
            password = pair.first
            userName = pair.second
        }
    }
}
```

如以下Java代码片段所示，我们只能访问声明为public的lateinit变量：

```java
public class Main {
    static void callLateInit() {
        System.out.println(LateInitSample.userName);
        // System.out.println(LateInitSample.password); compilation error
    }
}
```

让我们看一下LateInitSample类的反编译Kotlin字节码的摘录：

```java
public final class LateInitSample {
    private static String password;
    public static String userName;
    @NotNull
    public static final LateInitSample.Companion Companion = new LateInitSample.Companion((DefaultConstructorMarker)null);

    public static final class Companion {
        @NotNull
        public final String getUserName() {
            String var10000 = LateInitSample.userName;
            if (var10000 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("userName");
            }

            return var10000;
        }

        public final void setUserName(@NotNull String var1) {
            Intrinsics.checkNotNullParameter(var1, "<set-?>");
            LateInitSample.userName = var1;
        }

        public final void setData(@NotNull Pair pair) {
            Intrinsics.checkNotNullParameter(pair, "pair");
            LateInitSample.password = (String)pair.getFirst();
            ((LateInitSample.Companion)this).setUserName((String)pair.getSecond());
        }

        private Companion() {
        }
    }
}
```

从上面生成的代码中，我们可以看到**编译器为lateinit变量生成了静态字段**，可见性与实际字段的可见性相同。

### 3.3 特例-const修饰符

**我们可以通过应用const关键字在Kotlin中定义一个编译时常量**，如果我们在Java中定义了一个常量，我们就会知道所有这些字段都是静态最终的。**Kotlin的const相当于Java static final字段**，让我们看一下在伴生对象中定义const变量的示例代码以及如何在Java中访问它：

```kotlin
class ConstSample {
    companion object {
        const val VERSION: Int = 100
    }
}
```

```java
public class Main {
    static void callConst() {
        System.out.println(ConstSample.VERSION);
    }
}
```

让我们看看ConstSample类的反编译Kotlin字节码：

```java
public final class ConstSample {
    public static final int VERSION = 100;
    @NotNull
    public static final ConstSample.Companion Companion = new ConstSample.Companion((DefaultConstructorMarker)null);


    public static final class Companion {
        private Companion() {
        }

        // $FF: synthetic method
        public Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}
```

如我们所见，编译器在内部将VERSION字段转换为Java中的静态最终字段。

## 4. 访问伴生对象的方法

**要从Java访问伴生对象的方法，我们需要使用@JvmStatic注解来标记这些方法**。让我们看一个例子：

```kotlin
class MethodSample {
    companion object {
        @JvmStatic
        fun increment(num: Int): Int {
            return num + 1
        }
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        MethodSample.increment(1);
    }
}
```

这个注解告诉Kotlin编译器在封闭类中生成一个静态方法，让我们看一下反编译的Kotlin字节码的摘录：

```java
public final class MethodSample {
    @NotNull
    public static final MethodSample.Companion Companion = new MethodSample.Companion((DefaultConstructorMarker)null);

    @JvmStatic
    public static final int increment(int num) {
        return Companion.increment(num);
    }


    public static final class Companion {
        @JvmStatic
        public final int increment(int num) {
            return num + 1;
        }

        private Companion() {
        }

        // $FF: synthetic method
        public Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}
```

在上面的代码片段中，我们可以看到，**在内部，Kotlin编译器正在从封闭类生成的静态increment方法中调用Companion.increment()方法**。

## 5. 总结

在本文中，我们了解了如何使用Java中的Kotlin伴生对象的成员，我们还看到了生成的字节码，以更好地理解这些特性。