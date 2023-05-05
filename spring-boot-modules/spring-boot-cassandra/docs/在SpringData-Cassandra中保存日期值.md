## 1. 概述

Apache Cassandra是一个可扩展的NoSQL数据库。它提供了**无单点故障的连续可用性**。此外，Cassandra能够以卓越的性能处理大量数据。

在本教程中，我们将介绍如何使用Spring Data和Docker连接到Cassandra。此外，我们将利用Spring Data Repository抽象来处理Cassandra的数据层。

我们将看到如何在Cassandra中保存不同的Java日期值。最后，我们将研究如何将这些日期值映射到Cassandra类型。

## 2. Spring Data Cassandra

[Spring Data Apache Cassandra](https://www.baeldung.com/spring-data-cassandra-tutorial)为Spring开发人员**提供了一个熟悉的接口来使用Cassandra**。该项目将核心Spring概念应用于使用Cassandra数据存储的解决方案的开发。

Spring Data允许我们基于通用的Spring接口创建Repository。它还允许使用QueryBuilder来消除学习Cassandra查询语言(CQL)的需要。该项目提供了支持丰富对象映射的简单注解。

有两个重要的辅助类：

-   CqlTemplate处理常见的数据访问操作
-   CassandraTemplate提供对象映射

该项目与Spring的核心JDBC支持有明显的相似之处。

## 3. 搭建测试环境

为了开始使用，我们需要建立到Cassandra实例的连接。

请注意，我们也可以改为连接到AstraDB数据库，这是一个基于Apache Cassandra构建的基于云的数据库。

本指南将向你展示如何[连接到Datastax Astra DB](https://www.baeldung.com/datastax-docs)。

### 3.1 Cassandra容器

让我们使用[Testcontainers](https://www.baeldung.com/docker-test-containers)库配置和启动Cassandra。首先，我们将定义一个Cassandra容器并将其公开到特定端口：

```java
@Container
public static final CassandraContainer cassandra = (CassandraContainer) new CassandraContainer("cassandra:3.11.2")
    .withExposedPorts(9042);
```

接下来，我们需要覆盖Spring Data所需的[测试属性](https://www.baeldung.com/spring-tests-override-properties)，以便能够与Cassandra容器建立连接：

```java
TestPropertyValues.of(
    "spring.data.cassandra.keyspace-name=" + KEYSPACE_NAME,
    "spring.data.cassandra.contact-points=" + cassandra.getContainerIpAddress(),
    "spring.data.cassandra.port=" + cassandra.getMappedPort(9042)
).applyTo(configurableApplicationContext.getEnvironment());
```

最后，在创建任何对象/表之前，我们需要创建一个键空间：

```java
session.execute("CREATE KEYSPACE IF NOT EXISTS " + KEYSPACE_NAME + " WITH replication = {'class':'SimpleStrategy','replication_factor':'1'};");
```

键空间只是Cassandra中的一个数据容器。实际上，它非常类似于RDBMS中的数据库。

### 3.2 Cassandra Repository

**Spring Data的[Repository支持](https://www.baeldung.com/spring-data-cassandra-tutorial)显著简化了DAO的实施**。让我们从创建一个简单的DAO开始。

org.springframework.data.cassandra.core.mapping包中提供的@Table注解启用域对象映射：

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

接下来，我们将通过扩展CassandraRepository接口为我们的DAO定义一个Spring Data Repository：

```java
@Repository
public interface PersonRepository extends CassandraRepository<Person, UUID> {}
```

最后，在开始编写集成测试之前，我们需要定义两个额外的属性：

```properties
spring.data.cassandra.schema-action=create_if_not_exists
spring.data.cassandra.local-datacenter=datacenter1
```

第一个属性将确保Spring Data自动为我们创建带注解的表。

我们应该注意，**不建议将此设置用于生产系统**。

## 4. 使用日期值

在Spring Data Apache Cassandra的现代版本中，使用日期值非常简单。**Spring Data将自动确保Java日期类型正确映射到Apache Cassandra表示形式**。

### 4.1 LocalDate类型

让我们向Person添加一个类型为LocalDate的新字段birthDate：

```java
@Test
public void givenValidPersonUsingLocalDate_whenSavingIt_thenDataIsPersisted() {
    UUID personId = UUIDs.timeBased();
    Person newPerson = new Person(personId, "Luka", "Modric");
    newPerson.setBirthDate(LocalDate.of(1985, 9, 9));
    personRepository.save(newPerson);

    List<Person> savedPersons = personRepository.findAllById(List.of(personId));
    assertThat(savedPersons.get(0)).isEqualTo(newPerson);
}
```

**Spring Data自动将Java的LocalDate转换为Cassandra的date类型**。在从Cassandra保存和获取记录后，DAO中的LocalDate值是相同的。

### 4.2 LocalDateTime类型

让我们向Person添加另一个名为lastVisitedDate的字段，类型为LocalDateTime：

```java
@Test
public void givenValidPersonUsingLocalDateTime_whenSavingIt_thenDataIsPersisted() {
    UUID personId = UUIDs.timeBased();
    Person newPerson = new Person(personId, "Luka", "Modric");
    newPerson.setLastVisitedDate(LocalDateTime.of(2021, 7, 13, 11, 30));
    personRepository.save(newPerson);

    List<Person> savedPersons = personRepository.findAllById(List.of(personId));
    assertThat(savedPersons.get(0)).isEqualTo(newPerson);
}
```

**Spring Data自动将Java的LocalDateTime转换为Cassandra的timestamp类型**。在从Cassandra保存和获取记录后，DAO中的LocalDateTime值是相同的。

### 4.3 旧日期类型

最后，让我们将遗留类型Date的名为lastPurchasedDate的字段添加到我们的Person：

```java
@Test
public void givenValidPersonUsingLegacyDate_whenSavingIt_thenDataIsPersisted() {
    UUID personId = UUIDs.timeBased();
    Person newPerson = new Person(personId, "Luka", "Modric");
    newPerson.setLastPurchasedDate(new Date(LocalDate.of(2021, 7, 13).toEpochDay()));
    personRepository.save(newPerson);

    List<Person> savedPersons = personRepository.findAllById(List.of(personId));
    assertThat(savedPersons.get(0)).isEqualTo(newPerson);
}
```

与LocalDateTime一样，**Spring Data将Java的java.util.Date转换为Cassandra的timestamp类型**。

### 4.4 映射的Cassandra类型

让我们使用CQLSH检查保存在Cassandra中的数据。它是一个命令行shell，用于通过CQL与Cassandra进行交互。

为了在测试执行期间检查Cassandra容器中存储了哪些数据，我们可以简单地在测试中放置一个断点。在暂停的测试执行期间，我们可以通过Docker Desktop应用程序连接到Docker容器CLI：

<img src="../assets/img.png">

连接到Docker容器CLI后，我们应该首先选择键空间，然后选择表：

```shell
# cqlsh
Connected to Test Cluster at 127.0.0.1:9042.
[cqlsh 5.0.1 | Cassandra 3.11.2 | CQL spec 3.4.4 | Native protocol v4]
Use HELP for help.
cqlsh> USE test;
cqlsh:test> select * from person;
```

结果，CQLSH将向我们显示保存在表中的数据的格式化输出：

```shell
 id                                   | birthdate  | firstname | lastname | lastpurchaseddate | lastvisiteddate
--------------------------------------+------------+-----------+----------+-------------------+-----------------
 9abef910-e3fd-11eb-9829-c5149ac796de | 1985-09-09 |      Luka |   Modric |              null |            null
```

但是，我们还想检查用于特定日期列的数据类型：

```shell
cqlsh:test> DESC TABLE person;
```

输出返回用于创建表的CQL命令。因此，它包含所有数据类型定义：

```shell
CREATE TABLE test.person (
    id uuid PRIMARY KEY,
    birthdate date,
    firstname text,
    lastname text,
    lastpurchaseddate timestamp,
    lastvisiteddate timestamp
)
```

## 5. 总结

在本文中，我们探讨了在Spring Data Apache Cassandra中使用不同的日期值。

在示例中，我们介绍了如何使用LocalDate、LocalDateTime和旧的Date Java类型。我们看到了如何连接到使用Testcontainers启动的Cassandra实例。最后，我们使用Spring Data Repository抽象来操作存储在Cassandra中的数据。