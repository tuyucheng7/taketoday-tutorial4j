## 1. 概述

在本教程中，我们将编写一个简单的应用程序来展示使用 Spring Data Reactive MongoDB 和 Spring SSeEmitter的完全响应式流程。

一方面，我们将应用 Spring Data Reactive MongoDB 通过 Mongo 反应式数据库保存数据，并将其与 Server-Sent-Events 机制相结合，以通知订阅的客户端传入数据。

此外，我们将利用 Spring Boot 的 Kotlin 支持。

那么，让我们开始吧！

## 2.设置

首先，我们必须配置我们的 Maven 项目，在我们的pom.xml中添加 Spring Data Reactive MongoDB 依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
</dependency>
```

此外，要使用 Kotlin，我们需要将[Kotlin 标准库](https://search.maven.org/classic/#search|gav|1|g%3A"org.jetbrains.kotlin" AND a%3A"kotlin-stdlib")添加到同一个文件中：

```xml
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-stdlib</artifactId>
</dependency>
```

现在，我们准备开始开发我们的应用程序。我们将开始配置环境以支持响应式编程和 Mongo DB，让我们开始吧！

## 3.反应式Mongo配置

我们要做的第一件事是配置我们的项目以支持响应式 Spring Data。我们将添加一个从 AbstractReactiveMongoConfiguration扩展的新类来配置 Mongo 反应式客户端和 Spring Data Repository：

```java
@Configuration
@EnableReactiveMongoRepositories(
  basePackageClasses = arrayOf(EventRepository::class))
class MongoConfig : AbstractReactiveMongoConfiguration() {
 
    override fun getDatabaseName() = "mongoDatabase"
 
    override fun reactiveMongoClient() = mongoClient()
 
    @Bean
    fun mongoClient() = MongoClients.create()
 
    @Bean
    override fun reactiveMongoTemplate()
     = ReactiveMongoTemplate(mongoClient(), databaseName)
}
```

如果我们想以非反应方式与 MongoDB 交互，则不需要此配置。请注意，我们必须添加 @EnableReactiveMongoRepositories 标记，让配置知道我们的 Spring Data 存储库在哪里。 

在此之后，我们现在准备开始实施主要功能。我们要做的第一件事是开发一个新的数据类来保存传入的信息，然后是一个相关的 Spring Data 反应性存储库来管理该持久性。

## 4.文件

文档 是MongoDB数据库中存储数据的单位。该单元使用 JSON 样式来存储数据。

在我们的项目中，我们将使用一个名为Event 的虚拟文档 来保持简单，该文档具有两个属性：id和name：

```java
@Document
class Event(id: String, name: String)
```

## 5. Spring 数据反应存储库 

Spring Data 抽象的目标是减少为持久性存储实现数据访问层所需的代码量。

因此，反应式版本的工作方式相同，因此我们将使用以下行来实现整个反应式存储库：

```java
interface EventRepository : ReactiveMongoRepository<Event, String>
```

## 6.控制器 

每当保存任何反应数据时，Controller类将负责发送 Server-Sent Event 。

方法saveAndSend 将首先将传入的数据保存到我们的 Mongo Reactive 数据库中，并将此操作委托给我们的EventRepository。 

因此，我们将添加一个新端点来创建和保存新事件。

首先，让我们看一下 Kotlin 代码：

```java
@GetMapping(value = "/save", 
  produces = arrayOf(MediaType.TEXT_EVENT_STREAM_VALUE))
fun saveAndSend(@RequestParam("eventName") eventName: String) =
  eventRepository
    .save(Event(UUID.randomUUID().toString(), eventName))
    .flux()
```

如我们所见，保存新数据后，Spring Data 反应式存储库将返回一个 SSE，该 SSE 将发送到订阅的客户端。

至此，我们可以说我们有了一个完整的响应式 Kotlin 服务器端项目。我们已经拥有执行 Spring Boot 应用程序所需的所有元素。

因此，我们现在来看看如何创建一个简单的 Web 客户端来发送和接收我们创建的所有 Server-Sent 事件。

## 7.订阅者

这里我们有一个简单的 Web 客户端，它将能够保存数据并从服务器接收更改。

让我们看看它是如何实现的：

### 7.1. 发送数据

客户端将通过“保存新事件 ”按钮保存键入的事件名称。

反过来，这将向我们的服务器端点saveEvent发出 HTTP 请求：

```html
<form method="get" action="/save">
    <input type="text" name="eventName">
    <button type="submit">Save new event</button>
</form>
```

### 7.2. 接收数据

另一方面，客户端也将监听以保存 端点。请注意，每种编程语言都有特定的框架来管理 SSE。

但是，对于我们的示例，我们将使其尽可能简单：

```html
<div id="content"></div>
<script>
    var source = new EventSource("save");
source.addEventListener('message', function (e) {
        console.log('New message is received');
        const index = JSON.parse(e.data);
        const content = `New event added: ${index.name}<br>`;
        document.getElementById("content").innerHTML += content;
    }, false);
</script>
```

## 八、总结

总之，Spring Data MongoDB 已更新为利用 Spring Framework 5 中引入的反应式编程模型。我们现在有一种简单的方法来使用这种编程范例和服务器发送的事件。

因此，这假设了一个替代传统非反应性应用程序的数据库阻塞问题。

这个例子的实现可以[在 GitHub 项目](https://github.com/Baeldung/kotlin-tutorials/tree/master/spring-reactive-kotlin)中查看。

这是一个基于 Maven 的项目，那么，执行 Spring Boot 应用程序看看它是如何工作的。不要忘记先运行 Mongo DB 服务器。