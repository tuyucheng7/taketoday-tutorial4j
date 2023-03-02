## 1. 简介

**Spring Cloud Data Flow是一个用于构建数据集成和实时数据处理管道的工具包**。 

在这种情况下，管道是使用[Spring Cloud Stream](https://cloud.spring.io/spring-cloud-stream/)或[Spring Cloud Task](https://spring.io/projects/spring-cloud-task)框架构建的Spring Boot应用程序。

在本教程中，我们将展示如何将Spring Cloud Data Flow与[Apache Spark](https://www.baeldung.com/apache-spark)结合使用。

## 2. 数据流本地服务器

首先，我们需要运行[数据流服务器](https://www.baeldung.com/spring-cloud-data-flow-stream-processing)才能部署我们的作业。

要在本地运行数据流服务器，我们需要创建一个具有[spring-cloud-starter-dataflow-server-local](https://search.maven.org/search?q=spring-cloud-starter-dataflow-server-local)依赖项的新项目：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-dataflow-server-local</artifactId>
    <version>1.7.4.RELEASE</version>
</dependency>
```

之后，我们需要在服务端的主类上添加注解@EnableDataFlowServer：

```java
@EnableDataFlowServer
@SpringBootApplication
public class SpringDataFlowServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(
              SpringDataFlowServerApplication.class, args);
    }
}
```

一旦我们运行这个应用程序，我们将在端口9393上有一个本地数据流服务器。

## 3. 创建项目

我们将[创建一个Spark作业](https://www.baeldung.com/apache-spark)作为独立的本地应用程序，这样我们就不需要任何集群来运行它。

### 3.1 依赖关系

首先，我们将添加[Spark](https://search.maven.org/artifact/org.apache.spark/spark-core_2.10/2.2.3/jar)依赖项：

```xml
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-core_2.10</artifactId>
    <version>2.4.0</version>
</dependency>
```

### 3.2 创建作业

对于我们的作业，让我们近似pi：

```java
public class PiApproximation {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("BaeldungPIApproximation");
        JavaSparkContext context = new JavaSparkContext(conf);
        int slices = args.length >= 1 ? Integer.valueOf(args[0]) : 2;
        int n = (100000L * slices) > Integer.MAX_VALUE ? Integer.MAX_VALUE : 100000 * slices;

        List<Integer> xs = IntStream.rangeClosed(0, n)
              .mapToObj(element -> Integer.valueOf(element))
              .collect(Collectors.toList());

        JavaRDD<Integer> dataSet = context.parallelize(xs, slices);

        JavaRDD<Integer> pointsInsideTheCircle = dataSet.map(integer -> {
            double x = Math.random() * 2 - 1;
            double y = Math.random() * 2 - 1;
            return (x * x + y * y ) < 1 ? 1: 0;
        });

        int count = pointsInsideTheCircle.reduce((integer, integer2) -> integer + integer2);

        System.out.println("The pi was estimated as:" + count / n);

        context.stop();
    }
}
```

## 4. Data Flow Shell

Data Flow Shell是一个使我们**能够与服务器交互的应用程序**。Shell使用DSL命令来描述数据流。

要使用[Data Flow Shell](https://www.baeldung.com/spring-cloud-data-flow-stream-processing)，我们需要创建一个允许我们运行它的项目。首先，我们需要[spring-cloud-dataflow-shell](https://search.maven.org/search?q=spring-cloud-dataflow-shell)依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-dataflow-shell</artifactId>
    <version>1.7.4.RELEASE</version>
</dependency>
```

添加依赖项后，我们可以创建将运行数据流shell的类：

```java
@EnableDataFlowShell
@SpringBootApplication
public class SpringDataFlowShellApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDataFlowShellApplication.class, args);
    }
}
```

## 5. 部署项目

为了部署我们的项目，我们将使用所谓的任务运行器，该运行器可用于Apache Spark的三个版本：cluster、yarn和client。我们将继续使用本地客户端版本。

**任务运行器运行我们的Spark作业**。

为此，我们首先需要**使用Data Flow Shell注册我们的任务**：

```shell
app register --type task --name spark-client --uri maven://org.springframework.cloud.task.app:spark-client-task:1.0.0.BUILD-SNAPSHOT
```

该任务允许我们指定多个不同的参数，其中一些是可选的，但某些参数是正确部署Spark作业所必需的：

-   spark.app-class，我们提交的作业的主类
-   spark.app-jar，包含我们作业的fat jar的路径
-   spark.app-name，将用于我们的作业的名称
-   spark.app-args，将传递给作业的参数

我们可以使用已注册的任务spark-client来提交我们的作业，记得提供所需的参数：

```shell
task create spark1 --definition "spark-client \
  --spark.app-name=my-test-pi --spark.app-class=cn.tuyucheng.taketoday.spring.cloud.PiApproximation \
  --spark.app-jar=/apache-spark-job-1.0.0.jar --spark.app-args=10"
```

请注意，spark.app-jar是我们作业中fat-jar的路径。

成功创建任务后，我们可以继续使用以下命令运行它：

```shell
task launch spark1
```

这将调用我们的任务的执行。

## 6. 总结

在本教程中，我们展示了如何使用Spring Cloud Data Flow框架通过Apache Spark处理数据。可以在[文档](https://cloud.spring.io/spring-cloud-dataflow/)中找到有关Spring Cloud Data Flow框架的更多信息。