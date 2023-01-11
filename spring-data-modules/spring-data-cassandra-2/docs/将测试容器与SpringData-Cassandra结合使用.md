## 1. 概述

[Apache Cassandra](https://www.baeldung.com/cassandra-with-java)是一个开源的分布式 NoSQL 数据库。它旨在处理大量数据，具有快速读写性能且无单点故障。

在本教程中，我们将着眼于测试使用 Cassandra 数据库的 Spring Boot 应用程序。[我们将解释如何使用Testcontainers](https://www.baeldung.com/spring-boot-testcontainers-integration-test)库中的 Cassandra 容器设置集成测试。此外，我们将利用 Spring Data 存储库抽象来处理 Cassandra 的数据层。

最后，我们将展示如何在多个集成测试中重用共享的 Cassandra 容器实例。

## 2. 测试容器

Testcontainers 是一个 Java 库，它提供轻量级、一次性的 Docker 容器实例。因此，我们通常在 Spring 中使用它来对使用数据库的应用程序进行集成测试。Testcontainers 使我们能够在真实的数据库实例上进行测试，而无需我们在本地计算机上安装和管理数据库。

### 2.1. Maven 依赖项

Cassandra 容器在[Cassandra Testcontainers 模块](https://www.testcontainers.org/modules/databases/cassandra/)中可用。这允许使用容器化的 Cassandra 实例。

与[cassandra-unit](https://www.baeldung.com/spring-data-cassandra-tutorial) 库不同， Testcontainers 库与 JUnit 5 完全兼容。让我们首先列出所需的 Maven 依赖项：

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.15.3</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>cassandra</artifactId>
    <version>1.15.3</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.15.3</version>
    <scope>test</scope>
<dependency>
```

### 2.2. 卡桑德拉容器

容器化数据库实例通常用于集成测试。以及确保我们的数据访问层代码与特定数据库版本完全兼容。

首先，我们需要用@SpringBootTest和@Testcontainers注解我们的测试类：

```java
@SpringBootTest
@Testcontainers
class CassandraSimpleIntegrationTest {}
```

然后，我们可以定义一个 Cassandra 容器并公开它的 特定端口：

```java
@Container
public static final CassandraContainer cassandra 
  = (CassandraContainer) new CassandraContainer("cassandra:3.11.2").withExposedPorts(9042);

```

在这里，我们公开了容器端口9042。但是，我们应该注意，Testcontainers 会将其链接到一个随机主机端口，稍后我们可以获取该端口。

使用以上内容，Testcontainers 库会自动为我们启动一个与测试类的生命周期保持一致的 dockerized Cassandra 容器实例：

```java
@Test
void givenCassandraContainer_whenSpringContextIsBootstrapped_thenContainerIsRunningWithNoExceptions() {
    assertThat(cassandra.isRunning()).isTrue();
}
```

现在我们有一个正在运行的 Cassandra 容器。但是，Spring 应用程序还不知道它。

### 2.3. 覆盖测试属性

为了让 Spring Data 能够与 Cassandra 容器建立连接，我们需要提供一些连接属性。我们将通过java.lang.System类定义系统属性来覆盖默认的 Cassandra 连接属性：

```java
@BeforeAll
static void setupCassandraConnectionProperties() {
    System.setProperty("spring.data.cassandra.keyspace-name", KEYSPACE_NAME);
    System.setProperty("spring.data.cassandra.contact-points", cassandra.getContainerIpAddress());
    System.setProperty("spring.data.cassandra.port", String.valueOf(cassandra.getMappedPort(9042)));
}
```

现在我们配置了 Spring Data 来连接我们的 Cassandra 容器。但是，我们仍然需要创建一个键空间。

### 2.4. 创建键空间

作为在 Cassandra 中创建任何表之前的最后一步，我们需要创建一个键空间：

```java
private static void createKeyspace(Cluster cluster) {
    try (Session session = cluster.connect()) {
        session.execute("CREATE KEYSPACE IF NOT EXISTS " + KEYSPACE_NAME +
          " WITH replication = n" +
          "{'class':'SimpleStrategy','replication_factor':'1'};");
    }
}
```

Cassandra 中的键空间与 RDBMS 中的数据库非常相似。它定义了数据如何在 Cassandra 集群的节点上。

## 3. Cassandra 的春季数据

[Apache Cassandra 的 Spring Data](https://drafts.baeldung.com/spring-data-cassandra-tutorial) 将核心 Spring 概念应用于使用 Cassandra 的应用程序开发。它为丰富的对象映射提供存储库、查询构建器和简单注解。因此，它为使用不同数据库的 Spring 开发人员提供了一个熟悉的界面。

### 3.1. 数据访问对象

让我们从准备一个简单的 DAO 类开始，稍后我们将在集成测试中使用它：

```java
@Table
public class Car {

    @PrimaryKey
    private UUID id;
    private String make;
    private String model;
    private int year;

    public Car(UUID id, String make, String model, int year) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
    }

    //getters, setters, equals and hashcode
}
```

这里的关键是使用org.springframework.data.cassandra.core.mapping包中的@Table注解来注解类。事实上，这个注解启用了自动域对象映射。

### 3.2. 卡桑德拉存储库

Spring Data 使得为我们的 DAO 创建存储库变得非常简单。首先，我们需要在 Spring Boot 主类中启用 Cassandra 存储库：

```java
@SpringBootApplication
@EnableCassandraRepositories(basePackages = "org.baeldung.springcassandra.repository")
public class SpringCassandraApplication {}
```

然后，我们只需要创建一个扩展CassandraRepository的接口：

```java
@Repository
public interface CarRepository extends CassandraRepository<Car, UUID> {}
```

在开始集成测试之前，我们需要定义两个附加属性：

```bash
spring.data.cassandra.local-datacenter=datacenter1
spring.data.cassandra.schema-action=create_if_not_exists
```

第一个属性定义默认的本地数据中心名称。第二个将确保 Spring Data 自动为我们创建所需的数据库表。我们应该注意，此设置不应在生产系统中使用。

由于我们使用的是 Testcontainer，因此我们无需担心在测试完成后删除表。每次我们运行测试时，都会为我们启动一个新容器。

## 4. 集成测试

现在我们已经设置了 Cassandra 容器、一个简单的 DAO 类和一个 Spring Data 存储库，我们可以开始编写集成测试了。

### 4.1. 保存记录测试

让我们从测试向 Cassandra 数据库中插入一条新记录开始：

```java
@Test
void givenValidCarRecord_whenSavingIt_thenRecordIsSaved() {
    UUID carId = UUIDs.timeBased();
    Car newCar = new Car(carId, "Nissan", "Qashqai", 2018);

    carRepository.save(newCar);

    List<Car> savedCars = carRepository.findAllById(List.of(carId));
    assertThat(savedCars.get(0)).isEqualTo(newCar);
}
```

### 4.2. 更新记录测试

然后，我们可以编写一个类似的测试来更新现有的数据库记录：

```java
@Test
void givenExistingCarRecord_whenUpdatingIt_thenRecordIsUpdated() {
    UUID carId = UUIDs.timeBased();
    Car existingCar = carRepository.save(new Car(carId, "Nissan", "Qashqai", 2018));

    existingCar.setModel("X-Trail");
    carRepository.save(existingCar);

    List<Car> savedCars = carRepository.findAllById(List.of(carId));
    assertThat(savedCars.get(0).getModel()).isEqualTo("X-Trail");
}
```

### 4.3. 删除记录测试

最后，让我们编写一个删除现有数据库记录的测试：

```java
@Test
void givenExistingCarRecord_whenDeletingIt_thenRecordIsDeleted() {
    UUID carId = UUIDs.timeBased();
    Car existingCar = carRepository.save(new Car(carId, "Nissan", "Qashqai", 2018));

    carRepository.delete(existingCar);

    List<Car> savedCars = carRepository.findAllById(List.of(carId));
    assertThat(savedCars.isEmpty()).isTrue();
}
```

## 5.共享容器实例

大多数时候，在进行集成测试时，我们希望在多个测试中重用同一个数据库实例。我们可以通过使用多个嵌套测试类来共享同一个容器实例：

```java
@Testcontainers
@SpringBootTest
class CassandraNestedIntegrationTest {

    private static final String KEYSPACE_NAME = "test";

    @Container
    private static final CassandraContainer cassandra 
      = (CassandraContainer) new CassandraContainer("cassandra:3.11.2").withExposedPorts(9042);

    // Set connection properties and create keyspace

    @Nested
    class ApplicationContextIntegrationTest {
        @Test
        void givenCassandraContainer_whenSpringContextIsBootstrapped_thenContainerIsRunningWithNoExceptions() {
            assertThat(cassandra.isRunning()).isTrue();
        }
    }

    @Nested
    class CarRepositoryIntegrationTest {

        @Autowired
        private CarRepository carRepository;

        @Test
        void givenValidCarRecord_whenSavingIt_thenRecordIsSaved() {
            UUID carId = UUIDs.timeBased();
            Car newCar = new Car(carId, "Nissan", "Qashqai", 2018);

            carRepository.save(newCar);

            List<Car> savedCars = carRepository.findAllById(List.of(carId));
            assertThat(savedCars.get(0)).isEqualTo(newCar);
        }

        // Tests for update and delete
    }
}
```

由于 Docker 容器需要时间来启动，多个嵌套测试类之间的共享容器实例将确保更快的执行。但是，我们应该注意，这个共享实例不会在测试之间自动清除。

## 六，总结

在本文中，我们探讨了使用 Cassandra 容器来测试使用 Cassandra 数据库的 Spring Boot 应用程序。

在示例中，我们介绍了设置一个 dockerized Cassandra 容器实例、覆盖测试属性、创建一个键空间、一个 DAO 类和一个 Cassandra 存储库接口。

我们看到了如何编写使用 Cassandra 容器的集成测试。因此我们的示例测试不需要模拟。最后，我们看到了如何在多个嵌套测试类中重用同一个容器实例。