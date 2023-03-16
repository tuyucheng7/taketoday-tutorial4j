## 1. 简介

在本文中，我们将快速浏览一下[JBehave](http://jbehave.org/)，然后重点从BDD的角度测试REST API。

## 2. JBehave和BDD

JBehave是一个行为驱动开发框架。它旨在为自动化验收测试提供一种直观且可访问的方式。

如果你不熟悉BDD，最好从[本文](使用Cucumber进行RestAPI测试.md)开始，介绍另一个BDD测试框架Cucumber，其中我们介绍了一般BDD结构和功能。

与其他BDD框架类似，JBehave采用以下概念：

-   故事：代表业务功能的自动可执行增量，包含一个或多个场景
-   场景：代表系统行为的具体例子
-   步骤：使用经典BDD关键字表示实际行为：Given、When和Then

一个典型的场景是：

```gherkin
Given a precondition
When an event occurs
Then the outcome should be captured
```

场景中的每个步骤都对应于JBehave中的一个注解：

-   @Given：启动上下文
-   @When：执行操作
-   @Then：测试预期结果

## 3. Maven依赖

要在我们的Maven项目中使用JBehave，应该在pom中包含[jbehave-core](https://central.sonatype.com/artifact/org.jbehave/jbehave-core/5.1)依赖项：

```xml
<dependency>
    <groupId>org.jbehave</groupId>
    <artifactId>jbehave-core</artifactId>
    <version>4.1</version>
    <scope>test</scope>
</dependency>
```

## 4. 一个简单的例子

要使用JBehave，我们需要遵循以下步骤：

1.  编写用户故事
2.  将用户故事的步骤映射到Java代码
3.  配置用户故事
4.  运行JBehave测试
5.  审查结果

### 4.1 故事

让我们从下面这个简单的故事开始：“作为一个用户，我想递增counter，这样我就可以让counter的值增加1”。

我们可以在.story文件中定义故事：

```gherkin
Scenario: when a user increases a counter, its value is increased by 1

Given a counter
And the counter has any integral value
When the user increases the counter
Then the value of the counter must be 1 greater than previous value
```

### 4.2 映射步骤

给定这些步骤，我们在Java中实现它们：

```java
public class IncreaseSteps {
    private int counter;
    private int previousValue;

    @Given("a counter")
    public void aCounter() {
    }

    @Given("the counter has any integral value")
    public void counterHasAnyIntegralValue() {
        counter = new Random().nextInt();
        previousValue = counter;
    }

    @When("the user increases the counter")
    public void increasesTheCounter() {
        counter++;
    }

    @Then("the value of the counter must be 1 greater than previous value")
    public void theValueOfTheCounterMustBe1Greater() {
        assertEquals(1, counter - previousValue);
    }
}
```

请记住，**注解中的值必须与描述精确匹配**。

### 4.3 配置我们的故事

要执行这些步骤，我们需要为我们的故事设置舞台：

```java
public class IncreaseStoryLiveTest extends JUnitStories {

    @Override
    public Configuration configuration() {
        return new MostUsefulConfiguration()
              .useStoryLoader(new LoadFromClasspath(this.getClass()))
              .useStoryReporterBuilder(new StoryReporterBuilder()
                    .withCodeLocation(codeLocationFromClass(this.getClass()))
                    .withFormats(CONSOLE));
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        return new InstanceStepsFactory(configuration(), new IncreaseSteps());
    }

    @Override
    protected List<String> storyPaths() {
        return Collections.singletonList("increase.story");
    }
}
```

在storyPaths()中，我们提供了要由JBehave解析的.story文件路径。实际步骤实现在stepsFactory()中提供。然后在configuration()中，正确配置了故事加载器和故事报告。

现在我们已经准备好了一切，我们可以通过运行mvn clean test来开始我们的故事。

### 4.4 查看测试结果

我们可以在控制台中看到我们的测试结果。由于我们的测试已成功通过，因此输出将与我们的故事相同：

```gherkin
Scenario: when a user increases a counter, its value is increased by 1
Given a counter
And the counter has any integral value
When the user increases the counter
Then the value of the counter must be 1 greater than previous value
```

如果我们忘记实现场景的任何步骤，报告会告诉我们。假设我们没有实现@When步骤：

```gherkin
Scenario: when a user increases a counter, its value is increased by 1
Given a counter
And the counter has any integral value
When the user increases the counter (PENDING)
Then the value of the counter must be 1 greater than previous value (NOT PERFORMED)
```

```java
@When("the user increases the counter")
@Pending
public void whenTheUserIncreasesTheCounter() {
    // PENDING
}
```

测试报告会说@When步骤处于待办状态，因此@Then步骤将不会执行。

如果我们的@Then步骤失败了怎么办？我们可以立即从报告中发现错误：

```gherkin
Scenario: when a user increases a counter, its value is increased by 1
Given a counter
And the counter has any integral value
When the user increases the counter
Then the value of the counter must be 1 greater than previous value (FAILED)
(java.lang.AssertionError)
```

## 5. 测试REST API

现在我们掌握了JBehave的基础知识；接下来我们将介绍如何使用它测试REST API。我们的测试基于我们之前讨论地[如何使用Java测试REST API的文章](https://www.baeldung.com/integration-testing-a-rest-api)。

在那篇文章中，我们测试了[GitHub REST API](https://docs.github.com/en/rest)，主要关注HTTP响应码、标头和有效负载。为简单起见，我们可以将它们分别写成三个独立的故事。

### 5.1 测试状态码

故事：

```gherkin
Scenario: when a user checks a non-existent user on github, github would respond 'not found'

Given github user profile api
And a random non-existent username
When I look for the random user via the api
Then github respond: 404 not found

When I look for tuyucheng1 via the api
Then github respond: 404 not found

When I look for tuyucheng2 via the api
Then github respond: 404 not found
```

步骤：

```java
public class GithubUserNotFoundSteps {

    private String api;
    private String nonExistentUser;
    private int githubResponseCode;

    @Given("github user profile api")
    public void givenGithubUserProfileApi() {
        api = "https://api.github.com/users/%s";
    }

    @Given("a random non-existent username")
    public void givenANonexistentUsername() {
        nonExistentUser = randomAlphabetic(8);
    }

    @When("I look for the random user via the api")
    public void whenILookForTheUserViaTheApi() throws IOException {
        githubResponseCode = getGithubUserProfile(api, nonExistentUser)
              .getStatusLine()
              .getStatusCode();
    }

    @When("I look for $user via the api")
    public void whenILookForSomeNonExistentUserViaTheApi(String user) throws IOException {
        githubResponseCode = getGithubUserProfile(api, user)
              .getStatusLine()
              .getStatusCode();
    }

    static HttpResponse getGithubUserProfile(String api, String username) throws IOException {
        HttpUriRequest request = new HttpGet(String.format(api, username));
        return HttpClientBuilder
              .create()
              .build()
              .execute(request);
    }

    @Then("github respond: 404 not found")
    public void thenGithubRespond404NotFound() {
        assertEquals(SC_NOT_FOUND, githubResponseCode);
    }
}
```

请注意，在步骤实现中，我们如何**使用参数注入功能**。

从候选步骤中提取的参数只是按照自然顺序与带注解的Java方法中的参数匹配。

此外，还支持带注解的命名参数：

```java
@When("I look for $username via the api")
public void whenILookForSomeNonExistentUserViaTheApi(@Named("username") String user) throws IOException
```

### 5.2 测试媒体类型

下面是一个简单的MIME类型测试故事：

```gherkin
Scenario: when a user checks a valid user's profile on github, github would respond json data

Given github user profile api
And a valid username
When I look for the user via the api
Then github respond data of type json
```

以下是步骤：

```java
public class GithubUserResponseMediaTypeSteps {

    private String api;
    private String validUser;
    private String mediaType;

    @Given("github user profile api")
    public void givenGithubUserProfileApi() {
        api = "https://api.github.com/users/%s";
    }

    @Given("a valid username")
    public void givenAValidUsername() {
        validUser = "tuyucheng";
    }

    @When("I look for the user via the api")
    public void whenILookForTheUserViaTheApi() throws IOException {
        mediaType = ContentType
              .getOrDefault(getGithubUserProfile(api, validUser).getEntity())
              .getMimeType();
    }

    @Then("github respond data of type json")
    public void thenGithubRespondDataOfTypeJson() {
        assertEquals("application/json", mediaType);
    }
}
```

### 5.3 测试JSON负载

然后是最后一个故事：

```gherkin
Scenario: when a user checks a valid user's profile on github, github's response json should include a login payload with the same username

Given github user profile api
When I look for tuyucheng via the api
Then github's response contains a 'login' payload same as tuyucheng
```

以及简单直接的步骤实现：

```java
public class GithubUserResponsePayloadSteps {

    private String api;
    private GitHubUser resource;

    @Given("github user profile api")
    public void givenGithubUserProfileApi() {
        api = "https://api.github.com/users/%s";
    }

    @When("I look for $user via the api")
    public void whenILookForTuyuchengViaTheApi(String user) throws IOException {
        HttpResponse httpResponse = GithubUserNotFoundSteps.getGithubUserProfile(api, user);
        resource = RetrieveUtil.retrieveResourceFromResponse(httpResponse, GitHubUser.class);
    }

    @Then("github's response contains a 'login' payload same as $username")
    public void thenGithubsResponseContainsAloginPayloadSameAsTuyucheng(String username) {
        assertThat(username, Matchers.is(resource.getLogin()));
    }
}
```

## 6. 总结

在本文中，我们简要介绍了JBehave并实现了BDD风格的REST API测试。

与我们普通的Java测试代码相比，使用JBehave实现的代码看起来更加清晰直观，测试结果报告看起来更加优雅。