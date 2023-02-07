##  1. 概述

Netflix Archaius提供了用于连接到许多数据源的库和功能。

**在本教程中，我们将学习如何获取配置**：

-   **使用JDBC API连接到数据库**
-   **从存储在DynamoDB实例中的配置**
-   **通过将Zookeeper配置为动态分布式配置**

关于Netflix Archaius的介绍，请看[这篇](https://www.baeldung.com/netflix-archaius-spring-cloud-integration)文章。

## 2. 将Netflix Archaius与JDBC连接结合使用

**正如我们在介绍性教程中解释的那样，每当我们希望Archaius处理配置时，我们都需要创建一个Apache的AbstractConfiguration bean**。

该bean将被Spring Cloud Bridge自动捕获并添加到Archaius的复合配置堆栈中。

### 2.1 依赖关系

使用JDBC连接到数据库所需的所有功能都包含在核心库中，因此除了我们在介绍性教程中提到的那些之外，我们不需要任何额外的依赖项：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-archaius</artifactId>
    </dependency>
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-netflix</artifactId>
            <version>2.0.1.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

我们可以检查Maven Central以验证我们使用的是最新版本的[入门库](https://search.maven.org/search?q=spring-cloud-starter-netflix-archaius)。

### 2.2 如何创建配置Bean

**在这种情况下，我们需要使用JDBCConfigurationSource实例创建AbstractConfiguration bean**。

要指示如何从JDBC数据库获取值，我们必须指定：

-   一个javax.sql.Datasource对象
-   一个SQL查询字符串，它将检索至少两列，其中包含配置的键及其对应的值
-   两列分别表示属性键和值

让我们继续创建这个bean：

```java
@Autowired
DataSource dataSource;

@Bean
public AbstractConfiguration addApplicationPropertiesSource() {
    PolledConfigurationSource source = new JDBCConfigurationSource(dataSource,
        "select distinct key, value from properties",
        "key",
        "value");
    return new DynamicConfiguration(source, new FixedDelayPollingScheduler());
}
```

### 2.3 试一试

为了保持简单并且仍然有一个可操作的示例，我们将使用一些初始数据设置一个H2内存数据库实例。

为此，我们将首先添加必要的依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>2.0.5.RELEASE</version>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.197</version>
    <scope>runtime</scope>
</dependency>
```

注意：我们可以在Maven Central查看最新版本的[h2](https://search.maven.org/search?q=g:com.h2database)和[spring-boot-starter-data-jpa](https://search.maven.org/search?q=a:spring-boot-starter-data-jpa)库。

接下来，我们将声明将包含我们的属性的JPA实体：

```java
@Entity
public class Properties {
    @Id
    private String key;
    private String value;
}
```

我们将在我们的资源中包含一个data.sql文件，以使用一些初始值填充内存数据库：

```sql
insert into properties
values('tuyucheng.archaius.properties.one', 'one FROM:jdbc_source');
```

最后，为了检查属性在任何给定点的值，我们可以创建一个端点来检索由Archaius管理的值：

```java
@RestController
public class ConfigPropertiesController {

    private DynamicStringProperty propertyOneWithDynamic = DynamicPropertyFactory
          .getInstance()
          .getStringProperty("tuyucheng.archaius.properties.one", "not found!");

    @GetMapping("/properties-from-dynamic")
    public Map<String, String> getPropertiesFromDynamic() {
        Map<String, String> properties = new HashMap<>();
        properties.put(propertyOneWithDynamic.getName(), propertyOneWithDynamic.get());
        return properties;
    }
}
```

如果数据在任何时候发生变化，Archaius将在运行时检测到它并开始检索新值。

当然，这个端点也可以在下一个示例中使用。

## 3. 如何使用DynamoDB实例创建配置源

正如我们在上一节中所做的那样，我们将创建一个功能齐全的项目，以正确分析Archaius如何使用DynamoDB实例作为配置源来管理属性。

### 3.1 依赖关系

让我们将以下库添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk-dynamodb</artifactId>
    <version>1.11.414</version>
</dependency>
<dependency>
    <groupId>com.github.derjust</groupId>
    <artifactId>spring-data-dynamodb</artifactId>
    <version>5.0.3</version>
</dependency>
<dependency>
    <groupId>com.netflix.archaius</groupId>
    <artifactId>archaius-aws</artifactId>
    <version>0.7.6</version>
</dependency>
```

我们可以检查Maven Central以获取最新的依赖项版本，但对于archaius-aws，我们建议坚持使用[Spring Cloud Netflix](https://github.com/spring-cloud/spring-cloud-netflix/blob/main/spring-cloud-netflix-dependencies/pom.xml)库支持的版本。

[aws-java-sdk-dynamodb](https://search.maven.org/search?q=a:aws-java-sdk-dynamodb)依赖项将允许我们设置DynamoDB客户端以连接到数据库。

使用[spring-data-dynamodb](https://search.maven.org/search?q=g:com.github.derjust)库，我们将设置DynamoDB Repository。

最后，我们将使用[archaius-aws](https://search.maven.org/search?q=a:archaius-aws)库来创建AbstractConfiguration。

### 3.2 使用DynamoDB作为配置源

这一次，**将使用DynamoDbConfigurationSource对象创建AbstractConfiguration**： 

```java
@Autowired
AmazonDynamoDB amazonDynamoDb;

@Bean
public AbstractConfiguration addApplicationPropertiesSource() {
    PolledConfigurationSource source = new DynamoDbConfigurationSource(amazonDynamoDb);
    return new DynamicConfiguration(source, new FixedDelayPollingScheduler());
}
```

**默认情况下，Archaius在Dynamo数据库中搜索名为“archaiusProperties”的表，其中包含“key”和“value”属性以用作源**。

如果我们想覆盖这些值，我们必须声明以下系统属性：

-   com.netflix.config.dynamo.tableName
-   com.netflix.config.dynamo.keyAttributeName
-   com.netflix.config.dynamo.valueAttributeName

### 3.3 创建一个功能齐全的示例

正如我们在本[DynamoDB指南](https://www.baeldung.com/spring-data-dynamodb#DynamoDB)中所做的那样，我们将从安装本地DynamoDB实例开始，以轻松测试功能。

我们还将按照指南的说明创建我们之前“自动装配”的AmazonDynamoDB实例。

为了用一些初始数据填充数据库，我们将首先创建一个DynamoDBTable实体来映射数据：

```java
@DynamoDBTable(tableName = "archaiusProperties")
public class ArchaiusProperties {

    @DynamoDBHashKey
    @DynamoDBAttribute
    private String key;

    @DynamoDBAttribute
    private String value;

    // ...getters and setters...
}
```

接下来，我们将为该实体创建一个CrudRepository：

```java
public interface ArchaiusPropertiesRepository extends CrudRepository<ArchaiusProperties, String> {
}
```

最后，我们将使用Repository和AmazonDynamoDB实例创建表并随后插入数据：

```java
@Autowired
private ArchaiusPropertiesRepository repository;

@Autowired
AmazonDynamoDB amazonDynamoDb;

private void initDatabase() {
    DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDb);
    CreateTableRequest tableRequest = mapper
        .generateCreateTableRequest(ArchaiusProperties.class);
    tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
    TableUtils.createTableIfNotExists(amazonDynamoDb, tableRequest);

    ArchaiusProperties property = new ArchaiusProperties("tuyucheng.archaius.properties.one", "one FROM:dynamoDB");
    repository.save(property);
}
```

我们可以在创建DynamoDbConfigurationSource之前立即调用此方法。

现在，我们已经准备好运行该应用程序了。

## 4. 如何设置动态Zookeeper分布式配置

**正如我们之前在**[介绍Zookeeper的文章](https://www.baeldung.com/java-zookeeper)**中看到的那样，此工具的好处之一是可以将其用作分布式配置存储**。

如果我们将它与Archaius结合，我们最终会得到一个灵活且可扩展的配置管理解决方案。

### 4.1 依赖关系

让我们按照[Spring Cloud官方的说明设置](https://cloud.spring.io/spring-cloud-zookeeper/single/spring-cloud-zookeeper.html#spring-cloud-zookeeper-install)更稳定版本的Apache的Zookeeper。

唯一的区别是我们只需要Zookeeper提供的一部分功能，因此我们可以使用spring-cloud-starter-zookeeper-config依赖而不是官方指南中使用的依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zookeeper-config</artifactId>
    <version>2.0.0.RELEASE</version>
    <exclusions>
        <exclusion>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.4.13</version>
    <exclusions>
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

同样，我们可以在Maven Central中检查最新版本的[spring-cloud-starter-zookeeper-config](https://search.maven.org/search?q=a:spring-cloud-starter-zookeeper-config)和[zookeeper](https://search.maven.org/search?q=a:zookeeper)依赖项。

请确保避免使用zookeeper beta版本。

### 4.2 Spring Cloud的自动配置

**正如官方文档中所解释的那样，包括spring-cloud-starter-zookeeper-config依赖项足以设置Zookeeper属性源**。

默认情况下，只有一个源是自动配置的，在config/application Zookeeper节点下搜索属性。因此，此节点用作不同应用程序之间的共享配置源。

此外，如果我们使用spring.application.name属性指定应用程序名称，则会自动配置另一个源，这次是在 config/<app_name\>节点中搜索属性。

**这些父节点下的每个节点名都会指示一个属性键，它们的数据就是属性值**。

对我们来说幸运的是，由于Spring Cloud将这些属性源添加到上下文中，Archaius会自动管理它们。无需以编程方式创建AbstractConfiguration。

### 4.3 准备初始数据

在这种情况下，我们还需要一个本地Zookeeper服务器来将配置存储为节点。我们可以按照[Apache的指南](https://zookeeper.apache.org/doc/r3.1.2/zookeeperStarted.html#sc_InstallingSingleMode)设置一个在端口2181上运行的独立服务器。

要连接到Zookeeper服务并创建一些初始数据，我们将使用[Apache的Curator客户端](https://www.baeldung.com/apache-curator)：

```java
@Component
public class ZookeeperConfigsInitializer {

    @Autowired
    CuratorFramework client;

    @EventListener
    public void appReady(ApplicationReadyEvent event) throws Exception {
        createBaseNodes();
        if (client.checkExists().forPath("/config/application/tuyucheng.archaius.properties.one") == null) {
            client.create()
                  .forPath("/config/application/tuyucheng.archaius.properties.one",
                        "one FROM:zookeeper".getBytes());
        } else {
            client.setData()
                  .forPath("/config/application/tuyucheng.archaius.properties.one",
                        "one FROM:zookeeper".getBytes());
        }
    }

    private void createBaseNodes() throws Exception {
        if (client.checkExists().forPath("/config") == null) {
            client.create().forPath("/config");
        }
        if (client.checkExists().forPath("/config/application") == null) {
            client.create().forPath("/config/application");
        }
    }
}
```

我们可以检查日志以查看属性来源，以验证Netflix Archaius在属性更改后是否刷新了属性。

## 5. 总结

在本文中，我们了解了如何使用Netflix Archaius设置高级配置源。我们必须考虑到它也支持其他来源，例如Etcd、Typesafe、AWS S3文件和JClouds。