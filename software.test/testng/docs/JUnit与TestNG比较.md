## 一、概述

JUnit 和 TestNG 无疑是 Java 生态系统中最流行的两个单元测试框架。虽然 JUnit 激发了 TestNG 本身，但它提供了其独特的功能，并且与 JUnit 不同，它适用于功能测试和更高级别的测试。

在这篇文章中，我们将通过介绍它们的特性和常见用例来讨论和比较这些框架。

## 2. 测试设置

在编写测试用例时，我们经常需要在测试执行之前执行一些配置或初始化指令，以及在测试完成后进行一些清理。让我们在这两个框架中评估这些。

JUnit 在每个方法和类之前和之后提供两个级别的初始化和清理。我们 在方法级别有@BeforeEach、@AfterEach注释，在类级别有@BeforeAll和@AfterAll：

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

请注意，此示例使用 JUnit 5。在之前的 JUnit 4 版本中，我们需要使用 等效于@BeforeEach 和@AfterEach 的@Before和@After注释 。 同样，@BeforeAll和@AfterAll是JUnit 4 的 @BeforeClass和@AfterClass 的替代品。

与 JUnit 类似，TestNG 也提供方法和类级别的初始化和清理。虽然@BeforeClass和@AfterClass在类级别保持不变，但方法级别的注解是@BeforeMethod和@AfterMethod：

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

TestNG 还为套件和组级别的配置提供@BeforeSuite、@AfterSuite、@BeforeGroup 和@AfterGroup注释：

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

此外，如果我们需要在 TestNG XML 配置文件中的<test>标记中包含的测试用例之前或之后进行任何配置，我们可以使用@BeforeTest和@AfterTest ：

```xml
<test name="test setup">
    <classes>
        <class name="SummationServiceTest">
            <methods>
                <include name="givenNumbers_sumEquals_thenCorrect" />
            </methods>
        </class>
    </classes>
</test>
```

注意@BeforeClass和@AfterClass方法的声明在JUnit 中必须是静态的。相比之下，TestNG 方法声明没有这些约束。

## 3. 忽略测试

两个框架都支持忽略测试用例，尽管它们的做法完全不同。JUnit 提供了 @Ignore注解：

```java
@Ignore
@Test
public void givenNumbers_sumEquals_thenCorrect() {
    int sum = numbers.stream().reduce(0, Integer::sum);
    Assert.assertEquals(6, sum);
}
```

而 TestNG 使用带有布尔值true或false的参数“启用”的@Test：

```java
@Test(enabled=false)
public void givenNumbers_sumEquals_thenCorrect() {
    int sum = numbers.stream.reduce(0, Integer::sum);
    Assert.assertEquals(6, sum);
}
```

## 4. 一起运行测试

在JUnit和 TestNG中都可以将测试作为一个集合一起运行 ，但是它们以不同的方式进行。

我们可以使用 @Suite、 @SelectPackages和@SelectClasses注释对测试用例进行分组，并在JUnit 5中将它们作为一个套件运行。套件是一组测试用例，我们可以将它们组合在一起并作为单个测试运行。

如果我们想将不同包的测试用例分组在一个套件 中一起运行，我们需要 @SelectPackages注释：

```java
@Suite
@SelectPackages({ "org.baeldung.java.suite.childpackage1", "org.baeldung.java.suite.childpackage2" })
public class SelectPackagesSuiteUnitTest {

}
```

如果我们希望特定的测试类一起运行，JUnit 5通过 @SelectClasses提供了灵活性：

```java
@Suite
@SelectClasses({Class1UnitTest.class, Class2UnitTest.class})
public class SelectClassesSuiteUnitTest {

}
```

以前使用JUnit 4 ，我们使用@RunWith和@Suite 注释实现了对多个测试的分组和运行：

```java
@RunWith(Suite.class)
@Suite.SuiteClasses({ RegistrationTest.class, SignInTest.class })
public class SuiteTest {

}
```

在 TestNG 中，我们可以使用 XML 文件对测试进行分组：

```xml
<suite name="suite">
    <test name="test suite">
        <classes>
            <class name="com.baeldung.RegistrationTest" />
            <class name="com.baeldung.SignInTest" />
        </classes>
    </test>
</suite>
```

这表明RegistrationTest和SignInTest将一起运行。

除了对类进行分组外，TestNG 还可以使用@Test(groups=”groupName”)注解对方法进行分组：

```java
@Test(groups = "regression")
public void givenNegativeNumber_sumLessthanZero_thenCorrect() {
    int sum = numbers.stream().reduce(0, Integer::sum);
    Assert.assertTrue(sum < 0);
}
```

