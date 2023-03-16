## 1. 概述

在本教程中，我们将学习如何使用Spring Data Repository在[Couchbase](https://www.baeldung.com/spring-data-couchbase)上以响应方式配置和实现数据库操作。

我们将介绍ReactiveCrudRepository和ReactiveSortingRepository的基本用法。此外，我们将使用AbstractReactiveCouchbaseConfiguration配置我们的测试应用程序。

## 2. Maven依赖

首先，让我们添加必要的依赖项：

```xml
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-couchbase-reactive</artifactId>
</dependency>
```

[spring-boot-starter-data-couchbase-reactive](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-data-couchbase-reactive/3.0.3)依赖项包含我们使用响应式API在Couchbase上操作所需的一切。

我们还将包含[reactor-core](https://central.sonatype.com/artifact/io.projectreactor/reactor-core/3.5.3)依赖项以使用Project Reactor API。

## 3. 配置

接下来，让我们定义Couchbase和我们的应用程序之间的连接设置。

让我们首先创建一个类来保存我们的属性：

```java
@Configuration
public class CouchbaseProperties {

    private List<String> bootstrapHosts;
    private String bucketName;
    private String bucketPassword;
    private int port;

    public CouchbaseProperties(
          @Value("${spring.couchbase.bootstrap-hosts}") List<String> bootstrapHosts,
          @Value("${spring.couchbase.bucket.name}") String bucketName,
          @Value("${spring.couchbase.bucket.password}") String bucketPassword,
          @Value("${spring.couchbase.port}") int port) {
        this.bootstrapHosts = Collections.unmodifiableList(bootstrapHosts);
        this.bucketName = bucketName;
        this.bucketPassword = bucketPassword;
        this.port = port;
    }

    // getters
}
```

为了使用响应式支持，我们应该创建扩展AbstractReactiveCouchbaseConfiguration的配置类：

```java
@Configuration
@EnableReactiveCouchbaseRepositories("cn.tuyucheng.taketoday.couchbase.domain.repository")
public class ReactiveCouchbaseConfiguration extends AbstractReactiveCouchbaseConfiguration {

    private CouchbaseProperties couchbaseProperties;

    public ReactiveCouchbaseConfiguration(CouchbaseProperties couchbaseProperties) {
        this.couchbaseProperties = couchbaseProperties;
    }

    @Override
    protected List<String> getBootstrapHosts() {
        return couchbaseProperties.getBootstrapHosts();
    }

    @Override
    protected String getBucketName() {
        return couchbaseProperties.getBucketName();
    }

    @Override
    protected String getBucketPassword() {
        return couchbaseProperties.getBucketPassword();
    }

    @Override
    public CouchbaseEnvironment couchbaseEnvironment() {
        return DefaultCouchbaseEnvironment
              .builder()
              .bootstrapHttpDirectPort(couchbaseProperties.getPort())
              .build();
    }
}
```

此外，我们还使用了@EnableReactiveCouchbaseRepositories来启用将位于指定包下的响应式Repository。

此外，我们重写了couchbaseEnvironment()以传递Couchbase连接端口。

## 4. Repository

在本节中，我们将学习如何创建和使用响应式Repository。**默认情况下，“all”[视图](https://docs.couchbase.com/server/6.0/learn/views/views-intro.html)支持大多数CRUD操作。自定义Repository方法由[N1QL](https://www.baeldung.com/n1ql-couchbase)支持**。如果集群不支持N1QL，则在初始化期间将抛出UnsupportedCouchbaseFeatureException。

首先，让我们创建我们的Repository将使用的POJO类：

```java
@Document
public class Person {
    @Id private UUID id;
    private String firstName;

    // getters and setters
}
```

### 4.1 基于视图的Repository

现在，我们将为Person创建一个Repository：

```java
@Repository
@ViewIndexed(designDoc = ViewPersonRepository.DESIGN_DOCUMENT)
public interface ViewPersonRepository extends ReactiveCrudRepository<Person, UUID> {

    String DESIGN_DOCUMENT = "person";
}
```

该Repository扩展了ReactiveCrudRepository接口，以便使用Reactor API与Couchbase进行交互。

此外，我们可以添加一个自定义方法并使用@View注解使其基于视图：

```java
@View(designDocument = ViewPersonRepository.DESIGN_DOCUMENT)
Flux<Person> findByFirstName(String firstName);
```

默认情况下，查询将查找名为byFirstName的视图。如果我们想提供自定义视图名称，我们必须使用viewName参数。

最后，让我们在StepVerifier的帮助下创建一个简单的CRUD测试：

```java
@Test
public void shouldSavePerson_findById_thenDeleteIt() {
    final UUID id = UUID.randomUUID();
    final Person person = new Person(id, "John");
    personRepository
        .save(person)
        .subscribe();
 
    final Mono<Person> byId = personRepository.findById(id);
 
    StepVerifier
        .create(byId)
        .expectNextMatches(result -> result
            .getId()
            .equals(id))
        .expectComplete()
        .verify();
 
    personRepository
        .delete(person)
        .subscribe();
}
```

### 4.2 N1QL/基于视图的Repository

现在，我们将为Person创建使用N1QL查询的响应式Repository：

```java
@Repository
@N1qlPrimaryIndexed
public interface N1QLPersonRepository extends ReactiveCrudRepository<Person, UUID> {
    Flux<Person> findAllByFirstName(String firstName);
}
```

该Repository扩展了ReactiveCrudRepository以便也使用Reactor API。此外，我们还添加了一个自定义的findAllByFirstName方法，该方法创建支持N1QL的查询。

之后，让我们为findAllByFirstName方法添加测试：

```java
@Test
public void shouldFindAll_byLastName() {
    final String firstName = "John";
    final Person matchingPerson = new Person(UUID.randomUUID(), firstName);
    final Person nonMatchingPerson = new Person(UUID.randomUUID(), "NotJohn");
    personRepository
        .save(matchingPerson)
        .subscribe();
    personRepository
        .save(nonMatchingPerson)
        .subscribe();
 
    final Flux<Person> allByFirstName = personRepository.findAllByFirstName(firstName);
 
    StepVerifier
        .create(allByFirstName)
        .expectNext(matchingPerson)
        .verifyComplete();
}
```

此外，我们将创建一个Repository，允许我们使用排序抽象来检索Person：

```java
@Repository
public interface N1QLSortingPersonRepository extends ReactiveSortingRepository<Person, UUID> {
    Flux<Person> findAllByFirstName(String firstName, Sort sort);
}
```

最后，让我们编写一个测试来检查数据是否真正排序：

```java
@Test
public void shouldFindAll_sortedByFirstName() {
    final Person firstPerson = new Person(UUID.randomUUID(), "John");
    final Person secondPerson = new Person(UUID.randomUUID(), "Mikki");
    personRepository
        .save(firstPerson)
        .subscribe();
    personRepository
        .save(secondPerson)
        .subscribe();
 
    final Flux<Person> allByFirstName = personRepository
        .findAll(Sort.by(Sort.Direction.DESC, "firstName"));
 
    StepVerifier
        .create(allByFirstName)
        .expectNextMatches(person -> person
            .getFirstName()
            .equals(secondPerson.getFirstName()))
        .expectNextMatches(person -> person
            .getFirstName()
            .equals(firstPerson.getFirstName()))
        .verifyComplete();
}
```

## 5. 总结

在本文中，我们学习了如何通过Couchbase和Spring Data Reactive框架使用响应式编程来使用Repository。