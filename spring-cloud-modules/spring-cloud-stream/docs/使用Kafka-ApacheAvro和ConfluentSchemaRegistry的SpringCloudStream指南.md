## 一、简介

Apache Kafka 是一个消息传递平台。有了它，我们可以在不同的应用程序之间大规模交换数据。

Spring Cloud Stream 是一个用于构建消息驱动应用程序的框架。它可以简化将 Kafka 集成到我们的服务中。

通常，Kafka 与 Avro 消息格式一起使用，由模式注册表支持。在本教程中，我们将使用 Confluent Schema Registry。我们将尝试 Spring 与 Confluent Schema Registry 集成的实现以及 Confluent 本机库。

## 2. Confluent Schema 注册表

Kafka 将所有数据表示为字节，因此通常使用外部模式并根据该模式序列化和反序列化为字节。与其为每条消息提供该模式的副本（这将是一项昂贵的开销），不如将模式保存在注册表中并为每条消息提供一个 ID 也很常见。

Confluent Schema Registry 提供了一种简单的方法来存储、检索和管理模式。它公开了几个有用的[RESTful API](https://docs.confluent.io/current/schema-registry/develop/api.html)。

模式按主题存储，默认情况下，注册表会在允许针对主题上传新模式之前进行兼容性检查。

每个生产者都知道它使用的模式，每个消费者都应该能够使用任何格式的数据，或者应该有一个它更喜欢读入的特定模式。生产者咨询注册表以建立正确的 ID，以便在发送消息时使用信息。消费者使用注册表来获取发送者的模式。 

当消费者知道发送者的模式和自己想要的消息格式时，Avro 库可以将数据转换成消费者想要的格式。

## 3.阿帕奇阿夫罗

[Apache Avro](https://www.baeldung.com/java-apache-avro)是一个数据序列化系统。

它使用 JSON 结构来定义模式，提供字节和结构化数据之间的序列化。

Avro 的一个优势是它支持将在一个模式版本中编写的消息演变为由兼容的替代模式定义的格式。

Avro 工具集还能够生成类来表示这些模式的数据结构，从而使序列化进出 POJO 变得容易。

## 4.设置项目

要将模式注册表与[Spring Cloud Stream](https://search.maven.org/search?q=g:org.springframework.cloud AND a:spring-cloud-dependencies&core=gav)一起使用，我们需要[Spring Cloud Kafka Binder](https://search.maven.org/search?q=a:spring-cloud-stream-binder-kafka)和[模式注册表](https://search.maven.org/search?q=g:org.springframework.cloud AND a:spring-cloud-stream-schema)Maven 依赖项：

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

对于[Confluent 的序列化器](https://docs.confluent.io/1.0/installation.html?highlight=maven#installation-maven)，我们需要：

```xml
<dependency>
    <groupId>io.confluent</groupId>
    <artifactId>kafka-avro-serializer</artifactId>
    <version>4.0.0</version>
</dependency>
```

Confluent 的 Serializer 在他们的仓库中：

```xml
<repositories>
    <repository>
        <id>confluent</id>
        <url>https://packages.confluent.io/maven/</url>
    </repository>
</repositories>
```

另外，让我们使用[Maven 插件](https://search.maven.org/search?q=g:org.apache.avro AND a:avro-maven-plugin&core=gav)来生成 Avro 类：

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

对于测试，我们可以使用现有的 Kafka 和 Schema Registry 设置或使用[dockerized Confluent 和 Kafka。](https://hub.docker.com/r/confluent/kafka)

## 5.春云流

现在我们已经设置了项目，接下来让我们使用[Spring Cloud Stream](https://www.baeldung.com/spring-cloud-stream)编写一个生产者。它将发布有关某个主题的员工详细信息。

然后，我们将创建一个消费者，它将从主题中读取事件并将它们写在日志语句中。

### 5.1. 图式

首先，让我们为员工详细信息定义一个架构。我们可以将其命名为employee-schema.avsc。

我们可以将模式文件保存在src/main/resources 中：

```plaintext
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
    }]
}
```

创建上述模式后，我们需要构建项目。然后，Apache Avro 代码生成器将在包com.baeldung.schema下创建一个名为Employee的 POJO 。

### 5.2. 制作人

Spring Cloud Stream 提供了Processor接口。这为我们提供了一个输出和输入通道。

让我们用它来制作一个生产者，将Employee对象发送到员工详细信息Kafka 主题：

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

### 5.2. 消费者

现在，让我们写下我们的消费者：

```java
@StreamListener(Processor.INPUT)
public void consumeEmployeeDetails(Employee employeeDetails) {
    logger.info("Let's process employee details: {}", employeeDetails);
}
```

该消费者将阅读在员工详细信息主题上发布的事件。让我们将它的输出定向到日志，看看它做了什么。

### 5.3. 卡夫卡绑定

到目前为止，我们只处理 了处理器对象的输入和 输出通道。这些通道需要配置正确的目的地。

让我们使用application.yml来提供 Kafka 绑定：

```plaintext
spring:
  cloud:
    stream: 
      bindings:
        input:
          destination: employee-details
          content-type: application/+avro
        output:
          destination: employee-details
          content-type: application/+avro
```

我们应该注意，在这种情况下，目的地 是指 Kafka 主题。它被称为目的地可能有点令人困惑，因为在这种情况下它是输入源，但它是消费者和生产者之间的一致术语。

### 5.4. 入口点

现在我们有了生产者和消费者，让我们公开一个 API 来获取用户的输入并将其传递给生产者：

```java
@Autowired
private AvroProducer avroProducer;

@PostMapping("/employees/{id}/{firstName}/{lastName}")
public String producerAvroMessage(@PathVariable int id, @PathVariable String firstName, 
  @PathVariable String lastName) {
    avroProducer.produceEmployeeDetails(id, firstName, lastName);
    return "Sent employee details to consumer";
}
```

### 5.5. 启用 Confluent Schema 注册表和绑定

最后，为了使我们的应用程序同时应用 Kafka 和模式注册表绑定，我们需要在我们的配置类之一上添加@EnableBinding和@EnableSchemaRegistryClient ：

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

endPoint是 Confluent Schema Registry 的 URL 。

### 5.6. 测试我们的服务

让我们用 POST 请求测试服务：

```plaintext
curl -X POST localhost:8080/employees/1001/Harry/Potter
```

日志告诉我们这是有效的：

```plaintext
2019-06-11 18:45:45.343  INFO 17036 --- [container-0-C-1] com.baeldung.consumer.AvroConsumer       : Let's process employee details: {"id": 1001, "firstName": "Harry", "lastName": "Potter"}
```

### 5.7. 处理过程中发生了什么？

让我们尝试了解我们的示例应用程序到底发生了什么：

1.  生产者使用Employee对象构建 Kafka 消息
2.  生产者向模式注册表注册员工模式以获取模式版本 ID，这将创建一个新 ID 或为该确切模式重用现有 ID
3.  Avro 使用模式序列化了Employee对象
4.  Spring Cloud 将 schema-id 放在消息头中
5.  该消息已在该主题上发布
6.  当消息到达消费者时，它从标题中读取模式 ID
7.  消费者使用 schema-id从注册表中获取Employee模式
8.  消费者找到一个可以表示该对象的本地类并将消息反序列化到其中

## 6. 使用原生 Kafka 库进行序列化/反序列化

Spring Boot 提供了一些开箱即用的消息转换器。默认情况下，Spring Boot 使用Content-Type标头来选择合适的消息转换器。

在我们的示例中，Content-Type是application/+avro，因此它使用AvroSchemaMessageConverter 来读写 Avro 格式。但是，Confluent 建议使用KafkaAvroSerializer和KafkaAvroDeserializer进行消息转换。

虽然 Spring 自己的格式运行良好，但它在分区方面有一些缺点，并且它不能与 Confluent 标准互操作，而我们的 Kafka 实例上的一些非 Spring 服务可能需要这样。

让我们更新application.yml以使用 Confluent 转换器：

```plaintext
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
          content-type: application/+avro
        output:
          destination: employee-details
          content-type: application/+avro
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

我们启用了useNativeEncoding。它强制 Spring Cloud Stream 将序列化委托给提供的类。

我们还应该知道如何使用kafka.binder.producer-properties和 kafka.binder.consumer-properties 在 Spring Cloud 中为 Kafka 提供本机设置属性。

## 7.消费组和分区

消费者组是属于同一应用程序的一组消费者。来自同一个Consumer Group的消费者共享同一个组名。

让我们更新application.yml以添加消费者组名称：

```plaintext
spring:
  cloud:
    stream:
      // ...     
      bindings:
        input:
          destination: employee-details
          content-type: application/+avro
          group: group-1
      // ...
```

所有的消费者在它们之间平均分配主题分区。不同分区中的消息将被并行处理。

在一个消费者组中，一次读取消息的最大消费者数等于分区数。因此我们可以配置分区和消费者的数量以获得所需的并行度。一般来说，我们的分区数应该多于我们服务所有副本的消费者总数。

### 7.1. 分区键

在处理我们的消息时，它们的处理顺序可能很重要。当我们的消息被并行处理时，处理的顺序将很难控制。

Kafka 提供了这样的规则，即在给定的分区中，消息始终按照它们到达的顺序进行处理。因此，如果以正确的顺序处理某些消息很重要，我们会确保它们彼此位于同一分区中。

我们可以在向主题发送消息时提供分区键。具有相同分区键的消息将始终转到相同的分区。如果分区键不存在，消息将以循环方式进行分区。

让我们试着用一个例子来理解这一点。想象一下，我们正在接收一个员工的多条消息，我们想要按顺序处理一个员工的所有消息。部门名称和员工id可以唯一标识一个员工。

因此，让我们使用员工 ID 和部门名称定义分区键：

```javascript
{
    "type": "record",
    "name": "EmployeeKey",
    "namespace": "com.baeldung.schema",
    "fields": [
     {
        "name": "id",
        "type": "int"
    },
    {
        "name": "departmentName",
        "type": "string"
    }]
}
```

构建项目后，EmployeeKey POJO 将在包com.baeldung.schema下生成。

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

现在，同一个分区将收到具有相同员工 ID 和部门名称的消息。

### 7.2. 消费者并发

Spring Cloud Stream 允许我们在application.yml中为消费者设置并发：

```plaintext
spring:
  cloud:
    stream:
      // ... 
      bindings:
        input:
          destination: employee-details
          content-type: application/+avro
          group: group-1
          concurrency: 3
```

现在我们的消费者将同时阅读主题中的三条消息。换句话说，Spring 将生成三个不同的线程来独立使用。

## 八、结论

在本文中，我们将Apache Kafka 的生产者和消费者与 Avro 模式和 Confluent Schema Registry集成在一起。

我们在单个应用程序中执行此操作，但生产者和消费者可以部署在不同的应用程序中，并且能够拥有自己的模式版本，并通过注册表保持同步。

我们了解了如何使用Spring 的 Avro 和 Schema Registry 客户端实现，然后我们了解了如何切换到序列化和反序列化的Confluent 标准实现以实现互操作性。

最后，我们研究了如何划分我们的主题并确保我们拥有正确的消息密钥以实现消息的安全并行处理。