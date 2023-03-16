## 1. 概述

在本教程中，我们将研究覆盖Spring测试中的属性的各种方法。

Spring实际上为此提供了许多解决方案，所以我们在这里有很多东西值得探讨。

## 2. Maven依赖

当然，为了使用Spring测试，我们需要添加一个测试依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <version>2.7.2</version>
    <scope>test</scope>
</dependency>
```

此依赖项还包括我们的JUnit 5。

## 3. 设置

首先，我们需要创建一个类，该类将使用我们在配置文件定义的属性：

```java
@Component
public class PropertySourceResolver {

    @Value("${example.firstProperty}")
    private String firstProperty;
    @Value("${example.secondProperty}")
    private String secondProperty;

    public String getFirstProperty() {
        return firstProperty;
    }

    public String getSecondProperty() {
        return secondProperty;
    }
}
```

接下来，我们将为它们赋值。我们可以通过在src/main/resources目录中创建application.properties文件实现这一点：

```properties
#application.properties
example.firstProperty=defaultFirst
example.secondProperty=defaultSecond
```

## 4. 覆盖属性文件

现在，我们通过将属性文件放入测试资源文件夹中来覆盖src/main/resources中application.properties定义的属性。**此文件必须与默认文件位于同一类路径上**。

此外，它**应该包含默认文件中指定的所有属性key**。因此，我们将application.properties文件添加到src/test/resources中：

```properties
example.firstProperty=file
example.secondProperty=file
```

现在让我们编写一个测试验证是否有效：

```java
@SpringBootTest
@EnableWebMvc
class TestResourcePropertySourceResolverIntegrationTest {

    @Autowired
    private PropertySourceResolver propertySourceResolver;

    @Test
    void shouldTestResourceFile_overridePropertyValues() {
        final String firstProperty = propertySourceResolver.getFirstProperty();
        final String secondProperty = propertySourceResolver.getSecondProperty();

        assertEquals("file", firstProperty);
        assertEquals("file", secondProperty);
    }
}
```

当我们想要覆盖文件中的多个属性时，此方法非常有用。

如果我们不在src/test/resources目录的application.properties文件中定义example.secondProperty属性，应用程序上下文将找不到此属性的定义。

## 5. Spring Profiles

在本节中，我们将学习如何使用Spring Profiles来处理我们的问题。**与前面的方法不同，此方法合并Spring Boot默认配置文件和Profile文件中的属性**。

首先，让我们在src/test/resources中创建一个application-test.properties文件：

```properties
#application-test.properties
example.firstProperty=profile
```

然后我们将创建一个使用test Profile的测试：

```java
@SpringBootTest
@ActiveProfiles("test")
@EnableWebMvc
class ProfilePropertySourceResolverIntegrationTest {

    @Autowired
    private PropertySourceResolver propertySourceResolver;

    @Test
    void shouldProfiledProperty_overridePropertyValues() {
        final String firstProperty = propertySourceResolver.getFirstProperty();
        final String secondProperty = propertySourceResolver.getSecondProperty();

        assertEquals("profile", firstProperty);
        assertEquals("file", secondProperty);
    }
}
```

注意，secondProperty的值是从src/test/resources目录application.properties文件中读取的。如果该文件中不存在该属性的定义，应用程序上下文将找不到此属性，运行会抛出以下异常：

```shell
Caused by: java.lang.IllegalArgumentException: Could not resolve placeholder 'example.secondProperty' in value " ${example.secondProperty}"
```

这种方法允许我们同时使用默认值和测试值。因此，**当我们需要覆盖文件中的多个属性，但仍然想使用一些默认属性时，这是一个很好的方法**。

我们可以在[Spring Profiles](https://www.baeldung.com/spring-profiles)一文中了解有关Spring Profile的更多信息。

## 6. @SpringBootTest注解

覆盖属性值的另一种方法是使用@SpringBootTest注解：

```java
@SpringBootTest(properties = {"example.firstProperty=annotation"})
@EnableWebMvc
class SpringBootPropertySourceResolverIntegrationTest {

    @Autowired
    private PropertySourceResolver propertySourceResolver;

    @Test
    void shouldSpringBootTestAnnotation_overridePropertyValues() {
        final String firstProperty = propertySourceResolver.getFirstProperty();
        final String secondProperty = propertySourceResolver.getSecondProperty();

        assertEquals("annotation", firstProperty);
        assertEquals("file", secondProperty);
    }
}
```

**正如我们所看到的，example.firstProperty已被覆盖，而example.secondProperty没有被覆盖**。因此，当我们只需要覆盖测试的特定属性时，这是一个很好的解决方案，并且这也是唯一需要使用到Spring Boot的方法。

## 7. TestPropertySourceUtils

在本节中，我们将学习如何使用ApplicationContextInitializer中的TestPropertySourceUtils类来覆盖属性。

TestPropertySourceUtils附带了两个方法，我们可以使用它们来定义不同的属性值。

让我们创建一个将在测试中使用的PropertyOverrideContextInitializer类：

```java
public class PropertyOverrideContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    static final String PROPERTY_FIRST_VALUE = "contextClass";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext, "example.firstProperty=" + PROPERTY_FIRST_VALUE);
        TestPropertySourceUtils.addPropertiesFilesToEnvironment(applicationContext, "classpath:context-override-application.properties");
    }
}
```

接下来，我们在src/test/resources中添加一个context-override-application.properties文件：

```properties
example.secondProperty=contextFile
```

最后，我们应该创建一个使用PropertyOverrideContextInitializer的测试类：

```java
@SpringBootTest
@ContextConfiguration(initializers = PropertyOverrideContextInitializer.class, classes = TestApplication.class)
class ContextPropertySourceResolverIntegrationTest {

    @Autowired
    private PropertySourceResolver propertySourceResolver;

    @Test
    void shouldContext_overridePropertyValues() {
        final String firstProperty = propertySourceResolver.getFirstProperty();
        final String secondProperty = propertySourceResolver.getSecondProperty();

        assertEquals(PropertyOverrideContextInitializer.PROPERTY_FIRST_VALUE, firstProperty);
        assertEquals("contextFile", secondProperty);
    }
}
```

example.firstProperty属性被TestPropertySourceUtils.addInlinedPropertiesToEnvironment()方法覆盖。

example.secondProperty属性被TestPropertySourceUtils.addPropertiesFilesToEnvironment()方法中的特定文件覆盖。这种方法允许我们在初始化上下文时定义不同的属性值。

## 8. 总结

在本文中，我们重点介绍了可以在测试中覆盖属性的多种方式。我们还讨论了何时使用哪种方法，或者在某些情况下，何时混合使用它们。

当然，我们也可以使用[@TestPropertySource](https://www.baeldung.com/spring-test-property-source)注解。