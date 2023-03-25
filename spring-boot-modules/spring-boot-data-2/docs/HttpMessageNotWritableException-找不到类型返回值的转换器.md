## 1. 概述

在本教程中，我们将阐明Spring中的HttpMessageNotWritableException：“No converter found for return value of type”异常。

首先，我们将解释该异常的主要原因。然后，我们将更深入地了解如何使用实际示例重现它，并最终了解如何修复它。

## 2. 原因

通常，当Spring无法获取返回对象的属性时，会发生此异常。

此异常的最典型原因通常是**返回的对象没有任何用于访问其属性的公共getter方法**。

默认情况下，Spring Boot依赖于[Jackson库](https://www.baeldung.com/jackson)来完成序列化/反序列化请求和响应对象的所有繁重工作。

因此，**该异常的另一个常见原因可能是缺少或使用了错误的Jackson依赖版本**。

简而言之，解决此类异常的一般准则是检查是否存在：

+ 默认构造函数
+ Getter方法
+ Jackson依赖

注意，[异常类型](https://github.com/spring-projects/spring-framework/commit/c32299f734521e907585de50d70a46dcd8018f13#diff-ea43e736983102ff93143d5a8e5a0e63837233bafa3a5f8bae78256211ed9113)已从java.lang.IllegalArgumentException更改为org.springframework.http.converter.HttpMessageNotWritableException。

## 3. 示例

现在，让我们看一个生成org.springframework.http.converter.HttpMessageNotWritableException：“No converter found for return value of type”的示例。

为了演示一个真实的用例，我们将使用[Spring Boot](https://www.baeldung.com/spring-boot)构建一个用于学生管理的基本REST API。

首先，**让我们创建模型类Student并假装忘记生成getter方法**：

```java
public class Student {
    private int id;
    private String firstName;
    private String lastName;
    private String grade;

    public Student() {
    }

    public Student(int id, String firstName, String lastName, String grade) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.grade = grade;
    }

    // setter ...
}
```

其次，我们将创建一个带有单个处理程序方法的[Spring控制器](https://www.baeldung.com/spring-controllers)，通过id检索Student对象：

```java
@RestController
@RequestMapping(value = "/api")
public class StudentRestController {

    @GetMapping("/student/{id}")
    public ResponseEntity<Student> get(@PathVariable("id") int id) {
        return ResponseEntity.ok(new Student(id, "John", "Wiliams", "AA"));
    }
}
```

现在，如果我们使用[CURL](https://www.baeldung.com/curl-rest)向[http://localhost:8080/api/student/1](http://localhost:8080/api/student/1)发送请求：

```shell
curl http://localhost:8080/api/student/1
```

将返回以下响应：

```json
{
    "timestamp": "2022-02-14T14:54:19.426+00:00",
    "status": 500,
    "error": "Internal Server Error",
    "message": "",
    "path": "/api/student/1"
}
```

查看日志，Spring抛出了HttpMessageNotWritableException：

```shell
[org.springframework.http.converter.HttpMessageNotWritableException: No converter found for return value of type: class cn.tuyucheng.boot.noconverterfound.model.Student]
```

最后，让我们创建一个测试用例，看看当Student类中没有定义getter方法时Spring的行为：

```java
@ExtendWith(SpringExtension.class)
@WebMvcTest(StudentRestController.class)
class NoConverterFoundIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Disabled("Remove Getters from Student class to successfully run this test case")
    void whenGettersNotDefined_thenThrowException() throws Exception {
        String url = "/api/student/1";

        this.mockMvc.perform(get(url))
              .andExpect(status().isInternalServerError())
              .andExpect(result -> assertThat(result.getResolvedException())
                    .isInstanceOf(HttpMessageNotWritableException.class))
              .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                    .contains("No converter found for return value of type"));
    }
}
```

## 4. 解决方案

防止该异常的最常见解决方案之一是**为我们想要以JSON返回的每个对象的属性定义一个getter方法**。

因此，让我们在Student类中添加getter方法，并创建一个新的测试用例来验证一切是否按预期工作：

```java
@Test
void whenGettersAreDefined_thenReturnObject() throws Exception {
    String url = "/api/student/2";

    this.mockMvc.perform(get(url))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("John"));
}
```

一个不明智的解决方案是将模型类的属性定义为public。但是，这不是100%安全的方法，因为它违背了一些最佳实践。

## 5. 总结

在这篇简短的文章中，我们解释了导致Spring抛出org.springframework.http.converter.HttpMessageNotWritableException：“No converter found for return value of type”的原因。

然后，我们讨论了如何重现该异常以及如何在实践中解决它。