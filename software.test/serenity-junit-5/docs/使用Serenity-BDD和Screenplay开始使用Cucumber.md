## 目标[#](https://serenity-bdd.github.io/docs/tutorials/cucumber-screenplay#objectives)

在本教程结束时，你将完成以下活动。

1.  在著名的[TodoMVC](http://todomvc.com/)项目的文件中使用[Gherkin](https://cucumber.io/docs/gherkin/reference/)语言**通过示例**编写你的第一个规范`.feature`
2.  `.feature`使用Serenity BDD和带有 Screenplay 模式的 Cucumber使规范(来自上述步骤 1 的文件)可执行
3.  创建同时用作测试报告和进度报告的**动态文档**

## 先决条件[#](https://serenity-bdd.github.io/docs/tutorials/cucumber-screenplay#pre-requisites)

要运行本教程，你需要在计算机上安装一些东西：

-   **Java**：Serenity BDD是一个Java库，因此你需要安装最新的 JDK。JDK 1.8 或更高版本应该没问题。
-   **Maven**：你需要在计算机上安装 Maven 3 或更高版本。这充当构建工具，在构建时也会下载依赖项。
-   **Java IDE**：你还需要一个Java开发环境，例如 IntelliJ 或 Eclipse(以及Java的应用知识)。
-   **Git**：我们将在 Github 上使用一个入门项目，该项目的示例代码也位于 Github 上，因此我假设你对 Git 有基本的了解。

## 创建你的项目[#](https://serenity-bdd.github.io/docs/tutorials/cucumber-screenplay#creating-your-project)

[使用Cucumber](https://cucumber.io/)项目启动新的Serenity BDD的最快方法是克隆启动项目。对于本教程，我们将使用**[带有Cucumber和 Screenplay](https://github.com/serenity-bdd/serenity-cucumber-starter)**模板项目的Serenity BDD，它使用Serenity BDD和Cucumber6.x。

该项目附带一个已经实现的示例功能文件供我们参考。现在，我们将忽略它并从头开始编写一个新的功能文件。

##### 信息

为了确保初学者模板的示例文件不会影响我们在本教程中的体验，请**删除**以下文件/目录。

1.  目录 -`src/test/resources/features/search`
2.  目录 - `src/test/java/starter/navigation`
3.  目录 - `src/test/java/starter/search`
4.  文件 -`src/test/java/starter/stepdefinitions/SearchStepDefinitions.java`

## 项目目录结构[#](https://serenity-bdd.github.io/docs/tutorials/cucumber-screenplay#the-project-directory-structure)

我们将根据下面概述的标准 Maven 项目结构，使用一些简单的约定来组织我们的功能文件和支持的Java类：

```undefined
src├───main│   └───java│       └───starter└───test    ├───java    │   └───starter    │       └───helpers    │       └───stepdefinitions    └───resources        └───features
```



下面是关于目录结构的一些注意事项。

1.  由于我们将测试公开可用的[TodoMVC](http://todomvc.com/) Web 应用程序，因此目录中不会有任何代码`src/main`。
2.  我们将使用该`src/test/resources/features`目录来存储我们的`.feature`文件，这些文件是定义需求的规范。
3.  我们将使用该`src/test/java/starter/stepdefinitions`目录来存储实现文件中提到的步骤的代码`.feature`。此代码称为胶水代码或步骤定义。
4.  我们将使用该`src/test/java/starter/helpers`目录来存储我们的步骤定义所需的任何帮助程序类的代码。

## 编写第一个功能文件[#](https://serenity-bdd.github.io/docs/tutorials/cucumber-screenplay#writing-the-first-feature-file)

现在，让我们开始编写一个功能文件来描述将新项目添加到待办事项列表中。

`add_new_todo.feature`在目录中创建一个名称`src/test/resources/features`为以下内容的新文件。

```gherkin
Feature: Add new item to TODO list
Scenario: Add buying milk to the listGiven Rama is looking at his TODO listWhen he adds "Buy some milk" to the listThen he sees "Buy some milk" as an item in the TODO list
```



## 编写步骤定义框架[#](https://serenity-bdd.github.io/docs/tutorials/cucumber-screenplay#writing-the-step-definitions-skeleton)

为了将 中的步骤转换为可执行的操作，我们编写了称为**Step Definitions**`add_new_todo.feature`的Java 类。

`AddItemStepDefinitions.java`让我们在目录中创建一个名为`src/test/java/starter/stepdefinitions`以下骨架内容的新文件。请注意，这只是一个框架内容。我们稍后会在这个类中添加内容。

```java
package starter.stepdefinitions;
import io.cucumber.java.PendingException;import io.cucumber.java.en.Given;import io.cucumber.java.en.Then;import io.cucumber.java.en.When;import net.serenitybdd.screenplay.Actor;
public class AddItemStepDefinitions {
    @Given("{actor} is looking at his TODO list")    public void actor_is_looking_at_his_todo_list(Actor actor) {        // Write code here that turns the phrase above into concrete actions        throw new PendingException("Implement me");    }    @When("{actor} adds {string} to the list")    public void he_adds_to_the_list(Actor actor, String itemName) {        // Write code here that turns the phrase above into concrete actions        throw new PendingException("Implement me");    }    @Then("{actor} sees {string} as an item in the TODO list")    public void he_sees_as_an_item_in_the_todo_list(Actor actor, String expectedItemName) {        // Write code here that turns the phrase above into concrete actions        throw new PendingException("Implement me");    }
}
```



只要Cucumber尝试执行这些步骤并将它们标记为待处理，上述文件就会抛出异常。

让我们尝试运行 Maven 构建以查看此阶段的结果。我们希望构建失败，说明场景正在等待实施。

在终端或命令提示符中运行以下命令。

```bash
mvn clean verify
```



命令完成后，你可以看到类似于以下内容的输出。

```undefined
[ERROR] Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 7.255 s <<< FAILURE! - in starter.CucumberTestSuite[ERROR] Add new item to TODO list.Add buying milk to the list  Time elapsed: 0.713 s  <<< ERROR!io.cucumber.java.PendingException: TODO: implement me
[INFO] [INFO] Results:[INFO][ERROR] Errors: [ERROR]   TODO: implement me[INFO][ERROR] Tests run: 1, Failures: 0, Errors: 1, Skipped: 0
.........
[INFO] -----------------------------------------[INFO]  SERENITY TESTS: PENDING[INFO] -----------------------------------------[INFO] | Test cases executed    | 1[INFO] | Tests executed         | 1[INFO] | Tests passed           | 0[INFO] | Tests failed           | 0[INFO] | Tests with errors      | 0[INFO] | Tests compromised      | 0[INFO] | Tests aborted          | 0[INFO] | Tests pending          | 1[INFO] | Tests ignored/skipped  | 0[INFO] ------------------------ | --------------[INFO] | Total Duration         | 365ms[INFO] | Fastest test took      | 365ms[INFO] | Slowest test took      | 365ms[INFO] -----------------------------------------
.........
[INFO][INFO] --- maven-failsafe-plugin:3.0.0-M5:verify (default) @ cucumber-starter ---[INFO] ------------------------------------------------------------------------[INFO] BUILD FAILURE[INFO] ------------------------------------------------------------------------[INFO] Total time:  30.465 s[INFO] Finished at: 2022-08-12T14:52:57+05:30[INFO] ------------------------------------------------------------------------
```



上面的输出符合我们的预期。构建失败并显示 a`PendingException`并且测试被标记为待处理。

## 为步骤定义创建辅助类[#](https://serenity-bdd.github.io/docs/tutorials/cucumber-screenplay#creating-helper-classes-for-step-definitions)

到目前为止，我们只有虚拟步骤定义。现在让我们实施真正的测试。为了实现真正的测试，让我们创建一些辅助类。

#### 页面对象[#](https://serenity-bdd.github.io/docs/tutorials/cucumber-screenplay#page-object)

让我们先`TodoListPage.java`在目录中创建一个文件，`src/test/java/starter/helpers`内容如下。

```java
package starter.helpers;
import net.serenitybdd.core.pages.PageObject;import net.serenitybdd.screenplay.targets.Target;import net.thucydides.core.annotations.DefaultUrl;
@DefaultUrl("https://todomvc.com/examples/angularjs/#/")public class TodoListPage extends PageObject {    public static Target ITEM_NAME_FIELD = Target.the("item name field").locatedBy(".new-todo");
    public static Target ITEMS_LIST = Target.the(" item list").locatedBy(".todo-list li");}
```



这个类就是我们所说的`PageObject`. 这包含我们使用特定网页(在本例中为 TODO 应用程序)所需的所有信息。

`@DefaultUrl`注释指定需要在浏览器地址栏中键入的 URL 才能访问此页面。

有两个静态字段`ITEM_NAME_FIELD`可`ITEMS_LIST`帮助识别页面上的特定 HTML 元素，稍后我们将在步骤定义文件中使用它们。

#### 导航助手[#](https://serenity-bdd.github.io/docs/tutorials/cucumber-screenplay#navigation-helper)

`NavigateTo.java`让我们在目录中创建一个包含`src/test/java/starter/helpers`以下内容的文件。

```java
package starter.helpers;
import net.serenitybdd.screenplay.Performable;import net.serenitybdd.screenplay.Task;import net.serenitybdd.screenplay.actions.Open;
public class NavigateTo {     public static Performable theTodoListPage() {        return Task.where("{0} opens the Todo list page",                Open.browserOn().the(TodoListPage.class));    }}
```



上面的类使用Serenity BDD的[Screenplay 模式](https://serenity-bdd.github.io/docs/screenplay/screenplay_fundamentals)以清晰的方式描述行为。此类帮助我们使用正确的 URL 打开浏览器。

#### 动作定义[#](https://serenity-bdd.github.io/docs/tutorials/cucumber-screenplay#action-definition)

`AddAnItem.java`接下来，让我们在目录中创建一个包含`src/test/java/starter/helpers`以下内容的文件。

```java
package starter.helpers;
import net.serenitybdd.screenplay.Performable;import net.serenitybdd.screenplay.Task;import net.serenitybdd.screenplay.actions.Enter;import org.openqa.selenium.Keys;
public class AddAnItem {
    public static Performable withName(String itemName){        return Task.where("{0} adds an item with name "+itemName,                Enter.theValue(itemName)                        .into(TodoListPage.ITEM_NAME_FIELD)                        .thenHit(Keys.ENTER)        );    }}
```



上面的代码解释了将项目添加到列表所需的步骤，即在文本框中键入项目名称并按 ENTER 键。

## 向步骤定义添加细节[#](https://serenity-bdd.github.io/docs/tutorials/cucumber-screenplay#adding-details-to-step-definitions)

现在我们的助手类已经准备就绪，我们可以向中的步骤定义添加真正的细节`AddItemStepDefinitions.java`

打开`AddItemStepDefinitions.java`文件(我们已经创建了这个文件)并编辑它以具有以下内容。

```java
package starter.stepdefinitions;
import io.cucumber.java.en.Given;import io.cucumber.java.en.Then;import io.cucumber.java.en.When;import net.serenitybdd.screenplay.Actor;import net.serenitybdd.screenplay.ensure.Ensure;import starter.helpers.AddAnItem;import starter.helpers.NavigateTo;import starter.helpers.TodoListPage;
public class AddItemStepDefinitions {    @Given("{actor} is looking at his TODO list")    public void actor_is_looking_at_his_todo_list(Actor actor) {        actor.wasAbleTo(NavigateTo.theTodoListPage());    }    @When("{actor} adds {string} to the list")    public void he_adds_to_the_list(Actor actor, String itemName) {       actor.attemptsTo(AddAnItem.withName(itemName));    }    @Then("{actor} sees {string} as an item in the TODO list")    public void he_sees_as_an_item_in_the_todo_list(Actor actor, String expectedItemName) {        actor.attemptsTo(Ensure.that(TodoListPage.ITEMS_LIST).hasText(expectedItemName));    }
}
```



注意代码读起来像英语口语。这是在Cucumber步骤定义中使用 Screenplay 模式的令人愉快的副作用之一。

## 再次运行构建[#](https://serenity-bdd.github.io/docs/tutorials/cucumber-screenplay#running-the-build-again)

现在，让我们通过从终端或命令行发出以下命令来再次运行构建。

```bash
mvn clean verify
```



现在，你将看到以下输出。

```undefined
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 28.42 s - in starter.CucumberTestSuite[INFO] [INFO] Results:[INFO][INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
.........
[INFO] -----------------------------------------[INFO]  SERENITY TESTS: SUCCESS[INFO] -----------------------------------------[INFO] | Test cases executed    | 1[INFO] | Tests executed         | 1[INFO] | Tests passed           | 1[INFO] | Tests failed           | 0[INFO] | Tests with errors      | 0[INFO] | Tests compromised      | 0[INFO] | Tests aborted          | 0[INFO] | Tests pending          | 0[INFO] | Tests ignored/skipped  | 0[INFO] ------------------------ | --------------[INFO] | Total Duration         | 20s 001ms[INFO] | Fastest test took      | 20s 001ms[INFO] | Slowest test took      | 20s 001ms[INFO] -----------------------------------------[INFO][INFO] SERENITY REPORTS[INFO]   - Full Report: file:///C:/Users/calib/source-codes/temp/serenity-cucumber-starter/target/site/serenity/index.html[INFO][INFO] --- maven-failsafe-plugin:3.0.0-M5:verify (default) @ cucumber-starter ---[INFO] ------------------------------------------------------------------------[INFO] BUILD SUCCESS[INFO] ------------------------------------------------------------------------[INFO] Total time:  49.894 s[INFO] Finished at: 2022-08-12T15:28:52+05:30[INFO] ------------------------------------------------------------------------
```



是的，测试通过了，现在构建成功了。我们已经成功地测试了我们的功能 🎉

## 报告和生活文件[#](https://serenity-bdd.github.io/docs/tutorials/cucumber-screenplay#reporting-and-living-documentation)

如果你仔细观察，该`mvn clean verify`命令的输出告诉我们，报告是在`target/site/serenity/index.html`

当你在 Web 浏览器中打开此文件时，你会看到这样一个漂亮的报告。

![举报主页](https://serenity-bdd.github.io/assets/images/cucumber-test-report-home-03608f63deecb7521cb7fcd9cf1830c1.png)

你还可以在选项卡中找到详细说明场景的功能结果`Features`。

![特征报告](https://serenity-bdd.github.io/assets/images/cucumber-report-features-555853b0e8d29b79c13e71fb32f39233.png)

请随意浏览本报告中的链接并四处看看。

这也称为产品的**动态文档**，因为它是通过实际执行规范生成的，而不是仅仅将其编写为 wiki 或存储在云中的文档。随着产品的发展，场景将被添加，并且此报告是有关产品中哪些有效以及哪些待实施的唯一真实来源。

在某些情况下，团队使用此文档来为团队招募新成员。如果你喜欢冒险，本文档也可以用作用户指南。

## 下一步[#](https://serenity-bdd.github.io/docs/tutorials/cucumber-screenplay#next-steps)

在本教程中，我们只是触及了将Serenity BDD与Cucumber结合使用的皮毛。有多种方法可以自定义报告、组织特征文件、实施步骤定义等。请参阅用户手册中的链接以了解更多关于更多可能性的信息。