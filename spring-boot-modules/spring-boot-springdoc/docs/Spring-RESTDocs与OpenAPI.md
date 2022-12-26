## 1. 概述

[Spring REST Docs](https://docs.spring.io/spring-restdocs/docs/2.0.2.RELEASE/reference/html5/#introduction)和[OpenAPI 3.0](http://spec.openapis.org/oas/v3.0.3)是为REST API创建API文档的两种方式。

在本教程中，我们将研究它们的相对优点和缺点。

## 2. 起源简介

[Spring REST Docs](https://www.baeldung.com/spring-rest-docs)是由Spring社区开发的框架，旨在为RESTful API创建准确的文档。**它采用测试驱动的方法**，其中文档被编写为Spring MVC测试、Spring Webflux的WebTestClient或REST-Assured。

运行测试的输出被创建为[AsciiDoc](http://asciidoc.org/)文件，可以使用[Asciidoctor](https://asciidoctor.org/)将这些文件组合在一起以生成描述我们的API的HTML页面。**由于它遵循TDD方法，Spring REST Docs自动带来了它的所有优势**，例如不易出错的代码、更少的返工和更快的反馈周期，仅举几例。

另一方面，[OpenAPI](https://www.baeldung.com/spring-rest-openapi-documentation)是从Swagger 2.0中诞生的规范，在撰写本文时，它的最新版本是3.0，并且有许多已知的[实现](https://github.com/OAI/OpenAPI-Specification/blob/master/IMPLEMENTATIONS.md)。

与任何其他规范一样，OpenAPI为其实现制定了某些要遵循的基本规则。简而言之，**所有OpenAPI实现都应该以JSON或YAML格式将文档生成为JSON对象**。

还有[许多工具](https://github.com/OAI/OpenAPI-Specification/blob/master/IMPLEMENTATIONS.md#user-interfaces)可以将此JSON/YAML输入并生成一个UI来可视化和导航API。例如，这在验收测试期间会派上用场。在这里的代码示例中，我们将使用[springdoc](https://springdoc.org/)-一个用于带有Spring Boot的OpenAPI 3的库。

在详细介绍这两者之前，让我们快速设置一个要记录的API。

## 3. REST API

让我们使用Spring Boot开发一个基本的CRUD API。

### 3.1 Repository

在这里，我们将使用的Repository是一个基本的PagingAndSortingRepository接口，模型为Foo：

```java
@Repository
public interface FooRepository extends PagingAndSortingRepository<Foo, Long>{}
```

```java
@Entity
public class Foo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    @Column()
    private String body;

    // constructor, getters and setters
}
```

我们还将使用schema.sql和data.sql[加载Repository](https://www.baeldung.com/spring-boot-data-sql-and-schema-sql)。

### 3.2 Controller

接下来，让我们看看控制器，为简洁起见跳过其实现细节：

```java
@RestController
@RequestMapping("/foo")
public class FooController {

    @Autowired
    FooRepository repository;

    @GetMapping
    public ResponseEntity<List<Foo>> getAllFoos() {
        // implementation
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<Foo> getFooById(@PathVariable("id") Long id) {
        // implementation
    }

    @PostMapping
    public ResponseEntity<Foo> addFoo(@RequestBody @Valid Foo foo) {
        // implementation
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoo(@PathVariable("id") long id) {
        // implementation
    }

    @PutMapping("/{id}")
    public ResponseEntity<Foo> updateFoo(@PathVariable("id") long id, @RequestBody Foo foo) {
        // implementation
    }
}
```

### 3.3 Application

最后，启动应用程序：

```java
@SpringBootApplication()
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## 4. OpenAPI/Springdoc

现在让我们看看springdoc如何向我们的Foo REST API添加文档。

回想一下，**它将生成一个JSON对象和基于该对象的API的UI可视化**。

### 4.1 基本用户界面

首先，我们只添加几个Maven依赖项-用于生成JSON的[springdoc-openapi-data-rest](https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-data-rest)和用于渲染UI的[springdoc-openapi-ui](https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-ui)。

该工具将检查我们API的代码，并读取控制器方法的注解。在此基础上，它将生成将在[http://localhost:8080/api-docs/](http://localhost:8080/api-docs/)上运行的API JSON，它还将在http://localhost:8080/swagger-ui-custom.html提供一个基本的UI：

<img src="../assets/img_8.png">

正如我们所看到的，根本没有添加任何代码，我们就获得了API的漂亮可视化效果，一直到Foo模式。使用Try it out按钮，我们甚至可以执行操作并查看结果。

现在，**如果我们想向API添加一些真实的文档怎么办**？就API的全部内容而言、它的所有操作意味着什么、应该输入什么，以及期望什么响应？

我们将在下一节中对此进行介绍。

### 4.2 详细的用户界面

让我们首先看看如何向API添加一个通用的描述。

为此，我们将向我们的Spring Boot主类添加一个OpenAPI bean：

```java
@Bean
public OpenAPI customOpenAPI(@Value("${springdoc.version}") String appVersion) {
	return new OpenAPI().info(new Info()
        .title("Foobar API")
		.version(appVersion)
		.description("This is a sample Foobar server created using springdocs - a library for OpenAPI 3 with spring boot.")
		.termsOfService("http://swagger.io/terms/")
		.license(new License().name("Apache 2.0")
			.url("http://springdoc.org")));
}
```

接下来，为了向我们的API操作添加一些信息，我们将使用一些特定于OpenAPI的注解来标注我们的映射请求方法。

让我们看看如何描述getFooById，我们将在另一个控制器FooBarController中执行此操作，它类似于我们的FooController：

```java
@RestController
@RequestMapping("/foobar")
@Tag(name = "foobar", description = "the foobar API with documentation annotations")
public class FooBarController {
    @Autowired
    FooRepository repository;

    @Operation(summary = "Get a foo by foo id")
    @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "found the foo", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Foo.class))}),
          @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
          @ApiResponse(responseCode = "404", description = "Foo not found", content = @Content) })
    @GetMapping(value = "{id}")
    public ResponseEntity getFooById(@Parameter(description = "id of foo to be searched")
                                     @PathVariable("id") String id) {
        // implementation omitted for brevity
    }
    // other mappings, similarly annotated with @Operation and @ApiResponses
}
```

现在让我们看看在UI上的效果：

<img src="../assets/img_9.png">

因此，通过这些最少的配置，我们API的用户现在可以看到它的内容、如何使用它以及预期的结果，我们所要做的就是编译代码并运行Spring Boot应用程序。

## 5. Spring REST Docs

REST Docs是一种完全不同的API文档，如前所述，该过程是测试驱动的，输出采用静态HTML页面的形式。

在这里的示例中，**我们将使用**[Spring MVC测试](https://www.baeldung.com/integration-testing-in-spring)**来创建文档片段**。

一开始，我们需要将[spring-restdocs-mockmvc](https://mvnrepository.com/artifact/org.springframework.restdocs/spring-restdocs-mockmvc)依赖项和[asciidoc Maven插件](https://www.baeldung.com/spring-rest-docs#asciidocs)添加到我们的pom中。

### 5.1 JUnit5测试

现在让我们看一下包含我们文档的JUnit5测试：

```java
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest(classes = Application.class)
public class SpringRestDocsIntegrationTest {
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
              .apply(documentationConfiguration(restDocumentation))
              .build();
    }

    @Test
    public void whenGetFooById_thenSuccessful() throws Exception {
        ConstraintDescriptions desc = new ConstraintDescriptions(Foo.class);
        this.mockMvc.perform(get("/foo/{id}", 1))
              .andExpect(status().isOk())
              .andDo(document("getAFoo", preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint()),
                    pathParameters(parameterWithName("id").description("id of foo to be searched")),
                    responseFields(fieldWithPath("id")
                                .description("The id of the foo" + collectionToDelimitedString(desc.descriptionsForProperty("id"), ". ")),
                          fieldWithPath("title").description("The title of the foo"),
                          fieldWithPath("body").description("The body of the foo"))));
    }
    // more test methods to cover other mappings
}
```

运行此测试后，我们可以在targets/generated-snippets目录中找到几个文件，其中包含有关给定API操作的信息。特别是，whenGetFooById_thenSuccessful将在目录中的getAFoo文件夹中为我们提供8个adoc：

<img src="../assets/img_10.png">

下面是一个示例http-response.adoc，当然包含响应正文：

```adoc
[source,http,options="nowrap"]
----
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 58

