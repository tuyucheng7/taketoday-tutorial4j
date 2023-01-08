## 1. 概述

使用 JSON 时的一个典型用例是执行从一种模型到另一种模型的转换。例如，我们可能希望将一个复杂的、密集嵌套的对象图解析为一个更直接的模型，以便在另一个领域中使用。

在本快速教程中，我们将了解如何使用[Jackson](https://github.com/FasterXML/jackson) 映射嵌套值以展平复杂的数据结构。我们将以三种不同的方式反序列化 JSON：

-   使用@JsonProperty
-   使用JsonNode
-   使用自定义JsonDeserializer

## 延伸阅读：

## [与 Jackson 一起使用 Optional](https://www.baeldung.com/jackson-optional)

快速概述我们如何将 Optional 与 Jackson 一起使用。

[阅读更多](https://www.baeldung.com/jackson-optional)→

## [与杰克逊的继承](https://www.baeldung.com/jackson-inheritance)

本教程将演示如何使用 Jackson 处理包含子类型元数据和忽略从超类继承的属性。

[阅读更多](https://www.baeldung.com/jackson-inheritance)→

## [在Spring Boot中使用@JsonComponent](https://www.baeldung.com/spring-boot-jsoncomponent)

了解如何在Spring Boot中使用 @JsonComponent 注解。

[阅读更多](https://www.baeldung.com/spring-boot-jsoncomponent)→

## 2.Maven依赖

让我们首先将以下依赖项添加到pom.xml：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.3</version>
</dependency>
```

我们可以在[Maven Central上找到最新版本的](https://search.maven.org/classic/#search|gav|1|g%3A"com.fasterxml.jackson.core" AND a%3A"jackson-databind")jackson-databind。

## 3.JSON源

将以下 JSON 作为我们示例的源材料。

虽然结构是人为设计的，但请注意，我们包括嵌套两层深的属性：

```javascript
{
    "id": "957c43f2-fa2e-42f9-bf75-6e3d5bb6960a",
    "name": "The Best Product",
    "brand": {
        "id": "9bcd817d-0141-42e6-8f04-e5aaab0980b6",
        "name": "ACME Products",
        "owner": {
            "id": "b21a80b1-0c09-4be3-9ebd-ea3653511c13",
            "name": "Ultimate Corp, Inc."
        }
    }  
}

```

## 4. 简化领域模型

在下面的Product类描述的扁平域模型中，我们将提取brandName，它嵌套在我们的源 JSON 中的一层深处。

此外，我们将提取ownerName，它嵌套了两层，位于嵌套的品牌对象中：

```java
public class Product {

    private String id;
    private String name;
    private String brandName;
    private String ownerName;

    // standard getters and setters
}

```

## 5. 带注解的映射

要映射嵌套的brandName属性，我们首先需要将嵌套的brand对象解包到Map并提取name属性。要映射ownerName ，我们将嵌套的owner对象解包到Map并提取其name属性。

我们可以通过使用@JsonProperty和我们添加到Product类的一些自定义逻辑的组合来指示 Jackson解压嵌套属性：

```java
public class Product {
    // ...

    @SuppressWarnings("unchecked")
    @JsonProperty("brand")
    private void unpackNested(Map<String,Object> brand) {
        this.brandName = (String)brand.get("name");
        Map<String,String> owner = (Map<String,String>)brand.get("owner");
        this.ownerName = owner.get("name");
    }
}

```

我们的客户端代码现在可以使用ObjectMapper来转换我们的源 JSON，它在测试类中作为字符串常量SOURCE_JSON存在：

```java
@Test
public void whenUsingAnnotations_thenOk() throws IOException {
    Product product = new ObjectMapper()
      .readerFor(Product.class)
      .readValue(SOURCE_JSON);

    assertEquals(product.getName(), "The Best Product");
    assertEquals(product.getBrandName(), "ACME Products");
    assertEquals(product.getOwnerName(), "Ultimate Corp, Inc.");
}
```

## 6.用JsonNode映射

使用JsonNode映射嵌套数据结构需要做更多的工作。

这里我们使用ObjectMapper的readTree来解析出所需的字段：

```java
@Test
public void whenUsingJsonNode_thenOk() throws IOException {
    JsonNode productNode = new ObjectMapper().readTree(SOURCE_JSON);

    Product product = new Product();
    product.setId(productNode.get("id").textValue());
    product.setName(productNode.get("name").textValue());
    product.setBrandName(productNode.get("brand")
      .get("name").textValue());
    product.setOwnerName(productNode.get("brand")
      .get("owner").get("name").textValue());

    assertEquals(product.getName(), "The Best Product");
    assertEquals(product.getBrandName(), "ACME Products");
    assertEquals(product.getOwnerName(), "Ultimate Corp, Inc.");
}
```

## 7. 使用自定义JsonDeserializer 进行映射

从实现的角度来看，使用自定义JsonDeserializer映射嵌套数据结构与JsonNode方法相同。

我们首先创建JsonDeserializer：

```java
public class ProductDeserializer extends StdDeserializer<Product> {

    public ProductDeserializer() {
        this(null);
    }

    public ProductDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Product deserialize(JsonParser jp, DeserializationContext ctxt) 
      throws IOException, JsonProcessingException {
 
        JsonNode productNode = jp.getCodec().readTree(jp);
        Product product = new Product();
        product.setId(productNode.get("id").textValue());
        product.setName(productNode.get("name").textValue());
        product.setBrandName(productNode.get("brand")
          .get("name").textValue());
        product.setOwnerName(productNode.get("brand").get("owner")
          .get("name").textValue());		
        return product;
    }
}


```

### 7.1. 解串器的手动注册

要手动注册我们的自定义反序列化器，我们的客户端代码必须将JsonDeserializer添加到Module ，使用ObjectMapper注册Module 并调用readValue：

```java
@Test
public void whenUsingDeserializerManuallyRegistered_thenOk()
 throws IOException {
 
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(Product.class, new ProductDeserializer());
    mapper.registerModule(module);

    Product product = mapper.readValue(SOURCE_JSON, Product.class);
 
    assertEquals(product.getName(), "The Best Product");
    assertEquals(product.getBrandName(), "ACME Products");
    assertEquals(product.getOwnerName(), "Ultimate Corp, Inc.");
}

```

### 7.2. Deserializer的自动注册

作为手动注册JsonDeserializer的替代方法，我们可以直接在类上注册反序列化器：

```java
@JsonDeserialize(using = ProductDeserializer.class)
public class Product {
    // ...
}
```

使用这种方法，无需手动注册。

我们来看看我们使用自动注册的客户端代码：

```java
@Test
public void whenUsingDeserializerAutoRegistered_thenOk()
  throws IOException {
 
    ObjectMapper mapper = new ObjectMapper();
    Product product = mapper.readValue(SOURCE_JSON, Product.class);

    assertEquals(product.getName(), "The Best Product");
    assertEquals(product.getBrandName(), "ACME Products");
    assertEquals(product.getOwnerName(), "Ultimate Corp, Inc.");
}
```

## 八. 总结

在本文中，我们演示了使用Jackson 解析包含嵌套值的 JSON 的几种方法。查看我们的主要[Jackson 教程](https://www.baeldung.com/jackson)页面以获取更多示例。