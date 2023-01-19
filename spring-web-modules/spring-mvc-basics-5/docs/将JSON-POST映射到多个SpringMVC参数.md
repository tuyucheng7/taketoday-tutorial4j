## 1. 概述

当使用 Spring 对 JSON 反序列化的默认支持时，我们被迫将传入的 JSON 映射到单个请求处理程序参数。然而，有时我们更喜欢更细粒度的方法签名。

在本教程中，我们将学习如何使用自定义HandlerMethodArgumentResolver将 JSON POST 反序列化为多个强类型参数。

## 2.问题

首先，让我们看一下Spring MVC默认的 JSON 反序列化方法的局限性。

### 2.1. 默认的 @RequestBody行为

让我们从一个示例 JSON 正文开始：

```json
{
   "firstName" : "John",
   "lastName"  :"Smith",
   "age" : 10,
   "address" : {
      "streetName" : "Example Street",
      "streetNumber" : "10A",
      "postalCode" : "1QW34",
      "city" : "Timisoara",
      "country" : "Romania"
   }
}
```

接下来，让我们创建与 JSON 输入匹配的[DTO](https://www.baeldung.com/java-dto-pattern)：

```java
public class UserDto {
    private String firstName;
    private String lastName;
    private String age;
    private AddressDto address;

    // getters and setters
}
public class AddressDto {

    private String streetName;
    private String streetNumber;
    private String postalCode;
    private String city;
    private String country;

    // getters and setters
}
```

最后，我们将使用[标准方法](https://www.baeldung.com/spring-mvc-send-json-parameters) 使用@RequestBody注解将我们的 JSON 请求反序列化为UserDto：

```java
@Controller
@RequestMapping("/user")
public class UserController {

    @PostMapping("/process")
    public ResponseEntity process(@RequestBody UserDto user) {
        / business processing /
        return ResponseEntity.ok()
            .body(user.toString());
    }
}
```

### 2.2. 限制

上述标准解决方案的主要好处是我们不必手动将 JSON POST 反序列化为 UserDto 对象。

但是，整个 JSON POST 必须映射到单个请求参数。这意味着我们必须为每个预期的 JSON 结构创建一个单独的 POJO，用专门用于此目的的类污染我们的代码库。

当我们只需要 JSON 属性的一个子集时，这种结果尤为明显。在我们上面的请求处理程序中，我们只需要用户的firstName和 city属性，但我们不得不反序列化整个UserDto。

虽然 Spring 允许我们使用Map或 ObjectNode作为参数而不是自己开发的 DTO，但两者都是单参数选项。与 DTO 一样，所有内容都打包在一起。由于Map和ObjectNode内容是String值，我们必须自己将它们编组为对象。这些选项使我们免于声明一次性 DTO，但产生了更多的复杂性。

## 3.自定义HandlerMethodArgumentResolver

让我们看一下解决上述限制的方法。我们可以使用Spring MVC的 HandlerMethodArgumentResolver来允许我们将所需的 JSON 属性声明为请求处理程序中的参数。

### 3.1. 创建控制器

首先，让我们创建一个自定义注解，我们可以使用它来将请求处理程序参数映射到 JSON 路径：

```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonArg {
    String value() default "";
}
```

接下来，我们将创建一个请求处理程序，它使用注解将firstName和city映射为与 JSON POST 正文中的属性相关的单独参数：

```java
@Controller
@RequestMapping("/user")
public class UserController {
    @PostMapping("/process/custom")
    public ResponseEntity process(@JsonArg("firstName") String firstName,
      @JsonArg("address.city") String city) {
        / business processing /
        return ResponseEntity.ok()
            .body(String.format("{"firstName": %s, "city" : %s}", firstName, city));
    }
}
```

### 3.2. 创建自定义 HandlerMethodArgumentResolver

在Spring MVC决定哪个请求处理程序应该处理传入请求后，它会尝试自动解析参数。这包括遍历 Spring 上下文中实现HandlerMethodArgumentResolver接口的所有 bean，以防可以解析Spring MVC无法自动执行的任何参数。

让我们定义一个HandlerMethodArgumentResolver的实现，它将处理所有用@JsonArg注解的请求处理程序参数：

```java
public class JsonArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String JSON_BODY_ATTRIBUTE = "JSON_REQUEST_BODY";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JsonArg.class);
    }

    @Override
    public Object resolveArgument(
      MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) 
      throws Exception {
        String body = getRequestBody(webRequest);
        String jsonPath = Objects.requireNonNull(
          Objects.requireNonNull(parameter.getParameterAnnotation(JsonArg.class)).value());
        Class<?> parameterType = parameter.getParameterType();
        return JsonPath.parse(body).read(jsonPath, parameterType);
    }

    private String getRequestBody(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = Objects.requireNonNull(
          webRequest.getNativeRequest(HttpServletRequest.class));
        String jsonBody = (String) servletRequest.getAttribute(JSON_BODY_ATTRIBUTE);
        if (jsonBody == null) {
            try {
                jsonBody = IOUtils.toString(servletRequest.getInputStream());
                servletRequest.setAttribute(JSON_BODY_ATTRIBUTE, jsonBody);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return jsonBody;
    }
}
```

Spring 使用supportsParameter()方法来检查这个类是否可以解析给定的参数。由于我们希望我们的处理程序处理任何用@JsonArg注解的参数，如果给定参数具有该注解，我们将返回true 。

接下来，在resolveArgument()方法中，我们提取 JSON 主体，然后将其作为属性附加到请求中，以便我们可以直接访问它以进行后续调用。然后我们从@JsonArg注解中获取 JSON 路径并使用反射来获取参数的类型。通过 JSON 路径和参数类型信息，我们可以将 JSON 主体的离散部分反序列化为丰富的对象。

### 3.3. 注册自定义HandlerMethodArgumentResolver

为了让Spring MVC使用我们的JsonArgumentResolver，我们需要注册它：

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        JsonArgumentResolver jsonArgumentResolver = new JsonArgumentResolver();
        argumentResolvers.add(jsonArgumentResolver);
    }
}
```

我们的JsonArgumentResolver现在将处理所有用@JsonArgs注解的请求处理程序参数。我们需要确保@JsonArgs值是一个有效的 JSON 路径，但与@RequestBody方法相比，这是一个更轻的过程，后者需要为每个 JSON 结构提供一个单独的 POJO。

### 3.4. 将参数与自定义类型一起使用

为了证明这也适用于自定义Java类，让我们定义一个带有强类型 POJO 参数的请求处理程序：

```java
@PostMapping("/process/custompojo")
public ResponseEntity process(
  @JsonArg("firstName") String firstName, @JsonArg("lastName") String lastName,
  @JsonArg("address") AddressDto address) {
    / business processing /
    return ResponseEntity.ok()
      .body(String.format("{"firstName": %s, "lastName": %s, "address" : %s}",
        firstName, lastName, address));
}
```

我们现在可以将AddressDto映射为单独的参数。

### 3.5. 测试自定义JsonArgumentResolver

让我们编写一个测试用例来证明 JsonArgumentResolver按预期工作：

```java
@Test
void whenSendingAPostJSON_thenReturnFirstNameAndCity() throws Exception {

    String jsonString = "{"firstName":"John","lastName":"Smith","age":10,"address":{"streetName":"Example Street","streetNumber":"10A","postalCode":"1QW34","city":"Timisoara","country":"Romania"}}";
    
    mockMvc.perform(post("/user/process/custom").content(jsonString)
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("John"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.city").value("Timisoara"));
}
```

接下来，让我们编写一个测试，我们调用第二个端点将 JSON 直接解析为 POJO：

```java
@Test
void whenSendingAPostJSON_thenReturnUserAndAddress() throws Exception {
    String jsonString = "{"firstName":"John","lastName":"Smith","address":{"streetName":"Example Street","streetNumber":"10A","postalCode":"1QW34","city":"Timisoara","country":"Romania"}}";
    ObjectMapper mapper = new ObjectMapper();
    UserDto user = mapper.readValue(jsonString, UserDto.class);
    AddressDto address = user.getAddress();

    String mvcResult = mockMvc.perform(post("/user/process/custompojo").content(jsonString)
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();

    assertEquals(String.format("{"firstName": %s, "lastName": %s, "address" : %s}",
      user.getFirstName(), user.getLastName(), address), mvcResult);
}
```

## 4. 总结

在本文中，我们了解了Spring MVC默认反序列化行为中的一些限制，然后学习了如何使用自定义HandlerMethodArgumentResolver来克服它们。