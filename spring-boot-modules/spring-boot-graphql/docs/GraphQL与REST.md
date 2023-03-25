## 1. 概述

在创建Web服务来支持我们的应用程序时，我们可能会选择使用[REST](https://www.baeldung.com/category/rest/)或GraphQL作为通信模式。虽然两者都最有可能通过HTTP使用JSON，但它们各有优缺点。

在本教程中，我们将比较GraphQL和REST。我们将创建一个产品数据库示例，并比较两种解决方案在执行相同的客户端操作时的差异。

## 2. 示例服务

我们的示例服务将使我们能够：

-   创建处于草稿状态的产品
-   更新产品详细信息
-   获取产品列表
-   获取单个产品及其订单的详细信息

让我们首先使用REST创建应用程序。

## 3. REST

REST(Representational State Transfer)是分布式超媒体系统的一种[架构风格](https://www.ics.uci.edu/~fielding/pubs/dissertation/rest_arch_style.htm)。REST中的主要数据元素称为[资源](https://www.ics.uci.edu/~fielding/pubs/dissertation/rest_arch_style.htm#tab_5_1)。在这个例子中，资源是“product”。

### 3.1 创建产品

要创建产品，我们将在API上使用POST方法：

```curl
curl --request POST 'http://localhost:8081/product' \
--header 'Content-Type: application/json' \
--data '{
    "name": "Watch",
    "description": "Special Swiss Watch",
    "status": "Draft",
    "currency": "USD",
    "price": null,
    "imageUrls": null,
    "videoUrls": null,
    "stock": null,
    "averageRating": null
}'
```

因此，系统将创建一个新产品。

### 3.2 更新产品

在REST中，我们通常使用PUT方法更新产品：

```curl
curl --request PUT 'http://localhost:8081/product/{product-id}' \
--header 'Content-Type: application/json' \
--data '{
    "name": "Watch",
    "description": "Special Swiss Watch",
    "status": "Draft",
    "currency": "USD",
    "price": 1200.0,
    "imageUrls": [
        "https://graphqlvsrest.com/imageurl/product-id"
    ],
    "videoUrls": [
        "https://graphqlvsrest.com/videourl/product-id"
    ],
    "stock": 10,
    "averageRating": 0.0
}'
```

因此，{product-id}对象将有一个更新。

### 3.3 获取产品列表

列出产品通常是一个GET操作，我们可以使用查询字符串来指定分页：

```curl
curl --request GET 'http://localhost:8081/product?size=10&page=0'
```

第一个响应对象是：

```json
{
    "id": 1,
    "name": "T-Shirt",
    "description": "Special beach T-Shirt",
    "status": Published,
    "currency": "USD",
    "price": 30.0,
    "imageUrls": ["https://graphqlvsrest.com/imageurl/1"],
    "videoUrls": ["https://graphqlvsrest.com/videourl/1"],
    "stock": 10,
    "averageRating": 3.5
}
```

### 3.4 通过订单获取单个产品

要获取产品及其订单，我们通常可能希望使用之前的API获取产品列表，然后调用订单资源以查找相关订单：

```curl
curl --request GET 'localhost:8081/order?product-id=1'
```

第一个响应对象是：

```json
{
    "id": 1,
    "productId": 1,
    "customerId": "de68a771-2fcc-4e6b-a05d-e30a8dd0d756",
    "status": "Delivered",
    "address": "43-F 12th Street",
    "creationDate": "Mon Jan 17 01:00:18 GST 2022"
}
```

当我们通过product-id查询订单时，将id作为查询参数提供给GET操作是有意义的。但是，我们应该注意，在获取所有产品的原始操作之上，我们需要为我们感兴趣的每个产品执行一次此操作。这与[N+1选择问题](https://www.baeldung.com/hibernate-common-performance-problems-in-logs#2-n1-select-issue)有关。

## 4. GraphQL

[GraphQL](https://www.baeldung.com/graphql)是一种API查询语言，它带有一个框架，可以使用现有的数据服务来完成这些查询。

GraphQL的构建块是[queries和mutations](https://graphql.org/learn/queries/)。查询负责获取数据，而突变用于创建和更新。

query和mutation都定义了一个[schema](https://graphql.org/learn/schema/)。该模式定义了可能的客户端请求和响应。

让我们使用[GraphQL服务器](https://www.baeldung.com/spring-graphql)重新实现我们的示例。

### 4.1 创建产品

让我们使用一个名为saveProduct的突变：

```curl
curl --request POST 'http://localhost:8081/graphql' \
--header 'Content-Type: application/json' \
--data \
'{
    "query": "mutation {saveProduct (
        product: {
            name: \"Bed-Side Lamp\",
            price: 24.0,
            status: \"Draft\",
            currency: \"USD\"
        }){ id name currency price status}
    }"
}'
```

在此saveProduct函数中，圆括号内的所有内容都是[输入类型schema](https://graphql.org/learn/schema/#input-types)。这之后的大括号描述了服务器要返回的[字段](https://graphql.org/learn/queries/#fields)。

当我们运行突变时，我们应该期望一个包含所选字段的响应：

```json
{
    "data": {
        "saveProduct": {
            "id": "12",
            "name": "Bed-Side Lamp",
            "currency": "USD",
            "price": 24.0,
            "status": "Draft"
        }
    }
}
```

此请求与我们使用REST版本发出的POST请求非常相似，只是我们现在可以稍微自定义响应。

### 4.2 更新产品

同样，我们可以使用另一个名为updateProduct的突变来修改产品：

```curl
curl --request POST 'http://localhost:8081/graphql' \
--header 'Content-Type: application/json' \
--data \
'{"query": "mutation {updateProduct(
    id: 11
    product: {
        price: 14.0,
        status: \"Publish\"
    }){ id name currency price status }  
  }","variables":{}}'
```

我们在响应中得到选定的字段：

```json
{
    "data": {
        "updateProduct": {
            "id": "12",
            "name": "Bed-Side Lamp",
            "currency": "USD",
            "price": 14.0,
            "status": "Published"
        }
    }
}
```

正如我们所看到的，**GraphQL提供了响应格式的灵活性**。

### 4.3 获取产品列表

要从服务器获取数据，我们将使用查询：

```curl
curl --request POST 'http://localhost:8081/graphql' \
--header 'Content-Type: application/json' \
--data \
'{
    "query": "query {products(size:10,page:0){id name status}}"
}'
```

在这里，我们还描述了我们希望看到的结果页：

```json
{
    "data": {
        "products": [
            {
                "id": "1",
                "name": "T-Shirt",
                "status": "Published"
            },
            ...
        ]
    }
}
```

### 4.4 通过订单获取单个产品

使用GraphQL，我们可以要求GraphQL服务器将产品和订单连接在一起：

```curl
curl --request POST 'http://localhost:8081/graphql' \
--header 'Content-Type: application/json' \
--data \
'{
    "query": "query {product(id:1){ id name orders{customerId address status creationDate}}}"
}'
```

在此查询中，我们获取id等于1的产品及其订单。这使我们能够在单个操作中发出请求，让我们检查响应：

```json
{
    "data": {
        "product": {
            "id": "1",
            "name": "T-Shirt",
            "orders": [
                {
                    "customerId": "de68a771-2fcc-4e6b-a05d-e30a8dd0d756",
                    "status": "Delivered",
                    "address": "43-F 12th Street",
                    "creationDate": "Mon Jan 17 01:00:18 GST 2022"
                },
                ...
            ]
        }
    }
}
```

正如我们在这里看到的，响应包含产品的详细信息及其各自的订单。

为了使用REST实现这一点，我们需要发送几个请求-第一个请求获取产品，第二个请求获取相应的订单。

## 5. 比较

这些示例展示了GraphQL的使用如何减少客户端和服务器之间的流量，并允许客户端为响应提供一些格式化规则。

值得注意的是，这些API背后的数据源可能仍然需要执行相同的操作来修改或获取数据，但客户端和服务器之间接口的丰富性允许客户端使用GraphQL做更少的工作。

让我们进一步比较这两种方法。

### 5.1 灵活和动态

GraphQL允许灵活和动态的查询：

-   客户端应用程序只能请求必填字段
-   [别名](https://graphql.org/learn/queries/#aliases)可用于请求具有自定义key的字段
-   客户端可以使用查询来管理结果顺序
-   客户端可以更好地与API中的任何更改分离，因为没有单一版本的响应对象结构可遵循

### 5.2 成本较低的操作

**每个服务器请求都有往返时间和负载大小的成本**。

在REST中，我们可能最终会发送多个请求来实现所需的功能。这些多个请求将是一项昂贵的操作。此外，响应有效负载可能包含客户端应用程序可能不需要的不必要数据。

GraphQL倾向于避免昂贵的操作。**我们通常可以使用GraphQL在单个请求中获取我们需要的所有数据**。

### 5.3 何时使用REST？

**GraphQL不是REST的替代品**。两者甚至可以共存于同一个应用程序中。托管GraphQL端点的复杂性增加可能是值得的，具体取决于用例。

在以下情况下，我们可能更倾向REST：

-   我们的应用程序自然是资源驱动的，其中的操作非常直接且完全与各个资源实体相关联
-   需要网络缓存，因为GraphQL本身不支持它
-   我们需要文件上传，因为GraphQL本身不支持它

## 6. 总结

在本文中，我们使用一个实际示例比较了REST和GraphQL。