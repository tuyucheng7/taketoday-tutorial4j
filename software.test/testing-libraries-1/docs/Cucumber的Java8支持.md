## 1. 概述

在这个快速教程中，我们介绍如何在Cucumber中使用Java 8 lambda表达式。

## 2. Maven配置

首先，我们需要将以下依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>info.cukes</groupId>
    <artifactId>cucumber-java8</artifactId>
    <version>1.2.5</version>
    <scope>test</scope>
</dependency>
```

## 3. 使用Lambda定义步骤

接下来，我们讨论如何使用Java 8 lambda表达式编写步骤定义：

```java
public class ShoppingStepsDef implements En {

    private int budget = 0;

    public ShoppingStepsDef() {
        Given("I have (\\d+) in my wallet", (Integer money) -> budget = money);

        When("I buy .* with (\\d+)", (Integer price) -> budget -= price);

        Then("I should have (\\d+) in my wallet", (Integer finalBudget) -> assertEquals(budget, finalBudget.intValue()));
    }
}
```

我们以一个简单的购物功能为例：

```gherkin
Given("I have (\\d+) in my wallet", (Integer money) -> budget = money);
```

注意：

- 在这个步骤中，我们设置了初始budget，并有一个类型为Integer的参数money
- 由于我们的lambda体只包含一条语句，因此不需要花括号

## 4. 测试场景

最后来看看我们的测试场景：

```gherkin
Feature: Shopping

    Scenario: Track my budget 
        Given I have 100 in my wallet
        When I buy milk with 10
        Then I should have 90 in my wallet
    
    Scenario: Track my budget 
        Given I have 200 in my wallet
        When I buy rice with 20
        Then I should have 180 in my wallet
```

以及测试配置：

```java
@RunWith(Cucumber.class)
@CucumberOptions(features = { "classpath:features/shopping.feature" })
public class ShoppingIntegrationTest {
    // ...
}
```

有关Cucumber配置的更多详细信息，请查看[Cucumber和Scenario Outline]()教程。

## 5. 总结

在本文中我们学习了如何在Cucumber中使用Java 8 lambda表达式定义步骤。