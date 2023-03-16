## 1. 概述

JUnit和TestNG无疑是Java生态系统中最流行的两个单元测试框架。虽然JUnit激发了TestNG本身，但它提供了其独特的功能，并且与JUnit不同，它适用于功能测试和更高级别的测试。

在这篇文章中，**我们将通过介绍它们的功能和常见用例来讨论和比较这些框架**。

## 2. 测试设置

在编写测试用例时，我们通常需要在测试执行之前执行一些配置或初始化指令，以及在测试完成后进行一些清理。

**JUnit在每个方法和类之前和之后提供两个级别的初始化和清理**。我们在方法级别有@BeforeEach、@AfterEach注解，在类级别有@BeforeAll和@AfterAll：

```java
public class SummationServiceTest {

    private static List<Integer> numbers;

    @BeforeAll
    public static void initialize() {
        numbers = new ArrayList<>();
    }

    @AfterAll
    public static void tearDown() {
        numbers = null;
    }

    @BeforeEach
    public void runBeforeEachTest() {
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
    }

    @AfterEach
    public void runAfterEachTest() {
        numbers.clear();
    }

    @Test
    public void givenNumbers_sumEquals_thenCorrect() {
        int sum = numbers.stream().reduce(0, Integer::sum);
        assertEquals(6, sum);
    }
}
```

请注意，此示例使用JUnit 5。在之前的JUnit 4版本中，我们需要使用等效于@BeforeEach和@AfterEach的@Before和@After注解。同样，@BeforeAll和@AfterAll是JUnit 4的@BeforeClass和@AfterClass的替代品。

与JUnit类似，**TestNG也提供方法和类级别的初始化和清理**。虽然类级别的注解@BeforeClass和@AfterClass保持不变，但方法级别的注解是@BeforeMethod和@AfterMethod：

```java
@BeforeClass
public void initialize() {
    numbers = new ArrayList<>();
}

@AfterClass
public void tearDown() {
    numbers = null;
}

@BeforeMethod
public void runBeforeEachTest() {
    numbers.add(1);
    numbers.add(2);
    numbers.add(3);
}

@AfterMethod
public void runAfterEachTest() {
    numbers.clear();
}
```

**TestNG还为套件和组级别的配置提供@BeforeSuite、@AfterSuite、@BeforeGroup和@AfterGroup注解**：

```java
@BeforeGroups("positive_tests")
public void runBeforeEachGroup() {
    numbers.add(1);
    numbers.add(2);
    numbers.add(3);
}

@AfterGroups("negative_tests")
public void runAfterEachGroup() {
    numbers.clear(); 
}
```

此外，如果我们需要在TestNG XML配置文件中的<test\>标签中包含的测试用例之前或之后进行任何配置，我们可以使用@BeforeTest和@AfterTest：

```xml
<test name="test setup">
    <classes>
        <class name="SummationServiceTest">
            <methods>
                <include name="givenNumbers_sumEquals_thenCorrect"/>
            </methods>
        </class>
    </classes>
</test>
```

请注意，@BeforeClass和@AfterClass方法的声明在JUnit中必须是静态的。相比之下，TestNG方法声明没有这些约束。

## 3. 忽略测试

**这两个框架都支持忽略测试用例**，尽管它们的做法完全不同。JUnit提供了@Ignore注解：

```java
@Ignore
@Test
public void givenNumbers_sumEquals_thenCorrect() {
    int sum = numbers.stream().reduce(0, Integer::sum);
    Assert.assertEquals(6, sum);
}
```

而TestNG使用带有参数“enabled”的@Test，其值为为true或false：

```java
@Test(enabled=false)
public void givenNumbers_sumEquals_thenCorrect() {
    int sum = numbers.stream.reduce(0, Integer::sum);
    Assert.assertEquals(6, sum);
}
```

## 4. 运行多个测试

在JUnit和TestNG中都可以将测试作为一个集合一起运行，但是它们以不同的方式进行。

