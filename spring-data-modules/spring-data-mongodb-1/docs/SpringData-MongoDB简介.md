## 1. 概述

本文将快速实用地**介绍Spring Data MongoDB**。

我们将使用MongoTemplate和MongoRepository来介绍基础知识，并通过实际示例来说明每个操作。

## 延伸阅读

### [MongoDB中的地理空间支持](https://www.baeldung.com/mongodb-geospatial-support)

了解如何使用MongoDB存储、索引和搜索地理空间数据

[阅读更多](https://www.baeldung.com/mongodb-geospatial-support)→

### [使用嵌入式MongoDB进行Spring Boot集成测试](https://www.baeldung.com/spring-boot-embedded-mongodb)

了解如何结合使用Flapdoodle的嵌入式MongoDB解决方案和Spring Boot来顺利运行MongoDB集成测试。

[阅读更多](https://www.baeldung.com/spring-boot-embedded-mongodb)→

## 2. MongoTemplate和MongoRepository

**MongoTemplate**遵循Spring中的标准模板模式，并为底层持久化引擎提供了一个随时可用的基本API。

**Repository**遵循以Spring Data为中心的方法，并基于所有Spring Data项目中众所周知的访问模式，提供更灵活和复杂的API操作。

对于两者，我们需要从定义依赖关系开始-例如，在pom.xml中，使用Maven：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-mongodb</artifactId>
    <version>3.0.3.RELEASE</version>
</dependency>
```

要检查是否已发布任何新版本的库，请在[此处](https://central.sonatype.com/artifact/org.springframework.data/spring-data-mongodb/4.0.3)跟踪版本。

## 3. MongoTemplate的配置

### 3.1 XML配置

让我们从Mongo模板的简单XML配置开始：

```xml
<mongo:mongo-client id="mongoClient" host="localhost" />
<mongo:db-factory id="mongoDbFactory" dbname="test" mongo-client-ref="mongoClient" />
```

我们首先需要定义负责创建Mongo实例的工厂bean。

接下来，我们需要实际定义(和配置)模板bean：

```xml
<bean id="mongoTemplate" class="org.springframework.data.mongodb.core.MongoTemplate"> 
    <constructor-arg ref="mongoDbFactory"/> 
</bean>
```

最后，我们需要定义一个后处理器来转换@Repository注解类中抛出的任何MongoException：

```xml
<bean class= "org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor"/>
```

### 3.2 Java配置

现在让我们通过扩展MongoDB配置AbstractMongoConfiguration的基类，使用Java配置创建一个类似的配置：

```java
@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "test";
    }

    @Override
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017/test");
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
              .applyConnectionString(connectionString)
              .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Override
    public Collection getMappingBasePackages() {
        return Collections.singleton("cn.tuyucheng.taketoday");
    }
}
```

请注意，我们不需要在之前的配置中定义MongoTemplate bean，因为它已经在AbstractMongoClientConfiguration中定义了。

我们还可以在不扩展AbstractMongoClientConfiguration的情况下从头开始使用我们的配置：

```java
@Configuration
public class SimpleMongoConfig {

