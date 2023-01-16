## 1. 简介

在本文中，我们将构建一个由[Fauna](https://www.baeldung.com/fauna-app)和 Spring 提供支持的 IoT 应用程序。

## 2.物联网应用——Fast Edges和分布式数据库

IoT 应用程序靠近用户工作。它们负责以低延迟消耗和处理大量实时数据。他们需要快速的边缘计算服务器和分布式数据库来实现低延迟和最佳性能。

此外，IoT 应用程序处理非结构化数据，主要是因为它们使用这些数据的来源各不相同。IoT 应用程序需要能够有效处理这些非结构化数据的数据库。

在这篇文章中，我们将构建一个物联网应用程序的后端，负责处理和存储个人的生命体征，例如温度、心率、血氧水平等。这个物联网应用程序可以从摄像头、红外线设备中获取生命体征。红色扫描仪，或通过智能手表等可穿戴设备的传感器。

## 3. 将动物群用于物联网应用

在上一节中，我们了解了典型 IoT 应用程序的特征，我们还了解了人们对数据库用于 IoT 空间的期望。

Fauna 由于其以下特性，最适合用作 IoT 应用程序中的数据库：

分布式： 当我们在 Fauna 中创建应用程序时，它会自动分布在多个云区域。[它对使用 Cloudflare Workers 或Fastly Compute @Edge](https://www.baeldung.com/fauna-build)等技术的边缘计算应用程序非常有益。对于我们的用例，此功能可以帮助我们以最小的延迟快速访问、处理和存储分布在全球各地的个人的生命体征。

文档关系： Fauna 将 JSON 文档的灵活性和熟悉性与传统关系数据库的关系和查询能力相结合。此功能有助于大规模处理非结构化物联网数据。

无服务器：有了 Fauna——我们可以完全专注于构建我们的应用程序，而不是与数据库相关的管理和基础设施开销。

## 4. 应用的高级请求流程

在高层次上，这就是我们应用程序的请求流程：

[![请求流程](https://www.baeldung.com/wp-content/uploads/2022/08/request-flow.png)](https://www.baeldung.com/wp-content/uploads/2022/08/request-flow.png)

Here Aggregator是一个简单的应用程序，可以聚合从各种传感器接收到的数据。在本文中，我们不会专注于构建Aggregator，但我们可以通过部署在云端的一个简单的 Lambda 函数来解决它的目的。

接下来，我们将使用Spring Boot构建边缘服务，并设置具有不同区域组的 Fauna 数据库，以处理来自不同区域的请求。

## 5. 创建边缘服务——使用 Spring Boot

让我们使用Spring Boot创建边缘服务，它将使用来自健康传感器的数据并将其推送到适当的动物群区域。

在我们之前关于 Fauna 的教程、[使用 Spring 介绍 FaunaDB](https://www.baeldung.com/faunadb-spring)和[使用 Fauna 和 Spring 为你的第一个 Web 代理客户端构建 Web 应用程序 中](https://www.baeldung.com/faunadb-spring-web-app)，我们探索了如何创建与 Fauna 连接的Spring Boot应用程序。请随意深入阅读这些文章以获取更多详细信息。

### 5.1. 领域

让我们首先了解我们的边缘服务的域。

如前所述，我们的边缘服务将使用和处理生命体征，让我们创建一个包含个人基本生命体征的记录：

```java
public record HealthData(
  
    String userId,
  
    float temperature, 
    float pulseRate,
    int bpSystolic,
    int bpDiastolic,
  
    double latitude, 
    double longitude, 
    ZonedDateTime timestamp) {
}
```

### 5.2. 健康服务

Out health service 将负责处理健康数据，从请求中识别区域，并将其路由到适当的 Fauna 区域：

```java
public interface HealthService {
    void process(HealthData healthData);
}
```

让我们开始构建实现：

```java
public class DefaultHealthService implements HealthService {

    @Override
    public void process(HealthData healthData) {
        // ...
    }
}
```

接下来，让我们添加用于识别请求区域的代码，即从哪里触发请求。

Java 中有几个库可以识别地理位置。但是，对于本文，我们将添加一个简单的实现，为所有请求返回区域“US”。

让我们添加接口：

```java
public interface GeoLocationService {
    String getRegion(double latitude, double longitude);
}
```

以及为所有请求返回“美国”区域的实现：

```java
public class DefaultGeoLocationService implements GeoLocationService {

    @Override
    public String getRegion(double latitude, double longitude) {
        return "US";
    }
}
```

接下来，让我们在我们的HealthService中使用这个GeoLocationService ；让我们注入它：

```java
@Autowired
private GeoLocationService geoLocationService;
```

并在处理方法中使用它来提取区域：

```java
public void process(HealthData healthData) {

    String region = geoLocationService.getRegion(
        healthData.latitude(), 
        healthData.longitude());
    
    // ...
}
```

有了区域后，我们必须查询适当的动物群区域组来存储数据，但在我们开始之前，让我们用区域组设置动物群数据库。之后我们将恢复我们的整合。

## 6. 带区域组的动物群——设置

让我们从在 Fauna 中创建新数据库开始。如果我们还没有一个帐户，我们需要[创建一个。](https://www.baeldung.com/fauna-register2)

### 6.1. 创建数据库

登录后，让我们创建一个新数据库：

[![动物群创建数据库](https://www.baeldung.com/wp-content/uploads/2022/08/fauna-create-db.png)](https://www.baeldung.com/wp-content/uploads/2022/08/fauna-create-db.png)

在这里，我们在欧洲 (EU) 地区创建此数据库；让我们在美国地区创建相同的数据库来处理来自美国的请求：

[![动物群数据库](https://www.baeldung.com/wp-content/uploads/2022/08/fauna-db-us.png)](https://www.baeldung.com/wp-content/uploads/2022/08/fauna-db-us.png)

接下来，我们需要一个安全密钥来从外部访问我们的数据库，在我们的例子中，是从我们创建的边缘服务。我们将为两个数据库分别创建密钥：

[![动物群钥匙](https://www.baeldung.com/wp-content/uploads/2022/08/fauna-key.png)](https://www.baeldung.com/wp-content/uploads/2022/08/fauna-key.png)

同样，我们将创建用于访问healthapp-us数据库的密钥。有关在 Fauna 中创建数据库和安全密钥的更多详细说明，请参阅我们[的 FaunaDB 与 Spring 简介](https://www.baeldung.com/faunadb-spring)文章。

创建密钥后，让我们将区域特定的 Fauna 连接 URL 和安全密钥存储在我们的Spring Boot服务的application.properties中：

```properties
fauna-connections.EU=https://db.eu.fauna.com/
fauna-secrets.EU=eu-secret
fauna-connections.US=https://db.us.fauna.com/
fauna-secrets.US=us-secret
```

当我们使用它们来配置 Fauna 客户端以连接 Fauna 数据库时，我们将需要这些属性。

### 6.2. 创建HealthData集合

接下来，让我们在 Fauna 中创建HealthData集合来存储个人的生命体征。

让我们通过导航到数据库仪表板中的“集合”选项卡并单击“新建集合”按钮来添加集合：

[![动物群](https://www.baeldung.com/wp-content/uploads/2022/08/fauna-collection.png)](https://www.baeldung.com/wp-content/uploads/2022/08/fauna-collection.png)

接下来，让我们单击下一个屏幕上的“新建文档”按钮以插入一个示例文档并在 JavaScript 控制台中添加以下 JSON：

```javascript
{
  "userId": "baeldung-user",
  "temperature": "37.2",
  "pulseRate": "90",
  "bpSystolic": "120",
  "bpDiastolic": "80",
  "latitude": "40.758896",
  "longitude": "-73.985130",
  "timestamp": Now()
}
```

Now() 函数将在时间戳字段中插入当前时间戳。

当我们点击保存时，上面的数据被插入，我们可以在我们的healthdata集合中的 Collection 选项卡中查看所有插入的文档：

[![动物群集合视图](https://www.baeldung.com/wp-content/uploads/2022/08/fauna-collection-view.png)](https://www.baeldung.com/wp-content/uploads/2022/08/fauna-collection-view.png)

现在我们已经创建了数据库、密钥和集合，让我们继续将我们的边缘服务与我们的 Fauna 数据库集成并对其执行操作。

## 7. 边缘服务与动物群的集成

要将我们的Spring Boot应用程序与 Fauna 集成，我们需要将Java的 Fauna 驱动程序添加到我们的项目中。让我们添加依赖项：

```xml
<dependency>
    <groupId>com.faunadb</groupId>
    <artifactId>faunadb-java</artifactId>
    <version>4.2.0</version>
    <scope>compile</scope>
</dependency>
```

我们总能在[这里找到](https://mvnrepository.com/artifact/com.faunadb/faunadb-java)faunadb-java 的最新版本。

### 7.1. 创建特定区域的动物群客户端

该驱动程序为我们提供了FaunaClient，我们可以使用给定的连接端点和密码轻松配置它：

```java
FaunaClient client = FaunaClient.builder()
    .withEndpoint("connection-url")
    .withSecret("secret")
    .build();
```

在我们的应用程序中，根据请求的来源，我们需要连接 Fauna 的欧盟和美国地区。我们可以通过分别为两个区域预配置不同的FaunaClient 实例或在运行时动态配置客户端来解决这个问题。让我们研究第二种方法。

让我们创建一个新类FaunaClients，它接受一个区域并返回正确配置的FaunaClient：

```java
public class FaunaClients {

    public FaunaClient getFaunaClient(String region) {
        // ...
    }
}
```

我们已经在application.properties 中存储了 Fauna 的区域特定端点和秘密；我们 可以将端点和秘密作为映射注入：

```java
@ConfigurationProperties
public class FaunaClients {

    private final Map<String, String> faunaConnections = new HashMap<>();
    private final Map<String, String> faunaSecrets = new HashMap<>();

    public Map<String, String> getFaunaConnections() {
        return faunaConnections;
    }

    public Map<String, String> getFaunaSecrets() {
        return faunaSecrets;
    }
}
```

在这里，我们使用了@ConfigurationProperties，它将配置属性注入到我们的类中。要启用此注解，我们还需要添加：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

最后，我们需要从各自的地图中提取正确的连接端点和秘密，并相应地使用它们来创建FaunaClient：

```java
public FaunaClient getFaunaClient(String region) {

    String faunaUrl = faunaConnections.get(region);
    String faunaSecret = faunaSecrets.get(region);

    log.info("Creating Fauna Client for Region: {} with URL: {}", region, faunaUrl);
    
    return FaunaClient.builder()
        .withEndpoint(faunaUrl)
        .withSecret(faunaSecret)
        .build();
}
```

我们还添加了一个日志来检查在创建客户端时是否选择了正确的 Fauna URL。

### 7.2. 在卫生服务中使用特定地区的动物群客户端

一旦我们的客户准备就绪，让我们在我们的健康服务中使用它们来将健康数据发送给动物群。

让我们注入FaunaClients：

```java
public class DefaultHealthService implements HealthService {
    
    @Autowired
    private FaunaClients faunaClients;
    
    // ...
}
```

接下来，让我们通过传入先前从GeoLocationService中提取的区域来获取特定于区域的FaunaClient：

```java
public void process(HealthData healthData) {

    String region = geoLocationService.getRegion(
        healthData.latitude(), 
        healthData.longitude());
    
    FaunaClient faunaClient = faunaClients.getFaunaClient(region);
}
```

一旦我们有了区域特定的FaunaClient，我们就可以用它来将健康数据插入到特定的数据库中。

我们将基于我们现有的[Faunadb Spring Web App](https://www.baeldung.com/faunadb-spring-web-app)文章，我们在 FQL(Fauna 查询语言)中编写了几个 CRUD 查询以与 Fauna 集成。

让我们添加查询以在 Fauna 中创建健康数据；我们将从关键字Create开始，并提及我们要为其插入数据的集合名称：

```java
Value queryResponse = faunaClient.query(
    Create(Collection("healthdata"), //)
    ).get();
```

接下来，我们将创建要插入的实际数据对象。我们将对象的属性及其值定义为Map的条目，并使用 FQL 的Value关键字对其进行包装：

```java
Create(Collection("healthdata"), 
    Obj("data", 
        Obj(Map.of(
            "userId", Value(healthData.userId())))))
)
```

在这里，我们从健康数据记录中读取userId，并将其映射到我们要插入的文档中的 userId字段。

同样，我们可以为所有剩余的属性做：

```java
Create(Collection("healthdata"), 
    Obj("data", 
        Obj(Map.of(
            "userId", Value(healthData.userId()), 
            "temperature", Value(healthData.temperature()),
            "pulseRate", Value(healthData.pulseRate()),
            "bpSystolic", Value(healthData.bpSystolic()),
            "bpDiastolic", Value(healthData.bpDiastolic()),
            "latitude", Value(healthData.latitude()),
            "longitude", Value(healthData.longitude()),
            "timestamp", Now()))))
```

最后，让我们记录查询的响应，以便我们了解查询执行期间的任何问题：

```java
log.info("Query response received from Fauna: {}", queryResponse);

```

注意：出于本文的目的，我们将边缘服务构建为Spring Boot应用程序。在生产中，这些服务可以用任何语言构建，并由[Fastly](https://www.fastly.com/edge-cloud-network/)、[Cloudflare Workers](https://workers.cloudflare.com/)、[Lambda@Edge](https://aws.amazon.com/lambda/edge/)等边缘提供商部署在全球网络中。

我们的整合已经完成；让我们通过集成测试来测试整个流程。

## 8. 测试端到端集成

让我们添加一个测试来验证我们的集成是否端到端地工作，以及我们的请求是否发送到正确的动物区系。我们将模拟GeoLocationService来为我们的测试切换区域：

```java
@SpringBootTest
class DefaultHealthServiceTest {

    @Autowired
    private DefaultHealthService defaultHealthService;

    @MockBean
    private GeoLocationService geoLocationService;
    
    // ...
} 
```

让我们为欧盟地区添加一个测试：

```java
@Test
void givenEURegion_whenProcess_thenRequestSentToEURegion() {

    HealthData healthData = new HealthData("user-1-eu",
        37.5f,
        99f,
        120, 80,
        51.50, -0.07,
        ZonedDateTime.now());

    // ...
}
```

接下来，让我们模拟该区域并调用process方法：

```java
when(geoLocationService.getRegion(51.50, -0.07)).thenReturn("EU");

defaultHealthService.process(healthData);
```

当我们运行测试时，我们可以检查日志是否获取了正确的 URL 以创建FaunaClient：

```java
Creating Fauna Client for Region:EU with URL:https://db.eu.fauna.com/
```

我们还可以检查 Fauna 服务器返回的响应，确认记录已正确创建：

```java
Query response received from Fauna: 
{
ref: ref(id = "338686945465991375", 
collection = ref(id = "healthdata", collection = ref(id = "collections"))), 
ts: 1659255891215000, 
data: {bpDiastolic: 80, 
userId: "user-1-eu", 
temperature: 37.5, 
longitude: -0.07, latitude: 51.5, 
bpSystolic: 120, 
pulseRate: 99.0, 
timestamp: 2022-07-31T08:24:51.164033Z}}
```

我们还可以在欧盟数据库的HealthData集合下的 Fauna 仪表板中验证相同的记录：

[![动物仪表板集合](https://www.baeldung.com/wp-content/uploads/2022/08/fauna-dashboard-collection.png)](https://www.baeldung.com/wp-content/uploads/2022/08/fauna-dashboard-collection.png)

同样，我们可以添加美国地区的测试：

```java
@Test
void givenUSRegion_whenProcess_thenRequestSentToUSRegion() {

    HealthData healthData = new HealthData("user-1-us", //
        38.0f, //
        100f, //
        115, 85, //
        40.75, -74.30, //
        ZonedDateTime.now());

    when(geoLocationService.getRegion(40.75, -74.30)).thenReturn("US");

    defaultHealthService.process(healthData);
}
```

## 9.总结

在本文中，我们探讨了如何利用 Fauna 的[分布式](https://www.baeldung.com/fauna-docs-requirements)、[文档关系](https://www.baeldung.com/fauna-docs-dr)和无服务器功能将其用作 IoT 应用程序的数据库。[Fauna's Region 组](https://www.baeldung.com/fauna-docs-rg)基础架构解决了局部问题并减轻了边缘服务器的延迟问题。

你还可以查看 Fauna 的[点播网络研讨会](https://www.baeldung.com/fauna-webinar)，了解 Fauna 如何减少边缘应用程序的延迟。