**我们可以使用@Suite、@SelectPackages和@SelectClasses注解对测试用例进行分组，并在JUnit 5中将它们作为一个套件运行**。套件是测试用例的集合，我们可以将它们组合在一起并作为单个测试运行。

如果我们想将不同包的测试用例分组在单个套件中一起运行，我们需要@SelectPackages注解：

```java
@Suite
@SelectPackages({ "cn.tuyucheng.taketoday.java.suite.childpackage1", "cn.tuyucheng.taketoday.java.suite.childpackage2" })
public class SelectPackagesSuiteUnitTest {
}
```

如果我们希望特定的测试类一起运行，JUnit 5通过@SelectClasses提供了灵活性：

```java
@Suite
@SelectClasses({Class1UnitTest.class, Class2UnitTest.class})
public class SelectClassesSuiteUnitTest {
}
```

以前使用JUnit 4，我们使用@RunWith和@Suite注解实现了对多个测试的分组和运行：

```java
@RunWith(Suite.class)
@Suite.SuiteClasses({ RegistrationTest.class, SignInTest.class })
public class SuiteTest {
}
```

**在TestNG中，我们可以使用XML文件对测试进行分组**：

```xml
<suite name="suite">
    <test name="test suite">
        <classes>
            <class name="cn.tuyucheng.taketoday.RegistrationTest"/>
            <class name="cn.tuyucheng.taketoday.SignInTest"/>
        </classes>
    </test>
</suite>
```

这表明RegistrationTest和SignInTest将一起运行。

除了对类进行分组外，TestNG还可以使用@Test(groups="groupName")注解对方法进行分组：

```java
@Test(groups = "regression")
public void givenNegativeNumber_sumLessthanZero_thenCorrect() {
    int sum = numbers.stream().reduce(0, Integer::sum);
    Assert.assertTrue(sum < 0);
}
```

让我们使用XML来执行组：

```xml
<test name="test groups">
    <groups>
        <run>
            <include name="regression"/>
        </run>
    </groups>
    <classes>
        <class
                name="cn.tuyucheng.taketoday.SummationServiceTest"/>
    </classes>
</test>
```

这将执行标记为regression组的测试方法。

## 5. 测试异常

**JUnit和TestNG都提供使用注解测试异常的功能**。

让我们首先创建一个包含抛出异常的方法的类：

```java
public class Calculator {
    public double divide(double a, double b) {
        if (b == 0) {
            throw new DivideByZeroException("Divider cannot be equal to zero!");
        }
        return a / b;
    }
}
```

在JUnit 5中，我们可以使用assertThrows API来测试异常：

```java
@Test
public void whenDividerIsZero_thenDivideByZeroExceptionIsThrown() {
    Calculator calculator = new Calculator();
    assertThrows(DivideByZeroException.class, () -> calculator.divide(10, 0));
}
```

在JUnit 4中，我们可以通过在测试API上使用@Test(expected=DivideByZeroException.class)来实现这一点。

使用TestNG，我们也可以实现相同的功能：

```java
@Test(expectedExceptions = ArithmeticException.class) 
public void givenNumber_whenThrowsException_thenCorrect() { 
    int i = 1 / 0;
}
```

此功能意味着从一段代码中抛出什么异常，这是测试的一部分。

## 6. 参数化测试

参数化单元测试有助于在多种条件下测试相同的代码。借助参数化单元测试，我们可以设置一个从某个数据源获取数据的测试方法。主要思想是使单元测试方法可重用并使用不同的输入集进行测试。

**在JUnit 5中，我们的优势在于测试方法直接从配置的源中使用数据参数**。默认情况下，JUnit 5提供了一些源注解，例如：

-   @ValueSource：我们可以将它与Short、Byte、Int、Long、Float、Double、Char和String类型的值数组一起使用：

    ```java
    @ParameterizedTest
    @ValueSource(strings = { "Hello", "World" })
    void givenString_TestNullOrNot(String word) {
        assertNotNull(word);
    }
    ```