    @Bean
    public MongoClient mongo() {
        ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017/test");
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
              .applyConnectionString(connectionString)
              .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), "test");
    }
}
```

## 4. MongoRepository的配置

### 4.1 XML配置

要使用自定义Repository(扩展MongoRepository)，我们需要继续第3.1节中的配置。并设置Repository：

```xml
<mongo:repositories base-package="cn.tuyucheng.taketoday.repository" mongo-template-ref="mongoTemplate"/>
```

### 4.2 Java配置

同样，我们将在第3.2节中创建的配置的基础上进行构建。并添加一个新注解：

```java
@EnableMongoRepositories(basePackages = "cn.tuyucheng.taketoday.repository")
```

### 4.3 创建Repository

配置完成后，我们需要创建一个Repository-扩展现有的MongoRepository接口：

```java
public interface UserRepository extends MongoRepository<User, String> {
    // 
}
```

现在我们可以自动注入这个UserRepository并使用来自MongoRepository的操作或添加自定义操作。

## 5. 使用MongoTemplate

### 5.1 Insert

让我们从插入操作和一个空数据库开始：

```json
{
}
```

现在，如果我们插入一个新用户：

```java
User user = new User();
user.setName("Jon");
mongoTemplate.insert(user, "user");
```

数据库将如下所示：

```json
{
    "_id" : ObjectId("55b4fda5830b550a8c2ca25a"),
    "_class" : "cn.tuyucheng.taketoday.model.User",
    "name" : "Jon"
}
```

### 5.2 Save-Insert

保存操作具有保存或更新语义：如果存在id，则执行更新，如果不存在，则执行插入。

让我们看看第一个语义-插入。

这是数据库的初始状态：

```json
{
}
```

当我们现在保存一个新用户时：

```java
User user = new User();
user.setName("Albert"); 
mongoTemplate.save(user, "user");
```

该实体将被插入到数据库中：

```json
{
    "_id" : ObjectId("55b52bb7830b8c9b544b6ad5"),
    "_class" : "cn.tuyucheng.taketoday.model.User",
    "name" : "Albert"
}
```

接下来，我们将查看具有更新语义的相同操作-保存。

### 5.3 Save-Update

现在让我们看一下使用更新语义保存，对现有实体进行操作：

```json
{
    "_id" : ObjectId("55b52bb7830b8c9b544b6ad5"),
    "_class" : "cn.tuyucheng.taketoday.model.User",
    "name" : "Jack"
}
```

当我们保存现有用户时，我们将更新它：

```java
user = mongoTemplate.findOne(Query.query(Criteria.where("name").is("Jack")), User.class);
user.setName("Jim");
mongoTemplate.save(user, "user");
```

数据库将如下所示：

```json
{
    "_id" : ObjectId("55b52bb7830b8c9b544b6ad5"),
    "_class" : "cn.tuyucheng.taketoday.model.User",
    "name" : "Jim"
}
```

我们可以看到，在这个特定的例子中，save使用了update的语义，因为我们使用了一个给定_id的对象。

### 5.4 UpdateFirst

updateFirst更新匹配查询的第一个文档。

让我们从数据库的初始状态开始：

```json
[
    {
        "_id" : ObjectId("55b5ffa5511fee0e45ed614b"),
        "_class" : "cn.tuyucheng.taketoday.model.User",
        "name" : "Alex"
    },
    {
        "_id" : ObjectId("55b5ffa5511fee0e45ed614c"),
        "_class" : "cn.tuyucheng.taketoday.model.User",
        "name" : "Alex"
    }
]
```

当我们现在运行updateFirst时：

```java
Query query = new Query();
query.addCriteria(Criteria.where("name").is("Alex"));
Update update = new Update();
update.set("name", "James");
mongoTemplate.updateFirst(query, update, User.class);
```

只有第一个条目将被更新：

```json
[
    {
        "_id" : ObjectId("55b5ffa5511fee0e45ed614b"),
        "_class" : "cn.tuyucheng.taketoday.model.User",
        "name" : "James"
    },
    {
        "_id" : ObjectId("55b5ffa5511fee0e45ed614c"),
        "_class" : "cn.tuyucheng.taketoday.model.User",
        "name" : "Alex"
    }
]
```

### 5.5 UpdateMulti

**UpdateMulti更新与给定查询匹配的所有文档**。

首先，这是执行updateMulti之前数据库的状态：

```json
[
    {
        "_id" : ObjectId("55b5ffa5511fee0e45ed614b"),
        "_class" : "cn.tuyucheng.taketoday.model.User",
        "name" : "Eugen"
    },
    {
        "_id" : ObjectId("55b5ffa5511fee0e45ed614c"),
        "_class" : "cn.tuyucheng.taketoday.model.User",
        "name" : "Eugen"
    }
]
```

现在让我们运行updateMulti操作：

```java
Query query = new Query();
query.addCriteria(Criteria.where("name").is("Eugen"));
Update update = new Update();
update.set("name", "Victor");
mongoTemplate.updateMulti(query, update, User.class);
```

两个现有对象都将在数据库中更新：

```json
[
    {
        "_id" : ObjectId("55b5ffa5511fee0e45ed614b"),
        "_class" : "cn.tuyucheng.taketoday.model.User",
        "name" : "Victor"
    },
    {
        "_id" : ObjectId("55b5ffa5511fee0e45ed614c"),
        "_class" : "cn.tuyucheng.taketoday.model.User",
        "name" : "Victor"
    }
]
```

### 5.6 FindAndModify

此操作与updateMulti类似，但它**返回修改前的对象**。

首先，这是调用findAndModify之前数据库的状态：

```json
{
    "_id" : ObjectId("55b5ffa5511fee0e45ed614b"),
    "_class" : "cn.tuyucheng.taketoday.model.User",
    "name" : "Markus"
}
```

下面来看实际运行代码：

```java
Query query = new Query();
query.addCriteria(Criteria.where("name").is("Markus"));
Update update = new Update();
update.set("name", "Nick");
User user = mongoTemplate.findAndModify(query, update, User.class);
```

返回的用户对象与数据库中的初始状态具有相同的值。

但是，这是数据库中的新状态：

```json
{
    "_id" : ObjectId("55b5ffa5511fee0e45ed614b"),
    "_class" : "cn.tuyucheng.taketoday.model.User",
    "name" : "Nick"
}
```

### 5.7 Upsert

upsert具有**查找并修改或创建语义**：如果文档匹配，则更新它，或者通过组合查询和更新对象来创建新文档。

让我们从数据库的初始状态开始：

```json
{
    "_id" : ObjectId("55b5ffa5511fee0e45ed614b"),
    "_class" : "cn.tuyucheng.taketoday.model.User",
    "name" : "Markus"
}
```

现在让我们运行upsert：

```java
Query query = new Query();
query.addCriteria(Criteria.where("name").is("Markus"));
Update update = new Update();
update.set("name", "Nick");
mongoTemplate.upsert(query, update, User.class);
```

这是操作后数据库的状态：

```json
{
    "_id" : ObjectId("55b5ffa5511fee0e45ed614b"),
    "_class" : "cn.tuyucheng.taketoday.model.User",
    "name" : "Nick"
}
```

### 5.8 Remove

我们将在调用remove之前查看数据库的状态：

```json
{
    "_id" : ObjectId("55b5ffa5511fee0e45ed614b"),
    "_class" : "cn.tuyucheng.taketoday.model.User",
    "name" : "Benn"
}
```

现在让我们运行remove：

```java
mongoTemplate.remove(user, "user");
```

结果将如预期的那样：

```json
{
}
```

## 6. 使用MongoRepository

### 6.1 Insert

首先，我们将在运行插入之前查看数据库的状态：

```json
{
}
```

现在我们将插入一个新用户：

```java
User user = new User();
user.setName("Jon");
userRepository.insert(user);
```

这是数据库的最终状态：

```json
{
    "_id" : ObjectId("55b4fda5830b550a8c2ca25a"),
    "_class" : "cn.tuyucheng.taketoday.model.User",
    "name" : "Jon"
}
```

请注意该操作的工作方式与MongoTemplate API中的插入相同。

### 6.2 Save–Insert

同样，save与MongoTemplate API中的save操作相同。

这是数据库的初始状态：

```json
{
}
```

现在我们执行save操作：

```java
User user = new User();
user.setName("Aaron");
userRepository.save(user);
```

结果是用户被添加到数据库中：

```json
{
    "_id" : ObjectId("55b52bb7830b8c9b544b6ad5"),
    "_class" : "cn.tuyucheng.taketoday.model.User",
    "name" : "Aaron"
}
```

再次注意save如何与insert语义一起工作，因为我们正在插入一个新对象。

### 6.3 Save–Update

现在让我们看一下相同的操作，但具有**更新语义**。

首先，这是运行新保存之前的数据库状态：

```json
{
    "_id" : ObjectId("55b52bb7830b8c9b544b6ad5"),
    "_class" : "cn.tuyucheng.taketoday.model.User",
    "name" : "Jack"81*6
}
```

现在我们执行操作：

```java
user = mongoTemplate.findOne(Query.query(Criteria.where("name").is("Jack")), User.class);
user.setName("Jim");
userRepository.save(user);
```

最后，这是数据库的状态：

```json
{
    "_id" : ObjectId("55b52bb7830b8c9b544b6ad5"),
    "_class" : "cn.tuyucheng.taketoday.model.User",
    "name" : "Jim"
}
```

再次注意保存如何与更新语义一起工作，因为我们使用的是现有对象。

### 6.4 Delete

这是调用delete之前数据库的状态：

```json
{
    "_id" : ObjectId("55b5ffa5511fee0e45ed614b"),
    "_class" : "cn.tuyucheng.taketoday.model.User",
    "name" : "Benn"
}
```

让我们运行delete：

```java
userRepository.delete(user);
```

这是我们的结果：

```json
{
}
```

### 6.5 FindOne

接下来，这是调用findOne时数据库的状态：

```json
{
    "_id" : ObjectId("55b5ffa5511fee0e45ed614b"),
    "_class" : "cn.tuyucheng.taketoday.model.User",
    "name" : "Chris"
}
```

现在让我们执行findOne：

```java
userRepository.findOne(user.getId())
```

结果将返回现有数据：

```json
{
    "_id" : ObjectId("55b5ffa5511fee0e45ed614b"),
    "_class" : "cn.tuyucheng.taketoday.model.User",
    "name" : "Chris"
}
```

### 6.6 Exists

调用之前数据库的状态存在：

```json
{
    "_id" : ObjectId("55b5ffa5511fee0e45ed614b"),
    "_class" : "cn.tuyucheng.taketoday.model.User",
    "name" : "Harris"
}
```

现在让我们运行exists，它当然会返回true：

```java
boolean isExists = userRepository.exists(user.getId());
```

### 6.7 FindAll与Sort

调用findAll之前的数据库状态：

```json
[
    {
        "_id" : ObjectId("55b5ffa5511fee0e45ed614b"),
        "_class" : "cn.tuyucheng.taketoday.model.User",
        "name" : "Brendan"
    },
    {
       "_id" : ObjectId("67b5ffa5511fee0e45ed614b"),
       "_class" : "cn.tuyucheng.taketoday.model.User",
       "name" : "Adam"
    }
]
```

现在让我们使用Sort运行findAll：

```java
List<User> users = userRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
```

结果将**按名称升序排序**：

```json
[
    {
        "_id" : ObjectId("67b5ffa5511fee0e45ed614b"),
        "_class" : "cn.tuyucheng.taketoday.model.User",
        "name" : "Adam"
    },
    {
        "_id" : ObjectId("55b5ffa5511fee0e45ed614b"),
        "_class" : "cn.tuyucheng.taketoday.model.User",
        "name" : "Brendan"
    }
]
```

### 6.8 FindAll与Pageable

调用findAll之前的数据库状态：

```json
[
    {
        "_id" : ObjectId("55b5ffa5511fee0e45ed614b"),
        "_class" : "cn.tuyucheng.taketoday.model.User",
        "name" : "Brendan"
    },
    {
        "_id" : ObjectId("67b5ffa5511fee0e45ed614b"),
        "_class" : "cn.tuyucheng.taketoday.model.User",
        "name" : "Adam"
    }
]
```

现在让我们使用Pageable执行findAll：

```java
Pageable pageableRequest = PageRequest.of(0, 1);
Page<User> page = userRepository.findAll(pageableRequest);
List<User> users = pages.getContent();
```

生成的用户列表将只有一个用户：


```json
{
    "_id" : ObjectId("55b5ffa5511fee0e45ed614b"),
    "_class" : "cn.tuyucheng.taketoday.model.User",
    "name" : "Brendan"
}
```

## 7. 注解

最后，让我们回顾一下Spring Data用来驱动这些API操作的简单注解。

字段级@Id注解可以修饰任何类型，包括long和String：

```java
@Id
private String id;
```

如果@Id字段的值不为null，则按原样存储在数据库中；否则，转换器将假定我们要在数据库中存储一个ObjectId(可以是ObjectId、String或BigInteger)。

接下来我们将看看@Document：

```java
@Document
public class User {
    //
}
```

这个注解只是**将一个类标记为需要持久保存到数据库的域对象**，同时允许我们选择要使用的集合的名称。

## 8. 总结

本文快速而全面地介绍了如何通过MongoTemplate API以及如何使用MongoRepository将MongoDB与Spring Data结合使用。