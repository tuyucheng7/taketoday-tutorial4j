## 1. 概述

在本文中，我们将介绍TestNG测试框架。

我们将重点介绍：框架设置、编写简单的测试用例和配置、测试执行、测试报告生成和并发测试执行。

## 2. 设置

让我们首先在pom.xml文件中添加Maven依赖项：

```xml
<dependency>
    <groupId>org.testng</groupId>
    <artifactId>testng</artifactId>
    <version>7.1.0</version>
    <scope>test</scope>
</dependency>
```

最新版本可以在[Maven仓库](https://central.sonatype.com/artifact/org.testng/testng/7.7.1)中找到。

使用Eclipse时，可以从[Eclipse Marketplace](https://marketplace.eclipse.org/)下载并安装TestNG插件。

## 3. 编写测试用例

要使用TestNG编写测试，我们只需要使用org.testng.annotations.Test注解对测试方法进行标注：

```java
@Test
public void givenNumber_whenEven_thenTrue() {
    assertTrue(number % 2 == 0);
}
```

## 4. 测试配置

在编写测试用例时，我们通常需要在测试执行之前执行一些配置或初始化指令，以及在测试完成后进行一些清理。TestNG在方法、类、组和套件级别提供了许多初始化和清理功能：

```java
@BeforeClass
public void setup() {
    number = 12;
}

@AfterClass
public void tearDown() {
    number = 0;
}
```

使用@BeforeClass注解标注的setup()方法将在执行该测试类的任何方法之前被调用，并且在执行该测试类的所有方法之后调用tearDown()。

同样，我们可以在方法、组、测试和套件级别的任何配置中使用@BeforeMethod/@AfterMethod、@Before/AfterGroup、@Before/AfterTest和@Before/AfterSuite注解。

## 5. 测试执行

我们可以使用Maven的“test”命令运行测试用例，它将执行所有带有@Test注解的测试用例，并将它们放入默认的测试套件中。我们还可以使用[maven-surefire-plugin](https://central.sonatype.com/artifact/org.apache.maven.plugins/maven-surefire-plugin/3.0.0)从TestNG测试套件XML文件运行测试用例：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>
    <configuration>
        <suiteXmlFiles>
            <suiteXmlFile>
                src\test\resources\test_suite.xml
            </suiteXmlFile>
        </suiteXmlFiles>
    </configuration>
</plugin>
```

请注意，如果我们有多个涵盖所有测试用例的XML文件，我们可以将它们全部添加到suiteXmlFiles标签中：

```xml
<suiteXmlFiles>
    <suiteXmlFile>
        src/test/resources/parametrized_test.xml
    </suiteXmlFile>
    <suiteXmlFile>
        src/test/resources/registration_test.xml
    </suiteXmlFile>
</suiteXmlFiles>
```

为了独立运行测试，我们需要在类路径中包含TestNG库和编译的测试类以及XML配置文件：

```shell
java org.testng.TestNG test_suite.xml
```

## 6. 分组测试

测试可以分组运行，例如，在50个测试用例中，可以将每15个组合在一起并执行，而其他测试用例保持不变。

在TestNG中，套件中的分组测试是使用XML文件完成的：

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

请注意，两个测试类RegistrationTest、SignInTest现在属于同一个套件，一旦执行套件，该类中的测试用例将被执行。

除了测试套件，我们还可以在TestNG中创建测试组，而不是测试类方法被组合在一起。为此，请在@Test注解中添加groups参数：

```java
@Test(groups = "regression")
public void givenNegativeNumber_sumLessthanZero_thenCorrect() {
    int sum = numbers.stream().reduce(0, Integer::sum);
 
    assertTrue(sum < 0);
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

这将在SummationServiceTest类中执行标记为regression组的测试方法。

## 7. 参数化测试

参数化单元测试用于在多种条件下测试相同的代码。借助参数化单元测试，我们可以设置一个从某个数据源获取数据的测试方法。主要思想是使单元测试方法可重用并使用不同的输入集进行测试。

在TestNG中，我们可以使用@Parameter或@DataProvider注解对测试进行参数化。在使用XML文件时，使用@Parameter标注测试方法：

```java
@Test
@Parameters({"value", "isEven"})
public void givenNumberFromXML_ifEvenCheckOK_thenCorrect(int value, boolean isEven) {
    assertEquals(isEven, value % 2 == 0);
}
```

并使用XML文件提供数据：

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

使用XML文件中的数据很有用，但我们通常需要更复杂的数据。@DataProvider注解用于处理这些场景，可用于映射复杂的参数类型以供测试方法使用。@DataProvider用于原始数据类型：

```java
@DataProvider(name = "numbers")
public static Object[][] evenNumbers() {
    return new Object[][]{{1, false}, {2, true}, {4, true}};
}
 
@Test(dataProvider = "numbers")
public void givenNumberFromDataProvider_ifEvenCheckOK_thenCorrect(Integer number, boolean expected) {    
    assertEquals(expected, number % 2 == 0);
}
```

@DataProvider用于对象类型：

```java
@Test(dataProvider = "numbersObject")
public void givenNumberObjectFromDataProvider_ifEvenCheckOK_thenCorrect(EvenNumber number) {  
    assertEquals(number.isEven(), number.getValue() % 2 == 0);
}
 
@DataProvider(name = "numbersObject")
public Object[][] parameterProvider() {
    return new Object[][]{{new EvenNumber(1, false)}, {new EvenNumber(2, true)}, {new EvenNumber(4, true)}};
}
```

使用它，可以在测试中创建和使用任何必须测试的对象。这对于集成测试用例非常有用。

## 8. 忽略测试用例

我们有时希望在开发过程中暂时不执行某个测试用例。这可以通过在@Test注解中添加enabled=false来完成：

```java
@Test(enabled=false)
public void givenNumbers_sumEquals_thenCorrect() { 
    int sum = numbers.stream.reduce(0, Integer::sum);
    assertEquals(6, sum);
}
```

## 9. 依赖性测试

让我们考虑一个场景，如果初始测试用例失败，所有后续测试用例都应该被执行，而不是被标记为跳过。TestNG通过@Test注解的dependsOnMethods参数提供了这个功能：

```java
@Test
public void givenEmail_ifValid_thenTrue() {
    boolean valid = email.contains("@");
 
    assertEquals(valid, true);
}
 
@Test(dependsOnMethods = {"givenEmail_ifValid_thenTrue"})
public void givenValidEmail_whenLoggedIn_thenTrue() {
    LOGGER.info("Email {} valid >> logging in", email);
}
```

请注意，登录测试用例依赖于电子邮件验证测试用例。因此，如果电子邮件验证失败，将跳过登录测试。

## 10. 并发测试执行

TestNG允许测试以并行或多线程模式运行，从而提供了一种测试这些多线程代码片段的方法。

你可以将方法、类和套件配置为在它们自己的线程中运行，从而减少总执行时间。

### 10.1 并行的类和方法

要并行运行测试类，请在XML配置文件的suite标签中指定parallel属性，并通过classes属性指定测试类：

```xml
<suite name="suite" parallel="classes" thread-count="2">
    <test name="test suite">
        <classes>
            <class name="cn.tuyucheng.taketoday.RegistrationTest"/>
            <class name="cn.tuyucheng.taketoday.SignInTest"/>
        </classes>
    </test>
</suite>
```

请注意，如果我们在XML文件中有多个test标签，这些测试也可以通过指定parallel="tests"并行运行。同样要并行执行单个方法，请指定parallel="methods"。

### 10.2 测试方法的多线程执行

假设我们需要测试代码在多个线程中运行时的行为。TestNG允许在多个线程中运行一个测试方法：

```java
public class MultiThreadedTests {

    @Test(threadPoolSize = 5, invocationCount = 10, timeOut = 1000)
    public void givenMethod_whenRunInThreads_thenCorrect() {
        int count = Thread.activeCount();

        assertTrue(count > 1);
    }
}
```

threadPoolSize表示该方法将在n个线程中运行，如上所述。invocationCount和timeOut表示测试将被执行多次，如果测试需要更多时间，则测试失败。

## 11. 功能测试

TestNG具有可用于功能测试的功能。与Selenium结合使用，它既可以用于测试Web应用程序的功能，也可以用于使用[HttpClient](https://hc.apache.org/httpcomponents-client-ga/index.html)测试Web服务。

有关使用Selenium和TestNG进行功能测试的更多详细信息，请参见[此处](https://www.cn.tuyucheng.taketoday/java-selenium-with-junit-and-testng)。[本文](https://www.cn.tuyucheng.taketoday/integration-testing-a-rest-api)中还有一些关于集成测试的内容。

## 12. 总结

在本文中，我们快速介绍了如何设置TestNG并执行简单的测试用例、生成报告、测试用例的并发执行以及一些关于函数式编程的知识。有关依赖测试、忽略测试用例、测试组和套件等更多功能，你可以在此处参考我们的[JUnit与TestNG](https://www.cn.tuyucheng.taketoday/junit-vs-testng)文章。