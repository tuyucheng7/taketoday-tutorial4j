## **一、简介**

**在本文中，我们将探索[Fauna 分布式数据库](https://www.baeldung.com/partners-fauna)。**我们将看到它为我们的应用程序带来了哪些特性，我们可以用它做什么，以及如何与它交互。

## **2. 什么是动物群？**

**Fauna 是一种多协议、多模型、多租户、分布式、事务性数据库即服务 (DBaaS) 产品。**这听起来很复杂，所以让我们分解一下。

### **2.1. 数据库即服务**

**“数据库即服务”意味着数据库由云提供商托管，云提供商负责所有基础架构和维护，因此我们只需要处理特定领域的细节——集合、索引、查询**、等。这有助于消除管理此类系统的许多复杂性，同时仍能从其功能中受益。

### **2.2. 分布式事务数据库**

**分布式意味着数据库在多个服务器上运行。**这有助于同时提高效率和容错能力。如果一台服务器出现故障，那么整个数据库仍然能够继续正常工作。

**事务性意味着数据库为数据的有效性提供了强有力的保证。**在单个事务中执行的数据更新作为一个整体要么成功要么失败，没有使数据处于部分状态的风险。

作为进一步的措施，Fauna 提供了隔离级别，以确保跨多个分布式节点进行多个交易的结果始终是正确的。这是分布式数据库的一个重要考虑因素——否则，不同的交易可能会在不同的节点上以不同的方式进行，并最终得到不同的结果。

例如，让我们考虑以下应用于同一记录的事务：

1.  将值设置为“15”
2.  将值增加“3”

如果按照显示的顺序播放，最终结果将是“18”。然而，如果它们以相反的顺序播放，最终结果将是“15”。如果同一系统中不同节点的结果不同，这会更加令人困惑，因为这意味着我们的数据将在节点之间不一致。

### **2.3. 多模型数据库**

**多模型数据库意味着它允许我们以不同的方式对不同类型的数据建模**，所有这些都在同一个数据库引擎中，并且可以从相同的连接访问。

在内部，Fauna 是一个文档数据库。这意味着它将每条记录存储为结构化文档，具有以 JSON 表示的任意形状。这允许 Fauna 充当键值存储——文档只有一个字段，*值*——或作为表格存储——文档具有所需的尽可能多的字段，但它们都是平面的。但是，我们也可以存储更复杂的文档，包括嵌套字段、数组等：

```javascript
// Key-Value document
{
  "value": "Baeldung"
}

// Tabular document
{
  "name": "Baeldung",
  "url": "https://www.baeldung.com/"
}

// Structured document
{
  "name": "Baeldung",
  "sites": [
    {
      "id": "cs",
      "name": "Computer Science",
      "url": "https://www.baeldung.com/cs"
    },
    {
      "id": "linux",
      "name": "Linux",
      "url": "https://www.baeldung.com/linux"
    },
    {
      "id": "scala",
      "name": "Scala",
      "url": "https://www.baeldung.com/scala"
    },
    {
      "id": "kotlin",
      "name": "Kotlin",
      "url": "https://www.baeldung.com/kotlin"
    },
  ]
}复制
```

除此之外，我们还可以访问关系数据库中常见的一些功能。具体来说，我们可以在文档上创建索引以提高查询效率，跨多个集合应用约束以确保数据保持一致，并一次性执行跨多个集合的查询。

Fauna 的查询引擎还支持图查询，使我们能够构建跨越多个集合的复杂数据结构，并像访问单个数据图一样访问它们。

最后，Fauna 具有时间建模功能，可以让我们在数据库生命周期的任何时候与我们的数据库进行交互。这意味着我们不仅可以看到记录随时间发生的所有变化，还可以直接访问给定时间点的数据。

### **2.4. 多租户数据库**

**多租户数据库服务器意味着它支持不同用户使用的多个不同数据库。**这在用于云托管的数据库引擎中很常见，因为这意味着一台服务器可以支持许多不同的客户。

动物群采取了稍微不同的方向。Fauna 使用租户代表单个客户的不同数据子集，而不是在单个已安装的数据库引擎中代表不同客户的不同租户。

**可以创建本身是其他数据库的子数据库的数据库。**然后我们可以创建用于访问这些子数据库的凭据。然而，Fauna 的不同之处在于我们可以对来自我们所连接的子数据库的数据执行只读查询。但是，无法访问父数据库或同级数据库中的数据。

这允许我们在同一个父数据库中为不同的服务创建子数据库，然后让管理员用户一次性查询所有数据——这对于分析目的来说很方便。

### **2.5. 多协议数据库**

**这意味着我们有多种不同的方式来访问相同的数据。**

访问我们数据的标准方法是通过提供的驱动程序之一使用动物群查询语言 (FQL)。这使我们能够访问数据库引擎的全部功能，使我们能够以任何需要的方式访问所有数据。

或者，Fauna 还公开了一个我们可以使用的 GraphQL 端点。这样做的好处是我们可以在任何应用程序中使用它，而不管编程语言是什么，而不是依赖于我们语言的专用驱动程序。但是，并非所有功能都可通过此界面使用。特别是，我们需要提前创建一个描述数据形状的 GraphQL 模式，这意味着我们不能在同一集合中拥有不同形状的不同记录。

## **3. 创建动物数据库**

现在我们知道 Fauna 可以为我们做什么，让我们实际创建一个数据库供我们使用。

如果我们还没有帐户，我们需要[创建一个](https://www.baeldung.com/fauna-register)。

登录后，在仪表板上，我们只需单击“创建数据库”链接：

[![动物群创建数据库](https://www.baeldung.com/wp-content/uploads/2022/01/fauna-create-db.png)](https://www.baeldung.com/wp-content/uploads/2022/01/fauna-create-db.png)

然后，这会为数据库的名称和区域打开一个窗格。我们还可以选择使用一些示例数据预填充数据库以查看其工作原理，以帮助我们习惯该系统：

[![动物区系](https://www.baeldung.com/wp-content/uploads/2022/01/fauna-db-region.png)](https://www.baeldung.com/wp-content/uploads/2022/01/fauna-db-region.png)

在这个屏幕上，“区域组”的选择很重要，这既是因为我们必须为超过免费限制的任何东西支付的金额，也是因为我们需要用来从外部连接到数据库的端点。

完成此操作后，我们就有了一个可以根据需要使用的完整数据库。如果我们选择了演示数据，那么它会包含一些填充的集合、索引、自定义函数和 GraphQL 架构。如果没有，那么数据库完全是空的，可以让我们创建我们想要的结构：

[![动物数据库结构](https://www.baeldung.com/wp-content/uploads/2022/01/fauna-db-structure.png)](https://www.baeldung.com/wp-content/uploads/2022/01/fauna-db-structure.png)

最后，为了从外部连接到数据库，我们需要一个身份验证密钥。我们可以从侧边栏的“安全”选项卡创建一个：

[![动物群认证密钥](https://www.baeldung.com/wp-content/uploads/2022/01/fauna-auth-key.png)](https://www.baeldung.com/wp-content/uploads/2022/01/fauna-auth-key.png)

创建新密钥时，请务必将其复制下来，因为出于安全原因，离开屏幕后无法再次取回。

## **4. 与动物互动**

现在我们有了一个数据库，我们可以开始使用它了。

**Fauna 提供了两种不同的方式来从外部在我们的数据库中读取和写入数据：FQL 驱动程序和 GraphQL API。**我们还可以访问 Fauna Shell，它允许我们从 Web UI 中执行任意命令。

### **4.1. 动物壳**

Fauna Shell 允许我们从 Web UI 中执行任何命令。我们可以使用我们配置的任何密钥来执行此操作——就像我们使用该密钥从外部连接一样——或者作为某些特殊的管理连接：

[![动物群 贝壳](https://www.baeldung.com/wp-content/uploads/2022/01/fauna-shell.png)](https://www.baeldung.com/wp-content/uploads/2022/01/fauna-shell.png)

这使我们能够以非常低摩擦的方式探索我们的数据并测试我们希望从我们的应用程序中使用的查询。

### **4.2. 与 FQL 连接**

**如果我们想将我们的应用程序连接到 Fauna 并使用 FQL，我们需要使用[提供的驱动程序](https://www.baeldung.com/fauna-drivers)**之一——包括用于 Java 和 Scala 的驱动程序。

**Java 驱动程序要求我们在 Java 11 或更高版本上运行。**

我们需要做的第一件事是添加依赖项。如果我们使用 Maven，我们只需将它添加到我们的*pom.xml*文件中：

```xml
<dependency>
    <groupId>com.faunadb</groupId>
    <artifactId>faunadb-java</artifactId>
    <version>4.2.0</version>
    <scope>compile</scope>
</dependency>复制
```

然后我们需要创建一个客户端连接，我们可以用它来与数据库通信：

```java
FaunaClient client = FaunaClient.builder()
    .withEndpoint("https://db.us.fauna.com/")
    .withSecret("put-your-authorization-key-here")
    .build();复制
```

请注意，我们需要为数据库端点提供正确的值（根据创建数据库时选择的区域组而有所不同）以及我们之前创建的密钥。

**该客户端将充当连接池，根据不同查询的需要打开与数据库的新连接。**这意味着我们可以在应用程序开始时创建一次，然后根据需要重复使用它。

如果我们需要连接不同的秘密，这将需要不同的客户端。例如，如果我们想与同一个父数据库中的多个不同的子数据库进行交互。

现在我们有了一个客户端，我们可以使用它向数据库发送查询：

```java
client.query(
    language.Get(language.Ref(language.Collection("customers"), 101))
).get();复制
```

### **4.3. 与 GraphQL 连接**

Fauna 提供了一个完整的 GraphQL API，用于与我们的数据库进行交互。这可以让我们在没有任何特殊驱动程序的情况下使用数据库，只需要一个 HTTP 客户端。

**为了使用 GraphQL 支持，我们需要先创建一个 GraphQL 模式。**这将定义模式本身以及它如何映射到我们预先存在的 Fauna 数据库构造——例如集合、索引和函数。**完成后，任何支持 GraphQL 的客户端——甚至只是一个 HTTP 客户端，如\*RestTemplate——\*都可以用来调用我们的数据库。**

请注意，这将只允许我们与数据库中的数据进行交互。如果我们希望使用任何管理命令——例如创建新的集合或索引——那么这需要一个 FQL 命令或 Web 管理 UI。

*通过 GraphQL 连接到 Fauna 需要我们使用正确的 URL——美国地区为 https://graphql.us.fauna.com/graphql——并在授权*标头中提供我们的身份验证密钥作为不记名令牌。此时，我们可以将它用作任何普通的 GraphQL 端点，方法是向 URL 发出 POST 请求并在正文中提供查询或突变，可选择与它们一起使用的任何变量。

## **5.使用春天的动物群**

现在我们了解了 Fauna 是什么以及如何使用它，我们可以看看如何将它集成到我们的 Spring 应用程序中。

Fauna 没有任何本地 Spring 驱动程序。相反，我们将把普通的 Java 驱动程序配置为 Spring bean，以便在我们的应用程序中使用。

### 5.1. 动物群配置

**在我们使用 Fauna 之前，我们需要一些配置。**具体来说，我们需要知道我们的 Fauna 数据库所在的区域——然后我们可以从中导出适当的 URL——并且我们需要知道可以用来连接到数据库的秘密。

为此，我们将*fauna.region*和*fauna.secret*的属性添加到我们的*application.properties*文件——或任何其他受支持的[Spring 配置方法](https://www.baeldung.com/properties-with-spring)：

```plaintext
fauna.region=us
fauna.secret=FaunaSecretHere复制
```

请注意，我们在这里定义动物群区域而不是 URL。这使我们能够从相同的设置中正确地导出 FQL 和 GraphQL 的 URL。这避免了我们可能会以不同方式配置两个 URL 的风险。

### **5.2. FQL客户端**

**如果我们计划在我们的应用程序中使用 FQL，我们可以将\*FaunaClient\* bean 添加到 Spring 上下文中。**这将涉及创建一个 Spring 配置对象以使用适当的属性并构造*FaunaClient*对象：

```java
@Configuration
class FaunaClientConfiguration {
    @Value("https://db.${fauna.region}.fauna.com/")
    private String faunaUrl;

    @Value("${fauna.secret}")
    private String faunaSecret;

    @Bean
    FaunaClient getFaunaClient() throws MalformedURLException {
        return FaunaClient.builder()
            .withEndpoint(faunaUrl)
            .withSecret(faunaSecret)
            .build();
    }
}
复制
```

这让我们可以在应用程序的任何地方直接使用*FaunaClient*，就像我们使用*JdbcTemplate*访问 JDBC 数据库一样。如果我们愿意，我们也有机会将其包装在更高级别的对象中以在特定领域的术语中工作。

### **5.3. GraphQL 客户端**

如果我们计划使用 GraphQL 访问 Fauna，则需要做更多的工作。没有用于调用 GraphQL API 的标准客户端。相反，**我们将使用[Spring RestTemplate](https://www.baeldung.com/rest-template)向 GraphQL 端点发出标准 HTTP 请求。**如果我们正在构建基于 WebFlux 的应用程序，则较新的[WebClient](https://www.baeldung.com/spring-5-webclient)将同样有效。

为此，我们将编写一个包装*RestTemplate*的类，并可以对 Fauna 进行适当的 HTTP 调用：

```java
@Component
public class GraphqlClient {
    @Value("https://graphql.${fauna.region}.fauna.com/graphql")
    private String faunaUrl;

    @Value("${fauna.secret}")
    private String faunaSecret;

    private RestTemplate restTemplate = new RestTemplate();

    public <T> T query(String query, Class<T> cls) {
        return query(query, Collections.emptyMap(), cls);
    }

    public <T, V> T query(String query, V variables, Class<T> cls) {
        var body = Map.of("query", query, "variables", variables);

        var request = RequestEntity.post(faunaUrl)
            .header("Authorization", "Bearer " + faunaSecret)
            .body(body);
        var response = restTemplate.exchange(request, cls);

        return response.getBody();
    }
}复制
```

该客户端允许我们从应用程序的其他组件对 Fauna 进行 GraphQL 调用。我们有两种方法，一种只接受 GraphQL 查询字符串，另一种额外接受一些变量来使用它。

它们也都采用将查询结果反序列化为的类型。使用它可以处理与 Fauna 对话的所有细节，让我们可以专注于我们的应用程序需求。

## **6.总结**

**在本文中，我们简要介绍了 Fauna 数据库，了解了它提供的一些功能，使其成为我们下一个项目的极具吸引力的选择，并了解了我们如何从我们的应用程序中与它进行交互**。

为什么不在您的下一个项目中探索我们在此处提到的一些功能？