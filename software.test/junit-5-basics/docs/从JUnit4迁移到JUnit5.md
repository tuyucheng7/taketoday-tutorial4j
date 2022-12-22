## 1. 概述

在本教程中，我们学习如何从JUnit 4迁移到最新的JUnit 5版本，并概述两个版本的JUnit之间的差异。

## 2. Junit 5的优势

JUnit 4一些明显的限制有：

+ 单个jar包包含整个框架。我们需要导入整个库，即使我们只需要一个特定的功能。
  **在JUnit 5中，我们可以做更细粒度的控制，并且可以只导入必要的内容**。
+ 在JUnit 4中一次只有一个测试Runner可以执行测试(例如SpringJUnit4ClassRunner或Parameterized)。
  **JUnit 5允许多个Runner同时工作**。
+ JUnit 4从未超越Java 7，错过了Java 8的许多特性。**JUnit 5很好地利用了Java 8特性**。

JUnit 5背后的想法是完全重写JUnit 4，以消除大多数这些缺点。

## 3. 差异

JUnit 4被划分为包括JUnit 5的模块：

+ **JUnit Platform** - 该模块涵盖了我们可能感兴趣的所有扩展框架：测试执行、发现和报告。
+ **JUnit Vintage** - 该模块允许向后兼容JUnit 4甚至JUnit 3。

### 3.1 注解

JUnit 5对其注解进行了重要更改。**最重要的一点是我们不能再使用@Test注解来指定expected属性**。

在JUnit 4中，我们可以根据以下方式指定expected属性来断言一个异常的抛出：

```java

public class AnnotationTestExampleUnitTest {

    @Test(expected = Exception.class)
    public void shouldRaiseAnException() throws Exception {
        throw new Exception("This is my expected exception");
    }
}
```

在Junit 5中我们可以使用assertThrows方法：

```java
class AnnotationTestExampleUnitTest {

    @Test
    void shouldRaiseAnException() {
        assertThrows(Exception.class, () -> {
            throw new Exception("This is my expected exception");
        });
    }
}
```

JUnit 4中的timeout属性：

```java
public class AnnotationTestExampleUnitTest {

    @Test(timeout = 1)
    @Ignore
    public void shouldFailBecauseTimeout() throws InterruptedException {
        Thread.sleep(10);
    }
}
```

在Junit 5中我们可以使用assertTimeout()方法：

```java
class AnnotationTestExampleUnitTest {

    @Test
    @Disabled
    void shouldFailBecauseTimeout() {
        assertTimeout(Duration.ofMillis(1), () -> Thread.sleep(10));
    }
}
```

以下是在JUnit 5中有所改动的一些其他注解：

+ @Before更换为@BeforeEach
+ @After更换为@AfterEach
+ @BeforeClass更换为@BeforeAll
+ @AfterClass更换为@AfterAll
+ @Ignore更换为@Disabled

### 3.2 Assertions

我们还可以在JUnit 5中使用lambda编写断言消息，允许惰性求值在需要时跳过复杂的消息构造：

```java
class AssertionsExampleUnitTest {

    @Test
    @Disabled
    void shouldFailBecauseTheNumbersAreNotEqual_lazyEvaluation() {
        assertTrue(2 == 3, () -> "Numbers " + 2 + " and " + 3 + " are not equal!");
    }
}
```

此外，我们可以在JUnit 5中对断言进行分组：

```java
class AssertionsExampleUnitTest {

    @Test
    void shouldAssertAllTheGroup() {
        List<Integer> list = Arrays.asList(1, 2, 3);

        assertAll("List is not incremental",
                () -> assertEquals(list.get(0).intValue(), 1),
                () -> assertEquals(list.get(1).intValue(), 2),
                () -> assertEquals(list.get(2).intValue(), 3)
        );
    }
}
```

### 3.3 Assumptions

新的Assumptions类现在位于org.junit.jupiter.api包中。
JUnit 5完全支持JUnit 4中现有的assumptions方法，并且还添加了一组新方法，允许我们仅在特定场景下运行一些断言：

