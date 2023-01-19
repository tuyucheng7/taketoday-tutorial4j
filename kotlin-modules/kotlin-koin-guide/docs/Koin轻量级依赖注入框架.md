## 一、简介

依赖注入将继续存在。没有它，很难想象令人垂涎的关注点分离或适当的可测试性。

同时，虽然 Spring Framework 是一个普遍的选择，但它并不适合所有人。有些人更喜欢更轻量级的框架，更好地支持异步 IO。有些人会喜欢静态依赖解析以获得更好的启动性能。

总是有[Guice](https://github.com/google/guice)，但如果我们想要具有更多 Kotlin 外观和感觉的东西，我们应该看看[Koin](https://insert-koin.io/)。这个轻量级框架通过 DSL 提供依赖注入功能，这在 Java 专用的 Guice 中很难实现。

除了作为一种在我们的代码中声明实体之间依赖关系的表达方式之外，Koin 还原生支持流行的 Kotlin 应用程序，例如 Ktor 和 Android 平台。作为另一个权衡，Koin 是“无魔法的”——它不生成任何代理，不使用反射，也不尝试试探法来找到合适的实现来满足我们的依赖性。另一方面，它只会做明确告诉它要做的事情，不会有类似 Spring 的“自动装配”。

在本教程中，我们将学习 Koin 的基础知识，并为更高级地使用该框架铺平道路。

## 2. 如何从 Koin 开始

与所有库一样，我们必须添加一些依赖项。根据项目的不同，我们将需要 vanilla Kotlin 设置或 Ktor。对于 Gradle Kotlin DSL，有两个依赖项可以使其在 vanilla 模式下工作：

```kotlin
val koin_version = "3.2.0-beta-1"
implementation("io.insert-koin:koin-core:$koin_version")
testImplementation("io.insert-koin:koin-test:$koin_version")
```

如果我们计划在我们的项目中使用 JUnit 5，我们还需要它的依赖项：

```kotlin
testImplementation("io.insert-koin:koin-test-junit5:$koin_version")
```

同样，对于 Ktor 版本，有一个特殊的依赖(它取代了 Ktor 应用程序中的核心依赖)：

```kotlin
implementation("io.insert-koin:koin-ktor:$koin_version")
```

这就是我们开始使用该库所需的全部内容。我们将使用最新的测试版，以便该指南的相关性更长。

## 3. 模块和定义

让我们开始我们的旅程，为我们的 DI 创建一个注册表，以便在注入依赖项时使用。

### 3.1. 模块

这些模块包含服务、资源和存储库之间的依赖关系声明。可以有多个模块，每个语义字段一个模块。在创建 Koin 上下文时，所有模块都进入modules()函数，稍后讨论。

这些模块可以依赖于其他模块的定义。Koin 懒惰地计算定义。这意味着定义甚至可以形成依赖环。但是，避免创建语义圈仍然是有意义的，因为它们将来可能难以支持。

要创建模块，我们必须使用 function module {}：

```kotlin
class HelloSayer() {
    fun sayHello() = "Hello!"
}

val koinModule = module {
    single { HelloSayer() }
}
```

模块可以相互包含：

```kotlin
val koinModule = module {
    // Some configuration
}

val anotherKoinModule = module {
    // More configuration
}

val compositeModule = module {
    includes(koinModule, anotherKoinModule)
}
```

此外，它们可以形成一棵复杂的树，而不会在运行时造成重大损失。includes()函数将展平所有定义。

### 3.2. 单例和工厂定义

要创建一个定义，大多数情况下，我们将不得不使用单个 <T>{}函数，其中T是一个类型，它应该与稍后的get<T>()调用中请求的类型相匹配：

```kotlin
single<RumourTeller> { RumourMonger(get()) }
```

单个 {}将为单例对象创建一个定义，并在每次调用get()时返回同一个实例。

另一种创建单例的方法是新的 3.2 版本功能singleOf()。它基于两个观察。

首先，大多数 Kotlin 类只有一个构造函数。多亏了默认值，它们不需要多个构造函数来支持像 Java 中的各种用例。

其次，大多数定义没有替代方案。在旧的 Koin 版本中，这导致了如下定义：

```kotlin
single<SomeType> { get(), get(), get(), get(), get(), get() }
```

因此，我们可以提及我们想要调用的构造函数：

```kotlin
class BackLoop(val dependency: Dependency)

val someModule = module {
    singleOf(::BackLoop)
}
```

另一个动词是factory {}，它会在每次请求注册定义时创建一个实例：

```kotlin
factory { RumourSource() }
```

除非我们使用createdAtStart = true参数声明单个 {}依赖项，否则创建者 lambda 将仅在KoinComponent明确请求依赖项时运行。

### 3.3. 定义选项

我们需要了解的是每个定义都是一个 lambda。这意味着，虽然它通常是一个简单的构造函数调用，但它不一定是：

```kotlin
fun helloSayer() = HelloSayer()

val factoryFunctionModule = module {
    single { helloSayer() }
}
```

此外，定义可以有一个参数：

```scss
module {
    factory { (rumour: String) -> RumourSource(rumour) }
}
```

在单例的情况下，第一次调用将创建一个实例，所有其他传递参数的尝试都将被忽略：

```kotlin
val singleWithParamModule = module {
    single { (rumour: String) -> RumourSource(rumour) }
}

startKoin {
    modules(singleWithParamModule)
}
val component = object : KoinComponent {
    val instance1 = get<RumourSource> { parametersOf("I've seen nothing") }
    val instance2 = get<RumourSource> { parametersOf("Jane is seeing Gill") }
}

assertEquals("I've heard that I've seen nothing", component.instance1.tellRumour())
assertEquals("I've heard that I've seen nothing", component.instance2.tellRumour())
```

在工厂的情况下，每个注入都将按预期使用其参数实例化：

```kotlin
val factoryScopeModule = module {
    factory { (rumour: String) -> RumourSource(rumour) }
}

startKoin {
    modules(factoryScopeModule)
}
// Same component instantiation

assertEquals("I've heard that I've seen nothing", component.instance1.tellRumour())
assertEquals("I've heard that Jane is seeing Gill", component.instance2.tellRumour())
```

另一个技巧是如何定义多个相同类型的对象。没有比这更简单的了：

```javascript
val namedSources = module {
    single(named("Silent Bob")) { RumourSource("I've seen nothing") }
    single(named("Jay")) { RumourSource("Jack is kissing Alex") }
}
```

现在我们可以在注入它们时区分它们。

## 4. Koin 组件

来自模块的定义用于KoinComponent s。实现KoinComponent的类有点类似于 Spring @Component。它有一个到全局Koin实例的链接，并作为模块中编码的对象树的入口点：

```kotlin
class SimpleKoinApplication : KoinComponent {
    private val service: HelloSayer by inject()
}
```

默认情况下，到Koin实例的链接是隐式的并使用GlobalContext，但在某些情况下可以覆盖此机制。

我们应该正常实例化 Koin 组件，通过它们的构造函数，而不是通过将它们注入到模块中。这是库作者的[建议：将组件包含到模块中可能会导致性能下降或递归过深的风险。](https://insert-koin.io/docs/reference/koin-core/koin-component)

### 4.1. Eager Evaluation 与 Lazy Evaluation

一个KoinComponent有权力inject()或get()一个依赖：

```kotlin
class SimpleKoinApplication : KoinComponent {
    private val service: HelloSayer by inject()
    private val rumourMonger: RumourTeller = get()
}
```

注入意味着惰性评估：它需要使用by关键字并返回一个在第一次调用时评估的委托。获取立即返回依赖项。

## 5. 合币实例

要激活我们所有的定义，我们必须创建一个Koin实例。它可以在GlobalContext中创建和注册，并且在我们的整个运行时都可用，或者我们可以创建一个独立的Koin并自己注意对它的引用。

要创建一个独立的 Koin 实例，我们必须使用koinApplication{}：

```kotlin
val app = koinApplication {
    modules(koinModule)
}
```

我们必须保留对应用程序的引用，并在以后使用它来初始化我们的组件。startKoin {}函数创建Koin的全局实例：

```kotlin
startKoin {
    modules(koinModule)
}
```

但是，Koin 特别偏爱某些框架。一个例子是 Ktor。它有其初始化 Koin 配置的方式。让我们谈谈在“vanilla”Kotlin 应用程序和 Ktor Web 服务器中设置 Koin。

### 5.1. 基本香草 Koin 应用程序

要从基本的 Koin 配置开始，我们必须创建一个Koin实例：

```kotlin
startKoin {
    logger(PrintLogger(Level.INFO))
    modules(koinModule, factoryScopeModule)
    fileProperties()
    properties(mapOf("a" to "b", "c" to "d"))
    environmentProperties()
    createEagerInstances()
}
```

每个 JVM 生命周期只能调用一次该函数。其中最重要的部分是modules()调用，其中加载了所有定义。但是，我们可以加载额外的模块或稍后使用loadKoinModules()和unloadKoinModules()卸载一些模块。

让我们看看startKoin {} lambda 中的其他调用。有logger()、各种properties()和createEagerInstances()调用。前两个值得他们的章节，而第三个创建那些具有createdAtStart = true参数的单例。

### 5.2. 带有 Koin 的基本 Ktor 服务器

对于 Ktor 服务器，Koin 只是我们安装的另一个功能。它扮演startKoin {}调用的角色：

```kotlin
fun Application.module(testing: Boolean = false) {
    koin {
        modules(koinModule)
    }
}
```

之后，Application类具有KoinComponent的功能，可以注入()依赖项：

```kotlin
routing {
    val helloSayer: HelloSayer by inject()
    get("/") {
        call.respondText("${helloSayer.sayHello()}, world!")
    }
}
```

### 5.3. 独立的 Koin 实例

最好不要将全局 Koin 实例用于 SDK 和库。为此，我们必须使用koinApplication {}启动器并将返回的引用保留为Koin实例：

```kotlin
val app = koinApplication {
    modules(koinModule)
}
```

然后我们需要覆盖部分默认的KoinComponent功能：

```kotlin
class StandaloneKoinApplication(private val koinInstance: Koin) : KoinComponent {
    override fun getKoin(): Koin = koinInstance
    // other component configuration
}
```

之后，我们将能够在运行时使用一个额外的参数——Koin实例来实例化组件：

```kotlin
StandaloneKoinApplication(app.koin).invoke()
```

## 6. 日志记录和属性

现在让我们谈谈startKoin {}配置中的那些logger()和properties()函数。

### 6.1. 我体验过Logger

为了方便搜索配置问题，我们可以启用 Koin 记录器。事实上，它始终处于启用状态，但默认情况下，它使用其EmptyLogger实现。我们可以将其更改为PrintLogger以在标准输出中查看 Koin 日志：

```kotlin
startKoin {
    logger(PrintLogger(Level.INFO))
}
```

或者，我们可以实现我们的记录器，或者，如果我们使用 Ktor、Spark 或 Android 风格的 Koin，我们可以使用它们的记录器：SLF4JLogger或AndroidLogger。

### 6.2. 特性

Koin 还可以从文件(默认位置是classpath:koin.properties，因此在我们的项目中，这个文件应该是src/main/resources/koin.properties)，从系统环境和直接从传递的映射中使用属性：

```kotlin
startKoin {
    modules(initByProperty)
    fileProperties()
    properties(mapOf("rumour" to "Max is kissing Alex"))
    environmentProperties()
}
```

然后在模块中，我们可以通过getProperty()方法访问这些属性：

```kotlin
val initByProperty = module { 
    single { RumourSource(getProperty("rumour", "Some default rumour")) }
}
```

## 7. 测试 Koin 应用程序

Koin 还提供了相当广泛的测试基础设施。通过实现KoinTest接口，我们赋予我们的测试KoinComponent权力等等：

```kotlin
class KoinSpecificTest : KoinTest {
    @Test
    fun `when test implements KoinTest then KoinComponent powers are available`() {
        startKoin {
            modules(koinModule)
        }
        val helloSayer: HelloSayer = get()
        assertEquals("Hello!", helloSayer.sayHello())
    }
}
```

### 7.1. 模拟 Koin 定义

模拟或以其他方式替换模块中声明的实体的一种简单方法是在我们在测试中启动 Koin 时创建一个临时模块：

```kotlin
startKoin {
    modules(
        koinModule,
        module {
            single<RumourTeller> { RumourSource("I know everything about everyone!") }
        }
    )
}
```

另一种方法是对startKoin {}和模拟使用 JUnit 5 扩展：

```kotlin
@JvmField
@RegisterExtension
val koinTestExtension = KoinTestExtension.create {
    modules(
        module {
            single<RumourTeller> { RumourSource("I know everything about everyone!") }
        }
    )
}

@JvmField
@RegisterExtension
val mockProvider = MockProviderExtension.create { clazz ->
    mockkClass(clazz)
}
```

注册这些扩展后，模拟不必在一个特殊的模块或一个地方。对declareMock<T> {}的任何调用都将创建一个适当的模拟：

```kotlin
@Test
fun when_extensions_are_used_then_mocking_is_easier() {
    declareMock<RumourTeller> {
        every { tellRumour() } returns "I don't even know."
    }
    val mockedTeller: RumourTeller by inject()
    assertEquals("I don't even know.", mockedTeller.tellRumour())
}
```

我们可以使用任何我们喜欢的模拟库或模拟方法，因为 Koin 没有指定模拟框架。

### 7.2. 检查 Koin 模块

Koin 还提供工具来检查我们的模块配置并发现我们在运行时可能遇到的所有可能的注入问题。这很容易做到：我们需要在KoinApplication配置中调用checkModules()或者使用checkKoinModules ( ) 检查模块列表： 

```kotlin
koinApplication {
    modules(koinModule, staticRumourModule)
    checkModules()
}
```

模块检查有其 DSL。这种语言允许模拟模块中的某些值或提供替代值。它还允许传递模块可能需要实例化的参数：

```kotlin
koinApplication {
    modules(koinModule, module { single { RumourSource() } })
    checkModules {
        withInstance<RumourSource>()
        withParameter<RumourTeller> { "Some param" }
    }
}
```

## 八、总结

在本指南中，我们非常仔细地研究了 Koin 库以及如何使用它。

Koin 创建了一个根上下文对象，其中包含用于实例化依赖项的配置以及这些依赖项的特定参数。在单例对象的情况下，它还包含对这些依赖项实例的引用。

依赖关系可以在模块中描述。这些模块被输入到Koin对象的创建者函数中。他们描述了如何使用生产者函数创建依赖关系，这些函数通常只是对构造函数的调用。模块可以相互依赖，单独的定义可以有不同的作用域，比如单例和工厂。这些定义还可能具有允许我们将特定于环境的数据注入对象树的参数。

要访问依赖项，我们需要将一个或多个对象标记为KoinComponent。然后我们可以将此类对象的字段声明为inject()委托，或者使用get()热切地实例化它们。

在 Koin 创建过程中， 我们可以从文件、环境和以编程方式创建的Map中注入参数。我们可以使用所有这三种方式或它们的任何子集。在Koin对象中，这些属性被放置在一个注册表中，因此最后一个要加载的属性定义获胜。

对于模块也是如此：允许重写定义，最新的定义优先。

Koin 提供了用于测试功能和 Koin 配置本身的功能。