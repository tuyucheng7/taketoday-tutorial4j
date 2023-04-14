## 1. 概述

本文介绍了使用Spring HATEOAS项目创建超媒体驱动的REST Web服务的过程。

## 2. Spring-HATEOAS

Spring HATEOAS项目是一个API库，我们可以使用它轻松创建遵循HATEOAS(Hypertext as the Engine of Application State)原则的REST表示。

**一般来说，该原则意味着API应该通过返回有关后续潜在步骤的相关信息以及每个响应来引导客户端完成应用程序**。

在本文中，我们将使用Spring HATEOAS构建一个示例，目标是解耦客户端和服务器，并在理论上允许API更改其URI模式而不会破坏客户端。

## 3. 准备工作

首先，让我们添加Spring HATEOAS依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-hateoas</artifactId>
    <version>2.6.4</version>
</dependency>
```

如果我们不使用Spring Boot，我们可以将以下库添加到我们的项目中：

```xml
<dependency>
    <groupId>org.springframework.hateoas</groupId>
    <artifactId>spring-hateoas</artifactId>
    <version>1.4.1</version>
</dependency>
<dependency>
    <groupId>org.springframework.plugin</groupId>
    <artifactId>spring-plugin-core</artifactId>
    <version>1.2.0.RELEASE</version>
</dependency>
```

与往常一样，我们可以在Maven Central中搜索最新版本的[spring-boot-starter-hateoas](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-hateoas/3.0.5)、[spring-hateoas](https://central.sonatype.com/artifact/org.springframework.hateoas/spring-hateoas/2.0.3)和[spring-plugin-core](https://central.sonatype.com/artifact/org.springframework.plugin/spring-plugin-core/3.0.0)依赖项。

接下来，我们有没有Spring HATEOAS支持的Customer资源：

```java
public class Customer {

    private String customerId;
    private String customerName;
    private String companyName;

    // standard getters and setters
}
```

我们有一个不支持Spring HATEOAS的控制器类：

```java
@RestController
@RequestMapping(value = "/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping("/{customerId}")
    public Customer getCustomerById(@PathVariable String customerId) {
        return customerService.getCustomerDetail(customerId);
    }
}
```

最后，客户资源表示：

```json
{
    "customerId": "10A",
    "customerName": "Jane",
    "customerCompany": "ABC Company"
}
```

## 4. 添加HATEOAS支持

在Spring HATEOAS项目中，我们既不需要查找Servlet上下文，也不需要将路径变量拼接到基本URI。

相反，**Spring HATEOAS提供了三个用于创建URI的抽象-RepresentationModel、Link和WebMvcLinkBuilder**。我们可以使用这些来创建元数据并将其关联到资源表示。

### 4.1 为资源添加超媒体支持

该项目提供了一个名为RepresentationModel的基类，可在创建资源表示时从中继承：

```java
public class Customer extends RepresentationModel<Customer> {
    private String customerId;
    private String customerName;
    private String companyName;

    // standard getters and setters
}
```

**Customer资源从RepresentationModel类扩展以继承add()方法**。因此，一旦我们创建了一个链接，我们就可以轻松地将该值设置为资源表示，而无需向其添加任何新字段。

### 4.2 创建链接

**Spring HATEOAS提供了一个Link对象来存储元数据(资源的位置或URI)**。

首先，我们将手动创建一个简单的链接：

```java
Link link = new Link("http://localhost:8080/spring-security-rest/api/customers/10A");
```

Link对象遵循[Atom](https://en.wikipedia.org/wiki/Atom_(standard))链接语法，由标识与资源的关系的rel和实际链接本身的href属性组成。

下面是Customer资源在包含新链接后的样子：

```json
{
    "customerId": "10A",
    "customerName": "Jane",
    "customerCompany": "ABC Company",
    "_links": {
        "self": {
            "href": "http://localhost:8080/spring-security-rest/api/customers/10A"
        }
    }
}
```

与响应关联的URI被限定为self链接。self关系的语义很清楚-它只是可以访问资源的规范位置。

### 4.3 创建更好的链接

该库提供的另一个非常重要的抽象是**WebMvcLinkBuilder-它通过避免对链接进行硬编码来简化URI的构建**。

以下代码片段演示如何使用WebMvcLinkBuilder类构建客户self链接：

```java
linkTo(CustomerController.class).slash(customer.getCustomerId()).withSelfRel();
```

我们来看一下：

-   linkTo()方法检查控制器类并获取其根映射
-   slash()方法将customerId值添加为链接的路径变量
-   最后，withSelfMethod()将关系限定为self链接

## 5. 关系

在上一节中，我们展示了自引用关系。但是，更复杂的系统也可能涉及其他关系。

例如，客户可以与订单建立关系。让我们也将Order类建模为资源：

```java
public class Order extends RepresentationModel<Order> {
    private String orderId;
    private double price;
    private int quantity;

