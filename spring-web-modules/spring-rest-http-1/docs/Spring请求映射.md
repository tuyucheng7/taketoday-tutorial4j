## 1. 概述

在本教程中，我们将重点关注Spring MVC中的主要注解之一：@RequestMapping。

简单的说，注解就是用来将web请求映射到Spring Controller的方法上。

## 2. @RequestMapping基础

让我们从一个简单的示例开始：使用一些基本条件将HTTP请求映射到方法。

### 2.1 @RequestMapping-按路径

```java
@RequestMapping(
      value = "/ex/foos", 
      method = RequestMethod.GET)
@ResponseBody
public String getFoosBySimplePath() {
    return "Get some Foos";
}
```

要使用简单的curl命令测试此映射，请运行：

```bash
curl -i http://localhost:8080/spring-rest/ex/foos
```

### 2.2 @RequestMapping-HTTP方法

HTTP方法参数没有默认值。因此，如果我们不指定值，它将映射到任何HTTP请求。

这是一个简单的示例，与上一个示例类似，但这次映射到HTTP POST请求：

```java
@RequestMapping(
      value = "/ex/foos", 
      method = POST)
@ResponseBody
public String postFoos() {
    return "Post some Foos";
}
```

要通过curl命令测试POST：

```bash
curl -i -X POST http://localhost:8080/spring-rest/ex/foos
```

## 3. RequestMapping和HTTP标头

### 3.1 @RequestMapping和headers属性

通过为请求指定标头，可以进一步缩小映射范围：

```java
@RequestMapping(
      value = "/ex/foos", 
      headers = "key=val", 
      method = GET)
@ResponseBody
public String getFoosWithHeader() {
    return "Get some Foos with Header";
}
```

为了测试操作，我们将使用curl标头支持：

```bash
curl -i -H "key:val" http://localhost:8080/spring-rest/ex/foos
```

甚至通过@RequestMapping的headers属性使用多个标头：

```java
@RequestMapping(
      value = "/ex/foos", 
      headers = { "key1=val1", "key2=val2" }, 
      method = GET)
@ResponseBody
public String getFoosWithHeaders() {
    return "Get some Foos with Header";
}
```

我们可以使用以下命令进行测试：

```bash
curl -i -H "key1:val1" -H "key2:val2" http://localhost:8080/spring-rest/ex/foos
```

请注意，对于curl语法，冒号分隔标头键和标头值，与HTTP规范中相同，而在Spring中，使用等号。

### 3.2 @RequestMapping消费和生产

控制器方法生成的映射媒体类型值得特别注意。

我们可以通过上面介绍的@RequestMapping headers属性根据其Accept标头映射请求：

```java
@RequestMapping(
      value = "/ex/foos", 
      method = GET, 
      headers = "Accept=application/json")
@ResponseBody
public String getFoosAsJsonFromBrowser() {
    return "Get some Foos with Header Old";
}
```

这种定义Accept标头的方式的匹配是灵活的，它使用contains而不是equals，因此像下面这样的请求仍然可以正确映射：

```bash
curl -H "Accept:application/json,text/html" 
  http://localhost:8080/spring-rest/ex/foos
```

从Spring 3.1开始，@RequestMapping注解现在有了produces和consumes属性，专门用于此目的：

```java
@RequestMapping(
      value = "/ex/foos", 
      method = RequestMethod.GET, 
      produces = "application/json")
@ResponseBody
public String getFoosAsJsonFromREST() {
    return "Get some Foos with Header New";
}
```

此外，从Spring 3.1开始，具有headers属性的旧映射类型将自动转换为新的produces机制，因此结果将是相同的。

这是通过curl以相同的方式使用的：

```bash
curl -H "Accept:application/json" 
  http://localhost:8080/spring-rest/ex/foos
```

此外，produces也支持多个值：

```java
@RequestMapping(
      value = "/ex/foos", 
      method = GET, 
      produces = {"application/json", "application/xml"})
```

请记住，这些指定Accept标头的旧方法和新方法，基本上是相同的映射，因此Spring不允许将它们放在一起。

激活这两种方法将导致：

```bash
Caused by: java.lang.IllegalStateException: Ambiguous mapping found. 
Cannot map 'fooController' bean method 
java.lang.String 
cn.tuyucheng.taketoday.spring.web.controller
  .FooController.getFoosAsJsonFromREST()
to 
{ [/ex/foos],
  methods=[GET],params=[],headers=[],
  consumes=[],produces=[application/json],custom=[]
}: 
There is already 'fooController' bean method
java.lang.String 
cn.tuyucheng.taketoday.spring.web.controller
  .FooController.getFoosAsJsonFromBrowser() 
mapped.
```

关于新的生产和消费机制的最后一点说明，它们的行为与大多数其他注解不同：在类型级别指定时，方法级别的注解不会补充而是覆盖类型级别的信息。

