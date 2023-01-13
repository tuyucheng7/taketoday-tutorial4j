## 1. 简介

Swagger规范现在更广为人知的名称是[OpenAPI规范](https://www.openapis.org/)，尽管[Smartbear](https://smartbear.com/)仍然支持某些工具，例如用于Java和Kotlin代码的[Swagger注解](https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations)，**但最好尽可能使用所有工具的OpenAPI版本**。

在这里，我们将探讨**OpenAPI规范在我们项目中的优势**。主要有两种方法：要么我们有代码并想为其生成规范，要么我们有规范但现在想生成一些代码。

在任何一种情况下，OpenApi/Swagger工具都会提供一个自动API文档页面。

## 2. 将OpenAPI引入Spring Boot项目

让我们使用Gradle来设置我们的项目，我们将在[Spring Initializr](https://start.spring.io/)上使用Gradle、Kotlin和JDK 17创建一个项目，Spring Web框架将是我们唯一的依赖项。然后，我们可以下载一个准备运行的项目。

**要开始在我们的项目中显示文档，添加三个依赖项就足够了**：

```kotlin
implementation("org.springdoc:springdoc-openapi-data-rest:1.6.0")
implementation("org.springdoc:springdoc-openapi-ui:1.6.0")
implementation("org.springdoc:springdoc-openapi-kotlin:1.6.0")
```

就这样，现在，我们可以运行项目并访问http://localhost:8080/swagger-ui.html。这将出现一个空的文档页面，接下来让我们填充它。

## 3. 从现有代码生成规范

通常，我们来到一个已经有一些历史的项目，此类项目公开了他们的API，但让新客户使用该API通常充满困难。像往常一样，一切都在变化，文档在发布之日就变得过时了。

**使用我们的代码作为我们文档的真实来源可确保文档始终保持相关性**，如果我们向组织外部的用户提供API，这就特别有意义。因此，我们可以在不咨询任何人的情况下制作新版本，然后，当新的API集上线时，我们会通知我们的客户。

### 3.1 使用注解生成规范

**为了实现生成文档的目标，我们必须使用注解标注我们的代码**，包括作为参数和返回值的REST控制器和模型。对于模型，我们应该看看@Schema注解，它是数据传输类的主要注解：

```kotlin
@Schema(description = "Model for a dealer's view of a car.")
data class DealerCar(
    // Other fields
    @field:Schema(
        description = "A year when this car was made",
        example = "2021",
        type = "int",
        minimum = "1900",
        maximum = "2500"
    )
    val year: Int,
    // More fields
)
```

这个注解有很多[属性](https://docs.swagger.io/swagger-core/v2.1.12/apidocs/io/swagger/v3/oas/annotations/media/Schema.html)：指定取值范围、提供示例值、指定字符串字段的格式等。我们需要在类字段上使用特定于Kotlin的字段目标，以便swagger-core库能够正确处理它们。

**我们的REST控制器的方法有@Operation注解来解释它们的目的和用法**，我们还可以指定返回码并解释我们返回它们的情况：

```kotlin
@Operation(summary = "Sets a price for a chosen car", description = "Returns 202 if successful")
@ApiResponses(
    value = [
        ApiResponse(responseCode = "202", description = "Successful Operation"),
        ApiResponse(responseCode = "404", description = "Such a car does not exist"),
    ]
)
@PostMapping("/price/{carId}", consumes = ["application/json"])
fun setPrice(
    @PathVariable carId: Int,
    @RequestBody @OASRequestBody(
        description = "New price for the car",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(type = "number", format = "float", minimum = "0.0", example = "23000.34"),
        )]
    ) price: BigDecimal
): ResponseEntity<Unit> {
    carService.setPrice(carId, price)
    return ResponseEntity.accepted().build()
}
```

在这里，**我们可以指定我们的方法可能返回的所有响应以及它作为参数的请求主体**。不用说，这种方法可能很快就会变得难以管理，注解占用的空间比实际代码多得多，因此从规范开始通常是值得的。

## 4. 从现有规范生成服务器或客户端

**在客户端上遵守规范和编写HTTP Web端点通常是重复和乏味的**，通过生成该代码，我们让生活更轻松。

首先制定规范的另一个用途是，**前端和后端之间更容易就规范达成一致**。

而且，正如我们已经看到的，我们可以通过将规范和代码分开来获得更具可读性的源代码。

### 4.1 使用规范文件创建Spring服务器

首先，**我们需要创建一个规范文件**，[Swagger在线编辑器](https://editor.swagger.io/)站点就是一个这样的例子。但是，为了进行简单演示，我们将使用更直接的设置，可以在我们的[GitHub]()上找到。

然后，我们将使用[OpenAPI生成器](https://openapi-generator.tech/)工具，它可作为独立的CLI工具使用，因此应该运行一次以启动项目。然而，**当它在每次规范更改时运行时，它为我们提供了一个关于我们对规范的遵从程度的持续反馈循环**。因此，它带来了最大的优势。

所以，我们将把它添加到我们的项目build.gradle.kts中：

```kotlin
// The values oasPackage, oasSpecLocation, oasGenOutputDir are defined earlier
tasks.register("generateServer", org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
    input = project.file(oasSpecLocation).path
    outputDir.set(oasGenOutputDir.get().toString())
    modelPackage.set("$oasPackage.model")
    apiPackage.set("$oasPackage.api")
    packageName.set(oasPackage)
    generatorName.set("kotlin-spring")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "interfaceOnly" to "true",
            "useTags" to "true"
        )
    )
}
```

我们必须**记住将此任务的依赖项添加到KotlinCompile任务中**，并**将generated source添加到main source set中**。这个插件可以生成整个REST服务器，然后我们必须提供一个具有实际业务逻辑的ApiDelegate，但就我们的目的而言，仅接口就足够了：

```kotlin
class CarCrudController(private val carService: CarService) : CarsApi {
    override fun createCar(carBody: CarBody): ResponseEntity<Car> = TODO("Implementation")
    override fun getCar(id: Int): ResponseEntity<Car> = TODO("Implementation")
    override fun getCars(): ResponseEntity<List<Car>> = TODO("Implementation")
}
```

### 4.2 从现有规范生成客户端

与服务器生成类似，我们**也可以创建一个客户端**，我们只需要更改Gradle脚本中的一行并使用generatorName.set("kotlin")。之后，我们应该为客户端添加必要的依赖项：

```kotlin
implementation("com.squareup.moshi:moshi-kotlin:1.13.0")
implementation("com.squareup.moshi:moshi-adapters:1.13.0")
implementation("com.squareup.okhttp3:okhttp:4.9.3")
```

然后，我们可以调用客户端：

```kotlin
val car = CarsApi("http://localhost:8080")
    .createCar(ClientCarBody(model = "CM-X", make = "Gokyoro", year = 2021))
```

## 5. 总结

在本教程中，我们讨论了如何使用Swagger的继承者OpenAPI创建API规范，前提是我们拥有实现该API的源代码，根据规范创建代码，并显示根据同一规范生成的用户友好文档。