    // standard getters and setters
}
```

此时，我们可以使用返回特定客户的所有订单的方法扩展CustomerController：

```java
@GetMapping(value = "/{customerId}/orders", produces = { "application/hal+json" })
public CollectionModel<Order> getOrdersForCustomer(@PathVariable final String customerId) {
    List<Order> orders = orderService.getAllOrdersForCustomer(customerId);
    for (final Order order : orders) {
        Link selfLink = linkTo(methodOn(CustomerController.class)
            .getOrderById(customerId, order.getOrderId())).withSelfRel();
        order.add(selfLink);
    }
 
    Link link = linkTo(methodOn(CustomerController.class).getOrdersForCustomer(customerId)).withSelfRel();
    CollectionModel<Order> result = CollectionModel.of(orders, link);
    return result;
}
```

我们的方法返回一个符合HAL返回类型的CollectionModel对象，以及每个订单和完整列表的“_self”链接。

这里要注意的重要一点是，客户订单的超链接取决于getOrdersForCustomer()方法的映射。我们将这些类型的链接称为方法链接，并展示WebMvcLinkBuilder如何帮助创建它们。

## 6. 控制器方法的链接

WebMvcLinkBuilder为Spring MVC控制器提供了丰富的支持。以下示例演示如何基于CustomerController类的getOrdersForCustomer()方法构建HATEOAS超链接：

```java
Link ordersLink = linkTo(methodOn(CustomerController.class)
    .getOrdersForCustomer(customerId)).withRel("allOrders");
```

**methodOn()通过在代理控制器上对目标方法进行虚拟调用来获取方法映射**，并将customerId设置为URI的路径变量。

## 7. Spring HATEOAS实战

**让我们将自链接和方法链接创建放在一个getAllCustomers()方法中**：

```java
@GetMapping(produces = { "application/hal+json" })
public CollectionModel<Customer> getAllCustomers() {
    List<Customer> allCustomers = customerService.allCustomers();

    for (Customer customer : allCustomers) {
        String customerId = customer.getCustomerId();
        Link selfLink = linkTo(CustomerController.class).slash(customerId).withSelfRel();
        customer.add(selfLink);
        if (orderService.getAllOrdersForCustomer(customerId).size() > 0) {
            Link ordersLink = linkTo(methodOn(CustomerController.class).getOrdersForCustomer(customerId)).withRel("allOrders");
            customer.add(ordersLink);
        }
    }

    Link link = linkTo(CustomerController.class).withSelfRel();
    CollectionModel<Customer> result = CollectionModel.of(allCustomers, link);
    return result;
}
```

接下来，让我们调用getAllCustomers()方法：

```shell
curl http://localhost:8080/spring-security-rest/api/customers
```

并检查结果：

```json
{
    "_embedded": {
        "customerList": [
            {
                "customerId": "10A",
                "customerName": "Jane",
                "companyName": "ABC Company",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/spring-security-rest/api/customers/10A"
                    },
                    "allOrders": {
                        "href": "http://localhost:8080/spring-security-rest/api/customers/10A/orders"
                    }
                }
            },
            {
                "customerId": "20B",
                "customerName": "Bob",
                "companyName": "XYZ Company",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/spring-security-rest/api/customers/20B"
                    },
                    "allOrders": {
                        "href": "http://localhost:8080/spring-security-rest/api/customers/20B/orders"
                    }
                }
            },
            {
                "customerId": "30C",
                "customerName": "Tim",
                "companyName": "CKV Company",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/spring-security-rest/api/customers/30C"
                    }
                }
            }
        ]
    },
    "_links": {
        "self": {
            "href": "http://localhost:8080/spring-security-rest/api/customers"
        }
    }
}
```

在每个资源表示中，都有一个self链接和allOrders链接以提取客户的所有订单。如果客户没有订单，则不会显示订单链接。

这个例子演示了Spring HATEOAS如何在REST Web服务中促进API的可发现性。**如果链接存在，客户可以跟随它并获得客户的所有订单**：

```shell
curl http://localhost:8080/spring-security-rest/api/customers/10A/orders
```

```json
{
    "_embedded": {
        "orderList": [
            {
                "orderId": "001A",
                "price": 150,
                "quantity": 25,
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/spring-security-rest/api/customers/10A/001A"
                    }
                }
            },
            {
                "orderId": "002A",
                "price": 250,
                "quantity": 15,
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/spring-security-rest/api/customers/10A/002A"
                    }
                }
            }
        ]
    },
    "_links": {
        "self": {
            "href": "http://localhost:8080/spring-security-rest/api/customers/10A/orders"
        }
    }
}
```

## 8. 总结

在本教程中，我们讨论了如何**使用Spring HATEOAS项目构建超媒体驱动的Spring REST Web服务**。

在示例中，我们看到客户端可以有一个应用程序入口点，并且可以根据响应表示中的元数据采取进一步的操作。

这允许服务器在不破坏客户端的情况下更改其URI模式。此外，应用程序可以通过在表示中放置新链接或URI来通告新功能。