## 1. 概述

JSON Schema是一种声明性语言，用于验证JSON 对象的格式和结构。它允许我们指定特殊原语的数量来准确描述有效的JSON 对象的外观。

JSON Schema规范分为三个部分：

-   [JSON Schema Core](https://json-schema.org/latest/json-schema-core.html)：JSON Schema Core 规范是定义模式术语的地方。
-   [JSON 模式验证](https://json-schema.org/latest/json-schema-validation.html)：JSON 模式验证规范是定义定义验证约束的有效方法的文档。本文档还定义了一组可用于指定 JSON API 验证的关键字。在接下来的示例中，我们将使用其中的一些关键字。
-   [JSON Hyper-Schema](https://json-schema.org/draft/2019-09/json-schema-hypermedia.html)：这是 JSON Schema 规范的另一个扩展，其中定义了超链接和超媒体相关关键字。

## 2. 定义 JSON 模式

现在我们已经定义了JSON Schema的用途，让我们创建一个JSON 对象和描述它的相应JSON Schema 。

以下是表示产品目录的简单JSON 对象：

```java
{
    "id": 1,
    "name": "Lampshade",
    "price": 0
}
```

我们可以定义它的JSON Schema如下：

```java
{
    "$schema": "http://json-schema.org/draft-04/schema#",
    "title": "Product",
    "description": "A product from the catalog",
    "type": "object",
    "properties": {
        "id": {
            "description": "The unique identifier for a product",
            "type": "integer"
        },
        "name": {
            "description": "Name of the product",
            "type": "string"
        },
        "price": {
            "type": "number",
            "minimum": 0,
            "exclusiveMinimum": true
        }
    },
    "required": ["id", "name", "price"]
}
```

正如我们所见，JSON Schema是一个JSON 文档，该文档必须是一个对象。JSON Schema定义的对象成员(或属性)称为关键字。

让我们解释一下我们在示例中使用的关键字：

-   $schema关键字声明此模式是根据 v4 规范草案编写的。
-   标题和描述关键字只是描述性的，因为它们不会对正在验证的数据添加约束。模式的意图是用这两个关键字来说明的：描述一个产品。
-   type关键字定义了我们JSON数据的第一个约束：它必须是一个JSON对象。

此外，JSON 模式可能包含不是模式关键字的属性。在我们的例子中， id、name、price 将是JSON Object的成员(或属性) 。

对于每个属性，我们可以定义类型。我们将id和name定义为字符串 ，将价格定义为数字。在JSON Schema中，数字可以有最小值。默认情况下，这个最小值是包容性的，所以我们需要指定exclusiveMinimum。

最后，架构告诉我们需要id、name和price。

## 3. 使用 JSON Schema 进行验证

有了我们的JSON 模式，我们就可以验证我们的JSON 对象。

有许多[库](https://json-schema.org/implementations.html)可以完成这项任务。对于我们的示例，我们选择了Java[json-schema](https://github.com/networknt/json-schema-validator)库。

首先，我们需要将以下依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.networknt</groupId>
    <artifactId>json-schema-validator</artifactId>
    <version>1.0.72</version>
</dependency>

```

最后，我们可以编写几个简单的测试用例来验证我们的JSON 对象：

```java
@Test
public void givenInvalidInput_whenValidating_thenInvalid() throws IOException {
    JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
    JsonSchema jsonSchema = factory.getSchema(
     JSONSchemaUnitTest.class.getResourceAsStream("/schema.json"));
    JsonNode jsonNode = mapper.readTree(
     JSONSchemaUnitTest.class.getResourceAsStream("/product_invalid.json"));
    Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);
    assertThat(errors).isNotEmpty().asString().contains("price: must have a minimum value of 0");
}
```

在这种情况下，将收到验证错误。

第二个测试如下所示：

```java
@Test 
public void givenValidInput_whenValidating_thenValid() throws ValidationException { 
    JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4); 
    JsonSchema jsonSchema = factory.getSchema( 
     JSONSchemaUnitTest.class.getResourceAsStream("/schema.json")); 
    JsonNode jsonNode = mapper.readTree( 
     JSONSchemaUnitTest.class.getResourceAsStream("/product_valid.json")); 
    Set<ValidationMessage> errors = jsonSchema.validate(jsonNode); 
    assertThat(errors).isEmpty(); 
}
```

由于我们使用有效的JSON 对象，因此不会抛出验证错误。

## 4. 总结

在本文中，我们定义了什么是 JSON Schema，以及哪些是帮助我们定义 schema 的相关关键字。

将JSON Schema与其相应的JSON 对象表示相结合，我们可以执行一些验证任务。