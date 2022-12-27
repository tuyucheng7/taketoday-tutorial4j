## 1. 概述

在创建Swagger文档时，我们经常需要隐藏端点以免暴露给最终用户，最常见的情况是端点尚未准备好。此外，我们可能有一些我们不想公开的私有端点。

在这篇简短的文章中，我们将了解如何从Swagger API文档中隐藏端点。为此，我们需要在控制器类中使用一些注解。

## 2. 使用@ApiIgnore隐藏端点

**@ApiIgnore注解允许我们隐藏端点**，让我们在控制器中为端点添加一下注解：

```java
@ApiIgnore
@ApiOperation(value = "This method is used to get the author name.")
@GetMapping("/getAuthor")
public String getAuthor() {
    return "Umang Budhwar";
}
```

## 3. 使用@ApiOperation隐藏端点

或者，我们可以**使用@ApiOperation隐藏单个端点**：

```java
@ApiOperation(value = "This method is used to get the current date.", hidden = true)
@GetMapping("/getDate")
public LocalDate getDate() {
    return LocalDate.now();
}
```

请注意，我们**需要将hidden属性设置为true**以使Swagger忽略此端点。

## 4. 使用@ApiIgnore隐藏所有端点

尽管如此，有时我们需要**隐藏控制器类的所有端点**，我们可以通过使用@ApiIgnore注解标注控制器类来实现这一点：

```java
@ApiIgnore
@RestController
public class RegularRestController {

}
```

需要注意的是，**这将从文档中隐藏控制器本身**。

## 5. 使用@Hidden隐藏端点

如果我们使用的是OpenAPI v3，我们可以使用@Hidden注解隐藏端点：

```java
@Hidden
@ApiOperation(value = "This method is used to get the author name.")
@GetMapping("/getAuthor")
public String getAuthor() {
    return "Umang Budhwar";
}
```

## 6. 使用@Hidden隐藏所有端点

同样，我们可以使用@Hidden标注控制器以隐藏所有端点：

```java
@Hidden
@RestController
public class RegularRestController {
    // regular code
}
```

这也将从文档中隐藏控制器。

**注意：我们只能在使用OpenAPI时使用@Hidden，Swagger v3中对该注解的支持仍在进行中**。

## 7. 总结

在本教程中，我们了解了如何从Swagger文档中隐藏端点，并讨论了如何隐藏单个端点以及控制器类的所有端点。