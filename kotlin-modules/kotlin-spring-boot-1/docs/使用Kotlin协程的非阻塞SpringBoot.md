## 1. 概述

[Kotlin Coroutines](https://www.baeldung.com/kotlin-coroutines)通常可以为反应式、大量回调的代码增加可读性。

在本教程中，我们将了解如何利用这些协程来构建非阻塞[Spring Boot](https://www.baeldung.com/spring-boot)应用程序。我们还将比较反应式方法和协程方法。

## 2. 协程的动机

如今，系统通常会处理数千甚至数百万个请求。因此，开发领域正朝着非阻塞计算和请求处理的方向发展。通过从核心线程卸载 I/O 操作，有效地利用系统资源，与传统的线程每个请求方法相比，可以处理更多的请求。

异步处理不是一项微不足道的任务，而且容易出错。幸运的是，我们有解决这种复杂性的工具，例如Java[CompletableFutures](https://www.baeldung.com/java-completablefuture)或响应式库，例如[RxJava](https://www.baeldung.com/rxjava-tutorial)。事实上，[Spring 框架](https://www.baeldung.com/spring-tutorial)已经支持[Reactor](https://www.baeldung.com/spring-reactor)和[WebFlux](https://www.baeldung.com/spring-webflux)框架的响应式方法。

异步代码可能难以阅读，但Kotlin[语言](https://www.baeldung.com/kotlin)提供了协程的概念，允许以顺序方式编写并发和异步代码。

协程非常灵活，因此我们可以通过 Jobs 和 Scopes 更好地控制任务的执行。除此之外，Kotlin 协程可以与现有的Java非阻塞框架完美协同工作。

Spring 将从 5.2 版本开始支持KotlinCoroutines。

## 3.项目设置

让我们从添加我们需要的依赖项开始。

由于本教程中使用的大多数依赖项还没有稳定版本，我们需要包含快照和里程碑存储库：

```xml
<repositories>
    <repository>
        <id>spring-snapshots</id>
        <name>Spring Snapshots</name>
        <url>https://repo.spring.io/snapshot</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
    <repository>
        <id>spring-milestones</id>
        <name>Spring Milestones</name>
        <url>https://repo.spring.io/milestone</url>
    </repository>
</repositories>
```

让我们使用[Netty](https://www.baeldung.com/netty)框架，一个异步的客户端-服务器事件驱动框架。我们将使用NettyWebServer作为反应式 Web 服务器的嵌入式实现。

此外，从 3.0 版本开始，Servlet 规范引入了对应用程序以非阻塞方式处理请求的支持。因此，我们也可以使用像[Jetty](https://www.baeldung.com/jetty-embedded)或[Tomcat](https://www.baeldung.com/tomcat)这样的 servlet 容器 。

让我们使用这些版本，包括通过Spring Boot的Spring5.2：

```xml
<properties>
    <kotlin.version>1.3.31</kotlin.version>
    <r2dbc.version>1.0.0.RELEASE</r2dbc.version>
    <r2dbc-spi.version>1.0.0.M7</r2dbc-spi.version>
    <h2-r2dbc.version>1.0.0.BUILD-SNAPSHOT</h2-r2dbc.version>
    <kotlinx-coroutines.version>1.2.1</kotlinx-coroutines.version>
    <spring-boot.version>2.2.0.M2</spring-boot.version>
</properties>
```

接下来，由于我们依赖 WebFlux 进行异步处理，因此使用[spring-boot-starter-webflux](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-webflux) 而不是spring-boot-starter-web非常重要。所以我们需要在我们的pom.xml中包含这个依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
    <version>${spring-boot.version}</version>
</dependency>
```

接下来，我们将添加R2DBC 依赖项以支持响应式数据库访问：

```xml
<dependency>
    <groupId>io.r2dbc</groupId>
    <artifactId>r2dbc-h2</artifactId>
    <version>${h2-r2dbc.version}</version>
</dependency>
<dependency>
    <groupId>io.r2dbc</groupId>
    <artifactId>r2dbc-spi</artifactId>
    <version>${r2dbc-spi.version}</version>
</dependency>
```

最后，我们将添加Kotlin核心和协程依赖项：

```xml
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-reflect</artifactId>
</dependency>
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-stdlib-jdk8</artifactId>
</dependency>
<dependency>
    <groupId>org.jetbrains.kotlinx</groupId>
    <artifactId>kotlinx-coroutines-core</artifactId>
    <version>${kotlinx-coroutines.version}</version>
</dependency>
<dependency>
    <groupId>org.jetbrains.kotlinx</groupId>
    <artifactId>kotlinx-coroutines-reactor</artifactId>
    <version>${kotlinx-coroutines.version}</version>
</dependency>
```

## 4. 带协程的SpringData R2DBC

在本节中，我们将重点关注以反应式和协程式两种方式访问数据库。

### 4.1. 反应式 R2DBC

让我们从反应式关系数据库客户端开始。简单地说，R2DBC 是一种 API 规范，它声明了一个由数据库供应商实现的反应式 API。

我们的数据存储将由内存中的[H2 数据库](https://www.baeldung.com/spring-boot-h2-database)提供支持。此外，反应式关系驱动程序可用于 PostgreSQL 和 Microsoft SQL。

首先，让我们使用反应式方法实现一个简单的存储库：

```java
@Repository
class ProductRepository(private val client: DatabaseClient) {

    fun getProductById(id: Int): Mono<Product> {
        return client.execute()
          .sql("SELECT  FROM products WHERE id = $1")
          .bind(0, id)
          .`as`(Product::class.java)
          .fetch()
          .one()
    }

    fun addNewProduct(name: String, price: Float): Mono<Void> {
        return client.execute()
          .sql("INSERT INTO products (name, price) VALUES($1, $2)")
          .bind(0, name)
          .bind(1, price)
          .then()
    }

    fun getAllProducts(): Flux<Product> {
        return client.select()
          .from("products")
          .`as`(Product::class.java)
          .fetch()
          .all()
    }
}

```

在这里，我们使用非阻塞DatabaseClient对数据库执行查询。现在，让我们使用挂起函数和相应的Kotlin类型重写我们的存储库类。

### 4.2. 带协程的 R2DBC

为了将函数从响应式转换为协程 API，我们在函数定义之前添加 suspend 修饰符：

```java
fun noResultFunc(): Mono<Void>
suspend fun noResultFunc()
```

此外，我们可以省略Void返回类型。在非空结果的情况下，我们只返回定义类型的结果，而不将其包装在Mono类中：

```java
fun singleItemResultFunc(): Mono<T>
fun singleItemResultFunc(): T?
```

接下来，如果一个源可能发出多个项目，我们只需将Flux更改为Flow，如下所示：

```java
fun multiItemsResultFunc(): Flux<T>
fun mutliItemsResultFunc(): Flow<T>

```

让我们应用这些规则并重构我们的存储库：

```java
@Repository
class ProductRepositoryCoroutines(private val client: DatabaseClient) {

    suspend fun getProductById(id: Int): Product? =
        client.execute()
          .sql("SELECT  FROM products WHERE id = $1")
          .bind(0, id)
          .`as`(Product::class.java)
          .fetch()
          .one()
          .awaitFirstOrNull()

    suspend fun addNewProduct(name: String, price: Float) =
        client.execute()
          .sql("INSERT INTO products (name, price) VALUES($1, $2)")
          .bind(0, name)
          .bind(1, price)
          .then()
          .awaitFirstOrNull()

    @FlowPreview
    fun getAllProducts(): Flow<Product> =
        client.select()
          .from("products")
          .`as`(Product::class.java)
          .fetch()
          .all()
          .asFlow()
}
```

在上面的代码片段中，有几点需要我们注意。这些await函数从何而来？它们在kotlin-coroutines-reactive library中被定义为Kotlin 扩展函数。

[此外， spring-data-r2dbc库](https://github.com/spring-projects/spring-data-r2dbc)中还有更多可用的扩展。

## 5.SpringWebFlux 控制器

到目前为止，我们已经了解了如何实现存储库，但还没有对数据存储进行任何实际查询。因此，在本节中，我们将了解如何通过创建非阻塞控制器来将协程与SpringWebFlux 框架一起应用。

### 5.1. 反应控制器

让我们定义两个简单的端点，它们依次通过我们的存储库查询数据库。

让我们从更熟悉的响应式风格开始：

```java
@RestController
class ProductController {
    @Autowired
    lateinit var productRepository: ProductRepository

    @GetMapping("/{id}")
    fun findOne(@PathVariable id: Int): Mono<Product> {
        return productRepository.getProductById(id)
    }

    @GetMapping("/")
    fun findAll(): Flux<Product> {
        return productRepository.getAllProducts()
    }
}
```

这就提出了一个问题，哪个线程负责执行实际的 I/O 操作？默认情况下，每个查询的操作都在一个单独的反应器 NIO 线程上运行，该线程由底层调度程序实现选择。

### 5.2. 带有协程的控制器

让我们通过利用挂起函数并使用相应的存储库类来重构控制器：

```java
@RestController
class ProductControllerCoroutines {
    @Autowired
    lateinit var productRepository: ProductRepositoryCoroutines

    @GetMapping("/{id}")
    suspend fun findOne(@PathVariable id: Int): Product? {
        return productRepository.getProductById(id)
    }

    @FlowPreview
    @GetMapping("/")
    fun findAll(): Flow<Product> {
        return productRepository.getAllProducts()
    }
}
```

首先，请注意findAll()函数不是暂停函数。然而，就我们返回Flow 而言，它在内部调用挂起函数。

对于这个版本，数据库查询将在与反应式示例相同的反应器线程上运行。

## 6.SpringWebFlux 网络客户端

接下来，假设我们的系统中有[微服务](https://www.baeldung.com/spring-cloud-series)。

为了完成一个请求，我们需要查询另一个服务来获取额外的数据。因此，在我们的例子中，一个很好的例子就是获取产品库存数量。要通过 API 调用其他服务，我们将使用WebFlux 框架中的[WebClient](https://www.baeldung.com/spring-5-webclient)。

### 6.1. 反应式网络客户端

首先，让我们看看如何发出一个简单的请求：

```java
val htmlResponse = webClient.get()
  .uri("https://www.baeldung.com/")
  .retrieve().bodyToMono<String>()

```

下一步是调用外部库存服务以获取库存数量，然后将组合结果返回给客户端。首先，我们将从存储库中获取产品，然后查询库存服务：

```java
@GetMapping("/{id}/stock")
fun findOneInStock(@PathVariable id: Int): Mono<ProductStockView> {
   val product = productRepository.getProductById(id)
   
   val stockQuantity = webClient.get()
     .uri("/stock-service/product/$id/quantity")
     .accept(MediaType.APPLICATION_JSON)
     .retrieve()
     .bodyToMono<Int>()
   return product.zipWith(stockQuantity) { 
       productInStock, stockQty ->
         ProductStockView(productInStock, stockQty)
   }
}

```

请注意，我们从存储库返回一个Mono<Product>类型的对象。然后，我们从WebClient得到一个Mono<Int>。最后，实际订阅发生在我们调用[zipWith()](https://www.baeldung.com/reactor-combine-streams)方法时。我们等待两个请求完成，最后将它们组合成一个新对象。

### 6.2. WebClient与协程

现在，让我们看看如何将WebClient与协程一起使用。

要执行 GET 请求，我们应用 awaitBody()暂停扩展函数：

```java
val htmlResponse = webClient.get()
  .uri("https://www.baeldung.com/")
  .retrieve()
  .awaitBody<String>()
```

这样，如果 API 调用返回的不是 2xx 响应，则retrieve() 方法将抛出异常。为了自定义不同响应状态的响应处理，我们可以使用awaitExchange() 挂起扩展函数：

```java
val response: ResponseEntity<String> = webClient.get()
  .uri("https://www.baeldung.com/")
  .awaitExchange()
  .awaitEntity()
```

由于我们可以访问生成的 ResponseEntity， 因此我们可以检查状态代码，然后采取相应的行动。

让我们回到我们的微服务示例。我们可以对股票服务执行请求：

```java
@GetMapping("/{id}/stock")
suspend fun findOneInStock(@PathVariable id: Int): ProductStockView {
    val product = productRepository.getProductById(id)
    val quantity = webClient.get()
      .uri("/stock-service/product/$id/quantity")
      .accept(APPLICATION_JSON)
      .retrieve()
      .awaitBody<Int>()
    return ProductStockView(product!!, quantity)
}
```

我们应该注意到这看起来像一个阻塞代码。使用协程的主要好处之一是能够以流畅且可读的方式编写异步代码。

在上面的例子中，数据库查询和网络请求会依次执行。这是因为协程默认是顺序的。

我们可以并行运行挂起函数吗？绝对地！让我们修改端点方法以并行运行查询：

```java
@GetMapping("/{id}/stock")
suspend fun findOneInStock(@PathVariable id: Int): ProductStockView = coroutineScope {
    val product: Deferred<Product?> = async(start = CoroutineStart.LAZY) {
        productRepository.getProductById(id)
    }
    val quantity: Deferred<Int> = async(start = CoroutineStart.LAZY) {
        webClient.get()
          .uri("/stock-service/product/$id/quantity")
          .accept(APPLICATION_JSON)
          .retrieve().awaitBody<Int>()
    }
    ProductStockView(product.await()!!, quantity.await())
}
```

在这里，通过在async{}块中包装一个挂起函数，我们得到了一个Deferred<>类型的对象。默认情况下， 协程会立即安排执行。因此，如果我们想在调用await()方法时恰好运行它们，我们需要将 CoroutineStart.LAZY 作为可选的 启动 参数传递。

最后，要开始执行函数，我们调用await()方法。在这种情况下，这两个函数将并行执行。这种技术也称为并行分解。

有趣的是，异步块中的函数被分派到单独的工作线程。之后，实际的 I/O 操作发生在来自 Reactor NIO 池的线程上。

为了强制执行结构化并发，我们使用了coroutineScope{} 范围函数来创建我们自己的范围。它将等待块内的所有协程完成，然后再完成自身。但是， 与[runBlocking相比， ](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/run-blocking.html)coroutineScope{}函数不会阻塞当前线程。

## 7. WebFlux.fn DSL 路由

最后，让我们看看如何使用带有[DSL 路由定义](https://www.baeldung.com/spring-webflux-kotlin)的协程。

由Kotlin提供支持的[WebFlux Functional Framework提供了一种简洁流畅的方式来定义端点。](https://www.baeldung.com/spring-5-functional-web)coRouter [{}](https://docs.spring.io/spring-framework/docs/5.2.0.M1/kdoc-api/spring-framework/org.springframework.web.reactive.function.server/co-router.html) DSL 支持KotlinCoroutines 来定义路由器函数。

首先，让我们以 DSL 方式定义路由器端点：

```java
@Configuration
class RouterConfiguration {
    @FlowPreview
    @Bean
    fun productRoutes(productsHandler: ProductsHandler) = coRouter {
        GET("/", productsHandler::findAll)
        GET("/{id}", productsHandler::findOne)
        GET("/{id}/stock", productsHandler::findOneInStock)
    }
}
```

现在我们有了路由定义，让我们使用与ProductController相同的功能来实现ProductsHandler：

```java
@Component
class ProductsHandler(
  @Autowired var webClient: WebClient, 
  @Autowired var productRepository: ProductRepositoryCoroutines) {
    
    @FlowPreview
    suspend fun findAll(request: ServerRequest): ServerResponse =
        ServerResponse.ok().json().bodyAndAwait(productRepository.getAllProducts())

    suspend fun findOneInStock(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toInt()
        val product: Deferred<Product?> = GlobalScope.async {
            productRepository.getProductById(id)
        }
        val quantity: Deferred<Int> = GlobalScope.async {
            webClient.get()
              .uri("/stock-service/product/$id/quantity")
              .accept(MediaType.APPLICATION_JSON)
              .retrieve().awaitBody<Int>()
        }
        return ServerResponse.ok()
          .json()
          .bodyAndAwait(ProductStockView(product.await()!!, quantity.await()))
    }

    suspend fun findOne(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toInt()
        return ServerResponse.ok()
          .json()
          .bodyAndAwait(productRepository.getProductById(id)!!)
    }
}
```

我们应该注意到，我们使用了挂起函数来定义ProductsHandler类。除了请求和响应类型之外，与控制器相比没有太大变化。

这就是我们设置一个简单的 REST 控制器所需的全部。因此，由于使用 Routes DSL 和Kotlin协程，我们拥有流畅简洁的端点定义。

## 八、总结

在本文中，我们探索了Kotlin 协程，并了解了如何将它们与Spring框架、R2DBC 和 WebFlux 集成，尤其是。

在项目中应用非阻塞方法可以提高应用程序性能和可伸缩性。此外，我们还看到了使用Kotlin协程如何使异步代码更具可读性。

我们应该注意，上述库的中期开发版本在达到稳定版本之前可能会发生很大变化，并且次要版本差异可能彼此不兼容。