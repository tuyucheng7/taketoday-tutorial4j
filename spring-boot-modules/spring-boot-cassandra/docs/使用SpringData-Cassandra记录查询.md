## 1. 概述

Apache Cassandra是一个**可扩展的分布式NoSQL数据库**。Cassandra在节点之间流式传输数据，并提供无单点故障的连续可用性。事实上，Cassandra能够以卓越的性能处理大量数据。

在开发使用数据库的应用程序时，能够记录和调试已执行的查询非常重要。在本教程中，我们将研究在将Apache Cassandra与Spring Boot结合使用时如何记录查询和语句。

在我们的示例中，我们将使用Spring Data Repository抽象和[Testcontainers](https://www.baeldung.com/spring-boot-testcontainers-integration-test)库。我们将看到如何通过Spring配置来配置Cassandra查询日志记录。此外，我们将探索Datastax请求记录器。我们可以配置此内置组件以进行更高级的日志记录。

## 2. 搭建测试环境

为了演示查询日志记录，我们需要设置一个测试环境。首先，我们将使用[Spring Data Apache Cassandra](https://www.baeldung.com/spring-data-cassandra-tutorial)设置测试数据。接下来，我们将使用[Testcontainers](https://www.baeldung.com/spring-boot-testcontainers-integration-test)库来运行Cassandra数据库容器以进行集成测试。

### 2.1 Cassandra Repository

Spring Data使我们能够基于通用的Spring接口创建Cassandra Repository。首先，让我们从定义一个简单的DAO类开始：

```java
@Table
public class Person {

    @PrimaryKey
    private UUID id;
    private String firstName;
    private String lastName;

    public Person(UUID id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // getters, setters, equals and hash code
}
```

然后，我们将通过扩展CassandraRepository接口为我们的DAO定义一个Spring Data Repository：

```java
@Repository
public interface PersonRepository extends CassandraRepository<Person, UUID> {}
```

最后，我们将在application.properties文件中添加两个属性：

```properties
spring.data.cassandra.schema-action=create_if_not_exists
spring.data.cassandra.local-datacenter=datacenter1
```

因此，Spring Data会自动为我们创建带注解的表。

我们应该注意，不建议将create_if_not_exists选项用于生产系统。

作为替代方案，可以通过从标准根类路径加载[schema.sql脚本](https://www.baeldung.com/spring-boot-data-sql-and-schema-sql)来创建表。

### 2.2 Cassandra容器

下一步，让我们在特定端口上配置和公开Cassandra容器：

```java
@Container
public static final CassandraContainer cassandra = (CassandraContainer) new CassandraContainer("cassandra:3.11.2").withExposedPorts(9042);
```

在使用容器进行集成测试之前，我们需要覆盖Spring Data建立连接所需的[测试属性](https://www.baeldung.com/spring-tests-override-properties)：

```java
TestPropertyValues.of(
    "spring.data.cassandra.keyspace-name=" + KEYSPACE_NAME,
    "spring.data.cassandra.contact-points=" + cassandra.getContainerIpAddress(),
    "spring.data.cassandra.port=" + cassandra.getMappedPort(9042)
).applyTo(configurableApplicationContext.getEnvironment());

createKeyspace(cassandra.getCluster());
```

最后，在创建任何对象/表之前，我们需要**创建一个Cassandra键空间**。键空间类似于RDBMS中的数据库。

### 2.3 集成测试

现在，我们已经准备就绪，可以开始编写集成测试了。

我们**对记录选择、插入和删除查询感兴趣**。因此，我们将编写几个测试来触发这些不同类型的查询。

首先，我们将编写一个用于保存和更新Person的测试。我们希望此测试执行两次插入和一次选择数据库查询：

```java
@Test
void givenExistingPersonRecord_whenUpdatingIt_thenRecordIsUpdated() {
    UUID personId = UUIDs.timeBased();
    Person existingPerson = new Person(personId, "Luka", "Modric");
    personRepository.save(existingPerson);
    existingPerson.setFirstName("Marko");
    personRepository.save(existingPerson);

    List<Person> savedPersons = personRepository.findAllById(List.of(personId));
    assertThat(savedPersons.get(0).getFirstName()).isEqualTo("Marko");
}
```

然后，我们将编写一个用于保存和删除现有Person的测试。我们希望此测试执行一次插入、删除和选择数据库查询：

```java
@Test
void givenExistingPersonRecord_whenDeletingIt_thenRecordIsDeleted() {
    UUID personId = UUIDs.timeBased();
    Person existingPerson = new Person(personId, "Luka", "Modric");

    personRepository.delete(existingPerson);

    List<Person> savedPersons = personRepository.findAllById(List.of(personId));
    assertThat(savedPersons.isEmpty()).isTrue();
}
```

默认情况下，我们不会观察到控制台中记录的任何数据库查询。

## 3. Spring Data CQL日志记录

使用Spring Data Apache Cassandra 2.0或更高版本，可以**在application.properties中为CqlTemplate类设置日志级别**：

```properties
logging.level.org.springframework.data.cassandra.core.cql.CqlTemplate=DEBUG
```

因此，通过将日志级别设置为DEBUG，我们可以启用所有已执行查询和预准备语句的日志记录：

```shell
2021-09-25 12:41:58.679 DEBUG 17856 --- [           main] o.s.data.cassandra.core.cql.CqlTemplate:
  Executing CQL statement [CREATE TABLE IF NOT EXISTS person
  (birthdate date, firstname text, id uuid, lastname text, lastpurchaseddate timestamp, lastvisiteddate timestamp, PRIMARY KEY (id));]
2021-09-25 12:42:01.204 DEBUG 17856 --- [           main] o.s.data.cassandra.core.cql.CqlTemplate:
  Preparing statement [INSERT INTO person (birthdate,firstname,id,lastname,lastpurchaseddate,lastvisiteddate)
  VALUES (?,?,?,?,?,?)] using org.springframework.data.cassandra.core.CassandraTemplate$PreparedStatementHandler@4d16975b
2021-09-25 12:42:01.253 DEBUG 17856 --- [           main] o.s.data.cassandra.core.cql.CqlTemplate:
  Executing prepared statement [INSERT INTO person (birthdate,firstname,id,lastname,lastpurchaseddate,lastvisiteddate) VALUES (?,?,?,?,?,?)]
2021-09-25 12:42:01.279 DEBUG 17856 --- [           main] o.s.data.cassandra.core.cql.CqlTemplate:
  Preparing statement [INSERT INTO person (birthdate,firstname,id,lastname,lastpurchaseddate,lastvisiteddate)
  VALUES (?,?,?,?,?,?)] using org.springframework.data.cassandra.core.CassandraTemplate$PreparedStatementHandler@539dd2d0
2021-09-25 12:42:01.290 DEBUG 17856 --- [           main] o.s.data.cassandra.core.cql.CqlTemplate:
  Executing prepared statement [INSERT INTO person (birthdate,firstname,id,lastname,lastpurchaseddate,lastvisiteddate) VALUES (?,?,?,?,?,?)]
2021-09-25 12:42:01.351 DEBUG 17856 --- [           main] o.s.data.cassandra.core.cql.CqlTemplate:
  Preparing statement [SELECT * FROM person WHERE id IN (371bb4a0-1ded-11ec-8cad-934f1aec79e6)]
  using org.springframework.data.cassandra.core.CassandraTemplate$PreparedStatementHandler@3e61cffd
2021-09-25 12:42:01.370 DEBUG 17856 --- [           main] o.s.data.cassandra.core.cql.CqlTemplate:
  Executing prepared statement [SELECT * FROM person WHERE id IN (371bb4a0-1ded-11ec-8cad-934f1aec79e6)]
```

不幸的是，使用这个解决方案，我们将看不到语句中使用的绑定值的输出。

## 4. Datastax请求跟踪器

**[DataStax](https://www.baeldung.com/datastax-post)请求跟踪器是一个会话范围的组件，用于接收有关每个Cassandra请求结果的通知**。

Apache Cassandra的DataStax Java驱动程序带有一个可选的请求跟踪器实现，用于记录所有请求。

### 4.1 Noop请求跟踪器

默认请求跟踪器实现称为NoopRequestTracker。因此，它什么都不做：

```java
System.setProperty("datastax-java-driver.advanced.request-tracker.class", "NoopRequestTracker");
```

要设置不同的跟踪器，我们应该在Cassandra配置中或通过系统属性指定一个实现RequestTracker的类。

### 4.2 请求记录器

**RequestLogger是RequestTracker的内置实现，用于记录每个请求**。

我们可以通过设置特定的DataStax Java驱动程序系统属性来启用它：

```java
System.setProperty("datastax-java-driver.advanced.request-tracker.class", "RequestLogger");
System.setProperty("datastax-java-driver.advanced.request-tracker.logs.success.enabled", "true");
System.setProperty("datastax-java-driver.advanced.request-tracker.logs.slow.enabled", "true");
System.setProperty("datastax-java-driver.advanced.request-tracker.logs.error.enabled", "true");
```

在此示例中，我们启用了所有成功、缓慢和失败请求的日志记录。

现在，当我们运行测试时，我们将在日志中观察所有已执行的数据库查询：

```shell
2021-09-25 13:06:31.799  INFO 11172 --- [        s0-io-4] c.d.o.d.i.core.tracker.RequestLogger:
  [s0|90232530][Node(endPoint=localhost/[0:0:0:0:0:0:0:1]:49281, hostId=c50413d5-03b6-4037-9c46-29f0c0da595a, hashCode=68c305fe)]
  Success (6 ms) [6 values] INSERT INTO person (birthdate,firstname,id,lastname,lastpurchaseddate,lastvisiteddate)
  VALUES (?,?,?,?,?,?) [birthdate=NULL, firstname='Luka', id=a3ad6890-1df0-11ec-a295-7d319da1858a, lastname='Modric', lastpurchaseddate=NULL, lastvisiteddate=NULL]
2021-09-25 13:06:31.811  INFO 11172 --- [        s0-io-4] c.d.o.d.i.core.tracker.RequestLogger:
  [s0|778232359][Node(endPoint=localhost/[0:0:0:0:0:0:0:1]:49281, hostId=c50413d5-03b6-4037-9c46-29f0c0da595a, hashCode=68c305fe)]
  Success (4 ms) [6 values] INSERT INTO person (birthdate,firstname,id,lastname,lastpurchaseddate,lastvisiteddate)
  VALUES (?,?,?,?,?,?) [birthdate=NULL, firstname='Marko', id=a3ad6890-1df0-11ec-a295-7d319da1858a, lastname='Modric', lastpurchaseddate=NULL, lastvisiteddate=NULL]
2021-09-25 13:06:31.847  INFO 11172 --- [        s0-io-4] c.d.o.d.i.core.tracker.RequestLogger:
  [s0|1947131919][Node(endPoint=localhost/[0:0:0:0:0:0:0:1]:49281, hostId=c50413d5-03b6-4037-9c46-29f0c0da595a, hashCode=68c305fe)]
  Success (5 ms) [0 values] SELECT * FROM person WHERE id IN (a3ad6890-1df0-11ec-a295-7d319da1858a)
```

我们将看到所有请求都记录在com.datastax.oss.driver.internal.core.tracker.RequestLogger类下。

此外，**语句中使用的所有绑定值也会默认记录下来**。

### 4.3 绑定值

**内置的RequestLogger是一个高度可定制的组件**。我们可以使用以下系统属性配置绑定值的输出：

```java
System.setProperty("datastax-java-driver.advanced.request-tracker.logs.show-values", "true");
System.setProperty("datastax-java-driver.advanced.request-tracker.logs.max-value-length", "100");
System.setProperty("datastax-java-driver.advanced.request-tracker.logs.max-values", "100");
```

如果值的格式化表示比max-value-length属性定义的值长，则该值的格式化表示将被截断。

使用max-values属性，我们可以定义要记录的绑定值的最大数量。

### 4.4 其他选项

在我们的第一个示例中，我们启用了慢速请求的日志记录。我们可以**使用threshold属性将成功的请求归类为慢**：

```java
System.setProperty("datastax-java-driver.advanced.request-tracker.logs.slow.threshold ", "1 second");
```

默认情况下，会记录所有失败请求的堆栈跟踪。如果我们禁用它们，我们只会在日志中看到异常的字符串表示形式：

```java
System.setProperty("datastax-java-driver.advanced.request-tracker.logs.show-stack-trace", "true");
```

成功和缓慢的请求使用INFO日志级别。另一方面，失败的请求使用ERROR级别。

## 5. 总结

在本文中，我们探讨了将Apache Cassandra与Spring Boot结合使用时查询和语句的日志记录。

在示例中，我们介绍了在Spring Data Apache Cassandra中配置日志级别。我们看到Spring Data会记录查询，但不会记录绑定值。最后，我们探索了Datastax请求跟踪器。它是一个高度可定制的组件，我们可以使用它来记录Cassandra查询及其绑定值。