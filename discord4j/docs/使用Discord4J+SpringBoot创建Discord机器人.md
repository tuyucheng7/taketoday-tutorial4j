## 1. 概述

[Discord4J](https://discord4j.com/)是一个开源Java库，主要用于快速访问[Discord Bot API](https://discord.com/developers/docs/intro)。[它与Project Reactor](https://projectreactor.io/)高度集成以提供完全非阻塞的反应式 API。

我们将在本教程中使用 Discord4J 创建一个能够响应预定义命令的简单 Discord 机器人。我们将在Spring Boot之上构建机器人，以演示将我们的机器人扩展到Spring Boot启用的许多其他功能上是多么容易。

当我们完成后，这个机器人将能够监听一个名为“!todo”的命令，并将打印出一个静态定义的待办事项列表。

## 2. 创建一个 Discord 应用程序

为了让我们的机器人从 Discord 接收更新并在频道中发布响应，我们需要在 Discord 开发者门户中创建一个 Discord 应用程序并将其设置为机器人。这是一个简单的过程。由于 Discord 允许在一个开发者帐户下创建多个应用程序或机器人，请随意使用不同的设置多次尝试。

以下是创建新应用程序的步骤：

-   登录到[Discord 开发者门户](https://discord.com/developers/applications)
-   在“应用程序”选项卡中，单击“新建应用程序”
-   输入我们的机器人的名称，然后单击“创建”
-   上传应用程序图标和描述，然后单击“保存更改”

[![BaeldungBot应用程序](https://www.baeldung.com/wp-content/uploads/2020/11/BaeldungBotApplication-1024x267.png)](https://www.baeldung.com/wp-content/uploads/2020/11/BaeldungBotApplication.png)

现在应用程序已经存在，我们只需要向它添加机器人功能。这将生成 Discord4J 所需的机器人令牌。

以下是将应用程序转换为机器人的步骤：

-   在“应用程序”选项卡中，选择我们的应用程序(如果尚未选择)。
-   在“机器人”选项卡中，单击“添加机器人”并确认我们要这样做。

[![BaeldungBot机器人](https://www.baeldung.com/wp-content/uploads/2020/11/BaeldungBotBot-1024x309.png)](https://www.baeldung.com/wp-content/uploads/2020/11/BaeldungBotBot.png)

现在我们的应用程序已经成为一个真正的机器人，令牌以便我们可以将它添加到我们的应用程序属性中。请注意不要公开共享此令牌，因为其他人可以在冒充我们的机器人时执行恶意代码。

我们现在准备好编写一些代码了！

## 3. 创建一个Spring Boot应用程序

在构建新的Spring Boot应用程序后，我们需要确保包含[Discord4J 核心](https://search.maven.org/artifact/com.discord4j/bom)依赖项：

```xml
<dependency>
    <groupId>com.discord4j</groupId>
    <artifactId>discord4j-core</artifactId>
    <version>3.1.1</version>
</dependency>
```

Discord4J 通过使用我们之前创建的 bot 令牌初始化[GatewayDiscordClient来工作。](https://javadoc.io/doc/com.discord4j/discord4j-core/3.1.1/discord4j/core/GatewayDiscordClient.html)这个客户端对象允许我们注册事件侦听器并配置许多东西，但至少，我们必须至少调用login()方法。这会将我们的机器人显示为在线。

首先，让我们将机器人令牌添加到我们的application.yml文件中：

```yaml
token: 'our-token-here'
```

接下来，让我们将它注入一个@Configuration类，我们可以在其中实例化我们的GatewayDiscordClient：

```java
@Configuration
public class BotConfiguration {

    @Value("${token}")
    private String token;

    @Bean
    public GatewayDiscordClient gatewayDiscordClient() {
        return DiscordClientBuilder.create(token)
          .build()
          .login()
          .block();
    }
}
```

此时，我们的机器人将被视为在线，但它还没有做任何事情。让我们添加一些功能。

## 4. 添加事件监听器

聊天机器人最常见的功能是命令。这是在[CLI](https://www.baeldung.com/spring-shell-cli)中看到的一种抽象，用户在其中键入一些文本来触发某些功能。我们可以在我们的 Discord 机器人中实现这一点，方法是侦听用户发送的新消息并在适当时使用智能响应进行回复。

我们可以监听多种类型的事件。然而，注册一个监听器对他们来说都是一样的，所以让我们首先为我们所有的事件监听器创建一个接口：

```java
import discord4j.core.event.domain.Event;

public interface EventListener<T extends Event> {

    Logger LOG = LoggerFactory.getLogger(EventListener.class);
    
    Class<T> getEventType();
    Mono<Void> execute(T event);
    
    default Mono<Void> handleError(Throwable error) {
        LOG.error("Unable to process " + getEventType().getSimpleName(), error);
        return Mono.empty();
    }
}
```

[现在我们可以为任意数量的discord4j.core.event.domain.Event](https://javadoc.io/doc/com.discord4j/discord4j-core/3.1.1/discord4j/core/event/domain/Event.html)扩展实现这个接口。

在我们实现我们的第一个事件监听器之前，让我们修改我们的客户端@Bean配置以期望一个EventListener列表，以便它可以注册在[Spring ApplicationContext](https://www.baeldung.com/spring-application-context)中找到的每个：

```java
@Bean
public <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<EventListener<T>> eventListeners) {
    GatewayDiscordClient client = DiscordClientBuilder.create(token)
      .build()
      .login()
      .block();

    for(EventListener<T> listener : eventListeners) {
        client.on(listener.getEventType())
          .flatMap(listener::execute)
          .onErrorResume(listener::handleError)
          .subscribe();
    }

    return client;
}
```

现在，注册事件侦听器所需要做的就是实现我们的接口并使用 Spring 的[基于](https://www.baeldung.com/spring-bean-annotations)[@Component](https://www.baeldung.com/spring-bean-annotations)的构造型注解对其进行注解。注册现在将为我们自动进行！

我们本可以选择单独明确地注册每个事件。但是，通常最好采用更加模块化的方法来获得更好的代码可伸缩性。

我们的事件侦听器设置现已完成，但机器人仍未执行任何操作，因此让我们添加一些要侦听的事件。

### 4.1. 命令处理

要接收用户的命令，我们可以监听两种不同的事件类型：用于新消息的[MessageCreateEvent](https://javadoc.io/doc/com.discord4j/discord4j-core/3.1.1/discord4j/core/event/domain/message/MessageCreateEvent.html)和用于更新消息的[MessageUpdateEvent 。](https://javadoc.io/doc/com.discord4j/discord4j-core/3.1.1/discord4j/core/event/domain/message/MessageUpdateEvent.html)我们可能只想收听新消息，但作为一个学习机会，让我们假设我们想要为我们的机器人支持这两种事件。这将提供我们的用户可能会欣赏的额外的稳健性。

两个事件对象都包含有关每个事件的所有相关信息。特别是，我们对消息内容、消息作者以及消息发布到的频道感兴趣。幸运的是，所有这些数据点都存在于这两种事件类型提供的Message对象中。

收到消息后，我们可以检查作者以确保它不是机器人，我们可以检查消息内容以确保它与我们的命令匹配，然后我们可以使用消息的通道发送响应。

由于我们可以通过它们的Message对象从两个事件中完全操作，所以让我们将所有下游逻辑放在一个公共位置，以便两个事件侦听器都可以使用它：

```java
import discord4j.core.object.entity.Message;

public abstract class MessageListener {

    public Mono<Void> processCommand(Message eventMessage) {
        return Mono.just(eventMessage)
          .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
          .filter(message -> message.getContent().equalsIgnoreCase("!todo"))
          .flatMap(Message::getChannel)
          .flatMap(channel -> channel.createMessage("Things to do today:n - write a botn - eat lunchn - play a game"))
          .then();
    }
}
```

这里发生了很多事情，但这是命令和响应的最基本形式。这种方法使用[反应式功能设计](https://www.baeldung.com/reactor-core)，但可以使用block()以更传统的命令式方式编写它。

跨多个机器人命令进行扩展、调用不同的服务或数据存储库，甚至使用 Discord 角色作为某些命令的授权，这些都是良好机器人命令架构的常见部分。由于我们的侦听器是 Spring 管理的@Service，我们可以轻松地注入其他 Spring 管理的 bean 来处理这些任务。但是，我们不会在本文中解决任何问题。

### 4.2. 事件监听器<MessageCreateEvent>

要接收来自用户的新消息，我们必须监听MessageCreateEvent。由于命令处理逻辑已经存在于MessageListener中，我们可以扩展它以继承该功能。此外，我们需要实现我们的EventListener接口以符合我们的注册设计：

```java
@Service
public class MessageCreateListener extends MessageListener implements EventListener<MessageCreateEvent> {

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return processCommand(event.getMessage());
    }
}
```

通过继承，消息被传递到我们的processCommand()方法，所有验证和响应都在该方法中发生。

此时，我们的机器人将接收并响应“!todo”命令。但是，如果用户更正他们输入错误的命令，机器人将不会响应。让我们用另一个事件监听器来支持这个用例。

### 4.3. EventListener<MessageUpdateEvent>

MessageUpdateEvent在用户编辑消息时发出。我们可以侦听此事件以识别命令，就像我们侦听MessageCreateEvent的方式一样。

出于我们的目的，如果消息内容发生更改，我们只关心此事件。我们可以忽略此事件的其他实例。幸运的是，我们可以使用isContentChanged()方法来过滤掉此类实例：

```java
@Service
public class MessageUpdateListener extends MessageListener implements EventListener<MessageUpdateEvent> {
    
    @Override
    public Class<MessageUpdateEvent> getEventType() {
        return MessageUpdateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageUpdateEvent event) {
        return Mono.just(event)
          .filter(MessageUpdateEvent::isContentChanged)
          .flatMap(MessageUpdateEvent::getMessage)
          .flatMap(super::processCommand);
    }
}
```

在这种情况下，由于getMessage()返回Mono<Message>而不是原始Message，我们需要使用flatMap()将其发送到我们的超类。

## 5. 在 Discord 中测试机器人

现在我们有了一个正常运行的 Discord 机器人，我们可以将它邀请到 Discord 服务器并对其进行测试。

要创建邀请链接，我们必须指定机器人需要哪些权限才能正常运行。流行的第三方[Discord 权限计算器](https://discordapi.com/permissions.html)通常用于生成具有所需权限的邀请链接。虽然不推荐用于生产，但我们可以简单地选择“管理员”进行测试，而不用担心其他权限。只需为我们的机器人提供客户端 ID(在 Discord 开发者门户中找到)并使用生成的链接邀请我们的机器人到[服务器](https://support.discord.com/hc/en-us/articles/204849977-How-do-I-create-a-server-)。

如果我们不授予机器人管理员权限，我们可能需要调整频道权限，以便机器人可以在频道中读写。

机器人现在响应消息“！todo”，当消息被编辑为“！todo”时：

[![BaeldungBotCommand](https://www.baeldung.com/wp-content/uploads/2020/11/BaeldungBotCommand.png)](https://www.baeldung.com/wp-content/uploads/2020/11/BaeldungBotCommand.png)

## 六、概述

本教程描述了使用 Discord4J 库和Spring Boot创建 Discord 机器人的所有必要步骤。最后，它描述了如何为机器人设置一个基本的可扩展命令和响应结构。

要获得完整的工作机器人，请[在 GitHub 上](https://github.com/eugenp/tutorials/tree/master/discord4j)查看源代码。运行它需要一个有效的机器人令牌。