```java
class AssumptionUnitTest {

    @Test
    void whenEnvironmentIsWeb_thenUrlsShouldStartWithHttp() {
        assumingThat("WEB".equals(System.getenv("ENV")), Assertions::fail);
    }
}
```

### 3.4  Tagging和Filtering

在JUnit 4中，我们可以使用@Category注解对测试进行分组。在JUnit 5中，@Category注解被@Tag注解取代：

```java

@Tag("junit5")
@Tag("annotations")
public class AnnotationTestExampleUnitTest {
    // ...
}
```

我们可以使用maven surefire插件包含/排除特定标签:

```xml

<build>
    <plugins>
        <plugin>
            <artifactId>maven-surefire-plugin</artifactId>
            <configuration>
                <properties>
                    <includeTags>junit5</includeTags>
                </properties>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 3.5 运行测试的新注解

在JUnit 4中，我们使用@RunWith注解将测试上下文与其他框架集成，或者更改测试用例中的整体执行流程。

在JUnit 5中，我们现在可以使用@ExtendWith注解来提供类似的功能。

例如，要使用JUnit 4中的Spring功能：

```java

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringTestConfiguration.class})
public class GreetingsSpringUnitTest {
    // ...
}
```

在JUnit 5中，它是一个简单的Extension：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringTestConfiguration.class})
public class GreetingsSpringUnitTest {
    // ...
}
```

### 3.6 新的Rule注解

在JUnit 4中，我们使用@Rule和@ClassRule注解向测试添加特殊功能。

在JUnit 5中，我们可以使用@ExtendWith注解实现相同的逻辑。

例如，假设我们在JUnit 4中有一个自定义Rule，用于在测试之前和之后编写日志记录：

```java
public class TraceUnitTestRule implements TestRule {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraceUnitTestRule.class);

    @Override
    public Statement apply(Statement base, Description description) {

        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                List<Throwable> errors = new ArrayList<>();

                LOGGER.debug("Starting test ... {}", description.getMethodName());
                try {
                    base.evaluate();
                } catch (Throwable e) {
                    errors.add(e);
                } finally {
                    LOGGER.debug("... test finished. {}", description.getMethodName());
                }

                MultipleFailureException.assertEmpty(errors);
            }
        };
    }
}
```

我们在一个测试类中使用它：

```java
public class RuleExampleUnitTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleExampleUnitTest.class);

    @Rule
    public final TraceUnitTestRule traceRuleTests = new TraceUnitTestRule();

    @Test
    public void whenTracingTests() {
        LOGGER.info("This is my test");
    }
}
```

在JUnit 5中，我们可以用更直观的方式编写相同的代码：

```java
public class TraceUnitExtension implements AfterEachCallback, BeforeEachCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraceUnitExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) {
        LOGGER.info("Starting test ... {}", context.getDisplayName());
    }

    @Override
    public void afterEach(ExtensionContext context) {
        LOGGER.info("... test finished. {}", context.getDisplayName());
    }
}
```

使用org.junit.jupiter.api.extension包中提供的JUnit 5的AfterEachCallback和BeforeEachCallback接口，我们可以在测试套件中轻松实现此Rule：

```java

@ExtendWith(TraceUnitExtension.class)
class RuleExampleUnitTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleExampleUnitTest.class);

    @Test
    void whenTracingTests() {
        LOGGER.info("This is my test");
    }
}
```

### 3.7 JUnit 5 Vintage

JUnit Vintage通过在JUnit 5上下文中运行JUnit 3或JUnit 4测试来帮助迁移JUnit测试。

我们可以通过导入junit-vintage-engine依赖来使用它：

```xml

<dependency>
    <groupId>org.junit.vintage</groupId>
    <artifactId>junit-vintage-engine</artifactId>
    <version>5.8.1</version>
    <scope>test</scope>
</dependency>
```

## 4. 总结

JUnit 5是JUnit 4框架的模块化和现代版。在本文中，我们介绍了这两个版本之间的主要区别，并介绍了如何从JUnit 4测试迁移到JUnit 5。