## 1. 概述

在本教程中，我们将了解定义自定义媒体类型并通过Spring REST控制器生成它们。

使用自定义媒体类型的一个很好的用例是对API进行版本控制。

## 2. API–版本1

让我们从一个简单的例子开始，一个通过id公开单个资源的API。

我们将从我们向客户端公开的资源的版本1开始。为此，我们将使用自定义HTTP标头“application/vnd.tuyucheng.api.v1+json”。

客户端将通过Accept标头请求此自定义媒体类型。

这是我们的简单端点：

```java
@RequestMapping(
    method = RequestMethod.GET, 
    value = "/public/api/items/{id}", 
    produces = "application/vnd.tuyucheng.api.v1+json"
)
@ResponseBody
public TuyuchengItem getItem( @PathVariable("id") String id ) {
    return new TuyuchengItem("itemId1");
}
```

注意此处的produces参数，指定此API能够处理的自定义媒体类型。

现在，TuyuchengItem资源它只有一个字段itemId：

```java
public class TuyuchengItem {
    private String itemId;
    
    // standard getters and setters
}
```

最后但同样重要的是，让我们为端点编写一个集成测试：

```java
@Test
public void givenServiceEndpoint_whenGetRequestFirstAPIVersion_then200() {
    given().accept("application/vnd.tuyucheng.api.v1+json")
    .when().get(URL_PREFIX + "/public/api/items/1")
    .then().contentType(ContentType.JSON).and().statusCode(200);
}
```

## 3. API–版本2

现在假设我们需要更改我们通过资源向客户端公开的详细信息。

我们过去常常公开一个原始ID，假设现在我们需要隐藏它并公开一个名称，以获得更多的灵活性。

重要的是要了解此更改不向后兼容；基本上，这是一个突破性的变化。

这是我们的新资源定义：

```java
public class TuyuchengItemV2 {
    private String itemName;

    // standard getters and setters
}
```

因此，我们在这里需要做的是，将我们的API迁移到第二个版本。

我们将通过创建下一个版本的自定义媒体类型并定义一个新端点来做到这一点：

```java
@RequestMapping(
    method = RequestMethod.GET, 
    value = "/public/api/items/{id}", 
    produces = "application/vnd.tuyucheng.api.v2+json"
)
@ResponseBody
public TuyuchengItemV2 getItemSecondAPIVersion(@PathVariable("id") String id) {
    return new TuyuchengItemV2("itemName");
}
```

因此，我们现在拥有完全相同的端点，但能够处理新的V2操作。

当客户端请求“application/vnd.tuyucheng.api.v1+json”时，Spring将委托给旧操作，客户端将收到一个带有itemId字段(V1)的TuyuchengItem。

但是当客户端现在将Accept标头设置为“application/vnd.tuyucheng.api.v2+json”时——他们将正确地点击新操作并取回带有itemName字段的资源(V2)：

```java
@Test
public void givenServiceEndpoint_whenGetRequestSecondAPIVersion_then200() {
    given().accept("application/vnd.tuyucheng.api.v2+json")
    .when().get(URL_PREFIX + "/public/api/items/2")
    .then().contentType(ContentType.JSON).and().statusCode(200);
}
```

请注意测试是如何相似的，但使用的是不同的Accept标头。

## 4. 类级别的自定义媒体类型

最后，让我们谈谈媒体类型的类范围定义——这也是可能的：

```java
@RestController
@RequestMapping(
    value = "/", 
    produces = "application/vnd.tuyucheng.api.v1+json"
)
public class CustomMediaTypeController {}
```

正如预期的那样，@RequestMapping注解很容易在类级别上工作，并允许我们指定value、produces和consumes参数。

## 5. 总结

本文举例说明了定义自定义媒体类型可能对公共API版本化很有用。