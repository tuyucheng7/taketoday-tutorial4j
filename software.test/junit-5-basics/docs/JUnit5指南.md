## 1. 概述

JUnit是Java生态系统中最常用的单元测试框架之一。
JUnit 5版本包含了许多重大的变化，其目标是支持Java 8及以上版本的新功能，并支持多种不同风格的测试。

## 2. maven依赖

```xml

<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>5.8.1</version>
    <scope>test</scope>
</dependency>
```

此外，现在可以直接在Eclipse和IntelliJ IDEA上支持的JUnit platform上运行单元测试。当然，我们也可以使用Maven test goal运行测试。

另一方面，IntelliJ默认支持JUnit 5。因此，在IntelliJ上运行JUnit 5非常容易，我们只需右键单击–>run，或使用快捷键Ctrl+Shift+F10。

需要注意的是，这个版本需要Java 8才能使用。

## 3. 架构

JUnit 5包含来自三个不同子项目的几个不同模块。

### 3.1 JUnit Platform

Platform负责在JVM上启动测试框架。它在JUnit及其客户端(如构建工具)之间定义了一个稳定而强大的接口。

Platform可以轻松地将客户端与JUnit集成，以发现和执行测试。

它还定义了TestEngine API，用于开发在JUnit Platform上运行的测试框架。通过实现自己的TestEngine，我们可以将第三方测试库直接插入JUnit。

### 3.2 JUnit Jupiter

该模块包括用于在JUnit 5中编写测试的新编程和扩展模型。与JUnit 4相比，新的注解包括:

+ @TestFactory - 表示作为动态测试的测试工厂的方法。
+ @DisplayName - 定义测试类或测试方法的自定义显示名称。
+ @Nested - 表示带注解的类是嵌套的非静态测试类。
+ @Tag - 声明过滤测试的标签。
+ @ExtendWith - 注册自定义的Extensions。
+ @BeforeEach - 表示带注解的方法将在每个测试方法之前执行，等效于Junit 4中的@Before。
+ @AfterEach - 表示带注解的方法将在每个测试方法之后执行，等效于Junit 4中的@After。
+ @BeforeAll - 表示带注解的方法将在当前类中的所有测试方法之前执行，等效于Junit 4中的@BeforeClass。
+ @AfterAll - 表示带注解的方法将在当前类中的所有测试方法之后执行，等效于Junit 4中的@AfterClass。
+ @Disabled - 禁用测试类或方法，等效于Junit 4中的@Ignore。

### 3.3 JUnit Vintage

JUnit Vintage支持在JUnit 5 Platform上运行基于JUnit 3和JUnit 4的测试。

## 4. 基础注解

### 4.1 @BeforeEach和@BeforeAll

下面是在主要测试用例之前执行的简单代码示例：

```java
class JUnit5NewFeaturesUnitTest {
    private static final Logger log = LoggerFactory.getLogger(JUnit5NewFeaturesUnitTest.class);

    @BeforeAll
    static void setup() {
        log.info("@BeforeAll - executes once before all test method in this class");
    }

    @BeforeEach
    void init() {
        log.info("@BeforeEach - executes before each test method in this class");
    }
}
```

需要注意的是，带有@BeforeAll注解的方法必须是静态的。

### 4.2 @DisplayName和@Disabled

现在，让我们看看如何禁用测试方法:

```java
class JUnit5NewFeaturesUnitTest {

    @Test
    @DisplayName("Single test successful")
    void Single_test_successful() {
        log.info("success");
    }

    @Test
    @Disabled("Not implemented yet.")
    void testShowSomething() {

    }
}
```

正如我们所看到的，我们可以使用新的注解来更改测试的显示名称或禁用带有注解的测试方法。

### 4.3 @AfterEache和@AfterAll

```java

class JUnit5NewFeaturesUnitTest {

    @AfterAll
    static void done() {
        log.info("@AfterAll - executes after all test method");
    }

    @AfterEach
    void tearDown() {
        log.info("@AfterEach - executes after each test method");
    }
}
```

请注意，带有@AfterAll注解的方法也需要是静态方法。

## 5. Assertions和Assumptions

Junit 5的更新充分利用了Java 8的新特性，尤其是lambda表达式的使用，使得我们能编写更简洁的测试代码。

### 5.1 Assertions

Assertions类已经移动到org.junit.jupiter.api.Assertions包中，并得到了显著改善，如前所述，我们现在可以在断言中使用lambdas：

```java
class FirstUnitTests {

    @Test
    void lambdaExpressions() {
        List<Integer> numbers = Arrays.asList(1, 2, 3);
        assertTrue(numbers.stream().mapToInt(x -> x).sum() > 5, "Sum should be greater than 5");
    }
}
```

尽管上面的例子很简单，但对断言消息使用lambda表达式的一个优点是它是惰性求值的，如果消息构造成本很高，这可以节省时间和资源。

