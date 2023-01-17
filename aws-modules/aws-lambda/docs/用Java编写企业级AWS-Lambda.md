## 1. 概述

用 Java编写[基本的 AWS Lambda](https://www.baeldung.com/java-aws-lambda)不需要太多代码。为了保持小型化，我们通常创建没有框架支持的无服务器应用程序。

然而，如果我们需要以企业质量部署和监控我们的软件，我们需要解决许多使用 Spring 等框架开箱即用的问题。

在本教程中，我们将了解如何在 AWS Lambda 中包含配置和日志记录功能，以及如何减少样板代码的库，同时仍然保持轻量级。

## 2. 构建示例

### 2.1. 框架选项

不能使用Spring Boot等框架来创建 AWS Lambda。Lambda 具有与服务器应用程序不同的生命周期，并且它与 AWS 运行时接口而不直接使用 HTTP。

Spring 提供了[Spring Cloud Function](https://www.baeldung.com/spring-cloud-function)，它可以帮助我们创建一个 AWS Lambda，但我们往往需要更小更简单的东西。

我们将从[DropWizard](https://www.baeldung.com/java-dropwizard)中获得灵感，它的功能集比 Spring 小，但仍支持通用标准，包括可配置性、日志记录和依赖项注入。

虽然我们可能不需要从一个 Lambda 到下一个 Lambda 的每一个功能，但我们将构建一个示例来解决所有这些问题，因此我们可以选择在未来的开发中使用哪些技术。

### 2.2. 示例问题

让我们创建一个每隔几分钟运行一次的应用程序。它会查看“待办事项列表”，找到未标记为已完成的最早的工作，然后创建一个博客帖子作为提醒。它还将生成有用的日志，以允许 CloudWatch 警报发出错误警报。

我们将使用[JsonPlaceholder](https://jsonplaceholder.typicode.com/)上的 API作为我们的后端，我们将使应用程序可针对 API 的基本 URL 和我们将在该环境中使用的凭据进行配置。

### 2.3. 基本设置

我们将使用[AWS SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)创建一个基本的 [Hello World 示例](https://www.baeldung.com/java-aws-lambda-hibernate#2-creating-the-sam-template)。

然后我们将默认的 App类(其中包含一个示例 API 处理程序)更改为一个在启动时记录的简单 RequestStreamHandler：

```java
public class App implements RequestStreamHandler {

    @Override
    public void handleRequest(
      InputStream inputStream, 
      OutputStream outputStream, 
      Context context) throws IOException {
        context.getLogger().log("App startingn");
    }
}
```

由于我们的示例不是 API 处理程序，因此我们不需要读取任何输入或产生任何输出。现在，我们在传递给函数的Context中使用LambdaLogger来进行日志记录，不过稍后，我们将看看如何使用[Log4j](https://www.baeldung.com/log4j2-appenders-layouts-filters)和[Slf4j](https://www.baeldung.com/slf4j-with-log4j2-logback)。

让我们快速测试一下：

```bash
$ sam build
$ sam local invoke

Mounting todo-reminder/.aws-sam/build/ToDoFunction as /var/task:ro,delegated inside runtime container
App starting
END RequestId: 2aaf6041-cf57-4414-816d-76a63c7109fd
REPORT RequestId: 2aaf6041-cf57-4414-816d-76a63c7109fd  Init Duration: 0.12 ms  Duration: 121.70 ms
  Billed Duration: 200 ms Memory Size: 512 MB     Max Memory Used: 512 MB 

```

我们的存根应用程序已启动并将“App starting”记录到日志中。

## 三、配置

由于我们可能将应用程序部署到多个环境，或者希望将凭证之类的东西与我们的代码分开，因此我们需要能够在部署或运行时传递配置值。这通常是通过设置环境变量来实现的。

### 3.1. 将环境变量添加到模板

template.yaml 文件包含 lambda 的设置 。我们可以使用AWS::Serverless::Function部分下的Environment部分将环境变量添加到我们的函数中 ：

```yaml
Environment: 
  Variables:
    PARAM1: VALUE
```

生成的示例模板有一个硬编码的环境变量 PARAM1，但我们需要在部署时设置我们的环境变量。

假设我们希望我们的应用程序在变量 ENV_NAME中知道其环境的名称。

首先，让我们使用默认环境名称将参数添加到template.yaml文件的最顶部：

```yaml
AWSTemplateFormatVersion: '2010-09-09'
Transform: AWS::Serverless-2016-10-31
Description: todo-reminder application

Parameters:
  EnvironmentName:
    Type: String
    Default: dev
```

接下来，让我们将该参数连接到AWS::Serverless::Function部分中的环境变量：

```yaml
Environment: 
  Variables: 
    ENV_NAME: !Ref EnvironmentName
```

现在，我们准备好在运行时读取环境变量。

### 3.2. 读取环境变量

让我们在构建App对象时[读取环境变量](https://www.baeldung.com/java-system-get-property-vs-system-getenv#using-systemgetenv) ENV_NAME ：

```java
private String environmentName = System.getenv("ENV_NAME");
```

我们还可以在 调用handleRequest时记录环境 ：

```java
context.getLogger().log("Environment: " + environmentName + "n");
```

日志消息必须以“n”结尾以分隔日志行。我们可以看到输出：

```bash
$ sam build
$ sam local invoke

START RequestId: 12fb0c05-f222-4352-a26d-28c7b6e55ac6 Version: $LATEST
App starting
Environment: dev
```

在这里，我们看到环境已从template.yaml中的默认值设置。

### 3.3. 更改参数值

我们可以使用参数覆盖在运行时或部署时提供不同的值：

```bash
$ sam local invoke --parameter-overrides "ParameterKey=EnvironmentName,ParameterValue=test"

START RequestId: 18460a04-4f8b-46cb-9aca-e15ce959f6fa Version: $LATEST
App starting
Environment: test
```

### 3.4. 使用环境变量进行单元测试

由于环境变量对应用程序来说是全局的，我们可能会试图将其初始化为 私有静态最终常量。然而，这使得单元测试变得非常困难。

由于处理程序类在应用程序的整个生命周期中由 AWS Lambda 运行时初始化为单例，因此最好使用处理程序的实例变量来存储运行时状态。

我们可以使用[系统存根](https://www.baeldung.com/java-system-stubs)来设置环境变量，并[使用 Mockito 深度存根](https://www.baeldung.com/mockito-fluent-apis#deep-mocking)使我们的 LambdaLogger在Context中可测试。首先，我们必须将MockitoJUnitRunner添加到测试中：

```java
@RunWith(MockitoJUnitRunner.class)
public class AppTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Context mockContext;

    // ...
}
```

接下来，我们可以使用 EnvironmentVariablesRule使我们能够在 创建App对象之前控制环境变量：

```java
@Rule
public EnvironmentVariablesRule environmentVariablesRule = 
  new EnvironmentVariablesRule();
```

现在，我们可以编写测试：

```java
environmentVariablesRule.set("ENV_NAME", "unitTest");
new App().handleRequest(fakeInputStream, fakeOutputStream, mockContext);

verify(mockContext.getLogger()).log("Environment: unitTestn");
```

随着我们的 lambda 变得越来越复杂，能够对处理程序类进行单元测试非常有用，包括它加载配置的方式。

## 4. 处理复杂配置

对于我们的示例，我们需要 API 的端点地址以及环境名称。端点在测试时可能会有所不同，但它有一个默认值。

我们可以多次 使用System.getenv ，甚至可以使用Optional和 orElse来设置默认值：

```java
String setting = Optional.ofNullable(System.getenv("SETTING"))
  .orElse("default");
```

但是，这可能需要大量重复代码和协调大量单独的String。

### 4.1. 将配置表示为 POJO

如果我们构建一个Java类来包含我们的配置，我们可以与需要它的服务共享它：

```java
public class Config {
    private String toDoEndpoint;
    private String postEndpoint;
    private String environmentName;

    // getters and setters
}
```

现在我们可以使用当前配置构建我们的运行时组件：

```java
public class ToDoReaderService {
    public ToDoReaderService(Config configuration) {
        // ...
    }
}
```

该服务可以从Config对象中获取它需要的任何配置值 。我们甚至可以将配置建模为对象的层次结构，如果我们有重复的结构(如凭据)，这可能会很有用：

```java
private Credentials toDoCredentials;
private Credentials postCredentials;
```

到目前为止，这只是一种设计模式。让我们看看如何在实践中加载这些值。

### 4.2. 配置加载器

我们可以使用[lightweight-config从资源中的](https://github.com/webcompere/lightweight-config).yml文件加载我们的配置 。

让我们将[依赖](https://search.maven.org/classic/#search|gav|1|g%3A"uk.org.webcompere" AND a%3A"lightweight-config")项添加到我们的pom.xml：

```xml
<dependency>
    <groupId>uk.org.webcompere</groupId>
    <artifactId>lightweight-config</artifactId>
    <version>1.1.0</version>
</dependency>
```

然后，让我们在src/main/resources目录中添加一个configuration.yml文件。该文件反映了我们配置 POJO 的结构，并包含硬编码值、从环境变量中填充的占位符和默认值：

```yaml
toDoEndpoint: https://jsonplaceholder.typicode.com/todos
postEndpoint: https://jsonplaceholder.typicode.com/posts
environmentName: ${ENV_NAME}
toDoCredentials:
  username: baeldung
  password: ${TODO_PASSWORD:-password}
postCredentials:
  username: baeldung
  password: ${POST_PASSWORD:-password}
```

我们可以使用ConfigLoader将这些设置加载到我们的 POJO 中：

```java
Config config = ConfigLoader.loadYmlConfigFromResource("configuration.yml", Config.class);
```

这会填充环境变量中的占位符表达式，在:-表达式之后应用默认值。它与 DropWizard 中内置的配置加载器非常相似。

### 4.3. 在某处保持上下文

如果我们有多个组件(包括配置)要在 lambda 首次启动时加载，将它们放在一个中心位置会很有用。

让我们创建一个名为 ExecutionContext的类，应用程序可以使用它来创建对象：

```java
public class ExecutionContext {
    private Config config;
    private ToDoReaderService toDoReaderService;
    
    public ExecutionContext() {
        this.config = 
          ConfigLoader.loadYmlConfigFromResource("configuration.yml", Config.class);
        this.toDoReaderService = new ToDoReaderService(config);
    }
}
```

该 应用程序可以在其初始化列表中创建其中之一：

```java
private ExecutionContext executionContext = new ExecutionContext();
```

现在，当 App需要一个“bean”时，它可以从这个对象中获取它。

## 5. 更好的日志记录

到目前为止，我们对 LambdaLogger的使用非常基础。如果我们引入[执行日志记录](https://www.baeldung.com/java-logging-intro)的库，他们很可能会期望 Log4j或 Slf4j存在。理想情况下，我们的日志行将包含时间戳和其他有用的上下文信息。

最重要的是，当我们遇到错误时，我们应该用大量有用的信息记录它们，而Logger.error通常比自制代码在这个任务上做得更好。

### 5.1. 添加 AWS Log4j 库

我们可以通过向我们的pom.xml添加依赖项来启用[AWS lambda Log4j运行时](https://search.maven.org/classic/#search|gav|1|g%3A"com.amazonaws" AND a%3A"aws-lambda-java-log4j2")：

```xml
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-lambda-java-log4j2</artifactId>
    <version>1.2.0</version>
</dependency>
```

我们还需要 src/main/resources中的log4j2.xml文件 配置为使用此记录器：

```java
<?xml version="1.0" encoding="UTF-8"?>
<Configuration packages="com.amazonaws.services.lambda.runtime.log4j2">
    <Appenders>
        <Lambda name="Lambda">
            <PatternLayout>
                <pattern>%d{yyyy-MM-dd HH:mm:ss} %X{AWSRequestId} %-5p %c{1} - %m%n</pattern>
            </PatternLayout>
        </Lambda>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="Lambda" />
        </Root>
    </Loggers>
</Configuration>
```

### 5.2. 编写日志语句

现在，我们将标准的Log4j Logger样板添加到我们的类中：

```java
public class ToDoReaderService {
    private static final Logger LOGGER = LogManager.getLogger(ToDoReaderService.class);

    public ToDoReaderService(Config configuration) {
        LOGGER.info("ToDo Endpoint on: {}", configuration.getToDoEndpoint());
        // ...
    }

    // ...
}
```

然后我们可以从命令行测试它：

```bash
$ sam build
$ sam local invoke

START RequestId: acb34989-980c-42e5-b8e4-965d9f497d93 Version: $LATEST
2021-05-23 20:57:15  INFO  ToDoReaderService - ToDo Endpoint on: https://jsonplaceholder.typicode.com/todos

```

### 5.3. 单元测试日志输出

在测试日志输出很重要的情况下，我们可以使用系统存根来做到这一点。我们的配置针对 AWS Lambda 进行了优化，将日志输出定向到System.out，我们可以点击：

```java
@Rule
public SystemOutRule systemOutRule = new SystemOutRule();

@Test
public void whenTheServiceStarts_thenItOutputsEndpoint() {
    Config config = new Config();
    config.setToDoEndpoint("https://todo-endpoint.com");
    ToDoReaderService service = new ToDoReaderService(config);

    assertThat(systemOutRule.getLinesNormalized())
      .contains("ToDo Endpoint on: https://todo-endpoint.com");
}
```

### 5.4. 添加 Slf4j 支持

我们可以通过添加依赖来添加[Slf4j](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.logging.log4j" AND a%3A"log4j-slf4j-impl")：

```xml
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-slf4j-impl</artifactId>
    <version>2.13.2</version>
</dependency>
```

这使我们能够看到来自 支持 Slf4j的库的日志消息。我们也可以直接使用它：

```java
public class ExecutionContext {
    private static final Logger LOGGER =
      LoggerFactory.getLogger(ExecutionContext.class);

    public ExecutionContext() {
        LOGGER.info("Loading configuration");
        // ...
    }

    // ...
}
```

Slf4j日志记录通过 AWS Log4j运行时路由：

```bash
$ sam local invoke

START RequestId: 60b2efad-bc77-475b-93f6-6fa7ddfc9f88 Version: $LATEST
2021-05-23 21:13:19  INFO  ExecutionContext - Loading configuration

```

## 6. 使用 Feign 使用 REST API

如果我们的 Lambda 使用 REST 服务，我们可以直接使用JavaHTTP 库。但是，使用轻量级框架也有好处。

[OpenFeign](https://www.baeldung.com/intro-to-feign)是一个很好的选择。它允许我们为 HTTP 客户端、日志记录、JSON 解析等插入我们选择的组件。

### 6.1. 添加伪装

我们将在此示例中使用[Feign](https://search.maven.org/classic/#search|gav|1|g%3A"io.github.openfeign" AND a%3A"feign-core")默认客户端，尽管[Java 11 客户端](https://github.com/OpenFeign/feign/tree/master/java11)也是一个很好的选择，并且可以与基于 Amazon Corretto的 Lambda java11运行时一起使用。

此外，我们将使用 [Slf4j](https://search.maven.org/classic/#search|gav|1|g%3A"io.github.openfeign" AND a%3A"feign-slf4j")日志记录和 [Gson](https://search.maven.org/classic/#search|gav|1|g%3A"io.github.openfeign" AND a%3A"feign-gson")作为我们的 JSON 库：

```xml
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-core</artifactId>
    <version>11.2</version>
</dependency>
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-slf4j</artifactId>
    <version>11.2</version>
</dependency>
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-gson</artifactId>
    <version>11.2</version>
</dependency>
```

我们在这里使用 Gson作为我们的 JSON 库，因为[Gson比Jackson](https://www.baeldung.com/jackson-vs-gson)小得多。我们可以使用[Jackson](https://www.baeldung.com/jackson)，但这会使启动时间变慢。还有使用[Jackson-jr](https://github.com/OpenFeign/feign/tree/master/jackson-jr)的选项，尽管这仍然是实验性的。

### 6.2. 定义一个 Feign 接口

首先，我们描述我们将使用接口调用的 API：

```java
public interface ToDoApi {
    @RequestLine("GET /todos")
    List<ToDoItem> getAllTodos();
}
```

这描述了 API 中的路径以及要从 JSON 响应中生成的任何对象。让我们创建 ToDoItem来模拟来自我们 API 的响应：

```java
public class ToDoItem {
    private int userId;
    private int id;
    private String title;
    private boolean completed;

    // getters and setters
}
```

### 6.3. 从接口定义客户端

接下来，我们使用Feign.Builder将接口转换为客户端：

```java
ToDoApi toDoApi = Feign.builder()
  .decoder(new GsonDecoder())
  .logger(new Slf4jLogger())
  .target(ToDoApi.class, config.getToDoEndpoint());
```

在我们的示例中，我们还使用了凭据。假设这些是通过基本身份验证提供的，这将要求我们在目标调用之前添加一个BasicAuthRequestInterceptor ：

```java
.requestInterceptor(
   new BasicAuthRequestInterceptor(
     config.getToDoCredentials().getUsername(),
     config.getToDoCredentials().getPassword()))
```

## 7. 将对象连接在一起

到目前为止，我们已经为应用程序创建了配置和 bean，但还没有将它们连接在一起。为此，我们有两种选择。我们要么使用纯Java将对象连接在一起，要么使用某种依赖注入解决方案。

### 7.1. 构造函数注入

由于一切都是普通的Java对象，并且我们已经构建了 ExecutionContext类来协调构造，所以我们可以在其构造函数中完成所有工作。

我们可能希望扩展构造函数以按顺序构建所有 bean：

```java
this.config = ... // load config
this.toDoApi = ... // build api
this.postApi = ... // build post API
this.toDoReaderService = new ToDoReaderService(toDoApi);
this.postService = new PostService(postApi);
```

这是最简单的解决方案。它鼓励定义明确的组件，这些组件在运行时既可测试又易于组合。

然而，超过一定数量的组件，这开始变得冗长且难以管理。

### 7.2. 引入依赖注入框架

DropWizard 使用[Guice](https://www.baeldung.com/guice)进行依赖注入。这个库相对较小，可以帮助管理 AWS Lambda 中的组件。

让我们添加它的[依赖](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.inject" AND a%3A"guice")项：

```xml
<dependency>
    <groupId>com.google.inject</groupId>
    <artifactId>guice</artifactId>
    <version>5.0.1</version>
</dependency>
```

### 7.3. 在容易的地方使用注射

我们可以使用@Inject注解来注解从其他 bean 构造的 bean，使它们可以自动注入：

```java
public class PostService {
    private PostApi postApi;

    @Inject
    public PostService(PostApi postApi) {
        this.postApi = postApi;
    }

    // other functions
}
```

### 7.4. 创建自定义注入模块

对于我们必须使用自定义加载或构造代码的任何 bean，我们可以使用 Module作为工厂：

```java
public class Services extends AbstractModule {
    @Override
    protected void configure() {
        Config config = 
          ConfigLoader.loadYmlConfigFromResource("configuration.yml", Config.class);

        ToDoApi toDoApi = Feign.builder()
          .decoder(new GsonDecoder())
          .logger(new Slf4jLogger())
          .logLevel(FULL)
          .requestInterceptor(... // omitted
          .target(ToDoApi.class, config.getToDoEndpoint());

        PostApi postApi = Feign.builder()
          .encoder(new GsonEncoder())
          .logger(new Slf4jLogger())
          .logLevel(FULL)
          .requestInterceptor(... // omitted
          .target(PostApi.class, config.getPostEndpoint());

        bind(Config.class).toInstance(config);
        bind(ToDoApi.class).toInstance(toDoApi);
        bind(PostApi.class).toInstance(postApi);
    }
}
```

然后我们通过Injector在我们的ExecutionContext中使用这个模块：

```java
public ExecutionContext() {
    LOGGER.info("Loading configuration");

    try {
        Injector injector = Guice.createInjector(new Services());
        this.toDoReaderService = injector.getInstance(ToDoReaderService.class);
        this.postService = injector.getInstance(PostService.class);
    } catch (Exception e) {
        LOGGER.error("Could not start", e);
    }
}
```

这种方法可以很好地扩展，因为它将 bean 依赖性定位到最接近每个 bean 的类。通过构建每个 bean 的中央配置类，依赖项中的任何更改也总是需要在那里进行更改。

我们还应注意，记录启动期间发生的错误很重要——如果失败，Lambda 将无法运行。

### 7.5. 一起使用对象

现在我们有了一个ExecutionContext和服务，这些服务内部有 API，由Config 配置，让我们完成我们的处理程序：

```java
@Override
public void handleRequest(InputStream inputStream, 
  OutputStream outputStream, Context context) throws IOException {

    PostService postService = executionContext.getPostService();
    executionContext.getToDoReaderService()
      .getOldestToDo()
      .ifPresent(postService::makePost);
}
```

让我们测试一下：

```bash
$ sam build
$ sam local invoke

Mounting /Users/ashleyfrieze/dev/tutorials/aws-lambda/todo-reminder/.aws-sam/build/ToDoFunction as /var/task:ro,delegated inside runtime container
2021-05-23 22:29:43  INFO  ExecutionContext - Loading configuration
2021-05-23 22:29:44  INFO  ToDoReaderService - ToDo Endpoint on: https://jsonplaceholder.typicode.com
App starting
Environment: dev
2021-05-23 22:29:44 73264c34-ca48-4c3e-a2b4-5e7e74e13960 INFO  PostService - Posting about: ToDoItem{userId=1, id=1, title='delectus aut autem', completed=false}
2021-05-23 22:29:44 73264c34-ca48-4c3e-a2b4-5e7e74e13960 INFO  PostService - Post: PostItem{title='To Do is Out Of Date: 1', body='Not done: delectus aut autem', userId=1}
END RequestId: 73264c34-ca48-4c3e-a2b4-5e7e74e13960

```

## 八. 总结

在本文中，我们了解了使用Java构建企业级 AWS Lambda 时配置和日志记录等功能的重要性。我们看到了像 Spring 和 DropWizard 这样的框架是如何默认提供这些工具的。

我们探索了如何使用环境变量来控制配置，以及如何构建我们的代码以使单元测试成为可能。

然后，我们查看了用于加载配置、构建 REST 客户端、编组 JSON 数据以及将我们的对象连接在一起的库，重点是选择较小的库以使我们的 Lambda 尽快启动。