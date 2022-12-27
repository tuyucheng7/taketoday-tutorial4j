## 1. 概述

在本快速教程中，我们将讨论**如何从Spring Boot测试中排除自动配置类**。

Spring Boot的自动配置功能非常方便，因为它为我们处理了很多初始设置。但是，如果我们不希望某个自动配置干扰我们对模块的测试，这也可能是测试期间的一个问题。

这方面的一个常见示例是安全自动配置，我们也将在我们的例子中使用它。

## 2. 测试示例

首先，我们来看一下我们的测试示例。

**我们拥有一个带有简单主页的Spring Boot Security应用程序**，当我们尝试在没有身份验证的情况下访问主页时，响应w为“401 UNAUTHORIZED”。

我们可以在测试中通过使用RestAssured进行调用看到这一点：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AutoConfigIntegrationTest {
    @LocalServerPort
    private int port;

    @Test
    void givenNoAuthentication_whenAccessHome_thenUnauthorized() {
        int statusCode = RestAssured.get("http://localhost:" + port).statusCode();
        
        assertEquals(HttpStatus.UNAUTHORIZED.value(), statusCode);
    }
}
```

另一方面，我们可以在通过身份验证时成功访问主页：

```java
@Test
void givenAuthenticationWhenAccessHomeThenSuccess() {
    int statusCode = RestAssured.given()
            .auth()
            .basic("john", "123")
            .get("http://localhost:" + port)
            .getStatusCode();
    
    assertEquals(HttpStatus.OK.value(), statusCode);
}
```

在以下小节中，我们将**尝试不同的方法从测试配置中排除SecurityAutoConfiguration类**。

## 3. 使用@EnableAutoConfiguration注解

有多种方法可以从测试配置中排除特定的自动配置类。

首先，**让我们看看如何使用@EnableAutoConfiguration(exclude={CLASS_NAME})注解**：

```java
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExcludeAutoConfig3IntegrationTest {
    @LocalServerPort
    private int port;

    @Test
    void givenSecurityConfigExcluded_whenAccessHome_thenNoAuthenticationRequired() {
        int statusCode = RestAssured.get("http://localhost:" + port).getStatusCode();
        
        assertEquals(HttpStatus.OK.value(), statusCode);
    }
}
```

在此示例中，我们使用exclude属性排除了SecurityAutoConfiguration类，但我们可以对任何自动配置类执行相同的操作。

现在我们可以运行我们的测试，无需身份验证即可访问主页，并且它不会再失败。

## 4. 使用@TestPropertySource注解

接下来，**我们可以使用@TestPropertySource注入属性“spring.autoconfigure.exclude”**：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"})
class ExcludeAutoConfig1IntegrationTest {
    @LocalServerPort
    private int port;

    @Test
    void givenSecurityConfigExcluded_whenAccessHome_thenNoAuthenticationRequired() {
        int statusCode = RestAssured.get("http://localhost:" + port).getStatusCode();
        
        assertEquals(HttpStatus.OK.value(), statusCode);
    }
}
```

请注意，我们需要为属性指定完整的全限定类名(包名+类名)。

## 5. 使用profiles

**我们还可以使用profile为我们的测试环境设置”spring.autoconfigure.exclude“**。

```java
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExcludeAutoConfig2IntegrationTest {
    @LocalServerPort
    private int port;

    @Test
    void givenSecurityConfigExcluded_whenAccessHome_thenNoAuthenticationRequired() {
        int statusCode = RestAssured.get("http://localhost:" + port).getStatusCode();

        assertEquals(HttpStatus.OK.value(), statusCode);
    }
}
```

并在application-test.properties文件中包含所有特定于“test” profile相关的属性：

以下是我们的application-test.properties文件：

```properties
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
```

## 6. 使用自定义的测试配置类

最后，**我们可以使用单独的配置应用程序类进行测试**：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExcludeAutoConfig4IntegrationTest {
    @LocalServerPort
    private int port;

    @Test
    void givenSecurityConfigExcluded_whenAccessHome_thenNoAuthenticationRequired() {
        int statusCode = RestAssured.get("http://localhost:" + port).getStatusCode();

        assertEquals(HttpStatus.OK.value(), statusCode);
    }
}
```

并从@SpringBootApplication(exclude={CLASS_NAME})中排除自动配置类：

```java
@SpringBootApplication(scanBasePackages = "cn.tuyucheng.taketoday.boot", exclude = SecurityAutoConfiguration.class)
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
```

## 7. 总结

在本文中，我们探讨了从Spring Boot测试中排除自动配置类的不同方法。