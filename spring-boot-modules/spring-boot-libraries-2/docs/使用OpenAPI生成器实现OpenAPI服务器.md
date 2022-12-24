## 1. 概述

顾名思义，[OpenAPI生成器](https://github.com/OpenAPITools/openapi-generator)根据[OpenAPI]()规范生成代码，它可以为客户端库、服务器存根、文档和配置创建代码。

它支持各种语言和框架。值得注意的是，它支持C++、C#、Java、PHP、Python、Ruby、Scala-几乎[所有广泛使用](https://openapi-generator.tech/docs/generators/)的语言。

在本教程中，我们将学习**如何通过其Maven插件使用OpenAPI Generator实现基于Spring的服务器存根**。

使用生成器的其他方法是通过其[CLI](https://openapi-generator.tech/docs/installation/)或[在线工具](http://api.openapi-generator.tech/index.html)。

## 2. YAML文件

首先，我们需要一个指定API的YAML文件，我们会将其作为生成器的输入来生成服务器存根。

以下是我们的petstore.yml的片段：

```yaml
openapi: "3.0.0"
paths:
    /pets:
        get:
            summary: List all pets
            operationId: listPets
            tags:
                - pets
            parameters:
                -   name: limit
                    in: query
                    ...
            responses:
                ...
        post:
            summary: Create a pet
            operationId: createPets
            ...
    /pets/{petId}:
        get:
            summary: Info for a specific pet
            operationId: showPetById
            ...
components:
    schemas:
        Pet:
            type: object
            required:
                - id
                - name
            properties:
                id:
                    type: integer
                    format: int64
                name:
                    type: string
                tag:
                    type: string
        Error:
            type: object
            required:
                - code
                - message
            properties:
                code:
                    type: integer
                    format: int32
                message:
                    type: string
```

## 3. Maven依赖

### 3.1 OpenAPI生成器插件

接下来，让我们为生成器插件添加[Maven依赖](https://search.maven.org/search?q=a:openapi-generator-maven-plugin AND g: org.openapitools)：

```xml
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>5.1.0</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>
                    ${project.basedir}/src/main/resources/petstore.yml
                </inputSpec>
                <generatorName>spring</generatorName>
                <apiPackage>cn.tuyucheng.taketoday.openapi.api</apiPackage>
                <modelPackage>cn.tuyucheng.taketoday.openapi.model</modelPackage>
                <supportingFilesToGenerate>
                    ApiUtil.java
                </supportingFilesToGenerate>
                <configOptions>
                    <delegatePattern>true</delegatePattern>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

如我们所见，我们将YAML文件作为inputSpec传递，之后，由于我们需要一个基于Spring的服务器，我们将generatorName用作spring。

然后apiPackage指定将生成API的包名称。

接下来，我们有生成器放置数据模型的模型包。

将delegatePattern设置为true后，我们要求创建一个可以作为自定义[@Service]()类实现的接口。

重要的是，**无论我们使用CLI、Maven/Gradle插件还是在线生成选项**，[OpenAPI生成器的选项](https://openapi-generator.tech/docs/generators/spring/)**都是相同的**。

### 3.2 Maven依赖项

由于我们将生成一个Spring服务器，**我们还需要它的依赖项**([Spring Boot Starter Web](https://search.maven.org/search?q=a:spring-boot-starter-web AND g:org.springframework.boot)和[Spring Data JPA](https://search.maven.org/search?q=a:spring-data-jpa AND g:org.springframework.data))，**以便我们生成的代码按预期编译和运行**：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>2.4.4</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.data</groupId>
        <artifactId>spring-data-jpa</artifactId>
        <version>2.4.6</version>
    </dependency>
</dependencies>
```

除了上述Spring依赖项之外，我们还需要[jackson-databind](https://search.maven.org/search?q=a:jackson-databind-nullable)和[swagger2](https://search.maven.org/search?q=a:springfox-swagger2)依赖项，以便我们生成的代码能够成功编译：

```xml
<dependency>
    <groupId>org.openapitools</groupId>
    <artifactId>jackson-databind-nullable</artifactId>
    <version>0.2.1</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
</dependency>
```

## 4. 代码生成

要生成服务器存根，我们只需要运行以下命令：

```bash
mvn clean install
```

结果，这就是我们得到的：

<img src="../assets/img_2.png">

现在让我们看一下代码，从apiPackage的内容开始。

首先，**我们得到一个名为PetsApi的API接口**，其中包含YAML规范中定义的所有请求映射。

这是代码片段：

```java
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-12-23T23:10:12.223529600+08:00[Asia/Shanghai]")
@Validated
@Api(value = "pets", description = "the pets API")
public interface PetsApi {

    /**
     * GET /pets : List all pets
     *
     * @param limit How many items to return at one time (max 100) (optional)
     * @return A paged array of pets (status code 200)
     *         or unexpected error (status code 200)
     */
    @ApiOperation(value = "List all pets", nickname = "listPets", notes = "", response = Pet.class, responseContainer = "List", tags={ "pets", })
    @ApiResponses(value = {
          @ApiResponse(code = 200, message = "A paged array of pets", response = Pet.class, responseContainer = "List"),
          @ApiResponse(code = 200, message = "unexpected error", response = Error.class) })
    @GetMapping(
          value = "/pets",
          produces = { "application/json" }
    )
    default ResponseEntity<List<Pet>> listPets(@ApiParam(value = "How many items to return at one time (max 100)") @Valid @RequestParam(value = "limit", required = false) Integer limit) {
        return getDelegate().listPets(limit);
    }

    // other generated methods
}
```

其次，由于我们使用的是委托模式，OpenAPI还为我们生成了一个名为PetsApiDelegate的委托接口。

特别是，**在此接口中声明的方法默认返回501 Not Implemented的HTTP状态**：

```java
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-12-23T23:10:12.223529600+08:00[Asia/Shanghai]")
public interface PetsApiDelegate {

    /**
     * GET /pets : List all pets
     *
     * @param limit How many items to return at one time (max 100) (optional)
     * @return A paged array of pets (status code 200)
     *         or unexpected error (status code 200)
     * @see PetsApi#listPets
     */
    default ResponseEntity<List<Pet>> listPets(Integer limit) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"name\" : \"name\", \"id\" : 0, \"tag\" : \"tag\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    // other generated method declarations
}
```

在那之后，**我们看到有一个PetsApiController类，它简单地注入了委托者**：

```java
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-12-23T23:10:12.223529600+08:00[Asia/Shanghai]")
@Controller
@RequestMapping("${openapi.swaggerPetstore.base-path:}")
public class PetsApiController implements PetsApi {

    private final PetsApiDelegate delegate;

    public PetsApiController(@org.springframework.beans.factory.annotation.Autowired(required = false) PetsApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new PetsApiDelegate() {});
    }

    @Override
    public PetsApiDelegate getDelegate() {
        return delegate;
    }
}
```

在modelPackage中，**根据我们的YAML输入中定义的模式，生成了两个名为Error和Pet的数据模型POJO**。

让我们看看其中之一Pet：

```java
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen",
      date = "2022-12-23T23:10:12.223529600+08:00[Asia/Shanghai]")
public class Pet {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("tag")
    private String tag;

    // constructor

    @ApiModelProperty(required = true, value = "")
    @NotNull
    public Long getId() {
        return id;
    }

    // other getters and setters

    // equals, hashcode, and toString methods
}
```

## 5. 测试服务器

现在，要使服务器存根具有服务器功能，所需要做的就是添加委托者接口的实现。

为了简单起见，我们不会在这里这样做，而是只测试存根。

此外，在此之前，我们需要一个Spring应用程序：

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 5.1 使用curl进行测试

启动应用程序后，我们只需运行以下命令：

```bash
curl -I http://localhost:8080/pets/
```

这是预期的结果：

```http request
HTTP/1.1 501 
Content-Length: 0
Date: Fri, 26 Mar 2022 17:29:25 GMT
Connection: close
```

### 5.2 集成测试

或者，我们可以为此编写一个简单的[集成测试]()：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OpenApiApplication.class)
@AutoConfigureMockMvc
public class OpenApiPetsIntegrationTest {

    private static final String PETS_PATH = "/pets/";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenReadAll_thenStatusIsNotImplemented() throws Exception {
        this.mockMvc.perform(get(PETS_PATH))
              .andExpect(status().isNotImplemented());
    }

    @Test
    public void whenReadOne_thenStatusIsNotImplemented() throws Exception {
        this.mockMvc.perform(get(PETS_PATH + 1))
              .andExpect(status().isNotImplemented());
    }
}
```

## 6. 总结

在本文中，**我们了解了如何使用OpenAPI生成器的Maven插件从YAML规范生成基于Spring的服务器存根**。

下一步，我们还可以使用它来[生成客户端]()。