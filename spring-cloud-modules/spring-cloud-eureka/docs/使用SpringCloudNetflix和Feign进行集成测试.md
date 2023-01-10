## 1. 概述

在本文中，我们将介绍Feign Client的集成测试。

我们将创建一个基本的Open Feign客户端，在WireMock的帮助下为其编写一个简单的集成测试。

之后，我们将向客户端添加Ribbon配置，并为其构建集成测试。最后，我们将配置一个Eureka测试容器并测试此设置，以确保我们的整个配置按预期工作。

## 2. Feign Client

要设置我们的Feign Client，我们应该首先添加Spring Cloud OpenFeign Maven依赖项：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

之后，创建一个Book类用作我们的模型：

```java
public class Book {
	private String title;
	private String author;
}
```

最后，让我们创建我们的Feign Client接口：

```java
@FeignClient(value = "simple-books-client", url = "${book.service.url}")
public interface BooksClient {
	@RequestMapping("/books") 
	List<Book> getBooks();
}
```

现在，我们有一个从REST服务检索Book列表的Feign Client。现在，让我们继续编写一些集成测试。

## 3. WireMock

### 3.1 设置WireMock服务器

如果我们想测试BooksClient，我们需要一个提供/books端点的mock服务。我们的客户将对这个mock服务进行调用。为此，我们将使用WireMock。

因此，让我们添加WireMock Maven依赖项：

```xml
<dependency>
	<groupId>com.github.tomakehurst</groupId>
	<artifactId>wiremock</artifactId>
	<scope>test</scope>
</dependency>
```

并配置mock服务器：

```java
@TestConfiguration
@ActiveProfiles("test")
public class WireMockConfig {

	@Bean(initMethod = "start", destroyMethod = "stop")
	public WireMockServer mockBooksService() {
		return new WireMockServer(9561);
	}
}
```

我们现在有一个正在运行的mock服务器，在9651端口上接收连接。

### 3.2 设置Mock

让我们将属性book.service.url添加到application-test.yml中，以指向WireMockServer端口：

```yaml
book:
    service:
        url: http://localhost:9561
```

我们还要为/books端点准备一个mock响应get-books-response.json。

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

现在让我们为/books端点上的GET请求配置mock响应：

```java
public class BookMocks {

	public static void setupMockBooksResponse(WireMockServer mockService) throws IOException {
		mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/books"))
				.willReturn(
						WireMock.aResponse()
								.withStatus(HttpStatus.OK.value())
								.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
								.withBody(
										copyToString(
												BookMocks.class.getClassLoader().getResourceAsStream("payload/get-books-response.json"),
												defaultCharset()))));
	}
}
```

此时，所有必需的配置都已就绪。让我们继续编写我们的第一个测试。

## 4. 我们的第一个集成测试

让我们创建一个集成测试BooksClientIntegrationTest：

```java
@SpringBootTest
@ActiveProfiles("test")
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {WireMockConfig.class})
class BooksClientIntegrationTest {

	@Autowired
	private WireMockServer mockBooksService;

	@Autowired
	private BooksClient booksClient;

	@BeforeEach
	void setUp() throws IOException {
		setupMockBooksResponse(mockBooksService);
	}

	@Test
	void whenGetBooks_thenBooksShouldBeReturned() {
		assertFalse(booksClient.getBooks().isEmpty());
	}

	@Test
	void whenGetBooks_thenTheCorrectBooksShouldBeReturned() {
		assertTrue(booksClient.getBooks()
				.containsAll(asList(
						new Book("Dune", "Frank Herbert"),
						new Book("Foundation", "Isaac Asimov"))));
	}
}
```

此时，我们有一个配置了WireMockServer的SpringBootTest，它可以在BooksClient调用/Books端点时返回预定义的Book列表。

## 5. 与Ribbon集成

现在让我们通过添加Ribbon提供的负载均衡功能来改进我们的客户端。

在客户端接口中，我们只需删除硬编码的服务URL，通过服务名称book-service引用该服务：

```java
@FeignClient("books-service")
public interface BooksClient {

	@RequestMapping("/books")
	List<Book> getBooks();
}
```

接下来，添加Netflix Ribbon Maven依赖项：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
</dependency>
```

最后，在application-test.yaml文件中，我们现在应该删除book.service.url并定义Ribbon listOfServers：

```yaml
books-service:
    ribbon:
        listOfServers: http://localhost:9561
```

现在让我们再次运行BooksClientIntegrationTest。它应该正常通过，以验证新的配置按预期工作。

### 5.1 动态端口配置

如果我们不想硬编码服务器的端口，我们可以将WireMock配置为在启动时使用动态端口。

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

	@Bean(name = "secondMockBooksService", initMethod = "start", destroyMethod = "stop")
	public WireMockServer secondBooksMockService() {
		return new WireMockServer(options().dynamicPort());
	}

	@Bean
	public ServerList<Server> ribbonServerList() {
		return new StaticServerList<>(
				new Server("localhost", mockBooksService.port()),
				new Server("localhost", secondMockBooksService.port()));
	}
}
```

