## 1. 概述

[Spring Cloud Data Flow](https://cloud.spring.io/spring-cloud-dataflow/)是一个用于构建实时数据管道和批处理的云原生工具包。Spring Cloud Data Flow已准备好用于一系列数据处理用例，例如简单的导入/导出、ETL处理、事件流和预测分析。

在本教程中，我们将学习一个实时提取转换和加载(ETL)示例，该示例使用流管道从JDBC数据库中提取数据，将其转换为简单的POJO并将其加载到MongoDB中。

## 2. ETL和事件流处理

ETL(提取、转换和加载)通常被称为将数据从多个数据库和系统批量加载到一个公共数据仓库中的过程。在这个数据仓库中，可以在不影响系统整体性能的情况下进行繁重的数据分析处理。

然而，新的趋势正在改变这样做的方式。ETL在将数据传输到数据仓库和数据湖方面仍然发挥着作用。

如今，**这可以在Spring Cloud Data Flow的帮助下使用事件流架构中的流来完成**。

## 3. Spring Cloud Data Flow

借助Spring Cloud Data Flow(SCDF)，开发人员可以创建两种类型的数据管道：

-   使用Spring Cloud Stream的长寿命实时流应用程序
-   使用Spring Cloud Task的短期批处理任务应用程序

在本文中，我们将介绍第一个基于Spring Cloud Stream的长期流式应用程序。

### 3.1 Spring Cloud Stream应用程序

SCDF Stream管道由步骤组成，**其中每个步骤都是一个使用Spring Cloud Stream微框架以Spring Boot风格构建的应用程序**。这些应用程序由Apache Kafka或RabbitMQ等消息传递中间件集成。

这些应用程序分为源(sources)、处理器(processors)和接收器(sinks)。与ETL过程相比，我们可以说源是“提取”，处理器是“转换”，接收器是“加载”部分。

在某些情况下，我们可以在管道的一个或多个步骤中使用应用程序启动器。这意味着我们不需要为一个步骤实现一个新的应用程序，而是配置一个已经实现的现有应用程序启动器。

**可以在[此处](https://cloud.spring.io/spring-cloud-stream-app-starters/)找到应用程序启动器列表**。

### 3.2 Spring Cloud Data Flow Server

**该架构的最后一部分是Spring Cloud Data Flow Server**。SCDF服务器使用Spring Cloud Deployer规范部署应用程序和管道流。该规范通过部署到一系列现代运行时(例如Kubernetes、Apache Mesos、Yarn和Cloud Foundry)来支持SCDF云原生风格。

此外，我们可以将流作为本地部署运行。

可以在[此处](https://www.baeldung.com/spring-cloud-data-flow-stream-processing)找到有关SCDF架构的更多信息。

## 4. 环境搭建

在开始之前，我们需要**选择这个复杂部署的各个部分**。第一个要定义的部分是SCDF服务器。

为了测试，**我们将使用SCDF Server Local进行本地开发**。对于生产部署，我们稍后可以选择云原生运行时，如[SCDF Server Kubernetes](https://cloud.spring.io/spring-cloud-dataflow-server-kubernetes/)。我们可以在[此处](https://cloud.spring.io/spring-cloud-dataflow/)找到服务器运行时列表。

现在，让我们检查运行此服务器的系统要求。

### 4.1 系统要求

要运行SCDF服务器，我们必须定义和设置两个依赖项：

-   消息传递中间件，以及
-   关系型数据库管理系统

**对于消息传递中间件，我们将使用RabbitMQ，我们选择PostgreSQL作为RDBMS来存储我们的管道流定义**。

要运行RabbitMQ，请在[此处](https://www.rabbitmq.com/download.html)下载最新版本并使用默认配置启动RabbitMQ实例或运行以下Docker命令：

```shell
docker run --name dataflow-rabbit -p 15672:15672 -p 5672:5672 -d rabbitmq:3-management
```

作为最后一个设置步骤，在默认端口5432上安装并运行PostgreSQL RDBMS。之后，使用以下脚本创建一个SCDF可以在其中存储其流定义的数据库：

```sql
CREATE DATABASE dataflow;
```

### 4.2 Spring Cloud Data Flow Server Local

为了运行SCDF Server Local，我们可以选择使用[docker-compose](https://docs.spring.io/spring-cloud-dataflow/docs/current/reference/htmlsingle/#getting-started-deploying-spring-cloud-dataflow-docker)启动服务器，或者我们可以将其作为Java应用程序启动。

**在这里，我们将SCDF Server Local作为Java应用程序运行**。为了配置应用程序，我们必须将配置定义为Java应用程序参数。我们需要在系统路径中使用Java 8。

要托管jars和依赖项，我们需要为我们的SCDF服务器创建一个主文件夹，并将SCDF Server Local发行版下载到该文件夹中。你可以在[此处](https://cloud.spring.io/spring-cloud-dataflow/)下载SCDF Server Local的最新发行版。

此外，我们需要创建一个lib文件夹并将JDBC驱动程序放在那里。最新版本的PostgreSQL驱动程序可在[此处](https://jdbc.postgresql.org/download.html#current)获得。 

最后，让我们运行SCDF local server：

```shell
$java -Dloader.path=lib -jar spring-cloud-dataflow-server-local-1.6.3.RELEASE.jar \
    --spring.datasource.url=jdbc:postgresql://127.0.0.1:5432/dataflow \
    --spring.datasource.username=postgres_username \
    --spring.datasource.password=postgres_password \
    --spring.datasource.driver-class-name=org.postgresql.Driver \
    --spring.rabbitmq.host=127.0.0.1 \
    --spring.rabbitmq.port=5672 \
    --spring.rabbitmq.username=guest \
    --spring.rabbitmq.password=guest
```

我们可以通过查看此URL来检查它是否正在运行：

[http://localhost:9393/dashboard](http://localhost:9393/dashboard)

### 4.3 Spring Cloud Data Flow Shell

**SCDF Shell是一个命令行工具，可以轻松组合和部署我们的应用程序和管道**。这些Shell命令在Spring Cloud Data Flow Server [REST API](https://docs.spring.io/spring-cloud-dataflow/docs/current-SNAPSHOT/reference/htmlsingle/#api-guide-overview)上运行。

将最新版本的jar下载到你的SCDF主文件夹中，可在[此处](https://repo.spring.io/release/org/springframework/cloud/spring-cloud-dataflow-shell/)获取。完成后，运行以下命令(根据需要更新版本)：

```shell
$ java -jar spring-cloud-dataflow-shell-1.6.3.RELEASE.jar
  ____                              ____ _                __
 / ___| _ __  _ __(_)_ __   __ _   / ___| | ___  _   _  __| |
 \___ \| '_ \| '__| | '_ \ / _` | | |   | |/ _ \| | | |/ _` |
  ___) | |_) | |  | | | | | (_| | | |___| | (_) | |_| | (_| |
 |____/| .__/|_|  |_|_| |_|\__, |  \____|_|\___/ \__,_|\__,_|
  ____ |_|    _          __|___/                 __________
 |  _ \  __ _| |_ __ _  |  ___| | _____      __  \ \ \ \ \ \
 | | | |/ _` | __/ _` | | |_  | |/ _ \ \ /\ / /   \ \ \ \ \ \
 | |_| | (_| | || (_| | |  _| | | (_) \ V  V /    / / / / / /
 |____/ \__,_|\__\__,_| |_|   |_|\___/ \_/\_/    /_/_/_/_/_/


Welcome to the Spring Cloud Data Flow shell. For assistance hit TAB or type "help".
dataflow:>
```

如果在最后一行中得到的不是“dataflow:>”，而是“server-unknown:>”，则表明你没有在本地主机上运行SCDF服务器。在这种情况下，请运行以下命令连接到另一台主机：

```shell
server-unknown:>dataflow config server http://{host}
```

现在，Shell已连接到SCDF服务器，我们可以运行我们的命令。

我们需要在Shell中做的第一件事是导入应用程序启动器。在[此处](https://cloud.spring.io/spring-cloud-stream-app-starters/)找到Spring Boot 2.0.x中RabbitMQ + Maven的最新版本，并运行以下命令(同样，根据需要更新版本，此处为“Darwin-SR1”)：

```shell
$ dataflow:>app import --uri http://bit.ly/Darwin-SR1-stream-applications-rabbit-maven
```

要检查已安装的应用程序，请运行以下Shell命令：

```shell
$ dataflow:> app list
```

结果，我们应该看到一个包含所有已安装应用程序的表格。

此外，SCDF提供了一个名为Flo的图形界面，我们可以通过以下地址访问它：[http://localhost:9393/dashboard](http://localhost:9393/dashboard)。但是，它的使用不在本文的讨论范围内。

## 5. 组成ETL管道

现在让我们创建我们的流管道。为此，我们将使用JDBC Source应用程序启动器从我们的关系型数据库中提取信息。

此外，我们将创建一个用于转换信息结构的自定义处理器和一个用于将我们的数据加载到MongoDB中的自定义接收器。

### 5.1 提取-准备关系型数据库以进行提取

让我们创建一个名为crm的数据库和一个名为customer的表：

```sql
CREATE DATABASE crm;
```

```sql
CREATE TABLE customer
(
    id            bigint NOT NULL,
    imported      boolean DEFAULT false,
    customer_name character varying(50),
    PRIMARY KEY (id)
)
```

请注意，我们正在使用标志imported，它将存储已经导入的记录。如有必要，我们还可以将此信息存储在另一个表中。

现在，让我们插入一些数据：

```sql
INSERT INTO customer(id, customer_name, imported) VALUES (1, 'John Doe', false);
```

### 5.2 转换—-将JDBC字段映射到MongoDB字段结构

对于转换步骤，我们会将源表中的字段customer_name简单转换为新字段name。其他转换可以在这里完成，但让我们保持示例简短。

**为此，我们将创建一个名为customer-transform的新项目**。最简单的方法是使用[Spring Initializr](https://start.spring.io/)站点来创建项目。到达网站后，选择一个Group和一个Artifact名称。我们将分别使用com.customer和customer-transform。

完成此操作后，单击“Generate Project”按钮下载项目。然后，解压缩项目并将其导入到你喜欢的IDE中，并将以下依赖项添加到pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-stream-binder-rabbit</artifactId>
</dependency>
```

现在我们准备开始编码字段名称转换。为此，我们将创建Customer类作为适配器。此类将通过setName()方法接收customer_name，并通过getName方法输出其值。

@JsonProperty注解将在从JSON反序列化为Java时进行转换： 

```java
public class Customer {

    private Long id;

    private String name;

    @JsonProperty("customer_name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    // Getters and Setters
}
```

处理器需要从输入接收数据，执行转换并将结果绑定到输出通道。让我们创建一个类来执行此操作：

```java
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.Transformer;

@EnableBinding(Processor.class)
public class CustomerProcessorConfiguration {

    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    public Customer convertToPojo(Customer payload) {

        return payload;
    }
}
```

在上面的代码中，我们可以观察到转换是自动发生的。输入以JSON形式接收数据，Jackson使用set方法将其反序列化为Customer对象。

相反，对于输出，数据使用get方法序列化为JSON。

### 5.3 加载-MongoDB中的接收器

与转换步骤类似，**我们将创建另一个Maven项目，现在名称为customer-mongodb-sink**。再次访问[Spring Initializr](https://start.spring.io/)，为Group选择com.customer，为Artifact选择customer-mongodb-sink。然后，在依赖项搜索框中输入“MongoDB”并下载项目。

接下来，解压缩并将其导入到你喜欢的IDE。

然后，添加与customer-transform项目中相同的额外依赖项。

现在我们将创建另一个Customer类，用于接收此步骤中的输入：

```java
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="customer")
public class Customer {

    private Long id;
    private String name;

    // Getters and Setters
}
```

为了接收Customer，我们将创建一个Listener类，该类将使用CustomerRepository保存客户实体：

```java
@EnableBinding(Sink.class)
public class CustomerListener {

    @Autowired
    private CustomerRepository repository;

    @StreamListener(Sink.INPUT)
    public void save(Customer customer) {
        repository.save(customer);
    }
}
```

在这种情况下，CustomerRepository是来自Spring Data的MongoRepository：

```java
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, Long> {
}
```

### 5.4 流定义

现在，**这两个自定义应用程序都已准备好在SCDF服务器上注册**。为此，使用Maven命令mvn install编译这两个项目。

然后我们使用Spring Cloud Data Flow Shell注册它们：

```shell
app register --name customer-transform --type processor --uri maven://com.customer:customer-transform:1.0.0
```

```shell
app register --name customer-mongodb-sink --type sink --uri maven://com.customer:customer-mongodb-sink:jar:1.0.0
```

最后，让我们检查应用程序是否存储在SCDF中，在shell中运行应用程序列表命令：

```shell
app list
```

因此，我们应该在结果表中看到这两个应用程序。

#### 5.4.1 流管道领域特定语言-DSL

DSL定义了应用程序之间的配置和数据流。SCDF DSL很简单。在第一个词中，我们定义了应用程序的名称，然后是配置。

此外，该语法是一种受Unix启发的[管道语法](https://en.wikipedia.org/wiki/Pipeline_(Unix))，它使用竖线(也称为“管道”)来连接多个应用程序：

```shell
http --port=8181 | log
```

这将创建一个在端口8181中提供服务的HTTP应用程序，该应用程序将任何接收到的正文有效负载发送到日志。

现在，让我们看看如何创建JDBC源的DSL流定义。

#### 5.4.2 JDBC源流定义

JDBC Source的关键配置是query和update。**query将选择未读记录，而update将更改标志以防止重新读取当前记录**。

此外，我们将定义JDBC源以在30秒的固定延迟内轮询并轮询最多1000行。最后，我们将定义连接配置，如驱动程序、用户名、密码和连接URL：

```shell
jdbc
    --query='SELECT id, customer_name FROM public.customer WHERE imported = false'
    --update='UPDATE public.customer SET imported = true WHERE id in (:id)'
    --max-rows-per-poll=1000
    --fixed-delay=30 --time-unit=SECONDS
    --driver-class-name=org.postgresql.Driver
    --url=jdbc:postgresql://localhost:5432/crm
    --username=postgres
    --password=postgres
```

可以在[此处](https://docs.spring.io/spring-cloud-stream-app-starters/docs/Celsius.SR2/reference/htmlsingle/#spring-cloud-stream-modules-jdbc-source)找到更多JDBC源配置属性。

#### 5.4.3 Customer MongoDB接收器流定义

由于我们没有在customer-mongodb-sink的application.properties中定义连接配置，我们将通过DSL参数进行配置。

我们的应用程序完全基于MongoDataAutoConfiguration。你可以在[此处](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-nosql.html#boot-features-mongodb)查看其他可能的配置。基本上，我们将定义spring.data.mongodb.uri：

```shell
customer-mongodb-sink --spring.data.mongodb.uri=mongodb://localhost/main
```

#### 5.4.4 创建和部署流

首先，要创建最终的流定义，请返回到Shell并执行以下命令(不带换行符，它们只是为了便于阅读而插入)：

```shell
stream create --name jdbc-to-mongodb 
  --definition "jdbc 
  --query='SELECT id, customer_name FROM public.customer WHERE imported=false' 
  --fixed-delay=30 
  --max-rows-per-poll=1000 
  --update='UPDATE customer SET imported=true WHERE id in (:id)' 
  --time-unit=SECONDS 
  --password=postgres 
  --driver-class-name=org.postgresql.Driver 
  --username=postgres 
  --url=jdbc:postgresql://localhost:5432/crm | customer-transform | customer-mongodb-sink 
  --spring.data.mongodb.uri=mongodb://localhost/main"
```

这个流DSL定义了一个名为jdbc-to-mongodb的流。接下来，**我们将按名称部署流**：

```shell
stream deploy --name jdbc-to-mongodb
```

最后，我们应该在日志输出中看到所有可用日志的位置：

```shell
Logs will be in {PATH_TO_LOG}/spring-cloud-deployer/jdbc-to-mongodb/jdbc-to-mongodb.customer-mongodb-sink

Logs will be in {PATH_TO_LOG}/spring-cloud-deployer/jdbc-to-mongodb/jdbc-to-mongodb.customer-transform

Logs will be in {PATH_TO_LOG}/spring-cloud-deployer/jdbc-to-mongodb/jdbc-to-mongodb.jdbc
```

## 6. 总结

在本文中，我们看到了使用Spring Cloud Data Flow的ETL数据管道的完整示例。

最值得注意的是，我们看到了应用程序启动器的配置，使用Spring Cloud Data Flow Shell创建了一个ETL流管道，并为我们的读取、转换和写入数据实现了自定义应用程序。