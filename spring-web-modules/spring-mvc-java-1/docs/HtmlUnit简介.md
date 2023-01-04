## 1. 简介

在本文中，我们将介绍HtmlUnit，该工具允许我们使用JAVA API以编程方式与HTML站点进行交互和测试。

## 2. 关于HtmlUnit

[HtmlUnit](http://htmlunit.sourceforge.net/)是一个无GUI的浏览器，一个旨在以编程方式使用而不是直接由用户使用的浏览器。

该浏览器支持JavaScript(通过[Mozilla Rhino](https://developer.mozilla.org/en-US/docs/Mozilla/Projects/Rhino)引擎)，甚至可以用于具有复杂AJAX功能的网站。所有这些都可以通过模拟典型的基于GUI的浏览器(如Chrome或Firefox)来完成。

HtmlUnit这个名字可能会让你认为它是一个测试框架，但尽管它绝对可以用于测试，但它可以做的远不止于此。

它也被[集成到Spring 4](https://docs.spring.io/spring-framework/docs/4.2.x/spring-framework-reference/htmlsingle/#spring-mvc-test-server-htmlunit)中，可以与Spring MVC测试框架无缝结合使用。

## 3. 下载和Maven依赖

HtmlUnit可以从[SourceForge](https://sourceforge.net/projects/htmlunit/)或[官方网站](http://htmlunit.sourceforge.net/)下载。此外，你可以将其包含在你的构建工具(如Maven或Gradle等)中，如你[在此处](http://htmlunit.sourceforge.net/dependency-info.html)所见。例如，这是你当前可以包含在项目中的Maven依赖项：

```xml
<dependency>
    <groupId>net.sourceforge.htmlunit</groupId>
    <artifactId>htmlunit</artifactId>
    <version>2.23</version>
</dependency>
```

最新版本可以在[这里](https://search.maven.org/classic/#search|ga|1|htmlunit)找到。

## 4. Web测试

测试Web应用程序的方法有很多种——我们在网站上曾多次介绍过其中的大部分方法。

使用HtmlUnit，你可以直接解析站点的HTML，就像普通用户在浏览器中进行交互一样，检查JavaScript和CSS语法，提交表单并解析响应以查看其HTML元素的内容。所有这些，都使用纯Java代码。

让我们从一个简单的测试开始：创建一个WebClient并获取www.baeldung.com导航的第一页：

```java
private WebClient webClient;

@Before
public void init() throws Exception {
    webClient = new WebClient();
}

@After
public void close() throws Exception {
    webClient.close();
}

@Test
public void givenAClient_whenEnteringBaeldung_thenPageTitleIsOk()throws Exception {
    HtmlPage page = webClient.getPage("/");
    
    Assert.assertEquals("Baeldung | Java, Spring and Web Development tutorials", page.getTitleText());
}
```

如果我们的网站存在JavaScript或CSS问题，你可以在运行该测试时看到一些警告或错误。你应该纠正它们。

有时，如果你知道自己在做什么(例如，如果你发现唯一的错误来自你不应修改的第三方JavaScript库)，你可以通过调用setThrowExceptionOnScriptError来防止这些错误导致测试失败假的：

```java
@Test
public void givenAClient_whenEnteringBaeldung_thenPageTitleIsCorrect()throws Exception {
    webClient.getOptions().setThrowExceptionOnScriptError(false);
    HtmlPage page = webClient.getPage("/");
    
    Assert.assertEquals("Baeldung | Java, Spring and Web Development tutorials", page.getTitleText());
}
```

## 5. 网页抓取

你不需要只为自己的网站使用HtmlUnit。毕竟，它是一个浏览器：你可以使用它来浏览你喜欢的任何网络，根据需要发送和检索数据。

从网站获取、解析、存储和分析数据的过程称为网络抓取，HtmlUnit可以帮助你完成获取和解析部分。

前面的例子展示了我们如何进入任何网站并浏览它，检索我们想要的所有信息。

例如，让我们转到Baeldung的完整文章存档，导航到最新文章并检索其标题(第一个<h1\>标签)。对于我们的测试，这就足够了；但是，如果我们想存储更多信息，例如，我们也可以检索标题(所有<h2\>标签)，从而对文章的内容有一个基本的了解。

通过ID获取元素很容易，但通常，如果你需要查找元素，使用XPath语法会更方便。HtmlUnit允许我们使用它，所以我们会。

```java
@Test
public void givenBaeldungArchive_whenRetrievingArticle_thenHasH1()throws Exception {
    webClient.getOptions().setCssEnabled(false);
    webClient.getOptions().setJavaScriptEnabled(false);

    String url = "/full_archive";
    HtmlPage page = webClient.getPage(url);
    String xpath = "(//ul[@class='car-monthlisting']/li)[1]/a";
    HtmlAnchor latestPostLink = (HtmlAnchor) page.getByXPath(xpath).get(0);
    HtmlPage postPage = latestPostLink.click();

    List<HtmlHeading1> h1 = (List<HtmlHeading1>) postPage.getByXPath("//h1");
 
    Assert.assertTrue(h1.size() > 0);
}
```

首先注意如何——在这种情况下，我们对CSS和JavaScript不感兴趣，只想解析HTML布局，所以我们关闭了CSS和JavaScript。

在真正的网络抓取中，你可以以h1和h2标题为例，结果将是这样的：

```bash
Java Web Weekly, Issue 135
1. Spring and Java
2. Technical and Musings
3. Comics
4. Pick of the Week
```

你可以检查检索到的信息是否确实对应于Baeldung中的最新文章：

<img src="../assets/img.png">

## 6. AJAX怎么样？

AJAX功能可能是个问题，因为HtmlUnit通常会在AJAX调用完成之前检索页面。很多时候你需要它们完成以正确测试你的网站或检索你想要的数据。有一些方法可以对付它们：

-   你可以使用webClient.setAjaxController(new NicelyResynchronizingAjaxController())。这会重新同步从主线程执行的调用，并且这些调用是同步执行的，以确保有一个稳定的状态来测试。
-   当进入Web应用程序的页面时，你可以等待几秒钟，以便有足够的时间让AJAX调用完成。为此，你可以使用webClient.waitForBackgroundJavaScript(MILLIS)或webClient.waitForBackgroundJavaScriptStartingBefore(MILLIS)。你应该在检索页面之后但在使用它之前调用它们。
-   你可以等到与执行AJAX调用相关的某些预期条件得到满足。例如：

```java
for (int i = 0; i < 20; i++) {
    if (condition_to_happen_after_js_execution) {
        break;
    }
    synchronized (page) {
        page.wait(500);
    }
}
```

-   不要创建一个默认为最受支持的Web浏览器的新WebClient()，而是尝试其他浏览器，因为它们可能更适合你的JavaScript或AJAX调用。例如，这将创建一个使用Chrome浏览器的webClient：

```java
WebClient webClient = new WebClient(BrowserVersion.CHROME);
```

## 7. 一个Spring的例子

如果我们正在测试我们自己的Spring应用程序，那么事情会变得更容易一些——我们不再需要一个正在运行的服务器。

让我们实现一个非常简单的示例应用程序：只有一个控制器和一个接收文本的方法，以及一个带有表单的HTML页面。用户可以在表单中输入文本，提交表单，文本将显示在该表单下方。

在这种情况下，我们将为该HTML页面使用[Thymeleaf模板](http://www.thymeleaf.org/)，你可以[在此处](https://www.baeldung.com/thymeleaf-in-spring-mvc)查看完整的Thymeleaf示例：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestConfig.class })
public class HtmlUnitAndSpringTest {

    @Autowired
    private WebApplicationContext wac;

    private WebClient webClient;

    @Before
    public void setup() {
        webClient = MockMvcWebClientBuilder
              .webAppContextSetup(wac).build();
    }

    @Test
    public void givenAMessage_whenSent_thenItShows() throws Exception {
        String text = "Hello world!";
        HtmlPage page;

        String url = "http://localhost/message/showForm";
        page = webClient.getPage(url);

        HtmlTextInput messageText = page.getHtmlElementById("message");
        messageText.setValueAttribute(text);

        HtmlForm form = page.getForms().get(0);
        HtmlSubmitInput submit = form.getOneHtmlElementByAttribute(
              "input", "type", "submit");
        HtmlPage newPage = submit.click();

        String receivedText = newPage.getHtmlElementById("received")
              .getTextContent();

        Assert.assertEquals(receivedText, text);
    }
}
```

这里的关键是使用WebApplicationContext中的MockMvcWebClientBuilder构建WebClient对象。使用WebClient，我们可以获得导航的第一页(注意它是如何由localhost提供服务的)，并从那里开始浏览。

如你所见，测试解析表单输入消息(在ID为“message”的字段中)，提交表单，并在新页面上断言收到的文本(ID为“received”的字段)是与我们提交的文本相同。

## 8. 总结

HtmlUnit是一个很棒的工具，它允许你轻松地测试你的Web应用程序，填写表单字段并提交它们，就像你在浏览器上使用Web一样。

它与Spring 4无缝集成，并与Spring MVC测试框架一起为你提供了一个非常强大的环境，即使没有Web服务器也可以对所有页面进行集成测试。

此外，使用HtmlUnit，你可以自动执行与网页浏览相关的任何任务，例如获取、解析、存储和分析数据(网页抓取)。