## 1. 概述

内联是许多编译器用来优化代码性能的最古老的技巧之一，因此，自然地，为了展示更好的性能和更小的占用空间，Kotlin也利用了这一技巧。

在本教程中，我们将讨论Kotlin中lambda函数内联的两个结果：noinline和crossinline。

## 2. 快速在线复习

**Kotlin中的**[内联函数](https://www.baeldung.com/kotlin/inline-functions)**帮助我们避免为每个lambda表达式分配额外的内存和不必要的方法调用**。例如，在这个简单的例子中：

```kotlin
inline fun execute(action: () -> Unit) {
    action()
}
```

Kotlin将在调用站点中内联对execute()函数和lambda函数的调用，所以，如果我们从另一个函数调用这个函数：

```kotlin
fun main() {
    execute {
        print("Hello ")
        print("World")
    }
}
```

那么内联结果将类似于：

```kotlin
fun main() {
    print("Hello ")
    print("World")
}
```

如上所示，没有execute()方法调用的迹象，当然也没有lambda的迹象。因此，内联将提高我们应用程序的性能，同时使用更少的分配。

## 3. noinline效应

默认情况下，**inline关键字将指示编译器在调用站点上内联方法调用和所有传递的lambda函数**：

```kotlin
inline fun executeAll(action1: () -> Unit, action2: () -> Unit) {
    // omitted
}
```

在上面的示例中，Kotlin将内联executeAll()方法调用以及两个lambda函数(action1和action2)。

**有时，我们可能出于某种原因希望将某些传递的lambda函数从内联中排除**，在这种情况下，我们可以使用noinline修饰符将标记的lambda函数从内联中排除：

```kotlin
inline fun executeAll(action1: () -> Unit, noinline action2: () -> Unit) {
    action1()
    action2()
}
```

在上面的例子中，Kotlin仍然会内联executeAll()方法调用和action1 lambda；但是，由于noinline修饰符，它不会对action2 lambda函数执行相同的操作。

基本上，我们可以预期Kotlin将编译以下代码：

```kotlin
fun main() {
    executeAll({ print("Hello") }, { print(" World") })
}
```

进入类似的东西：

```kotlin
fun main() {
    print("Hello")
    val action2 = { print(" World") }
    action2()
}
```

如上所示，没有调用executeAll()方法的迹象。此外，第一个lambda函数显然是内联的，但是，第二个lambda函数按原样存在，没有内联。

### 3.1 字节码表示

早些时候，我们说过Kotlin编译main函数中的内联函数调用如下：

```kotlin
fun main() { 
    print("Hello") 
    val action2 = { print(" World") } 
    action2() 
}
```

**尽管这种心智模型可以帮助我们更好地理解细节，但Kotlin不会从原始代码生成另一个Kotlin或Java代码**，要查看实际细节，我们应该查看Kotlin在这种情况下如何生成字节码。

为此，让我们编译Kotlin代码并使用[javap](https://www.baeldung.com/java-class-view-bytecode)查看字节码：

```bash
>> kotlinc Inlines.kt
>> javap -c -p cn.tuyucheng.taketoday.crossinline.InlinesKt
// omitted
public static final void main();
    Code:
       0: getstatic     #41       // Field com/baeldung/crossinline/InlinesKt$main$2.INSTANCE:LInlinesKt$main$2;
       3: checkcast     #18       // class kotlin/jvm/functions/Function0
       6: astore_0.               // storing the lambda
       7: iconst_0
       8: istore_1
       9: iconst_0
      10: istore_2
      11: ldc           #43       // String Hello
      13: astore_3
      14: iconst_0
      15: istore        4
      17: getstatic     #49       // Field java/lang/System.out:Ljava/io/PrintStream;
      20: aload_3
      21: invokevirtual #55       // Method java/io/PrintStream.print:(Ljava/lang/Object;)V
      24: nop
      25: aload_0                 // thelambdaat index 5
      26: invokeinterface #22,  1 // InterfaceMethod kotlin/jvm/functions/Function0.invoke:()LObject;
      31: pop
      32: nop
      33: return
// use -v flag to see the following line
InnerClasses:
static final #37; // class cn/tuyucheng/taketoday/crossinline/InlinesKt$main$2
```

从上面的字节码，我们可以明白几点：

-   没有invokestatic表示对executeAll()方法的调用，因此Kotlin肯定会内联此方法调用
-   索引11到21表示直接调用System.out.print(“Hello”)，因此第一个lambda函数也是内联的
-   在索引5处，我们得到cn/tuyucheng/taketoday/crossinline/InlinesKt$main$2单例实例，它是Kotlin中Function0的子类型(索引3)，Kotlin将没有参数和Unit返回类型的lambda函数编译为Function0。在索引26处，我们在此Function0上调用invoke()方法，这基本上等同于调用非内联lambda函数

## 4. crossinline效应

**在Kotlin中，我们只能使用普通的、无限定的return来退出命名函数、匿名函数或内联函数**。为了退出lambda，我们必须使用标签(如return@label)，我们不能在lambda中使用正常的返回，因为它会从封闭函数中退出：

```kotlin
fun foo() {
    val f = {
        println("Hello")
        return // won't compile
    }
}
```

在这里，Kotlin编译器不允许我们 在lambda中使用return退出封闭函数，这样的return称为非本地return。

我们可以在内联函数中使用非本地控制流，因为lambda将在内联调用站点中：

```kotlin
inline fun foo(f: () -> Unit) {
    f()
}

fun main() {
    foo { 
        println("Hello World")
        return
    }
}
```

即使我们从lambda退出，lambda本身也是内联在main函数中的。因此，这个return语句直接发生在main函数中，而不是在lambda中，**这就是我们可以在内联函数中使用普通return的原因**。

鉴于此，将lambda函数从内联函数传递到非内联函数时会发生什么？让我们来看看：

```kotlin
inline fun foo(f: () -> Unit) {
    bar { f() }
}

fun bar(f: () -> Unit) {
    f()
}
```

在这里，我们将f lambda从内联函数传递给非内联函数，**当像这样将内联函数中的lambda参数传递给另一个非内联函数上下文时，我们不能使用非本地return。所以，上面的代码甚至不会在Kotlin中编译**。

让我们看看如果Kotlin允许这样做会导致什么样的问题。

### 4.1 问题

如果Kotlin允许上述功能，我们实际上可以在调用站点使用非本地返回：

```kotlin
fun main() {
    foo {
        println("Hello World")
        return
    }
}
```

由于foo是一个内联函数，Kotlin会将其内联到调用站点中，lambda也是如此。因此，在一天结束时，Kotlin会将main函数编译成如下形式：

```kotlin
fun main() {
    bar {
        println("Hello World")
        return // root cause
    }
}
```

尽管Kotlin在这里内联了一些调用，但对bar函数的调用仍然保持原样。因此，如果Kotlin允许内联函数foo的非局部返回，它最终将违反其关于在函数bar的lambda中不使用非局部控制流的规则。

这就是这条规则背后的原因，所以，总结一下，我们可以在三种情况下使用非局部控制流：

-   普通命名函数
-   匿名函数
-   仅当我们直接调用lambda或将其传递给另一个内联函数时才使用内联函数

### 4.2 解决方案

**有时，我们知道我们不会在lambda函数中使用非本地控制流。同时，我们可能还想受益于内联函数的优势**。

在这种情况下，我们可以用crossinline修饰符标记内联函数的lambda参数：

```kotlin
inline fun foo(crossinline f: () -> Unit) {
    bar { f() }
}

fun bar(f: () -> Unit) {
    f()
}
```

在crossinline修饰符的帮助下，上面的代码将通过编译。但是，我们仍然不能在调用站点上使用非本地return：

```kotlin
fun main() {
    foo {
        println("Hello World")
        return // won't compile
    }
}
```

这就是crossinline的全部存在目的：受益于内联函数的效率，同时失去在lambda中使用非本地控制流的能力。

### 4.3 noinline与crossinline

令人惊讶的是，我们甚至可以使用noinline修饰符来编译foo和bar函数：

```kotlin
inline fun foo(noinline f: () -> Unit) {
    bar { f() }
}

fun bar(f: () -> Unit) {
    f()
}
```

这肯定会编译，然而，**我们失去了内联函数的效率和使用非本地returns的能力**，甚至Kotlin也会在编译期间警告我们这个事实：

```bash
>> kotlinc Inlines.kt
Inlines.kt:12:1: warning: expected performance impact from inlining is insignificant. Inlining works best for functions with parameters of functional types
inline fun foo(noinline f: () -> Unit) {
^
```

在这里，Kotlin编译器明确指出，当所有lambda都标记为noinline时，我们不会从内联函数中获得太多好处。

## 5. 总结

在本文中，我们评估了noinline和crossinline修饰符之间的差异，更具体地说，前者允许我们从内联中排除一些lambda参数。

此外，当将lambda从内联函数传递给非内联函数时，我们可以使用noinline。但是，如果我们为此目的使用这个修饰符，我们将失去内联的效率。同样，crossinline修饰符适用于相同的场景，但有一个很大的不同：我们仍然可以受益于内联的优越性。