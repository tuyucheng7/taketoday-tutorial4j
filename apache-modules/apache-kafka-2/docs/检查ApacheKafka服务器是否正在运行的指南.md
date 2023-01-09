## 1. 概述

使用 Apache Kafka 的客户端应用程序通常属于两个类别之一，即生产者和消费者。生产者和消费者都需要底层的 Kafka 服务器启动并运行，然后才能分别开始生产和消费工作。

在本文中，我们将学习一些策略来确定 Kafka 服务器是否正在运行。

## 2.使用Zookeeper命令

找出是否有活动代理的最快方法之一是使用 Zookeeper 的转储命令。dump命令是可用于管理 Zookeeper 服务器的 [4LW](https://zookeeper.apache.org/doc/r3.4.10/zookeeperAdmin.html#sc_zkCommands)命令之一。

让我们继续使用[nc](https://www.baeldung.com/linux/netcat-command)命令通过监听 2181 端口的 Zookeeper 服务器发送转储命令：

```bash
$ echo dump | nc localhost 2181 | grep -i broker | xargs
/brokers/ids/0
```

在执行该命令时，我们会看到在 Zookeeper 服务器上注册的临时代理 ID 列表。如果不存在临时 ID，则没有代理节点正在运行。

此外，重要的是要注意dump命令需要在zookeeper.properties或zoo.cfg配置文件中通常可用的配置中明确允许：

```bash
lw.commands.whitelist=dump
```

或者，我们也可以使用 Zookeeper API 来查找[活动代理列表](https://www.baeldung.com/ops/kafka-list-active-brokers-in-cluster#zookeeper-apis)。

## 3.使用Apache Kafka的AdminClient

如果我们的生产者或消费者是Java应用程序，那么我们可以使用 Apache Kafka 的[AdminClient](https://kafka.apache.org/28/javadoc/org/apache/kafka/clients/admin/Admin.html)类来了解 Kafka 服务器是否已启动。

让我们定义KafkaAdminClient 类来包装AdminClient类的实例，以便我们可以快速测试我们的代码：

```java
public class KafkaAdminClient {
    private final AdminClient client;

    public KafkaAdminClient(String bootstrap) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrap);
        props.put("request.timeout.ms", 3000);
        props.put("connections.max.idle.ms", 5000);

        this.client = AdminClient.create(props);
    }
}

```

接下来，让我们在KafkaAdminClient类中定义verifyConnection()方法来验证客户端是否可以连接到正在运行的代理服务器：

```java
public boolean verifyConnection() throws ExecutionException, InterruptedException {
    Collection<Node> nodes = this.client.describeCluster()
      .nodes()
      .get();
    return nodes != null && nodes.size() > 0;
}
```

最后，让我们通过连接到正在运行的 Kafka 集群来测试我们的代码：

```java
@Test
void givenKafkaIsRunning_whenCheckedForConnection_thenConnectionIsVerified() throws Exception {
    boolean alive = kafkaAdminClient.verifyConnection();
    assertThat(alive).isTrue();
}
```

## 4. 使用kcat实用程序

我们可以使用[kcat](https://manpages.ubuntu.com/manpages/focal/man1/kafkacat.1.html)(以前称为kafkacat)命令来查明是否有正在运行的 Kafka 代理节点。为此，让我们使用-L选项来显示现有主题的元数据：

```yaml
$ kcat -b localhost:9092 -t demo-topic -L
Metadata for demo-topic (from broker -1: localhost:9092/bootstrap):
 1 brokers:
  broker 0 at 192.168.1.53:9092 (controller)
 1 topics:
  topic "demo-topic" with 1 partitions:
    partition 0, leader 0, replicas: 0, isrs: 0
```

接下来，让我们在代理节点关闭时执行相同的命令：

```matlab
$ kcat -b localhost:9092 -t demo-topic -L -m 1
%3|1660579562.937|FAIL|rdkafka#producer-1| [thrd:localhost:9092/bootstrap]: localhost:9092/bootstrap: Connect to ipv4#127.0.0.1:9092 failed: Connection refused (after 1ms in state CONNECT)
% ERROR: Failed to acquire metadata: Local: Broker transport failure (Are the brokers reachable? Also try increasing the metadata timeout with -m <timeout>?)
```

对于这种情况，我们会收到“连接被拒绝”错误，因为没有正在运行的代理节点。此外，我们必须注意，通过使用-m选项将请求超时限制为 1 秒，我们能够快速失败。

## 5. 使用用户界面工具

对于不需要自动检查的实验性 POC 项目，我们可以依赖[Offset Explorer](https://www.kafkatool.com/)等 UI 工具。但是，如果我们想为企业级 Kafka 客户端验证代理节点的状态，则不建议使用这种方法。

让我们使用 Offset Explorer 使用 Zookeeper 主机和端口详细信息连接到 Kafka 集群：[![使用 Offset Explorer 检查连接](https://www.baeldung.com/wp-content/uploads/2022/08/Offset-Explorer-Check-Connection-1024x580.png)](https://www.baeldung.com/wp-content/uploads/2022/08/Offset-Explorer-Check-Connection.png)

我们可以在左侧窗格中看到正在运行的代理列表。而已。我们只需单击一个按钮即可获得它。

## 六. 总结

在本教程中，我们探索了一些使用 Zookeeper 命令、Apache 的AdminClient和kcat实用程序的命令行方法，然后是基于 UI 的方法来确定 Kafka 服务器是否已启动。