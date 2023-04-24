## 1. 概述

在本文中，我们将快速浏览一下Serenity BDD中的剧本模式。我们建议你在阅读本文之前先阅读[Serenity BDD的基础知识](https://www.baeldung.com/serenity-bdd)。此外，关于[Serenity BDD与Spring集成](https://www.baeldung.com/serenity-spring-jbehave)的文章也可能很有趣。

Serenity BDD中引入的Screenplay旨在通过使团队编写更健壮和可靠的测试来鼓励良好的测试习惯和精心设计的测试套件。它基于Selenium WebDriver和Page Objects模型。如果你阅读过我们对[Selenium的介绍](https://www.baeldung.com/java-selenium-with-junit-and-testng)，你会发现这些概念相当熟悉。

## 2. Maven依赖

首先，让我们将以下依赖项添加到pom.xml文件中：

```xml
<dependency>
    <groupId>net.serenity-bdd</groupId>
    <artifactId>serenity-junit</artifactId>
    <version>1.4.0</version>
</dependency>
<dependency>
    <groupId>net.serenity-bdd</groupId>
    <artifactId>serenity-screenplay</artifactId>
    <version>1.4.0</version>
</dependency>
<dependency>
    <groupId>net.serenity-bdd</groupId>
    <artifactId>serenity-screenplay-webdriver</artifactId>
    <version>1.4.0</version>
</dependency>
```

可以从Maven中央仓库获取最新版本的[serenity-screenplay](https://central.sonatype.com/artifact/net.serenity-bdd/serenity-screenplay/3.6.12)和[serenity-screenplay-webdriver](https://central.sonatype.com/artifact/net.serenity-bdd/serenity-screenplay-webdriver/3.6.12)。

我们还需要网WebDriver来执行剧本-[ChromeDriver](https://sites.google.com/a/chromium.org/chromedriver/)或[Mozilla-GeckoDriver](https://github.com/mozilla/geckodriver/releases)都可以。在本文中，我们将使用ChromeDriver。

启用WebDriver需要如下插件配置，其中webdriver.chrome.driver的值应该是我们Maven项目中ChromeDriver二进制文件的相对路径：

```xml
<plugin>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>2.20</version>
    <configuration>
        <systemProperties>
            <webdriver.chrome.driver>chromedriver</webdriver.chrome.driver>
        </systemProperties>
    </configuration>
</plugin>
```

## 3. WebDriver支持

我们可以通过在WebDriver变量上标记@Managed注解来让Serenity管理WebDriver实例。Serenity将在每次测试开始时打开相应的驱动程序，并在测试完成时将其关闭。

在下面的例子中，我们启动ChromeDriver并打开Google搜索“tuyucheng”。我们希望Eugen的名字出现在搜索结果中：

```java
@RunWith(SerenityRunner.class)
public class GoogleSearchLiveTest {

    @Managed(driver = "chrome")
    private WebDriver browser;

    @Test
    public void whenGoogleTuyuchengThenShouldSeeEugen() {
        browser.get("https://www.google.com/ncr");

        browser
              .findElement(By.name("q"))
              .sendKeys("tuyucheng", Keys.ENTER);

        new WebDriverWait(browser, 5)/*https://www.tuyucheng.com/serenity-screenplay*/
              .until(visibilityOfElementLocated(By.cssSelector("._ksh")));

        assertThat(browser
              .findElement(By.cssSelector("._ksh"))
              .getText(), containsString("Eugen (Tuyucheng)"));
    }
}
```

如果我们没有为@Managed指定任何参数，Serenity BDD在这种情况下将使用Firefox。@Managed注解支持的驱动程序的完整列表：firefox、chrome、iexplorer、htmlunit、phantomjs。

如果我们需要在IExplorer或Edge中进行测试，我们可以分别从[这里(对于IE)](https://selenium-release.storage.googleapis.com/index.html)和[这里(对于Edge)](https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver)下载WebDriver。Safari WebDriver仅在MacOS上的/usr/bin/safaridriver下可用。

## 4. 页面对象

Serenity页面对象代表一个WebDriver页面对象。[PageObject](http://thucydides.info/docs/apidocs/net/thucydides/core/pages/PageObject.html)隐藏WebDriver详细信息以供重用。

### 4.1 使用PageObject重构示例

让我们首先通过提取元素定位、搜索和结果验证操作来完善我们之前使用[PageObject](http://thucydides.info/docs/apidocs/net/thucydides/core/pages/PageObject.html)的测试：

```java
@DefaultUrl("https://www.google.com/ncr")
public class GoogleSearchPageObject extends PageObject {

    @FindBy(name = "q")
    private WebElement search;

    @FindBy(css = "._ksh")
    private WebElement result;

    public void searchFor(String keyword) {
        search.sendKeys(keyword, Keys.ENTER);
    }

    public void resultMatches(String expected) {
        assertThat(result.getText(), containsString(expected));
    }
}
```

[WebElement](https://seleniumhq.github.io/selenium/docs/api/java/org/openqa/selenium/WebElement.html)表示一个HTML元素。我们可以通过接口的API与网页进行交互。在上面的示例中，我们使用了两种在页面中定位Web元素的方法：按元素名称和按元素的CSS类。

在查找Web元素时有更多的方法可以应用，例如按标签名称查找、按链接文本查找等。有关更多详细信息，请参阅我们的[Selenium指南](https://www.baeldung.com/java-selenium-with-junit-and-testng)。

我们还可以将WebElement替换为[WebElementFacade](http://thucydides.info/docs/apidocs/net/thucydides/core/pages/WebElementFacade.html)，它提供了更流式的API来处理Web元素。

由于**Serenity会自动实例化JUnit测试中的任何PageObject字段**，因此可以将之前的测试重写为更简洁的测试：

```java
@RunWith(SerenityRunner.class)
public class GoogleSearchPageObjectLiveTest {

    @Managed(driver = "chrome")
    private WebDriver browser;

    GoogleSearchPageObject googleSearch;

    @Test
    public void whenGoogleTuyuchengThenShouldSeeEugen() {
        googleSearch.open();

        googleSearch.searchFor("tuyucheng");

        googleSearch.resultMatches("Eugen (Tuyucheng)");
    }
}
```

现在我们可以使用其他关键字进行搜索并匹配相关的搜索结果，而无需对GoogleSearchPageObject进行任何更改。

### 4.2 异步支持

如今，许多网页都是动态提供或呈现的。为了处理这种情况，PageObject还支持许多丰富的功能，使我们能够检查元素的状态。**我们可以检查元素是否可见，或者等到它们可见后再继续**。

让我们通过确保我们想要看到的元素可见来增强resultMatches方法：

```java
public void resultMatches(String expected) {
    waitFor(result).waitUntilVisible();
    assertThat(result.getText(), containsString(expected));
}
```

如果我们不希望等待太久，我们可以显式指定等待操作的超时时间：

```java
public void resultMatches(String expected) {
    withTimeoutOf(5, SECONDS)
        .waitFor(result)
        .waitUntilVisible();
    assertThat(result.getText(), containsString(expected));
}
```

## 5. 剧本模式

剧本模式将SOLID设计原则应用于自动化验收测试。对剧本模式的一般理解可以在given_when_then上下文中解释为：

-   given：能够执行某些任务的Actor
-   when：Actor执行任务
-   then：Actor应该看到效果并验证结果

现在让我们将之前的测试场景融入到剧本模式中：给定一个可以使用Google的用户Kitty，当她在Google上搜索“tuyucheng”时，Kitty应该会在结果中看到Eugen的名字。

首先，定义Kitty可以执行的任务。

1.  Kitty可以使用Google：

    ```java
    public class StartWith implements Task {
    
        public static StartWith googleSearchPage() {
            return instrumented(StartWith.class);
        }
    
        GoogleSearchPage googleSearchPage;
    
        @Step("{0} starts a google search")
        public <T extends Actor> void performAs(T t) {
            t.attemptsTo(Open
                  .browserOn()
                  .the(googleSearchPage));
        }
    }
    ```

2.  Kitty可以在Google上进行搜索：

    ```java
    public class SearchForKeyword implements Task {
    
        @Step("{0} searches for '#keyword'")
        public <T extends Actor> void performAs(T actor) {
            actor.attemptsTo(Enter
                  .theValue(keyword)
                  .into(GoogleSearchPage.SEARCH_INPUT_BOX)
                  .thenHit(Keys.RETURN));
        }
    
        private String keyword;
    
        public SearchForKeyword(String keyword) {
            this.keyword = keyword;
        }
    
        public static Task of(String keyword) {
            return Instrumented
                  .instanceOf(SearchForKeyword.class)
                  .withProperties(keyword);
        }
    }
    ```

3.  Kitty可以看到谷歌搜索结果：

    ```java
    public class GoogleSearchResults implements Question<List<String>> {
    
        public static Question<List<String>> displayed() {
            return new GoogleSearchResults();
        }
    
        public List<String> answeredBy(Actor actor) {
            return Text
                  .of(GoogleSearchPage.SEARCH_RESULT_TITLES)
                  .viewedBy(actor)
                  .asList();
        }
    }
    ```

此外，我们已经定义了Google搜索PageObject：

```java
@DefaultUrl("https://www.google.com/ncr")
public class GoogleSearchPage extends PageObject {

    public static final Target SEARCH_RESULT_TITLES = Target
          .the("search results")
          .locatedBy("._ksh");

    public static final Target SEARCH_INPUT_BOX = Target
          .the("search input box")
          .locatedBy("#lst-ib");
}
```

现在我们的主要测试类如下所示：

```java
@RunWith(SerenityRunner.class)
public class GoogleSearchScreenplayLiveTest {

    @Managed(driver = "chrome")
    WebDriver browser;

    Actor kitty = Actor.named("kitty");

    @Before
    public void setup() {
        kitty.can(BrowseTheWeb.with(browser));
    }

    @Test
    public void whenGoogleTuyuchengThenShouldSeeEugen() {
        givenThat(kitty).wasAbleTo(StartWith.googleSearchPage());

        when(kitty).attemptsTo(SearchForKeyword.of("tuyucheng"));

        then(kitty).should(seeThat(GoogleSearchResults.displayed(),
              hasItem(containsString("Eugen (Tuyucheng)"))));
    }
}
```

运行此测试后，我们将在测试报告中看到Kitty执行的每个步骤的屏幕截图：

<img src="../assets/img_7.png">

## 6. 总结

在本文中，我们介绍了如何将剧本模式与Serenity BDD一起使用。此外，在PageObject的帮助下，我们不必直接与WebDrivers交互，使我们的测试更易于阅读、维护和扩展。

有关Serenity BDD中的PageObject和剧本模式的更多详细信息，请查看Serenity文档的相关部分。