## 1. 概述

在本文中，我们将介绍Kotlin语法中最有趣的特性之一：惰性初始化。

我们还将研究lateinit关键字，它允许我们欺骗编译器并在类的主体中初始化非空字段，而不是在构造函数中。

## 2. Java中的惰性初始化模式

有时我们需要构造具有繁琐初始化过程的对象，此外，我们常常不能确定我们在程序开始时为此付出了初始化成本的对象是否会在我们的程序中使用。

**“惰性初始化”的概念旨在防止不必要的对象初始化**，在Java中，以惰性和线程安全的方式创建对象并不是一件容易的事情，像Singleton这样的模式在多线程、测试等方面存在重大缺陷-它们现在被广泛称为要避免的反模式。

或者，我们可以利用Java中内部对象的静态初始化来实现惰性：

```java
public class ClassWithHeavyInitialization {

    private ClassWithHeavyInitialization() {
    }

    private static class LazyHolder {
        public static final ClassWithHeavyInitialization INSTANCE = new ClassWithHeavyInitialization();
    }

    public static ClassWithHeavyInitialization getInstance() {
        return LazyHolder.INSTANCE;
    }
}
```

请注意，只有当我们调用ClassWithHeavyInitialization的getInstance()方法时，才会加载静态LazyHolder类，并创建ClassWithHeavyInitialization的新实例。接下来，实例将被分配给静态最终的INSTANCE引用。

我们可以测试getInstance()每次调用时是否都返回相同的实例：

```java
@Test
public void giveHeavyClass_whenInitLazy_thenShouldReturnInstanceOnFirstCall() {
    // when
    ClassWithHeavyInitialization classWithHeavyInitialization = ClassWithHeavyInitialization.getInstance();
    ClassWithHeavyInitialization classWithHeavyInitialization2 = ClassWithHeavyInitialization.getInstance();

    // then
    assertTrue(classWithHeavyInitialization == classWithHeavyInitialization2);
}
```

这在技术上是可以的，**但对于这样一个简单的概念来说当然有点太复杂了**。

## 3. Kotlin中的惰性初始化

我们可以看到在Java中使用惰性初始化模式是相当麻烦的，我们需要编写大量样板代码才能实现我们的目标。**幸运的是，Kotlin语言内置了对惰性初始化的支持**。

要创建一个将在第一次访问它时初始化的对象，我们可以使用lazy方法：

```kotlin
@Test
fun givenLazyValue_whenGetIt_thenShouldInitializeItOnlyOnce() {
    // given
    val numberOfInitializations: AtomicInteger = AtomicInteger()
    val lazyValue: ClassWithHeavyInitialization by lazy {
        numberOfInitializations.incrementAndGet()
        ClassWithHeavyInitialization()
    }
    // when
    println(lazyValue)
    println(lazyValue)

    // then
    assertEquals(numberOfInitializations.get(), 1)
}
```

正如我们所见，传递给lazy函数的lambda只执行了一次。

当我们第一次访问lazyValue时，实际的初始化发生了，ClassWithHeavyInitialization类的返回实例被分配给lazyValue引用，对lazyValue的后续访问返回了先前初始化的对象。

我们可以将LazyThreadSafetyMode作为参数传递给lazy函数，默认发布模式是SYNCHRONIZED，这意味着只有一个线程可以初始化给定的对象。

我们可以将PUBLICATION作为模式传递-这将导致每个线程都可以初始化给定的属性，分配给引用的对象将是第一个返回值-因此第一个线程获胜。

让我们看一下这个场景：

```kotlin
@Test
fun whenGetItUsingPublication_thenCouldInitializeItMoreThanOnce() {

    // given
    val numberOfInitializations: AtomicInteger = AtomicInteger()
    val lazyValue: ClassWithHeavyInitialization
            by lazy(LazyThreadSafetyMode.PUBLICATION) {
                numberOfInitializations.incrementAndGet()
                ClassWithHeavyInitialization()
            }
    val executorService = Executors.newFixedThreadPool(2)
    val countDownLatch = CountDownLatch(1)

    // when
    executorService.submit { countDownLatch.await(); println(lazyValue) }
    executorService.submit { countDownLatch.await(); println(lazyValue) }
    countDownLatch.countDown()

    // then
    executorService.awaitTermination(1, TimeUnit.SECONDS)
    executorService.shutdown()
    assertEquals(numberOfInitializations.get(), 2)
}
```

我们可以看到同时启动两个线程会导致ClassWithHeavyInitialization的初始化发生两次。

还有第三种模式NONE，但它不应该在多线程环境中使用，因为它的行为是未定义的。

## 4. Kotlin的lateinit

在Kotlin中，类中声明的每个不可为null的类属性都应在构造函数中或作为变量声明的一部分进行初始化，如果我们未能做到这一点，那么Kotlin编译器将抱怨一条错误消息：

```shell
Kotlin: Property must be initialized or be abstract
```

这基本上意味着我们应该初始化变量或将其标记为abstract。

另一方面，在某些情况下，可以通过依赖注入等方式动态分配变量。

为了延迟变量的初始化，我们可以指定一个字段为lateinit，我们通知编译器这个变量将在稍后赋值，并且我们将编译器从确保此变量初始化的责任中解放出来：

```kotlin
lateinit var a: String

@Test
fun givenLateInitProperty_whenAccessItAfterInit_thenPass() {
    // when
    a = "it"
    println(a)

    // then not throw
}
```

如果我们忘记初始化lateinit属性，我们将得到一个UninitializedPropertyAccessException：

```kotlin
@Test(expected = UninitializedPropertyAccessException::class)
fun givenLateInitProperty_whenAccessItWithoutInit_thenThrow() {
    // when
    println(a)
}
```

**值得一提的是，我们只能对非原始数据类型使用lateinit变量**，因此，不可能写这样的东西：

```kotlin
lateinit var value: Int
```

如果我们这样做，我们会得到一个编译错误：

```shell
Kotlin: 'lateinit' modifier is not allowed on properties of primitive types
```

## 5. 总结

在这个快速教程中，我们了解了对象的惰性初始化。

首先，我们了解了如何在Java中创建线程安全的惰性初始化，我们看到它非常繁琐，需要大量的样板代码。

接下来，我们深入研究了用于属性延迟初始化的Kotlin lazy关键字。最后，我们看到了如何使用lateinit关键字延迟分配变量。