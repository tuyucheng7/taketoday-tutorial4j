## 1. 概述

在本教程中，我们将演示如何使用Swagger注解来使我们的文档更具描述性。首先，我们将学习如何为API的不同部分添加描述，例如方法、参数和错误代码。然后我们将了解如何添加请求/响应示例。

## 2. 项目设置

我们将创建一个简单的产品API，它提供创建和获取产品的方法。

要从头开始创建REST API，我们可以按照Spring Docs中的教程[使用Spring Boot创建一个RESTful Web服务](https://spring.io/guides/gs/rest-service/)。

下一步将是为项目设置依赖项和配置，我们可以按照本文中的步骤[使用Spring REST API设置Swagger2](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)。

## 3. 创建API

让我们创建我们的产品API并检查生成的文档。

### 3.1 模型

首先定义我们的产品类：

```java
public class Product implements Serializable {
    private long id;
    private String name;
    private String price;

    // constructor and getter/setters
}
```

### 3.2 控制器

让我们定义两个API方法：

```java
@RestController
@ApiOperation("Products API")
public class ProductController {

    @PostMapping("/products")
    public ResponseEntity<Void> createProduct(@RequestBody Product product) {
        // creation logic
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        // retrieval logic
        return ResponseEntity.ok(new Product(1, "Product 1", "$21.99"));
    }
}
```

当我们运行项目时，库将读取所有公开的路径并创建与它们对应的文档。

因此，我们可以从默认地址http://localhost:8080/swagger-ui/index.html查看文档：

<img src="../assets/img_3.png">

我们可以进一步展开文档中控制器的方法以查看它们各自的文档。接下来，我们将详细了解它们。

## 4. 使我们的文档具有描述性

现在让我们通过向方法的不同部分添加描述来使我们的文档更具描述性。

### 4.1 为方法和参数添加说明

让我们看一下使方法具有描述性的几种方法，我们将为方法、参数和响应代码添加说明，首先我们从getProduct()方法开始：

```java
@ApiOperation(value = "Get a product by id", notes = "Returns a product as per the id")
@ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully retrieved"),
    @ApiResponse(code = 404, message = "Not found - The product was not found")
})
@GetMapping("/products/{id}")
public ResponseEntity<Product> getProduct(@PathVariable("id") @ApiParam(name = "id", value = "Product id", example = "1") Long id) {
    //retrieval logic
    return ResponseEntity.ok(new Product(1, "Product 1", "$21.99"));
}
```

**@ApiOperation定义API方法的属性**，我们使用value属性为操作添加了名称，并使用notes属性向操作添加了说明。

**@ApiResponses用于覆盖响应代码附带的默认消息**，对于我们想要更改的每个响应消息，我们需要添加一个@ApiResponse对象。

例如，假设找不到产品，并且我们的API在这种情况下返回HTTP 404状态码。如果我们不添加自定义消息，则原始消息“Not found”可能很难理解。调用者可能会将其解释为URL错误，但是，添加“The product was not found”的描述会使其更加清晰。

**@ApiParam定义方法参数的属性**，它可以与路径、查询、标头和表单参数一起使用。我们为“id”参数添加了一个名称、一个值(描述)和一个示例。如果我们不添加自定义项，则库将只选择参数的名称和类型，如我们在第一张图中看到的那样。

让我们看看更改后文档的样子：

<img src="../assets/img_4.png">

在这里，我们可以在API路径/products/{id}旁边看到名称“Get a product id”，我们还可以看到它下面的描述。此外，在Parameters部分，我们提供了字段id的描述和示例。最后，在Responses部分，我们可以看到200和404代码的错误描述发生了怎样的变化。

### 4.2 为模型添加描述和示例

我们可以在createProduct()方法中进行类似的改进，由于该方法接收Product对象，因此在Product类本身中提供描述和示例更有意义。

让我们在Product类中进行一些更改以实现此目的：

```java
@ApiModelProperty(notes = "Product ID", example = "1", required = true)
private Long id;
@ApiModelProperty(notes = "Product name", example = "Product 1", required = false)
private String name;
@ApiModelProperty(notes = "Product price", example = "$100.00", required = true)
private String price;
```

**@ApiModelProperty注解定义字段的属性**，我们在每个字段上使用此注解来设置其注解(描述)、示例和必需的属性。

让我们重新启动应用程序并再次查看我们的Product模型的文档：

<img src="../assets/img_5.png">

如果我们将其与原始文档图片进行比较，我们会发现上面给出的截图包含示例、描述和红色星号(*)以标识所需的参数。

**通过向模型添加示例，我们可以在使用模型作为输入或输出的每个方法中自动创建示例响应**。例如，从与getProduct()方法对应的图片中，我们可以看到响应包含一个示例，该示例包含我们在模型中提供的相同值。

在我们的文档中添加示例很重要，因为它使值格式更加精确。如果我们的模型包含日期、时间或价格等字段，则需要精确的值格式。预先定义格式可以使API提供者和API客户端的开发过程更加有效。

## 5. 总结

在本文中，我们探讨了提高API文档可读性的不同方法，我们学习了如何使用注解@ApiParam、@ApiOperation、@ApiResponses、@ApiResponse和@ApiModelProperty来记录方法、参数、错误消息和模型。