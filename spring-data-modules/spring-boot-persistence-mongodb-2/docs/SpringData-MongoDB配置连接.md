## 1. 简介

在本教程中，我们将看到配置数据库连接的不同方法。**我们将使用[Spring Boot](https://www.baeldung.com/spring-boot)和[Spring Data MongoDB](https://www.baeldung.com/spring-data-mongodb-tutorial)**。探索Spring的灵活配置，我们将为每种方法创建不同的应用程序。因此，我们将能够选择最合适的一个。

## 2. 测试我们的连接

在开始构建我们的应用程序之前，我们将创建一个测试类。其中包含一些可重用的常量：

```java
public class MongoConnectionApplicationLiveTest {
    private static final String HOST = "localhost";
    private static final String PORT = "27017";
    private static final String DB = "tuyucheng";
    private static final String USER = "tuyucheng";
    private static final String PASS = "tu001118";

    // test cases
}
```

**我们的测试包括运行我们的应用程序，然后尝试将文档插入名为“items”的集合中**。插入文档后，我们应该从数据库中收到一个“_id”，我们认为测试成功。让我们为此创建一个工具方法：

```java
private void assertInsertSucceeds(ConfigurableApplicationContext context) {
    String name = "A";

    MongoTemplate mongo = context.getBean(MongoTemplate.class);
    Document doc = Document.parse("{\"name\":\"" + name + "\"}");
    Document inserted = mongo.insert(doc, "items");

    assertNotNull(inserted.get("_id"));
    assertEquals(inserted.get("name"), name);
}
```

我们的方法从我们的应用程序接收Spring上下文，以便我们可以检索[MongoTemplate](https://www.baeldung.com/spring-data-mongodb-tutorial#mongotemplate-and-mongorepository)实例。之后，我们使用Document.parse()从字符串构建一个简单的JSON文档。

**这样，我们就不需要创建Repository或文档类**。然后，在插入之后，我们断言插入文档中的属性是我们所期望的。

## 3. 通过application.properties进行最小设置

我们的第一个示例是配置连接的最常见方式。**我们只需要在我们的application.properties中提供我们的数据库信息**：

```properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=tuyucheng
spring.data.mongodb.username=tuyucheng
spring.data.mongodb.password=tu001118
```

所有可用的属性都位于Spring Boot的MongoProperties类中。我们还可以使用此类来检查默认值。**我们可以通过应用程序参数在我们的属性文件中定义任何配置**。我们将在下一节中看到它是如何工作的。

在我们的应用程序类中，我们不需要任何特殊的东西来启动和运行：

```java
@SpringBootApplication
public class SpringMongoConnectionViaPropertiesApp {

    public static void main(String... args) {
        SpringApplication.run(SpringMongoConnectionViaPropertiesApp.class, args);
    }
}
```

**此配置是我们连接到数据库实例所需的全部**。@SpringBootApplication注解包括[@EnableAutoConfiguration](https://www.baeldung.com/spring-boot-custom-auto-configuration)，它会根据我们的类路径发现我们的应用程序是一个MongoDB应用程序。

为了测试它，我们可以使用SpringApplicationBuilder来获取对应用程序上下文的引用。**然后，为了断言我们的连接有效，我们使用之前创建的assertInsertSucceeds方法**：

```java
@Test
void whenPropertiesConfig_thenInsertSucceeds() {
    SpringApplicationBuilder app = new SpringApplicationBuilder(SpringMongoConnectionViaPropertiesApp.class)
    app.run();

    assertInsertSucceeds(app.context());
}
```

最后，我们的应用程序使用我们的application.properties文件成功连接。

### 3.1 使用命令行参数覆盖属性

在使用[命令行参数](https://www.baeldung.com/java-command-line-arguments)运行我们的应用程序时，我们可以覆盖我们的属性文件。当使用[java命令](https://www.baeldung.com/java-run-jar-with-arguments)、[mvn命令](https://www.baeldung.com/spring-boot-run-maven-vs-executable-jar)或IDE配置运行时，这些将传递给应用程序。提供这些的方法将取决于我们使用的命令。

**让我们看一个[使用mvn运行我们的Spring Boot应用程序](https://www.baeldung.com/spring-boot-command-line-arguments)的示例**：

```shell
mvn spring-boot:run -Dspring-boot.run.arguments='--spring.data.mongodb.port=27017 --spring.data.mongodb.host=localhost'
```

要使用它，我们将属性指定为spring-boot.run.arguments参数的值。我们使用相同的属性名称，但在它们前面加上两个破折号。**从Spring Boot 2开始，多个属性应该用空格分隔**。最后，运行命令后，应该不会出现错误。

以这种方式配置的选项始终优先于属性文件。**当我们需要在不更改属性文件的情况下更改应用程序参数时，此选项很有用**。例如，如果我们的凭据已更改并且我们无法再连接。

为了在我们的测试中模拟这一点，我们可以在运行我们的应用程序之前设置系统属性。此外，我们可以使用properties方法覆盖我们的application.properties：

```java
@Test
public void givenPrecedence_whenSystemConfig_thenInsertSucceeds() {
    System.setProperty("spring.data.mongodb.host", HOST);
    System.setProperty("spring.data.mongodb.port", PORT);
    System.setProperty("spring.data.mongodb.database", DB);
    System.setProperty("spring.data.mongodb.username", USER);
    System.setProperty("spring.data.mongodb.password", PASS);

    SpringApplicationBuilder app = new SpringApplicationBuilder(SpringMongoConnectionViaPropertiesApp.class)
        .properties(
            "spring.data.mongodb.host=oldValue",
            "spring.data.mongodb.port=oldValue",
            "spring.data.mongodb.database=oldValue",
            "spring.data.mongodb.username=oldValue",
            "spring.data.mongodb.password=oldValue"
        );
    app.run();

    assertInsertSucceeds(app.context());
}
```

**因此，我们属性文件中的旧值不会影响我们的应用程序，因为系统属性具有更高的优先级**。当我们需要在不更改代码的情况下使用新的连接详细信息重新启动应用程序时，这会很有用。

### 3.2 使用连接URI属性

也可以使用单个属性而不是单个主机、端口等：

```properties
spring.data.mongodb.uri="mongodb://admin:password@localhost:27017/tuyucheng"
```

**此属性包括初始属性中的所有值，因此我们不需要指定所有五个**。让我们检查一下基本格式：

```properties
mongodb://<username>:<password>@<host>:<port>/<database>
```

URI中的database部分更具体地说是默认的[auth DB](https://www.mongodb.com/docs/master/reference/connection-string/#std-label-connections-standard-connection-string-format)。**最重要的是，spring.data.mongodb.uri属性不能与主机、端口和凭据的单独属性一起指定**。否则，我们将在运行应用程序时收到以下错误：

```java
@Test
public void givenConnectionUri_whenAlsoIncludingIndividualParameters_thenInvalidConfig() {
    System.setProperty(
            "spring.data.mongodb.uri", "mongodb://" + USER + ":" + PASS + "@" + HOST + ":" + PORT + "/" + DB
    );

    SpringApplicationBuilder app = new SpringApplicationBuilder(SpringMongoConnectionViaPropertiesApp.class)
        .properties(
            "spring.data.mongodb.host=" + HOST,
            "spring.data.mongodb.port=" + PORT,
            "spring.data.mongodb.username=" + USER,
            "spring.data.mongodb.password=" + PASS
        );

    BeanCreationException e = assertThrows(BeanCreationException.class, () -> {
        app.run();
    });

    Throwable rootCause = e.getRootCause();
    assertTrue(rootCause instanceof IllegalStateException);
    assertThat(rootCause.getMessage().contains("Invalid mongo configuration, either uri or host/port/credentials/replicaSet must be specified"));
}
```

**最后，这个配置选项不仅更短，而且有时是必需的**。那是因为某些选项只能通过连接字符串使用。例如，使用[mongodb+srv](https://www.mongodb.com/developer/products/mongodb/srv-connection-strings/)连接到副本集。因此，我们将在下一个示例中仅使用这个更简单的配置属性。

## 4. 使用MongoClient进行Java设置

**MongoClient表示我们与MongoDB数据库的连接，并且始终在后台创建**。但是，我们也可以通过编程方式设置它。尽管比较冗长，但这种方法有一些优点。让我们在接下来的部分中看看它们。

### 4.1 通过AbstractMongoClientConfiguration连接

在我们的第一个示例中，我们将在应用程序类中扩展Spring Data MongoDB的AbstractMongoClientConfiguration类：

```java
@SpringBootApplication
public class SpringMongoConnectionViaClientApp extends AbstractMongoClientConfiguration {
    // main method
}
```

接下来，让我们注入我们需要的属性：

```java
@Value("${spring.data.mongodb.uri}")
private String uri;

@Value("${spring.data.mongodb.database}")
private String db;
```

澄清一下，这些属性可以是硬编码的。此外，他们可以使用与预期的Spring Data变量不同的名称。**最重要的是，这一次，我们使用URI而不是单独的连接属性，后者不能混合使用**。因此，我们不能为这个应用程序重用我们的application.properties，我们应该将其移动到其他地方。

AbstractMongoClientConfiguration要求我们覆盖getDatabaseName()。这是因为URI中不需要数据库名称：

```java
protected String getDatabaseName() {
    return db;
}
```

此时，因为我们正在使用默认的Spring Data变量，所以我们已经能够连接到我们的数据库。此外，如果数据库不存在，MongoDB会创建该数据库。让我们测试一下：

```java
@Test
public void whenClientConfig_thenInsertSucceeds() {
    SpringApplicationBuilder app = new SpringApplicationBuilder(SpringMongoConnectionViaClientApp.class);
    app.web(WebApplicationType.NONE)
        .run(
            "--spring.data.mongodb.uri=mongodb://" + USER + ":" + PASS + "@" + HOST + ":" + PORT + "/" + DB,
            "--spring.data.mongodb.database=" + DB
        );

    assertInsertSucceeds(app.context());
}
```

最后，我们可以覆盖mongoClient()以获得优于传统配置的优势。**此方法将使用我们的URI变量来构建MongoDB客户端。这样，我们就可以直接引用它**。例如，这使我们能够列出我们连接中可用的所有数据库：

```java
@Override
public MongoClient mongoClient() {
    MongoClient client = MongoClients.create(uri);
    ListDatabasesIterable<Document> databases = client.listDatabases();
    databases.forEach(System.out::println);
    return client;
}
```

如果我们想要完全控制MongoDB客户端的创建，以这种方式配置连接很有用。

### 4.2 创建自定义MongoClientFactoryBean

在下一个示例中，我们将创建一个MongoClientFactoryBean。**这一次，我们将创建一个名为custom.uri的属性来保存我们的连接配置**：

```java
@SpringBootApplication
public class SpringMongoConnectionViaFactoryApp {

    // main method

    @Bean
    public MongoClientFactoryBean mongo(@Value("${custom.uri}") String uri) {
        MongoClientFactoryBean mongo = new MongoClientFactoryBean();
        ConnectionString conn = new ConnectionString(uri);
        mongo.setConnectionString(conn);

        MongoClient client = mongo.getObject();
        client.listDatabaseNames().forEach(System.out::println);
        return mongo;
    }
}
```

**使用这种方法，我们不需要扩展AbstractMongoClientConfiguration**。此外，我们可以控制MongoClient的创建。例如，通过调用mongo.setSingleton(false)，我们每次调用mongo.getObject()时都会得到一个新的客户端，而不是单例。

### 4.3 使用MongoClientSettingsBuilderCustomizer设置连接详细信息

在我们的最后一个示例中，我们将使用MongoClientSettingsBuilderCustomizer：

```java
@SpringBootApplication
public class SpringMongoConnectionViaBuilderApp {

    // main method

    @Bean
    public MongoClientSettingsBuilderCustomizer customizer(@Value("${custom.uri}") String uri) {
        ConnectionString connection = new ConnectionString(uri);
        return settings -> settings.applyConnectionString(connection);
    }
}
```

我们使用此类来自定义连接的某些部分，但其余部分仍具有自动配置。**当我们需要以编程方式设置几个属性时很有用**。

## 5. 总结

在本文中，我们看到了Spring Data MongoDB带来的不同工具。我们使用它们以不同的方式建创建连接。此外，我们构建了测试用例以保证我们的配置按预期工作。同时，我们看到了配置优先级如何影响我们的连接属性。