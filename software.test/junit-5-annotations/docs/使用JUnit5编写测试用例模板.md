## 1. 概述

与以前的版本相比，JUnit 5提供了许多新特性，其中一个功能是测试模板。简而言之，测试模板是JUnit 5中参数化和重复测试的概括。

在本教程中，我们将学习如何使用JUnit 5创建测试模板。

## 2. maven依赖

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>5.8.1</version>
</dependency>
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.8.1</version>
</dependency>
```

## 3. 问题陈述

在了解测试模板之前，让我们简要地回顾一下JUnit 5的参数化测试。参数化测试允许我们将不同的参数注入到测试方法中，因此，**在使用参数化测试时，我们可以使用不同的参数多次执行单个测试方法**。

假设我们现在想要多次运行我们的测试方法-不仅使用不同的参数，而且每次都在不同的调用上下文中。

**换句话说，我们希望测试方法被执行多次，每次调用使用不同的配置组合，例如**：

+ 使用不同的参数
+ 以不同的方式准备测试类实例，即将不同的依赖注入到测试实例中
+ 在不同条件下运行测试，例如，如果环境是“QA”，则启用/禁用调用子集
+ 以不同的生命周期回调行为运行，也许我们想在调用子集之前和之后设置和关闭数据库

在这种情况下，使用参数化测试是无法满足的。庆幸的是，JUnit 5以测试模板的形式为这种场景提供了强大的解决方案。

## 4. 测试模板

测试模板本身不是测试用例，顾名思义，它们只是给定测试用例的模板，它们是参数化和重复测试的强大概括。

**对于调用上下文提供程序提供给测试模板的每个调用上下文，只调用一次测试模板**。

现在让我们看一个测试模板的例子，主要包含的角色有：

+ 目标测试方法
+ 测试模板方法
+ 使用模板方法注册的一个或多个调用上下文提供程序
+ 每个调用上下文提供程序提供的一个或多个调用上下文

### 4.1 目标测试方法

对于这个例子，我们将使用一个简单的UserIdGeneratorImpl.generate()方法作为我们的目标测试方法：

```java
public class UserIdGeneratorImpl implements UserIdGenerator {
    private boolean isFeatureEnabled;

    public UserIdGeneratorImpl(boolean isFeatureEnabled) {
        this.isFeatureEnabled = isFeatureEnabled;
    }

    public String generate(String firstName, String lastName) {
        String initialAndLastName = firstName.substring(0, 1).concat(lastName);
        return isFeatureEnabled ? "tyc".concat(initialAndLastName) : initialAndLastName;
    }
}
```

generate()是我们的目标测试方法，它接收firstName和lastName作为参数并生成一个用户ID，返回的结果取决于isFeatureEnabled成员变量的值。

比如：

> Given feature switch is disabled When firstName = "John" and lastName = "Smith" Then "JSmith" is returned

> Given feature switch is enabled When firstName = "John" and lastName = "Smith" Then "tycJSmith" is returned

接下来，让我们编写测试模板方法。

### 4.2 测试模板方法

下面是我们的目标测试方法UserIdGeneratorImpl.generate()的测试模板：

```java
class UserIdGeneratorImplUnitTest {

