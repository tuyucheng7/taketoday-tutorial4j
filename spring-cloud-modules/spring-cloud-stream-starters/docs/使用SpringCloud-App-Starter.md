## 1. 简介

在本文中，我们将演示如何使用Spring Cloud App Starters-它提供了引导式和现成的应用程序，可以作为未来开发的起点。

简而言之，Task App Starters专用于数据库迁移和分布式测试等用例，而Stream App Starters提供与外部系统的集成。

总体而言，有超过55个Starters；在[此处](https://cloud.spring.io/spring-cloud-task-app-starters/)和[此处](https://cloud.spring.io/spring-cloud-stream-app-starters/)查看官方文档以获取有关这两者的更多信息。

接下来，我们将构建一个小型分布式Twitter应用程序，它将Twitter帖子流式传输到Hadoop分布式文件系统中。

## 2. 开始设置

我们将使用consumer-key和access-token来创建一个简单的Twitter应用程序。

然后，我们设置Hadoop，以便为未来的大数据目的保留我们的Twitter流。

最后，我们可以选择使用提供的Spring GitHub仓库使用Maven编译和组装sources–processors-sinks架构模式的独立组件，或者通过它们的Spring Stream绑定接口组合sources、processors和sinks。

我们将介绍执行此操作的两种方法。

值得注意的是，以前，所有Stream App Starters都被整理到一个大型仓库中，位于[github.com/spring-cloud/spring-cloud-stream-app-starters](https://github.com/spring-cloud/spring-cloud-stream-app-starters/tree/master/hdfs/spring-cloud-starter-stream-sink-hdfs)。每个Starter都经过简化和隔离。

## 3. 推特凭证

首先，让我们设置我们的Twitter开发人员凭据。要获取Twitter开发人员凭据，请按照[官方Twitter开发人员文档](https://apps.twitter.com/)中的步骤设置应用程序并创建访问令牌。

具体来说，我们需要：

1.  Consumer Key
2.  Consumer Key Secret
3.  Access Token Secret
4.  Access Token

确保保持该窗口打开或记下它们，因为我们将在下面使用它们！

## 4. 安装Hadoop

接下来，让我们安装Hadoop！我们可以按照[官方文档](https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/SingleCluster.html#Installing_Software)或简单地利用Docker：

```shell
$ sudo docker run -p 50070:50070 sequenceiq/hadoop-docker:2.4.1
```

## 5. 编译我们的App Starters

要使用独立的、完全独立的组件，我们可以从他们的GitHub仓库中单独下载和编译所需的Spring Cloud Stream App Starters。

### 5.1 Twitter Spring Cloud Stream App Starter

让我们将Twitter Spring Cloud Stream App Starter(org.springframework.cloud.stream.app.twitterstream.source)添加到我们的项目中：

```shell
git clone https://github.com/spring-cloud-stream-app-starters/twitter.git
```

然后，我们运行Maven：

```shell
./mvnw clean install -PgenerateApps
```

生成的已编译Starter App将在本地项目根目录的“/target”中可用。

然后我们可以运行编译后的.jar并传入相关的应用程序属性，如下所示：

```shell
java -jar twitter_stream_source.jar --consumerKey=<CONSUMER_KEY> --consumerSecret=<CONSUMER_SECRET> \
    --accessToken=<ACCESS_TOKEN> --accessTokenSecret=<ACCESS_TOKEN_SECRET>
```

我们还可以使用熟悉的Spring application.properties传递我们的凭据：

```properties
twitter.credentials.access-token=...
twitter.credentials.access-token-secret=...
twitter.credentials.consumer-key=...
twitter.credentials.consumer-secret=...
```

### 5.2 HDFS Spring Cloud Stream App Starter

现在(已经设置好Hadoop)，让我们将HDFS Spring Cloud Stream App Starter(org.springframework.cloud.stream.app.hdfs.sink)依赖项添加到我们的项目中。

首先，克隆相关的仓库：

```shell
git clone https://github.com/spring-cloud-stream-app-starters/hdfs.git
```

然后，运行Maven作业：

```shell
./mvnw clean install -PgenerateApps
```

生成的已编译Starter App将在本地项目根目录的“/target”中可用。然后我们可以运行编译后的.jar并传入相关的应用程序属性：

```shell
java -jar hdfs-sink.jar --fsUri=hdfs://127.0.0.1:50010/
```

'hdfs://127.0.0.1:50010/'是Hadoop的默认端口，但你的默认HDFS端口可能会有所不同，具体取决于你配置实例的方式。

根据我们之前传入的配置，我们可以在“http://0.0.0.0:50070”看到数据节点列表(及其当前端口) 。

我们还可以在编译之前使用熟悉的Spring application.properties传递我们的凭据-因此我们不必总是通过CLI传递这些凭据。

让我们将application.properties配置为使用默认的Hadoop端口：

```properties
hdfs.fs-uri=hdfs://127.0.0.1:50010/
```

## 6. 使用AggregateApplicationBuilder 

或者，我们可以通过org.springframework.cloud.stream.aggregate.AggregateApplicationBuilder将我们的Spring Stream Source和Sink组合成一个简单的Spring Boot应用程序！

首先，我们将两个Stream App Starter添加到我们的pom.xml中：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud.stream.app</groupId>
        <artifactId>spring-cloud-starter-stream-source-twitterstream</artifactId>
        <version>2.1.2.RELEASE</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud.stream.app</groupId>
        <artifactId>spring-cloud-starter-stream-sink-hdfs</artifactId>
        <version>2.1.2.RELEASE</version>
    </dependency>
</dependencies>
```

然后，我们将开始组合我们的两个Stream App Starter依赖项，方法是将它们包装到各自的子应用程序中。

### 6.1 构建我们的应用程序组件

我们的SourceApp指定要转换或消费的Source：

```java
@SpringBootApplication
@EnableBinding(Source.class)
@Import(TwitterstreamSourceConfiguration.class)
public class SourceApp {
	@InboundChannelAdapter(Source.OUTPUT)
	public String timerMessageSource() {
		return new SimpleDateFormat().format(new Date());
	}
}
```

请注意，我们将SourceApp绑定到org.springframework.cloud.stream.messaging.Source并注入适当的配置类以从我们的环境属性中获取所需的设置。

接下来，我们设置一个简单的org.springframework.cloud.stream.messaging.Processor绑定：

```java
@SpringBootApplication
@EnableBinding(Processor.class)
public class ProcessorApp {
	@Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
	public String processMessage(String payload) {
		log.info("Payload received!");
		return payload;
	}
}
```

然后，我们创建我们的消费者(Sink)：

```java
@SpringBootApplication
@EnableBinding(Sink.class)
@Import(HdfsSinkConfiguration.class)
public class SinkApp {
	@ServiceActivator(inputChannel= Sink.INPUT)
	public void loggerSink(Object payload) {
		log.info("Received: " + payload);
	}
}
```

在这里，我们将SinkApp绑定到org.springframework.cloud.stream.messaging.Sink并再次注入正确的配置类以使用我们指定的Hadoop设置。

最后，我们在AggregateApp main方法中使用AggregateApplicationBuilder组合我们的SourceApp、ProcessorApp和SinkApp：

```java
@SpringBootApplication
public class AggregateApp {
    public static void main(String[] args) {
        new AggregateApplicationBuilder()
          .from(SourceApp.class).args("--fixedDelay=5000")
          .via(ProcessorApp.class)
          .to(SinkApp.class).args("--debug=true")
          .run(args);
    }
}
```

与任何Spring Boot应用程序一样，我们可以通过application.properties或以编程方式将指定设置作为环境属性注入。

由于我们使用的是Spring Stream框架，因此我们还可以将参数传递给AggregateApplicationBuilder构造函数。

### 6.2 运行完成的应用程序

然后我们可以使用以下命令行指令编译并运行我们的应用程序：

```shell
$ mvn install
$ java -jar twitterhdfs.jar
```

请记住将每个@SpringBootApplication类保存在一个单独的包中(否则，将抛出几个不同的绑定异常)！有关如何使用AggregateApplicationBuilder的更多信息，请查看[官方文档](https://github.com/spring-cloud/spring-cloud-stream-samples/)。

在编译并运行我们的应用程序之后，我们应该在控制台中看到类似以下的内容(当然，内容会因推文而异)：

```shell
2018-01-15 04:38:32.255  INFO 28778 --- [itterSource-1-1] 
c.t.t.twitterhdfs.processor.ProcessorApp   : Payload received!
2018-01-15 04:38:32.255  INFO 28778 --- [itterSource-1-1] 
c.t.t.twitterhdfs.sink.SinkApp    : Received: {"created_at":
"Mon Jan 15 04:38:32 +0000 2018","id":952761898239385601,"id_str":
"952761898239385601","text":"RT @mighty_jimin: 180114 ...
```

这些演示了我们的Processor和Sink在从Source接收数据时的正确操作！在这个例子中，我们没有配置我们的HDFS Sink来做很多事情-它只会打印消息“Payload received!”

## 7. 总结

在本教程中，我们学习了如何将两个很棒的Spring Stream App Starters组合成一个漂亮的Spring Boot示例！

这里有一些关于[Spring Boot Starters](https://www.baeldung.com/spring-boot-starters)以及如何创建[自定义Starter](https://www.baeldung.com/spring-boot-custom-starter)的其他很棒的官方文章！