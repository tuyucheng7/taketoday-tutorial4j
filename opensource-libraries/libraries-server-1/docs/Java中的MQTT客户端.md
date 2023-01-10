## 1. 概述

在本教程中，我们将了解如何使用[Eclipse Paho 项目](https://www.eclipse.org/paho/)提供的库在Java项目中添加 MQTT 消息传递。

## 2. MQTT 入门

MQTT(MQ 遥测传输)是一种消息传递协议，旨在满足对一种简单轻量级方法的需求，以便将数据传输到低功率设备或从低功率设备传输数据，例如工业应用中使用的设备。

随着 IoT(物联网)设备的日益普及，MQTT 的使用越来越多，导致 OASIS 和 ISO 对其进行了标准化。

该协议支持单一消息传递模式，即发布-订阅模式：客户端发送的每条消息都包含一个关联的“主题”，代理使用该主题将其路由到订阅的客户端。主题名称可以是简单的字符串，如“ oiltemp ”或类似路径的字符串“ motor/1/rpm ”。

为了接收消息，客户端使用其确切名称或包含支持的通配符之一的字符串(“#”表示多级主题，“+”表示单级)订阅一个或多个主题。

## 3.项目设置

为了在 Maven 项目中包含 Paho 库，我们必须添加以下依赖项：

```xml
<dependency>
  <groupId>org.eclipse.paho</groupId>
  <artifactId>org.eclipse.paho.client.mqttv3</artifactId>
  <version>1.2.0</version>
</dependency>
```

 可以从 Maven Central 下载最新版本的[Eclipse PahoJava库模块。](https://search.maven.org/classic/#search|gav|1|g%3A"org.eclipse.paho" AND a%3A"org.eclipse.paho.client.mqttv3")

## 4.客户端设置

使用 Paho 库时，为了从 MQTT 代理发送和/或接收消息，我们需要做的第一件事是获取IMqttClient接口的实现。 该接口包含应用程序建立与服务器的连接、发送和接收消息所需的所有方法。

Paho 开箱即用，带有此接口的两个实现，一个异步实现 ( MqttAsyncClient ) 和一个同步实现 ( MqttClient )。 在我们的例子中，我们将关注同步版本，它具有更简单的语义。

设置本身是一个两步过程：我们首先创建 MqttClient类的实例，然后将其连接到我们的服务器。以下小节详细介绍了这些步骤。

### 4.1. 创建一个新的IMqttClient实例

下面的代码片段展示了如何创建一个新的 IMqttClient同步实例：

```java
String publisherId = UUID.randomUUID().toString();
IMqttClient publisher = new MqttClient("tcp://iot.eclipse.org:1883",publisherId);
```

在这种情况下，我们使用最简单的可用构造函数，它采用我们的 MQTT 代理的端点地址和一个客户端标识符，它唯一地标识我们的客户端。

在我们的例子中，我们使用了一个随机的 UUID，因此每次运行都会生成一个新的客户端标识符。

Paho 还提供了额外的构造函数，我们可以使用这些构造函数来自定义用于存储未确认消息的持久性机制和/或 用于运行协议引擎实现所需的后台任务的ScheduledExecutorService 。

我们使用的服务器端点是由 Paho 项目托管的公共 MQTT 代理，它允许任何具有互联网连接的人测试客户端而无需任何身份验证。

### 4.2. 连接到服务器

我们新创建 的 MqttClient实例没有连接到服务器。我们通过调用它的 connect()方法来实现，可选择传递一个 MqttConnectOptions 实例，该实例允许我们自定义协议的某些方面。

特别是，我们可以使用这些选项来传递附加信息，例如安全凭证、会话恢复模式、重新连接模式等。

MqttConnectionOptions 类将这些选项公开为我们可以使用普通设置方法设置的简单属性。我们只需要设置场景所需的属性——其余的将采用默认值。

用于与服务器建立连接的代码通常如下所示：

```java
MqttConnectOptions options = new MqttConnectOptions();
options.setAutomaticReconnect(true);
options.setCleanSession(true);
options.setConnectionTimeout(10);
publisher.connect(options);
```

在这里，我们定义了连接选项，以便：

-   发生网络故障时，库会自动尝试重新连接服务器
-   它将丢弃上一次运行未发送的消息
-   连接超时设置为 10 秒

## 5. 发送消息

使用已连接的 MqttClient发送消息非常简单。我们使用publish()方法变体之一将始终为字节数组的有效负载发送到给定主题，使用以下服务质量选项之一：

-   0 – “至多一次”语义，也称为“即发即弃”。当可以接受消息丢失时使用此选项，因为它不需要任何类型的确认或持久性
-   1 – “至少一次”语义。当不能接受消息丢失 并且的订阅者可以处理重复消息时使用此选项
-   2 – “恰好一次”语义。当不能接受消息丢失 并且的订阅者无法处理重复消息时使用此选项

在我们的示例项目中， EngineTemperatureSensor 类扮演模拟传感器的角色，每次我们调用它的 call() 方法时都会生成一个新的温度读数。

此类实现了 Callable 接口，因此我们可以轻松地将它与java.util.concurrent包中可用的ExecutorService实现 之一一起使用：

```java
public class EngineTemperatureSensor implements Callable<Void> {

    // ... private members omitted
    
    public EngineTemperatureSensor(IMqttClient client) {
        this.client = client;
    }

    @Override
    public Void call() throws Exception {        
        if ( !client.isConnected()) {
            return null;
        }           
        MqttMessage msg = readEngineTemp();
        msg.setQos(0);
        msg.setRetained(true);
        client.publish(TOPIC,msg);        
        return null;        
    }

    private MqttMessage readEngineTemp() {             
        double temp =  80 + rnd.nextDouble()  20.0;        
        byte[] payload = String.format("T:%04.2f",temp)
          .getBytes();        
        return new MqttMessage(payload);           
    }
}
```

MqttMessage 封装了有效负载本身、请求的服务质量以及消息的 保留标志。此标志向代理指示它应保留此消息，直到被订阅者使用为止。

我们可以使用这个特性来实现“最后一次正确”的行为，这样当一个新的订阅者连接到服务器时，它会立即收到保留的消息。

## 6.接收消息

为了从 MQTT 代理接收消息，我们需要使用 subscribe()方法变体之一，它允许我们指定：

-   我们希望接收的消息的一个或多个主题过滤器
-   相关的 QoS
-   处理接收到的消息的回调处理器

在以下示例中，我们展示了如何将消息侦听器添加到现有 IMqttClient实例以接收来自给定主题的消息。我们使用 CountDownLatch 作为我们的回调和主执行线程之间的同步机制，每次新消息到达时递减它。

在示例代码中，我们使用了不同的IMqttClient实例来接收消息。我们这样做只是为了更清楚哪个客户端做什么，但这不是 Paho 的限制——如果你愿意，你可以使用相同的客户端来发布和接收消息：

```java
CountDownLatch receivedSignal = new CountDownLatch(10);
subscriber.subscribe(EngineTemperatureSensor.TOPIC, (topic, msg) -> {
    byte[] payload = msg.getPayload();
    // ... payload handling omitted
    receivedSignal.countDown();
});    
receivedSignal.await(1, TimeUnit.MINUTES);
```

上面使用的subscribe()变体将IMqttMessageListener实例作为其第二个参数。

在我们的例子中，我们使用一个简单的 lambda 函数来处理有效负载并递减一个计数器。如果在指定的时间窗口(1 分钟)内没有足够的消息到达，则 await()方法将抛出异常。

使用 Paho 时，我们不需要明确确认消息接收。如果回调正常返回，Paho 认为消费成功并向服务器发送确认。

如果回调抛出Exception，客户端将被关闭。 请注意，这将导致丢失 QoS 级别为 0 的任何消息。

一旦客户端重新连接并再次订阅主题，以 QoS 级别 1 或 2 发送的消息将由服务器重新发送。

## 七. 总结

在本文中，我们演示了如何使用 Eclipse Paho 项目提供的库在我们的Java应用程序中添加对 MQTT 协议的支持。

该库处理所有低级协议细节，使我们能够专注于我们解决方案的其他方面，同时留出良好的空间来自定义其内部功能的重要方面，例如消息持久性。