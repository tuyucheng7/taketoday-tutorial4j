## 1. 概述

通常，我们使用Spring的自动配置系统(如@SpringBootTest)来测试Spring Boot应用程序。但**这会导致大量导入自动配置的组件**。

但是，仅加载测试应用程序切片所需的部件始终会有所帮助。**为此，Spring Boot提供了很多用于切片测试的注解**。最重要的是，这些Spring注解中的每一个都加载了一组非常有限的自动配置组件，这些组件是特定层所需的。

在本教程中，我们将重点测试Spring Boot应用程序的Cassandra数据库切片，以**了解Spring提供的@DataCassandraTest注解**。

此外，我们还将查看一个基于Cassandra的小型Spring Boot应用程序。 

而且，如果你在生产环境中运行Cassandra，你绝对可以省去运行和维护自己服务器的复杂性，转而使用[Astra数据库](https://www.baeldung.com/datastax-post)，这是**一个基于Apache Cassandra构建的基于云的数据库**。

## 2. Maven依赖

要在我们的Cassandra Spring Boot应用程序中使用@DataCassandraTest注解，我们必须添加[spring-boot-starter-test](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-test)依赖项：

```xml
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-test</artifactId>
    <version>2.5.3</version>
    <scope>test</scope>
</dependency>
```

Spring的[spring-boot-test-autoconfigure](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-test-autoconfigure/3.0.6)是[spring-boot-starter-test](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-test)库的一部分，它包含许多用于测试应用程序不同部分的自动配置组件。

**通常，此测试注解具有@XXXTest的模式**。

@DataCassandraTest注解导入了以下Spring Data自动配置组件：

-   CacheAutoConfiguration
-   CassandraAutoConfiguration
-   CassandraDataAutoConfiguration
-   CassandraReactiveDataAutoConfiguration
-   CassandraReactiveRepositoriesAutoConfiguration
-   CassandraRepositoriesAutoConfiguration

## 3. Cassandra Spring Boot应用示例

为了说明这些概念，我们有一个简单的Spring Boot Web应用程序，其主要域是车辆库存。

**为简单起见，此应用程序提供了库存数据的基本CRUD操作**。

### 3.1 Cassandra Maven依赖

Spring为Cassandra Data提供了[spring-boot-starter-data-cassandra](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-data-cassandra)模块：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-cassandra</artifactId>
    <version>2.5.3</version>
</dependency>
```

我们还需要依赖Datastax Cassandra的[java-driver-core](https://search.maven.org/artifact/com.datastax.oss/java-driver-core)来启用集群连接和请求执行：

```xml
<dependency> 
    <groupId>com.datastax.oss</groupId> 
    <artifactId>java-driver-core</artifactId> 
    <version>4.13.0</version> 
</dependency>
```

### 3.2 Cassandra配置

在这里，我们的CassandraConfig类扩展了Spring的AbstractCassandraConfiguration，它是Spring Data Cassandra配置的基类。

**这个Spring类用于使用CqlSession配置Cassandra客户端应用程序以连接到Cassandra集群**。

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
}
```

## 4. 数据模型

以下[Cassandra查询语言(CQL)](https://www.baeldung.com/datastax-docs-keyspace)通过名称inventory创建Cassandra键空间：

```cassandraql
CREATE KEYSPACE inventory
    WITH replication = {
        'class' : 'NetworkTopologyStrategy',
        'datacenter1' : 3
        };
```

这个[CQL](https://www.baeldung.com/datastax-docs-spk)在inventory键空间中按名称vehicles创建一个Cassandra表：

```cassandraql
use inventory;

CREATE TABLE vehicles
(
    vin   text PRIMARY KEY,
    year  int,
    make  varchar,
    model varchar
);
```

## 5. @DataCassandraTest用法

让我们看看如何在单元测试中使用@DataCassandraTest来测试应用程序的数据层。

**@DataCassandraTest注解导入所需的Cassandra自动配置模块，其中包括扫描@Table和@Repository组件**。因此，可以@Autowire Repository类。

但是，**它不会扫描和导入常规的@Component和@ConfigurationProperties bean**。

此外，对于JUnit 4，此注解应与@RunWith(SpringRunner.class)结合使用。

### 5.1 集成测试类

这是带有@DataCassandraTest注解和@Autowired Repository的InventoryServiceIntegrationTest类：

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
}
```

我们还在上面添加了一个简单的测试方法。

为了更容易运行此测试，我们将使用DockerCompose测试容器，它设置了一个三节点Cassandra集群： 

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
}
```

你可以在[GitHub项目](https://github.com/tu-yucheng/taketoday-tutorial4j/blob/main/spring-boot-modules/spring-boot-cassandra-test/src/test/resources/compose-test.yml)上找到compose-test.yml文件。

### 5.2 Repository类

示例Repository类InventoryRepository定义了一些自定义JPA方法：

```java
@Repository
public interface InventoryRepository extends CrudRepository<Vehicle, String> {

    @Query("select * from vehicles")
    List<Vehicle> findAllVehicles();

    Optional<Vehicle> findByVin(@Param("vin") String vin);

    void deleteByVin(String vin);
}
```

我们还将通过将此属性添加到application.yml文件来定义“local-quorum”的一致性级别，这意味着强一致性：

```yaml
spring:
    data:
        cassandra:
            request:
                consistency: local-quorum
```

## 6. 其他@DataXXXTest注解

以下是一些其他类似的注解，用于测试任何应用程序的数据库层：

-   @DataJpaTest导入JPA Repository、@Entity类等
-   @DataJdbcTest导入Spring Data Repository、JdbcTemplate等
-   @DataMongoTest导入Spring Data MongoDB Repository、MongoTemplate和@Document类
-   @DataNeo4jTest导入Spring Data Neo4j Repository，@Node类
-   @DataRedisTest导入Spring Data Redis Repository，@RedisHash

请访问[Spring文档](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.testing.spring-boot-applications.autoconfigured-tests)以获取更多测试注解和详细信息。

## 7. 总结

在本文中，我们学习了@DataCassandraTest注解如何加载一些Spring Data Cassandra自动配置组件。因此，它避免加载许多不需要的Spring上下文模块。