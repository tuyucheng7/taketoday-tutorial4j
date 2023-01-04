## 1. 概述

在这个简短的教程中，我们将仔细研究Spring的[RestTemplate](https://www.baeldung.com/rest-template)异常IllegalArgumentException：Not enough variables available to expand。

首先，我们将详细讨论此异常背后的主要原因。然后，我们将展示如何生成它，最后是如何解决它。

## 2. 原因

简而言之，异常通常发生在我们尝试在GET请求中发送JSON数据时。

简单来说，RestTemplate提供了[getForObject](https://www.baeldung.com/rest-template#2-retrieving-pojo-instead-of-json)方法，通过对指定的URL进行GET请求来获取表示。

异常的主要原因是RestTemplate将花括号中封装的JSON数据视为URI变量的占位符。

因为我们没有为预期的URI变量提供任何值，所以getForObject方法会抛出异常。

例如，尝试发送{"name": "HP EliteBook"}作为值：

```java
String url = "http://products.api.com/get?key=a123456789z&criterion={\"name\":\"HP EliteBook\"}";
Product product = restTemplate.getForObject(url, Product.class);
```

只会导致RestTemplate抛出异常：

```bash
java.lang.IllegalArgumentException: Not enough variable values available to expand 'name'
```

## 3. 示例应用

现在，让我们看看如何使用RestTemplate生成此IllegalArgumentException的示例。

为了简单起见，我们将创建一个基本的[REST API](https://www.baeldung.com/rest-with-spring-series)，用于使用单个GET端点进行产品管理。

首先，让我们创建我们的模型类Product：

```java
public class Product {

    private int id;
    private String name;
    private double price;

    // default constructor + all args constructor + getters + setters 
}
```

接下来，我们将定义一个Spring控制器来封装我们的REST API的逻辑：

```java
@RestController
@RequestMapping("/api")
public class ProductApi {

    private List<Product> productList = new ArrayList<>(Arrays.asList(
          new Product(1, "Acer Aspire 5", 437),
          new Product(2, "ASUS VivoBook", 650),
          new Product(3, "Lenovo Legion", 990)
    ));

    @GetMapping("/get")
    public Product get(@RequestParam String criterion) throws JsonMappingException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Criterion crt = objectMapper.readValue(criterion, Criterion.class);
        if (crt.getProp().equals("name")) {
            return findByName(crt.getValue());
        }

        // Search by other properties (id,price)

        return null;
    }

    private Product findByName(String name) {
        for (Product product : this.productList) {
            if (product.getName().equals(name)) {
                return product;
            }
        }
        return null;
    }

    // Other methods
}
```

## 4. 示例应用说明

处理程序方法get()的基本思想是根据特定标准检索产品对象。

该标准可以表示为具有两个键的JSON字符串：prop和value。

prop key指的是产品属性，因此它可以是id、名称或价格。

如上所示，标准作为字符串参数传递给处理程序方法。我们使用[ObjectMapper](https://www.baeldung.com/jackson-object-mapper-tutorial)类将我们的[JSON字符串](https://www.baeldung.com/spring-mvc-send-json-parameters#send-json-parameter-in-get)转换为Criterion的对象。

这是我们的Criterion类的样子：

```java
public class Criterion {

    private String prop;
    private String value;

    // default constructor + getters + setters
}
```

最后，让我们尝试向映射到处理程序方法get()的URL发送GET请求。

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { RestTemplate.class, RestTemplateExceptionApplication.class })
public class RestTemplateExceptionLiveTest {

    @Autowired
    RestTemplate restTemplate;

    @Test(expected = IllegalArgumentException.class)
    public void givenGetUrl_whenJsonIsPassed_thenThrowException() {
        String url = "http://localhost:8080/spring-rest/api/get?criterion={\"prop\":\"name\",\"value\":\"ASUS VivoBook\"}";
        Product product = restTemplate.getForObject(url, Product.class);
    }
}
```

实际上，单元测试会抛出IllegalArgumentException，因为我们试图将{"prop": "name", "value": "ASUS VivoBook"}作为URL的一部分传递。

## 5. 解决方案

根据经验，我们应该始终使用POST请求来发送JSON数据。

然而，虽然不推荐，但使用GET的可能解决方案是定义一个包含我们的标准的String对象，并在URL中提供一个真实的URI变量。

```java
@Test
public void givenGetUrl_whenJsonIsPassed_thenGetProduct() {
    String criterion = "{\"prop\":\"name\",\"value\":\"ASUS VivoBook\"}";
    String url = "http://localhost:8080/spring-rest/api/get?criterion={criterion}";
    Product product = restTemplate.getForObject(url, Product.class, criterion);

    assertEquals(product.getPrice(), 650, 0);
}
```

让我们看看另一个使用[UriComponentsBuilder](https://www.baeldung.com/spring-uricomponentsbuilder)类的解决方案：

```java
@Test
public void givenGetUrl_whenJsonIsPassed_thenReturnProduct() {
    String criterion = "{\"prop\":\"name\",\"value\":\"Acer Aspire 5\"}";
    String url = "http://localhost:8080/spring-rest/api/get";

    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("criterion", criterion);
    Product product = restTemplate.getForObject(builder.build().toUri(), Product.class);

    assertEquals(product.getId(), 1, 0);
}
```

如我们所见，在将URI传递给getForObject方法之前，我们使用UriComponentsBuilder类通过查询参数标准构造我们的URI。

## 6. 总结

在这篇快速文章中，我们讨论了导致RestTemplate抛出IllegalArgumentException的原因：“Not enough variables available to expand”。

在此过程中，我们通过一个实际示例展示了如何产生异常并解决它。