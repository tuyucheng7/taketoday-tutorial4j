## 1. 概述

在本教程中，我们将了解为 IoT 应用程序创建数据管道时的要求。

在此过程中，我们将了解物联网架构的特征，并了解如何利用 MQTT 代理、NiFi 和 InfluxDB 等不同工具为物联网应用程序构建高度可扩展的数据管道。

## 2.物联网及其架构

首先，让我们了解一些基本概念并了解 IoT 应用程序的一般架构。

### 2.1. 什么是物联网？

物联网(IoT) 泛指物理对象的网络，称为“物”。例如，事物可以包括从普通的家用物品(如灯泡)到复杂的工业设备的任何东西。通过这个网络，我们可以将各种传感器和执行器连接到互联网以交换数据：

[![物联网家庭自动化 1](https://www.baeldung.com/wp-content/uploads/2021/02/IoT-Home-Automation-1.jpg)](https://www.baeldung.com/wp-content/uploads/2021/02/IoT-Home-Automation-1.jpg)

现在，我们可以在非常不同的环境中部署东西——例如，环境可以是我们的家或其他完全不同的东西，比如移动的货运卡车。然而，我们不能真正对这些东西可用的电源和网络的质量做出任何假设。因此，这对物联网应用提出了独特的要求。

### 2.2. 物联网架构简介

典型的 IoT 架构通常将自身分为四个不同的层。让我们了解数据实际上是如何流经这些层的：

[![物联网架构层](https://www.baeldung.com/wp-content/uploads/2021/02/IoT-Architecture-Layers-1024x340.jpg)](https://www.baeldung.com/wp-content/uploads/2021/02/IoT-Architecture-Layers.jpg)

首先，传感层主要由从环境中收集测量值的传感器组成。然后，网络层帮助聚合原始数据并将其发送到 Internet 上进行处理。此外，数据处理层过滤原始数据并生成早期分析。最后，应用层利用强大的数据处理能力，对数据进行更深层次的分析和管理。

## 3. MQTT、NiFi、InfluxDB介绍

现在，让我们来看看我们今天在 IoT 设置中广泛使用的一些产品。这些都提供了一些独特的功能，使它们适合物联网应用程序的数据要求。

### 3.1. MQTT

[消息队列遥测传输 (MQTT)](https://mqtt.org/)是一种轻量级的发布-订阅网络协议。它现在是[OASIS](https://www.oasis-open.org/committees/tc_home.php?wg_abbrev=mqtt)和[ISO 标准](https://www.iso.org/standard/69466.html)。IBM 最初开发它是为了在设备之间传输消息。MQTT 适用于内存、网络带宽和电源稀缺的受限环境。

MQTT遵循客户端-服务器模型，其中不同的组件可以充当客户端并通过 TCP 连接到服务器。我们知道这个服务器是一个 MQTT 代理。客户端可以将消息发布到一个称为主题的地址。他们还可以订阅一个主题并接收发布到该主题的所有消息。

在典型的物联网设置中，传感器可以向 MQTT 代理发布温度等测量值，上游数据处理系统可以订阅这些主题以接收数据：

[![MQTT架构](https://www.baeldung.com/wp-content/uploads/2021/02/MQTT-Architecture.jpg)](https://www.baeldung.com/wp-content/uploads/2021/02/MQTT-Architecture.jpg)

正如我们所见，MQTT 中的主题是分层的。系统可以使用通配符轻松订阅整个主题层次结构。

MQTT支持三个级别的服务质量 (QoS)。这些是“最多交付一次”、“至少交付一次”和“恰好交付一次”。QoS 定义客户端和服务器之间的协议级别。每个客户都可以选择适合其环境的服务级别。

客户端还可以请求代理在发布时保留消息。在某些设置中，MQTT 代理可能需要来自客户端的用户名和密码身份验证才能连接。此外，为了隐私，可以使用 SSL/TLS 对 TCP 连接进行加密。

有多种 MQTT 代理实现和客户端库可供使用——例如，[HiveMQ](https://www.hivemq.com/)、[Mosquitto](https://mosquitto.org/)和[Paho MQTT](https://www.eclipse.org/paho/)。我们将在本教程的示例中使用 Mosquitto。Mosquitto 是 Eclipse Foundation 的一部分，我们可以轻松地将它安装在 Raspberry Pi 或 Arduino 等板上。

### 3.2. 阿帕奇尼菲

[Apache NiFi](https://nifi.apache.org/)最初由 NSA 开发为 NiagaraFiles。它促进了系统间数据流的自动化和管理，并且基于将应用程序定义为黑盒进程网络的[基于流的编程模型。](https://en.wikipedia.org/wiki/Flow-based_programming)

让我们先了解一些基本概念。在 NiFi 中通过系统移动的对象称为 FlowFile。FlowFile 处理器实际上执行有用的工作，如 FlowFiles 的路由、转换和中介。FlowFile 处理器与 Connections 相连。

Process Group 是一种将组件组合在一起以在 NiFi 中组织数据流的机制。进程组可以通过输入端口接收数据并通过输出端口发送数据。远程进程组 (RPG) 提供了一种向 NiFi 远程实例发送数据或从其接收数据的机制。

现在，有了这些知识，让我们来看看 NiFi 架构：

[![NiFi架构](https://www.baeldung.com/wp-content/uploads/2021/02/NiFi-Architecture.jpg)](https://www.baeldung.com/wp-content/uploads/2021/02/NiFi-Architecture.jpg)

NiFi 是一个基于 Java 的程序，它在 JVM 中运行多个组件。Web 服务器是托管命令和控制 API 的组件。Flow Controller 是 NiFi 的核心组件，它管理扩展接收资源执行的时间安排。扩展允许 NiFi 可扩展并支持与不同系统的集成。

NiFi 在 FlowFile 存储库中跟踪 FlowFile 的状态。FlowFile 的实际内容字节驻留在内容存储库中。最后，与 FlowFile 相关的出处事件数据驻留在出处存储库中。

由于在源头收集数据可能需要更小的占用空间和更低的资源消耗，因此 NiFi 有一个名为[MiNiFi](https://nifi.apache.org/minifi/index.html)的子项目。MiNiFi 为 NiFi 提供了一种互补的数据收集方法，并通过 Site-to-Site (S2S) 协议轻松与 NiFi 集成：

[![NiFi MiNiFi C2](https://www.baeldung.com/wp-content/uploads/2021/02/NiFi-MiNiFi-C2.jpg)](https://www.baeldung.com/wp-content/uploads/2021/02/NiFi-MiNiFi-C2.jpg)

[此外，它还可以通过MiNiFi 命令和控制 (C2)](https://github.com/apache/nifi-minifi/tree/master/minifi-c2)协议对代理进行集中管理。此外，它还有助于通过生成完整的监管信息链来确定数据来源。

### 3.3. Influx数据库

[InfluxDB](https://www.influxdata.com/)是由[InfluxData](https://www.influxdata.com/)开发的Go 语言编写的时序数据库。它专为快速且高可用性的时间序列数据存储和检索而设计。这特别适用于处理应用程序指标、物联网传感器数据和实时分析。

首先，InfluxDB 中的数据是按时间序列组织的。时间序列可以包含零个或多个点。一个点代表一个单一的数据记录，它有四个组成部分——测量、标签集、字段集和时间戳：

[![InfluxDB 点](https://www.baeldung.com/wp-content/uploads/2021/02/InfluxDB-Point.jpg)](https://www.baeldung.com/wp-content/uploads/2021/02/InfluxDB-Point.jpg)

首先，时间戳显示与特定点关联的 UTC 日期和时间。字段集由一个或多个字段键和字段值对组成。他们捕获带有点标签的实际数据。同样，tag-set 由 tag-key 和 tag-value 对组成，但它们是可选的。它们基本上充当一个点的元数据，可以被索引以加快查询响应。

测量充当标记集、字段集和时间戳的容器。此外，InfluxDB 中的每个点都可以有一个与之关联的保留策略。保留策略描述了 InfluxDB 将保留数据多长时间以及它将通过复制创建多少副本。

最后，数据库充当用户、保留策略、连续查询和时间序列数据的逻辑容器。我们可以将 InfluxDB 中的数据库理解为与传统的关系数据库大体相似。

此外，InfluxDB 是 InfluxData 平台的一部分，该平台提供其他几种产品来有效处理时间序列数据。InfluxData 现在将其作为开源平台 InfluxDB OSS 2.0 和商业产品 InfluxDB Cloud 提供：

[![InfluxDB 平台 2](https://www.baeldung.com/wp-content/uploads/2021/02/InfluxDB-Platform-2.jpg)](https://www.baeldung.com/wp-content/uploads/2021/02/InfluxDB-Platform-2.jpg)

除了 InfluxDB，该平台还包括[Chronograf](https://www.influxdata.com/time-series-platform/chronograf/)，它为 InfluxData 平台提供了完整的接口。此外，它还包括[Telegraf](https://www.influxdata.com/time-series-platform/telegraf/)，一个用于收集和报告指标和事件的代理。最后是实时流数据处理引擎[Kapacitor 。](https://www.influxdata.com/time-series-platform/kapacitor/)

## 4. 物联网数据管道实践

现在，我们已经涵盖了足够的基础，可以一起使用这些产品来为我们的 IoT 应用程序创建数据管道。在本教程中，我们假设我们正在从多个城市的多个观测站收集与空气质量相关的测量值。例如，测量值包括地面臭氧、一氧化碳、二氧化硫、二氧化氮和气溶胶。

### 4.1. 设置基础设施

首先，我们假设一个城市的每个气象站都配备了所有传感设备。此外，这些传感器连接到像 Raspberry Pi 这样的板上以收集模拟数据并将其数字化。该板连接到无线以向上游发送原始测量值：

[![物联网基础设施设置](https://www.baeldung.com/wp-content/uploads/2021/02/IoT-Infrastructure-Set-up.jpg)](https://www.baeldung.com/wp-content/uploads/2021/02/IoT-Infrastructure-Set-up.jpg)

区域控制站从一个城市的所有气象站收集数据。我们可以汇总这些数据并将其提供给一些本地分析引擎，以便更快地获得洞察力。来自所有区域控制中心的过滤数据被发送到一个中央指挥中心，该中心主要托管在云端。

### 4.2. 创建物联网架构

现在，我们已准备好为简单的空气质量应用程序设计物联网架构。我们将在这里使用 MQTT 代理、MiNiFi Java 代理、NiFi 和 InfluxDB：

[![物联网架构](https://www.baeldung.com/wp-content/uploads/2021/02/IoT-Architecture-1024x457.jpg)](https://www.baeldung.com/wp-content/uploads/2021/02/IoT-Architecture.jpg)

如我们所见，我们在气象站站点上使用 Mosquitto MQTT 代理和 MiNiFi Java 代理。在区域控制中心，我们使用 NiFi 服务器来聚合和路由数据。最后，我们使用 InfluxDB 在命令中心级别存储测量值。

### 4.3. 执行安装

在像 Raspberry Pi 这样的板上安装 Mosquitto MQTT 代理和 MiNiFi Java 代理非常容易。但是，对于本教程，我们会将它们安装在我们的本地计算机上。

Eclipse Mosquito 的官方[下载页面](https://mosquitto.org/download/)提供了多个平台的二进制文件。安装后，从安装目录启动 Mosquitto 非常简单：

```powershell
net start mosquitto复制
```

此外，[NiFi 二进制文件也可](http://nifi.apache.org/download.html)从其官方网站下载。我们必须将下载的存档解压缩到合适的目录中。由于 MiNiFi 将使用站点到站点协议连接到 NiFi，我们必须在 <NIFI_HOME>/conf/nifi.properties 中指定站点到站点输入套接字端口：

```powershell
# Site to Site properties
nifi.remote.input.host=
nifi.remote.input.secure=false
nifi.remote.input.socket.port=1026
nifi.remote.input.http.enabled=true
nifi.remote.input.http.transaction.ttl=30 sec复制
```

然后，我们可以启动 NiFi：

```powershell
<NIFI_HOME>/bin/run-nifi.bat复制
```

同样，[Java 或 C++ MiNiFi 代理和工具包二进制文件](http://nifi.apache.org/minifi/download.html)可从官方网站下载。同样，我们必须将档案解压缩到合适的目录中。

默认情况下， MiNiFi 带有一组非常少的处理器。由于我们将使用来自 MQTT 的数据，因此我们必须将 MQTT 处理器复制到 <MINIFI_HOME>/lib 目录中。这些被捆绑为 NiFi 存档 (NAR) 文件，可以位于 <NIFI_HOME>/lib 目录中：

```powershell
COPY <NIFI_HOME>/lib/nifi-mqtt-nar-x.x.x.nar <MINIFI_HOME>/lib/nifi-mqtt-nar-x.x.x.nar复制
```

然后我们可以启动 MiNiFi 代理：

```powershell
<MINIFI_HOME>/bin/run-minifi.bat复制
```

最后，我们可以从官方网站[下载开源版本的 InfluxDB 。](https://portal.influxdata.com/downloads/)和以前一样，我们可以提取存档并使用一个简单的命令启动 InfluxDB：

```powershell
<INFLUXDB_HOME>/influxd.exe复制
```

我们应该保留所有其他配置，包括端口，作为本教程的默认配置。这结束了我们本地机器上的安装和设置。

### 4.4. 定义 NiFi 数据流

现在，我们已准备好定义我们的数据流。NiFi提供了一个易于使用的界面来创建和监控数据流。这可以通过 URL http://localhost:8080/nifi 访问。

首先，我们将定义将在 NiFi 服务器上运行的主要数据流：

[![NiFi 主数据流合并](https://www.baeldung.com/wp-content/uploads/2021/02/NiFi-Main-Data-Flow-Combined-1024x384.jpg)](https://www.baeldung.com/wp-content/uploads/2021/02/NiFi-Main-Data-Flow-Combined.jpg)

在这里，正如我们所见，我们定义了一个输入端口，它将接收来自 MiNiFi 代理的数据。它还通过连接将数据发送到负责将数据存储在 InfluxDB 中的[PutInfluxDB处理器。](https://nifi.apache.org/docs/nifi-docs/components/org.apache.nifi/nifi-influxdb-nar/1.6.0/org.apache.nifi.processors.influxdb.PutInfluxDB/)在此处理器的配置中，我们定义了 InfluxDB 的连接 URL 和我们要将数据发送到的数据库名称。

### 4.5. 定义 MiNiFi 数据流

接下来，我们将定义将在 MiNiFi 代理上运行的数据流。我们将使用与 NiFi 相同的用户界面并将数据流导出为模板，以便在 MiNiFi 代理中进行配置。让我们为 MiNiFi 代理定义数据流：

[![NiFi MiNiFi 数据流合并](https://www.baeldung.com/wp-content/uploads/2021/02/NiFi-MiNiFi-Data-Flow-Combined-1024x588.jpg)](https://www.baeldung.com/wp-content/uploads/2021/02/NiFi-MiNiFi-Data-Flow-Combined.jpg)

在这里，我们定义了负责从 MQTT 代理获取数据的[ConsumeMQTT处理器。](https://nifi.apache.org/docs/nifi-docs/components/org.apache.nifi/nifi-mqtt-nar/1.5.0/org.apache.nifi.processors.mqtt.ConsumeMQTT/)我们在属性中提供了代理 URI 以及主题过滤器。我们正在从层次结构air-quality下定义的所有主题中提取数据。

我们还定义了一个远程进程组并将其连接到 ConcumeMQTT 处理器。远程进程组负责通过点对点协议向NiFi推送数据。

我们可以将此数据流另存为模板并将其下载为 XML 文件。我们将此文件命名为config.xml。现在，我们可以使用[转换器工具包](https://nifi.apache.org/minifi/minifi-toolkit.html)将此模板从 XML 转换为 MiNiFi 代理使用的 YAML：

```powershell
<MINIFI_TOOLKIT_HOME>/bin/config.bat transform config.xml config.yml复制
```

这将为我们提供config.yml文件，我们必须在其中手动添加 NiFi 服务器的主机和端口：

```powershell
  Input Ports:
  - id: 19442f9d-aead-3569-b94c-1ad397e8291c
    name: From MiNiFi
    comment: ''
    max concurrent tasks: 1
    use compression: false
    Properties: # Deviates from spec and will later be removed when this is autonegotiated      
      Port: 1026      
      Host Name: localhost复制
```

我们现在可以将这个文件放在目录 <MINIFI_HOME>/conf 中，替换那里可能已经存在的文件。在此之后我们必须重新启动 MiNiFi 代理。

在这里，我们正在做大量的手动工作来创建数据流并在 MiNiFi 代理中配置它。这对于远程位置可能存在数百个代理的现实场景是不切实际的。但是，正如我们之前所见，我们可以使用 MiNiFi C2 服务器自动执行此操作。但这不在本教程的范围内。

### 4.6. 测试数据管道

最后，我们准备测试我们的数据管道！由于我们没有使用真实传感器的自由，我们将创建一个小型模拟。我们将使用一个小的 Java 程序生成传感器数据：

```java
class Sensor implements Callable<Boolean> {
    String city;
    String station;
    String pollutant;
    String topic;
    Sensor(String city, String station, String pollutant, String topic) {
        this.city = city;
        this.station = station;
        this.pollutant = pollutant;
        this.topic = topic;
    }

    @Override
    public Boolean call() throws Exception {
        MqttClient publisher = new MqttClient(
          "tcp://localhost:1883", UUID.randomUUID().toString());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        publisher.connect(options);
        IntStream.range(0, 10).forEach(i -> {
            String payload = String.format("%1$s,city=%2$s,station=%3$s value=%4$04.2f",
              pollutant,
              city,
              station,
              ThreadLocalRandom.current().nextDouble(0, 100));
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(0);
            message.setRetained(true);
            try {
                publisher.publish(topic, message);
                Thread.sleep(1000);
            } catch (MqttException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        return true;
    }
}复制
```

在这里，我们使用[Eclipse Paho Java 客户端](https://www.eclipse.org/paho/index.php?page=clients/java/index.php)向 MQTT 代理生成消息。我们可以添加任意数量的传感器来创建我们的模拟：

```java
ExecutorService executorService = Executors.newCachedThreadPool();
List<Callable<Boolean>> sensors = Arrays.asList(
  new Simulation.Sensor("london", "central", "ozone", "air-quality/ozone"),
  new Simulation.Sensor("london", "central", "co", "air-quality/co"),
  new Simulation.Sensor("london", "central", "so2", "air-quality/so2"),
  new Simulation.Sensor("london", "central", "no2", "air-quality/no2"),
  new Simulation.Sensor("london", "central", "aerosols", "air-quality/aerosols"));
List<Future<Boolean>> futures = executorService.invokeAll(sensors);复制
```

如果一切正常，我们将能够在 InfluxDB 数据库中查询我们的数据：

[![InfluxDB 查询结果](https://www.baeldung.com/wp-content/uploads/2021/02/InfluxDB-Query-Result-1024x446.jpg)](https://www.baeldung.com/wp-content/uploads/2021/02/InfluxDB-Query-Result.jpg)

例如，我们可以在数据库“空气质量”中看到属于测量“臭氧”的所有点。

## 5.总结

总而言之，我们在本教程中介绍了一个基本的物联网用例。我们还了解了如何使用 MQTT、NiFi 和 InfluxDB 等工具来构建可扩展的数据管道。当然，这并没有涵盖物联网应用的全部范围，扩展数据分析管道的可能性是无穷无尽的。

此外，我们在本教程中选择的示例仅用于演示目的。IoT 应用程序的实际基础架构和架构可能千差万别且非常复杂。此外，我们可以通过将可操作的见解作为命令向后推送来完成反馈周期。