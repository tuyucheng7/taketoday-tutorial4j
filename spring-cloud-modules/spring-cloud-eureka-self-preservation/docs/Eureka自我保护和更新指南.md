## 1. 概述

在本教程中，我们将学习[Eureka](https://www.baeldung.com/spring-cloud-netflix-eureka)自我保护和更新。

我们将从创建一个Eureka服务器和多个Eureka客户端实例开始。

然后，我们将向我们的Eureka服务器注册这些客户端，以展示自我保护的工作原理。

## 2. Eureka自我保护

在讨论自我保护之前，让我们先了解一下Eureka服务端是如何维护客户端实例注册表的。

**在启动期间，客户端触发与Eureka服务器的REST调用以自行注册到服务器的实例注册表**。当使用后发生正常关闭时，客户端会触发另一个REST调用，以便服务器可以清除与调用者相关的所有数据。

为了处理不正常的客户端关闭，服务器期望在特定时间间隔从客户端发送心跳。这称为更新。如果服务器在指定的持续时间内停止接收心跳，那么它将开始驱逐过时的实例。

**当心跳低于预期阈值时停止逐出实例的机制称为自我保护**。这可能发生在网络分区不佳的情况下，其中实例仍处于运行状态，但暂时无法访问，或者在客户端突然关闭的情况下。

当服务器激活自我保护模式时，它会暂停实例逐出，直到更新率回到预期阈值以上。

让我们看看实际效果。

## 3. 创建服务器

首先，通过使用@EnableEurekaServer标注我们的Spring Boot主类来创建Eureka服务器：

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

但是现在，让我们添加启动服务器的基本配置：

```properties
eureka.client.registerWithEureka=false
eureka.client.fetchRegistry=false
eureka.instance.hostname=localhost
```

由于我们不希望我们的Eureka服务器自行注册，因此我们将属性eureka.client.registerWithEureka设置为false。这里的属性eureka.instance.hostname=localhost特别重要，因为我们在本地机器上运行它。否则，我们最终可能会在Eureka服务器中创建一个不可用的副本-搞乱客户端的心跳计数。

现在让我们看看所有配置及其在下一节自我保护上下文中的相关性。

### 3.1 自我保护配置

默认情况下，Eureka服务器在启用自我保护的情况下运行。

然而，为了便于理解，让我们在服务器端浏览一下这些配置中的每一个。

-   eureka.server.enable-self-preservation：禁用自我保护的配置，默认值为true
-   eureka.server.expected-client-renewal-interval-seconds：服务器期望客户端心跳以使用此属性配置的时间间隔，默认值为30
-   eureka.instance.lease-expiration-duration-in-seconds：指示Eureka服务器在从其注册表中删除该客户端之前从客户端接收到最后一次心跳后等待的时间(以秒为单位)，默认值为90
-   eureka.server.eviction-interval-timer-in-ms：此属性告诉Eureka服务器以该频率运行作业以驱逐过期的客户端，默认值为60秒
-   eureka.server.renewal-percent-threshold：基于此属性，服务器计算所有已注册客户端每分钟的预期心跳，默认值为0.85
-   eureka.server.renewal-threshold-update-interval-ms：此属性告诉Eureka服务器以该频率运行作业，以计算此时来自所有注册客户端的预期心跳，默认值为15分钟

在大多数情况下，默认配置就足够了。但是对于特定的要求，我们可能需要更改这些配置。**在这些情况下需要格外小心，以避免意外后果，例如错误的更新阈值计算或延迟的自我保护模式激活**。

## 4. 注册客户端

现在，让我们创建一个Eureka客户端并启动六个实例：

```java
@SpringBootApplication
@EnableEurekaClient
public class EurekaClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientApplication.class, args);
    }
}
```

以下是客户端的配置：

```properties
spring.application.name=Eurekaclient
server.port=${PORT:0}
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka
eureka.instance.preferIpAddress=true
eureka.instance.lease-renewal-interval-in-seconds=30
```

此配置允许我们使用PORT程序参数启动同一客户端的多个实例。配置eureka.instance.lease-renewal-interval-in-seconds表示客户端向服务端发送心跳的间隔时间。默认值为30秒，即客户端每30秒发送一次心跳。

现在让我们启动这6个端口号从8081到8086的客户端实例，并导航到[http://localhost:8761](http://localhost:8761/)以检查这些实例是否已注册到Eureka服务器。

<img src="../assets/img.png">

从截图中我们可以看到，我们的Eureka服务器有6个已注册的客户端实例，总的续订阈值为11。阈值的计算基于三个因素：

-   已注册客户端实例总数：6
-   配置的客户端更新间隔：30秒
-   配置的更新百分比阈值：0.85

考虑到所有这些因素，在我们的例子中，阈值是11。

## 5. 测试自我保护

为了模拟临时网络问题，让我们在客户端将属性eureka.client.should-unregister-on-shutdown设置为false并停止我们的客户端实例之一。**由于我们将should-unregister-on-shutdown标志设置为false，因此客户端不会调用取消注册调用并且服务器假定这是一个不正常的关闭**。

现在让我们等待90秒，由我们的eureka.instance.lease-expiration-duration-in-seconds属性设置，然后再次导航到[http://localhost:8761](http://localhost:8761/)。红色粗体文本表示Eureka服务器现在处于自我保护模式并停止逐出实例。

现在让我们检查已注册的实例部分，看看已停止的实例是否仍然可用。正如我们所看到的，它是可用的，但状态为DOWN：

<img src="../assets/img_1.png">

**服务器退出自我保护模式的唯一方法是启动已停止的实例或禁用自我保护本身**。如果我们通过将标志eureka.server.enable-self-preservation设置为false来重复相同的步骤，那么Eureka服务器将在配置的租约到期持续时间属性后从注册表中驱逐已停止的实例。

## 6. 总结

在本教程中，我们了解了Eureka自我保护的工作原理以及我们如何配置与自我保护相关的不同选项。