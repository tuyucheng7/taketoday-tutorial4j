## 1. 概述

在本教程中，我们将熟悉几种在Kotlin中获取当前正在执行的函数名称的方法。

如果我们以Java 9为目标，第一种方法是最惯用的方法。除此之外，我们还将探索一些肯定有效但也有缺点的后备方法。

## 2. Stack-Walking API

**Java 9引入了**[Stack-Walking](https://www.baeldung.com/java-9-stackwalking-api) **API以延迟浏览当前线程的堆栈帧**，此API提供了一种更好的遍历堆栈帧的方法，因为它不会急切地捕获所有帧，这会产生很高的成本。此外，它不会分配额外的不必要的对象或定义新的类，这与替代方法不同。

每个堆栈中的顶部帧表示最后一个函数调用，因此，我们可以使用这个API并从顶部堆栈帧中获取函数名称：

```kotlin
fun functionNameWithStackWalker(): String? {
    return StackWalker.getInstance().walk { frames ->
        frames.findFirst().map { it.methodName }.orElse(null)
    }
}
```

[walk()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/StackWalker.html#walk(java.util.function.Function))方法接收一个[Function](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/function/Function.html)，该函数接收一个[StackFrame](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/StackWalker.StackFrame.html)的[Stream](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/util/stream/Stream.html)作为输入，这样，我们就可以以惰性的方式一帧一帧地遍历所有帧，并从每一帧中提取所需的信息。

在上面的函数中，我们只需要第一帧或顶帧，所以我们调用了findFirst()方法。此外，**每个StackFrame都封装了特定帧的详细信息**，在这种情况下，我们使用methodName属性(Java中的[getMethodName()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/StackWalker.StackFrame.html#getMethodName()))来查找当前函数名称。

让我们确保此功能按预期工作：

```kotlin
val name = functionNameWithStackWalker()
assertEquals("functionNameWithStackWalker", name)
```

## 3. 匿名内部类

我们可以在函数定义中定义一个[匿名内部类](https://www.baeldung.com/java-anonymous-classes)，**由于Java中的**[Class](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/Class.html) **API允许访问封闭成员的详细信息**，我们还可以通过定义一个匿名内部类来获取当前函数名：

```kotlin
fun functionNameWithAnonymousInnerClass(): String {
    return object {}.javaClass.enclosingMethod.name
}
```

与Kotlin的object {}声明等效的Java是：

```java
new Object() {};
```

所以，我们在函数内部定义了一个匿名内部类，因此，可以使用enclosingMethod属性(Java中的[getEnclosingMethod()方法](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/Class.html#getEnclosingMethod()))获取封闭函数名称：

```kotlin
val name = functionNameWithAnonymousInnerClass()
assertEquals("functionNameWithAnonymousInnerClass", name)
```

**由于我们正在创建一个完全不必要的对象，因此这种方法的效率和惯用性都稍差一些**。所以，只有当我们的目标JVM版本低于9时，我们才应该使用这个。

## 4. 捕获堆栈跟踪

**Thread类的**[getStackTrace()](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/Thread.html#getStackTrace())**方法返回一个堆栈跟踪元素数组，表示线程的堆栈转储**，我们可以结合使用此方法和[Thread.currentThread()](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/Thread.html#currentThread())方法来捕获当前线程的所有堆栈跟踪元素：

```kotlin
fun functionNameWithStackTraces(): String {
    return Thread.currentThread().stackTrace[1].methodName
}
```

使用Thread.currentThread().getStackTrace()时，getStackTrace()方法将位于堆栈顶部，因此，第二个数组元素将代表封闭函数。

**由于getStackTrace()方法急切地捕获大量堆栈跟踪元素，因此这是效率最低的方法**，所以我们应该尽可能避免使用这个。

## 5. 总结

在这个简短的教程中，我们看到了几种不同的方法来获取当前正在执行的函数的名称。