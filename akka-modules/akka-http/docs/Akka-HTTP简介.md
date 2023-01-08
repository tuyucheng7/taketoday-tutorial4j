## 1. 概述

在本教程中，借助 Akka 的[Actor](https://www.baeldung.com/akka-actors-java)和[Stream](https://www.baeldung.com/akka-streams)模型，我们将学习如何设置 Akka 以创建提供基本 CRUD 操作的 HTTP API。

## 2.Maven依赖

首先，让我们看一下开始使用 Akka HTTP 所需的依赖项：

```xml
<dependency>
    <groupId>com.typesafe.akka</groupId>
    <artifactId>akka-http_2.12</artifactId>
    <version>10.0.11</version>
</dependency>
<dependency>
    <groupId>com.typesafe.akka</groupId>
    <artifactId>akka-stream_2.12</artifactId>
    <version>2.5.11</version>
</dependency>
<dependency>
    <groupId>com.typesafe.akka</groupId>
    <artifactId>akka-http-jackson_2.12</artifactId>
    <version>10.0.11</version>
</dependency>
<dependency>
    <groupId>com.typesafe.akka</groupId>
    <artifactId>akka-http-testkit_2.12</artifactId>
    <version>10.0.11</version>
    <scope>test</scope>
</dependency>
```

当然，我们可以在[Maven Central](https://search.maven.org/search?q=com.typesafe.akka)上找到这些 Akka 库的最新版本。

## 3.创建演员

例如，我们将构建一个允许我们管理用户资源的 HTTP API。API 将支持两种操作：

-   创建新用户
-   加载现有用户

在我们可以提供 HTTP API 之前，我们需要实现一个提供我们需要的操作的参与者：

```java
class UserActor extends AbstractActor {

  private UserService userService = new UserService();

  static Props props() {
    return Props.create(UserActor.class);
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
      .match(CreateUserMessage.class, handleCreateUser())
      .match(GetUserMessage.class, handleGetUser())
      .build();
  }

  private FI.UnitApply<CreateUserMessage> handleCreateUser() {
    return createUserMessage -> {
      userService.createUser(createUserMessage.getUser());
      sender()
        .tell(new ActionPerformed(
           String.format("User %s created.", createUserMessage.getUser().getName())), getSelf());
    };
  }

  private FI.UnitApply<GetUserMessage> handleGetUser() {
    return getUserMessage -> {
      sender().tell(userService.getUser(getUserMessage.getUserId()), getSelf());
    };
  }
}
```

基本上，我们扩展了AbstractActor类并实现了它的createReceive()方法。

在createReceive()中，我们将传入消息类型映射到处理相应类型消息的方法。

消息类型是简单的可序列化容器类，其中包含一些描述特定操作的字段。GetUserMessage并有一个字段userId 来标识要加载的用户。CreateUserMessage包含一个User对象，其中包含我们创建新用户所需的用户数据。

稍后，我们将看到如何将传入的 HTTP 请求转换为这些消息。

最终，我们将所有消息委托给UserService实例，该实例提供管理持久用户对象所需的业务逻辑。

另外，请注意 props()方法。虽然props() 方法对于扩展AbstractActor不是必需的，但稍后在创建ActorSystem时它会派上用场 。

有关演员的更深入讨论，请查看我们[对 Akka 演员的介绍](https://www.baeldung.com/akka-actors-java)。

## 4. 定义 HTTP 路由

有了一个为我们做实际工作的 actor，我们剩下要做的就是提供一个 HTTP API，将传入的 HTTP 请求委托给我们的 actor。

Akka 使用路由的概念来描述 HTTP API。对于每个操作，我们都需要一条路线。

要创建 HTTP 服务器，我们扩展框架类HttpApp并实现routes方法：

```java
class UserServer extends HttpApp {

  private final ActorRef userActor;

  Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));

  UserServer(ActorRef userActor) {
    this.userActor = userActor;
  }

  @Override
  public Route routes() {
    return path("users", this::postUser)
      .orElse(path(segment("users").slash(longSegment()), id -> route(getUser(id))));
  }

  private Route getUser(Long id) {
    return get(() -> {
      CompletionStage<Optional<User>> user = 
        PatternsCS.ask(userActor, new GetUserMessage(id), timeout)
          .thenApply(obj -> (Optional<User>) obj);

      return onSuccess(() -> user, performed -> {
        if (performed.isPresent())
          return complete(StatusCodes.OK, performed.get(), Jackson.marshaller());
        else
          return complete(StatusCodes.NOT_FOUND);
      });
    });
  }

  private Route postUser() {
    return route(post(() -> entity(Jackson.unmarshaller(User.class), user -> {
      CompletionStage<ActionPerformed> userCreated = 
        PatternsCS.ask(userActor, new CreateUserMessage(user), timeout)
          .thenApply(obj -> (ActionPerformed) obj);

      return onSuccess(() -> userCreated, performed -> {
        return complete(StatusCodes.CREATED, performed, Jackson.marshaller());
      });
    })));
  }
}

```

现在，这里有相当多的样板文件，但请注意，我们遵循与之前映射操作相同的模式，这次是路由。 让我们分解一下。

在getUser()中，我们只需将传入的用户 ID 包装在GetUserMessage类型的消息中，并将该消息转发给我们的userActor。

一旦参与者处理完消息，就会调用onSuccess处理程序，在其中我们通过发送具有特定 HTTP 状态和特定 JSON 主体的响应来完成HTTP 请求。我们使用[Jackson](https://www.baeldung.com/jackson-object-mapper-tutorial) marshaller 将参与者给出的答案序列化为 JSON 字符串。

在postUser()中，我们做的事情有点不同，因为我们期望 HTTP 请求中有一个 JSON 主体。我们使用entity()方法将传入的 JSON 主体映射到User对象，然后将其包装到CreateUserMessage并将其传递给我们的 actor。同样，我们使用 Jackson 在Java和 JSON 之间进行映射，反之亦然。

由于HttpApp希望我们提供单个Route对象，因此我们在routes方法中将两个路由合并为一个。在这里，我们使用path指令最终提供我们的 API 应该可用的 URL 路径。

我们将postUser()提供的路由绑定到路径 /users。如果传入请求不是 POST 请求，Akka 将自动进入orElse分支并期望路径为/users/<id>并且 HTTP 方法为 GET。

如果 HTTP 方法是 GET，请求将被转发到getUser() 路由。如果用户不存在，Akka 将返回 HTTP 状态 404(未找到)。如果该方法既不是 POST 也不是 GET，Akka 将返回 HTTP 状态 405(方法不允许)。

有关如何使用 Akka 定义 HTTP 路由的更多信息，请查看[Akka 文档](https://doc.akka.io/docs/akka-http/current/routing-dsl/routes.html)。

## 5.启动服务器

一旦我们像上面那样创建了一个HttpApp实现，我们就可以用几行代码启动我们的 HTTP 服务器：

```java
public static void main(String[] args) throws Exception {
  ActorSystem system = ActorSystem.create("userServer");
  ActorRef userActor = system.actorOf(UserActor.props(), "userActor");
  UserServer server = new UserServer(userActor);
  server.startServer("localhost", 8080, system);
}
```

我们只需创建一个ActorSystem ，其中包含一个类型为UserActor的单个 actor，并在localhost上启动服务器。

## 六. 总结

在本文中，我们通过一个示例了解了 Akka HTTP 的基础知识，该示例展示了如何设置 HTTP 服务器并公开端点以创建和加载资源，类似于 REST API。