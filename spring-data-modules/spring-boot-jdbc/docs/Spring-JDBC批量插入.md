## 1. 概述

在本教程中，我们将学习如何使用 Spring JDBC Batch 支持将大量数据有效地插入到我们的目标 RDBMS 中，并且我们将比较使用批量插入与多个单个插入的性能。

## 2. 了解批处理

一旦我们的应用程序建立了与数据库的连接，我们就可以一次执行多个 SQL 语句，而不是一个接一个地发送每个语句。因此，我们大大减少了通信开销。

实现此目的的一种选择是使用 Spring JDBC API，这是以下部分的重点。

### 2.1. 支持数据库

尽管[JDBC API](https://www.baeldung.com/jdbc-batch-processing)提供了批处理功能，但不能保证我们使用的底层 JDBC 驱动程序确实实现了这些 API 并支持此功能。

Spring 提供了一个名为JdbcUtils.supportsBatchUpdates()的实用方法，它将 JDBC连接作为参数，并简单地返回true或false。然而，在大多数使用JdbcTemplate API 的情况下，Spring 已经为我们检查了它，否则会回退到常规行为。

### 2.2. 可能影响整体性能的因素

插入大量数据时，我们应该考虑几个方面：

-   我们创建的与数据库服务器对话的连接数
-   我们插入的表
-   我们为执行单个逻辑任务所做的数据库请求数

通常，为了克服第一点，我们使用连接池。这有助于重用现有连接而不是创建新连接。

另一个重要的点是目标表。准确地说，我们拥有的索引列越多，性能就越差，因为数据库服务器需要在每个新行之后调整索引。

最后，我们可以使用批处理支持来减少插入大量条目的往返次数。

然而，我们应该知道并非所有 JDBC 驱动程序/数据库服务器都提供相同的批处理操作效率级别，即使它们支持它。例如，虽然 Oracle、Postgres、SQL Server 和 DB2 等数据库服务器提供了显着的收益，但 MySQL 在没有任何额外配置的情况下提供的收益较差。

## 3. Spring JDBC批量插入

在这个例子中，我们将使用 Postgres 14 作为我们的数据库服务器。因此，我们需要将相应的[postgresql](https://search.maven.org/search?q=a:postgresql AND g:org.postgresql) JDBC 驱动程序添加到我们的依赖项中：

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

然后，为了使用 Spring 的 JDBC 抽象，我们还要添加[spring-boot-starter-jdbc](https://search.maven.org/search?q=a:spring-boot-starter-jdbc)依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
```

出于演示目的，我们将探索 2 种不同的方法：首先，我们将为每条记录进行常规插入，然后我们将尝试利用批处理支持。在任何一种情况下，我们都将使用单个事务。

让我们先从简单的Product表开始：

```sql
CREATE TABLE product (
    id              SERIAL PRIMARY KEY,
    title           VARCHAR(40),
    created_ts      timestamp without time zone,
    price           numeric
);
```

这是相应的模型产品类：

```java
public class Product {
    private long id;
    private String title;
    private LocalDateTime createdTs;
    private BigDecimal price;
  
    // standard setters and getters
}
```

### 3.1. 配置数据源

通过将以下配置添加到我们的application.properties中，Spring Boot为我们创建了一个DataSource和一个JdbcTemplate bean：

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sample-baeldung-db
spring.datasource.username=postgres
spring.datasource.password=root
spring.datasource.driver-class-name=org.postgresql.Driver
```

### 3.2. 准备常规插入物

我们首先创建一个简单的存储库界面来保存产品列表：

```java
public interface ProductRepository {
    void saveAll(List<Product> products);
}

```

然后第一个实现简单地迭代产品并将它们一个一个地插入到同一个事务中：

```java
@Repository
public class SimpleProductRepository implements ProductRepository {

    private JdbcTemplate jdbcTemplate;

    public SimpleProductRepository(JdbcTemplate jdbcTemplate) {
      this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void saveAll(List<Product> products) {
      for (Product product : products) {
        jdbcTemplate.update("INSERT INTO PRODUCT (TITLE, CREATED_TS, PRICE) " +
          "VALUES (?, ?, ?)",
          product.getTitle(),
          Timestamp.valueOf(product.getCreatedTs()),
          product.getPrice());
      }
    }

}
```

现在，我们需要一个服务类ProductService来生成给定数量的 Product 对象并启动插入过程。首先，我们有一种方法可以使用一些预定义的值以随机方式生成给定数量的Product实例：

```java
public class ProductService {

    private ProductRepository productRepository;
    private Random random;
    private Clock clock;

    // constructor for the dependencies

    private List<Product> generate(int count) {
        final String[] titles = { "car", "plane", "house", "yacht" };
        final BigDecimal[] prices = {
          new BigDecimal("12483.12"),
          new BigDecimal("8539.99"),
          new BigDecimal("88894"),
          new BigDecimal("458694")
        };

        final List<Product> products = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            Product product = new Product();
            product.setCreatedTs(LocalDateTime.now(clock));
            product.setPrice(prices[random.nextInt(4)]);
            product.setTitle(titles[random.nextInt(4)]);
            products.add(product);
        }
        return products;
    }
}

```

其次，我们将另一个方法添加到ProductService类中，该方法获取生成的Product实例并将它们插入：

```java
@Transactional
public long createProducts(int count) {
  List<Product> products = generate(count);
  long startTime = clock.millis();
  productRepository.saveAll(products);
  return clock.millis() - startTime;
}
```

为了使ProductService 成为 Spring bean，我们还要添加以下配置：

```java
@Configuration
public class AppConfig {

    @Bean
    public ProductService simpleProductService(SimpleProductRepository simpleProductRepository) {
      return new ProductService(simpleProductRepository, new Random(), Clock.systemUTC());
    }
}
```

正如我们所看到的，这个ProductService bean 使用SimpleProductRepository来执行常规插入。

### 3.3. 准备批量插入

现在，是时候看看 Spring JDBC 批处理支持的实际应用了。首先，让我们开始创建我们的ProductRepository类的另一个批处理实现：

```java
@Repository
public class BatchProductRepository implements ProductRepository {

    private JdbcTemplate jdbcTemplate;

    public BatchProductRepository(JdbcTemplate jdbcTemplate) {
      this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void saveAll(List<Product> products) {
      jdbcTemplate.batchUpdate("INSERT INTO PRODUCT (TITLE, CREATED_TS, PRICE) " +
        "VALUES (?, ?, ?)",
        products,
        100,
        (PreparedStatement ps, Product product) -> {
          ps.setString(1, product.getTitle());
          ps.setTimestamp(2, Timestamp.valueOf(product.getCreatedTs()));
          ps.setBigDecimal(3, product.getPrice());
        });
     }
}
```

重要的是要注意，对于此示例，我们使用批处理大小 100。这意味着 Spring 将每 100 个插入进行批处理并单独发送它们。换句话说，它将帮助我们将往返次数减少 100 次。

通常，建议的批处理大小为 50-100，但这在很大程度上取决于我们的数据库服务器配置和每个批处理包的大小。

例如，MySQL 服务器具有名为max_allowed_packet的配置属性，每个网络包的大小限制为 64MB。在设置批处理大小时，我们需要注意不要超过我们的数据库服务器限制。

现在，我们在AppConfig类中添加一个额外的ProductService bean 配置：

```java
@Bean
public ProductService batchProductService(BatchProductRepository batchProductRepository) {
  return new ProductService(batchProductRepository, new Random(), Clock.systemUTC());
}
```

## 4.性能比较

是时候运行我们的示例并查看基准测试了。为了简单起见，我们通过实现 Spring 提供的CommandLineRunner 接口来准备一个[命令行 Spring Boot](https://www.baeldung.com/spring-boot-console-app)应用程序。我们针对这两种方法多次运行示例：

```java
@SpringBootApplication
public class SpringJdbcBatchPerformanceApplication implements CommandLineRunner {

    @Autowired
    @Qualifier("batchProductService")
    private ProductService batchProductService;
    @Autowired
    @Qualifier("simpleProductService")
    private ProductService simpleProductService;

    public static void main(String[] args) {
      SpringApplication.run(SpringJdbcBatchPerformanceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
      int[] recordCounts = {1, 10, 100, 1000, 10_000, 100_000, 1000_000};

      for (int recordCount : recordCounts) {
        long regularElapsedTime = simpleProductService.createProducts(recordCount);
        long batchElapsedTime = batchProductService.createProducts(recordCount);

        System.out.println(String.join("", Collections.nCopies(50, "-")));
        System.out.format("%-20s%-5s%-10s%-5s%8smsn", "Regular inserts", "|", recordCount, "|", regularElapsedTime);
        System.out.format("%-20s%-5s%-10s%-5s%8smsn", "Batch inserts", "|", recordCount, "|", batchElapsedTime);
        System.out.printf("Total gain: %d %sn", calculateGainInPercent(regularElapsedTime, batchElapsedTime), "%");
      }

    }

    int calculateGainInPercent(long before, long after) {
      return (int) Math.floor(100D  (before - after) / before);
    }
}
```

这是我们的基准测试结果：

```plaintext
--------------------------------------------------
Regular inserts     |    1         |          14ms
Batch inserts       |    1         |           8ms
Total gain: 42 %
--------------------------------------------------
Regular inserts     |    10        |           4ms
Batch inserts       |    10        |           1ms
Total gain: 75 %
--------------------------------------------------
Regular inserts     |    100       |          29ms
Batch inserts       |    100       |           6ms
Total gain: 79 %
--------------------------------------------------
Regular inserts     |    1000      |         175ms
Batch inserts       |    1000      |          24ms
Total gain: 86 %
--------------------------------------------------
Regular inserts     |    10000     |         861ms
Batch inserts       |    10000     |         128ms
Total gain: 85 %
--------------------------------------------------
Regular inserts     |    100000    |        5098ms
Batch inserts       |    100000    |        1126ms
Total gain: 77 %
--------------------------------------------------
Regular inserts     |    1000000   |       47738ms
Batch inserts       |    1000000   |       13066ms
Total gain: 72 %
--------------------------------------------------

```

结果看起来很有希望。

然而，这还不是全部。一些数据库如 Postgres、MySQL 和 SQL Server 支持多值插入。它有助于减少插入语句的整体大小。让我们看看这是如何工作的：

```sql
-- REGULAR INSERTS TO INSERT 4 RECORDS
INSERT INTO PRODUCT
(TITLE, CREATED_TS, PRICE)
VALUES
 ('test1', LOCALTIMESTAMP, 100.10);

INSERT INTO PRODUCT
(TITLE, CREATED_TS, PRICE)
VALUES
 ('test2', LOCALTIMESTAMP, 101.10);
 
INSERT INTO PRODUCT
(TITLE, CREATED_TS, PRICE)
VALUES
 ('test3', LOCALTIMESTAMP, 102.10);

INSERT INTO PRODUCT
(TITLE, CREATED_TS, PRICE)
VALUES
 ('test4', LOCALTIMESTAMP, 103.10);

-- EQUIVALENT MULTI-VALUE INSERT
INSERT INTO PRODUCT
(TITLE, CREATED_TS, PRICE)
VALUES
 ('test1', LOCALTIMESTAMP, 100.10),
 ('test2', LOCALTIMESTAMP, 101.10),
 ('test3', LOCALTIMESTAMP, 102.10),
 ('test4', LOCALTIMESTAMP, 104.10);

```

要在 Postgres 数据库中利用此功能，只需在我们的application.properties文件中设置spring.datasource.hikari.data-source-properties.reWriteBatchedInserts=true 即可 。底层 JDBC 驱动程序开始将我们的常规插入语句重写为多值语句以进行批量插入。

此配置特定于 Postgres。其他支持数据库可能有不同的配置要求。

让我们在启用此功能的情况下重新运行我们的应用程序，看看有什么不同：

```plaintext
--------------------------------------------------
Regular inserts     |    1         |          15ms
Batch inserts       |    1         |          10ms
Total gain: 33 %
--------------------------------------------------
Regular inserts     |    10        |           3ms
Batch inserts       |    10        |           2ms
Total gain: 33 %
--------------------------------------------------
Regular inserts     |    100       |          42ms
Batch inserts       |    100       |          10ms
Total gain: 76 %
--------------------------------------------------
Regular inserts     |    1000      |         141ms
Batch inserts       |    1000      |          19ms
Total gain: 86 %
--------------------------------------------------
Regular inserts     |    10000     |         827ms
Batch inserts       |    10000     |         104ms
Total gain: 87 %
--------------------------------------------------
Regular inserts     |    100000    |        5093ms
Batch inserts       |    100000    |         981ms
Total gain: 80 %
--------------------------------------------------
Regular inserts     |    1000000   |       50482ms
Batch inserts       |    1000000   |        9821ms
Total gain: 80 %
--------------------------------------------------

```

我们可以看到，当我们有一个相对较大的数据集时，启用此功能会提高整体性能。

## 5.总结

在本文中，我们创建了一个简单的示例来展示我们如何从 Spring JDBC 对插入的批处理支持中获益。我们将常规插入与批处理插入进行了比较，获得了大约 80-90% 的性能提升。当然，在使用批处理功能的同时，我们还需要考虑我们的JDBC驱动的支持和效率。

此外，我们了解到一些数据库/驱动程序提供多值插入功能以进一步提高性能，并且我们了解了如何在 Postgres 中使用它。