## 1. 概述

在本教程中，我们将学习如何使用Flapdoodle的嵌入式MongoDB解决方案与Spring Boot一起运行MongoDB集成测试。

**MongoDB是一种流行的NoSQL文档数据库**。由于其高度的可扩展性、内置分片和出色的社区支持，它通常被许多开发人员用作“NoSQL存储”的首选数据库。

与任何其他持久性技术一样，**能够轻松地测试数据库与我们应用程序其余部分的集成至关重要**。值得庆幸的是，Spring Boot允许我们轻松编写此类测试。

## 2. Maven依赖

首先，让我们为Boot项目设置Maven父级。

得益于父级，我们不需要手动为每个Maven依赖项定义版本。

我们自然会使用Spring Boot：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.2</version>
    <relativePath /> <!-- lookup parent from repository -->
</parent>
```

你可以在[此处](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-parent/3.0.3)找到最新的Boot版本。

由于我们添加了Spring Boot Parent，我们可以添加所需的依赖项而无需指定它们的版本：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

spring-boot-starter-data-mongodb为MongoDB启用Spring支持：

```xml
<dependency>
    <groupId>de.flapdoodle.embed</groupId>
    <artifactId>de.flapdoodle.embed.mongo</artifactId>
    <scope>test</scope>
</dependency>
```

de.flapdoodle.embed.mongo提供用于集成测试的嵌入式MongoDB。

## 3. 使用嵌入式MongoDB进行测试

本节涵盖两种场景：Spring Boot测试和手动测试。

### 3.1 Spring Boot测试

**添加de.flapdoodle.embed.mongo依赖后，Spring Boot将在运行测试时自动尝试下载并启动嵌入式MongoDB**。

每个版本只下载一次包，以便后续测试运行得更快。

在此阶段，我们应该能够启动并通过简单的JUnit 5集成测试：

```java
@ContextConfiguration(classes = SpringBootPersistenceApplication.class)
@DataMongoTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
class MongoDbSpringIntegrationTest {

    @DisplayName("Given object When save object using MongoDB template Then object can be found")
    @Test
    void test(@Autowired MongoTemplate mongoTemplate) {
        // given
        DBObject objectToSave = BasicDBObjectBuilder.start()
              .add("key", "value")
              .get();

        // when
        mongoTemplate.save(objectToSave, "collection");

        // then
        assertThat(mongoTemplate.findAll(DBObject.class, "collection"))
              .extracting("key").containsOnly("value");
    }
}
```

如我们所见，嵌入式数据库是Spring自动启动的，控制台也应该有这样的日志：

```shell
... "msg":"MongoDB starting","attr":{"pid":16564,"port":58128...}} 
```

### 3.2 手动配置测试

Spring Boot会自动启动并配置嵌入式数据库，然后为我们注入MongoTemplate实例。但是，**有时我们可能需要手动配置嵌入式Mongo数据库**(例如，在测试特定的数据库版本时)。

以下代码片段显示了我们如何手动配置嵌入式MongoDB实例。这大致相当于之前的Spring测试：

```java
class ManualEmbeddedMongoDbIntegrationTest {

    private static final String CONNECTION_STRING = "mongodb://%s:%d";

    private MongodExecutable mongodExecutable;
    private MongoTemplate mongoTemplate;

    @AfterEach
    void clean() {
        mongodExecutable.stop();
    }

    @BeforeEach
    void setup() throws Exception {
        String ip = "localhost";
        int randomPort = SocketUtils.findAvailableTcpPort();

        ImmutableMongodConfig mongodbConfig = MongodConfig
              .builder()
              .version(Version.Main.PRODUCTION)
              .net(new Net(ip, randomPort, Network.localhostIsIPv6()))
              .build();

        MongodStarter starter = MongodStarter.getDefaultInstance();
        mongodExecutable = starter.prepare(mongodbConfig);
        mongodExecutable.start();
        mongoTemplate = new MongoTemplate(MongoClients.create(String.format(CONNECTION_STRING, ip, randomPort)), "test");
    }

    @DisplayName("Given object When save object using MongoDB template Then object can be found")
    @Test
    void test() throws Exception {
        // given
        DBObject objectToSave = BasicDBObjectBuilder.start()
              .add("key", "value")
              .get();

        // when
        mongoTemplate.save(objectToSave, "collection");

        // then
        assertThat(mongoTemplate.findAll(DBObject.class, "collection")).extracting("key")
              .containsOnly("value");
    }
}
```

请注意，我们可以快速创建配置为使用我们手动配置的嵌入式数据库的MongoTemplate bean，并将其注册到Spring容器中。例如，创建一个带有@Bean方法的@TestConfiguration，该方法将返回new MongoTemplate(MongoClients.create(connectionString, "test")。

更多示例可以在[官方Flapdoodle的GitHub仓库](https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo)中找到。

### 3.3 日志

我们可以在运行集成测试时通过将这两个属性添加到src/test/resources/application.properties文件中来为MongoDB配置日志消息：

```properties
logging.level.org.springframework.boot.autoconfigure.mongo.embedded
logging.level.org.mongodb
```

例如，要禁用日志记录，我们只需将值设置为off：

```properties
logging.level.org.springframework.boot.autoconfigure.mongo.embedded=off
logging.level.org.mongodb=off
```

### 3.4 在生产环境中使用真实数据库

由于我们添加de.flapdoodle.embed.mongo依赖项的时候指定了<scope\>test</scope\>，**因此在生产环境中运行时无需禁用嵌入式数据库**。我们所要做的就是指定MongoDB连接的详细信息(例如，主机和端口)。

要在测试之外使用嵌入式数据库，我们可以使用Spring Profile，Spring将根据激活的Profile注册正确的MongoClient(嵌入式或生产)。

我们还需要将生产依赖项的范围更改为<scope\>runtime</scope\>。

## 4. 嵌入式测试争议

一开始，使用嵌入式数据库可能看起来是个好主意。事实上，当我们想要测试我们的应用程序在以下方面是否正确运行时，这是一个很好的方法：

-   对象<->文档映射配置
-   自定义持久化生命周期事件监听器(参考AbstractMongoEventListener)
-   直接使用持久层的任何代码的逻辑

不幸的是，**使用嵌入式服务器不能被视为“完全集成测试”**，Flapdoodle的嵌入式MongoDB不是官方的MongoDB产品。因此，我们无法确定它的行为是否与生产环境中的行为完全相同。

如果我们想在尽可能接近生产的环境中运行通信测试，更好的解决方案是使用Docker这样的环境容器。

## 5. 总结

Spring Boot使运行验证正确文档映射和数据库集成的测试变得极其简单。通过添加正确的Maven依赖项，我们可以立即在Spring Boot集成测试中使用MongoDB组件。

需要记住的是，**嵌入式MongoDB服务器不能被视为“真实”服务器的替代品**。