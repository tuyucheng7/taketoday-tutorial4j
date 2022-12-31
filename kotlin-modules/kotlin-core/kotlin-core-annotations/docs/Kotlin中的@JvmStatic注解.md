## 1. 概述

在本文中，我们将了解@JvmStatic注解如何影响生成的字节码。此外，我们将熟悉此注解的用例。

在整篇文章中，我们将相当广泛地查看字节码，以了解在不同情况下发生了什么。

## 2. @JvmStatic注解

为了演示，我们将在整篇文章中使用一个非常简单的Kotlin文件，因此，让我们创建一个名为Math.kt的文件：

```kotlin
class Math {
    companion object {
        fun abs(x: Int) = if (x < 0) -x else x
    }
}

fun main() {
    println(Math.abs(-2))
}
```

该文件包含一个带有[伴生对象](https://www.baeldung.com/kotlin/objects#what-is-a-companion-object)和main()函数的类，主函数调用abs()函数，该函数计算任何给定数字的绝对值。当然，这不是abs的最佳实现，但绝对可以作为一个很好的例子。

### 2.1 无注解

**对于我们中的许多人来说，Kotlin中的伴生对象是一种实现类静态行为的工具**。因此，很自然地，我们可能期望Kotlin编译器会将abs()函数编译为底层的静态方法。

为了验证这个假设，首先，让我们编译 Math.kt文件：

```bash
>> kotlinc Math.kt
```

编译后会有三个class文件：

```bash
>> ls *.class
Math$Companion.class
Math.class
MathKt.class
```

如上所示，一个类文件用于main函数，一个用于Math类，一个用于伴生对象。

现在，让我们看看使用[javap](https://www.baeldung.com/java-class-view-bytecode) 工具生成的JVM字节码：

```bash
>> javap -c -p Math
public final class Math {
  public static final Math$Companion Companion;

  public Math();
    Code:
       0: aload_0
       1: invokespecial #8             // Method java/lang/Object."<init>":()V
       4: return

  static {};
    Code:
       0: new           #13            // class Math$Companion
       3: dup
       4: aconst_null
       5: invokespecial #16            // Method Math$Companion."<init>":(LDefaultConstructorMarker;)V
       8: putstatic     #20            // Field Companion:LMath$Companion;
      11: return
}

>> javap -c -p -v Math
// omitted
InnerClasses:
  public static final #17= #13 of #2;     // Companion=class Math$Companion of class Math
```

如字节码所示，**Kotlin将伴生对象编译为静态内部类**。此外，它定义了一个静态最终字段来保存封闭类(在本例中为Math类)内的内部类的实例：

```bash
public static final Math$Companion Companion;
```

为了初始化这个静态字段，它使用了一个静态初始化块：

```bash
static {};
    Code:
       0: new           #13            // class Math$Companion
       3: dup
       4: aconst_null
       5: invokespecial #16            // Method Math$Companion."<init>":(LDefaultConstructorMarker;)V
       8: putstatic     #20            // Field Companion:LMath$Companion;
      11: return
```

如上所示，它首先创建伴生对象的新实例(索引0)，然后调用其构造函数(索引5)，最后，将新对象存储在该静态字段(索引8)中。

现在，让我们检查伴生对象的字节码：

```bash
>> javap -c -p Math.Companion
public final class Math$Companion {
  // omitted
  public final int abs(int);
    Code:
       0: iload_1
       1: ifge          9
       4: iload_1
       5: ineg
       6: goto          10
       9: iload_1
      10: ireturn
}
```

正如我们在这里看到的，**Kotlin将abs()函数编译为一个简单的实例方法，而不是一个静态方法**。所以，每次我们从另一个Kotlin函数调用这个函数时，都将是一个简单的实例方法调用：

```bash
>> javap -c -p MathKt // main function
public final class MathKt {
  public static final void main();
    Code:
       0: getstatic     #12                 // Field Math.Companion:LMath$Companion;
       3: bipush        -2
       5: invokevirtual #18                 // Method Math$Companion.abs:(I)I
       // omitted
}
```

在这里，我们可以看到从Kotlin调用Math.abs()方法转换为对Math.Companion.abs()的简单(而非静态)方法调用，**如果我们要从Java调用这个函数，我们只能使用Math.Companion.abs()方法**：

```kotlin
Math.abs(-2) // won't compile
Math.Companion.abs(-2) // works
```

第一个甚至不会编译，因为Math类中没有名为abs()的静态方法，我们可以从Java访问此功能的唯一方法是通过我们之前看到的Companion static final字段。

### 2.2 带注解

**当我们将@JvmStatic注解放在abs()函数上时，Kotlin也会额外生成一个静态方法**。例如，这是我们使用注解的时候：

```kotlin
class Math {
    companion object {
        @JvmStatic
        fun abs(x: Int) = if (x < 0) -x else x
    }
}
```

现在，如果我们编译上面的文件，我们可以看到Kotlin实际上为abs()函数生成了一个附加静态方法：

```bash
>> javap -c -p Math
public final class Math {
  public static final Math$Companion Companion;

  public static final int abs(int);
    Code:
       0: getstatic     #17            // Field Companion:LMath$Companion;
       3: iload_0
       4: invokevirtual #21            // Method Math$Companion.abs:(I)I
       7: ireturn
  // same as before
}
```

有趣的是，**这个静态方法委托给了Companion.abs()方法**。请注意，除了我们在上一节中看到的所有其他方法和类之外，还将生成此静态方法。

**由于现在有一个静态方法，我们可以通过两种方式从Java调用这个函数**：

```java
Math.abs(-2)
Math.Companion.abs(-2)
```

但是，**Kotlin将继续调用实例方法而不是静态方法**：

```bash
>> javap -c -p MathKt
public final class MathKt {
  public static final void main();
    Code:
       0: getstatic     #12                 // Field Math.Companion:LMath$Companion;
       3: bipush        -2
       5: invokevirtual #18                 // Method Math$Companion.abs:(I)I
       // omitted
}
```

让我们看一下为这个Kotlin片段生成的部分字节码：

```kotlin
fun main() {
    println(Math.abs(-2))
}
```

由此，**我们可以理解这个注解是为了Java互操作性而存在的，对于纯Kotlin代码库来说不是必需的**。

此外，@JvmStatic注解还可以应用于对象或伴生对象的属性。这样，相应的getter和setter方法将是该对象或包含伴生对象的类中的静态成员。

简单回顾一下，当我们使用@JvmStatic时，Kotlin会生成对应于以下Java代码的字节码：

```java
public class Math {
    public static final Companion Companion = new Companion();
    
    // only if we use @JvmStatic
    public static int abs(int x) {
        return Companion.abs(x); // referring to the static field above
    }
    
    public static class Companion {
        public int abs(int x) {
            if (x < 0) return -x;
            return x;
        }
   
        private Companion() {} // private constructor
    }
}
```

如果我们省略@JvmStatic注解，除了静态方法之外，字节码将是相同的。

## 3. @JvmStatic的用例

**@JvmStatic注解最重要的用例是Java互操作性**，更具体地说，使用静态方法可以更轻松地与一些Java优先的框架集成。例如，JUnit 5要求方法源是静态的：

```kotlin
@ParameterizedTest
@MethodSource("sumProvider")
fun `sum should work as expected`(a: Int, b: Int, expected: Int) {
    assertThat(a + b).isEqualTo(expected)
}

companion object {
    @JvmStatic
    fun sumProvider() = Stream.of(
        Arguments.of(1, 2, 3),
        Arguments.of(5, 10, 15)
    )
}
```

除此之外，调用@JvmStatic方法在Java世界中更容易和更惯用。

## 4. 总结

在本文中，我们了解了@JvmStatic注解如何影响生成的JVM字节码。简而言之，这个注解告诉Kotlin编译器在后台为带注解的函数生成一个额外的静态方法。

此外，此注解最重要的用例当然是更好的Java互操作性。通过这种方法，可以更容易地与一些Java框架(如JUnit)集成，从Java调用此类方法也更容易和更惯用。