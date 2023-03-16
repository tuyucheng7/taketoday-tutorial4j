## 1. 简介

[R2DBC](https://r2dbc.io/)(响应式关系型数据库连接)是Pivotal在Spring One Platform 2018期间推出的一项成果。它旨在为SQL数据库创建一个响应式API。

**换句话说，这项工作可以使用完全非阻塞的驱动程序创建一个数据库连接**。

在本教程中，我们将看一个使用Spring Data R2BDC的应用程序示例。有关更底层R2DBC API的指南，请查看我们[之前的文章](https://www.baeldung.com/r2dbc-operations)。

## 2. 我们的第一个Spring Data R2DBC项目

首先，R2DBC项目是最近才推出的。目前，只有**PostGres、MSSQL和H2具有R2DBC驱动程序**。此外，我们**不能使用它的所有Spring Boot功能**。因此，我们需要手动添加一些步骤。但是，**我们可以利用像[Spring Data](https://www.baeldung.com/spring-data)这样的项目来帮助我们**。

我们将首先创建一个Maven项目。此时，R2DBC存在一些依赖性问题，因此我们的pom.xml将比正常情况下更大。

对于本文的范围，我们将使用[H2](https://central.sonatype.com/artifact/com.h2database/h2/2.1.212)作为我们的数据库，并为我们的应用程序创建响应式CRUD函数。

让我们打开生成的项目的pom.xml并添加适当的依赖项以及一些早期发布的Spring库：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.data</groupId>
        <artifactId>spring-data-r2dbc</artifactId>
        <version>1.0.0.RELEASE</version>
    </dependency>
    <dependency>
        <groupId>io.r2dbc</groupId>
        <artifactId>r2dbc-h2</artifactId>
        <version>0.8.1.RELEASE</version>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>1.4.199</version>
    </dependency>
</dependencies>
```

其他所需的工件包括[Lombok](https://central.sonatype.com/artifact/org.projectlombok/lombok/1.18.26)、[Spring WebFlux](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-webflux/3.0.3)和其他一些完成我们项目[依赖项](https://github.com/rodrigolgraciano/tutorials/blob/master/spring-5-data-reactive/pom.xml)的工件。

## 3. 连接工厂

使用数据库时，我们需要一个连接工厂。所以，我们也需要同样的东西来使用R2DBC。

因此我们现在将添加详细信息以连接到我们的实例：

```java
@Configuration
@EnableR2dbcRepositories
class R2DBCConfiguration extends AbstractR2dbcConfiguration {
    @Bean
    public H2ConnectionFactory connectionFactory() {
        return new H2ConnectionFactory(
              H2ConnectionConfiguration.builder()
                    .url("mem:testdb;DB_CLOSE_DELAY=-1;")
                    .username("sa")
                    .build()
        );
    }
}
```

我们在上面的代码中首先要注意到的是@EnableR2dbcRepositories。我们需要这个注解来使用Spring Data功能。此外，**我们扩展了AbstractR2dbcConfiguration，因为它将提供我们稍后需要的大量bean**。

## 4. 我们的第一个R2DBC应用程序

我们的下一步是创建Repository：

```java
interface PlayerRepository extends ReactiveCrudRepository<Player, Integer> {}
```

**ReactiveCrudRepository接口非常有用。例如，它提供基本的CRUD功能**。

最后，我们将定义我们的模型类并使用Lombok来避免样板代码：

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
class Player {
    @Id
    Integer id;
    String name;
    Integer age;
}
```

## 5. 测试

是时候[测试](https://www.baeldung.com/reactive-streams-step-verifier-test-publisher)我们的代码了。那么，让我们从创建一些测试用例开始：

```java
@Test
public void whenDeleteAll_then0IsExpected() {
    playerRepository.deleteAll()
        .as(StepVerifier::create)
        .expectNextCount(0)
        .verifyComplete();
}

@Test
public void whenInsert6_then6AreExpected() {
    insertPlayers();
    playerRepository.findAll()
        .as(StepVerifier::create)
        .expectNextCount(6)
        .verifyComplete();
}
```

## 6. 自定义查询

**我们还可以生成自定义查询**。为了添加它，我们需要更改我们的PlayerRepository：

```java
@Query("select id, name, age from player where name = $1")
Flux<Player> findAllByName(String name);

@Query("select * from player where age = $1")
Flux<Player> findByAge(int age);
```

除了现有的测试之外，我们还将添加更新后Repository方法的相应测试：

```java
@Test
public void whenSearchForCR7_then1IsExpected() {
    insertPlayers();
    playerRepository.findAllByName("CR7")
        .as(StepVerifier::create)
        .expectNextCount(1)
        .verifyComplete();
}

@Test
public void whenSearchFor32YearsOld_then2AreExpected() {
    insertPlayers();
    playerRepository.findByAge(32)
        .as(StepVerifier::create)
        .expectNextCount(2)
        .verifyComplete();
}

private void insertPlayers() {
    List<Player> players = Arrays.asList(
        new Player(1, "Kaka", 37),
        new Player(2, "Messi", 32),
        new Player(3, "Mbappé", 20),
        new Player(4, "CR7", 34),
        new Player(5, "Lewandowski", 30),
        new Player(6, "Cavani", 32)
    );
    playerRepository.saveAll(players).subscribe();
}
```

## 7. 批处理

R2DBC的另一个特性是创建批处理。批处理在执行多个SQL语句时很有用，因为它们的性能比单个操作更好。

要创建Batch，我们需要一个Connection对象：

```java
Batch batch = connection.createBatch();
```

在我们的应用程序创建Batch实例后，我们可以添加任意数量的SQL语句。要执行它，我们将调用execute()方法。批处理的结果是一个Publisher，它将为每个语句返回一个结果对象。

因此，让我们跳入代码，看看如何创建Batch：

```java
@Test
public void whenBatchHas2Operations_then2AreExpected() {
    Mono.from(factory.create())
        .flatMapMany(connection -> Flux.from(connection
            .createBatch()
            .add("select * from player")
            .add("select * from player")
            .execute()))
        .as(StepVerifier::create)
        .expectNextCount(2)
        .verifyComplete();
}
```

## 8. 总结

总而言之，R2DBC仍处于早期阶段。这是创建一个SPI的尝试，该SPI将为SQL数据库定义一个响应式API。当与[Spring WebFlux](https://www.baeldung.com/spring-webflux)一起使用时，R2DBC允许我们编写一个应用程序，从顶部异步处理数据，一直到数据库。