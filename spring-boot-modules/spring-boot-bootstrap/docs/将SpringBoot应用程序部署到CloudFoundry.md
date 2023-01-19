## 一、概述

将 Spring Boot 应用程序部署到 Cloud Foundry 是一项简单的练习。在本教程中，我们将向你展示如何操作。

## 2. Spring Cloud 依赖

由于此项目将需要 Spring Cloud 项目的新依赖项，因此我们将添加 Spring Cloud 依赖项 BOM：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>Greenwhich.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

我们可以在[Maven Central上找到最新版本的](https://search.maven.org/search?q=g:org.springframework.cloud AND a:spring-cloud-dependencies&core=gav)spring-cloud-dependencies库。

现在，我们想要为 Cloud Foundry 维护一个单独的构建，因此我们将在 Maven pom.xml中创建一个名为cloudfoundry的配置文件。

我们还将添加编译器排除和 Spring Boot 插件来配置包的名称：

```xml
<build>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <excludes>
                <exclude>/logback.xml</exclude>
            </excludes>
        </resource>
    </resources>
    <plugins>
        <plugin>                        
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <finalName>${project.name}-cf</finalName>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <excludes>
                    <exclude>/cloud/config/.java</exclude>
                </excludes>
            </configuration>
        </plugin>
    </plugins>
</build>
```

我们还想从正常构建中排除特定于云的文件，因此我们向 Maven 编译器插件添加全局配置文件排除：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <excludes>
                    <exclude>/cloud/.java</exclude>
                </excludes>
            </configuration>
        </plugin>
    </plugins>
</build>
```

然后，我们需要添加 Spring Cloud Starter 和 Spring Cloud Connectors 库，它们为 Cloud Foundry 提供支持：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cloud-connectors</artifactId>
</dependency>
```

## 3. Cloud Foundry配置

要完成本教程，我们需要在[此处注册试用或下载](https://run.pivotal.io/)[Native Linux](https://github.com/cloudfoundry-incubator/cfdev)或[Virtual Box](https://docs.pivotal.io/pcf-dev/)的预配置开发环境。

此外，还需要安装 Cloud Foundry CLI。说明在[这里](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html)。

在向 Cloud Foundry 提供商注册后，API URL 将可用(你可以通过左侧的“工具”选项返回)。

应用程序容器允许我们将服务绑定到应用程序。接下来我们登录Cloud Foundry环境：

```bash
cf login -a <url>
```

Cloud Foundry Marketplace 是一个服务目录，例如数据库、消息传递、电子邮件、监控、日志记录等等。大多数服务提供免费或试用计划。

让我们在 Marketplace 中搜索“MySQL”并为我们的应用程序创建一个服务：

```bash
cf marketplace | grep MySQL
>
cleardb     spark, boost, amp, shock         Highly available MySQL for your Apps.

```

输出列出了描述中带有“MySQL”的服务。在 PCF 上，MySQL 服务名为cleardb，非免费计划用星号标记。

接下来，我们使用以下方式列出服务的详细信息：

```bash
cf marketplace -s cleardb
>
service plan description                                                                 free or paid
spark        Great for getting started and developing your apps                             free
boost        Best for light production or staging your applications                         paid
amp          For apps with moderate data requirements                                       paid
shock        Designed for apps where you need real MySQL reliability, power and throughput  paid
```

现在我们创建一个名为 spring-bootstrap-db的免费 MySQL 服务实例：

```bash
cf create-service cleardb spark spring-bootstrap-db
```

## 4. 应用配置

接下来，我们添加一个扩展AbstractCloudConfig的@Configuration注解类， 以在名为 org.baeldung.cloud.config的包中创建一个数据源 ：

```java
@Configuration
@Profile("cloud")
public class CloudDataSourceConfig extends AbstractCloudConfig {
 
    @Bean
    public DataSource dataSource() {
        return connectionFactory().dataSource();
    }
}
```

添加 @Profile(“cloud”) 可确保在我们进行本地测试时云连接器处于非活动状态。我们还将@ActiveProfiles(profiles = {“local”})添加到集成测试中。

然后构建应用程序：

```bash
mvn clean install spring-boot:repackage -P cloudfoundry
```

此外，我们需要提供一个manifest.yml文件，以将服务绑定到应用程序。

我们通常将manifest.yml文件放在项目文件夹中，但在本例中，我们将创建一个cloudfoundry文件夹，因为我们将演示部署到多个云原生提供商：

```python
---
applications:
- name: spring-boot-bootstrap
  memory: 768M
  random-route: true
  path: ../target/spring-boot-bootstrap-cf.jar
  env:
    SPRING_PROFILES_ACTIVE: cloud,mysql
  services:
  - spring-bootstrap-db
```

## 5.部署

部署应用程序现在非常简单：

```bash
cd cloudfoundry
cf push
```

Cloud Foundry 将使用 Java buildpack 部署应用程序并创建到应用程序的随机路由。

我们可以使用以下命令查看日志文件中的最后几个条目：

```bash
cf logs spring-boot-bootstrap --recent
```

或者我们可以跟踪日志文件：

```bash
cf logs spring-boot-bootstrap
```

最后，我们需要路由名称来测试应用程序：

```bash
cf app spring-boot-bootstrap
>
name:              spring-boot-bootstrap
requested state:   started
routes:            spring-boot-bootstrap-delightful-chimpanzee.cfapps.io
last uploaded:     Thu 23 Aug 08:57:20 SAST 2018
stack:             cflinuxfs2
buildpacks:        java-buildpack=v4.15-offline-...

type:           web
instances:      1/1
memory usage:   768M
     state     since                  cpu    memory           disk
#0   running   2018-08-23T06:57:57Z   0.5%   290.9M of 768M   164.7M of 1G

```

执行以下命令将添加一本新书：

```bash
curl -i --request POST 
    --header "Content-Type: application/json" 
    --data '{"title": "The Player of Games", "author": "Iain M. Banks"}' 
    https://<app-route>/api/books
#OR
http POST https://<app-route>/api/books title="The Player of Games" author="Iain M. Banks"

```

这个命令将列出所有书籍：

```bash
curl -i https://<app-route>/api/books 
#OR 
http https://<app-route>/api/books
>
HTTP/1.1 200 OK

[
    {
        "author": "Iain M. Banks",
        "id": 1,
        "title": "Player of Games"
    },
    {
        "author": "J.R.R. Tolkien",
        "id": 2,
        "title": "The Hobbit"
    }
]

```

## 6. 扩展应用程序

最后，在 Cloud Foundry 上扩展应用程序就像使用scale命令一样简单：

```bash
cf scale spring-cloud-bootstrap-cloudfoundry <options>
Options:
-i <instances>
-m <memory-allocated> # Like 512M or 1G
-k <disk-space-allocated> # Like 1G or 2G
-f # Force restart without prompt
```

当我们不再需要它时，请记住删除该应用程序：

```bash
cf delete spring-cloud-bootstrap-cloudfoundry
```

## 七、总结

在本文中，我们介绍了使用 Spring Boot 简化云原生应用程序开发的 Spring Cloud 库。使用 Cloud Foundry CLI 进行部署在[此处](https://docs.cloudfoundry.org/cf-cli/cf-help.html)有详细记录。

[插件存储库](https://plugins.cloudfoundry.org/)中提供了 CLI 的额外插件。