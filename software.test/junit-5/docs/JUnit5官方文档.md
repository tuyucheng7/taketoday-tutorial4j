## 1. 概述

本文档的目标是为编写测试的程序员、作者和引擎作者以及构建工具和IDE供应商提供全面的参考文档。

该文档还提供[PDF下载](https://junit.org/junit5/docs/current/user-guide/junit-user-guide-5.9.1.pdf)。

### 1.1 什么是JUnit 5？

与以前版本的JUnit不同，JUnit 5由来自三个不同子项目的几个不同模块组成。

**JUnit 5 = JUnit Platform + JUnit Jupiter + JUnit Vintage**

**JUnit Platform**作为在JVM上[启动测试框架](https://junit.org/junit5/docs/current/user-guide/#launcher-api)的基础。它还定义了TestEngine用于开发在平台上运行的测试框架的API。此外，该平台提供了一个[控制台启动器](https://junit.org/junit5/docs/current/user-guide/#running-tests-console-launcher)以从命令行启动平台，并提供[JUnit平台套件引擎](https://junit.org/junit5/docs/current/user-guide/#junit-platform-suite-engine)以使用平台上的一个或多个测试引擎运行自定义测试套件。流行的 IDE(参见[IntelliJ IDEA](https://junit.org/junit5/docs/current/user-guide/#running-tests-ide-intellij-idea)、[Eclipse](https://junit.org/junit5/docs/current/user-guide/#running-tests-ide-eclipse)、[NetBeans](https://junit.org/junit5/docs/current/user-guide/#running-tests-ide-netbeans)和[Visual Studio Code](https://junit.org/junit5/docs/current/user-guide/#running-tests-ide-vscode))和构建工具(参见[Gradle](https://junit.org/junit5/docs/current/user-guide/#running-tests-build-gradle)、[Maven](https://junit.org/junit5/docs/current/user-guide/#running-tests-build-maven)和[Ant](https://junit.org/junit5/docs/current/user-guide/#running-tests-build-ant))。

JUnit Jupiter是用于在JUnit 5中编写测试和扩展的[编程模型](https://junit.org/junit5/docs/current/user-guide/#writing-tests)和[扩展模型](https://junit.org/junit5/docs/current/user-guide/#extensions)的组合。Jupiter子项目提供了一个TestEngine用于在平台上运行基于Jupiter的测试。

JUnit Vintage提供了一个TestEngine用于在平台上运行基于JUnit 3和JUnit 4的测试。它需要JUnit 4.12或更高版本出现在类路径或模块路径上。

### 1.2 支持的Java版本

JUnit 5在运行时需要Java 8(或更高版本)。但是，你仍然可以测试使用以前版本的JDK编译的代码。

### 1.3 获得帮助

[在Stack Overflow](https://stackoverflow.com/questions/tagged/junit5)上询问JUnit 5相关问题或在[Gitter](https://gitter.im/junit-team/junit5)上与社区聊天。

### 1.4 入门

#### 1.4.1 下载JUnit工件

要了解哪些工件可供下载并包含在你的项目中，请参阅[依赖元数据](https://junit.org/junit5/docs/current/user-guide/#dependency-metadata)。要为你的构建设置依赖管理，请参阅[构建支持](https://junit.org/junit5/docs/current/user-guide/#running-tests-build)和[示例项目](https://junit.org/junit5/docs/current/user-guide/#overview-getting-started-example-projects)。

#### 1.4.2 JUnit 5特性

要了解JUnit 5中可用的功能以及如何使用它们，请阅读本用户指南中按主题组织的相应部分。

-   [在JUnit Jupiter中编写测试](https://junit.org/junit5/docs/current/user-guide/#writing-tests)
-   [从JUnit 4迁移到 JUnit Jupiter](https://junit.org/junit5/docs/current/user-guide/#migrating-from-junit4)
-   [运行测试](https://junit.org/junit5/docs/current/user-guide/#running-tests)
-   [JUnit Jupiter的扩展模型](https://junit.org/junit5/docs/current/user-guide/#extensions)
-   高级主题
    -   [JUnit 平台启动器 API](https://junit.org/junit5/docs/current/user-guide/#launcher-api)
    -   [JUnit 平台测试套件](https://junit.org/junit5/docs/current/user-guide/#testkit)

#### 1.4.3 示例项目

要查看你可以复制和试验的完整、有效的项目示例，[junit5-samples](https://github.com/junit-team/junit5-samples)存储库是一个很好的起点。该 junit5-samples存储库托管了一组基于 JUnit Jupiter、JUnit Vintage 和其他测试框架的示例项目。你将在示例项目中找到适当的构建脚本(例如build.gradle，，pom.xml等)。下面的链接突出显示了你可以选择的一些组合。

-   对于Gradle和Java，请查看junit5-jupiter-starter-gradle项目。
-   对于Gradle和Kotlin，请查看junit5-jupiter-starter-gradle-kotlin项目。
-   对于Gradle和Groovy，请查看junit5-jupiter-starter-gradle-groovy项目。
-   对于Maven，请查看该junit5-jupiter-starter-maven项目。
-   对于Ant，请查看该junit5-jupiter-starter-ant项目。

## 2. 编写测试

以下示例简要介绍了在JUnit Jupiter中编写测试的最低要求，本章的后续部分将提供有关所有可用功能的更多详细信息。

第一个测试用例：

```java
import static org.junit.jupiter.api.Assertions.assertEquals;

import example.util.Calculator;

import org.junit.jupiter.api.Test;

class MyFirstJUnitJupiterTests {

    private final Calculator calculator = new Calculator();
    
    @Test
    void addition() {
        assertEquals(2, calculator.add(1, 1));
    }
}
```

### 2.1 注解

JUnit Jupiter支持以下用于配置测试和扩展框架的注解。

除非另有说明，否则所有核心注解都位于junit-jupiter-api模块中的`org.junit.jupiter.api`包中。

| 注解                   | 描述                                                         |
| :--------------------- | :----------------------------------------------------------- |
| @Test                  | 表示方法是测试方法。与JUnit 4的@Test注解不同，此注解不声明任何属性，因为JUnit Jupiter中的测试扩展基于它们自己的专用注解进行操作。此类方法将被继承，除非它们被覆盖。 |
| @ParameterizedTest     | 表示方法是[参数化测试](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests)。此类方法将被继承，除非它们被覆盖。 |
| @RepeatedTest          | 表示方法是[重复测试](https://junit.org/junit5/docs/current/user-guide/#writing-tests-repeated-tests)的测试模板。此类方法将被继承，除非它们被覆盖。 |
| @TestFactory           | 表示方法是[动态测试](https://junit.org/junit5/docs/current/user-guide/#writing-tests-dynamic-tests)的测试工厂。此类方法将被继承，除非它们被覆盖。 |
| @TestTemplate          | 表示方法是[测试用例的模板，设计用于根据注册](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-templates)[提供者](https://junit.org/junit5/docs/current/user-guide/#extensions-test-templates)返回的调用上下文的数量多次调用。此类方法将被继承，除非它们被覆盖。 |
| @TestClassOrder        | 用于配置注解测试类中测试类的[测试类执行顺序。](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-execution-order-classes)@Nested这样的注解是继承的。 |
| @TestMethodOrder       | 用于配置注解测试类的[测试方法执行顺序](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-execution-order-methods)；类似于JUnit 4的@FixMethodOrder. 这样的注解是继承的。 |
| @TestInstance          | 用于为带注解的测试类配置[测试实例生命周期。](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-instance-lifecycle)这样的注解是继承的。 |
| @DisplayName           | 声明测试类或测试方法的自定义[显示名称。](https://junit.org/junit5/docs/current/user-guide/#writing-tests-display-names)此类注解不会被继承。 |
| @DisplayNameGeneration | 为测试类声明自定义[显示名称生成器。](https://junit.org/junit5/docs/current/user-guide/#writing-tests-display-name-generator)这样的注解是继承的。 |
| @BeforeEach            | 表示被注解的方法应该在当前类中的每个, , , 或方法之前执行； 类似于JUnit 4的. 此类方法是继承的——除非它们被覆盖或取代(即，仅根据签名进行替换，而不考虑 Java 的可见性规则)。 @Test@RepeatedTest@ParameterizedTest@TestFactory@Before |
| @AfterEach             | 表示被注解的方法应该在当前类中的每个, , , 或方法之后执行； 类似于JUnit 4的. 此类方法是继承的——除非它们被覆盖或取代(即，仅根据签名进行替换，而不考虑 Java 的可见性规则)。 @Test@RepeatedTest@ParameterizedTest@TestFactory@After |
| @BeforeAll             | 表示被注解的方法应该在当前类中的所有, , , 和方法之前执行； 类似于JUnit 4的. 此类方法是继承的——除非它们被隐藏、覆盖或取代(即，仅基于签名替换，而不考虑 Java 的可见性规则)——并且必须是，除非使用“每类”[测试实例生命周期](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-instance-lifecycle)。 @Test@RepeatedTest@ParameterizedTest@TestFactory@BeforeClassstatic |
| @AfterAll              | 表示被注解的方法应该在当前类中的所有, , , 和方法之后执行； 类似于JUnit 4的. 此类方法是继承的——除非它们被隐藏、覆盖或取代(即，仅基于签名替换，而不考虑 Java 的可见性规则)——并且必须是，除非使用“每类”[测试实例生命周期](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-instance-lifecycle)。 @Test@RepeatedTest@ParameterizedTest@TestFactory@AfterClassstatic |
| @Nested                | 表示被注解的类是一个非静态的[嵌套测试类](https://junit.org/junit5/docs/current/user-guide/#writing-tests-nested)。在Java 8到Java 15中，@BeforeAll除非使用“每类”[测试实例生命周期](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-instance-lifecycle)@AfterAll，否则不能直接在测试类中使用方法。从 Java 16 开始，可以使用任一测试实例生命周期模式在测试类中声明方法。此类注解不会被继承。@Nested@BeforeAll@AfterAllstatic@Nested |
| @Tag                   | 用于在类或方法级别声明[过滤测试的标签；](https://junit.org/junit5/docs/current/user-guide/#writing-tests-tagging-and-filtering)类似于 TestNG 中的测试组或JUnit 4中的类别。此类注解在类级别继承，但不在方法级别继承。 |
| @Disabled              | 用于[禁用](https://junit.org/junit5/docs/current/user-guide/#writing-tests-disabling)测试类或测试方法；类似于JUnit 4的@Ignore. 此类注解不会被继承。 |
| @Timeout               | 如果测试、测试工厂、测试模板或生命周期方法的执行超过给定持续时间，则用于使测试、测试工厂、测试模板或生命周期方法失败。这样的注解是继承的。 |
| @ExtendWith            | 用于以[声明方式注册扩展](https://junit.org/junit5/docs/current/user-guide/#extensions-registration-declarative)。这样的注解是继承的。 |
| @RegisterExtension     | 用于通过字段以[编程方式注册扩展。](https://junit.org/junit5/docs/current/user-guide/#extensions-registration-programmatic)这些字段是继承的，除非它们被隐藏。 |
| @TempDir               | 用于在生命周期方法或测试方法中通过字段注入或参数注入提供[临时目录；](https://junit.org/junit5/docs/current/user-guide/#writing-tests-built-in-extensions-TempDirectory)位于org.junit.jupiter.api.io包中。 |

>   一些注解目前可能是实验性的。[有关详细信息，请参阅实验性API](https://junit.org/junit5/docs/current/user-guide/#api-evolution-experimental-apis)中的表格。

#### 2.1.1 元注解和组合注解

JUnit Jupiter注解可以用作元注解。这意味着你可以定义自己的组合注解，该注解将自动继承其元注解的语义。

例如，无需@Tag("fast")在整个代码库中进行复制和粘贴(请参阅[标记和过滤](https://junit.org/junit5/docs/current/user-guide/#writing-tests-tagging-and-filtering))，你可以创建一个自定义组合注解，命名@Fast如下。@Fast然后可以用作@Tag("fast")。

```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Tag("fast")
public @interface Fast {
}
```

以下方法演示了注解@Test的用法。@Fast

```java
@Fast
@Test
void myFastTest() {
    // ...
}
```


你甚至可以通过引入自定义注解来更进一步，该@FastTest注解可以用作 和 的直接替代@Tag("fast") 品 @Test。

```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Tag("fast")
@Test
public @interface FastTest {
}
```


JUnit 自动将以下@Test方法识别为标有“快速”的方法。

```java
@FastTest
void myFastTest() {
    // ...
}
```

### 2.2 定义

Platform概念

-   容器

    测试树中包含其他容器或测试作为其子节点的节点(例如测试类)。

-   测试

    测试树中的一个节点，用于验证执行时的预期行为(例如@Test方法)。

Jupiter概念

-   生命周期方法

    任何直接用 @BeforeAll、@AfterAll、@BeforeEach或进行注解或元注解的方法@AfterEach。

-   测试类

    任何顶级类、static成员类或至少包含一个测试方法的[@Nested类](https://junit.org/junit5/docs/current/user-guide/#writing-tests-nested)，即容器。测试类不能而且必须有一个构造函数。abstract

-   测试方法

    @Test任何用, @RepeatedTest, @ParameterizedTest, @TestFactory, 或直接注解或元注解的实例方法 @TestTemplate。除了，它们在测试树中 @Test创建一个容器，用于对测试进行分组，或者可能(对于@TestFactory)，其他容器。

### 2.3 测试类和方法

测试方法和生命周期方法可以在当前测试类中本地声明、从超类继承或从接口继承(请参阅 [测试接口和默认方法](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-interfaces-and-default-methods))。此外，测试方法和生命周期方法不得abstract也不得返回值(@TestFactory 需要返回值的方法除外)。

|      | 类和方法可见性测试类、测试方法和生命周期方法不需要是public，但它们不能是private。通常建议省略public测试类、测试方法和生命周期方法的修饰符，除非有这样做的技术原因——例如，当一个测试类被另一个包中的测试类扩展时。创建类和方法的另一个技术原因public是在使用 Java 模块系统时简化模块路径上的测试。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

下面的测试类演示了@Test方法的使用和所有支持的生命周期方法。有关运行时语义的更多信息，请参阅 [测试执行顺序](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-execution-order)和 [回调的包装行为](https://junit.org/junit5/docs/current/user-guide/#extensions-execution-order-wrapping-behavior)。

一个标准的测试类

```java
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class StandardTests {

    @BeforeAll
    static void initAll() {
    }

    @BeforeEach
    void init() {
    }

    @Test
    void succeedingTest() {
    }

    @Test
    void failingTest() {
        fail("a failing test");
    }

    @Test
    @Disabled("for demonstration purposes")
    void skippedTest() {
        // not executed
    }

    @Test
    void abortedTest() {
        assumeTrue("abc".contains("Z"));
        fail("test should have been aborted");
    }

    @AfterEach
    void tearDown() {
    }

    @AfterAll
    static void tearDownAll() {
    }
}
```

### 2.4 显示名称

测试类和测试方法可以通过@DisplayName 空格、特殊字符甚至表情符号来声明自定义显示名称，这些名称将显示在测试报告中以及由测试运行程序和IDE显示。

```java
java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("A special test case")
class DisplayNameDemo {

    @Test
    @DisplayName("Custom test name containing spaces")
    void testWithDisplayNameContainingSpaces() {
    }

    @Test
    @DisplayName("╯°□°)╯")
    void testWithDisplayNameContainingSpecialCharacters() {
    }

    @Test
    @DisplayName("😱")
    void testWithDisplayNameContainingEmoji() {
    }
}
```

#### 2.4.1 显示名称生成器

JUnit Jupiter支持可以通过 @DisplayNameGeneration注解配置的自定义显示名称生成器。通过@DisplayName注解提供的值始终优先于DisplayNameGenerator.

生成器可以通过实现来创建DisplayNameGenerator。以下是 Jupiter中可用的一些默认值：

| 显示名称生成器        | 行为                                                      |
| :-------------------- | :-------------------------------------------------------- |
| Standard            | 匹配自JUnit Jupiter5.0 发布以来的标准显示名称生成行为。 |
| Simple              | 删除没有参数的方法的尾随括号。                            |
| ReplaceUnderscores  | 用空格替换下划线。                                        |
| IndicativeSentences | 通过连接测试名称和封闭类来生成完整的句子。                |

请注意，对于，你可以使用以下示例中所示的IndicativeSentences方式自定义分隔符和底层生成器。@IndicativeSentencesGeneration

```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.IndicativeSentencesGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DisplayNameGeneratorDemo {

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class A_year_is_not_supported {

        @Test
        void if_it_is_zero() {
        }

        @DisplayName("A negative value for year is not supported by the leap year computation.")
        @ParameterizedTest(name = "For example, year {0} is not supported.")
        @ValueSource(ints = { -1, -4 })
        void if_it_is_negative(int year) {
        }
    }

    @Nested
    @IndicativeSentencesGeneration(separator = " -> ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
    class A_year_is_a_leap_year {

        @Test
        void if_it_is_divisible_by_4_but_not_by_100() {
        }

        @ParameterizedTest(name = "Year {0} is a leap year.")
        @ValueSource(ints = { 2016, 2020, 2048 })
        void if_it_is_one_of_the_following_years(int year) {
        }
    }
}
```

```shell
+-- DisplayNameGeneratorDemo [OK]
  +-- A year is not supported [OK]
  | +-- A negative value for year is not supported by the leap year computation. [OK]
  | | +-- For example, year -1 is not supported. [OK]
  | | '-- For example, year -4 is not supported. [OK]
  | '-- if it is zero() [OK]
  '-- A year is a leap year [OK]
    +-- A year is a leap year -> if it is divisible by 4 but not by 100. [OK]
    '-- A year is a leap year -> if it is one of the following years. [OK]
      +-- Year 2016 is a leap year. [OK]
      +-- Year 2020 is a leap year. [OK]
      '-- Year 2048 is a leap year. [OK]
```


#### 2.4.2 设置默认显示名称生成器

你可以使用junit.jupiter.displayname.generator.default [配置参数](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params)DisplayNameGenerator来指定你希望默认使用的完全限定类名。就像通过@DisplayNameGeneration注解配置的显示名称生成器一样，提供的类必须实现DisplayNameGenerator接口。默认显示名称生成器将用于所有测试，除非@DisplayNameGeneration注解出现在封闭的测试类或测试接口上。通过 @DisplayName注解提供的值始终优先于 DisplayNameGenerator.

例如，要ReplaceUnderscores默认使用显示名称生成器，你应该将配置参数设置为相应的完全限定类名(例如，in src/test/resources/junit-platform.properties)：


junit.jupiter.displayname.generator.default = 
    org.junit.jupiter.api.DisplayNameGenerator$ReplaceUnderscores


同样，你可以指定任何实现 DisplayNameGenerator.

总之，测试类或方法的显示名称根据以下优先级规则确定：

1.  @DisplayName注解的值(如果存在)
2.  通过调用 注解中DisplayNameGenerator指定的(如果存在)@DisplayNameGeneration
3.  通过调用DisplayNameGenerator通过配置参数配置的默认值(如果存在)
4.  通过调用org.junit.jupiter.api.DisplayNameGenerator.Standard

### 2.5 断言

JUnit Jupiter附带了许多JUnit 4拥有的断言方法，并添加了一些非常适合与Java 8lambda 一起使用的断言方法。所有JUnit Jupiter断言都是类中的static方法org.junit.jupiter.api.Assertions。

```java
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofMinutes;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;

import example.domain.Person;
import example.util.Calculator;

import org.junit.jupiter.api.Test;

class AssertionsDemo {

    private final Calculator calculator = new Calculator();

    private final Person person = new Person("Jane", "Doe");

    @Test
    void standardAssertions() {
        assertEquals(2, calculator.add(1, 1));
        assertEquals(4, calculator.multiply(2, 2),
                "The optional failure message is now the last parameter");
        assertTrue('a' < 'b', () -> "Assertion messages can be lazily evaluated -- "
                + "to avoid constructing complex messages unnecessarily.");
    }

    @Test
    void groupedAssertions() {
        // In a grouped assertion all assertions are executed, and all
        // failures will be reported together.
        assertAll("person",
            () -> assertEquals("Jane", person.getFirstName()),
            () -> assertEquals("Doe", person.getLastName())
        );
    }

    @Test
    void dependentAssertions() {
        // Within a code block, if an assertion fails the
        // subsequent code in the same block will be skipped.
        assertAll("properties",
            () -> {
                String firstName = person.getFirstName();
                assertNotNull(firstName);

                // Executed only if the previous assertion is valid.
                assertAll("first name",
                    () -> assertTrue(firstName.startsWith("J")),
                    () -> assertTrue(firstName.endsWith("e"))
                );
            },
            () -> {
                // Grouped assertion, so processed independently
                // of results of first name assertions.
                String lastName = person.getLastName();
                assertNotNull(lastName);

                // Executed only if the previous assertion is valid.
                assertAll("last name",
                    () -> assertTrue(lastName.startsWith("D")),
                    () -> assertTrue(lastName.endsWith("e"))
                );
            }
        );
    }

    @Test
    void exceptionTesting() {
        Exception exception = assertThrows(ArithmeticException.class, () ->
            calculator.divide(1, 0));
        assertEquals("/ by zero", exception.getMessage());
    }

    @Test
    void timeoutNotExceeded() {
        // The following assertion succeeds.
        assertTimeout(ofMinutes(2), () -> {
            // Perform task that takes less than 2 minutes.
        });
    }

    @Test
    void timeoutNotExceededWithResult() {
        // The following assertion succeeds, and returns the supplied object.
        String actualResult = assertTimeout(ofMinutes(2), () -> {
            return "a result";
        });
        assertEquals("a result", actualResult);
    }

    @Test
    void timeoutNotExceededWithMethod() {
        // The following assertion invokes a method reference and returns an object.
        String actualGreeting = assertTimeout(ofMinutes(2), AssertionsDemo::greeting);
        assertEquals("Hello, World!", actualGreeting);
    }

    @Test
    void timeoutExceeded() {
        // The following assertion fails with an error message similar to:
        // execution exceeded timeout of 10 ms by 91 ms
        assertTimeout(ofMillis(10), () -> {
            // Simulate task that takes more than 10 ms.
            Thread.sleep(100);
        });
    }

    @Test
    void timeoutExceededWithPreemptiveTermination() {
        // The following assertion fails with an error message similar to:
        // execution timed out after 10 ms
        assertTimeoutPreemptively(ofMillis(10), () -> {
            // Simulate task that takes more than 10 ms.
            new CountDownLatch(1).await();
        });
    }

    private static String greeting() {
        return "Hello, World!";
    }
}
```


> 抢占式超时assertTimeoutPreemptively()类中的各种assertTimeoutPreemptively()方法Assertions执行提供的executable或supplier在与调用代码不同的线程中执行。executable如果在或supplier依赖java.lang.ThreadLocal存储中执行的代码，此行为可能会导致不良副作用。一个常见的例子是 Spring 框架中的事务测试支持。ThreadLocal具体来说，Spring 的测试支持在调用测试方法之前将事务状态绑定到当前线程(通过 a )。因此，如果一个executable或 supplier提供给assertTimeoutPreemptively()调用参与事务的 Spring 管理的组件，则这些组件采取的任何操作都不会与测试管理的事务一起回滚。相反，即使回滚了测试管理的事务，此类操作也会提交给持久存储(例如，关系数据库)。ThreadLocal其他依赖存储的框架可能会遇到类似的副作用 。


#### 2.5.1 Kotlin断言支持

JUnit Jupiter还附带了一些适合在[Kotlin](https://kotlinlang.org/)中使用的断言方法。所有JUnit JupiterKotlin 断言都是包中的顶级函数org.junit.jupiter.api。

```kotlin
import example.domain.Person
import example.util.Calculator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.assertTimeout
import org.junit.jupiter.api.assertTimeoutPreemptively
import java.time.Duration

class KotlinAssertionsDemo {

    private val person = Person("Jane", "Doe")
    private val people = setOf(person, Person("John", "Doe"))

    @Test
    fun exception absence testing() {
        val calculator = Calculator()
        val result = assertDoesNotThrow("Should not throw an exception") {
            calculator.divide(0, 1)
        }
        assertEquals(0, result)
    }

    @Test
    fun expected exception testing() {
        val calculator = Calculator()
        val exception = assertThrows<ArithmeticException> ("Should throw an exception") {
            calculator.divide(1, 0)
        }
        assertEquals("/ by zero", exception.message)
    }

    @Test
    fun grouped assertions() {
        assertAll(
            "Person properties",
            { assertEquals("Jane", person.firstName) },
            { assertEquals("Doe", person.lastName) }
        )
    }

    @Test
    fun grouped assertions from a stream() {
        assertAll(
            "People with first name starting with J",
            people
                .stream()
                .map {
                    // This mapping returns Stream<() -> Unit>
                    { assertTrue(it.firstName.startsWith("J")) }
                }
        )
    }

    @Test
    fun grouped assertions from a collection() {
        assertAll(
            "People with last name of Doe",
            people.map { { assertEquals("Doe", it.lastName) } }
        )
    }

    @Test
    fun timeout not exceeded testing() {
        val fibonacciCalculator = FibonacciCalculator()
        val result = assertTimeout(Duration.ofMillis(1000)) {
            fibonacciCalculator.fib(14)
        }
        assertEquals(377, result)
    }

    @Test
    fun timeout exceeded with preemptive termination() {
        // The following assertion fails with an error message similar to:
        // execution timed out after 10 ms
        assertTimeoutPreemptively(Duration.ofMillis(10)) {
            // Simulate task that takes more than 10 ms.
            Thread.sleep(100)
        }
    }
}
```


#### 2.5.2 第三方断言库

尽管JUnit Jupiter提供的断言工具足以满足许多测试场景，但有时仍 需要或需要更强大的功能和附加功能，例如匹配器。在这种情况下，JUnit 团队建议使用第三方断言库，如[AssertJ](https://joel-costigliola.github.io/assertj/)、[Hamcrest](https://hamcrest.org/JavaHamcrest/)、[Truth](https://truth.dev/)等。开发人员因此可以自由选择使用他们选择的断言库。

例如，匹配器和流畅的 API 的组合可用于使断言更具描述性和可读性。但是，JUnit Jupiter的org.junit.jupiter.api.Assertions类没有提供 [assertThat()](https://junit.org/junit4/javadoc/latest/org/junit/Assert.html#assertThat) 像 JUnit 4org.junit.Assert类中接受 Hamcrest 的方法[Matcher](https://junit.org/junit4/javadoc/latest/org/hamcrest/Matcher.html)。相反，鼓励开发人员使用第三方断言库提供的对匹配器的内置支持。

以下示例演示了如何assertThat()在JUnit Jupiter测试中使用 Hamcrest 的支持。只要将 Hamcrest 库添加到类路径中，就可以静态导入 、 等方法，assertThat()然后在测试中使用它们，如下面的方法所示。is()equalTo()assertWithHamcrestMatcher()

```java
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import example.util.Calculator;

import org.junit.jupiter.api.Test;

class HamcrestAssertionsDemo {

    private final Calculator calculator = new Calculator();

    @Test
    void assertWithHamcrestMatcher() {
        assertThat(calculator.subtract(4, 1), is(equalTo(3)));
    }

}
```

当然，基于JUnit 4编程模型的遗留测试可以继续使用 org.junit.Assert#assertThat.

### 2.6 假设

JUnit Jupiter附带了JUnit 4提供的假设方法的一个子集，并添加了一些非常适合与Java 8lambda 表达式和方法引用一起使用的方法。所有JUnit Jupiter假设都是 org.junit.jupiter.api.Assumptions类中的静态方法。

```java
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import example.util.Calculator;

import org.junit.jupiter.api.Test;

class AssumptionsDemo {

    private final Calculator calculator = new Calculator();

    @Test
    void testOnlyOnCiServer() {
        assumeTrue("CI".equals(System.getenv("ENV")));
        // remainder of test
    }

    @Test
    void testOnlyOnDeveloperWorkstation() {
        assumeTrue("DEV".equals(System.getenv("ENV")),
            () -> "Aborting test: not on developer workstation");
        // remainder of test
    }

    @Test
    void testInAllEnvironments() {
        assumingThat("CI".equals(System.getenv("ENV")),
            () -> {
                // perform these assertions only on the CI server
                assertEquals(2, calculator.divide(4, 2));
            });

        // perform these assertions in all environments
        assertEquals(42, calculator.multiply(6, 7));
    }
}
```

> 从JUnit Jupiter 5.4开始，也可以使用JUnit 4org.junit.Assume类中的方法进行假设。具体来说，JUnit Jupiter支持JUnit 4AssumptionViolatedException来发出测试应中止而不是标记为失败的信号。

### 2.7 禁用测试

可以通过注解、[条件测试执行](https://junit.org/junit5/docs/current/user-guide/#writing-tests-conditional-execution)@Disabled 中讨论的注解之一 或自定义的.[ExecutionCondition](https://junit.org/junit5/docs/current/user-guide/#extensions-conditions)

这是一个@Disabled测试类。

```java
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Disabled until bug #99 has been fixed")
class DisabledClassDemo {

    @Test
    void testWillBeSkipped() {
    }
}
```

这是一个包含@Disabled测试方法的测试类。

```java
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class DisabledTestsDemo {

    @Disabled("Disabled until bug #42 has been resolved")
    @Test
    void testWillBeSkipped() {
    }

    @Test
    void testWillBeExecuted() {
    }
}
```

> @Disabled可以在不提供理由的情况下宣布；然而，JUnit 团队建议开发人员提供一个简短的解释，说明为什么禁用测试类或测试方法。因此，上面的例子都显示了原因的使用——例如，@Disabled("Disabled until bug #42 has been resolved")。一些开发团队甚至要求在自动可追溯性等 原因中存在问题跟踪号。

### 2.8 条件测试执行

JUnit Jupiter中的[ExecutionCondition](https://junit.org/junit5/docs/current/user-guide/#extensions-conditions)扩展 API 允许开发人员以编程方式启用或禁用容器或基于特定条件进行测试。这种情况的最简单示例是支持注解 的内置 (请参阅[禁用测试](https://junit.org/junit5/docs/current/user-guide/#writing-tests-disabling))。除了，JUnit Jupiter还支持包中的其他几个基于注解的条件 ，允许开发人员以声明方式启用或禁用容器和测试。当注册了多个扩展时，一旦其中一个条件返回禁用，容器或测试就会被禁用DisabledCondition@Disabled@Disabledorg.junit.jupiter.api.conditionExecutionCondition. 如果你希望提供有关它们可能被禁用的原因的详细信息，与这些内置条件关联的每个注解都有一个disabledReason可用于该目的的属性。

有关详细信息，请参阅[ExecutionCondition](https://junit.org/junit5/docs/current/user-guide/#extensions-conditions)以下部分。

|      | 组合注解请注意，以下部分中列出的任何条件注解也可以用作元注解以创建自定义组合注解。例如，[@EnabledOnOs 演示](https://junit.org/junit5/docs/current/user-guide/#writing-tests-conditional-execution-os-demo)@TestOnMac中的注解 展示了如何将和合并到一个可重用的注解中。@Test@EnabledOnOs |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

|      | 除非另有说明，否则以下部分中列出的每个条件注解只能在给定的测试接口、测试类或测试方法上声明一次。如果条件注解在给定元素上直接存在、间接存在或元存在多次，则只会使用 JUnit 发现的第一个此类注解；任何额外的声明都将被忽略。但是请注意，每个条件注解都可以与org.junit.jupiter.api.condition包中的其他条件注解结合使用。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 2.8.1 操作系统和架构条件

@EnabledOnOs可以通过和@DisabledOnOs 注解在特定操作系统、体系结构或两者的组合上启用或禁用容器或测试。

基于操作系统的条件执行

```java
@Test
@EnabledOnOs(MAC)
void onlyOnMacOs() {
    // ...
}

@TestOnMac
void testOnMac() {
    // ...
}

@Test
@EnabledOnOs({ LINUX, MAC })
void onLinuxOrMac() {
    // ...
}

@Test
@DisabledOnOs(WINDOWS)
void notOnWindows() {
    // ...
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Test
@EnabledOnOs(MAC)
@interface TestOnMac {
}
```

基于架构的条件执行

```java
@Test
@EnabledOnOs(architectures = "aarch64")
void onAarch64() {
    // ...
}

@Test
@DisabledOnOs(architectures = "x86_64")
void notOnX86_64() {
    // ...
}

@Test
@EnabledOnOs(value = MAC, architectures = "aarch64")
void onNewMacs() {
    // ...
}

@Test
@DisabledOnOs(value = MAC, architectures = "aarch64")
void notOnNewMacs() {
    // ...
}
```

#### 2.8.2 Java运行时环境条件

可以通过和 注解在特定版本的 Java 运行时环境 (JRE) 上启用或禁用容器或测试，或者通过@EnabledOnJre和@DisabledOnJre注解在特定范围的 JRE版本上启用或禁用。范围默认为下边界 ( ) 和上边界 ( )，这允许使用半开放范围。@EnabledForJreRange@DisabledForJreRangeJRE.JAVA_8minJRE.OTHERmax

```java
@Test
@EnabledOnJre(JAVA_8)
void onlyOnJava8() {
    // ...
}

@Test
@EnabledOnJre({ JAVA_9, JAVA_10 })
void onJava9Or10() {
    // ...
}

@Test
@EnabledForJreRange(min = JAVA_9, max = JAVA_11)
void fromJava9to11() {
    // ...
}

@Test
@EnabledForJreRange(min = JAVA_9)
void fromJava9toCurrentJavaFeatureNumber() {
    // ...
}

@Test
@EnabledForJreRange(max = JAVA_11)
void fromJava8To11() {
    // ...
}

@Test
@DisabledOnJre(JAVA_9)
void notOnJava9() {
    // ...
}

@Test
@DisabledForJreRange(min = JAVA_9, max = JAVA_11)
void notFromJava9to11() {
    // ...
}

@Test
@DisabledForJreRange(min = JAVA_9)
void notFromJava9toCurrentJavaFeatureNumber() {
    // ...
}

@Test
@DisabledForJreRange(max = JAVA_11)
void notFromJava8to11() {
    // ...
}
```

#### 2.8.3 原生镜像条件

可以通过 和注解在[GraalVM 本机映像](https://www.graalvm.org/reference-manual/native-image/)中启用或禁用容器或测试 。[这些注解通常在使用来自 GraalVM Native Build Tools](https://graalvm.github.io/native-build-tools/latest/)项目的 Gradle 和 Maven 插件在本机图像中运行测试时使用。@EnabledInNativeImage@DisabledInNativeImage

```java
@Test
@EnabledInNativeImage
void onlyWithinNativeImage() {
    // ...
}

@Test
@DisabledInNativeImage
void neverWithinNativeImage() {
    // ...
}
```

#### 2.8.4 系统属性条件

可以通过和 注解根据namedJVM 系统属性的值启用或禁用容器或测试。通过属性提供的值将被解释为正则表达式。@EnabledIfSystemProperty@DisabledIfSystemPropertymatches

```java
@Test
@EnabledIfSystemProperty(named = "os.arch", matches = ".64.")
void onlyOn64BitArchitectures() {
    // ...
}

@Test
@DisabledIfSystemProperty(named = "ci-server", matches = "true")
void notOnCiServer() {
    // ...
}
```

>   从JUnit Jupiter5.6 开始，@EnabledIfSystemProperty和@DisabledIfSystemProperty是 可重复的注解。因此，这些注解可能会在测试接口、测试类或测试方法上多次声明。具体来说，如果这些注解直接存在、间接存在或元存在于给定元素上，它们将被发现。

#### 2.8.5 环境变量条件

可以根据named 来自底层操作系统的环境变量值通过 @EnabledIfEnvironmentVariable和@DisabledIfEnvironmentVariable注解启用或禁用容器或测试。通过matches属性提供的值将被解释为正则表达式。

```java
@Test
@EnabledIfEnvironmentVariable(named = "ENV", matches = "staging-server")
void onlyOnStagingServer() {
    // ...
}

@Test
@DisabledIfEnvironmentVariable(named = "ENV", matches = ".development.")
void notOnDeveloperWorkstation() {
    // ...
}
```

>   从JUnit Jupiter5.6 开始，@EnabledIfEnvironmentVariable和 @DisabledIfEnvironmentVariable是可重复的注解。因此，这些注解可能会在测试接口、测试类或测试方法上多次声明。具体来说，如果这些注解直接存在、间接存在或元存在于给定元素上，它们将被发现。

#### 2.8.6 自定义条件

作为实现 的替代方法，可以根据通过和注解配置的条件方法[ExecutionCondition](https://junit.org/junit5/docs/current/user-guide/#extensions-conditions)启用或禁用容器或测试。条件方法必须具有 返回类型，并且可以不接受参数或接受单个参数。@EnabledIf@DisabledIfbooleanExtensionContext

下面的测试类演示了如何配置名为 customConditionvia@EnabledIf和的本地方法@DisabledIf。

```java
@Test
@EnabledIf("customCondition")
void enabled() {
    // ...
}

@Test
@DisabledIf("customCondition")
void disabled() {
    // ...
}

boolean customCondition() {
    return true;
}
```

或者，条件方法可以位于测试类之外。在这种情况下，它必须由其完全限定名称引用，如以下示例所示。

```java
package example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

class ExternalCustomConditionDemo {

    @Test
    @EnabledIf("example.ExternalCondition#customCondition")
    void enabled() {
        // ...
    }

}

class ExternalCondition {

    static boolean customCondition() {
        return true;
    }

}
```

>   当@EnabledIfor@DisabledIf在类级别使用时，条件方法必须始终为static。位于外部类中的条件方法也必须是static. 在任何其他情况下，你可以使用静态方法或实例方法作为条件方法。

>   通常情况下，你可以将实用程序类中的现有静态方法用作自定义条件。例如，java.awt.GraphicsEnvironment提供了一种public static boolean isHeadless() 方法，可用于判断当前环境是否不支持图形显示。因此，如果你有一个依赖于图形支持的测试，你可以在此类支持不可用时禁用它，如下所示。@DisabledIf(value = "java.awt.GraphicsEnvironment#isHeadless",    disabledReason = "headless environment")

### 2.9 标记和过滤

测试类和方法可以通过@Tag注解来标记。这些标记稍后可用于过滤[测试发现和执行](https://junit.org/junit5/docs/current/user-guide/#running-tests)。有关 JUnit 平台中标签支持的更多信息，请参阅 [标签部分。](https://junit.org/junit5/docs/current/user-guide/#running-tests-tags)

```java
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("fast")
@Tag("model")
class TaggingDemo {

    @Test
    @Tag("taxes")
    void testingTaxCalculation() {
    }
}
```

>   有关演示如何为标签创建自定义注解的示例 ， 请参阅[元注解和组合注解。](https://junit.org/junit5/docs/current/user-guide/#writing-tests-meta-annotations)

### 2.10 测试执行顺序

默认情况下，测试类和方法将使用确定性但故意不明显的算法进行排序。这确保了测试套件的后续运行以相同的顺序执行测试类和测试方法，从而允许可重复的构建。

>   有关测试方法和 测试类的定义，请参阅[测试类和方法](https://junit.org/junit5/docs/current/user-guide/#writing-tests-classes-and-methods)。

#### 2.10.1 方法顺序

虽然真正的单元测试通常不应该依赖于它们的执行顺序，但有时有必要强制执行特定的测试方法执行顺序——例如，在编写集成测试或功能测试时，测试顺序是重要的，尤其是与 @TestInstance(Lifecycle.PER_CLASS).

要控制测试方法的执行顺序，请使用注解你的测试类或测试接口@TestMethodOrder并指定所需的MethodOrderer 实现。你可以实现自己的自定义MethodOrderer或使用以下内置MethodOrderer实现之一。

-   MethodOrderer.DisplayName：根据显示名称按字母数字顺序对测试方法进行排序(请参阅[显示名称生成优先规则](https://junit.org/junit5/docs/current/user-guide/#writing-tests-display-name-generator-precedence-rules))
-   MethodOrderer.MethodName：根据名称和形式参数列表按字母数字顺序对测试方法进行排序
-   MethodOrderer.OrderAnnotation：根据通过注解指定的值对测试方法进行数字排序@Order
-   MethodOrderer.Random：伪随机排序测试方法并支持自定义种子的配置
-   MethodOrderer.Alphanumeric：根据名称和形式参数列表按字母数字顺序对测试方法进行排序；已弃用MethodOrderer.MethodName，将在 6.0 中删除

>   另请参阅：[回调的包装行为](https://junit.org/junit5/docs/current/user-guide/#extensions-execution-order-wrapping-behavior)

以下示例演示了如何保证测试方法按照@Order注解指定的顺序执行。

```java
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
class OrderedTestsDemo {

    @Test
    @Order(1)
    void nullValues() {
        // perform assertions against null values
    }

    @Test
    @Order(2)
    void emptyValues() {
        // perform assertions against empty values
    }

    @Test
    @Order(3)
    void validValues() {
        // perform assertions against valid values
    }
}
```

##### 设置默认方法排序器

你可以使用junit.jupiter.testmethod.order.default [配置参数](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params)MethodOrderer来指定你希望默认使用的完全限定类名 。就像通过@TestMethodOrder注解配置的订购者一样，提供的类必须实现该 MethodOrderer接口。默认排序器将用于所有测试，除非 @TestMethodOrder注解出现在封闭的测试类或测试接口上。

例如，要MethodOrderer.OrderAnnotation默认使用方法 orderer，你应该将配置参数设置为相应的完全限定类名(例如，in src/test/resources/junit-platform.properties)：


junit.jupiter.testmethod.order.default = 
    org.junit.jupiter.api.MethodOrderer$OrderAnnotation


同样，你可以指定任何实现 MethodOrderer.

#### 2.10.2 类顺序

虽然测试类通常不应该依赖于它们的执行顺序，但有时需要强制执行特定的测试类执行顺序。你可能希望以随机顺序执行测试类以确保测试类之间没有意外的依赖性，或者你可能希望对测试类进行排序以优化构建时间，如以下场景所述。

-   首先运行先前失败的测试和更快的测试：“快速失败”模式
-   启用并行执行后，首先运行较长的测试：“最短测试计划执行持续时间”模式
-   各种其他用例

要为整个测试套件 全局配置测试类执行顺序，请使用junit.jupiter.testclass.order.default [配置参数](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params)ClassOrderer指定你要使用的完全限定类名。提供的类必须实现该ClassOrderer接口。

你可以实现自己的自定义ClassOrderer或使用以下内置 ClassOrderer实现之一。

-   ClassOrderer.ClassName：根据完全限定的类名按字母数字顺序对测试类进行排序
-   ClassOrderer.DisplayName：根据显示名称按字母数字顺序对测试类进行排序(请参阅[显示名称生成优先规则](https://junit.org/junit5/docs/current/user-guide/#writing-tests-display-name-generator-precedence-rules))
-   ClassOrderer.OrderAnnotation：根据通过注解指定的值对测试类进行数字排序@Order
-   ClassOrderer.Random：伪随机排序测试类并支持自定义种子的配置

例如，要在测试类@Order上使用注解，你应该使用具有相应完全限定类名(例如，in )的配置参数来配置类排序器：ClassOrderer.OrderAnnotationsrc/test/resources/junit-platform.properties


junit.jupiter.testclass.order.default = 
    org.junit.jupiter.api.ClassOrderer$OrderAnnotation


配置的ClassOrderer将应用于所有顶级测试类(包括 static嵌套测试类)和@Nested测试类。

>   顶级测试类将相对于彼此排序；然而，@Nested 测试类将相对于@Nested共享相同 封闭类的其他测试类排序。

要在本地为测试类配置测试类执行顺序，请在要排序的测试类的封闭类上@Nested声明 @TestClassOrder注解，并提供对要在注解中直接使用@Nested的实现的类引用。配置的 将递归地应用于测试类及其测试类。请注意，局部声明始终会覆盖继承的 声明或通过配置参数全局 配置的声明。ClassOrderer@TestClassOrderClassOrderer@Nested@Nested@TestClassOrder@TestClassOrderClassOrdererjunit.jupiter.testclass.order.default

以下示例演示了如何保证测试类按照注解@Nested指定的顺序执行。@Order

```java
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class OrderedNestedTestClassesDemo {

    @Nested
    @Order(1)
    class PrimaryTests {

        @Test
        void test1() {
        }
    }

    @Nested
    @Order(2)
    class SecondaryTests {

        @Test
        void test2() {
        }
    }
}
```

### 2.11 测试实例生命周期

为了允许单独的测试方法独立执行并避免由于可变测试实例状态而导致的意外副作用，JUnit 在执行每个测试方法之前为每个测试类创建一个新实例(请参阅 [测试类和方法](https://junit.org/junit5/docs/current/user-guide/#writing-tests-classes-and-methods))。这种“按方法”测试实例生命周期是JUnit Jupiter中的默认行为，类似于所有以前版本的 JUnit。

|      | 请注意，即使在“按方法”测试实例生命周期模式处于活动状态时， 如果通过[条件](https://junit.org/junit5/docs/current/user-guide/#writing-tests-conditional-execution)(例如，、等 )禁用了给定 的测试方法 ，测试类仍将被实例化。@Disabled@DisabledOnOs |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

如果你希望JUnit Jupiter在同一个测试实例上执行所有测试方法，请使用@TestInstance(Lifecycle.PER_CLASS). 使用此模式时，将为每个测试类创建一次新的测试实例。因此，如果你的测试方法依赖于存储在实例变量中的状态，你可能需要在 @BeforeEach或@AfterEach方法中重置该状态。

“per-class”模式比默认的“per-method”模式有一些额外的好处。具体来说，使用“每类”模式，可以在非静态方法和接口方法上声明@BeforeAll和 。因此，“每类”模式也使得在测试类中使用和 方法成为可能。@AfterAlldefault@BeforeAll@AfterAll@Nested

|      | 从 Java 16 开始，@BeforeAll方法@AfterAll可以像 static在@Nested测试类中一样声明。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

如果你使用 Kotlin 编程语言编写测试，你可能还会发现通过切换到“每类”测试实例生命周期模式更容易实现@BeforeAll和方法。@AfterAll

#### 2.11.1 更改默认测试实例生命周期

如果测试类或测试接口未使用 注解@TestInstance，JUnit Jupiter将使用默认的生命周期模式。标准的默认模式是PER_METHOD；但是，可以更改整个测试计划执行的默认值。要更改默认测试实例生命周期模式，请将 junit.jupiter.testinstance.lifecycle.default 配置参数设置为 中定义的枚举常量的名称TestInstance.Lifecycle，忽略大小写。这可以作为 JVM 系统属性提供，作为传递给 的配置参数， 或通过 JUnit 平台配置文件提供(有关详细信息，请参阅[配置参数](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params))。LauncherDiscoveryRequestLauncher

例如，要将默认测试实例生命周期模式设置为Lifecycle.PER_CLASS，你可以使用以下系统属性启动 JVM。


-Djunit.jupiter.testinstance.lifecycle.default=per_class


但是请注意，通过 JUnit 平台配置文件设置默认测试实例生命周期模式是一种更强大的解决方案，因为配置文件可以与你的项目一起检入版本控制系统，因此可以在 IDE 和你的构建软件中使用.

要将默认测试实例生命周期模式设置为Lifecycle.PER_CLASS通过 JUnit 平台配置文件，请创建一个junit-platform.properties在类路径的根目录中命名的文件(例如，src/test/resources)，其中包含以下内容。


junit.jupiter.testinstance.lifecycle.default = per_class


|      | 如果不一致地应用， 更改默认测试实例生命周期模式可能会导致不可预测的结果和脆弱的构建。例如，如果构建将“按类”语义配置为默认值，但 IDE 中的测试是使用“按方法”语义执行的，那么调试构建服务器上发生的错误可能会很困难。因此，建议更改 JUnit 平台配置文件中的默认值，而不是通过 JVM 系统属性。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

### 2.12 嵌套测试

@Nested测试赋予测试编写者更多的能力来表达几组测试之间的关系。此类嵌套测试利用 Java 的嵌套类，并有助于对测试结构进行分层思考。这是一个详细的示例，既作为源代码又作为在 IDE 中执行的屏幕截图。

用于测试堆栈的嵌套测试套件

```java
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EmptyStackException;
import java.util.Stack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("A stack")
class TestingAStackDemo {

    Stack<Object> stack;

    @Test
    @DisplayName("is instantiated with new Stack()")
    void isInstantiatedWithNew() {
        new Stack<>();
    }

    @Nested
    @DisplayName("when new")
    class WhenNew {

        @BeforeEach
        void createNewStack() {
            stack = new Stack<>();
        }

        @Test
        @DisplayName("is empty")
        void isEmpty() {
            assertTrue(stack.isEmpty());
        }

        @Test
        @DisplayName("throws EmptyStackException when popped")
        void throwsExceptionWhenPopped() {
            assertThrows(EmptyStackException.class, stack::pop);
        }

        @Test
        @DisplayName("throws EmptyStackException when peeked")
        void throwsExceptionWhenPeeked() {
            assertThrows(EmptyStackException.class, stack::peek);
        }

        @Nested
        @DisplayName("after pushing an element")
        class AfterPushing {

            String anElement = "an element";

            @BeforeEach
            void pushAnElement() {
                stack.push(anElement);
            }

            @Test
            @DisplayName("it is no longer empty")
            void isNotEmpty() {
                assertFalse(stack.isEmpty());
            }

            @Test
            @DisplayName("returns the element when popped and is empty")
            void returnElementWhenPopped() {
                assertEquals(anElement, stack.pop());
                assertTrue(stack.isEmpty());
            }

            @Test
            @DisplayName("returns the element when peeked but remains not empty")
            void returnElementWhenPeeked() {
                assertEquals(anElement, stack.peek());
                assertFalse(stack.isEmpty());
            }
        }
    }
}
```

在IDE中执行此示例时，GUI中的测试执行树将类似于下图。

![编写测试嵌套测试ide](https://junit.org/junit5/docs/current/user-guide/images/writing-tests_nested_test_ide.png)

在IDE中执行嵌套测试

在此示例中，通过为设置代码定义分层生命周期方法，将来自外部测试的先决条件用于内部测试。例如，createNewStack()是一个 @BeforeEach生命周期方法，它在定义它的测试类中以及在定义它的类下面的嵌套树中的所有级别中使用。

外部测试的设置代码在内部测试执行之前运行这一事实使你能够独立运行所有测试。你甚至可以单独运行内部测试而不运行外部测试，因为始终会执行外部测试的设置代码。

|      | 只有非静态嵌套类(即内部类)可以作为@Nested测试类。嵌套可以任意深，并且这些内部类受到完整的生命周期支持，但有一个例外：默认情况下@BeforeAll方法@AfterAll不起作用。原因是 Java 不允许在 Java 16 之前的内部类中使用成员。但是，可以通过使用(请参阅 [测试实例生命周期](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-instance-lifecycle))注解测试类来规避此限制。如果你使用的是 Java 16 或更高版本， 并且可以在测试类中声明方法，则此限制不再适用。 static@Nested@TestInstance(Lifecycle.PER_CLASS)@BeforeAll@AfterAllstatic@Nested |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

### 2.13 构造函数和方法的依赖注入

在所有以前的 JUnit 版本中，测试构造函数或方法不允许有参数(至少在标准Runner实现中不允许)。作为JUnit Jupiter的主要变化之一，现在允许测试构造函数和方法具有参数。这允许更大的灵活性并为构造函数和方法启用依赖注入。

ParameterResolver为希望在运行时动态 解析参数的测试扩展定义 API。如果测试类构造函数、测试方法或 生命周期方法(请参阅[测试类和方法](https://junit.org/junit5/docs/current/user-guide/#writing-tests-classes-and-methods))接受参数，则该参数必须在运行时由已注册的ParameterResolver.

当前有三个自动注册的内置解析器。

-   TestInfoParameterResolver：如果构造函数或方法参数的类型为 TestInfo，TestInfoParameterResolver则将提供TestInfo 对应于当前容器或测试的实例作为参数的值。然后 TestInfo可以使用 来检索有关当前容器或测试的信息，例如显示名称、测试类、测试方法和相关标签。显示名称可以是技术名称，例如测试类或测试方法的名称，也可以是通过配置的自定义名称@DisplayName。

    TestInfo充当TestName JUnit 4规则的直接替代。下面演示了如何TestInfo注入测试构造函数、 @BeforeEach方法和@Test方法。

```java
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

@DisplayName("TestInfo Demo")
class TestInfoDemo {

    TestInfoDemo(TestInfo testInfo) {
        assertEquals("TestInfo Demo", testInfo.getDisplayName());
    }

    @BeforeEach
    void init(TestInfo testInfo) {
        String displayName = testInfo.getDisplayName();
        assertTrue(displayName.equals("TEST 1") || displayName.equals("test2()"));
    }

    @Test
    @DisplayName("TEST 1")
    @Tag("my-tag")
    void test1(TestInfo testInfo) {
        assertEquals("TEST 1", testInfo.getDisplayName());
        assertTrue(testInfo.getTags().contains("my-tag"));
    }

    @Test
    void test2() {
    }
}
```

-   RepetitionInfoParameterResolver：如果 、 或方法中的方法参数@RepeatedTest属于 @BeforeEach类型@AfterEach，RepetitionInfo则将 RepetitionInfoParameterResolver提供 的实例RepetitionInfo。 RepetitionInfo然后可用于检索有关当前重复的信息以及相应@RepeatedTest. 但是请注意，它RepetitionInfoParameterResolver没有在 a 的上下文之外注册@RepeatedTest。请参阅[重复测试示例](https://junit.org/junit5/docs/current/user-guide/#writing-tests-repeated-tests-examples)。

-   TestReporterParameterResolver：如果构造函数或方法参数的类型为 TestReporter，TestReporterParameterResolver则将提供 的实例 TestReporter。可TestReporter用于发布有关当前测试运行的附加数据。数据可以通过 中的reportingEntryPublished()方法使用TestExecutionListener，允许在 IDE 中查看或包含在报告中。

    在JUnit Jupiter中，你应该在 JUnit 4或JUnit 4中使用TestReporterwhere you used to print information 。使用会将所有报告的条目输出到. 此外，一些 IDE 将报告条目打印到用户界面或在用户界面中显示它们以获取测试结果。stdoutstderr@RunWith(JUnitPlatform.class)stdoutstdout

```java
class TestReporterDemo {

    @Test
    void reportSingleValue(TestReporter testReporter) {
        testReporter.publishEntry("a status message");
    }

    @Test
    void reportKeyValuePair(TestReporter testReporter) {
        testReporter.publishEntry("a key", "a value");
    }

    @Test
    void reportMultipleKeyValuePairs(TestReporter testReporter) {
        Map<String, String> values = new HashMap<>();
        values.put("user name", "dk38");
        values.put("award year", "1974");

        testReporter.publishEntry(values);
    }
}
```

|      | 必须通过注册适当的扩展来显式启用其他 参数[解析器](https://junit.org/junit5/docs/current/user-guide/#extensions)@ExtendWith。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

查看RandomParametersExtension自定义 ParameterResolver. 虽然不打算用于生产，但它展示了扩展模型和参数解析过程的简单性和表现力。MyRandomParametersTest演示如何将随机值注入@Test 方法。

```java
@ExtendWith(RandomParametersExtension.class)
class MyRandomParametersTest {

    @Test
    void injectsInteger(@Random int i, @Random int j) {
        assertNotEquals(i, j);
    }

    @Test
    void injectsDouble(@Random double d) {
        assertEquals(0.0, d, 1.0);
    }
}
```

对于真实世界的用例，请查看MockitoExtension和 的源代码SpringExtension。

当要注入的参数类型是你的唯一条件时 ParameterResolver，你可以使用通用TypeBasedParameterResolver基类。该supportsParameters方法在幕后实现并支持参数化类型。

### 2.14 测试接口和默认方法

JUnit Jupiter允许在接口 方法上声明@Test、@RepeatedTest、@ParameterizedTest、@TestFactory、 @TestTemplate、@BeforeEach和。并且可以在测试接口中的方法或接口方法上声明，如果测试接口或测试类被注解(请参阅 [测试实例生命周期](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-instance-lifecycle))。这里有些例子。@AfterEachdefault@BeforeAll@AfterAllstaticdefault@TestInstance(Lifecycle.PER_CLASS)

```java
@TestInstance(Lifecycle.PER_CLASS)
interface TestLifecycleLogger {

    static final Logger logger = Logger.getLogger(TestLifecycleLogger.class.getName());

    @BeforeAll
    default void beforeAllTests() {
        logger.info("Before all tests");
    }

    @AfterAll
    default void afterAllTests() {
        logger.info("After all tests");
    }

    @BeforeEach
    default void beforeEachTest(TestInfo testInfo) {
        logger.info(() -> String.format("About to execute [%s]",
            testInfo.getDisplayName()));
    }

    @AfterEach
    default void afterEachTest(TestInfo testInfo) {
        logger.info(() -> String.format("Finished executing [%s]",
            testInfo.getDisplayName()));
    }

}
interface TestInterfaceDynamicTestsDemo {

    @TestFactory
    default Stream<DynamicTest> dynamicTestsForPalindromes() {
        return Stream.of("racecar", "radar", "mom", "dad")
            .map(text -> dynamicTest(text, () -> assertTrue(isPalindrome(text))));
    }

}
```

@ExtendWith并且@Tag可以在测试接口上声明，以便实现该接口的类自动继承它的标签和扩展。[有关TimingExtension](https://junit.org/junit5/docs/current/user-guide/#extensions-lifecycle-callbacks-timing-extension)的源代码， 请参阅 [测试执行回调之前和之后](https://junit.org/junit5/docs/current/user-guide/#extensions-lifecycle-callbacks-before-after-execution)。

```java
@Tag("timed")
@ExtendWith(TimingExtension.class)
interface TimeExecutionLogger {
}
```

然后，在你的测试类中，你可以实现这些测试接口以应用它们。

```java
class TestInterfaceDemo implements TestLifecycleLogger,
        TimeExecutionLogger, TestInterfaceDynamicTestsDemo {

    @Test
    void isEqualValue() {
        assertEquals(1, "a".length(), "is always equal");
    }

}
```

运行TestInterfaceDemo结果输出类似如下：

```shell
INFO example.TestLifecycleLogger - 在所有测试之前
INFO example.TestLifecycleLogger - 即将执行 [dynamicTestsForPalindromes()]
INFO example.TimingExtension - 方法 [dynamicTestsForPalindromes] 耗时 19 毫秒。
INFO example.TestLifecycleLogger - 完成执行 [dynamicTestsForPalindromes()]
INFO example.TestLifecycleLogger - 即将执行 [isEqualValue()]
INFO example.TimingExtension - 方法 [isEqualValue] 耗时 1 毫秒。
INFO example.TestLifecycleLogger - 完成执行 [isEqualValue()]
INFO example.TestLifecycleLogger - 所有测试之后
```

此功能的另一个可能应用是为接口契约编写测试。例如，你可以针对Object.equalsor 的实现Comparable.compareTo方式编写测试，如下所示。

```java
public interface Testable<T> {

    T createValue();

}
public interface EqualsContract<T> extends Testable<T> {

    T createNotEqualValue();

    @Test
    default void valueEqualsItself() {
        T value = createValue();
        assertEquals(value, value);
    }

    @Test
    default void valueDoesNotEqualNull() {
        T value = createValue();
        assertFalse(value.equals(null));
    }

    @Test
    default void valueDoesNotEqualDifferentValue() {
        T value = createValue();
        T differentValue = createNotEqualValue();
        assertNotEquals(value, differentValue);
        assertNotEquals(differentValue, value);
    }

}
public interface ComparableContract<T extends Comparable<T>> extends Testable<T> {

    T createSmallerValue();

    @Test
    default void returnsZeroWhenComparedToItself() {
        T value = createValue();
        assertEquals(0, value.compareTo(value));
    }

    @Test
    default void returnsPositiveNumberWhenComparedToSmallerValue() {
        T value = createValue();
        T smallerValue = createSmallerValue();
        assertTrue(value.compareTo(smallerValue) > 0);
    }

    @Test
    default void returnsNegativeNumberWhenComparedToLargerValue() {
        T value = createValue();
        T smallerValue = createSmallerValue();
        assertTrue(smallerValue.compareTo(value) < 0);
    }

}
```

然后在你的测试类中，你可以实现两个合同接口，从而继承相应的测试。当然，你必须实现抽象方法。

```java
class StringTests implements ComparableContract<String>, EqualsContract<String> {

    @Override
    public String createValue() {
        return "banana";
    }

    @Override
    public String createSmallerValue() {
        return "apple"; // 'a' < 'b' in "banana"
    }

    @Override
    public String createNotEqualValue() {
        return "cherry";
    }

}
```

|      | 以上测试仅作为示例，因此并不完整。 |
| ---- | ---------------------------------- |
|      |                                    |

### 2.15 重复测试

@RepeatedTestJUnit Jupiter提供了通过注解方法并指定所需的重复总次数来重复测试指定次数的能力。重复测试的每次调用都像执行常规 @Test方法一样，完全支持相同的生命周期回调和扩展。

以下示例演示如何声明一个名为的测试，该测试repeatedTest()将自动重复 10 次。

```java
@RepeatedTest(10)
void repeatedTest() {
    // ...
}
```

除了指定重复次数外，还可以通过 注解的name属性为每次重复配置自定义显示名称。@RepeatedTest此外，显示名称可以是由静态文本和动态占位符组合而成的模式。当前支持以下占位符。

-   {displayName}@RepeatedTest:方法的显示名称
-   {currentRepetition}: 当前重复次数
-   {totalRepetitions}：总重复次数

给定重复的默认显示名称是根据以下模式生成的："repetition {currentRepetition} of {totalRepetitions}". 因此，上一个示例的单个重复的显示名称repeatedTest()将是： repetition 1 of 10、repetition 2 of 10等。如果你希望@RepeatedTest方法的显示名称包含在每个重复的名称中，你可以定义自己的自定义模式或使用预定义RepeatedTest.LONG_DISPLAY_NAME模式。后者等于"{displayName} :: repetition {currentRepetition} of {totalRepetitions}"这会导致单个重复项的显示名称，例如 repeatedTest() :: repetition 1 of 10,repeatedTest() :: repetition 2 of 10等。

为了以编程方式检索有关当前重复和重复总数的信息，开发人员可以选择将实例 RepetitionInfo注入到@RepeatedTest、@BeforeEach或@AfterEach方法中。

#### 2.15.1 重复测试示例

本RepeatedTestsDemo节末尾的课程演示了几个重复测试的例子。

该repeatedTest()方法与上一节中的示例相同；然而， repeatedTestWithRepetitionInfo()演示了如何将实例 RepetitionInfo注入到测试中以访问当前重复测试的重复总数。

接下来的两个方法演示了如何在每次重复的显示名称中包含方法@DisplayName的 自定义。 将自定义显示名称与自定义模式相结合，然后用于验证生成的显示名称的格式。是来自声明的 the，来自 。相反， 使用前面提到的预定义 模式。@RepeatedTestcustomDisplayName()TestInfoRepeat!{displayName}@DisplayName1/1{currentRepetition}/{totalRepetitions}customDisplayNameWithLongPattern()RepeatedTest.LONG_DISPLAY_NAME

repeatedTestInGerman()演示了将重复测试的显示名称翻译成外语的能力——在本例中为德语，从而产生了个别重复的名称，例如：Wiederholung 1 von 5、Wiederholung 2 von 5等。

由于该beforeEach()方法带有注解，@BeforeEach因此它将在每次重复测试的每次重复之前执行。通过将TestInfo和 RepetitionInfo注入到方法中，我们看到可以获取有关当前正在执行的重复测试的信息。RepeatedTestsDemo 在启用日志级别的情况下执行INFO会产生以下输出。


信息：即将执行重复测试的重复 1 次，共 10 次
信息：即将为重复测试执行重复 2 次，共 10 次
信息：即将执行重复测试的重复 3 次，共 10 次
信息：即将对重复测试执行重复 4 次，共 10 次
信息：即将对重复测试执行重复 5 次，共 10 次
信息：即将执行 repetition 6 of 10 for repeatedTest
信息：即将执行 repetition 7 of 10 for repeatedTest
信息：即将执行 repetition 8 of 10 for repeatedTest
信息：即将对重复测试执行重复 9 次，共 10 次
信息：即将执行重复测试的重复 10 次，共 10 次
信息：即将对 repeatedTestWithRepetitionInfo 执行重复 1 次，共 5 次
信息：即将对 repeatedTestWithRepetitionInfo 执行重复 2 次，共 5 次
信息：即将对 repeatedTestWithRepetitionInfo 执行重复 3 次，共 5 次
信息：即将执行 repeatedTestWithRepetitionInfo 的第 4 次重复，共 5 次重复
信息：即将对 repeatedTestWithRepetitionInfo 执行重复 5 次，共 5 次
信息：即将为 customDisplayName 执行重复 1 次，共 1 次
信息：即将为 customDisplayNameWithLongPattern 执行第 1 次重复，共 1 次重复
信息：即将执行 repeatedTestInGerman 的第 1 次重复，共 5 次
信息：即将对 repeatedTestInGerman 执行重复 2 次，共 5 次
信息：即将为 repeatedTestInGerman 执行重复 3 次，共 5 次
信息：即将对 repeatedTestInGerman 执行重复 4 次，共 5 次
信息：即将为 repeatedTestInGerman 执行重复 5 次，共 5 次

```java
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.TestInfo;

class RepeatedTestsDemo {

    private Logger logger = // ...

    @BeforeEach
    void beforeEach(TestInfo testInfo, RepetitionInfo repetitionInfo) {
        int currentRepetition = repetitionInfo.getCurrentRepetition();
        int totalRepetitions = repetitionInfo.getTotalRepetitions();
        String methodName = testInfo.getTestMethod().get().getName();
        logger.info(String.format("About to execute repetition %d of %d for %s", //
            currentRepetition, totalRepetitions, methodName));
    }

    @RepeatedTest(10)
    void repeatedTest() {
        // ...
    }

    @RepeatedTest(5)
    void repeatedTestWithRepetitionInfo(RepetitionInfo repetitionInfo) {
        assertEquals(5, repetitionInfo.getTotalRepetitions());
    }

    @RepeatedTest(value = 1, name = "{displayName} {currentRepetition}/{totalRepetitions}")
    @DisplayName("Repeat!")
    void customDisplayName(TestInfo testInfo) {
        assertEquals("Repeat! 1/1", testInfo.getDisplayName());
    }

    @RepeatedTest(value = 1, name = RepeatedTest.LONG_DISPLAY_NAME)
    @DisplayName("Details...")
    void customDisplayNameWithLongPattern(TestInfo testInfo) {
        assertEquals("Details... :: repetition 1 of 1", testInfo.getDisplayName());
    }

    @RepeatedTest(value = 5, name = "Wiederholung {currentRepetition} von {totalRepetitions}")
    void repeatedTestInGerman() {
        // ...
    }

}
```

在ConsoleLauncher启用 unicode 主题的情况下使用时，执行 RepeatedTestsDemo结果会向控制台输出以下内容。

```shell
├─ RepeatedTestsDemo ✔
│ ├─ repeatedTest() ✔
│ │ ├─ 重复第 1 次，共 10 次 ✔
│ │ ├─ 重复第 2 次，共 10 次 ✔
│ │ ├─ 重复第 3 次，共 10 次 ✔
│ │ ├─ 重复第 4 次，共 10 次 ✔
│ │ ├─ 重复第 5 次，共 10 次 ✔
│ │ ├─ 重复第 6 次，共 10 次 ✔
│ │ ├─ 重复第 7 次，共 10 次 ✔
│ │ ├─ 重复第 8 次，共 10 次 ✔
│ │ ├─ 重复第 9 次，共 10 次 ✔
│ │ └─ 重复 10 次，共 10 次 ✔
│ ├─ repeatedTestWithRepetitionInfo(RepetitionInfo) ✔
│ │ ├─ 重复第 1 次，共 5 次 ✔
│ │ ├─ 重复第 2 次，共 5 次 ✔
│ │ ├─ 重复第 3 次，共 5 次 ✔
│ │ ├─ 重复第 4 次，共 5 次 ✔
│ │ └─ 重复第 5 次，共 5 次 ✔
│ ├─ 重复！✔
│ │ └─ 重复！1/1 ✔
│ ├─ 详情... ✔
│ │ └─ 详情... :: 重复 1 of 1 ✔
│ └─ repeatedTestInGerman() ✔
│ ├─ 重复第 1 次，共 5 次 ✔
│ ├─ 重复 2 of 5 ✔
│ ├─ 重复 3 of 5 ✔
│ ├─ 重复第 4 次，共 5 次 ✔
│ └─ 重复第 5 次，共 5 次 ✔
```


### 2.16 参数化测试

参数化测试可以使用不同的参数多次运行测试。它们的声明方式与常规@Test方法一样，但使用 @ParameterizedTest注解代替。此外，你必须至少声明一个 源，该源将为每次调用提供参数，然后在测试方法中使用这些参数。

以下示例演示了一个参数化测试，该测试使用@ValueSource 注解将String数组指定为参数源。

```java
@ParameterizedTest
@ValueSource(strings = { "racecar", "radar", "able was I ere I saw elba" })
void palindromes(String candidate) {
    assertTrue(StringUtils.isPalindrome(candidate));
}
```

在执行上述参数化测试方法时，每次调用都会单独报告。例如，ConsoleLauncher将打印类似于以下内容的输出。


回文(字符串)✔
├─ [1] candidate=racecar ✔
├─ [2] 候选人=雷达 ✔
└─ [3] candidate=able was I saw elba ✔


#### 2.16.1 所需设置

为了使用参数化测试，你需要添加对 junit-jupiter-params工件的依赖。详情请参考[依赖元](https://junit.org/junit5/docs/current/user-guide/#dependency-metadata)数据。

#### 2.16.2 消耗参数

参数化测试方法通常直接从配置的源中使用参数(请参阅[参数源](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests-sources))，遵循参数源索引和方法参数索引之间的一对一关联(请参阅 [@CsvSource](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests-sources-CsvSource)中的示例)。但是，参数化测试方法也可以选择将来自源的参数聚合到传递给该方法的单个对象中(请参阅[参数聚合](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests-argument-aggregation))。a 也可以提供附加参数ParameterResolver(例如，获取 、 等的实例TestInfo)TestReporter。具体来说，参数化测试方法必须根据以下规则声明形式参数。

-   必须首先声明零个或多个索引参数。
-   接下来必须声明零个或多个聚合器。
-   a 提供的零个或多个参数ParameterResolver必须最后声明。

在此上下文中，索引参数是 Arguments由 an 提供的给定索引ArgumentsProvider的参数，它作为参数传递给参数化方法，位于方法的形式参数列表中的相同索引处。聚合器是任何类型的参数或 ArgumentsAccessor任何带有注解的参数 @AggregateWith。

|      | 可自动关闭的参数在为当前参数化测试调用调用方法和 扩展后，实现java.lang.AutoCloseable(或java.io.Closeable扩展 java.lang.AutoCloseable)的参数将自动关闭。@AfterEachAfterEachCallback为防止这种情况发生，请将autoCloseArguments属性 设置@ParameterizedTest为false. 具体来说，如果实现的参数 AutoCloseable被重复用于同一参数化测试方法的多次调用，则必须使用注解该方法@ParameterizedTest(autoCloseArguments = false)以确保参数在调用之间不会关闭。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 2.16.3 争论的来源

开箱即用的JUnit Jupiter提供了相当多的源注解。以下每个小节都提供了简要概述和每个小节的示例。请参阅org.junit.jupiter.params.provider包中的 Javadoc 以获取更多信息。

##### @ValueSource

@ValueSource是最简单的来源之一。它允许你指定一个文字值数组，并且只能用于为每个参数化测试调用提供一个参数。

支持以下类型的文字值@ValueSource。

-   short
-   byte
-   int
-   long
-   float
-   double
-   char
-   boolean
-   java.lang.String
-   java.lang.Class

例如，下面的@ParameterizedTest方法将被调用三次，值1分别为2、 和3。

```java
@ParameterizedTest
@ValueSource(ints = { 1, 2, 3 })
void testWithValueSource(int argument) {
    assertTrue(argument > 0 && argument < 4);
}
```

##### 空源和空源

为了检查极端情况并验证我们的软件在提供错误输入时的正确行为，将空值提供给我们的参数化测试null可能很有用。以下注解用作接受单个参数的参数化测试的来源和空值。null

-   @NullSourcenull: 为带注解的@ParameterizedTest 方法提供单个参数。
    -   @NullSource不能用于具有原始类型的参数。
-   @EmptySource: 为以下类型的参数的注解 方法提供单个空参数： , , , , 原始数组(例如，,等)，对象数组(例如，,等)。@ParameterizedTestjava.lang.Stringjava.util.Listjava.util.Setjava.util.Mapint[]char[][]String[]Integer[][]
    -   不支持受支持类型的子类型。
-   @NullAndEmptySource:组合注解，结合了 @NullSource和的功能@EmptySource。

如果你需要为参数化测试提供多种不同类型的空白字符串，你可以使用[@ValueSource](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests-sources-ValueSource)来实现 ——例如，@ValueSource(strings = {" ", "  ", "t", "n"}).

你还可以组合@NullSource、@EmptySource和@ValueSource来测试更广泛的null、empty和blank输入。以下示例演示了如何为字符串实现此目的。

```java
@ParameterizedTest
@NullSource
@EmptySource
@ValueSource(strings = { " ", "   ", "t", "n" })
void nullEmptyAndBlankStrings(String text) {
    assertTrue(text == null || text.trim().isEmpty());
}
```

使用组合@NullAndEmptySource注解可以简化上述内容，如下所示。

```java
@ParameterizedTest
@NullAndEmptySource
@ValueSource(strings = { " ", "   ", "t", "n" })
void nullEmptyAndBlankStrings(String text) {
    assertTrue(text == null || text.trim().isEmpty());
}
```

|      | 参数化测试方法的两种变体都会nullEmptyAndBlankStrings(String)导致 6 次调用：1 次用于null，1 次用于空字符串，4 次用于通过 提供的显式空白字符串@ValueSource。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

##### @EnumSource

@EnumSourceEnum提供了一种使用常量的便捷方式。

```java
@ParameterizedTest
@EnumSource(ChronoUnit.class)
void testWithEnumSource(TemporalUnit unit) {
    assertNotNull(unit);
}
```

注解的value属性是可选的。省略时，使用第一个方法参数的声明类型。如果不引用枚举类型，测试将失败。因此，value在上面的示例中需要该属性，因为方法参数被声明为TemporalUnit，即由 实现的接口ChronoUnit，它不是枚举类型。将方法参数类型更改为ChronoUnit允许你从注解中省略显式枚举类型，如下所示。

```java
@ParameterizedTest
@EnumSource
void testWithEnumSourceWithAutoDetection(ChronoUnit unit) {
    assertNotNull(unit);
}
```

注解提供了一个可选names属性，允许你指定应使用哪些常量，如以下示例所示。如果省略，将使用所有常量。

```java
@ParameterizedTest
@EnumSource(names = { "DAYS", "HOURS" })
void testWithEnumSourceInclude(ChronoUnit unit) {
    assertTrue(EnumSet.of(ChronoUnit.DAYS, ChronoUnit.HOURS).contains(unit));
}
```

该@EnumSource注解还提供了一个可选mode属性，可以对哪些常量传递给测试方法进行细粒度控制。例如，你可以从枚举常量池中排除名称或指定正则表达式，如以下示例所示。

```java
@ParameterizedTest
@EnumSource(mode = EXCLUDE, names = { "ERAS", "FOREVER" })
void testWithEnumSourceExclude(ChronoUnit unit) {
    assertFalse(EnumSet.of(ChronoUnit.ERAS, ChronoUnit.FOREVER).contains(unit));
}
@ParameterizedTest
@EnumSource(mode = MATCH_ALL, names = "^.DAYS$")
void testWithEnumSourceRegex(ChronoUnit unit) {
    assertTrue(unit.name().endsWith("DAYS"));
}
```

##### @方法源

@MethodSource允许你引用测试类或外部类的一个或多个工厂方法。

测试类中的工厂方法必须是static，除非测试类被注解为@TestInstance(Lifecycle.PER_CLASS)；而外部类中的工厂方法必须始终是static.

每个工厂方法都必须生成一个参数流，并且流中的每组参数都将作为物理参数提供给注解方法的各个调用。一般来说，这翻译成一个 of (ie, ); 然而，实际的具体返回类型可以采用多种形式。在此上下文中，“流”是 JUnit 可以可靠地转换为 的任何内容，例如、、、 、、、、对象数组或原语数组。流中的“参数”可以作为 对象数组的实例提供(例如，@ParameterizedTestStreamArgumentsStream<Arguments>StreamStreamDoubleStreamLongStreamIntStreamCollectionIteratorIterableArgumentsObject[])，如果参数化测试方法接受单个参数，则为单个值。

如果你只需要一个参数，则可以返回Stream参数类型的实例，如以下示例所示。

```java
@ParameterizedTest
@MethodSource("stringProvider")
void testWithExplicitLocalMethodSource(String argument) {
    assertNotNull(argument);
}

static Stream<String> stringProvider() {
    return Stream.of("apple", "banana");
}
```

如果你没有通过 显式提供工厂方法名称@MethodSource，JUnit Jupiter将按照约定搜索与当前方法同名 的工厂@ParameterizedTest方法。这在以下示例中进行了演示。

```java
@ParameterizedTest
@MethodSource
void testWithDefaultLocalMethodSource(String argument) {
    assertNotNull(argument);
}

static Stream<String> testWithDefaultLocalMethodSource() {
    return Stream.of("apple", "banana");
}
```

如以下示例所示，还支持基本类型(DoubleStream、IntStream和)的流。LongStream

```java
@ParameterizedTest
@MethodSource("range")
void testWithRangeMethodSource(int argument) {
    assertNotEquals(9, argument);
}

static IntStream range() {
    return IntStream.range(0, 20).skip(10);
}
```

如果参数化测试方法声明了多个参数，则需要返回集合、流或Arguments实例数组或对象数组，如下所示(@MethodSource有关支持的返回类型的更多详细信息，请参阅 Javadoc)。请注意，这是在 接口arguments(Object…)中定义的静态工厂方法。Arguments此外，Arguments.of(Object…)可以用作 arguments(Object…).

```java
@ParameterizedTest
@MethodSource("stringIntAndListProvider")
void testWithMultiArgMethodSource(String str, int num, List<String> list) {
    assertEquals(5, str.length());
    assertTrue(num >=1 && num <=2);
    assertEquals(2, list.size());
}

static Stream<Arguments> stringIntAndListProvider() {
    return Stream.of(
        arguments("apple", 1, Arrays.asList("a", "b")),
        arguments("lemon", 2, Arrays.asList("x", "y"))
    );
}
```

可以通过提供完全限定的方法名称来引用外部static 工厂方法，如以下示例所示。

```java
package example;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ExternalMethodSourceDemo {

    @ParameterizedTest
    @MethodSource("example.StringsProviders#tinyStrings")
    void testWithExternalMethodSource(String tinyString) {
        // test with tiny string
    }
}

class StringsProviders {

    static Stream<String> tinyStrings() {
        return Stream.of(".", "oo", "OOO");
    }
}
```

工厂方法可以声明参数，这些参数将由ParameterResolver扩展 API 的注册实现提供。在下面的示例中，工厂方法通过其名称引用，因为在测试类中只有一个这样的方法。如果有多个同名的方法，则工厂方法必须通过其完全限定的方法名来引用——例如， @MethodSource("example.MyTests#factoryMethodWithArguments(java.lang.String)").

```java
@RegisterExtension
static final IntegerResolver integerResolver = new IntegerResolver();

@ParameterizedTest
@MethodSource("factoryMethodWithArguments")
void testWithFactoryMethodWithArguments(String argument) {
    assertTrue(argument.startsWith("2"));
}

static Stream<Arguments> factoryMethodWithArguments(int quantity) {
    return Stream.of(
            arguments(quantity + " apples"),
            arguments(quantity + " lemons")
    );
}

static class IntegerResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
            ExtensionContext extensionContext) {
    
        return parameterContext.getParameter().getType() == int.class;
    }
    
    @Override
    public Object resolveParameter(ParameterContext parameterContext,
            ExtensionContext extensionContext) {
    
        return 2;
    }

}
```

##### @CsvSource

@CsvSource允许你将参数列表表示为逗号分隔值(即 CSV String文字)。value通过中的属性提供的每个字符串@CsvSource 代表一个 CSV 记录，并导致参数化测试的一次调用。useHeadersInDisplayName第一条记录可以选择性地用于提供 CSV 标头(有关详细信息和示例，请参阅该属性的 Javadoc )。

```java
@ParameterizedTest
@CsvSource({
    "apple,         1",
    "banana,        2",
    "'lemon, lime', 0xF1",
    "strawberry,    700_000"
})
void testWithCsvSource(String fruit, int rank) {
    assertNotNull(fruit);
    assertNotEquals(0, rank);
}
```

默认分隔符是逗号 ( )，但你可以通过设置属性,使用其他字符 。delimiter或者，该delimiterString属性允许你使用 String定界符而不是单个字符。但是，不能同时设置两个定界符属性。

默认情况下，@CsvSource使用单引号 ( ') 作为其引号字符，但这可以通过quoteCharacter属性更改。请参阅上'lemon, lime'例和下表中的值。 除非设置了属性，否则空的引用值 ( '') 会导致空值；然而，一个完全空的值被解释为一个引用。通过指定一个或多个，可以将自定义值解释为引用(参见下表中的示例)。如果 引用的目标类型是基本类型，则抛出。StringemptyValuenullnullValuesnullNILArgumentConversionExceptionnull

|      | 无论通过属性 配置的任何自定义值如何， 未加引号的空值将始终转换为引用。nullnullValues |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

除了带引号的字符串外，CSV 列中的前导和尾随空格在默认情况下会被删除。可以通过将 ignoreLeadingAndTrailingWhitespace属性设置为 来更改此行为true。

| 示例输入                                                     | 结果参数列表                  |
| :----------------------------------------------------------- | :---------------------------- |
| @CsvSource({ "apple, banana" })                            | "apple", "banana"         |
| @CsvSource({ "apple, 'lemon, lime'" })                     | "apple","lemon, lime"     |
| @CsvSource({ "apple, ''" })                                | "apple",""                |
| @CsvSource({ "apple, " })                                  | "apple",null              |
| @CsvSource(value = { "apple, banana, NIL" }, nullValues = "NIL") | "apple", "banana", null |
| @CsvSource(value = { " apple , banana" }, ignoreLeadingAndTrailingWhitespace = false) | " apple "," banana"       |

如果你使用的编程语言支持文本块 ——例如，Java SE 15 或更高版本——你也可以textBlock使用@CsvSource. 文本块中的每条记录都代表一个 CSV 记录，并导致参数化测试的一次调用。第一条记录可以选择用于通过将useHeadersInDisplayName属性设置为来提供 CSV 标头，true如下例所示。

使用文本块，可以按如下方式实现前面的示例。

```java
@ParameterizedTest(name = "[{index}] {arguments}")
@CsvSource(useHeadersInDisplayName = true, textBlock = """
    FRUIT,         RANK
    apple,         1
    banana,        2
    'lemon, lime', 0xF1
    strawberry,    700_000
    """)
void testWithCsvSource(String fruit, int rank) {
    // ...
}
```

为前面的示例生成的显示名称包括 CSV 标头名称。


[1] 水果 = 苹果，等级 = 1
[2] 水果 = 香蕉，等级 = 2
[3] FRUIT = 柠檬、酸橙，RANK = 0xF1
[4] 水果 = 草莓，等级 = 700_000


与通过属性提供的 CSV 记录相反value，文本块可以包含注解。任何以符号开头的行都#将被视为注解并被忽略。但是请注意，该#符号必须是行中没有任何前导空格的第一个字符。因此，建议将结束文本块分隔符 ( """) 放置在最后一行输入的末尾或下一行，与输入的其余部分左对齐(如下面的示例所示，该示例演示了格式设置类似于表格)。

```java
@ParameterizedTest
@CsvSource(delimiter = '|', quoteCharacter = '"', textBlock = """
    #-----------------------------
    #    FRUIT     |     RANK
    #-----------------------------
         apple     |      1
    #-----------------------------
         banana    |      2
    #-----------------------------
      "lemon lime" |     0xF1
    #-----------------------------
       strawberry  |    700_000
    #-----------------------------
    """)
void testWithCsvSource(String fruit, int rank) {
    // ...
}
```

|      | 编译代码时，Java 的[文本块](https://docs.oracle.com/en/java/javase/15/text-blocks/index.html) 功能会自动删除附带的空格。然而，其他 JVM 语言(例如 Groovy 和 Kotlin)却没有。因此，如果你使用的是 Java 以外的编程语言，并且你的文本块包含注解或带引号的字符串中的新行，你将需要确保文本块中没有前导空格。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

##### @CsvFileSource

@CsvFileSource允许你使用类路径或本地文件系统中的逗号分隔值 (CSV) 文件。CSV 文件中的每条记录都会导致一次参数化测试调用。第一条记录可以选择性地用于提供 CSV 标头。numLinesToSkip你可以通过属性指示 JUnit 忽略标头。如果你希望在显示名称中使用标头，你可以将该useHeadersInDisplayName 属性设置为true. 下面的示例演示了numLinesToSkip和 的用法useHeadersInDisplayName。

默认分隔符是逗号 ( )，但你可以通过设置属性,使用其他字符 。delimiter或者，该delimiterString属性允许你使用 String定界符而不是单个字符。但是，不能同时设置两个定界符属性。

|      | CSV 文件中的注解任何以符号开头的行都#将被解释为注解并被忽略。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

```java
@ParameterizedTest
@CsvFileSource(resources = "/two-column.csv", numLinesToSkip = 1)
void testWithCsvFileSourceFromClasspath(String country, int reference) {
    assertNotNull(country);
    assertNotEquals(0, reference);
}

@ParameterizedTest
@CsvFileSource(files = "src/test/resources/two-column.csv", numLinesToSkip = 1)
void testWithCsvFileSourceFromFile(String country, int reference) {
    assertNotNull(country);
    assertNotEquals(0, reference);
}

@ParameterizedTest(name = "[{index}] {arguments}")
@CsvFileSource(resources = "/two-column.csv", useHeadersInDisplayName = true)
void testWithCsvFileSourceAndHeaders(String country, int reference) {
    assertNotNull(country);
    assertNotEquals(0, reference);
}
```

两列.csv


COUNTRY, REFERENCE
Sweden, 1
Poland, 2
"United States of America", 3
France, 700_000


以下清单显示了为上面的前两个参数化测试方法生成的显示名称。


[1] 国家=瑞典，参考=1
[2] 国家=波兰，参考=2
[3] country=美利坚合众国，reference=3
[4] 国家=法国，参考=700_000


以下清单显示了为上面最后一个使用 CSV 标头名称的参数化测试方法生成的显示名称。


[1] 国家 = 瑞典，参考 = 1
[2] 国家 = 波兰，参考 = 2
[3] 国家 = 美利坚合众国，参考 = 3
[4] 国家 = 法国，参考 = 700_000


与 中使用的默认语法相反，默认@CsvSource使用@CsvFileSource双引号 ( ") 作为引号字符，但这可以通过 quoteCharacter属性更改。请参阅上"United States of America"例中的值。除非 设置了属性，否则空的引用值 ( "") 会导致空值；然而，一个完全空的值被解释为一个 引用。通过指定一个或多个，可以将自定义值解释为引用。如果引用的目标类型是基本类型，则抛出。StringemptyValuenullnullValuesnullArgumentConversionExceptionnull

|      | 无论通过属性 配置的任何自定义值如何， 未加引号的空值将始终转换为引用。nullnullValues |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

除了带引号的字符串外，CSV 列中的前导和尾随空格在默认情况下会被删除。可以通过将 ignoreLeadingAndTrailingWhitespace属性设置为 来更改此行为true。

##### @ArgumentsSource

@ArgumentsSource可用于指定自定义的、可重用的ArgumentsProvider. 请注意，ArgumentsProvider必须将 的实现声明为顶级类或static嵌套类。

```java
@ParameterizedTest
@ArgumentsSource(MyArgumentsProvider.class)
void testWithArgumentsSource(String argument) {
    assertNotNull(argument);
}
public class MyArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of("apple", "banana").map(Arguments::of);
    }
}
```

#### 2.16.4 参数转换

##### 扩大转换

[JUnit](https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.1.2) Jupiter支持 对提供给@ParameterizedTest. 例如，@ValueSource(ints = { 1, 2, 3 })可以声明带有注解的参数化测试不仅接受类型的参数，int而且还接受 、 或 类型long的float参数double。

##### 隐式转换

为了支持像这样的用例@CsvSource，JUnit Jupiter提供了许多内置的隐式类型转换器。转换过程取决于每个方法参数的声明类型。

例如，如果 a@ParameterizedTest声明了一个类型的参数，TimeUnit并且声明的源提供的实际类型是 a String，则字符串将自动转换为相应的TimeUnit枚举常量。

```java
@ParameterizedTest
@ValueSource(strings = "SECONDS")
void testWithImplicitArgumentConversion(ChronoUnit argument) {
    assertNotNull(argument.name());
}
```

String实例隐式转换为以下目标类型。

|      | 十进制、十六进制和八进制String文字将被转换为它们的整数类型：byte、short、int、long，以及它们的盒装对应类型。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

| 目标类型                   | 例子                                                         |
| :------------------------- | :----------------------------------------------------------- |
| boolean/Boolean        | "true" → true                                            |
| byte/Byte              | "15", "0xF", 或"017" →(byte) 15                      |
| char/Character         | "o" → 'o'                                                |
| short/Short            | "15", "0xF", 或"017" →(short) 15                     |
| int/Integer            | "15", "0xF", 或"017" →15                             |
| long/Long              | "15", "0xF", 或"017" →15L                            |
| float/Float            | "1.0" → 1.0f                                             |
| double/Double          | "1.0" →1.0d                                              |
| Enum子类                 | "SECONDS" → TimeUnit.SECONDS                             |
| java.io.File             | "/path/to/file" → new File("/path/to/file")              |
| java.lang.Class          | "java.lang.Integer" → java.lang.Integer.class ($用于嵌套类，例如"java.lang.Thread$State") |
| java.lang.Class          | "byte" → byte.class (支持原始类型)                   |
| java.lang.Class          | "char[]" → char[].class (支持数组类型)               |
| java.math.BigDecimal     | "123.456e789" → new BigDecimal("123.456e789")            |
| java.math.BigInteger     | "1234567890123456789" → new BigInteger("1234567890123456789") |
| java.net.URI             | "https://junit.org/" → URI.create("https://junit.org/")  |
| java.net.URL             | "https://junit.org/" →new URL("https://junit.org/")      |
| java.nio.charset.Charset | "UTF-8" → Charset.forName("UTF-8")                       |
| java.nio.file.Path       | "/path/to/file" →Paths.get("/path/to/file")              |
| java.time.Duration       | "PT3S" → Duration.ofSeconds(3)                           |
| java.time.Instant        | "1970-01-01T00:00:00Z" → Instant.ofEpochMilli(0)         |
| java.time.LocalDateTime  | "2017-03-14T12:34:56.789" → LocalDateTime.of(2017, 3, 14, 12, 34, 56, 789_000_000) |
| java.time.LocalDate      | "2017-03-14" → LocalDate.of(2017, 3, 14)                 |
| java.time.LocalTime      | "12:34:56.789" → LocalTime.of(12, 34, 56, 789_000_000)   |
| java.time.MonthDay       | "--03-14" →MonthDay.of(3, 14)                            |
| java.time.OffsetDateTime | "2017-03-14T12:34:56.789Z" → OffsetDateTime.of(2017, 3, 14, 12, 34, 56, 789_000_000, ZoneOffset.UTC) |
| java.time.OffsetTime     | "12:34:56.789Z" →OffsetTime.of(12, 34, 56, 789_000_000, ZoneOffset.UTC) |
| java.time.Period         | "P2M6D" →Period.of(0, 2, 6)                              |
| java.time.YearMonth      | "2017-03" →YearMonth.of(2017, 3)                         |
| java.time.Year           | "2017" →Year.of(2017)                                    |
| java.time.ZonedDateTime  | "2017-03-14T12:34:56.789Z" →ZonedDateTime.of(2017, 3, 14, 12, 34, 56, 789_000_000, ZoneOffset.UTC) |
| java.time.ZoneId         | "Europe/Berlin" →ZoneId.of("Europe/Berlin")              |
| java.time.ZoneOffset     | "+02:30" → ZoneOffset.ofHoursMinutes(2, 30)              |
| java.util.Currency       | "JPY" →Currency.getInstance("JPY")                       |
| java.util.Locale         | "en" → new Locale("en")                                  |
| java.util.UUID           | "d043e930-7b3b-48e3-bdbe-5a3ccfb833db" → UUID.fromString("d043e930-7b3b-48e3-bdbe-5a3ccfb833db") |

###### 回退字符串到对象的转换

除了从字符串到上表中列出的目标类型的隐式转换之外，JUnit Jupiter还提供了一种回退机制， String如果目标类型恰好声明了一个合适的工厂方法或工厂构造函数，则从 a 到给定目标类型的自动转换以下。

-   工厂方法：在目标类型中声明的非私有static方法，它接受单个String参数并返回目标类型的实例。方法的名称可以是任意的，不需要遵循任何特定的约定。
-   工厂构造函数：目标类型中接受单个String参数的非私有构造函数。请注意，目标类型必须声明为顶级类或static嵌套类。

|      | 如果发现多个工厂方法，它们将被忽略。如果发现工厂方法和工厂构造函数，则将使用工厂方法而不是构造函数。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

例如，在下面的@ParameterizedTest方法中，Book参数将通过调用Book.fromTitle(String)工厂方法并"42 Cats" 作为书名传递来创建。

```java
@ParameterizedTest
@ValueSource(strings = "42 Cats")
void testWithImplicitFallbackArgumentConversion(Book book) {
    assertEquals("42 Cats", book.getTitle());
}
public class Book {

    private final String title;
    
    private Book(String title) {
        this.title = title;
    }
    
    public static Book fromTitle(String title) {
        return new Book(title);
    }
    
    public String getTitle() {
        return this.title;
    }
}
```

##### 显式转换

除了依赖隐式参数转换，你还可以 ArgumentConverter使用注解显式指定要用于特定参数的an @ConvertWith，如下例所示。请注意，ArgumentConverter必须将 的实现声明为顶级类或static嵌套类。

```java
@ParameterizedTest
@EnumSource(ChronoUnit.class)
void testWithExplicitArgumentConversion(
        @ConvertWith(ToStringArgumentConverter.class) String argument) {

    assertNotNull(ChronoUnit.valueOf(argument));
}
public class ToStringArgumentConverter extends SimpleArgumentConverter {

    @Override
    protected Object convert(Object source, Class<?> targetType) {
        assertEquals(String.class, targetType, "Can only convert to String");
        if (source instanceof Enum<?>) {
            return ((Enum<?>) source).name();
        }
        return String.valueOf(source);
    }
}
```

如果转换器只是为了将一种类型转换为另一种类型，你可以扩展 TypedArgumentConverter以避免样板类型检查。

```java
public class ToLengthArgumentConverter extends TypedArgumentConverter<String, Integer> {

    protected ToLengthArgumentConverter() {
        super(String.class, Integer.class);
    }
    
    @Override
    protected Integer convert(String source) {
        return (source != null ? source.length() : 0);
    }

}
```

显式参数转换器旨在由测试和扩展作者实现。因此，junit-jupiter-params只提供了一个也可以作为参考实现的显式参数转换器：JavaTimeArgumentConverter。它通过组合注解使用JavaTimeConversionPattern。

```java
@ParameterizedTest
@ValueSource(strings = { "01.01.2017", "31.12.2017" })
void testWithExplicitJavaTimeConverter(
        @JavaTimeConversionPattern("dd.MM.yyyy") LocalDate argument) {

    assertEquals(2017, argument.getYear());
}
```

#### 2.16.5 参数聚合

默认情况下，提供给方法的每个参数@ParameterizedTest都对应一个方法参数。因此，预期提供大量参数的参数源可能会导致大型方法签名。

在这种情况下，ArgumentsAccessor可以使用一个代替多个参数。使用此 API，你可以通过传递给测试方法的单个参数来访问提供的参数。此外，还支持类型转换，如 [隐式转换](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests-argument-conversion-implicit)中所述。

```java
@ParameterizedTest
@CsvSource({
    "Jane, Doe, F, 1990-05-20",
    "John, Doe, M, 1990-10-22"
})
void testWithArgumentsAccessor(ArgumentsAccessor arguments) {
    Person person = new Person(arguments.getString(0),
                               arguments.getString(1),
                               arguments.get(2, Gender.class),
                               arguments.get(3, LocalDate.class));

    if (person.getFirstName().equals("Jane")) {
        assertEquals(Gender.F, person.getGender());
    }
    else {
        assertEquals(Gender.M, person.getGender());
    }
    assertEquals("Doe", person.getLastName());
    assertEquals(1990, person.getDateOfBirth().getYear());
}
```

的实例ArgumentsAccessor会自动注入到类型的任何参数中 ArgumentsAccessor。

##### 自定义聚合器

除了使用 直接访问@ParameterizedTest方法的参数 外ArgumentsAccessor，JUnit Jupiter还支持使用自定义的、可重用的 聚合器。

要使用自定义聚合器，请实现ArgumentsAggregator接口并通过方法@AggregateWith中兼容参数上的注解 注册它@ParameterizedTest。当调用参数化测试时，聚合的结果将作为相应参数的参数提供。请注意，ArgumentsAggregator必须将 的实现声明为顶级类或static嵌套类。

```java
@ParameterizedTest
@CsvSource({
    "Jane, Doe, F, 1990-05-20",
    "John, Doe, M, 1990-10-22"
})
void testWithArgumentsAggregator(@AggregateWith(PersonAggregator.class) Person person) {
    // perform assertions against person
}
public class PersonAggregator implements ArgumentsAggregator {
    @Override
    public Person aggregateArguments(ArgumentsAccessor arguments, ParameterContext context) {
        return new Person(arguments.getString(0),
                          arguments.getString(1),
                          arguments.get(2, Gender.class),
                          arguments.get(3, LocalDate.class));
    }
}
```

如果你发现自己@AggregateWith(MyTypeAggregator.class)在代码库中反复声明多个参数化测试方法，你可能希望创建一个自定义 组合注解，例如@CsvToMyType使用 @AggregateWith(MyTypeAggregator.class). 以下示例使用自定义@CsvToPerson注解演示了这一点。

```java
@ParameterizedTest
@CsvSource({
    "Jane, Doe, F, 1990-05-20",
    "John, Doe, M, 1990-10-22"
})
void testWithCustomAggregatorAnnotation(@CsvToPerson Person person) {
    // perform assertions against person
}
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@AggregateWith(PersonAggregator.class)
public @interface CsvToPerson {
}
```

#### 2.16.6 自定义显示名称

默认情况下，参数化测试调用的显示名称包含调用索引和该String特定调用的所有参数的表示。如果存在于字节码中(对于 Java，测试代码必须使用编译器标志进行编译)，则它们中的每一个都以参数名称开头(除非参数只能通过ArgumentsAccessoror获得)。ArgumentAggregator-parameters

name但是，你可以通过注解的属性 自定义调用显示名称@ParameterizedTest，如下例所示。

```java
@DisplayName("Display name of container")
@ParameterizedTest(name = "{index} ==> the rank of ''{0}'' is {1}")
@CsvSource({ "apple, 1", "banana, 2", "'lemon, lime', 3" })
void testWithCustomDisplayNames(String fruit, int rank) {
}
```

使用 执行上述方法时，ConsoleLauncher你将看到类似于以下内容的输出。


容器的显示名称 ✔
├─ 1 ==> 'apple' 的等级是 1 ✔
├─ 2 ==> 'banana' 的排名是 2 ✔
└─ 3 ==> 'lemon, lime' 的等级是 3 ✔


请注意，这name是一种MessageFormat模式。因此，单引号 ( ') 需要表示为双单引号 ( '') 才能显示。

自定义显示名称中支持以下占位符。

| 占位符                 | 描述                                       |
| :--------------------- | :----------------------------------------- |
| {displayName}        | 方法的显示名称                             |
| {index}              | 当前调用索引(从 1 开始)                  |
| {arguments}          | 完整的、逗号分隔的参数列表                 |
| {argumentsWithNames} | 带有参数名称的完整的、以逗号分隔的参数列表 |
| {0}, {1}, …        | 一个单独的论点                             |

|      | 在显示名称中包含参数时，如果它们超过配置的最大长度，它们的字符串表示将被截断。该限制可通过 junit.jupiter.params.displayname.argument.maxlength配置参数进行配置，默认为 512 个字符。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

使用@MethodSourceor时，你可以使用API@ArgumentsSource为参数提供自定义名称。Named如果参数包含在调用显示名称中，将使用自定义名称，如下例所示。

```java
@DisplayName("A parameterized test with named arguments")
@ParameterizedTest(name = "{index}: {0}")
@MethodSource("namedArguments")
void testWithNamedArguments(File file) {
}

static Stream<Arguments> namedArguments() {
    return Stream.of(
        arguments(named("An important file", new File("path1"))),
        arguments(named("Another file", new File("path2")))
    );
}
```

带有命名参数的参数化测试✔
├─ 1：重要文件✔
└─ 2：另一个文件✔


如果你想为项目中的所有参数化测试设置默认名称模式，你可以junit.jupiter.params.displayname.default在文件中声明配置参数，junit-platform.properties如以下示例所示(有关其他选项，请参阅 [配置参数](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params))。


junit.jupiter.params.displayname.default = {index}


参数化测试的显示名称根据以下优先规则确定：

1.  name中的属性@ParameterizedTest，如果存在
2.  配置参数的值(junit.jupiter.params.displayname.default如果存在)
3.  DEFAULT_DISPLAY_NAME常量定义于@ParameterizedTest

#### 2.16.7 生命周期和互操作性


@Test 参数化测试的每次调用都与常规方法具有相同的生命周期。例如，@BeforeEach方法将在每次调用之前执行。与[Dynamic Tests](https://junit.org/junit5/docs/current/user-guide/#writing-tests-dynamic-tests)类似，调用将在 IDE 的测试树中一个接一个地出现。你可以随意 在同一个测试类中混合使用常规@Test方法和方法。@ParameterizedTest


你可以将ParameterResolver扩展与@ParameterizedTest方法一起使用。但是，由参数源解析的方法参数需要在参数列表中排在第一位。由于测试类可能包含常规测试以及具有不同参数列表的参数化测试，因此来自参数源的值不会为生命周期方法(例如@BeforeEach)和测试类构造函数解析。

```java
@BeforeEach
void beforeEach(TestInfo testInfo) {
    // ...
}

@ParameterizedTest
@ValueSource(strings = "apple")
void testWithRegularParameterResolver(String argument, TestReporter testReporter) {
    testReporter.publishEntry("argument", argument);
}

@AfterEach
void afterEach(TestInfo testInfo) {
    // ...
}
```

### 2.17 测试模板

方法@TestTemplate不是常规测试用例，而是测试用例的模板。因此，它被设计为根据注册提供者返回的调用上下文的数量被多次调用。因此，它必须与已注册的TestTemplateInvocationContextProvider扩展一起使用。测试模板方法的每次调用都像执行常规@Test 方法一样，完全支持相同的生命周期回调和扩展。有关使用示例，请参阅 [为测试模板提供调用上下文。](https://junit.org/junit5/docs/current/user-guide/#extensions-test-templates)

|      | [重复测试](https://junit.org/junit5/docs/current/user-guide/#writing-tests-repeated-tests)和[参数化测试](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests)是测试模板的内置特化。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

### 2.18 动态测试

@Test注解中描述的JUnit Jupiter中 的标准注解与JUnit 4中的[注解](https://junit.org/junit5/docs/current/user-guide/#writing-tests-annotations)非常相似@Test。两者都描述了实现测试用例的方法。这些测试用例是静态的，因为它们在编译时已完全指定，并且它们的行为不会因运行时发生的任何事情而改变。假设提供了动态行为的基本形式，但有意限制了它们的表达能力。

除了这些标准测试之外，JUnit Jupiter中还引入了一种全新的测试编程模型。这种新型测试是一种动态测试，它在运行时由带有注解的工厂方法生成@TestFactory。

与@Test方法不同，@TestFactory方法本身不是测试用例，而是测试用例的工厂。因此，动态测试是工厂的产品。从技术上讲，一个方法@TestFactory必须返回一个DynamicNode或一个 Stream、、、、或数组实例。和的可实例化子类。 实例由显示名称和动态子节点列表组成，可以创建任意嵌套的动态节点层次结构。 实例将被延迟执行，从而支持动态甚至非确定性的测试用例生成。CollectionIterableIteratorDynamicNodeDynamicNodeDynamicContainerDynamicTestDynamicContainerDynamicTest

Streama 返回的任何内容@TestFactory都将通过调用正确关闭 stream.close()，从而可以安全地使用诸如Files.lines().

与@Test方法一样，@TestFactory方法不能是private或static并且可以选择声明要由 解析的参数ParameterResolvers。

ADynamicTest是运行时生成的测试用例。它由一个显示名称 和一个Executable. Executable是一个@FunctionalInterface，这意味着动态测试的实现可以作为lambda 表达式或方法引用提供。

|      | 动态测试生命周期@Test动态测试的执行生命周期与标准案例 的执行生命周期完全不同。具体来说，没有针对单个动态测试的生命周期回调。这意味着@BeforeEach和@AfterEach方法及其相应的扩展回调是为@TestFactory方法执行的，而不是为每个动态测试执行的。换句话说，如果你从动态测试的 lambda 表达式中的测试实例访问字段，则这些字段不会被回调方法或在同一 @TestFactory方法生成的各个动态测试执行之间的扩展重置。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

从JUnit Jupiter5.9.1 开始，动态测试必须始终由工厂方法创建；但是，这可能会在以后的版本中得到注册工具的补充。

#### 2.18.1 动态测试示例

下面的DynamicTestsDemo类演示了几个测试工厂和动态测试的例子。

第一个方法返回无效的返回类型。由于在编译时无法检测到无效的返回类型，JUnitException因此在运行时检测到时会抛出 a 。

接下来的六个方法演示了Collection、Iterable、Iterator、数组或实例Stream的生成。DynamicTest这些示例中的大多数并没有真正展示动态行为，而只是原则上演示了支持的返回类型。但是，dynamicTestsFromStream()并dynamicTestsFromIntStream()演示如何为给定的一组字符串或一系列输入数字生成动态测试。

下一个方法本质上是真正动态的。generateRandomNumberOfTests()实现 Iterator生成随机数的 an、显示名称生成器和测试执行器，然后将所有三者提供给DynamicTest.stream(). 尽管 的非确定性行为generateRandomNumberOfTests()当然与测试可重复性相冲突，因此应谨慎使用，但它可以证明动态测试的表现力和强大功能。

下一个方法generateRandomNumberOfTests()在灵活性方面与此类似；但是，通过工厂方法dynamicTestsFromStreamFactoryMethod()从现有的生成动态测试流。StreamDynamicTest.stream()

出于演示目的，该dynamicNodeSingleTest()方法生成单个 DynamicTest而不是流，并且该dynamicNodeSingleContainer()方法使用DynamicContainer.

```java
import static example.util.StringUtils.isPalindrome;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.junit.jupiter.api.Named.named;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import example.util.Calculator;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;

class DynamicTestsDemo {

    private final Calculator calculator = new Calculator();
    
    // This will result in a JUnitException!
    @TestFactory
    List<String> dynamicTestsWithInvalidReturnType() {
        return Arrays.asList("Hello");
    }
    
    @TestFactory
    Collection<DynamicTest> dynamicTestsFromCollection() {
        return Arrays.asList(
            dynamicTest("1st dynamic test", () -> assertTrue(isPalindrome("madam"))),
            dynamicTest("2nd dynamic test", () -> assertEquals(4, calculator.multiply(2, 2)))
        );
    }
    
    @TestFactory
    Iterable<DynamicTest> dynamicTestsFromIterable() {
        return Arrays.asList(
            dynamicTest("3rd dynamic test", () -> assertTrue(isPalindrome("madam"))),
            dynamicTest("4th dynamic test", () -> assertEquals(4, calculator.multiply(2, 2)))
        );
    }
    
    @TestFactory
    Iterator<DynamicTest> dynamicTestsFromIterator() {
        return Arrays.asList(
            dynamicTest("5th dynamic test", () -> assertTrue(isPalindrome("madam"))),
            dynamicTest("6th dynamic test", () -> assertEquals(4, calculator.multiply(2, 2)))
        ).iterator();
    }
    
    @TestFactory
    DynamicTest[] dynamicTestsFromArray() {
        return new DynamicTest[] {
            dynamicTest("7th dynamic test", () -> assertTrue(isPalindrome("madam"))),
            dynamicTest("8th dynamic test", () -> assertEquals(4, calculator.multiply(2, 2)))
        };
    }
    
    @TestFactory
    Stream<DynamicTest> dynamicTestsFromStream() {
        return Stream.of("racecar", "radar", "mom", "dad")
            .map(text -> dynamicTest(text, () -> assertTrue(isPalindrome(text))));
    }
    
    @TestFactory
    Stream<DynamicTest> dynamicTestsFromIntStream() {
        // Generates tests for the first 10 even integers.
        return IntStream.iterate(0, n -> n + 2).limit(10)
            .mapToObj(n -> dynamicTest("test" + n, () -> assertTrue(n % 2 == 0)));
    }
    
    @TestFactory
    Stream<DynamicTest> generateRandomNumberOfTestsFromIterator() {
    
        // Generates random positive integers between 0 and 100 until
        // a number evenly divisible by 7 is encountered.
        Iterator<Integer> inputGenerator = new Iterator<Integer>() {
    
            Random random = new Random();
            int current;
    
            @Override
            public boolean hasNext() {
                current = random.nextInt(100);
                return current % 7 != 0;
            }
    
            @Override
            public Integer next() {
                return current;
            }
        };
    
        // Generates display names like: input:5, input:37, input:85, etc.
        Function<Integer, String> displayNameGenerator = (input) -> "input:" + input;
    
        // Executes tests based on the current input value.
        ThrowingConsumer<Integer> testExecutor = (input) -> assertTrue(input % 7 != 0);
    
        // Returns a stream of dynamic tests.
        return DynamicTest.stream(inputGenerator, displayNameGenerator, testExecutor);
    }
    
    @TestFactory
    Stream<DynamicTest> dynamicTestsFromStreamFactoryMethod() {
        // Stream of palindromes to check
        Stream<String> inputStream = Stream.of("racecar", "radar", "mom", "dad");
    
        // Generates display names like: racecar is a palindrome
        Function<String, String> displayNameGenerator = text -> text + " is a palindrome";
    
        // Executes tests based on the current input value.
        ThrowingConsumer<String> testExecutor = text -> assertTrue(isPalindrome(text));
    
        // Returns a stream of dynamic tests.
        return DynamicTest.stream(inputStream, displayNameGenerator, testExecutor);
    }
    
    @TestFactory
    Stream<DynamicTest> dynamicTestsFromStreamFactoryMethodWithNames() {
        // Stream of palindromes to check
        Stream<Named<String>> inputStream = Stream.of(
                named("racecar is a palindrome", "racecar"),
                named("radar is also a palindrome", "radar"),
                named("mom also seems to be a palindrome", "mom"),
                named("dad is yet another palindrome", "dad")
            );
    
        // Returns a stream of dynamic tests.
        return DynamicTest.stream(inputStream,
            text -> assertTrue(isPalindrome(text)));
    }
    
    @TestFactory
    Stream<DynamicNode> dynamicTestsWithContainers() {
        return Stream.of("A", "B", "C")
            .map(input -> dynamicContainer("Container " + input, Stream.of(
                dynamicTest("not null", () -> assertNotNull(input)),
                dynamicContainer("properties", Stream.of(
                    dynamicTest("length > 0", () -> assertTrue(input.length() > 0)),
                    dynamicTest("not empty", () -> assertFalse(input.isEmpty()))
                ))
            )));
    }
    
    @TestFactory
    DynamicNode dynamicNodeSingleTest() {
        return dynamicTest("'pop' is a palindrome", () -> assertTrue(isPalindrome("pop")));
    }
    
    @TestFactory
    DynamicNode dynamicNodeSingleContainer() {
        return dynamicContainer("palindromes",
            Stream.of("racecar", "radar", "mom", "dad")
                .map(text -> dynamicTest(text, () -> assertTrue(isPalindrome(text)))
        ));
    }

}
```

#### 2.18.2 动态测试的URI测试源

JUnit 平台提供TestSource了测试或容器源的表示，用于通过 IDE 和构建工具导航到它的位置。

对于TestSource动态测试或动态容器，可以从 java.net.URI可以分别通过工厂方法DynamicTest.dynamicTest(String, URI, Executable)或DynamicContainer.dynamicContainer(String, URI, Stream)工厂方法提供的 构建。将URI转换为以下TestSource 实现之一。

-   ClasspathResourceSource

    如果URI包含classpath方案——例如， classpath:/test/foo.xml?line=20,column=2.

-   DirectorySource

    如果URI表示文件系统中存在的目录。

-   FileSource

    如果URI表示文件系统中存在的文件。

-   MethodSource

    如果URI包含method方案和完全限定的方法名称 (FQMN) — 例如，method:org.junit.Foo#bar(java.lang.String, java.lang.String[]). DiscoverySelectors.selectMethod(String)有关 FQMN 支持的格式，请参阅 Javadoc 。

-   ClassSource

    如果URI包含class方案和完全限定的类名——例如，class:org.junit.Foo?line=42.

-   UriSource

    如果以上TestSource实现都不适用。

### 2.19 超时

@Timeout注解允许人们声明如果测试、测试工厂、测试模板或生命周期方法的执行时间超过给定的持续时间，则该方法应该失败。持续时间的时间单位默认为秒，但可以配置。

以下示例显示如何@Timeout应用于生命周期和测试方法。

```java
class TimeoutDemo {

    @BeforeEach
    @Timeout(5)
    void setUp() {
        // fails if execution time exceeds 5 seconds
    }
    
    @Test
    @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
    void failsIfExecutionTimeExceeds500Milliseconds() {
        // fails if execution time exceeds 500 milliseconds
    }
    
    @Test
    @Timeout(value = 500, unit = TimeUnit.MILLISECONDS, threadMode = ThreadMode.SEPARATE_THREAD)
    void failsIfExecutionTimeExceeds500MillisecondsInSeparateThread() {
        // fails if execution time exceeds 500 milliseconds, the test code is executed in a separate thread
    }

}
```

要将相同的超时应用于测试类及其所有类中的所有测试方法@Nested ，你可以@Timeout在类级别声明注解。然后它将应用于该类及其 @Nested类中的所有测试、测试工厂和测试模板方法，除非被@Timeout特定方法或 @Nested类上的注解覆盖。请注意，@Timeout在类级别声明的注解不适用于生命周期方法。


@Timeout在方法上声明@TestFactory检查工厂方法是否在指定的持续时间内返回，但不验证 DynamicTest工厂生成的每个个体的执行时间。请 为此目的使用assertTimeout()或。assertTimeoutPreemptively()


如果@Timeout出现在一个@TestTemplate方法上——例如，一个@RepeatedTest或 @ParameterizedTest——每个调用都会应用给定的超时。

#### 2.19.1 线程模式

可以使用以下三种线程模式之一应用超时：SAME_THREAD、 SEPARATE_THREAD或INFERRED。

使用时SAME_THREAD，注解方法的执行在测试的主线程中进行。如果超过超时时间，主线程会被另一个线程中断。这样做是为了确保与 Spring 等框架的互操作性，这些框架使用对当前运行的线程敏感的机制——例如，ThreadLocal事务管理。

相反，使用 when 时SEPARATE_THREAD，就像assertTimeoutPreemptively() 断言一样，注解方法的执行在单独的线程中进行，这可能会导致不良的副作用，请参阅[Preemptive Timeouts withassertTimeoutPreemptively()](https://junit.org/junit5/docs/current/user-guide/#writing-tests-assertions-preemptive-timeouts)。

当使用INFERRED(默认)线程模式时，线程模式通过 junit.jupiter.execution.timeout.thread.mode.default配置参数解析。如果提供的配置参数无效或不存在，则SAME_THREAD用作回退。

#### 2.19.2 默认超时

以下[配置参数](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params)可用于指定特定类别的所有方法的默认超时，除非它们或封闭的测试类被注解为@Timeout：

-   junit.jupiter.execution.timeout.default

    所有可测试和生命周期方法的默认超时

-   junit.jupiter.execution.timeout.testable.method.default

    所有可测试方法的默认超时

-   junit.jupiter.execution.timeout.test.method.default

    @Test方法的默认超时

-   junit.jupiter.execution.timeout.testtemplate.method.default

    @TestTemplate方法的默认超时

-   junit.jupiter.execution.timeout.testfactory.method.default

    @TestFactory方法的默认超时

-   junit.jupiter.execution.timeout.lifecycle.method.default

    所有生命周期方法的默认超时

-   junit.jupiter.execution.timeout.beforeall.method.default

    @BeforeAll方法的默认超时

-   junit.jupiter.execution.timeout.beforeeach.method.default

    @BeforeEach方法的默认超时

-   junit.jupiter.execution.timeout.aftereach.method.default

    @AfterEach方法的默认超时

-   junit.jupiter.execution.timeout.afterall.method.default

    @AfterAll方法的默认超时

更具体的配置参数会覆盖不太具体的配置参数。例如， junit.jupiter.execution.timeout.test.method.defaultoverride junit.jupiter.execution.timeout.testable.method.defaultwhich overrides junit.jupiter.execution.timeout.default。

此类配置参数的值必须采用以下不区分大小写的格式：<number> [ns|μs|ms|s|m|h|d]. 数字和单位之间的空格可以省略。不指定单位等同于使用秒。

| 参数值  | 等效注解                                    |
| :------ | :------------------------------------------ |
| 42    | @Timeout(42)                              |
| 42 ns | @Timeout(value = 42, unit = NANOSECONDS)  |
| 42 μs | @Timeout(value = 42, unit = MICROSECONDS) |
| 42 ms | @Timeout(value = 42, unit = MILLISECONDS) |
| 42 s  | @Timeout(value = 42, unit = SECONDS)      |
| 42 m  | @Timeout(value = 42, unit = MINUTES)      |
| 42 h  | @Timeout(value = 42, unit = HOURS)        |
| 42 d  | @Timeout(value = 42, unit = DAYS)         |

#### 2.19.3 使用@Timeout 进行轮询测试

在处理异步代码时，通常会编写在执行任何断言之前等待某事发生的同时进行轮询的测试。在某些情况下，你可以重写逻辑以使用一种CountDownLatch或另一种同步机制，但有时这是不可能的——例如，如果被测对象将消息发送到外部消息代理中的通道，并且断言无法执行，直到消息已成功通过通道发送。像这样的异步测试需要某种形式的超时，以确保它们不会因无限期执行而挂起测试套件，如果异步消息从未成功传递就会出现这种情况。

通过为轮询的异步测试配置超时，你可以确保测试不会无限期地执行。以下示例演示了如何使用JUnit Jupiter的@Timeout注解来实现这一点。该技术可用于非常轻松地实现“轮询直到”逻辑。

```java
@Test
@Timeout(5) // Poll at most 5 seconds
void pollUntil() throws InterruptedException {
    while (asynchronousResultNotAvailable()) {
        Thread.sleep(250); // custom poll interval
    }
    // Obtain the asynchronous result and perform assertions
}
```

|      | 如果你需要更好地控制轮询间隔并通过异步测试获得更大的灵活性，请考虑使用专用库，例如[ Awaitility](https://github.com/awaitility/awaitility)。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 2.19.4 全局禁用@Timeout

在调试会话中单步执行代码时，固定超时限制可能会影响测试结果，例如，尽管满足所有断言，但将测试标记为失败。

JUnit Jupiter支持junit.jupiter.execution.timeout.mode配置参数来配置何时应用超时。共有三种模式：enabled、disabled和disabled_on_debug。默认模式是enabled。-agentlib:jdwp当其输入参数之一以或开头时，VM 运行时被视为在调试模式下运行-Xrunjdwp。该启发式由disabled_on_debug模式查询。

### 2.20 并行执行

|      | 并行测试执行是一项实验性功能我们邀请你尝试一下并向 JUnit 团队提供反馈，以便他们改进并最终[推广](https://junit.org/junit5/docs/current/user-guide/#api-evolution)此功能。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

默认情况下，JUnit Jupiter测试在单个线程中按顺序运行。并行运行测试——例如，以加快执行速度——从 5.3 版开始作为可选功能提供。要启用并行执行，请将 junit.jupiter.execution.parallel.enabled配置参数设置为true — 例如，in junit-platform.properties(有关其他选项，请参阅[配置参数](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params))。

请注意，启用此属性只是并行执行测试所需的第一步。如果启用，测试类和方法仍将默认按顺序执行。测试树中的节点是否并发执行由其执行模式控制。可以使用以下两种模式。

-   SAME_THREAD

    强制在父级使用的同一线程中执行。例如，当在测试方法上使用时，测试方法将在与包含测试类的任何@BeforeAll或 方法相同的线程中执行。@AfterAll

-   CONCURRENT

    并发执行，除非资源锁强制在同一个线程中执行。

默认情况下，测试树中的节点使用SAME_THREAD执行模式。junit.jupiter.execution.parallel.mode.default你可以通过设置配置参数来更改默认值。或者，你可以使用@Execution注解来更改带注解的元素及其子元素(如果有)的执行模式，这允许你为单个测试类一个一个地激活并行执行。

并行执行所有测试的配置参数


junit.jupiter.execution.parallel.enabled = true
junit.jupiter.execution.parallel.mode.default = concurrent


默认执行模式应用于测试树的所有节点，但有一些明显的例外，即使用Lifecycle.PER_CLASS模式或 a MethodOrderer(除了MethodOrderer.Random)的测试类。在前一种情况下，测试作者必须确保测试类是线程安全的；在后者中，并发执行可能与配置的执行顺序冲突。@Execution(CONCURRENT) 因此，在这两种情况下，只有在测试类或方法上存在注解时，此类测试类中的测试方法才会同时执行。

配置CONCURRENT执行模式的测试树的所有节点将根据提供的 [配置](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parallel-execution-config)完全并行执行，同时遵守声明式[同步](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parallel-execution-synchronization) 机制。请注意，需要单独启用[捕获标准输出/错误。](https://junit.org/junit5/docs/current/user-guide/#running-tests-capturing-output)

junit.jupiter.execution.parallel.mode.classes.default此外，你可以通过设置配置参数来配置顶级类的默认执行模式。通过组合这两个配置参数，你可以将类配置为并行运行，但它们的方法在同一个线程中：

配置参数以并行执行顶级类但在同一线程中执行方法


junit.jupiter.execution.parallel.enabled = true
junit.jupiter.execution.parallel.mode.default = same_thread
junit.jupiter.execution.parallel.mode.classes.default = concurrent


相反的组合将并行运行一个类中的所有方法，但顶级类将按顺序运行：

配置参数顺序执行顶级类，但并行执行它们的方法


junit.jupiter.execution.parallel.enabled = true
junit.jupiter.execution.parallel.mode.default = concurrent
junit.jupiter.execution.parallel.mode.classes.default = same_thread


下图说明了两个顶级测试类的执行A以及 每个类的两个测试方法对于和 B的所有四种组合的行为方式 (请参阅第一列中的标签)。junit.jupiter.execution.parallel.mode.defaultjunit.jupiter.execution.parallel.mode.classes.default

![编写测试执行模式](https://junit.org/junit5/docs/current/user-guide/images/writing-tests_execution_mode.svg)

默认执行模式配置组合

如果junit.jupiter.execution.parallel.mode.classes.default未显式设置配置参数，junit.jupiter.execution.parallel.mode.default则将使用的值代替。

#### 2.20.1 配置

所需的并行度和最大池大小等属性可以使用ParallelExecutionConfigurationStrategy. JUnit 平台提供了两种开箱即用的实现：dynamic和fixed. 或者，你可以实施 custom策略。

要选择策略，请将junit.jupiter.execution.parallel.config.strategy 配置参数设置为以下选项之一。

-   dynamic

    根据可用处理器/内核的数量乘以junit.jupiter.execution.parallel.config.dynamic.factor 配置参数(默认为1)计算所需的并行度。

-   fixed

    使用强制junit.jupiter.execution.parallel.config.fixed.parallelism 配置参数作为所需的并行度。

-   custom

    允许你ParallelExecutionConfigurationStrategy 通过强制junit.jupiter.execution.parallel.config.custom.class 配置参数指定自定义实现以确定所需的配置。

如果没有设置配置策略，JUnit Jupiter使用dynamic因子为1. 因此，所需的并行度将等于可用处理器/内核的数量。

|      | 并行性并不意味着最大并发线程数JUnit Jupiter不保证并发执行测试的数量不会超过配置的并行度。例如，当使用下一节中描述的同步机制之一时，在ForkJoinPool幕后使用的 可能会产生额外的线程以确保执行以足够的并行度继续进行。因此，如果你在测试类中需要此类保证，请使用你自己的方式来控制并发。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

##### 相关属性

下表列出了配置并行执行的相关属性。有关如何设置此类属性的详细信息，请参阅 [配置参数。](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params)

| 财产                                                        | 描述                                                         | 支持的值                           | 默认值        |
| :---------------------------------------------------------- | :----------------------------------------------------------- | :--------------------------------- | :------------ |
| junit.jupiter.execution.parallel.enabled                  | 启用并行测试执行                                             | truefalse                      | false       |
| junit.jupiter.execution.parallel.mode.default             | 测试树中节点的默认执行方式                                   | concurrentsame_thread          | same_thread |
| junit.jupiter.execution.parallel.mode.classes.default     | 顶级类的默认执行方式                                         | concurrentsame_thread          | same_thread |
| junit.jupiter.execution.parallel.config.strategy          | 所需并行性和最大池大小的执行策略                             | dynamicfixedcustom           | dynamic     |
| junit.jupiter.execution.parallel.config.dynamic.factor    | 乘以可用处理器/内核数量以确定dynamic配置策略所需的并行度的系数 | 正十进制数                         | 1.0         |
| junit.jupiter.execution.parallel.config.fixed.parallelism | fixed配置策略所需的并行度                                  | 正整数                             | 没有默认值    |
| junit.jupiter.execution.parallel.config.custom.class      | 用于配置策略的ParallelExecutionConfigurationStrategy的完全限定类名custom | 例如，org.example.CustomStrategy | 没有默认值    |

#### 2.20.2 同步化

JUnit Jupiter除了使用@Execution注解来控制执行模式外，还提供了另一种基于注解的声明式同步机制。@ResourceLock注解允许你声明测试类或方法使用需要同步访问的特定共享资源以确保可靠的测试执行。 共享资源由唯一名称标识，该名称是String. 该名称可以是用户定义的，也可以是Resources: SYSTEM_PROPERTIES、SYSTEM_OUT、SYSTEM_ERR、LOCALE或中的预定义常量之一TIME_ZONE。

如果以下示例中的测试在不使用 [@ResourceLock](https://junit.org/junit5/docs/current/api/org.junit.jupiter.api/org/junit/jupiter/api/parallel/ResourceLock.html)的情况下并行运行，它们将是不稳定的。有时它们会通过，而在其他时候它们会由于写入然后读取相同的 JVM 系统属性的固有竞争条件而失败。

当使用@ResourceLock注解声明对共享资源的访问时，JUnit Jupiter引擎使用此信息来确保没有冲突的测试并行运行。

|      | 隔离运行测试如果你的大部分测试类可以在没有任何同步的情况下并行运行，但你有一些测试类需要隔离运行，你可以用 @Isolated注解标记后者。这些类中的测试按顺序执行，没有任何其他测试同时运行。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

除了String唯一标识共享资源的 之外，你还可以指定访问模式。需要READ访问共享资源的两个测试可以彼此并行运行，但不能同时READ_WRITE运行需要访问同一共享资源的任何其他测试。

```java
@Execution(CONCURRENT)
class SharedResourcesDemo {

    private Properties backup;
    
    @BeforeEach
    void backup() {
        backup = new Properties();
        backup.putAll(System.getProperties());
    }
    
    @AfterEach
    void restore() {
        System.setProperties(backup);
    }
    
    @Test
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ)
    void customPropertyIsNotSetByDefault() {
        assertNull(System.getProperty("my.prop"));
    }
    
    @Test
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ_WRITE)
    void canSetCustomPropertyToApple() {
        System.setProperty("my.prop", "apple");
        assertEquals("apple", System.getProperty("my.prop"));
    }
    
    @Test
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ_WRITE)
    void canSetCustomPropertyToBanana() {
        System.setProperty("my.prop", "banana");
        assertEquals("banana", System.getProperty("my.prop"));
    }

}
```

### 2.21 内置扩展

虽然 JUnit 团队鼓励在单独的库中打包和维护可重用扩展，但JUnit JupiterAPI 工件包含一些面向用户的扩展实现，这些实现被认为非常有用，用户不必添加其他依赖项。

#### 2.21.1 临时目录扩展

|      | @TempDir是一个实验性特征我们邀请你尝试一下并向 JUnit 团队提供反馈，以便他们改进并最终[推广](https://junit.org/junit5/docs/current/user-guide/#api-evolution)此功能。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

内置TempDirectory扩展用于为单个测试或测试类中的所有测试创建和清理临时目录。它是默认注册的。要使用它，请在生命周期方法或测试方法中注解类型为java.nio.file.Path或 java.io.File的非最终、未分配字段@TempDir或添加类型为java.nio.file.Path或 java.io.File注解为的参数。@TempDir

例如，以下测试@TempDir为单个测试方法声明了一个带注解的参数，在临时目录中创建并写入一个文件，并检查其内容。

需要临时目录的测试方法

```java
@Test
void writeItemsToFile(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("test.txt");

    new ListWriter(file).write("a", "b", "c");
    
    assertEquals(singletonList("a,b,c"), Files.readAllLines(file));
}
```

你可以通过指定多个带注解的参数来注入多个临时目录。

需要多个临时目录的测试方法

```java
@Test
void copyFileFromSourceToTarget(@TempDir Path source, @TempDir Path target) throws IOException {
    Path sourceFile = source.resolve("test.txt");
    new ListWriter(sourceFile).write("a", "b", "c");

    Path targetFile = Files.copy(sourceFile, target.resolve("test.txt"));
    
    assertNotEquals(sourceFile, targetFile);
    assertEquals(singletonList("a,b,c"), Files.readAllLines(targetFile));
}
```

|      | 要恢复为整个测试类或方法使用单个临时目录的旧行为(取决于使用注解的级别)，你可以将junit.jupiter.tempdir.scope配置参数设置为per_context. 但是，请注意此选项已弃用，并将在未来的版本中删除。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

@TempDir构造函数参数不支持。如果你希望跨生命周期方法和当前测试方法保留对临时目录的单个引用，请通过使用 . 注解实例字段来使用字段注入@TempDir。

以下示例将共享临时目录存储在static字段中。这允许在sharedTempDir测试类的所有生命周期方法和测试方法中使用相同的方法。为了更好地隔离，你应该使用一个实例字段，以便每个测试方法使用一个单独的目录。

跨测试方法共享临时目录的测试类

```java
class SharedTempDirectoryDemo {

    @TempDir
    static Path sharedTempDir;
    
    @Test
    void writeItemsToFile() throws IOException {
        Path file = sharedTempDir.resolve("test.txt");
    
        new ListWriter(file).write("a", "b", "c");
    
        assertEquals(singletonList("a,b,c"), Files.readAllLines(file));
    }
    
    @Test
    void anotherTestThatUsesTheSameTempDir() {
        // use sharedTempDir
    }

}
```

@TempDir注解有一个可选属性，cleanup可以设置为 NEVER、ON_SUCCESS或ALWAYS。如果清理模式设置为NEVER，则在测试完成后不会删除临时目录。如果设置为ON_SUCCESS，只有在测试成功完成后才会删除临时目录。

默认清理模式是ALWAYS. 你可以使用 junit.jupiter.tempdir.cleanup.mode.default [配置参数](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params)来覆盖此默认值。

具有未清理的临时目录的测试类

```java
class TempDirCleanupModeDemo {

    @Test
    void fileTest(@TempDir(cleanup = ON_SUCCESS) Path tempDir) {
        // perform test
    }
}
```

## 3. 从JUnit 4迁移

尽管JUnit Jupiter编程模型和扩展模型不支持JUnit 4特性，例如Rules和Runners本机，但预计源代码维护者不需要更新所有现有测试、测试扩展和自定义构建测试基础设施以迁移到 JUnit木星。

相反，JUnit 通过JUnit Vintage 测试引擎提供了一个温和的迁移路径，该引擎允许使用 JUnit 平台基础设施执行基于 JUnit 3 和JUnit 4的现有测试。由于特定于JUnit Jupiter的所有类和注解都位于org.junit.jupiter基础包下，因此在类路径中同时包含JUnit 4和JUnit Jupiter不会导致任何冲突。因此，将现有的JUnit 4测试与JUnit Jupiter测试一起维护是安全的。此外，由于 JUnit 团队将继续为 JUnit 4.x 基线提供维护和错误修复版本，开发人员有充足的时间按照自己的时间表迁移到 JUnit Jupiter。

### 3.1 在JUnit平台上运行JUnit 4测试

确保junit-vintage-engine工件在你的测试运行时路径中。在那种情况下，JUnit 3 和JUnit 4测试将由 JUnit 平台启动程序自动选取。

请参阅存储库中的示例项目，[junit5-samples](https://github.com/junit-team/junit5-samples)了解如何使用 Gradle 和 Maven 完成此操作。

#### 3.1.1 类别支持

对于用 注解的测试类或方法，@CategoryJUnit Vintage 测试引擎将类别的完全限定类名公开 为相应测试类或测试方法的[标记。](https://junit.org/junit5/docs/current/user-guide/#running-tests-tags)例如，如果一个测试方法被注解为@Category(Example.class)，它将被标记为"com.acme.Example"。与JUnit 4中的运行器类似Categories，此信息可用于在执行之前过滤发现的测试(有关详细信息，请参阅[运行测试](https://junit.org/junit5/docs/current/user-guide/#running-tests))。

### 3.2 迁移技巧

以下是将现有JUnit 4测试迁移到JUnit Jupiter时应该注意的主题。

-   注解驻留在org.junit.jupiter.api包中。
-   断言驻留在org.junit.jupiter.api.Assertions.
    -   请注意，你可以继续使用来自org.junit.Assert或任何其他断言库的断言方法，例如[AssertJ](https://joel-costigliola.github.io/assertj/)、[Hamcrest](https://hamcrest.org/JavaHamcrest/)、[Truth](https://truth.dev/)等。
-   假设驻留在org.junit.jupiter.api.Assumptions.
    -   请注意，JUnit Jupiter5.4 及更高版本支持JUnit 4org.junit.Assume类中的方法进行假设。具体来说，JUnit Jupiter支持 JUnit 4AssumptionViolatedException来发出测试应中止而不是标记为失败的信号。
-   @Before并且@After不再存在；使用@BeforeEachand@AfterEach代替。
-   @BeforeClass并且@AfterClass不再存在；使用@BeforeAlland@AfterAll 代替。
-   @Ignore不再存在：改为使用@Disabled或其他内置 [执行条件之一](https://junit.org/junit5/docs/current/user-guide/#writing-tests-conditional-execution)
    -   另请参阅[JUnit 4@Ignore 支持](https://junit.org/junit5/docs/current/user-guide/#migrating-from-junit4-ignore-annotation-support)。
-   @Category不复存在; 改用@Tag。
-   @RunWith不复存在; @ExtendWith被.取代
-   @Rule并且@ClassRule不再存在；@ExtendWith被和 取代@RegisterExtension。
    -   另请参阅[有限的JUnit 4规则支持](https://junit.org/junit5/docs/current/user-guide/#migrating-from-junit4-rule-support)。
-  JUnit Jupiter中的断言和假设接受失败消息作为它们的最后一个参数而不是第一个参数。
    -   有关详细信息，请参阅[失败消息参数](https://junit.org/junit5/docs/current/user-guide/#migrating-from-junit4-failure-message-arguments)。

### 3.3 有限的JUnit 4规则支持

如上所述，JUnit Jupiter本身不支持也不会支持JUnit 4规则。然而，JUnit 团队意识到许多组织，尤其是大型组织，可能拥有使用自定义规则的大型JUnit 4代码库。为了服务于这些组织并实现渐进的迁移路径，JUnit 团队决定在JUnit Jupiter中逐字支持一系列JUnit 4规则。这种支持基于适配器，并且仅限于那些在语义上与JUnit Jupiter扩展模型兼容的规则，即那些不会完全改变测试的整体执行流程的规则。

来自 JUnit Jupiter的junit-jupiter-migrationsupport模块目前支持以下三种Rule类型，包括这些类型的子类：

-   org.junit.rules.ExternalResource(包括org.junit.rules.TemporaryFolder)
-   org.junit.rules.Verifier(包括org.junit.rules.ErrorCollector)
-   org.junit.rules.ExpectedException

与JUnit 4一样，支持规则注解的字段和方法。通过在测试类上使用这些类级扩展Rule，遗留代码库中的此类实现可以保持不变，包括JUnit 4规则导入语句。

这种有限形式的Rule支持可以通过类级注解开启 @EnableRuleMigrationSupport。此注解是一个组合注解，它启用所有规则迁移支持扩展：VerifierSupport、ExternalResourceSupport和 ExpectedExceptionSupport。你也可以选择注解你的测试类， @EnableJUnit4MigrationSupport其中注册了对规则的迁移支持和JUnit 4的@Ignore注解(请参阅[JUnit 4@Ignore Support](https://junit.org/junit5/docs/current/user-guide/#migrating-from-junit4-ignore-annotation-support))。

但是，如果你打算为JUnit Jupiter开发新的扩展，请使用JUnit Jupiter的新扩展模型，而不是JUnit 4的基于规则的模型。

### 3.4 JUnit 4的@Ignore支持

为了提供从JUnit 4到JUnit Jupiter的平滑迁移路径，该 junit-jupiter-migrationsupport模块提供了对@Ignore 类似于 Jupiter@Disabled注解的JUnit 4注解的支持。

要@Ignore与基于JUnit Jupiter的测试一起使用，请在你的构建中配置对模块的测试依赖性 junit-jupiter-migrationsupport，然后使用@ExtendWith(IgnoreCondition.class)或注解你的测试类@EnableJUnit4MigrationSupport(它会自动注册IgnoreConditionLimited [JUnit 4Rule Support](https://junit.org/junit5/docs/current/user-guide/#migrating-from-junit4-rule-support))。IgnoreCondition是一个 禁用带有注解的ExecutionCondition测试类或测试方法的 @Ignore.

```java
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.EnableJUnit4MigrationSupport;

// @ExtendWith(IgnoreCondition.class)
@EnableJUnit4MigrationSupport
class IgnoredTestsDemo {

    @Ignore
    @Test
    void testWillBeIgnored() {
    }
    
    @Test
    void testWillBeExecuted() {
    }
}
```

### 3.5 失败消息参数

JUnit Jupiter中的Assumptions和Assertions类以不同于JUnit 4的顺序声明参数。在JUnit 4中，断言和假设方法接受失败消息作为第一个参数；而在JUnit Jupiter中，断言和假设方法接受失败消息作为最后一个参数。

例如，assertEqualsJUnit 4中的方法声明为assertEquals(String message, Object expected, Object actual)，但在JUnit Jupiter中它声明为 assertEquals(Object expected, Object actual, String message). 这样做的理由是失败消息是可选的，可选参数应该在方法签名中的必需参数之后声明。

受此更改影响的方法如下：

-   断言
    -   assertTrue
    -   assertFalse
    -   assertNull
    -   assertNotNull
    -   assertEquals
    -   assertNotEquals
    -   assertArrayEquals
    -   assertSame
    -   assertNotSame
    -   assertThrows
-   假设
    -   assumeTrue
    -   assumeFalse

## 4. 运行测试

### 4.1 集成开发环境支持

#### 4.1.1 我明白这个想法

IntelliJ IDEA 从 2016.2 版开始支持在 JUnit 平台上运行测试。有关详细信息，请参阅 [IntelliJ IDEA 博客上的帖子](https://blog.jetbrains.com/idea/2016/08/using-junit-5-in-intellij-idea/)。但是请注意，建议使用 IDEA 2017.3 或更新版本，因为这些更新版本的 IDEA 将根据项目中使用的 API 版本自动下载以下 JAR：junit-platform-launcher、、 junit-jupiter-engine和junit-vintage-engine。

|      | IDEA 2017.3 之前的 IntelliJ IDEA 版本捆绑了JUnit 5的特定版本。因此，如果你想使用更新版本的 JUnit Jupiter，IDE 中的测试执行可能会因版本冲突而失败。在这种情况下，请按照以下说明使用比与 IntelliJ IDEA 捆绑的版本更新的JUnit 5版本。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

为了使用不同的JUnit 5版本(例如，5.9.1)，你可能需要在类路径中包含 、 和 JAR 的junit-platform-launcher相应 junit-jupiter-engine版本junit-vintage-engine。

额外的摇篮依赖


testImplementation(platform("org.junit:junit-bom:5.9.1"))
testRuntimeOnly("org.junit.platform:junit-platform-launcher") {
  because("Only needed to run tests in a version of IntelliJ IDEA that bundles older versions")
}
testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
testRuntimeOnly("org.junit.vintage:junit-vintage-engine")


额外的 Maven 依赖项

```xml
<!-- ... -->
<dependencies>
    <!-- Only needed to run tests in a version of IntelliJ IDEA that bundles older versions -->
    <dependency>
        <groupId>org.junit.platform</groupId>
        <artifactId>junit-platform-launcher</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-engine</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.junit.vintage</groupId>
        <artifactId>junit-vintage-engine</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.junit</groupId>
            <artifactId>junit-bom</artifactId>
            <version>5.9.1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

#### 4.1.2 蚀

自 Eclipse Oxygen.1a (4.7.1a) 发布以来，Eclipse IDE 提供了对 JUnit 平台的支持。

有关在 Eclipse 中使用JUnit 5的更多信息，请参阅[Eclipse Project Oxygen.1a (4.7.1a) - New and Noteworthy文档的官方](https://www.eclipse.org/eclipse/news/4.7.1a/#junit-5-support)Eclipse 对 JUnit 5的支持部分 。

#### 4.1.3 网豆

[自Apache NetBeans 10.0 版本](https://netbeans.apache.org/download/nb100/nb100.html)以来，NetBeans 提供了对JUnit Jupiter和 JUnit Platform 的支持 。

[有关详细信息，请参阅Apache NetBeans 10.0 发行说明](https://netbeans.apache.org/download/nb100/index.html#_junit_5)的JUnit 5部分 。

#### 4.1.4 视觉工作室代码

[Visual Studio Code](https://code.visualstudio.com/)通过默认作为 [Java 扩展包的一部分安装的](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)[Java Test Runner](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-test)扩展支持JUnit Jupiter和 JUnit 平台 。

有关详细信息，请参阅[Visual Studio Code](https://code.visualstudio.com/docs/languages/java#_testing) 文档中 Java 的测试部分 。

#### 4.1.5 其他IDE

如果你使用的编辑器或 IDE 不是前面几节中列出的编辑器或 IDE，JUnit 团队提供了两种替代解决方案来帮助你使用 JUnit 5。你可以手动使用[控制台启动器](https://junit.org/junit5/docs/current/user-guide/#running-tests-console-launcher)——例如，从命令行——或者如果你的 IDE 内置了对JUnit 4的支持，则使用[基于JUnit 4的 Runner](https://junit.org/junit5/docs/current/user-guide/#running-tests-junit-platform-runner)执行测试。

### 4.2 建立支持

#### 4.2.1 摇篮

|      | JUnit 平台 Gradle 插件已停产由junit-platform-gradle-pluginJUnit 团队开发，在 JUnit Platform 1.2 中已弃用，并在 1.3 中停产。请切换到 Gradle 的标准test任务。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

从[4.6 版](https://docs.gradle.org/4.6/release-notes.html)开始，Gradle 为 在 JUnit 平台上执行测试提供了[原生支持。](https://docs.gradle.org/current/userguide/java_testing.html#using_junit5)要启用它，你需要 useJUnitPlatform()在test任务声明中指定build.gradle：


test {
    useJUnitPlatform()
}


还支持按[tags](https://junit.org/junit5/docs/current/user-guide/#running-tests-tags)、 [tag expressions](https://junit.org/junit5/docs/current/user-guide/#running-tests-tag-expressions)或 engines过滤：

```groovy
test {
    useJUnitPlatform {
        includeTags("fast", "smoke & feature-a")
        // excludeTags("slow", "ci")
        includeEngines("junit-jupiter")
        // excludeEngines("junit-vintage")
    }
}
```

请参阅 [官方 Gradle 文档](https://docs.gradle.org/current/userguide/java_plugin.html#sec:java_test) 以获取完整的选项列表。

|      | 有关如何覆盖 Spring Boot 应用程序中使用的 JUnit 版本的详细信息， 请参阅[Spring Boot 。](https://junit.org/junit5/docs/current/user-guide/#running-tests-build-spring-boot) |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

##### 配置参数

标准的 Gradletest任务目前不提供专用的 DSL 来设置 JUnit 平台[配置参数](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params)来影响测试发现和执行。但是，你可以通过系统属性(如下所示)或通过 junit-platform.properties文件在构建脚本中提供配置参数。

```groovy
test {
    // ...
    systemProperty("junit.jupiter.conditions.deactivate", "")
    systemProperty("junit.jupiter.extensions.autodetection.enabled", true)
    systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_class")
    // ...
}
```

##### 配置测试引擎

为了运行任何测试，一个TestEngine实现必须在类路径上。

要配置对基于JUnit Jupiter的测试的支持，请配置testImplementation对依赖项聚合JUnit Jupiter工件的依赖项，类似于以下内容。

```groovy
dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
}
```

JUnit Platform 可以运行基于JUnit 4的测试，只要你配置testImplementation 对JUnit 4的testRuntimeOnly依赖项和对 JUnit VintageTestEngine 实现的依赖项，类似于以下内容。

```groovy
dependencies {
    testImplementation("junit:junit:4.13.2")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.9.1")
}
```

##### 配置日志记录(可选)

JUnit 使用java.util.logging包中的 Java Logging API(又名JUL)来发出警告和调试信息。配置选项请参考官方文档 LogManager。

或者，可以将日志消息重定向到其他日志记录框架，例如 [Log4j](https://logging.apache.org/log4j/2.x/)或[Logback](https://logback.qos.ch/)。要使用提供自定义实现的日志记录框架 ，请将系统属性LogManager设置为要使用的实现的完全限定类名。下面的示例演示了如何配置 Log4j 2.x(有关详细信息，请参阅[Log4jJDK日志记录适配器](https://logging.apache.org/log4j/2.x/log4j-jul/index.html))。java.util.logging.managerLogManager

```groovy
test {
    systemProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager")
}
```

其他日志记录框架提供了不同的方法来重定向使用 java.util.logging. 例如，对于[Logback](https://logback.qos.ch/) ，你可以 通过向运行时类路径添加额外的依赖项来使用[JUL 到 SLF4J Bridge 。](https://www.slf4j.org/legacy.html#jul-to-slf4j)

#### 4.2.2 行家

|      | JUnit Platform Maven Surefire Provider 已停产junit-platform-surefire-provider最初由 JUnit 团队开发的 ，在 JUnit Platform 1.3 中被弃用，并在 1.4 中停产。请改用 Maven Surefire 的本地支持。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

从[版本 2.22.0](https://issues.apache.org/jira/browse/SUREFIRE-1330)开始，Maven Surefire 和 Maven Failsafe 为 在 JUnit 平台上执行测试提供[原生支持。](https://maven.apache.org/surefire/maven-surefire-plugin/examples/junit-platform.html)项目中的pom.xml文件 junit5-jupiter-starter-maven演示了如何使用 Maven Surefire 插件，并且可以作为配置 Maven 构建的起点。

|      | 有关如何覆盖 Spring Boot 应用程序中使用的 JUnit 版本的详细信息， 请参阅[Spring Boot 。](https://junit.org/junit5/docs/current/user-guide/#running-tests-build-spring-boot) |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

##### 配置测试引擎

为了让 Maven Surefire 或 Maven Failsafe 运行任何测试，至少 TestEngine必须将一个实现添加到测试类路径中。

要配置对基于JUnit Jupiter的测试的支持，请配置test对JUnit JupiterAPI 和JUnit Jupiter实现的作用域依赖项，TestEngine类似于以下内容。

```xml
<!-- ... -->
<dependencies>
    <!-- ... -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.9.1</version>
        <scope>test</scope>
    </dependency>
    <!-- ... -->
</dependencies>
<build>
    <plugins>
        <plugin>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>2.22.2</version>
        </plugin>
        <plugin>
            <artifactId>maven-failsafe-plugin</artifactId>
            <version>2.22.2</version>
        </plugin>
    </plugins>
</build>
<!-- ... -->
```

Maven Surefire 和 Maven Failsafe 可以运行基于JUnit 4的测试以及 Jupiter测试，只要你配置testJUnit 4的作用域依赖项和 TestEngine类似于以下的 JUnit Vintage 实现。

```xml
<!-- ... -->
<dependencies>
    <!-- ... -->
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.13.2</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.junit.vintage</groupId>
        <artifactId>junit-vintage-engine</artifactId>
        <version>5.9.1</version>
        <scope>test</scope>
    </dependency>
    <!-- ... -->
</dependencies>
<!-- ... -->
<build>
    <plugins>
        <plugin>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>2.22.2</version>
        </plugin>
        <plugin>
            <artifactId>maven-failsafe-plugin</artifactId>
            <version>2.22.2</version>
        </plugin>
    </plugins>
</build>
<!-- ... -->
```

##### 按测试类名称过滤

Maven Surefire 插件将扫描其完全限定名称与以下模式匹配的测试类。

-   /Test.java
-   /Test.java
-   /Tests.java
-   /TestCase.java

而且，它默认会排除所有嵌套类(包括静态成员类)。

但是请注意，你可以通过 在文件中配置显式include和exclude规则来覆盖此默认行为。pom.xml例如，要防止 Maven Surefire 排除静态成员类，你可以按如下方式覆盖其排除规则。

覆盖 Maven Surefire 的排除规则

```xml
<!-- ... -->
<build>
    <plugins>
        <plugin>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>2.22.2</version>
            <configuration>
                <excludes>
                    <exclude/>
                </excludes>
            </configuration>
        </plugin>
    </plugins>
</build>
<!-- ... -->
```

有关详细信息，请参阅 Maven Surefire[的测试包含和排除](https://maven.apache.org/surefire/maven-surefire-plugin/examples/inclusion-exclusion.html) 文档。

##### 按标签过滤

你可以使用以下配置属性按[标签](https://junit.org/junit5/docs/current/user-guide/#running-tests-tags)或 [标签表达式](https://junit.org/junit5/docs/current/user-guide/#running-tests-tag-expressions)过滤测试。

-   要包含标签或标签表达式，请使用groups.
-   要排除标签或标签表达式，请使用excludedGroups.

```xml
<!-- ... -->
<build>
    <plugins>
        <plugin>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>2.22.2</version>
            <configuration>
                <groups>acceptance | !feature-a</groups>
                <excludedGroups>integration, regression</excludedGroups>
            </configuration>
        </plugin>
    </plugins>
</build>
<!-- ... -->
```

##### 配置参数

你可以通过使用 Java文件语法(如下所示)或通过文件声明 属性和提供键值对来设置 JUnit Platform[配置参数](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params)以影响测试发现和执行。configurationParametersPropertiesjunit-platform.properties

```xml
<!-- ... -->
<build>
    <plugins>
        <plugin>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>2.22.2</version>
            <configuration>
                <properties>
                    <configurationParameters>
                        junit.jupiter.conditions.deactivate = 
                        junit.jupiter.extensions.autodetection.enabled = true
                        junit.jupiter.testinstance.lifecycle.default = per_class
                    </configurationParameters>
                </properties>
            </configuration>
        </plugin>
    </plugins>
</build>
<!-- ... -->
```

#### 4.2.3 蚂蚁

从 version 开始1.10.3，[Ant](https://ant.apache.org/)的 [junitlauncher](https://ant.apache.org/manual/Tasks/junitlauncher.html)任务是为在 JUnit 平台上启动测试提供本机支持。该junitlauncher 任务仅负责启动 JUnit 平台并将选定的测试集合传递给它。然后 JUnit 平台委托注册的测试引擎来发现和执行测试。

该junitlauncher任务尝试尽可能地与本机 Ant 构造保持一致，例如 [资源集合](https://ant.apache.org/manual/Types/resources.html#collection) ，以允许用户选择他们希望测试引擎执行的测试。与许多其他核心 Ant 任务相比，这使该任务具有一致且自然的感觉。

从1.10.6Ant 版本开始，该junitlauncher任务支持 [在单独的 JVM 中分叉测试](https://ant.apache.org/manual/Tasks/junitlauncher.html#fork)。

项目中的build.xml文件junit5-jupiter-starter-ant演示了如何使用任务并可以作为起点。

##### 基本用法

以下示例演示如何配置junitlauncher任务以选择单个测试类(即org.myapp.test.MyFirstJUnit5Test)。

```xml
<path id="test.classpath">
    <!-- The location where you have your compiled classes -->
    <pathelement location="${build.classes.dir}" />
</path>

<!-- ... -->

<junitlauncher>
    <classpath refid="test.classpath" />
    <test name="org.myapp.test.MyFirstJUnit5Test" />
</junitlauncher>
```

该test元素允许你指定要选择和执行的单个测试类。该classpath元素允许你指定用于启动 JUnit 平台的类路径。该类路径还将用于定位作为执行一部分的测试类。

以下示例演示如何配置junitlauncher任务以从多个位置选择测试类。

```xml
<path id="test.classpath">
    <!-- The location where you have your compiled classes -->
    <pathelement location="${build.classes.dir}" />
</path>
<!-- ... -->
<junitlauncher>
    <classpath refid="test.classpath" />
    <testclasses outputdir="${output.dir}">
        <fileset dir="${build.classes.dir}">
            <include name="org/example//demo//" />
        </fileset>
        <fileset dir="${some.other.dir}">
            <include name="org/myapp//" />
        </fileset>
    </testclasses>
</junitlauncher>
```

在上面的示例中，该testclasses元素允许你选择驻留在不同位置的多个测试类。

有关使用和配置选项的更多详细信息，请参阅该 [junitlauncher任务](https://ant.apache.org/manual/Tasks/junitlauncher.html)的官方 Ant 文档。

#### 4.2.4 弹簧靴

[Spring Boot](https://spring.io/projects/spring-boot)自动支持管理项目中使用的 JUnit 版本。此外，该 spring-boot-starter-test工件自动包含 JUnit Jupiter、AssertJ、Mockito 等测试库。

如果你的构建依赖于 Spring Boot 的依赖管理支持，则不应[junit-bom](https://junit.org/junit5/docs/current/user-guide/#dependency-metadata-junit-bom)在构建脚本中导入，因为这将导致 JUnit 依赖管理的重复(并且可能存在冲突)。

如果你需要覆盖 Spring Boot 应用程序中使用的依赖项的版本，则必须覆盖 Spring Boot 插件使用的 BOM 中定义的[版本属性的确切名称。](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#appendix.dependency-versions.properties)例如，Spring Boot 中JUnit Jupiter版本属性的名称是junit-jupiter.version. [为Gradle](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#managing-dependencies.dependency-management-plugin.customizing) 和 [Maven](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/#using.parent-pom)记录了更改依赖版本的机制 。

使用 Gradle，你可以通过在 build.gradle文件中包含以下内容来覆盖JUnit Jupiter版本。


ext['junit-jupiter.version'] = '5.9.1'


使用 Maven，你可以通过在 pom.xml文件中包含以下内容来覆盖JUnit Jupiter版本。

```xml
<properties>
    <junit-jupiter.version>5.9.1</junit-jupiter.version>
</properties>
```

### 4.3 控制台启动器

这ConsoleLauncher是一个命令行 Java 应用程序，可让你从控制台启动 JUnit 平台。例如，它可用于运行 JUnit Vintage 和JUnit Jupiter测试并将测试执行结果打印到控制台。

junit-platform-console-standalone-1.9.1.jar包含所有依赖项的可执行文件发布在[junit-platform-console-standalone](https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone) 目录下的[Maven 中央](https://search.maven.org/)存储库中 。它包括以下依赖项：

-   junit:junit:4.13.2
-   org.apiguardian:apiguardian-api:1.1.2
-   org.hamcrest:hamcrest-core:1.3
-   org.junit.jupiter:junit-jupiter-api:5.9.1
-   org.junit.jupiter:junit-jupiter-engine:5.9.1
-   org.junit.jupiter:junit-jupiter-params:5.9.1
-   org.junit.platform:junit-platform-commons:1.9.1
-   org.junit.platform:junit-platform-console:1.9.1
-   org.junit.platform:junit-platform-engine:1.9.1
-   org.junit.platform:junit-platform-launcher:1.9.1
-   org.junit.platform:junit-platform-reporting:1.9.1
-   org.junit.platform:junit-platform-suite-api:1.9.1
-   org.junit.platform:junit-platform-suite-commons:1.9.1
-   org.junit.platform:junit-platform-suite-engine:1.9.1
-   org.junit.platform:junit-platform-suite:1.9.1
-   org.junit.vintage:junit-vintage-engine:5.9.1
-   org.opentest4j:opentest4j:1.2.0

你可以[运行](https://docs.oracle.com/javase/tutorial/deployment/jar/run.html)独立ConsoleLauncher的，如下所示。


$ java -jar junit-platform-console-standalone-1.9.1.jar <OPTIONS>

├─ JUnit Vintage
│  └─ example.JUnit4Tests
│     └─ standardJUnit4Test ✔
└─ JUnit Jupiter
   ├─ StandardTests
   │  ├─ succeedingTest() ✔
   │  └─ skippedTest() ↷ for demonstration purposes
   └─ A special test case
      ├─ Custom test name containing spaces ✔
      ├─ ╯°□°)╯ ✔
      └─ 😱 ✔

Test run finished after 64 ms
[         5 containers found      ]
[         0 containers skipped    ]
[         5 containers started    ]
[         0 containers aborted    ]
[         5 containers successful ]
[         0 containers failed     ]
[         6 tests found           ]
[         1 tests skipped         ]
[         5 tests started         ]
[         0 tests aborted         ]
[         5 tests successful      ]
[         0 tests failed          ]


你还可以运行独立ConsoleLauncher程序，如下所示(例如，将所有 jar 包含在一个目录中)：


$ java -cp classes:testlib/ org.junit.platform.console.ConsoleLauncher <OPTIONS>


|      | 退出代码如果有任何容器或测试失败 ，则ConsoleLauncher退出状态代码。1如果未发现任何测试并且--fail-if-no-tests提供了命令行选项，则ConsoleLauncher退出状态代码为2。否则退出代码为0。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 4.3.1 选项


用法：ConsoleLauncher [选项]
启动用于测试发现和执行的 JUnit 平台。
      [@<filename>...] 一个或多个包含选项的参数文件。

命令

  -h, --help 显示帮助信息。
      --list-engines 列出所有可观察的测试引擎。

选择器

      --scan-classpath, --scan-class-path[=路径]
                             扫描类路径或显式类路径上的所有目录
                               根。没有参数，只有系统上的目录
                               类路径以及通过提供的其他类路径条目
                               -cp(目录和 JAR 文件)被扫描。显式类路径
                               不在类路径上的根将被默默地忽略。
                               这个选项可以重复。
      --scan-modules 实验：扫描所有解析的模块以进行测试发现。
  -u, --select-uri=URI 选择用于测试发现的 URI。这个选项可以重复。
  -f, --select-file=FILE 选择用于测试发现的文件。这个选项可以重复。
  -d, --select-directory=DIR 选择用于测试发现的目录。这个选项可以是
                               重复。
  -o, --select-module=NAME EXPERIMENTAL: 选择单个模块进行测试发现。这个
                               选项可以重复。
  -p, --select-package=PKG 选择用于测试发现的包。这个选项可以重复。
  -c, --select-class=CLASS 选择一个用于测试发现的类。这个选项可以重复。
  -m, --select-method=NAME 选择测试发现的方法。这个选项可以重复。
  -r, --select-resource=资源
                             选择用于测试发现的类路径资源。这个选项可以
                               被重复。
  -i, --select-iteration=TYPE:VALUE[INDEX(..INDEX)?(,INDEX(..INDEX)?)]
                             选择用于测试发现的迭代(例如 method:com.acme.Foo#m()
                               [1..2])。这个选项可以重复。

过滤器

  -n, --include-classname=模式
                             提供一个正则表达式以仅包含完全符合以下条件的类
                               限定名称匹配。为了避免不必要地加载类，
                               默认模式仅包括以
                               “测试”或以“测试”或“测试”结尾。当这个选项是
                               重复，所有模式将使用 OR 语义组合。
                               默认值：^(Test.|.+[.$]Test.|.Tests?)$
  -N, --exclude-classname=模式
                             提供一个正则表达式来排除那些完全
                               限定名称匹配。重复此选项时，所有
                               模式将使用 OR 语义组合。
      --include-package=PKG 提供要包含在测试运行中的包。这个选项可以
                               被重复。
      --exclude-package=PKG 提供要从测试运行中排除的包。这个选项
                               可以重复。
  -t, --include-tag=TAG 提供标签或标签表达式以仅包含其标签的测试
                               匹配。重复此选项时，所有花样都将
                               使用 OR 语义组合。
  -T, --exclude-tag=TAG 提供一个标签或标签表达式来排除那些标签的测试
                               匹配。重复此选项时，所有花样都将
                               使用 OR 语义组合。
  -e, --include-engine=ID 提供要包含在测试运行中的引擎的 ID。这个
                               选项可以重复。
  -E, --exclude-engine=ID 提供要从测试运行中排除的引擎的 ID。
                               这个选项可以重复。

运行时配置

      -cp, --classpath, --class-path=路径
                             提供额外的类路径条目——例如，用于添加
                               引擎及其依赖项。这个选项可以重复。
      --config=KEY=VALUE 设置用于测试发现和执行的配置参数。
                               这个选项可以重复。

报告

      --fail-if-no-tests 如果未找到测试，则失败并返回退出状态代码 2。
      --reports-dir=DIR 启用报告输出到指定的本地目录(将是
                               如果不存在则创建)。

控制台输出

      --disable-ansi-colors 在输出中禁用 ANSI 颜色(并非所有终端都支持)。
      --color-palette=FILE 指定属性文件的路径以自定义 ANSI 样式
                               输出(并非所有终端都支持)。
      --single-color Style 测试输出只使用文本属性，没有颜色(不是
                               所有终端都支持)。
      --disable-banner 禁止打印欢迎信息。
      --details=MODE 选择执行测试时的输出详细信息模式。利用
                               以下之一：无、摘要、平面、树、冗长。如果“无”是
                               选中，则仅显示摘要和测试失败。
                               默认值：树。
      --details-theme=THEME 选择执行测试时的输出细节树主题。
                               使用以下之一：ascii、unicode。默认检测基于
                               默认字符编码。

有关详细信息，请参阅 JUnit 用户指南，网址为
https://junit.org/junit5/docs/current/user-guide/


#### 4.3.2 参数文件(@-文件)

在某些平台上，当你创建带有很多选项或长参数的命令行时，你可能会遇到命令行长度的系统限制。

从 1.3 版开始，ConsoleLauncher支持参数文件，也称为 @-files。参数文件是本身包含要传递给命令的参数的文件。当底层的[picocli](https://github.com/remkop/picocli)命令行解析器遇到以字符 开头的参数时@，它会将该文件的内容扩展到参数列表中。

文件中的参数可以用空格或换行符分隔。如果一个参数包含嵌入的空格，整个参数应该用双引号或单引号引起来——例如，"-f=My Files/Stuff.java".

如果参数文件不存在或无法读取，则参数将按字面意思处理，不会被删除。这可能会导致出现“参数不匹配”错误消息。你可以通过执行 picocli.trace系统属性设置为的命令来解决此类错误DEBUG。

可以在命令行上指定多个@-files 。指定的路径可以是相对于当前目录的，也可以是绝对的。

@你可以通过使用附加@符号转义来传递具有初始字符的真实参数。例如，@@somearg将成为@somearg和不会受到扩张。

#### 4.3.3 颜色定制

ConsoleLauncher可以自定义输出中使用的颜色。该选项--single-color将应用内置的单色样式，同时 --color-palette接受一个属性文件来覆盖 [ANSI SGR](https://en.wikipedia.org/wiki/ANSI_escape_code#Colors)颜色样式。下面的属性文件演示了默认样式：


SUCCESSFUL = 32
ABORTED = 33
FAILED = 31
SKIPPED = 35
CONTAINER = 35
TEST = 34
DYNAMIC = 35
REPORTED = 37


### 4.4 使用JUnit 4运行 JUnit 平台

|      | 跑步者JUnitPlatform已被弃用JUnitPlatformrunner 由 JUnit 团队开发，作为在JUnit 4环境中的 JUnit 平台上运行测试套件和测试的临时解决方案。近年来，所有主流构建工具和 IDE 都提供了直接在 JUnit 平台上运行测试的内置支持。此外，模块@Suite提供的支持 的引入使得运行器过时了。有关详细信息，请参阅 [JUnit 平台套件引擎](https://junit.org/junit5/docs/current/user-guide/#junit-platform-suite-engine)。junit-platform-suite-engineJUnitPlatform因此，JUnitPlatformrunner 和@UseTechnicalNames注解在 JUnit Platform 1.8 中已被弃用，并将在 JUnit Platform 2.0 中删除。如果你正在使用JUnitPlatform跑步者，请迁移到@Suite支持。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

JUnitPlatformrunner 是基于JUnit 4的，Runner它使你能够在JUnit 4环境中的 JUnit 平台上运行其编程模型受支持的任何测试——例如，JUnit Jupiter测试类。

注解一个类@RunWith(JUnitPlatform.class)允许它与支持JUnit 4但尚不直接支持 JUnit 平台的 IDE 和构建系统一起运行。

|      | 由于 JUnit 平台具有JUnit 4所没有的功能，因此运行器只能支持 JUnit 平台功能的一个子集，尤其是在报告方面(请参阅[显示名称与技术名称](https://junit.org/junit5/docs/current/user-guide/#running-tests-junit-platform-runner-technical-names))。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 4.4.1 设置

你需要以下工件及其对类路径的依赖性。有关组 ID、工件 ID 和版本的详细信息，请参阅 [依赖项元数据。](https://junit.org/junit5/docs/current/user-guide/#dependency-metadata)

##### 显式依赖

-   junit-platform-runner在测试范围内：JUnitPlatform跑步者的位置
-   junit-4.13.2.jar在测试范围内：使用JUnit 4运行测试
-   junit-jupiter-api在测试范围内：用于使用JUnit Jupiter编写测试的 API，包括@Test等。
-   junit-jupiter-engine在测试运行时范围内：TestEngineJUnit JupiterAPI 的实现

##### 传递依赖

-   junit-platform-suite-api在测试范围内
-   junit-platform-suite-commons在测试范围内
-   junit-platform-launcher在测试范围内
-   junit-platform-engine在测试范围内
-   junit-platform-commons在测试范围内
-   opentest4j在测试范围内

#### 4.4.2 显示名称与技术名称

要为类运行定义自定义显示名称@RunWith(JUnitPlatform.class)，请使用 注解类@SuiteDisplayName并提供自定义值。

默认情况下，显示名称将用于测试工件；但是，当 JUnitPlatform使用运行器通过 Gradle 或 Maven 等构建工具执行测试时，生成的测试报告通常需要包含测试工件的技术名称——例如，完全限定的类名——而不是较短的显示名称，如测试类的简单名称或包含特殊字符的自定义显示名称。要启用用于报告目的的技术名称，请在 @UseTechnicalNames旁边声明注解@RunWith(JUnitPlatform.class)。

请注意，存在@UseTechnicalNames会覆盖通过配置的任何自定义显示名称@SuiteDisplayName。

#### 4.4.3 单一测试类

使用运行器的一种方法JUnitPlatform是直接用注解测试类 @RunWith(JUnitPlatform.class)。请注意，以下示例中的测试方法带有org.junit.jupiter.api.Test(JUnit Jupiter) 注解，而不是 org.junit.Test(JUnit 4)。此外，在这种情况下，测试类必须是public；否则，某些 IDE 和构建工具可能无法将其识别为JUnit 4测试类。

```java
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

@RunWith(org.junit.platform.runner.JUnitPlatform.class)
public class JUnitPlatformClassDemo {

    @Test
    void succeedingTest() {
        / no-op /
    }
    
    @Test
    void failingTest() {
        fail("Failing for failing's sake.");
    }

}
```

#### 4.4.4 测试套件

如果你有多个测试类，你可以创建一个测试套件，如以下示例所示。

```java
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.runner.RunWith;

@RunWith(org.junit.platform.runner.JUnitPlatform.class)
@SuiteDisplayName("JUnit Platform Suite Demo")
@SelectPackages("example")
public class JUnitPlatformSuiteDemo {
}
```

将JUnitPlatformSuiteDemo发现并运行example包及其子包中的所有测试。默认情况下，它将仅包含名称以 或 开头或Test结尾的测试类。TestTests

|      | 其他配置选项有更多的配置选项用于发现和过滤测试，而不仅仅是 @SelectPackages. 请查阅org.junit.platform.suite.api包的 Javadoc 以获取更多详细信息。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

|      | 注解的测试类和套件@RunWith(JUnitPlatform.class) 不能直接在 JUnit 平台上执行(或作为某些 IDE 中记录的“JUnit 5”测试)。此类类和套件只能使用JUnit 4基础架构执行。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

### 4.5 配置参数

除了指示平台包含哪些测试类和测试引擎、扫描哪些包等之外，有时还需要提供特定于特定测试引擎、侦听器或注册扩展的额外自定义配置参数。例如，JUnit JupiterTestEngine支持以下用例的配置参数。

-   [更改默认测试实例生命周期](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-instance-lifecycle-changing-default)
-   [启用自动扩展检测](https://junit.org/junit5/docs/current/user-guide/#extensions-registration-automatic-enabling)
-   [停用条件](https://junit.org/junit5/docs/current/user-guide/#extensions-conditions-deactivation)
-   [设置默认显示名称生成器](https://junit.org/junit5/docs/current/user-guide/#writing-tests-display-name-generator-default)

配置参数是基于文本的键值对，可以通过以下机制之一提供给在 JUnit 平台上运行的测试引擎。

1.  其中的configurationParameter()和configurationParameters()方法 LauncherDiscoveryRequestBuilder用于构建提供给 [LauncherAPI](https://junit.org/junit5/docs/current/user-guide/#launcher-api)的请求。通过 JUnit 平台提供的其中一种工具运行测试时，你可以指定配置参数，如下所示：
    -   [控制台启动器](https://junit.org/junit5/docs/current/user-guide/#running-tests-console-launcher)：使用--config 命令行选项。
    -   [Gradle](https://junit.org/junit5/docs/current/user-guide/#running-tests-build-gradle-config-params)：使用 systemProperty或systemPropertiesDSL。
    -   [Maven Surefire 提供者](https://junit.org/junit5/docs/current/user-guide/#running-tests-build-maven-config-params)：使用该 configurationParameters属性。
2.  JVM 系统属性。
3.  JUnit 平台配置文件：junit-platform.properties在类路径的根目录中命名的文件，遵循 JavaProperties文件的语法规则。

|      | 按照上面定义的确切顺序查找配置参数。因此，直接提供给的配置参数Launcher优先于通过系统属性和配置文件提供的配置参数。同样，通过系统属性提供的配置参数优先于通过配置文件提供的配置参数。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 4.5.1 模式匹配语法

本节介绍应用于用于以下功能的配置参数的模式匹配语法。

-   [停用条件](https://junit.org/junit5/docs/current/user-guide/#extensions-conditions-deactivation)
-   [停用 TestExecutionListener](https://junit.org/junit5/docs/current/user-guide/#launcher-api-listeners-custom-deactivation)

如果给定配置参数的值仅由星号 ( ) 组成，则该模式将匹配所有候选类。否则，该值将被视为以逗号分隔的模式列表，其中每个模式将与每个候选类的完全限定类名 ( FQCN ) 匹配。模式中的任何点 ( .) 将与FQCN中的点 ( .) 或美元符号 ( ) 匹配。$任何星号 ( ) 都将匹配 FQCN 中的一个或多个字符。模式中的所有其他字符将与 FQCN 一对一匹配。

例子：

-   : 匹配所有候选类。
-   org.junit.: 匹配org.junit基础包及其任何子包下的所有候选类。
-   .MyCustomImpl: 匹配每个简单类名完全为 的候选类 MyCustomImpl。
-   System: 匹配其 FQCN 包含的每个候选类System。
-   System, Unit: 匹配每个 FQCN 包含 Systemor的候选类Unit。
-   org.example.MyCustomImpl: 匹配 FQCN 恰好为 的候选类 org.example.MyCustomImpl。
-   org.example.MyCustomImpl, org.example.TheirCustomImpl: 匹配 FQCN 恰好为org.example.MyCustomImpl或的候选类org.example.TheirCustomImpl。

### 4.6 标签

标记是用于标记和过滤测试的 JUnit 平台概念。将标签添加到容器和测试的编程模型由测试框架定义。例如，在基于JUnit Jupiter的测试中，应该使用@Tag注解(请参阅 [标记和过滤)。](https://junit.org/junit5/docs/current/user-guide/#writing-tests-tagging-and-filtering)对于基于JUnit 4的测试，Vintage 引擎将@Category注解映射到标签(请参阅 [类别支持](https://junit.org/junit5/docs/current/user-guide/#migrating-from-junit4-categories-support))。其他测试框架可能会定义自己的注解或其他方式供用户指定标签。

#### 4.6.1 标记的语法规则

无论标签是如何指定的，JUnit 平台都强制执行以下规则：

-   标签不能为null或空白。
-   修剪后的标签不得包含空格。
-   修剪后的标签不得包含 ISO 控制字符。
-   修剪标签不得包含以下任何保留字符。
    -   ,:逗号
    -   (:左括号
    -   ):右括号
    -   &：＆符号
    -   |:垂直条
    -   !:感叹号

|      | 在上面的上下文中，“修剪”意味着前导和尾随空白字符已被删除。 |
| ---- | ---------------------------------------------------------- |
|      |                                                            |

#### 4.6.2 标记表达式

标记表达式是带有运算符!,&和的布尔表达式|。此外， (and)可用于调整运算符优先级。

支持两个特殊表达式，any()和none()，它们分别选择所有带有 任何标签的测试和所有没有任何标签的测试。这些特殊表达式可以像普通标签一样与其他表达式组合。

| 操作员 | 意义 | 结合性 |
| :----- | :--- | :----- |
| !    | 不是 | 正确的 |
| &    | 和   | 剩下   |
| |    | 或者 | 剩下   |

如果你跨多个维度标记测试，标记表达式可帮助你选择要执行的测试。当按测试类型(例如micro、integration、 end-to-end)和功能(例如product、catalog、shipping)标记时，以下标记表达式可能很有用。

| 标记表达式                                     | 选择                                       |
| :--------------------------------------------- | :----------------------------------------- |
| product                                      | 产品的所有测试                         |
| catalog | shipping                           | 目录的所有测试加上运输的所有测试   |
| catalog & shipping                           | 目录和运输之间交集的所有测试       |
| product & !end-to-end                        | product的所有测试，但不是端到端测试  |
| (micro | integration) & (product | shipping) | 产品或运输的所有微观或集成测试 |

### 4.7 捕获标准输出/错误

System.out从 1.3 版开始，JUnit 平台提供了对捕获打印到和的输出的选择性支持System.err。要启用它，请将 junit.platform.output.capture.stdout和/或junit.platform.output.capture.stderr [配置参数](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params)设置为true。此外，你可以配置每个执行的测试或容器使用的最大缓冲字节数junit.platform.output.capture.maxBuffer。

如果启用，JUnit 平台会捕获相应的输出，并在报告测试或容器完成之前立即使用stdout或stderr键将 其作为报告条目发布到所有已注册的实例。TestExecutionListener

请注意，捕获的输出将仅包含用于执行容器或测试的线程发出的输出。其他线程的任何输出都将被忽略，因为特别是 [在并行执行测试](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parallel-execution)时，不可能将其归因于特定的测试或容器。

### 4.8 使用监听器

JUnit 平台提供以下侦听器 API，允许 JUnit、第三方和自定义用户代码对在发现和执行TestPlan.

-   LauncherSessionListenerLauncherSession: 在 a打开和关闭时接收事件。
-   LauncherDiscoveryListener：接收测试发现期间发生的事件。
-   TestExecutionListener：接收测试执行期间发生的事件。

API 通常由构建工具或 IDE 实现，LauncherSessionListener并自动为你注册，以支持构建工具或 IDE 的某些功能。

和API 通常用于生成某种形式的报告或在 IDE 中显示测试计划的图形表示LauncherDiscoveryListener。TestExecutionListener此类侦听器可能由构建工具或 IDE 实现并自动注册，或者它们可能包含在第三方库中——可能会自动为你注册。你还可以实施和注册自己的侦听器。

有关注册和配置侦听器的详细信息，请参阅本指南的以下部分。

-   [注册一个 LauncherSessionListener](https://junit.org/junit5/docs/current/user-guide/#launcher-api-launcher-session-listeners-custom)
-   [注册一个 LauncherDiscoveryListener](https://junit.org/junit5/docs/current/user-guide/#launcher-api-launcher-discovery-listeners-custom)
-   [注册一个 TestExecutionListener](https://junit.org/junit5/docs/current/user-guide/#launcher-api-listeners-custom)
-   [配置 TestExecutionListener](https://junit.org/junit5/docs/current/user-guide/#launcher-api-listeners-config)
-   [停用 TestExecutionListener](https://junit.org/junit5/docs/current/user-guide/#launcher-api-listeners-custom-deactivation)

JUnit 平台提供了以下监听器，你可能希望将它们与你的测试套件一起使用。

-   [JUnit 平台报告](https://junit.org/junit5/docs/current/user-guide/#junit-platform-reporting)

    LegacyXmlReportGeneratingListener可以通过 [控制台启动器](https://junit.org/junit5/docs/current/user-guide/#running-tests-console-launcher)使用或手动注册以生成与基于JUnit 4的测试报告的事实标准兼容的 XML 报告。OpenTestReportGeneratingListener[以Open Test Reporting](https://github.com/ota4j-team/open-test-reporting)指定的基于事件的格式生成 XML 报告。它是自动注册的，可以通过[配置参数](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params)启用和配置。有关详细信息，请参阅[JUnit 平台报告](https://junit.org/junit5/docs/current/user-guide/#junit-platform-reporting)。

-   [飞行记录器支持](https://junit.org/junit5/docs/current/user-guide/#running-tests-listeners-flight-recorder)

    FlightRecordingExecutionListener并且FlightRecordingDiscoveryListener在测试发现和执行期间生成 Java Flight Recorder 事件。

-   LoggingListener

    TestExecutionListener用于通过使用和的 a 记录所有事件的信息性 BiConsumer消息。ThrowableSupplier<String>

-   SummaryGeneratingListener

    TestExecutionListener生成测试执行的摘要，可以通过PrintWriter.

-   UniqueIdTrackingListener

    TestExecutionListener它跟踪在执行期间跳过或执行的所有测试的唯一 ID，并在执行完成后TestPlan生成一个包含唯一 ID 的文件TestPlan。

#### 4.8.1 飞行记录器支持

从 1.7 版开始，JUnit 平台提供了对生成 Flight Recorder 事件的选择性支持。[JEP 328](https://openjdk.java.net/jeps/328)将 Java 飞行记录器 (JFR) 描述为：

|      | Flight Recorder 记录来自应用程序、JVM 和操作系统的事件。事件存储在一个文件中，该文件可以附加到错误报告中并由支持工程师检查，从而允许在导致问题的时期内对问题进行事后分析。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

为了记录运行测试时生成的 Flight Recorder 事件，你需要：

1.  确保你使用的是Java 8Update 262 或更高版本或者 Java 11 或更高版本。

2.  在测试运行时在类路径或模块路径上提供org.junit.platform.jfr模块 ( )。junit-platform-jfr-1.9.1.jar

3.  启动试运行时开始飞行记录。Flight Recorder 可以通过 java 命令行选项启动：

    
    -XX:StartFlightRecording:文件名=...
    

请查阅构建工具的手册以获取适当的命令。

要分析记录的事件，请使用 最新JDK附带 的[jfr命令行工具或使用](https://docs.oracle.com/en/java/javase/14/docs/specs/man/jfr.html)[JDK Mission Control](https://jdk.java.net/jmc/)打开记录文件。

|      | 飞行记录器支持目前是一项实验性功能。我们邀请你尝试一下并向 JUnit 团队提供反馈，以便他们改进并最终 [推广](https://junit.org/junit5/docs/current/user-guide/#api-evolution)此功能。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

## 5. 扩展模型

### 5.1 概述

与JUnit 4中相互竞争Runner的TestRule、 和MethodRule扩展点相比，JUnit Jupiter扩展模型由一个单一的、连贯的概念组成： ExtensionAPI。但是请注意，它Extension本身只是一个标记界面。

### 5.2 注册扩展

扩展可以通过 以声明[@ExtendWith](https://junit.org/junit5/docs/current/user-guide/#extensions-registration-declarative)方式注册，通过 以编程方式 注册[@RegisterExtension](https://junit.org/junit5/docs/current/user-guide/#extensions-registration-programmatic)，或通过 Java机制自动注册。[ServiceLoader](https://junit.org/junit5/docs/current/user-guide/#extensions-registration-automatic)

#### 5.2.1 声明式扩展注册

开发人员可以通过注解测试接口、测试类、测试方法或自定义[组合注解](https://junit.org/junit5/docs/current/user-guide/#writing-tests-meta-annotations)并为要注册的扩展提供类引用，以声明方式注册一个或多个扩展。从JUnit Jupiter5.8 开始，也可以在测试类构造函数、测试方法以及 、 、 和生命周期方法中的字段或参数上 声明。@ExtendWith(…)@ExtendWith@BeforeAll@AfterAll@BeforeEach@AfterEach

例如，要WebServerExtension为特定的测试方法注册 a，你可以按如下方式注解测试方法。我们假设WebServerExtension启动了一个本地 Web 服务器并将服务器的 URL 注入到带有注解的参数中@WebServerUrl。

```java
@Test
@ExtendWith(WebServerExtension.class)
void getProductList(@WebServerUrl String serverUrl) {
    WebClient webClient = new WebClient();
    // Use WebClient to connect to web server using serverUrl and verify response
    assertEquals(200, webClient.get(serverUrl + "/products").getResponseStatus());
}
```

要WebServerExtension为特定类及其子类中的所有测试注册，你可以按如下方式注解测试类。

```java
@ExtendWith(WebServerExtension.class)
class MyTests {
    // ...
}
```

可以像这样一起注册多个扩展：

```java
@ExtendWith({ DatabaseExtension.class, WebServerExtension.class })
class MyFirstTests {
    // ...
}
```

作为替代方案，可以像这样分别注册多个扩展：

```java
@ExtendWith(DatabaseExtension.class)
@ExtendWith(WebServerExtension.class)
class MySecondTests {
    // ...
}
```

|      | 延期登记令通过在类级别、方法级别或参数级别以声明方式注册的扩展@ExtendWith将按照它们在源代码中声明的顺序执行。例如，在 和 中执行的测试将由MyFirstTestsandMySecondTests以DatabaseExtension完全相同的顺序进行扩展。WebServerExtension |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

如果你希望以可重用的方式组合多个扩展，你可以定义自定义 [组合注解](https://junit.org/junit5/docs/current/user-guide/#writing-tests-meta-annotations)并@ExtendWith用作 元注解，如以下代码清单所示。然后@DatabaseAndWebServerExtension 可以用来代替@ExtendWith({ DatabaseExtension.class, WebServerExtension.class }).

```java
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({ DatabaseExtension.class, WebServerExtension.class })
public @interface DatabaseAndWebServerExtension {
}
```

上面的示例演示了如何@ExtendWith在类级别或方法级别应用；但是，对于某些用例，在字段或参数级别以声明方式注册扩展是有意义的。考虑 RandomNumberExtension生成随机数的 a ，这些随机数可以注入到字段中或通过构造函数、测试方法或生命周期方法中的参数注入。如果扩展提供了@Random元注解的注解 @ExtendWith(RandomNumberExtension.class)(见下面的清单)，则可以透明地使用扩展，如以下RandomNumberDemo示例所示。

```java
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(RandomNumberExtension.class)
public @interface Random {
}
class RandomNumberDemo {

    // use random number field in test methods and @BeforeEach
    // or @AfterEach lifecycle methods
    @Random
    private int randomNumber1;
    
    RandomNumberDemo(@Random int randomNumber2) {
        // use random number in constructor
    }
    
    @BeforeEach
    void beforeEach(@Random int randomNumber3) {
        // use random number in @BeforeEach method
    }
    
    @Test
    void test(@Random int randomNumber4) {
        // use random number in test method
    }

}
```

|      | @ExtendWith在字段上的扩展注册订单通过字段以声明方式注册的扩展将使用确定性但故意不明显的算法@ExtendWith相对于@RegisterExtension字段和其他字段进行排序。@ExtendWith但是，可以使用注解@ExtendWith对字段进行排序。@Order有关详细信息，请参阅字段的[扩展注册订单](https://junit.org/junit5/docs/current/user-guide/#extensions-registration-programmatic-order)提示。@RegisterExtension |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

|      | @ExtendWith字段可以是静态的也可以static是非静态的。[有关字段的静态字段](https://junit.org/junit5/docs/current/user-guide/#extensions-registration-programmatic-static-fields)和 [实例字段](https://junit.org/junit5/docs/current/user-guide/#extensions-registration-programmatic-instance-fields)的 文档 @RegisterExtension也适用于@ExtendWith字段。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 5.2.2 程序化扩展注册

开发人员可以通过在测试类中使用注解字段以编程方式注册扩展@RegisterExtension。

当通过 以声明方式注册扩展时[@ExtendWith](https://junit.org/junit5/docs/current/user-guide/#extensions-registration-declarative)，它通常只能通过注解进行配置。相比之下，当通过 注册扩展时@RegisterExtension，可以通过编程方式对其进行配置 ——例如，为了将参数传递给扩展的构造函数、静态工厂方法或构建器 API。

|      | 延期登记令默认情况下，@RegisterExtension通过字段以编程方式或声明方式注册的扩展@ExtendWith将使用确定性但有意不明显的算法进行排序。这确保了测试套件的后续运行以相同的顺序执行扩展，从而允许可重复的构建。但是，有时需要以明确的顺序注册扩展。为此，请使用注解@RegisterExtension字段或@ExtendWith字段@Order。任何@RegisterExtension字段或未@ExtendWith注解的字段@Order将使用默认顺序进行排序，默认顺序的值为Integer.MAX_VALUE / 2。这允许@Order带注解的扩展字段在未带注解的扩展字段之前或之后明确排序。显式顺序值小于默认顺序值的扩展将在未注解的扩展之前注册。类似地，显式顺序值大于默认顺序值的扩展将在未注解的扩展之后注册。例如，为扩展分配一个大于默认顺序值的显式顺序值允许在最后和之后注册回调扩展回调扩展首先注册，相对于其他以编程方式注册的扩展。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

|      | @RegisterExtension字段不得null(在评估时)但可以是static静态的或非静态的。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

##### 静态字段

如果一个@RegisterExtension字段是static，扩展将在类级别注册的扩展之后注册@ExtendWith。此类静态扩展不限于它们可以实现的扩展 API。因此，通过静态字段注册的扩展可以实现类级别和实例级别的扩展 API，例如BeforeAllCallback、AfterAllCallback、 TestInstancePostProcessor和TestInstancePreDestroyCallback，以及方法级别的扩展 API，例如BeforeEachCallback等。

在以下示例中，server测试类中的字段使用WebServerExtension. 配置WebServerExtension将自动注册为类级别的扩展——例如，为了在类中的所有测试之前启动服务器，然后在类中的所有测试完成后停止服务器。此外，用@BeforeAllor注解的静态生命周期方法@AfterAll以及@BeforeEach, @AfterEach, 和方法可以在必要时@Test通过字段访问扩展的实例 。server

通过 Java 中的静态字段注册扩展

```java
class WebServerDemo {

    @RegisterExtension
    static WebServerExtension server = WebServerExtension.builder()
        .enableSecurity(false)
        .build();
    
    @Test
    void getProductList() {
        WebClient webClient = new WebClient();
        String serverUrl = server.getServerUrl();
        // Use WebClient to connect to web server using serverUrl and verify response
        assertEquals(200, webClient.get(serverUrl + "/products").getResponseStatus());
    }

}
```

###### Kotlin中的静态字段

Kotlin 编程语言没有static字段的概念。但是，可以指示编译器使用 Kotlinprivate static中的注解生成一个字段。@JvmStatic如果你想让 Kotlin 编译器生成一个public static字段，你可以使用@JvmField注解来代替。

以下示例是WebServerDemo上一节中已移植到 Kotlin 的版本。

通过 Kotlin 中的静态字段注册扩展

```java
class KotlinWebServerDemo {

    companion object {
        @JvmStatic
        @RegisterExtension
        val server = WebServerExtension.builder()
            .enableSecurity(false)
            .build()
    }
    
    @Test
    fun getProductList() {
        // Use WebClient to connect to web server using serverUrl and verify response
        val webClient = WebClient()
        val serverUrl = server.serverUrl
        assertEquals(200, webClient.get("$serverUrl/products").responseStatus)
    }
}
```

##### 实例字段

如果一个@RegisterExtension字段是非静态的(即一个实例字段)，扩展将在测试类被实例化后注册，并且在每个注册后 TestInstancePostProcessor都有机会对测试实例进行后处理(可能注入用于注解字段的扩展名)。因此，如果此类实例扩展实现了类级别或实例级别的扩展 API，例如BeforeAllCallback、AfterAllCallback或 TestInstancePostProcessor，那么这些 API 将不会被接受。默认情况下，实例扩展将在通过 ; 在方法级别注册的扩展之后@ExtendWith注册；但是，如果测试类配置了 @TestInstance(Lifecycle.PER_CLASS)语义，则实例扩展将在 之前注册通过 . 在方法级别注册的扩展@ExtendWith。

在以下示例中，docs测试类中的字段通过调用自定义lookUpDocsDir()方法并将结果提供forPath()给DocumentationExtension. 配置的 DocumentationExtension将自动注册为方法级别的扩展。此外，如果需要@BeforeEach，@AfterEach、 和方法可以通过字段@Test访问扩展的实例。docs

通过实例字段注册的扩展

```java
class DocumentationDemo {

    static Path lookUpDocsDir() {
        // return path to docs dir
    }
    
    @RegisterExtension
    DocumentationExtension docs = DocumentationExtension.forPath(lookUpDocsDir());
    
    @Test
    void generateDocumentation() {
        // use this.docs ...
    }
}
```

#### 5.2.3 自动扩展注册

除了[声明式扩展注册](https://junit.org/junit5/docs/current/user-guide/#extensions-registration-declarative) 和使用注解的[编程式扩展注册](https://junit.org/junit5/docs/current/user-guide/#extensions-registration-programmatic)支持之外，JUnit Jupiter还支持通过 Java 机制进行全局扩展注册ServiceLoader，允许根据类路径中可用的内容自动检测和自动注册第三方扩展。

具体来说，可以通过在其封闭 JAR 文件的文件夹org.junit.jupiter.api.extension.Extension内 命名的文件中提供其完全限定的类名来注册自定义扩展。/META-INF/services

##### 启用自动扩展检测

自动检测是一项高级功能，因此默认情况下不启用。要启用它，请将junit.jupiter.extensions.autodetection.enabled 配置参数设置为 true. 这可以作为 JVM 系统属性提供，作为传递给 的配置参数，或通过 JUnit 平台配置文件提供(有关详细信息，请参阅[配置参数](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params))。LauncherDiscoveryRequestLauncher

例如，要启用扩展的自动检测，你可以使用以下系统属性启动 JVM。


-Djunit.jupiter.extensions.autodetection.enabled=true


启用自动检测后，通过该ServiceLoader机制发现的扩展将添加到JUnit Jupiter的全局扩展(例如，支持 , 等)之后的扩展注册表TestInfo中TestReporter。

#### 5.2.4. 扩展继承

已注册的扩展在具有自上而下语义的测试类层次结构中继承。同样，在类级别注册的扩展在方法级别继承。此外，对于给定的扩展上下文及其父上下文，特定的扩展实现只能注册一次。因此，任何注册重复扩展实现的尝试都将被忽略。

### 5.3 条件测试执行

ExecutionCondition定义Extension用于程序化、条件测试执行的 API 。

为每个容器(例如，测试类)评估AnExecutionCondition以确定是否应根据提供的 执行它包含的所有测试 。类似地，为每个测试评估 an以确定是否应根据提供的 执行给定的测试方法 。ExtensionContextExecutionConditionExtensionContext

注册多个ExecutionCondition扩展时，只要其中一个条件返回disabled ，容器或测试就会被禁用。因此，不能保证条件被评估，因为另一个扩展可能已经导致容器或测试被禁用。换句话说，评估的工作方式类似于短路布尔 OR 运算符。

有关具体示例，请参见DisabledCondition和的源代码。@Disabled

#### 5.3.1 停用条件

有时在某些条件未激活的情况下运行测试套件可能很有用。例如，你可能希望运行测试，即使它们被注解@Disabled为以查看它们是否仍然是broken。为此，为 junit.jupiter.conditions.deactivate 配置参数提供一个模式，以指定哪些条件应该为当前测试运行停用(即，不评估)。该模式可以作为 JVM 系统属性提供，作为传递给 的配置参数提供 ，或者通过 JUnit 平台配置文件提供(有关详细信息，请参阅[配置参数](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params))。LauncherDiscoveryRequestLauncher

例如，要停用 JUnit 的@Disabled条件，你可以使用以下系统属性启动 JVM。


-Djunit.jupiter.conditions.deactivate=org.junit.DisabledCondition


##### 模式匹配语法

有关详细信息，请参阅[模式匹配语法](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params-deactivation-pattern)。

### 5.4 测试实例预构造回调

TestInstancePreConstructCallbackExtensions为希望 在构建测试实例之前调用的 API 定义 API (通过构造函数调用或通过 TestInstanceFactory)。

此扩展提供对其他扩展的对称调用，TestInstancePreDestroyCallback并可与其他扩展结合使用，以准备构造函数参数或跟踪测试实例及其生命周期。

### 5.5 测试实例工厂

TestInstanceFactory定义Extensions希望创建测试类实例的 API。

常见用例包括从依赖注入框架获取测试实例或调用静态工厂方法来创建测试类实例。

如果没有TestInstanceFactory注册，框架将调用测试类的唯一 构造函数来实例化它，可能通过注册的ParameterResolver扩展解析构造函数参数。

实现的扩展TestInstanceFactory可以在测试接口、顶级测试类或@Nested测试类上注册。

|      | 注册为任何单个类实现的多个扩展TestInstanceFactory将导致对该类、任何子类和任何嵌套类中的所有测试抛出异常。请注意，任何TestInstanceFactory在超类或封闭类中注册的(即，在@Nested测试类的情况下)都是继承的。用户有责任确保只TestInstanceFactory为任何特定的测试类注册一个。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

### 5.6 测试实例后处理

TestInstancePostProcessorExtensions为希望发布流程测试实例定义 API 。

常见用例包括将依赖项注入测试实例、在测试实例上调用自定义初始化方法等。

有关具体示例，请参阅 和 的源MockitoExtension代码 SpringExtension。

### 5.7 测试实例预销毁回调

TestInstancePreDestroyCallbackExtensions为那些希望在测试实例被用于测试之后和被销毁之前处理测试实例的 API 定义。

常见用例包括清除已注入测试实例的依赖项、在测试实例上调用自定义反初始化方法等。

### 5.8 参数解析

ParameterResolver定义了Extension在运行时动态解析参数的 API。

如果测试类构造函数、测试方法或生命周期方法(请参阅 [测试类和方法](https://junit.org/junit5/docs/current/user-guide/#writing-tests-classes-and-methods))声明了一个参数，则该参数必须 在运行时由ParameterResolver. AParameterResolver可以是内置的(参见 参考资料TestInfoParameterResolver)或[由用户注册](https://junit.org/junit5/docs/current/user-guide/#extensions-registration)。一般来说，参数可以通过name、type、 annotation或它们的任意组合来解析。

如果你希望实现ParameterResolver仅基于参数类型解析参数的自定义，你可能会发现扩展 TypeBasedParameterResolverwhich 作为此类用例的通用适配器很方便。

有关具体示例，请参阅 、 和 的CustomTypeParameterResolver源 CustomAnnotationParameterResolver代码MapOfListsTypeBasedParameterResolver。

|      | 由于JDK9 之前的JDK版本生成的字节码中存在一个错误，对于内部类构造函数(例如， 测试类中的构造函数) ，javac直接通过核心 API 查找参数注解java.lang.reflect.Parameter 总是失败的。@Nested因此，提供给实现的ParameterContextAPIParameterResolver包括以下用于正确查找参数注解的便捷方法。强烈建议扩展作者使用这些方法而不是提供的方法，java.lang.reflect.Parameter以避免JDK中的此错误。boolean isAnnotated(Class<? extends Annotation> annotationType)Optional<A> findAnnotation(Class<A> annotationType)List<A> findRepeatableAnnotations(Class<A> annotationType) |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

|      | 其他扩展也可以利用已注册ParameterResolvers的方法和构造函数调用，ExecutableInvoker使用 .getExecutableInvoker()ExtensionContext |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

### 5.9 测试结果处理

TestWatcher为希望处理测试方法执行结果的扩展定义 API。具体来说，TestWatcher将使用以下事件的上下文信息调用 a。

-   testDisabled：在跳过禁用的测试方法后调用
-   testSuccessful：在测试方法成功完成后调用
-   testAborted：在测试方法中止后调用
-   testFailed: 在测试方法失败后调用

|      | [与Test Classes and Methods](https://junit.org/junit5/docs/current/user-guide/#writing-tests-classes-and-methods)中 给出的“测试方法”的定义相反 ，在此上下文中，测试方法指的是任何 @Test方法或@TestTemplate方法(例如， a@RepeatedTest或 @ParameterizedTest)。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

实现此接口的扩展可以在方法级别或类级别注册。在后一种情况下，它们将被任何包含的测试方法调用， 包括那些在@Nested类中的测试方法。

|      | 在调用此 API 中的方法 之前，将关闭ExtensionContext.Store.CloseableResource存储在所Store提供的中的任何实例(请参阅[在扩展中保持状态](https://junit.org/junit5/docs/current/user-guide/#extensions-keeping-state))。你可以使用父上下文来处理此类资源。ExtensionContextStore |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

### 5.10 测试生命周期回调

以下接口定义了用于在测试执行生命周期的各个点扩展测试的 API。请参阅以下部分的示例和包中每个接口的 Javadoc 以org.junit.jupiter.api.extension获取更多详细信息。

-   BeforeAllCallback
    -   BeforeEachCallback
        -   BeforeTestExecutionCallback
        -   AfterTestExecutionCallback
    -   AfterEachCallback
-   AfterAllCallback

|      | 实现多个扩展 API扩展开发人员可以选择在单个扩展中实现任意数量的这些接口。具体示例请查阅 的源代码SpringExtension。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 5.10.1 测试执行回调之前和之后


BeforeTestExecutionCallback并为希望添加将分别在执行测试方法之前和 之后立即AfterTestExecutionCallback执行的行为定义 API 。因此，这些回调非常适合计时、跟踪和类似用例。如果你需要实现围绕和方法调用的回调，请改为实现 和。Extensions @BeforeEach@AfterEachBeforeEachCallbackAfterEachCallback


以下示例显示如何使用这些回调来计算和记录测试方法的执行时间。TimingExtension实现BeforeTestExecutionCallback 和AfterTestExecutionCallback为了计时和记录测试执行。

对测试方法的执行进行计时和记录的扩展

```java
import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

public class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final Logger logger = Logger.getLogger(TimingExtension.class.getName());
    
    private static final String START_TIME = "start time";
    
    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        getStore(context).put(START_TIME, System.currentTimeMillis());
    }
    
    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Method testMethod = context.getRequiredTestMethod();
        long startTime = getStore(context).remove(START_TIME, long.class);
        long duration = System.currentTimeMillis() - startTime;
    
        logger.info(() ->
            String.format("Method [%s] took %s ms.", testMethod.getName(), duration));
    }
    
    private Store getStore(ExtensionContext context) {
        return context.getStore(Namespace.create(getClass(), context.getRequiredTestMethod()));
    }
}
```

由于TimingExtensionTests该类注册了TimingExtensionvia @ExtendWith，因此其测试将在执行时应用此计时。

使用示例 TimingExtension 的测试类

```java
@ExtendWith(TimingExtension.class)
class TimingExtensionTests {

    @Test
    void sleep20ms() throws Exception {
        Thread.sleep(20);
    }
    
    @Test
    void sleep50ms() throws Exception {
        Thread.sleep(50);
    }
}
```

TimingExtensionTests以下是运行时生成的日志记录示例。


信息：方法 [sleep20ms] 花费了 24 毫秒。
信息：方法 [sleep50ms] 耗时 53 毫秒。


### 5.11 异常处理

在测试执行期间抛出的异常可能会在进一步传播之前被拦截并进行相应处理，因此可以在专门的Extensions.JUnit Jupiter提供了 API，用于Extensions希望处理在@Testvia 方法期间抛出的异常以及TestExecutionExceptionHandler 在测试生命周期方法( 、 和 )之一期间@BeforeAll抛出@BeforeEach的 异常。@AfterEach@AfterAllLifecycleMethodExecutionExceptionHandler

以下示例显示了一个扩展，它将吞下所有实例，IOException 但会重新抛出任何其他类型的异常。

在测试执行中过滤 IOExceptions 的异常处理扩展

```java
public class IgnoreIOExceptionExtension implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
            throws Throwable {
    
        if (throwable instanceof IOException) {
            return;
        }
        throw throwable;
    }
}
```

另一个示例显示了如何在设置和清理过程中抛出意外异常时准确记录被测应用程序的状态。请注意，与依赖生命周期回调不同，生命周期回调可能会或可能不会根据测试状态执行，此解决方案保证在失败后立即执行@BeforeAll、 或。@BeforeEach@AfterEach@AfterAll

记录错误时应用程序状态的异常处理扩展

```java
class RecordStateOnErrorExtension implements LifecycleMethodExecutionExceptionHandler {

    @Override
    public void handleBeforeAllMethodExecutionException(ExtensionContext context, Throwable ex)
            throws Throwable {
        memoryDumpForFurtherInvestigation("Failure recorded during class setup");
        throw ex;
    }
    
    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable ex)
            throws Throwable {
        memoryDumpForFurtherInvestigation("Failure recorded during test setup");
        throw ex;
    }
    
    @Override
    public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable ex)
            throws Throwable {
        memoryDumpForFurtherInvestigation("Failure recorded during test cleanup");
        throw ex;
    }
    
    @Override
    public void handleAfterAllMethodExecutionException(ExtensionContext context, Throwable ex)
            throws Throwable {
        memoryDumpForFurtherInvestigation("Failure recorded during class cleanup");
        throw ex;
    }
}
```

可以按照声明的顺序为同一个生命周期方法调用多个执行异常处理程序。如果其中一个处理程序吞下了已处理的异常，则不会执行后续处理程序，并且不会将任何故障传播到 JUnit 引擎，就好像从未抛出过异常一样。处理程序还可以选择重新抛出异常或抛出一个不同的异常，可能会包装原始异常。

扩展实现LifecycleMethodExecutionExceptionHandler希望处理期间抛出的异常@BeforeAll或@AfterAll需要在类级别上注册，而处理程序BeforeEach也AfterEach可以为单个测试方法注册。

注册多个异常处理扩展

```java
// Register handlers for @Test, @BeforeEach, @AfterEach as well as @BeforeAll and @AfterAll
@ExtendWith(ThirdExecutedHandler.class)
class MultipleHandlersTestCase {

    // Register handlers for @Test, @BeforeEach, @AfterEach only
    @ExtendWith(SecondExecutedHandler.class)
    @ExtendWith(FirstExecutedHandler.class)
    @Test
    void testMethod() {
    }

}
```

### 5.12 拦截调用

InvocationInterceptorExtensions为希望拦截对测试代码的调用定义 API 。

以下示例显示了一个扩展，它在 Swing 的事件调度线程中执行所有测试方法。

在用户定义的线程中执行测试的扩展

```java
public class SwingEdtInterceptor implements InvocationInterceptor {

    @Override
    public void interceptTestMethod(Invocation<Void> invocation,
            ReflectiveInvocationContext<Method> invocationContext,
            ExtensionContext extensionContext) throws Throwable {
    
        AtomicReference<Throwable> throwable = new AtomicReference<>();
    
        SwingUtilities.invokeAndWait(() -> {
            try {
                invocation.proceed();
            }
            catch (Throwable t) {
                throwable.set(t);
            }
        });
        Throwable t = throwable.get();
        if (t != null) {
            throw t;
        }
    }
}
```

### 5.13 为测试模板提供调用上下文

一种@TestTemplate方法只有在至少 TestTemplateInvocationContextProvider注册了一个方法时才能执行。每个这样的提供者负责提供一个Stream实例TestTemplateInvocationContext。每个上下文都可以指定一个自定义显示名称和一个仅用于下一次调用该@TestTemplate方法的附加扩展列表。

以下示例显示了如何编写测试模板以及如何注册和实现TestTemplateInvocationContextProvider.

带有扩展的测试模板

```java
final List<String> fruits = Arrays.asList("apple", "banana", "lemon");

@TestTemplate
@ExtendWith(MyTestTemplateInvocationContextProvider.class)
void testTemplate(String fruit) {
    assertTrue(fruits.contains(fruit));
}

public class MyTestTemplateInvocationContextProvider
        implements TestTemplateInvocationContextProvider {

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }
    
    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
            ExtensionContext context) {
    
        return Stream.of(invocationContext("apple"), invocationContext("banana"));
    }
    
    private TestTemplateInvocationContext invocationContext(String parameter) {
        return new TestTemplateInvocationContext() {
            @Override
            public String getDisplayName(int invocationIndex) {
                return parameter;
            }
    
            @Override
            public List<Extension> getAdditionalExtensions() {
                return Collections.singletonList(new ParameterResolver() {
                    @Override
                    public boolean supportsParameter(ParameterContext parameterContext,
                            ExtensionContext extensionContext) {
                        return parameterContext.getParameter().getType().equals(String.class);
                    }
    
                    @Override
                    public Object resolveParameter(ParameterContext parameterContext,
                            ExtensionContext extensionContext) {
                        return parameter;
                    }
                });
            }
        };
    }
}
```

在这个例子中，测试模板将被调用两次。apple调用的显示名称将由banana调用上下文指定。每次调用都会注册一个ParameterResolver用于解析方法参数的自定义。使用时的输出ConsoleLauncher如下。


└─ 测试模板(字符串) ✔
   ├─ 苹果✔
   └─ 香蕉✔


扩展 API 主要用于实现不同类型的测试，这些TestTemplateInvocationContextProvider测试依赖于重复调用类似测试的方法，尽管在不同的上下文中——例如，使用不同的参数，通过以不同方式准备测试类实例，或者在不修改上下文的情况下多次调用. 请参考使用此扩展点提供其功能的[重复测试](https://junit.org/junit5/docs/current/user-guide/#writing-tests-repeated-tests)或 [参数化测试的实现。](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests)

### 5.14 在扩展中保持状态

通常，一个扩展只会被实例化一次。所以问题变得相关：如何保持状态从一次扩展调用到下一次调用？ExtensionContextAPIStore正是为此目的提供的 。扩展可以将值放入存储中以供以后检索。有关将 与方法级范围一起TimingExtension使用的示例，请参见 。Store重要的是要记住，ExtensionContext在测试执行期间存储的值在周围将不可用ExtensionContext。由于ExtensionContexts可能是嵌套的，内部上下文的范围也可能是有限的。有关可用于通过Store.

|      | ExtensionContext.Store.CloseableResource扩展上下文存储绑定到它的扩展上下文生命周期。当扩展上下文生命周期结束时，它会关闭其关联的存储。作为其实例的所有存储值都通过以与添加它们的顺序相反的顺序CloseableResource调用它们的close() 方法来通知。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

### 5.15 扩展中支持的实用程序

该junit-platform-commons工件公开了一个名为的包 org.junit.platform.commons.support，其中包含用于处理注解、类、反射和类路径扫描任务的维护实用程序方法。鼓励作者使用这些受支持的方法，以便与 JUnit 平台的行为保持一致TestEngine。 Extension

#### 5.15.1 注解支持

AnnotationSupport提供对带注解的元素(例如，包、注解、类、接口、构造函数、方法和字段)进行操作的静态实用方法。这些包括检查元素是否使用特定注解进行注解或元注解、搜索特定注解以及在类或接口中查找带注解的方法和字段的方法。其中一些方法在实现的接口和类层次结构中搜索以查找注解。有关详细信息，请查阅 Javadoc AnnotationSupport。

#### 5.15.2 课堂支持

ClassSupport提供用于处理类(即 的实例java.lang.Class)的静态实用程序方法。有关详细信息，请查阅 Javadoc ClassSupport。

#### 5.15.3 反思支持

ReflectionSupport提供增强标准JDK反射和类加载机制的静态实用方法。这些包括扫描类路径以搜索与指定谓词匹配的类、加载和创建类的新实例以及查找和调用方法的方法。其中一些方法遍历类层次结构以找到匹配的方法。有关详细信息，请查阅 Javadoc ReflectionSupport。

#### 5.15.4 修改器支持

ModifierSupport提供用于使用成员和类修饰符的静态实用方法——例如，确定成员是否声明为public, private, abstract,static等。有关详细信息，请参阅 Javadoc ModifierSupport。

### 5.16 用户代码和扩展的相对执行顺序

当执行包含一个或多个测试方法的测试类时，除了用户提供的测试和生命周期方法之外，还会调用许多扩展回调。

|      | 另请参阅：[测试执行顺序](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-execution-order) |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 5.16.1 用户和分机代码

下图说明了用户提供的代码和扩展代码的相对顺序。用户提供的测试和生命周期方法以橙色显示，由扩展实现的回调代码以蓝色显示。灰色框表示执行单个测试方法，并将对测试类中的每个测试方法重复执行。

![扩展生命周期](https://junit.org/junit5/docs/current/user-guide/images/extensions_lifecycle.png)

用户代码和分机代码

下表进一步解释了 [用户代码和扩展代码](https://junit.org/junit5/docs/current/user-guide/#extensions-execution-order-diagram)图中的十六个步骤。

| 步   | 接口/注解                                                    | 描述                                               |
| :--- | :----------------------------------------------------------- | :------------------------------------------------- |
| 1    | 界面org.junit.jupiter.api.extension.BeforeAllCallback      | 在执行容器的所有测试之前执行的扩展代码             |
| 2    | 注解org.junit.jupiter.api.BeforeAll                        | 在执行容器的所有测试之前执行的用户代码             |
| 3    | 界面org.junit.jupiter.api.extension.LifecycleMethodExecutionExceptionHandler #handleBeforeAllMethodExecutionException | 用于处理从@BeforeAll方法抛出的异常的扩展代码     |
| 4    | 界面org.junit.jupiter.api.extension.BeforeEachCallback     | 在执行每个测试之前执行的扩展代码                   |
| 5    | 注解org.junit.jupiter.api.BeforeEach                       | 在执行每个测试之前执行的用户代码                   |
| 6    | 界面org.junit.jupiter.api.extension.LifecycleMethodExecutionExceptionHandler #handleBeforeEachMethodExecutionException | 用于处理从@BeforeEach方法抛出的异常的扩展代码    |
| 7    | 界面org.junit.jupiter.api.extension.BeforeTestExecutionCallback | 在执行测试之前立即执行的扩展代码                   |
| 8    | 注解org.junit.jupiter.api.Test                             | 实际测试方法的用户代码                             |
| 9    | 界面org.junit.jupiter.api.extension.TestExecutionExceptionHandler | 用于处理测试期间抛出的异常的扩展代码               |
| 10   | 界面org.junit.jupiter.api.extension.AfterTestExecutionCallback | 测试执行后立即执行的扩展代码及其相应的异常处理程序 |
| 11   | 注解org.junit.jupiter.api.AfterEach                        | 执行每个测试后执行的用户代码                       |
| 12   | 界面org.junit.jupiter.api.extension.LifecycleMethodExecutionExceptionHandler #handleAfterEachMethodExecutionException | 用于处理从@AfterEach方法抛出的异常的扩展代码     |
| 13   | 界面org.junit.jupiter.api.extension.AfterEachCallback      | 执行每个测试后执行的扩展代码                       |
| 14   | 注解org.junit.jupiter.api.AfterAll                         | 执行容器的所有测试后执行的用户代码                 |
| 15   | 界面org.junit.jupiter.api.extension.LifecycleMethodExecutionExceptionHandler #handleAfterAllMethodExecutionException | 用于处理从@AfterAll方法抛出的异常的扩展代码      |
| 16   | 界面org.junit.jupiter.api.extension.AfterAllCallback       | 执行完容器的所有测试后执行的扩展代码               |

在最简单的情况下，只会执行实际的测试方法(步骤 8)；所有其他步骤都是可选的，具体取决于用户代码的存在或对相应生命周期回调的扩展支持。有关各种生命周期回调的更多详细信息，请参阅每个注解和扩展的相应 Javadoc。

上表中用户代码方法的所有调用都可以通过实现[InvocationInterceptor](https://junit.org/junit5/docs/current/user-guide/#extensions-intercepting-invocations).

#### 5.16.2 回调的包装行为

JUnit Jupiter始终保证实现生命周期回调的多个注册扩展的包装行为，例如BeforeAllCallback、AfterAllCallback、 BeforeEachCallback、AfterEachCallback、BeforeTestExecutionCallback和 AfterTestExecutionCallback。

这意味着，给定两个扩展Extension1并Extension2在Extension1 之前注册Extension2，由 实现的任何“之前”回调Extension1都保证在实现的任何“之前”回调之前执行Extension2。类似地，给定以相同顺序注册的两个相同的两个扩展，由 实现的任何“之后”回调Extension1都保证在实现的任何“之后”回调之后执行Extension2。Extension1因此被称为wrap Extension2。

JUnit Jupiter还保证了用户提供的生命周期方法的类和接口层次结构中的包装行为(请参阅[测试类和方法](https://junit.org/junit5/docs/current/user-guide/#writing-tests-classes-and-methods))。

-   @BeforeAll只要方法未被隐藏、 覆盖或取代(即，仅根据签名进行替换，不考虑 Java 的可见性规则)，方法就会从超类继承。此外，超类中的方法将在子类中的方法之前@BeforeAll执行。 @BeforeAll
    -   类似地，@BeforeAll在接口中声明的方法只要未被隐藏或覆盖，就会被继承，并且@BeforeAll来自接口的方法将在实现该接口的类中的方法之前执行。 @BeforeAll
-   @AfterAll只要方法未被隐藏、 覆盖或取代(即，仅根据签名进行替换，不考虑 Java 的可见性规则)，方法就会从超类继承。此外，超类中的方法将在子类中的方法之后@AfterAll执行。 @AfterAll
    -   同样，@AfterAll在接口中声明的方法只要不被隐藏或覆盖，就会被继承，接口中的@AfterAll方法将在实现该接口的类中的方法之后执行。 @AfterAll
-   @BeforeEach只要方法不 被覆盖或取代(即，仅根据签名进行替换，不考虑 Java 的可见性规则)，方法就会从超类继承。此外，超类中的方法将在子类中的方法之前@BeforeEach执行。 @BeforeEach
    -   同样，@BeforeEach声明为接口默认方法的方法只要不被重写，就会被继承，@BeforeEach默认方法会 先 @BeforeEach于实现该接口的类中的方法执行。
-   @AfterEach只要方法不 被覆盖或取代(即，仅根据签名进行替换，不考虑 Java 的可见性规则)，方法就会从超类继承。此外，超类中的方法将在子类中的方法之后@AfterEach执行。 @AfterEach
    -   同样@AfterEach声明为接口默认方法的方法只要不被重写就会被继承，@AfterEach默认方法会 在实现该接口的类中的方法之后执行。 @AfterEach

以下示例演示了此行为。请注意，这些示例实际上并没有做任何实际的事情。相反，它们模仿常见场景来测试与数据库的交互。从Logger类日志上下文信息静态导入的所有方法，以帮助我们更好地理解用户提供的回调方法和扩展中的回调方法的执行顺序。

分机1

```java
import static example.callbacks.Logger.afterEachCallback;
import static example.callbacks.Logger.beforeEachCallback;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class Extension1 implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        beforeEachCallback(this);
    }
    
    @Override
    public void afterEach(ExtensionContext context) {
        afterEachCallback(this);
    }

}
```

分机2

```java
import static example.callbacks.Logger.afterEachCallback;
import static example.callbacks.Logger.beforeEachCallback;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class Extension2 implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        beforeEachCallback(this);
    }
    
    @Override
    public void afterEach(ExtensionContext context) {
        afterEachCallback(this);
    }

}
```

抽象数据库测试

```java
import static example.callbacks.Logger.afterAllMethod;
import static example.callbacks.Logger.afterEachMethod;
import static example.callbacks.Logger.beforeAllMethod;
import static example.callbacks.Logger.beforeEachMethod;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/
  Abstract base class for tests that use the database.
 /
abstract class AbstractDatabaseTests {

    @BeforeAll
    static void createDatabase() {
        beforeAllMethod(AbstractDatabaseTests.class.getSimpleName() + ".createDatabase()");
    }
    
    @BeforeEach
    void connectToDatabase() {
        beforeEachMethod(AbstractDatabaseTests.class.getSimpleName() + ".connectToDatabase()");
    }
    
    @AfterEach
    void disconnectFromDatabase() {
        afterEachMethod(AbstractDatabaseTests.class.getSimpleName() + ".disconnectFromDatabase()");
    }
    
    @AfterAll
    static void destroyDatabase() {
        afterAllMethod(AbstractDatabaseTests.class.getSimpleName() + ".destroyDatabase()");
    }

}
```

数据库测试演示

```java
import static example.callbacks.Logger.afterEachMethod;
import static example.callbacks.Logger.beforeAllMethod;
import static example.callbacks.Logger.beforeEachMethod;
import static example.callbacks.Logger.testMethod;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/
  Extension of {@link AbstractDatabaseTests} that inserts test data
  into the database (after the database connection has been opened)
  and deletes test data (before the database connection is closed).
 /
@ExtendWith({ Extension1.class, Extension2.class })
class DatabaseTestsDemo extends AbstractDatabaseTests {

    @BeforeAll
    static void beforeAll() {
        beforeAllMethod(DatabaseTestsDemo.class.getSimpleName() + ".beforeAll()");
    }
    
    @BeforeEach
    void insertTestDataIntoDatabase() {
        beforeEachMethod(getClass().getSimpleName() + ".insertTestDataIntoDatabase()");
    }
    
    @Test
    void testDatabaseFunctionality() {
        testMethod(getClass().getSimpleName() + ".testDatabaseFunctionality()");
    }
    
    @AfterEach
    void deleteTestDataFromDatabase() {
        afterEachMethod(getClass().getSimpleName() + ".deleteTestDataFromDatabase()");
    }
    
    @AfterAll
    static void afterAll() {
        beforeAllMethod(DatabaseTestsDemo.class.getSimpleName() + ".afterAll()");
    }

}
```

执行DatabaseTestsDemo测试类时，会记录以下内容。

```java
@BeforeAll AbstractDatabaseTests.createDatabase()
@BeforeAll DatabaseTestsDemo.beforeAll()
  Extension1.beforeEach()
  Extension2.beforeEach()
    @BeforeEach AbstractDatabaseTests.connectToDatabase()
    @BeforeEach DatabaseTestsDemo.insertTestDataIntoDatabase()
      @测试数据库TestsDemo.testDatabaseFunctionality()
    @AfterEach DatabaseTestsDemo.deleteTestDataFromDatabase()
    @AfterEach AbstractDatabaseTests.disconnectFromDatabase()
  Extension2.afterEach()
  Extension1.afterEach()
@BeforeAll DatabaseTestsDemo.afterAll()
@AfterAll AbstractDatabaseTests.destroyDatabase()
```

以下序列图有助于进一步阐明执行测试类JupiterTestEngine时实际发生的情况。DatabaseTestsDemo

![扩展数据库测试演示](https://junit.org/junit5/docs/current/user-guide/images/extensions_DatabaseTestsDemo.png)

数据库测试演示

JUnit Jupiter不保证在单个测试类或测试接口中声明的多个生命周期方法的执行顺序。有时，JUnit Jupiter可能会按字母顺序调用此类方法。然而，事实并非如此。排序类似于@Test单个测试类中方法的排序。

|      | 在单个测试类或测试接口中声明的生命周期方法将使用确定性但故意不明显的算法进行排序。这确保了测试套件的后续运行以相同的顺序执行生命周期方法，从而允许可重复的构建。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

此外，JUnit Jupiter不支持在单个测试类或测试接口中声明的多个生命周期方法的包装行为。

以下示例演示了此行为。具体来说，由于本地声明的生命周期方法的执行顺序，生命周期方法配置被破坏。

-   数据库连接还没有打开就插入了测试数据，导致连接不上数据库。
-   删除测试数据前关闭数据库连接，导致无法连接数据库。

BrokenLifecycleMethodConfigDemo

```java
import static example.callbacks.Logger.afterEachMethod;
import static example.callbacks.Logger.beforeEachMethod;
import static example.callbacks.Logger.testMethod;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/
  Example of "broken" lifecycle method configuration.

  <p>Test data is inserted before the database connection has been opened.

  <p>Database connection is closed before deleting test data.
 /
@ExtendWith({ Extension1.class, Extension2.class })
class BrokenLifecycleMethodConfigDemo {

    @BeforeEach
    void connectToDatabase() {
        beforeEachMethod(getClass().getSimpleName() + ".connectToDatabase()");
    }
    
    @BeforeEach
    void insertTestDataIntoDatabase() {
        beforeEachMethod(getClass().getSimpleName() + ".insertTestDataIntoDatabase()");
    }
    
    @Test
    void testDatabaseFunctionality() {
        testMethod(getClass().getSimpleName() + ".testDatabaseFunctionality()");
    }
    
    @AfterEach
    void deleteTestDataFromDatabase() {
        afterEachMethod(getClass().getSimpleName() + ".deleteTestDataFromDatabase()");
    }
    
    @AfterEach
    void disconnectFromDatabase() {
        afterEachMethod(getClass().getSimpleName() + ".disconnectFromDatabase()");
    }
}
```

执行BrokenLifecycleMethodConfigDemo测试类时，会记录以下内容。

```java
Extension1.beforeEach()
Extension2.beforeEach()
  @BeforeEach BrokenLifecycleMethodConfigDemo.insertTestDataIntoDatabase()
  @BeforeEach BrokenLifecycleMethodConfigDemo.connectToDatabase()
    @Test BrokenLifecycleMethodConfigDemo.testDatabaseFunctionality()
  @AfterEach BrokenLifecycleMethodConfigDemo.disconnectFromDatabase()
  @AfterEach BrokenLifecycleMethodConfigDemo.deleteTestDataFromDatabase()
Extension2.afterEach()
Extension1.afterEach()
```

以下序列图有助于进一步阐明执行测试类JupiterTestEngine时实际发生的情况。BrokenLifecycleMethodConfigDemo

![扩展 BrokenLifecycleMethodConfigDemo](https://junit.org/junit5/docs/current/user-guide/images/extensions_BrokenLifecycleMethodConfigDemo.png)

BrokenLifecycleMethodConfigDemo

|      | 由于上述行为，JUnit 团队建议开发人员为每个测试类或测试接口最多声明每种类型的生命周期方法(请参阅[测试类和方法](https://junit.org/junit5/docs/current/user-guide/#writing-tests-classes-and-methods))，除非此类生命周期方法之间不存在依赖关系。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

## 6. 高级话题

### 6.1 JUnit平台报告

该junit-platform-reporting工件包含TestExecutionListener以两种方式生成 XML 测试报告的实现： [遗留](https://junit.org/junit5/docs/current/user-guide/#junit-platform-reporting-legacy-xml)和 [开放测试报告](https://junit.org/junit5/docs/current/user-guide/#junit-platform-reporting-open-test-reporting)。

|      | 该模块还包含TestExecutionListener可用于构建自定义报告的其他实现。有关详细信息，请参阅[使用侦听器](https://junit.org/junit5/docs/current/user-guide/#running-tests-listeners)。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 6.1.1 遗留XML格式

LegacyXmlReportGeneratingListener为 中的每个根生成一个单独的 XML 报告 TestPlan。请注意，生成的 XML 格式与 Ant 构建系统流行的基于JUnit 4的测试报告的事实标准兼容。

LegacyXmlReportGeneratingListener也被控制台[启动器](https://junit.org/junit5/docs/current/user-guide/#running-tests-console-launcher)使用 。

#### 6.1.2 打开测试报告XML格式

OpenTestReportGeneratingListener[以Open Test Reporting](https://github.com/ota4j-team/open-test-reporting)指定的基于事件的格式为整个执行编写 XML 报告，该格式支持 JUnit 平台的所有功能，例如分层测试结构、显示名称、标签等。

侦听器是自动注册的，可以通过以下 [配置参数](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params)进行配置：

-   junit.platform.reporting.open.xml.enabled=true|false

    启用/禁用编写报告。

-   junit.platform.reporting.output.dir=<path>

    配置报告的输出目录。默认情况下，build如果找到 Gradle 构建脚本，并且target如果找到 Maven POM，则使用；否则，使用当前工作目录。

如果启用，侦听器会在配置的输出目录中创建一个名为 junit-platform-events-<random-id>.xmlper test run 的 XML 报告文件。

|      | [Open Test Reporting CLI Tool](https://github.com/ota4j-team/open-test-reporting#cli-tool-for-validation-and-format-conversion)可用于将基于事件的格式转换为更易于阅读的分层格式 。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

##### 摇篮

对于 Gradle，编写 Open Test Reporting 兼容的 XML 报告可以通过系统属性启用和配置。以下示例将其输出目录配置为 Gradle 用于其自己的 XML 报告的同一目录。ACommandLineArgumentProvider 用于保持任务可跨不同机器重新定位，这在使用 Gradle 的构建缓存时很重要。

Groovy DSL

```groovy
dependencies {
    testRuntimeOnly("org.junit.platform:junit-platform-reporting:1.9.1")
}
tasks.withType(Test).configureEach {
    def outputDir = reports.junitXml.outputLocation
    jvmArgumentProviders << ({
        [
            "-Djunit.platform.reporting.open.xml.enabled=true",
            "-Djunit.platform.reporting.output.dir=${outputDir.get().asFile.absolutePath}"
        ]
    } as CommandLineArgumentProvider)
}
```

科特林 DSL

```groovy
dependencies {
    testRuntimeOnly("org.junit.platform:junit-platform-reporting:1.9.1")
}
tasks.withType<Test>().configureEach {
    val outputDir = reports.junitXml.outputLocation
    jvmArgumentProviders += CommandLineArgumentProvider {
        listOf(
            "-Djunit.platform.reporting.open.xml.enabled=true",
            "-Djunit.platform.reporting.output.dir=${outputDir.get().asFile.absolutePath}"
        )
    }
}
```

##### 行家

对于 Maven Surefire/Failsafe，你可以启用开放测试报告输出并将生成的 XML 文件配置为写入 Surefire/Failsafe 用于其自己的 XML 报告的同一目录，如下所示：

```xml
<project>
    <!-- ... -->
    <dependencies>
        <dependency>
            <groupId>org.junit.platform</groupId>
            <artifactId>junit-platform-reporting</artifactId>
            <version>1.9.1</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>2.22.2</version>
                <configuration>
                    <properties>
                        <configurationParameters>
                            junit.platform.reporting.open.xml.enabled = true
                            junit.platform.reporting.output.dir = target/surefire-reports
                        </configurationParameters>
                    </properties>
                </configuration>
            </plugin>
        </plugins>
    </build>
    <!-- ... -->
</project>
```

##### 控制台启动器

使用[Console Launcher](https://junit.org/junit5/docs/current/user-guide/#running-tests-console-launcher)时，你可以通过以下方式设置配置参数来启用 Open Test Reporting 输出--config：


$ java -jar junit-platform-console-standalone-1.9.1.jar <OPTIONS> 
  --config=junit.platform.reporting.open.xml.enabled=true 
  --config=junit.platform.reporting.output.dir=reports


### 6.2 JUnit平台套件引擎

JUnit 平台支持从任何使用 JUnit 平台的测试引擎声明式定义和执行测试套件。

#### 6.2.1 设置

除了junit-platform-suite-api和junit-platform-suite-engine工件之外，你还需要至少一个其他测试引擎及其在类路径上的依赖项。有关组 ID、工件 ID 和版本的详细信息，请参阅 [依赖项元数据。](https://junit.org/junit5/docs/current/user-guide/#dependency-metadata)

##### 必需的依赖项

-   junit-platform-suite-api在测试范围内：包含配置测试套件所需的注解的工件
-   junit-platform-suite-engine在测试运行时范围内： TestEngine声明性测试套件的 API 实现

|      | 这两个必需的依赖项都聚集在junit-platform-suite 可以在测试junit-platform-suite-api范围内声明的工件中，而不是声明对和的显式依赖项junit-platform-suite-engine。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

##### 传递依赖

-   junit-platform-suite-commons在测试范围内
-   junit-platform-launcher在测试范围内
-   junit-platform-engine在测试范围内
-   junit-platform-commons在测试范围内
-   opentest4j在测试范围内

#### 6.2.2 @Suite 例子

通过用它注解一个类，@Suite它被标记为 JUnit 平台上的测试套件。如以下示例所示，然后可以使用选择器和过滤器注解来控制套件的内容。

```java
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("JUnit Platform Suite Demo")
@SelectPackages("example")
@IncludeClassNamePatterns(".Tests")
class SuiteDemo {
}
```

|      | 其他配置选项有许多配置选项可用于在测试套件中发现和过滤测试。请查阅org.junit.platform.suite.api包的 Javadoc 以获取支持的注解的完整列表和更多详细信息。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

### 6.3 JUnit 平台测试套件

该junit-platform-testkit工件支持在 JUnit 平台上执行测试计划，然后验证预期结果。从 JUnit Platform 1.4 开始，这种支持仅限于执行单个TestEngine(请参阅[Engine Test Kit](https://junit.org/junit5/docs/current/user-guide/#testkit-engine))。

#### 6.3.1 发动机测试套件

该org.junit.platform.testkit.engine包支持在 JUnit 平台上执行TestPlan给定的TestEngine运行，然后通过流畅的 API 访问结果以验证预期结果。此 API 的关键入口点是 EngineTestKit提供名为engine()and的静态工厂方法execute()。建议你选择其中一种engine()变体，以便从用于构建LauncherDiscoveryRequest.

|      | 如果你更喜欢使用LauncherDiscoveryRequestBuilderAPILauncher来构建你的LauncherDiscoveryRequest，则必须execute()使用 EngineTestKit. |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

以下使用JUnit Jupiter编写的测试类将在后续示例中使用。

```java
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import example.util.Calculator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
public class ExampleTestCase {

    private final Calculator calculator = new Calculator();
    
    @Test
    @Disabled("for demonstration purposes")
    @Order(1)
    void skippedTest() {
        // skipped ...
    }
    
    @Test
    @Order(2)
    void succeedingTest() {
        assertEquals(42, calculator.multiply(6, 7));
    }
    
    @Test
    @Order(3)
    void abortedTest() {
        assumeTrue("abc".contains("Z"), "abc does not contain Z");
        // aborted ...
    }
    
    @Test
    @Order(4)
    void failingTest() {
        // The following throws an ArithmeticException: "/ by zero"
        calculator.divide(1, 0);
    }

}
```

为简洁起见，以下部分将演示如何测试 JUnit 自身 JupiterTestEngine的唯一引擎 ID 为"junit-jupiter". 如果你想测试你自己的TestEngine实现，你需要使用它唯一的引擎 ID。或者，你可以通过向静态工厂方法TestEngine提供实例来测试你自己的 方法。EngineTestKit.engine(TestEngine)

#### 6.3.2 断言统计

测试套件最常见的功能之一是能够针对在执行TestPlan. 以下测试演示了如何在JUnit Jupiter中断言容器和测试TestEngine的统计信息。有关哪些统计信息可用的详细信息，请参阅 Javadoc EventStatistics。

```java
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import example.ExampleTestCase;

import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

class EngineTestKitStatisticsDemo {

    @Test
    void verifyJupiterContainerStats() {
        EngineTestKit
            .engine("junit-jupiter") 
            .selectors(selectClass(ExampleTestCase.class)) 
            .execute() 
            .containerEvents() 
            .assertStatistics(stats -> stats.started(2).succeeded(2)); 
    }
    
    @Test
    void verifyJupiterTestStats() {
        EngineTestKit
            .engine("junit-jupiter") 
            .selectors(selectClass(ExampleTestCase.class)) 
            .execute() 
            .testEvents() 
            .assertStatistics(stats ->
                stats.skipped(1).started(3).succeeded(1).aborted(1).failed(1)); 
    }

}
```

|      | 选择 JUnit 木星TestEngine。                                |
| ---- | ------------------------------------------------------------ |
|      | 选择[ExampleTestCase](https://junit.org/junit5/docs/current/user-guide/#testkit-engine-ExampleTestCase)测试类。 |
|      | 执行TestPlan.                                              |
|      | 按容器事件过滤。                                           |
|      | 断言容器事件的统计信息。                                   |
|      | 按测试事件过滤。                                           |
|      | 断言测试事件的统计数据。                                   |

|      | 在verifyJupiterContainerStats()测试方法中，started和 succeeded统计信息的计数是2因为JupiterTestEngine和 [ExampleTestCase](https://junit.org/junit5/docs/current/user-guide/#testkit-engine-ExampleTestCase)类都被视为容器。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 6.3.3 断言事件

如果你发现仅[断言统计信息](https://junit.org/junit5/docs/current/user-guide/#testkit-engine-statistics)不足以验证测试执行的预期行为，你可以直接使用记录的Event元素并对它们执行断言。

例如，如果要验证跳过skippedTest()方法中 的原因，[ExampleTestCase](https://junit.org/junit5/docs/current/user-guide/#testkit-engine-ExampleTestCase)可以按如下方式进行。

|      | assertThatEvents()以下示例中的方法是 AssertJorg.assertj.core.api.Assertions.assertThat(events.list())断言[库](https://joel-costigliola.github.io/assertj/)的快捷方式。有关哪些条件可用于针对事件的 AssertJ 断言的详细信息，请参阅 Javadoc EventConditions。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

```java
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;
import static org.junit.platform.testkit.engine.EventConditions.event;
import static org.junit.platform.testkit.engine.EventConditions.skippedWithReason;
import static org.junit.platform.testkit.engine.EventConditions.test;

import example.ExampleTestCase;

import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;
import org.junit.platform.testkit.engine.Events;

class EngineTestKitSkippedMethodDemo {

    @Test
    void verifyJupiterMethodWasSkipped() {
        String methodName = "skippedTest";
    
        Events testEvents = EngineTestKit 
            .engine("junit-jupiter") 
            .selectors(selectMethod(ExampleTestCase.class, methodName)) 
            .execute() 
            .testEvents(); 
    
        testEvents.assertStatistics(stats -> stats.skipped(1)); 
    
        testEvents.assertThatEvents() 
            .haveExactly(1, event(test(methodName),
                skippedWithReason("for demonstration purposes")));
    }

}
```

|      | 选择 JUnit 木星TestEngine。                                |
| ---- | ------------------------------------------------------------ |
|      | 选择测试类skippedTest()中的方法。[ExampleTestCase](https://junit.org/junit5/docs/current/user-guide/#testkit-engine-ExampleTestCase) |
|      | 执行TestPlan.                                              |
|      | 按测试事件过滤。                                           |
|      | 将测试 保存Events到局部变量。                            |
|      | 可选择断言预期的统计数据。                                   |
|      | 断言记录的测试skippedTest事件恰好包含一个名为with"for demonstration purposes"作为原因的跳过测试 。 |

如果要验证从中的failingTest()方法 抛出的异常类型[ExampleTestCase](https://junit.org/junit5/docs/current/user-guide/#testkit-engine-ExampleTestCase)，可以按如下方式进行。

|      | 有关哪些条件可用于针对事件和执行结果的 AssertJ 断言的详细信息，请分别查阅 和 的EventConditionsJavadoc TestExecutionResultConditions。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

```java
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.testkit.engine.EventConditions.event;
import static org.junit.platform.testkit.engine.EventConditions.finishedWithFailure;
import static org.junit.platform.testkit.engine.EventConditions.test;
import static org.junit.platform.testkit.engine.TestExecutionResultConditions.instanceOf;
import static org.junit.platform.testkit.engine.TestExecutionResultConditions.message;

import example.ExampleTestCase;

import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

class EngineTestKitFailedMethodDemo {

    @Test
    void verifyJupiterMethodFailed() {
        EngineTestKit.engine("junit-jupiter") 
            .selectors(selectClass(ExampleTestCase.class)) 
            .execute() 
            .testEvents() 
            .assertThatEvents().haveExactly(1, 
                event(test("failingTest"),
                    finishedWithFailure(
                        instanceOf(ArithmeticException.class), message("/ by zero"))));
    }

}
```

|      | 选择 JUnit 木星TestEngine。                                |
| ---- | ------------------------------------------------------------ |
|      | 选择[ExampleTestCase](https://junit.org/junit5/docs/current/user-guide/#testkit-engine-ExampleTestCase)测试类。 |
|      | 执行TestPlan.                                              |
|      | 按测试事件过滤。                                           |
|      | 断言记录的测试failingTest事件恰好包含一个名为exception 类型的失败测试 ArithmeticException和等于 的错误消息"/ by zero"。 |

虽然通常没有必要，但有时你需要验证在执行TestPlan. 下面的测试演示了如何通过APIassertEventsMatchExactly()中的方法实现这一点。EngineTestKit

|      | 由于assertEventsMatchExactly()匹配条件完全按照事件触发的顺序进行，[ExampleTestCase](https://junit.org/junit5/docs/current/user-guide/#testkit-engine-ExampleTestCase)因此已被注解@TestMethodOrder(OrderAnnotation.class)并且每个测试方法都已被注解@Order(…)。这使我们能够强制执行测试方法的执行顺序，从而使我们的verifyAllJupiterEvents()测试变得可靠。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

如果你想在有或没有排序要求的情况下进行部分匹配，你可以分别使用方法和。assertEventsMatchLooselyInOrder()assertEventsMatchLoosely()

```java
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.testkit.engine.EventConditions.abortedWithReason;
import static org.junit.platform.testkit.engine.EventConditions.container;
import static org.junit.platform.testkit.engine.EventConditions.engine;
import static org.junit.platform.testkit.engine.EventConditions.event;
import static org.junit.platform.testkit.engine.EventConditions.finishedSuccessfully;
import static org.junit.platform.testkit.engine.EventConditions.finishedWithFailure;
import static org.junit.platform.testkit.engine.EventConditions.skippedWithReason;
import static org.junit.platform.testkit.engine.EventConditions.started;
import static org.junit.platform.testkit.engine.EventConditions.test;
import static org.junit.platform.testkit.engine.TestExecutionResultConditions.instanceOf;
import static org.junit.platform.testkit.engine.TestExecutionResultConditions.message;

import java.io.StringWriter;
import java.io.Writer;

import example.ExampleTestCase;

import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;
import org.opentest4j.TestAbortedException;

class EngineTestKitAllEventsDemo {

    @Test
    void verifyAllJupiterEvents() {
        Writer writer = // create a java.io.Writer for debug output
    
        EngineTestKit.engine("junit-jupiter") 
            .selectors(selectClass(ExampleTestCase.class)) 
            .execute() 
            .allEvents() 
            .debug(writer) 
            .assertEventsMatchExactly( 
                event(engine(), started()),
                event(container(ExampleTestCase.class), started()),
                event(test("skippedTest"), skippedWithReason("for demonstration purposes")),
                event(test("succeedingTest"), started()),
                event(test("succeedingTest"), finishedSuccessfully()),
                event(test("abortedTest"), started()),
                event(test("abortedTest"),
                    abortedWithReason(instanceOf(TestAbortedException.class),
                        message(m -> m.contains("abc does not contain Z")))),
                event(test("failingTest"), started()),
                event(test("failingTest"), finishedWithFailure(
                    instanceOf(ArithmeticException.class), message("/ by zero"))),
                event(container(ExampleTestCase.class), finishedSuccessfully()),
                event(engine(), finishedSuccessfully()));
    }

}
```

|      | 选择 JUnit 木星TestEngine。                                |
| ---- | ------------------------------------------------------------ |
|      | 选择[ExampleTestCase](https://junit.org/junit5/docs/current/user-guide/#testkit-engine-ExampleTestCase)测试类。 |
|      | 执行TestPlan.                                              |
|      | 按所有事件过滤。                                           |
|      | 将所有事件打印到提供writer的用于调试目的。调试信息也可以写入OutputStream诸如System.out或System.err. |
|      | 严格按照测试引擎触发事件的顺序断言所有事件。               |

前面示例中的debug()调用会产生类似于以下内容的输出。

All Events:
    Event [type = STARTED, testDescriptor = JupiterEngineDescriptor: [engine:junit-jupiter], timestamp = 2018-12-14T12:45:14.082280Z, payload = null]
    Event [type = STARTED, testDescriptor = ClassTestDescriptor: [engine:junit-jupiter]/[class:example.ExampleTestCase], timestamp = 2018-12-14T12:45:14.089339Z, payload = null]
    Event [type = SKIPPED, testDescriptor = TestMethodTestDescriptor: [engine:junit-jupiter]/[class:example.ExampleTestCase]/[method:skippedTest()], timestamp = 2018-12-14T12:45:14.094314Z, payload = 'for demonstration purposes']
    Event [type = STARTED, testDescriptor = TestMethodTestDescriptor: [engine:junit-jupiter]/[class:example.ExampleTestCase]/[method:succeedingTest()], timestamp = 2018-12-14T12:45:14.095182Z, payload = null]
    Event [type = FINISHED, testDescriptor = TestMethodTestDescriptor: [engine:junit-jupiter]/[class:example.ExampleTestCase]/[method:succeedingTest()], timestamp = 2018-12-14T12:45:14.104922Z, payload = TestExecutionResult [status = SUCCESSFUL, throwable = null]]
    Event [type = STARTED, testDescriptor = TestMethodTestDescriptor: [engine:junit-jupiter]/[class:example.ExampleTestCase]/[method:abortedTest()], timestamp = 2018-12-14T12:45:14.106121Z, payload = null]
    Event [type = FINISHED, testDescriptor = TestMethodTestDescriptor: [engine:junit-jupiter]/[class:example.ExampleTestCase]/[method:abortedTest()], timestamp = 2018-12-14T12:45:14.109956Z, payload = TestExecutionResult [status = ABORTED, throwable = org.opentest4j.TestAbortedException: Assumption failed: abc does not contain Z]]
    Event [type = STARTED, testDescriptor = TestMethodTestDescriptor: [engine:junit-jupiter]/[class:example.ExampleTestCase]/[method:failingTest()], timestamp = 2018-12-14T12:45:14.110680Z, payload = null]
    Event [type = FINISHED, testDescriptor = TestMethodTestDescriptor: [engine:junit-jupiter]/[class:example.ExampleTestCase]/[method:failingTest()], timestamp = 2018-12-14T12:45:14.111217Z, payload = TestExecutionResult [status = FAILED, throwable = java.lang.ArithmeticException: / by zero]]
    Event [type = FINISHED, testDescriptor = ClassTestDescriptor: [engine:junit-jupiter]/[class:example.ExampleTestCase], timestamp = 2018-12-14T12:45:14.113731Z, payload = TestExecutionResult [status = SUCCESSFUL, throwable = null]]
    Event [type = FINISHED, testDescriptor = JupiterEngineDescriptor: [engine:junit-jupiter], timestamp = 2018-12-14T12:45:14.113806Z, payload = TestExecutionResult [status = SUCCESSFUL, throwable = null]]


### 6.4 JUnit平台启动器API

JUnit 5 的突出目标之一是使 JUnit 与其编程客户端(构建工具和 IDE)之间的接口更加强大和稳定。目的是将发现和执行测试的内部机制与外部所需的所有过滤和配置分离。

JUnit 5 引入了Launcher可用于发现、过滤和执行测试的概念。此外，第三方测试库——如 Spock、Cucumber 和 FitNesse——可以通过提供自定义 [TestEngine](https://junit.org/junit5/docs/current/user-guide/#test-engines)插入 JUnit 平台的启动基础设施。

启动器 API 在junit-platform-launcher模块中。

启动器 API 的示例使用者是ConsoleLauncher项目中的 junit-platform-console。

#### 6.4.1 发现测试

将测试发现作为平台本身的专用功能，可以将 IDE 和构建工具从之前 JUnit 版本中识别测试类和测试方法所必须经历的大部分困难中解放出来。

使用示例：

```java
import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.platform.engine.FilterResult;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryListener;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;
import org.junit.platform.launcher.PostDiscoveryFilter;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherConfig;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.platform.reporting.legacy.xml.LegacyXmlReportGeneratingListener;
LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
    .selectors(
        selectPackage("com.example.mytests"),
        selectClass(MyTestClass.class)
    )
    .filters(
        includeClassNamePatterns(".Tests")
    )
    .build();

try (LauncherSession session = LauncherFactory.openSession()) {
    TestPlan testPlan = session.getLauncher().discover(request);

    // ... discover additional test plans or execute tests
}
```

你可以选择类、方法和包中的所有类，甚至可以在类路径或模块路径中搜索所有测试。发现发生在所有参与的测试引擎中。

结果TestPlan是所有引擎、类和测试方法的分层(和只读)描述，符合LauncherDiscoveryRequest. 客户端可以遍历树，检索有关节点的详细信息，并获得指向原始源(如类、方法或文件位置)的链接。测试计划中的每个节点都有一个唯一的 ID ，可用于调用特定的测试或测试组。

客户可以通过 注册一个或多个LauncherDiscoveryListener实现， LauncherDiscoveryRequestBuilder以深入了解测试发现期间发生的事件。默认情况下，构建器会注册一个“失败时中止”侦听器，该侦听器会在遇到第一个发现失败后中止测试发现。LauncherDiscoveryListener可以通过 junit.platform.discovery.listener.default [配置参数](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params)更改默认值 。

#### 6.4.2 执行测试

要执行测试，客户端可以使用与LauncherDiscoveryRequest发现阶段相同的方法或创建新请求。可以通过使用以下示例中TestExecutionListener的 as 注册一个或多个实现来实现测试进度和报告。Launcher

```java
LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
    .selectors(
        selectPackage("com.example.mytests"),
        selectClass(MyTestClass.class)
    )
    .filters(
        includeClassNamePatterns(".Tests")
    )
    .build();

SummaryGeneratingListener listener = new SummaryGeneratingListener();

try (LauncherSession session = LauncherFactory.openSession()) {
    Launcher launcher = session.getLauncher();
    // Register a listener of your choice
    launcher.registerTestExecutionListeners(listener);
    // Discover tests and build a test plan
    TestPlan testPlan = launcher.discover(request);
    // Execute test plan
    launcher.execute(testPlan);
    // Alternatively, execute the request directly
    launcher.execute(request);
}

TestExecutionSummary summary = listener.getSummary();
// Do something with the summary...
```

该方法没有返回值execute()，但你可以使用 a TestExecutionListener来聚合结果。有关示例，请参见 SummaryGeneratingListener、LegacyXmlReportGeneratingListener和 UniqueIdTrackingListener。

#### 6.4.3 注册测试引擎

有关详细信息，请参阅有关[TestEngine 注册](https://junit.org/junit5/docs/current/user-guide/#test-engines-registration)的专门部分。

#### 6.4.4 注册一个 PostDiscoveryFilter

除了将发现后过滤器指定为LauncherDiscoveryRequest 传递给LauncherAPI 的一部分之外，PostDiscoveryFilter实现将在运行时通过 JavaServiceLoader机制发现，并由 Launcher请求的一部分自动应用。

例如，在文件中example.CustomTagFilter实现PostDiscoveryFilter和声明的类/META-INF/services/org.junit.platform.launcher.PostDiscoveryFilter 会自动加载和应用。

#### 6.4.5 注册一个 LauncherSessionListener

当 a打开(在第一次发现和执行测试之前)和关闭(当不再发现或执行更多测试时)时，LauncherSessionListener会通知 注册的实现。它们可以通过传递给 的 以编程方式注册，或者它们可以在运行时通过 Java机制被发现并自动注册(除非禁用自动注册。)LauncherSessionLauncherLauncherConfigLauncherFactoryServiceLoaderLauncherSession

##### 工具支持

已知以下构建工具和 IDE 可为 提供全面支持LauncherSession：

-   Gradle 4.6 及更高版本
-   Maven Surefire/Failsafe 3.0.0-M6 及更高版本
-   IntelliJ IDEA 2017.3 及更高版本

其他工具也可能有效，但尚未经过明确测试。

##### 用法示例

ALauncherSessionListener非常适合实现每个 JVM 设置/拆卸行为一次，因为它分别在启动器会话中的第一个测试之前和最后一个测试之后调用。启动器会话的范围取决于使用的 IDE 或构建工具，但通常对应于测试 JVM 的生命周期。在执行第一个测试之前启动 HTTP 服务器并在执行最后一个测试之后停止它的自定义侦听器可能如下所示：

src/test/java/example/session/GlobalSetupTeardownListener.java

```java
package example.session;

import static java.net.InetAddress.getLoopbackAddress;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;

public class GlobalSetupTeardownListener implements LauncherSessionListener {

    private Fixture fixture;
    
    @Override
    public void launcherSessionOpened(LauncherSession session) {
        // Avoid setup for test discovery by delaying it until tests are about to be executed
        session.getLauncher().registerTestExecutionListeners(new TestExecutionListener() {
            @Override
            public void testPlanExecutionStarted(TestPlan testPlan) {
                if (fixture == null) {
                    fixture = new Fixture();
                    fixture.setUp();
                }
            }
        });
    }
    
    @Override
    public void launcherSessionClosed(LauncherSession session) {
        if (fixture != null) {
            fixture.tearDown();
            fixture = null;
        }
    }
    
    static class Fixture {
    
        private HttpServer server;
        private ExecutorService executorService;
    
        void setUp() {
            try {
                server = HttpServer.create(new InetSocketAddress(getLoopbackAddress(), 0), 0);
            }
            catch (IOException e) {
                throw new UncheckedIOException("Failed to start HTTP server", e);
            }
            server.createContext("/test", exchange -> {
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
            });
            executorService = Executors.newCachedThreadPool();
            server.setExecutor(executorService);
            server.start(); 
            int port = server.getAddress().getPort();
            System.setProperty("http.server.host", getLoopbackAddress().getHostAddress()); 
            System.setProperty("http.server.port", String.valueOf(port)); 
        }
    
        void tearDown() {
            server.stop(0); 
            executorService.shutdownNow();
        }
    }

}
```

|      | 启动 HTTP 服务器                       |
| ---- | -------------------------------------- |
|      | 将其主机地址导出为系统属性以供测试使用 |
|      | 将其端口导出为系统属性以供测试使用     |
|      | 停止 HTTP 服务器                       |

此示例使用JDK附带的 jdk.httpserver 模块中的 HTTP 服务器实现，但与任何其他服务器或资源的工作方式类似。为了让 JUnit 平台拾取监听器，你需要通过将具有以下名称和内容的资源文件添加到你的测试运行时类路径(例如，通过将文件添加到src/test/resources)将其注册为服务：

src/test/resources/META-INF/services/org.junit.platform.launcher.LauncherSessionListener


example.session.GlobalSetupTeardownListener


你现在可以使用测试中的资源：

src/test/java/example/session/HttpTests.java

```java
package example.session;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.Test;

class HttpTests {

    @Test
    void respondsWith204() throws Exception {
        String host = System.getProperty("http.server.host"); 
        String port = System.getProperty("http.server.port"); 
        URL url = new URL("http://" + host + ":" + port + "/test");
    
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode(); 
    
        assertEquals(204, responseCode); 
    }
}
```

|      | 从监听器设置的系统属性中读取服务器的主机地址 |
| ---- | -------------------------------------------- |
|      | 从监听器设置的系统属性中读取服务器的端口     |
|      | 向服务器发送请求                             |
|      | 查看响应的状态码                             |

#### 6.4.6 注册一个 LauncherDiscoveryListener

除了将发现侦听器指定为 a 的一部分或通过API LauncherDiscoveryRequest以编程方式注册它们之外，还可以在运行时通过 Java机制发现自定义实现， 并通过.LauncherLauncherDiscoveryListenerServiceLoaderLauncherLauncherFactory

例如，在文件中example.CustomLauncherDiscoveryListener实现 LauncherDiscoveryListener和声明 的类/META-INF/services/org.junit.platform.launcher.LauncherDiscoveryListener会自动加载和注册。

#### 6.4.7 注册一个 TestExecutionListener

除了用于以Launcher编程方式注册测试执行侦听器的公共 API 方法之外，自定义TestExecutionListener实现将在运行时通过 Java 的ServiceLoader机制被发现，并自动注册到 Launcher通过LauncherFactory.

例如，在文件中example.CustomTestExecutionListener实现 TestExecutionListener和声明 的类/META-INF/services/org.junit.platform.launcher.TestExecutionListener会自动加载和注册。

#### 6.4.8 配置 TestExecutionListener

当 aTestExecutionListener通过LauncherAPI 以编程方式注册时，侦听器可以提供编程方式来配置它——例如，通过其构造函数、setter 方法等。但是，当 aTestExecutionListener通过 JavaServiceLoader机制自动注册时(请参阅 [注册 TestExecutionListener](https://junit.org/junit5/docs/current/user-guide/#launcher-api-listeners-custom))，用户无法直接配置监听器。在这种情况下，a 的作者TestExecutionListener可以选择通过[配置参数](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params)使侦听器可配置。然后侦听器可以通过TestPlan提供给 testPlanExecutionStarted(TestPlan)和testPlanExecutionFinished(TestPlan)回调方法访问配置参数。有关UniqueIdTrackingListener示例，请参见。

#### 6.4.9 停用 TestExecutionListener

有时在某些执行侦听器未处于活动状态的情况下运行测试套件可能很有用。例如，你可能有自定义 aTestExecutionListener将测试结果发送到外部系统以进行报告，而在调试时你可能不希望报告这些调试结果。为此，为 junit.platform.execution.listeners.deactivate 配置参数提供一个模式，以指定哪些执行侦听器应该为当前测试运行停用(即不注册)。

|      | ServiceLoader只有通过文件中的机制 注册的侦听/META-INF/services/org.junit.platform.launcher.TestExecutionListener器才能被停用。换句话说，任何TestExecutionListener通过 显式注册的 LauncherDiscoveryRequest都不能通过 junit.platform.execution.listeners.deactivate 配置参数停用。此外，由于执行侦听器是在测试运行开始之前注册的，因此 junit.platform.execution.listeners.deactivate 配置参数只能作为 JVM 系统属性或通过 JUnit 平台配置文件提供(有关详细信息，请参阅 [配置参数](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params))。无法在传递给 的中提供此配置参数。LauncherDiscoveryRequestLauncher |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

##### 模式匹配语法

有关详细信息，请参阅[模式匹配语法](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params-deactivation-pattern)。

#### 6.4.10 配置启动器

如果你需要对测试引擎和侦听器的自动检测和注册进行细粒度控制，你可以创建一个实例LauncherConfig并将其提供给LauncherFactory. 通常， 的实例LauncherConfig是通过内置的 fluent builder API 创建的，如以下示例所示。

```java
LauncherConfig launcherConfig = LauncherConfig.builder()
    .enableTestEngineAutoRegistration(false)
    .enableLauncherSessionListenerAutoRegistration(false)
    .enableLauncherDiscoveryListenerAutoRegistration(false)
    .enablePostDiscoveryFilterAutoRegistration(false)
    .enableTestExecutionListenerAutoRegistration(false)
    .addTestEngines(new CustomTestEngine())
    .addLauncherSessionListeners(new CustomLauncherSessionListener())
    .addLauncherDiscoveryListeners(new CustomLauncherDiscoveryListener())
    .addPostDiscoveryFilters(new CustomPostDiscoveryFilter())
    .addTestExecutionListeners(new LegacyXmlReportGeneratingListener(reportsDir, out))
    .addTestExecutionListeners(new CustomTestExecutionListener())
    .build();

LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
    .selectors(selectPackage("com.example.mytests"))
    .build();

try (LauncherSession session = LauncherFactory.openSession(launcherConfig)) {
    session.getLauncher().execute(request);
}
```

### 6.5 测试引擎

ATestEngine有助于发现和执行特定编程模型的测试。

例如，JUnit 提供了一个TestEngine发现和执行使用JUnit Jupiter编程模型编写的测试的工具(请参阅[编写测试](https://junit.org/junit5/docs/current/user-guide/#writing-tests)和[扩展模型](https://junit.org/junit5/docs/current/user-guide/#extensions))。

#### 6.5.1 JUnit 测试引擎

JUnit 提供了三种TestEngine实现。

-   junit-jupiter-engine：JUnit Jupiter的核心。
-   junit-vintage-engine：JUnit 4之上的一个薄层，允许使用 JUnit 平台启动器基础设施运行老式 测试(基于 JUnit 3.8 和 JUnit 4)。
-   junit-platform-suite-engine：使用 JUnit 平台启动器基础结构执行声明性测试套件。

#### 6.5.2 自定义测试引擎

TestEngine你可以通过在 [junit-platform-engine](https://junit.org/junit5/docs/current/api/org.junit.platform.engine/org/junit/platform/engine/package-summary.html)模块中实现接口并注册你的引擎来贡献你自己的定制。

每个人都TestEngine必须提供自己唯一的 ID，从 中发现测试 EngineDiscoveryRequest，并根据.执行ExecutionRequest这些测试。

|      | 唯一的junit-ID 前缀是为 JUnit 团队的 TestEngines 保留的JUnit 平台Launcher强制只有TestEngineJUnit 团队发布的实现可以使用junit-前缀作为其TestEngineID。如果任何第三方TestEngine声称是junit-jupiter或junit-vintage，将抛出异常，立即停止 JUnit 平台的执行。如果任何第三方TestEngine使用junit-前缀作为其 ID，将记录一条警告消息。JUnit 平台的后续版本将针对此类违规行为抛出异常。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

为了在启动 JUnit 平台之前促进 IDE 和工具中的测试发现，TestEngine鼓励实现使用@Testable 注解。例如，JUnit Jupiter中的@Test和@TestFactory注解使用 进行元注解@Testable。有关详细信息，请查阅 Javadoc @Testable。

如果你的自定义TestEngine需要配置，请考虑允许用户通过[配置参数](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params)提供配置。但是请注意，强烈建议你为测试引擎支持的所有配置参数使用唯一的前缀。这样做将确保你的配置参数名称与其他测试引擎的名称没有冲突。此外，由于配置参数可能作为 JVM 系统属性提供，因此避免与其他系统属性的名称发生冲突是明智的。例如，JUnit Jupiter将junit.jupiter.其用作所有支持的配置参数的前缀。junit-此外，正如上面关于 ID前缀 的警告一样TestEngine，你不应该使用junit.作为你自己的配置参数名称的前缀。

虽然目前没有关于如何实现自定义的官方指南TestEngine，但你可以参考[JUnit 测试引擎的实现或](https://junit.org/junit5/docs/current/user-guide/#test-engines-junit)[JUnit 5 wiki](https://github.com/junit-team/junit5/wiki/Third-party-Extensions#junit-platform-test-engines)中列出的第三方测试引擎的实现 。你还将在 Internet 上找到各种教程和博客，它们演示了如何编写自定义TestEngine.

|      | HierarchicalTestEngine是 TestEngineSPI(由 所使用junit-jupiter-engine)的一个方便的抽象基础实现，它只需要实现者提供测试发现的逻辑。它实现TestDescriptors了实现Node接口的执行，包括对并行执行的支持。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

#### 6.5.3 注册测试引擎

TestEngine通过 Java 的ServiceLoader机制支持注册。

例如，该junit-jupiter-engine模块将其注册 到JAR中文件夹 内org.junit.jupiter.engine.JupiterTestEngine命名 org.junit.platform.engine.TestEngine的文件中。/META-INF/servicesjunit-jupiter-engine

## 7. API演进

JUnit 5 的主要目标之一是提高维护人员发展 JUnit 的能力，尽管它已在许多项目中使用。在JUnit 4中，许多最初作为内部结构添加的东西只被外部扩展编写者和工具构建者使用。这使得更改JUnit 4特别困难，有时甚至是不可能的。

这就是为什么JUnit 5为所有公开可用的接口、类和方法引入了定义的生命周期。

### 7.1 API版本和状态

每个已发布的工件都有一个版本号<major>.<minor>.<patch>，并且所有公开可用的接口、类和方法都使用来自 [@API Guardian](https://github.com/apiguardian-team/apiguardian)项目的[@API注解。](https://apiguardian-team.github.io/apiguardian/docs/current/api/)可以为注解的属性分配以下值之一。status

| 地位           | 描述                                                         |
| :------------- | :----------------------------------------------------------- |
| INTERNAL     | 不得由 JUnit 本身以外的任何代码使用。可能会被删除，恕不另行通知。 |
| DEPRECATED   | 不应再使用；可能会在下一个次要版本中消失。                   |
| EXPERIMENTAL | 用于我们正在寻找反馈的新的实验性功能。 谨慎使用此元素；它可能会被提升到MAINTAINED或 STABLE在未来，但也可能会在没有事先通知的情况下被删除，即使是在补丁中。 |
| MAINTAINED   | 用于至少在当前主要版本的下一个次要版本中不会以向后不兼容的方式更改的功能。如果计划删除，它将被降级为DEPRECATED第一个。 |
| STABLE       | 5.适用于在当前主要版本 ( )中不会以向后不兼容的方式更改的功能。 |

如果@API注解存在于某个类型上，则它也被认为适用于该类型的所有公共成员。允许成员声明status 较低稳定性的不同值。

### 7.2 实验性API

下表列出了当前通过 指定为实验性@API(status = EXPERIMENTAL)的 API 。依赖此类 API 时应谨慎。

| 包裹名字                                          | 类型名称                                                     | 自从  |
| :------------------------------------------------ | :----------------------------------------------------------- | :---- |
| org.junit.jupiter.api                           | ClassDescriptor (界面)                                 | 5.8 |
| org.junit.jupiter.api                           | ClassOrderer (界面)                                    | 5.8 |
| org.junit.jupiter.api                           | ClassOrdererContext (界面)                             | 5.8 |
| org.junit.jupiter.api                           | DisplayNameGenerator.IndicativeSentences (班级)        | 5.7 |
| org.junit.jupiter.api                           | IndicativeSentencesGeneration (注解)                   | 5.7 |
| org.junit.jupiter.api                           | MethodOrderer.DisplayName (班级)                       | 5.7 |
| org.junit.jupiter.api                           | MethodOrderer.MethodName (班级)                        | 5.7 |
| org.junit.jupiter.api                           | TestClassOrder (注解)                                  | 5.8 |
| org.junit.jupiter.api                           | Timeout.ThreadMode (枚举)                              | 5.9 |
| org.junit.jupiter.api.extension                 | DynamicTestInvocationContext (界面)                    | 5.8 |
| org.junit.jupiter.api.extension                 | ExecutableInvoker (界面)                               | 5.9 |
| org.junit.jupiter.api.extension                 | InvocationInterceptor (界面)                           | 5.5 |
| org.junit.jupiter.api.extension                 | InvocationInterceptor.Invocation (界面)                | 5.5 |
| org.junit.jupiter.api.extension                 | LifecycleMethodExecutionExceptionHandler (界面)        | 5.5 |
| org.junit.jupiter.api.extension                 | ReflectiveInvocationContext (界面)                     | 5.5 |
| org.junit.jupiter.api.extension                 | TestInstancePreConstructCallback (界面)                | 5.9 |
| org.junit.jupiter.api.extension                 | TestInstantiationException (班级)                      | 5.3 |
| org.junit.jupiter.api.extension.support         | TypeBasedParameterResolver (班级)                      | 5.6 |
| org.junit.jupiter.api.io                        | CleanupMode (枚举)                                     | 5.9 |
| org.junit.jupiter.api.io                        | TempDir (注解)                                         | 5.4 |
| org.junit.jupiter.api.parallel                  | Execution (注解)                                       | 5.3 |
| org.junit.jupiter.api.parallel                  | ExecutionMode (枚举)                                   | 5.3 |
| org.junit.jupiter.api.parallel                  | Isolated (注解)                                        | 5.7 |
| org.junit.jupiter.api.parallel                  | ResourceAccessMode (枚举)                              | 5.3 |
| org.junit.jupiter.api.parallel                  | ResourceLock (注解)                                    | 5.3 |
| org.junit.jupiter.api.parallel                  | ResourceLocks (注解)                                   | 5.3 |
| org.junit.jupiter.api.parallel                  | Resources (班级)                                       | 5.3 |
| org.junit.jupiter.params.converter              | TypedArgumentConverter (班级)                          | 5.7 |
| org.junit.platform.commons.support              | SearchOption (枚举)                                    | 1.8 |
| org.junit.platform.console                      | ConsoleLauncherToolProvider (班级)                     | 1.6 |
| org.junit.platform.engine                       | EngineDiscoveryListener (界面)                         | 1.6 |
| org.junit.platform.engine                       | SelectorResolutionResult (班级)                        | 1.6 |
| org.junit.platform.engine.discovery             | IterationSelector (班级)                               | 1.9 |
| org.junit.platform.engine.support.config        | PrefixedConfigurationParameters (班级)                 | 1.3 |
| org.junit.platform.engine.support.discovery     | EngineDiscoveryRequestResolver (班级)                  | 1.5 |
| org.junit.platform.engine.support.discovery     | EngineDiscoveryRequestResolver.Builder (班级)          | 1.5 |
| org.junit.platform.engine.support.discovery     | EngineDiscoveryRequestResolver.InitializationContext (界面) | 1.5 |
| org.junit.platform.engine.support.discovery     | SelectorResolver (界面)                                | 1.5 |
| org.junit.platform.engine.support.discovery     | SelectorResolver.Context (界面)                        | 1.5 |
| org.junit.platform.engine.support.discovery     | SelectorResolver.Match (班级)                          | 1.5 |
| org.junit.platform.engine.support.discovery     | SelectorResolver.Resolution (班级)                     | 1.5 |
| org.junit.platform.engine.support.hierarchical  | DefaultParallelExecutionConfigurationStrategy (枚举)   | 1.3 |
| org.junit.platform.engine.support.hierarchical  | ExclusiveResource (班级)                               | 1.3 |
| org.junit.platform.engine.support.hierarchical  | ForkJoinPoolHierarchicalTestExecutorService (班级)     | 1.3 |
| org.junit.platform.engine.support.hierarchical  | HierarchicalTestExecutorService (界面)                 | 1.3 |
| org.junit.platform.engine.support.hierarchical  | Node.ExecutionMode (枚举)                              | 1.3 |
| org.junit.platform.engine.support.hierarchical  | Node.Invocation (界面)                                 | 1.4 |
| org.junit.platform.engine.support.hierarchical  | ParallelExecutionConfiguration (界面)                  | 1.3 |
| org.junit.platform.engine.support.hierarchical  | ParallelExecutionConfigurationStrategy (界面)          | 1.3 |
| org.junit.platform.engine.support.hierarchical  | ResourceLock (界面)                                    | 1.3 |
| org.junit.platform.engine.support.hierarchical  | SameThreadHierarchicalTestExecutorService (班级)       | 1.3 |
| org.junit.platform.jfr                          | FlightRecordingDiscoveryListener (班级)                | 1.8 |
| org.junit.platform.jfr                          | FlightRecordingExecutionListener (班级)                | 1.8 |
| org.junit.platform.launcher                     | EngineDiscoveryResult (班级)                           | 1.6 |
| org.junit.platform.launcher                     | LauncherDiscoveryListener (界面)                       | 1.6 |
| org.junit.platform.launcher                     | LauncherSession (界面)                                 | 1.8 |
| org.junit.platform.launcher                     | LauncherSessionListener (界面)                         | 1.8 |
| org.junit.platform.launcher.listeners           | UniqueIdTrackingListener (班级)                        | 1.8 |
| org.junit.platform.launcher.listeners.discovery | LauncherDiscoveryListeners (班级)                      | 1.6 |
| org.junit.platform.reporting.open.xml           | OpenTestReportGeneratingListener (班级)                | 1.9 |
| org.junit.platform.suite.api                    | ConfigurationParameter (注解)                          | 1.8 |
| org.junit.platform.suite.api                    | ConfigurationParameters (注解)                         | 1.8 |
| org.junit.platform.suite.api                    | DisableParentConfigurationParameters (注解)            | 1.8 |
| org.junit.platform.suite.api                    | SelectClasspathResource (注解)                         | 1.8 |
| org.junit.platform.suite.api                    | SelectClasspathResources (注解)                        | 1.8 |
| org.junit.platform.suite.api                    | SelectDirectories (注解)                               | 1.8 |
| org.junit.platform.suite.api                    | SelectFile (注解)                                      | 1.8 |
| org.junit.platform.suite.api                    | SelectFiles (注解)                                     | 1.8 |
| org.junit.platform.suite.api                    | SelectModules (注解)                                   | 1.8 |
| org.junit.platform.suite.api                    | SelectUris (注解)                                      | 1.8 |
| org.junit.platform.suite.api                    | Suite (注解)                                           | 1.8 |

### 7.3 弃用的API

下表列出了当前通过 指定为弃用 的@API(status = DEPRECATED)API 。你应尽可能避免使用已弃用的 API，因为此类 API 可能会在即将发布的版本中被删除。

| 包裹名字                                         | 类型名称                                    | 自从  |
| :----------------------------------------------- | :------------------------------------------ | :---- |
| org.junit.jupiter.api                          | MethodOrderer.Alphanumeric (班级)     | 5.7 |
| org.junit.platform.commons.util                | BlacklistedExceptions (班级)          | 1.7 |
| org.junit.platform.commons.util                | PreconditionViolationException (班级) | 1.5 |
| org.junit.platform.engine.support.filter       | ClasspathScanningSupport (班级)       | 1.5 |
| org.junit.platform.engine.support.hierarchical | SingleTestExecutor (班级)             | 1.2 |
| org.junit.platform.launcher.listeners          | LegacyReportingUtils (班级)           | 1.6 |
| org.junit.platform.runner                      | JUnitPlatform (班级)                  | 1.8 |
| org.junit.platform.suite.api                   | UseTechnicalNames (注解)              | 1.8 |

### 7.4 @API工具支持

[@API Guardian](https://github.com/apiguardian-team/apiguardian)项目计划为使用[@API](https://apiguardian-team.github.io/apiguardian/docs/current/api/)注解的 API 的发布者和消费者提供工具支持。例如，工具支持可能会提供一种方法来检查 JUnit API 的使用是否与@API注解声明一致。

## 8. 贡献者

直接在 GitHub 上浏览[当前的贡献者列表。](https://github.com/junit-team/junit5/graphs/contributors)

## 9. 发行说明

发行说明可[在此处](https://junit.org/junit5/docs/current/release-notes/index.html#release-notes)获得。

## 10. 附录

### 10.1 可重现的构建

从 5.7 版开始，JUnit 5 的目标是使其非 javadoc JAR 可 [重现](https://reproducible-builds.org/)。

在相同的构建条件下，例如 Java 版本，重复构建应该逐字节提供相同的输出。

这意味着任何人都可以在 Maven Central/Sonatype 上重现工件的构建条件并在本地生成相同的输出工件，从而确认存储库中的工件实际上是从该源代码生成的。

### 10.2 依赖元数据

最终版本和里程碑的工件部署到[Maven Central](https://search.maven.org/)，快照工件部署到[/org/junit](https://oss.sonatype.org/content/repositories/snapshots/org/junit/)下 的 Sonatype[快照存储库](https://oss.sonatype.org/content/repositories/snapshots)。

#### 10.2.1 JUnit平台

-   组号：org.junit.platform

-   版本：1.9.1

-   工件 ID：

    -   junit-platform-commons

        JUnit 平台的通用 API 和支持实用程序。任何带有注解的 API @API(status = INTERNAL)仅供在 JUnit 框架本身内使用。不支持外部方对内部 API 的任何使用！

    -   junit-platform-console

        支持从控制台在 JUnit 平台上发现和执行测试。有关详细信息，请参阅[控制台启动器](https://junit.org/junit5/docs/current/user-guide/#running-tests-console-launcher)。

    -   junit-platform-console-standalone

        [Maven Central 的junit-platform-console-standalone](https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone) 目录下提供了一个包含所有依赖项的可执行 JAR 。有关详细信息，请参阅[控制台启动器](https://junit.org/junit5/docs/current/user-guide/#running-tests-console-launcher)。

    -   junit-platform-engine

        测试引擎的公共 API。有关详细信息，请参阅[注册测试引擎](https://junit.org/junit5/docs/current/user-guide/#launcher-api-engines-custom)。

    -   junit-platform-jfr

        为 JUnit 平台上的 Java Flight Recorder 事件提供一个LauncherDiscoveryListener和TestExecutionListener。有关详细信息，请参阅[飞行记录器支持](https://junit.org/junit5/docs/current/user-guide/#running-tests-listeners-flight-recorder) 。

    -   junit-platform-launcher

        用于配置和启动测试计划的公共 API——通常由 IDE 和构建工具使用。有关详细信息，请参阅[JUnit 平台启动器 API](https://junit.org/junit5/docs/current/user-guide/#launcher-api)。

    -   junit-platform-reporting

        TestExecutionListener生成测试报告的实现——通常由 IDE 和构建工具使用。有关详细信息，请参阅[JUnit 平台报告](https://junit.org/junit5/docs/current/user-guide/#junit-platform-reporting)。

    -   junit-platform-runner

        用于在JUnit 4环境中的 JUnit 平台上执行测试和测试套件的运行程序。有关详细信息，请参阅[使用JUnit 4运行 JUnit 平台](https://junit.org/junit5/docs/current/user-guide/#running-tests-junit-platform-runner)。

    -   junit-platform-suite

        JUnit Platform Suite 工件，可传递地引入对 构建工具(如 Gradle 和 Maven )的依赖关系junit-platform-suite-api并简化依赖管理。junit-platform-suite-engine

    -   junit-platform-suite-api

        用于在 JUnit 平台上配置测试套件的注解。由 [JUnit Platform Suite Engine](https://junit.org/junit5/docs/current/user-guide/#junit-platform-suite-engine)和 [JUnitPlatform runner](https://junit.org/junit5/docs/current/user-guide/#running-tests-junit-platform-runner)支持。

    -   junit-platform-suite-commons

        用于在 JUnit 平台上执行测试套件的通用支持实用程序。

    -   junit-platform-suite-engine

        在 JUnit 平台上执行测试套件的引擎；仅在运行时需要。有关详细信息，请参阅 [JUnit 平台套件引擎](https://junit.org/junit5/docs/current/user-guide/#junit-platform-suite-engine)。

    -   junit-platform-testkit

        支持执行给定的测试计划TestEngine，然后通过流畅的 API 访问结果以验证预期结果。

#### 10.2.2 JUnit木星

-   组号：org.junit.jupiter

-   版本：5.9.1

-   工件 ID：

    -   junit-jupiter

       JUnit Jupiter聚合器工件，可传递地引入对 junit-jupiter-api、junit-jupiter-params和 的依赖项，junit-jupiter-engine以简化构建工具(例如 Gradle 和 Maven)中的依赖项管理。

    -   junit-jupiter-api

        [用于编写测试](https://junit.org/junit5/docs/current/user-guide/#writing-tests)和[扩展](https://junit.org/junit5/docs/current/user-guide/#extensions)的JUnit JupiterAPI 。

    -   junit-jupiter-engine

        JUnit Jupiter测试引擎实现；仅在运行时需要。

    -   junit-jupiter-params

        支持JUnit Jupiter中的[参数化测试](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests)。

    -   junit-jupiter-migrationsupport

        支持从JUnit 4迁移到 JUnit Jupiter；只需要支持JUnit 4的@Ignore注解和运行选定的JUnit 4规则。

#### 10.2.3 JUnit 复古

-   组号：org.junit.vintage

-   版本：5.9.1

-   工件编号：

    -   junit-vintage-engine

        JUnit Vintage 测试引擎实现，允许在 JUnit 平台上运行 Vintage JUnit 测试。老式测试包括使用 JUnit 3 或JUnit 4API 编写的测试或使用构建在这些 API 上的测试框架编写的测试。

#### 10.2.4 物料清单 (BOM)

在使用[Maven](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Importing_Dependencies) 或[Gradle引用上述多个工件时，在以下 Maven 坐标下提供](https://docs.gradle.org/current/userguide/managing_transitive_dependencies.html#sec:bom_import)的材料清单POM 可用于简化依赖关系管理 。

-   组号：org.junit
-   工件编号：junit-bom
-   版本：5.9.1

#### 10.2.5 依赖关系

大多数上述工件在其发布的 Maven POM 中都依赖于以下@API Guardian JAR。

-   组号：org.apiguardian
-   工件编号：apiguardian-api
-   版本：1.1.2

此外，大多数上述工件对以下OpenTest4J JAR 具有直接或可传递的依赖性。

-   组号：org.opentest4j
-   工件编号：opentest4j
-   版本：1.2.0

### 10.3 依赖关系图

![组件图](https://junit.org/junit5/docs/current/user-guide/images/component-diagram.svg)