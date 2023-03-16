## 1. 简介

当我们想要为每个场景或步骤执行特定操作时，[Cucumber](https://www.baeldung.com/cucumber-scenario-outline)的钩子可以派上用场，并且这些钩子不是定义在Feature文件代码中。

在本教程中，我们将介绍Cucumber中的@Before、@BeforeStep、@AfterStep和@After钩子。

## 2. Cucumber中的钩子概述

### 2.1 什么时候应该使用钩子？

钩子可用于执行不属于业务功能的后台任务。此类任务可能是：

- 启动浏览器
- 设置或清除cookie
- 连接到数据库
- 检查系统状态
- 监控

监控的一个用例是使用实时测试进度更新仪表板。

钩子在Gherkin代码中是不可见的。因此，**我们不应该将它们视为[Cucumber Background](https://www.baeldung.com/java-cucumber-background)或Given步骤的替代品**。

我们将演示一个示例，其中我们在测试执行期间使用钩子截取屏幕截图。

### 2.2 钩子范围

**钩子会影响每个场景**。因此，最好在专用配置类中定义所有钩子。

没有必要在每个步骤定义代码类中定义相同的钩子。如果我们将步骤定义代码与钩子定义在同一个类中，则代码的可读性就会降低。

## 3. 钩子

让我们先看一下各个钩子。然后，我们将查看一个完整的示例，其中我们将看到钩子在组合时如何执行。

### 3.1 @Before

使用@Before标注的方法将**在每个场景之前执行**，注意该注解来自Cucumber库而不是JUnit。在我们的示例中，我们将在每个场景之前启动浏览器：

```java
@Before
public void initialization() {
    startBrowser();
}
```

**如果我们使用@Before标注多个方法，我们可以显式定义步骤的执行顺序**：

```java
@Before(order = 2)
public void beforeScenario() {
    takeScreenshot();
}
```

上面的方法在initialization方法之后执行，因为我们将2作为order参数的值传递给注解。我们还可以将1作为initialization方法的order参数的值：

```java
@Before(order = 1)
public void initialization()
```

因此，当我们执行一个场景时，initialization()首先执行，然后执行beforeScenario()。

### 3.2 @BeforeStep

使用@BeforeStep标注的方法在**每个步骤之前执行**。假设我们想在每个步骤执行之前截图，我们可以使用@BeforeStep标注beforeStep()方法：

```java
@BeforeStep
public void beforeStep() {
    takeScreenshot();
}
```

### 3.3 @AfterStep

使用@AfterStep标注的方法**在每个步骤之后执行**：

```java
@AfterStep
public void afterStep() {
    takeScreenshot();
}
```

我们在这里使用@AfterStep在每一步之后截取屏幕截图。**无论步骤是成功完成还是失败，都会执行这个钩子**。

### 3.4 @After

使用@After注解的方法**在每个场景之后执行**：

```java
@After
public void afterScenario() {
    takeScreenshot();
    closeBrowser();
}
```

在我们的示例中，我们将截取最终屏幕截图并关闭浏览器。**无论场景是否成功完成，都会执行这个钩子**。

### 3.5 Scenario参数

使用钩子注解标注的方法可以接收Scenario类型的参数：

```java
@After
public void beforeScenario(Scenario scenario) { 
    // some code
}
```

Scenario类型的对象包含有关当前场景的信息。包括场景名称、步骤数、步骤名称和状态(通过或失败)。**如果我们想对通过和失败的测试执行不同的操作，这会很有用**。

## 4. 钩子执行

### 4.1 快乐流

现在让我们看看当我们使用所有四种类型的钩子运行Cucumber场景时会发生什么：

```gherkin
Feature: Book Store With Hooks

    Background: The Book Store
        Given The following books are available in the store
            | The Devil in the White City          | Erik Larson |
            | The Lion, the Witch and the Wardrobe | C.S. Lewis  |
            | In the Garden of Beasts              | Erik Larson |

    Scenario: 1 - Find books by author
        When I ask for a book by the author Erik Larson
        Then The salesperson says that there are 2 books

    Scenario: 2 - Find books by author, but isn't there
        When I ask for a book by the author Marcel Proust
        Then The salesperson says that there are 0 books
```

查看IntelliJ IDEA中的测试运行结果，我们可以看到执行顺序：

<img src="../assets/img_7.png">

首先，我们的两个@Before钩子执行。然后在每个步骤之前和之后，@BeforeStep和@AfterStep钩子分别运行。最后@After钩子运行。所有钩子都针对这两个场景执行。

### 4.2 不快乐的流程：某个步骤失败

让我们看看如果某个步骤失败会发生什么。正如我们在下面的截图中看到的，失败步骤的@Before和@After钩子都被执行。跳过后续步骤，最后@After钩子执行：

<img src="../assets/img_8.png">

@After的行为类似于Java中try-catch之后的finally子句。如果步骤失败，我们可以使用它来执行清理任务。在我们的示例中，即使场景失败，我们仍然会执行beforeScenario()方法。

### 4.3 不快乐的流程：某个钩子失败

让我们看看当钩子本身失败时会发生什么。在下面的示例中，第一个@BeforeStep失败。

在这种情况下，实际步骤不会运行，但@AfterStep钩子会运行。后续步骤也不会运行，而@After钩子在最后执行：

<img src="../assets/img_9.png">

## 5. 带标签的条件执行

钩子是全局定义的，会影响所有场景和步骤。但是，在Cucumber标签的帮助下，我们可以准确定义应该针对哪些场景执行钩子：

```java
@Before(order = 2, value = "@Screenshots")
public void beforeScenario() {
    takeScreenshot();
}
```

通过指定value属性为“@Screenshots”，此钩子将仅针对标记为@Screenshots的场景执行：

```gherkin
@Screenshots
Scenario: 1 - Find books by author 
    When I ask for a book by the author Erik Larson 
    Then The salesperson says that there are 2 books
```

## 6. Java 8

我们可以添加[Cucumber Java 8支持](https://www.baeldung.com/cucumber-java-8-support)来将所有钩子以Lambda表达式的形式定义。

回顾上面示例中的初始化钩子：

```java
@Before(order = 2)
public void initialization() {
    startBrowser();
}
```

通过使用Lambda表达式我们可以重写为：

```java
public BookStoreWithHooksRunSteps() {
    Before(2, () -> startBrowser());
}
```

这同样适用于@BeforeStep、@After和@AfterStep。

## 7. 总结

在本文中，我们了解了如何定义Cucumber钩子。

我们讨论了在哪些情况下应该使用它们，什么时候不应该使用它们。然后，我们看到了钩子执行的顺序以及我们如何实现条件执行。

最后，我们介绍了如何使用Java 8 Lambda表达式定义钩子。