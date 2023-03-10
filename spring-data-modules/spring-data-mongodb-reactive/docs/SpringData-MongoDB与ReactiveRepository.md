## 1. 简介

在本教程中，我们将了解如何通过带有MongoDB的Spring Data Reactive Repository使用响应式编程配置和实现数据库操作。

我们将介绍ReactiveCrud Repository、ReactiveMongoRepository以及ReactiveMongoTemplate的基本用法。

尽管这些实现使用[响应式编程](https://www.baeldung.com/spring-5-functional-web)，但这不是本教程的主要重点。

## 2. 环境

为了使用Reactive MongoDB，我们需要将依赖项添加到我们的pom.xml中。

我们还将添加一个嵌入式MongoDB用于测试：

```xml
<dependencies>
    // ...
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
    </dependency>
    <dependency>
        <groupId>de.flapdoodle.embed</groupId>
        <artifactId>de.flapdoodle.embed.mongo</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 3. 配置

为了激活响应式支持，我们需要使用@EnableReactiveMongoRepositories以及一些基础设施设置：

```java
@EnableReactiveMongoRepositories
public class MongoReactiveApplication extends AbstractReactiveMongoConfiguration {

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create();
    }

    @Override
    protected String getDatabaseName() {
        return "reactive";
    }
}
```

请注意，如果我们使用独立的MongoDB安装，则以上内容是必需的。但是，由于我们在示例中使用带有嵌入式MongoDB的Spring Boot，因此不需要上述配置。

## 4. 创建文档

对于下面的示例，让我们创建一个Account类并用@Document对其进行标注以在数据库操作中使用它：

```java
@Document
public class Account {

    @Id
    private String id;
    private String owner;
    private Double value;

    // getters and setters
}
```

## 5. 使用响应式Repository

我们已经熟悉[Repository编程模型](https://www.baeldung.com/spring-data-mongodb-tutorial)，已经定义了CRUD方法以及对其他一些常见事物的支持。

现在使用响应式模型，我们得到了相同的一组方法和规范，只是我们将以一种响应式方式处理结果和参数。

### 5.1 ReactiveCrudRepository

我们可以像使用阻塞式CrudRepository一样使用这个Repository：

```java
@Repository
public interface AccountCrudRepository extends ReactiveCrudRepository<Account, String> {

    Flux<Account> findAllByValue(String value);
    Mono<Account> findFirstByOwner(Mono<String> owner);
}
```

正如我们在findFirstByOwner()方法中看到的那样，我们可以传递不同类型的参数，如普通参数(String)、包装参数(Optional、Stream)或响应类型参数(Mono、Flux)。

### 5.2 ReactiveMongoRepository

还有ReactiveMongoRepository接口，它继承自ReactiveCrudRepository并添加了一些新的查询方法：

```java
@Repository
public interface AccountReactiveRepository extends ReactiveMongoRepository<Account, String> {
}
```

使用ReactiveMongoRepository，我们可以通过Example查询：

```java
Flux<Account> accountFlux = repository
    .findAll(Example.of(new Account(null, "owner", null)));
```

因此，我们将获得与传递的Example相同的每个帐户。

创建Repository后，他们已经定义了执行一些我们不需要实现的数据库操作的方法：

```java
Mono<Account> accountMono = repository.save(new Account(null, "owner", 12.3));
Mono<Account> accountMono2 = repository
    .findById("123456");
```

### 5.3 RxJava2CrudRepository

使用RxJava2CrudRepository，我们具有与ReactiveCrudRepository相同的行为，但使用来自RxJava的结果和参数类型：

```java
@Repository
public interface AccountRxJavaRepository extends RxJava2CrudRepository<Account, String> {

    Observable<Account> findAllByValue(Double value);
    Single<Account> findFirstByOwner(Single<String> owner);
}
```

### 5.4 测试我们的基本操作

为了测试我们的Repository方法，我们将使用测试订阅者：

```java
@Test
public void givenValue_whenFindAllByValue_thenFindAccount() {
    repository.save(new Account(null, "Bill", 12.3)).block();
    Flux<Account> accountFlux = repository.findAllByValue(12.3);

    StepVerifier
        .create(accountFlux)
        .assertNext(account -> {
            assertEquals("Bill", account.getOwner());
            assertEquals(Double.valueOf(12.3) , account.getValue());
            assertNotNull(account.getId());
        })
        .expectComplete()
        .verify();
}

@Test
public void givenOwner_whenFindFirstByOwner_thenFindAccount() {
    repository.save(new Account(null, "Bill", 12.3)).block();
    Mono<Account> accountMono = repository
        .findFirstByOwner(Mono.just("Bill"));

    StepVerifier
        .create(accountMono)
        .assertNext(account -> {
            assertEquals("Bill", account.getOwner());
            assertEquals(Double.valueOf(12.3) , account.getValue());
            assertNotNull(account.getId());
        })
        .expectComplete()
        .verify();
}

@Test
public void givenAccount_whenSave_thenSaveAccount() {
    Mono<Account> accountMono = repository.save(new Account(null, "Bill", 12.3));

    StepVerifier
        .create(accountMono)
        .assertNext(account -> assertNotNull(account.getId()))
        .expectComplete()
        .verify();
}
```

## 6. ReactiveMongoTemplate

除了Repository方法，我们还有ReactiveMongoTemplate。

首先，我们需要将ReactiveMongoTemplate注册为一个bean：

```java
@Configuration
public class ReactiveMongoConfig {

    @Autowired
    MongoClient mongoClient;

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(mongoClient, "test");
    }
}
```

然后，我们可以将这个bean注入到我们的服务中以执行数据库操作：

```java
@Service
public class AccountTemplateOperations {

    @Autowired
    ReactiveMongoTemplate template;

    public Mono<Account> findById(String id) {
        return template.findById(id, Account.class);
    }

    public Flux<Account> findAll() {
        return template.findAll(Account.class);
    }
    public Mono<Account> save(Mono<Account> account) {
        return template.save(account);
    }
}
```

ReactiveMongoTemplate还有一些方法和我们的域无关，你可以在[文档](https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/core/ReactiveMongoTemplate.html)中查看它们。

## 7. 总结

在这个简短的教程中，我们介绍了使用带有Spring Data Reactive Repository框架的MongoDB响应式编程来使用Repository和模板。