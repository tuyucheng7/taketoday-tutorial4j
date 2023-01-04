## 1. 简介

在本快速教程中，我们将说明如何使用Spring的[RestTemplate](https://www.baeldung.com/rest-template)来使POST请求发送JSON内容。


## 2. 设置示例

让我们首先添加一个简单的Person模型类来表示要发布的数据：

```java
public class Person {
    private Integer id;
    private String name;

    // standard constructor, getters, setters
}
```

要使用Person对象，我们将使用两种方法添加PersonService接口和实现：

```java
public interface PersonService {
    Person saveUpdatePerson(Person person);
    Person findPersonById(Integer id);
}
```

这些方法的实现将简单地返回一个对象。我们在这里使用该层的虚拟实现，因此我们可以专注于Web层。

## 3. REST API设置

让我们为我们的Person类定义一个简单的REST API：

```java
@PostMapping(value = "/createPerson", consumes = "application/json", produces = "application/json")
public Person createPerson(@RequestBody Person person) {
    return personService.saveUpdatePerson(person);
}

@PostMapping(value = "/updatePerson", consumes = "application/json", produces = "application/json")
public Person updatePerson(@RequestBody Person person, HttpServletResponse response) {
    response.setHeader("Location", ServletUriComponentsBuilder.fromCurrentContextPath()
        .path("/findPerson/" + person.getId()).toUriString());
    
    return personService.saveUpdatePerson(person);
}
```

请记住，我们要以JSON格式发布数据。为此，我们在@PostMapping注解中添加了consumes属性，两种方法的值为“application/json”。

同样，我们将produces属性设置为“application/json”以告诉Spring我们需要JSON格式的响应主体。

我们为这两种方法都使用[@RequestBody](https://www.baeldung.com/spring-request-response-body)注解对person参数进行了注解。这将告诉Spring person对象将绑定到HTTP请求的主体。

最后，这两种方法都返回一个将绑定到响应主体的Person对象。请注意，我们将使用@RestController注解我们的API类，以使用隐藏的[@ResponseBody](https://www.baeldung.com/spring-request-response-body)注解来标注所有API方法。

## 4. 使用RestTemplate

现在我们可以编写一些单元测试来测试我们的Person REST API。在这里，我们将尝试使用RestTemplate提供的POST方法向Person API发送POST请求：postForObject、postForEntity和postForLocation。

在我们开始实施单元测试之前，让我们定义一个设置方法来初始化我们将在所有单元测试方法中使用的对象：

```java
@BeforeClass
public static void runBeforeAllTestMethods() {
    createPersonUrl = "http://localhost:8082/spring-rest/createPerson";
    updatePersonUrl = "http://localhost:8082/spring-rest/updatePerson";

    restTemplate = new RestTemplate();
    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    personJsonObject = new JSONObject();
    personJsonObject.put("id", 1);
    personJsonObject.put("name", "John");
}
```

除了此设置方法外，请注意，我们将在单元测试中引用以下映射器将JSON String转换为JSONNode对象：

```java
private final ObjectMapper objectMapper = new ObjectMapper();
```

如前所述，我们希望以JSON格式发布数据。为此，我们将使用APPLICATION_JSON媒体类型向我们的请求添加一个Content-Type标头。

Spring的HttpHeaders类提供了不同的方法来访问标头。在这里，我们通过调用setContentType方法将Content-Type标头设置为application/json。我们会将标头对象附加到我们的请求中。

### 4.1 使用postForObject发布JSON

RestTemplate的postForObject方法通过将对象发布到给定的URI模板来创建新资源。它返回自动转换为responseType参数中指定类型的结果。

假设我们想向我们的Person API发出POST请求以创建一个新的Person对象并在响应中返回这个新创建的对象。

首先，我们将基于personJsonObject和包含Content-Type的标头构建HttpEntity类型的请求对象。这允许postForObject方法发送JSON请求正文：

```java
@Test
public void givenDataIsJson_whenDataIsPostedByPostForObject_thenResponseBodyIsNotNull() throws IOException {
    HttpEntity<String> request = new HttpEntity<String>(personJsonObject.toString(), headers);
    
    String personResultAsJsonStr = restTemplate.postForObject(createPersonUrl, request, String.class);
    JsonNode root = objectMapper.readTree(personResultAsJsonStr);
    
    assertNotNull(personResultAsJsonStr);
    assertNotNull(root);
    assertNotNull(root.path("name").asText());
}
```

postForObject()方法将响应主体作为String类型返回。

我们还可以通过设置responseType参数将响应作为Person对象返回：

```java
Person person = restTemplate.postForObject(createPersonUrl, request, Person.class);

assertNotNull(person);
assertNotNull(person.getName());
```

实际上，我们的请求处理程序方法与createPersonUrl URI匹配会生成JSON格式的响应主体。

但这对我们来说不是限制，postForObject能够自动将响应主体转换为在responseType参数中指定的请求的Java类型(例如String、Person)。

### 4.2 使用postForEntity发布JSON

与postForObject()相比，postForEntity()将响应作为[ResponseEntity](https://www.baeldung.com/spring-response-entity)对象返回。除此之外，这两种方法都做同样的工作。

假设我们想向我们的Person API发出POST请求以创建一个新的Person对象并将响应作为ResponseEntity返回。

我们可以使用postForEntity方法来实现这个：

```java
@Test
public void givenDataIsJson_whenDataIsPostedByPostForEntity_thenResponseBodyIsNotNull() throws IOException {
    HttpEntity<String> request = new HttpEntity<String>(personJsonObject.toString(), headers);
    
    ResponseEntity<String> responseEntityStr = restTemplate.postForEntity(createPersonUrl, request, String.class);
    JsonNode root = objectMapper.readTree(responseEntityStr.getBody());
 
    assertNotNull(responseEntityStr.getBody());
    assertNotNull(root.path("name").asText());
}
```

与postForObject类似，postForEntity具有responseType参数，用于将响应主体转换为请求的Java类型。

在这里，我们能够将响应主体作为ResponseEntity<String\>返回。

我们还可以通过将responseType参数设置为Person.class来将响应作为ResponseEntity<Person\>对象返回：

```java
ResponseEntity<Person> responseEntityPerson = restTemplate.postForEntity(createPersonUrl, request, Person.class);
 
assertNotNull(responseEntityPerson.getBody());
assertNotNull(responseEntityPerson.getBody().getName());
```

### 4.3 使用postForLocation发布JSON

与postForObject和postForEntity方法类似，postForLocation也通过将给定对象发布到给定URI来创建新资源。唯一的区别是它返回Location标头的值。

请记住，我们已经在上面的updatePerson REST API方法中看到了如何设置响应的Location标头：

```java
response.setHeader("Location", ServletUriComponentsBuilder.fromCurrentContextPath()
    .path("/findPerson/" + person.getId()).toUriString());
```

现在假设我们想要在更新我们发布的人员对象后返回响应的Location 标头。

我们可以使用postForLocation方法来实现：

```java
@Test
public void givenDataIsJson_whenDataIsPostedByPostForLocation_thenResponseBodyIsTheLocationHeader()throws JsonProcessingException {
    HttpEntity<String> request = new HttpEntity<String>(personJsonObject.toString(), headers);
    URI locationHeader = restTemplate.postForLocation(updatePersonUrl, request);
    
    assertNotNull(locationHeader);
}
```

## 5. 总结

在本文中，我们探讨了如何使用RestTemplate来使用JSON发出POST请求。