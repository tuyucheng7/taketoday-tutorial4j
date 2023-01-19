## 一、概述

Spring Boot 是对 Spring 平台的一个自以为是的补充，专注于约定优于配置——对于以最小的努力开始和创建独立的生产级应用程序非常有用。

本教程是 Boot 的起点，换句话说，是一种以简单方式开始使用基本 Web 应用程序的方法。

我们将介绍一些核心配置、前端、快速数据操作和异常处理。

## 延伸阅读：

## [如何在 Spring Boot 中更改默认端口](https://www.baeldung.com/spring-boot-change-port)

看看如何更改 Spring Boot 应用程序中的默认端口。

[阅读更多](https://www.baeldung.com/spring-boot-change-port)→

## [Spring Boot Starters 简介](https://www.baeldung.com/spring-boot-starters)

最常见的 Spring Boot Starters 的快速概述，以及有关如何在实际项目中使用它们的示例。

[阅读更多](https://www.baeldung.com/spring-boot-starters)→

## 2.设置

首先，让我们使用[Spring Initializr](https://start.spring.io/)为我们的项目生成基础。

生成的项目依赖于 Boot parent：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.2</version>
    <relativePath />
</parent>
```

初始依赖关系将非常简单：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
```

## 3. 应用配置

接下来，我们将为我们的应用程序配置一个简单的主类：

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

```

请注意我们如何使用@SpringBootApplication作为我们的主要应用程序配置类。在幕后，这相当于@Configuration、@EnableAutoConfiguration 和@ComponentScan在一起。

最后，我们将定义一个简单的application.properties文件，该文件目前只有一个属性：

```bash
server.port=8081

```

server.port将服务器端口从默认的 8080 更改为 8081；当然还有更多[可用的 Spring Boot 属性](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html)。

## 4. 简单的 MVC 视图

现在让我们使用 Thymeleaf 添加一个简单的前端。

首先，我们需要将spring-boot-starter-thymeleaf依赖项添加到我们的pom.xml中：

```java
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-thymeleaf</artifactId> 
</dependency>

```

默认情况下启用 Thymeleaf。无需额外配置。

我们现在可以在我们的application.properties中配置它：

```java
spring.thymeleaf.cache=false
spring.thymeleaf.enabled=true 
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html

spring.application.name=Bootstrap Spring Boot

```

接下来，我们将定义一个简单的[控制器](https://www.baeldung.com/spring-controllers)和一个带有欢迎消息的基本主页：

```java
@Controller
public class SimpleController {
    @Value("${spring.application.name}")
    String appName;

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("appName", appName);
        return "home";
    }
}

```

最后，这是我们的home.html：

```html
<html>
<head><title>Home Page</title></head>
<body>
<h1>Hello !</h1>
<p>Welcome to <span th:text="${appName}">Our App</span></p>
</body>
</html>

```

请注意我们如何使用我们在属性中定义的属性，然后将其注入以便我们可以在主页上显示它。

## 5. 安全

接下来，让我们通过首先包含安全启动器来为我们的应用程序添加安全性：

```xml
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-security</artifactId> 
</dependency>

```

到目前为止，我们可以注意到一个模式：大多数 Spring 库都可以通过使用简单的启动器轻松导入到我们的项目中。

一旦spring-boot-starter-security依赖项位于应用程序的类路径上，默认情况下所有端点都会受到保护，使用基于 Spring Security 的内容协商策略的httpBasic或formLogin 。

这就是为什么，如果我们在类路径上有启动器，我们通常应该定义我们自己的自定义安全配置：

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .anyRequest()
            .permitAll()
            .and()
            .csrf()
            .disable();
        return http.build();
    }
}

```

在我们的示例中，我们允许不受限制地访问所有端点。

当然，Spring Security 是一个广泛的主题，不是简单的几行配置就能涵盖的。因此，我们绝对鼓励[深入阅读该主题](https://www.baeldung.com/security-spring)。

## 6.简单的坚持

让我们从定义我们的数据模型开始，一个简单的Book实体：

```java
@Entity
public class Book {
 
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    private String author;
}
```

及其存储库，在这里充分利用 Spring Data：

```java
public interface BookRepository extends CrudRepository<Book, Long> {
    List<Book> findByTitle(String title);
}
```

最后，我们当然需要配置我们新的持久层：

```java
@EnableJpaRepositories("cn.tuyucheng.taketoday.persistence.repo") 
@EntityScan("cn.tuyucheng.taketoday.persistence.model")
@SpringBootApplication 
public class Application {
   ...
}
```

请注意，我们正在使用以下内容：

-   @EnableJpaRepositories扫描指定包的存储库
-   @EntityScan获取我们的 JPA 实体

为了简单起见，我们在这里使用 H2 内存数据库。这是为了让我们在运行项目时没有任何外部依赖。

一旦我们包含 H2 依赖项，Spring Boot 会自动检测它并设置我们的持久性，而不需要额外的配置，除了数据源属性：

```bash
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.url=jdbc:h2:mem:bootapp;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=

```

当然，与安全性一样，持久性是一个比这里的基本设置更广泛的主题，[当然还有待进一步探讨](https://www.baeldung.com/persistence-with-spring-series)。

## 7. Web 和控制器

接下来，让我们看一下 Web 层。我们将从设置一个简单的控制器BookController 开始。

我们将实现基本的 CRUD 操作，通过一些简单的验证来公开Book资源：

```java
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public Iterable findAll() {
        return bookRepository.findAll();
    }

    @GetMapping("/title/{bookTitle}")
    public List findByTitle(@PathVariable String bookTitle) {
        return bookRepository.findByTitle(bookTitle);
    }

    @GetMapping("/{id}")
    public Book findOne(@PathVariable Long id) {
        return bookRepository.findById(id)
          .orElseThrow(BookNotFoundException::new);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookRepository.findById(id)
          .orElseThrow(BookNotFoundException::new);
        bookRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Book updateBook(@RequestBody Book book, @PathVariable Long id) {
        if (book.getId() != id) {
          throw new BookIdMismatchException();
        }
        bookRepository.findById(id)
          .orElseThrow(BookNotFoundException::new);
        return bookRepository.save(book);
    }
}

```

鉴于应用程序的这一方面是一个 API，我们在这里使用了@RestController注解——它相当于一个@Controller和@ ResponseBody——这样每个方法都将返回的资源正确地编组到 HTTP 响应。

请注意，我们在这里将Book实体公开为我们的外部资源。这对于这个简单的应用程序来说很好，但在实际应用程序中，我们可能希望[将这两个概念分开](https://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application)。

## 八、错误处理

现在核心应用程序已经准备就绪，让我们专注于使用@ControllerAdvice的简单集中式错误处理机制：

```java
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ BookNotFoundException.class })
    protected ResponseEntity<Object> handleNotFound(
      Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, "Book not found", 
          new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ BookIdMismatchException.class, 
      ConstraintViolationException.class, 
      DataIntegrityViolationException.class })
    public ResponseEntity<Object> handleBadRequest(
      Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getLocalizedMessage(), 
          new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}

```

除了我们在这里处理的标准异常之外，我们还使用了自定义异常BookNotFoundException：

```java
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    // ...
}

```

这让我们了解了这种全局异常处理机制的可能性。要查看完整的实现，请查看[深入教程](https://www.baeldung.com/exception-handling-for-rest-with-spring)。

请注意，Spring Boot 默认还提供一个/error映射。我们可以通过创建一个简单的error.html来自定义它的视图：

```xml
<html lang="en">
<head><title>Error Occurred</title></head>
<body>
    <h1>Error Occurred!</h1>    
    <b>[<span th:text="${status}">status</span>]
        <span th:text="${error}">error</span>
    </b>
    <p th:text="${message}">message</p>
</body>
</html>
```

与 Boot 中的大多数其他方面一样，我们可以使用一个简单的属性来控制它：

```bash
server.error.path=/error2
```

## 9. 测试

最后，让我们测试一下新的 Books API。

我们可以使用[@SpringBootTest](https://www.baeldung.com/spring-boot-testing)加载应用程序上下文并验证运行应用程序时没有错误：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringContextTest {

    @Test
    public void contextLoads() {
    }
}
```

[接下来，让我们使用REST Assured](https://www.baeldung.com/rest-assured-tutorial)添加一个 JUnit 测试来验证对我们编写的 API 的调用。

首先，我们将添加[rest-assured](https://search.maven.org/artifact/io.rest-assured/rest-assured)依赖项：

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <scope>test</scope>
</dependency>
```

现在我们可以添加测试：

```java
public class SpringBootBootstrapLiveTest {

    private static final String API_ROOT
      = "http://localhost:8081/api/books";

    private Book createRandomBook() {
        Book book = new Book();
        book.setTitle(randomAlphabetic(10));
        book.setAuthor(randomAlphabetic(15));
        return book;
    }

    private String createBookAsUri(Book book) {
        Response response = RestAssured.given()
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .body(book)
          .post(API_ROOT);
        return API_ROOT + "/" + response.jsonPath().get("id");
    }
}

```

首先，我们可以尝试使用变体方法查找书籍：

```java
@Test
public void whenGetAllBooks_thenOK() {
    Response response = RestAssured.get(API_ROOT);
 
    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
}

@Test
public void whenGetBooksByTitle_thenOK() {
    Book book = createRandomBook();
    createBookAsUri(book);
    Response response = RestAssured.get(
      API_ROOT + "/title/" + book.getTitle());
    
    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    assertTrue(response.as(List.class)
      .size() > 0);
}
@Test
public void whenGetCreatedBookById_thenOK() {
    Book book = createRandomBook();
    String location = createBookAsUri(book);
    Response response = RestAssured.get(location);
    
    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    assertEquals(book.getTitle(), response.jsonPath()
      .get("title"));
}

@Test
public void whenGetNotExistBookById_thenNotFound() {
    Response response = RestAssured.get(API_ROOT + "/" + randomNumeric(4));
    
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
}

```

接下来，我们将测试创建一本新书：

```java
@Test
public void whenCreateNewBook_thenCreated() {
    Book book = createRandomBook();
    Response response = RestAssured.given()
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .body(book)
      .post(API_ROOT);
    
    assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
}

@Test
public void whenInvalidBook_thenError() {
    Book book = createRandomBook();
    book.setAuthor(null);
    Response response = RestAssured.given()
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .body(book)
      .post(API_ROOT);
    
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
}

```

然后我们将更新现有的书：

```java
@Test
public void whenUpdateCreatedBook_thenUpdated() {
    Book book = createRandomBook();
    String location = createBookAsUri(book);
    book.setId(Long.parseLong(location.split("api/books/")[1]));
    book.setAuthor("newAuthor");
    Response response = RestAssured.given()
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .body(book)
      .put(location);
    
    assertEquals(HttpStatus.OK.value(), response.getStatusCode());

    response = RestAssured.get(location);
    
    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    assertEquals("newAuthor", response.jsonPath()
      .get("author"));
}

```

我们可以删除一本书：

```java
@Test
public void whenDeleteCreatedBook_thenOk() {
    Book book = createRandomBook();
    String location = createBookAsUri(book);
    Response response = RestAssured.delete(location);
    
    assertEquals(HttpStatus.OK.value(), response.getStatusCode());

    response = RestAssured.get(location);
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
}

```

## 10.总结

这是对 Spring Boot 的快速而全面的介绍。

当然，我们在这里几乎没有触及表面。这个框架的内容比我们在一篇介绍性文章中涵盖的要多得多。

这就是为什么[我们在网站上有不止一篇介绍 Boot 的文章的](https://www.baeldung.com/tag/spring-boot/)原因。