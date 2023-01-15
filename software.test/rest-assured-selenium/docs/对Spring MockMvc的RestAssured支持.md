## 一、简介

在本教程中，我们将学习如何使用***RestAssuredMockMvc*****测试我们的 Spring REST 控制器，这是一个构建在 Spring 的***MockMvc*之上的 REST-assured API 。

首先，我们将检查不同的设置选项。然后，我们将深入研究如何编写单元测试和集成测试。

本教程使用[Spring MVC](https://www.baeldung.com/spring-mvc)、[Spring MockMVC](https://www.baeldung.com/integration-testing-in-spring)和[REST-assured](https://www.baeldung.com/rest-assured-tutorial)，因此请务必查看这些教程。

## 2.Maven依赖

在开始编写测试之前，我们需要将[*io.rest-assured:spring-mock-mvc*模块](https://search.maven.org/search?q=g:io.rest-assured AND a:spring-mock-mvc)导入到 Maven *pom.xml*中：

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>spring-mock-mvc</artifactId>
    <version>3.3.0</version>
    <scope>test</scope>
</dependency>复制
```

## 3. 初始化*RestAssuredMockMvc*

接下来，我们需要在*独立*或*Web 应用程序上下文*模式下初始化*RestAssuredMockMvc，*这是 DSL 的起点。

在这两种模式下，我们可以在每次测试时即时执行此操作，也可以静态执行一次。让我们看一些例子。

### 3.1。独立

在独立模式下，我们**使用一个或多个*****[@Controller](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/stereotype/Controller.html)*****或*****[@ControllerAdvice](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/ControllerAdvice.html)*****注释类来初始化*RestAssuredMockMvc*。**

如果我们只有几个测试，我们可以及时初始化*RestAssuredMockMvc*：

```java
@Test
public void whenGetCourse() {
    given()
      .standaloneSetup(new CourseController())
      //...
}复制
```

但是，如果我们有很多测试，那么静态地做一次会更容易：

```java
@Before
public void initialiseRestAssuredMockMvcStandalone() {
    RestAssuredMockMvc.standaloneSetup(new CourseController());
}复制
```

### 3.2. Web 应用程序上下文

在 Web 应用程序上下文模式中，我们使用 Spring **[*WebApplicationContext*](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/context/WebApplicationContext.html)****的实例****初始化*RestAssuredMockMvc*。**

与我们在独立模式设置中看到的类似，我们可以在每次测试时及时初始化*RestAssuredMockMvc ：*

```java
@Autowired
private WebApplicationContext webApplicationContext;

@Test
public void whenGetCourse() {
    given()
      .webAppContextSetup(webApplicationContext)
      //...
}复制
```

或者，我们也可以静态地做一次：

```java
@Autowired
private WebApplicationContext webApplicationContext;

@Before
public void initialiseRestAssuredMockMvcWebApplicationContext() {
    RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
}复制
```

## 4. 被测系统 (SUT)

在我们深入研究一些示例测试之前，我们需要一些东西来测试。让我们检查一下我们的测试系统，从我们的*[@SpringBootApplication](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/SpringBootApplication.html)*配置开始：

```java
@SpringBootApplication
class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}复制
```

接下来，我们有一个简单的*[@RestController](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RestController.html)*公开我们的*Course*域：

```java
@RestController
@RequestMapping(path = "/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    public Collection<Course> getCourses() {
        return courseService.getCourses();
    }

    @GetMapping(path = "/{code}", produces = APPLICATION_JSON_UTF8_VALUE)
    public Course getCourse(@PathVariable String code) {
        return courseService.getCourse(code);
    }
}复制
class Course {

    private String code;
    
    // usual contructors, getters and setters
}复制
```

最后但同样重要的是，我们的服务类和*@ControllerAdvice*来处理我们的*CourseNotFoundException*：

```java
@Service
class CourseService {

    private static final Map<String, Course> COURSE_MAP = new ConcurrentHashMap<>();

    static {
        Course wizardry = new Course("Wizardry");
        COURSE_MAP.put(wizardry.getCode(), wizardry);
    }

    Collection<Course> getCourses() {
        return COURSE_MAP.values();
    }

    Course getCourse(String code) {
        return Optional.ofNullable(COURSE_MAP.get(code)).orElseThrow(() -> 
          new CourseNotFoundException(code));
    }
}复制
@ControllerAdvice(assignableTypes = CourseController.class)
public class CourseControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CourseNotFoundException.class)
    public void handleCourseNotFoundException(CourseNotFoundException cnfe) {
        //...
    }
}复制
class CourseNotFoundException extends RuntimeException {