当然，如果你想更深入地了解如何使用Spring构建REST API，请查看[新的REST与Spring课程](https://www.baeldung.com/rest-with-spring-course)。

## 4. RequestMapping带路径变量

部分映射URI可以通过@PathVariable注解绑定到变量。

### 4.1 单个@PathVariable

一个带有单个路径变量的简单示例：

```java
@RequestMapping(value = "/ex/foos/{id}", method = GET)
@ResponseBody
public String getFoosBySimplePathWithPathVariable(@PathVariable("id") long id) {
    return "Get a specific Foo with id=" + id;
}
```

这可以用curl测试：

```bash
curl http://localhost:8080/spring-rest/ex/foos/1
```

如果方法参数的名称与路径变量的名称完全匹配，则可以通过使用不带值的@PathVariable来简化：

```java
@RequestMapping(value = "/ex/foos/{id}", method = GET)
@ResponseBody
public String getFoosBySimplePathWithPathVariable(@PathVariable String id) {
    return "Get a specific Foo with id=" + id;
}
```

请注意，@PathVariable受益于自动类型转换，因此我们也可以将id声明为：

```java
@PathVariable long id
```

### 4.2 多个@PathVariable

更复杂的URI可能需要将URI的多个部分映射到多个值：

```java
@RequestMapping(value = "/ex/foos/{fooid}/bar/{barid}", method = GET)
@ResponseBody
public String getFoosBySimplePathWithPathVariables (@PathVariable long fooid, @PathVariable long barid) {
    return "Get a specific Bar with id=" + barid + " from a Foo with id=" + fooid;
}
```

这很容易以相同的方式用curl进行测试：

```bash
curl http://localhost:8080/spring-rest/ex/foos/1/bar/2
```

### 4.3 @PathVariable与正则表达式

映射@PathVariable时也可以使用正则表达式。

例如，我们将限制映射只接受id的数值：

```java
@RequestMapping(value = "/ex/bars/{numericId:[d]+}", method = GET)
@ResponseBody
public String getBarsBySimplePathWithPathVariable(@PathVariable long numericId) {
    return "Get a specific Bar with id=" + numericId;
}
```

这意味着以下URI将匹配：

```bash
http://localhost:8080/spring-rest/ex/bars/1
```

但这不会：

```bash
http://localhost:8080/spring-rest/ex/bars/abc
```

## 5. 带请求参数的RequestMapping

@RequestMapping允许使用@RequestParam注解轻松映射URL参数。


我们现在将请求映射到URI：

```bash
http://localhost:8080/spring-rest/ex/bars?id=100
```

```java
@RequestMapping(value = "/ex/bars", method = GET)
@ResponseBody
public String getBarBySimplePathWithRequestParam(@RequestParam("id") long id) {
    return "Get a specific Bar with id=" + id;
}
```

然后，我们使用控制器方法签名中的@RequestParam("id")注解提取id参数的值。

要发送带有id参数的请求，我们将使用curl中的参数支持：

```bash
curl -i -d id=100 http://localhost:8080/spring-rest/ex/bars
```

在这个例子中，参数是直接绑定的，没有先声明。

对于更高级的场景，@RequestMapping可以选择定义参数作为缩小请求映射的另一种方式：

```java
@RequestMapping(value = "/ex/bars", params = "id", method = GET)
@ResponseBody
public String getBarBySimplePathWithExplicitRequestParam(@RequestParam("id") long id) {
    return "Get a specific Bar with id=" + id;
}
```

允许更灵活的映射。可以设置多个params值，并非必须使用所有参数值：

```java
@RequestMapping(
    value = "/ex/bars", 
    params = { "id", "second" }, 
    method = GET)
@ResponseBody
public String getBarBySimplePathWithExplicitRequestParams(@RequestParam("id") long id) {
    return "Narrow Get a specific Bar with id=" + id;
}
```

当然，还有对URI的请求，例如：

```bash
http://localhost:8080/spring-rest/ex/bars?id=100&second=something
```

将始终映射到最佳匹配，这是更窄的匹配，它定义了id和第二个参数。

## 6. RequestMapping边角案例

### 6.1 @RequestMapping-映射到同一控制器方法的多个路径

尽管单个@RequestMapping路径值通常用于单个控制器方法(只是良好实践，不是硬性规定)，但在某些情况下可能需要将多个请求映射到同一方法。

在这种情况下，@RequestMapping的值属性确实接受多个映射，而不仅仅是一个：

```java
@RequestMapping(
    value = {"/ex/advanced/bars", "/ex/advanced/foos"}, 
    method = GET)
@ResponseBody
public String getFoosOrBarsByPath() {
    return "Advanced - Get some Foos or Bars";
}
```

现在这两个curl命令都应该使用相同的方法：

```bash
curl -i http://localhost:8080/spring-rest/ex/advanced/foos
curl -i http://localhost:8080/spring-rest/ex/advanced/bars
```

### 6.2 @RequestMapping-同一控制器方法的多个HTTP请求方法

使用不同 HTTP 动词的多个请求可以映射到相同的控制器方法：

```java
@RequestMapping(
    value = "/ex/foos/multiple", 
    method = {RequestMethod.PUT, RequestMethod.POST}
)
@ResponseBody
public String putAndPostFoos() {
    return "Advanced - PUT and POST within single method";
}
```

使用curl，这两个现在都将使用相同的方法：

```bash
curl -i -X POST http://localhost:8080/spring-rest/ex/foos/multiple
curl -i -X PUT http://localhost:8080/spring-rest/ex/foos/multiple
```

### 6.3. @RequestMapping-所有请求的回退

要使用特定的HTTP方法为所有请求实现简单的回退，例如，对于GET：

```java
@RequestMapping(value = "*", method = RequestMethod.GET)
@ResponseBody
public String getFallback() {
    return "Fallback for GET Requests";
}
```

甚至对于所有请求：

```java
@RequestMapping(
    value = "", 
    method = { RequestMethod.GET, RequestMethod.POST ... })
@ResponseBody
public String allFallback() {
    return "Fallback for All Requests";
}
```

### 6.4 模糊映射错误

当Spring评估两个或多个请求映射对于不同的控制器方法相同时，就会发生不明确的映射错误。当具有相同的HTTP方法、URL、参数、标头和媒体类型时，请求映射是相同的。

例如，这是一个模糊映射：

```java
@GetMapping(value = "foos/duplicate" )
public String duplicate() {
    return "Duplicate";
}

@GetMapping(value = "foos/duplicate" )
public String duplicateEx() {
    return "Duplicate";
}
```

抛出的异常通常确实有以下几行错误消息：

```bash
Caused by: java.lang.IllegalStateException: Ambiguous mapping.
  Cannot map 'fooMappingExamplesController' method 
  public java.lang.String cn.tuyucheng.taketoday.web.controller.FooMappingExamplesController.duplicateEx()
  to {[/ex/foos/duplicate],methods=[GET]}:
  There is already 'fooMappingExamplesController' bean method
  public java.lang.String cn.tuyucheng.taketoday.web.controller.FooMappingExamplesController.duplicate() mapped.
```

仔细阅读错误消息指出Spring无法映射方法cn.tuyucheng.taketoday.web.controller.FooMappingExamplesController.duplicateEx()，因为它与已映射的cn.tuyucheng.taketoday.web.controller有冲突的映射.FooMappingExamplesController.duplicate()。

下面的代码片段不会导致不明确的映射错误，因为这两种方法返回不同的内容类型：

```java
@GetMapping(value = "foos/duplicate", produces = MediaType.APPLICATION_XML_VALUE)
public String duplicateXml() {
    return "<message>Duplicate</message>";
}
    
@GetMapping(value = "foos/duplicate", produces = MediaType.APPLICATION_JSON_VALUE)
public String duplicateJson() {
    return "{\"message\":\"Duplicate\"}";
}
```

这种差异化允许我们的控制器根据请求中提供的Accepts标头返回正确的数据表示 。

解决此问题的另一种方法是更新分配给所涉及的两种方法之一的URL。

## 7. 新的请求映射快捷方式

Spring Framework 4.3引入了[一些新的](https://www.baeldung.com/spring-new-requestmapping-shortcuts)HTTP映射注解，全部基于@RequestMapping：

-   @GetMapping
-   @PostMapping
-   @PutMapping
-   @DeleteMapping
-   @PatchMapping

这些新注解可以提高可读性并减少代码的冗长。

让我们通过创建一个支持CRUD操作的RESTful API来了解这些新注解的作用：

```java
@GetMapping("/{id}")
public ResponseEntity<?> getBazz(@PathVariable String id){
    return new ResponseEntity<>(new Bazz(id, "Bazz"+id), HttpStatus.OK);
}

@PostMapping
public ResponseEntity<?> newBazz(@RequestParam("name") String name){
    return new ResponseEntity<>(new Bazz("5", name), HttpStatus.OK);
}

@PutMapping("/{id}")
public ResponseEntity<?> updateBazz(@PathVariable String id, @RequestParam("name") String name) {
    return new ResponseEntity<>(new Bazz(id, name), HttpStatus.OK);
}

@DeleteMapping("/{id}")
public ResponseEntity<?> deleteBazz(@PathVariable String id){
    return new ResponseEntity<>(new Bazz(id), HttpStatus.OK);
}
```

可以[在此处](https://www.baeldung.com/spring-new-requestmapping-shortcuts)深入了解这些内容。

## 8. Spring配置

考虑到我们的FooController在以下包中定义，Spring MVC配置非常简单：

```java
package cn.tuyucheng.taketoday.spring.web.controller;

@Controller
public class FooController { ... }
```

我们只需要一个@Configuration类来启用完整的 MVC 支持并为控制器配置类路径扫描：

```java
@Configuration
@EnableWebMvc
@ComponentScan({"cn.tuyucheng.taketoday.spring.web.controller"})
public class MvcConfig {
    //
}
```

## 9. 总结

本文重点介绍了Spring中的@RequestMapping注解，讨论了一个简单的用例、HTTP标头的映射、使用@PathVariable绑定部分URI，以及使用URI参数和@RequestParam注解。

如果你想了解如何在Spring MVC中使用另一个核心注解，可以在此处探索[@ModelAttribute注解](https://www.baeldung.com/spring-mvc-and-the-modelattribute-annotation)。