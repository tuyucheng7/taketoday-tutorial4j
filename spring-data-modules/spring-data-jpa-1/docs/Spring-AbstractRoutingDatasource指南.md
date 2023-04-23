## 1. 概述

在这篇简短的文章中，我们将把 Spring 的AbstractRoutingDatasource视为一种基于当前上下文动态确定实际DataSource的方法。

因此，我们将看到我们可以将DataSource查找逻辑保留在数据访问代码之外。

## 2.Maven依赖

让我们首先在pom.xml中将spring-context、spring-jdbc、spring-test和h2声明为依赖项：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>4.3.8.RELEASE</version>
    </dependency>

    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-jdbc</artifactId>
        <version>4.3.8.RELEASE</version>
    </dependency>

    <dependency> 
        <groupId>org.springframework</groupId> 
        <artifactId>spring-test</artifactId>
        <version>4.3.8.RELEASE</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>1.4.195</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

可以在[此处](https://search.maven.org/classic/#search|ga|1|(g%3A"org.springframework" AND (a%3A"spring-context" OR a%3A"spring-jdbc" OR a%3A"spring-test")) OR (g%3A"com.h2database" AND a%3A"h2"))找到最新版本的依赖项。

如果你使用的是 Spring Boot，我们可以使用 Spring Data 和 Test 的启动器：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency> 
        <groupId>org.springframework.boot</groupId> 
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>1.4.195</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 3.数据源上下文

AbstractRoutingDatasource需要信息以了解要路由到哪个实际数据源。此信息通常称为上下文。

虽然与AbstractRoutingDatasource一起使用的上下文可以是任何对象，但使用枚举来定义它们。在我们的示例中，我们将使用ClientDatabase的概念作为具有以下实现的上下文：

```java
public enum ClientDatabase {
    CLIENT_A, CLIENT_B
}
```

值得注意的是，在实践中，上下文可以是对相关领域有意义的任何内容。

例如，另一个常见用例涉及使用环境的概念来定义上下文。在这种情况下，上下文可以是一个包含PRODUCTION、DEVELOPMENT和TESTING的枚举。

## 4.上下文持有者

上下文持有者实现是一个将当前上下文存储为[ThreadLocal](https://www.baeldung.com/java-threadlocal)引用的容器。

除了保存引用之外，它还应该包含用于设置、获取和清除它的静态方法。AbstractRoutingDatasource将在 ContextHolder 中查询 Context，然后使用上下文查找实际的DataSource。

在这里使用ThreadLocal非常重要，以便将上下文绑定到当前正在执行的线程。

采用这种方法非常重要，这样当数据访问逻辑跨越多个数据源并使用事务时，行为才可靠：

```java
public class ClientDatabaseContextHolder {

    private static ThreadLocal<ClientDatabase> CONTEXT
      = new ThreadLocal<>();

    public static void set(ClientDatabase clientDatabase) {
        Assert.notNull(clientDatabase, "clientDatabase cannot be null");
        CONTEXT.set(clientDatabase);
    }

    public static ClientDatabase getClientDatabase() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
```

## 5.数据源路由器

我们定义我们的ClientDataSourceRouter来扩展 Spring AbstractRoutingDataSource。我们实施必要的determineCurrentLookupKey方法来查询我们的ClientDatabaseContextHolder并返回适当的键。

AbstractRoutingDataSource实现为我们处理剩下的工作，并透明地返回适当的数据源：

```java
public class ClientDataSourceRouter
  extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return ClientDatabaseContextHolder.getClientDatabase();
    }
}
```

## 六、配置

我们需要一个上下文映射到DataSource对象来配置我们的AbstractRoutingDataSource。如果没有设置上下文，我们还可以指定要使用的默认数据源。

我们使用的DataSource可以来自任何地方，但通常是在运行时创建或使用 JNDI 查找：

```java
@Configuration
public class RoutingTestConfiguration {

    @Bean
    public ClientService clientService() {
        return new ClientService(new ClientDao(clientDatasource()));
    }
 
    @Bean
    public DataSource clientDatasource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        DataSource clientADatasource = clientADatasource();
        DataSource clientBDatasource = clientBDatasource();
        targetDataSources.put(ClientDatabase.CLIENT_A, 
          clientADatasource);
        targetDataSources.put(ClientDatabase.CLIENT_B, 
          clientBDatasource);

        ClientDataSourceRouter clientRoutingDatasource 
          = new ClientDataSourceRouter();
        clientRoutingDatasource.setTargetDataSources(targetDataSources);
        clientRoutingDatasource.setDefaultTargetDataSource(clientADatasource);
        return clientRoutingDatasource;
    }

    // ...
}
```

使用Spring Boot时，你可以在application.properties文件中配置数据源(即 ClientA和 ClientB)：

```ini
#database details for CLIENT_A
client-a.datasource.name=CLIENT_A
client-a.datasource.script=SOME_SCRIPT.sql

#database details for CLIENT_B
client-b.datasource.name=CLIENT_B
client-b.datasource.script=SOME_SCRIPT.sql
```

然后，你可以创建 POJO 来保存你的DataSources的属性：

```java
@Component
@ConfigurationProperties(prefix = "client-a.datasource")
public class ClientADetails {

    private String name;
    private String script;

    // Getters & Setters
}
```

并使用它们来构造你的数据源 bean：

```java
@Autowired
private ClientADetails clientADetails;
@Autowired
private ClientBDetails clientBDetails;

private DataSource clientADatasource() {
EmbeddedDatabaseBuilder dbBuilder = new EmbeddedDatabaseBuilder();
return dbBuilder.setType(EmbeddedDatabaseType.H2)
.setName(clientADetails.getName())
.addScript(clientADetails.getScript())
.build();
}

private DataSource clientBDatasource() {
EmbeddedDatabaseBuilder dbBuilder = new EmbeddedDatabaseBuilder();
return dbBuilder.setType(EmbeddedDatabaseType.H2)
.setName(clientBDetails.getName())
.addScript(clientBDetails.getScript())
.build();
}
```

## 七、用法

使用我们的AbstractRoutingDataSource时，我们首先设置上下文，然后执行我们的操作。我们使用一个服务层，它将上下文作为参数并在委托给数据访问代码之前设置它并在调用后清除上下文。

作为在服务方法中手动清除上下文的替代方法，清除逻辑可以通过 AOP 切点来处理。

重要的是要记住上下文是线程绑定的，尤其是当数据访问逻辑将跨越多个数据源和事务时：

```java
public class ClientService {

    private ClientDao clientDao;

    // standard constructors

    public String getClientName(ClientDatabase clientDb) {
        ClientDatabaseContextHolder.set(clientDb);
        String clientName = this.clientDao.getClientName();
        ClientDatabaseContextHolder.clear();
        return clientName;
    }
}
```

## 八. 总结

在本教程中，我们查看了如何使用 Spring AbstractRoutingDataSource的示例。我们使用客户端的概念实现了一个解决方案——每个客户端都有自己的数据源。