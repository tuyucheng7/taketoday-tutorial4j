## 1. 概述

在本快速教程中，我们将探讨 Spring 的[@RequestParam](https://www.baeldung.com/spring-request-param)和@PathVariable注解之间的区别。

@RequestParam和@PathVariable都可以用来从请求URI 中提取值，但它们有点不同。

## 延伸阅读：

## [在 Spring 中验证 RequestParams 和 PathVariables](https://www.baeldung.com/spring-validate-requestparam-pathvariable)

了解如何使用Spring MVC验证请求参数和路径变量

[阅读更多](https://www.baeldung.com/spring-validate-requestparam-pathvariable)→

## [Spring 的 RequestBody 和 ResponseBody 注解](https://www.baeldung.com/spring-request-response-body)

了解 Spring @RequestBody 和 @ResponseBody 注解。

[阅读更多](https://www.baeldung.com/spring-request-response-body)→

## [使用 Spring @ResponseStatus 设置 HTTP 状态码](https://www.baeldung.com/spring-response-status)

查看 @ResponseStatus 注解以及如何使用它来设置响应状态代码。

[阅读更多](https://www.baeldung.com/spring-response-status)→

## 2.查询参数与URI路径

@RequestParam从查询字符串中提取值，@PathVariable从URI 路径中提取值：

```java
@GetMapping("/foos/{id}")
@ResponseBody
public String getFooById(@PathVariable String id) {
    return "ID: " + id;
}
```

然后我们可以根据路径进行映射：

```bash
http://localhost:8080/spring-mvc-basics/foos/abc
----
ID: abc
```

对于@RequestParam，它将是：

```java
@GetMapping("/foos")
@ResponseBody
public String getFooByIdUsingQueryParam(@RequestParam String id) {
    return "ID: " + id;
}
```

这会给我们相同的响应，只是一个不同的 URI：

```bash
http://localhost:8080/spring-mvc-basics/foos?id=abc
----
ID: abc
```

## 3.编码值与精确值

因为@PathVariable从 URI 路径中提取值，所以它没有被编码。另一方面， @RequestParam被编码。

使用前面的示例，ab+c将按原样返回：

```bash
http://localhost:8080/spring-mvc-basics/foos/ab+c
----
ID: ab+c
```

但是对于@RequestParam 请求，参数是 URL 解码的：

```bash
http://localhost:8080/spring-mvc-basics/foos?id=ab+c
----
ID: ab c
```

## 4.可选值

@RequestParam和@PathVariable都可以是可选的。

我们可以通过使用从 Spring 4.3.3 开始的必需属性使@PathVariable可选：

```java
@GetMapping({"/myfoos/optional", "/myfoos/optional/{id}"})
@ResponseBody
public String getFooByOptionalId(@PathVariable(required = false) String id){
    return "ID: " + id;
}
```

然后我们可以这样做：

```bash
http://localhost:8080/spring-mvc-basics/myfoos/optional/abc
----
ID: abc

```

或者：

```bash
http://localhost:8080/spring-mvc-basics/myfoos/optional
----
ID: null
```

对于@RequestParam，我们还可以使用[required](https://www.baeldung.com/spring-request-param#making-an-optional-request-parameter)属性。

请注意，在将@PathVariable 设为可选时我们应该小心，以避免路径冲突。

## 5.总结

在本文中，我们了解了@RequestParam和@PathVariable之间的区别。