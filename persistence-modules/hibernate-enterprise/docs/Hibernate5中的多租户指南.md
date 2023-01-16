## 1. 简介

[多租户](https://docs.jboss.org/hibernate/orm/5.0/userguide/html_single/chapters/multitenancy/MultiTenancy.html)允许多个客户端或租户使用单个资源，或者在本文的上下文中，使用单个数据库实例。目的是将每个租户需要的信息与共享数据库隔离开来。

在本教程中，我们将介绍在 Hibernate 5 中配置多租户的各种方法。

## 2.Maven依赖

我们需要在pom.xml文件中包含[hibernate -core依赖项](https://search.maven.org/classic/#search|ga|1|g%3A"org.hibernate" AND a%3A"hibernate-core")：

```xml
<dependency>
   <groupId>org.hibernate</groupId>
   <artifactId>hibernate-core</artifactId>
   <version>5.2.12.Final</version>
</dependency>
```

为了进行测试，我们将使用 H2 内存数据库，因此我们也[将此依赖](https://search.maven.org/classic/#search|ga|1|g%3A"com.h2database" AND a%3A"h2")项添加到pom.xml文件中：

```xml
<dependency>
   <groupId>com.h2database</groupId>
   <artifactId>h2</artifactId>
   <version>1.4.196</version>
</dependency>
```

## 3. 了解 Hibernate 中的多租户

[正如Hibernate](https://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#multitenacy)官方用户指南中提到的，在 Hibernate 中存在三种实现多租户的方法：

-   独立模式——同一物理数据库实例中每个租户一个模式
-   单独的数据库——每个租户一个单独的物理数据库实例
-   分区(鉴别器)数据——每个租户的数据按鉴别器值进行分区

Hibernate 尚不支持分区(鉴别器)数据方法。跟进[此 JIRA 问题](https://hibernate.atlassian.net/browse/HHH-6054)以获取未来进展。

像往常一样，Hibernate 抽象了每种方法实现的复杂性。

我们所需要的只是提供这两个接口的实现：

-   [MultiTenantConnectionProvider](https://docs.jboss.org/hibernate/orm/current/javadocs/org/hibernate/engine/jdbc/connections/spi/MultiTenantConnectionProvider.html) – 为每个租户提供连接
-   [CurrentTenantIdentifierResolver](https://docs.jboss.org/hibernate/orm/current/javadocs/org/hibernate/context/spi/CurrentTenantIdentifierResolver.html) – 解析要使用的租户标识符

在浏览数据库和模式方法示例之前，让我们更详细地了解每个概念。

### 3.1. 多租户连接提供者

基本上，这个接口为具体的租户标识符提供了一个数据库连接。

让我们看看它的两个主要方法：

```java
interface MultiTenantConnectionProvider extends Service, Wrapped {
    Connection getAnyConnection() throws SQLException;

    Connection getConnection(String tenantIdentifier) throws SQLException;
     // ...
}
```

如果 Hibernate 无法解析要使用的租户标识符，它将使用方法getAnyConnection来获取连接。否则，它将使用方法getConnection。

根据我们定义数据库连接的方式，Hibernate 提供了该接口的两种实现：

-   使用来自Java的[DataSource](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/javax/sql/DataSource.html)接口——我们将使用DataSourceBasedMultiTenantConnectionProviderImpl实现
-   使用来自 Hibernate的ConnectionProvider接口——我们将使用AbstractMultiTenantConnectionProvider实现

### 3.2. 当前租户标识符解析器

有许多可能的方法来解析租户标识符。例如，我们的实现可以使用配置文件中定义的一个租户标识符。

另一种方法是使用路径参数中的租户标识符。

让我们看看这个界面：

```java
public interface CurrentTenantIdentifierResolver {

    String resolveCurrentTenantIdentifier();

    boolean validateExistingCurrentSessions();
}
```

Hibernate 调用resolveCurrentTenantIdentifier方法来获取租户标识符。如果我们希望 Hibernate 验证所有现有会话属于同一个租户标识符，方法validateExistingCurrentSessions应该返回 true。

## 4.模式方法

在此策略中，我们将在同一物理数据库实例中使用不同的模式或用户。当我们需要为我们的应用程序提供最佳性能并且可以牺牲特殊的数据库功能(例如每个租户的备份)时，应该使用这种方法。

此外，我们将模拟CurrentTenantIdentifierResolver接口以提供一个租户标识符作为我们在测试期间的选择：

```java
public abstract class MultitenancyIntegrationTest {

    @Mock
    private CurrentTenantIdentifierResolver currentTenantIdentifierResolver;

    private SessionFactory sessionFactory;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);

        when(currentTenantIdentifierResolver.validateExistingCurrentSessions())
          .thenReturn(false);

        Properties properties = getHibernateProperties();
        properties.put(
          AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, 
          currentTenantIdentifierResolver);

        sessionFactory = buildSessionFactory(properties);

        initTenant(TenantIdNames.MYDB1);
        initTenant(TenantIdNames.MYDB2);
    }

    protected void initTenant(String tenantId) {
        when(currentTenantIdentifierResolver
         .resolveCurrentTenantIdentifier())
           .thenReturn(tenantId);
        createCarTable();
    }
}
```

我们对MultiTenantConnectionProvider接口的实现将设置每次请求连接时使用的模式：

```java
class SchemaMultiTenantConnectionProvider
  extends AbstractMultiTenantConnectionProvider {

    private ConnectionProvider connectionProvider;

    public SchemaMultiTenantConnectionProvider() throws IOException {
        this.connectionProvider = initConnectionProvider();
    }

    @Override
    protected ConnectionProvider getAnyConnectionProvider() {
        return connectionProvider;
    }

    @Override
    protected ConnectionProvider selectConnectionProvider(
      String tenantIdentifier) {
 
        return connectionProvider;
    }

    @Override
    public Connection getConnection(String tenantIdentifier)
      throws SQLException {
 
        Connection connection = super.getConnection(tenantIdentifier);
        connection.createStatement()
          .execute(String.format("SET SCHEMA %s;", tenantIdentifier));
        return connection;
    }

    private ConnectionProvider initConnectionProvider() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass()
          .getResourceAsStream("/hibernate.properties"));

        DriverManagerConnectionProviderImpl connectionProvider 
          = new DriverManagerConnectionProviderImpl();
        connectionProvider.configure(properties);
        return connectionProvider;
    }
}
```

因此，我们将使用一个内存中的 H2 数据库和两个模式——每个租户一个。

让我们配置hibernate.properties以使用模式多租户模式和我们对MultiTenantConnectionProvider接口的实现：

```plaintext
hibernate.connection.url=jdbc:h2:mem:mydb1;DB_CLOSE_DELAY=-1;
  INIT=CREATE SCHEMA IF NOT EXISTS MYDB1;CREATE SCHEMA IF NOT EXISTS MYDB2;
hibernate.multiTenancy=SCHEMA
hibernate.multi_tenant_connection_provider=
  com.baeldung.hibernate.multitenancy.schema.SchemaMultiTenantConnectionProvider
```

出于我们测试的目的，我们配置了hibernate.connection.url属性来创建两个模式。这对于实际应用程序来说不是必需的，因为模式应该已经到位。

对于我们的测试，我们将在租户myDb1中添加一个Car条目。我们将验证此条目是否存储在我们的数据库中，并且它不在租户myDb2 中：

```java
@Test
void whenAddingEntries_thenOnlyAddedToConcreteDatabase() {
    whenCurrentTenantIs(TenantIdNames.MYDB1);
    whenAddCar("myCar");
    thenCarFound("myCar");
    whenCurrentTenantIs(TenantIdNames.MYDB2);
    thenCarNotFound("myCar");
}
```

正如我们在测试中看到的那样，我们在调用whenCurrentTenantIs方法时更改了租户。

## 5.数据库方法

数据库多租户方法为每个租户使用不同的物理数据库实例。由于每个租户都是完全隔离的，所以当我们需要特殊的数据库功能(例如每个租户的备份)而不是最佳性能时，我们应该选择此策略。

对于数据库方法，我们将使用与上面相同的MultitenancyIntegrationTest类和CurrentTenantIdentifierResolver接口。

对于MultiTenantConnectionProvider接口，我们将使用Map集合来获取每个租户标识符的ConnectionProvider：

```java
class MapMultiTenantConnectionProvider
  extends AbstractMultiTenantConnectionProvider {

    private Map<String, ConnectionProvider> connectionProviderMap
     = new HashMap<>();

    public MapMultiTenantConnectionProvider() throws IOException {
        initConnectionProviderForTenant(TenantIdNames.MYDB1);
        initConnectionProviderForTenant(TenantIdNames.MYDB2);
    }

    @Override
    protected ConnectionProvider getAnyConnectionProvider() {
        return connectionProviderMap.values()
          .iterator()
          .next();
    }

    @Override
    protected ConnectionProvider selectConnectionProvider(
      String tenantIdentifier) {
 
        return connectionProviderMap.get(tenantIdentifier);
    }

    private void initConnectionProviderForTenant(String tenantId)
     throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream(
          String.format("/hibernate-database-%s.properties", tenantId)));
        DriverManagerConnectionProviderImpl connectionProvider 
          = new DriverManagerConnectionProviderImpl();
        connectionProvider.configure(properties);
        this.connectionProviderMap.put(tenantId, connectionProvider);
    }
}
```

每个ConnectionProvider都通过配置文件hibernate-database-<tenant identifier>.properties 填充，其中包含所有连接详细信息：

```plaintext
hibernate.connection.driver_class=org.h2.Driver
hibernate.connection.url=jdbc:h2:mem:<Tenant Identifier>;DB_CLOSE_DELAY=-1
hibernate.connection.username=sa
hibernate.dialect=org.hibernate.dialect.H2Dialect
```

最后，让我们再次更新 hibernate.properties以使用数据库多租户模式和我们对MultiTenantConnectionProvider接口的实现：

```java
hibernate.multiTenancy=DATABASE
hibernate.multi_tenant_connection_provider=
  com.baeldung.hibernate.multitenancy.database.MapMultiTenantConnectionProvider
```

如果我们运行与模式方法中完全相同的测试，测试将再次通过。

## 六. 总结

本文介绍了 Hibernate 5 对使用单独数据库和单独模式方法的多租户支持。我们提供了非常简单的实现和示例来探究这两种策略之间的差异。