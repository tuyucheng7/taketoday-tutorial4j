## 1. 概述

多年来，[GraphQL](GraphQL简介.md)已被广泛接受为Web服务的通信模式之一，虽然它在使用上非常丰富和灵活，但在某些场景下可能会带来挑战。其中之一是从查询中返回一个Map，这是一个挑战，因为Map不是GraphQL中的类型。

在本教程中，我们将学习从GraphQL查询返回Map的技术。

## 2. 示例

让我们以具有无限数量的自定义属性的产品数据库为例。

作为数据库实体的Product可能具有一些固定字段，如name、price、category等。但是，它也可能具有因类别而异的属性，这些属性应该以保留其标识键的方式返回给客户端。

为此，我们可以使用Map作为这些属性的类型。

## 3. 返回Map

为了返回一个Map，我们有三个选项：

-   作为JSON字符串返回
-   使用GraphQL自定义[标量类型](https://graphql.org/learn/schema/#scalar-types)
-   以键值对列表的形式返回

对于前两个选项，我们将使用以下GraphQL查询：

```graphql
query {
    product(id:1){
        id
        name
        description
        attributes
    }
}
```

参数属性将以Map格式表示。

### 3.1 JSON字符串

这是最简单的选择，我们将在Product解析器中将Map序列化为JSON字符串格式：

```java
String attributes = objectMapper.writeValueAsString(product.getAttributes());
```

GraphQL模式本身如下：

```graphql
type Product {
    id: ID
    name: String!
    description: String
    attributes:String
}
```

下面是此实现后的查询结果：

```json
{
    "data": {
        "product": {
            "id": "1",
            "name": "Product 1",
            "description": "Product 1 description",
            "attributes": "{\"size\": {
                                     \"name\": \"Large\",
                                     \"description\": \"This is custom attribute description\",
                                     \"unit\": \"This is custom attribute unit\"
                                    },
                   \"attribute_1\": {
                                     \"name\": \"Attribute1 name\",
                                     \"description\": \"This is custom attribute description\",
                                     \"unit\": \"This is custom attribute unit\"
                                    }
                        }"
        }
    }
}
```

这个方法有两个问题，**第一个问题是JSON字符串需要在客户端处理成可用的格式，第二个问题是我们不能对属性进行子查询**。

为了克服第一个问题，GraphQL自定义标量类型的第二个选项可以提供帮助。

### 3.2 GraphQL自定义标量类型

对于实现，我们将在Java中使用GraphQL的[扩展标量](https://github.com/graphql-java/graphql-java-extended-scalars)库。

首先，我们在pom.xml中包含[graphql-java-extended-scalars](https://mvnrepository.com/artifact/com.graphql-java/graphql-java-extended-scalars/18.0)依赖项：

```xml
<dependency>
    <groupId>com.graphql-java</groupId>
    <artifactId>graphql-java-extended-scalars</artifactId>
    <version>2022-04-06T00-10-27-a70541e</version>
</dependency>
```

然后，我们在GraphQL配置组件中注册我们选择的标量类型。在这种情况下，标量类型是JSON：

```java
@Bean
public GraphQLScalarType json() {
    return ExtendedScalars.Json;
}
```

最后，我们将相应地更新我们的GraphQL模式：

```graphql
type Product {
    id: ID
    name: String!
    description: String
    attributes: JSON
}
scalar JSON
```

这是执行后的结果：

```json
{
    "data": {
        "product": {
            "id": "1",
            "name": "Product 1",
            "description": "Product 1 description",
            "attributes": {
                "size": {
                    "name": "Large",
                    "description": "This is custom attribute description",
                    "unit": "This is a custom attribute unit"
                },
                "attribute_1": {
                    "name": "Attribute1 name",
                    "description": "This is custom attribute description",
                    "unit": "This is a custom attribute unit"
                }
            }
        }
    }
}
```

使用这种方法，我们不需要在客户端处理属性映射。但是，标量类型有其自身的局限性。

**在GraphQL中，标量类型是查询的叶子，这表明它们不能被进一步查询**。

### 3.3 键值对列表

如果需要进一步查询Map，那么这是最可行的选择，我们会将Map对象转换为键值对对象列表。

下面是表示键值对的类：

```java
public class AttributeKeyValueModel {
    private String key;
    private Attribute value;

    public AttributeKeyValueModel(String key, Attribute value) {
        this.key = key;
        this.value = value;
    }
}
```

在Product解析器中，我们将添加以下实现：

```java
List<AttributeKeyValueModel> attributeModelList = new LinkedList<>();
product.getAttributes().forEach((key, val) -> attributeModelList.add(new AttributeKeyValueModel(key, val)));
```

最后，我们更新GraphQL模式：

```graphql
type Product {
    id: ID
    name: String!
    description: String
    attributes:[AttributeKeyValuePair]
}
type AttributeKeyValuePair {
    key:String
    value:Attribute
}
type Attribute {
    name:String
    description:String
    unit:String
}
```

由于我们已经更新了模式，因此我们也会更新查询：

```graphql
query {
    product(id:1){
        id
        name
        description
        attributes {
            key
            value {
                name
                description
                unit
            }
        }
    }
}
```

现在，让我们看看结果：

```json
{
    "data": {
        "product": {
            "id": "1",
            "name": "Product 1",
            "description": "Product 1 description",
            "attributes": [
                {
                    "key": "size",
                    "value": {
                        "name": "Large",
                        "description": "This is custom attribute description",
                        "unit": "This is custom attribute unit"
                    }
                },
                {
                    "key": "attribute_1",
                    "value": {
                        "name": "Attribute1 name",
                        "description": "This is custom attribute description",
                        "unit": "This is custom attribute unit"
                    }
                }
            ]
        }
    }
}
```

这个方法也有两个问题：GraphQL查询变得有点复杂，并且对象结构需要硬编码。在这种情况下，未知的Map对象将不起作用。

## 4. 总结

在本文中，我们介绍了三种不同的技术来从GraphQL查询返回Map对象，我们讨论了它们各自的局限性，由于没有一种技术是完美的，因此必须根据需要使用它们。