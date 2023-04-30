## Lightrun示例应用程序-任务管理

此应用程序作为Lightrun系列文章的示例存在。

## 构建

此应用程序需要[Apache Maven](https://maven.apache.org/)和[Java 17+](https://www.oracle.com/java/technologies/downloads/)。

通过在模块目录执行以下命令构建代码：

```shell
$ mvn install
```

## 运行

该应用程序由三个服务组成：

* Tasks
* Users
* API

这些都是Spring Boot应用程序。

任务和用户服务作为微服务存在，用于管理数据的一个方面。每个都使用一个数据库，并利用JMS也在它们之间排队。为方便起见，此基础结构全部嵌入在应用程序中。

这确实意味着启动顺序很重要。JMS队列存在于任务服务中，并连接到从用户服务。因此，必须在其他服务之前启动任务服务。

每个服务都可以通过在适当的目录中执行`mvn spring-boot:run`来启动。或者，作为Spring Boot应用程序，构建将在`target`目录中生成一个可执行的JAR文件，可以运行例如以下命令执行：

```shell
$ java -jar ./target/spring-boot-lightrun-tasks-service-1.0.0.jar
```

## 相关文章

+ [使用Java的Lightrun简介](docs/使用Java的Lightrun简介.md)