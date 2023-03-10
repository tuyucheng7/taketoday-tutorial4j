## 1. 简介

在本文中，我们将重点介绍如何将Akka与Spring框架集成，以允许将基于Spring的服务注入到Akka actors中。

在阅读本文之前，建议先了解Akka的基础知识。

### 延伸阅读

### [Java Akka Actor简介](https://www.baeldung.com/akka-actors-java)

了解如何使用Java中的Akka Actors构建并发和分布式应用程序。

[阅读更多](https://www.baeldung.com/akka-actors-java)→

### [Akka Streams指南](https://www.baeldung.com/akka-streams)

使用Akka Streams库在Java中进行数据流转换的快速实用指南。

[阅读更多](https://www.baeldung.com/akka-streams)→

## 2. Akka中的依赖注入

[Akka](http://akka.io/)是一个强大的基于Actor并发模型的应用程序框架。该框架是用Scala编写的，这当然使其也可以在基于Java的应用程序中完全使用。因此，我们经常希望将Akka与现有的基于Spring的应用程序集成，或者简单地使用Spring将bean连接到actor中。

Spring/Akka 集成的问题在于Spring中bean的管理与Akka中actor的管理之间的差异：actor 具有不同于典型的Springbean lifecycle 的特定生命周期。

此外，actor 被分成一个actor本身(这是一个内部实现细节，不能由Spring管理)和一个actor引用，它可以被客户端代码访问，并且可以在不同的Akka运行时之间序列化和移植。

幸运的是，Akka 提供了一种机制，即[Akka 扩展](http://doc.akka.io/docs/akka/current/java/extending-akka.html)，这使得使用外部依赖注入框架成为一项相当容易的任务。

## 3.Maven依赖

为了在我们的Spring项目中演示Akka的用法，我们需要一个最低限度的Spring依赖项——spring- context库，以及akka-actor库。可以将库版本提取到pom的<properties>部分：

```xml
<properties>
    <spring.version>4.3.1.RELEASE</spring.version>
    <akka.version>2.4.8</akka.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>${spring.version}</version>
    </dependency>

    <dependency>
        <groupId>com.typesafe.akka</groupId>
        <artifactId>akka-actor_2.11</artifactId>
        <version>${akka.version}</version>
    </dependency>

</dependencies>
```

确保检查 Maven Central 以获取最新版本的[spring-context](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework" AND a%3A"spring-context")和[akka-actor](https://search.maven.org/classic/#search|gav|1|g%3A"com.typesafe.akka" AND a%3A"akka-actor_2.11")依赖项。

请注意，akka-actor依赖项的名称中有一个_2.11后缀，这表示此版本的Akka框架是针对 Scala 2.11 版构建的。相应版本的 Scala 库将传递包含在你的构建中。

## 4. 将SpringBeans 注入AkkaActor

让我们创建一个简单的 Spring/Akka 应用程序，该应用程序由一个actor组成，它可以通过向这个人发出问候来回答这个人的名字。问候语的逻辑将被提取到一个单独的服务中。我们希望将此服务自动装配到一个actor实例。Spring Integration 将帮助我们完成这项任务。

### 4.1. 定义参与者和服务

为了演示将服务注入演员，我们将创建一个简单的类GreetingActor，定义为无类型演员(扩展Akka的UntypedActor基类)。每个Akkaactor 的主要方法是onReceive方法，它接收消息并根据一些指定的逻辑处理它。

在我们的例子中，GreetingActor实现检查消息是否属于预定义类型Greet，然后从Greet实例中获取此人的姓名，然后使用GreetingService接收此人的问候语并使用收到的问候语字符串回答发件人。如果消息是某种其他未知类型，则将其传递给参与者的预定义未处理方法。

我们来看一下：

```java
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GreetingActor extends UntypedActor {

    private GreetingService greetingService;

    // constructor

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof Greet) {
            String name = ((Greet) message).getName();
            getSender().tell(greetingService.greet(name), getSelf());
        } else {
            unhandled(message);
        }
    }

    public static class Greet {

        private String name;

        // standard constructors/getters

    }
}
```

请注意，Greet消息类型被定义为该actor内部的静态内部类，这被认为是一种很好的做法。接受的消息类型应定义为尽可能接近参与者，以避免混淆该参与者可以处理的消息类型。

另请注意Spring注解@Component和@Scope——它们将类定义为具有原型作用域的Spring管理的 bean。

作用域非常重要，因为每个bean检索请求都应该产生一个新创建的实例，因为这种行为与Akka的actor生命周期相匹配。如果你使用其他范围实现此 bean，则Akka中重启actor的典型情况很可能无法正常运行。

最后，请注意我们不必显式地@Autowire GreetingService实例——这是可能的，因为Spring4.3 的新特性称为隐式构造函数注入。

GreeterService的实现非常简单，请注意我们通过向其添加@Component注解将其定义为Spring管理的 bean(具有默认的单例范围)：

```java
@Component
public class GreetingService {

    public String greet(String name) {
        return "Hello, " + name;
    }
}
```

### 4.2. 通过Akka扩展添加Spring支持

将Spring与Akka集成的最简单方法是通过Akka扩展。

扩展是为每个参与者系统创建的单例实例。它由一个扩展类本身组成，它实现了标记接口Extension和一个通常继承AbstractExtensionId的扩展 id 类。

由于这两个类紧密耦合，因此实现嵌套在 ExtensionId 类中的Extension类是有意义的：

```java
public class SpringExtension 
  extends AbstractExtensionId<SpringExtension.SpringExt> {

    public static final SpringExtension SPRING_EXTENSION_PROVIDER 
      = new SpringExtension();

    @Override
    public SpringExt createExtension(ExtendedActorSystem system) {
        return new SpringExt();
    }

    public static class SpringExt implements Extension {
        private volatile ApplicationContext applicationContext;

        public void initialize(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        public Props props(String actorBeanName) {
            return Props.create(
              SpringActorProducer.class, applicationContext, actorBeanName);
        }
    }
}
```

首先——SpringExtension从AbstractExtensionId类实现了一个单独的createExtension方法——它负责创建一个扩展实例，即SpringExt对象。

SpringExtension类还有一个静态字段SPRING_EXTENSION_PROVIDER ，它包含对其唯一实例的引用。添加私有构造函数以显式声明SpringExtention应该是单例类通常是有意义的，但为了清楚起见，我们将省略它。

其次，静态内部类SpringExt本身就是扩展。由于Extension只是一个标记接口，我们可以根据需要定义此类的内容。

在我们的例子中，我们将需要initialize方法来保存SpringApplicationContext实例——这个方法在每次扩展的初始化中只会被调用一次。

我们还需要props方法来创建Props对象。Props实例是演员的蓝图，在我们的例子中，Props.create方法接收一个SpringActorProducer类和该类的构造函数参数。这些是将调用此类的构造函数的参数。

每次我们需要Spring管理的actor引用时，都会执行props方法。

第三块也是最后一块拼图是SpringActorProducer类。它实现了Akka的IndirectActorProducer接口，该接口允许通过实现produce和actorClass方法来覆盖actor的实例化过程。

正如你可能已经猜到的那样，它不会直接实例化，而是始终从Spring的ApplicationContext中检索一个actor实例。由于我们已经将actor设为一个prototype作用域的 bean，每次调用produce方法都会返回一个新的actor实例：

```java
public class SpringActorProducer implements IndirectActorProducer {

    private ApplicationContext applicationContext;

    private String beanActorName;

    public SpringActorProducer(ApplicationContext applicationContext, 
      String beanActorName) {
        this.applicationContext = applicationContext;
        this.beanActorName = beanActorName;
    }

    @Override
    public Actor produce() {
        return (Actor) applicationContext.getBean(beanActorName);
    }

    @Override
    public Class<? extends Actor> actorClass() {
        return (Class<? extends Actor>) applicationContext
          .getType(beanActorName);
    }
}
```

### 4.3. 把它们放在一起

剩下要做的唯一一件事就是创建一个Spring配置类(标有@Configuration注解)，它将告诉Spring扫描当前包以及所有嵌套包(这由@ComponentScan注解确保)并创建一个Spring容器.

我们只需要添加一个额外的 bean—— ActorSystem实例——并在这个ActorSystem上初始化Spring扩展：

```java
@Configuration
@ComponentScan
public class AppConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public ActorSystem actorSystem() {
        ActorSystem system = ActorSystem.create("akka-spring-demo");
        SPRING_EXTENSION_PROVIDER.get(system)
          .initialize(applicationContext);
        return system;
    }
}
```

### 4.4. 检索 Spring-Wired Actor

为了测试一切正常，我们可以将ActorSystem实例注入我们的代码(一些Spring管理的应用程序代码，或基于Spring的测试)，使用我们的扩展为actor创建一个Props对象，检索对actor的引用通过Props对象并尝试向某人打招呼：

```java
ActorRef greeter = system.actorOf(SPRING_EXTENSION_PROVIDER.get(system)
  .props("greetingActor"), "greeter");

FiniteDuration duration = FiniteDuration.create(1, TimeUnit.SECONDS);
Timeout timeout = Timeout.durationToTimeout(duration);

Future<Object> result = ask(greeter, new Greet("John"), timeout);

Assert.assertEquals("Hello, John", Await.result(result, duration));
```

这里我们使用返回 Scala 的Future实例的典型akka.pattern.Patterns.ask模式。计算完成后，Future将使用我们在GreetingActor.onMessasge方法中返回的值进行解析。

我们可以通过将 Scala 的Await.result方法应用于Future来等待结果，或者更优选地，使用异步模式构建整个应用程序。

## 5.总结

在本文中，我们展示了如何将SpringFramework 与Akka集成，以及如何将bean自动装配到actor中。