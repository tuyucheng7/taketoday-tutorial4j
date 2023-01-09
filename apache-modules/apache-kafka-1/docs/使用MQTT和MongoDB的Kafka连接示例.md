## 1. 概述

在[上一篇文章中](https://www.baeldung.com/kafka-connectors-guide)，我们快速介绍了 Kafka Connect，包括不同类型的连接器、Connect 的基本功能以及 REST API。

在本教程中，我们将使用 Kafka 连接器构建一个更“真实世界”的示例。

我们将使用连接器通过 MQTT 收集数据，并将收集的数据写入 MongoDB。

## 2. 使用 Docker 设置

我们将使用[Docker Compose](https://docs.docker.com/compose/)来设置基础设施。这包括作为源的 MQTT 代理、Zookeeper、一个 Kafka 代理以及作为中间件的 Kafka Connect，最后是一个包含 GUI 工具作为接收器的 MongoDB 实例。

### 2.1. 连接器安装

我们的示例所需的连接器(MQTT 源和 MongoDB 接收器连接器)不包含在普通 Kafka 或 Confluent 平台中。

正如我们在上一篇文章中讨论的那样，我们可以从 Confluent 中心下载连接器( [MQTT](https://www.confluent.io/connector/kafka-connect-mqtt/)和[MongoDB )。](https://www.confluent.io/connector/kafka-connect-mongodb-sink/)之后，我们必须将 jars 解压到一个文件夹中，我们将在下一节中将其安装到 Kafka Connect 容器中。

让我们为此使用文件夹/tmp/custom/jars。在下一节中启动组合堆栈之前，我们必须将 jars 移到那里，因为 Kafka Connect 在启动期间在线加载连接器。

### 2.2. Docker 组合文件

我们将我们的设置描述为一个简单的 Docker 组合文件，其中包含六个容器：

```xml
version: '3.3'

services:
  mosquitto:
    image: eclipse-mosquitto:1.5.5
    hostname: mosquitto
    container_name: mosquitto
    expose:
      - "1883"
    ports:
      - "1883:1883"
  zookeeper:
    image: zookeeper:3.4.9
    restart: unless-stopped
    hostname: zookeeper
    container_name: zookeeper
    ports:
      - "2181:2181"
    environment:
        ZOO_MY_ID: 1
        ZOO_PORT: 2181
        ZOO_SERVERS: server.1=zookeeper:2888:3888
    volumes:
      - ./zookeeper/data:/data
      - ./zookeeper/datalog:/datalog
  kafka:
    image: confluentinc/cp-kafka:5.1.0
    hostname: kafka
    container_name: kafka
    ports:
      - "9092:9092"
    environment:
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092
      KAFKA_ZOOKEEPER_CONNECT: "zookeeper:2181"
      KAFKA_BROKER_ID: 1
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    volumes:
      - ./kafka/data:/var/lib/kafka/data
    depends_on:
      - zookeeper
  kafka-connect:
    image: confluentinc/cp-kafka-connect:5.1.0
    hostname: kafka-connect
    container_name: kafka-connect
    ports:
      - "8083:8083"
    environment:
      CONNECT_BOOTSTRAP_SERVERS: "kafka:9092"
      CONNECT_REST_ADVERTISED_HOST_NAME: connect
      CONNECT_REST_PORT: 8083
      CONNECT_GROUP_ID: compose-connect-group
      CONNECT_CONFIG_STORAGE_TOPIC: docker-connect-configs
      CONNECT_OFFSET_STORAGE_TOPIC: docker-connect-offsets
      CONNECT_STATUS_STORAGE_TOPIC: docker-connect-status
      CONNECT_KEY_CONVERTER: org.apache.kafka.connect.json.JsonConverter
      CONNECT_VALUE_CONVERTER: org.apache.kafka.connect.json.JsonConverter
      CONNECT_INTERNAL_KEY_CONVERTER: "org.apache.kafka.connect.json.JsonConverter"
      CONNECT_INTERNAL_VALUE_CONVERTER: "org.apache.kafka.connect.json.JsonConverter"
      CONNECT_CONFIG_STORAGE_REPLICATION_FACTOR: "1"
      CONNECT_OFFSET_STORAGE_REPLICATION_FACTOR: "1"
      CONNECT_STATUS_STORAGE_REPLICATION_FACTOR: "1"
      CONNECT_PLUGIN_PATH: '/usr/share/java,/etc/kafka-connect/jars'
      CONNECT_CONFLUENT_TOPIC_REPLICATION_FACTOR: 1
    volumes:
      - /tmp/custom/jars:/etc/kafka-connect/jars
    depends_on:
      - zookeeper
      - kafka
      - mosquitto
  mongo-db:
    image: mongo:4.0.5
    hostname: mongo-db
    container_name: mongo-db
    expose:
      - "27017"
    ports:
      - "27017:27017"
    command: --bind_ip_all --smallfiles
    volumes:
      - ./mongo-db:/data
  mongoclient:
    image: mongoclient/mongoclient:2.2.0
    container_name: mongoclient
    hostname: mongoclient
    depends_on:
      - mongo-db
    ports:
      - 3000:3000
    environment:
      MONGO_URL: "mongodb://mongo-db:27017"
      PORT: 3000
    expose:
      - "3000"
```

mosquitto容器提供了一个基于 Eclipse Mosquitto 的简单 MQTT 代理。 

容器 zookeeper和kafka定义了单节点 Kafka 集群。

kafka-connect以分布式模式定义我们的 Connect 应用程序。

最后，mongo-db定义了我们的 sink 数据库，以及基于 web 的 mongoclient，它帮助我们验证发送的数据是否正确到达数据库。

我们可以使用以下命令启动堆栈：

```bash
docker-compose up
```

## 3.连接器配置

由于 Kafka Connect 现已启动并运行，我们现在可以配置连接器。

### 3.1. 配置源连接器

让我们使用 REST API 配置源连接器：

```bash
curl -d @<path-to-config-file>/connect-mqtt-source.json -H "Content-Type: application/json" -X POST http://localhost:8083/connectors
```

我们的 connect-mqtt-source.json 文件如下所示：

```javascript
{
    "name": "mqtt-source",
    "config": {
        "connector.class": "io.confluent.connect.mqtt.MqttSourceConnector",
        "tasks.max": 1,
        "mqtt.server.uri": "tcp://mosquitto:1883",
        "mqtt.topics": "baeldung",
        "kafka.topic": "connect-custom",
        "value.converter": "org.apache.kafka.connect.converters.ByteArrayConverter",
        "confluent.topic.bootstrap.servers": "kafka:9092",
        "confluent.topic.replication.factor": 1
    }
}
```

有一些我们以前没有使用过的属性：

-   mqtt.server.uri是我们的连接器将连接到的端点
-   mqtt.topics是我们的连接器将订阅的 MQTT 主题
-   kafka.topic定义连接器将接收到的数据发送到的 Kafka 主题
-   value.converter定义了一个转换器，它将应用于接收到的有效负载。我们需要ByteArrayConverter，因为 MQTT 连接器默认使用 Base64，而我们想使用纯文本
-   最新版本的连接器需要confluent.topic.bootstrap.servers
-   这同样适用于 confluent.topic.replication.factor：它定义了 Confluent 内部主题的因子——因为我们的集群中只有一个节点，我们必须将该值设置为 1

### 3.2. 测试源连接器

让我们通过向 MQTT 代理发布一条短消息来运行快速测试：

```bash
docker run 
-it --rm --name mqtt-publisher --network 04_custom_default 
efrecon/mqtt-client 
pub -h mosquitto  -t "baeldung" -m "{"id":1234,"message":"This is a test"}"
```

如果我们听主题， connect-custom：

```bash
docker run 
--rm 
confluentinc/cp-kafka:5.1.0 
kafka-console-consumer --network 04_custom_default --bootstrap-server kafka:9092 --topic connect-custom --from-beginning
```

然后我们应该看到我们的测试消息。

### 3.3. 设置接收器连接器

接下来，我们需要我们的接收器连接器。让我们再次使用 REST API：

```bash
curl -d @<path-to-config file>/connect-mongodb-sink.json -H "Content-Type: application/json" -X POST http://localhost:8083/connectors
```

我们的 connect-mongodb-sink.json文件如下所示：

```javascript
{
    "name": "mongodb-sink",
    "config": {
        "connector.class": "at.grahsl.kafka.connect.mongodb.MongoDbSinkConnector",
        "tasks.max": 1,
        "topics": "connect-custom",
        "mongodb.connection.uri": "mongodb://mongo-db/test?retryWrites=true",
        "mongodb.collection": "MyCollection",
        "key.converter": "org.apache.kafka.connect.json.JsonConverter",
        "key.converter.schemas.enable": false,
        "value.converter": "org.apache.kafka.connect.json.JsonConverter",
        "value.converter.schemas.enable": false
    }
}
```

我们这里有以下 MongoDB 特定的属性：

-   mongodb.connection.uri包含我们的 MongoDB 实例的连接字符串
-   mongodb.collection定义集合
-   由于 MongoDB 连接器需要 JSON，我们必须为key.converter和value.converter设置JsonConverter
-   而且我们还需要 MongoDB 的无模式 JSON，所以我们必须将key.converter.schemas.enable和value.converter.schemas.enable设置为false

### 3.4. 测试接收器连接器

由于我们的主题connect-custom已经包含来自 MQTT 连接器测试的消息，MongoDB 连接器应该在创建后直接获取它们。

因此，我们应该立即在我们的 MongoDB 中找到它们。我们可以为此使用 Web 界面，方法是打开 URL [http://localhost:3000/](http://localhost:3000/)。 登录后，我们可以在左侧选择我们的MyCollection ，点击Execute，我们的测试消息应该会显示出来。

### 3.5. 端到端测试

现在，我们可以使用 MQTT 客户端发送任何 JSON 结构：

```javascript
{
    "firstName": "John",
    "lastName": "Smith",
    "age": 25,
    "address": {
        "streetAddress": "21 2nd Street",
        "city": "New York",
        "state": "NY",
        "postalCode": "10021"
    },
    "phoneNumber": [{
        "type": "home",
        "number": "212 555-1234"
    }, {
        "type": "fax",
        "number": "646 555-4567"
    }],
    "gender": {
        "type": "male"
    }
}
```

MongoDB 支持无架构的 JSON 文档，并且当我们为转换器禁用架构时，任何结构都会立即通过我们的连接器链传递并存储在数据库中。

[同样，我们可以使用位于http://localhost:3000/](http://localhost:3000/)的 Web 界面。

### 3.6. 清理

完成后，我们可以清理我们的实验并移除两个连接器：

```bash
curl -X DELETE http://localhost:8083/connectors/mqtt-source
curl -X DELETE http://localhost:8083/connectors/mongodb-sink
```

之后，我们可以使用Ctrl + C关闭 Compose 堆栈。

## 4. 总结

在本教程中，我们使用 Kafka Connect 构建了一个示例，以通过 MQTT 收集数据，并将收集的数据写入 MongoDB。