## 1. 简介

在本文中，我们将了解Spring Boot Cloud CLI(或简称Cloud CLI)。该工具为Spring Boot CLI提供了一组命令行增强功能，有助于进一步抽象和简化Spring Cloud部署。

CLI于2016年底推出，**允许使用命令行、.yml配置文件和Groovy脚本快速自动配置和部署标准Spring Cloud服务**。

## 2. 设置

Spring Boot Cloud CLI 1.3.x需要Spring Boot CLI 1.5.x，因此请确保从[Maven Central](https://search.maven.org/classic/#search|ga|1|a%3A"spring-boot-cli")([安装说明](https://docs.spring.io/spring-boot/docs/current/reference/html/getting-started-installing-spring-boot.html#getting-started-manual-cli-installation))获取最新版本的Spring Boot CLI，并从[Maven Repository](https://search.maven.org/classic/#search|ga|1|a%3A"spring-cloud-cli")([官方Spring仓库](https://repo.spring.io/snapshot/org/springframework/cloud/spring-cloud-cli/))获取最新版本的Cloud CLI！

为确保CLI已安装并可以使用，只需运行：

```shell
$ spring --version
```

验证你的Spring Boot CLI安装后，安装最新稳定版本的Cloud CLI：

```shell
$ spring install org.springframework.cloud:spring-cloud-cli:1.3.2.RELEASE
```

然后验证Cloud CLI：

```shell
$ spring cloud --version
```

可以在官方Cloud CLI[页面](https://cloud.spring.io/spring-cloud-cli/)上找到高级安装功能！

## 3. 默认服务和配置

CLI提供七种核心服务，可以使用单行命令运行和部署。

要在http://localhost:8888上启动Cloud Config服务器，请执行以下操作：

```shell
$ spring cloud configserver
```

在http://localhost:8761上启动Eureka服务器：

```shell
$ spring cloud eureka
```

在http://localhost:9095上启动H2服务器：

```shell
$ spring cloud h2
```

要在http://localhost:9091上启动Kafka服务器：

```shell
$ spring cloud kafka
```

要在http://localhost:9411上启动Zipkin服务器：

```shell
$ spring cloud zipkin
```

要在http://localhost:9393上启动数据流服务器：

```shell
$ spring cloud dataflow
```

要在http://localhost:7979上启动Hystrix仪表板：

```shell
$ spring cloud hystrixdashboard
```

列出当前运行的云服务：

```shell
$ spring cloud --list
```

方便的帮助命令：

```shell
$ spring help cloud
```

有关这些命令的更多详细信息，请查看官方[博客](https://spring.io/blog/2016/11/02/introducing-the-spring-cloud-cli-launcher)。

## 4. 使用YML定制云服务

每个可通过Cloud CLI部署的服务也可以使用相应命名的.yml文件进行配置：

```yaml
spring:
    profiles:
        active: git
    cloud:
        config:
            server:
                git:
                    uri: https://github.com/spring-cloud-samples/config-repo
```

这构成了一个简单的配置文件，我们可以使用它来启动云配置服务器。

例如，我们可以指定一个Git仓库作为URI源，当我们发出“spring cloud configserver”命令时，它将自动克隆和部署。

**Cloud CLI在底层使用Spring Cloud Launcher。这意味着Cloud CLI支持大多数Spring Boot配置机制**。[这](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html)是Spring Boot属性的官方列表。

Spring Cloud配置符合“spring.cloud...”约定。可以在[此链接](https://cloud.spring.io/spring-cloud-static/spring-cloud-config/1.3.3.RELEASE/single/spring-cloud-config.html#_environment_repository)中找到Spring Cloud和Spring Config Server的设置。

我们还可以直接在cloud.yml中指定几个不同的模块和服务：

```yaml
spring:
    cloud:
        launcher:
            deployables:
                -   name: configserver
                    coordinates: maven://...:spring-cloud-launcher-configserver:1.3.2.RELEASE
                    port: 8888
                    waitUntilStarted: true
                    order: -10
                -   name: eureka
                    coordinates: maven:/...:spring-cloud-launcher-eureka:1.3.2.RELEASE
                    port: 8761
```

cloud.yml允许添加自定义服务或模块以及使用Maven和Git仓库。

## 5. 运行自定义Groovy脚本

自定义组件可以用Groovy编写并高效部署，因为Cloud CLI可以编译和部署Groovy代码。

下面是一个最小REST API实现示例：

```groovy
@RestController
@RequestMapping('/api')
class api {

    @GetMapping('/get')
    def get() { [message: 'Hello'] }
}
```

假设脚本保存为rest.groovy，我们可以像这样启动我们的最小服务器：

```shell
$ spring run rest.groovy
```

Ping http://localhost:8080/api/get 应该显示：

```shell
{"message":"Hello"}
```

## 6. 加密/解密

Cloud CLI还提供了一个用于加密和解密的工具(位于包org.springframework.cloud.cli.command.*中)，该工具可以直接通过命令行使用，也可以通过将值传递给云配置服务器端点来间接使用。

让我们设置它并看看如何使用它。

### 6.1 设置

Cloud CLI和Spring Cloud Config Server都使用org.springframework.security.crypto.encrypt.*来处理加密和解密命令。

因此，两者都需要Oracle在[此处](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)提供的JCE无限强度扩展。

### 6.2 通过命令加密和解密

要通过终端加密“my_value”，请调用：

```shell
$ spring encrypt my_value --key my_key
```

文件路径可以通过使用“@”后跟路径(通常用于RSA公钥)来代替密钥名称(例如上面的“my_key”)：

```shell
$ spring encrypt my_value --key @${WORKSPACE}/foos/foo.pub
```

“my_value”现在将被加密为如下内容：

```shell
c93cb36ce1d09d7d62dffd156ef742faaa56f97f135ebd05e90355f80290ce6b
```

此外，它将存储在内存中的密钥“my_key”下。这允许我们通过命令行将“my_key”解密回“my_value”：

```shell
$ spring decrypt --key my_key
```

我们现在还可以在配置YAML或属性文件中使用加密值，加载时云配置服务器将在其中自动解密：

```shell
encrypted_credential: "{cipher}c93cb36ce1d09d7d62dffd156ef742faaa56f97f135ebd05e90355f80290ce6b"
```

### 6.3 使用配置服务器加密和解密

Spring Cloud Config Server公开RESTful端点，其中密钥和加密值对可以存储在Java安全存储或内存中。

有关如何正确设置和配置云配置服务器以接受对称或非对称加密的更多信息，请查看我们的[文章](https://www.baeldung.com/spring-cloud-configuration)或官方[文档](https://cloud.spring.io/spring-cloud-static/spring-cloud-config/1.3.3.RELEASE/single/spring-cloud-config.html#_encryption_and_decryption)。

使用“spring cloud configserver”命令配置并运行Spring Cloud Config Server后，你将能够调用其API：

```shell
$ curl localhost:8888/encrypt -d mysecret
//682bc583f4641835fa2db009355293665d2647dade3375c0ee201de2a49f7bda
$ curl localhost:8888/decrypt -d 682bc583f4641835fa2db009355293665d2647dade3375c0ee201de2a49f7bda
//mysecret
```

## 7. 总结

我们在这里重点介绍了Spring Boot Cloud CLI。有关更多信息，请查看[官方文档](https://cloud.spring.io/spring-cloud-static/spring-cloud-cli/1.3.2.RELEASE/)。