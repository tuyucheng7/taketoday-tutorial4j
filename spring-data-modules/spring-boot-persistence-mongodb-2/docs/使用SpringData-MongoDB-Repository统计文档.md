## 1. 概述

在本教程中，我们将看到使用[Spring Data MongoDB](https://www.baeldung.com/spring-data-mongodb-tutorial)对集合中的文档进行计数的不同方法。我们将使用MongoRepository中提供的所有工具。

我们将使用注解、查询方法和CrudRepository中的方法。此外，我们将构建一个简单的服务来聚合我们不同的用例。

## 2. 用例设置

我们的用例由模型类、Repository和Service类组成。此外，我们将创建一个测试类来确保一切都按预期工作。

### 2.1 模型

我们将从创建我们的模型类开始。它将基于汽车的一些属性：

```java
@Document
public class Car {
    private String name;

    private String brand;

    public Car(String brand) {
        this.brand = brand;
    }

    // getters and setters
}
```

我们省略了ID属性，因为我们在示例中不需要它。此外，我们添加了一个构造函数，该构造函数将brand属性作为参数以使测试更容易。

### 2.2 Repository

让我们定义一个不包含任何方法的Repository：

```java
public interface CarRepository extends MongoRepository<Car, String> {
}
```

上面我们将ID类型指定为String，即使我们没有在我们的模型中声明一个ID属性。这是因为MongoDB创建了一个默认的唯一ID，如果需要，我们仍然可以通过findById()访问它。

### 2.3 Service

我们的Service类将以不同的方式利用Spring Data Repository接口：

```java
@Service
public class CountCarService {

    @Autowired
    private CarRepository repo;
}
```

### 2.4 准备测试

我们所有的测试都将在我们的Service类上运行。我们只需要一些设置，这样我们就不用编写重复的代码：

```java
@SpringBootTest(classes = SpringBootCountApplication.class)
@DirtiesContext
@ExtendWith(SpringExtension.class)
@TestPropertySource("/embedded.properties")
class CountCarServiceIntegrationTest {
    @Autowired
    private CountCarService service;

    Car car1 = new Car("B-A");

    @BeforeEach
    void init() {
        service.insertCar(car1);
        service.insertCar(new Car("B-B"));
    }
}
```

**我们将在每次测试之前运行init以简化我们的测试场景**。此外，我们在init()之外定义了car1，以便在以后的测试中可以访问它。

## 3. 使用CrudRepository

**当使用扩展CrudRepository的MongoRepository时，我们可以访问基本功能，包括count()方法**。

### 3.1 count()方法

因此，在我们的第一个计数示例中，我们的Repository中没有任何方法，但我们仍然可以在我们的Service中调用它：

```java
public long getCountWithCrudRepository() {
    return repo.count();
}
```

下面是一个简单的测试：

```java
@Test
void givenAllDocs_whenCrudRepositoryCount_thenCountEqualsSize() {
    List<Car> all = service.findCars();
    long count = service.getCountWithCrudRepository();
    
    assertEquals(count, all.size());
}
```

因此，我们确保count()输出的数字与我们集合中所有文档列表的大小相同。

**最重要的是，我们必须记住，count操作比列出所有文档更具成本效益**。这既体现在性能方面，也体现在减少代码方面。它不会对小集合产生影响，但对于大集合，我们最终可能会得到一个[OutOfMemoryError](https://www.baeldung.com/java-gc-overhead-limit-exceeded)。**简而言之，通过列出整个集合来统计文档并不是一个好主意**。

### 3.2 使用Example对象过滤

如果我们想要计算具有特定属性值的文档，CrudRepository也可以提供帮助。**count()方法有一个重载版本，它接收一个[Example](https://www.baeldung.com/spring-data-query-by-example)对象**：

```java
public long getCountWithExample(Car item) {
    return repo.count(Example.of(item));
}
```

因此，这简化了任务。**现在我们只需用我们想要过滤的属性填充一个对象，Spring将完成剩下的工作**。让我们在测试中涵盖它：

```java
@Test
void givenFilteredDocs_whenExampleCount_thenCountEqualsSize() {
    long all = service.findCars()
        .stream()
        .filter(car -> car.getBrand().equals(car1.getBrand()))
        .count();
    long count = service.getCountWithExample(car1);
    
    assertEquals(count, all);
}
```

## 4. 使用@Query注解

我们的下一个示例将基于@Query注解：

```java
@Query(value = "{}", count = true)
Long countWithAnnotation();
```

我们必须指定value属性，否则Spring将尝试从我们的方法名称创建查询。**但是，由于我们想要计算所有文档，因此我们只需指定一个空查询**。

**然后，我们通过将count属性设置为true来指定此查询的结果应该是计数[投影](https://www.baeldung.com/java-mongodb-aggregations)**。

下面是对应的测试：

```java
@Test
void givenAllDocs_whenQueryAnnotationCount_thenCountEqualsSize() {
    List<Car> all = service.findCars();
    long count = service.getCountWithQueryAnnotation();
    
    assertEquals(count, all.size());
}
```

### 4.1 过滤属性

**我们可以扩展我们的示例，按brand属性过滤**。让我们向Repository添加一个新方法：

```java
@Query(value = "{brand: ?0}", count = true)
public long countBrand(String brand);
```

在value属性中，我们指定了完整的[MongoDB样式查询](https://www.mongodb.com/docs/manual/tutorial/query-documents/)。“?0”占位符代表我们方法的第一个参数，这将是我们的[查询](https://www.baeldung.com/queries-in-spring-data-mongodb)参数值。

MongoDB查询有一个JSON结构，我们在其中指定字段名称以及我们要过滤的值。因此，当我们调用countBrand("A")时，查询将转换为{brand: "A"}。这意味着我们将按brand属性值为“A”的元素过滤我们的集合。

## 5. 编写派生查询方法

[派生查询方法](https://courses.baeldung.com/courses/1295711/lectures/30127898)是我们Repository中不包含带有value的@Query注解的任何方法。这些方法由Spring按名称解析，因此我们不必编写查询。

**由于我们的CrudRepository中已经有一个count()方法，因此让我们创建一个按特定brand计数的示例**：

```java
Long countByBrand(String brand);
```

**此方法将计算brand属性与参数值匹配的所有文档**。

现在，我们将它添加到我们的Service中：

```java
public long getCountBrandWithQueryMethod(String brand) {
    return repo.countByBrand(brand);
}
```

然后我们通过将其与过滤流计数操作进行比较来确保我们的方法行为正确：

```java
@Test
void givenFilteredDocs_whenQueryMethodCountByBrand_thenCountEqualsSize() {
    String filter = "B-A";
    long all = service.findCars()
        .stream()
        .filter(car -> car.getBrand().equals(filter))
        .count();
    long count = service.getCountBrandWithQueryMethod(filter);
    
    assertEquals(count, all);
}
```

**当我们只需要编写几个不同的查询时，这非常有用**。但是，如果我们需要太多不同的计数查询，它就会变得难以维护。

## 6. 使用带条件的动态计数查询

**当我们需要一种更健壮的方法时，我们可以将Criteria与Query对象一起使用**。

但是，要运行Query，我们需要MongoTemplate。它在启动期间实例化，并在SimpleMongoRepository的mongoOperations字段中可用。

访问它的一种方法是扩展SimpleMongoRepository并创建自定义实现，而不是简单地扩展MongoRepository。但是，有一个更简单的方法，我们可以将它注入到我们的Service中：

```java
@Autowired
private MongoTemplate mongo;
```

然后我们可以创建新的计数方法，将Query传递给MongoTemplate中的count()方法：

```java
public long getCountBrandWithCriteria(String brand) {
    Query query = new Query();
    query.addCriteria(Criteria.where("brand").is(brand));
    return mongo.count(query, Car.class);
}
```

**当我们需要创建动态查询时，这种方法很有用**。我们可以完全控制投影的创建方式。

### 6.1 使用Example对象过滤

**Criteria对象还允许我们传递一个Example对象**：

```java
public long getCountWithExampleCriteria(Car item) {
    Query query = new Query();
    query.addCriteria(Criteria.byExample(item));
    return mongo.count(query, Car.class);
}
```

这使得按属性过滤更容易，同时仍然允许动态部分。

## 7. 总结

在本文中，我们看到了在Spring Data MongoDB中使用Repository方法使用计数投影的不同方法。

我们使用了可用的方法，还使用不同的方法创建了新的方法。此外，我们通过将计数方法与列出集合中的所有对象进行比较来创建测试。同样，我们了解了为什么这样计算文档不是一个好主意。

此外，我们更深入地使用了MongoTemplate来创建更动态的计数查询。