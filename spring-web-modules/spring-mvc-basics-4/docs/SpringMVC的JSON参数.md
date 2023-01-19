## 1. 概述

在这个简短的教程中，我们将仔细研究如何在Spring MVC中使用 JSON 参数。

首先，我们将从 JSON 参数的一些背景知识开始。然后，我们将深入研究如何在 POST 和 GET 请求中发送 JSON 参数。

## 2. Spring MVC中的JSON参数

使用[JSON](https://www.baeldung.com/java-org-json)发送或接收数据是 Web 开发人员的常见做法。JSON 字符串的层次结构提供了一种更紧凑和人类可读的方式来表示 HTTP 请求参数。

默认情况下，Spring MVC 为String等简单数据类型提供开箱即用的数据绑定。为此，它在后台使用了[一系列内置属性编辑器](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/beans/propertyeditors/package-summary.html)。

然而，在实际项目中，我们可能希望绑定更复杂的数据类型。例如，将 JSON 参数映射到模型对象可能会很方便。

## 3. POST发送JSON数据

Spring 提供了一种通过 POST 请求发送 JSON 数据的直接方式。[内置的](https://www.baeldung.com/spring-request-response-body#@requestbody)@RequestBody[注解](https://www.baeldung.com/spring-request-response-body#@requestbody)[可以](https://www.baeldung.com/spring-request-response-body#@requestbody)自动将请求体中封装的JSON数据反序列化为特定的模型对象。

一般来说，我们不必自己解析请求体。我们可以使用[Jackson 库](https://www.baeldung.com/jackson)为我们完成所有繁重的工作。

现在，让我们看看如何在Spring MVC中通过 POST 请求发送 JSON 数据。

首先，我们需要创建一个模型对象来表示传递的 JSON 数据。例如，考虑Product类：

```java
public class Product {

    private int id;
    private String name;
    private double price;

    // default constructor + getters + setters

}
```

其次，让我们定义一个接受 POST 请求的 Spring 处理程序方法：

```java
@PostMapping("/create")
@ResponseBody
public Product createProduct(@RequestBody Product product) {
    // custom logic
    return product;
}
```

如我们所见，使用@RequestBody注解产品参数足以绑定从客户端发送的 JSON 数据。

现在，我们可以使用[cURL](https://www.baeldung.com/curl-rest)测试我们的 POST 请求：

```bash
curl -i 
-H "Accept: application/json" 
-H "Content-Type: application/json" 
-X POST --data 
  '{"id": 1,"name": "Asus Zenbook","price": 800}' "http://localhost:8080/spring-mvc-basics-4/products/create"
```

## 4. GET方式发送JSON参数

Spring MVC 提供[@RequestParam](https://www.baeldung.com/spring-request-param)来从 GET 请求中提取查询参数。但是，与@RequestBody不同的是， @RequestParam注解仅支持简单的数据类型，例如int和String。

因此，要发送 JSON，我们需要将 JSON 参数定义为一个简单的字符串。

这里的大问题是：我们如何将 JSON 参数(它是一个String)转换为Product类的对象？

答案很简单！Jackson 库提供的[ObjectMapper类](https://www.baeldung.com/jackson-object-mapper-tutorial)提供了一种将 JSON 字符串转换为Java对象的灵活方法。

现在，让我们看看如何在Spring MVC中通过 GET 请求发送 JSON 参数。首先，我们需要在控制器中创建另一个处理程序方法来处理 GET 请求：

```java
@GetMapping("/get")
@ResponseBody
public Product getProduct(@RequestParam String product) throws JsonMappingException, JsonProcessingException {
    Product prod = objectMapper.readValue(product, Product.class);
    return prod;
}
```

如上所示，readValue()方法允许将 JSON 参数product直接反序列化为Product类的实例。

请注意，我们将 JSON 查询参数定义为String对象。现在，如果我们想像使用@RequestBody时那样传递一个Product对象怎么办？

[为了回答这个问题，Spring 通过自定义属性编辑器](https://www.baeldung.com/spring-mvc-custom-property-editor)提供了一种简洁灵活的解决方案。

首先，我们需要创建一个自定义属性编辑器来封装将作为String给出的 JSON 参数转换为Product 对象的逻辑：

```java
public class ProductEditor extends PropertyEditorSupport {

    private ObjectMapper objectMapper;

    public ProductEditor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.isEmpty(text)) {
            setValue(null);
        } else {
            Product prod = new Product();
            try {
                prod = objectMapper.readValue(text, Product.class);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException(e);
            }
            setValue(prod);
        }
    }

}
```

接下来，让我们将 JSON 参数绑定到Product类的对象：

```java
@GetMapping("/get2")
@ResponseBody
public Product get2Product(@RequestParam Product product) {
    // custom logic
    return product;
}
```

最后，我们需要添加最后一块缺失的拼图。让我们在 Spring 控制器中注册ProductEditor：

```java
@InitBinder
public void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(Product.class, new ProductEditor(objectMapper));
}
```

请记住，我们需要对 JSON 参数进行 URL 编码以确保安全传输。

所以，而不是：

```bash
GET /spring-mvc-basics-4/products/get2?product={"id": 1,"name": "Asus Zenbook","price": 800}
```

我们需要发送：

```bash
GET /spring-mvc-basics-4/products/get2?product=%7B%22id%22%3A%201%2C%22name%22%3A%20%22Asus%20Zenbook%22%2C%22price%22%3A%20800%7D
```

## 5.总结

总而言之，我们看到了如何在Spring MVC中使用 JSON。一路上，我们展示了如何在 POST 和 GET 请求中发送 JSON 参数。