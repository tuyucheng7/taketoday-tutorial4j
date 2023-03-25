## 1. 概述

在本教程中，我们将介绍如何在一个简单的[Spring Boot](https://www.baeldung.com/spring-boot)应用程序中设置和使用JaVers来跟踪实体的更改。

## 2. JaVers

在处理可变数据时，我们通常只有存储在数据库中的实体的最后状态。作为开发人员，我们花费大量时间调试应用程序，在日志文件中搜索更改状态的事件。在生产环境中，当许多不同的用户使用系统时，这变得更加棘手。

幸运的是，我们拥有像[JaVers](https://www.baeldung.com/javers)这样的出色工具。JaVers是一个审计日志框架，有助于跟踪应用程序中实体的更改。

此工具的使用不仅限于调试和审计。它也可以成功地应用于执行分析、强制安全策略和维护事件日志。

## 3. 项目构建

首先，要开始使用JaVers，我们需要配置审计存储库以持久化实体的快照。其次，我们需要调整JaVers的一些可配置属性。最后，我们还将介绍如何正确配置域模型。

但是，值得一提的是，JaVers提供了默认配置选项，因此我们几乎无需配置即可开始使用它。

### 3.1 依赖

首先，我们需要将JaVers Spring Boot启动器依赖项添加到我们的项目中。根据持久化存储的类型，我们有两种选择：[org.javers:javers-spring-boot-starter-sql](https://mvnrepository.com/artifact/org.javers/javers-spring-boot-starter-sql)和[org.javers:javers-spring-boot-starter-mongo](https://mvnrepository.com/artifact/org.javers/javers-spring-boot-starter-mongo)。在本教程中，我们将使用javers-spring-boot-starter-sql。

```xml
<dependency>
    <groupId>org.javers</groupId>
    <artifactId>javers-spring-boot-starter-sql</artifactId>
    <version>6.6.5</version>
</dependency>
```

当我们要使用H2数据库时，我们还包含以下依赖项：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
```

### 3.2 JaVers存储

JaVers使用Repository抽象来存储提交和序列化实体。所有数据都以[JSON格式](https://www.baeldung.com/java-org-json)存储。因此，使用NoSQL存储可能是一个不错的选择。但是，为了简单起见，我们使用[H2内存数据库](https://www.baeldung.com/spring-boot-h2-database)。

默认情况下，JaVers利用内存中的存储实现，如果我们使用Spring Boot，则无需额外配置。此外，**在使用[Spring Data](https://www.baeldung.com/spring-data)启动器时，JaVers会重用应用程序的数据库配置**。

JaVers为SQL和Mongo持久层技术提供了两个启动器，它们与Spring Data兼容，默认情况下不需要额外配置。但是，我们始终可以覆盖默认的配置bean：分别为[JaversSqlAutoConfiguration](https://github.com/javers/javers/blob/master/javers-spring-boot-starter-sql/src/main/java/org/javers/spring/boot/sql/JaversSqlAutoConfiguration.java)和[JaversMongoAutoConfiguration](https://github.com/javers/javers/blob/master/javers-spring-boot-starter-mongo/src/main/java/org/javers/spring/boot/mongo/JaversMongoAutoConfiguration.java)。

### 3.3 JaVers属性

JaVers允许配置多个选项，不过在大多数用例中，[Spring Boot默认设置](https://javers.org/documentation/spring-boot-integration/#javers-configuration-properties)已经足够了。

在这里我们只覆盖一个newObjectSnapshot属性，这样我们就可以获得新创建对象的快照：

```properties
javers.newObjectSnapshot=true
```

### 3.4 JaVers域配置

JaVers在内部定义了以下类型：实体、值对象、值、容器和原始类型。其中一些术语来自[DDD(领域驱动设计)](https://www.baeldung.com/spring-data-ddd)术语。

**拥有多种类型的主要目的是根据类型提供不同的diff算法**。每种类型都有相应的diff策略。因此，如果应用程序类配置不正确，我们将得到不可预知的结果。

为了告诉JaVers要为类使用什么类型，我们有几种方式：

+ Explicitly：第一种方式是显式地使用JaversBuilder类的register*方法，第二种方式是使用注解
+ Implicitly：JaVers提供了基于类关系自动检测类型的算法
+ Defaults：默认情况下，JaVers会将所有类视为ValueObjects

在本教程中，我们将使用注解方法显式配置JaVers。

最重要的是**JaVers与javax.persistence注解兼容**。因此，我们不需要在实体上使用特定于JaVers的注解。

## 4. 示例项目

现在我们将创建一个简单的应用程序，其中将包含我们将要审计的几个域实体。

### 4.1 域模型

我们的域将包括有产品的商店。

让我们定义Store实体：

```java
@Entity
public class Store {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    
    @Embedded
    private Address address;
    
    @OneToMany(
          mappedBy = "store",
          cascade = CascadeType.ALL,
          orphanRemoval = true
    )
    private List<Product> products = new ArrayList<>();

    // constructors, getters, setters ...
}
```

请注意，我们使用的是默认的JPA注解。JaVers按以下方式映射它们：

+ @javax.persistence.Entity映射到@org.javers.core.metamodel.annotation.Entity
+ @javax.persistence.Embeddable映射到@org.javers.core.metamodel.annotation.ValueObject

@Embeddable类以通常的方式定义：

```java
@Embeddable
public class Address {
    private String address;
    private Integer zipCode;
}
```

### 4.2 Repository

为了审计JPA Repository，JaVers提供了@JaversSpringDataAuditable注解。

让我们使用该注解定义StoreRepository：

```java
@JaversSpringDataAuditable
public interface StoreRepository extends CrudRepository<Store, Integer> {
}
```

此外，我们还定义ProductRepository，但不指定注解：

```java
public interface ProductRepository extends CrudRepository<Product, Integer> {
}
```

现在考虑一个我们不使用Spring Data Repository的情况。JaVers还有另一个用于此目的的方法级注解：@JaversAuditable。

例如，我们可以定义一个持久化产品的方法，如下所示：

```java
@JaversAuditable
public void saveProduct(Product product) {
    // save object
}
```

或者，我们甚至可以直接在Repository接口中的方法上方添加此注解：

```java
public interface ProductRepository extends CrudRepository<Product, Integer> {
    @Override
    @JaversAuditable
    <S extends Product> S save(S s);
}
```

### 4.3 AuthorProvider

JaVers中的每个提交更改都应该有其作者。此外，JaVers开箱即用的支持[Spring Security](https://www.baeldung.com/security-spring)。

因此，每个提交都是由特定的经过身份验证的用户进行的。但是，对于本教程，我们将创建AuthorProvider接口的一个非常简单的自定义实现：

```java
private static class SimpleAuthorProvider implements AuthorProvider {

    @Override
    public String provide() {
        return "Tuyucheng Author";
    }
}
```

最后一步，为了让JaVers使用我们的自定义实现，我们需要覆盖默认配置bean：

```java
@Bean
public AuthorProvider provideJaversAuthor() {
    return new SimpleAuthorProvider();
}
```

## 5. JaVers审计

最后，我们准备审计我们的应用程序。我们将使用一个简单的控制器将更改分派到我们的应用程序中并检索JaVers提交日志。或者，我们也可以访问H2控制台来查看我们数据库的内部结构：

<img src="../assets/img.png">

为了添加一些初始测试数据，让我们使用EventListener来保存一些实体数据：

```java
@SpringBootApplication
public class SpringBootJaVersApplication {

    @Autowired
    StoreRepository storeRepository;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootJaVersApplication.class, args);
    }

    @EventListener
    public void appReady(ApplicationReadyEvent event) {
        Store store = new Store("Tuyucheng store", new Address("Some street", 22222));
        for (int i = 1; i < 3; i++) {
            Product product = new Product("Product #" + i, 100 * i);
            store.addProduct(product);
        }
        storeRepository.save(store);
    }
}
```

### 5.1 初始提交

创建对象时，JaVers**首先进行INITIAL类型的提交**。

让我们在应用程序启动后检查快照：

```java
@RestController
public class StoreController {
    private final StoreService storeService;
    private final Javers javers;

    public StoreController(StoreService customerService, Javers javers) {
        this.storeService = customerService;
        this.javers = javers;
    }

    @GetMapping("/stores/snapshots")
    public String getStoresSnapshots() {
        QueryBuilder jqlQuery = QueryBuilder.byClass(Store.class);
        List<CdoSnapshot> snapshots = javers.findSnapshots(jqlQuery.build());
        return javers.getJsonConverter().toJson(snapshots);
    }
}
```

在上面的代码中，我们向JaVers查询Store类的快照。如果我们向这个端点发出请求，将得到如下所示的结果：

```json
[
    {
        "commitMetadata": {
            "author": "Tuyucheng Author",
            "properties": [],
            "commitDate": "2022-09-18T20:16:35.591",
            "commitDateInstant": "2022-09-18T12:16:35.591825600Z",
            "id": 1.00
        },
        "globalId": {
            "entity": "cn.tuyucheng.javers.domain.Store",
            "cdoId": 1
        },
        "state": {
            "address": {
                "valueObject": "cn.tuyucheng.javers.domain.Address",
                "ownerId": {
                    "entity": "cn.tuyucheng.javers.domain.Store",
                    "cdoId": 1
                },
                "fragment": "address"
            },
            "name": "Tuyucheng store",
            "id": 1,
            "products": [
                {
                    "entity": "cn.tuyucheng.javers.domain.Product",
                    "cdoId": 2
                },
                {
                    "entity": "cn.tuyucheng.javers.domain.Product",
                    "cdoId": 3
                }
            ]
        },
        "changedProperties": [
            "address",
            "name",
            "id",
            "products"
        ],
        "type": "INITIAL",
        "version": 1
    }
]
```

请注意，**尽管ProductRepository接口缺少注解，但上面的快照包括添加到商店的所有产品**。

默认情况下，JaVers将审计聚合根的所有相关模型(如果它们与父级一起持久化)。

我们可以使用@DiffIgnore注解告诉JaVers忽略特定的类。

例如，我们可以在Store实体中使用@DiffIgnore注解来标注products字段：

```java
@DiffIgnore
private List<Product> products = new ArrayList<>();
```

因此，JaVers不会跟踪来自Store实体的产品更改。

### 5.2 更新提交

下一种提交类型是UPDATE提交。这是最有价值的提交类型，因为它表示对象状态的更改。

让我们定义一个方法来更新商店实体和商店中的所有产品：

```java
@Service
public class StoreService {
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    public StoreService(ProductRepository productRepository, StoreRepository storeRepository) {
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
    }

    public void rebrandStore(int storeId, String updatedName) {
        Optional<Store> storeOpt = storeRepository.findById(storeId);
        storeOpt.ifPresent(store -> {
            store.setName(updatedName);
            store.getProducts().forEach(product -> {
                product.setNamePrefix(updatedName);
            });
            storeRepository.save(store);
        });
    }
}
```

如果我们运行此方法，我们可以看到以下日志(如果产品和商店数量相同)：

```shell
20:26:00.957 [http-nio-8080-exec-6] INFO  [org.javers.core.Javers] >>> Commit(id:2.00, snapshots:3, author:Tuyucheng Author, changes - ValueChange:3), done in 25 millis (diff:22, persist:3) 
```

由于JaVers已成功持久化更改，让我们查询产品的快照：

```java
@GetMapping("/products/snapshots")
public String getProductSnapshots() {
    QueryBuilder jqlQuery = QueryBuilder.byClass(Product.class);
    List<CdoSnapshot> snapshots = javers.findSnapshots(jqlQuery.build());
    return javers.getJsonConverter().toJson(snapshots);
}
```

我们会得到之前的INITIAL提交和新的UPDATE提交：

```json
[
    {
        "commitMetadata": {
            "author": "Tuyucheng Author",
            "properties": [],
            "commitDate": "2022-09-18T20:26:00.932",
            "commitDateInstant": "2022-09-18T12:26:00.932940Z",
            "id": 2.00
        },
        "globalId": {
            "entity": "cn.tuyucheng.javers.domain.Product",
            "cdoId": 3
        },
        "state": {
            "price": 200.0,
            "name": "Product #11Product #2",
            "id": 3,
            "store": {
                "entity": "cn.tuyucheng.javers.domain.Store",
                "cdoId": 1
            }
        },
        "changedProperties": [
            "name"
        ],
        "type": "UPDATE",
        "version": 2
    },
    {
        "commitMetadata": {
            "author": "Tuyucheng Author",
            "properties": [],
            "commitDate": "2022-09-18T20:26:00.932",
            "commitDateInstant": "2022-09-18T12:26:00.932940Z",
            "id": 2.00
        },
        "globalId": {
            "entity": "cn.tuyucheng.javers.domain.Product",
            "cdoId": 2
        },
        "state": {
            "price": 100.0,
            "name": "Product #11Product #1",
            "id": 2,
            "store": {
                "entity": "cn.tuyucheng.javers.domain.Store",
                "cdoId": 1
            }
        },
        "changedProperties": [
            "name"
        ],
        "type": "UPDATE",
        "version": 2
    },
    {
        "commitMetadata": {
            "author": "Tuyucheng Author",
            "properties": [],
            "commitDate": "2022-09-18T20:21:45.279",
            "commitDateInstant": "2022-09-18T12:21:45.279688500Z",
            "id": 1.00
        },
        "globalId": {
            "entity": "cn.tuyucheng.javers.domain.Product",
            "cdoId": 3
        },
        "state": {
            "price": 200.0,
            "name": "Product #2",
            "id": 3,
            "store": {
                "entity": "cn.tuyucheng.javers.domain.Store",
                "cdoId": 1
            }
        },
        "changedProperties": [
            "price",
            "name",
            "id",
            "store"
        ],
        "type": "INITIAL",
        "version": 1
    },
    {
        "commitMetadata": {
            "author": "Tuyucheng Author",
            "properties": [],
            "commitDate": "2022-09-18T20:21:45.279",
            "commitDateInstant": "2022-09-18T12:21:45.279688500Z",
            "id": 1.00
        },
        "globalId": {
            "entity": "cn.tuyucheng.javers.domain.Product",
            "cdoId": 2
        },
        "state": {
            "price": 100.0,
            "name": "Product #1",
            "id": 2,
            "store": {
                "entity": "cn.tuyucheng.javers.domain.Store",
                "cdoId": 1
            }
        },
        "changedProperties": [
            "price",
            "name",
            "id",
            "store"
        ],
        "type": "INITIAL",
        "version": 1
    }
]
```

在这里，我们可以看到有关我们所做更改的所有信息。

值得注意的是，**JaVers不会创建与数据库的新连接。相反，它会重用现有的连接**。JaVers数据在同一事务中与应用程序数据一起提交或回滚。

### 5.3 更改

**JaVers将更改记录为对象版本之间的原子差异**。正如我们从JaVers模式中可以看到的那样，没有单独的表来存储更改，因此**JaVers动态计算更改作为快照之间的差异**。

让我们更新产品价格：

```java
public void updateProductPrice(Integer productId, Double price) {
    Optional<Product> productOpt = productRepository.findById(productId);
    productOpt.ifPresent(product -> {
        product.setPrice(price);
        productRepository.save(product);
    });
}
```

然后，让我们向JaVers查询更改：

```java
@GetMapping("/products/{productId}/changes")
public String getProductChanges(@PathVariable int productId) {
    Product product = storeService.findProductById(productId);
    QueryBuilder jqlQuery = QueryBuilder.byInstance(product);
    Changes changes = javers.findChanges(jqlQuery.build());
    return javers.getJsonConverter().toJson(changes);
}
```

输出包含更改的属性及其前后的值：

```json
[
    {
        "changeType": "ValueChange",
        "globalId": {
            "entity": "cn.tuyucheng.javers.domain.Product",
            "cdoId": 2
        },
        "commitMetadata": {
            "author": "Tuyucheng Author",
            "properties": [],
            "commitDate": "2022-09-18T20:33:58.026",
            "commitDateInstant": "2022-09-18T12:33:58.026093200Z",
            "id": 3.00
        },
        "property": "price",
        "propertyChangeType": "PROPERTY_VALUE_CHANGED",
        "left": 100.0,
        "right": 300.0
    },
    {
        "changeType": "ValueChange",
        "globalId": {
            "entity": "cn.tuyucheng.javers.domain.Product",
            "cdoId": 2
        },
        "commitMetadata": {
            "author": "Tuyucheng Author",
            "properties": [],
            "commitDate": "2022-09-18T20:26:00.932",
            "commitDateInstant": "2022-09-18T12:26:00.932940Z",
            "id": 2.00
        },
        "property": "name",
        "propertyChangeType": "PROPERTY_VALUE_CHANGED",
        "left": "Product #1",
        "right": "Product #11Product #1"
    },
    {
        "changeType": "NewObject",
        "globalId": {
            "entity": "cn.tuyucheng.javers.domain.Product",
            "cdoId": 2
        },
        "commitMetadata": {
            "author": "Tuyucheng Author",
            "properties": [],
            "commitDate": "2022-09-18T20:21:45.279",
            "commitDateInstant": "2022-09-18T12:21:45.279688500Z",
            "id": 1.00
        }
    },
    {
        "changeType": "InitialValueChange",
        "globalId": {
            "entity": "cn.tuyucheng.javers.domain.Product",
            "cdoId": 2
        },
        "commitMetadata": {
            "author": "Tuyucheng Author",
            "properties": [],
            "commitDate": "2022-09-18T20:21:45.279",
            "commitDateInstant": "2022-09-18T12:21:45.279688500Z",
            "id": 1.00
        },
        "property": "name",
        "propertyChangeType": "PROPERTY_VALUE_CHANGED",
        "left": null,
        "right": "Product #1"
    },
    {
        "changeType": "InitialValueChange",
        "globalId": {
            "entity": "cn.tuyucheng.javers.domain.Product",
            "cdoId": 2
        },
        "commitMetadata": {
            "author": "Tuyucheng Author",
            "properties": [],
            "commitDate": "2022-09-18T20:21:45.279",
            "commitDateInstant": "2022-09-18T12:21:45.279688500Z",
            "id": 1.00
        },
        "property": "price",
        "propertyChangeType": "PROPERTY_VALUE_CHANGED",
        "left": null,
        "right": 100.0
    },
    {
        "changeType": "ReferenceChange",
        "globalId": {
            "entity": "cn.tuyucheng.javers.domain.Product",
            "cdoId": 2
        },
        "commitMetadata": {
            "author": "Tuyucheng Author",
            "properties": [],
            "commitDate": "2022-09-18T20:21:45.279",
            "commitDateInstant": "2022-09-18T12:21:45.279688500Z",
            "id": 1.00
        },
        "property": "store",
        "propertyChangeType": "PROPERTY_VALUE_CHANGED",
        "left": null,
        "right": {
            "entity": "cn.tuyucheng.javers.domain.Store",
            "cdoId": 1
        }
    }
]
```

为了检测更改的类型，JaVers会比较对象更新的后续快照。在上面的例子中，当我们更改了实体的属性时，我们得到的是PROPERTY_VALUE_CHANGED更改类型。

### 5.4 阴影

此外，JaVers提供了另一种被审计实体的视图，称为阴影。阴影表示从快照恢复的对象状态。这个概念与[事件溯源](https://www.baeldung.com/cqrs-event-sourced-architecture-resources)密切相关。

阴影有四种不同的范围：

+ Shallow：阴影是从JQL查询中选择的快照创建的
+ Child-value-object：阴影包含所选实体拥有的所有子值对象
+ Commit-deep：阴影是从与选定实体相关的所有快照创建的
+ Deep+：JaVers尝试在加载所有对象的情况下(可能)恢复完整的对象图

让我们使用Child-value-object范围并获取单个商店的阴影：

```java
@GetMapping("/stores/{storeId}/shadows")
public String getStoreShadows(@PathVariable int storeId) {
    Store store = storeService.findStoreById(storeId);
    JqlQuery jqlQuery = QueryBuilder.byInstance(store)
        .withChildValueObjects().build();
    List<Shadow<Store>> shadows = javers.findShadows(jqlQuery);
    return javers.getJsonConverter().toJson(shadows.get(0));
}
```

因此，我们会获得带有Address值对象的Store实体：

```json
{
    "commitMetadata": {
        "author": "Tuyucheng Author",
        "properties": [],
        "commitDate": "2022-09-18T20:26:00.932",
        "commitDateInstant": "2022-09-18T12:26:00.932940Z",
        "id": 2.00
    },
    "it": {
        "id": 1,
        "name": "Product #11",
        "address": {
            "address": "Some street",
            "zipCode": 22222
        },
        "products": []
    }
}
```

为了在结果中获得Product对象值，我们可以应用Commit-deep范围。

## 6. 总结

在本教程中，我们演示了JaVers与Spring Boot和Spring Data的集成。总而言之，JaVers几乎是零配置的，并且JaVers可以有不同的应用场景，从调试到复杂分析。