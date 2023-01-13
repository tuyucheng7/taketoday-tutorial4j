## 1. 概述

[Lambda 表达式](https://www.baeldung.com/kotlin-lambda-expressions)使我们能够在 Kotlin 中以更简洁优雅的方式表达行为。

在本教程中，我们将了解 Kotlin lambda 表达式如何使用 SAM 转换与 Java 函数式接口互操作。在此过程中，我们将深入挖掘以了解这种互操作性在字节码级别的内部表示。

## 2.功能接口

在 Java 中，lambda 表达式是根据[功能接口](https://www.baeldung.com/java-8-functional-interfaces)来实现的。函数式接口只有一个抽象方法可以实现。

另一方面，对于 Kotlin，我们在编译时拥有适当的函数类型。例如 (String) -> Int 是一个接受 String 作为输入并返回 Int 作为输出的函数。

尽管它们的实现存在差异，但Kotlin lambda 与 Java 中的函数接口完全可互操作。

为了使事情更具体，让我们考虑 java.lang.Thread 类。此类具有一个构造函数，该构造函数将 Runnable 接口实例作为输入：

```java
public Thread(Runnable target) {
    // omitted
}
```

在 Java 8 之前，我们必须从 Runnable中创建一个[匿名内部类](https://www.baeldung.com/java-anonymous-classes) 来创建 Thread 实例：

```java
Thread thread = new Thread(new Runnable() {
    @Override
    public void run() {
        // the logic
    }
});
```

这里我们同时创建了一个子类和一个实例。

在 Kotlin 中，类似地，我们可以使用[对象表达式](https://www.baeldung.com/kotlin/anonymous-inner-classes)来实现同样的事情：

```kotlin
val thread = Thread(object : Runnable {
    override fun run() {
        // the logic
    }
})
```

但是，这感觉不太对，远谈不上优雅和简洁。

好消息是因为 Kotlin 中的 lambda 表达式与 Java 中的函数式接口的互操作性很强，我们可以将 lambda 传递给 Thread 构造函数：

```kotlin
val thread = Thread({
    // the logic
})
```

这显然是一个更令人愉快的 API。由于最后一个参数是 lambda，我们可以省略括号并将 lambda 块移出它：

```kotlin
val thread = Thread {
    // the logic
}
```

简而言之，即使 Java API 期望我们提供函数式接口，我们仍然能够将相应的 lambda 传递给它。在幕后，Kotlin 编译器会将 lambda 转换为函数式接口。

现在我们知道了这种可能性，让我们深入挖掘一下，看看事情是如何在幕后运作的。

## 3. SAM 转换

lambda 到函数式接口的转换之所以有效，是因为函数式接口只有一个抽象方法。此类接口称为单一抽象方法或 SAM 接口。此外，这种自动转换也称为 SAM 转换。

然而，在幕后，Kotlin 编译器仍然为 SAM 转换创建一个匿名内部类。例如，让我们考虑同一个Thread示例：

```kotlin
val thread = Thread {
    // the logic
}
```

如果我们使用 kotlinc编译代码：

```bash
$ kotlinc SamConversions.kt
```

然后我们可以使用javap检查字节码 ：

```bash
$ javap -v -p -c SamConversionsKt
// truncated
0: new           #11       // class java/lang/Thread
3: dup
4: getstatic     #17       // Field SamConversionsKt$main$thread$1.INSTANCE:LSamConversionsKt$main$thread$1;
7: checkcast     #19       // class java/lang/Runnable
10: invokespecial #23      // Method java/lang/Thread."<init>":(Ljava/lang/Runnable;)V
13: astore_0
// truncated
InnerClasses:
  static final #13;        // class SamConversionsKt$main$thread$1
```

这是 Kotlin 编译器为我们生成的内容：

1.  它定义了一个匿名内部类 ——SamConversionsKt$main$thread$1 部分
2.  它获取该类的单例实例 ——SamConversionsKt$main$thread$1.INSTANCE 部分
3.  然后，它确保它是一个Runnable 实例，然后
4.  它将该实例传递给Thread 构造函数

### 3.1. 对象表达式

正如我们之前提到的，我们可以使用对象表达式来实现同样的事情：

```kotlin
Thread(object : Runnable {
    override fun run() {
        // the logic
    }
})
```

但是，正如下面的字节码所示，每次我们创建 Thread 实例时，都会创建一个匿名内部类：

```bash
0: new           #11      // class java/lang/Thread
3: dup
4: new           #13      // class SamConversionsKt$main$thread$1
7: dup
8: invokespecial #16      // Method SamConversionsKt$main$thread$1."<init>":()V
11: checkcast     #18     // class java/lang/Runnable
14: invokespecial #21     // Method java/lang/Thread."<init>":(Ljava/lang/Runnable;)V
17: astore_0

```

在索引 8 处，JVM 正在创建匿名内部类的实例并调用其[实例初始化方法](https://www.baeldung.com/jvm-init-clinit-methods)，即它的构造函数。

另一方面，lambda 将创建一个单例实例并在我们每次需要时重用该实例：

```bash
4: getstatic #17 // Field SamConversionsKt$main$thread$1.INSTANCE:LSamConversionsKt$main$thread$1;
```

因此，除了更简洁之外，lambda 表达式和 SAM 转换将创建更少的对象，因此将表现出更好的内存占用。

### 3.2. 闭包

但是，如果我们从外部范围捕获变量，这种性能提升可能会很脆弱：

```kotlin
var answer = 42
val thread = Thread {
    println(answer)
}
```

在这种情况下，我们看到一个重要的变化：

```bash
23: invokespecial #25   // Method SamConversionsKt$main$thread$1."<init>":(Lkotlin/jvm/internal/Ref$IntRef;)V
26: checkcast     #27   // class java/lang/Runnable

```

如上所示，匿名内部类现在在其构造函数中接受一个参数。事实上，Kotlin 编译器会将捕获的变量包装在一个 [IntRef](https://github.com/JetBrains/kotlin/blob/deb416484c5128a6f4bc76c39a3d9878b38cec8c/libraries/stdlib/jvm/runtime/kotlin/jvm/internal/Ref.java#L40) 实例中，并将其传递给内部类构造函数。

因此，如果我们在 lambda 中捕获一个变量，JVM 将在每次调用时创建一个实例。所以我们将失去性能增益。为了避免这种情况，我们可以在 Kotlin中使用[内联函数。](https://www.baeldung.com/kotlin-inline-functions)

## 4. SAM 构造器

通常，编译器会自动将 lambda 表达式转换为相应的函数式接口。但是，有时我们需要手动执行此转换。

例如，ExecutorService 接口提供了submit()的两个重载版本：

```java
Future<T> submit(Callable task);
Future<?> submit(Runnable task);
```

Callable 和 Runnable 都是 函数式接口。所以如果我们写：

```kotlin
val result = executor.submit {
    return@submit 42
}
```

然后 Kotlin 编译器无法推断我们使用的是哪个重载版本。为了解决这种困惑，我们可以使用 SAM 构造函数：

```kotlin
val submit = executor.submit(Callable {
    return@Callable 42
})
```

如上所示，SAM 构造函数的名称与底层功能接口的名称相同。此外，构造函数本身是一个特殊的编译器生成函数，可以让我们将 lambda 表达式显式转换为函数式接口的实例。

此外，SAM 构造函数在返回功能接口时也很有用：

```kotlin
fun doSomething(): Runnable = Runnable {
    // doing something
}
```

或者甚至将 lambda 存储到变量中：

```kotlin
val runnable = Runnable { 
    // doing something
}
```

请注意，SAM 转换仅适用于功能接口而不适用于抽象类，即使这些抽象类只有一个抽象方法。

### 4.1. Kotlin 接口的 SAM 转换

[从 Kotlin 1.4 开始](https://kotlinlang.org/docs/reference/whatsnew14.html#sam-conversions-for-kotlin-interfaces)，我们也可以对 Kotlin 接口使用 SAM 转换。我们所要做的就是用 fun 修饰符标记一个 Kotlin 接口：

```kotlin
fun interface Predicate<T> {
    fun accept(element: T): Boolean
}
```

现在我们可以将 SAM 转换应用于此接口的实例：

```kotlin
val isAnswer = Predicate<Int> { i -> i == 42 }
```

请注意，当我们将 fun 修饰符应用于 Kotlin 接口时，我们应该确保该接口只包含一个抽象方法。否则，代码将无法编译：

```kotlin
fun interface NotSam {
    // no abstract methods
}
```

上面的代码将在编译时失败并显示错误消息：

```plaintext
Fun interfaces must have exactly one abstract method
```

## 5.总结

在本文中，我们研究了 SAM 接口。我们还看到了 Kotlin 如何将 lambda 表达式转换为 Java 中的函数式接口。这使我们能够在需要功能接口的任何地方传递 lambda 表达式。

最后，我们了解到有时我们必须告诉编译器通过 SAM 构造函数显式地进行此转换。