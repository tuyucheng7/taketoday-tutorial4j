## 1. 简介

在这篇简短的文章中，我们将了解Spring 5中可用的新@SpringJUnitConfig和@SpringJUnitWebConfig注解。

**这些注解是JUnit 5和Spring 5注解的组合**，它们使测试创建更加容易和快速。

## 2. @SpringJUnitConfig

@SpringJUnitConfig结合了这两个注解：

- **来自JUnit 5的@ExtendWith(SpringExtension.class)**使用SpringExtension类运行测试
- **来自Spring Testing的@ContextConfiguration**用于加载Spring上下文

让我们创建一个测试并在实践中使用这个注解：

```java
@SpringJUnitConfig(SpringJUnitConfigIntegrationTest.Config.class)
public class SpringJUnitConfigIntegrationTest {

    @Configuration
    static class Config {}
}
```

**请注意，与@ContextConfiguration相比，配置类是使用value属性声明的**。但是，应该使用locations属性指定资源位置。

我们现在可以验证Spring上下文是否真的加载了：

```java
@Autowired
private ApplicationContext applicationContext;

@Test
void givenAppContext_WhenInjected_ThenItShouldNotBeNull() {
    assertNotNull(applicationContext);
}
```

最后，这里我们有@SpringJUnitConfig(SpringJUnitConfigTest.Config.class)的等效代码：

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringJUnitConfigTest.Config.class)
```

## 3. @SpringJUnitWebConfig

**@SpringJUnitWebConfig结合了与@SpringJUnitConfig相同的注解加上Spring测试中的@WebAppConfiguration**-加载WebApplicationContext。

让我们看看这个注解是如何工作的：

```java
@SpringJUnitWebConfig(SpringJUnitWebConfigIntegrationTest.Config.class)
public class SpringJUnitWebConfigIntegrationTest {

    @Configuration
    static class Config {
    }
}
```

与@SpringJUnitConfig一样，**配置类位于value属性中**，任何资源都使用locations属性指定。

此外，@WebAppConfiguration的value属性现在应该使用resourcePath属性指定。**默认情况下，此属性设置为“src/main/webapp”**。

现在让我们验证WebApplicationContext是否真的加载了：

```java
@Autowired
private WebApplicationContext webAppContext;

@Test
void givenWebAppContext_WhenInjected_ThenItShouldNotBeNull() {
    assertNotNull(webAppContext);
}
```

同样，这里我们有不使用@SpringJUnitWebConfig的等效代码：

```java
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = SpringJUnitWebConfigIntegrationTest.Config.class)
```

## 4. 总结

在这个简短的教程中，我们展示了如何使用Spring 5中新引入的@SpringJUnitConfig和@SpringJUnitWebConfig注解。