## 1. 概述

在本教程中，我们将了解 Azure Cosmos DB 以及我们如何使用 Spring Data 与其进行交互。

## 2. Azure Cosmos 数据库

[Azure Cosmos DB](https://github.com/Azure/azure-sdk-for-java/tree/master/sdk/cosmos/azure-cosmos)是 Microsoft 的全球分布式数据库服务。

它是一个 NoSQL 数据库，为吞吐量、延迟、可用性和一致性保证提供全面的服务级别协议。此外，它确保了[99.999%](https://docs.microsoft.com/en-us/azure/cosmos-db/introduction#guaranteed-low-latency-at-99th-percentile-worldwide)的读写可用性。

Azure Cosmos DB 并没有只给出两种一致性选择，即要么一致要么不一致。相反，我们有五个一致性选择：strong、bounded staleness、session、consistent prefix和eventual。

我们可以弹性扩展 Azure Cosmos DB 的吞吐量和存储。

此外，它在所有 Azure 区域都可用，并提供交钥匙全球分发，因为我们只需单击一个按钮就可以在任何 Azure 区域我们的数据。这有助于我们让数据更接近用户，以便我们可以更快地满足他们的请求。

它与模式无关 ，因为它没有模式。此外，我们不需要为 Azure Cosmos Db 做任何索引管理。它会自动为我们做数据索引。

我们可以使用不同的标准 API(例如 SQL、MongoDB、Cassandra 等)来处理 Azure CosmosDb。

## 3. Spring Data Azure Cosmos DB

Microsoft 还提供了一个模块，允许我们使用 Spring Data 来处理 Cosmos DB。在下一节中，我们将了解如何在 Spring Boot 应用程序中使用 Azure Cosmos DB。

在我们的示例中，我们将创建一个 Spring Web 应用程序，它将产品实体存储在 Azure Cosmos 数据库中并对其执行基本的 CRUD 操作。[首先，我们需要按照文档](https://docs.microsoft.com/en-us/azure/cosmos-db/create-cosmosdb-resources-portal)中的说明在 Azure 门户中配置帐户和数据库。

如果我们不想在 Azure 门户上创建帐户，Azure 还提供了 Azure Cosmos 模拟器。尽管它不包含 Azure Cosmos 服务的所有功能，并且存在一些差异，但我们可以使用它进行本地开发和测试。

我们可以通过两种方式在本地环境中使用模拟器：在我们的机器上下载 Azure Cosmos 模拟器，或者在适用于 Windows 的 Docker 上[运行模拟器](https://docs.microsoft.com/en-us/azure/cosmos-db/local-emulator#running-on-docker)。

我们将选择在 Docker for Windows 上运行它的选项。让我们通过运行以下命令来拉取 Docker 镜像：

```bash
docker pull mcr.microsoft.com/cosmosdb/windows/azure-cosmos-emulator
```

然后我们可以运行 Docker 镜像并通过运行以下命令启动容器：

```powershell
md $env:LOCALAPPDATACosmosDBEmulatorbind-mount 2>null

docker run --name azure-cosmosdb-emulator --memory 2GB --mount 
"type=bind,source=$env:LOCALAPPDATACosmosDBEmulatorbind-mount,destination=C:CosmosDB.Emulatorbind-mount" 
--interactive --tty -p 8081:8081 -p 8900:8900 -p 8901:8901 -p 8902:8902 -p 10250:10250 
-p 10251:10251 -p 10252:10252 -p 10253:10253 -p 10254:10254 -p 10255:10255 -p 10256:10256 -p 10350:10350 
mcr.microsoft.com/cosmosdb/windows/azure-cosmos-emulator
```

一旦我们在 Azure 门户或 Docker 中配置了 Azure Cosmos DB 帐户和数据库，我们就可以继续在我们的 Spring Boot 应用程序中配置它。

## 4. 在 Spring 中使用 Azure Cosmos DB

### 4.1. 使用 Spring 配置 Spring Data Azure Cosmos DB

我们首先在pom.xml中添加[spring-data-cosmosdb](https://mvnrepository.com/artifact/com.microsoft.azure/spring-data-cosmosdb/2.3.0)依赖项：

```xml
<dependency> 
    <groupId>com.microsoft.azure</groupId> 
    <artifactId>spring-data-cosmosdb</artifactId> 
    <version>2.3.0</version> 
</dependency>

```

要从我们的 Spring 应用程序访问 Azure Cosmos DB，我们需要数据库的 URI、它的[访问密钥](https://docs.microsoft.com/en-us/azure/cosmos-db/secure-access-to-data)和数据库名称。 然后我们将在application.properties中添加连接属性：

```xml
azure.cosmosdb.uri=cosmodb-uri
azure.cosmosdb.key=cosmodb-primary-key
azure.cosmosdb.secondaryKey=cosmodb-secondary-key
azure.cosmosdb.database=cosmodb-name

```

我们可以从 Azure 门户中找到上述属性的值。URI、主键和辅助键将在 Azure 门户的 Azure Cosmos DB 的键部分中提供。

要从我们的应用程序连接到 Azure Cosmos DB，我们需要创建一个客户端。为此，我们需要在配置类中扩展AbstractCosmosConfiguration类并添加@EnableCosmosRepositories 注解。

此注解将扫描指定包中扩展 Spring Data 存储库接口的接口。

我们还需要配置CosmosDBConfig类型的 bean ：

```java
@Configuration
@EnableCosmosRepositories(basePackages = "com.baeldung.spring.data.cosmosdb.repository")
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

### 4.2. 为 Azure Cosmos DB 创建实体

为了与 Azure Cosmos DB 交互，我们使用了实体。因此，让我们创建一个将存储在 Azure Cosmos DB 中的实体。为了使我们的Product类成为一个实体，我们将使用@Document注解：

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

在这个例子中，我们使用了collection属性和值products 来指示这将是我们容器在数据库中的名称。如果我们不为集合参数提供任何值，那么类名将用作数据库中的容器名。

我们还为文档定义了一个 id。我们可以在我们的类中创建一个名为id的字段，也可以使用@Id注解来注解一个字段。这里我们使用了productid字段作为文档 ID。

我们可以通过使用分区键对容器中的数据进行逻辑分区，方法是使用@PartitionKey 注解字段。在我们的课程中，我们使用productCategory字段作为分区键。

默认情况下，索引策略由 Azure 定义，但我们也可以通过在我们的实体类上使用@DocumentIndexingPolicy 注解来自定义它。

我们还可以通过创建一个名为_etag的字段并使用@Version 对其进行注解来为我们的实体容器启用乐观锁定。

### 4.3. 定义存储库

现在让我们创建一个扩展CosmosRepository的ProductRepository接口。使用此接口，我们可以在 Azure Cosmos DB 上执行 CRUD 操作：

```java
@Repository
public interface ProductRepository extends CosmosRepository<Product, String> {
    List findByProductName(String productName);

}
```

如我们所见，它的定义方式与其他 Spring Data 模块类似。

### 4.4. 测试连接

现在我们可以创建一个 Junit 测试来使用我们的ProductRepository将Product实体保存在 Azure Cosmos DB 中：

```java
@SpringBootTest
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

通过运行此 Junit 测试，我们可以从 Spring 应用程序测试我们与 Azure Cosmos DB 的连接。

## 5.总结

在本教程中，我们了解了 Azure Cosmos DB。此外，我们学习了如何从 Spring Boot 应用程序访问 Azure Cosmos DB，如何通过扩展CosmosRepository来创建实体和配置存储库以与之交互。