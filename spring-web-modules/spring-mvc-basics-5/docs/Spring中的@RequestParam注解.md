## 1. 概述

在本快速教程中，我们将探索 Spring 的@RequestParam注解及其属性。

简单的说，我们可以使用@RequestParam从请求中提取查询参数、表单参数甚至文件。

## 延伸阅读：

## [Spring @RequestMapping 新的快捷键注解](https://www.baeldung.com/spring-new-requestmapping-shortcuts)

在本文中，我们介绍了不同类型的@RequestMapping 快捷方式，用于使用传统的Spring MVC框架进行快速 Web 开发。

[阅读更多](https://www.baeldung.com/spring-new-requestmapping-shortcuts)→

## [Spring @Controller 和@RestController 注解](https://www.baeldung.com/spring-controller-vs-restcontroller)

了解Spring MVC中@Controller 和@RestController 注解的区别。

[阅读更多](https://www.baeldung.com/spring-controller-vs-restcontroller)→

## 2. 一个简单的映射

假设我们有一个端点/api/foos，它接受一个名为 id的查询参数：

```java
@GetMapping("/api/foos")
@ResponseBody
public String getFoos(@RequestParam String id) {
    return "ID: " + id;
}
```

在这个例子中，我们使用@RequestParam来提取id查询参数。

一个简单的 GET 请求将调用getFoos：

```bash
http://localhost:8080/spring-mvc-basics/api/foos?id=abc
----
ID: abc
```

接下来，让我们看一下注解的属性：name、 value、required和defaultValue。

## 3.指定请求参数名称

在前面的示例中，变量名和参数名都是相同的。

不过，有时我们希望它们有所不同。或者，如果我们不使用 Spring Boot，我们可能需要进行特殊的编译时配置，否则参数名称实际上不会出现在字节码中。

幸运的是，我们可以使用name属性配置@RequestParam名称：

```java
@PostMapping("/api/foos")
@ResponseBody
public String addFoo(@RequestParam(name = "id") String fooId, @RequestParam String name) { 
    return "ID: " + fooId + " Name: " + name;
}
```

我们也可以做 @RequestParam(value = “id”)或只是@RequestParam(“id”)。

## 4.可选的请求参数

 默认情况下需要使用@RequestParam注解的方法参数 。

这意味着如果请求中不存在该参数，我们将收到错误消息：

```bash
GET /api/foos HTTP/1.1
-----
400 Bad Request
Required String parameter 'id' is not present
```

但是，我们可以将@RequestParam配置为可选的，具有必需 的属性：

```java
@GetMapping("/api/foos")
@ResponseBody
public String getFoos(@RequestParam(required = false) String id) { 
    return "ID: " + id;
}
```

在这种情况下，两者：

```bash
http://localhost:8080/spring-mvc-basics/api/foos?id=abc
----
ID: abc
```

和

```bash
http://localhost:8080/spring-mvc-basics/api/foos
----
ID: null
```

将正确调用该方法。

未指定参数时，方法参数绑定到null。

### 4.1. 使用Java8可选

或者，我们可以将参数包装在 [Optional](https://www.baeldung.com/java-optional)中：

```java
@GetMapping("/api/foos")
@ResponseBody
public String getFoos(@RequestParam Optional<String> id){
    return "ID: " + id.orElseGet(() -> "not provided");
}
```

在这种情况下，我们不需要指定所需的属性。

如果没有提供请求参数，将使用默认值：

```bash
http://localhost:8080/spring-mvc-basics/api/foos 
---- 
ID: not provided
```

## 5.请求参数的默认值

我们还可以 使用defaultValue属性为@RequestParam设置默认值：

```java
@GetMapping("/api/foos")
@ResponseBody
public String getFoos(@RequestParam(defaultValue = "test") String id) {
    return "ID: " + id;
}
```

这类似于required=false， 因为用户不再需要提供参数：

```bash
http://localhost:8080/spring-mvc-basics/api/foos
----
ID: test
```

虽然，我们仍然可以提供它：

```bash
http://localhost:8080/spring-mvc-basics/api/foos?id=abc
----
ID: abc
```

请注意，当我们设置 defaultValue 属性时， required确实设置为false。

## 6. 映射所有参数

我们还可以有多个参数而不定义它们的名称或仅使用Map进行计数：

```java
@PostMapping("/api/foos")
@ResponseBody
public String updateFoos(@RequestParam Map<String,String> allParams) {
    return "Parameters are " + allParams.entrySet();
}
```

然后将反射回发送的任何参数：

```bash
curl -X POST -F 'name=abc' -F 'id=123' http://localhost:8080/spring-mvc-basics/api/foos
-----
Parameters are {[name=abc], [id=123]}
```

## 7.映射多值参数

单个@RequestParam可以有多个值：

```java
@GetMapping("/api/foos")
@ResponseBody
public String getFoos(@RequestParam List<String> id) {
    return "IDs are " + id;
}
```

Spring MVC 将映射一个逗号分隔的 id 参数：

```bash
http://localhost:8080/spring-mvc-basics/api/foos?id=1,2,3
----
IDs are [1,2,3]
```

或单独的id参数列表：

```bash
http://localhost:8080/spring-mvc-basics/api/foos?id=1&id=2
----
IDs are [1,2]
```

## 八. 总结

在本文中，我们学习了如何使用@RequestParam。