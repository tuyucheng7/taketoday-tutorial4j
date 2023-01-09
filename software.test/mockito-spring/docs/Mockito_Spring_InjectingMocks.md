## 1. 概述

在本教程中，我们介绍如何使用依赖注入将Mockito mock注入Spring Beans以进行单元测试。

在实际应用程序中，组件通常依赖于访问外部系统，因此提供适当的测试隔离非常重要，这样我们就可以专注于测试给定单元的功能，而不必涉及每个测试的整个类层次结构。注入mock是引入这种隔离的一种很好的方法。

## 2. Maven依赖

我们需要以下Maven依赖项来进行单元测试和mock对象：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <version>2.6.1</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <version>2.6.1</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>4.0.0</version>
</dependency>
```

我们在这个例子中使用Spring Boot，但原生的Spring框架也可以正常配置Mockito使用。

## 3. 编写测试

### 3.1 业务逻辑

首先，让我们创建一个我们将要测试的简单Service类：

```java
@Service
public class NameService {
    
    public String getUserName(String id) {
        return "Real user name";
    }
}
```

然后我们将它注入到UserService类中：

```java
@Service
public class UserService {

    private final NameService nameService;

    @Autowired
    public UserService(NameService nameService) {
        this.nameService = nameService;
    }

    public String getUserName(String id) {
        return nameService.getUserName(id);
    }
}
```

为了简单起见，不论提供给getUserName()方法的参数id是什么，都返回一个固定的值。

我们还需要一个标准的Spring Boot主类来扫描bean并初始化应用程序：

```java
@SpringBootApplication
public class MocksApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MocksApplication.class, args);
    }
}
```

### 3.2 测试

首先，我们必须为测试配置应用程序上下文：

```java
@Profile("test")
@Configuration
public class NameServiceTestConfiguration {

    @Bean
    @Primary
    public NameService nameService() {
        return Mockito.mock(NameService.class);
    }
}
```

@Profile注解告诉Spring仅在“test” Profile处于激活状态时应用此配置。@Primary注解用于确保使用nameService()方法创建的NameService mock实例进行自动注入，而不是使用真实实例。

现在我们可以编写单元测试：

```java
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MocksApplication.class)
class UserServiceUnitTest {

    @Autowired
    private UserService userService;

    @Autowired
    private NameService nameService;

    @Test
    void whenUserIdIsProvided_thenRetrievedNameIsCorrect() {
        Mockito.when(nameService.getUserName("SomeId")).thenReturn("Mock user name");

        String testName = userService.getUserName("SomeId");

        assertEquals("Mock user name", testName);
    }
}
```

我们使用@ActiveProfiles注解来启用“test” Profile并激活我们之前编写的mock配置。因此，Spring会自动注入UserService类的真实实例，但是会mock NameService类。测试本身是一个相当典型的JUnit5 + Mockito测试。我们配置mock的期望行为，然后调用我们想要测试的方法，并断言它返回我们期望的值。

也可以(尽管不推荐)避免在此类测试中使用Profile。为此，我们可以删除@Profile和@ActiveProfiles注解，并将@ContextConfiguration(classes = NameServiceTestConfiguration.class)注解添加到UserServiceTest类上。

## 4. 总结

在这篇简短的文章中，我们介绍了如何将Mockito mock注入到Spring Bean。