## 1. 概述

在本教程中，我们将学习如何使用[RestExpress](https://github.com/RestExpress/RestExpress)构建RESTful微服务。

RestExpress是一个开源的Java框架，使我们能够快速轻松地构建RESTful微服务。它基于[Netty](https://netty.io/)框架，旨在减少样板代码并加快RESTful微服务的开发。

此外，它使用插件架构允许我们向微服务添加功能。它支持用于缓存、安全性和持久性等常用功能的插件。

## 2. RestExpress原型

[RestExpress原型](https://github.com/RestExpress/RestExpress-Archetype)是一个支持项目，它提供了一组用于创建RestExpress项目的Maven原型。

在撰写本文时，有三种原型可用：

1.  **minimal**：**包含创建RESTful项目所需的最少代码**。它包含主类、属性文件和示例API
2.  **mongodb**：**创建一个支持MongoDB的RESTful项目**。除了最小原型之外，它还包括一个MongoDB层
3.  **cassandra**：类似于mongodb原型，**在最小原型中添加了一个Cassandra层**

每个原型都带有一组插件来为我们的微服务添加功能：

-   CacheControlPlugin：添加对Cache-Control标头的支持
-   CORSPlugin：添加对CORS的支持
-   MetricsPlugin：添加对指标的支持
-   SwaggerPlugin：添加对Swagger的支持
-   HyperExpressPlugin：添加对HATEOAS的支持

默认情况下，只有MetricsPlugin被启用，并且它使用[Dropwizard Metrics](https://www.baeldung.com/dropwizard-metrics)。**我们可以通过向其实现之一添加依赖项来启用其他插件**。我们可能还需要添加属性来配置和启用某些插件。

在下一节中，我们将探讨如何使用mongodb原型创建项目。我们将继续了解应用程序是如何配置的，然后我们将查看生成代码的某些方面。

### 2.1 使用原型创建项目

让我们使用mongodb原型创建一个项目。

在终端上，让我们导航到要创建项目的目录。我们将使用[mvn archetype:generate](https://www.baeldung.com/maven-archetype#using-archetype)命令：

```shell
$ mvn archetype:generate -DarchetypeGroupId=com.strategicgains.archetype -DarchetypeArtifactId=restexpress-mongodb -DarchetypeVersion=1.18 -DgroupId=cn.tuyucheng.taketoday -DartifactId=rest-express -Dversion=1.0.0
```

这将创建一个包含一些示例代码和配置的项目：

<img src="../assets/img.png">

原型会自动为我们创建一些组件。它使用默认配置创建服务器。**这些配置存在于environment.properties文件中**。

**objectid和uuid包中有两组CRUD API**。这些包中的每一个都包含一个实体、一个控制器、一个服务和一个Repository类。

**Configuration、Server、Main和Routes类有助于在启动期间配置服务器**。

我们将在下一节中探讨这些生成的类。

## 3. 生成代码

让我们探索生成的代码。我们将专注于主类、API方法和DB层。这将使我们了解如何使用RestExpress创建一个简单的CRUD应用程序。

### 3.1 主类

**主类是我们应用程序的入口点。它负责启动服务器和配置应用程序**。

我们看一下Main类的main()方法：

```java
public static void main(String[] args) throws Exception {
    Configuration config = Environment.load(args, Configuration.class);
    Server server = new Server(config);
    server.start().awaitShutdown();
}
```

让我们详细了解一下代码：

-   **Environment.load()方法从environment.properties文件加载配置并创建一个Configuration对象**。
-   Server类负责启动服务器，它需要一个Configuration对象来设置服务器。我们将在下一节中介绍Configuration和Server类。
-   start()方法启动服务器，awaitShutdown()方法等待服务器关闭。

### 3.2 读取属性

**environment.properties文件包含我们应用程序的配置。要读取属性，会自动创建Configuration类**。

让我们看看配置类的不同部分。

Configuration类扩展了Environment类。这允许我们**从环境中读取属性**。为此，它覆盖了Environment类的fillValues()方法：

```java
@Override
protected void fillValues(Properties p) {
    this.port = Integer.parseInt(p.getProperty(PORT_PROPERTY, String.valueOf(RestExpress.DEFAULT_PORT)));
    this.baseUrl = p.getProperty(BASE_URL_PROPERTY, "http://localhost:" + String.valueOf(port));
    this.executorThreadPoolSize = Integer.parseInt(p.getProperty(EXECUTOR_THREAD_POOL_SIZE, DEFAULT_EXECUTOR_THREAD_POOL_SIZE));
    this.metricsSettings = new MetricsConfig(p);
    MongoConfig mongo = new MongoConfig(p);
    initialize(mongo);
}
```

上面的代码从环境中读取端口、基本URL和执行程序线程池大小，并将这些值设置为字段。它还创建一个MetricsConfig对象和一个MongoConfig对象。

我们将在下一节中介绍initialize()方法。

### 3.3 初始化控制器和Repository

**initialize()方法负责初始化控制器和Repository**：

```java
private void initialize(MongoConfig mongo) {
    SampleUuidEntityRepository samplesUuidRepository = new SampleUuidEntityRepository(mongo.getClient(), mongo.getDbName());
    SampleUuidEntityService sampleUuidService = new SampleUuidEntityService(samplesUuidRepository);
    sampleUuidController = new SampleUuidEntityController(sampleUuidService);

    SampleOidEntityRepository samplesOidRepository = new SampleOidEntityRepository(mongo.getClient(), mongo.getDbName());
    SampleOidEntityService sampleOidService = new SampleOidEntityService(samplesOidRepository);
    sampleOidController = new SampleOidEntityController(sampleOidService);
}
```

上面的代码**使用Mongo客户端和数据库名称创建了一个SampleUuidEntityRepository对象。然后它使用Repository创建一个SampleUuidEntityService对象**。最后，**它使用服务创建一个SampleUuidEntityController对象**。

对SampleOidEntityController重复相同的过程。这样，API和数据库层就被初始化了。

Configuration类负责创建我们想要在服务器启动时配置的任何对象。我们可以在initialize()方法中添加任何其他初始化代码。

同样，**我们可以在environment.properties文件中添加更多属性，并在fillValues()方法中读取它们**。

我们还可以使用自己的实现来扩展Configuration类。在这种情况下，我们需要更新Main类以使用我们的实现而不是Configuration类。

## 4. RestExpress API

在上一节中，我们了解了Configuration类如何初始化控制器。让我们看看SampleUuidEntityController类以了解如何创建API方法。

**示例控制器包含create()、read()、readAll()、update()和delete()方法的工作代码。每个方法在内部调用服务类的相应方法，随后调用Repository类**。

接下来，让我们看看几种方法来了解它们的工作原理。

### 4.1 create

让我们看一下create()方法：

```java
public SampleOidEntity create(Request request, Response response) {
    SampleOidEntity entity = request.getBodyAs(SampleOidEntity.class, "Resource details not provided");
    SampleOidEntity saved = service.create(entity);

    // Construct the response for create...
    response.setResponseCreated();

    // Include the Location header...
    String locationPattern = request.getNamedUrl(HttpMethod.GET, Constants.Routes.SINGLE_OID_SAMPLE);
    response.addLocationHeader(LOCATION_BUILDER.build(locationPattern, new DefaultTokenResolver()));

    // Return the newly-created resource...
    return saved;
}
```

上面的代码：

-   读取请求主体并将其转换为SampleOidEntity对象
-   调用服务类的create()方法，传递实体对象
-   将响应代码设置为201–created
-   将location标头添加到响应中
-   返回新创建的实体

如果我们看一下服务类，我们会看到它执行验证并调用Repository类的create()方法。

SampleOidEntityRepository类扩展了MongodbEntityRepository类，该类在内部使用Mongo Java驱动程序来执行数据库操作：

```java
public class SampleOidEntityRepository extends MongodbEntityRepository<SampleOidEntity> {

    @SuppressWarnings("unchecked")
    public SampleOidEntityRepository(MongoClient mongo, String dbName) {
        super(mongo, dbName, SampleOidEntity.class);
    }
}
```

### 4.2 read

现在，让我们看一下read()方法：

```java
public SampleOidEntity read(Request request, Response response) {
    String id = request.getHeader(Constants.Url.SAMPLE_ID, "No resource ID supplied");
    SampleOidEntity entity = service.read(Identifiers.MONGOID.parse(id));

    return entity;
}
```

该方法从请求标头中解析ID，并调用服务类的read()方法。然后服务类调用Repository类的read()方法，Repository类从数据库中检索并返回实体。

## 5. 服务器和路由

最后，让我们看一下Server类。**Server类引导应用程序。它定义了路由和路由的控制器。它还使用指标和其他插件配置服务器**。

### 5.1 创建服务器

让我们看一下Server类的构造函数：

```java
public Server(Configuration config) {
    this.config = config;
    RestExpress.setDefaultSerializationProvider(new SerializationProvider());
    Identifiers.UUID.useShortUUID(true);

    this.server = new RestExpress()
        .setName(SERVICE_NAME)
        .setBaseUrl(config.getBaseUrl())
        .setExecutorThreadCount(config.getExecutorThreadPoolSize())
        .addMessageObserver(new SimpleConsoleLogMessageObserver());

    Routes.define(config,server);
    Relationships.define(server);
    configurePlugins(config,server);
    mapExceptions(server);
}
```

构造函数执行几个步骤：

-   **它创建一个RestExpress对象并设置名称、基本URL、执行程序线程池大小和控制台日志记录的消息观察器**。RestExpress**在内部创建一个Netty服务器**，当我们在主类中调用start()方法时，该服务器将启动。
-   它调用Routes.define()方法来定义路由。我们将在下一节中介绍Routes类。
-   它为我们的实体定义关系，根据提供的属性配置插件，并将某些内部异常映射到应用程序已处理的异常。

### 5.2 路由

Routes.define()方法定义了路由和为每个路由调用的控制器方法。

让我们看看SampleOidEntityController的路由：

```java
public static void define(Configuration config, RestExpress server) {
    // other routes omitted for brevity...
        
    server.uri("/samples/oid/{uuid}.{format}", config.getSampleOidEntityController())
        .method(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE)
        .name(Constants.Routes.SINGLE_OID_SAMPLE);
  
    server.uri("/samples/oid.{format}", config.getSampleOidEntityController())
        .action("readAll", HttpMethod.GET)
        .method(HttpMethod.POST)
        .name(Constants.Routes.SAMPLE_OID_COLLECTION);
}
```

让我们详细看看第一个路由定义。uri()方法将模式/samples/oid/{uuid}.{format}映射到SampleOidEntityController。{uuid}和{format}是URL的路径参数。

**GET、PUT和DELETE方法分别映射到控制器的read()、update()和delete()方法。这是Netty服务器的默认行为**。

为路由分配了一个名称，以便于通过名称检索路由。**如果需要，可以使用server.getRouteUrlsByName()方法完成此检索**。

上面的模式适用于read()、update()和delete()，因为它们都需要一个ID。对于create()和readAll()，我们需要使用不需要ID的不同模式。

让我们看一下第二个路由定义。模式/samples/oid.{format}映射到SampleOidEntityController。

**action()方法用于将方法名称映射到HTTP方法**。在本例中，readAll()方法被映射到GET方法。

POST方法允许在模式上使用，默认情况下映射到控制器的create()方法。和以前一样，为路由分配了一个名称。

需要注意的重要一点是，**如果我们在控制器中定义更多方法或更改标准方法的名称，我们将需要使用action()方法将它们映射到各自的HTTP方法**。

我们需要定义的任何其他URL模式都必须添加到Routes.define()方法中。

## 6. 运行应用程序

让我们运行应用程序并对实体执行一些操作。我们将使用[curl](https://www.baeldung.com/curl-rest)命令来执行这些操作。

让我们通过运行Main类来启动应用程序。**该应用程序在端口8081上启动**。

默认情况下，SampleOidEntity除了ID和时间戳之外没有任何字段。让我们向实体添加一个名为name的字段：

```java
public class SampleOidEntity extends AbstractMongodbEntity implements Linkable {
    private String name;

    // constructors, getters and setters 
}
```

### 6.1 测试API

让我们通过运行[curl](https://www.baeldung.com/curl-rest)命令来创建一个新实体：

```shell
$ curl -X POST -H "Content-Type: application/json" -d "{\"name\":\"test\"}" http://localhost:8081/samples/oid.json
```

这应该返回带有ID的新创建的实体：

```json
{
    "_links": {
        "self": {
            "href": "http://localhost:8081/samples/oid/63a5d983ef1e572664c148fd"
        },
        "up": {
            "href": "http://localhost:8081/samples/oid"
        }
    },
    "name": "test",
    "id": "63a5d983ef1e572664c148fd",
    "createdAt": "2022-12-23T16:38:27.733Z",
    "updatedAt": "2022-12-23T16:38:27.733Z"
}
```

接下来，让我们尝试使用上面返回的id来读取创建的实体：

```shell
$ curl -X GET http://localhost:8081/samples/oid/63a5d983ef1e572664c148fd.json
```

这应该返回与以前相同的实体。

## 7. 总结

在本文中，我们探讨了如何使用RestExpress创建REST API。

我们使用RestExpress mongodb原型创建了一个项目。然后，我们查看了项目结构和生成的类。最后，我们运行应用程序并执行一些操作来测试API。