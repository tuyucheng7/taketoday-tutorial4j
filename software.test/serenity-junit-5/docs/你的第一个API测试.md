# 你的第一个 API 测试

在之前的教程中，我们了解了如何使用Serenity BDD编写简单的 Web 测试。但是 Serenity 不仅仅用于 Web 测试：Serenity BDD还提供与 Rest Assured 的紧密集成，[Rest Assured](https://rest-assured.io/)是一个流行的用于测试 REST API 的开源库。

## 先决条件[#](https://serenity-bdd.github.io/docs/tutorials/rest#pre-requisites)

要运行本教程，你需要在计算机上安装一些东西：

-   **Java**：Serenity BDD是一个Java库，因此你需要安装最新的 JDK。JDK 1.8 或更高版本应该没问题。
-   **Maven**：你需要在计算机上安装 Maven 3 或更高版本。这充当构建工具，在构建时也会下载依赖项。
-   **Java IDE**：你还需要一个Java开发环境，例如 IntelliJ 或 Eclipse(以及Java的应用知识)。
-   **Git**：我们将在 Github 上使用一个入门项目，该项目的示例代码也位于 Github 上，因此我假设你对 Git 有基本的了解。

## 创建你的项目[#](https://serenity-bdd.github.io/docs/tutorials/rest#creating-your-project)

我们将使用**[Serenity BDDJunit Starter](https://github.com/serenity-bdd/serenity-junit-starter)**模板项目来快速启动和运行一个简单的项目。

该项目附带了一个基于Junit 5的示例测试，供我们参考。现在，我们将忽略它并从头开始编写新测试。

转到[Github 上的项目模板页面，](https://github.com/serenity-bdd/serenity-junit-starter)然后单击[使用此模板](https://github.com/serenity-bdd/serenity-junit-starter/generate)。

## 删除不需要的文件[#](https://serenity-bdd.github.io/docs/tutorials/rest#deleting-the-unnecessary-files)

只是为了确保初学者模板的示例文件不会影响我们在本教程中的体验，请**删除**该`src/test/java/starter/wikipedia`目录。

## 添加 Serenity RestAssured 依赖项[#](https://serenity-bdd.github.io/docs/tutorials/rest#adding-the-serenity-restassured-dependency)

打开`pom.xml`根目录中的文件并在该`<dependencies>`部分中添加以下行，类似于文件中已有的行。

```xml
 <dependency>      <groupId>net.serenity-bdd</groupId>      <artifactId>serenity-rest-assured</artifactId>      <version>${serenity.version}</version>      <scope>test</scope>  </dependency>
```



## 启用详细的 HTML 报告[#](https://serenity-bdd.github.io/docs/tutorials/rest#enabling-detailed-html-reports)

当我们使用模板的默认配置时，我们只会得到一个单页 HTML 报告。我们希望在本教程中生成详细的 HTML 报告。因此，让我们从文件中**删除**以下行。`pom.xml`

```xml
<reports>single-page-html</reports>  <!-- DELETE the line above. Yes, delete it! -->
```



`serenity-maven-plugin`你可以在插件的配置部分找到它。

## 项目目录结构[#](https://serenity-bdd.github.io/docs/tutorials/rest#the-project-directory-structure)

我们将根据下面概述的标准 Maven 项目结构，使用一些简单的约定来组织我们的功能文件和支持的Java类：

```undefined
├───src│   ├───main│   │   └───java│   │       └───starter│   └───test│       ├───java│       │   └───starter│       │       └───petstore│       └───resources
```



下面是关于目录结构的一些注意事项。

1.  由于我们将测试公开可用的[Pet Store API](https://petstore.swagger.io/)，因此目录中不会有任何代码`src/main`。
2.  我们将(从模板中)重新使用目录中已有的内容`src/test/resources/`。
3.  `petstore`我们将在目录下创建一个新目录`src/test/java/starter`来存储我们的测试类及其助手。

## 编写 API 测试[#](https://serenity-bdd.github.io/docs/tutorials/rest#writing-an-api-test)

让我们从编写 API 测试开始。在此测试中，我们将测试[`GET /pet/{petId}`](https://petstore.swagger.io/#/pet/getPetById)API。当你`id`在 URL 中提供宠物时，此 API 将返回宠物。

但是，我们不能在没有任何`id`. 因此，我们的测试需要先创建一个 Pet 并`id`在它调用`GET /pet/{petId}`API 端点之前获取它。

换句话说，我们可以`Given-When-Then`按如下格式编写测试。

```Gherkin
Given Kitty is available in the pet storeWhen I ask for a pet using Kitty's IDThen I get Kitty as result
```



### 基本测试结构[#](https://serenity-bdd.github.io/docs/tutorials/rest#basic-test-structure)

现在我们创建一个新的测试类(我们称之为`WhenFetchingAlreadyAvailablePet`)和一个空的测试用例(我们可以称之为`fetchAlreadyAvailablePet`)。

```java
package starter.petstore;
import net.serenitybdd.junit5.SerenityJUnit5Extension;import org.junit.jupiter.api.Test;import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SerenityJUnit5Extension.class)public class WhenFetchingAlreadyAvailablePet {
  @Test  public void fetchAlreadyAvailablePet() {      }}
```



##### 警告

这里有几点需要注意：

-   注释告诉 JUnit此`@ExtendWith`测试使用 Serenity - 不要忘记此注释，否则你的测试将不会被识别为 Serenity 测试
-   `@Test`注释使用来自 JUnit 5 的类`org.junit.jupiter.api.Test`。注意不要将其与同名 ( `org.junit.Test`) 的 JUnit 4 注释混淆，否则你的测试将不会运行。
-   请注意，测试类的名称以`When`. 这是确保它被识别为要在 Maven 构建过程中运行的测试的方法之一。有关这方面的更多详细信息，请参阅`pom.xml`文件的`configuration`部分。`maven-failsafe-plugin`

### 创建动作类[#](https://serenity-bdd.github.io/docs/tutorials/rest#creating-action-classes)

`fetchAlreadyAvailablePet()`我们可以简单地开始在我们的方法中编写整个测试代码。那会很好用。但是保持测试代码的组织良好和结构良好对于降低维护成本至关重要。Serenity BDD为我们提供了多种方法来做到这一点。

执行此操作的最简单方法之一称为*Action Classes*。操作类是小型的、可重用的类，带有封装关键用户操作的方法。我们将使用这些类来调用 HTTP API。

例如，我们可以将`fetchAlreadyAvailablePet()`测试分为三个步骤：

1.  **安排(给定)：设置调用 GET api 的阶段，方法是使用**[此处](https://petstore.swagger.io/#/pet/addPet)记录的 HTTP POST 调用预加载名为“Kitty”的宠物。
2.  **Act (When):**使用'Kitty'的ID调用被测API
3.  **断言(然后)：**检查 API 是否返回名为“Kitty”的宠物

让我们在测试所在的同一个包中创建一个`PetApiActions`使用以下骨架代码命名的动作类。`petstore`

```java
package starter.petstore;
import io.cucumber.java.en.Given;import io.cucumber.java.en.Then;import io.cucumber.java.en.When;import net.serenitybdd.core.steps.UIInteractions;

public class PetApiActions extends UIInteractions {
    @Given("Kitty is available in the pet store")    public Long givenKittyIsAvailableInPetStore() {                  }
    @When("I ask for a pet using Kitty's ID: {0}")    public void whenIAskForAPetWithId(Long id) {            }
    @Then("I get Kitty as result")    public void thenISeeKittyAsResult() {           }}
```



##### 警告

**注意事项**

1.  由于我们要在接下来的步骤中使用 API 生成的 ID，因此我们将 ID 作为 Long 返回值返回。
2.  我们扩展了`UIInteractions`Serenity BDD附带的类，以帮助我们与 API 进行交互。

`"Kitty"`让我们从实施第一个操作开始：通过调用 POST API预先创建一个带有名称的宠物来设置舞台。

由于我们需要创建一个Java对象来保存`Pet`，所以我们可以使用以下代码`Pet.java`在包下创建一个类。`starter.petstore`

```java
package starter.petstore;
public class Pet {    private String name;    private String status;    private Long id;
    public Pet(String name, String status, Long id) {        this.name = name;        this.status = status;        this.id = id;    }
    public Pet(String name, String status) {        this.name = name;        this.status = status;    }
    public String getName() {        return this.name;    }
    public String getStatus() {        return this.status;    }
    public Long getId() {        return id;    }
    public void setName(String name) {        this.name = name;    }
    public void setStatus(String status) {        this.status = status;    }
    public void setId(Long id) {        this.id = id;    }}
```



现在我们有了在代码中表示宠物的方法，让我们`givenKittyIsAvailableInPetStore()` 在类的函数中编写我们的第一个动作`PetApiActions`。

```java
package starter.petstore;
import io.cucumber.java.en.Given;import io.cucumber.java.en.Then;import io.cucumber.java.en.When;import io.restassured.http.ContentType;import io.restassured.mapper.ObjectMapperType;import net.serenitybdd.core.steps.UIInteractions;

import static net.serenitybdd.rest.SerenityRest.*;
public class PetApiActions extends UIInteractions {
    @Given("Kitty is available in the pet store")    public Long givenKittyIsAvailableInPetStore() {
        Pet pet = new Pet("Kitty", "available");
        Long newId = given()                .baseUri("https://petstore.swagger.io")                .basePath("/v2/pet")                .body(pet, ObjectMapperType.GSON)                .accept(ContentType.JSON)                .contentType(ContentType.JSON).post().getBody().as(Pet.class, ObjectMapperType.GSON).getId();        return newId;    }
    @When("I ask for a pet using Kitty's ID: {0}")    public void whenIAskForAPetWithId(Long id) {
    }
    @Then("I get Kitty as result")    public void thenISeeKittyAsResult() {
    }}
```



接下来，让我们编写`whenIAskForAPetWithId`函数的实现。这将包括仅调用需要测试的 GET API。

```java
    @When("I ask for a pet using Kitty's ID: {0}")    public void whenIAskForAPetWithId(Long id) {        when().get("/" + id);    }
```



##### 警告

**注意事项**

1.  在`get`上面的方法调用中，部分中的and`baseUri`被重用。这就是为什么你不必在此方法中重复这些细节的原因。`basePath``given()`
2.  由于我们将`id`用作输入参数，因此我们`{0}`在描述中使用，以便它可以出现在我们的报告中。

接下来，让我们编写`thenISeeKittyAsResult`方法的实现如下。

```java
    @Then("I get Kitty as result")    public void thenISeeKittyAsResult() {        then().body("name", Matchers.equalTo("Kitty"));    }
```



将所有内容放在一起，`PetApiActions.java`文件如下所示。

```java
package starter.petstore;
import io.cucumber.java.en.Given;import io.cucumber.java.en.Then;import io.cucumber.java.en.When;import io.restassured.http.ContentType;import io.restassured.mapper.ObjectMapperType;import net.serenitybdd.core.steps.UIInteractions;import org.hamcrest.Matchers;
import static net.serenitybdd.rest.SerenityRest.*;
public class PetApiActions extends UIInteractions {
    @Given("Kitty is available in the pet store")    public Long givenKittyIsAvailableInPetStore() {
        Pet pet = new Pet("Kitty", "available");
        Long newId = given()                .baseUri("https://petstore.swagger.io")                .basePath("/v2/pet")                .body(pet, ObjectMapperType.GSON)                .accept(ContentType.JSON)                .contentType(ContentType.JSON).post().getBody().as(Pet.class, ObjectMapperType.GSON).getId();        return newId;    }
    @When("I ask for a pet using Kitty's ID: {0}")    public void whenIAskForAPetWithId(Long id) {        when().get("/" + id);    }
    @Then("I get Kitty as result")    public void thenISeeKittyAsResult() {        then().body("name", Matchers.equalTo("Kitty"));    }
}
```



### 完成我们的测试用例[#](https://serenity-bdd.github.io/docs/tutorials/rest#completing-our-test-case)

现在我们已经准备好 Actions 类，让我们完成在类中编写测试用例`WhenFetchingAlreadyAvailablePet`。

```java
package starter.petstore;
import net.serenitybdd.junit5.SerenityJUnit5Extension;import org.junit.jupiter.api.Test;import org.junit.jupiter.api.extension.ExtendWith;
@ExtendWith(SerenityJUnit5Extension.class)public class WhenFetchingAlreadyAvailablePet {
    Long newPetId = null;    PetApiActions petApi;
    @Test    public void fetchAlreadyAvailablePet() {        newPetId = petApi.givenKittyIsAvailableInPetStore();        petApi.whenIAskForAPetWithId(newPetId);        petApi.thenISeeKittyAsResult();    }}
```



让我们尝试运行 Maven 构建以查看结果。如果 API 按预期工作，我们希望测试通过并生成详细的 HTML 报告。

在终端或命令提示符中运行以下命令。

```bash
mvn clean verify
```



命令完成后，你可以看到类似于以下内容的输出。

```undefined
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 10.009 s - in starter.petstore.WhenFetchingAlreadyAvailablePet[INFO] [INFO] Results:[INFO][INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
.........
[INFO] -----------------------------------------[INFO]  SERENITY TESTS: SUCCESS[INFO] -----------------------------------------[INFO] | Test cases executed    | 1[INFO] | Tests executed         | 1[INFO] | Tests passed           | 1[INFO] | Tests failed           | 0[INFO] | Tests with errors      | 0[INFO] | Tests compromised      | 0[INFO] | Tests aborted          | 0[INFO] | Tests pending          | 0[INFO] | Tests ignored/skipped  | 0[INFO] ------------------------ | --------------[INFO] | Total Duration         | 9s 212ms[INFO] | Fastest test took      | 9s 212ms[INFO] | Slowest test took      | 9s 212ms[INFO] -----------------------------------------[INFO][INFO] SERENITY REPORTS[INFO]   - Full Report: file:///C:/Users/calib/source-codes/temp/serenity-junit-starter/target/site/serenity/index.html[INFO][INFO] --- maven-failsafe-plugin:3.0.0-M5:verify (default) @ serenity-junit-starter ---[INFO] ------------------------------------------------------------------------[INFO] BUILD SUCCESS[INFO] ------------------------------------------------------------------------[INFO] Total time:  39.104 s[INFO] Finished at: 2022-09-02T17:33:14+05:30[INFO] ------------------------------------------------------------------------
```



是的，测试通过，现在构建成功。我们已经成功地测试了我们的 API 🎉

## 报告和生活文件[#](https://serenity-bdd.github.io/docs/tutorials/rest#reporting-and-living-documentation)

如果你仔细观察，该`mvn clean verify`命令的输出告诉我们，报告是在`target/site/serenity/index.html`

当你在 Web 浏览器中打开此文件时，你会看到这样一个漂亮的报告。

![举报主页](https://serenity-bdd.github.io/assets/images/rest-report-home-a2aa1a51d9d3c6af7f8263bf8021803a.png)

你还可以在选项卡中找到详细说明步骤的测试结果`Stories`，详细说明 REST API 调用。

![报告](https://serenity-bdd.github.io/assets/images/rest-report-stories-67c1feb64856b4eb56f3490782e91a7e.jpg)

如果你想查看 HTTP 请求中使用的确切细节，可以单击上面屏幕截图中圈出的链接。这将向你显示详细信息，如下所示。

![使用 HTTP 请求报告](https://serenity-bdd.github.io/assets/images/rest-report-http-requests-69463f8dc1251640a8c70994e2500b81.png)

请随意浏览本报告中的链接并四处看看。

## 结论[#](https://serenity-bdd.github.io/docs/tutorials/rest#conclusion)

在本教程中，你创建了自己的 API 测试用例并使用Serenity BDD运行它们以生成漂亮的报告。