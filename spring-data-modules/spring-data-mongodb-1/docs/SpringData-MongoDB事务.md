## 1. 概述

从 4.0 版本开始，MongoDB 支持多文档 ACID 事务。而且，Spring Data Lovelace 现在提供对这些原生 MongoDB 事务的支持。

在本教程中，我们将讨论 Spring Data MongoDB 对同步和反应事务的支持。

我们还将查看用于非本机事务支持的 Spring Data TransactionTemplate 。

有关此 Spring Data 模块的介绍，请查看我们的[介绍性文章](https://www.baeldung.com/spring-data-mongodb-tutorial)。

## 2. 安装 MongoDB 4.0

首先，我们需要设置最新的 MongoDB 来尝试新的本机事务支持。

首先，我们必须从[MongoDB 下载中心](https://www.mongodb.com/download-center?initial=true#atlas)下载最新版本。

接下来，我们将使用命令行启动mongod服务：

```bash
mongod --replSet rs0
```

最后，启动副本集——如果还没有的话：

```bash
mongo --eval "rs.initiate()"
```

请注意，MongoDB 目前支持副本集上的事务。

## 3.Maven配置

接下来，我们需要将以下依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-mongodb</artifactId>
    <version>3.0.3.RELEASE</version>
</dependency>
```

可以在[中央存储库上找到该库的最新版本](https://search.maven.org/search?q=g:org.springframework.data AND a:spring-data-mongodb)

## 4.MongoDB配置

现在，让我们来看看我们的配置：

```java
@Configuration
@EnableMongoRepositories(basePackages = "com.baeldung.repository")
public class MongoConfig extends AbstractMongoClientConfiguration{

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @Override
    protected String getDatabaseName() {
        return "test";
    }

    @Override
    public MongoClient mongoClient() {
        final ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017/test");
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build();
        return MongoClients.create(mongoClientSettings);
    }
}
```

请注意，我们需要在我们的配置中注册MongoTransactionManager以启用本机 MongoDB 事务，因为它们默认情况下是禁用的。

## 5. 同步交易

完成配置后，我们需要做的就是使用原生 MongoDB 事务——用@Transactional注解我们的方法。

注解方法中的所有内容都将在一个事务中执行：

```java
@Test
@Transactional
public void whenPerformMongoTransaction_thenSuccess() {
    userRepository.save(new User("John", 30));
    userRepository.save(new User("Ringo", 35));
    Query query = new Query().addCriteria(Criteria.where("name").is("John"));
    List<User> users = mongoTemplate.find(query, User.class);

    assertThat(users.size(), is(1));
}
```

请注意，我们不能在多文档事务中使用listCollections命令——例如：

```java
@Test(expected = MongoTransactionException.class)
@Transactional
public void whenListCollectionDuringMongoTransaction_thenException() {
    if (mongoTemplate.collectionExists(User.class)) {
        mongoTemplate.save(new User("John", 30));
        mongoTemplate.save(new User("Ringo", 35));
    }
}
```

此示例在我们使用collectionExists()方法时抛出MongoTransactionException 。

## 6.交易模板

我们看到了 Spring Data 如何支持新的 MongoDB 本机事务。此外，Spring Data 还提供了非本机选项。

我们可以使用 Spring Data TransactionTemplate执行非本地事务：

```java
@Test
public void givenTransactionTemplate_whenPerformTransaction_thenSuccess() {
    mongoTemplate.setSessionSynchronization(SessionSynchronization.ALWAYS);                                     

    TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
        @Override
        protected void doInTransactionWithoutResult(TransactionStatus status) {
            mongoTemplate.insert(new User("Kim", 20));
            mongoTemplate.insert(new User("Jack", 45));
        };
    });

    Query query = new Query().addCriteria(Criteria.where("name").is("Jack")); 
    List<User> users = mongoTemplate.find(query, User.class);

    assertThat(users.size(), is(1));
}
```

我们需要将SessionSynchronization设置为ALWAYS以使用非本地 Spring Data 事务。

## 7.反应性交易

最后，我们将看一下Spring Data 对 MongoDB 反应式事务的支持。

我们需要向pom.xml添加更多依赖项以使用反应式 MongoDB：

```xml
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongodb-driver-reactivestreams</artifactId>
    <version>4.1.0</version>
</dependency>

<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongodb-driver-sync</artifactId>
    <version>4.0.5</version>
</dependency>
        
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-test</artifactId>
    <version>3.2.0.RELEASE</version>
    <scope>test</scope>
</dependency>
```

Maven Central 上提供了[mongodb-driver-reactivestreams](https://search.maven.org/search?q=mongodb-driver-reactivestreams)、[mongodb-driver-sync 和](https://search.maven.org/search?q=mongodb-driver-sync) [reactor-test](https://search.maven.org/search?q=a:reactor-test AND g:io.projectreactor)依赖项。

当然，我们需要配置 Reactive MongoDB：

```java
@Configuration
@EnableReactiveMongoRepositories(basePackages 
  = "com.baeldung.reactive.repository")
public class MongoReactiveConfig 
  extends AbstractReactiveMongoConfiguration {

    @Override
    public MongoClient reactiveMongoClient() {
        return MongoClients.create();
    }

    @Override
    protected String getDatabaseName() {
        return "reactive";
    }
}
```

要在反应式 MongoDB 中使用事务，我们需要使用 ReactiveMongoOperations 中的inTransaction ()方法：

```java
@Autowired
private ReactiveMongoOperations reactiveOps;

@Test
public void whenPerformTransaction_thenSuccess() {
    User user1 = new User("Jane", 23);
    User user2 = new User("John", 34);
    reactiveOps.inTransaction()
      .execute(action -> action.insert(user1)
      .then(action.insert(user2)));
}
```

[此处](https://www.baeldung.com/spring-data-mongodb-reactive)提供了有关 Spring Data 中的反应式存储库的更多信息。

## 八. 总结

在这篇文章中，我们学习了如何使用 Spring Data 使用本机和非本机 MongoDB 事务。