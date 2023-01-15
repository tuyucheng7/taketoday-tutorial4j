## 1. 简介

在本教程中，我们介绍Java TestContainers库。它允许我们在测试中使用Docker容器，因此，我们可以编写依赖于外部资源的独立集成测试。

我们可以在测试中使用任何具有docker镜像的资源。例如，有用于数据库、Web浏览器、Web服务器和消息队列的镜像。因此，我们可以在测试中将它们作为容器运行。

## 2. 要求

TestContainers库可用于Java 8及更高版本。此外，它与JUnit Rules API兼容。

首先，我们为核心功能定义maven依赖项：

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.16.3</version>
</dependency>
```

还有一些专门用于容器的模块。在本教程中，我们将使用PostgreSQL和Selenium：

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql </artifactId>
    <version>1.11.4</version>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>selenium </artifactId>
    <version>1.11.4</version>
</dependency>
```

**此外，我们需要Docker来运行容器**。有关安装说明，请参阅[Docker文档](https://docs.docker.com/install/)。确保你能够在测试环境中运行Docker容器。

## 3. 用法

下面我们配置一个通用的容器Rule：

```java
@ClassRule
public static GenericContainer simpleWebServer =
		new GenericContainer("alpine:3.2")
				.withExposedPorts(80)
				.withCommand("/bin/sh", "-c", "while true; do echo " + "\"HTTP/1.1 200 OK\n\nHello World!\" | nc -l -p 80; done");
```

我们通过指定docker镜像名称来构建一个GenericContainer测试Rule。然后，我们使用构建器方法对其进行配置：

-   我们使用withExposedPorts从容器中公开一个端口
-   withCommand定义了一个容器命令，它将在容器启动时执行

该Rule使用@ClassRule进行标注。因此，它将在该类中的任何测试运行之前启动Docker容器。所有方法执行完毕后，容器将被销毁。

如果你使用@Rule注解，则GenericContainer Rule将为每个测试方法启动一个新容器。当该测试方法完成时，它将停止容器。

**我们可以使用IP地址和端口与容器中运行的进程进行通信**：

```java
@Test
public void givenSimpleWebServerContainer_whenGetReuqest_thenReturnsResponse() throws Exception {
	String address = "http://"
			+ simpleWebServer.getContainerIpAddress()
			+ ":" + simpleWebServer.getMappedPort(80);
	String response = simpleGetRequest(address);
    
	assertEquals("Hello World!", response);
}
```

## 4. 使用方式

测试容器有多种使用模式，我们在上面演示了一个运行GenericContainer的示例。

TestContainers库还具有带有专用功能的Rule定义。它们用于MySQL、PostgreSQL等常见数据库的容器；以及其他一些如Web客户端的容器。

尽管我们可以将它们作为通用容器运行，但专用的Rule提供了扩展的便利方法。

### 4.1 数据库

假设我们需要一个用于数据访问层集成测试的数据库服务器，我们可以借助TestContainers库在容器中运行数据库。

例如，我们使用PostgreSQLContainer Rule启动一个PostgreSQL容器。然后，我们可以使用工具方法，**这些是用于数据库连接的getJdbcUrl、getUsername、getPassword**：

```java
@Testable
public class PostgreSqlContainerLiveTest {
    @Rule
    public PostgreSQLContainer postgresContainer = new PostgreSQLContainer();

    @Test
    public void whenSelectQueryExecuted_thenResultsReturned() throws Exception {
        ResultSet resultSet = performQuery(postgresContainer, "SELECT 1");
        resultSet.next();
        int result = resultSet.getInt(1);
        
        assertEquals(1, result);
    }

    private ResultSet performQuery(PostgreSQLContainer postgres, String query) throws SQLException {
        String jdbcUrl = postgres.getJdbcUrl();
        String username = postgres.getUsername();
        String password = postgres.getPassword();
        Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
        return conn.createStatement()
                .executeQuery(query);
    }
}
```

也可以将PostgreSQL作为通用容器运行，但是配置连接会更加困难。

### 4.2 WebDriver

另一个有用的场景是使用Web浏览器运行容器。BrowserWebDriverContainer Rule允许在docker -selenium容器中运行Chrome和Firefox。然后，我们使用RemoteWebDriver管理它们。 

**这对于自动化Web应用程序的UI/验收测试非常有用**：

```java
public class WebDriverContainerLiveTest {
    @Rule
    public BrowserWebDriverContainer chrome = new BrowserWebDriverContainer()
            .withCapabilities(new ChromeOptions());

    @Test
    public void whenNavigatedToPage_thenHeadingIsInThePage() {
        RemoteWebDriver driver = chrome.getWebDriver();
        driver.get("http://example.com");
        String heading = driver.findElement(By.xpath("/html/body/div/h1")).getText();
        
        assertEquals("Example Domain", heading);
    }
}
```

### 4.3 Docker Compose

如果测试需要更复杂的服务，我们可以在docker-compose文件中指定它们 ：

```yaml
simpleWebServer:
    image: alpine:3.2
    command: [ "/bin/sh", "-c", "while true; do echo 'HTTP/1.1 200 OKnnHello World!' | nc -l -p 80; done" ]
```

然后，我们使用DockerComposeContainer Rule；此Rule将启动并运行compose文件中定义的服务。

**我们使用getServiceHost和getServicePost方法来建立到服务的连接地址**：

```java
public class DockerComposeContainerLiveTest {
	@ClassRule
	public static DockerComposeContainer compose = new DockerComposeContainer(new File("src/test/resources/test-compose.yml"))
					.withExposedService("simpleWebServer_1", 80);

	@Test
	public void givenSimpleWebServerContainer_whenGetRequest_thenReturnsResponse() throws Exception {
		String address = "http://" + compose.getServiceHost("simpleWebServer_1", 80)
				+ ":" + compose.getServicePort("simpleWebServer_1", 80);
		String response = simpleGetRequest(address);

		assertEquals("Hello World!", response);
	}

	// simpleGetRequest method ...
}
```

## 5. 总结

我们演示了如何使用TestContainers库来简化开发和运行集成测试。

我们对给定docker镜像的容器使用了GenericContainer Rule。然后，我们介绍了PostgreSQLContainer、BrowserWebDriverContainer和DockerComposeContainerRule，它们为特定用例提供了更多功能。