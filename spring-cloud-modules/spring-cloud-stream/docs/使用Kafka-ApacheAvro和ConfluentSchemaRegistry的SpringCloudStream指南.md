## 1. 简介

**Apache Kafka是一个消息传递平台**。有了它，我们可以在不同的应用程序之间大规模交换数据。

Spring Cloud Stream是一个用于构建消息驱动应用程序的框架。**它可以简化Kafka与我们的服务的集成**。

通常，Kafka与Avro消息格式一起使用，由模式注册表支持。在本教程中，我们将使用Confluent Schema Registry。我们将尝试Spring与Confluent Schema Registry集成的实现以及Confluent原生库。

## 2. Confluent Schema Registry

Kafka将所有数据表示为字节，因此通常**使用外部模式并根据该模式序列化和反序列化为字节**。与其为每条消息提供该模式的副本(这将是一项昂贵的开销)，不如将模式保存在注册表中并为每条消息提供一个ID。

Confluent Schema Registry提供了一种简单的方法来存储、检索和管理模式。它公开了几个有用的[RESTful API](https://docs.confluent.io/current/schema-registry/develop/api.html)。

模式按主题存储，默认情况下，注册表会在允许针对主题上传新模式之前进行兼容性检查。

每个生产者都知道它使用的模式，每个消费者都应该能够消费任何格式的数据，或者应该有一个它更喜欢读入的特定模式。**生产者咨询注册表以建立正确的ID，以便在发送消息时消费信息。消费者使用注册表来获取发送者的模式**。 

当消费者知道发送者的模式和自己想要的消息格式时，Avro库可以将数据转换成消费者想要的格式。

## 3. Apache Avro

**[Apache Avro](https://www.baeldung.com/java-apache-avro)是一个数据序列化系统**。

它使用JSON结构来定义模式，提供字节和结构化数据之间的序列化。

Avro的一个优势是它支持将在一个模式版本中编写的消息演变为由兼容的替代模式定义的格式。

Avro工具集还能够生成类来表示这些模式的数据结构，从而可以轻松地在POJO中序列化。

## 4. 设置项目

要将模式注册表与[Spring Cloud Stream](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-dependencies/2022.0.1)一起使用，我们需要[Spring Cloud Kafka Binder](https://search.maven.org/search?q=a:spring-cloud-stream-binder-kafka)和[模式注册表](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-stream-schema/2.2.1.RELEASE)Maven依赖项：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-stream-binder-kafka</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-stream-schema</artifactId>
</dependency>
```

对于[Confluent的序列化器](https://docs.confluent.io/1.0/installation.html?highlight=maven#installation-maven)，我们需要：

```xml
<dependency>
    <groupId>io.confluent</groupId>
    <artifactId>kafka-avro-serializer</artifactId>
    <version>4.0.0</version>
</dependency>
```

Confluent的序列化器在他们的仓库中：

```xml
<repositories>
    <repository>
        <id>confluent</id>
        <url>https://packages.confluent.io/maven/</url>
    </repository>
</repositories>
```

另外，让我们使用[Maven插件](https://central.sonatype.com/artifact/org.apache.avro/avro-maven-plugin/1.11.1)来生成Avro类：

```xml
<build>
	<plugins>
		<plugin>
			<groupId>org.apache.avro</groupId>
			<artifactId>avro-maven-plugin</artifactId>
			<version>1.8.2</version>
			<executions>
				<execution>
					<id>schemas</id>
					<phase>generate-sources</phase>
					<goals>
						<goal>schema</goal>
						<goal>protocol</goal>
						<goal>idl-protocol</goal>
					</goals>
					<configuration>
						<sourceDirectory>${project.basedir}/src/main/resources/</sourceDirectory>
						<outputDirectory>${project.basedir}/src/main/java/</outputDirectory>
					</configuration>
				</execution>
			</executions>
		</plugin>
	</plugins>
</build>
```

对于测试，我们可以使用现有的Kafka和Schema Registry设置或使用[容器化的Confluent和Kafka](https://hub.docker.com/r/confluent/kafka)。

## 5. Spring Cloud Stream

现在我们已经设置好了项目，接下来让我们使用[Spring Cloud Stream](https://www.baeldung.com/spring-cloud-stream)编写一个生产者。它将发布有关某个主题的员工详细信息。

然后，我们将创建一个消费者，它将从主题中读取事件并将它们写在日志语句中。

### 5.1 模式

首先，让我们为员工详细信息定义一个模式。我们可以将其命名为employee-schema.avsc。

我们可以将模式文件保存在src/main/resources中：

```json
{
	"type": "record",
	"name": "Employee",
	"namespace": "com.baeldung.schema",
	"fields": [
		{
			"name": "id",
			"type": "int"
		},
		{
			"name": "firstName",
			"type": "string"
		},
		{
			"name": "lastName",
			"type": "string"
		}
	]
}
```

创建上述模式后，我们需要构建项目。然后，Apache Avro代码生成器将在包cn.tuyucheng.taketoday.schema下创建一个名为Employee的POJO。

### 5.2 生产者

Spring Cloud Stream提供了Processor接口。这为我们提供了一个输出和输入通道。

让我们使用它来创建一个生产者，将Employee对象发送到employee-details Kafka主题：

```java
@Autowired
private Processor processor;

public void produceEmployeeDetails(int empId, String firstName, String lastName) {
    // creating employee details
    Employee employee = new Employee();
    employee.setId(empId);
    employee.setFirstName(firstName);
    employee.setLastName(lastName);

    Message<Employee> message = MessageBuilder.withPayload(employee)
    	.build();

    processor.output()
    	.send(message);
}
```

### 5.2 消费者

现在，让我们编写我们的消费者：

```java
@StreamListener(Processor.INPUT)
public void consumeEmployeeDetails(Employee employeeDetails) {
    logger.info("Let's process employee details: {}", employeeDetails);
}
```

该消费者将阅读在employee-detail主题上发布的事件。让我们将它的输出定向到日志，看看它做了什么。

### 5.3 Kafka绑定

到目前为止，我们只处理了处理器对象的输入和输出通道。这些通道需要配置正确的目的地。

让我们使用application.yml来提供Kafka绑定：

```yaml
spring:
    cloud:
        stream:
            bindings:
                input:
                    destination: employee-details
                    content-type: application/*+avro
                output:
                    destination: employee-details
                    content-type: application/*+avro
```

我们应该注意，在这种情况下，目的地是指Kafka主题。它被称为目的地可能有点令人困惑，因为在这种情况下它是输入源，但它是消费者和生产者之间的一致术语。

### 5.4 入口点

现在我们有了生产者和消费者，让我们公开一个API来获取用户的输入并将其传递给生产者：

```java
@Autowired
private AvroProducer avroProducer;

@PostMapping("/employees/{id}/{firstName}/{lastName}")
public String producerAvroMessage(@PathVariable int id, @PathVariable String firstName, @PathVariable String lastName) {
    avroProducer.produceEmployeeDetails(id, firstName, lastName);
    return "Sent employee details to consumer";
}
```

### 5.5 启用Confluent Schema Registry和绑定

最后，为了使我们的应用程序同时应用Kafka和模式注册表绑定，我们需要在我们的配置类之一上添加@EnableBinding和@EnableSchemaRegistryClient：

```java
@SpringBootApplication
@EnableBinding(Processor.class)
// The @EnableSchemaRegistryClient annotation needs to be uncommented to use the Spring native method.
// @EnableSchemaRegistryClient
public class AvroKafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AvroKafkaApplication.class, args);
	}
}
```

我们应该提供一个ConfluentSchemaRegistryClient bean：

```java
@Value("${spring.cloud.stream.kafka.binder.producer-properties.schema.registry.url}")
private String endPoint;

@Bean
public SchemaRegistryClient schemaRegistryClient() {
    ConfluentSchemaRegistryClient client = new ConfluentSchemaRegistryClient();
    client.setEndpoint(endPoint);
    return client;
}
```

endPoint是Confluent Schema Registry的URL。

### 5.6 测试我们的服务

让我们使用POST请求测试服务：

```shell
curl -X POST localhost:8080/employees/1001/Harry/Potter
```

日志告诉我们这是有效的：

```shell
2019-06-11 18:45:45.343  INFO 17036 --- [container-0-C-1] cn.tuyucheng.taketoday.consumer.AvroConsumer: Let's process employee details: {"id": 1001, "firstName": "Harry", "lastName": "Potter"}
```

### 5.7 处理过程中发生了什么？

让我们尝试了解我们的示例应用程序到底发生了什么：

1.  生产者使用Employee对象构建Kafka消息
2.  生产者向模式注册表注册employee模式以获取模式版本ID，这将创建一个新ID或为该确切模式重用现有ID
3.  Avro使用模式序列化了Employee对象
4.  Spring Cloud将schema-id放在消息头中
5.  该消息已在该主题上发布
6.  当消息到达消费者时，它从标头中读取模式ID
7.  消费者使用schema-id从注册表中获取employee模式
8.  消费者找到一个可以表示该对象的本地类并将消息反序列化到其中

## 6. 使用原生Kafka库进行序列化/反序列化

Spring Boot提供了一些开箱即用的消息转换器。**默认情况下，Spring Boot使用Content-Type标头来选择合适的消息转换器**。

在我们的示例中，Content-Type是application/*+avro，因此它使用AvroSchemaMessageConverter来读写Avro格式。但是，**Confluent建议使用KafkaAvroSerializer和KafkaAvroDeserializer进行消息转换**。

虽然Spring自己的格式运行良好，但它在分区方面有一些缺点，并且它不能与Confluent标准互操作，而我们的Kafka实例上的一些非Spring服务可能需要这样。

让我们更新application.yml以使用Confluent转换器：

```yaml
spring:
    cloud:
        stream:
            default:
                producer:
                    useNativeEncoding: true
                consumer:
                    useNativeEncoding: true
            bindings:
                input:
                    destination: employee-details
                    content-type: application/*+avro
                output:
                    destination: employee-details
                    content-type: application/*+avro
            kafka:
                binder:
                    producer-properties:
                        key.serializer: io.confluent.kafka.serializers.KafkaAvroSerializer
                        value.serializer: io.confluent.kafka.serializers.KafkaAvroSerializer
                        schema.registry.url: http://localhost:8081
                    consumer-properties:
                        key.deserializer: io.confluent.kafka.serializers.KafkaAvroDeserializer
                        value.deserializer: io.confluent.kafka.serializers.KafkaAvroDeserializer
                        schema.registry.url: http://localhost:8081
                        specific.avro.reader: true
```

我们启用了useNativeEncoding。它强制Spring Cloud Stream将序列化委托给提供的类。

我们还应该知道如何使用kafka.binder.producer-properties和kafka.binder.consumer-properties在Spring Cloud中为Kafka提供本机设置属性。

## 7. 消费者组和分区

**消费者组是属于同一应用程序的一组消费者**。来自同一个消费者组的消费者共享同一个组名。

让我们更新application.yml以添加消费者组名称：

```yaml
spring:
    cloud:
        stream:
            # ...
            bindings:
                input:
                    destination: employee-details
                    content-type: application/*+avro
                    group: group-1
            # ...
```

所有的消费者在它们之间平均分配主题分区。不同分区中的消息将被并行处理。

**在一个消费者组中，一次读取消息的最大消费者数等于分区数**。因此我们可以配置分区和消费者的数量以获得所需的并行度。一般来说，我们的分区数应该多于我们服务所有副本的消费者总数。

### 7.1 分区键

在处理我们的消息时，它们的处理顺序可能很重要。当我们的消息被并行处理时，处理的顺序将很难控制。

Kafka提供了这样的规则，即**在给定的分区中，消息始终按照它们到达的顺序进行处理**。因此，如果以正确的顺序处理某些消息很重要，我们会确保它们彼此位于同一分区中。

我们可以在向主题发送消息时提供分区键。**具有相同分区键的消息将始终转到相同的分区**。如果分区键不存在，将以轮循机制方式对消息进行分区。

让我们尝试通过一个例子来理解这一点。想象一下，我们正在接收一个员工的多条消息，我们想要按顺序处理一个员工的所有消息。部门名称和员工id可以唯一标识一个员工。

因此，让我们使用员工id和部门名称定义分区键：

```json
{
	"type": "record",
	"name": "EmployeeKey",
	"namespace": "cn.tuyucheng.taketoday.schema",
	"fields": [
		{
			"name": "id",
			"type": "int"
		},
		{
			"name": "departmentName",
			"type": "string"
		}
	]
}
```

构建项目后，EmployeeKey POJO将在包cn.tuyucheng.taketoday.schema下生成。

让我们更新生产者以使用EmployeeKey作为分区键：

```java
public void produceEmployeeDetails(int empId, String firstName, String lastName) {
    // creating employee details
    Employee employee = new Employee();
    employee.setId(empId);
    // ...

    // creating partition key for kafka topic
    EmployeeKey employeeKey = new EmployeeKey();
    employeeKey.setId(empId);
    employeeKey.setDepartmentName("IT");

    Message<Employee> message = MessageBuilder.withPayload(employee)
        .setHeader(KafkaHeaders.MESSAGE_KEY, employeeKey)
        .build();

    processor.output()
        .send(message);
}
```

在这里，我们将分区键放在消息标头中。

现在，同一个分区将收到具有相同员工id和部门名称的消息。

### 7.2 消费者并发

Spring Cloud Stream允许我们在application.yml中为消费者设置并发：

```yaml
spring:
    cloud:
        stream:
            # ...
            bindings:
                input:
                    destination: employee-details
                    content-type: application/*+avro
                    group: group-1
                    concurrency: 3
```

现在我们的消费者将同时读取主题中的3条消息。换句话说，Spring将生成3个不同的线程来独立消费。

## 8. 总结

在本文中，我们**将Apache Kafka的生产者和消费者与Avro模式和Confluent Schema Registry集成在一起**。

我们在单个应用程序中执行此操作，但生产者和消费者可以部署在不同的应用程序中，并且能够拥有自己的模式版本，并通过注册表保持同步。

我们了解了如何使用**Spring的Avro和Schema Registry客户端实现**，然后我们了解了如何切换到序列化和反序列化的**Confluent标准实现**以实现互操作性。

最后，我们研究了如何对主题进行分区并确保我们拥有正确的消息键以实现消息的安全并行处理。