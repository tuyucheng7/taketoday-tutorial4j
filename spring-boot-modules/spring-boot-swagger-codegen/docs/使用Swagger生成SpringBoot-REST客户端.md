## 1. 概述

在本文中，我们将使用[Swagger Codegen](https://github.com/swagger-api/swagger-codegen)和[OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator)项目从[OpenAPI/Swagger规范](https://swagger.io/specification/)文件生成REST客户端。

此外，我们将创建一个Spring Boot项目，我们将在其中使用生成的类。

我们将对所有内容使用[Swagger Petstore](http://petstore.swagger.io/) API示例。

## 2. 使用Swagger Codegen生成REST客户端

Swagger提供了一个实用程序jar，它允许我们为各种编程语言和多个框架生成REST客户端。

### 2.1 下载Jar文件

code-gen_cli.jar可以从[这里](https://search.maven.org/classic/remotecontent?filepath=io/swagger/swagger-codegen-cli/2.2.3/swagger-codegen-cli-2.2.3.jar)下载。

对于最新版本，请检查[swagger-codegen-cli](https://central.sonatype.com/artifact/io.swagger/swagger-codegen-cli/3.0.0-rc1)仓库。

### 2.2 生成客户端

让我们通过执行命令java -jar swagger-code-gen-cli.jar generate来生成我们的客户端：

```shell
java -jar swagger-codegen-cli.jar generate \
  -i http://petstore.swagger.io/v2/swagger.json \
  --api-package cn.tuyucheng.taketoday.petstore.client.api \
  --model-package cn.tuyucheng.taketoday.petstore.client.model \
  --invoker-package cn.tuyucheng.taketoday.petstore.client.invoker \
  --group-id cn.tuyucheng.taketoday \
  --artifact-id spring-swagger-codegen-api-client \
  --artifact-version 1.0.0 \
  -l java \
  --library resttemplate \
  -o spring-swagger-codegen-api-client
```

提供的参数包括：

-   源swagger文件URL或路径：使用-i参数提供
-   生成类的包名称：使用–api-package、–model-package、–invoker-package提供
-   生成的Maven项目属性–group-id、–artifact-id、–artifact-version
-   生成的客户端的编程语言：使用-l提供
-   实现框架：使用-library提供
-   输出目录：使用-o提供

要列出所有与Java相关的选项，请输入以下命令：

```shell
java -jar swagger-codegen-cli.jar config-help -l java
```

Swagger Codegen支持以下Java库(成对的HTTP客户端和JSON处理库)：

-   jersey1：Jersey1 + Jackson
-   jersey2：Jersey2 + Jackson
-   feign：OpenFeign + Jackson
-   okhttp-gson：OkHttp + Gson
-   retrofit(Obsolete)：Retrofit1/OkHttp + Gson
-   retrofit2：Retrofit2/OkHttp + Gson
-   rest-template：Spring RestTemplate + Jackson
-   rest-easy：Resteasy + Jackson

在这篇文章中，我们选择了rest-template，因为它是Spring生态系统的一部分。

## 3. 使用OpenAPI Generator生成REST客户端

OpenAPI Generator是Swagger Codegen的一个分支，能够从任何OpenAPI规范2.0/3.x文档生成50多个客户端。

Swagger Codegen由SmartBear维护，而OpenAPI Generator由一个社区维护，该社区包括Swagger Codegen的40多位顶级贡献者和模板创建者作为创始团队成员。

### 3.1 安装

也许最简单和最便携的安装方法是使用[npm package](https://www.npmjs.com/package/@openapitools/openapi-generator-cli)包装器，它通过在Java代码支持的命令行选项之上提供CLI包装器来工作。安装很简单：

```shell
npm install @openapitools/openapi-generator-cli -g
```

对于那些想要JAR文件的人，可以在[Maven Central](https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli)中找到它。让我们现在下载它：

```shell
wget https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/4.2.3/openapi-generator-cli-4.2.3.jar \
  -O openapi-generator-cli.jar
```

### 3.2 生成客户端

首先，OpenAPI Generator的选项与Swagger Codegen的选项几乎相同。最显著的区别是-l语言标志替换为-g生成器标志，该标志将生成客户端的语言作为参数。

接下来，让我们使用jar命令生成一个与我们使用Swagger Codegen生成的客户端等效的客户端：

```bash
java -jar openapi-generator-cli.jar generate \
  -i http://petstore.swagger.io/v2/swagger.json \
  --api-package cn.tuyucheng.taketoday.petstore.client.api \
  --model-package cn.tuyucheng.taketoday.petstore.client.model \
  --invoker-package cn.tuyucheng.taketoday.petstore.client.invoker \
  --group-id cn.tuyucheng.taketoday \
  --artifact-id spring-openapi-generator-api-client \
  --artifact-version 1.0.0 \
  -g java \
  -p java8=true \
  --library resttemplate \
  -o spring-openapi-generator-api-client
```

要列出所有与Java相关的选项，请输入以下命令：

```shell
java -jar openapi-generator-cli.jar config-help -g java
```

OpenAPI Generator支持与Swagger CodeGen相同的所有Java库以及一些额外的库。OpenAPI Generator支持以下Java库(HTTP客户端和JSON处理库对)：

-   jersey1：Jersey1 + Jackson
-   jersey2：Jersey2 + Jackson
-   feign：OpenFeign + Jackson
-   okhttp-gson：OkHttp + Gson
-   retrofit(Obsolete)：Retrofit1/OkHttp + Gson
-   retrofit2：Retrofit2/OkHttp + Gson
-   resttemplate：Spring RestTemplate + Jackson
-   webclient：Spring 5 WebClient + Jackson(仅限OpenAPI Generator)
-   resteasy：Resteasy + Jackson
-   vertx：VertX + Jackson
-   google-api-client：Google API Client + Jackson
-   rest-assured：rest-assured + Jackson/Gson(仅限Java 8)
-   native：Java原生HttpClient + Jackson(仅限Java 11；仅限OpenAPI Generator)
-   microprofile：Microprofile客户端 + Jackson(仅限OpenAPI Generator)

## 4. 生成Spring Boot项目

现在让我们创建一个新的Spring Boot项目。

### 4.1 Maven依赖

我们首先将生成的API客户端库的依赖项添加到我们的项目pom.xml文件中：

```xml
<dependency>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>spring-swagger-codegen-api-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 4.2 将API类公开为Spring Bean

要访问生成的类，我们需要将它们配置为beans：

```java
@Configuration
public class PetStoreIntegrationConfig {

    @Bean
    public PetApi petApi() {
        return new PetApi(apiClient());
    }

    @Bean
    public ApiClient apiClient() {
        return new ApiClient();
    }
}
```

### 4.3 API客户端配置

ApiClient类用于配置身份验证、API的基本路径、公共标头，并负责执行所有API请求。

例如，如果你使用OAuth：

```java
@Bean
public ApiClient apiClient() {
    ApiClient apiClient = new ApiClient();

    OAuth petStoreAuth = (OAuth) apiClient.getAuthentication("petstore_auth");
    petStoreAuth.setAccessToken("special-key");

    return apiClient;
}
```

### 4.4 Spring主类

我们需要导入新创建的配置：

```java
@SpringBootApplication
@Import(PetStoreIntegrationConfig.class)
public class PetStoreApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(PetStoreApplication.class, args);
    }
}
```

### 4.5 接口使用

由于我们将API类配置为beans，因此我们可以将它们自由地注入到Spring管理的类中：

```java
@Autowired
private PetApi petApi;

public List<Pet> findAvailablePets() {
    return petApi.findPetsByStatus(Arrays.asList("available"));
}
```

## 5. 替代解决方案

除了执行Swagger Codegen或OpenAPI Generator CLI之外，还有其他生成REST客户端的方法。

### 5.1 Maven插件

可以在pom.xml中轻松配置的[swagger-codegen Maven插件](https://github.com/swagger-api/swagger-codegen/blob/master/modules/swagger-codegen-maven-plugin/README.md)允许使用与Swagger Codegen CLI相同的选项生成客户端。

这是一个基本的代码片段，我们可以将其包含在项目的pom.xml中以自动生成客户端：

```xml
<plugin>
    <groupId>io.swagger</groupId>
    <artifactId>swagger-codegen-maven-plugin</artifactId>
    <version>2.2.3</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>swagger.yaml</inputSpec>
                <language>java</language>
                <library>resttemplate</library>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### 5.2 Swagger Codegen在线生成器API

一个已经发布的API，它通过向URL [http://generator.swagger.io/api/gen/clients/java](http://generator.swagger.io/api/gen/clients/java)发送POST请求来帮助我们生成客户端，并在请求正文中传递规范URL和其他选项。

让我们使用一个简单的curl命令举例子：

```shell
curl -X POST -H "content-type:application/json" \
  -d '{"swaggerUrl":"http://petstore.swagger.io/v2/swagger.json"}' \
  http://generator.swagger.io/api/gen/clients/java
```

响应将是JSON格式，其中包含一个可下载的链接，该链接包含生成的zip格式的客户端代码。你可以传递与Swagger Codegen CLI中使用的相同选项来自定义输出客户端。

[https://generator.swagger.io](https://generator.swagger.io/)包含API的Swagger文档，我们可以在其中查看其文档并试用。

### 5.3 OpenAPI Generator在线生成器API

与Swagger Godegen一样，OpenAPI Generator也有在线生成器。让我们使用一个简单的curl命令来执行一个示例：

```bash
curl -X POST -H "content-type:application/json" \
  -d '{"openAPIUrl":"http://petstore.swagger.io/v2/swagger.json"}' \
  http://api.openapi-generator.tech/api/gen/clients/java
```

JSON格式的响应将包含指向生成的zip格式客户端代码的可下载链接。你可以传递Swagger Codegen CLI中使用的相同选项来自定义输出客户端。

[https://github.com/OpenAPITools/openapi-generator/blob/master/docs/online.md](https://github.com/OpenAPITools/openapi-generator/blob/master/docs/online.md)包含API的文档。

## 6. 总结

Swagger Codegen和OpenAPI Generator使你能够使用多种语言和你选择的库为你的API快速生成REST客户端。我们可以使用CLI工具、Maven插件或在线API生成客户端库。

这是一个基于Maven的项目，包含三个Maven模块：生成的Swagger API客户端、生成的OpenAPI客户端和Spring Boot应用程序。