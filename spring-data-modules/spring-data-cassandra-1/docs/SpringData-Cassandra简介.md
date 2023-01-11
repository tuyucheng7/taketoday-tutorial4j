## 1. 概述

本文是对使用 Cassandra 和 Spring Data 的实用介绍。

我们将从基础开始，通过配置和编码，最终构建一个完整的 Spring Data Cassandra 模块。

## 延伸阅读：

## [使用 Cassandra、Astra 和 Stargate 构建仪表板](https://www.baeldung.com/cassandra-astra-stargate-dashboard)

了解如何使用 DataStax Astra 构建仪表板，DataStax Astra 是一种由 Apache Cassandra 和 Stargate API 提供支持的数据库即服务。

[阅读更多](https://www.baeldung.com/cassandra-astra-stargate-dashboard)→

## [使用 Cassandra、Astra、REST 和 GraphQL 构建仪表板——记录状态更新](https://www.baeldung.com/cassandra-astra-rest-dashboard-updates)

使用 Cassandra 存储时间序列数据的示例。

[阅读更多](https://www.baeldung.com/cassandra-astra-rest-dashboard-updates)→

## [使用 Cassandra、Astra 和 CQL 构建仪表板——映射事件数据](https://www.baeldung.com/cassandra-astra-rest-dashboard-map)

了解如何根据存储在 Astra 数据库中的数据在交互式地图上显示事件。

[阅读更多](https://www.baeldung.com/cassandra-astra-rest-dashboard-map)→

## 2.Maven依赖

让我们从使用 Maven在pom.xml中定义依赖关系开始：

```xml
<dependency>
    <groupId>com.datastax.cassandra</groupId>
    <artifactId>cassandra-driver-core</artifactId>
    <version>2.1.9</version>
</dependency>
```

## 3. Cassandra 的配置

我们将在整个过程中使用 Java 风格的配置来配置 Cassandra 集成。

### 3.1. 主要配置(春季)

为此，我们将使用 Java 风格的配置。让我们从主配置类开始——当然是通过类级别的@Configuration注解驱动的：

```java
@Configuration
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Override
    protected String getKeyspaceName() {
        return "testKeySpace";
    }

    @Bean
    public CassandraClusterFactoryBean cluster() {
        CassandraClusterFactoryBean cluster = 
          new CassandraClusterFactoryBean();
        cluster.setContactPoints("127.0.0.1");
        cluster.setPort(9142);
        return cluster;
    }

    @Bean
    public CassandraMappingContext cassandraMapping() 
      throws ClassNotFoundException {
        return new BasicCassandraMappingContext();
    }
}
```

请注意新 bean – BasicCassandraMappingContext – 具有默认实现。这是在对象和持久格式之间映射持久实体所必需的。

由于默认实现足够强大，我们可以直接使用它。

### 3.2. 主要配置(Spring Boot)

让我们通过application.properties进行 Cassandra 配置：

```plaintext
spring.data.cassandra.keyspace-name=testKeySpace
spring.data.cassandra.port=9142
spring.data.cassandra.contact-points=127.0.0.1
```

我们完成了！这就是我们在使用 Spring Boot 时所需要的。

### 3.3. Cassandra 连接属性

我们必须配置三个强制设置来为 Cassandra 客户端设置连接。

我们必须设置运行 Cassandra 服务器的主机名作为联系点。端口只是服务器中请求的监听端口。KeyspaceName是定义节点上数据的命名空间，它基于一个Cassandra 相关的概念。

## 4. Cassandra 存储库

我们将使用CassandraRepository作为数据访问层。这遵循 Spring Data 存储库抽象，它专注于抽象出跨不同持久性机制实现数据访问层所需的代码。

### 4.1. 创建Cassandra 存储库

让我们创建要在配置中使用的CassandraRepository：

```java
@Repository
public interface BookRepository extends CassandraRepository<Book> {
    //
}
```

### 4.2. CassandraRepository的配置

现在，我们可以扩展 3.1 节中的配置，在 CassandraConfig 中添加@EnableCassandraRepositories类级别注解来标记我们在 4.1 节中创建的 Cassandra Repository ：

```java
@Configuration
@EnableCassandraRepositories(
  basePackages = "com.baeldung.spring.data.cassandra.repository")
public class CassandraConfig extends AbstractCassandraConfiguration {
    //
}
```

## 5.实体

让我们快速浏览一下实体——我们将要使用的模型类。该类被注解并定义了用于在嵌入式模式下创建元数据 Cassandra 数据表的附加参数。

使用@Table注解，bean 直接映射到 Cassandra 数据表。每个属性也被定义为一种主键或一个简单的列：

```java
@Table
public class Book {
    @PrimaryKeyColumn(
      name = "isbn", 
      ordinal = 2, 
      type = PrimaryKeyType.CLUSTERED, 
      ordering = Ordering.DESCENDING)
    private UUID id;
    @PrimaryKeyColumn(
      name = "title", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String title;
    @PrimaryKeyColumn(
      name = "publisher", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String publisher;
    @Column
    private Set<String> tags = new HashSet<>();
    // standard getters and setters
}
```

## 6. 使用嵌入式服务器进行测试

### 6.1. Maven 依赖项

如果你想在嵌入式模式下运行 Cassandra(无需手动安装单独的 Cassandra 服务器)，你需要在pom.xml中添加cassandra-unit相关依赖项：

```xml
<dependency>
    <groupId>org.cassandraunit</groupId>
    <artifactId>cassandra-unit-spring</artifactId>
    <version>2.1.9.2</version>
    <scope>test</scope>
    <exclusions>
        <exclusion>
        <groupId>org.cassandraunit</groupId>
        <artifactId>cassandra-unit</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.cassandraunit</groupId>
    <artifactId>cassandra-unit-shaded</artifactId>
    <version>2.1.9.2</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.hectorclient</groupId>
    <artifactId>hector-core</artifactId>
    <version>2.0-0</version>
</dependency>
```

可以使用嵌入式 Cassandra 服务器来测试此应用程序。主要优点是你不想显式安装 Cassandra。

该嵌入式服务器也与 Spring JUnit 测试兼容。在这里，我们可以使用@RunWith注解和嵌入式服务器一起设置SpringJUnit4ClassRunner 。因此，无需运行外部 Cassandra 服务即可实现完整的测试套件。

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CassandraConfig.class)
public class BookRepositoryIntegrationTest {
    //
}
```

### 6.2. 启动和停止服务器

如果你运行的是外部 Cassandra 服务器，则可以忽略此部分。

我们必须为整个测试套件启动一次服务器，因此服务器启动方法用@BeforeClass注解标记：

```java
@BeforeClass
public static void startCassandraEmbedded() { 
    EmbeddedCassandraServerHelper.startEmbeddedCassandra(); 
    Cluster cluster = Cluster.builder()
      .addContactPoints("127.0.0.1").withPort(9142).build();
    Session session = cluster.connect(); 
}
```

接下来我们必须确保服务器在测试套件执行完成后停止：

```java
@AfterClass
public static void stopCassandraEmbedded() {
    EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
}
```

### 6.3. 清理数据表

最好在每次测试执行之前删除并创建数据表，以避免由于早期测试执行中的数据操作而导致意外结果。

现在我们可以在服务器启动时创建数据表：

```java
@Before
public void createTable() {
    adminTemplate.createTable(
      true, CqlIdentifier.cqlId(DATA_TABLE_NAME), 
      Book.class, new HashMap<String, Object>());
}
```

并在每次执行单个测试用例后删除：

```java
@After
public void dropTable() {
    adminTemplate.dropTable(CqlIdentifier.cqlId(DATA_TABLE_NAME));
}
```

## 7. 使用CassandraRepository访问数据

我们可以直接使用上面创建的BookRepository来持久化、操作和获取 Cassandra 数据库中的数据。

### 7.1. 保存一本新书

我们可以将一本新书保存到我们的书店：

```java
Book javaBook = new Book(
  UUIDs.timeBased(), "Head First Java", "O'Reilly Media", 
  ImmutableSet.of("Computer", "Software"));
bookRepository.save(ImmutableSet.of(javaBook));
```

然后我们可以检查数据库中插入的书的可用性：

```java
Iterable<Book> books = bookRepository.findByTitleAndPublisher(
  "Head First Java", "O'Reilly Media");
assertEquals(javaBook.getId(), books.iterator().next().getId());
```

### 7.2. 更新现有书籍

Lat 首先插入一本新书：

```java
Book javaBook = new Book(
  UUIDs.timeBased(), "Head First Java", "O'Reilly Media", 
  ImmutableSet.of("Computer", "Software"));
bookRepository.save(ImmutableSet.of(javaBook));
```

让我们按书名取书：

```java
Iterable<Book> books = bookRepository.findByTitleAndPublisher(
  "Head First Java", "O'Reilly Media");
```

那我们改一下书名：

```java
javaBook.setTitle("Head First Java Second Edition");
bookRepository.save(ImmutableSet.of(javaBook));
```

最后让我们检查标题是否在数据库中更新：

```java
Iterable<Book> books = bookRepository.findByTitleAndPublisher(
  "Head First Java Second Edition", "O'Reilly Media");
assertEquals(
  javaBook.getTitle(), updateBooks.iterator().next().getTitle());
```

### 7.3. 删除现有书籍

插入一本新书：

```java
Book javaBook = new Book(
  UUIDs.timeBased(), "Head First Java", "O'Reilly Media",
  ImmutableSet.of("Computer", "Software"));
bookRepository.save(ImmutableSet.of(javaBook));
```

然后删除新输入的书：

```java
bookRepository.delete(javaBook);

```

现在我们可以检查删除：

```java
Iterable<Book> books = bookRepository.findByTitleAndPublisher(
  "Head First Java", "O'Reilly Media");
assertNotEquals(javaBook.getId(), books.iterator().next().getId());
```

这将导致从代码中抛出一个 NoSuchElementException 以确保该书已被删除。

### 7.4. 查找所有书籍

先插入一本新书：

```java
Book javaBook = new Book(
  UUIDs.timeBased(), "Head First Java", "O'Reilly Media", 
  ImmutableSet.of("Computer", "Software"));
Book dPatternBook = new Book(
  UUIDs.timeBased(), "Head Design Patterns","O'Reilly Media",
  ImmutableSet.of("Computer", "Software"));
bookRepository.save(ImmutableSet.of(javaBook));
bookRepository.save(ImmutableSet.of(dPatternBook));
```

查找所有书籍：

```java
Iterable<Book> books = bookRepository.findAll();
```

然后我们可以检查数据库中可用书籍的数量：

```java
int bookCount = 0;
for (Book book : books) bookCount++;
assertEquals(bookCount, 2);
```

## 八、总结

我们使用最常见的方法使用CassandraRepository数据访问机制，通过 Spring 数据对 Cassandra 进行了基本的实践介绍。