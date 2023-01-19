## 一、概述

在本教程中，我们将展示如何将应用程序从我们的 [Bootstrap a Simple Application using Spring Boot](https://www.baeldung.com/spring-boot-start) 教程部署到[AWS Elastic Beanstalk](https://docs.aws.amazon.com/elastic-beanstalk/index.html#lang/en_us)。

作为其中的一部分，我们将：

-   安装和配置 AWS CLI 工具
-   创建 Beanstalk 项目和 MySQL 部署
-   在 AWS RDS 中为 MySQL 配置应用程序
-   部署、测试和扩展应用程序

## 2. AWS Elastic Beanstalk 配置

作为先决条件，我们应该在 AWS 上注册自己并[在 Elastic Beanstalk 上创建 Java 8 环境](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/using-features.environments.html)。我们还需要[安装 AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html) ，这将使我们能够连接到我们的环境。

因此，鉴于此，我们需要登录并初始化我们的应用程序：

```bash
cd .../spring-boot-bootstrap
eb init

>
Select a default region
1) us-east-1 : US East (N. Virginia)
2) us-west-1 : US West (N. California)
3) us-west-2 : US West (Oregon)
4) eu-west-1 : EU (Ireland)
5) eu-central-1 : EU (Frankfurt)
6) ap-south-1 : Asia Pacific (Mumbai)
7) ap-southeast-1 : Asia Pacific (Singapore)
8) ap-southeast-2 : Asia Pacific (Sydney)
9) ap-northeast-1 : Asia Pacific (Tokyo)
10) ap-northeast-2 : Asia Pacific (Seoul)
11) sa-east-1 : South America (Sao Paulo)
12) cn-north-1 : China (Beijing)
13) cn-northwest-1 : China (Ningxia)
14) us-east-2 : US East (Ohio)
15) ca-central-1 : Canada (Central)
16) eu-west-2 : EU (London)
17) eu-west-3 : EU (Paris)
18) eu-north-1 : EU (Stockholm)
(default is 3):
```

如上图，提示我们选择区域。

最后，我们可以选择应用程序：

```plaintext
>
Select an application to use
1) baeldung-demo
2) [ Create new Application ]
(default is 2): 

```

此时，CLI 将创建一个名为.elasticbeanstalk/config.yml的文件。 该文件将保留项目的默认值。

## 3.数据库

现在，我们可以在 AWS Web 控制台上或使用 CLI 使用以下命令创建数据库：

```bash
eb create --single --database
```

我们需要按照说明提供用户名和密码。

创建数据库后，让我们现在为我们的应用程序配置 RDS 凭据。我们将通过在我们的应用程序中 创建 src/main/resources/application-beanstalk.properties在名为beanstalk的 Spring 配置文件中执行此操作：

```plaintext
spring.datasource.url=jdbc:mysql://${rds.hostname}:${rds.port}/${rds.db.name}
spring.datasource.username=${rds.username}
spring.datasource.password=${rds.password}

```

Spring 将搜索名为 rds.hostname 的属性作为名为RDS_HOSTNAME的环境变量。相同的逻辑将适用于其余部分。

## 4.申请

现在，我们将向pom.xml添加一个 Beanstalk –特定的 Maven 配置文件 ：

```xml
<profile>
    <id>beanstalk</id>
    <build>
        <finalName>${project.name}-eb</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
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
</profile>
```

接下来，我们将在 Elastic Beanstalk 配置文件 .elasticbeanstalk/config.yml中指定工件：

```plaintext
deploy:
  artifact: target/spring-boot-bootstrap-eb.jar

```

最后，我们将在 Elastic Beanstalk 中包含两个环境变量。第一个将指定活动的 Spring 配置文件，第二个将确保使用 Beanstalk 期望的默认端口 5000：

```bash
eb setenv SPRING_PROFILES_ACTIVE=beanstalk,mysql
eb setenv SERVER_PORT=5000
```

## 5.部署和测试

现在我们准备构建和部署：

```bash
mvn clean package spring-boot:repackage
eb deploy

```

接下来，我们将检查状态并确定已部署应用程序的 DNS 名称：

```bash
eb status
```

我们的输出应该是这样的：

```plaintext
Environment details for: BaeldungDemo-env
  Application name: baeldung-demo
  Region: us-east-2
  Deployed Version: app-181216_154233
  Environment ID: e-42mypzuc2x
  Platform: arn:aws:elasticbeanstalk:us-east-2::platform/Java 8 running on 64bit Amazon Linux/2.7.7
  Tier: WebServer-Standard-1.0
  CNAME: BaeldungDemo-env.uv3tr7qfy9.us-east-2.elasticbeanstalk.com
  Updated: 2018-12-16 13:43:22.294000+00:00
  Status: Ready
  Health: Green
```

我们现在可以测试应用程序——注意使用 CNAME 字段作为 DNS 来完成 URL。

让我们现在向我们的图书馆添加一本书：

```bash
http POST http://baeldungdemo-env.uv3tr7qfy9.us-east-2.elasticbeanstalk.com/api/books title="The Player of Games" author="Iain M. Banks"
```

而且，如果一切顺利，我们应该得到如下信息：

```plaintext
HTTP/1.1 201 
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Connection: keep-alive
Content-Type: application/json;charset=UTF-8
Date: Wed, 19 Dec 2018 15:36:31 GMT
Expires: 0
Pragma: no-cache
Server: nginx/1.12.1
Transfer-Encoding: chunked
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block

{
    "author": "Iain M. Banks",
    "id": 5,
    "title": "The Player of Games"
}
```

## 6. 扩展应用程序

最后，我们扩展部署以运行两个实例：

```bash
eb scale 2
```

Beanstalk 现在将运行应用程序的 2 个实例，并在两个实例之间负载平衡流量。

生产的自动缩放有点复杂，所以我们改天再讲[。](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/using-features.managing.as.html)

## 七、总结

在本教程中，我们：

-   安装并配置了 AWS Beanstalk CLI 并配置了在线环境
-   部署 MySQL 服务并配置数据库连接属性
-   构建并部署我们配置的 Spring Boot 应用程序，以及
-   测试并扩展了应用程序

有关详细信息，请查看[Beanstalk Java 文档](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/create_deploy_Java.html)。