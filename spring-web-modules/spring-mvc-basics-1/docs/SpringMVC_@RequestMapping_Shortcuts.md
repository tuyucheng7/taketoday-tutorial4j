## 1. 概述

Spring 4.3引入了一些非常方便的方法级组合注解来平滑典型Spring MVC项目中的@RequestMapping处理。

在本文中，我们将学习如何有效地使用它们。

## 2. 新注解

通常，如果我们想使用传统的@RequestMapping注解来实现URL处理程序，它应该是这样的：

```text
@RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
```

新方法可以将其简化为：

```text
@GetMapping("/get/{id}")
```

Spring目前支持五种类型的内置注解，用于处理不同类型的HTTP请求方法，即GET、POST、PUT、DELETE和PATCH。对应的注解为：

+ @GetMapping
+ @PostMapping
+ @PutMapping
+ @DeleteMapping
+ @PatchMapping

从命名约定我们可以看出，每个注解都是为了处理各自传入的请求方法类型，即@GetMapping用于处理GET类型的请求方法，@PostMapping用于处理POST类型的请求方法等。

## 3. 工作原理

以上所有注解都已经在内部使用@RequestMapping和method属性的相应值进行了标注。

例如，如果我们查看@GetMapping注解的源代码，我们可以看到它已经通过以下方式使用RequestMethod.GET进行了注解：

```java

@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping(method = {RequestMethod.GET})
public @interface GetMapping {
    // abstract codes
}
```

所有其他注解都以相同的方式创建，即@PostMapping使用RequestMethod.POST进行标注，@PutMapping使用RequestMethod.PUT进行标注等。

## 4. 实现

接下来我们使用这些注解来构建一个简单的REST应用程序。

请注意，由于我们将使用Maven构建项目并使用Spring MVC来创建我们的应用程序，因此我们需要在pom.xml中添加必要的依赖项：

```xml

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>5.3.13</version>
</dependency>
```

现在，我们需要创建控制器来映射传入的请求URL。在这个控制器中，我们将一一使用所有这些注解。

### 4.1 @GetMapping

```java

@RestController
public class RequestMappingShortcutsController {

    @GetMapping("/get")
    public @ResponseBody ResponseEntity<String> get() {
        return new ResponseEntity<>("GET Response", HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public @ResponseBody ResponseEntity<String> getById(@PathVariable String id) {
        return new ResponseEntity<>("GET Response : " + id, HttpStatus.OK);
    }
}
```

### 4.2 @PostMapping

```java

@RestController
public class RequestMappingShortcutsController {

    @PostMapping("/post")
    public @ResponseBody ResponseEntity<String> post() {
        return new ResponseEntity<>("POST Response", HttpStatus.OK);
    }
}
```

### 4.3 @PutMapping

```java

@RestController
public class RequestMappingShortcutsController {

    @PutMapping("/put")
    public @ResponseBody ResponseEntity<String> put() {
        return new ResponseEntity<>("PUT Response", HttpStatus.OK);
    }
}
```

### 4.4 @DeleteMapping

```java

@RestController
public class RequestMappingShortcutsController {

    @DeleteMapping("/delete")
    public @ResponseBody ResponseEntity<String> delete() {
        return new ResponseEntity<>("DELETE Response", HttpStatus.OK);
    }
}
```

### 4.5 @PatchMapping

```java

@RestController
public class RequestMappingShortcutsController {

    @PatchMapping("/patch")
    public @ResponseBody ResponseEntity<String> patch() {
        return new ResponseEntity<>("PATCH Response", HttpStatus.OK);
    }
}
```

**注意点**：

+ 我们使用了必要的注解来处理对应的URL请求方式。例如，@GetMapping处理“/get” URI，@PostMapping处理“/post” URI等等
+ 由于我们构建的是一个基于REST的应用程序，因此我们返回一个常量字符串(对于每种请求类型都是唯一的)以简化应用程序，响应状态码为200。
  在这种情况下，我们使用了Spring的@ResponseBody注解。
+ 如果我们必须处理任何URL路径变量，我们可以在使用@RequestMapping时使用的更少的方法来处理。

## 5. 测试

要测试应用程序，我们需要使用JUnit创建几个测试用例。我们将使用MockMvc来模拟请求的发送和响应，
并创建五个不同的测试用例来测试我们在控制器中声明的每个注解和每个处理程序。

下面是针对@GetMapping注解的测试用例：

```java
class RequestMappingShortcutsIntegrationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new RequestMappingShortcutsController()).build();
    }

    @Test
    void givenUrl_whenGetRequest_thenFindGetResponse() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/get");

        ResultMatcher contentMatcher = MockMvcResultMatchers.content()
                .string("GET Response");

        this.mockMvc.perform(builder)
                .andExpect(contentMatcher)
                .andExpect(status().isOk());
    }
}
```

如我们所见，如果我们访问GET URL “/get”，我们期望响应一个常量字符串“GET Response”。

下面是针对@PostMapping的测试用例：

```java
class RequestMappingShortcutsIntegrationTest {

    @Test
    void givenUrl_whenPostRequest_thenFindPostResponse() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/post");

        ResultMatcher contentMatcher = MockMvcResultMatchers.content()
                .string("POST Response");

        this.mockMvc.perform(builder)
                .andExpect(contentMatcher)
                .andExpect(status().isOk());
    }
}
```

完整的代码包含其余的测试用例来测试所有的HTTP方法。

或者，我们可以使用任何常见的REST客户端，例如Postman、RESTClient等来测试我们的应用程序。
在这种情况下，我们在使用REST客户端时需要小心地选择正确的HTTP方法类型。否则，它会抛出405错误状态码。

## 6. 总结

在本文中，我们简要介绍了使用传统Spring MVC框架进行快速Web开发的不同类型的@RequestMapping快捷注解。
我们可以利用这些注解使我们的代码更加的清爽。