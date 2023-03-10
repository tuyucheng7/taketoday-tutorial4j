## 1. 概述

使用Spring Data MongoDB，我们可以创建一个MongoClient来对数据库进行操作。然而，有时，我们可能需要在我们的应用程序中使用多个数据库。

在本教程中，**我们将创建多个与MongoDB的连接。我们还将添加一些Spring Boot测试来模拟这种情况**。

## 2. Spring Data MongoDB多数据库应用

使用[MongoDB](https://www.baeldung.com/spring-data-mongodb-tutorial)时，我们创建一个MongoTemplate来访问数据。因此，我们可以创建多个模板来连接到各种数据库。

**但是，这样我们会得到[NoUniqueBeanDefinitionException](http://baeldung.com/spring-nosuchbeandefinitionexception#cause-2.)，因为Spring会找到多个MongoTemplate bean**。

考虑到这一点，让我们看看如何构建Spring Boot配置。

### 2.1 依赖设置

让我们从向pom.xml添加依赖项开始。首先，我们需要一个[Spring Boot启动器](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-parent/3.0.3)：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.5</version>
    <relativePath />
</parent>
```

然后，我们需要[Web启动器](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-data/3.0.3)和[Data MongoDB](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-data-mongodb/3.0.3)的依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

同样，如果我们使用Gradle，我们添加到build.gradle：

```bash
plugins {
    id 'org.springframework.boot' version '2.7.5'
}
implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'
implementation 'org.springframework.boot:spring-boot-starter-web'
```

作为替代方案，我们可以使用[Spring Initializer](https://start.spring.io/)。

### 2.2 模型

**首先，让我们添加我们的模型。我们将创建两个将由两个不同数据库使用的文档**。

例如，我们将创建一个User文档：

```java
@Document(collection = "user")
public class User {

    @MongoId
    private ObjectId id;

    private String name;

    private String surname;
    private String email;

    private int age;

    // getters and setters
}
```

然后，我们还添加一个Account文档：

```java
@Document(collection = "account")
public class Account {

    @MongoId
    private ObjectId id;

    private String userEmail;

    private String nickName;

    private String accountDomain;

    private String password;

    // getters and setters
}
```

### 2.3 Repository

然后，我们使用一些Spring Data方法为每个模型类创建一个Repository。

首先，让我们添加一个UserRepository：

```java
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    User findByEmail(String email);
}
```

接下来，我们添加一个AccountRepository：

```java
@Repository
public interface AccountRepository extends MongoRepository<Account, String> {

    Account findByAccountDomain(String account);
}
```

### 2.4 连接属性

让我们为正在使用的多个数据库定义属性：

```properties
mongodb.primary.host=localhost
mongodb.primary.database=db1
mongodb.primary.authenticationDatabase=admin
mongodb.primary.username=user1
mongodb.primary.password=password
mongodb.primary.port=27017

mongodb.secondary.host=localhost
mongodb.secondary.database=db2
mongodb.secondary.authenticationDatabase=admin
mongodb.secondary.username=user2
mongodb.secondary.password=password
mongodb.secondary.port=27017
```

值得注意的是，我们有一个用于身份验证的特定数据库的属性。

### 2.5 主要配置

现在，我们需要我们的配置。我们将为每个数据库创建一个。

让我们看一下用于UserRepository的主要类定义：

```java
@Configuration
@EnableMongoRepositories(basePackageClasses = UserRepository.class, mongoTemplateRef = "primaryMongoTemplate")
@EnableConfigurationProperties
public class PrimaryConfig {
    // beans
}
```

为了演示，让我们分解所有的bean和注解。

首先，我们将使用[MongoProperties](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/mongo/MongoProperties.html)检索和设置属性。这样，我们直接将所有属性映射到一个bean：

```java
@Bean(name = "primaryProperties")
@ConfigurationProperties(prefix = "mongodb.primary")
@Primary
public MongoProperties primaryProperties() {
    return new MongoProperties();
}
```

为了向多个用户授予访问权限，我们使用带有[MongoCredential](https://mongodb.github.io/mongo-java-driver/3.6/javadoc/com/mongodb/MongoCredential.html)的MongoDB[身份验证机制](https://www.mongodb.com/docs/drivers/java/sync/v4.6/fundamentals/auth/)。我们通过添加身份验证数据库(在本例中为admin)来构建我们的凭证对象：

```java
@Bean(name = "primaryMongoClient")
public MongoClient mongoClient(@Qualifier("primaryProperties") MongoProperties mongoProperties) {
    MongoCredential credential = MongoCredential.createCredential(
        mongoProperties.getUsername(), 
        mongoProperties.getAuthenticationDatabase(), 
        mongoProperties.getPassword());
    
    return MongoClients.create(MongoClientSettings.builder()
        .applyToClusterSettings(builder -> builder.hosts(singletonList(new ServerAddress(mongoProperties.getHost(), mongoProperties.getPort()))))
        .credential(credential)
        .build());
}
```

**正如最新版本所建议的那样，我们使用[SimpleMongoClientDatabaseFactory](https://docs.spring.io/spring-data/data-mongo/docs/4.0.x/reference/html/#mongo.mongo-db-factory)而不是从连接字符串创建MongoTemplate**：

```java
@Primary
@Bean(name = "primaryMongoDBFactory")
public MongoDatabaseFactory mongoDatabaseFactory(@Qualifier("primaryMongoClient") MongoClient mongoClient,
                                                 @Qualifier("primaryProperties") MongoProperties mongoProperties) {
    return new SimpleMongoClientDatabaseFactory(mongoClient, mongoProperties.getDatabase());
}
```

**我们需要我们的bean在这里成为@Primary，因为我们将添加更多的数据库配置**。否则，我们将陷入我们之前讨论的唯一性约束。

**当我们映射[多个EntityManager](https://www.baeldung.com/spring-data-jpa-multiple-databases)时，我们对JPA做同样的事情。同样，我们需要在@EnableMongoRepositories中引用Mongotemplate**：

```java
@EnableMongoRepositories(basePackageClasses = UserRepository.class, mongoTemplateRef = "primaryMongoTemplate")
```

### 2.6 第二个配置

最后，为了仔细检查，让我们看一下第二个数据库配置：

```java
@Configuration
@EnableMongoRepositories(basePackageClasses = AccountRepository.class, mongoTemplateRef = "secondaryMongoTemplate")
@EnableConfigurationProperties
public class SecondaryConfig {

    @Bean(name = "secondaryProperties")
    @ConfigurationProperties(prefix = "mongodb.secondary")
    public MongoProperties secondaryProperties() {
        return new MongoProperties();
    }

    @Bean(name = "secondaryMongoClient")
    public MongoClient mongoClient(@Qualifier("secondaryProperties") MongoProperties mongoProperties) {

        MongoCredential credential = MongoCredential.createCredential(
              mongoProperties.getUsername(),
              mongoProperties.getAuthenticationDatabase(),
              mongoProperties.getPassword());

        return MongoClients.create(MongoClientSettings.builder()
              .applyToClusterSettings(builder -> builder.hosts(singletonList(new ServerAddress(mongoProperties.getHost(), mongoProperties.getPort()))))
              .credential(credential)
              .build());
    }

    @Bean(name = "secondaryMongoDBFactory")
    public MongoDatabaseFactory mongoDatabaseFactory(@Qualifier("secondaryMongoClient") MongoClient mongoClient,
                                                     @Qualifier("secondaryProperties") MongoProperties mongoProperties) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, mongoProperties.getDatabase());
    }

    @Bean(name = "secondaryMongoTemplate")
    public MongoTemplate mongoTemplate(@Qualifier("secondaryMongoDBFactory") MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}
