## 1. 概述

微服务是近年来流行起来的。微服务的基本特征之一是它们是模块化的、隔离的并且易于扩展。微服务需要协同工作并交换数据。为此，我们创建了称为 DTO 的共享数据传输对象。

在本文中，我们将介绍微服务之间共享 DTO 的方式。

## 2. 将域对象暴露为 DTO

表示应用程序域的模型使用微服务进行管理。领域模型是不同的关注点，我们在 DAO 层将它们与数据模型分开。

这样做的主要原因是我们不想通过服务向客户暴露我们领域的复杂性。相反，我们在通过 REST API 为应用程序客户端提供服务的服务之间公开 DTO。当 DTO 在这些服务之间传递时，我们将它们转换为域对象。

[![应用程序架构与 dtos 和服务门面原创](https://www.baeldung.com/wp-content/uploads/2020/06/application_architecture_with_dtos_and_service_facade_original-1.png)](https://www.baeldung.com/wp-content/uploads/2020/06/application_architecture_with_dtos_and_service_facade_original-1.png)

上面的[面向服务的架构](https://xebia.com/blog/jpa-implementation-patterns-service-facades-and-data-transfers-objects/)示意性地展示了 DTO 到 Domain 对象的组件和流程。

## 3、微服务间共享DTO

以客户订购产品的过程为例。此过程基于客户订单模型。我们从服务架构的角度来看流程。

假设客户服务将请求数据发送到订单服务：

```javascript
"order": {
    "customerId": 1,
    "itemId": "A152"
}
```

Customer 和 Order 服务使用契约相互通信。 合同(否则为服务请求)以 JSON 格式显示。作为Java模型，OrderDTO类代表客户服务和订单服务之间的契约：

```java
public class OrderDTO {
    private int customerId;
    private String itemId;

    // constructor, getters, setters
}
```

### 3.1. 使用客户端模块(库)共享 DTO

微服务需要来自其他服务的某些信息来处理任何请求。假设有第三个微服务接收订单支付请求。与订单服务不同，此服务需要不同的客户信息：

```java
public class CustomerDTO {
    private String firstName;
    private String lastName;
    private String cardNumber;

    // constructor, getters, setters
}
```

如果我们还添加送货服务，客户信息将具有：

```java
public class CustomerDTO {
    private String firstName;
    private String lastName;
    private String homeAddress;
    private String contactNumber;

    // constructor, getters, setters
}
```

因此，将CustomerDTO类放在共享模块中不再符合预期目的。为了解决这个问题，我们采用了一种不同的方法。

在每个微服务模块中，让我们创建一个客户端模块(库) ，然后在它旁边创建一个服务器模块：

```powershell
order-service
|__ order-client
|__ order-server
```

order-client模块包含一个与客户服务共享的 DTO 。因此，order-client模块的结构如下：

```powershell
order-service
└──order-client
     OrderClient.java
     OrderClientImpl.java
     OrderDTO.java

```

OrderClient是一个接口，它定义了一个用于处理订单请求的订单方法：

```java
public interface OrderClient {
    OrderResponse order(OrderDTO orderDTO);
}
```

为了实现order方法，我们使用RestTemplate对象向 Order 服务发送 POST 请求：

```java
String serviceUrl = "http://localhost:8002/order-service";
OrderResponse orderResponse = restTemplate.postForObject(serviceUrl + "/create", 
  request, OrderResponse.class);
```

此外，order-client模块已经可以使用了。现在成为customer-service模块的依赖库：

```shell
[INFO] --- maven-dependency-plugin:3.1.2:list (default-cli) @ customer-service ---
[INFO] The following files have been resolved:
[INFO]    com.baeldung.orderservice:order-client:jar:1.0-SNAPSHOT:compile
```

当然，如果没有订单服务器模块将“/create”服务端点暴露给订单客户端，这将毫无意义：

```java
@PostMapping("/create")
public OrderResponse createOrder(@RequestBody OrderDTO request)
```

由于这个服务端点，客户服务可以通过其订单客户端发送订单请求。通过使用客户端模块，微服务以更隔离的方式相互通信。DTO 中的属性在客户端模块中更新。因此，违约仅限于使用相同客户端模块的服务。

## 4. 总结

在本文中，我们解释了一种在微服务之间共享 DTO 对象的方法。充其量，我们通过将特殊契约作为微服务客户端模块(库)的一部分来实现这一点。通过这种方式，我们将服务客户端与包含 API 资源的服务器部分分开。结果，有一些好处：

-   服务之间的DTO代码没有冗余
-   违约仅限于使用相同客户端库的服务