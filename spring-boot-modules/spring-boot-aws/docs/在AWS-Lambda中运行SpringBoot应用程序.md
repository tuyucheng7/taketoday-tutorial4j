## 1. 概述

在本教程中，我们将探讨如何使用[Serverless应用程序模型(SAM)](https://aws.amazon.com/it/serverless/sam/)框架将Spring Boot应用程序部署到AWS Lambda。

我们可能会发现这种方法对于将现有API服务器迁移到Serverless很有用。

通过这样做，我们可以利用AWS Lambda的可扩展性和按执行付费定价模型来高效且经济地运行我们的应用程序。

## 2. 了解Lambda

[AWS Lambda](https://www.baeldung.com/java-aws-lambda)是Amazon Web Services(AWS)提供的Serverless计算服务。它允许我们运行代码而无需配置或管理服务器。

Lambda函数与传统服务器之间的主要区别之一是**Lambda函数是事件驱动的并且生命周期非常短**。

Lambda函数不像服务器那样连续运行，而是仅在响应特定事件时运行，例如API请求、队列中的消息或文件上传到S3。

我们应该注意到，lambda需要时间来启动它们服务的第一个请求。这称为“冷启动”。

如果下一个请求在短时间内到来，则可能会使用相同的lambda运行时，这称为“热启动”。如果同时出现多个请求，则会启动多个Lambda运行时。

由于与Lambda的理想毫秒数相比，**Spring Boot的启动时间相对较长**，因此我们将讨论这对性能的影响。

## 3. 项目设置

因此，让我们通过修改pom.xml并添加一些配置来迁移现有的Spring Boot项目。

支持的Spring版本为2.2.x、2.3.x、2.4.x、2.5.x、2.6.x和2.7.x。

### 3.1 示例Spring Boot API

我们的应用程序由一个简单的API组成，该API处理对api/v1/users端点的任何GET请求：

```java
@RestController
@RequestMapping("/api/v1/")
public class ProfileController {

    @GetMapping(value = "users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getUser() {
        return List.of(new User("John", "Doe", "john.doe@tuyucheng.com"),
              new User("John", "Doe", "john.doe-2@tuyucheng.com"));
    }
}
```

以User对象列表响应：

```java
public class User {

    private String name;
    private String surname;
    private String emailAddress;

    // standard constructor, getters and setters
}
```

让我们启动我们的应用程序并调用API：

```shell
$ java -jar app.jar
$ curl -X GET http://localhost:8080/api/v1/users -H "Content-Type: application/json"
```

API响应为：

```json
[
    {
        "name": "John",
        "surname": "Doe",
        "email": "john.doe@tuyucheng.come"
    },
    {
        "name": "John",
        "surname": "Doe",
        "email": "john.doe-2@tuyucheng.come"
    }
]
```

### 3.2 通过Maven将Spring Boot应用程序转换为Lambda

为了在Lambda上运行我们的应用程序，让我们将[aws-serverless-java-container-springboot2](https://central.sonatype.com/artifact/com.amazonaws.serverless/aws-serverless-java-container-springboot2/1.9.1)依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>com.amazonaws.serverless</groupId>
    <artifactId>aws-serverless-java-container-springboot2</artifactId>
    <version>${springboot2.aws.version}</version>
</dependency>
```

然后，我们将添加[maven-shade-plugin](https://central.sonatype.com/artifact/org.apache.maven.plugins/maven-shade-plugin/3.3.0)并删除[spring-boot-maven-plugin](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-maven-plugin/3.0.4)。

[Maven Shade Plugin](https://maven.apache.org/plugins/maven-shade-plugin/)用于创建阴影(或uber)JAR文件。**阴影JAR文件是一个自包含的可执行JAR文件，它在JAR本身中包含所有依赖项**，因此它可以独立运行：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.3.0</version>
    <configuration>
        <createDependencyReducedPom>false</createDependencyReducedPom>
    </configuration>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <artifactSet>
                    <excludes>
                        <exclude>org.apache.tomcat.embed:*</exclude>
                    </excludes>
                </artifactSet>
            </configuration>
        </execution>
    </executions>
</plugin>
```

总的来说，此配置将在Maven构建的package阶段生成一个阴影JAR文件。

JAR文件将包含Spring Boot通常打包的所有类和资源，Tomcat除外。我们不需要运行嵌入式Web容器即可与AWS Lambda配合使用。

## 4. Lambda处理程序

下一步是创建一个实现RequestHandler的类。

**RequestHandler是一个接口，它定义了单个方法handleRequest**。有几种不同的方法来处理请求，具体取决于我们正在构建的Lambda类型。

在这种情况下，我们正在处理来自API网关的请求，因此我们可以使用RequestHandler<AwsProxyRequest, AwsProxyResponse\>版本，其中输入是API网关请求，响应是API网关响应。

由AWS提供的Spring Boot Serverless库为我们提供了一个特殊的SpringBootLambdaContainerHandler类，用于通过Spring处理API调用，从而使Spring Boot API服务器代码库像Lambda一样运行。

### 4.1 启动时间

我们应该注意到，在AWS Lambda中，**初始化阶段的时间限制为10秒**。

如果我们的应用程序启动时间超过此时间，AWS Lambda将超时并尝试启动新的Lambda运行时。

根据我们的Spring Boot应用程序启动的速度，我们可以选择两种方式来初始化我们的Lambda处理程序：

-   同步：应用程序的启动时间远小于时间限制
-   异步：应用程序的启动时间可能需要更长的时间

### 4.2 同步初始化

让我们在我们的Spring Boot项目中定义一个新的处理程序：

```java
public class LambdaHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {
    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    static {
        try {
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(Application.class); }
        catch (ContainerInitializationException ex){
            throw new RuntimeException("Unable to load spring boot application",ex); }
    }

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest input, Context context) {
        return handler.proxy(input, context);
    }
}
```

我们使用SpringBootLambdaContainerHandler来处理API网关请求并通过我们的应用程序上下文传递它们。我们在LambdaHandler类的静态构造函数中初始化此处理程序，并从handleRequest函数中调用它。

**然后，处理程序对象调用Spring Boot应用程序中的适当方法来处理请求并生成响应**。最后，它将响应返回给Lambda运行时以传递回API网关。

让我们通过Lambda处理程序调用我们的API：

```java
@Test
void whenTheUsersPathIsInvokedViaLambda_thenShouldReturnAList() throws IOException {
    LambdaHandler lambdaHandler = new LambdaHandler();
    AwsProxyRequest req = new AwsProxyRequestBuilder("/api/v1/users", "GET").build();
    AwsProxyResponse resp = lambdaHandler.handleRequest(req, lambdaContext);
    Assertions.assertNotNull(resp.getBody());
    Assertions.assertEquals(200, resp.getStatusCode());
}
```

### 4.3 异步初始化

有时Spring Boot应用程序可能启动缓慢。这是因为，在启动阶段，Spring引擎会构建其[上下文](https://www.baeldung.com/spring-application-context)，扫描并初始化代码库中的所有bean。

此过程可能会影响启动时间，并可能在Serverless环境中产生很多问题。

为了解决这个问题，我们可以定义一个新的处理程序：

```java
public class AsynchronousLambdaHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {
    private SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    public AsynchronousLambdaHandler() throws ContainerInitializationException {
        handler = (SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse>)
              new SpringBootProxyHandlerBuilder()
                    .springBootApplication(Application.class)
                    .asyncInit()
                    .buildAndInitialize();
    }

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest input, Context context) {
        return handler.proxy(input, context);
    }
}
```

这种方法与前一种方法类似。在此实例中，SpringBootLambdaContainerHandler是在请求处理程序的对象构造函数中构造的，而不是在静态构造函数中构造的。因此，它在Lambda启动的不同阶段执行。

## 5. 部署应用程序

AWS SAM(Serverless应用程序模型)是一个用于在AWS上构建Serverless应用程序的开源框架。

为我们的Spring Boot应用程序定义Lambda处理程序后，我们需要准备所有组件以使用SAM进行部署。

### 5.1 SAM模板

**SAM模板(SAM YAML)是一个YAML格式的文件，它定义了部署Serverless应用程序所需的AWS资源**。基本上，它提供了一种声明方式来指定Serverless应用程序的配置。

因此，让我们定义我们的template.yaml：

```yaml
AWSTemplateFormatVersion: '2010-09-09'
Transform: AWS::Serverless-2016-10-31

Globals:
    Function:
        Timeout: 30

Resources:
    ProfileApiFunction:
        Type: AWS::Serverless::Function
        Properties:
            CodeUri: .
            Handler: cn.tuyucheng.taketoday.aws.handler.LambdaHandler::handleRequest
            Runtime: java11
            AutoPublishAlias: production
            SnapStart:
                ApplyOn: PublishedVersions
            Architectures:
                - x86_64
            MemorySize: 2048
            Environment:
                Variables:
                    JAVA_TOOL_OPTIONS: -XX:+TieredCompilation -XX:TieredStopAtLevel=1
            Events:
                HelloWorld:
                    Type: Api
                    Properties:
                        Path: /{proxy+}
                        Method: ANY
```

关于该配置的解释：

-   type：表示此资源是使用AWS::Serverless::Function资源类型定义的AWS Lambda函数
-   coreUri：指定函数代码的位置
-   AutoPublishAlias：指定AWS Lambda在自动发布函数的新版本时应使用的别名
-   Handler：指定lambda处理程序类
-   Events：指定触发Lambda函数的事件
-   Type：指定这是一个Api事件源
-   Properties：对于API事件，这定义了API网关应响应的HTTP方法和路径

### 5.2 SAM部署

是时候将我们的应用程序部署为AWS Lambda了。

第一步是下载并安装[AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)，然后是[AWS SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html)。

让我们在template.yaml所在的路径上运行AWS SAM CLI并执行命令：

```shell
$ sam build
```

当我们运行此命令时，AWS SAM CLI会将我们的Lambda函数的源代码和依赖项打包并构建到一个ZIP文件中，该文件用作我们的部署程序包。

让我们在本地部署我们的应用程序：

```shell
$ sam local start-api
```

接下来，让我们在通过sam local运行时触发我们的Spring Boot服务：

```shell
$ curl localhost:3000/api/v1/users
```

API响应与之前相同：

```json
[
    {
        "name": "John",
        "surname": "Doe",
        "email": "john.doe@tuyucheng.come"
    },
    {
        "name": "John",
        "surname": "Doe",
        "email": "john.doe-2@tuyucheng.come"
    }
]
```

我们也可以将它部署到AWS：

```shell
$ sam deploy
```

## 6. 在Lambda中使用Spring的限制

尽管Spring是用于构建复杂且可扩展的应用程序的强大而灵活的框架，但它可能并不总是在Lambda上下文中使用的最佳选择。

这样做的主要原因是**Lambda被设计为小型、单一用途的函数，可以快速高效地执行**。

### 6.1 冷启动

AWS Lambda函数的冷启动时间是在处理事件之前初始化函数环境所花费的时间。

有几个因素会影响Lambda函数的冷启动性能：

-   包大小：包越大，初始化时间越长，冷启动越慢。
-   初始化时间：Spring框架初始化和设置应用程序上下文所花费的时间。这包括初始化任何依赖项，例如数据库连接、HTTP客户端或缓存框架。
-   自定义初始化逻辑：重要的是尽量减少自定义初始化逻辑的数量并确保它针对冷启动进行优化。

**我们可以使用[Lambda SnapStart](https://docs.aws.amazon.com/lambda/latest/dg/snapstart.html)缩短启动时间**。

### 6.2 数据库连接池

在像AWS Lambda这样按需执行函数的Serverless环境中，维护[连接池](https://www.baeldung.com/java-connection-pooling)可能具有挑战性。

**当事件触发Lambda时，AWS Lambda引擎可以创建应用程序的新实例**。在请求之间，运行时会暂停或终止。

许多连接池持有打开的连接。这可能会导致混乱或错误，因为池在热启动后被重用，并且可能导致某些数据库引擎的资源泄漏。简而言之，标准连接池依赖于服务器持续运行和维护连接。

为了解决这个问题，AWS提供了一个名为[RDS Proxy](https://aws.amazon.com/it/rds/proxy/)的解决方案，它为Lambda函数提供连接池服务。

通过使用RDS Proxy，Lambda函数可以连接到数据库，而无需维护自己的连接池。

## 7. 总结

在本文中，我们学习了如何将现有的Spring Boot API应用程序转换为AWS Lambda。

我们查看了AWS提供的库来帮助解决这个问题。此外，我们还考虑了Spring Boot较慢的启动时间如何影响我们的设置方式。

然后我们研究了如何部署Lambda并使用SAM CLI对其进行测试。