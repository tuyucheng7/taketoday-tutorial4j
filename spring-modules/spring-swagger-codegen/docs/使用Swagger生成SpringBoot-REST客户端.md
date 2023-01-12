## 1. 概述

在本文中，我们将使用[Swagger Codegen](https://github.com/swagger-api/swagger-codegen)和[OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator)项目从[OpenAPI/Swagger 规范](https://swagger.io/specification/)文件生成 REST 客户端。

此外，我们将创建一个 Spring Boot 项目，我们将在其中使用生成的类。

我们将对所有内容使用[Swagger Petstore](http://petstore.swagger.io/) API 示例。

## 2.使用Swagger Codegen生成REST客户端

Swagger 提供了一个实用程序 jar，它允许我们为各种编程语言和多个框架生成 REST 客户端。

### 2.1. 下载 Jar 文件

code-gen_cli.jar可以从这里[下载](https://search.maven.org/classic/remotecontent?filepath=io/swagger/swagger-codegen-cli/2.2.3/swagger-codegen-cli-2.2.3.jar)。

对于最新版本，请检查[swagger-codegen-cli](https://search.maven.org/classic/#search|gav|1|g%3A"io.swagger" AND a%3A"swagger-codegen-cli")存储库。

### 2.2. 生成客户端

让我们通过执行命令java -jar swagger-code-gen-cli.jar generate 来生成我们的客户端：

```bash
java -jar swagger-codegen-cli.jar generate 
  -i http://petstore.swagger.io/v2/swagger.json 
  --api-package com.baeldung.petstore.client.api 
  --model-package com.baeldung.petstore.client.model 
  --invoker-package com.baeldung.petstore.client.invoker 
  --group-id com.baeldung 
  --artifact-id spring-swagger-codegen-api-client 
  --artifact-version 0.0.1-SNAPSHOT 
  -l java 
  --library resttemplate 
  -o spring-swagger-codegen-api-client
```

提供的参数包括：

-   源 swagger 文件 URL 或路径——使用-i参数提供
-   生成类的包名称——使用–api-package、–model-package、–invoker-package提供
-   生成的 Maven 项目属性–group-id , –artifact-id , –artifact-version
-   生成的客户端的编程语言——使用-l提供
-   实现框架——使用-library提供
-   输出目录——使用-o提供

要列出所有与 Java 相关的选项，请键入以下命令：

```bash
java -jar swagger-codegen-cli.jar config-help -l java
```

Swagger Codegen 支持以下 Java 库(成对的 HTTP 客户端和 JSON 处理库)：

-   jersey1 – Jersey1 + 杰克逊
-   jersey2 – Jersey2 + 杰克逊
-   假装——OpenFeign + Jackson
-   okhttp-gson – OkHttp + Gson
-   改造(过时)——Retrofit1/OkHttp + Gson
-   retrofit2 – Retrofit2/OkHttp + Gson
-   休息模板——Spring RestTemplate + Jackson
-   轻松休息——Resteasy + Jackson

在本文中，我们选择了rest-template，因为它是 Spring 生态系统的一部分。

## 3. 使用 OpenAPI 生成器生成 REST 客户端

OpenAPI 生成器是 Swagger Codegen 的一个分支，能够从任何 OpenAPI 规范 2.0/3.x 文档生成 50 多个客户端。

Swagger Codegen 由 SmartBear 维护，而 OpenAPI Generator 由一个社区维护，该社区包括 Swagger Codegen 的 40 多位顶级贡献者和模板创建者作为创始团队成员。

### 3.1. 安装

也许最简单和最便携的安装方法是使用[npm包](https://www.npmjs.com/package/@openapitools/openapi-generator-cli)包装器，它通过在 Java 代码支持的命令行选项之上提供 CLI 包装器来工作。安装很简单：

```bash
npm install @openapitools/openapi-generator-cli -g
```

对于那些想要 JAR 文件的人，可以在[Maven Central](https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli)中找到它。让我们现在下载它：

```bash
wget https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/4.2.3/openapi-generator-cli-4.2.3.jar 
  -O openapi-generator-cli.jar


```

### 3.2. 生成客户端

首先，OpenAPI Generator 的选项与 Swagger Codegen 的选项几乎相同。最显着的区别是-l语言标志替换为-g生成器标志，它将生成客户端的语言作为参数。

接下来，让我们使用jar命令生成一个与我们使用 Swagger Codegen 生成的客户端等效的客户端：

```bash
java -jar openapi-generator-cli.jar generate 
  -i http://petstore.swagger.io/v2/swagger.json 
  --api-package com.baeldung.petstore.client.api 
  --model-package com.baeldung.petstore.client.model 
  --invoker-package com.baeldung.petstore.client.invoker 
  --group-id com.baeldung 
  --artifact-id spring-openapi-generator-api-client 
  --artifact-version 0.0.1-SNAPSHOT 
  -g java 
  -p java8=true 
  --library resttemplate 
  -o spring-openapi-generator-api-client
```

要列出所有与 Java 相关的选项，请键入以下命令：

```bash
java -jar openapi-generator-cli.jar config-help -g java
```

OpenAPI Generator 支持与 Swagger CodeGen 相同的所有 Java 库以及一些额外的库。OpenAPI Generator 支持以下 Java 库(HTTP 客户端和 JSON 处理库对)：

-   jersey1 – Jersey1 + 杰克逊
-   jersey2 – Jersey2 + 杰克逊
-   假装——OpenFeign + Jackson
-   okhttp-gson – OkHttp + Gson
-   改造(过时)——Retrofit1/OkHttp + Gson
-   retrofit2 – Retrofit2/OkHttp + Gson
-   休息模板——Spring RestTemplate + Jackson
-   webclient – Spring 5 WebClient + Jackson(仅限 OpenAPI 生成器)
-   resteasy – Resteasy + Jackson
-   vertx – VertX + 杰克逊
-   google-api-client – 谷歌 API 客户端 + Jackson
-   rest-assured – rest-assured + Jackson/Gson(仅限Java 8)
-   native – Java 原生 HttpClient + Jackson(仅限 Java 11；仅限 OpenAPI 生成器)
-   microprofile – Microprofile 客户端 + Jackson(仅限 OpenAPI 生成器)

## 4.生成Spring Boot项目

现在让我们创建一个新的 Spring Boot 项目。

### 4.1. Maven 依赖

我们首先将生成的 API 客户端库的依赖项添加到我们的项目pom.xml文件中：

```xml
<dependency>
    <groupId>com.baeldung</groupId>
    <artifactId>spring-swagger-codegen-api-client</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 4.2. 将 API 类公开为 Spring Bean

要访问生成的类，我们需要将它们配置为 beans：

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

### 4.3. API客户端配置

ApiClient类用于配置身份验证、API 的基本路径、公共标头，并负责执行所有 API 请求。

例如，如果你使用 OAuth：

```java
@Bean
public ApiClient apiClient() {
    ApiClient apiClient = new ApiClient();

    OAuth petStoreAuth = (OAuth) apiClient.getAuthentication("petstore_auth");
    petStoreAuth.setAccessToken("special-key");

    return apiClient;
}
```

### 4.4. Spring主要应用

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

### 4.5. 接口使用

由于我们将 API 类配置为 beans，因此我们可以将它们自由地注入到 Spring 管理的类中：

```java
@Autowired
private PetApi petApi;

public List<Pet> findAvailablePets() {
    return petApi.findPetsByStatus(Arrays.asList("available"));
}
```

## 5.替代解决方案

除了执行 Swagger Codegen 或 OpenAPI Generator CLI 之外，还有其他生成 REST 客户端的方法。

### 5.1. Maven插件

可以在pom.xml中轻松配置的[swagger-codegen Maven 插件](https://github.com/swagger-api/swagger-codegen/blob/master/modules/swagger-codegen-maven-plugin/README.md)允许使用与 Swagger Codegen CLI 相同的选项生成客户端。

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

### 5.2. Swagger Codegen 在线生成器 API

一个已经发布的 API，它通过向 URL http://generator.swagger.io/api/gen/clients/java发送 POST 请求来帮助我们生成客户端，并在请求正文中传递规范 URL 和其他选项。

让我们使用一个简单的 curl 命令来做一个例子：

```bash
curl -X POST -H "content-type:application/json" 
  -d '{"swaggerUrl":"http://petstore.swagger.io/v2/swagger.json"}' 
  http://generator.swagger.io/api/gen/clients/java
```

响应将是 JSON 格式，其中包含一个可下载链接，该链接包含生成的 zip 格式的客户端代码。你可以传递与 Swaager Codegen CLI 中使用的相同选项来自定义输出客户端。

[https://generator.swagger.io](https://generator.swagger.io/)包含 API 的 Swagger 文档，我们可以在其中查看其文档并试用。

### 5.3. OpenAPI Generator 在线生成器 API

与 Swagger Godegen 一样，OpenAPI Generator 也有在线生成器。让我们使用一个简单的 curl 命令来执行一个示例：

```bash
curl -X POST -H "content-type:application/json" 
  -d '{"openAPIUrl":"http://petstore.swagger.io/v2/swagger.json"}' 
  http://api.openapi-generator.tech/api/gen/clients/java
```

JSON 格式的响应将包含指向生成的 zip 格式客户端代码的可下载链接。你可以传递 Swagger Codegen CLI 中使用的相同选项来自定义输出客户端。

https://github.com/OpenAPITools/openapi-generator/blob/master/docs/online.md包含 API 的文档。

## 六，总结

Swagger Codegen 和 OpenAPI Generator 使你能够使用多种语言和你选择的库为你的 API 快速生成 REST 客户端。我们可以使用 CLI 工具、Maven 插件或在线 API 生成客户端库。

这是一个基于 Maven 的项目，包含三个 Maven 模块：生成的 Swagger API 客户端、生成的 OpenAPI 客户端和 Spring Boot 应用程序。