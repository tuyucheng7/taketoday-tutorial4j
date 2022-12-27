## 1. 概述

在本教程中，我们将学习如何修改Swagger API响应。首先，我们将从OpenAPI规范和Swagger API响应的一些解释开始。然后，我们将使用Spring Boot实现一个简单的示例，以[记录使用OpenApi 3.0的Spring REST API](https://www.baeldung.com/spring-rest-openapi-documentation)。之后，我们将使用Swagger的注解来设置响应主体以提供对象列表。

## 2. 实现

在此实现中，我们将使用Swagger UI设置一个简单的Spring Boot项目，因此，我们将拥有包含应用程序所有端点的Swagger UI。之后，我们将修改响应主体以返回一个列表。

### 2.1 使用Swagger UI设置Spring Boot项目

首先，我们将创建一个ProductService类，在其中保存产品列表。接下来，在ProductController中，我们定义REST API来让用户获取已创建的产品列表。

首先，让我们定义Product类：

```java
public class Product {
    String code;
    String name;

    // standard getters and setters
}
```

然后，我们实现ProductService类：

```java
@Service
public class ProductService {
    List<Product> productsList = new ArrayList<>();

    public List<Product> getProductsList() {
        return productsList;
    }
}
```

最后，我们将有一个控制器类来定义REST API：

```java
@RestController
public class ProductController {
    final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public List<Product> getProductsList(){
        return productService.getProductsList();
    }
}
```

### 2.2 修改Swagger API响应

有几个Swagger注解可用于记录REST API，**使用@ApiResponses，我们可以定义一个@ApiResponse数组来定义我们对REST API的预期响应**。

现在，让我们使用@ApiResponses将响应内容设置为getProductList方法的Product对象列表：

```java
@ApiResponses(value = {
	@ApiResponse(
		content = {@Content(mediaType = "application/json", 
		array = @ArraySchema(schema = @Schema(implementation = Product.class))
		)})
})
@GetMapping("/products")
public List<Product> getProductsList() {
	return productService.getProductsList();
}
```

在此示例中，我们在响应正文中将媒体类型设置为application/json。此外，我们使用content关键字修改了响应主体。此外，使用array关键字，我们将响应设置为Product对象的数组：

<img src="../assets/img_13.png">

## 3. 总结

在本教程中，我们快速浏览了OpenAPI规范和Swagger API响应，Swagger为我们提供了@ApiResponses等各种注解，包括不同的关键字。因此，我们可以很容易地使用它们来修改请求和响应，以满足我们应用程序的要求。在我们的实现中，我们使用@ApiResponses来修改Swagger响应主体中的内容。