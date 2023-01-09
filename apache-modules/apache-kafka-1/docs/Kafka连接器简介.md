## 1. 概述

Apache Kafka® 是一个分布式流媒体平台。在之前的教程中，我们讨论了如何[使用 Spring 实现 Kafka 消费者和生产者](https://www.baeldung.com/spring-kafka)。

在本教程中，我们将学习如何使用 Kafka 连接器。

我们来看看：

-   不同类型的 Kafka 连接器
-   Kafka Connect的特性和模式
-   使用属性文件和 REST API 的连接器配置

## 2. Kafka Connect 和 Kafka 连接器基础知识

Kafka Connect 是一个框架，用于使用所谓的连接器将 Kafka 与外部系统(例如数据库、键值存储、搜索索引和文件系统)连接起来。

Kafka Connectors 是即用型组件，可以帮助我们 将外部系统的数据导入到 Kafka 主题中，以及将 Kafka 主题 中的数据导出到外部系统中。 我们可以将现有的连接器实现用于通用数据源和接收器，或者实现我们自己的连接器。

源连接器从 系统收集数据。源系统可以是整个数据库、流表或消息代理。源连接器还可以将应用服务器的指标收集到 Kafka 主题中，使数据可用于低延迟的流处理。

接收器连接器将数据从 Kafka 主题传送到其他系统，这些系统可能是 Elasticsearch 等索引、Hadoop 等批处理系统或任何类型的数据库。

一些连接器由社区维护，而另一些则由 Confluent 或其合作伙伴支持。实际上，我们可以找到适用于大多数流行系统的连接器，例如 S3、JDBC 和 Cassandra，仅举几例。

## 三、特点

Kafka 连接功能包括：

-   用于连接外部系统与 Kafka 的框架——它简化了连接器的开发、部署和管理
-   分布式和独立模式——它帮助我们利用 Kafka 的分布式特性部署大型集群，以及开发、测试和小型生产部署的设置
-   REST 接口——我们可以使用 REST API 管理连接器
-   自动偏移量管理——Kafka Connect 帮助我们处理 偏移量提交过程，这让我们免去了手动实现连接器开发中这个容易出错的部分的麻烦
-   默认分布式和可扩展 ——Kafka Connect 使用现有的组管理协议；我们可以添加更多工作人员来扩展 Kafka Connect 集群
-   流和批集成——Kafka Connect 是桥接流和批数据系统与 Kafka 现有功能的理想解决方案
-   转换——这些使我们能够对单个消息进行简单和轻量级的修改

## 4.设置

我们将下载 Confluent Platform，而不是使用普通的 Kafka 发行版，这是由 Kafka 背后的公司 Confluent, Inc. 提供的 Kafka 发行版。与普通 Kafka 相比，Confluent Platform 附带了一些额外的工具和客户端，以及一些额外的预构建连接器。

对于我们的案例，开源版本就足够了，可以在[Confluent 的网站](https://www.confluent.io/download/)上找到。

## 5.快速启动Kafka Connect

对于初学者，我们将讨论 Kafka Connect 的原理，使用其最基本的连接器，即文件源连接器和文件接收器连接器。

方便的是，Confluent Platform 附带了这两个连接器以及参考配置。

### 5.1. 源连接器配置

对于源连接器，参考配置位于$CONFLUENT_HOME/etc/kafka/connect-file-source.properties：

```plaintext
name=local-file-source
connector.class=FileStreamSource
tasks.max=1
topic=connect-test
file=test.txt
```

此配置具有所有源连接器共有的一些属性：

-   名称是连接器实例的用户指定名称
-   connector.class指定了实现类，基本上就是连接器的那种
-   tasks.max指定我们的源连接器应该并行运行多少个实例，并且
-   topic定义连接器应将输出发送到的主题

在这种情况下，我们还有一个特定于连接器的属性：

-   文件定义连接器应从中读取输入的文件

为了让它起作用，让我们创建一个包含一些内容的基本文件：

```shell
echo -e "foonbarn" > $CONFLUENT_HOME/test.txt
```

请注意，工作目录是 $CONFLUENT_HOME。

### 5.2. 接收器连接器配置

对于我们的接收器连接器，我们将使用$CONFLUENT_HOME/etc/kafka/connect-file-sink.properties中的参考配置：

```bash
name=local-file-sink
connector.class=FileStreamSink
tasks.max=1
file=test.sink.txt
topics=connect-test
```

从逻辑上讲，它包含完全相同的参数，尽管这次 connector.class指定了接收器连接器实现，而 文件是连接器应写入内容的位置。

### 5.3. 工人配置

最后，我们必须配置 Connect worker，它将集成我们的两个连接器并完成从源连接器读取和写入接收器连接器的工作。

为此，我们可以使用$CONFLUENT_HOME/etc/kafka/connect-standalone.properties：

```bash
bootstrap.servers=localhost:9092
key.converter=org.apache.kafka.connect.json.JsonConverter
value.converter=org.apache.kafka.connect.json.JsonConverter
key.converter.schemas.enable=false
value.converter.schemas.enable=false
offset.storage.file.filename=/tmp/connect.offsets
offset.flush.interval.ms=10000
plugin.path=/share/java
```

请注意， plugin.path可以包含路径列表，其中连接器实现可用

因为我们将使用与 Kafka 捆绑在一起的连接器，所以我们可以将plugin.path设置为$CONFLUENT_HOME/share/java。使用 Windows 时，可能需要在此处提供绝对路径。

对于其他参数，我们可以保留默认值：

-   bootstrap.servers包含 Kafka 代理的地址
-   key.converter和value.converter定义转换器类，当数据从源流到 Kafka，然后从 Kafka 流到接收器时，它们对数据进行序列化和反序列化
-   key.converter.schemas.enable和 value.converter.schemas.enable是特定于转换器的设置
-   offset.storage.file.filename是在独立模式下运行 Connect 时最重要的设置：它定义了 Connect 应该存储其偏移数据的位置
-   offset.flush.interval.ms定义工作人员尝试为任务提交偏移量的时间间隔

并且参数列表已经相当成熟，完整列表请查看 [官方文档](https://kafka.apache.org/documentation/#connectconfigs) 。

### 5.4. 独立模式下的 Kafka Connect

这样，我们就可以开始我们的第一个连接器设置了：

```shell
$CONFLUENT_HOME/bin/connect-standalone 
  $CONFLUENT_HOME/etc/kafka/connect-standalone.properties 
  $CONFLUENT_HOME/etc/kafka/connect-file-source.properties 
  $CONFLUENT_HOME/etc/kafka/connect-file-sink.properties
```

首先，我们可以使用命令行检查主题的内容：

```shell
$CONFLUENT_HOME/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic connect-test --from-beginning
```

正如我们所见，源连接器从test.txt文件中获取数据，将其转换为 JSON，并将其发送给 Kafka：

```javascript
{"schema":{"type":"string","optional":false},"payload":"foo"}
{"schema":{"type":"string","optional":false},"payload":"bar"}
```

而且，如果我们查看文件夹$CONFLUENT_HOME，我们可以看到这里创建了一个文件test.sink.txt ：

```bash
cat $CONFLUENT_HOME/test.sink.txt
foo
bar
```

当 sink 连接器从payload属性中提取值并将其写入目标文件时，test.sink.txt中的数据具有原始 test.txt 文件的内容。

现在让我们向test.txt 添加更多行。

当我们这样做时，我们看到源连接器自动检测到这些更改。

我们只需要确保在末尾插入一个换行符，否则，源连接器将不会考虑最后一行。

此时，让我们停止 Connect 进程，因为我们将 在几行中以分布式模式启动 Connect。

## 6. Connect 的 REST API

到目前为止，我们都是通过命令行传递属性文件来进行所有配置的。但是，由于 Connect 旨在作为服务运行，因此还有一个 REST API 可用。

默认情况下，它位于http://localhost:8083。几个端点是：

-   GET /connectors – 返回包含所有正在使用的连接器的列表
-   GET /connectors/{name} – 返回有关特定连接器的详细信息
-   POST /connectors – 创建一个新的连接器；请求正文应该是一个 JSON 对象，包含一个字符串名称字段和一个带有连接器配置参数的对象配置字段
-   GET /connectors/{name}/status – 返回连接器的当前状态 – 包括它是正在运行、失败还是暂停 – 它被分配给哪个 worker，失败时的错误信息，以及它所有任务的状态
-   DELETE /connectors/{name} – 删除连接器，优雅地停止所有任务并删除其配置
-   GET /connector-plugins – 返回安装在 Kafka Connect 集群中的连接器插件列表

[官方文档](https://kafka.apache.org/documentation/#connect_rest) 提供了包含所有端点的列表。 

我们将在下一节中使用 REST API 创建新的连接器。

## 7.分布式模式下的Kafka Connect

独立模式非常适合开发和测试，以及较小的设置。 但是，如果我们想充分利用Kafka的分布式特性，就必须以分布式方式启动Connect。

通过这样做，连接器设置和元数据存储在 Kafka 主题而不是文件系统中。因此，工作节点实际上是无状态的。

### 7.1. 开始连接 

分布式模式的参考配置可以在 $CONFLUENT_HOME /etc/kafka/connect-distributed.properties 中找到。

参数大部分与独立模式相同。只有几个区别：

-   group.id定义 Connect 集群组的名称。该值必须不同于任何消费者组 ID
-   offset.storage.topic、 config.storage.topic和status.storage.topic为这些设置定义主题。对于每个主题，我们还可以定义一个因子

同样， [官方文档](https://kafka.apache.org/documentation/#connectconfigs) 提供了包含所有参数的列表。

我们可以通过分布式方式启动Connect，如下：

```shell
$CONFLUENT_HOME/bin/connect-distributed $CONFLUENT_HOME/etc/kafka/connect-distributed.properties
```

### 7.2. 使用 REST API 添加连接器

现在，与独立启动命令相比，我们没有将任何连接器配置作为参数传递。相反，我们必须使用 REST API 创建连接器。

要设置我们之前的示例，我们必须向 http://localhost:8083/connectors发送两个包含以下 JSON 结构的 POST 请求。

首先，我们需要将源连接器 POST 的正文创建为 JSON 文件。在这里，我们将其称为connect-file-source.json：

```javascript
{
    "name": "local-file-source",
    "config": {
        "connector.class": "FileStreamSource",
        "tasks.max": 1,
        "file": "test-distributed.txt",
        "topic": "connect-distributed"
    }
}
```

请注意，这看起来与我们第一次使用的参考配置文件非常相似。

然后我们发布它：

```shell
curl -d @"$CONFLUENT_HOME/connect-file-source.json" 
  -H "Content-Type: application/json" 
  -X POST http://localhost:8083/connectors
```

然后，我们将对接收器连接器执行相同的操作，调用文件connect-file-sink.json：

```javascript
{
    "name": "local-file-sink",
    "config": {
        "connector.class": "FileStreamSink",
        "tasks.max": 1,
        "file": "test-distributed.sink.txt",
        "topics": "connect-distributed"
    }
}
```

并像以前一样执行 POST：

```shell
curl -d @$CONFLUENT_HOME/connect-file-sink.json 
  -H "Content-Type: application/json" 
  -X POST http://localhost:8083/connectors
```

如果需要，我们可以验证此设置是否正常工作：

```bash
$CONFLUENT_HOME/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic connect-distributed --from-beginning
{"schema":{"type":"string","optional":false},"payload":"foo"}
{"schema":{"type":"string","optional":false},"payload":"bar"}
```

而且，如果我们查看文件夹$CONFLUENT_HOME，我们可以看到这里创建了一个文件test-distributed.sink.txt ：

```bash
cat $CONFLUENT_HOME/test-distributed.sink.txt
foo
bar
```

在我们测试了分布式设置之后，让我们通过删除两个连接器来进行清理：

```shell
curl -X DELETE http://localhost:8083/connectors/local-file-source
curl -X DELETE http://localhost:8083/connectors/local-file-sink
```

## 8. 转换数据

### 8.1. 支持的转换

转换使我们能够对单个消息进行简单和轻量级的修改。

Kafka Connect 支持以下内置转换：

-   InsertField – 使用静态数据或记录元数据添加字段
-   ReplaceField – 过滤或重命名字段
-   MaskField – 将字段替换为类型的有效空值(例如，零或空字符串)
-   HoistField – 将整个事件包装为结构或映射中的单个字段
-   ExtractField – 从 struct 和 map 中提取特定字段，并在结果中仅包含该字段
-   SetSchemaMetadata – 修改架构名称或版本
-   TimestampRouter – 根据原始主题和时间戳修改记录的主题
-   RegexRouter – 根据原始主题、替换字符串和正则表达式修改记录的主题

使用以下参数配置转换：

-   transforms – 以逗号分隔的转换别名列表
-   transforms.$alias.type – 转换的类名
-   transforms.$alias.$transformationSpecificConfig – 相应转换的配置

### 8.2. 应用变压器

为了测试一些转换功能，让我们设置以下两个转换：

-   首先，让我们将整个消息包装成一个 JSON 结构
-   之后，让我们向该结构添加一个字段

在应用我们的转换之前，我们必须通过修改 connect-distributed.properties将 Connect 配置为使用无模式 JSON ：

```plaintext
key.converter.schemas.enable=false
value.converter.schemas.enable=false
```

之后，我们必须再次以分布式模式重新启动 Connect：

```bash
$CONFLUENT_HOME/bin/connect-distributed $CONFLUENT_HOME/etc/kafka/connect-distributed.properties
```

同样，我们需要将源连接器 POST 的正文创建为 JSON 文件。在这里，我们将其称为connect-file-source-transform.json。

除了已知的参数外，我们还为两个必需的转换添加了几行：

```javascript
{
    "name": "local-file-source",
    "config": {
        "connector.class": "FileStreamSource",
        "tasks.max": 1,
        "file": "test-transformation.txt",
        "topic": "connect-transformation",
        "transforms": "MakeMap,InsertSource",
        "transforms.MakeMap.type": "org.apache.kafka.connect.transforms.HoistField$Value",
        "transforms.MakeMap.field": "line",
        "transforms.InsertSource.type": "org.apache.kafka.connect.transforms.InsertField$Value",
        "transforms.InsertSource.static.field": "data_source",
        "transforms.InsertSource.static.value": "test-file-source"
    }
}
```

之后，让我们执行 POST：

```shell
curl -d @$CONFLUENT_HOME/connect-file-source-transform.json 
  -H "Content-Type: application/json" 
  -X POST http://localhost:8083/connectors
```

让我们在test-transformation.txt中写几行：

```bash
Foo
Bar
```

如果我们现在检查connect-transformation主题，我们应该得到以下几行：

```shell
{"line":"Foo","data_source":"test-file-source"}
{"line":"Bar","data_source":"test-file-source"}
```

## 9. 使用就绪连接器

使用完这些简单的连接器后，让我们看看更高级的即用型连接器，以及如何安装它们。

### 9.1. 在哪里可以找到连接器

预制连接器可从不同来源获得：

-   一些连接器与普通 Apache Kafka 捆绑在一起(文件和控制台的源和接收器)

-   更多连接器与 Confluent Platform 捆绑在一起(ElasticSearch、HDFS、JDBC 和 AWS S3)

-   还可以查看 

    Confluent Hub

    ，它是 Kafka 连接器的一种应用程序商店。提供的连接器数量在不断增长：

    -   Confluent 连接器(由 Confluent 开发、测试、记录并完全支持)
    -   经过认证的连接器(由第 3 方实施并由 Confluent 认证)
    -   社区开发和支持的连接器

-   除此之外，Confluent 还提供了一个[连接器页面](https://www.confluent.io/product/connectors/)，其中包含一些在 Confluent Hub 上也可用的连接器，还有一些更多的社区连接器

-   最后，还有供应商将连接器作为其产品的一部分提供。例如，Landoop 提供了一个名为[Lenses](https://www.landoop.com/downloads/)的流媒体库，其中还包含一组约 25 个开源连接器(其中许多还在其他地方交叉列出)

### 9.2. 从 Confluent Hub 安装连接器

Confluent 的企业版提供了一个脚本，用于从 Confluent Hub 安装连接器和其他组件(该脚本不包含在开源版本中)。如果我们使用的是企业版，我们可以使用以下命令安装连接器：

```bash
$CONFLUENT_HOME/bin/confluent-hub install confluentinc/kafka-connect-mqtt:1.0.0-preview
```

### 9.3. 手动安装连接器

如果我们需要一个 Confluent Hub 上没有的连接器，或者如果我们有 Confluent 的开源版本，我们可以手动安装所需的连接器。为此，我们必须下载并解压缩连接器，并将包含的库移动到指定为 plugin.path 的文件夹中。

对于每个连接器，存档应包含两个我们感兴趣的文件夹：

-   lib文件夹包含连接器jar，例如 kafka-connect-mqtt-1.0.0-preview.jar，以及连接器所需的更多 jar
-   etc文件夹包含一个或多个参考配置文件

我们必须将lib文件夹移动到$CONFLUENT_HOME/share/java ，或者我们在 connect-standalone.properties和 connect-distributed.properties中指定为 plugin.path的任何路径。这样做时，将文件夹重命名为有意义的名称也可能有意义。

我们可以通过在独立模式下启动时引用它们来使用来自etc的配置文件 ，或者我们可以只获取属性并从中创建一个 JSON 文件。

## 10.总结

在本教程中，我们了解了如何安装和使用 Kafka Connect。

我们研究了连接器的类型，包括源和接收器。我们还研究了 Connect 可以运行的一些特性和模式。然后，我们回顾了转换器。最后，我们了解了从何处获取以及如何安装自定义连接器。