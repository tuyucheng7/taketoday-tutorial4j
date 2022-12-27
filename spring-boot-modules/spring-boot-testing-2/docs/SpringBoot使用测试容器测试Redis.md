## 1. 概述

[Testcontainers](https://www.baeldung.com/spring-boot-testcontainers-integration-test)是一个Java库，用于为单元测试目的创建临时的Docker容器，当我们想要避免使用实际服务器进行测试时，它很有用。

在本教程中，**我们将学习如何在测试使用Redis的Spring Boot应用程序时使用Testcontainer**。

## 2. 项目设置

**使用任何测试容器的第一个先决条件是在我们运行测试的机器上安装Docker**，一旦我们安装了Docker(本文使用的是Windows Docker Desktop)，我们就可以开始设置我们的Spring Boot应用程序了。

在此应用程序中，我们将设置Redis哈希、Repository和将使用Repository与Redis交互的服务。

### 2.1 依赖关系

让我们首先将所需的依赖项添加到我们的项目中。

首先，我们将添加[Spring Boot Starter Test](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test)和[Spring Boot Starter Data Redis](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-redis)依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

接下来，让我们添加[Testcontainers](https://mvnrepository.com/artifact/org.testcontainers/testcontainers)依赖项：

```xml
<dependency> 
    <groupId>org.testcontainers</groupId> 
    <artifactId>testcontainers</artifactId> 
    <version>1.17.2</version> 
    <scope>test</scope> 
</dependency>
```

### 2.2 自动配置

由于我们不需要任何[高级配置](https://www.baeldung.com/spring-data-redis-tutorial#the-redis-configuration)，因此我们可以使用自动配置来建立与Redis服务器的连接。

为此，我们需要将Redis连接的详细信息添加到application.properties文件中：

```properties
spring.redis.host=127.0.0.1
spring.redis.port=6379
```

## 3. 应用程序设置

让我们从主应用程序的代码开始，我们将构建一个小型应用程序，用于读取产品并将其写入Redis数据库。

### 3.1 实体

首先我们从产品类开始：

```java
@RedisHash("product")
public class Product implements Serializable {
    private String id;
    private String name;
    private double price;

    // Constructor,  getters and setters
}
```

@RedisHash注解用于告诉Spring Data Redis此类应存储在Redis哈希中，另存为哈希适用于不包含嵌套对象的实体。

### 3.2 Repository

接下来，我们可以为产品哈希定义一个Repository：

```java
@Repository
public interface ProductRepository extends CrudRepository<Product, String> {
}
```

CrudRepository接口已经实现了我们需要的保存、更新、删除和查找产品的方法，所以我们不需要自己定义任何方法。

### 3.3 Service

最后，让我们创建一个使用ProductRepository执行读写操作的服务：

```java
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProduct(String id) {
        return productRepository.findById(id).orElse(null);
    }

    // other methods
}
```

然后控制器或其他服务类可以使用此服务对产品执行CRUD操作。在实际应用程序中，这些方法可能包含更复杂的逻辑，但出于本教程的目的，我们将只关注Redis交互。

## 4. 测试

现在，我们将为我们的ProductService编写测试来测试CRUD操作。

### 4.1 测试服务

让我们为ProductService编写一个集成测试：

```java
@Test
void givenProductCreated_whenGettingProductById_thenProductExistsAndHasSameProperties() {
    Product product = new Product("1", "Test Product", 10.0);
    productService.createProduct(product);
    Product productFromDb = productService.getProduct("1");
    
    assertEquals("1", productFromDb.getId());
    assertEquals("Test Product", productFromDb.getName());
    assertEquals(10.0, productFromDb.getPrice());
}
```

**这假定Redis数据库正在属性文件中指定的URL上运行，如果我们没有运行Redis实例或者我们的服务器无法连接到它，测试将运行错误**。

### 4.2 使用Testcontainer启动Redis容器

让我们通过在测试运行时运行Redis测试容器来解决这个问题，然后，我们将从代码本身更改连接的详细信息。

让我们看一下创建和运行测试容器的代码：

```java
static {
    GenericContainer<?> redis = 
      new GenericContainer<>(DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379);
    redis.start();
}
```

让我们了解这段代码的不同部分：

-   我们从镜像redis:5.0.3-alpine创建了一个新容器
-   默认情况下，Redis实例将在端口6379上运行，要公开这个端口，我们可以使用withExposedPorts()方法，**它将公开此端口并将其映射到主机上的随机端口**
-   start()方法将启动容器并等待它准备就绪
-   **我们已将此代码添加到静态代码块中，以便它在注入依赖项和运行测试之前运行**

### 4.3 更改连接详细信息

此时，我们有一个正在运行的Redis容器，但我们还没有更改应用程序使用的连接详细信息。为此，我们需要做的就是**使用系统属性覆盖application.properties文件中的连接详细信息**：

```java
static {
    GenericContainer<?> redis = 
      new GenericContainer<>(DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379);
    redis.start();
    System.setProperty("spring.redis.host", redis.getHost());
    System.setProperty("spring.redis.port", redis.getMappedPort(6379).toString());
}
```

我们**将spring.redis.host属性设置为容器的IP地址**，并且我们可以**获取6379端口的映射端口来设置spring.redis.port属性**。

现在，当测试运行时，它们将连接到容器上运行的Redis数据库。

## 5. 总结

在本文中，我们学习了如何使用Redis Testcontainer来运行测试，并研究了Spring Data Redis的某些方面以了解如何使用它。