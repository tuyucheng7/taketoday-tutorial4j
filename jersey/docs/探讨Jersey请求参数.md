## 1. 简介

[Jersey](https://www.baeldung.com/jersey-jax-rs-client) 是一个流行的Java框架，用于创建 RESTful Web 服务。

在本教程中，我们将探索如何通过一个简单的 Jersey 项目读取不同的请求参数类型。

## 2.项目设置

使用 Maven 原型，我们将能够为我们的文章生成一个工作项目：

```bash
mvn archetype:generate -DarchetypeArtifactId=jersey-quickstart-grizzly2
  -DarchetypeGroupId=org.glassfish.jersey.archetypes -DinteractiveMode=false
  -DgroupId=com.example -DartifactId=simple-service -Dpackage=com.example
  -DarchetypeVersion=2.28
```

生成的 Jersey 项目将在 Grizzly 容器之上运行。

现在，默认情况下，我们应用程序的端点将为http://localhost:8080/myapp。

让我们添加一个 项目资源，我们将用于我们的实验：

```java
@Path("items")
public class ItemsController {
    // our endpoints are defined here
}
```

请注意，顺便说一句，[Jersey 也适用于 Spring 控制器](https://www.baeldung.com/jersey-rest-api-with-spring)。

## 3. 注解参数类型

因此，在我们实际读取任何请求参数之前，让我们澄清一些规则。允许的参数类型是：

-   原始类型，如float和 char
-   具有带单个String参数的构造函数的类型
-   具有fromString或valueOf静态方法的类型；对于那些，单个String参数是强制性的
-   上述类型的集合——如List、Set和SortedSet

此外，我们可以注册 ParamConverterProvider JAX-RS 扩展 SPI 的实现。返回类型必须是能够从String转换为类型的ParamConverter实例。

## 4.饼干

我们可以使用@CookieParam注解在我们的 Jersey 方法中解析[cookie 值：](https://www.baeldung.com/cookies-java)

```java
@GET
public String jsessionid(@CookieParam("JSESSIONId") String jsessionId) {
    return "Cookie parameter value is [" + jsessionId+ "]";
}
```

如果我们启动我们的容器，我们可以[cURL](https://www.baeldung.com/curl-rest)这个端点来查看响应：

```bash
> curl --cookie "JSESSIONID=5BDA743FEBD1BAEFED12ECE124330923" http://localhost:8080/myapp/items
Cookie parameter value is [5BDA743FEBD1BAEFED12ECE124330923]
```

## 5.标题

或者，我们可以使用@HeaderParam注解解析[HTTP 标头](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers) ：

```java
@GET
public String contentType(@HeaderParam("Content-Type") String contentType) {
    return "Header parameter value is [" + contentType+ "]";
}
```

我们再测试一下：

```bash
> curl --header "Content-Type: text/html" http://localhost:8080/myapp/items
Header parameter value is [text/html]
```

## 6. 路径参数

特别是对于 RESTful API，在路径中包含信息是很常见的。

我们可以使用@PathParam提取路径元素：

```java
@GET
@Path("/{id}")
public String itemId(@PathParam("id") Integer id) {
    return "Path parameter value is [" + id + "]";
}
```

让我们发送另一个 值为3的curl命令：

```bash
> curl http://localhost:8080/myapp/items/3
Path parameter value is [3]
```

## 7. 查询参数

我们通常在 RESTful API 中使用查询参数来获取可选信息。

要读取这些值，我们可以使用@QueryParam注解：

```java
@GET
public String itemName(@QueryParam("name") String name) {
    return "Query parameter value is [" + name + "]";
}
```

所以，现在我们可以像以前一样使用curl进行测试：

```bash
> curl http://localhost:8080/myapp/items?name=Toaster
Query parameter value if [Toaster]
```

## 8. 表单参数

为了从表单提交中读取参数，我们将使用@FormParam注解：

```java
@POST
public String itemShipment(@FormParam("deliveryAddress") String deliveryAddress, 
  @FormParam("quantity") Long quantity) {
    return "Form parameters are [deliveryAddress=" + deliveryAddress+ ", quantity=" + quantity + "]";
}
```

我们还需要设置适当的Content-Type来模拟表单提交操作。让我们使用-d标志设置表单参数：

```bash
> curl -X POST -H 'Content-Type:application/x-www-form-urlencoded' 
  -d 'deliveryAddress=Washington nr 4&quantity=5' 
  http://localhost:8080/myapp/items
Form parameters are [deliveryAddress=Washington nr 4, quantity=5]
```

## 9.矩阵参数

[矩阵参数](https://www.baeldung.com/spring-mvc-matrix-variables)是一种更灵活的查询参数，因为它们可以添加到 URL 中的任何位置。

例如，在http://localhost:8080/myapp;name=value/items中，矩阵参数是name。

要读取这些值，我们可以使用可用的@MatrixParam注解：

```java
@GET
public String itemColors(@MatrixParam("colors") List<String> colors) {
    return "Matrix parameter values are " + Arrays.toString(colors.toArray());
}
```

现在我们将再次测试我们的端点：

```bash
> curl http://localhost:8080/myapp/items;colors=blue,red
Matrix parameter values are [blue,red]
```

## 10. Bean参数

最后，我们将检查如何使用 bean 参数组合请求参数。澄清一下，[bean参数](https://www.baeldung.com/jersey-bean-validation)实际上是一个组合了不同类型的请求参数的对象。

我们将在此处使用标头参数、路径和表单：

```java
public class ItemOrder {
    @HeaderParam("coupon")
    private String coupon;

    @PathParam("itemId")
    private Long itemId;

    @FormParam("total")
    private Double total;

    //getter and setter

    @Override
    public String toString() {
        return "ItemOrder {coupon=" + coupon + ", itemId=" + itemId + ", total=" + total + '}';
    }
}
```

此外，为了获得这样的参数组合，我们将使用@BeanParam注解：

```java
@POST
@Path("/{itemId}")
public String itemOrder(@BeanParam ItemOrder itemOrder) {
    return itemOrder.toString();
}
```

在curl命令中，我们添加了这三种类型的参数，最终得到一个ItemOrder对象：

```bash
> curl -X POST -H 'Content-Type:application/x-www-form-urlencoded' 
  --header 'coupon:FREE10p' 
  -d total=70 
  http://localhost:8080/myapp/items/28711
ItemOrder {coupon=FREE10p, itemId=28711, total=70}
```

## 11.总结

总而言之，我们为 Jersey 项目创建了一个简单的设置，以帮助我们探索如何使用 Jersey 从请求中读取不同的参数。