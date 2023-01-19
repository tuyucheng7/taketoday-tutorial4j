## 1. 概述

本教程将介绍分布式 实时计算系统[Apache Storm 。](https://storm.apache.org/)

我们将关注并涵盖：

-   Apache Storm到底是什么以及它解决了什么问题
-   它的架构，和
-   如何在项目中使用它

## 2. 什么是 Apache Storm？

Apache Storm 是用于实时计算的免费开源分布式系统。

它提供容错性、可扩展性并保证数据处理，尤其擅长处理无界数据流。 

Storm 的一些好的用例可以是处理信用卡操作以检测欺诈或处理来自智能家居的数据以检测故障传感器。

Storm 允许与市场上可用的各种数据库和队列系统集成。

## 3.Maven依赖

在我们使用 Apache Storm 之前，我们需要在我们的项目中包含[storm-core 依赖项](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.storm" AND a%3A"storm-core")：

```xml
<dependency>
    <groupId>org.apache.storm</groupId>
    <artifactId>storm-core</artifactId>
    <version>1.2.2</version>
    <scope>provided</scope>
</dependency>
```

如果我们打算在 Storm 集群上运行我们的应用程序，我们应该只使用提供的范围 。

要在本地运行应用程序，我们可以使用所谓的本地模式，在本地进程中模拟 Storm 集群，在这种情况下我们应该删除 提供的。

## 4. 数据模型

Apache Storm 的数据模型由两个元素组成：元组和流。

### 4.1. 元组

元组是具有动态类型的命名字段的有序列表。 这意味着我们不需要显式声明字段的类型。

Storm 需要知道如何序列化元组中使用的所有值。默认情况下，它已经可以序列化基本类型、字符串和字节数组。

由于 Storm 使用 Kryo 序列化，我们需要使用 Config注册序列化 程序以使用自定义类型。我们可以通过以下两种方式之一来做到这一点：

首先，我们可以使用全名注册要序列化的类：

```java
Config config = new Config();
config.registerSerialization(User.class);
```

在这种情况下，Kryo 将使用[FieldSerializer](https://github.com/EsotericSoftware/kryo#fieldserializer)序列化该类。 默认情况下，这将序列化该类的所有非瞬态字段，包括私有字段和公共字段。

或者，我们可以同时提供要序列化的类和我们希望 Storm 为该类使用的序列化程序：

```java
Config config = new Config();
config.registerSerialization(User.class, UserSerializer.class);
```

要创建自定义序列化程序，我们需要[扩展](https://www.baeldung.com/kryo) 具有两个方法 写入 和 读取的通用类序列化 程序。

### 4.2. 溪流

Stream是 Storm 生态系统中的核心抽象。 Stream是无限的元组序列。

Storms 允许并行处理多个流。

每个流都有一个在声明期间提供和分配的 ID。

## 5. 拓扑

实时 Storm 应用程序的逻辑被打包到拓扑中。该拓扑由 spouts和bolts组成。

### 5.1. 喷口

喷口是流的源头。它们向拓扑发出元组。

可以从 Kafka、Kestrel 或 ActiveMQ 等各种外部系统读取元组。

Spouts 可以是 可靠的 也 可以是不可靠的。Reliable 意味着 spout 可以回复 Storm 处理失败的元组。 不可靠意味着 spout 没有回复，因为它将使用即发即弃机制来发出元组。

要创建自定义 spout，我们需要实现 IRichSpout 接口或扩展任何已经实现该接口的类，例如抽象的 BaseRichSpout类。

让我们创建一个 不可靠 的spout：

```java
public class RandomIntSpout extends BaseRichSpout {

    private Random random;
    private SpoutOutputCollector outputCollector;

    @Override
    public void open(Map map, TopologyContext topologyContext,
      SpoutOutputCollector spoutOutputCollector) {
        random = new Random();
        outputCollector = spoutOutputCollector;
    }

    @Override
    public void nextTuple() {
        Utils.sleep(1000);
        outputCollector.emit(new Values(random.nextInt(), System.currentTimeMillis()));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("randomInt", "timestamp"));
    }
}
```

我们的自定义 RandomIntSpout将每秒生成随机整数和时间戳。

### 5.2. 螺栓

螺栓处理流中的元组。他们可以执行各种操作，如过滤、聚合或自定义函数。

有些操作需要多个步骤，因此在这种情况下我们需要使用多个螺栓。

要创建自定义 Bolt，我们需要实现 IRichBolt 或更简单的操作 IBasicBolt接口。

还有多个辅助类可用于实现 Bolt。 在这种情况下，我们将使用 BaseBasicBolt：

```java
public class PrintingBolt extends BaseBasicBolt {
    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        System.out.println(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}
```

这个自定义 PrintingBolt将简单地将所有元组打印到控制台。

## 6. 创建一个简单的拓扑

让我们将这些想法组合成一个简单的拓扑。我们的拓扑将有一个 spout 和三个 bolts。

### 6.1. 随机数Spout

一开始，我们会创建一个不可靠的 spout。它将每秒生成 (0,100) 范围内的随机整数：

```java
public class RandomNumberSpout extends BaseRichSpout {
    private Random random;
    private SpoutOutputCollector collector;

    @Override
    public void open(Map map, TopologyContext topologyContext, 
      SpoutOutputCollector spoutOutputCollector) {
        random = new Random();
        collector = spoutOutputCollector;
    }

    @Override
    public void nextTuple() {
        Utils.sleep(1000);
        int operation = random.nextInt(101);
        long timestamp = System.currentTimeMillis();

        Values values = new Values(operation, timestamp);
        collector.emit(values);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("operation", "timestamp"));
    }
}
```

### 6.2. 过滤螺栓

接下来，我们将创建一个螺栓，它将过滤掉 操作 等于 0 的所有元素：

```java
public class FilteringBolt extends BaseBasicBolt {
    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        int operation = tuple.getIntegerByField("operation");
        if (operation > 0) {
            basicOutputCollector.emit(tuple.getValues());
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("operation", "timestamp"));
    }
}
```

### 6.3. 聚合螺栓

接下来，让我们创建一个更复杂的 Bolt ，它将聚合每天的所有积极操作。

为此，我们将使用一个专门为实现在窗口上运行而不是在单个元组上运行的螺栓而创建的特定类： BaseWindowedBolt。

窗口是流处理中的基本概念，它将无限流拆分为有限块。然后我们可以对每个块应用计算。窗户一般有两种：

时间窗口用于使用时间戳对给定时间段内的元素进行分组。时间窗可能有不同数量的元素。

计数窗口用于创建具有定义大小的窗口。在这种情况下，所有窗口都将具有相同的大小，如果元素少于定义的大小，则不会发射窗口。

我们的AggregatingBolt将生成时间窗口中所有正操作的总和及其开始和结束时间戳：

```java
public class AggregatingBolt extends BaseWindowedBolt {
    private OutputCollector outputCollector;
    
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.outputCollector = collector;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("sumOfOperations", "beginningTimestamp", "endTimestamp"));
    }

    @Override
    public void execute(TupleWindow tupleWindow) {
        List<Tuple> tuples = tupleWindow.get();
        tuples.sort(Comparator.comparing(this::getTimestamp));

        int sumOfOperations = tuples.stream()
          .mapToInt(tuple -> tuple.getIntegerByField("operation"))
          .sum();
        Long beginningTimestamp = getTimestamp(tuples.get(0));
        Long endTimestamp = getTimestamp(tuples.get(tuples.size() - 1));

        Values values = new Values(sumOfOperations, beginningTimestamp, endTimestamp);
        outputCollector.emit(values);
    }

    private Long getTimestamp(Tuple tuple) {
        return tuple.getLongByField("timestamp");
    }
}
```

请注意，在这种情况下，直接获取列表的第一个元素是安全的。这是因为每个窗口都是使用 Tuple 的时间戳 字段计算的 ，因此每个窗口中必须 至少有一个元素。

### 6.4. 文件写入螺栓

最后，我们将创建一个螺栓，它将获取 sumOfOperations大于 2000 的所有元素，将它们序列化并将它们写入文件：

```java
public class FileWritingBolt extends BaseRichBolt {
    public static Logger logger = LoggerFactory.getLogger(FileWritingBolt.class);
    private BufferedWriter writer;
    private String filePath;
    private ObjectMapper objectMapper;

    @Override
    public void cleanup() {
        try {
            writer.close();
        } catch (IOException e) {
            logger.error("Failed to close writer!");
        }
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, 
      OutputCollector outputCollector) {
        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        
        try {
            writer = new BufferedWriter(new FileWriter(filePath));
        } catch (IOException e) {
            logger.error("Failed to open a file for writing.", e);
        }
    }

    @Override
    public void execute(Tuple tuple) {
        int sumOfOperations = tuple.getIntegerByField("sumOfOperations");
        long beginningTimestamp = tuple.getLongByField("beginningTimestamp");
        long endTimestamp = tuple.getLongByField("endTimestamp");

        if (sumOfOperations > 2000) {
            AggregatedWindow aggregatedWindow = new AggregatedWindow(
                sumOfOperations, beginningTimestamp, endTimestamp);
            try {
                writer.write(objectMapper.writeValueAsString(aggregatedWindow));
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                logger.error("Failed to write data to file.", e);
            }
        }
    }
    
    // public constructor and other methods
}
```

请注意，我们不需要声明输出，因为这将是我们拓扑中的最后一个螺栓

### 6.5. 运行拓扑

最后，我们可以将所有东西放在一起并运行我们的拓扑：

```java
public static void runTopology() {
    TopologyBuilder builder = new TopologyBuilder();

    Spout random = new RandomNumberSpout();
    builder.setSpout("randomNumberSpout");

    Bolt filtering = new FilteringBolt();
    builder.setBolt("filteringBolt", filtering)
      .shuffleGrouping("randomNumberSpout");

    Bolt aggregating = new AggregatingBolt()
      .withTimestampField("timestamp")
      .withLag(BaseWindowedBolt.Duration.seconds(1))
      .withWindow(BaseWindowedBolt.Duration.seconds(5));
    builder.setBolt("aggregatingBolt", aggregating)
      .shuffleGrouping("filteringBolt"); 
      
    String filePath = "./src/main/resources/data.txt";
    Bolt file = new FileWritingBolt(filePath);
    builder.setBolt("fileBolt", file)
      .shuffleGrouping("aggregatingBolt");

    Config config = new Config();
    config.setDebug(false);
    LocalCluster cluster = new LocalCluster();
    cluster.submitTopology("Test", config, builder.createTopology());
}
```

为了使数据流经拓扑中的每一部分，我们需要指出如何连接它们。shuffleGroup允许我们声明用于 filteringBolt的数据将来自 randomNumberSpout。

对于每个 Bolt，我们需要添加 shuffleGroup来定义这个 bolt 的元素源。 元素的来源可能是一个 Spout 或另一个 Bolt。如果我们为多个螺栓设置相同的源， 源将向每个螺栓发射所有元素。

在这种情况下，我们的拓扑将使用 LocalCluster 在本地运行作业。

## 七. 总结

在本教程中，我们介绍了分布式实时计算系统 Apache Storm。我们创建了一个 spout 和一些 bolts，并将它们拉到一起形成一个完整的拓扑结构。