让我们使用 XML 来执行组：

```xml
<test name="test groups">
    <groups>
        <run>
            <include name="regression" />
        </run>
    </groups>
    <classes>
        <class 
          name="com.baeldung.SummationServiceTest" />
    </classes>
</test>
```

这将执行带有 group regression标记的测试方法。

## 5. 测试异常

JUnit 和 TestNG 都提供使用注解测试异常的功能。

让我们首先创建一个带有抛出异常的方法的类：

```java
public class Calculator {
    public double divide(double a, double b) {
        if (b == 0) {
            throw new DivideByZeroException("Divider cannot be equal to zero!");
        }
        return a/b;
    }
}
```

在JUnit 5中，我们可以使用 assertThrows API 来测试异常：

```java
@Test
public void whenDividerIsZero_thenDivideByZeroExceptionIsThrown() {
    Calculator calculator = new Calculator();
    assertThrows(DivideByZeroException.class, () -> calculator.divide(10, 0));
}
```

在JUnit 4 中， 我们可以通过在测试 API 上使用 @Test(expected = DivideByZeroException.class) 来实现这一点。

使用 TestNG，我们也可以实现相同的功能：

```java
@Test(expectedExceptions = ArithmeticException.class) 
public void givenNumber_whenThrowsException_thenCorrect() { 
    int i = 1 / 0;
}
```

此功能意味着从一段代码中抛出什么异常，这是测试的一部分。

## 6. 参数化测试

参数化单元测试有助于在多种条件下测试相同的代码。借助参数化单元测试，我们可以设置一个从某个数据源获取数据的测试方法。主要思想是使单元测试方法可重用并使用不同的输入集进行测试。

在JUnit 5中，我们的优势在于测试方法直接从配置的源中使用数据参数。默认情况下，JUnit 5 提供了一些 源注释，例如：

-   @ValueSource：我们可以将它与Short、Byte、Int、Long、Float、Double、Char和String 类型的值数组一起使用：

```java
@ParameterizedTest
@ValueSource(strings = { "Hello", "World" })
void givenString_TestNullOrNot(String word) {
    assertNotNull(word);
}
```

-   @EnumSource – 将枚举 常量作为参数传递给测试方法：

```java
@ParameterizedTest
@EnumSource(value = PizzaDeliveryStrategy.class, names = {"EXPRESS", "NORMAL"})
void givenEnum_TestContainsOrNot(PizzaDeliveryStrategy timeUnit) {
    assertTrue(EnumSet.of(PizzaDeliveryStrategy.EXPRESS, PizzaDeliveryStrategy.NORMAL).contains(timeUnit));
}
```

-   @MethodSource – 传递外部方法生成流：

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

-   @CsvSource –使用 CSV 值作为参数的来源：

```java
@ParameterizedTest
@CsvSource({ "1, Car", "2, House", "3, Train" })
void givenCSVSource_TestContent(int id, String word) {
	assertNotNull(id);
	assertNotNull(word);
}
```

同样，如果我们需要从类路径和@ArgumentSource读取CSV 文件 来指定自定义、可重用的ArgumentsProvider，我们还有其他来源，例如@CsvFileSource 。

在JUnit 4中，必须使用@RunWith注释测试类以使其成为参数化类，并使用@Parameter来表示单元测试的参数值。

在 TestNG 中，我们可以使用@Parameter或@DataProvider注解对测试进行参数化。在使用 XML 文件时，使用 @Parameter 注释测试方法：

```java
@Test
@Parameters({"value", "isEven"})
public void 
  givenNumberFromXML_ifEvenCheckOK_thenCorrect(int value, boolean isEven) {
    Assert.assertEquals(isEven, value % 2 == 0);
}
```

并提供 XML 文件中的数据：

```xml
<suite name="My test suite">
    <test name="numbersXML">
        <parameter name="value" value="1"/>
        <parameter name="isEven" value="false"/>
        <classes>
            <class name="baeldung.com.ParametrizedTests"/>
        </classes>
    </test>
</suite>
```

虽然使用 XML 文件中的信息简单而有用，但在某些情况下，您可能需要提供更复杂的数据。

为此，我们可以使用 @DataProvider注释，它允许我们为测试方法映射复杂的参数类型。

下面是使用@DataProvider处理原始数据类型的示例：