现在还可以使用assertAll()对断言进行分组，这会使用MultipleFailuresError报告断言组内的任何失败断言：

```java
class AssertionsExampleUnitTest {

    @Test
    @Disabled("test to show MultipleFailureError")
    void groupAssertions() {
        int[] numbers = {0, 1, 2, 3, 4};
        assertAll("number",
                () -> assertEquals(1, numbers[0]),
                () -> assertEquals(3, numbers[3]),
                () -> assertEquals(1, numbers[4]));
    }
}
```

这意味着可以更安全地进行更复杂的断言，因为我们能够精确定位任何失败断言的确切位置。

### 5.2 Assumptions

只有在满足某些条件时，才会使用Assumptions来运行测试。这通常用于测试正常运行所需的外部条件，但与测试内容没有直接关系。

我们可以用assumeTrue()，assumeFalse()和assumingThat()声明一个Assumptions。

```java
class AssumptionUnitTest {

    @Test
    void trueAssumptions() {
        assumeTrue(5 > 1, () -> "5 is greater than 1");
        assertEquals(5 + 2, 7);
    }

    @Test
    void falseAssumptions() {
        assumeFalse(5 < 1, () -> "5 is less than 1");
        assertEquals(5 + 2, 7);
    }

    @Test
    void assumptionThat() {
        String something = "Just a string";
        assumingThat(something.equals("Just a string"), () -> assertEquals(2 + 2, 4));
    }
}
```

如果assume失败，则抛出TestAbortedException并跳过测试。Assumptions也支持lambda表达式。

## 6. 异常测试

JUnit 5中有两种异常测试方法，我们可以使用assertThrows()方法实现这两种方法：

```java
class ExceptionUnitTests {

    @Test
    void showThrowException() {
        Throwable exception = assertThrows(UnsupportedOperationException.class, () -> {
            throw new UnsupportedOperationException("Not supported");
        });
        assertEquals("Not supported", exception.getMessage());
    }

    @Test
    void assertThrowsNullPointerException() {
        String str = null;
        assertThrows(NullPointerException.class, () -> str.length());
    }
}
```

第一个方法验证引发的异常的详细信息，第二个方法验证异常的类型。

## 7. 测试套件

现在我们来看看在一个测试套件中聚合多个测试类的概念，以便我们可以同时运行这些测试类。
JUnit 5提供了两个注解@SelectPackages和@SelectClasses来创建测试套件。

首先看看第一个:

```java

@Suite
@SelectPackages("cn.tuyucheng.taketoday")
public class AllUnitTests {

}
```

@SelectPackage用于指定运行测试套件时要选择的包的名称。
在我们的示例中，它会运行"cn.tuyucheng.taketoday"包下的所有测试。第二个注解@SelectClasses用于指定运行测试套件时要选择的类:

```java

@Suite
@SelectClasses({FirstUnitTests.class, ExceptionUnitTests.class})
public class AllUnitTests {

}
```

例如，上面的类将创建一个包含两个测试类的套件。这些类不必在同一个包中。

## 8. 动态测试

最后我们介绍一下JUnit 5中的动态测试特性，它允许我们声明和运行在运行时生成的测试用例。
静态测试在编译时定义固定数量的测试用例，而动态测试允许我们在运行时动态定义测试用例。

动态测试可以通过带有@TestFactory注解的工厂方法生成。让我们看一下代码：

```java
public class DynamicTests {
    private final List<String> in = new ArrayList<>(Arrays.asList("Hello", "Yes", "No"));
    private final List<String> out = new ArrayList<>(Arrays.asList("你好", "是", "否"));

    @TestFactory
    public Stream<DynamicTest> translateDynamicTestsFromStream() {
        return in.stream().map(word -> DynamicTest.dynamicTest("Test translate " + word, () -> {
                    int id = in.indexOf(word);
                    assertEquals(out.get(id), translate(word));
                }
        ));
    }

    private String translate(String word) {
        if ("Hello".equals(word))
            return "你好";
        else if ("Yes".equals(word))
            return "是";
        else if ("No".equals(word))
            return "否";
        return "Error";
    }
}
```

我们使用两个ArrayList来翻译单词，分别命名为in和out。
使用@TestFactory注解标注的方法必须返回Stream，Collection，Iterable或Iterator。在我们的例子中，我们返回Java 8中的Stream。

同时，@TestFactory标注的方法不能是私有的或静态的。测试的数量是动态的，它取决于ArrayList的大小。

## 9. 总结

在本文中，我们整体介绍了JUnit 5相较于Junit 4的变化。

我们介绍了JUnit 5架构在platform launcher、IDE、其他单元测试框架、与构建工具的集成等方面的重大变化。
此外，JUnit 5 与Java 8的集成度更高，尤其是与lambdas和Stream的支持。