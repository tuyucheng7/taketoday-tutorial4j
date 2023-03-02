## 1. 概述

在本文中，我们将探讨**Feign Client的集成测试**。

我们将创建一个基本的[Open Feign Client](https://www.baeldung.com/spring-cloud-openfeign)，**我们将在[WireMock](https://www.baeldung.com/introduction-to-wiremock)的帮助下为其编写一个简单的集成测试**。

之后，**我们将向我们的客户端添加一个[Ribbon](https://www.baeldung.com/spring-cloud-rest-client-with-netflix-ribbon)配置**，并为其构建一个集成测试。最后，我们将**配置一个[Eureka](https://www.baeldung.com/spring-cloud-netflix-eureka)测试容器**并测试此设置以确保我们的整个配置按预期工作。

## 2. Feign Client

要设置我们的Feign客户端，我们应该首先添加[Spring Cloud OpenFeign](https://search.maven.org/artifact/org.springframework.cloud/spring-cloud-starter-openfeign) Maven依赖项：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

之后，让我们为我们的模型创建一个Book类：

```java
public class Book {
	private String title;
	private String author;
}
```

最后，让我们创建我们的Feign Client接口：

```java
@FeignClient(value="simple-books-client", url="${book.service.url}")
public interface BooksClient {

	@RequestMapping("/books")
	List<Book> getBooks();
}
```

现在，**我们有一个Feign客户端，它从REST服务中检索图书列表**。现在，让我们继续编写一些集成测试。

## 3. WireMock

### 3.1 设置WireMock服务器

如果我们想测试我们的BooksClient，**我们需要一个提供/books端点的模拟服务**。我们的客户端将针对此模拟服务进行调用。为此，我们将使用WireMock。

因此，让我们添加[WireMock](https://search.maven.org/artifact/com.github.tomakehurst/wiremock) Maven依赖项：

```xml
<dependency>
	<groupId>com.github.tomakehurst</groupId>
	<artifactId>wiremock</artifactId>
	<scope>test</scope>
</dependency>
```

并配置Mock服务器：

```java
@TestConfiguration
public class WireMockConfig {

	@Autowired
	private WireMockServer wireMockServer;

	@Bean(initMethod = "start", destroyMethod = "stop")
	public WireMockServer mockBooksService() {
		return new WireMockServer(9561);
	}
}
```

我们现在有一个正在运行的模拟服务器接收端口9651上的连接。

### 3.2 设置Mock

让我们将属性book.service.url添加到指向WireMockServer端口的application-test.yml中：

```yaml
book:
    service:
        url: http://localhost:9561
```

让我们也为/books端点准备一个模拟响应get-books-response.json：

```json
[
	{
		"title": "Dune",
		"author": "Frank Herbert"
	},
	{
		"title": "Foundation",
		"author": "Isaac Asimov"
	}
]
```

现在让我们为/books端点上的GET请求配置模拟响应：

```java
public class BookMocks {

	public static void setupMockBooksResponse(WireMockServer mockService) throws IOException {
		mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/books"))
			.willReturn(WireMock.aResponse()
				.withStatus(HttpStatus.OK.value())
				.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.withBody(
					copyToString(
						BookMocks.class.getClassLoader().getResourceAsStream("payload/get-books-response.json"),
						defaultCharset()))));
	}
}
```

此时，所有必需的配置都已就绪。让我们继续写我们的第一个测试。

## 4. 我们的第一次集成测试

让我们创建一个集成测试BooksClientIntegrationTest：

```java
@SpringBootTest
@ActiveProfiles("test")
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { WireMockConfig.class })
class BooksClientIntegrationTest {

	@Autowired
	private WireMockServer mockBooksService;

	@Autowired
	private BooksClient booksClient;

	@BeforeEach
	void setUp() throws IOException {
		BookMocks.setupMockBooksResponse(mockBooksService);
	}

	// ...
}
```

此时，我们有一个配置有WireMockServer的SpringBootTest，准备好在BooksClient调用/books端点时返回预定义的Books列表。

最后，让我们添加我们的测试方法：

```java
@Test
public void whenGetBooks_thenBooksShouldBeReturned() {
    assertFalse(booksClient.getBooks().isEmpty());
}

@Test
public void whenGetBooks_thenTheCorrectBooksShouldBeReturned() {
    assertTrue(booksClient.getBooks()
      	.containsAll(asList(
        	new Book("Dune", "Frank Herbert"),
        	new Book("Foundation", "Isaac Asimov"))));
}
```

## 5. 与Ribbon集成

**现在让我们通过添加Ribbon提供的负载均衡功能来改进我们的客户端**。

我们需要做的就是在客户端接口中删除硬编码的服务URL，而是通过服务名称book-service引用服务：

```java
@FeignClient("books-service")
public interface BooksClient {
// ...
```

接下来，添加[Netflix Ribbon](https://search.maven.org/artifact/org.springframework.cloud/spring-cloud-starter-netflix-ribbon) Maven依赖项：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
</dependency>
```

最后，在application-test.yml文件中，我们现在应该删除book.service.url并定义Ribbon listOfServers：

```yaml
books-service:
    ribbon:
        listOfServers: http://localhost:9561
```

现在让我们再次运行BooksClientIntegrationTest。它应该通过，确认新设置按预期工作。

### 5.1 动态端口配置

**如果我们不想对服务器的端口进行硬编码，我们可以将WireMock配置为在启动时使用动态端口**。

为此，让我们创建另一个测试配置RibbonTestConfig：

```java
@TestConfiguration
@ActiveProfiles("ribbon-test")
public class RibbonTestConfig {

	@Autowired
	private WireMockServer mockBooksService;

	@Autowired
	private WireMockServer secondMockBooksService;

	@Bean(initMethod = "start", destroyMethod = "stop")
	public WireMockServer mockBooksService() {
		return new WireMockServer(options().dynamicPort());
	}

	@Bean(name="secondMockBooksService", initMethod = "start", destroyMethod = "stop")
	public WireMockServer secondBooksMockService() {
		return new WireMockServer(options().dynamicPort());
	}

	@Bean
	public ServerList ribbonServerList() {
		return new StaticServerList<>(
			new Server("localhost", mockBooksService.port()),
			new Server("localhost", secondMockBooksService.port()));
	}
}
```

此配置设置了两个WireMock服务器，每个服务器都在运行时动态分配的不同端口上运行。此外，它还使用两个模拟服务器配置Ribbon服务器列表。

### 5.2 负载均衡测试

现在我们已经配置了Ribbon负载均衡器，**让我们确保我们的BooksClient在两个模拟服务器之间正确交替**：

```java
@SpringBootTest
@ActiveProfiles("ribbon-test")
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { RibbonTestConfig.class })
class LoadBalancerBooksClientIntegrationTest {

	@Autowired
	private WireMockServer mockBooksService;

	@Autowired
	private WireMockServer secondMockBooksService;

	@Autowired
	private BooksClient booksClient;

	@BeforeEach
	void setUp() throws IOException {
		setupMockBooksResponse(mockBooksService);
		setupMockBooksResponse(secondMockBooksService);
	}

	@Test
	void whenGetBooks_thenRequestsAreLoadBalanced() {
		for (int k = 0; k < 10; k++) {
			booksClient.getBooks();
		}

		mockBooksService.verify(
			moreThan(0), getRequestedFor(WireMock.urlEqualTo("/books")));
		secondMockBooksService.verify(
			moreThan(0), getRequestedFor(WireMock.urlEqualTo("/books")));
	}

	@Test
	public void whenGetBooks_thenTheCorrectBooksShouldBeReturned() {
		assertTrue(booksClient.getBooks()
			.containsAll(asList(
				new Book("Dune", "Frank Herbert"),
				new Book("Foundation", "Isaac Asimov"))));
	}
}
```

## 6. Eureka集成

到目前为止，我们已经了解了如何测试使用Ribbon进行负载均衡的客户端。但是**如果我们的设置使用像Eureka这样的服务发现系统呢？我们应该编写一个集成测试，以确保我们的BooksClient在这种情况下也能按预期工作**。

为此，我们将**运行一个Eureka服务器作为测试容器**。然后我们启动并使用我们的Eureka容器注册一个模拟图书服务。最后，一旦安装完成，我们就可以对其运行测试。

在继续之前，让我们添加[Testcontainers](https://search.maven.org/artifact/org.testcontainers/testcontainers)和[Netflix Eureka Client](https://search.maven.org/artifact/org.springframework.cloud/spring-cloud-starter-netflix-eureka-client) Maven依赖项：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <scope>test</scope>
</dependency>
```

### 6.1 测试容器设置

让我们创建一个TestContainer配置来启动我们的Eureka服务器：

```java
public class EurekaContainerConfig {

	public static class Initializer implements ApplicationContextInitializer {

		public static GenericContainer eurekaServer = new GenericContainer("springcloud/eureka").withExposedPorts(8761);

		@Override
		public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
			Startables.deepStart(Stream.of(eurekaServer)).join();

			TestPropertyValues
				.of("eureka.client.serviceUrl.defaultZone=http://localhost:"
					+ eurekaServer.getFirstMappedPort().toString()
					+ "/eureka")
				.applyTo(configurableApplicationContext);
		}
	}
}
```

如我们所见，上面的Initializer启动了容器。然后它公开Eureka服务器正在监听的端口8761。

最后，在Eureka服务启动后，我们需要更新eureka.client.serviceUrl.defaultZone属性。这定义了用于服务发现的Eureka服务器的地址。

### 6.2 注册模拟服务器

现在我们的Eureka服务器已经启动并正在运行，我们需要注册一个mock books-service。我们通过简单地创建一个RestController来做到这一点：

```java
@Configuration
@RestController
@ActiveProfiles("eureka-test")
public class MockBookServiceConfig {

	@RequestMapping("/books")
	public List getBooks() {
		return Collections.singletonList(new Book("Hitchhiker's Guide to the Galaxy", "Douglas Adams"));
	}
}
```

为了注册这个控制器，我们现在要做的就是确保我们的application-eureka-test.yml中的spring.application.name属性是books-service，与BooksClient接口中使用的服务名称相同。

注意：现在netflix-eureka-client库在我们的依赖项列表中，默认情况下将使用Eureka进行服务发现。因此，**如果我们希望之前不使用Eureka的测试继续通过，我们需要手动将eureka.client.enabled设置为false**。这样，即使库存在于路径上，BooksClient也不会尝试使用Eureka来查找服务，而是使用Ribbon配置。

### 6.3 集成测试

再一次，我们拥有所有需要的配置部分，所以让我们将它们放在一起进行测试：

```java
@ActiveProfiles("eureka-test")
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment =  SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { MockBookServiceConfig.class }, initializers = { EurekaContainerConfig.Initializer.class })
class ServiceDiscoveryBooksClientIntegrationTest {

	@Autowired
	private BooksClient booksClient;

	@Lazy
	@Autowired
	private EurekaClient eurekaClient;

	@BeforeEach
	void setUp() {
		await().atMost(60, SECONDS).until(() -> eurekaClient.getApplications().size() > 0);
	}

	@Test
	public void whenGetBooks_thenTheCorrectBooksAreReturned() {
		List books = booksClient.getBooks();

		assertEquals(1, books.size());
		assertEquals(
			new Book("Hitchhiker's guide to the galaxy", "Douglas Adams"),
			books.stream().findFirst().get());
	}
}
```

这个测试中发生了一些事情。让我们一一看看。

首先，EurekaContainerConfig中的上下文initializer启动Eureka服务。

然后，SpringBootTest启动books-service应用程序，该应用程序公开了MockBookServiceConfig中定义的控制器。

因为**Eureka容器和Web应用程序的启动可能需要几秒钟**，所以我们需要等到books-service被注册。这发生在测试的setUp中。

最后，测试方法验证BooksClient确实与Eureka配置结合使用时正常工作。

## 7. 总结

在本文中，我们探讨了为Spring Cloud Feign Client编写集成测试的不同方式。我们从一个基本的客户端开始，我们在WireMock的帮助下对其进行了测试。之后，我们继续使用Ribbon添加负载均衡。我们编写了一个集成测试，并确保我们的Feign Client与Ribbon提供的客户端负载均衡一起正常工作。最后，我们将Eureka服务发现添加到组合中。再一次，我们确保我们的客户端仍然按预期工作。