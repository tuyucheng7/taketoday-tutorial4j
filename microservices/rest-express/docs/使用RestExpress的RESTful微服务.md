## 一、概述

在本教程中，我们将学习如何使用[RestExpress](https://github.com/RestExpress/RestExpress)构建 RESTful 微服务。

RestExpress 是一个开源的 Java 框架，使我们能够快速轻松地构建 RESTful 微服务。它基于[Netty](https://netty.io/)框架，旨在减少样板代码并加快 RESTful 微服务的开发。

此外，它使用插件架构允许我们向微服务添加功能。它支持用于缓存、安全性和持久性等常用功能的插件。

## 2. RestExpress原型

[RestExpress Archetypes](https://github.com/RestExpress/RestExpress-Archetype)是一个支持项目，它提供了一组用于创建 RestExpress 项目的 Maven 原型。

在撰写本文时，可以使用三种原型：

1.  ***minimal*** –**包含创建 RESTful 项目所需的最少代码**。它包含主类、属性文件和示例 API。
2.  ***mongodb*** –**创建一个支持 MongoDB 的 RESTful 项目**。除了*最小*原型之外，它还包括一个 MongoDB 层。
3.  ***cassandra——***类似于*mongodb*原型，***在最小\*****原型中****添加了一个 Cassandra 层**

每个原型都带有一组插件来为我们的微服务添加功能：

-   CacheControlPlugin – 添加对 Cache-Control 标头的支持
-   CORSPlugin – 添加对 CORS 的支持
-   MetricsPlugin – 添加对指标的支持
-   SwaggerPlugin – 添加对 Swagger 的支持
-   HyperExpressPlugin – 添加对 HATEOAS 的支持

默认情况下，只有 MetricsPlugin 被启用，并且它使用[Dropwizard Metrics](https://www.baeldung.com/dropwizard-metrics)。我们可以**通过向其实现之一添加依赖项来启用其他插件**。我们可能还需要添加属性来配置和启用某些插件。

在下一节中，我们将探讨如何使用*mongodb*原型创建项目。我们将继续了解应用程序是如何配置的，然后我们将查看生成代码的某些方面。

### 2.1. 使用原型创建项目

*让我们使用mongodb*原型创建一个项目。

在终端上，让我们导航到要创建项目的目录。我们将使用*[mvn archetype:generate](https://www.baeldung.com/maven-archetype#using-archetype)*命令：

```bash
$ mvn archetype:generate -DarchetypeGroupId=com.strategicgains.archetype -DarchetypeArtifactId=restexpress-mongodb -DarchetypeVersion=1.18 -DgroupId=com.baeldung -DartifactId=rest-express -Dversion=1.0-SNAPSHOT复制
```

这将创建一个包含一些示例代码和配置的项目：

[![Rest Express 项目结构](https://www.baeldung.com/wp-content/uploads/2023/01/rest-express-project-structure-1.png)](https://www.baeldung.com/wp-content/uploads/2023/01/rest-express-project-structure-1.png)

 

原型会自动为我们创建一些组件。它使用默认配置创建服务器。这些**配置存在于\*environment.properties\*文件**中。

***objectid\*****和*****uuid\*****包**中有**两组 CRUD API**。这些包中的每一个都包含一个实体、一个控制器、一个服务和一个存储库类。

Configuration 、***Server、Main\*和\*Routes\*类有助于在启动期间配置服务器。**

我们将在下一节中探讨这些生成的类。

## 3. 生成代码

让我们探索生成的代码。我们将专注于主类、API 方法和 DB 层。这将使我们了解如何使用 RestExpress 创建一个简单的 CRUD 应用程序。

### 3.1. 主课

主类是我们应用程序的入口点。它**负责启动服务器和配置应用程序**。

我们看一下*Main* 类的*main()*方法 ：

```java
public static void main(String[] args) throws Exception {
    Configuration config = Environment.load(args, Configuration.class);
    Server server = new Server(config);
    server.start().awaitShutdown();
}
复制
```

让我们详细了解一下代码：

-   ***Environment.load()\* 方法** 从 ***environment.properties\*** **文件****加载配置** 并创建一个*Configuration*对象。
-   *Server*类 负责启动服务器。它需要一个*Configuration*对象来设置服务器。我们将在下一节中查看*Configuration*和*Server类。*
-   *start()*方法启动服务器， awaitShutdown *()*方法等待服务器关闭。

### 3.2. 读取属性

***environment.properties\***文件包含我们应用程序的 **配置。****要读取属性， 会自动创建*****Configuration\*** **类**。 

让我们看看*配置*类的不同部分。

*Configuration*类 扩展了*Environment* 类。这允许我们**从环境中读取属性**。为此，它覆盖了*Environment* 类的*fillValues()* 方法 ：

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
复制
```

上面的代码从环境中读取端口、基本 URL 和执行程序线程池大小，并将这些值设置为字段。它还创建一个*MetricsConfig*对象和一个*MongoConfig*对象。

我们将在 下一节中查看*initialize()方法。*

### 3.3. 初始化控制器和存储库

***initialize()\*方法 负责初始化控制器和存储库**：

```java
private void initialize(MongoConfig mongo) {
    SampleUuidEntityRepository samplesUuidRepository = new SampleUuidEntityRepository(mongo.getClient(), mongo.getDbName());
    SampleUuidEntityService sampleUuidService = new SampleUuidEntityService(samplesUuidRepository);
    sampleUuidController = new SampleUuidEntityController(sampleUuidService);

    SampleOidEntityRepository samplesOidRepository = new SampleOidEntityRepository(mongo.getClient(), mongo.getDbName());
    SampleOidEntityService sampleOidService = new SampleOidEntityService(samplesOidRepository);
    sampleOidController = new SampleOidEntityController(sampleOidService);
}
复制
```

上面的代码 **使用 Mongo 客户端和数据库名称****创建了一个 \*SampleUuidEntityRepository\***对象。然后它使用存储库**创建一个 \*SampleUuidEntityService\* 对象**。最后，它使用服务**创建一个 \*SampleUuidEntityController\* 对象**。

对 *SampleOidEntityController*重复相同的过程。这样，API 和数据库层就被初始化了。

*Configuration* 类负责创建我们想要在服务器启动时配置的任何对象。 我们可以在 *initialize()*方法中添加任何其他初始化代码。

同样，**我们可以在\*environment.properties\*文件中添加更多属性，并在\*fillValues()\* 方法**中读取它们。

我们还可以 使用自己的实现来扩展*Configuration类。*在这种情况下，我们需要更新 *Main*类以使用我们的实现而不是 *Configuration* 类。

## 4. RestExpress API

在上一节中，我们了解了 *Configuration*类如何初始化控制器。让我们看看 *SampleUuidEntityController* 类以了解如何创建 API 方法。

**示例控制器包含\*create()\*、 \*read()\*、 \*readAll()\*、 \*update()\*和\*delete()\*方法**的工作代码。 **每个方法在内部调用服务类的相应方法，随后调用存储库类。**

接下来，让我们看看几种方法来了解它们的工作原理。

### 4.1. 创建

让我们看一下 *create()* 方法：

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
}复制
```

上面的代码：

-   读取请求主体并将其转换为*SampleOidEntity*对象
-   调用服务类的*create()方法，传递实体对象*
-   将响应代码设置为*201 – 已创建*
-   将位置标头添加到响应中
-   返回新创建的实体

如果我们查看服务类，我们会看到它执行验证并调用存储库类的 *create()* 方法。

SampleOidEntityRepository 类扩展了 *MongodbEntityRepository类，**该类*在内部使用 Mongo Java 驱动程序来执行数据库操作：![img]()

```java
public class SampleOidEntityRepository extends MongodbEntityRepository<SampleOidEntity> {
    @SuppressWarnings("unchecked")
    public SampleOidEntityRepository(MongoClient mongo, String dbName) {
        super(mongo, dbName, SampleOidEntity.class);
    }
}
复制
```

### 4.2. 读

现在，让我们看一下*read()* 方法：

```java
public SampleOidEntity read(Request request, Response response) {
    String id = request.getHeader(Constants.Url.SAMPLE_ID, "No resource ID supplied");
    SampleOidEntity entity = service.read(Identifiers.MONGOID.parse(id));

    return entity;
}
复制
```

该方法从请求标头中解析 ID，并调用服务类的 *read()*方法。服务类然后调用存储库类的*read()* 方法。存储库类从数据库中检索并返回实体。

## 5. 服务器和路由

最后，让我们看一下*Server* 类。 ***Server\*****类 引导应用程序。它定义了路线和路线的控制器。它还使用指标和其他插件配置服务器**。

### 5.1. 创建服务器

我们看一下 *Server* 类的构造函数：

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
复制
```

构造函数执行几个步骤：

-   它**创建一个 \*RestExpress\*对象并设置名称、基本 URL、执行程序线程池大小和控制台日志记录的消息观察器**。RestExpress**内部创建了一个 Netty 服务器**。*当我们在主*类中调用*start()*方法时，该服务器将启动 。
-   它调用 *Routes.define()* 方法来定义路由。我们将在 下一节中查看*Routes类。*
-   它为我们的实体定义关系，根据提供的属性配置插件，并将某些内部异常映射到应用程序已处理的异常。

### 5.2. 航线

Routes.define *()* 方法定义了路由和为每个路由调用的控制器方法。

让我们看看 *SampleOidEntityController*的路由：![img]()

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
}复制
```

让我们详细看看第一个路由定义。*uri()*方法将模式 /samples/oid/{uuid}.{format *}*映射 到*SampleOidEntityController*。*{uuid}*和*{format}*是 URL 的路径参数。

***GET\*、 \*PUT\*和 \*DELETE\*方法分别映射到控制器的read \*()\*、 \*update()\*和 \*delete()\*方法。这是 Netty 服务器的默认行为。**

为路由分配了一个名称，以便于通过名称检索路由。**如果需要，可以使用\*server.getRouteUrlsByName()\*方法完成此检索。**

上面的模式适用于*read()*、*update()*和*delete()*，因为它们都需要一个 ID。对于*create()*和*readAll()*，我们需要使用不需要 ID 的不同模式。

让我们看一下第二个路由定义。模式*/samples/oid.{format}*映射到*SampleOidEntityController*。

***action()\***方法用于将**方法名称映射到 HTTP 方法**。在这种情况下，*readAll()*方法被映射到*GET*方法。 

*POST* 方法允许在模式上使用，默认情况下映射到控制器的 create *()*方法。和以前一样，为路线分配了一个名称。

需要注意的重要一点是，如果我们在控制器中定义更多方法或更改标准方法的名称，我们将需要使用***action()\*****方法****将它们映射到各自的 HTTP**方法。

我们需要定义的任何其他 URL 模式都必须添加到*Routes.define()* 方法中。

## 6. 运行应用程序

让我们运行应用程序并对实体执行一些操作。我们将使用 [*curl*](https://www.baeldung.com/curl-rest) 命令来执行这些操作。

让我们通过运行 *Main* 类来启动应用程序。 **该应用程序在端口\*8081 上启动。\***

默认情况下， *SampleOidEntity*除了 ID 和时间戳之外没有任何字段。 让我们向实体添加一个名为*name的字段：*

```java
public class SampleOidEntity extends AbstractMongodbEntity implements Linkable {
    private String name;

    // constructors, getters and setters 
}
复制
```

### 6.1. 测试 API

[*让我们通过运行curl*](https://www.baeldung.com/curl-rest)命令来创建一个新实体：![img]()

```bash
$ curl -X POST -H "Content-Type: application/json" -d "{\"name\":\"test\"}" http://localhost:8081/samples/oid.json复制
```

这应该返回带有 ID 的新创建的实体：

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
复制
```

接下来，让我们尝试使用上面返回的*id*来读取创建的实体：

```bash
$ curl -X GET http://localhost:8081/samples/oid/63a5d983ef1e572664c148fd.json复制
```

这应该返回与以前相同的实体。

## 七、结论

在本文中，我们探讨了如何使用 RestExpress 创建 REST API。

我们使用 RestExpress *mongodb*原型创建了一个项目。然后，我们查看了项目结构和生成的类。最后，我们运行应用程序并执行一些操作来测试 API。