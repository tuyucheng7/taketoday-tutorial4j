## 1. 概述

JUnit和TestNG无疑是Java生态系统中最流行的两个单元测试框架。
虽然JUnit激发了TestNG本身的灵感，但它提供了其独特的功能，并且与JUnit不同，它适用于功能测试和更高级别的测试。

在这篇文章中，我们将通过介绍它们的特性和常见用例来讨论和比较这两个框架。

## 2. 项目构建

在编写测试用例时，我们经常需要在测试执行之前执行一些配置或初始化代码，并在测试完成后进行一些清理。

**JUnit在每个方法和类之前和之后提供两个级别的初始化和清理**。
我们在方法级别可以使用@BeforeEach、@AfterEach注解，在类级别可以使用@BeforeAll和@AfterAll：

```java
class SummationServiceUnitTest {
    private static List<Integer> numbers;

    @BeforeAll
    static void initialize() {
        numbers = new ArrayList<>();
    }

    @AfterAll
    static void tearDown() {
        numbers = null;
    }

    @BeforeEach
    void runBeforeEachTest() {
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
    }

    @AfterEach
    void runAfterEachTest() {
        numbers.clear();
    }

    @Test
    void givenNumbers_sumEquals_thenCorrect() {
        int sum = numbers.stream().reduce(0, Integer::sum);
        assertEquals(6, sum);
    }
}
```

请注意，这个例子使用JUnit 5。在之前的JUnit 4版本中，我们需要使用等效于@BeforeEach和@AfterEach的@Before和@After注解。
同样，@BeforeAll和@AfterAll是JUnit 4的@BeforeClass和@AfterClass的替代注解。

与JUnit类似，TestNG也提供方法和类级别的初始化和清理。
其中类级别的注解与Junit 4一样，分别是@BeforeClass和@AfterClass，而方法级别的注解是@BeforeMethod和@AfterMethod：

```
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

**TestNG还提供@BeforeSuite,@AfterSuite,@BeforeGroup和@AfterGroup注解,用于套件和组级别的配置**:

```
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

此外,如果我们需要在TestNG
xml配置文件的test标签中包含的测试用例之前或之后进行任何配置,我们可以使用@BeforeTest和@AfterTest:

```
<test name="test setup">
  <classes>
    <class name="SummationServiceIntegrationTest">
      <methods>
        <include name="givenNumbers_sumEquals_thenCorrect" />
      </methods>
    </class>
  </classes>
</test>
```

请注意,@BeforeClass和@AfterClass方法的声明在JUnit中必须是静态的.相比之下,TestNG方法声明没有这些约束.

## 3. 忽略测试

这两个框架都支持忽略测试用例,尽管它们的做法截然不同.JUnit提供了@Ignore注解:

```
@Ignore
@Test
public void givenNumbers_sumEquals_thenCorrect() {
  int sum = numbers.stream().reduce(0, Integer::sum);
  Assert.assertEquals(6, sum);
}
```

而TestNG将@Test与参数"enabled"一起使用,参数的布尔值为true或false:

```
@Test(enabled=false)
public void givenNumbers_sumEquals_thenCorrect() {
  int sum = numbers.stream.reduce(0, Integer::sum);
  Assert.assertEquals(6, sum);
}
```

## 4. 一起运行测试

在JUnit和TestNG中,将测试作为一个集合一起运行是可能的,但它们以不同的方式运行.

我们可以使用@Suite,@SelectPackages和@SelectClasses注解对测试用例进行分组,并将它们作为一个套件在JUnit
5中运行.套件是一组测试用例,我们可以将它们组合在一起,作为单个测试运行.

如果我们想将不同包的测试用例分组,以便在一个套件中一起运行,我们需要@SelectPackages注解:

```java

@Suite
@SelectPackages({"cn.tuyucheng.taketoday.java.suite.childpackage1", "cn.tuyucheng.taketoday.java.suite.childpackage2"})
public class SelectPackagesSuiteUnitTest {

}
```

如果我们希望特定的测试类一起运行,JUnit 5通过@SelectClasses提供了这种灵活的方式:

```java

@Suite
@SelectClasses({Class1UnitTest.class, Class2UnitTest.class})
public class SelectClassesSuiteUnitTest {

}
```

在之前使用JUnit 4时,我们使用@RunWith和@Suite注解实现了分组和同时运行多个测试:

```java

@RunWith(Suite.class)
@Suite.SuiteClasses({RegistrationTest.class, SignInTest.class})
public class SuiteTest {

}
```

**在TestNG中,我们可以使用xml文件对测试进行分组**:

```xml
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd" >
<suite name="regression_test">
    <test name="test groups">
        <groups>
            <run>
                <include name="regression"/>
            </run>
        </groups>
        <classes>
            <class name="cn.tuyucheng.taketoday.RegistrationTest"/>
            <class name="cn.tuyucheng.taketoday.SignInTest"/>
        </classes>
    </test>
</suite>
```