    @TestTemplate
    @ExtendWith(UserIdGeneratorTestInvocationContextProvider.class)
    void whenUserIdRequested_thenUserIdIsReturnedInCorrectFormat(UserIdGeneratorTestCase testCase) {
        UserIdGenerator userIdGenerator = new UserIdGeneratorImpl(testCase.isFeatureEnabled());

        String actualUserId = userIdGenerator.generate(testCase.getFirstName(), testCase.getLastName());

        assertThat(actualUserId).isEqualTo(testCase.getExpectedUserId());
    }
}
```

**首先，我们通过使用JUnit 5中的@TestTemplate注解标记它来创建我们的测试模板方法**。

之后，我们使用@ExtendWith注解注册一个上下文提供程序UserIdGeneratorTestInvocationContextProvider。我们可以向测试模板注册多个上下文提供程序，这里只是演示，因此保持简单。

此外，模板方法接收UserIdGeneratorTestCase的实例作为参数，这只是测试用例的输入和预期输出的包装类：

```java
public class UserIdGeneratorTestCase {
    private final String displayName;
    private final boolean isFeatureEnabled;
    private final String firstName;
    private final String lastName;
    private final String expectedUserId;
    // constructors getters and setters ...
}
```

最后，我们调用测试目标方法并断言结果与预期一致。

### 4.3 调用上下文提供程序

我们需要在我们的测试模板中注册至少一个TestTemplateInvocationContextProvider，**每个注册的TestTemplateInvocationContextProvider都提供一个TestTemplateInvocationContext实例的Stream**。

之前，使用@ExtendWith注解，我们将UserIdGeneratorTestInvocationContextProvider注册为我们的调用提供程序。

现在让我们定义这个类：

```java
public class UserIdGeneratorTestInvocationContextProvider implements TestTemplateInvocationContextProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserIdGeneratorTestInvocationContextProvider.class);
    // ...
}
```

我们的调用上下文实现了TestTemplateInvocationContextProvider接口，该接口有两个方法：

+ supportsTestTemplate
+ provideTestTemplateInvocationContexts

首先我们介绍supportsTestTemplate方法：

```java
@Override
public boolean supportsTestTemplate(ExtensionContext extensionContext) {
    return true;
}
```

JUnit 5执行引擎首先调用supportsTestTemplate()方法来验证提供程序是否适用于给定的ExecutionContext。在这种情况下，我们只需返回true。

现在，让我们实现provideTestTemplateInvocationContexts()方法：

```java
@Override
public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext extensionContext) {
    boolean featureDisabled = false;
    boolean featureEnabled = true;
    return Stream.of(
            featureDisabledContext(
                    new UserIdGeneratorTestCase(
                            "Given feature switch disabled When user name is John Smith Then generated userid is JSmith",
                            featureDisabled, "John", "Smith", "JSmith")),
            featureEnabledContext(
                    new UserIdGeneratorTestCase(
                            "Given feature switch enabled When user name is John Smith Then generated userid is baelJSmith",
                            featureEnabled, "John", "Smith", "baelJSmith"))
    );
}
```

provideTestTemplateInvocationContexts()方法的目的是提供TestTemplateInvocationContext实例的Stream。在我们的例子中，它返回两个实例，由方法featureDisabledContext()和featureEnabledContext()提供，因此，我们的测试模板将运行两次。

接下来，我们看一下这两个返回TestTemplateInvocationContext实例的方法。

### 4.4 调用上下文实例

调用上下文是TestTemplateInvocationContext接口的实现，并实现以下方法：

+ getDisplayName：提供测试显示名称
+ getAdditionalExtensions：返回调用上下文的其他Extension

让我们定义返回第一个调用上下文实例的featureDisabledContext()方法：

```java
private TestTemplateInvocationContext featureDisabledContext(UserIdGeneratorTestCase userIdGeneratorTestCase) {
    return new TestTemplateInvocationContext() {
        @Override
        public String getDisplayName(int invocationIndex) {
            return userIdGeneratorTestCase.getDisplayName();
        }

        @Override
        public List<Extension> getAdditionalExtensions() {
            return asList(
                    new GenericTypedParameterResolver<>(userIdGeneratorTestCase),
                    (BeforeTestExecutionCallback) extensionContext -> LOGGER.debug("BeforeTestExecutionCallback:Disabled context"),
                    (AfterTestExecutionCallback) extensionContext -> LOGGER.debug("AfterTestExecutionCallback:Disabled context")
            );
        }
    };
}
```

首先，对于featureDisabledContext()方法返回的调用上下文，我们注册的Extension是：

+ GenericTypedParameterResolver：参数解析器扩展
+ BeforeTestExecutionCallback：在测试执行之前立即运行的生命周期回调扩展
+ AfterTestExecutionCallback：在测试执行之后立即运行的生命周期回调扩展

但是，对于由featureEnabledContext()方法返回的第二个调用上下文，我们可以注册一组不同的Extension(保留GenericTypedParameterResolver)：

```java
private TestTemplateInvocationContext featureEnabledContext(UserIdGeneratorTestCase userIdGeneratorTestCase) {
    return new TestTemplateInvocationContext() {
        @Override
        public String getDisplayName(int invocationIndex) {
            return userIdGeneratorTestCase.getDisplayName();
        }

        @Override
        public List<Extension> getAdditionalExtensions() {
            return asList(
                    new GenericTypedParameterResolver<>(userIdGeneratorTestCase),
                    new DisabledOnQAEnvironmentExtension(),
                    (BeforeEachCallback) extensionContext -> LOGGER.debug("BeforeEachCallback:Enabled context"),
                    (AfterEachCallback) extensionContext -> LOGGER.debug("AfterEachCallback:Enabled context")
            );
        }
    };
}
```

对于第二个调用上下文，我们注册的Extension是：

+ GenericTypedParameterResolver：参数解析器扩展
+ DisabledOnQAEnvironmentExtension：如果env属性(从application.properties文件加载)的值是“qa”，则禁用测试的执行条件
+ BeforeEachCallback：在每个测试方法执行之前运行的生命周期回调扩展
+ AfterEachCallback：在每个测试方法执行之后运行的生命周期回调扩展

从上面的例子可以清楚地看到：

+ 同一个测试方法在多个调用上下文中运行
+ 每个调用上下文都使用自己的一组Extensions，这些Extensions在数量和性质上都不同于其他调用上下文中的Extensions

因此，每次都可以在完全不同的调用上下文下多次调用测试方法。通过注册多个上下文提供程序，我们可以提供更多额外的调用上下文层来运行测试。

## 5. 总结

在本文中，我们了解了JUnit 5的测试模板是如何对参数化和重复测试进行强大概括的。

首先，我们说明了参数化测试的一些限制。接下来，我们讨论了测试模板如何通过允许测试在每次调用的不同上下文中运行来克服这些限制。最后，我们通过一个创建新测试模板的示例，知道了测试模板如何与调用上下文提供程序和调用上下文一起使用。