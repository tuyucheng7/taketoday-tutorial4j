## 1. 概述

在本教程中，我们将学习如何使用 Spring Data Cassandra 的反应式数据访问功能。

特别是，这是 Spring Data Cassandra 文章系列的第三篇文章。在这一篇中，我们将使用 REST API 公开一个 Cassandra 数据库。

[我们可以在本系列的第一](https://www.baeldung.com/spring-data-cassandra-tutorial)篇和[第二篇](https://www.baeldung.com/spring-data-cassandratemplate-cqltemplate)文章中阅读有关 Spring Data Cassandra 的更多信息 。

## 延伸阅读：

## [使用 Cassandra、Astra 和 Stargate 构建仪表板](https://www.baeldung.com/cassandra-astra-stargate-dashboard)

了解如何使用 DataStax Astra 构建仪表板，DataStax Astra 是一种由 Apache Cassandra 和 Stargate API 提供支持的数据库即服务。

[阅读更多](https://www.baeldung.com/cassandra-astra-stargate-dashboard)→

## [使用 Cassandra、Astra、REST 和 GraphQL 构建仪表板——记录状态更新](https://www.baeldung.com/cassandra-astra-rest-dashboard-updates)

使用 Cassandra 存储时间序列数据的示例。

[阅读更多](https://www.baeldung.com/cassandra-astra-rest-dashboard-updates)→

## [使用 Cassandra、Astra 和 CQL 构建仪表板——映射事件数据](https://www.baeldung.com/cassandra-astra-rest-dashboard-map)

了解如何根据存储在 Astra 数据库中的数据在交互式地图上显示事件。

[阅读更多](https://www.baeldung.com/cassandra-astra-rest-dashboard-map)→



## 2.Maven依赖

事实上，Spring Data Cassandra 支持 Project Reactor 和 RxJava 反应类型。为了演示，我们将在本教程中使用 Project reactor 的反应类型Flux和Mono 。

首先，让我们添加教程所需的依赖项：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-cassandra</artifactId>
    <version>2.1.2.RELEASE</version>
</dependency>
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
</dependency>
```

最新版本的 spring-data-cassandra 可以在[这里](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.data" AND a%3A"spring-data-cassandra")找到。

现在，我们将通过 REST API 从数据库公开SELECT操作。因此，让我们也为RestController添加依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

## 3. 实施我们的应用程序

由于我们将持久化数据，因此让我们首先定义我们的实体对象：

```java
@Table
public class Employee {
    @PrimaryKey
    private int id;
    private String name;
    private String address;
    private String email;
    private int age;
}
```

接下来，是时候创建一个 从 ReactiveCassandraRepository扩展的EmployeeRepository 了。需要注意的是，这个接口启用了对响应式类型的支持：

```java
public interface EmployeeRepository extends ReactiveCassandraRepository<Employee, Integer> {
    @AllowFiltering
    Flux<Employee> findByAgeGreaterThan(int age);
}
```

### 3.1. 用于 CRUD 操作的 Rest 控制器

为了便于说明，我们将 使用一个简单的 Rest Controller公开一些基本的SELECT操作：

```java
@RestController
@RequestMapping("employee")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    @PostConstruct
    public void saveEmployees() {
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(123, "John Doe", "Delaware", "jdoe@xyz.com", 31));
        employees.add(new Employee(324, "Adam Smith", "North Carolina", "asmith@xyz.com", 43));
        employees.add(new Employee(355, "Kevin Dunner", "Virginia", "kdunner@xyz.com", 24));
        employees.add(new Employee(643, "Mike Lauren", "New York", "mlauren@xyz.com", 41));
        employeeService.initializeEmployees(employees);
    }

    @GetMapping("/list")
    public Flux<Employee> getAllEmployees() {
        Flux<Employee> employees = employeeService.getAllEmployees();
        return employees;
    }

    @GetMapping("/{id}")
    public Mono<Employee> getEmployeeById(@PathVariable int id) {
        return employeeService.getEmployeeById(id);
    }

    @GetMapping("/filterByAge/{age}")
    public Flux<Employee> getEmployeesFilterByAge(@PathVariable int age) {
        return employeeService.getEmployeesFilterByAge(age);
    }
}
```

最后，让我们添加一个简单的EmployeeService：

```java
@Service
public class EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    public void initializeEmployees(List<Employee> employees) {
        Flux<Employee> savedEmployees = employeeRepository.saveAll(employees);
        savedEmployees.subscribe();
    }

    public Flux<Employee> getAllEmployees() {
        Flux<Employee> employees =  employeeRepository.findAll();
        return employees;
    }

    public Flux<Employee> getEmployeesFilterByAge(int age) {
        return employeeRepository.findByAgeGreaterThan(age);
    }

    public Mono<Employee> getEmployeeById(int id) {
        return employeeRepository.findById(id);
    }
}
```

### 3.2. 数据库配置

然后，让我们在application.properties中指定用于连接 Cassandra 的密钥空间和端口：

```plaintext
spring.data.cassandra.keyspace-name=practice
spring.data.cassandra.port=9042
spring.data.cassandra.local-datacenter=datacenter1
```

注意：datacenter1是默认的数据中心名称。

## 4. 测试端点

最后，是时候测试我们的 API 端点了。

### 4.1. 手动测试

首先，让我们从数据库中获取员工记录：

```bash
curl localhost:8080/employee/list
```

结果，我们得到了所有员工：

```javascript
[
    {
        "id": 324,
        "name": "Adam Smith",
        "address": "North Carolina",
        "email": "asmith@xyz.com",
        "age": 43
    },
    {
        "id": 123,
        "name": "John Doe",
        "address": "Delaware",
        "email": "jdoe@xyz.com",
        "age": 31
    },
    {
        "id": 355,
        "name": "Kevin Dunner",
        "address": "Virginia",
        "email": "kdunner@xyz.com",
        "age": 24
    },
    {
        "id": 643,
        "name": "Mike Lauren",
        "address": "New York",
        "email": "mlauren@xyz.com",
       "age": 41
    }
]
```

继续，让我们试着通过他的 ID 找到一个特定的员工：

```bash
curl localhost:8080/employee/643
```

结果，我们让 Mike Lauren 先生回来了：

```javascript
{
    "id": 643,
    "name": "Mike Lauren",
    "address": "New York",
    "email": "mlauren@xyz.com",
    "age": 41
}
```

最后，让我们看看我们的年龄过滤器是否有效：

```bash
curl localhost:8080/employee/filterByAge/35
```

正如预期的那样，我们得到了所有年龄大于 35 岁的员工：

```javascript
[
    {
        "id": 324,
        "name": "Adam Smith",
        "address": "North Carolina",
        "email": "asmith@xyz.com",
        "age": 43
    },
    {
        "id": 643,
        "name": "Mike Lauren",
        "address": "New York",
        "email": "mlauren@xyz.com",
        "age": 41
    }
]
```

### 4.2. 集成测试

此外，让我们通过编写测试用例来测试相同的功能：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class ReactiveEmployeeRepositoryIntegrationTest {

    @Autowired
    EmployeeRepository repository;

    @Before
    public void setUp() {
        Flux<Employee> deleteAndInsert = repository.deleteAll()
          .thenMany(repository.saveAll(Flux.just(
            new Employee(111, "John Doe", "Delaware", "jdoe@xyz.com", 31),
            new Employee(222, "Adam Smith", "North Carolina", "asmith@xyz.com", 43),
            new Employee(333, "Kevin Dunner", "Virginia", "kdunner@xyz.com", 24),
            new Employee(444, "Mike Lauren", "New York", "mlauren@xyz.com", 41))));

        StepVerifier
          .create(deleteAndInsert)
          .expectNextCount(4)
          .verifyComplete();
    }

    @Test
    public void givenRecordsAreInserted_whenDbIsQueried_thenShouldIncludeNewRecords() {
        Mono<Long> saveAndCount = repository.count()
          .doOnNext(System.out::println)
          .thenMany(repository
            .saveAll(Flux.just(
            new Employee(325, "Kim Jones", "Florida", "kjones@xyz.com", 42),
            new Employee(654, "Tom Moody", "New Hampshire", "tmoody@xyz.com", 44))))
          .last()
          .flatMap(v -> repository.count())
          .doOnNext(System.out::println);

        StepVerifier
          .create(saveAndCount)
          .expectNext(6L)
          .verifyComplete();
    }

    @Test
    public void givenAgeForFilter_whenDbIsQueried_thenShouldReturnFilteredRecords() {
        StepVerifier
          .create(repository.findByAgeGreaterThan(35))
          .expectNextCount(2)
          .verifyComplete();
    }
}
```

## 5.总结

总之，我们学习了如何使用 Spring Data Cassandra 使用反应类型来构建非阻塞应用程序。