这表明RegistrationTest和SignInTest将一起运行.

除了分组类,TestNG还可以使用@Test(groups="groupName")注解对方法进行分组:

```
@Test(groups = "regression", enabled = false)
public void givenNumbers_sumEquals_thenCorrect() {
  int sum = numbers.stream().reduce(0, Integer::sum);
  Assert.assertEquals(sum, 6);
}
```

让我们使用xml来执行组:

```xml
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd" >
<suite name="regression_test">
    <test name="test groups">
        <groups>
            <run>
                <include name="regression"/>
            </run>
        </groups>
        <classes>
            <class name="cn.tuyucheng.taketoday.SummationServiceIntegrationTest"/>
        </classes>
    </test>
</suite>
```

这将执行标记为regression组的测试方法.

## 5. 测试异常

**JUnit和TestNG中都提供了使用注解测试异常的功能**.

让我们首先创建一个类,其中包含一个抛出异常的方法:

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

在JUnit 5中,我们可以使用assertThrows API测试异常:

```
@Test
public void whenDividerIsZero_thenDivideByZeroExceptionIsThrown() {
  Calculator calculator = new Calculator();
  assertThrows(DivideByZeroException.class, () -> calculator.divide(10, 0));
}
```

在JUnit4中,我们可以通过在测试API上使用@Test(expected=DivideByZeroException.class)来实现这一点.

通过TestNG,我们也可以实现同样的功能:

```
@Test(expectedExceptions = ArithmeticException.class) 
public void givenNumber_whenThrowsException_thenCorrect() { 
  int i = 1 / 0;
}
```

这个特性意味着从一段代码中抛出什么异常,这是测试的一部分.

## 6. 参数化测试

参数化单元测试有助于在多种条件下测试同一代码.在参数化单元测试的帮助下,我们可以建立一种从某些数据源获取数据的测试方法.其主要思想是使单元测试方法可重用,并使用不同的输入集进行测试.

在JUnit5中,我们的优势是测试方法直接从配置的源代码中使用数据参数.默认情况下,JUnit 5提供了一些源注解,如:

+ @ValueSource : 我们可以将其用于Short,Byte,Int,Long,Float,Double,Char和String类型的value数组:

```
@ParameterizedTest
@ValueSource(strings = { "Hello", "World" })
void givenString_TestNullOrNot(String word) {
  assertNotNull(word);
}
```

+ @EnumSource : 将枚举常量作为参数传递给测试方法:

```
@EnumSource(value = PizzaDeliveryStrategy.class, names = {"EXPRESS", "NORMAL"})
void givenEnum_TestContainsOrNot(PizzaDeliveryStrategy timeUnit) {
  assertTrue(EnumSet.of(PizzaDeliveryStrategy.EXPRESS, PizzaDeliveryStrategy.NORMAL).contains(timeUnit));
}
```

+ @MethodSource : 传递外部方法生成的Stream:

```
static Stream<String> wordDataProvider() {
  return Stream.of("foo", "bar");
}

@ParameterizedTest
@MethodSource("wordDataProvider")
void givenMethodSource_TestInputStream(String argument) {
  assertNotNull(argument);
}
```

+ @CsvSource : 使用CSV值作为参数的源:

```
@ParameterizedTest
@CsvSource({ "1, Car", "2, House", "3, Train" })
void givenCSVSource_TestContent(int id, String word) {
  assertNotNull(id);
  assertNotNull(word);
}
```

类似地,如果我们需要从类路径读取CSV文件,我们可以使用@CsvFileSource.@ArgumentSource指定一个自定义的,可重用的ArgumentsProvider.

在JUnit 4中,测试类必须用@RunWith注解,使其成为参数化类,@Parameter表示单元测试为参数化测试.

在TestNG中,我们可以使用@Parameter或@DataProvider注解对测试进行参数化.使用xml文件时,用@Parameter注解测试方法:

```
@Test
@Parameters({"value", "isEven"})
public void givenNumberFromXML_ifEvenCheckOK_thenCorrect(int value, boolean isEven) {
  Assert.assertEquals(isEven, value % 2 == 0);
}
```

并在xml文件中提供数据:

```
<test name="numbersXML">
  <parameter name="value" value="1"/>
  <parameter name="isEven" value="false"/>
  <classes>
    <class name="cn.tuyucheng.taketoday.ParametrizedLongRunningUnitTest"/>
  </classes>
</test>
```

虽然使用xml文件中的信息简单而有用,但在某些情况下,可能需要提供更复杂的数据.

为此,我们可以使用@DataProvider注解,它允许我们映射测试方法的复杂参数类型.

下面是一个将@DataProvider用于基本数据类型的示例:

