## 1. 概述

[Spring MVC]()和[Spring Data]()各自在简化应用程序开发方面都做得很好。但是，如果我们把它们放在一起呢？

在本教程中，我们介绍[Spring Data的Web支持](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#core.web)以及它的解析器如何减少样板代码并使我们的控制器更具表现力。

在此过程中，我们将了解[Querydsl](https://www.baeldung.com/intro-to-querydsl)及其与Spring Data的集成情况。

## 2. 背景

**Spring Data的Web支持是在标准Spring MVC平台之上实现的一组与Web相关的特性，旨在为控制器层添加额外的功能**。

Spring Data Web支持的功能是围绕几个解析器类构建的，解析器简化了与[Spring Data]() Repository互操作的控制器方法的实现，并通过其他功能丰富了它们。

这些功能包括从Repository层获取域对象，而无需显式调用Repository实现，以及构建可以作为支持分页和排序的数据段发送给客户端的控制器响应。

此外，对采用一个或多个请求参数的控制器方法的请求可以在内部解析为[Querydsl]()查询。

## 3. Spring Boot项目

为了了解如何使用Spring Data Web支持来改进控制器的功能，我们创建一个基本的Spring Boot项目：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

在本例中，我们包含了[spring-boot-starter-web](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-web)依赖，因为我们将使用它来创建Restful控制器，[spring-boot-starter-data-jpa](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-data-jpa)用于实现持久层，以及[spring-boot-starter-test](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-test)用于测试控制器API。

由于我们将使用[H2]()作为基础数据库，因此我们也包含了[com.h2database](https://search.maven.org/search?q=g:com.h2database AND a:h2)。

请记住，**spring-boot-starter-web默认启用Spring Data Web支持**。因此，我们不需要创建任何额外的@Configuration类来让它在我们的应用程序中工作。相反，对于非Spring Boot项目，我们需要定义一个@Configuration类并使用@EnableWebMvc和@EnableSpringDataWebSupport注解对其进行标注。

### 3.1 域类

现在，我们添加一个简单的User JPA实体类：

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private final String name;
   
    // standard constructor / getters / toString
}
```

### 3.2 Repository

为了保持代码简单，我们的Spring Boot应用程序的功能将缩小到仅从H2内存数据库中获取一些用户实体。

Spring Boot使创建Repository实现变得容易，这些实现提供开箱即用的基本CRUD功能。因此，我们定义一个与User JPA实体一起使用的简单Repository接口：

```java
@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {}
```

UserRepository接口的定义本身并不复杂，只是它扩展了[PagingAndSortingRepository]()。

**这表明Spring MVC能够对数据库记录启用自动分页和排序功能**。

### 3.3 控制器

现在，我们至少需要实现一个基本的Restful控制器，充当客户端和Repository层之间的中间层。

因此，下面我们创建一个控制器类，它在其构造函数中接收一个UserRepository实例，并添加一个通过id查找用户实体的方法：

```java
@RestController
public class UserController {

	private final UserRepository userRepository;

	@Autowired
	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

    @GetMapping("/users/{id}")
    public User findUserById(@PathVariable("id") User user) {
        return user;
    }
}
```

### 3.4 运行应用程序

最后，我们定义一个Spring Boot主类，并通过CommandLineRunner为数据库添加一些初始数据：

```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner initialize(UserRepository userRepository) {
        return args -> {
            Stream.of("John", "Robert", "Nataly", "Helen", "Mary").forEach(name -> {
                User user = new User(name);
                userRepository.save(user);
            });
            userRepository.findAll().forEach(System.out::println);
        };
    }
}
```

现在，运行我们的应用程序。正如预期的那样，我们看到在启动时打印到控制台的持久化用户实体列表：

```shell
User{id=1, name=John}
User{id=2, name=Robert}
User{id=3, name=Nataly}
User{id=4, name=Helen}
User{id=5, name=Mary}
```

## 4. DomainClassConverter类

目前，UserController类只实现了findUserById()方法。乍一看，方法实现看起来相当简单，但它实际上在幕后封装了很多Spring Data Web支持功能。

由于该方法将User实例作为参数，我们可能最终会认为我们需要在请求中显式传递域对象。但是，我们没有。

**Spring MVC使用[DomainClassConverter](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/support/DomainClassConverter.html)类将id路径变量转换为域类的id类型，并使用它从Repository层获取匹配的域对象**，无需进一步查找。

例如，对[http://localhost:8080/users/1](http://localhost:8080/user/1)端点的GET HTTP请求将返回以下结果：

```json
{
    "id": 1,
    "name": "John"
}
```

因此，我们可以编写一个集成测试并检查findUserById()方法的行为：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private UserController userController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenUserControllerInjected_thenNotNull() throws Exception {
        assertThat(userController).isNotNull();
    }

    @Test
    void whenGetRequestToUsersEndPointWithIdPathVariable_thenCorrectResponse() throws Exception {
        mockMvc.perform(get("/users/{id}", "1").contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }
}
```

或者，我们可以使用REST API测试工具(例如[Postman]())来测试该方法。

DomainClassConverter的好处是我们不需要在控制器方法中显式调用Repository实现，**通过简单地指定id路径变量以及可解析的域类实例，我们自动触发了域对象的查找**。

## 5. PageableHandlerMethodArgumentResolver类

Spring MVC支持在控制器和Repository中使用[Pageable](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Pageable.html)类型。

简单地说，Pageable实例是一个保存分页信息的对象。因此，当我们将Pageable参数传递给控制器方法时，**Spring MVC使用[PageableHandlerMethodArgumentResolver](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/web/PageableHandlerMethodArgumentResolver.html)类将Pageable实例解析为[PageRequest](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/PageRequest.html)对象**，这是一个简单的Pageable实现。

### 5.1 使用Pageable作为控制器方法参数

要了解PageableHandlerMethodArgumentResolver类的工作原理，我们向UserController类添加一个新方法：

```java
@GetMapping("/users")
public Page<User> findAllUsers(Pageable pageable) {
    return userRepository.findAll(pageable);
}
```

与findUserById()方法相反，这里我们需要调用Repository实现来获取数据库中持久保存的所有User JPA实体。

由于该方法接收Pageable实例，因此它返回整个实体集的子集，存储在Page<User\>对象中。**[Page](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Page.html)对象是对象集合的子集，它公开了我们可以用于检索有关分页结果的信息的几种方法，包括结果页的总数和我们正在检索的页数**。

默认情况下，Spring MVC使用PageableHandlerMethodArgumentResolver类构造一个PageRequest对象，请求参数如下：

-   page：我们要检索的页面索引，该参数是0索引的，其默认值为0
-   size：我们要检索的页面大小，默认值为20
-   sort：我们可以用来对结果进行排序的一个或多个属性，使用以下格式：property1,property2(,asc|desc)，例如“?sort=name&sort=email,asc”

例如，对http://localhost:8080/user端点的GET请求将返回以下输出：

```json
{
    "content": [
        {
            "id": 1,
            "name": "John"
        },
        {
            "id": 2,
            "name": "Robert"
        },
        {
            "id": 3,
            "name": "Nataly"
        },
        {
            "id": 4,
            "name": "Helen"
        },
        {
            "id": 5,
            "name": "Mary"
        }
    ],
    "pageable": {
        "sort": {
            "sorted": false,
            "unsorted": true,
            "empty": true
        },
        "pageSize": 5,
        "pageNumber": 0,
        "offset": 0,
        "unpaged": false,
        "paged": true
    },
    "last": true,
    "totalElements": 5,
    "totalPages": 1,
    "numberOfElements": 5,
    "first": true,
    "size": 5,
    "number": 0,
    "sort": {
        "sorted": false,
        "unsorted": true,
        "empty": true
    },
    "empty": false
}
```

如我们所见，响应内容包括first、pageSize、totalElements和totalPages JSON元素。这些属性非常有用，因为前端可以使用这些元素轻松实现分页机制。

此外，我们可以使用集成测试来测试findAllUsers()方法：

```java
@Test
void whenGetRequestToUsersEndPoint_thenCorrectResponse() throws Exception {
	mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$['pageable']['paged']").value("true"));
}
```

### 5.2 自定义分页参数

在许多情况下，我们需要自定义分页参数。实现此目的的最简单方法是使用[@PageableDefault](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/web/PageableDefault.html)注解：

```java
@GetMapping("/users")
public Page<User> findAllUsers(@PageableDefault(value = 2, page = 0) Pageable pageable) {
    return userRepository.findAll(pageable);
}
```

或者，我们可以使用PageRequest的[of()](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/PageRequest.html#of-int-int-org.springframework.data.domain.Sort-)静态工厂方法来创建自定义PageRequest对象并将其传递给Repository方法：

```java
@GetMapping("/users")
public Page<User> findAllUsers() {
    Pageable pageable = PageRequest.of(0, 5);
    return userRepository.findAll(pageable);
}
```

第一个参数是从0开始的页号索引，而第二个参数是我们要检索的页的大小。在上面的示例中，我们创建了一个User实体的PageRequest对象，从第1页(0)开始，该页面有5条数据。

此外，我们可以使用page和size请求参数构建一个PageRequest对象：

```java
@GetMapping("/users")
public Page<User> findAllUsers(@RequestParam("page") int page, 
							   @RequestParam("size") int size,
							   Pageable pageable) {
	return userRepository.findAll(pageable);
}
```

使用此实现，对http://localhost:8080/users?page=0&size=2端点的GET请求将返回User对象的第1页，并且结果页面的大小为2：

```json
{
    "content": [
        {
            "id": 1,
            "name": "John"
        },
        {
            "id": 2,
            "name": "Robert"
        }
    ]

    // continues with pageable metadata
}
```

## 6. SortHandlerMethodArgumentResolver类

分页是有效管理大量数据库记录的实际方法。但是，就其本身而言，如果我们不能以某种特定方式对记录进行排序，那就毫无用处了。

为此，Spring MVC提供了[SortHandlerMethodArgumentResolver](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/web/SortHandlerMethodArgumentResolver.html)类。**解析器根据请求参数或[@SortDefault](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/web/SortDefault.html)注解自动创建[Sort](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Sort.html)实例**。

### 6.1 使用排序控制器方法参数

为了清楚地了解SortHandlerMethodArgumentResolver类的工作原理，我们将以下findAllUsersSortedByName()方法添加到控制器类：

```java
@GetMapping("/sortedusers")
public Page<User> findAllUsersSortedByName(@RequestParam("sort") String sort, Pageable pageable) {
    return userRepository.findAll(pageable);
}
```

在这种情况下，SortHandlerMethodArgumentResolver类将使用排序sort参数创建一个Sort对象。

因此，对http://localhost:8080/sortedusers?sort=name端点的GET请求将返回一个JSON数组，其中包含按name属性排序的User对象列表：

```json
{
    "content": [
        {
            "id": 4,
            "name": "Helen"
        },
        {
            "id": 1,
            "name": "John"
        },
        {
            "id": 5,
            "name": "Mary"
        },
        {
            "id": 3,
            "name": "Nataly"
        },
        {
            "id": 2,
            "name": "Robert"
        }
    ]

    // continues with pageable metadata
}
```

### 6.2 使用Sort.by()静态工厂方法

或者，我们可以使用[Sort.by()](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Sort.html#by-java.lang.String...-)静态工厂方法创建一个Sort对象，该方法接收非null、非空的String属性数组进行排序。

在本例中，我们仅按name属性对记录进行排序：

```java
@GetMapping("/sortedusers")
public Page<User> findAllUsersSortedByName() {
    Pageable pageable = PageRequest.of(0, 5, Sort.by("name"));
    return userRepository.findAll(pageable);
}
```

当然，我们可以使用多个属性，只要它们在域类中声明即可。

### 6.3 使用@SortDefault注解

同样，我们可以使用@SortDefault注解来实现相同的结果：

```java
@GetMapping("/sortedusers")
public Page<User> findAllUsersSortedByName(@SortDefault(sort = "name", direction = Sort.Direction.ASC)
										   Pageable pageable) {
	return userRepository.findAll(pageable);
}
```

最后，我们添加一个集成测试来测试方法的行为：

```java
@Test
void whenGetRequestToSorteredUsersEndPoint_thenCorrectResponse() throws Exception {
	mockMvc.perform(get("/sortedusers").contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$['sort']['sorted']").value("true"));
}
```

## 7. Querydsl Web支持

正如我们在介绍中提到的，Spring Data Web支持允许我们在控制器方法中使用请求参数来构建[Querydsl]()的[Predicate](http://www.querydsl.com/static/querydsl/4.1.3/apidocs/com/querydsl/core/types/Predicate.html)类型并构造Querydsl查询。

为了简单起见，我们只介绍Spring MVC如何将请求参数转换为Querydsl [BooleanExpression](http://www.querydsl.com/static/querydsl/4.1.3/apidocs/com/querydsl/core/types/dsl/BooleanExpression.html)，然后将其传递给[QuerydslPredicateExecutor](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/querydsl/QuerydslPredicateExecutor.html)。

为此，首先我们需要将[querydsl-apt](https://search.maven.org/search?q=g:com.querydsl AND a:querydsl-apt)和[querydsl-jpa](https://search.maven.org/search?q=g:com.querydsl AND a:querydsl-jpa)依赖项添加到pom.xml文件中：

```xml
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-apt</artifactId>
</dependency>
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-jpa</artifactId>
</dependency>
```

接下来需要重构我们的UserRepository接口，该接口还必须扩展QuerydslPredicateExecutor接口：

```java
@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long>, QuerydslPredicateExecutor<User> {
}
```

最后，我们将以下方法添加到UserController类：

```java
@GetMapping("/filteredusers")
public Iterable<User> getUsersByQuerydslPredicate(@QuerydslPredicate(root = User.class) Predicate predicate) {
    return userRepository.findAll(predicate);
}
```

尽管方法实现看起来相当简单，但它实际上在背后公开了很多功能。

假设我们要从数据库中获取与给定名称匹配的所有User实体，我们可以通过调用该方法并在URL中指定name请求参数来实现：

```http request
http://localhost:8080/filteredusers?name=John
```

正如预期的那样，该请求将返回以下结果：

```json
[
    {
        "id": 1,
        "name": "John"
    }
]
```

同样，我们可以使用集成测试来测试getUsersByQuerydslPredicate()方法：

```java
@Test
void whenGetRequestToFilteredUsersEndPoint_thenCorrectResponse() throws Exception {
	mockMvc.perform(get("/filteredusers").param("name", "John").contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].name").value("John"));
}
```

这只是Querydsl Web支持工作原理的一个基本示例，但它实际上并没有展现出它所有的力量。

现在，假设我们要获取与给定ID匹配的User实体。在这种情况下，我们只需要在URL中传递一个id请求参数：

```http request
http://localhost:8080/filteredusers?id=2
```

在这种情况下，我们将得到以下结果：

```json
[
    {
        "id": 2,
        "name": "Robert"
    }
]
```

很明显，Querydsl Web支持是一个非常强大的功能，我们可以使用它来获取与给定条件匹配的数据库记录。在所有情况下，**整个过程都归结为仅调用具有不同请求参数的单个控制器方法**。

## 8. 总结

在本教程中，我们深入了解了Spring Web支持的关键组件，并学习了如何在Spring Boot项目中使用它。