-   @EnumSource：将枚举常量作为参数传递给测试方法：

    ```java
    @ParameterizedTest
    @EnumSource(value = PizzaDeliveryStrategy.class, names = {"EXPRESS", "NORMAL"})
    void givenEnum_TestContainsOrNot(PizzaDeliveryStrategy timeUnit) {
        assertTrue(EnumSet.of(PizzaDeliveryStrategy.EXPRESS, PizzaDeliveryStrategy.NORMAL).contains(timeUnit));
    }
    ```

-   @MethodSource：传递生成流的外部方法：

    ```java
    static Stream<String> wordDataProvider() {
        return Stream.of("foo", "bar");
    }
    
    @ParameterizedTest
    @MethodSource("wordDataProvider")
    void givenMethodSource_TestInputStream(String argument) {
        assertNotNull(argument);
    }
    ```

-   @CsvSource：使用CSV值作为参数的来源：

    ```java
    @ParameterizedTest
    @CsvSource({ "1, Car", "2, House", "3, Train" })
    void givenCSVSource_TestContent(int id, String word) {
    	assertNotNull(id);
    	assertNotNull(word);
    }
    ```

同样，如果我们需要从类路径和@ArgumentSource读取CSV文件来指定自定义、可重用的ArgumentsProvider，我们还有其他来源，例如@CsvFileSource。

在JUnit 4中，必须使用@RunWith标注测试类以使其成为参数化类，并使用@Parameter来表示单元测试的参数值。

**在TestNG中，我们可以使用@Parameter或@DataProvider注解对测试进行参数化**。在使用XML文件时，使用@Parameter标注测试方法：

```java
@Test
@Parameters({"value", "isEven"})
public void givenNumberFromXML_ifEvenCheckOK_thenCorrect(int value, boolean isEven) {
    Assert.assertEquals(isEven, value % 2 == 0);
}
```

并在XML文件中提供数据：

```xml
<suite name="My test suite">
    <test name="numbersXML">
        <parameter name="value" value="1"/>
        <parameter name="isEven" value="false"/>
        <classes>
            <class name="cn.tuyucheng.taketoday.ParametrizedTests"/>
        </classes>
    </test>
</suite>
```

虽然使用XML文件中的信息既简单又有用，但在某些情况下，你可能需要提供更复杂的数据。

为此，我们可以使用@DataProvider注解，它允许我们为测试方法映射复杂的参数类型。

下面是使用@DataProvider处理原始数据类型的示例：

```java
@DataProvider(name = "numbers")
public static Object[][] evenNumbers() {
    return new Object[][]{{1, false}, {2, true}, {4, true}};
}

@Test(dataProvider = "numbers")
public void givenNumberFromDataProvider_ifEvenCheckOK_thenCorrect (Integer number, boolean expected) {
    Assert.assertEquals(expected, number % 2 == 0);
}
```

以及使用@DataProvider处理对象类型的示例：

```java
@Test(dataProvider = "numbersObject")
public void givenNumberObjectFromDataProvider_ifEvenCheckOK_thenCorrect (EvenNumber number) {
    Assert.assertEquals(number.isEven(), number.getValue() % 2 == 0);
}

@DataProvider(name = "numbersObject")
public Object[][] parameterProvider() {
    return new Object[][]{{new EvenNumber(1, false)}, {new EvenNumber(2, true)}, {new EvenNumber(4, true)}};
}
```

同样，可以使用数据提供程序创建和返回要测试的任何特定对象。当与Spring等框架集成时，它很有用。

请注意，在TestNG中，由于@DataProvider方法不必是静态的，因此我们可以在同一个测试类中使用多个数据提供程序方法。

## 7. 测试超时

