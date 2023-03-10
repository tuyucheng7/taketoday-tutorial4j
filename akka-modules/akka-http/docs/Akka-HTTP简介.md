## 1. 概述

在本教程中，借助Akka的[Actor](https://www.baeldung.com/akka-actors-java)和[Stream](https://www.baeldung.com/akka-streams)模型，我们将学习如何设置Akka以创建提供基本CRUD操作的HTTP API。

## 2. Maven依赖

首先，让我们看一下开始使用Akka HTTP所需的依赖项：

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

当然，我们可以在[Maven Central](https://search.maven.org/search?q=com.typesafe.akka)上找到这些Akka库的最新版本。

## 3. 创建Actor

例如，我们将构建一个允许我们管理用户资源的HTTP API。该API将支持两种操作：

-   创建新用户
-   加载现有用户

在我们可以提供HTTP API之前，**我们需要实现一个提供我们需要的操作的actor**：

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
                  .tell(new ActionPerformed(String.format("User %s created.", createUserMessage.getUser().getName())), getSelf());
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

在createReceive()中，我们将**传入消息类型映射**到处理相应类型消息的方法。

**消息类型是简单的可序列化容器类，其中包含一些描述特定操作的字段**。GetUserMessage并有一个字段userId来标识要加载的用户。CreateUserMessage包含一个User对象，其中包含我们创建新用户所需的用户数据。

稍后，我们将看到如何将传入的HTTP请求转换为这些消息。

最终，我们将所有消息委托给UserService实例，该实例提供管理持久用户对象所需的业务逻辑。

另外，请注意props()方法。**虽然props()方法对于扩展AbstractActor不是必需的，但稍后在创建ActorSystem时它会派上用场**。

有关Actor的更深入讨论，请查看我们对[Akka Actor的介绍](https://www.baeldung.com/akka-actors-java)。

## 4. 定义HTTP路由

有了一个为我们做实际工作的actor，**我们剩下要做的就是提供一个HTTP API，将传入的HTTP请求委托给我们的actor**。

Akka使用路由的概念来描述HTTP API。**对于每个操作，我们都需要一个路由**。

要创建HTTP服务器，我们扩展框架类HttpApp并实现routes方法：

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

现在，这里有相当多的样板代码，但请注意，我们遵循与之前映射操作相同的模式，这次是路由。让我们稍微分解一下。

在getUser()中，我们只需将传入的用户ID包装在GetUserMessage类型的消息中，并将该消息转发给我们的userActor。

一旦actor处理完消息，就会调用onSuccess处理程序，在该处理程序中我们通过发送具有特定HTTP状态和特定JSON主体的响应来完成HTTP请求。我们使用[Jackson](https://www.baeldung.com/jackson-object-mapper-tutorial)编组器将actor给出的答案序列化为JSON字符串。

在postUser()中，我们做的事情有点不同，因为我们期望HTTP请求中有一个JSON主体。我们使用entity()方法将传入的JSON主体映射到User对象，然后将其包装到CreateUserMessage并将其传递给我们的actor。同样，我们使用Jackson在Java和JSON之间进行映射，反之亦然。

**由于HttpApp希望我们提供单个Route对象，因此我们在routes方法中将两个路由合并为一个**。在这里，我们使用path方法最终提供我们的API应该可用的URL路径。

我们将postUser()提供的路由绑定到路径/users。如果传入请求不是POST请求，Akka将自动进入orElse分支并期望路径为/users/<id\>并且HTTP方法为GET。

如果HTTP方法是GET，则请求将被转发到getUser()路由。如果用户不存在，Akka将返回HTTP状态404(Not Found)。如果该方法既不是POST也不是GET，Akka将返回HTTP状态405(Method Not Allowed)。

有关如何使用Akka定义HTTP路由的更多信息，请查看[Akka文档](https://doc.akka.io/docs/akka-http/current/routing-dsl/routes.html)。

## 5. 启动服务器

一旦我们像上面那样创建了一个HttpApp实现，我们就可以用几行代码启动我们的HTTP服务器：

```java
public static void main(String[] args) throws Exception {
    ActorSystem system = ActorSystem.create("userServer");
    ActorRef userActor = system.actorOf(UserActor.props(), "userActor");
    UserServer server = new UserServer(userActor);
    server.startServer("localhost", 8080, system);
}
```

**我们只需创建一个ActorSystem，其中包含一个类型为UserActor的单个actor，并在localhost上启动服务器**。

## 6. 总结

在本文中，我们通过一个示例了解了Akka HTTP的基础知识，该示例展示了如何设置HTTP服务器并公开端点以创建和加载资源，类似于REST API。