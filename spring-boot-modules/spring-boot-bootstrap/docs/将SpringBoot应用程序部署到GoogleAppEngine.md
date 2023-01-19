## 一、概述

在本教程中，我们将展示如何 使用 Spring Boot 教程将应用程序从[Bootstrap a Simple Application](https://www.baeldung.com/spring-boot-start)部署到Google Cloud Platform 上的[App Engine 。](https://cloud.google.com/appengine/)

作为其中的一部分，我们将：

-   配置谷歌云平台控制台和 SDK
-   使用 Cloud SQL 创建 MySQL 实例
-   为 Spring Cloud GCP 配置应用程序
-   将应用程序部署到 App Engine 并进行测试

## 2.谷歌云平台配置

我们可以使用 GCP 控制台让我们的本地环境为 GCP 做好准备。我们可以在官网找到[安装过程](https://cloud.google.com/sdk/)。

[让我们使用GCP 控制台](https://console.cloud.google.com/)在 GCP 上创建一个项目：

```bash
gcloud init
```

接下来，让我们配置项目名称：

```bash
gcloud config set project baeldung-spring-boot-bootstrap
```

然后我们将安装 App Engine 支持并创建一个 App Engine 实例：

```bash
gcloud components install app-engine-java
gcloud app create
```

我们的应用程序需要连接到 Cloud SQL 环境中的 MySQL 数据库。由于 Cloud SQL 不提供免费套餐，我们必须 在 GCP 帐户上启用[计费。](https://cloud.google.com/billing/docs/how-to/modify-project)

我们可以轻松检查可用层：

```bash
gcloud sql tiers list

```

在继续之前，我们应该使用 GCP 网站启用[Cloud SQL Admin API](https://console.cloud.google.com/flows/enableapi?apiid=sqladmin)。

 现在我们可以使用 Cloud Console 或 SDK CLI在[Cloud SQL](https://console.cloud.google.com/sql/instances)中创建 MySQL 实例和数据库。在此过程中，我们将选择区域并提供实例名称和数据库名称。应用程序和数据库实例位于同一区域很重要。

由于我们要将应用程序部署到 europe-west2，因此让我们对实例执行相同的操作：

```bash
# create instance
gcloud sql instances create 
  baeldung-spring-boot-bootstrap-db 
    --tier=db-f1-micro 
    --region=europe-west2
# create database
gcloud sql databases create 
  baeldung_bootstrap_db 
    --instance=baeldung-spring-boot-bootstrap-db
```

## 3. Spring Cloud GCP 依赖

我们的应用程序需要来自[Spring Cloud GCP](https://spring.io/projects/spring-cloud-gcp) 项目的依赖项来获取云原生 API。为此，让我们使用一个名为cloud-gcp的 Maven 配置文件：

```xml
<profile>
  <id>cloud-gcp</id>
  <dependencies>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-gcp-starter</artifactId>
      <version>1.0.0.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-gcp-starter-sql-mysql</artifactId>
      <version>1.0.0.RELEASE</version>
    </dependency>
  </dependencies>
```

然后我们添加 App Engine Maven 插件：

```xml
    <build>
      <plugins>
        <plugin>
          <groupId>com.google.cloud.tools</groupId>
          <artifactId>appengine-maven-plugin</artifactId>
          <version>1.3.2</version>
        </plugin>
      </plugins>
    </build>
</profile>
```

## 4. 应用配置

现在，让我们定义允许应用程序使用数据库等云原生资源的配置。

Spring Cloud GCP 使用spring-cloud-bootstrap.properties来确定应用名称：

```plaintext
spring.cloud.appId=baeldung-spring-boot-bootstrap
```

我们将为此部署使用名为gcp的 Spring 配置文件 ，我们需要配置数据库连接。因此我们创建src/main/resources/application-gcp.properties：

```plaintext
spring.cloud.gcp.sql.instance-connection-name=
    baeldung-spring-boot-bootstrap:europe-west2:baeldung-spring-boot-bootstrap-db
spring.cloud.gcp.sql.database-name=baeldung_bootstrap_db
```

## 5.部署

Google App Engine 提供了两种 Java 环境：

-   Standard环境提供 Jetty 和 JDK8，Flexible环境只提供 JDK8 和
-   Flexible 环境是 Spring Boot 应用程序的最佳选择。

我们要求gcp和mysql Spring 配置文件处于活动状态，因此我们通过将 SPRING_PROFILES_ACTIVE环境变量添加到 src/main/appengine/app.yaml中的部署配置来为应用程序提供环境变量：

```plaintext
runtime: java
env: flex
runtime_config:
  jdk: openjdk8
env_variables:
  SPRING_PROFILES_ACTIVE: "gcp,mysql"
handlers:
- url: /.
  script: this field is required, but ignored
manual_scaling: 
  instances: 1
```

现在，让我们使用appengine maven 插件构建和部署应用程序：

```bash
mvn clean package appengine:deploy -P cloud-gcp
```

部署后我们可以查看或跟踪日志文件：

```bash
# view
gcloud app logs read

# tail
gcloud app logs tail
```

现在， 让我们通过添加一本书来验证我们的应用程序是否正常工作：

```bash
http POST https://baeldung-spring-boot-bootstrap.appspot.com/api/books 
        title="The Player of Games" author="Iain M. Banks"

```

期待以下输出：

```bash
HTTP/1.1 201 
{
    "author": "Iain M. Banks",
    "id": 1,
    "title": "The Player of Games"
}
```

## 6. 扩展应用程序

App Engine 中的默认缩放是自动的。

在我们了解运行时行为以及涉及的相关预算和成本之前，最好从手动缩放开始。我们可以为应用程序分配资源并在app.yaml中配置自动缩放：

```plaintext
# Application Resources
resources:
  cpu: 2
  memory_gb: 2
  disk_size_gb: 10
  volumes:
  - name: ramdisk1
    volume_type: tmpfs
    size_gb: 0.5
# Automatic Scaling
automatic_scaling: 
  min_num_instances: 1 
  max_num_instances: 4 
  cool_down_period_sec: 180 
  cpu_utilization: 
    target_utilization: 0.6
```

## 七、总结

在本教程中，我们：

-   配置了 Google Cloud Platform 和 App Engine
-   使用 Cloud SQL 创建了一个 MySQL 实例
-   配置 Spring Cloud GCP 以使用 MySQL
-   部署我们配置的 Spring Boot 应用程序，以及
-   测试并扩展了应用程序

我们始终可以参考 Google 的大量[App Engine 文档](https://cloud.google.com/appengine/docs/flexible/java/) 以获取更多详细信息。