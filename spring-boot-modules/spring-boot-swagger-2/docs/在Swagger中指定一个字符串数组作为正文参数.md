## 1. 概述

[Swagger](https://swagger.io/)是一组用于记录和描述REST API的规范，它还提供了端点参数的示例值。

在本教程中，我们将演示如何为字符串数组生成默认示例值，因为默认情况下Swagger没有启用此行为。

## 2. 在Swagger中指定一个字符串数组作为正文参数

当我们想在Swagger中指定一个字符串数组作为正文参数时，问题就出现了。

Swagger的默认Example Value有点不透明，正如我们在[Swagger编辑器](https://editor.swagger.io/)中看到的那样：

<img src="../assets/img_5.png">

所以，在这里我们看到Swagger并没有真正显示数组内容应该是什么样子的示例，让我们看看如何添加一个。

## 3. YAML

首先，我们首先使用YAML表示法在Swagger中指定字符串数组，在schema部分，我们包括type: array和items String。

为了更好地记录API并指导用户，我们可以使用如何插入值的example标签：

```yaml
parameters:
    -   in: body
        description: ""
        required: true
        name: name
        schema:
            type: array
            items:
                type: string
            example: [ "str1", "str2", "str3" ]
```

让我们看看我们的显示现在如何提供更多信息：

<img src="../assets/img_6.png">

## 4. Springfox

或者，我们可以使用[Springfox](https://www.google.com/search?client=safari&rls=en&q=springfox+baeldung&ie=UTF-8&oe=UTF-8)来实现相同的效果。

我们需要使用带有@ApiModel和@ApiModelProperty注解的数据模型中的dataType和example：

```java
@ApiModel
public class Foo {
    private long id;
    @ApiModelProperty(name = "name", dataType = "List", example = "[\"str1\", \"str2\", \"str3\"]")
    private List<String> name;
}
```

之后，我们还需要给Controller指定注解，让Swagger指向数据模型。

因此，让我们为此使用@ApiImplicitParams：

```java
@RequestMapping(method = RequestMethod.POST, value = "/foos")
@ResponseStatus(HttpStatus.CREATED)
@ResponseBody
@ApiImplicitParams({ @ApiImplicitParam(name = "foo", value = "List of strings", paramType = "body", dataType = "Foo") })
public Foo create(@RequestBody final Foo foo) {
}
```

就是这样，我们也可以实现相同的效果。

## 5. 总结

在记录REST API时，我们的参数可能是字符串数组，理想情况下，我们会使用示例值来记录这些内容。

我们可以在Swagger中使用example属性执行此操作。或者，我们可以使用Springfox中的example注解属性。