此配置设置了两个WireMock服务器，每个服务器运行在运行时动态分配的不同端口上。此外，它还使用两个mock服务器配置了Ribbon服务器列表。

### 5.2 负载均衡测试

现在我们已经配置了Ribbon负载均衡器，让我们确保BooksClient在两个mock服务器之间正确的交替调用：

```java
@SpringBootTest
@ActiveProfiles("ribbon-test")
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RibbonTestConfig.class})
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

		mockBooksService.verify(moreThan(0), getRequestedFor(WireMock.urlEqualTo("/books")));
		secondMockBooksService.verify(moreThan(0), getRequestedFor(WireMock.urlEqualTo("/books")));
	}

	@Test
	void whenGetBooks_thenTheCorrectBooksShouldBeReturned() {
		assertTrue(booksClient.getBooks()
				.containsAll(asList(
						new Book("Dune", "Frank Herbert"),
						new Book("Foundation", "Isaac Asimov"))));
	}
}
```

## 6. Eureka集成

到目前为止，我们已经看到了如何测试使用Ribbon进行负载均衡的客户端。但是如果我们的设置使用像Eureka这样的服务发现系统呢？我们应该编写一个集成测试，以确保我们的BooksClient在这种情况下也能按预期工作。

为此，我们将运行一个Eureka Server作为测试容器。然后，我们启动一个mock book-service，并将其注册到我们的Eureka容器中。最后，一旦完成，我们可以对其运行测试。

在这之前，我们先添加Testcontainers和Netflix Eureka Client Maven依赖项：

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

### 6.1 TestContainer设置

让我们创建一个TestContainer配置，以启动我们的Eureka Server：

```java
@TestConfiguration
@ActiveProfiles("eureka-test")
public class EurekaContainerConfig {

	public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		public static GenericContainer eurekaServer
				= new GenericContainer("springcloud/eureka")
				.withExposedPorts(8761);

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

正如我们所见，上面的初始化程序启动了容器。然后它会暴露Eureka Server正在监听的端口8761。

最后，在Eureka Server启动后，我们需要更新eureka.client.serviceUrl.defaultZone属性。这定义了用于服务发现的Eureka Server的地址。

### 6.2 注册Mock服务器

现在我们的Eureka Server已经启动并运行，我们需要注册一个mock books-service。我们只需创建一个RestController即可做到这一点：

```java
@Configuration
@RestController
@ActiveProfiles("eureka-test")
public class MockBookServiceConfig {

	@RequestMapping("/books")
	public List<Book> getBooks() {
		return Collections.singletonList(new Book("Hitchhiker's guide to the galaxy", "Douglas Adams"));
	}
}
```

为了注册这个控制器，我们现在要做的就是确保application-eureka-test.yml中的spring.application.name属性为books-service。与BooksClient接口中使用的服务名称相同。

注意：现在我们的依赖中包含netflix-eureka-client，默认情况下将使用Eureka进行服务发现。因此，如果我们希望之前不使用Eureka的测试继续通过，我们需要手动将eureka.client.enabled设置为false。这样，即使有netflix-eureka-client依赖，BooksClient也不会尝试使用Eureka来定位服务，而是使用Ribbon配置。

### 6.3 集成测试

现在，我们有了所有需要的配置项，现在我们可以测试所有的这些配置是否按预期生效：

```java
@ActiveProfiles("eureka-test")
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {MockBookServiceConfig.class}, initializers = {EurekaContainerConfig.Initializer.class})
class ServiceDiscoveryBooksClientLiveTest {

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
	void whenGetBooks_thenTheCorrectBooksAreReturned() {
		List<Book> books = booksClient.getBooks();

		assertEquals(1, books.size());
		assertEquals(
				new Book("Hitchhiker's guide to the galaxy", "Douglas Adams"),
				books.stream().findFirst().get());
	}
}
```

在这个测试中有一些注意到地方。让我们逐一看看。

首先，EurekaContainerConfig中的context initializer启动Eureka服务。

然后，SpringBootTest启动books-service应用程序，该应用程序公开MockBookServiceConfig中定义的控制器。

因为Eureka容器和Web应用程序的启动可能需要几秒钟，所以我们需要等到books-service注册。这发生在测试的setUp中。

最后，测试方法验证BooksClient确实可以与Eureka配置结合使用。

## 7. 总结

在本文中，我们介绍了为Spring Cloud Feign Client编写集成测试的不同方法。我们从一个基本客户端开始，在WireMock的帮助下对其进行了测试。之后，我们继续使用Ribbon添加负载均衡。我们编写了一个集成测试，并确保我们的Feign Client与Ribbon提供的客户端负载均衡正常工作。最后，我们添加了Eureka 服务发现。同样，我们确保我们的客户端仍然按预期工作。