## 1. 简介

[Akka](https://akka.io/)是一个开源库，可通过利用 Actor 模型帮助使用Java或 Scala 轻松开发并发和分布式应用程序。

在本教程中，我们将介绍基本功能，例如定义演员、他们如何交流以及我们如何杀死他们。在最后的说明中，我们还将说明使用 Akka 时的一些最佳实践。

## 2. 演员模型

Actor 模型对计算机科学界来说并不陌生。它于 1973 年由 Carl Eddie Hewitt 首次提出，作为处理并发计算的理论模型。

当软件行业开始意识到实施并发和分布式应用程序的陷阱时，它开始显示其实际适用性。

一个参与者代表一个独立的计算单元。一些重要的特征是：

-   参与者封装其状态和部分应用程序逻辑
-   参与者仅通过异步消息进行交互，从不通过直接方法调用
-   每个演员都有一个唯一的地址和一个邮箱，其他演员可以在其中传递消息
-   actor 将按顺序处理邮箱中的所有消息(邮箱的默认实现是 FIFO 队列)
-   参与者系统以树状层次结构组织
-   一个演员可以创建其他演员，可以向任何其他演员发送消息并停止自己或任何演员已创建

### 2.1. 优点

开发并发应用程序很困难，因为我们需要处理同步、锁和共享内存。通过使用 Akka actor，我们可以轻松编写异步代码，而无需锁和同步。

使用消息而不是方法调用的优点之一是发送方线程在向另一个参与者发送消息时不会阻塞等待返回值。接收方将通过向发送方发送回复消息来响应结果。

使用消息的另一大好处是我们不必担心多线程环境中的同步问题。这是因为所有消息都是按顺序处理的。

Akka actor 模型的另一个优点是错误处理。通过在层次结构中组织参与者，每个参与者都可以将失败通知其父级，因此它可以采取相应的行动。父 actor 可以决定停止或重新启动子 actor。

## 3.设置

为了利用 Akka actor，我们需要从[Maven Central](https://search.maven.org/classic/#search|ga|1|a%3A"akka-actor_2.12")添加以下依赖项：

```xml
<dependency>
    <groupId>com.typesafe.akka</groupId>
    <artifactId>akka-actor_2.12</artifactId>
    <version>2.5.11</version>
</dependency>

```

## 4.创建演员

如前所述，参与者是在层次结构系统中定义的。共享公共配置的所有参与者都将由ActorSystem 定义。

现在，我们将简单地定义一个具有默认配置和自定义名称的ActorSystem ：

```java
ActorSystem system = ActorSystem.create("test-system");

```

即使我们还没有创建任何演员，系统已经包含 3 个主要演员：

-   具有地址“/”的根监护人演员，正如名称所述，代表演员系统层次结构的根
-   地址为“/user”的用户监护人。这将是我们定义的所有演员的父母
-   地址为“/system”的系统监护人角色。这将是 Akka 系统内部定义的所有参与者的父级

任何 Akka actor 都将扩展AbstractActor抽象类并实现createReceive()方法来处理来自其他 actor 的传入消息：

```java
public class MyActor extends AbstractActor {
    public Receive createReceive() {
        return receiveBuilder().build();
    }
}
```

这是我们可以创建的最基本的 actor。它可以接收来自其他参与者的消息并将丢弃它们，因为ReceiveBuilder 中没有定义匹配的消息模式。我们将在本文后面讨论消息模式匹配。

现在我们已经创建了第一个 actor，我们应该将它包含在ActorSystem中：

```java
ActorRef readingActorRef 
  = system.actorOf(Props.create(MyActor.class), "my-actor");
```

### 4.1. 演员配置

Props类包含演员配置。我们可以配置诸如调度程序、邮箱或部署配置之类的东西。这个类是不可变的，因此是线程安全的，所以它可以在创建新角色时共享。

强烈建议并认为最佳实践是在 actor 对象中定义工厂方法，以处理Props对象的创建。

举例来说，让我们定义一个将进行一些文本处理的角色。actor 将收到一个String对象，它将对其进行处理：

```java
public class ReadingActor extends AbstractActor {
    private String text;

    public static Props props(String text) {
        return Props.create(ReadingActor.class, text);
    }
    // ...
}
```

现在，要创建这种类型的 actor 的实例，我们只需使用props()工厂方法将String参数传递给构造函数：

```java
ActorRef readingActorRef = system.actorOf(
  ReadingActor.props(TEXT), "readingActor");
```

现在我们知道了如何定义演员，让我们看看他们如何在演员系统内部进行交流。

## 5. 演员消息

为了相互交互，参与者可以发送和接收来自系统中任何其他参与者的消息。这些消息可以是任何类型的对象，条件是它是不可变的。

在 actor 类中定义消息是最佳实践。这有助于编写易于理解的代码，并了解参与者可以处理哪些消息。

### 5.1. 发送消息

在 Akka actor 系统中，消息是使用以下方法发送的：

-   告诉()
-   问()
-   向前()

当我们想要发送消息但不希望收到响应时，我们可以使用tell()方法。从性能的角度来看，这是最有效的方法：

```java
readingActorRef.tell(new ReadingActor.ReadLines(), ActorRef.noSender());

```

第一个参数表示我们发送到 actor 地址readingActorRef的消息。

第二个参数指定发件人是谁。当接收消息的 actor 需要向发送者以外的其他 actor(例如发送 actor 的父级)发送响应时，这很有用。

通常，我们可以将第二个参数设置为null或ActorRef.noSender()，因为我们不需要回复。当我们需要 actor 返回响应时，我们可以使用ask()方法：

```java
CompletableFuture<Object> future = ask(wordCounterActorRef, 
  new WordCounterActor.CountWords(line), 1000).toCompletableFuture();
```

当请求参与者的响应时，会返回一个CompletionStage对象，因此处理过程保持非阻塞。

我们必须注意的一个非常重要的事实是将响应的参与者内部的错误处理。要返回将包含异常的Future对象，我们必须向发送方参与者发送Status.Failure消息。

当 actor 在处理消息时抛出异常并且ask()调用将超时并且不会在日志中看到对异常的引用时，这不会自动完成：

```java
@Override
public Receive createReceive() {
    return receiveBuilder()
      .match(CountWords.class, r -> {
          try {
              int numberOfWords = countWordsFromLine(r.line);
              getSender().tell(numberOfWords, getSelf());
          } catch (Exception ex) {
              getSender().tell(
               new akka.actor.Status.Failure(ex), getSelf());
               throw ex;
          }
    }).build();
}
```

我们还有类似于tell()的forward()方法。不同的是，发送消息时保留了消息的原始发送者，因此转发消息的actor只充当中介actor：

```java
printerActorRef.forward(
  new PrinterActor.PrintFinalResult(totalNumberOfWords), getContext());
```

### 5.2. 接收消息

每个参与者都将实现createReceive()方法，该方法处理所有传入的消息。receiveBuilder()就像一个 switch 语句，试图将接收到的消息与定义的消息类型相匹配：

```java
public Receive createReceive() {
    return receiveBuilder().matchEquals("printit", p -> {
        System.out.println("The address of this actor is: " + getSelf());
    }).build();
}
```

收到消息后，会将消息放入 FIFO 队列，因此消息会按顺序处理。

## 6. 杀死演员

当我们结束使用一个 actor 时，我们可以通过从ActorRefFactory接口调用stop()方法来停止它：

```java
system.stop(myActorRef);
```

我们可以使用此方法终止任何子 actor 或 actor 本身。重要的是要注意停止是异步完成的，并且当前消息处理将在 actor 终止之前完成。演员邮箱将不再接受传入消息。

通过停止一个父 actor，我们也会向它产生的所有子 actor 发送一个 kill 信号。

当我们不再需要 actor 系统时，我们可以终止它以释放所有资源并防止任何内存泄漏：

```java
Future<Terminated> terminateResponse = system.terminate();
```

这将停止系统守护者 actor，从而停止此 Akka 系统中定义的所有 actor。

我们还可以向任何我们想要杀死的演员发送一条PoisonPill消息：

```java
myActorRef.tell(PoisonPill.getInstance(), ActorRef.noSender());
```

PoisonPill消息将像其他消息一样被actor接收并放入队列中。actor 将处理所有消息，直到到达PoisonPill one为止。只有这样演员才会开始终止过程。

另一个用于杀死 actor 的特殊消息是Kill消息。与PoisonPill 不同， actor在处理此消息时会抛出ActorKilledException ：

```java
myActorRef.tell(Kill.getInstance(), ActorRef.noSender());
```

## 七. 总结

在本文中，我们介绍了 Akka 框架的基础知识。我们展示了如何定义参与者、他们如何相互通信以及如何终止他们。

在使用 Akka 时，我们将总结一些最佳实践：

-   当关注性能时，使用tell()而不是ask()
-   使用ask()时，我们应该始终通过发送失败消息来处理异常
-   参与者不应该共享任何可变状态
-   不应在另一个演员中声明一个演员
-   actor不再被引用时不会自动停止。当我们不再需要它时，我们必须显式地销毁它以防止内存泄漏
-   参与者使用的消息应该始终是不可变的