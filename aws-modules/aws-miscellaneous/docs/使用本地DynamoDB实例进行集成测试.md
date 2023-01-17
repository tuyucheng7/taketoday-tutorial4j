## 1. 概述

如果我们开发一个使用 Amazon 的[DynamoDB](https://aws.amazon.com/dynamodb/)的应用程序，在没有本地实例的情况下开发集成测试可能会很棘手。

在本教程中，我们将探讨为集成测试配置、启动和停止本地 DynamoDB 的多种方法。

本教程还补充了我们现有的[DynamoDB 文章](https://www.baeldung.com/spring-data-dynamodb)。

## 2.配置

### 2.1. Maven 设置

[DynamoDB Local](https://aws.amazon.com/blogs/aws/dynamodb-local-for-desktop-development/)是亚马逊开发的一款支持所有 DynamoDB API 的工具。它不直接操作生产中的实际 DynamoDB 表，而是在本地执行。

首先，我们将 DynamoDB Local 依赖项添加到 Maven 配置中的依赖项列表中：

```xml
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>DynamoDBLocal</artifactId>
    <version>1.11.86</version>
    <scope>test</scope>
</dependency>

```

接下来，我们还需要添加 Amazon DynamoDB 存储库，因为依赖项不存在于 Maven 中央存储库中。

我们可以选择离我们当前 IP 地址地理位置最近的亚马逊服务器：

```xml
<repository>
    <id>dynamodb-local</id>
    <name>DynamoDB Local Release Repository</name>
    <url>https://s3-us-west-2.amazonaws.com/dynamodb-local/release</url>
</repository>
```

### 2.2. 添加 SQLite4Java 依赖

DynamoDB Local 内部使用[SQLite4Java](https://bitbucket.org/almworks/sqlite4java)库；因此，我们还需要在运行测试时包含库文件。SQLite4Java 库文件依赖于测试运行的环境，但是 Maven 可以在我们声明 DynamoDBLocal 依赖项后传递它们。

接下来，我们需要添加一个新的构建步骤，将本机库到我们稍后将在 JVM 系统属性中定义的特定文件夹中。

让我们将传递拉取的 SQLite4Java 库文件到名为native-libs的文件夹中：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <version>2.10</version>
    <executions>
        <execution>
            <id>copy</id>
            <phase>test-compile</phase>
            <goals>
                <goal>copy-dependencies</goal>
            </goals>
            <configuration>
                <includeScope>test</includeScope>
                <includeTypes>so,dll,dylib</includeTypes>
                <outputDirectory>${project.basedir}/native-libs</outputDirectory>
            </configuration>
        </execution>
    </executions>
</plugin>

```

### 2.3. 设置 SQLite4Java 系统属性

现在，我们将使用名为sqlite4java.library.path的 JVM 系统属性来引用之前创建的文件夹(SQLite4Java 库所在的位置) ：

```java
System.setProperty("sqlite4java.library.path", "native-libs");
```

为了稍后成功运行测试，必须将所有 SQLite4Java 库放在由sqlite4java.library.path系统属性定义的文件夹中。我们必须至少运行一次 Maven 测试编译 ( mvn test-compile )才能满足先决条件。

## 3. 设置测试数据库的生命周期 

我们可以在带有@BeforeClass 注解的设置方法中定义创建和启动本地 DynamoDB 服务器的代码；并且，对称地，在带有@AfterClass注解的拆卸方法中停止服务器。

在以下示例中，我们将在端口 8000 上启动本地 DynamoDB 服务器，并确保它在运行我们的测试后再次停止：

```java
public class ProductInfoDAOIntegrationTest {
    private static DynamoDBProxyServer server;

    @BeforeClass
    public static void setupClass() throws Exception {
        System.setProperty("sqlite4java.library.path", "native-libs");
        String port = "8000";
        server = ServerRunner.createServerFromCommandLineArgs(
          new String[]{"-inMemory", "-port", port});
        server.start();
        //...
    }

    @AfterClass
    public static void teardownClass() throws Exception {
        server.stop();
    }

    //...
}
```

我们还可以使用java.net.ServerSocket在任何可用端口而不是固定端口上运行本地 DynamoDB 服务器。在这种情况下，我们还必须配置测试以将端点设置为正确的 DynamoDB 端口：

```java
public String getAvailablePort() throws IOException {
    ServerSocket serverSocket = new ServerSocket(0);
    return String.valueOf(serverSocket.getLocalPort());
}
```

## 4.替代方法：使用@ClassRule

我们可以将前面的逻辑包装在执行相同操作的 JUnit 规则中：

```java
public class LocalDbCreationRule extends ExternalResource {
    private DynamoDBProxyServer server;

    public LocalDbCreationRule() {
        System.setProperty("sqlite4java.library.path", "native-libs");
    }

    @Override
    protected void before() throws Exception {
        String port = "8000";
        server = ServerRunner.createServerFromCommandLineArgs(
          new String[]{"-inMemory", "-port", port});
        server.start();
    }

    @Override
    protected void after() {
        this.stopUnchecked(server);
    }

    protected void stopUnchecked(DynamoDBProxyServer dynamoDbServer) {
        try {
            dynamoDbServer.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }    
    }
}
```

要使用我们的自定义规则，我们必须使用@ClassRule创建并注解一个实例，如下所示。同样，测试将在测试类初始化之前创建并启动本地 DynamoDB 服务器。

请注意，测试规则的访问修饰符必须是公开的才能运行测试：

```java
public class ProductInfoRepositoryIntegrationTest {
    @ClassRule
    public static LocalDbCreationRule dynamoDB = new LocalDbCreationRule();

    //...
}
```

 

在结束之前，快速说明一下——由于 DynamoDB Local 在内部使用 SQLite 数据库，它的性能并不能反映生产中的真实性能。

## 5.总结

在本文中，我们了解了如何设置和配置 DynamoDB Local 以运行集成测试。