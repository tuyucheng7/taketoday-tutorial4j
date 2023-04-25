## 1. 概述

为开发和部署过程的不同阶段创建不同的配置是很常见的。实际上，当我们在部署一个Spring应用程序时，我们可以为每个阶段分配一个[Spring Profile](https://www.baeldung.com/spring-profiles)，并创建专门的测试。

在本教程中，我们将重点介绍如何使用[JUnit 5](https://www.baeldung.com/junit-5)基于激活的Spring Profile执行测试。

## 2. 项目设置

首先，让我们在项目中添加[spring-boot-starter-web](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web)依赖：

```xml
<dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-web</artifactId>
     <version>2.7.8</version>
</dependency>
```

现在让我们创建一个简单的Spring Boot应用程序：

```java
@SpringBootApplication
public class ActiveProfileApplication {

    public static void main (String [] args){
        SpringApplication.run(Application.class);
    }
}
```

最后，让我们创建一个application.yaml文件作为我们的默认属性源文件。

## 3. Spring简介

**[Spring Profile](https://www.baeldung.com/spring-profiles)通过对特定于每个环境的配置设置进行分组，提供了一种定义和管理不同环境的方法**。

事实上，通过激活特定的Profile，我们可以轻松地在不同的配置和设置之间切换。

### 3.1 属性文件中的活动Profile

我们可以在application.yaml文件中指定活动Profile：

```yaml
spring:
    profiles:
        active: dev
```

所以现在Spring将检索活动Profile的所有属性并将所有专用bean加载到应用程序上下文中。

在本教程中，我们将为每个Profile创建一个专用的属性文件。**这是因为为每个Profile提供专用文件可以简化管理和更新每个特定环境的配置的过程**。

因此，让我们为test和prod环境创建两个Profile，每个Profile都具有profile.property.value属性，但具有与文件名相对应的不同值。

因此，让我们创建一个application-prod.yaml文件并添加profile.property.value属性：

```yaml
profile:
    property:
        value: This the the application-prod.yaml file
```

同样，我们对application-test.yaml做同样的事情：

```yaml
profile:
    property:
        value: This the the application-test.yaml file
```

最后，让我们在application.yaml中添加相同的属性：

```yaml
profile:
    property:
        value: This the the application.yaml file
```

现在让我们编写一个简单的测试来验证应用程序在此环境中的行为是否符合预期：

```java
@SpringBootTest(classes = ActiveProfileApplication.class)
public class DevActiveProfileUnitTest {

    @Value("${profile.property.value}")
    private String propertyString;

    @Test
    void whenDevIsActive_thenValueShouldBeKeptFromApplicationYaml() {
        Assertions.assertEquals("This the the application.yaml file", propertyString);
    }
}
```

注入到变量propertyString的值来自application.yaml中定义的属性值。这是因为dev是在测试执行期间激活的Profile，并且没有为该Profile定义属性文件。

### 3.2 在测试类上设置活动Profile

通过设置spring.profiles.active属性，我们可以激活相应的Profile并加载与其关联的配置文件。

但是，在某些情况下，我们可能希望使用特定Profile执行测试类，覆盖属性文件中定义的活动Profile。

**所以我们可以使用[@ActiveProfiles](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/context/ActiveProfiles.html)，这是一个与JUnit 5兼容的注解，它声明了在为测试类加载应用程序上下文时要使用的活动Profile**。

因此，如果我们使用@ActiveProfile注解对测试类进行标注并将value属性设置为test，则该类中的所有测试都将使用test Profile：

```java
@SpringBootTest(classes = ActiveProfileApplication.class)
@ActiveProfiles(value = "test")
public class TestActiveProfileUnitTest {

    @Value("${profile.property.value}")
    private String propertyString;

    @Test
    void whenTestIsActive_thenValueShouldBeKeptFromApplicationTestYaml() {
        Assertions.assertEquals("This the the application-test.yaml file", propertyString);
    }
}
```

value属性是profiles属性的别名，是一个字符串数组。**这是因为可以使用逗号分隔的列表指定多个活动Profile**。

例如，如果我们想将活动Profile指定为test和prod，我们可以使用：

```java
@ActiveProfiles({"prod", "test"})
```

通过这种方式，可以使用特定于test和dev Profile的属性和配置来配置应用程序上下文。

**配置将按照列出的顺序应用。如果不同Profile的配置发生冲突，则为最后列出的Profile定义的配置文件将优先**。

因此，基于活动Profile运行测试对于确保应用程序在不同环境中正确运行至关重要。但是，在其他环境中执行针对特定Profile设计的测试可能会带来重大风险。例如，在本地机器上运行测试时，我们可能会无意中运行为生产环境设计的测试。

为了避免这种情况，我们需要找到一种方法来根据活动Profile过滤测试执行。

## 4. @EnabledIf注解

在JUnit 4中，可以使用[@IfProfileValue](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/annotation/IfProfileValue.html)注解有条件地执行测试。实际上，它指定了执行测试所必须满足的条件。

**但是当我们的单元测试框架是JUnit 5时，我们应该避免使用@IfProfileValue，因为它在当前版本上不再受支持**。

因此我们可以改用[@EnabledIf](https://www.baeldung.com/spring-5-enabledif)，**这是一个基于条件启用或禁用方法或类的注解**。

Junit 5也提供了[@EnabledIf](https://junit.org/junit5/docs/5.7.1/api/org.junit.jupiter.api/org/junit/jupiter/api/condition/EnabledIf.html)注解。因此，我们应该确保导入Spring提供的那个，以避免任何混淆。

该注解具有以下属性：

-   value：为启用测试类或单个测试而应该为true的条件表达式
-   expression：它也指定了条件表达式。事实上，它被标记为[@AliasFor](https://www.baeldung.com/spring-aliasfor-annotation)
-   loadContext：指定是否需要加载上下文以评估条件。默认值为false
-   reason：说明为什么需要该条件

**为了评估使用Spring应用程序上下文中定义的值的条件，如活动Profile，我们应该将布尔属性loadContext设置为true**。

### 4.1 为单个Profile运行测试

如果我们只想在单个Profile处于激活状态时运行我们的测试类，我们可以使用[SPEL](https://www.baeldung.com/spring-expression-language)函数评估属性value：

```java
#{environment.getActiveProfiles()[0] == 'prod'}
```

在这个函数中，environment变量是一个实现了[Environment](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/env/Environment.html)的对象。因此environment.getActiveProfiles()返回当前环境中活动Profile的数组，\[0\]访问该数组的第一个元素。

因此，让我们将注解添加到我们的测试中：

```java
@SpringBootTest(classes = ActiveProfileApplication.class)
@EnabledIf(value = "#{environment.getActiveProfiles()[0] == 'prod'}", loadContext = true)
public class ProdActiveProfileUnitTest {

    @Value("${profile.property.value}")
    private String propertyString;

    @Test
    void whenProdIsActive_thenValueShouldBeKeptFromApplicationProdYaml() {
        Assertions.assertEquals("This the the application-prod.yaml file", propertyString);
    }
}
```

然后，让我们通过@ActiveProfiles激活prod Profile：

```java
@SpringBootTest(classes = ActiveProfileApplication.class)
@EnabledIf(value = "#{environment.getActiveProfiles()[0] == 'prod'}", loadContext = true)
@ActiveProfiles(value = "prod")
public class ProdActiveProfileUnitTest {

    @Value("${profile.property.value}")
    private String propertyString;

    @Test
    void whenProdIsActive_thenValueShouldBeKeptFromApplicationProdYaml() {
        Assertions.assertEquals("This the the application-prod.yaml file", propertyString);
    }
}
```

因此，我们类中的测试将始终使用为prod Profile指定的配置运行，并且当前Profile实际上是prod。

### 4.2 为多个Profile运行测试

如果我们想在不同的活动Profile下执行我们的测试，我们可以使用SPEL函数评估value或expression属性：

```java
#{{'test', 'prod'}.contains(environment.getActiveProfiles()[0])}
```

让我们分解一下功能：

-   {'test','prod'}定义了我们的Spring应用程序中定义的一组两个Profile名称
-   .contains{environment-getActiveProfiles()\[0\]}检查数组的第一个元素是否包含在之前定义的集合中

因此，让我们将@EnableIf注解添加到我们的测试类中：

```java
@SpringBootTest(classes = ActiveProfileApplication.class)
@EnabledIf(value = "#{{'test', 'prod'}.contains(environment.getActiveProfiles()[0])}", loadContext = true)
@ActiveProfiles(value = "test")
public class MultipleActiveProfileUnitTest {
    @Value("${profile.property.value}")
    private String propertyString;

    @Autowired
    private Environment env;

    @Test
    void whenDevIsActive_thenValueShouldBeKeptFromDedicatedApplicationYaml() {
        String currentProfile = env.getActiveProfiles()[0];
        Assertions.assertEquals(String.format("This the the application-%s.yaml file", currentProfile), propertyString);
    }
}
```

因此，我们类中的测试将始终在活动Profile为test或prod时运行。

## 5. 总结

在本文中，我们了解了如何使用JUnit 5注解基于激活的Spring Profile执行测试。我们学习了如何在测试类上启用Profile，以及如果一个或多个特定Profile处于激活状态，我们如何执行它们。