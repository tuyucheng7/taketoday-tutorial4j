## 1. 概述

在本教程中，我们将学习如何使用Flapdoodle的嵌入式MongoDB与Spring Boot一利运行MongoDB集成测试。

MongoDB是一种广泛使用的NoSQL文档数据库。由于高可扩展性、内置分片和出色的社区支持，它通常被许多开发人员视为“NoSQL存储”。

与任何其他持久层技术一样，能够轻松测试数据库与应用程序其余部分的集成至关重要。值得庆幸的是，Spring Boot允许我们轻松编写此类测试。

## 2. 依赖

```
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.6.1</version>
    <relativePath />
</parent>
```

由于我们添加了spring boot parent，因此可以添加所需的依赖项，而无需指定它们的版本：

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

spring-boot-starter-data-mongodb将为MongoDB启用Spring支持：

```
<dependency>
    <groupId>de.flapdoodle.embed</groupId>
    <artifactId>de.flapdoodle.embed.mongo</artifactId>
    <scope>test</scope>
</dependency>
```

de.flapdoodle.embed.mongo为集成测试提供嵌入式MongoDB。

## 3. 使用嵌入式MongoDB进行测试

本节涵盖两个场景：Spring Boot测试和手动测试。

### 3.1Spring BootTest

添加de.flapdoodle.embed.mongo依赖后，Spring Boot在运行测试时会自动尝试下载并启动嵌入式MongoDB。

每个版本只下载一次包，以便后续测试运行得更快。

在这个阶段，我们应该能够开始并通过示例JUnit 5集成测试：

```java

@ContextConfiguration(classes = SpringBootPersistenceApplication.class)
@DataMongoTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
public class MongoDbSpringIntegrationTest {
    @DisplayName("Given object When save object using MongoDB template Then object can be found")
    @Test
    public void test(@Autowired MongoTemplate mongoTemplate) {
        DBObject objectToSave = BasicDBObjectBuilder.start()
                .add("key", "value")
                .get();

        mongoTemplate.save(objectToSave, "collection");

        assertThat(mongoTemplate.findAll(DBObject.class, "collection")).extracting("key").containsOnly("value");
    }
}
```

如我们所见，嵌入式数据库是由Spring自动启动的，它也应该在控制台中打印以下日志：

```
...Starting MongodbExampleApplicationTests on arroyo with PID 10413...
```

### 3.2 手动配置测试

Spring Boot会自动启动并配置嵌入式数据库，然后为我们注入MongoTemplate实例。
但是，有时我们可能需要手动配置嵌入式Mongo数据库(例如，在测试特定数据库版本时)。

以下片段显示了我们如何手动配置嵌入式MongoDB实例。这大致相当于之前的Spring测试：

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

        ImmutableMongodConfig mongodConfig = MongodConfig
                .builder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(ip, randomPort, Network.localhostIsIPv6()))
                .build();

        MongodStarter starter = MongodStarter.getDefaultInstance();
        mongodExecutable = starter.prepare(mongodConfig);
        mongodExecutable.start();
        mongoTemplate = new MongoTemplate(MongoClients.create(String.format(CONNECTION_STRING, ip, randomPort)), "test");
    }

    @DisplayName("Given object When save object using MongoDB template Then object can be found")
    @Test
    void test() {
        // given
        DBObject objectToSave = BasicDBObjectBuilder.start()
                .add("key", "value")
                .get();

        mongoTemplate.save(objectToSave, "collection");

        assertThat(mongoTemplate.findAll(DBObject.class, "collection")).extracting("key")
                .containsOnly("value");
    }
}
```

请注意，我们可以快速创建配置为使用我们手动配置的嵌入式数据库的MongoTemplate bean，并将其注册到Spring容器中，
只需创建例如带有@Bean方法的@TestConfiguration，该方法将返回new MongoTemplate(MongoClients.create(connectionString, "
test")。

更多示例可以在官方Flapdoodle的[GitHub仓库](https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo)中找到。

### 3.3 日志

通过将这两个属性添加到src/test/resources/application.propertes文件中，我们可以在运行集成测试时为MongoDB配置日志记录消息：

```
logging.level.org.springframework.boot.autoconfigure.mongo.embedded
logging.level.org.mongodb
```

例如，要禁用日志记录，我们只需将值设置为off：

```
logging.level.org.springframework.boot.autoconfigure.mongo.embedded=off
logging.level.org.mongodb=off
```

### 3.4 在生产中使用真实数据库

由于我们使用添加de.flapdoodle.embed.mongo依赖时包含了<scope>test</scope>标签，因此在生产环境中运行时无需禁用嵌入式数据库。
我们所要做的就是指定MongoDB连接详细信息(例如，主机和端口)。

要在测试之外使用嵌入式数据库，我们可以使用Spring profile，该profile将根据激活的profile注册正确的MongoClient(嵌入式或生产)。

我们还需要将生产依赖的范围更改为<scope>runtime</scope>。

## 4. 嵌入式测试争议

一开始使用嵌入式数据库可能看起来是个好主意。事实上，当我们想要测试我们的应用程序在以下方面是否正确运行时，这是一个很好的方法：

+ 对象<->文档映射配置
+ 自定义持久层生命周期事件监听器(参考AbstractMongoEventListener)
+ 任何直接与持久层一起工作的代码的逻辑

不幸的是，使用嵌入式服务器不能被视为“完全集成测试”。 Flapdoodle的嵌入式MongoDB不是官方的MongoDB产品。
因此，我们不能确定它在生产环境中的行为是否完全相同。

如果我们想在尽可能接近生产的环境中运行通信测试，更好的解决方案是使用环境容器，例如Docker。

## 5. 总结

Spring Boot使得运行验证正确的文档映射和数据库集成的测试变得非常简单。通过添加正确的Maven依赖，我们可以立即在Spring
Boot集成测试中使用MongoDB组件。

我们需要记住，嵌入式MongoDB服务器不能被视为“真实”服务器的替代品。