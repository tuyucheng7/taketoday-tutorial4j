## 1. 概述

在本系列的[第一篇](https://www.baeldung.com/spring-cloud-data-flow-stream-processing)文章中，我们介绍了Spring Cloud Data Flow的架构组件以及如何使用它来创建流式数据管道。

与处理无限量数据的流管道相反，**批处理可以轻松创建按需执行任务的短期服务**。

## 2. 本地数据流服务器和Shell

Local Data Flow Server是负责部署应用程序的组件，而Data Flow Shell允许我们执行与服务器交互所需的DSL命令。

在[上一篇文章](https://www.baeldung.com/spring-cloud-data-flow-stream-processing)中，我们使用[Spring Initilizr](https://start.spring.io/)将它们设置为Spring Boot应用程序。

分别在服务端主类添加@EnableDataFlowServer注解，在shell主类添加@EnableDataFlowShell注解后，即可启动：

```shell
mvn spring-boot:run
```

服务器将在端口9393上启动，并且shell将准备好与提示符进行交互。

本地数据流服务器及其shell客户端的获取和使用方法可以参考上一篇文章。

## 3. 批处理应用程序

与服务器和shell一样，我们可以使用[Spring Initilizr](https://start.spring.io/)来设置根Spring Boot批处理应用程序。

到达网站后，只需选择一个Group、一个Artifact名称并从依赖项搜索框中选择Cloud Task。

完成此操作后，单击“Generate Project”按钮开始下载Maven工件。

该项目已预先配置并带有基本代码。让我们看看如何编辑它以构建我们的批处理应用程序。

### 3.1 Maven依赖项

首先，让我们添加几个Maven依赖项。由于这是一个批处理应用程序，我们需要从Spring Batch项目导入库：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-batch</artifactId>
</dependency>
```

此外，由于Spring Cloud Task使用关系型数据库来存储执行任务的结果，我们需要添加对RDBMS驱动程序的依赖：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
```

我们选择使用Spring提供的H2内存数据库。这为我们提供了一种引导开发的简单方法。但是，在生产环境中，你需要配置自己的DataSource。

请记住，工件的版本将从Spring Boot的父pom.xml文件继承。

### 3.2 主类

启用所需功能的关键点是将@EnableTask和@EnableBatchProcessing注解添加到Spring Boot的主类。这个类级别的注解告诉Spring Cloud Task引导一切：

```java
@EnableTask
@EnableBatchProcessing
@SpringBootApplication
public class BatchJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchJobApplication.class, args);
    }
}
```

### 3.3 作业配置

最后，让我们配置一个作业-在本例中是一个简单的字符串打印到日志文件：

```java
@Configuration
public class JobConfiguration {

    private static Log logger = LogFactory.getLog(JobConfiguration.class);

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job() {
        return jobBuilderFactory.get("job")
              .start(stepBuilderFactory.get("jobStep1")
                    .tasklet(new Tasklet() {

                        @Override
                        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                            logger.info("Job was run");
                            return RepeatStatus.FINISHED;
                        }
                    }).build()).build();
    }
}
```

有关如何配置和定义作业的详细信息超出了本文的范围。有关更多信息，你可以查看我们的[Spring Batch简介](https://www.baeldung.com/introduction-to-spring-batch)文章。

最后，我们的应用程序准备就绪。让我们将它安装在我们的本地Maven仓库中。为此cd进入项目的根目录并执行命令：

```shell
mvn clean install
```

现在是时候将应用程序放入数据流服务器中了。

## 4. 注册应用程序

要在App Registry中注册应用程序，我们需要提供一个唯一的名称、一个应用程序类型和一个可以解析为应用程序工件的URI。

转到Spring Cloud Data Flow Shell并从提示符发出命令：

```shell
app register --name batch-job --type task 
  --uri maven://cn.tuyucheng.taketoday.spring.cloud:batch-job:jar:1.0.0
```

## 5. 创建任务

可以使用以下命令创建任务定义：

```shell
task create myjob --definition batch-job
```

这将创建一个名为myjob的新任务，该任务指向先前注册的批处理作业应用程序。

可以使用以下命令获取当前任务定义的列表：

```shell
task list
```

## 6. 启动任务

要启动任务，我们可以使用以下命令：

```shell
task launch myjob
```

启动任务后，任务的状态将存储在关系型数据库中。我们可以使用以下命令检查任务执行的状态：

```shell
task execution list
```

## 7. 查看结果

在此示例中，作业只是在日志文件中打印一个字符串。日志文件位于数据流服务器日志输出中显示的目录中。

要查看结果，我们可以跟踪日志：

```shell
tail -f PATH_TO_LOG\spring-cloud-dataflow-2385233467298102321\myjob-1472827120414\myjob
[...] --- [main] o.s.batch.core.job.SimpleStepHandler: Executing step: [jobStep1]
[...] --- [main] o.t.t.spring.cloud.JobConfiguration: Job was run
[...] --- [main] o.s.b.c.l.support.SimpleJobLauncher:
  Job: [SimpleJob: [name=job]] completed with the following parameters: 
    [{}] and the following status: [COMPLETED]
```

## 8. 总结

在本文中，我们展示了如何通过使用Spring Cloud Data Flow处理批处理。