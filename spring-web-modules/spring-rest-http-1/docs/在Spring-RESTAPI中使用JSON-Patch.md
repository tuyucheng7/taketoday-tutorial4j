## 1. 简介

在可用的各种HTTP方法中，HTTP PATCH方法起着独特的作用。它允许我们对HTTP资源应用部分更新。

在本教程中，我们将了解如何使用HTTP PATCH方法和JSON补丁文档格式对我们的RESTful资源应用部分更新。

## 2. 用例

让我们首先考虑一个由JSON文档表示的示例HTTP客户资源：

```json
{ 
    "id":"1",
    "telephone":"001-555-1234",
    "favorites":["Milk","Eggs"],
    "communicationPreferences": {"post":true, "email":true}
}
```

假设此客户的电话号码已更改，并且客户将新项目添加到他们最喜欢的产品列表中。这意味着我们只需要更新Customer的telephone和favorites字段。

我们该怎么做？

首先想到流行的HTTP PUT方法。但是，由于PUT完全替换了资源，因此它不是优雅地应用部分更新的合适方法。此外，客户端必须在应用和保存更新之前执行GET。

这就是[HTTP PATCH](https://tools.ietf.org/html/rfc5789#page-8)方法派上用场的地方。

让我们了解HTTP PATCH方法和JSON补丁格式。

## 3. HTTP PATCH方法和JSON补丁格式

HTTP PATCH方法提供了一种对资源应用部分更新的好方法。因此，客户只需发送请求中的差异。

让我们看一个简单的HTTP PATCH请求示例：

```http request
PATCH /customers/1234 HTTP/1.1
Host: www.example.com
Content-Type: application/example
If-Match: "e0023aa4e"
Content-Length: 100

[description of changes]
```

HTTP PATCH请求正文描述了应该如何修改目标资源以生成新版本。此外，用于表示[更改说明]的格式因资源类型而异。对于JSON资源类型，用于描述变化的格式是[JSON Patch](https://tools.ietf.org/html/rfc6902)。

简单地说，JSON Patch格式使用“一系列操作”来描述目标资源应该如何修改。JSON补丁文档是一组JSON对象。数组中的每个对象代表一个JSON Patch操作。

现在让我们看看JSON补丁操作以及一些示例。

## 4. JSON补丁操作

JSON Patch操作由单个操作对象表示。

例如，这里我们定义了一个JSON补丁操作来更新客户的电话号码：

```json
{
    "op":"replace",
    "path":"/telephone",
    "value":"001-555-5678"
}
```

每个操作必须有一个路径成员。此外，某些操作对象还必须包含from成员。path和from成员的值是一个[JSON Pointer](https://tools.ietf.org/html/rfc6901)。它指的是目标文档中的位置。该位置可以指向目标对象中的特定键或数组元素。

现在让我们简要地看一下可用的JSON补丁操作。

### 4.1 添加操作

我们使用添加操作向对象添加新成员。此外，我们可以使用它来更新现有成员并将新值插入到指定索引处的数组中。

例如，让我们在索引0处将“Bread”添加到客户的收藏夹列表中：

```json
{
    "op":"add",
    "path":"/favorites/0",
    "value":"Bread"
}
```

添加操作后修改后的客户详细信息为：

```json
{
    "id":"1",
    "telephone":"001-555-1234",
    "favorites":["Bread","Milk","Eggs"],
    "communicationPreferences": {"post":true, "email":true}
}
```

### 4.2 删除操作

删除操作删除目标位置的值。此外，它可以从指定索引处的数组中删除一个元素。

例如，让我们为客户删除communcationPreferences：

```json
{
    "op":"remove",
    "path":"/communicationPreferences"
}
```

删除操作后修改后的客户详细信息为：

```json
{
    "id":"1",
    "telephone":"001-555-1234",
    "favorites":["Bread","Milk","Eggs"],
    "communicationPreferences":null
}
```

### 4.3 替换操作

替换操作使用新值更新目标位置的值。

例如，让我们更新客户的电话号码：

```json
{
    "op":"replace",
    "path":"/telephone",
    "value":"001-555-5678"
}
```

替换操作后修改后的客户详细信息将是：

```json
{ 
    "id":"1", 
    "telephone":"001-555-5678", 
    "favorites":["Bread","Milk","Eggs"], 
    "communicationPreferences":null
}
```

### 4.4 移动操作

移动操作删除指定位置的值并将其添加到目标位置。

例如，让我们将“Bread”从客户最喜欢的列表顶部移到列表底部：

```json
{
    "op":"move",
    "from":"/favorites/0",
    "path":"/favorites/-"
}
```

移动操作后修改后的客户详细信息为：

```json
{ 
    "id":"1", 
    "telephone":"001-555-5678", 
    "favorites":["Milk","Eggs","Bread"], 
    "communicationPreferences":null
}

```

上例中的/favorites/0和/favorites/-是指向收藏夹数组开始和结束索引的JSON指针。

### 4.5 复制操作

操作将指定位置的值到目标位置。

例如，让我们在收藏夹列表中“Milk”：

```json
{
    "op":"copy",
    "from":"/favorites/0",
    "path":"/favorites/-"
}
```

操作后修改后的客户详细信息为：

```json
{ 
    "id":"1", 
    "telephone":"001-555-5678", 
    "favorites":["Milk","Eggs","Bread","Milk"], 
    "communicationPreferences":null
}
```

### 4.6 测试操作

测试操作测试“路径”处的值是否等于“值”。因为PATCH操作是原子的，所以如果任何操作失败，则PATCH应该被丢弃。测试操作可用于验证前置条件和后置条件是否已得到满足。

例如，让我们测试对客户电话字段的更新是否成功：

```json
{
    "op":"test", 
    "path":"/telephone",
    "value":"001-555-5678"
}
```

现在让我们看看如何将上述概念应用到我们的示例中。

## 5. 使用JSON补丁格式的HTTP PATCH请求

我们将重新审视我们的客户用例。

以下是使用JSON补丁格式对客户的电话和收藏夹列表执行部分更新的HTTP PATCH请求：

```bash
curl -i -X PATCH http://localhost:8080/customers/1 -H "Content-Type: application/json-patch+json" -d '[
    {"op":"replace","path":"/telephone","value":"+1-555-56"},
    {"op":"add","path":"/favorites/0","value":"Bread"}
]'

```

最重要的是，JSON补丁请求的Content-Type是application/json-patch+json。此外，请求正文是一个JSON补丁操作对象数组：

```json
[
    {"op":"replace","path":"/telephone","value":"+1-555-56"},
    {"op":"add","path":"/favorites/0","value":"Bread"}
]
```

我们如何在服务器端处理这样的请求？

一种方法是编写一个自定义框架，按顺序评估操作并将它们作为一个原子单元应用于目标资源。显然，这种方法听起来很复杂。此外，它还可能导致使用非标准化的补丁文件方式。

幸运的是，我们不必手工处理JSON补丁请求。

[最初在JSR 353](https://jcp.org/en/jsr/detail?id=353)中定义的用于JSONProcessing 1.0或JSON-P 1.0的JavaAPI在[JSR 374](https://www.jcp.org/en/jsr/detail?id=374)中引入了对JSON补丁的支持。JSON-P API提供了JsonPatch类型来表示JSONPatch实现。

然而，JSON-P只是一个API。要使用JSON-P API，我们需要使用一个实现它的库。对于本文中的示例，我们将使用一个名为[json-patch](https://github.com/java-json-tools/json-patch)的此类库。

现在让我们看看如何使用上述JSON补丁格式构建一个使用HTTP PATCH请求的REST服务。

## 6. 在Spring Boot应用程序中实现JSON补丁

### 6.1 依赖关系

可以从Maven中央存储库中找到最新版本的[json-patch](https://search.maven.org/artifact/com.tananaev/json-patch)。

首先，让我们将依赖项添加到pom.xml：

```java
<dependency>
    <groupId>com.github.java-json-tools</groupId>
    <artifactId>json-patch</artifactId>
    <version>1.12</version>
</dependency>
```

现在，让我们定义一个模式类来表示客户JSON文档：

```java
public class Customer {
    private String id;
    private String telephone;
    private List<String> favorites;
    private Map<String, Boolean> communicationPreferences;

    // standard getters and setters
}
```

接下来，我们将看看我们的控制器方法。

### 6.2 REST控制器方法

然后，我们可以为我们的客户用例实施HTTP PATCH：

```java
@PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
public ResponseEntity<Customer> updateCustomer(@PathVariable String id, @RequestBody JsonPatch patch) {
    try {
        Customer customer = customerService.findCustomer(id).orElseThrow(CustomerNotFoundException::new);
        Customer customerPatched = applyPatchToCustomer(patch, customer);
        customerService.updateCustomer(customerPatched);
        return ResponseEntity.ok(customerPatched);
    } catch (JsonPatchException | JsonProcessingException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    } catch (CustomerNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

```

现在让我们了解此方法中发生的事情：

-   首先，我们使用@PatchMapping注解将该方法标记为PATCH处理程序方法
-   当带有application/json-patch+json “Content-Type”的补丁请求到达时，Spring Boot使用默认的MappingJackson2HttpMessageConverter将请求负载转换为JsonPatch实例。因此，我们的控制器方法将接收请求主体作为JsonPatch实例

在方法中：

1.  首先我们调用customerService.findCustomer(id)方法查找客户记录
2.  随后，如果找到客户记录，我们将调用applyPatchToCustomer(patch, customer)方法。这会将JsonPatch应用于客户(稍后会详细介绍)
3.  然后我们调用customerService.updateCustomer(customerPatched)来保存客户记录
4.  最后，我们向客户端返回200 OK响应，并在响应中包含修补后的客户详细信息

最重要的是，真正的魔法发生在applyPatchToCustomer(patch, customer)方法中：

```java
private Customer applyPatchToCustomer(JsonPatch patch, Customer targetCustomer) throws JsonPatchException, JsonProcessingException {
    JsonNode patched = patch.apply(objectMapper.convertValue(targetCustomer, JsonNode.class));
    return objectMapper.treeToValue(patched, Customer.class);
}
```

1.  首先，我们有JsonPatch实例，其中包含要应用于目标客户的操作列表
2.  然后，我们将目标Customer转换为com.fasterxml.jackson.databind.JsonNode的实例，并将其传递给JsonPatch.apply方法以应用补丁。在幕后，JsonPatch.apply处理将操作应用于目标。补丁的结果也是一个com.fasterxml.jackson.databind.JsonNode实例
3.  然后我们调用objectMapper.treeToValue方法，它将修补后的com.fasterxml.jackson.databind.JsonNode中的数据绑定到Customer类型。这是我们打过补丁的Customer实例
4.  最后，我们返回修补后的Customer实例

现在让我们对我们的API运行一些测试。

### 6.3 测试

首先，让我们使用对API的POST请求创建一个客户：

```bash
curl -i -X POST http://localhost:8080/customers -H "Content-Type: application/json" 
  -d '{"telephone":"+1-555-12","favorites":["Milk","Eggs"],"communicationPreferences":{"post":true,"email":true}}'
```

我们收到201 Created响应：

```bash
HTTP/1.1 201
Location: http://localhost:8080/customers/1
```

Location响应标头设置为新资源的位置。表示新Customer的id为1。

接下来，让我们使用PATCH请求向该客户请求部分更新：

```bash
curl -i -X PATCH http://localhost:8080/customers/1 -H "Content-Type: application/json-patch+json" -d '[
    {"op":"replace","path":"/telephone","value":"+1-555-56"}, 
    {"op":"add","path":"/favorites/0","value": "Bread"}
]'
```

我们收到带有修补的客户详细信息的200 OK响应：

```bash
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 14 Feb 2020 21:23:14 GMT

{"id":"1","telephone":"+1-555-56","favorites":["Bread","Milk","Eggs"],"communicationPreferences":{"post":true,"email":true}}
```

## 7. 总结

在本文中，我们研究了如何在Spring REST API中实现JSON补丁。

首先，我们研究了HTTP PATCH方法及其执行部分更新的能力。

然后我们研究了什么是JSONPatch并了解了各种JSONPatch操作。

最后，我们讨论了如何使用json-patch库在Spring Boot应用程序中处理HTTP PATCH请求。