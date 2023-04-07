## **一、概述**

通常，我们使用Spring的自动配置系统如*@SpringBootTest*来测试Spring Boot应用。但这**会导致大量导入自动配置的组件。**

但是，它总是有助于只加载所需的部分来测试应用程序的一部分。**为此，Spring Boot 提供了很多用于切片测试的注解。**最重要的是，这些 Spring 注释中的每一个都加载了一组非常有限的自动配置组件，这些组件是特定层所需的。

在本教程中，我们将重点测试 Spring Boot 应用程序的 Cassandra 数据库切片，以了解**Spring****提供的*****@DataCassandraTest注解。\***

此外，我们还将查看一个基于 Cassandra 的小型 Spring Boot 应用程序。 

而且，如果您在生产环境中运行 Cassandra，您绝对可以省去运行和维护自己服务器的复杂性，转而使用 Astra 数据库[，](https://www.baeldung.com/datastax-post)这是**一个基于 Apache Cassandra 构建的基于云的数据库。**

## **2.Maven依赖**

要在我们的 Cassandra Spring Boot 应用程序中使用*@DataCassandraTest*注解，我们必须添加[*spring-boot-starter-test*](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-test)依赖项：

```yaml
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-test</artifactId>
    <version>2.5.3</version>
    <scope>test</scope>
</dependency>复制
```

Spring 的[*spring-boot-test-autoconfigure是*](https://search.maven.org/search?q=a:spring-boot-test-autoconfigure)[*spring-boot-starter-test*](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-test)库的一部分，它包含许多用于测试应用程序不同部分的自动配置组件。

***通常，此测试注释具有@XXXTest\*****的模式****。**

*@DataCassandraTest*注解导入了以下 Spring 数据自动配置组件：

-   *缓存自动配置*
-   *Cassandra自动配置*
-   *Cassandra 数据自动配置*
-   *CassandraReactiveData自动配置*
-   *CassandraReactiveRepositoriesAutoConfiguration*
-   *CassandraRepositoriesAutoConfiguration*

## **3. Cassandra Spring Boot 应用示例**

为了说明这些概念，我们有一个简单的 Spring Boot Web 应用程序，其主要领域是车辆库存。

**为简单起见，此应用程序提供了****库存数据的基本*****CRUD操作。\***

### **3.1. Cassandra Maven 依赖**

Spring为Cassandra数据提供了[*spring-boot-starter-data-cassandra模块：*](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-data-cassandra)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-cassandra</artifactId>
    <version>2.5.3</version>
</dependency>复制
```

我们还需要依赖 Datastax Cassandra 的[*java-driver-core*](https://search.maven.org/artifact/com.datastax.oss/java-driver-core)来启用集群连接和请求执行：

```xml
<dependency> 
    <groupId>com.datastax.oss</groupId> 
    <artifactId>java-driver-core</artifactId> 
    <version>4.13.0</version> 
</dependency>复制
```

### **3.2. 卡桑德拉配置**

这里我们的*CassandraConfig*类扩展了 Spring 的*AbstractCassandraConfiguration*，它是 Spring Data Cassandra 配置的基类。

**这个 spring 类用于使用*****CqlSession\*****配置 Cassandra 客户端应用程序以连接到 Cassandra 集群。**

此外，我们可以配置键空间名称和集群主机：

```java
@Configuration
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Override
    protected String getKeyspaceName() {
        return "inventory";
    }

    @Override
    public String getContactPoints() {
        return "localhost";
    }

    @Override
    protected String getLocalDataCenter() {
         return "datacenter1";
    }
}复制
```

## **4. 数据模型**

以下[Cassandra 查询语言 ( ](https://www.baeldung.com/datastax-docs-keyspace)[*CQL*](https://www.baeldung.com/datastax-docs-keyspace)[ )通过名称](https://www.baeldung.com/datastax-docs-keyspace)*inventory*创建 Cassandra 键空间：

```sql
CREATE KEYSPACE inventory
WITH replication = {
    'class' : 'NetworkTopologyStrategy',
    'datacenter1' : 3
};复制
```

这个[*CQL*](https://www.baeldung.com/datastax-docs-spk)在*库存键空间中按名称**vehicles*创建一个 Cassandra 表：

```sql
use inventory;

CREATE TABLE vehicles (
   vin text PRIMARY KEY,
   year int,
   make varchar,
   model varchar
);复制
```

## **5.** ***@DataCassandraTest\*****用法**

让我们看看如何在单元测试中使用*@DataCassandraTest*来测试应用程序的数据层。

**@DataCassandraTest注释导入所需的 Cassandra 自动*****配置\*****模块，其中包括扫描*****@Table\*****和*****@Repository\*****组件。**因此，它可以*@Autowire* Repository*类*。

但是，**它不会扫描和导入常规的*****@Component\*****和*****@ConfigurationProperties\*** **bean。**

*此外，对于 JUnit 4，此注释应与@RunWith(SpringRunner.class)*结合使用。

### **5.1. 集成测试类**

这是带有*@DataCassandraTest*注释和存储库@Autowired 的*InventoryServiceIntegrationTest类**：*

```java
@RunWith(SpringRunner.class)
@DataCassandraTest
@Import(CassandraConfig.class)
public class InventoryServiceIntegrationTest {

    @Autowired
    private InventoryRepository repository;

    @Test
    public void givenVehiclesInDBInitially_whenRetrieved_thenReturnAllVehiclesFromDB() {
        List<Vehicle> vehicles = repository.findAllVehicles();

        assertThat(vehicles).isNotNull();
        assertThat(vehicles).isNotEmpty();
    }
}复制
```

我们还在上面添加了一个简单的测试方法。

为了更轻松地运行此测试，我们将使用 DockerCompose 测试容器，它设置了一个三节点 Cassandra 集群： 

```java
public class InventoryServiceLiveTest {

    // ...

    public static DockerComposeContainer container =
            new DockerComposeContainer(new File("src/test/resources/compose-test.yml"));

    @BeforeAll
    static void beforeAll() {
        container.start();
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }
}复制
```

[您可以在此处的](https://github.com/Baeldung/datastax-cassandra/blob/main/spring-data-cassandra-test/src/test/resources/compose-test.yml)GitHub 项目中找到 compose-test.yml 文件。

### **5.2. 存储库类**

示例存储库类*InventoryRepository*定义了一些自定义*JPA*方法：

```java
@Repository
public interface InventoryRepository extends CrudRepository<Vehicle, String> {

    @Query("select * from vehicles")
    List<Vehicle> findAllVehicles();

    Optional<Vehicle> findByVin(@Param("vin") String vin);

    void deleteByVin(String vin);
}复制
```

*我们还将通过将此属性添加到application.yml*文件来定义“local-quorum”的一致性级别，这意味着强一致性：

```yaml
spring:
  data:
    cassandra:
      request:
        consistency: local-quorum复制
```

## **6.其他*****@DataXXXTest\*****注解**

以下是一些其他类似的注释，用于测试任何应用程序的数据库层：

-   *@DataJpaTest*导入*JPA*存储库、*@Entity*类等。
-   *@DataJdbcTest*导入 Spring Data 存储库、JdbcTemplate 等。
-   *@DataMongoTest*导入 Spring Data MongoDB 存储库、Mongo 模板和*@Document*类
-   *@DataNeo4jTest*导入 Spring Data Neo4j 存储库，*@Node*类
-   *@DataRedisTest*导入 Spring Data Redis 存储库，*@RedisHash*

请访问[Spring 文档](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.testing.spring-boot-applications.autoconfigured-tests)以获取更多测试注释和详细信息。

## **七、结论**

在本文中，我们了解了*@DataCassandraTest*注解如何加载一些 Spring Data Cassandra 自动配置组件。因此，它避免加载许多不需要的 Spring 上下文模块。

一如既往，完整的源代码可[在 GitHub 上](https://github.com/Baeldung/datastax-cassandra/tree/main/spring-data-cassandra-test)获得。