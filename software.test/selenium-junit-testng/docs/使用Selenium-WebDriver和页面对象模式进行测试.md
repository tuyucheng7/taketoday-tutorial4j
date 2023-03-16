## 1. 概述

在本文中，我们将在[上一篇文章](https://www.baeldung.com/java-selenium-with-junit-and-testng)的基础上，通过引入页面对象模式继续改进我们的Selenium/WebDriver测试。

## 2. Selenium

让我们为我们的项目添加一个新的依赖项来编写更简单、更具可读性的断言：

```xml
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest-all</artifactId>
    <version>1.3</version>
</dependency>
```

最新版本可以在[Maven中央仓库](https://central.sonatype.com/artifact/org.hamcrest/hamcrest-all/1.3)中找到。

### 2.1 其他方法

在本系列的第一部分，我们使用了一些额外的工具方法，我们也将在这里使用它们。

我们将从navigateTo(String url)方法开始-它帮助我们导航到应用程序的不同页面：

```java
public void navigateTo(String url) {
    driver.navigate().to(url);
}
```

然后，clickElement(WebElement element)-顾名思义，该方法将负责对指定元素执行单击操作：

```java
public void clickElement(WebElement element) {
    element.click();
}
```

## 3. 页面对象模式

Selenium为我们提供了许多强大的低级API，我们可以使用这些API与HTML页面进行交互。

然而，随着我们测试的复杂性增加，与DOM的低级原始元素交互并不理想。我们的代码将更难更改，在对UI进行小的更改后可能会中断，并且简单地说，灵活性会降低。

**相反，我们可以利用简单的封装并将所有这些低级细节移动到页面对象中**。

在我们开始编写我们的首页对象之前，最好清楚地了解该模式-因为它应该允许我们模拟用户与我们的应用程序的交互。

页面对象将表现为一种接口，它将封装我们的页面或元素的详细信息，并公开一个高级API以与该元素或页面进行交互。

因此，一个重要的细节是为我们的方法提供描述性名称(例如clickButton()、navigateTo())，因为这样我们可以更容易地复制用户采取的操作，并且当我们链接步骤在一起时，通常会产生更好的API。

现在，让我们继续**创建我们的页面对象**-在本例中为我们的主页：

```java
public class BaeldungHomePage {

    private SeleniumConfig config;
    @FindBy(css = ".nav--logo_mobile")
    private WebElement title;
    @FindBy(css = ".menu-start-here > a")
    private WebElement startHere;

    public BaeldungHomePage(SeleniumConfig config) {
        this.config = config;
        PageFactory.initElements(this.config.getDriver(), this);
    }

    public void navigate() {
        this.config.navigateTo("https://www.baeldung.com/");
    }

    public String getPageTitle() {
        return title.getAttribute("title");
    }

    public StartHerePage clickOnStartHere() {
        config.clickElement(startHere);
        StartHerePage startHerePage = new StartHerePage(config);
        PageFactory.initElements(config.getDriver(), startHerePage);
        return startHerePage;
    }
}
```

请注意我们的实现如何处理DOM的低级细节并公开一个优雅的高级API。

例如，@FindBy注解允许我们预填充我们的WebElements，这也可以使用[By](https://seleniumhq.github.io/selenium/docs/api/java/org/openqa/selenium/By.html) API表示：

```java
private WebElement title = By.cssSelector(".header--menu > a");
```

当然，两者都是有效的，但是使用注解更简洁一些。

另外，请注意链接-我们的clickOnStartHere()方法返回一个StartHerePage对象，我们可以在其中继续交互：

```java
public class StartHerePage {

    private SeleniumConfig config;

    @FindBy(css = ".page-title")
    private WebElement title;

    public StartHerePage(SeleniumConfig config) {
        this.config = config;
    }

    public String getPageTitle() {
        return title.getText();
    }
}
```

让我们编写一个快速测试，我们只需导航到页面并检查其中一个元素：

```java
public class SeleniumPageObjectLiveTest {
    private SeleniumConfig config;
    private BaeldungHomePage homePage;
    private BaeldungAbout about;

    @Before
    public void setUp() {
        config = new SeleniumConfig();
        homePage = new BaeldungHomePage(config);
        about = new BaeldungAbout(config);
    }

    @After
    public void teardown() {
        config.close();
    }

    @Test
    public void givenHomePage_whenNavigate_thenShouldBeInStartHere() {
        homePage.navigate();
        StartHerePage startHerePage = homePage.clickOnStartHere();
        assertThat(startHerePage.getPageTitle(), is("Start Here"));
    }
}
```

重要的是要考虑到我们的主页有以下责任：

1. 根据给定的浏览器配置，导航到该页面
2. 然后，验证页面的内容(在本例中为标题)

我们的测试非常简单；我们导航到主页，单击“Start Here”元素，这将带我们进入同名页面，最后，我们只是验证标题是否存在。

在我们的测试运行后，close()方法将被执行，我们的浏览器应该会自动关闭。

### 3.1 分离关注点

我们可以考虑的另一种可能性是分离关注点(甚至更多)，通过拥有两个单独的类，一个将负责拥有我们页面的所有属性(WebElement或By)：

```java
public class BaeldungAboutPage {

    @FindBy(css = ".page-header > h1")
    public static WebElement title;
}
```

另一个将负责我们要测试的功能的所有实现：

```java
public class BaeldungAbout {

    private SeleniumConfig config;

    public BaeldungAbout(SeleniumConfig config) {
        this.config = config;
        PageFactory.initElements(config.getDriver(), BaeldungAboutPage.class);
    }

    public void navigateTo() {
        config.navigateTo("https://www.baeldung.com/about/");
    }

    public String getPageTitle() {
        return BaeldungAboutPage.title.getText();
    }
}
```

如果我们使用By属性而不是使用注解功能，建议在我们的页面类中添加一个私有构造函数以防止它被实例化。

值得一提的是，我们需要传递包含注解的类，在本例中为BaeldungAboutPage，这与我们在前面的示例中通过传递this关键字所做的相反。

```java
public class SeleniumPageObjectLiveTest {

    @Test
    public void givenAboutPage_whenNavigate_thenTitleMatch() {
        about.navigateTo();
        assertThat(about.getPageTitle(), is("About Baeldung"));
    }
}
```

请注意，我们现在如何在实现中保留与页面交互的所有内部细节，在这里，我们实际上可以在较高的可读级别上使用此客户端。

## 4. 总结

在这个快速教程中，我们专注于**在页面对象模式的帮助下改进Selenium/WebDriver的使用**。我们浏览了不同的示例和实现，以了解利用该模式与我们的网站进行交互的实用方法。