```
@DataProvider(name = "numbers")
public static Object[][] evenNumbers() {
  return new Object[][]{{1, false}, {2, true}, {4, true}};
}

@Test(dataProvider = "numbers")
public void givenNumberFromDataProvider_ifEvenCheckOk_thenCorrect(Integer number, boolean expected) {
  assertEquals(expected, number % 2 == 0);
}
```

对象的@DataProvider:

```
@DataProvider(name = "numbersObject")
public Object[][] parameterProvider() {
  return new Object[][]{
      {new EvenNumber(1, false)},
      {new EvenNumber(2, true)},
      {new EvenNumber(4, true)}
  };
}

@Test(dataProvider = "numbersObject")
public void givenNumberObjectFromDataProvider_ifEvenCheckOK_thenCorrect(EvenNumber number) {
  assertEquals(number.isEven(), number.getValue() % 2 == 0);
}
```

同样,可以使用Data Provider创建并返回要测试的任何特定对象.它在与Spring等框架集成时非常有用.

注意,在TestNG中,由于@DataProvider方法不需要是静态的,所以我们可以在同一个测试类中使用多个数据提供方法.

## 7. 超时测试

超时测试意味着,如果执行没有在指定的时间内完成,测试用例应该失败.JUnit和TestNG都支持超时测试.在JUnit
5中,我们可以编写一个超时测试,如下所示:

```
@Test
public void givenExecution_takeMoreTime_thenFail() throws InterruptedException {
  assertTimeout(Duration.ofMillis(1000), () -> Thread.sleep(10000));
}
```

在JUnit4和TestNG中,我们可以使用@Test(timeout=1000)进行相同的测试:

```
@Test(timeOut = 1000)
public void givenExecution_takeMoreTime_thenFail() {
  while (true);
}
```

## 8. 依赖测试

TestNG支持依赖性测试.这意味着在一组测试方法中,如果初始测试失败,那么将跳过所有后续的依赖测试,而不是像JUnit那样标记为失败.

让我们来看一个场景,我们需要验证电子邮件,如果成功,将继续登录:

```
@Test
public void givenEmail_ifValid_thenTrue() {
  boolean valid = email.contains("@");
  assertEquals(valid, true);
}
@Test(dependsOnMethods = {"givenEmail_ifValid_thenTrue"})
public void givenValidEmail_whenLoggedIn_thenTrue(){
  LOGGER.info("Email {} valid >> logging in",email);
}
```

## 9. 测试执行顺序

JUnit 4或TestNG中没有定义测试方法执行的隐式顺序.这些方法仅在Java反射API返回时调用.自JUnit4以来,它使用了一种更具确定性但不可预测的顺序.

为了更好地控制,我们将使用@FixMethodOrder注解对测试类进行标注,并给定一个MethodSorter:

```java

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SortedTests {

    @Test
    public void a_givenString_whenChangedtoInt_thenTrue() {
        Integer.valueOf("10");
        assertTrue(true);
    }

    @Test
    public void b_givenInt_whenChangedtoString_thenTrue() {
        assertTrue(true);
    }
}
```

MethodSorters.NAME_ASCENDING参数按方法名称的字典顺序对方法进行排序.除了这个排序器,我们还有MethodSorter.DEFAULT和MethodSorter.JVM.

TestNG还提供了几种方法来控制测试方法的执行顺序.我们在@Test注解中提供priority参数:

```
@Test(priority = 1)
public void givenString_whenChangedToInt_thenCorrect() {
  Assert.assertTrue(Integer.valueOf("10") instanceof Integer);
}

@Test(priority = 2)
public void givenInt_whenChangedToString_thenCorrect() {
  Assert.assertTrue(String.valueOf(23) instanceof String);
}
```

请注意,优先级会根据优先级调用测试方法,但不能保证在调用下一个优先级之前完成一个级别的测试.

有时,在TestNG中编写功能测试用例时,我们可能会有一个相互依赖的测试,其中每个测试运行的执行顺序必须相同.为了实现这一点,我们应该使用@Test注解的dependsOnMethods参数,正如我们在前面的部分中看到的那样.

## 10. 自定义测试名

默认情况下,无论何时运行测试,测试类和测试方法名称都会打印在控制台或IDE中.JUnit5提供了一个独特的特性,我们可以使用@DisplayName注解为类和测试方法提供自定义的描述性名称.

此注解没有提供任何测试好处,但它也带来了易于阅读和理解的测试结果:

```
@ParameterizedTest
@ValueSource(strings = { "Hello", "World" })
@DisplayName("Test Method to check that the inputs are not nullable")
void givenString_TestNullOrNot(String word) {
  assertNotNull(word);
}
```

无论何时运行测试,输出都将显示display name而不是方法名称.

现在,在TestNG中无法提供自定义名称.
