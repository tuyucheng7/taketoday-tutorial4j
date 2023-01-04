## 1. 概述

在本教程中，我们将展示Spring应用程序中POST请求的HTTP响应代码415 Unsupported MediaType的原因和解决方案。

## 2. 背景故事

我们的一位老业务客户要求我们为其产品设计和开发新的桌面应用程序。此应用程序的目的是管理用户。我们以前从未研究过该产品。

由于时间紧迫，我们决定使用他们不久前编写的现有后端API集。摆在我们面前的挑战是这些API的文档不是很丰富。因此，我们只能确定可用的API端点及其方法。因此，我们决定不触及这些服务，相反，我们将开始开发我们的应用程序，该应用程序将使用来自该服务的API。

## 3. API请求

我们的应用程序已开始使用API。让我们练习API来获取所有用户：

```bash
curl -X GET https://tuyucheng.service.com/user
```

欢呼！API已返回成功响应。之后，让我们请求个人用户：

```bash
curl -X GET https://tuyucheng.service.com/user/{user-id}
```

让我们检查响应：

```json
{
    "id": 1,
    "name": "Jason",
    "age": 23,
    "address": "14th Street"
}
```

这似乎也有效。因此，事情看起来很顺利。根据响应，我们能够确定用户具有以下参数：id、name、age和address。

现在，让我们尝试添加一个新用户：

```bash
curl -X POST -d '{"name":"Abdullah", "age":28, "address":"Apartment 2201"}' https://baeldung.service.com/user/
```

结果，我们收到了HTTP状态为415的错误响应：

```json
{
    "timestamp": "yyyy-MM-ddThh:mm:ss.SSS+00:00",
    "status": 415,
    "error": "Unsupported Media Type",
    "path": "/user/"
}
```

在我们弄清楚“为什么我们会收到这个错误？”之前，我们需要研究“这个错误是什么？”。

## 4. 状态代码415：不支持的媒体类型

根据规范[RFC 7231标题HTTP/1.1语义和内容部分6.5.13](https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.13)：

>   415(不支持的媒体类型)状态代码表示源服务器拒绝为请求提供服务，因为有效负载的格式不受此方法在目标资源上的支持。

正如规范所建议的那样，API不支持我们选择的媒体类型。选择JSON作为媒体类型的原因是因为GET请求的响应。响应数据格式为JSON。因此，我们假设POST请求也将接受JSON。然而，这个假设被证明是错误的。

为了找到API支持的格式，我们决定深入研究服务器端后端代码，我们找到了API定义：

```java
@PostMapping(value = "/", consumes = {"application/xml"})
void AddUser(@RequestBody User user)
```

这非常清楚地说明了API将只支持XML格式。这里可能会有人质疑：Spring中这个“consumes”元素的作用是什么？

根据Spring框架[文档](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RequestMapping.html#consumes--)，“consumes”元素的用途是：

>   按映射处理程序可以使用的媒体类型缩小主要映射。由一种或多种媒体类型组成，其中一种必须与请求的Content-Type标头匹配

## 5. 解决方案

我们面前有两种选择来解决这个问题。第一个选项是根据服务器的期望更改请求有效负载格式。第二个选项是更新API请求，使其开始支持JSON格式。

### 5.1 将请求的负载更改为XML

第一个选项是以XML格式而不是JSON格式发送请求：

```bash
curl -X POST -d '<user><name>Abdullah</name><age>28</age><address>Apartment 2201</address></user>' https://tuyucheng.service.com/user/
```

不幸的是，由于上述请求，我们得到了同样的错误。如果我们还记得的话，我们曾经问过这个[问题](https://www.baeldung.com/spring-415-unsupported-mediatype#1-consumes-method-in-spring)，即API中“consumer”元素的目的是什么。这为我们指出了我们的标题之一(“Content-Type”)缺失的方向。让我们发送请求，这次缺少标头：

```bash
curl -X POST -H "Content-Type: application/xml" -d '<user><name>Abdullah</name><age>28</age><address>Apartment 2201</address></user>' https://tuyucheng.service.com/user/
```

这一次，我们收到了成功的响应。但是，我们可能会遇到客户端应用程序无法以支持的格式发送请求的情况。在那种情况下，我们必须在服务器上进行更改以使事情相对灵活。

### 5.2 更新服务器上的API

假设我们的客户决定允许我们更改后端服务。上面提到的第二个选项是更新API请求以开始接受JSON格式。关于我们如何更新API请求，还有三个选项。让我们一一探索。

第一个也是最业余的选择是在API上用JSON替换XML格式：

```java
@PostMapping(value = "/", consumes = {"application/json"}) 
void AddUser(@RequestBody User user)
```

让我们从客户端应用程序以JSON格式再次发送请求：

```bash
curl -X POST -H "Content-Type: application/json" -d '{"name":"Abdullah", "age":28, "address":"Apartment 2201"} https://tuyucheng.service.com/user/'
```

响应将是成功的。但是，我们将面临这样一种情况，即所有以XML格式发送请求的现有客户端现在将开始收到415 Unsupported Media Type错误。

第二个更简单的选项是允许请求有效负载中的每种格式：

```java
@PostMapping(value = "/", consumes = {"*/*"}) 
void AddUser(@RequestBody User user
```

以JSON格式请求，响应成功。然而，这里的问题是它太灵活了。我们不希望允许范围广泛的格式。这将导致整个代码库(客户端和服务器端)的行为不一致。

第三个也是推荐的选项是专门添加客户端应用程序当前使用的那些格式。由于API已经支持XML格式，我们将保留它，因为现有的客户端应用程序会向API发送XML。为了使API也支持我们的应用程序格式，我们将进行简单的API代码更改：

```java
@PostMapping(value = "/", consumes = {"application/xml","application/json"}) 
void AddUser(@RequestBody User user
```

以JSON格式发送我们的请求后，响应将成功。这是在此特定场景中推荐的方法。

## 6. 总结

在本文中，我们了解到必须从客户端应用程序请求发送“Content-Type”标头，以避免出现415 Unsupported Media Type错误。此外，RFC明确说明客户端应用程序和服务器端应用程序的“Content-Type”标头必须同步，以避免在发送POST请求时出现此错误。