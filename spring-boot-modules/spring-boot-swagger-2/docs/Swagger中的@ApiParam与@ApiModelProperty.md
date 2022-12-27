## 1. 概述

在本教程中，我们将简要介绍Swagger的[@ApiParam](http://docs.swagger.io/swagger-core/v1.5.0/apidocs/io/swagger/annotations/ApiParam.html)和[@ApiModelProperty](http://docs.swagger.io/swagger-core/v1.5.0/apidocs/io/swagger/annotations/ApiModelProperty.html)注解。此外，我们将比较这些注解并确定每个注解的正确用法。

## 2. 主要区别

**简单的说，@ApiParam和@ApiModelProperty注解会向Swagger添加不同的元数据**，@ApiParam注解用于API资源请求的参数，而@ApiModelProperty注解用于模型的属性。

## 3. @ApiParam

@ApiParam注解仅与[JAX-RS 1.x/2.x参数](https://www.baeldung.com/jersey-request-parameters)注解一起使用，如@PathParam、@QueryParam、@HeaderParam、@FormParam和@BeanParam。尽管swagger-core默认扫描这些注解，但我们可以使用@ApiParam添加有关参数的更多详细信息或者在从代码中读取值时更改值。

**@ApiParam注解有助于指定参数的名称、类型、描述(值)和示例值**。此外，我们可以指定参数是必需的还是可选的。

让我们来看看它的用法：

```java
@RequestMapping(method = RequestMethod.POST, value = "/createUser", produces = "application/json; charset=UTF-8")
@ResponseStatus(HttpStatus.CREATED)
@ResponseBody
@ApiOperation(value = "Create user", notes = "This method creates a new user")
public User createUser(@ApiParam(
	name = "firstName",
	type = "String",
	value = "First Name of the user",
	example = "Vatsal",
	required = true) @RequestParam String firstName) {
	return new User(firstName);
}
```

让我们看看@ApiParam示例的[Swagger UI](https://swagger.io/tools/swagger-ui/)表示形式：

<img src="../assets/img_7.png">

现在，让我们看看@ApiModelProperty。

## 4. @ApiModelProperty

**@ApiModelProperty注解允许我们控制特定于Swagger的定义，例如描述(值)、名称、数据类型、示例值和模型属性的允许值**。

此外，它还提供了额外的过滤属性，以防我们想在某些情况下隐藏该属性。

让我们向User的firstName字段添加一些模型属性：

```java
@ApiModelProperty(
    value = "first name of the user", 
    name = "firstName", 
    dataType = "String", 
    example = "Vatsal"
)
String firstName;
```

现在，让我们看一下Swagger UI中User模型的规范：

<img src="../assets/img_8.png">

## 5. 总结

在这篇快速文章中，我们了解了两个可用于为参数和模型属性添加元数据的Swagger注解。然后，我们查看了一些使用这些注解的示例代码，并查看了它们在Swagger UI中的表示形式。