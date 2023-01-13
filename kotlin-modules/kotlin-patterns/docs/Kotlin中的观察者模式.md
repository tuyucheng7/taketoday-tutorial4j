##  一、简介

在本教程中，我们将了解[观察者模式](https://www.baeldung.com/java-observer-pattern)并了解 Kotlin 中的几种不同实现。

## 2. 什么是观察者模式？

观察者模式是一种行为软件模式。它首先在四人帮编写的设计模式一书中进行了描述。

该模式显示了订阅机制的定义，该机制将观察对象发生的任何更改通知多个对象。这就是观察者模式在分布式事件驱动系统中相当流行的原因。

为了更好地理解，我们可以考虑 Baeldung 读者的例子。假设每次出现新文章时，读者都会收到通知。

在这个例子中，读者是Observers而 Baeldung 是Observable。

## 3.标准观察者模式实现

作为第一个实现，我们将遵循四人帮一书中的设计。它由两个接口组成。

第一个，IObserver定义更新动作：

```java
interface IObserver {
    fun update()
}
```

每次我们正在观察的对象发生变化时，都会执行此方法。

第二个接口IObservable负责保存所有观察者的信息并向它们发送更新事件：

```java
interface IObservable {
    val observers: ArrayList<IObserver>

    fun add(observer: IObserver) {
        observers.add(observer)
    }

    fun remove(observer: IObserver) {
        observers.remove(observer)
    }

    fun sendUpdateEvent() {
        observers.forEach { it.update() }
    }
}
```

通过使用add()和remove()方法，我们可以跟踪观察者。sendUpdateEvent ()方法负责将更新事件发送给所有观察者。

在最简单的实现中，它只是遍历所有观察者并在每个观察者上触发update()方法。

现在让我们考虑这些接口的示例实现。

实际上，我们将创建一个简单的时事通讯引擎，用于存储有关最新 Baeldung 文章的信息。因此，一旦出现新文章，就会触发sendUpdateEvent()方法。

我们将观察者存储为IObserver实例列表，并将最新文章的 URL 存储为String：

```java
class BaeldungNewsletter : IObservable {
    override val observers: ArrayList<IObserver> = ArrayList()
    var newestArticleUrl = ""
        set(value) {
            field = value
            sendUpdateEvent()
        }
}
```

通过覆盖newestArticleUrl属性的设置器，我们能够执行更新方法。

现在，让我们看一下观察者的实现。为了简单起见，我们只将文章 URL 打印到控制台：

```java
class BaeldungReader(private var newsletter: BaeldungNewsletter) : IObserver {
    override fun update() {
        println("New Baeldung article: ${newsletter.newestArticleUrl}")
    }
}
```

## 4. Kotlin 的内置 Observable 委托

另一种实现观察者模式的方法是使用[Kotlin 的内置可观察委托](https://www.baeldung.com/kotlin-delegated-properties)。委托返回一个回调函数，该函数在读取或写入给定属性时执行。

在此实现中，观察者存储为 lambda 函数列表。因此，文章 URL 将像以前一样是一个字符串字段，但这次分配了一个委托：

```java
class BaeldungNewsletter {
    val newestArticleObservers = mutableListOf<(String) -> Unit>()

    var newestArticleUrl: String by Delegates.observable("") { _, _, newValue ->
        newestArticleObservers.forEach { it(newValue) }
    }
}
```

在我们的例子中，我们只听写操作。因此，Delegate 只定义了一个参数。

结果，newestArticleUrl参数获得了一个新值，我们将其传递给所有观察者。

观察者可以有任何想要的实现。为了简单起见，我们再次将最新的文章 URL 打印到控制台：

```java
val newsletter = BaeldungNewsletter()
newsletter.newestArticleObservers.add { newestArticleUrl ->
    println("New Baeldung article: ${newestArticleUrl}")
}
```

## 5.总结

在本教程中，我们描述了在 Kotlin中实现观察者设计模式的两种不同方式。