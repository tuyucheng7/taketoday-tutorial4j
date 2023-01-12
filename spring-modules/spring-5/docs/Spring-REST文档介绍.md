## 1. 概述

[Spring REST Docs](https://spring.io/projects/spring-restdocs)为 RESTful 服务生成既准确又可读的文档。它将手写文档与 Spring 测试生成的自动生成的文档片段相结合。

## 2.优点

该项目背后的一个主要理念是使用测试来生成文档。这确保生成的文档始终与 API 的实际行为准确匹配。此外，输出已准备好由[Asciidoctor](http://asciidoctor.org/)处理，Asciidoctor是一个以 AsciiDoc 语法为中心的发布工具链。这是用于生成 Spring Framework 文档的同一工具。

这些方法减少了其他框架施加的限制。Spring REST Docs 生成准确、简洁且结构良好的文档。然后，该文档允许 Web 服务消费者轻松获得他们需要的信息。

该工具还有一些其他优点，例如：

-   生成 curl 和 http 请求片段
-   易于在项目 jar 文件中打包文档
-   易于向片段添加额外信息
-   同时支持 JSON 和 XML

生成代码片段的测试可以使用 Spring MVC 测试支持、Spring Webflux 的WebTestClient或 REST-Assured 编写。

在我们的示例中，我们将使用 Spring MVC 测试，但使用其他框架非常相似。

## 3.依赖关系

在项目中开始使用 Spring REST Docs 的理想方式是使用依赖管理系统。在这里，我们使用 Maven 作为构建工具，因此可以将下面的依赖项并粘贴到你的 POM 中：

```xml
<dependency>
    <groupId>org.springframework.restdocs</groupId>
    <artifactId>spring-restdocs-mockmvc</artifactId>
    <version>2.0.4.RELEASE</version>
</dependency>
```

你还可以在[此处](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.restdocs" AND a%3A"spring-restdocs-mockmvc")检查 Maven Central 是否有新版本的依赖项。

在我们的示例中，我们需要spring-restdocs-mockmvc依赖项，因为我们使用 Spring MVC 测试支持来创建我们的测试。

如果我们想使用 WebTestClient 或 REST Assured 编写测试，我们将需要[spring-restdocs-webtestclient](https://search.maven.org/classic/#search|ga|1|a%3A"spring-restdocs-webtestclient")和[spring-restdocs-restassured](https://search.maven.org/classic/#search|ga|1|a%3A"spring-restdocs-restassured")依赖项。

## 4.配置

如前所述，我们将使用 Spring MVC 测试框架向要记录的 REST 服务发出请求。运行测试会生成请求和结果响应的文档片段。

我们可以将库与 JUnit 4 和 JUnit 5 测试一起使用。让我们看看每个所需的配置。

### 4.1. JUnit 4 配置

为 JUnit 4 测试生成文档片段的第一步是声明一个公共JUnitRestDocumentation字段，该字段被注解为 JUnit @Rule。

JUnitRestDocumentation规则配置了输出目录，生成的代码片段应保存到该目录中。比如这个目录可以是Maven的build out目录：

```java
@Rule
public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");
```

接下来，我们设置MockMvc上下文，以便将其配置为生成文档：

```java
@Autowired
private WebApplicationContext context;

private MockMvc mockMvc;

@Before
public void setUp(){
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
      .apply(documentationConfiguration(this.restDocumentation))
      .build();
}
```

MockMvc对象是使用 MockMvc RestDocumentationConfigurer配置的。可以从org.springframework.restdocs.mockmvc.MockMvcRestDocumentation上的静态documentationConfiguration()方法获得此类的实例。

### 4.2. JUnit 5 配置

要使用 JUnit 5 测试，我们必须使用RestDocumentationExtension类扩展测试：

```java
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
public class ApiDocumentationJUnit5IntegrationTest { //... }
```

使用 Maven 时，此类会自动配置/target/generated-snippets输出目录，或使用 Gradle 时/build/generate-snippets。

接下来，我们必须在@BeforeEach方法中设置MockMvc实例：

```java
@BeforeEach
public void setUp(WebApplicationContext webApplicationContext,
  RestDocumentationContextProvider restDocumentation) {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
      .apply(documentationConfiguration(restDocumentation)).build();
}
```

如果我们不使用 JUnit 进行测试，那么我们必须使用ManualRestDocumentation类。

## 5. RESTful 服务

让我们创建一个可以记录的 CRUD RESTful 服务：

```java
@RestController
@RequestMapping("/crud")
public class CRUDController {
	
    @GetMapping
    public List<CrudInput> read(@RequestBody CrudInput crudInput) {
        List<CrudInput> returnList = new ArrayList<CrudInput>();
        returnList.add(crudInput);
        return returnList;
    }
	
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public HttpHeaders save(@RequestBody CrudInput crudInput) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(
          linkTo(CRUDController.class).slash(crudInput.getTitle()).toUri());
        return httpHeaders;
    }
	
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long id) {
        // delete
    }
}
```

然后，我们还添加一个IndexController，它返回一个页面，其中包含指向CRUDController基本端点的链接：

```java
@RestController
@RequestMapping("/")
public class IndexController {

    static class CustomRepresentationModel extends RepresentationModel<CustomRepresentationModel> {
        public CustomRepresentationModel(Link initialLink) {
            super(initialLink);
        }
    }

    @GetMapping
    public CustomRepresentationModel index() {
        return new CustomRepresentationModel(linkTo(CRUDController.class).withRel("crud"));
    }
}
```

## 6. JUnit 测试

回到测试中，我们可以使用MockMvc实例来调用我们的服务并记录请求和响应。

首先，为了确保每个MockMvc调用都自动记录下来而无需任何进一步的配置，我们可以使用alwaysDo()方法：

```java
this.mockMvc = MockMvcBuilders
  //...
  .alwaysDo(document("{method-name}", 
    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
  .build();
```

此设置可确保每次调用MockMvc时，都会在具有测试方法名称的文件夹中创建默认片段。此外，应用prettyPrint()预处理器以更易于阅读的方式显示片段。

让我们继续自定义一些调用。

要记录包含链接的索引页面，我们可以使用静态links()方法：

```java
@Test
public void indexExample() throws Exception {
    this.mockMvc.perform(get("/")).andExpect(status().isOk())
      .andDo(document("index", 
        links(linkWithRel("crud").description("The CRUD resource")), 
        responseFields(subsectionWithPath("_links")
          .description("Links to other resources"))
        responseHeaders(headerWithName("Content-Type")
          .description("The Content-Type of the payload"))));
}
```

在这里，我们使用linkWithRel()方法来记录到/crud 的链接。

要将Content-Type标头添加到响应中，我们使用headerWithName()方法记录它并将其添加到responseHeaders()方法。

我们还使用responseFields()方法记录响应负载。这可用于记录响应的更复杂的小节或使用 subsectionWithPath() 或 fieldWithPath() 方法的单个字段。

与响应负载类似，我们也可以使用requestPayload() 记录请求负载：

```java
@Test
public void crudCreateExample() throws Exception {
    Map<String, Object> crud = new HashMap<>();
    crud.put("title", "Sample Model");
    crud.put("body", "http://www.baeldung.com/");
       
    this.mockMvc.perform(post("/crud").contentType(MediaTypes.HAL_JSON)
      .content(this.objectMapper.writeValueAsString(crud)))
      .andExpect(status().isCreated())
      .andDo(document("create-crud-example", 
        requestFields(fieldWithPath("id").description("The id of the input"),
          fieldWithPath("title").description("The title of the input"),
          fieldWithPath("body").description("The body of the input"),
        ))));
}
```

在此示例中，我们记录了我们的 POST 请求，该请求接收带有标题和正文字段的CrudInput模型并发送 CREATED 状态。每个字段都使用fieldWithPath()方法进行记录。

为了记录请求和路径参数，我们可以使用requestParameters()和pathParameters()方法。两种方法都使用parameterWithName()方法来描述每个参数：

```java
@Test
public void crudDeleteExample() throws Exception {
    this.mockMvc.perform(delete("/crud/{id}", 10)).andExpect(status().isOk())
      .andDo(document("crud-delete-example", 
      pathParameters(
        parameterWithName("id").description("The id of the input to delete")
      )));
}
```

在这里，我们记录了接收id路径参数的删除端点。

Spring REST Docs 项目包含更强大的文档功能，例如可以在[文档](https://docs.spring.io/spring-restdocs/docs/2.0.5.RELEASE/reference/html5/)中找到的字段约束和请求部分。

## 7.输出

构建成功运行后，将生成 REST 文档片段的输出并将其保存到target/generated-snippets文件夹中：

[![屏幕截图 2016-04-04 下午 11.48.52](https://www.baeldung.com/wp-content/uploads/2016/07/Screen-Shot-2016-04-04-at-11.48.52-PM-300x270.png)](https://www.baeldung.com/wp-content/uploads/2016/07/Screen-Shot-2016-04-04-at-11.48.52-PM-300x270.png)

生成的输出将包含有关服务的信息、如何调用 REST 服务(如“curl”调用)、来自 REST 服务的 HTTP 请求和响应以及服务的链接/端点：

<strong>CURL 命令</strong>

```plaintext
----
$ curl 'http://localhost:8080/' -i
----
```

<strong>HTTP – REST 响应</strong>

```plaintext
[source,http,options="nowrap"]
----
HTTP/1.1 200 OK
Content-Type: application/hal+json;charset=UTF-8
Content-Length: 93

{
  "_links" : {
    "crud" : {
      "href" : "http://localhost:8080/crud"
    }
  }
}
----
```

## 8. 使用片段创建文档

要在更大的文档中使用这些片段，你可以使用 Asciidoc includes 来引用它们。在我们的例子中，我们在src/docs中创建了一个名为api-guide.adoc 的文档：

[![屏幕截图 2016-05-01 下午 8.51.48](https://www.baeldung.com/wp-content/uploads/2016/07/Screen-Shot-2016-05-01-at-8.51.48-PM.png)](https://www.baeldung.com/wp-content/uploads/2016/07/Screen-Shot-2016-05-01-at-8.51.48-PM.png)

在该文档中，如果我们希望引用链接片段，我们可以使用占位符{snippets}将其包含在内， Maven 在处理文档时将替换该占位符：

```java
==== Links

include::{snippets}/index-example/links.adoc[]
```

## 9. Asciidocs Maven 插件

要将 API 指南从 Asciidoc 转换为可读格式，我们可以将 Maven 插件添加到构建生命周期。有几个步骤可以启用此功能：

1.  将 Asciidoctor 插件应用到pom.xml
2.  如依赖项部分所述，在testCompile配置中添加对spring-restdocs-mockmvc的依赖项
3.  配置属性以定义生成的代码段的输出位置
4.  配置测试任务以将 snippets 目录添加为输出
5.  配置asciidoctor任务
6.  定义一个名为snippets的属性，在将生成的代码片段包含在文档中时可以使用该属性
7.  使任务依赖于测试任务，以便在创建文档之前运行测试
8.  将代码片段目录配置为输入。所有生成的片段都将在该目录下创建

在pom.xml中添加 snippet 目录作为属性，这样 Asciidoctor 插件就可以使用这个路径在这个文件夹下生成代码片段：

```xml
<properties>
    <snippetsDirectory>${project.build.directory}/generated-snippets</snippetsDirectory>
</properties>
```

pom.xml中用于从构建中生成 Asciidoc 片段的 Maven 插件配置如下所示：

```xml
<plugin> 
    <groupId>org.asciidoctor</groupId>
    <artifactId>asciidoctor-maven-plugin</artifactId>
    <version>1.5.6</version>
    <executions>
        <execution>
            <id>generate-docs</id>
            <phase>package</phase> 
            <goals>
                <goal>process-asciidoc</goal>
            </goals>
            <configuration>
                <backend>html</backend>
                <doctype>book</doctype>
                <attributes>
                    <snippets>${snippetsDirectory}</snippets> 
                </attributes>
                <sourceDirectory>src/docs/asciidocs</sourceDirectory>
                <outputDirectory>target/generated-docs</outputDirectory>
             </configuration>
	 </execution>
    </executions>
</plugin>
```

## 10. API Doc生成流程

当 Maven 构建运行并执行测试时，所有代码片段将在配置的target/generated-snippets目录下的 snippets 文件夹中生成。生成片段后，构建过程会生成 HTML 输出。

[![屏幕截图 2016-05-08 下午 11.32.25](https://www.baeldung.com/wp-content/uploads/2016/07/Screen-Shot-2016-05-08-at-11.32.25-PM.png)](https://www.baeldung.com/wp-content/uploads/2016/07/Screen-Shot-2016-05-08-at-11.32.25-PM.png)

生成的 HTML 文件经过格式化且可读，因此 REST 文档可以随时使用。每次运行 Maven 构建时，还会生成包含最新更新的文档。

[![文档](https://www.baeldung.com/wp-content/uploads/2016/05/docs.png)](https://www.baeldung.com/wp-content/uploads/2016/05/docs.png)

## 11.总结

没有文档总比错误的文档好，但是 Spring REST 文档将有助于为 RESTful 服务生成准确的文档。

作为一个官方的 Spring 项目，它通过使用三个测试库来实现其目标：Spring MVC Test、WebTestClient和 REST Assured。这种生成文档的方法有助于支持测试驱动的方法来开发和记录 RESTful API。