```java
@DataProvider(name = "numbers")
public static Object[][] evenNumbers() {
    return new Object[][]{{1, false}, {2, true}, {4, true}};
}

@Test(dataProvider = "numbers")
public void givenNumberFromDataProvider_ifEvenCheckOK_thenCorrect
  (Integer number, boolean expected) {
    Assert.assertEquals(expected, number % 2 == 0);
}
```

对象 的@DataProvider ：

```java
@Test(dataProvider = "numbersObject")
public void givenNumberObjectFromDataProvider_ifEvenCheckOK_thenCorrect
  (EvenNumber number) {
    Assert.assertEquals(number.isEven(), number.getValue() % 2 == 0);
}

@DataProvider(name = "numbersObject")
public Object[][] parameterProvider() {
    return new Object[][]{{new EvenNumber(1, false)},
      {new EvenNumber(2, true)}, {new EvenNumber(4, true)}};
}
```

同样，可以使用数据提供程序创建和返回要测试的任何特定对象。它在与 Spring 等框架集成时很有用。

请注意，在 TestNG 中，由于@DataProvider方法不必是静态的，我们可以在同一个测试类中使用多个数据提供者方法。

## 7. 测试超时

超时测试是指，如果在某个指定的时间内没有完成执行，则测试用例应该失败。JUnit 和 TestNG 都支持超时测试。在JUnit 5中，我们可以将超时测试编写为：

```java
@Test
public void givenExecution_takeMoreTime_thenFail() throws InterruptedException {
    Assertions.assertTimeout(Duration.ofMillis(1000), () -> Thread.sleep(10000));
}
```

在JUnit 4和 TestNG 中，我们可以使用 @Test (timeout=1000)进行相同的测试

```java
@Test(timeOut = 1000)
public void givenExecution_takeMoreTime_thenFail() {
    while (true);
}
```

## 8. 依赖测试

TestNG 支持依赖测试。这意味着在一组测试方法中，如果初始测试失败，那么所有后续的依赖测试都将被跳过，不会像 JUnit 那样标记为失败。

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

在 JUnit 4 或 TestNG 中执行测试方法没有明确的隐含顺序。这些方法只是按照 Java 反射 API 返回的方式调用。从 JUnit 4 开始，它使用更具确定性但不可预测的顺序。

为了获得更多控制权，我们将使用@FixMethodOrder注释来注释测试类并提及方法排序器：

```java
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SortedTests {

    @Test
    public void a_givenString_whenChangedtoInt_thenTrue() {
        assertTrue(
          Integer.valueOf("10") instanceof Integer);
    }

    @Test
    public void b_givenInt_whenChangedtoString_thenTrue() {
        assertTrue(
          String.valueOf(10) instanceof String);
    }

}
```

MethodSorters.NAME_ASCENDING参数按方法名称按字典顺序对方法进行排序。除了这个排序器，我们还有MethodSorter.DEFAULT 和 MethodSorter.JVM。

虽然 TestNG 还提供了几种方法来控制测试方法的执行顺序。我们在@Test注解中提供了优先级参数：

```java
@Test(priority = 1)
public void givenString_whenChangedToInt_thenCorrect() {
    Assert.assertTrue(
      Integer.valueOf("10") instanceof Integer);
}

@Test(priority = 2)
public void givenInt_whenChangedToString_thenCorrect() {
    Assert.assertTrue(
      String.valueOf(23) instanceof String);
}
```

请注意，优先级基于优先级调用测试方法，但不保证在调用下一个优先级之前完成一个级别的测试。

有时在 TestNG 中编写功能测试用例时，我们可能会有一个相互依赖的测试，其中每个测试运行的执行顺序必须相同。为了实现这一点，我们应该使用dependsOnMethods参数来@Test注解，正如我们在前面部分中看到的那样。

## 10. 自定义测试名称

默认情况下，每当我们运行测试时，测试类和测试方法名称都会打印在控制台或 IDE 中。JUnit 5提供了一个独特的功能，我们可以使用@DisplayName注释为类和测试方法提及自定义描述性名称。

此注释不提供任何测试好处，但它也为非技术人员带来了易于阅读和理解的测试结果：

```java
@ParameterizedTest
@ValueSource(strings = { "Hello", "World" })
@DisplayName("Test Method to check that the inputs are not nullable")
void givenString_TestNullOrNot(String word) {
    assertNotNull(word);
}
```

每当我们运行测试时，输出将显示显示名称而不是方法名称。

目前，在TestNG中无法提供自定义名称。

## 11. 结论

JUnit 和 TestNG 都是用于在 Java 生态系统中进行测试的现代工具。

在本文中，我们快速浏览了使用这两个测试框架中的每一个编写测试的各种方法。