## 1. 概述

AWS Lambda 使我们能够创建可以轻松部署和扩展的轻量级应用程序。虽然我们可以使用像[Spring Cloud Function](https://www.baeldung.com/spring-cloud-function)这样的框架，但出于性能原因，我们通常会使用尽可能少的框架代码。

有时我们需要从 Lambda 访问关系数据库。这就是[Hibernate](https://www.baeldung.com/tag/hibernate/)和[JPA](https://www.baeldung.com/tag/jpa/)非常有用的地方。但是，我们如何在没有 Spring 的情况下将 Hibernate 添加到我们的 Lambda 中？

在本教程中，我们将探讨在 Lambda 中使用任何 RDBMS 的挑战，以及 Hibernate 如何以及何时有用。我们的示例将使用无服务器应用程序模型为我们的数据构建一个 REST 接口。

[我们将了解如何使用Docker](https://www.baeldung.com/tag/docker/)和 AWS SAM CLI在本地计算机上测试所有内容。

## 2. 在 Lambdas 中使用 RDBMS 和 Hibernate 的挑战

Lambda 代码需要尽可能小以加速冷启动。此外，Lambda 应该能够在几毫秒内完成它的工作。但是，使用关系数据库可能会涉及很多框架代码，并且运行速度会更慢。

在云原生应用中，我们尝试使用云原生技术进行设计。像[Dynamo DB](https://www.baeldung.com/aws-lambda-dynamodb-java)这样的无服务器数据库更适合 Lambdas。但是，对关系数据库的需求可能来自我们项目中的其他优先事项。

### 2.1. 从 Lambda 使用 RDBMS

Lambda 运行一小段时间，然后它们的容器暂停。该容器可能会在未来的调用中重复使用，或者如果不再需要，它可能会被 AWS 运行时处理掉。这意味着必须在单次调用的生命周期内仔细管理容器声明的任何资源。

具体来说，我们不能为我们的数据库依赖传统的连接池，因为任何打开的连接都可能在没有被安全处理的情况下保持打开状态。我们可以在调用过程中使用连接池，但每次都必须创建连接池。此外，我们需要在函数结束时关闭所有连接并释放所有资源。

这意味着将 Lambda 与数据库一起使用可能会导致连接问题。我们的 Lambda 突然升级会消耗太多连接。尽管 Lambda 可能会立即释放连接，但我们仍然依赖数据库能够为下一次 Lambda 调用做好准备。因此，在任何使用关系数据库的 Lambda 上使用最大并发限制通常是个好主意。

在某些项目中，Lambda 并不是连接到 RDBMS 的最佳选择，而传统的 Spring Data 服务，带有连接池，可能运行在 EC2 或 ECS 中，可能是更好的解决方案。

### 2.2. Hibernate 的案例

确定我们是否需要 Hibernate 的一个好方法是询问如果没有它我们必须编写什么样的代码。

如果不使用 Hibernate 会导致我们不得不编写复杂的连接或字段和列之间的大量样板映射，那么从编码的角度来看，Hibernate 是一个很好的解决方案。如果我们的应用程序没有遇到高负载或低延迟需求，那么 Hibernate 的开销可能不是问题。

### 2.3. Hibernate是一项重量级技术

但是，我们还需要考虑在 Lambda 中使用 Hibernate 的成本。

Hibernate jar 文件大小为 7 MB。Hibernate 在启动时需要时间来检查注解并创建其 ORM 功能。这是非常强大的，但对于 Lambda 来说，它可能有点矫枉过正。由于 Lambda 通常是为执行小任务而编写的，因此 Hibernate 的开销可能得不偿失。

[直接使用JDBC](https://www.baeldung.com/java-jdbc)可能更容易。或者，一个轻量级的类似 ORM 的框架，例如[JDBI](https://www.baeldung.com/jdbi) ，可以提供一个很好的查询抽象，而不会产生太多的开销。

## 3. 示例应用

在本教程中，我们将为小批量运输公司构建一个跟踪应用程序。假设他们从客户那里收集大件物品以创建寄售。然后，无论货物运往何处，都会使用时间戳登记，以便客户可以对其进行监控。每批货物都有一个来源和目的地，为此我们将使用[what3words.com](https://what3words.com/goes.pepper.fence)作为我们的地理定位服务。

我们还假设他们使用的移动设备连接不佳并重试。因此，在创建寄售后，有关它的其余信息可以按任何顺序到达。这种复杂性，以及每批货物需要两个列表——物品和登记——是使用 Hibernate 的一个很好的理由。

### 3.1. API设计

我们将使用以下方法创建一个 REST API：

-   POST /consignment – 创建一个新的托运，返回 ID，并提供来源 和 目的地；必须在任何其他操作之前完成
-   POST /consignment/{id}/item – 向托运中添加一个项目；总是添加到列表的末尾
-   POST /consignment/{id}/checkin – 在沿途的任何位置检查托运货物，提供位置和时间戳；会一直按照时间戳的顺序保存在数据库中
-   GET /consignment/{id} – 获取货物的完整历史记录，包括是否已到达目的地

### 3.2. 拉姆达设计

我们将使用单个 Lambda 函数为这个 REST API 提供[无服务器应用程序模型](https://www.baeldung.com/aws-serverless)来定义它。这意味着我们的单个 Lambda 处理程序函数将需要能够满足上述所有请求。

为了快速轻松地进行测试，而无需部署到 AWS 的开销，我们将在我们的开发机器上测试所有内容。

### 4. 创建 Lambda

让我们设置一个新的 Lambda 来满足我们的 API，但还没有实现它的数据访问层。

### 4.1. 先决条件

首先，如果我们还没有[安装 Docker](https://docs.docker.com/get-docker/)，我们需要安装它。我们需要它来托管我们的测试数据库，AWS SAM CLI 使用它来模拟 Lambda 运行时。

我们可以测试我们是否有 Docker：

```bash
$ docker --version
Docker version 19.03.12, build 48a66213fe
```

接下来，我们需要[安装 AWS SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)，然后对其进行测试：

```bash
$ sam --version
SAM CLI, version 1.1.0
```

现在我们准备好创建我们的 Lambda。

### 4.2. 创建 SAM 模板

SAM CLI 为我们提供了一种创建新 Lambda 函数的方法：

```bash
$ sam init
```

这将提示我们进行新项目的设置。让我们选择以下选项：

```bash
1 - AWS Quick Start Templates
13 -Java8
1 - maven
Project name - shipping-tracker
1 - Hello World Example: Maven
```

我们应该注意，这些选项编号可能会随着 SAM 工具的更高版本而有所不同。

现在，应该有一个名为 shipping-tracker的新目录，其中有一个存根应用程序。如果我们查看它的template.yaml文件的内容，我们会发现一个名为HelloWorldFunction的函数，它带有一个简单的 REST API：

```yaml
Events:
  HelloWorld:
    Type: Api 
    Properties:
      Path: /hello
      Method: get
```

默认情况下，这满足/hello上的基本 GET 请求。我们应该通过使用sam来构建和测试它来快速测试一切是否正常：

```bash
$ sam build
... lots of maven output
$ sam start-api
```

然后我们可以使用curl测试hello world API ：

```bash
$ curl localhost:3000/hello
{ "message": "hello world", "location": "192.168.1.1" }
```

之后，让我们使用 CTRL+C 中止程序来停止sam运行其 API 侦听器。

现在我们有了一个空的Java8 Lambda，我们需要自定义它以成为我们的 API。

### 4.3. 创建我们的 API

要创建我们的 API，我们需要将我们自己的路径添加到 template.yaml文件的事件部分 ：

```yaml
CreateConsignment:
  Type: Api 
  Properties:
    Path: /consignment
    Method: post
AddItem:
  Type: Api
  Properties:
    Path: /consignment/{id}/item
    Method: post
CheckIn:
  Type: Api
  Properties:
    Path: /consignment/{id}/checkin
    Method: post
ViewConsignment:
  Type: Api
  Properties:
    Path: /consignment/{id}
    Method: get
```

我们也将调用的函数从 HelloWorldFunction重命名为 ShippingFunction：

```yaml
Resources:
  ShippingFunction:
    Type: AWS::Serverless::Function 
```

接下来，我们将目录重命名为ShippingFunction并将Java包从helloworld更改为 com.baeldung.lambda.shipping。这意味着我们需要更新template.yaml中的CodeUri和 Handler属性以指向新位置：

```yaml
Properties:
  CodeUri: ShippingFunction
  Handler: com.baeldung.lambda.shipping.App::handleRequest
```

最后，为我们自己的实现腾出空间，让我们替换处理程序的主体：

```java
public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    headers.put("X-Custom-Header", "application/json");

    return new APIGatewayProxyResponseEvent()
      .withHeaders(headers)
      .withStatusCode(200)
      .withBody(input.getResource());
}
```

尽管单元测试是个好主意，但对于本示例，我们还将通过删除src/test目录来删除提供的单元测试。

### 4.4. 测试空 API

现在我们已经移动了一些东西并创建了我们的 API 和一个基本的处理程序，让我们仔细检查一切是否仍然有效：

```bash
$ sam build
... maven output
$ sam start-api
```

让我们使用curl来测试 HTTP GET 请求：

```bash
$ curl localhost:3000/consignment/123
/consignment/{id}
```

我们也可以使用curl -d来 POST：

```bash
$ curl -d '{"source":"data.orange.brings", "destination":"heave.wipes.clay"}' 
  -H 'Content-Type: application/json' 
  http://localhost:3000/consignment/
/consignment
```

正如我们所看到的，两个请求都成功结束。我们的存根代码输出资源——请求的路径——我们可以在设置到各种服务方法的路由时使用它。

### 4.5. 在 Lambda 中创建端点

我们使用单个 Lambda 函数来处理我们的四个端点。我们可以为同一代码库中的每个端点创建一个不同的处理程序类，或者为每个端点编写一个单独的应用程序，但是将相关的 API 放在一起允许单个 Lambda 队列使用公共代码为它们提供服务，这可以更好地利用资源。

但是，我们需要构建一个 REST 控制器的等价物来将每个请求分派给合适的Java函数。因此，我们将创建一个存根ShippingService类并从处理程序路由到它：

```java
public class ShippingService {
    public String createConsignment(Consignment consignment) {
        return UUID.randomUUID().toString();
    }

    public void addItem(String consignmentId, Item item) {
    }

    public void checkIn(String consignmentId, Checkin checkin) {
    }

    public Consignment view(String consignmentId) {
        return new Consignment();
    }
}
```

我们还将为Consignment、 Item和 Checkin创建空类。这些很快就会成为我们的榜样。

现在我们有了服务，让我们使用资源路由到适当的服务方法。我们将向我们的处理程序添加一个switch语句以将请求路由到服务：

```java
Object result = "OK";
ShippingService service = new ShippingService();

switch (input.getResource()) {
    case "/consignment":
        result = service.createConsignment(
          fromJson(input.getBody(), Consignment.class));
        break;
    case "/consignment/{id}":
        result = service.view(input.getPathParameters().get("id"));
        break;
    case "/consignment/{id}/item":
        service.addItem(input.getPathParameters().get("id"),
          fromJson(input.getBody(), Item.class));
        break;
    case "/consignment/{id}/checkin":
        service.checkIn(input.getPathParameters().get("id"),
          fromJson(input.getBody(), Checkin.class));
        break;
}

return new APIGatewayProxyResponseEvent()
  .withHeaders(headers)
  .withStatusCode(200)
  .withBody(toJson(result));
```

我们可以使用[Jackson](https://www.baeldung.com/jackson)来实现我们的 fromJson和 toJson功能。

### 4.6. 存根实现

到目前为止，我们已经学习了如何创建一个 AWS Lambda 来支持 API，如何使用sam和 curl对其进行测试，以及如何在我们的处理程序中构建基本的路由功能。我们可以在错误输入上添加更多错误处理。

我们应该注意到template.yaml中的映射已经期望 AWS API 网关过滤不适合我们 API 中正确路径的请求。因此，我们需要更少的错误路径错误处理。

现在，是时候用它的数据库、实体模型和 Hibernate 来实现我们的服务了。

## 5.设置数据库

对于这个例子，我们将使用 PostgreSQL 作为 RDBMS。任何关系数据库都可以工作。

### 5.1. 在 Docker 中启动 PostgreSQL

首先，我们将拉取一个 PostgreSQL docker 镜像：

```bash
$ docker pull postgres:latest
... docker output
Status: Downloaded newer image for postgres:latest
docker.io/library/postgres:latest
```

现在让我们为这个数据库创建一个 docker 网络来运行。这个网络将允许我们的 Lambda 与数据库容器通信：

```bash
$ docker network create shipping
```

接下来，我们需要在该网络中启动数据库容器：

```bash
docker run --name postgres 
  --network shipping 
  -e POSTGRES_PASSWORD=password 
  -d postgres:latest
```

使用 –name，我们为容器指定了名称postgres。使用 –network，我们已将其添加到我们的运输docker 网络中。为了设置服务器的密码，我们使用了环境变量POSTGRES_PASSWORD，通过-e开关设置。

我们还使用 -d在后台运行容器，而不是占用我们的 shell。PostgreSQL 将在几秒钟后启动。

### 5.2. 添加模式

我们的表需要一个新的模式，所以让我们在 PostgreSQL 容器中使用psql客户端来添加运输模式：

```bash
$ docker exec -it postgres psql -U postgres
psql (12.4 (Debian 12.4-1.pgdg100+1))
Type "help" for help.

postgres=#
```

在这个 shell 中，我们创建了模式：

```bash
postgres=# create schema shipping;
CREATE SCHEMA

```

然后我们使用CTRL+D退出 shell。

我们现在运行 PostgreSQL，准备好让 Lambda 使用它。

## 6. 添加我们的实体模型和 DAO

现在我们有一个数据库，让我们创建我们的实体模型和 DAO。虽然我们只使用单个连接，但让我们使用[Hikari 连接池](https://www.baeldung.com/hikaricp)来了解如何为可能需要在一次调用中针对数据库运行多个连接的 Lambda 对其进行配置。

### 6.1. 将 Hibernate 添加到项目中

我们将为 Hibernate 和 Hikari Connection Pool 添加依赖项[到](https://search.maven.org/artifact/org.hibernate/hibernate-core)我们[的](https://search.maven.org/artifact/org.hibernate/hibernate-hikaricp)pom.xml。我们还将添加[PostgreSQL JDBC 驱动程序](https://search.maven.org/artifact/org.postgresql/postgresql)：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.4.21.Final</version>
</dependency>
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-hikaricp</artifactId>
    <version>5.4.21.Final</version>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.2.16</version>
</dependency>
```

### 6.2. 实体模型

让我们充实实体对象。一个Consignment有一个项目和签入列表，以及它的 source，destination，以及它是否已经交付(也就是说，它是否已经签入它的最终目的地)：

```java
@Entity(name = "consignment")
@Table(name = "consignment")
public class Consignment {
    private String id;
    private String source;
    private String destination;
    private boolean isDelivered;
    private List items = new ArrayList<>();
    private List checkins = new ArrayList<>();
    
    // getters and setters
}
```

我们已经将该类注解为实体并使用表名。我们也将提供 getter 和 setter。让我们用列名标记吸气剂：

```java
@Id
@Column(name = "consignment_id")
public String getId() {
    return id;
}

@Column(name = "source")
public String getSource() {
    return source;
}

@Column(name = "destination")
public String getDestination() {
    return destination;
}

@Column(name = "delivered", columnDefinition = "boolean")
public boolean isDelivered() {
    return isDelivered;
}
```

对于我们的列表，我们将使用@ElementCollection注解使它们在单独的表中有序列表，并与 寄售表具有外键关系：

```java
@ElementCollection(fetch = EAGER)
@CollectionTable(name = "consignment_item", joinColumns = @JoinColumn(name = "consignment_id"))
@OrderColumn(name = "item_index")
public List getItems() {
    return items;
}

@ElementCollection(fetch = EAGER)
@CollectionTable(name = "consignment_checkin", joinColumns = @JoinColumn(name = "consignment_id"))
@OrderColumn(name = "checkin_index")
public List getCheckins() {
    return checkins;
}
```

这就是 Hibernate 开始为自己付出代价的地方，它非常容易地执行管理集合的工作。

Item实体更直接： 

```java
@Embeddable
public class Item {
    private String location;
    private String description;
    private String timeStamp;

    @Column(name = "location")
    public String getLocation() {
        return location;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    @Column(name = "timestamp")
    public String getTimeStamp() {
        return timeStamp;
    }

    // ... setters omitted
}
```

它被标记为@Embeddable以使其成为父对象中列表定义的一部分。

同样，我们将定义Checkin：

```java
@Embeddable
public class Checkin {
    private String timeStamp;
    private String location;

    @Column(name = "timestamp")
    public String getTimeStamp() {
        return timeStamp;
    }

    @Column(name = "location")
    public String getLocation() {
        return location;
    }

    // ... setters omitted
}

```

### 6.3. 创建航运 DAO

我们的 ShippingDao类将依赖于传递一个开放的 Hibernate Session。这将需要ShippingService来管理会话：

```java
public void save(Session session, Consignment consignment) {
    Transaction transaction = session.beginTransaction();
    session.save(consignment);
    transaction.commit();
}

public Optional<Consignment> find(Session session, String id) {
    return Optional.ofNullable(session.get(Consignment.class, id));
}
```

稍后我们会将其连接到我们的ShippingService中。

## 7. 休眠生命周期

到目前为止，我们的实体模型和 DAO 与非 Lambda 实现相当。下一个挑战是 在 Lambda 的生命周期内创建一个 Hibernate SessionFactory 。

### 7.1. 数据库在哪里？

如果我们要从我们的 Lambda 访问数据库，那么它需要是可配置的。让我们将 JDBC URL 和数据库凭证放入我们的template.yaml中的环境变量中：

```yaml
Environment: 
  Variables:
    DB_URL: jdbc:postgresql://postgres/postgres
    DB_USER: postgres
    DB_PASSWORD: password

```

这些环境变量将被注入到Java运行时中。postgres用户是我们 Docker PostgreSQL 容器的默认用户。 我们在之前启动容器时将密码指定为密码。

在DB_URL中，我们有服务器名称—— //postgres是我们给容器的名称——数据库名称postgres 是默认数据库。

值得注意的是，尽管我们在此示例中对这些值进行了硬编码，但 SAM 模板允许我们声明输入和参数覆盖。因此，它们可以在以后进行参数化。

### 7.2. 创建会话工厂

我们同时配置了 Hibernate 和[Hikari](https://www.baeldung.com/hikaricp)连接池。为了向 Hibernate 提供设置，我们将它们添加到Map中：

```java
Map<String, String> settings = new HashMap<>();
settings.put(URL, System.getenv("DB_URL"));
settings.put(DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
settings.put(DEFAULT_SCHEMA, "shipping");
settings.put(DRIVER, "org.postgresql.Driver");
settings.put(USER, System.getenv("DB_USER"));
settings.put(PASS, System.getenv("DB_PASSWORD"));
settings.put("hibernate.hikari.connectionTimeout", "20000");
settings.put("hibernate.hikari.minimumIdle", "1");
settings.put("hibernate.hikari.maximumPoolSize", "2");
settings.put("hibernate.hikari.idleTimeout", "30000");
settings.put(HBM2DDL_AUTO, "create-only");
settings.put(HBM2DDL_DATABASE_ACTION, "create");
```

在这里，我们使用System.getenv从环境中提取运行时设置。我们添加了 HBM2DDL_设置以使我们的应用程序[生成数据库表](https://www.baeldung.com/spring-data-jpa-generate-db-schema)。但是，我们应该在生成数据库模式后注解掉或删除这些行，并且应该避免让我们的 Lambda 在生产中执行此操作。不过，这对我们现在的测试很有帮助。

正如我们所见，许多设置都已经在 Hibernate 的 AvailableSettings类中定义了常量，但 Hikari 特定的设置没有。

现在我们有了设置，我们需要构建SessionFactory。我们将单独向其中添加我们的实体类：

```java
StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
  .applySettings(settings)
  .build();

return new MetadataSources(registry)
  .addAnnotatedClass(Consignment.class)
  .addAnnotatedClass(Item.class)
  .addAnnotatedClass(Checkin.class)
  .buildMetadata()
  .buildSessionFactory();
```

### 7.3. 管理资源

在启动时，Hibernate 围绕实体对象执行代码生成。应用程序不打算多次执行此操作，它会使用时间和内存来执行此操作。所以，我们想在我们的 Lambda 冷启动时执行一次。

因此，我们应该创建SessionFactory，因为我们的处理程序对象是由 Lambda 框架创建的。我们可以在处理程序类的初始化列表中这样做：

```java
private SessionFactory sessionFactory = createSessionFactory();
```

但是，由于我们的SessionFactory有一个连接池，因此它有可能在调用之间保持连接打开，从而占用数据库资源。

更糟糕的是，如果 Lambda 正在被 AWS 运行时处置，则没有生命周期事件允许 Lambda 关闭资源。因此，以这种方式保持的连接有可能永远不会被正确释放。

我们可以通过深入研究连接池的 SessionFactory并显式关闭所有连接来解决这个问题：

```java
private void flushConnectionPool() {
    ConnectionProvider connectionProvider = sessionFactory.getSessionFactoryOptions()
      .getServiceRegistry()
      .getService(ConnectionProvider.class);
    HikariDataSource hikariDataSource = connectionProvider.unwrap(HikariDataSource.class);
    hikariDataSource.getHikariPoolMXBean().softEvictConnections();
}
```

这在这种情况下有效，因为我们指定了 Hikari 连接池，它提供 softEvictConnections以允许我们释放它的连接。

我们应该注意到 SessionFactory的 close方法也会关闭连接，但它也会使SessionFactory不可用。

### 7.4. 添加到处理程序

现在，我们需要确保处理程序使用会话工厂并释放其连接。考虑到这一点，让我们将大部分控制器功能提取到一个名为routeRequest的方法中，并修改我们的处理程序以在finally块中释放资源：

```java
try {
    ShippingService service = new ShippingService(sessionFactory, new ShippingDao());
    return routeRequest(input, service);
} finally {
    flushConnectionPool();
}

```

我们还更改了 Shipping Service以将 SessionFactory和 ShippingDao作为属性，通过构造函数注入，但尚未使用它们。

### 7.5. 测试休眠

此时，虽然ShippingService什么都不做，但调用 Lambda 应该会导致 Hibernate 启动并生成 DDL。

在我们注解掉它的设置之前，让我们仔细检查它生成的 DDL：

```bash
$ sam build
$ sam local start-api --docker-network shipping
```

我们像以前一样构建应用程序，但现在我们将–docker-network参数添加到 sam local。这将在与我们的数据库相同的网络中运行测试 Lambda，以便 Lambda 可以使用其容器名称访问数据库容器。

当我们第一次使用 curl访问端点时，应该创建我们的表：

```java
$ curl localhost:3000/consignment/123
{"id":null,"source":null,"destination":null,"items":[],"checkins":[],"delivered":false}
```

存根代码仍然返回空白Consignment。但是，现在让我们检查数据库以查看表是否已创建：

```bash
$ docker exec -it postgres pg_dump -s -U postgres
... DDL output
CREATE TABLE shipping.consignment_item (
    consignment_id character varying(255) NOT NULL,
...
```

一旦我们对 Hibernate 设置感到满意，我们就可以注解掉 HBM2DDL_设置。

## 8.完成业务逻辑

剩下的就是让ShippingService使用ShippingDao来实现业务逻辑。每个方法都会在try-with-resources块中创建一个会话工厂， 以确保它被关闭。

### 8.1. 创建寄售

新的货物尚未交付，应该会收到一个新的 ID。然后我们应该将它保存在数据库中：

```java
public String createConsignment(Consignment consignment) {
    try (Session session = sessionFactory.openSession()) {
        consignment.setDelivered(false);
        consignment.setId(UUID.randomUUID().toString());
        shippingDao.save(session, consignment);
        return consignment.getId();
    }
}
```

### 8.2. 查看托运

要获取货物，我们需要通过 ID 从数据库中读取它。虽然 REST API 应该在未知请求上返回Not Found ，但对于这个例子，如果没有找到，我们将只返回一个空的货物：

```java
public Consignment view(String consignmentId) {
    try (Session session = sessionFactory.openSession()) {
        return shippingDao.find(session, consignmentId)
          .orElseGet(Consignment::new);
    }
}
```

### 8.3. 添加项目

物品将按照收到的顺序进入我们的物品清单：

```java
public void addItem(String consignmentId, Item item) {
    try (Session session = sessionFactory.openSession()) {
        shippingDao.find(session, consignmentId)
          .ifPresent(consignment -> addItem(session, consignment, item));
    }
}

private void addItem(Session session, Consignment consignment, Item item) {
    consignment.getItems()
      .add(item);
    shippingDao.save(session, consignment);
}
```

理想情况下，如果货物不存在，我们会有更好的错误处理，但对于这个例子，不存在的货物将被忽略。

### 8.4. 报到

签到需要按发生的时间排序，而不是按收到请求的时间排序。此外，当物品到达最终目的地时，应将其标记为已送达：

```java
public void checkIn(String consignmentId, Checkin checkin) {
    try (Session session = sessionFactory.openSession()) {
        shippingDao.find(session, consignmentId)
          .ifPresent(consignment -> checkIn(session, consignment, checkin));
    }
}

private void checkIn(Session session, Consignment consignment, Checkin checkin) {
    consignment.getCheckins().add(checkin);
    consignment.getCheckins().sort(Comparator.comparing(Checkin::getTimeStamp));
    if (checkin.getLocation().equals(consignment.getDestination())) {
        consignment.setDelivered(true);
    }
    shippingDao.save(session, consignment);
}
```

## 9. 测试应用

让我们模拟一个从白宫到帝国大厦的包裹。

代理创建旅程：

```bash
$ curl -d '{"source":"data.orange.brings", "destination":"heave.wipes.clay"}' 
  -H 'Content-Type: application/json' 
  http://localhost:3000/consignment/

"3dd0f0e4-fc4a-46b4-8dae-a57d47df5207"
```

我们现在有货物的 ID 3dd0f0e4-fc4a-46b4-8dae-a57d47df5207。然后，有人为托运物收集了两件物品——一幅画和一架钢琴：

```bash
$ curl -d '{"location":"data.orange.brings", "timeStamp":"20200101T120000", "description":"picture"}' 
  -H 'Content-Type: application/json' 
  http://localhost:3000/consignment/3dd0f0e4-fc4a-46b4-8dae-a57d47df5207/item
"OK"

$ curl -d '{"location":"data.orange.brings", "timeStamp":"20200101T120001", "description":"piano"}' 
  -H 'Content-Type: application/json' 
  http://localhost:3000/consignment/3dd0f0e4-fc4a-46b4-8dae-a57d47df5207/item
"OK"

```

稍后，有一个签到：

```bash
$ curl -d '{"location":"united.alarm.raves", "timeStamp":"20200101T173301"}' 
-H 'Content-Type: application/json' 
http://localhost:3000/consignment/3dd0f0e4-fc4a-46b4-8dae-a57d47df5207/checkin
"OK"
```

稍后再说：

```bash
$ curl -d '{"location":"wink.sour.chasing", "timeStamp":"20200101T191202"}' 
-H 'Content-Type: application/json' 
http://localhost:3000/consignment/3dd0f0e4-fc4a-46b4-8dae-a57d47df5207/checkin
"OK"
```

此时，客户请求货物的状态：

```bash
$ curl http://localhost:3000/consignment/3dd0f0e4-fc4a-46b4-8dae-a57d47df5207
{
  "id":"3dd0f0e4-fc4a-46b4-8dae-a57d47df5207",
  "source":"data.orange.brings",
  "destination":"heave.wipes.clay",
  "items":[
    {"location":"data.orange.brings","description":"picture","timeStamp":"20200101T120000"},
    {"location":"data.orange.brings","description":"piano","timeStamp":"20200101T120001"}
  ],
  "checkins":[
    {"timeStamp":"20200101T173301","location":"united.alarm.raves"},
    {"timeStamp":"20200101T191202","location":"wink.sour.chasing"}
  ],
  "delivered":false
}%

```

他们看到了进展，但尚未交付。

应该在 20:12 发送一条消息说它已到达 deflection.famed.apple，但它被延迟了，来自 21:46 的消息首先到达目的地：

```bash
$ curl -d '{"location":"heave.wipes.clay", "timeStamp":"20200101T214622"}' 
-H 'Content-Type: application/json' 
http://localhost:3000/consignment/3dd0f0e4-fc4a-46b4-8dae-a57d47df5207/checkin
"OK"
```

此时，客户请求货物的状态：

```bash
$ curl http://localhost:3000/consignment/3dd0f0e4-fc4a-46b4-8dae-a57d47df5207
{
  "id":"3dd0f0e4-fc4a-46b4-8dae-a57d47df5207",
...
    {"timeStamp":"20200101T191202","location":"wink.sour.chasing"},
    {"timeStamp":"20200101T214622","location":"heave.wipes.clay"}
  ],
  "delivered":true
}

```

现在交付了。因此，当延迟消息通过时：

```bash
$ curl -d '{"location":"deflection.famed.apple", "timeStamp":"20200101T201254"}' 
-H 'Content-Type: application/json' 
http://localhost:3000/consignment/3dd0f0e4-fc4a-46b4-8dae-a57d47df5207/checkin
"OK"

$ curl http://localhost:3000/consignment/3dd0f0e4-fc4a-46b4-8dae-a57d47df5207
{
"id":"3dd0f0e4-fc4a-46b4-8dae-a57d47df5207",
...
{"timeStamp":"20200101T191202","location":"wink.sour.chasing"},
{"timeStamp":"20200101T201254","location":"deflection.famed.apple"},
{"timeStamp":"20200101T214622","location":"heave.wipes.clay"}
],
"delivered":true
}

```

签到被放置在时间轴中的正确位置。

## 10.总结

在本文中，我们讨论了在 AWS Lambda 等轻量级容器中使用 Hibernate 等重量级框架的挑战。

我们构建了一个 Lambda 和 REST API，并学习了如何使用 Docker 和 AWS SAM CLI 在我们的本地机器上测试它。然后，我们为 Hibernate 构建了一个实体模型以与我们的数据库一起使用。我们还使用 Hibernate 来初始化我们的表。

最后，我们将 Hibernate SessionFactory集成到我们的应用程序中，确保在 Lambda 退出之前将其关闭。