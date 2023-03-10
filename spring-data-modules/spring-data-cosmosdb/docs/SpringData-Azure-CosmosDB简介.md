## 1. 概述

在本教程中，我们将了解Azure Cosmos DB以及我们如何使用Spring Data与其进行交互。

## 2. Azure Cosmos数据库

**[Azure Cosmos DB](https://github.com/Azure/azure-sdk-for-java/tree/master/sdk/cosmos/azure-cosmos)是Microsoft的全球分布式数据库服务**。

**它是一个NoSQL数据库**，为吞吐量、延迟、可用性和一致性保证提供全面的服务级别协议。此外，它确保了[99.999%](https://docs.microsoft.com/en-us/azure/cosmos-db/introduction#guaranteed-low-latency-at-99th-percentile-worldwide)的读写可用性。

Azure Cosmos DB并没有只给出两种一致性选择，即要么一致要么不一致。相反，**我们有五个一致性选择：strong、bounded staleness、session、consistent prefix和eventual**。

我们可以弹性扩展Azure Cosmos DB的吞吐量和存储。

此外，它在所有Azure区域中都可用，并提供统包全球分发，因为我们只需单击一个按钮就可以在任何Azure区域复制我们的数据。这有助于我们让数据更接近用户，以便我们可以更快地满足他们的请求。

**它与模式无关，因为它没有模式**。此外，我们不需要为Azure Cosmos Db做任何索引管理。它会自动为我们编制数据索引。

我们可以使用不同的标准API(例如SQL、MongoDB、Cassandra等)来处理Azure CosmosDb。

## 3. Spring Data Azure Cosmos DB

**Microsoft还提供了一个模块，允许我们使用Spring Data来处理CosmosDB**。在下一节中，我们将了解如何在Spring Boot应用程序中使用Azure Cosmos DB。

在我们的示例中，我们将创建一个Spring Web应用程序，该应用程序将产品实体存储在Azure Cosmos数据库中并对其执行基本的CRUD操作。首先，我们需要按照[文档](https://docs.microsoft.com/en-us/azure/cosmos-db/create-cosmosdb-resources-portal)中的说明在Azure门户中配置帐户和数据库。

**如果我们不想在Azure门户上创建帐户，Azure还提供了Azure Cosmos模拟器**。尽管它不包含Azure Cosmos服务的所有功能，并且存在一些差异，但我们可以使用它进行本地开发和测试。

我们可以通过两种方式在本地环境中使用模拟器：在我们的机器上下载Azure Cosmos模拟器，或者在适用于Windows的Docker上[运行模拟器](https://docs.microsoft.com/en-us/azure/cosmos-db/local-emulator#running-on-docker)。

**我们将选择在Docker for Windows上运行它的选项**。让我们通过运行以下命令来拉取Docker镜像：

```shell
docker pull mcr.microsoft.com/cosmosdb/windows/azure-cosmos-emulator
```

然后我们可以运行Docker镜像并通过运行以下命令启动容器：

```shell
md $env:LOCALAPPDATA\CosmosDBEmulator\bind-mount 2>null

docker run --name azure-cosmosdb-emulator --memory 2GB --mount 
"type=bind,source=$env:LOCALAPPDATA\CosmosDBEmulator\bind-mount,destination=C:\CosmosDB.Emulator\bind-mount" 
--interactive --tty -p 8081:8081 -p 8900:8900 -p 8901:8901 -p 8902:8902 -p 10250:10250 
-p 10251:10251 -p 10252:10252 -p 10253:10253 -p 10254:10254 -p 10255:10255 -p 10256:10256 -p 10350:10350 
mcr.microsoft.com/cosmosdb/windows/azure-cosmos-emulator
```

一旦我们在Azure门户或Docker中配置了Azure Cosmos DB帐户和数据库，我们就可以继续在我们的Spring Boot应用程序中配置它。

## 4. 在Spring中使用Azure Cosmos DB

### 4.1 使用Spring配置Spring Data Azure Cosmos DB

我们首先在pom.xml中添加[spring-data-cosmosdb](https://central.sonatype.com/artifact/com.microsoft.azure/spring-data-cosmosdb/3.0.0.M1)依赖项：

```xml
<dependency> 
    <groupId>com.microsoft.azure</groupId> 
    <artifactId>spring-data-cosmosdb</artifactId> 
    <version>2.3.0</version> 
</dependency>
```

**要从我们的Spring应用程序访问Azure Cosmos DB，我们需要数据库的URI、它的[访问密钥](https://docs.microsoft.com/en-us/azure/cosmos-db/secure-access-to-data)和数据库名称**。然后我们将在application.properties中添加连接属性：

```properties
azure.cosmosdb.uri=cosmodb-uri
azure.cosmosdb.key=cosmodb-primary-key
azure.cosmosdb.secondaryKey=cosmodb-secondary-key
azure.cosmosdb.database=cosmodb-name
```

我们可以从Azure门户中找到上述属性的值。URI、主键和辅助键将在Azure门户的Azure Cosmos DB的keys部分中提供。

要从我们的应用程序连接到Azure Cosmos DB，我们需要创建一个客户端。为此，**我们需要在配置类中扩展AbstractCosmosConfiguration类并添加@EnableCosmosRepositories注解**。

此注解将扫描指定包中扩展Spring Data Repository接口的接口。

我们还需要**配置一个CosmosDBConfig类型的bean**：

```java
@Configuration
@EnableCosmosRepositories(basePackages = "cn.tuyucheng.taketoday.spring.data.cosmosdb.repository")
public class AzureCosmosDbConfiguration extends AbstractCosmosConfiguration {

    @Value("${azure.cosmosdb.uri}")
    private String uri;

    @Value("${azure.cosmosdb.key}")
    private String key;

    @Value("${azure.cosmosdb.database}")
    private String dbName;

    private CosmosKeyCredential cosmosKeyCredential;

    @Bean
    public CosmosDBConfig getConfig() {
        this.cosmosKeyCredential = new CosmosKeyCredential(key);
        CosmosDBConfig cosmosdbConfig = CosmosDBConfig.builder(uri, this.cosmosKeyCredential, dbName)
              .build();
        return cosmosdbConfig;
    }
}
```

### 4.2 为Azure Cosmos DB创建实体

为了与Azure Cosmos DB交互，我们使用了实体。因此，让我们创建一个将存储在Azure Cosmos DB中的实体。为了使我们的Product类成为一个实体，**我们将使用@Document注解**：

```java
@Document(collection = "products")
public class Product {

    @Id
    private String productid;

    private String productName;

    private double price;

    @PartitionKey
    private String productCategory;
}
```

在这个例子中，**我们使用了collection属性和值products来指示这将是我们容器在数据库中的名称**。如果我们没有为collection参数提供任何值，那么类名将用作数据库中的容器名。

我们还为文档定义了一个id。我们可以在我们的类中创建一个名为id的字段，也可以使用@Id注解来标注一个字段。这里我们使用了productid字段作为文档ID。

**我们可以通过使用分区键对容器中的数据进行逻辑分区，方法是使用@PartitionKey标注字段**。在我们的类中，我们使用productCategory字段作为分区键。

默认情况下，索引策略由Azure定义，但我们也可以通过在我们的实体类上使用@DocumentIndexingPolicy注解来自定义它。

我们还可以通过创建一个名为_etag的字段并使用@Version对其进行标注来为我们的实体容器启用乐观锁定。

### 4.3 定义Repository

**现在让我们创建一个扩展CosmosRepository的ProductRepository接口**。使用此接口，我们可以在Azure Cosmos DB上执行CRUD操作：

```java
@Repository
public interface ProductRepository extends CosmosRepository<Product, String> {
    List findByProductName(String productName);
}
```

正如我们所看到的，它的定义方式与其他Spring Data模块类似。

### 4.4 测试连接

现在我们可以创建一个JUnit测试来使用我们的ProductRepository将Product实体保存在Azure Cosmos DB中：

```java
@Spring BootTest
public class AzureCosmosDbApplicationManualTest {

    @Autowired
    ProductRepository productRepository;

    @Test
    public void givenProductIsCreated_whenCallFindById_thenProductIsFound() {
        Product product = new Product();
        product.setProductid("1001");
        product.setProductCategory("Shirt");
        product.setPrice(110.0);
        product.setProductName("Blue Shirt");

        productRepository.save(product);
        Product retrievedProduct = productRepository.findById("1001", new PartitionKey("Shirt"))
              .orElse(null);
        Assert.notNull(retrievedProduct, "Retrieved Product is Null");
    }
}
```

通过运行此JUnit测试，我们可以从Spring应用程序测试我们与Azure Cosmos DB的连接。

## 5. 总结

在本教程中，我们了解了Azure Cosmos DB。此外，我们学习了如何从Spring Boot应用程序访问Azure Cosmos DB，如何通过扩展CosmosRepository来创建实体和配置Repository以与之交互。