## 1. 概述

从4.0版本开始，MongoDB支持多文档ACID事务。而且，**Spring Data Lovelace现在提供对这些原生MongoDB事务的支持**。

在本教程中，我们将讨论Spring Data MongoDB对同步和响应式事务的支持。

我们还将查看用于非原生事务支持的Spring Data TransactionTemplate。

有关此Spring Data模块的介绍，请查看我们的[介绍性文章](https://www.baeldung.com/spring-data-mongodb-tutorial)。

## 2. 安装MongoDB 4.0

首先，我们需要设置最新的MongoDB来尝试新的原生事务支持。

首先，我们必须从[MongoDB下载中心](https://www.mongodb.com/download-center?initial=true#atlas)下载最新版本。

接下来，我们将使用命令行启动mongod服务：

```bash
mongod --replSet rs0
```

最后，启动副本集-如果尚未启动：

```bash
mongo --eval "rs.initiate()"
```

请注意，MongoDB目前支持副本集上的事务。

## 3. Maven配置

接下来，我们需要将以下依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-mongodb</artifactId>
    <version>3.0.3.RELEASE</version>
</dependency>
```

可以在[中央仓库](https://central.sonatype.com/artifact/org.springframework.data/spring-data-mongodb/4.0.3)上找到该库的最新版本。

## 4. MongoDB配置

现在，让我们来看看我们的配置：

```java
@Configuration
@EnableMongoRepositories(basePackages = "cn.tuyucheng.taketoday.repository")
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

请注意，我们需要在我们的配置中**注册MongoTransactionManager**以启用原生MongoDB事务，因为它们默认情况下是禁用的。

## 5. 同步事务

完成配置后，我们需要做的就是使用原生MongoDB事务-**使用@Transactional标注我们的方法**。

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

请注意，我们不能在多文档事务中使用listCollections命令-例如：

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

此示例在我们使用collectionExists()方法时抛出MongoTransactionException。

## 6. TransactionTemplate

我们看到了Spring Data如何支持新的MongoDB原生事务。此外，Spring Data还提供了非原生选项。

**我们可以使用Spring DataTransactionTemplate执行非原生事务**：

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

我们需要将SessionSynchronization设置为ALWAYS以使用非原生Spring Data事务。

## 7. 响应式事务

最后，我们将看一下**Spring Data对MongoDB响应式事务的支持**。

我们需要向pom.xml添加更多依赖项以使用响应式MongoDB：

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

Maven Central上提供了[mongodb-driver-reactivestreams](https://central.sonatype.com/artifact/org.mongodb/mongodb-driver-reactivestreams/4.9.0)、[mongodb-driver-sync](https://central.sonatype.com/artifact/org.mongodb/mongodb-driver-sync/4.9.0)和[reactor-test](https://central.sonatype.com/artifact/io.projectreactor/reactor-test/3.5.3)依赖项。

当然，我们需要配置响应式MongoDB：

```java
@Configuration
@EnableReactiveMongoRepositories(basePackages = "cn.tuyucheng.taketoday.reactive.repository")
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

要在响应式MongoDB中使用事务，我们需要使用ReactiveMongoOperations中的inTransaction()方法：

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

[此处](https://www.baeldung.com/spring-data-mongodb-reactive)提供了有关Spring Data中的响应式Repository的更多信息。

## 8. 总结

在这篇文章中，我们学习了如何使用Spring Data使用原生和非原生MongoDB事务。