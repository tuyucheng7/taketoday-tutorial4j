## 1. 简介

Kinesis是一种用于实时收集、处理和分析数据流的工具，由亚马逊开发。它的主要优点之一是它有助于开发事件驱动的应用程序。

在本教程中，我们将探索一些库，**使我们的Spring应用程序能够从Kinesis Stream生成和消费记录**。代码示例将显示基本功能，但不代表生产就绪代码。

## 2. 先决条件

在我们进一步讨论之前，我们需要做两件事。

首先是[创建一个Spring项目](https://www.baeldung.com/spring-boot-start)，因为这里的目标是从Spring项目与Kinesis交互。

第二个是创建Kinesis Data Stream。我们可以通过AWS账户中的Web浏览器执行此操作。对于我们当中的AWS CLI粉丝来说，一种替代方法是使用[命令行](https://docs.aws.amazon.com/cli/latest/reference/kinesis/create-stream.html)。由于我们将从代码中与之交互，因此我们还必须手头有[AWS IAM凭证](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html)、访问密钥和私有密钥以及区域。

我们所有的生产者都将创建虚拟IP地址记录，而消费者将读取这些值并将它们列在应用程序控制台中。

## 3. 适用于Java的AWS开发工具包

我们将使用的第一个库是适用于Java的AWS开发工具包。它的优势在于它允许我们管理使用Kinesis Data Streams的许多部分。我们可以**读取数据、生产数据、创建数据流和重新分片数据流**。缺点是，为了拥有生产就绪的代码，我们必须对重新分片、错误处理或守护进程等方面进行编码，以保持消费者的活力。

### 3.1 Maven依赖

[amazon-kinesis-client](https://central.sonatype.com/artifact/com.amazonaws/amazon-kinesis-client/1.14.10) Maven依赖项将带来我们拥有工作示例所需的一切。我们现在将它添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk-kinesis</artifactId>
    <version>1.11.632</version>
</dependency>
```

### 3.2 Spring设置

让我们重用与Kinesis Stream交互所需的AmazonKinesis对象。我们将在我们的@SpringBootApplication类中将其创建为@Bean：

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

接下来，让我们在application.properties中定义本地机器所需的aws.access.key和aws.secret.key：

```properties
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

为了简单起见，我们将依赖@Scheduled方法来创建和消费记录。

### 3.3 消费者

**AWS SDK Kinesis消费者使用拉取模型**，这意味着我们的代码将从Kinesis数据流的碎片中提取记录：

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

**GetRecordsRequest对象构建对流数据的请求**。在我们的示例中，我们定义了每个请求25条记录的限制，并且我们会继续读取，直到没有更多内容可读取为止。

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

### 3.4 生产者

现在让我们看看如何**为我们的Kinesis数据流处理记录的创建**。

**我们使用PutRecordsRequest对象插入数据**。对于这个新对象，我们添加了一个包含多个PutRecordsRequestEntry对象的列表：

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

我们已经创建了一个基本的消费者和模拟IP记录的生产者。现在剩下要做的就是运行我们的Spring项目并查看应用程序控制台中列出的IP。

## 4. KCL和KPL

**Kinesis Client Library(KCL)是一个简化记录消费的库**。它也是针对Kinesis Data Streams的AWS开发工具包Java API的抽象层。在幕后，该库处理多个实例之间的负载平衡、响应实例故障、检查处理的记录以及对重新分片做出反应。

**Kinesis Producer Library(KPL)是一个可用于写入Kinesis数据流的库**。它还提供了一个位于Kinesis Data Streams的AWS开发工具包Java API之上的抽象层。为了获得更好的性能，该库会自动处理批处理、多线程和重试逻辑。

KCL和KPL的主要优点都是易于使用，因此我们可以专注于生产和消费记录。

### 4.1 Maven依赖项

如果需要的话，这两个库可以在我们的项目中单独引入。要在我们的Maven项目中包含[KPL](https://search.maven.org/search?q=amazon-kinesis-producer)和[KCL](https://central.sonatype.com/artifact/com.amazonaws/amazon-kinesis-client/1.14.10)，我们需要更新我们的pom.xml文件：

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

### 4.2 Spring设置

我们唯一需要的Spring准备工作是确保我们手头有IAM凭证。aws.access.key和aws.secret.key的值在我们的application.properties文件中定义，因此我们可以在需要时使用@Value读取它们。

### 4.3 消费者

首先，我们将**创建一个实现IRecordProcessor接口的类，并定义我们如何处理Kinesis数据流记录的逻辑**，即在控制台中打印它们：

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

下一步是定义一个实现**IRecordProcessorFactory接口**并返回先前创建的**IpProcessor对象的工厂类**：

```java
public class IpProcessorFactory implements IRecordProcessorFactory {
	@Override
	public IRecordProcessor createProcessor() {
		return new IpProcessor();
	}
}
```

现在，**对于最后一步，我们将使用Worker对象来定义我们的消费者管道**。我们需要一个KinesisClientLibConfiguration对象，该对象将在需要时定义IAM凭证和AWS区域。

我们会将KinesisClientLibConfiguration和我们的IpProcessorFactory对象传递给我们的Worker，然后在单独的线程中启动它。我们通过使用Worker类来保持这种消费记录的逻辑始终存在，所以我们现在不断地读取新记录：

```java
BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
KinesisClientLibConfiguration consumerConfig = new KinesisClientLibConfiguration(
  	"KinesisKCLConsumer",
  	IPS_STREAM,
  	"",
  	"",
  	DEFAULT_INITIAL_POSITION_IN_STREAM,
  	new AWSStaticCredentialsProvider(awsCredentials),
  	new AWSStaticCredentialsProvider(awsCredentials),
  	new AWSStaticCredentialsProvider(awsCredentials),
  	DEFAULT_FAILOVER_TIME_MILLIS,
  	"KinesisKCLConsumer",
  	DEFAULT_MAX_RECORDS,
  	DEFAULT_IDLETIME_BETWEEN_READS_MILLIS,
  	DEFAULT_DONT_CALL_PROCESS_RECORDS_FOR_EMPTY_RECORD_LIST,
  	DEFAULT_PARENT_SHARD_POLL_INTERVAL_MILLIS,
  	DEFAULT_SHARD_SYNC_INTERVAL_MILLIS,
  	DEFAULT_CLEANUP_LEASES_UPON_SHARDS_COMPLETION,
  	new ClientConfiguration(),
  	new ClientConfiguration(),
  	new ClientConfiguration(),
  	DEFAULT_TASK_BACKOFF_TIME_MILLIS,
  	DEFAULT_METRICS_BUFFER_TIME_MILLIS,
  	DEFAULT_METRICS_MAX_QUEUE_SIZE,
  	DEFAULT_VALIDATE_SEQUENCE_NUMBER_BEFORE_CHECKPOINTING,
  	Regions.EU_CENTRAL_1.getName(),
  	DEFAULT_SHUTDOWN_GRACE_MILLIS,
  	DEFAULT_DDB_BILLING_MODE,
  	null,
  	0,
  	0, 
  	0
);
final Worker worker = new Worker.Builder()
 	.recordProcessorFactory(new IpProcessorFactory())
 	.config(consumerConfig)
 	.build();
CompletableFuture.runAsync(worker.run());
```

### 4.4 生产者

现在让我们定义KinesisProducerConfiguration对象，添加IAM凭证和AWS区域：

```java
BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
KinesisProducerConfiguration producerConfig = new KinesisProducerConfiguration()
  	.setCredentialsProvider(new AWSStaticCredentialsProvider(awsCredentials))
  	.setVerifyCertificate(false)
  	.setRegion(Regions.EU_CENTRAL_1.getName());

this.kinesisProducer = new KinesisProducer(producerConfig);
```

我们将包含先前在@Scheduled作业中创建的kinesisProducer对象，并持续为我们的Kinesis数据流生成记录：

```java
IntStream.range(1, 200).mapToObj(ipSuffix -> ByteBuffer.wrap(("192.168.0." + ipSuffix).getBytes()))
  	.forEach(entry -> kinesisProducer.addUserRecord(IPS_STREAM, IPS_PARTITION_KEY, entry));
```

## 5. Spring Cloud Stream Binder Kinesis

我们已经看到了两个库，它们都是在Spring生态系统之外创建的。现在，我们将看到**Spring Cloud Stream Binder Kinesis如何在构建于[Spring Cloud Stream](https://www.baeldung.com/spring-cloud-stream)之上的同时进一步简化我们的编码**。

### 5.1 Maven依赖

我们需要在[Spring Cloud Stream Binder Kinesis](https://search.maven.org/search?q=a:spring-cloud-stream-binder-kinesis)的应用程序中定义的Maven依赖项是：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-stream-binder-kinesis</artifactId>
    <version>1.2.1.RELEASE</version>
</dependency>
```

### 5.2 Spring设置

在EC2上运行时，会自动发现所需的AWS属性，因此无需定义它们。由于我们在本地机器上运行我们的示例，因此需要为我们的AWS账户定义我们的IAM访问密钥、私有密钥和区域。我们还为应用程序禁用了自动CloudFormation堆栈名称检测：

```properties
cloud.aws.credentials.access-key=my-aws-access-key
cloud.aws.credentials.secret-key=my-aws-secret-key
cloud.aws.region.static=eu-central-1
cloud.aws.stack.auto=false
```

**Spring Cloud Stream捆绑了我们可以在流绑定中使用的三个接口**：

-   Sink用于数据引入
-   Source用于发布记录
-   Processor是两者的结合

如果需要，我们也可以定义自己的接口。

### 5.3 消费者

定义消费者是一项分为两部分的工作。首先，我们将在application.properties中定义我们将从中消费的数据流：

```properties
spring.cloud.stream.bindings.input-in-0.destination=live-ips
spring.cloud.stream.bindings.input-in-0.group=live-ips-group
spring.cloud.stream.bindings.input-in-0.content-type=text/plain
spring.cloud.stream.function.definition = input
```

接下来，让我们使用**允许我们从Kinesis流中读取的Supplier**来定义一个带有@Bean的Spring @Configuration类：

```java
@Configuration
public class ConsumerBinder {
	@Bean
	Consumer<String> input() {
		return str -> {
			System.out.println(str);
		};
	}
}
```

### 5.4 生产者

生产者也可以一分为二。首先，我们必须在application.properties中定义我们的流属性：

```properties
spring.cloud.stream.bindings.output-out-0.destination=myStream
spring.cloud.stream.bindings.output-out-0.content-type=text/plain
spring.cloud.stream.poller.fixed-delay = 3000
```

然后我们**在Spring @Configuration上添加带有Supplier的@Bean以每隔几秒创建新的测试消息**：

```java
@Configuration 
class ProducerBinder {
	@Bean 
	public Supplier output() {
		return () -> IntStream.range(1, 200)
			.mapToObj(ipSuffix ->"192.168.0." + ipSuffix)
			.map(entry ->MessageBuilder.withPayload(entry)
				.build());
	}
}
```

这就是Spring Cloud Stream Binder Kinesis工作所需的全部内容。我们现在可以简单地启动应用程序。

## 6. 总结

在本文中，我们了解了如何将Spring项目与两个AWS库集成以与Kinesis Data Stream进行交互。我们还了解了如何使用Spring Cloud Stream Binder Kinesis库来简化实现。