{
  "id" : 1,
  "title" : "title",
  "body" : "body"
}
----
```

### 5.2 fooapi.adoc

现在我们需要一个主文件，它将所有这些片段文件组合在一起，形成一个结构良好的HTML。

我们称它为fooapi.adoc并查看其中的一小部分：

```adoc
=== Accessing the foo GET
A `GET` request is used to access the foo read.

==== Request structure
include::{snippets}/getAFoo/http-request.adoc[]

==== Path Parameters
include::{snippets}/getAFoo/path-parameters.adoc[]

==== Example response
include::{snippets}/getAFoo/http-response.adoc[]

==== CURL request
include::{snippets}/getAFoo/curl-request.adoc[]
```

**执行asciidoctor-maven-plugin后，我们在target/generated-docs文件夹中得到最终的HTML文件fooapi.html**：

<img src="../assets/img_11.png">

这是它在浏览器中打开时的样子：

<img src="../assets/img_12.png">

## 6. 要点

现在我们已经了解了这两种实现方式，让我们总结一下它们的优点和缺点。

使用springdoc时，**我们必须使用的注解使我们的Rest控制器的代码变得混乱并降低了它的可读性**。此外，文档与代码紧密耦合，并将进入生产环境。

毋庸置疑，维护文档是这里的另一个挑战-如果API中的某些内容发生变化，程序员是否会始终记得更新相应的OpenAPI注解？

另一方面，**REST Docs看起来既不像其他UI那样吸引人，也不能用于验收测试**，但它也有它的优点。

值得注意的是，**Spring MVC测试的成功完成不仅为我们提供了代码片段，而且还像任何其他单元测试一样验证了我们的API**，这迫使我们根据API修改(如果有)对文档进行更改。此外，文档代码与实现完全分开。

但同样，另一方面，**我们不得不编写更多的代码来生成文档**。首先，测试本身可以说与OpenAPI注解一样冗长，其次是主adoc。

它还需要更多的步骤来生成最终的HTML-首先运行测试，然后运行插件；而Springdoc只要求我们运行我们的主应用程序即可。

## 7. 总结

在本教程中，我们了解了基于OpenAPI的springdoc和Spring REST Docs之间的区别，并演示了如何实现这两者来为基本的CRUD API生成文档。

总而言之，两者都有其优点和缺点，使用其中的哪一种取决于我们的具体要求。