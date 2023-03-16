## 1. 简介

Cucumber是一个BDD(行为驱动开发)测试框架。

使用Cucumber框架编写具有不同输入/输出排列的重复场景可能非常耗时，难以维护，当然也令人沮丧。

Cucumber提供了一个解决方案，通过结合使用Scenario Outline和Examples的概念来减少这种工作量。在下面的部分中，我们尝试通过一个例子，看看我们如何最大限度地减少这种重复。

如果你想了解更多有关BDD和Gherkin语言的信息，请查看[这篇](https://www.baeldung.com/cucumber-rest-api-testing)文章。

## 2. 添加Cucumber支持

要在简单的Maven项目中添加对Cucumber的支持，我们需要添加以下依赖项：

```xml
<dependency>
    <groupId>info.cukes</groupId>
    <artifactId>cucumber-junit</artifactId>
    <version>1.2.5</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>info.cukes</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>1.2.5</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest-library</artifactId>
    <version>1.3</version>
    <scope>test</scope>
</dependency>
```

指向Maven Central依赖项的有用链接：[cucumber-junit](https://central.sonatype.com/artifact/io.cucumber/cucumber-junit/7.11.1)、[cucumber-java](https://central.sonatype.com/artifact/io.cucumber/cucumber-java/7.11.1)、[hamcrest-library](https://central.sonatype.com/artifact/org.hamcrest/hamcrest-library/2.2)

由于这些是测试库，因此它们不需要与实际的可部署文件一起交付-这就是为什么它们都是test范围的。

## 3. 一个简单的例子

让我们演示一种臃肿的方式和一种简洁的方式来编写Feature文件。首先定义我们要为其编写测试的逻辑：

```java
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
}
```

## 4. 定义Cucumber测试

### 4.1 定义Feature文件

```gherkin
Feature: Calculator
    As a user
    I want to use a calculator to add numbers
    So that I don't need to add myself

    Scenario: Add two numbers -2 & 3
        Given I have a calculator
        When I add -2 and 3
        Then the result should be 1

    Scenario: Add two numbers 10 & 15
        Given I have a calculator
        When I add 10 and 15
        Then the result should be 25
```

如上面所见，这里使用了两种不同的数字组合来测试add方法逻辑。除了数字之外，所有场景都完全相同。

### 4.2 “Glue”代码(步骤定义)

为了测试这些场景，我们必须使用相应的代码定义每个步骤，以便将语句转换为功能代码段：

```java
public class CalculatorRunSteps {

    private int total;

    private Calculator calculator;

    @Before
    private void init() {
        total = -999;
    }

    @Given("^I have a calculator$")
    public void initializeCalculator() throws Throwable {
        calculator = new Calculator();
    }

    @When("^I add (-?\\d+) and (-?\\d+)$")
    public void testAdd(int num1, int num2) throws Throwable {
        total = calculator.add(num1, num2);
    }

    @Then("^the result should be (-?\\d+)$")
    public void validateResult(int result) throws Throwable {
        Assert.assertThat(total, Matchers.equalTo(result));
    }
}
```

### 4.3 Runner类

为了集成Feature和步骤定义代码，我们可以使用JUnit Runner：

```java
@RunWith(Cucumber.class)
@CucumberOptions(
      features = {"classpath:features/calculator.feature"},
      glue = {"cn.tuyucheng.taketoday.cucumber.calculator"})
public class CalculatorTest {
}
```

## 5. 使用Scenario Outlines重写Feature文件

我们在4.1节中看到，以这种简单的方式定义Feature文件可能是一项耗时且更容易出错的任务。而使用Scenario Outline可以将相同的Feature文件以更简洁的方式编写：

```gherkin
Feature: Calculator
    As a user
    I want to use a calculator to add numbers
    So that I don't need to add myself

    Scenario Outline: Add two numbers <num1> & <num2>
        Given I have a calculator
        When I add <num1> and <num2>
        Then the result should be <total>

        Examples:
            | num1 | num2 | total |
            | -2   | 3    | 1     |
            | 10   | 15   | 25    |
            | 99   | -99  | 0     |
            | -1   | -10  | -11   |
```

将常规Scenario Definition与Scenario Outline进行比较时，不再需要在步骤定义中对值进行硬编码。在步骤定义本身中，值被参数替换为<parameter_name\>。

在Scenario Outline的后半部分，使用Examples以竖线分隔的表格格式定义值。如下所示：

```gherkin
Examples:
    | Parameter_Name1 | Parameter_Name2 |
    | Value-1 | Value-2 |
    | Value-X | Value-Y |
```

## 6. 总结

通过这篇简短的文章，我们展示了如何在本质上使场景变得通用。并且还可以减少编写和维护这些场景的工作量。