```

在这种情况下，它将引用AccountRepository或属于同一包的所有类。

## 3. 测试

我们将针对MongoDB实例测试应用程序。我们可以将[MongoDB与Docker](https://www.mongodb.com/compatibility/docker)一起使用。

### 3.1 启动MongoDB容器

让我们使用[Docker Compose](https://www.baeldung.com/ops/docker-compose)运行一个MongoDB容器，下面是我们的YAML模板：

```yaml
services:
    mongo:
        hostname: localhost
        container_name: 'mongo'
        image: 'mongo:latest'
        expose:
            - 27017
        ports:
            - 27017:27017
        environment:
            - MONGO_INITDB_DATABASE=admin
            - MONGO_INITDB_ROOT_USERNAME=admin
            - MONGO_INITDB_ROOT_PASSWORD=admin
        volumes:
            - ./mongo-init.js:/docker-entrypoint-initdb.d/mongo-init.js
```

**如果我们想要进行身份验证，我们需要使用root用户进行初始化。为了用更多用户填充数据库，我们将[绑定挂载](https://docs.docker.com/storage/bind-mounts/)添加到JavaScript初始化文件**：

```javascript
db.createUser(
    {
        user: "user1",
        pwd: "password",
        roles: [ { role: "readWrite", db: "db1" } ]
    }
)

db.createUser(
    {
        user: "user2",
        pwd: "password",
        roles: [ { role: "readWrite", db: "db2" } ]
    }
)
```

让我们运行我们的容器：

```shell
docker-compose up -d
```

当容器启动时，它会为mongo-init.js文件创建一个卷并将其复制到容器的入口点。

### 3.2 Spring Boot测试

让我们在一些基本的[Spring Boot测试](https://www.baeldung.com/spring-boot-testing)中将它们包装在一起：

```java
@SpringBootTest(classes = { SpringBootMultipeDbApplication.class })
@TestPropertySource("/multipledb/multidb.properties")
public class MultipleDbUnitTest {

    // set up

    @Test
    void whenFindUserByEmail_thenNameOk() {
        assertEquals("name", userRepository.findByEmail("user@gmail.com")
              .getName());
    }

    @Test
    void whenFindAccountByDomain_thenNickNameOk() {
        assertEquals("nickname", accountRepository.findByAccountDomain("account@jira.tuyucheng.com").getNickName());
    }
}
```

首先，我们要为数据库建立连接并获得身份验证。如果我们不这样做，则可能会未填充数据库或未启动MongoDB实例。

在这些情况下，我们可以查看我们的数据库容器的日志，例如：

```shell
docker logs 30725c8635d4
```

值得注意的是，初始JavaScript仅在容器第一次运行时执行。因此，如果我们需要使用不同的脚本运行，我们可能需要删除卷：

```shell
docker-compose down -v
```

## 4. 总结

在本文中，我们了解了如何使用Spring Data MongoDB创建多个连接。我们看到了如何添加凭据以进行身份验证。最重要的是，我们已经了解了如何创建配置，其中之一必须是@Primary bean。最后，我们使用Docker和Spring Boot测试针对正在运行的MongoDB实例添加了一些测试用例，