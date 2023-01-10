## 1. 简介

Kinesis 是一种用于实时收集、处理和分析数据流的工具，由亚马逊开发。它的主要优点之一是它有助于开发事件驱动的应用程序。

在本教程中，我们将探索一些库，使我们的 Spring 应用程序能够从 Kinesis Stream 生成和使用记录。代码示例将显示基本功能，但不代表生产就绪代码。

## 2. 先决条件

在我们继续之前，我们需要做两件事。

首先是[创建一个 Spring 项目](https://www.baeldung.com/spring-boot-start)，因为这里的目标是从 Spring 项目与 Kinesis 交互。

第二个是创建 Kinesis Data Stream。我们可以通过 AWS 账户中的 Web 浏览器执行此操作。对于我们当中的 AWS CLI 粉丝来说，一种替代方法是[使用命令行](https://docs.aws.amazon.com/cli/latest/reference/kinesis/create-stream.html)。因为我们将从代码中与之交互，所以我们还必须手头有 AWS [IAM 凭证](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html)、访问密钥和秘密密钥以及区域。

我们所有的生产者都将创建虚拟 IP 地址记录，而消费者将读取这些值并将它们列在应用程序控制台中。

## 3. 适用于Java的 AWS 开发工具包

我们将使用的第一个库是适用于Java的 AWS 开发工具包。它的优势在于它允许我们管理使用 Kinesis Data Streams 的许多部分。我们可以读取数据、生产数据、创建数据流和重新分片数据流。缺点是，为了拥有生产就绪的代码，我们必须对重新分片、错误处理或守护进程等方面进行编码，以保持消费者的活力。

### 3.1. Maven 依赖

[amazon-kinesis-client](https://search.maven.org/search?q=g:com.amazonaws a:amazon-kinesis-client) Maven 依赖项将带来我们拥有工作示例所需的一切。我们现在将它添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk-kinesis</artifactId>
    <version>1.11.632</version>
</dependency>
```

### 3.2. 弹簧设置

让我们重用与 Kinesis Stream 交互所需的AmazonKinesis对象。我们将在我们的@SpringBootApplication类中将其创建为@Bean：

```java
@Bean
public AmazonKinesis buildAmazonKinesis() {
    BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
    return AmazonKinesisClientBuilder.standard()
      .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
      .withRegion(Regions.EU_CENTRAL_1)
      .build();
}
```

接下来，让我们在 application.properties 中定义本地机器所需的aws.access.key和aws.secret.key：

```plaintext
aws.access.key=my-aws-access-key-goes-here
aws.secret.key=my-aws-secret-key-goes-here
```

我们将使用@Value注解读取它们：

```java
@Value("${aws.access.key}")
private String accessKey;

@Value("${aws.secret.key}")
private String secretKey;
```

为了简单起见，我们将依赖@Scheduled方法来创建和使用记录。

### 3.3. 消费者

AWS SDK Kinesis Consumer 使用拉模型，这意味着我们的代码将从 Kinesis 数据流的碎片中提取记录：

```java
GetRecordsRequest recordsRequest = new GetRecordsRequest();
recordsRequest.setShardIterator(shardIterator.getShardIterator());
recordsRequest.setLimit(25);

GetRecordsResult recordsResult = kinesis.getRecords(recordsRequest);
while (!recordsResult.getRecords().isEmpty()) {
    recordsResult.getRecords().stream()
      .map(record -> new String(record.getData().array()))
      .forEach(System.out::println);

    recordsRequest.setShardIterator(recordsResult.getNextShardIterator());
    recordsResult = kinesis.getRecords(recordsRequest);
}
```

GetRecordsRequest对象构建对流数据的请求。在我们的示例中，我们定义了每个请求 25 条记录的限制，并且我们会继续读取，直到没有更多内容可读取为止。

我们还可以注意到，对于我们的迭代，我们使用了GetShardIteratorResult对象。我们在@PostConstruct方法中创建了这个对象，这样我们就可以立即开始跟踪记录：

```java
private GetShardIteratorResult shardIterator;

@PostConstruct
private void buildShardIterator() {
    GetShardIteratorRequest readShardsRequest = new GetShardIteratorRequest();
    readShardsRequest.setStreamName(IPS_STREAM);
    readShardsRequest.setShardIteratorType(ShardIteratorType.LATEST);
    readShardsRequest.setShardId(IPS_SHARD_ID);

    this.shardIterator = kinesis.getShardIterator(readShardsRequest);
}
```

### 3.4. 制作人

现在让我们看看如何为我们的 Kinesis 数据流处理记录的创建。

我们使用PutRecordsRequest对象插入数据。对于这个新对象，我们添加了一个包含多个PutRecordsRequestEntry对象的列表：

```java
List<PutRecordsRequestEntry> entries = IntStream.range(1, 200).mapToObj(ipSuffix -> {
    PutRecordsRequestEntry entry = new PutRecordsRequestEntry();
    entry.setData(ByteBuffer.wrap(("192.168.0." + ipSuffix).getBytes()));
    entry.setPartitionKey(IPS_PARTITION_KEY);
    return entry;
}).collect(Collectors.toList());

PutRecordsRequest createRecordsRequest = new PutRecordsRequest();
createRecordsRequest.setStreamName(IPS_STREAM);
createRecordsRequest.setRecords(entries);

kinesis.putRecords(createRecordsRequest);
```

我们已经创建了一个基本的消费者和模拟 IP 记录的生产者。现在剩下要做的就是运行我们的 Spring 项目并查看应用程序控制台中列出的 IP。

## 4. KCL和KPL

Kinesis Client Library (KCL) 是一个简化记录消费的库。它也是针对 Kinesis Data Streams 的 AWS 开发工具包JavaAPI 的抽象层。在幕后，该库处理多个实例之间的负载平衡、响应实例故障、检查处理的记录以及对重新分片做出反应。

Kinesis Producer Library (KPL) 是一个可用于写入 Kinesis 数据流的库。它还提供了一个位于 Kinesis Data Streams 的 AWS 开发工具包JavaAPI 之上的抽象层。为了获得更好的性能，该库自动处理批处理、多线程和重试逻辑。

KCL 和 KPL 的主要优点都是易于使用，因此我们可以专注于生产和消费记录。

### 4.1. Maven 依赖项

如果需要的话，这两个库可以在我们的项目中单独带上。要在我们的 Maven 项目中包含[KPL](https://search.maven.org/search?q=amazon-kinesis-producer)和[KCL](https://search.maven.org/search?q=a:amazon-kinesis-client g:com.amazonaws)，我们需要更新我们的 pom.xml 文件：

```xml
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>amazon-kinesis-producer</artifactId>
    <version>0.13.1</version>
</dependency>
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>amazon-kinesis-client</artifactId>
    <version>1.11.2</version>
</dependency>
```

### 4.2. 弹簧设置

我们唯一需要的 Spring 准备工作是确保我们手头有 IAM 凭证。aws.access.key和aws.secret.key的值在我们的application.properties文件中定义，因此我们可以在需要时使用@Value读取它们。

### 4.3. 消费者

首先，我们将创建一个实现IRecordProcessor接口的类，并定义我们如何处理 Kinesis 数据流记录的逻辑，即在控制台中打印它们：

```java
public class IpProcessor implements IRecordProcessor {
    @Override
    public void initialize(InitializationInput initializationInput) { }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        processRecordsInput.getRecords()
          .forEach(record -> System.out.println(new String(record.getData().array())));
    }

    @Override
    public void shutdown(ShutdownInput shutdownInput) { }
}
```

下一步是定义一个实现IRecordProcessorFactory接口并返回先前创建的IpProcessor对象的工厂类：

```java
public class IpProcessorFactory implements IRecordProcessorFactory {
    @Override
    public IRecordProcessor createProcessor() {
        return new IpProcessor();
    }
}
```

现在是最后一步，我们将使用Worker对象来定义我们的消费者管道。我们需要一个KinesisClientLibConfiguration对象，该对象将在需要时定义 IAM 凭证和 AWS 区域。

我们会将KinesisClientLibConfiguration和我们的IpProcessorFactory对象传递给我们的Worker，然后在单独的线程中启动它。我们通过使用Worker类来保持这种消费记录的逻辑始终存在，所以我们现在不断地读取新记录：

```java
BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
KinesisClientLibConfiguration consumerConfig = new KinesisClientLibConfiguration(
  APP_NAME, 
  IPS_STREAM,
  new AWSStaticCredentialsProvider(awsCredentials), 
  IPS_WORKER)
    .withRegionName(Regions.EU_CENTRAL_1.getName());

final Worker worker = new Worker.Builder()
  .recordProcessorFactory(new IpProcessorFactory())
  .config(consumerConfig)
  .build();
CompletableFuture.runAsync(worker.run());
```

### 4.4. 制作人

现在让我们定义KinesisProducerConfiguration对象，添加 IAM 凭证和 AWS 区域：

```java
BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
KinesisProducerConfiguration producerConfig = new KinesisProducerConfiguration()
  .setCredentialsProvider(new AWSStaticCredentialsProvider(awsCredentials))
  .setVerifyCertificate(false)
  .setRegion(Regions.EU_CENTRAL_1.getName());

this.kinesisProducer = new KinesisProducer(producerConfig);
```

我们将包含先前在@Scheduled作业中创建的kinesisProducer对象，并持续为我们的 Kinesis 数据流生成记录：

```java
IntStream.range(1, 200).mapToObj(ipSuffix -> ByteBuffer.wrap(("192.168.0." + ipSuffix).getBytes()))
  .forEach(entry -> kinesisProducer.addUserRecord(IPS_STREAM, IPS_PARTITION_KEY, entry));
```

## 5. Spring Cloud Stream 绑定器运动

我们已经看到了两个库，它们都是在 Spring 生态系统之外创建的。我们现在将看到 Spring Cloud Stream Binder Kinesis 如何在构建于[Spring Cloud Stream](https://www.baeldung.com/spring-cloud-stream)之上的同时进一步简化我们的生活。

### 5.1. Maven 依赖

我们需要在[Spring Cloud Stream Binder Kinesis](https://search.maven.org/search?q=a:spring-cloud-stream-binder-kinesis)的应用程序中定义的 Maven 依赖项是：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-stream-binder-kinesis</artifactId>
    <version>1.2.1.RELEASE</version>
</dependency>
```

### 5.2. 弹簧设置

在 EC2 上运行时，会自动发现所需的 AWS 属性，因此无需定义它们。由于我们在本地机器上运行我们的示例，我们需要为我们的 AWS 账户定义我们的 IAM 访问密钥、秘密密钥和区域。我们还为应用程序禁用了自动 CloudFormation 堆栈名称检测：

```plaintext
cloud.aws.credentials.access-key=my-aws-access-key
cloud.aws.credentials.secret-key=my-aws-secret-key
cloud.aws.region.static=eu-central-1
cloud.aws.stack.auto=false
```

Spring Cloud Stream 捆绑了我们可以在流绑定中使用的三个接口：

-   Sink用于数据摄取
-   来源用于发布记录
-   处理器是两者的结合

如果需要，我们也可以定义自己的接口。

### 5.3. 消费者

定义消费者是一项分为两部分的工作。首先，我们将在application.properties中定义我们将从中使用的数据流：

```plaintext
spring.cloud.stream.bindings.input.destination=live-ips
spring.cloud.stream.bindings.input.group=live-ips-group
spring.cloud.stream.bindings.input.content-type=text/plain
```

接下来，让我们定义一个 Spring @Component类。注解@EnableBinding(Sink.class)将允许我们使用注解的方法从 Kinesis 流中读取@StreamListener(Sink.INPUT)：

```java
@EnableBinding(Sink.class)
public class IpConsumer {

    @StreamListener(Sink.INPUT)
    public void consume(String ip) {
        System.out.println(ip);
    }
}
```

### 5.4. 制作人

生产者也可以一分为二。首先，我们必须在application.properties中定义我们的流属性：

```plaintext
spring.cloud.stream.bindings.output.destination=live-ips
spring.cloud.stream.bindings.output.content-type=text/plain
```

然后我们在 Spring @Component上添加@EnableBinding(Source.class)并每隔几秒创建新的测试消息：

```java
@Component
@EnableBinding(Source.class)
public class IpProducer {

    @Autowired
    private Source source;

    @Scheduled(fixedDelay = 3000L)
    private void produce() {
        IntStream.range(1, 200).mapToObj(ipSuffix -> "192.168.0." + ipSuffix)
          .forEach(entry -> source.output().send(MessageBuilder.withPayload(entry).build()));
    }
}
```

这就是 Spring Cloud Stream Binder Kinesis 工作所需的全部内容。我们现在可以简单地启动应用程序。

## 六. 总结

在本文中，我们了解了如何将 Spring 项目与两个 AWS 库集成以与 Kinesis Data Stream 进行交互。我们还了解了如何使用 Spring Cloud Stream Binder Kinesis 库来简化实施。