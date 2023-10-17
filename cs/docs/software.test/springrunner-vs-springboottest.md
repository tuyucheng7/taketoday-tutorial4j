## 1. 概述

测试对于任何应用程序都至关重要，无论是单元测试还是集成测试。[SpringRunner](https://www.baeldung.com/junit-springrunner-vs-mockitojunitrunner#springrunner)和[SpringBootTest](https://www.baeldung.com/spring-boot-testing#integration-testing-with-springboottest)类构成了运行集成测试的基础。

在本教程中，我们将了解两者。我们将学习如何在代码中使用它们并了解它们的相似点和差异。

## 2.SpringRunner _

[SpringRunner是](https://www.baeldung.com/junit-springrunner-vs-mockitojunitrunner#springrunner)[SpringJUnit4ClassRunner](https://www.baeldung.com/springjunit4classrunner-parameterized#springjunit4classrunner) 类的别名，适用于[基于JUnit4](https://www.baeldung.com/junit-4-custom-runners)的测试类。它加载 Spring TestContext，通过它 Spring bean 和配置可以与JUnit注释一起使用。我们需要JUnit4.12 或更高版本才能使用它。

要在代码中使用它，请使用@RunWith(SpringRunner.class)注释测试类：

```java
@RunWith(SpringRunner.class)
public class SampleIntegrationTest {

    @Test
    public void test() {
        //
    }
}
```

## 3.SpringBoot测试

[SpringBootTest是](https://www.baeldung.com/spring-boot-testing#integration-testing-with-springboottest)[SpringRunner](https://www.baeldung.com/junit-springrunner-vs-mockitojunitrunner#springrunner)的替代方案，可与[JUnit5配合使用](https://www.baeldung.com/junit-5)。它还用于运行集成测试和加载 Spring TestContext。

它非常丰富，并通过其注释参数提供了许多配置。它支持各种 Web 环境模式，例如MOCK、RANDOM_PORT、DEFINED_PORT和NONE。我们可以通过在测试运行之前注入到 spring 环境中的注释来传递应用程序属性：

```java
@SpringBootTest(
  properties = {"user.name=test_user"},
  webEnvironment = MOCK)
public class SampleIntegrationTest {

    @Test
    public void test() {
        //
    }
}
```

在类级别需要注释@SpringBootTest 才能运行集成测试。

## 4. SpringRunner和SpringBootTest的比较

在此表中，我们将比较这两个类别的优缺点。

|                 春跑者                  |           SpringBoot测试            |
| :---------------------------------------: | :---------------------------------------: |
| 用于运行集成测试并加载 Spring TestContext | 用于运行集成测试并加载 Spring TestContext |
|          JUnit注释也可以使用            |          JUnit注释也可以使用            |
|          需要JUnit4.12或更高版本          |          需要 JUnit5 或更高版本           |
|         就配置而言，API 并不丰富          |        提供丰富的API来配置测试配置        |
|                  不建议                   |    推荐，因为它支持新功能并且易于使用     |

## 5. 总结

在这篇文章中，我们了解了[SpringRunner](https://www.baeldung.com/junit-springrunner-vs-mockitojunitrunner#springrunner)和[SpringBootTest](https://www.baeldung.com/spring-boot-testing#integration-testing-with-springboottest)。我们已经学会了如何使用它们。我们还对它们进行了比较，了解了它们的差异和相似之处。

我们应该使用[SpringBootTest，](https://www.baeldung.com/spring-boot-testing#integration-testing-with-springboottest)因为它支持最新的[JUnit](https://www.baeldung.com/junit-5)，但是每当需要使用[JUnit 4 时，](https://www.baeldung.com/junit-4-custom-runners) [SpringRunner](https://www.baeldung.com/junit-springrunner-vs-mockitojunitrunner#springrunner)就是选择。