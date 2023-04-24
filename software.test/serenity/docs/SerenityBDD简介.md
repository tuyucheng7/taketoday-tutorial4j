## 1. 概述

在本教程中，我们将介绍[Serenity BDD](http://www.thucydides.info/)-一个应用[行为驱动开发(BDD)](https://www.baeldung.com/cs/bdd-guide)的出色工具。这是一种用于自动验收测试的解决方案，可生成图文并茂的测试报告。

## 2. 核心概念

Serenity背后的概念遵循BDD背后的概念。如果你想了解更多相关信息，请查看我们关于[Cucumber](https://www.baeldung.com/cucumber-rest-api-testing)和[JBehave](https://www.baeldung.com/jbehave-rest-testing)的文章。

### 2.1 需求

在Serenity中，需求分为三个级别：

1.  能力
2.  功能
3.  故事

通常，项目在电子商务项目中实现高级能力、订单管理和会员管理能力。每个能力都由许多功能组成，功能通过用户故事进行详细解释。

### 2.2 步骤和测试

步骤(Step)包含一组资源操作。它可以是动作、验证或上下文相关的操作。经典的Given_When_Then格式可以体现在步骤中。

测试与步骤齐头并进。**每个测试都讲述了一个简单的用户故事，该故事是使用特定的Step执行的**。

### 2.3 报告

Serenity不仅报告测试结果，还使用它们生成描述需求和应用程序行为的动态文档。

## 3. 使用Serenity BDD进行测试

要使用JUnit运行我们的Serenity测试，我们需要@RunWith(SerenityRunner.class)测试运行器。SerenityRunner检测步骤库并确保测试结果将由Serenity报告器记录和报告。

### 3.1 Maven依赖项

要在JUnit中使用Serenity，我们应该在pom.xml中包含[serenity-core](https://central.sonatype.com/artifact/net.serenity-bdd/serenity-core/3.6.12)和[serenity-junit](https://central.sonatype.com/artifact/net.serenity-bdd/serenity-junit/3.6.12)：

```xml
<dependency>
    <groupId>net.serenity-bdd</groupId>
    <artifactId>serenity-core</artifactId>
    <version>1.2.5-rc.11</version>
</dependency>
<dependency>
    <groupId>net.serenity-bdd</groupId>
    <artifactId>serenity-junit</artifactId>
    <version>1.2.5-rc.11</version>
</dependency>
```

我们还需要[serenity-maven-plugin](https://central.sonatype.com/artifact/net.serenity-bdd.maven.plugins/serenity-maven-plugin/3.6.12)来从测试结果中汇总报告：

```xml
<plugin>
    <groupId>net.serenity-bdd.maven.plugins</groupId>
    <artifactId>serenity-maven-plugin</artifactId>
    <version>1.2.5-rc.6</version>
    <executions>
        <execution>
            <id>serenity-reports</id>
            <phase>post-integration-test</phase>
            <goals>
                <goal>aggregate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

如果我们希望Serenity即使在测试失败的情况下也能生成报告，请将以下内容添加到pom.xml：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.20</version>
    <configuration>
        <testFailureIgnore>true</testFailureIgnore>
    </configuration>
</plugin>
```

### 3.2 会员积分示例

最初，我们的测试基于电子商务应用程序中的典型会员积分功能。客户可以加入会员计划。随着客户在平台上购买商品，会员积分会增加，客户的会员等级也会相应提升。

现在让我们针对上述场景编写几个测试，看看Serenity是如何工作的。

首先，让我们编写测试成员变量的初始化，看看我们需要哪些步骤：

```java
@RunWith(SerenityRunner.class)
public class MemberStatusIntegrationTest {

    @Steps
    private MemberStatusSteps memberSteps;

    @Test
    public void membersShouldStartWithBronzeStatus() {
        memberSteps.aClientJoinsTheMemberProgram();
        memberSteps.theMemberShouldHaveAStatusOf(Bronze);
    }
}
```

然后我们实现如下两个步骤：

```java
public class MemberStatusSteps {

    private Member member;

    @Step("Given a member has {0} points")
    public void aMemberHasPointsOf(int points) {
        member = Member.withInitialPoints(points);
    }

    @Step("Then the member grade should be {0}")
    public void theMemberShouldHaveAStatusOf(MemberGrade grade) {
        assertThat(member.getGrade(), equalTo(grade));
    }
}
```

现在我们准备使用mvn clean verify运行集成测试。报告将位于target/site/serenity/index.html：

<img src="../assets/img.png">

从报告中，我们可以看到我们只有一个验收测试“Members should start with bronze status”有能力并且正在通过。通过单击测试，步骤如下：

<img src="../assets/img_1.png">

正如我们所看到的，Serenity的报告可以让我们全面了解我们的应用程序正在做什么以及它是否符合我们的要求。如果我们有一些步骤要实现，我们可以将它们标记为@Pending：

```java
@Pending
@Step("When the member exchange {}")
public void aMemberExchangeA(Commodity commodity){
    // TODO
}
```

该报告将提醒我们下一步需要做什么。如果任何测试失败，也可以在报告中看到：

<img src="../assets/img_2.png" align="left">

将分别列出每个失败、忽略或跳过的步骤：

<img src="../assets/img_3.png" align="left">

## 4. 与JBehave集成

Serenity还可以与现有的BDD框架(例如JBehave)集成。

### 4.1 Maven依赖项

为了与JBehave集成，POM中还需要一个依赖项[serenity-jbehave](https://central.sonatype.com/artifact/net.serenity-bdd/serenity-jbehave/1.46.0)：

```xml
<dependency>
    <groupId>net.serenity-bdd</groupId>
    <artifactId>serenity-jbehave</artifactId>
    <version>1.24.0</version>
</dependency>
```

### 4.2 JBehave Github REST API

正如我们已经介绍了[如何使用JBehave进行REST API测试](https://www.baeldung.com/jbehave-rest-testing)，我们可以继续我们的JBehave REST API测试，看看它如何适应Serenity。

我们的故事是：

```gherkin
Scenario: Github user's profile should have a login payload same as username
 
Given github user profile api
When I look for eugenp via the api
Then github's response contains a 'login' payload same as eugenp
```

Given_When_Then步骤可以迁移为@Steps而无需任何更改：

```java
public class GithubRestUserAPISteps {

    private String api;
    private GitHubUser resource;

    @Step("Given the github REST API for user profile")
    public void withUserProfileAPIEndpoint() {
        api = "https://api.github.com/users/%s";
    }

    @Step("When looking for {0} via the api")
    public void getProfileOfUser(String username) throws IOException {
        HttpResponse httpResponse = getGithubUserProfile(api, username);
        resource = retrieveResourceFromResponse(httpResponse, GitHubUser.class);
    }

    @Step("Then there should be a login field with value {0} in payload of user {0}")
    public void profilePayloadShouldContainLoginValue(String username) {
        assertThat(username, Matchers.is(resource.getLogin()));
    }
}
```

为了使JBehave的故事到代码的映射按预期工作，我们需要使用@Steps实现JBehave的步骤定义：

```java
public class GithubUserProfilePayloadStepDefinitions {

    @Steps
    GithubRestUserAPISteps userAPISteps;

    @Given("github user profile api")
    public void givenGithubUserProfileApi() {
        userAPISteps.withUserProfileAPIEndpoint();
    }

    @When("looking for $user via the api")
    public void whenLookingForProfileOf(String user) throws IOException {
        userAPISteps.getProfileOfUser(user);
    }

    @Then("github's response contains a 'login' payload same as $user")
    public void thenGithubsResponseContainsAloginPayloadSameAs(String user) {
        userAPISteps.profilePayloadShouldContainLoginValue(user);
    }
}
```

使用SerenityStory，我们可以在IDE和构建过程中运行JBehave测试：

```java
import net.serenitybdd.jbehave.SerenityStory;

public class GithubUserProfilePayload extends SerenityStory {}
```

verify构建完成后，我们可以看到我们的测试报告：

<img src="../assets/img_4.png" align="left">

与JBehave的纯文本报告相比，Serenity的丰富报告让我们更赏心悦目，更能实时了解我们的故事和测试结果。

## 5. 与Rest-Assured集成

值得注意的是，Serenity支持与[Rest-Assured](http://rest-assured.io/)集成。要学习Rest-Assured，请查看[Rest-Assured指南](https://www.baeldung.com/rest-assured-tutorial)。

### 5.1 Maven依赖项

要将Rest-Assured与Serenity结合使用，应包含[serenity-rest-assured](https://central.sonatype.com/artifact/net.serenity-bdd/serenity-rest-assured/3.6.12)依赖项：

```xml
<dependency>
    <groupId>net.serenity-bdd</groupId>
    <artifactId>serenity-rest-assured</artifactId>
    <version>1.2.5-rc.11</version>
</dependency>
```

### 5.2 在Github REST API测试中使用Rest-Assured

现在我们可以用Rest-Assured实用程序替换我们的Web客户端：

```java
import static net.serenitybdd.rest.SerenityRest.rest;
import static net.serenitybdd.rest.SerenityRest.then;

public class GithubRestAssuredUserAPISteps {

    private String api;

    @Step("Given the github REST API for user profile")
    public void withUserProfileAPIEndpoint() {
        api = "https://api.github.com/users/{username}";
    }

    @Step("When looking for {0} via the api")
    public void getProfileOfUser(String username) throws IOException {
        rest().get(api, username);
    }

    @Step("Then there should be a login field with value {0} in payload of user {0}")
    public void profilePayloadShouldContainLoginValue(String username) {
        then().body("login", Matchers.equalTo(username));
    }
}
```

替换StepDefinition中userAPISteps的实现后，我们可以重新运行verify构建：

```java
public class GithubUserProfilePayloadStepDefinitions {

    @Steps
    GithubRestAssuredUserAPISteps userAPISteps;

    //...
}
```

在报告中，我们可以看到测试过程中调用的实际API，通过单击REST Query按钮，将显示请求和响应的详细信息：

<img src="../assets/img_5.png" align="left">

## 6. 与JIRA集成

到目前为止，我们已经有了一份很棒的测试报告，描述了我们对Serenity框架的需求的详细信息和状态。但对于敏捷团队来说，JIRA等问题跟踪系统通常用于跟踪需求。如果我们可以无缝地使用它们就更好了。

幸运的是，Serenity已经支持与JIRA的集成。

### 6.1 Maven依赖项

为了与JIRA集成，我们需要另一个依赖项：[serenity-jira-requirements-provider](https://central.sonatype.com/artifact/net.serenity-bdd/serenity-jira-requirements-provider/1.12.0)。

```xml
<dependency>
    <groupId>net.serenity-bdd</groupId>
    <artifactId>serenity-jira-requirements-provider</artifactId>
    <version>1.1.3-rc.5</version>
</dependency>
```

### 6.2 单向集成

要在故事中添加JIRA链接，我们可以使用故事的元标记添加JIRA问题：

```shell
Meta:
@issue #BDDTEST-1
```

此外，应在项目根目录下的文件serenity.properties中指定JIRA帐户和链接：

```properties
jira.url=<jira-url>
jira.project=<jira-project>
jira.username=<jira-username>
jira.password=<jira-password>
```

然后报告中会附加一个JIRA链接：

<img src="../assets/img_6.png" align="left">

Serenity还支持与JIRA的双向集成，更多细节可以参考[官方文档](http://www.thucydides.info/docs/serenity/#_two_way_integration_with_jira)。

## 7. 总结

在本文中，我们介绍了Serenity BDD以及与其他测试框架和需求管理系统的多种集成。

虽然我们已经介绍了Serenity可以做的大部分事情，但它当然可以做得更多。在我们的下一篇文章中，我们将介绍带有WebDriver支持的Serenity如何使我们能够使用剧本自动化Web应用程序页面。