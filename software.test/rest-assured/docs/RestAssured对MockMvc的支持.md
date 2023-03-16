## 1. 简介

在本教程中，我们将学习如何**使用RestAssuredMockMvc测试我们的Spring REST控制器**，这是一个构建在Spring的MockMvc之上的Rest-Assured API。

首先，我们将检查不同的设置选项。然后，我们将深入探讨如何编写单元测试和集成测试。

本教程使用[Spring MVC](https://www.baeldung.com/spring-mvc)、[Spring MockMVC](https://www.baeldung.com/integration-testing-in-spring)和[Rest-Assured](https://www.baeldung.com/rest-assured-tutorial)，因此请务必也查看这些教程。

## 2. Maven依赖

在开始编写测试之前，我们需要将[io.rest-assured:spring-mock-mvc](https://central.sonatype.com/artifact/io.rest-assured/spring-mock-mvc/5.3.0)模块添加到pom.xml中：

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>spring-mock-mvc</artifactId>
    <version>3.3.0</version>
    <scope>test</scope>
</dependency>
```

## 3. 初始化RestAssuredMockMvc

接下来，我们需要在独立或Web应用程序上下文模式下初始化RestAssuredMockMvc。

在这两种模式下，我们可以在每次测试中实时执行此操作，也可以静态执行一次。让我们看一些例子。

### 3.1 独立模式

在独立模式下，我们**使用一个或多个[@Controller](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/stereotype/Controller.html)或[@ControllerAdvice](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/ControllerAdvice.html)注解类来初始化RestAssuredMockMvc**。

如果我们只有几个测试，我们可以及时初始化RestAssuredMockMvc：

```java
@Test
void whenGetCourse() {
    given()
        .standaloneSetup(new CourseController())
    // ...
}
```

但是，如果我们有很多测试，那么静态地初始化一次会更容易：

```java
@BeforeEach
void initialiseRestAssuredMockMvcStandalone() {
    RestAssuredMockMvc.standaloneSetup(new CourseController());
}
```

### 3.2 Web应用上下文

在Web应用程序上下文模式中，我们**使用Spring [WebApplicationContext](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/context/WebApplicationContext.html)的实例初始化RestAssuredMockMvc**。

类似于我们在独立模式设置中看到的，我们可以在每个测试中及时初始化RestAssuredMockMvc：

```java
@Autowired
private WebApplicationContext webApplicationContext;

@Test
void whenGetCourse() {
    given()
        .webAppContextSetup(webApplicationContext)
    //...
}
```

或者，我们可以静态地初始化一次：

```java
@Autowired
private WebApplicationContext webApplicationContext;

@BeforeEach
void initialiseRestAssuredMockMvcWebApplicationContext() {
    RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
}
```

## 4. 被测系统(SUT)

在我们深入研究一些示例测试之前，我们需要准备一些东西用于测试。让我们构建一个将要用于测试的系统，从我们的[@SpringBootApplication](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/SpringBootApplication.html)配置开始：

```java
@SpringBootApplication
public class RestAssuredApplication {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

接下来，我们有一个简单的[@RestController](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RestController.html)公开我们的Course域：

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
}
```

```java
class Course {

    private String code;

    // usual constructors, getters and setters
}
```

最后但同样重要的是，我们的Service类和处理CourseNotFoundException异常的@ControllerAdvice：

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
        return Optional.ofNullable(COURSE_MAP.get(code)).orElseThrow(() -> new CourseNotFoundException(code));
    }
}
```

```java
@ControllerAdvice(assignableTypes = CourseController.class)
public class CourseControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CourseNotFoundException.class)
    @SuppressWarnings("ThrowablePrintedToSystemOut")
    public void handleCourseNotFoundException(CourseNotFoundException cnfe) {
        System.out.println(cnfe);
    }
}
```

```java
class CourseNotFoundException extends RuntimeException {

    CourseNotFoundException(String code) {
        super(code);
    }
}
```

现在我们有了要测试的系统，让我们看一些RestAssuredMockMvc测试。

## 5. 使用REST-Assured进行REST控制器单元测试

我们可以将RestAssuredMockMvc与我们最常用的测试工具[JUnit](https://www.baeldung.com/junit)和[Mockito](https://www.baeldung.com/mockito-series)一起使用来测试我们的@RestController。

首先，我们mock并构建我们的SUT，然后在独立模式下初始化RestAssuredMockMvc，如下所示：

```java
@ExtendWith(MockitoExtension.class)
class CourseControllerUnitTest {

    @Mock
    private CourseService courseService;
    @InjectMocks
    private CourseController courseController;
    @InjectMocks
    private CourseControllerExceptionHandler courseControllerExceptionHandler;

    @BeforeEach
    void initialiseRestAssuredMockMvcStandalone() {
        RestAssuredMockMvc.standaloneSetup(courseController, courseControllerExceptionHandler);
    }
}
```

因为我们已经在@BeforeEach方法中静态初始化了RestAssuredMockMvc，所以不需要在每个测试中都初始化它。

**独立模式非常适合单元测试，因为它只初始化我们提供的控制器**，而不是整个应用程序上下文。这使我们的测试保持快速。

现在，让我们看一个示例测试：

```java
@Test
void givenNoExistingCoursesWhenGetCoursesThenRespondWithStatusOkAndEmptyArray() {
	when(courseService.getCourses()).thenReturn(Collections.emptyList());
    
	given().when()
	    .get("/courses")
	    .then()
	    .log().ifValidationFails()
	    .statusCode(OK.value())
	    .contentType(JSON)
	    .body(is(equalTo("[]")));
}
```

除了@RestController之外，使用我们的@ControllerAdvice初始化RestAssuredMockMvc使我们能够测试我们的异常场景：

```java
@Test
void givenNoMatchingCoursesWhenGetCoursesThenRespondWithStatusNotFound() {
	String nonMatchingCourseCode = "nonMatchingCourseCode";
    
	when(courseService.getCourse(nonMatchingCourseCode)).thenThrow(new CourseNotFoundException(nonMatchingCourseCode));
    
	given().when()
	    .get("/courses/" + nonMatchingCourseCode)
	    .then()
	    .log().ifValidationFails()
	    .statusCode(NOT_FOUND.value());
}
```

如上所示，Rest-Assured使用熟悉的given-when-then场景格式来定义测试：

-   given()：指定HTTP请求详细信息
-   when()：指定HTTP谓词和路由
-   then()：验证HTTP响应

## 6. 使用REST-Assured进行REST控制器集成测试

我们还可以将RestAssuredMockMvc与Spring的测试工具一起使用来进行集成测试。

首先，我们使用@ExtendWith(SpringExtension.class)和[@SpringBootTest(webEnvironment = RANDOM_PORT)](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/test/context/SpringBootTest.html)设置我们的测试类：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CourseControllerIntegrationTest {
    // ...
}
```

这将在随机端口上使用@SpringBootApplication类中配置的应用程序上下文运行我们的测试。

接下来，我们注入我们的WebApplicationContext并使用它来初始化RestAssuredMockMvc，如下所示：

```java
@Autowired
private WebApplicationContext webApplicationContext;

@BeforeEach
void initialiseRestAssuredMockMvcWebApplicationContext() {
	RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
}
```

现在我们已经设置了测试类并初始化了RestAssuredMockMvc，我们可以开始编写测试了：

```java
@Test
void givenNoMatchingCourseCodeWhenGetCourseThenRespondWithStatusNotFound() {
	String nonMatchingCourseCode = "nonMatchingCourseCode";
    
	given().when()
	    .get("/courses/" + nonMatchingCourseCode)
	    .then()
	    .log().ifValidationFails()
	    .statusCode(NOT_FOUND.value());
}
```

请记住，由于我们已经在@BeforeEach方法中静态初始化了RestAssuredMockMvc，因此无需在每个测试中都对其进行初始化。

要更深入地了解Rest-Assured API，请查看我们的[REST-Assured指南](RestAssured指南.md)。

## 7. 总结

在本教程中，我们了解了如何使用Rest-Assured的spring-mock-mvc模块测试我们的Spring MVC应用程序。

在独立模式下初始化RestAssuredMockMvc非常适合单元测试，因为它只初始化提供的Controller，使我们的测试保持快速。

在Web应用程序上下文模式下初始化RestAssuredMockMvc非常适合集成测试，因为它使用了我们完整的WebApplicationContext。