超时测试意味着，如果执行未在某个指定时间段内完成，则测试用例应该失败。JUnit和TestNG都支持超时测试。在JUnit 5中，我们可以将超时测试编写为：

```java
@Test
public void givenExecution_takeMoreTime_thenFail() throws InterruptedException {
    Assertions.assertTimeout(Duration.ofMillis(1000), () -> Thread.sleep(10000));
}
```

在JUnit 4和TestNG中，我们可以使用@Test(timeout=1000)进行相同的测试

```java
@Test(timeOut = 1000)
public void givenExecution_takeMoreTime_thenFail() {
    while (true);
}
```

## 8. 依赖测试

TestNG支持依赖测试。这意味着在一组测试方法中，如果初始测试失败，那么所有后续的依赖测试都将被跳过，而不会像JUnit那样标记为失败。

让我们看一个场景，我们需要验证电子邮件，如果成功，将继续登录：

```java
@Test
public void givenEmail_ifValid_thenTrue() {
    boolean valid = email.contains("@");
    Assert.assertEquals(valid, true);
}

@Test(dependsOnMethods = {"givenEmail_ifValid_thenTrue"})
public void givenValidEmail_whenLoggedIn_thenTrue() {
    LOGGER.info("Email {} valid >> logging in", email);
}
```

## 9. 测试执行顺序

**在JUnit 4或TestNG中执行测试方法没有明确的隐含顺序**。这些方法只是按照Java反射API返回的方式调用。从JUnit 4开始，它使用更具确定性但不可预测的顺序。

为了获得更多控制权，我们将使用@FixMethodOrder注解来标注测试类并指定MethodSorters：

```java
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SortedTests {

    @Test
    public void a_givenString_whenChangedtoInt_thenTrue() {
        assertTrue(Integer.valueOf("10") instanceof Integer);
    }

    @Test
    public void b_givenInt_whenChangedtoString_thenTrue() {
        assertTrue(String.valueOf(10) instanceof String);
    }
}
```

MethodSorters.NAME_ASCENDING参数按方法名称按字典顺序对方法进行排序。除了这个排序器，我们还有MethodSorter.DEFAULT和MethodSorter.JVM。

而TestNG还提供了几种方法来控制测试方法的执行顺序。我们在@Test注解中可以指定priority参数：

```java
@Test(priority = 1)
public void givenString_whenChangedToInt_thenCorrect() {
    Assert.assertTrue(Integer.valueOf("10") instanceof Integer);
}

@Test(priority = 2)
public void givenInt_whenChangedToString_thenCorrect() {
    Assert.assertTrue(String.valueOf(23) instanceof String);
}
```

请注意，优先级测试基于优先级调用测试方法，但不能保证在调用下一个优先级之前完成当前级别的测试。

有时在TestNG中编写功能测试用例时，我们可能会有一个相互依赖的测试，其中每个测试运行的执行顺序必须相同。为了实现这一点，我们应该在@Test注解中使用dependsOnMethods参数，正如我们在前面部分中看到的那样。

## 10. 自定义测试名称

默认情况下，每当我们运行测试时，测试类和测试方法名称都会打印在控制台或IDE中。JUnit 5提供了一个独特的功能，我们可以使用@DisplayName注解为类和测试方法提供自定义描述性名称。

此注解不提供任何测试优势，但它可以为非技术人员带来易于阅读和理解的测试结果：

```java
@ParameterizedTest
@ValueSource(strings = { "Hello", "World" })
@DisplayName("Test Method to check that the inputs are not nullable")
void givenString_TestNullOrNot(String word) {
    assertNotNull(word);
}
```

每当我们运行测试时，输出将显示"Test Method to check that the inputs are not nullable"而不是方法名称。

目前，在TestNG中无法提供自定义名称。

## 11. 总结

JUnit和TestNG都是用于在Java生态系统中进行测试的现代工具。

在本文中，我们快速浏览了使用这两个测试框架中的每一个编写测试的各种方法。