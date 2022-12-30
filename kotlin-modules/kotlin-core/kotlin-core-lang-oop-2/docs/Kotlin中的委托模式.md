## 1. 概述

在许多用例中，委派优先于继承，Kotlin对此有很好的语言级支持。

在本教程中，我们将讨论**Kotlin对委托模式的原生支持**，并了解它的实际应用。

## 2. 实现

首先，假设我们在第三方库中有一个代码示例，其结构如下：

```kotlin
interface Producer {

    fun produce(): String
}

class ProducerImpl : Producer {

    override fun produce() = "ProducerImpl"
}
```

接下来，**让我们使用“by”关键字修饰现有的实现**，并添加额外的必要处理：

```kotlin
class EnhancedProducer(private val delegate: Producer) : Producer by delegate {

    override fun produce() = "${delegate.produce()} and EnhancedProducer"
}
```

因此，在本例中，我们指出EnhancedProducer类将封装Producer类型的委托对象。而且，它还可以使用Producer实现中的功能。

最后，让我们验证它是否按预期工作：

```kotlin
val producer = EnhancedProducer(ProducerImpl())
assertThat(producer.produce()).isEqualTo("ProducerImpl and EnhancedProducer")
```

## 3. 使用案例

现在，让我们看一下委托模式的两个常见用例。

首先，我们**可以使用委托模式使用现有实现来实现多个接口**：

```kotlin
class CompositeService : UserService by UserServiceImpl(), MessageService by MessageServiceImpl()
```

其次，我们可以**使用委托来增强现有的实现**。

后者是我们在上一节中所做的。但是，当我们无法修改现有实现时(例如第三方库代码)，像下面这样的更真实的示例特别有用：

```kotlin
class SynchronizedProducer(private val delegate: Producer) : Producer by delegate {

    private val lock = ReentrantLock()

    override fun produce(): String {
        lock.withLock {
            return delegate.produce()
        }
    }
}
```

## 4. 委托不是继承

现在，我们需要始终记住**委托对装饰器一无所知**。所以，我们不应该对它们尝试类似于GoF模板方法的方法。

让我们考虑一个例子：

```kotlin
interface Service {

    val seed: Int

    fun serve(action: (Int) -> Unit)
}

class ServiceImpl : Service {

    override val seed = 1

    override fun serve(action: (Int) -> Unit) {
        action(seed)
    }
}

class ServiceDecorator : Service by ServiceImpl() {
    override val seed = 2
}
```

在这里，委托(ServiceImpl)使用公共接口中定义的属性，我们在装饰器(ServiceDecorator)中覆盖它，但是，它不会影响委托的处理：

```kotlin
val service = ServiceDecorator()
service.serve {
    assertThat(it).isEqualTo(1)
}
```

最后，需要注意的是，在Kotlin中，我们不仅**可以委托接口，还可以委托**[分离属性](https://www.baeldung.com/kotlin-delegated-properties)。

## 5. 总结

在本教程中，我们讨论了Kotlin接口委托-何时使用、如何配置以及注意事项。