    CourseNotFoundException(String code) {
        super(code);
    }
}复制
```

现在我们有了一个要测试的系统，让我们看看几个*RestAssuredMockMvc*测试。

## 5. REST-assured 的 REST 控制器单元测试

我们可以使用*RestAssuredMockMvc*和我们最喜欢的测试工具[JUnit](https://www.baeldung.com/junit)和[Mockito](https://www.baeldung.com/mockito-series)来测试我们的*@RestController*。

首先，我们模拟并构建我们的 SUT，然后在独立模式下初始化*RestAssuredMockMvc*，如上：

```java
@RunWith(MockitoJUnitRunner.class)
public class CourseControllerUnitTest {

    @Mock
    private CourseService courseService;
    @InjectMocks
    private CourseController courseController;
    @InjectMocks
    private CourseControllerExceptionHandler courseControllerExceptionHandler;

    @Before
    public void initialiseRestAssuredMockMvcStandalone() {
        RestAssuredMockMvc.standaloneSetup(courseController, courseControllerExceptionHandler);
    }复制
```

因为我们已经在*@Before*方法中静态初始化了*RestAssuredMockMvc*，所以不需要在每个测试中初始化它。

**独立模式非常适合单元测试，因为它只初始化我们提供的控制器**，而不是整个应用程序上下文。这使我们的测试保持快速。

现在，让我们看一个示例测试：

```java
@Test
public void givenNoExistingCoursesWhenGetCoursesThenRespondWithStatusOkAndEmptyArray() {
    when(courseService.getCourses()).thenReturn(Collections.emptyList());

    given()
      .when()
        .get("/courses")
      .then()
        .log().ifValidationFails()
        .statusCode(OK.value())
        .contentType(JSON)
        .body(is(equalTo("[]")));
}复制
```

除了*@RestController*之外，使用*@ControllerAdvice*初始化*RestAssuredMockMvc*也使我们能够测试我们的异常场景：

```java
@Test
public void givenNoMatchingCoursesWhenGetCoursesThenRespondWithStatusNotFound() {
    String nonMatchingCourseCode = "nonMatchingCourseCode";

    when(courseService.getCourse(nonMatchingCourseCode)).thenThrow(
      new CourseNotFoundException(nonMatchingCourseCode));

    given()
      .when()
        .get("/courses/" + nonMatchingCourseCode)
      .then()
        .log().ifValidationFails()
        .statusCode(NOT_FOUND.value());
}复制
```

如上所示，REST-assured 使用熟悉的 given-when-then 场景格式来定义测试：

-   *given()* — 指定 HTTP 请求详细信息
-   *when()* — 指定 HTTP 动词和路由
-   *then()* — 验证 HTTP 响应

## 6. REST-assured 的 REST 控制器集成测试

我们还可以将*RestAssuredMockMvc*与 Spring 的测试工具一起用于我们的集成测试。

*首先，我们使用@RunWith(SpringRunner.class)*和[*@SpringBootTest(webEnvironment = RANDOM_PORT)*](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/test/context/SpringBootTest.html)设置我们的测试类：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CourseControllerIntegrationTest {
    //...
}复制
```

*这将使用在我们的@SpringBootApplication*类中配置的应用程序上下文在随机端口上运行我们的测试。

接下来，我们注入我们的*WebApplicationContext*并使用它来初始化*RestAssuredMockMvc*，如上：

```java
@Autowired
private WebApplicationContext webApplicationContext;

@Before
public void initialiseRestAssuredMockMvcWebApplicationContext() {
    RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
}复制
```

现在我们已经设置了测试类并初始化了*RestAssuredMockMvc*，我们准备开始编写我们的测试：

```java
@Test
public void givenNoMatchingCourseCodeWhenGetCourseThenRespondWithStatusNotFound() {
    String nonMatchingCourseCode = "nonMatchingCourseCode";

    given()
      .when()
        .get("/courses/" + nonMatchingCourseCode)
      .then()
        .log().ifValidationFails()
        .statusCode(NOT_FOUND.value());
}复制
```

请记住，由于我们在*@Before*方法中静态初始化了*RestAssuredMockMvc*，因此无需在每个测试中都对其进行初始化。

要深入了解 REST-assured API，请查看我们的[REST-assured Guide](https://www.baeldung.com/rest-assured-tutorial)。

## 7. 结论

在本教程中，我们看到了如何使用 REST-assured 的*spring-mock-mvc*模块来测试我们的 Spring MVC 应用程序。

在独立模式下初始化*RestAssuredMockMvc***非常适合单元测试**，因为它只初始化提供的*Controller*，让我们的测试保持快速。

在Web 应用程序上下文模式下初始化*RestAssuredMockMvc***非常适合集成测试**，因为它使用了我们完整的*WebApplicationContext*。