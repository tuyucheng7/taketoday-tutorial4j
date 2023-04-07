## **一、概述**

在本教程中，我们将学习如何使用 Sprint Boot 连接到[NoSQL](https://www.baeldung.com/category/persistence/nosql/)数据库。对于本文的重点，我们将使用[DataStax Astra DB ，这是一个由](https://www.baeldung.com/datastax-post)[Apache Cassandra](https://cassandra.apache.org/)提供支持的 DBaaS ，它允许我们使用云原生服务开发和部署数据驱动的应用程序。

首先，我们将首先了解如何使用 Astra DB 设置和配置我们的应用程序。然后我们将学习如何使用[Spring Boot](https://www.baeldung.com/category/spring/spring-boot/)构建一个简单的应用程序。

## **2.依赖关系**

让我们从将依赖项添加到我们的*pom.xml*开始。当然，我们将需要*[spring-boot-starter-data-cassandra](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-data-cassandra)*依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-cassandra</artifactId>
    <version>2.6.3</version>
</dependency>复制
```

接下来，我们将添加[*spring-boot-starter-web*](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-web)依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
     <version>2.6.3</version>
</dependency>复制
```

最后，我们将使用 Datastax [*astra-spring-boot-starter*](https://search.maven.org/search?q=g:com.datastax.astra AND a:astra-spring-boot-starter)：

```xml
<dependency>
    <groupId>com.datastax.astra</groupId>
    <artifactId>astra-spring-boot-starter</artifactId>
    <version>0.3.0</version>
</dependency>复制
```

现在我们已经配置了所有必要的依赖项，我们可以开始编写我们的 Spring Boot 应用程序了。

## **3.数据库设置**

在我们开始定义我们的应用程序之前，**重要的是快速重申 DataStax Astra 是一个基于云的数据库产品，由 Apache Cassandra 提供支持**。这为我们提供了一个完全托管、完全托管的 Cassandra 数据库，我们可以使用它来存储我们的数据。然而，正如我们将要看到的，我们设置和连接到数据库的方式有一些特殊性。

为了与我们的数据库进行交互，我们需要在主机平台上[设置我们的 Astra 数据库。](https://www.baeldung.com/cassandra-astra-stargate-dashboard#how-to-set-up-datastax-astra)然后，我们需要下载我们的[Secure Connect Bundle](https://www.baeldung.com/cassandra-astra-rest-dashboard-map#1-download-secure-connect-bundle)，其中包含 SSL 证书的详细信息和该数据库的连接详细信息，使我们能够安全连接。

出于本教程的目的，我们假设我们已经完成了这两项任务。

## **4. 应用配置**

接下来，我们将为我们的应用程序配置一个简单的 *主* 类：

```java
@SpringBootApplication
public class AstraDbSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(AstraDbSpringApplication.class, args);
    }
}复制
```

正如我们所见，这是一个普通的[Spring Boot 应用程序](https://www.baeldung.com/spring-boot-start)。现在让我们开始填充我们的*application.properties*文件：

```plaintext
astra.api.application-token=<token>
astra.api.database-id=<your_db_id>
astra.api.database-region=europe-west1复制
```

**这些是我们的 Cassandra 凭据，可以直接从 Astra 仪表板获取。**

为了通过标准*CqlSession使用*[CQL](https://www.baeldung.com/cassandra-data-types)，我们将添加另外几个属性，包括我们下载的安全连接包的位置：

```plaintext
astra.cql.enabled=true
astra.cql.downloadScb.path=~/.astra/secure-connect-shopping-list.zip
复制
```

最后，我们将添加几个标准的 Spring Data 属性来使用[Cassandra](https://www.baeldung.com/spring-data-cassandra-tutorial)：

```plaintext
spring.data.cassandra.keyspace=shopping_list
spring.data.cassandra.schema-action=CREATE_IF_NOT_EXISTS复制
```

在这里，我们指定了我们的数据库键空间，并告诉 Spring Data 在它们不存在时创建我们的表。

## **5. 测试我们的连接**

现在我们已经准备好测试数据库连接的所有部分。那么让我们继续定义一个简单的[REST 控制器](https://www.baeldung.com/spring-controller-vs-restcontroller)：

```java
@RestController
public class AstraDbApiController {

    @Autowired
    private AstraClient astraClient;

    @GetMapping("/ping")
    public String ping() {
        return astraClient.apiDevopsOrganizations()
          .organizationId();
    }

}复制
```

如我们所见，我们使用*AstraClient*类创建了一个简单的 ping 端点，它将返回我们数据库的组织 ID。**这是一个包装类，作为 Astra SDK 的一部分提供，我们可以使用它与各种 Astra API 进行交互**。

最重要的是，这只是一个简单的测试，以确保我们可以建立连接。那么让我们继续使用 Maven 运行我们的应用程序：

```bash
mvn clean install spring-boot:run复制
```

我们应该在我们的控制台上看到与我们的 Astra 数据库建立的连接：

```bash
...
13:08:00.656 [main] INFO  c.d.stargate.sdk.StargateClient - + CqlSession   :[ENABLED]
13:08:00.656 [main] INFO  c.d.stargate.sdk.StargateClient - + API Cql      :[ENABLED]
13:08:00.657 [main] INFO  c.d.stargate.sdk.rest.ApiDataClient - + API Data     :[ENABLED]
13:08:00.657 [main] INFO  c.d.s.sdk.doc.ApiDocumentClient - + API Document :[ENABLED]
13:08:00.658 [main] INFO  c.d.s.sdk.gql.ApiGraphQLClient - + API GraphQL  :[ENABLED]
13:08:00.658 [main] INFO  com.datastax.astra.sdk.AstraClient
  - [AstraClient] has been initialized.
13:08:01.515 [main] INFO  o.b.s.a.AstraDbSpringApplication
  - Started AstraDbSpringApplication in 7.653 seconds (JVM running for 8.097)
复制
```

同样，如果我们在浏览器中访问我们的端点或使用*curl 访问它，*我们应该会得到一个有效的响应：

```bash
$ curl http://localhost:8080/ping; echo
d23bf54d-1bc2-4ab7-9bd9-2c628aa54e85复制
```

伟大的！现在我们已经建立了数据库连接并使用 Spring Boot 实现了一个简单的应用程序，让我们看看如何存储和检索数据。

## **6. 使用 Spring 数据**

我们有多种风格可供选择，作为我们访问 Cassandra 数据库的基础。在本教程中，我们选择使用[支持 Cassandra 的](https://www.baeldung.com/spring-data-cassandra-tutorial)Spring Data 。

Spring Data 存储库抽象的主要目标是显着减少实现我们的数据访问层所需的样板代码量，这将有助于使我们的示例非常简单。

对于我们的数据模型，**我们将定义一个表示简单购物清单的实体**：

```java
@Table
public class ShoppingList {

    @PrimaryKey
    @CassandraType(type = Name.UUID)
    private UUID uid = UUID.randomUUID();

    private String title;
    private boolean completed = false;

    @Column
    private List<String> items = new ArrayList<>();

    // Standard Getters and Setters
}复制
```

在这个例子中，我们在我们的 bean 中使用了几个标准注释来将我们的实体映射到 Cassandra 数据表并定义一个名为*uid*的主键列。

现在让我们创建要在我们的应用程序中使用的*ShoppingListRepository ：*

```java
@Repository
public interface ShoppingListRepository extends CassandraRepository<ShoppingList, String> {

    ShoppingList findByTitleAllIgnoreCase(String title);

}复制
```

这遵循标准的 Spring Data 存储库抽象。除了*CassandraRepository*接口中包含的继承方法（例如*findAll ）*之外，**我们还添加了一个额外的方法\*findByTitleAllIgnoreCase\*，我们可以使用该方法使用标题查找购物清单。**

实际上，使用 Astra [Spring Boot Starter](https://www.baeldung.com/spring-boot-starters)的真正好处之一是它 使用之前定义的属性为我们创建了*CqlSession bean。*

## **7. 把它们放在一起**

现在我们有了数据访问存储库，让我们定义一个简单的服务和控制器：

```java
@Service
public class ShoppingListService {

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    public List<ShoppingList> findAll() {
        return shoppingListRepository.findAll(CassandraPageRequest.first(10)).toList();
    }

    public ShoppingList findByTitle(String title) {
        return shoppingListRepository.findByTitleAllIgnoreCase(title);
    }
    
    @PostConstruct
    public void insert() {
        ShoppingList groceries = new ShoppingList("Groceries");
        groceries.setItems(Arrays.asList("Bread", "Milk, Apples"));

        ShoppingList pharmacy = new ShoppingList("Pharmacy");
        pharmacy.setCompleted(true);
        pharmacy.setItems(Arrays.asList("Nappies", "Suncream, Aspirin"));

        shoppingListRepository.save(groceries);
        shoppingListRepository.save(pharmacy);
    }
    
}复制
```

为了我们的测试应用程序的目的，**我们添加了一个[\*@PostContruct\*注释](https://www.baeldung.com/spring-postconstruct-predestroy)以将一些测试数据插入到我们的数据库中。**

对于拼图的最后一部分，我们将添加一个带有一个端点的简单控制器来检索我们的购物清单：

```java
@RestController
@RequestMapping(value = "/shopping")
public class ShoppingListController {

    @Autowired
    private ShoppingListService shoppingListService;

    @GetMapping("/list")
    public List<ShoppingList> findAll() {
        return shoppingListService.findAll();
    }
}复制
```

现在，当我们运行我们的应用程序并访问 http://localhost:8080/shopping/list 时——我们将看到一个包含不同购物清单对象的 JSON 响应：

```json
[
  {
    "uid": "363dba2e-17f3-4d01-a44f-a805f74fc43d",
    "title": "Groceries",
    "completed": false,
    "items": [
      "Bread",
      "Milk, Apples"
    ]
  },
  {
    "uid": "9c0f407e-5fc1-41ad-8e46-b3c115de9474",
    "title": "Pharmacy",
    "completed": true,
    "items": [
      "Nappies",
      "Suncream, Aspirin"
    ]
  }
]复制
```

这证实了我们的应用程序工作正常。惊人的！

## **8. 使用 Cassandra 模板**

另一方面，也可以直接使用[Cassandra 模板](https://www.baeldung.com/spring-data-cassandratemplate-cqltemplate)，经典的 Spring CQL 方法，并且可能仍然是最流行的。

简单地说，我们可以很容易地扩展我们的*AstraDbApiController*来检索我们的数据中心：

```java
@Autowired
private CassandraTemplate cassandraTemplate;

@GetMapping("/datacenter")
public String datacenter() {
    return cassandraTemplate
        .getCqlOperations()
        .queryForObject("SELECT data_center FROM system.local", String.class);
}复制
```

这仍将利用我们定义的所有配置属性。**因此，正如我们所见，两种访问方法之间的切换是完全透明的。**

## **9.结论**

在本文中，我们学习了如何设置和连接到托管的 Cassandra Astra 数据库。接下来，我们构建了一个简单的购物清单应用程序来使用 Spring Data 存储和检索数据。最后，我们还讨论了如何使用较低级别的访问方法 Cassandra 模板。

一如既往，本文的完整源代码可 [在 GitHub 上](https://github.com/Baeldung/datastax-cassandra/tree/main/spring-boot-astra-db)获得。