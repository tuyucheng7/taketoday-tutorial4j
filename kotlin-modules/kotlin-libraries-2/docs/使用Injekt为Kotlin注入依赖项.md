## 一、简介

依赖注入是一种软件开发模式，我们将对象创建与正在创建的对象分开。我们可以使用它来保持我们的主要应用程序代码尽可能干净。这反过来又使其更易于使用和测试。

在本教程中，我们将探索将依赖注入引入 Kotlin 的[Injekt](https://github.com/kohesive/injekt)框架。

注意：Injekt 库不再积极开发，开发人员建议改用[Kodein](https://www.baeldung.com/kotlin-kodein-dependency-injection)。

## 2. 什么是依赖注入？

依赖注入是一种常见的软件开发模式，用于使应用程序更易于维护和构建。使用这种模式，我们将应用程序对象的构造与它们的实际运行时行为分开。这意味着我们应用程序的每个部分都是独立的，不直接依赖于任何其他部分。相反，当我们构造我们的对象时，我们可以提供它需要的所有依赖项。

使用依赖注入，我们可以轻松地测试我们的代码。因为我们控制了依赖关系，所以我们可以在测试时提供不同的依赖关系。这允许使用模拟或存根对象，以便我们的测试代码可以绝对控制单元外的所有内容。

我们还可以简单地更改应用程序某些部分的实现，而无需更改其他部分。例如，可以用基于 MongoDB 的 DAO 对象替换基于 JPA 的 DAO 对象，只要它实现相同的接口，就不需要更改其他任何内容。这是因为被注入的依赖已经改变，但是被注入的代码并不直接依赖于它。

在 Java 开发中，最著名的依赖注入框架是 Spring。然而，当我们使用它时，我们引入了很多我们通常不需要也不想要的附加功能。在其绝对核心，依赖注入只需要一个设置，我们在其中构建应用程序对象与我们如何使用它们分开。

## 3.Maven依赖

Injekt 是一个标准的 Kotlin 库，可以在 Maven Central 上找到以包含在我们的项目中。

我们可以在我们的项目中包括以下依赖项：

```xml
<dependency>
    <groupId>uy.kohesive.injekt</groupId>
    <artifactId>injekt-core</artifactId>
    <version>1.16.1</version>
</dependency>
```

为了让代码更简单，推荐使用star imports将Injekt引入到我们的应用中：

```groovy
import uy.kohesive.injekt.
import uy.kohesive.injekt.api.
```

## 4.简单的应用布线

一旦 Injekt 可用，我们就可以开始使用它来将我们的类连接在一起以构建我们的应用程序。

### 4.1. 开始我们的申请

在最简单的情况下，Injekt 提供了一个基类，我们可以将其用于我们的应用程序主类：

```groovy
class SimpleApplication {
    companion object : InjektMain() {
        @JvmStatic fun main(args: Array<String>) {
            SimpleApplication().run()
        }

        override fun InjektRegistrar.registerInjectables() {
            addSingleton(Server())
        }
    }

    fun run() {
        val server = Injekt.get<Server>()
        server.start()
    }
}
```

我们可以在 registerInjectables方法中定义我们的 beans，然后 run方法是我们应用程序的实际入口点。在这里我们可以访问我们根据需要注册的任何 beans。

### 4.2. 引入单例对象

正如我们在上面看到的，我们可以使用 addSingleton方法在我们的应用程序中注册 Singleton 对象。这一切所做的就是创建一个对象并将其放入我们的依赖注入容器中以供其他对象访问。

这也意味着我们在创建这些时不能引用容器中的其他 bean，因为容器还不存在。

或者，我们可以注册回调以仅在需要时构造单例。

这使我们能够依赖其他 bean，这也意味着我们不会在需要它们之前创建它们，从而使我们更有效率：

```groovy
class Server(private val config: Config) {
    private val LOG = LoggerFactory.getLogger(Server::class.java)
    fun start() {
        LOG.info("Starting server on ${config.port}")
    }
}
override fun InjektRegistrar.registerInjectables() {
    addSingleton(Config(port = 12345))
    addSingletonFactory { Server(Injekt.get()) }
}
```

请注意，我们使用回调方法构造我们的 服务器bean，它直接从 Injekt 容器提供所需的 Config对象。

我们不需要告诉 Injekt 这里需要的类型，因为它可以根据上下文推断它们——它需要返回一个 Config类型的对象，这就是我们得到的。

### 4.3. 引入工厂对象

有时，我们希望在每次使用时都创建一个新对象。例如，我们可能有一个对象是另一个服务的网络客户端，每个使用它的地方都应该注入它的客户端，以及它的网络连接和一切。

我们可以使用 addFactory方法而不是 addSingletonFactory来实现这一点。

这里唯一的区别是我们将在每次注入时创建一个新实例，而不是缓存它并重用它：

```groovy
class Client(private val config: Config) {
    private val LOG = LoggerFactory.getLogger(Client::class.java)
    fun start() {
        LOG.info("Opening connection to on ${config.host}:${config.port}")
    }
}
override fun InjektRegistrar.registerInjectables() {
    addSingleton(Config(host = "example.com", port = 12345))
    addFactory { Client(Injekt.get()) }
}
```

在此示例中，我们随后注入 Client的所有地方都将获得一个全新的实例，但所有这些实例将共享相同的 Config对象。

## 5.访问对象

我们可以通过不同的方式访问容器构建的对象，这取决于最合适的方式。上面我们已经看到我们可以在构造时将一个对象从容器中注入到另一个对象中。

### 5.1. 从容器直接访问

我们可以 随时从我们代码中的任何地方调用Injekt.get ，它会做同样的事情。这意味着我们也可以随时从实时应用程序中调用它来访问容器中的对象。

这对于工厂对象特别有用，我们每次在运行时都会获得一个新实例，而不是在构造时注入同一个实例：

```groovy
class Notifier {
    fun sendMessage(msg: String) {
        val client: Client = Injekt.get()
        client.use {
            client.send(msg)
        }
    }
}
```

这也意味着我们不限于为我们的代码使用类。我们也可以从顶级函数内部的容器中访问对象。

### 5.2. 用作默认参数

Kotlin 允许我们为参数指定默认值。我们还可以在这里使用 Injekt，以便在未提供替代值时从容器中获取一个值。

这对于编写单元测试特别有用，其中同一个对象可以在实时应用程序中使用——自动从容器中获取依赖项，或者从单元测试中——我们可以为测试目的提供一个替代方案：

```groovy
class Client(private val config: Config = Injekt.get()) {
    ...
}
```

我们同样可以将其用于构造函数参数和方法参数，以及类和顶级函数。

### 5.3. 使用代表

Injekt 为我们提供了一些委托，我们可以使用它们来自动访问容器对象作为类字段。

injectValue 委托将在类构造时立即从容器中获取对象，而 injectLazy委托仅在首次使用时才从容器中获取对象：

```groovy
class Notifier {
    private val client: Client by injectLazy()
}
```

## 6.高级对象构造

到目前为止，我们所做的一切都可以在不使用 Injekt 的情况下实现，尽管不如使用 Injekt 时那么干净。

不过，我们可以使用更先进的构造工具，从而允许使用我们自己更难管理的技术。

### 6.1. 每线程对象

一旦我们开始直接在代码中访问容器中的对象，我们就开始冒着对象争用的风险。我们可以通过每次获取一个新实例来解决这个问题——使用 addFactory创建——但这可能会变得昂贵。

或者，Injekt 可以为调用它的每个线程创建一个新实例，然后缓存该线程的实例。

这避免了争用的风险——每个线程一次只能做一件事——同时也减少了我们需要创建的对象数量：

```groovy
override fun InjektRegistrar.registerInjectables() {
    addPerThreadFactory { Client(Injekt.get()) }
}
```

我们现在可以随时获取一个 Client对象，它在当前线程中始终是相同的，但绝不会与任何其他线程中的相同。

### 6.2. 键控对象

我们需要注意不要被对象的每线程分配冲昏了头脑。如果有固定数量的线程，这很好，但如果线程被创建并经常被销毁，那么我们的对象集合可能会不必要地增长。

此外，有时出于不同的原因，我们需要同时访问同一类的不同实例。出于同样的原因，我们仍然希望能够访问同一个实例。

Injekt 使我们能够访问键控集合中的对象，其中请求对象的调用者提供键。

这意味着任何时候我们使用相同的键，我们都会得到相同的对象。如果需要以某种方式修改功能，工厂方法也可以访问此键：

```groovy
override fun InjektRegistrar.registerInjectables() {
    addPerKeyFactory { provider: String ->
        OAuthClientDetails(
            clientId = System.getProperty("oauth.provider.${provider}.clientId"),
            clientSecret = System.getProperty("oauth.provider.${provider}.clientSecret")
        )
    }
}
```

我们现在可以获得指定提供者的 OAuth 客户端详细信息——例如“google”或“twitter”。返回的对象是根据应用程序上设置的系统属性正确填充的。

## 七、模块化应用构建

到目前为止，我们只是在一个地方构建我们的容器。这可行，但随着时间的推移会变得笨拙。

Injekt 使我们能够做得比这更好，但将我们的配置拆分为模块。这让我们拥有更小、更有针对性的配置区域。它还允许我们将配置包含在它们适用的库中。

例如，我们可能有一个表示 Twitter 机器人的依赖项。这可能包括一个 Injekt 模块，这样任何其他使用它的人都可以直接插入它。

模块是扩展 InjektModule基类并实现 registerInjectables()方法的 Kotlin 对象。

我们已经使用之前使用的 InjektMain类完成了此操作。这是 InjektModule的直接子类，工作方式相同：

```groovy
object TwitterBotModule : InjektModule {
    override fun InjektRegistrar.registerInjectables() {
        addSingletonFactory { TwitterConfig(clientId = "someClientId", clientSecret = "someClientSecret") }
        addSingletonFactory { config = TwitterBot(Injekt.get()) }
    }
}
```

一旦我们有了一个模块，我们就可以使用 importModule方法将它包含在容器中的任何其他地方：

```groovy
override fun InjektRegistrar.registerInjectables() {
    importModule(TwitterBotModule)
}
```

此时，此模块中定义的所有对象都可以使用，就好像它们是直接在此处定义的一样。

## 八、总结

在本文中，我们介绍了 Kotlin 中的依赖注入，以及 Injekt 库如何使实现变得简单。

使用 Injekt 可以实现的远不止这里显示的那样。希望这能让你开始简单的依赖注入之旅。