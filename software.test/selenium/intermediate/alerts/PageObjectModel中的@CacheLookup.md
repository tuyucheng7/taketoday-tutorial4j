页面对象模型是一种很好的设计模式，可以从实际测试中抽象出网页元素及其操作。这样测试就不必担心 WebElements 的创建/查找。我们使用@FindBy和@FindAll 注解来标记页面对象中的WebElement。@CacheLookup是一个非常重要且被忽视的注释，它可以帮助我们使测试运行得更快。

@CacheLookup，顾名思义，帮助我们控制何时缓存 WebElement，何时不缓存。当应用于 WebElement 时，此注释指示 Selenium 保留 WebElement 的缓存，而不是每次都从 WebPage 搜索 WebElement。这有助于我们节省大量时间。

在本教程中，我们将讨论 @CacheLookup 的好处，并将尝试通过使用此注释来量化性能提升。但是，本教程不会讨论@CacheLookup注解的基础知识。

请在[此处](https://toolsqa.com/selenium-webdriver/c-sharp/pagefactory-cachelookup/)阅读涵盖@CacheLookup基础知识的教程

## 快速回顾页面对象中元素查找何时发生

让我们首先了解 Selenium 何时在 PageObject 模型中进行 FindElement 调用。每当你使用页面对象中的 WebElement 执行某些操作时，都会触发 FindElement 以从网页中查找最新版本的 WebElement。此查找基本上是 对浏览器 Web 驱动程序的 FindElement REST 请求。此查找是代码中最耗时的部分之一。

让我们使用页面对象编写一个小测试来查看此交互。我们将记录所有浏览器网络驱动程序交互以验证这一点。

以下是测试页面网址

http://toolsqa.com/automation-practice-form/

在此测试页面中，我们将只创建名字和姓氏文本框。这是代码

```java
public class PracticeFormPageObject {

	@FindBy(how = How.NAME, using = "firstname")
	public WebElement firsName;

	@FindBy(how = How.NAME, using = "lastname")
	public WebElement lastName;

}
```

在测试中我们将使用这个 Page 对象。测试只做两件事

1.  向名字和姓氏文本框添加一些文本
2.  从名字和姓氏文本框中读取文本

这是测试代码

```java
@Test
public void TestFirstAndLastNameFields()
{
	// In order to understand how action on WebElements using PageObjects work,
	// we will save all the logs of chrome driver. Below statement helps us
	// save all the logs in a file called TestLog.txt
	System.setProperty("webdriver.chrome.logfile", "TestLog.txt");

	ChromeDriver driver = new ChromeDriver();
	driver.get("https://toolsqa.com/automation-practice-form/");

	// Initialize the Page object
	PracticeFormPageObject pageObject = PageFactory.initElements(driver, PracticeFormPageObject.class);

	// Write some values to First and Last Name
	pageObject.firsName.sendKeys("Virender"); // A FindBy call is triggered to fetch First Name
	pageObject.lastName.sendKeys("Singh"); // A FindBy call is triggered to fetch Last Name

	// Read values from the Text box.
	pageObject.firsName.getText(); // A FindBy call is triggered to fetch First Name
	pageObject.lastName.getText(); // A FindBy call is triggered to fetch Last Name

	driver.close();
	driver.quit();
}
```

在此测试中，将触发FindBy调用的次数为四次。这在代码注释中已标记。每个像“pageObject.firstName.getText()”这样的语句实际上是两次调用

1.  FindBy 查找元素
2.  getText 获取文本

这两个调用都是对浏览器的 WebDriver 的 REST 调用。如果你运行上面的测试，你应该 在项目的根目录中得到一个名为： TestLog.txt的日志文件。让我们打开文本文件并查看其中的内容，以供参考我在这里添加了日志的修剪版本

```java
[12.654][INFO]: COMMAND FindElement {
   "using": "name",
   "value": "firstname"
}
[12.654][INFO]: Waiting for pending navigations...
[12.717][INFO]: Done waiting for pending navigations. Status: ok
[12.851][INFO]: Waiting for pending navigations...
[12.854][INFO]: Done waiting for pending navigations. Status: ok
[12.854][INFO]: RESPONSE FindElement {
   "ELEMENT": "0.8984444413515806-1"
}

[12.860][INFO]: COMMAND TypeElement {
   "id": "0.8984444413515806-1",
   "value": [ "Virender" ]
}
[12.860][INFO]: Waiting for pending navigations...
[12.861][INFO]: Done waiting for pending navigations. Status: ok
[13.045][INFO]: Waiting for pending navigations...
[13.050][INFO]: Done waiting for pending navigations. Status: ok
[13.050][INFO]: RESPONSE TypeElement

[13.053][INFO]: COMMAND FindElement {
   "using": "name",
   "value": "lastname"
}
[13.053][INFO]: Waiting for pending navigations...
[13.054][INFO]: Done waiting for pending navigations. Status: ok
[13.074][INFO]: Waiting for pending navigations...
[13.082][INFO]: Done waiting for pending navigations. Status: ok
[13.082][INFO]: RESPONSE FindElement {
   "ELEMENT": "0.8984444413515806-2"
}
[13.086][INFO]: COMMAND TypeElement {
   "id": "0.8984444413515806-2",
   "value": [ "Singh" ]
}
```

在日志中，我们可以看到每个语句写入语句都有成对的 FindElement 和 TypeElement 调用。对日志文件的进一步调查将向你表明，对于每个 getText 调用，你都会有一个 FindElement 和 GetElementText 调用。

这证明了我们的页面对象模型的情况，对于我们在 WebElement Selenium 上执行的每个操作，都会进行一次额外的查找调用。正如我们将在下面的部分中看到的那样，这种调用可能会很昂贵，并且在大多数情况下可以避免。

### @CacheLookup 与性能分析

现在让我们看看如何避免额外的查找调用并使我们的测试更快。Selenium WebDriver 团队的聪明人知道这个问题并且有一个很好的解决方案。他们为我们提供了@CacheLookup注解。如果WebElement用这个注解修饰，Selenium 将不会尝试在网页上查找 Web 元素，它只会返回该元素的缓存版本。缓存版本是在第一次查找网页时创建的，在第一次查找之后，所有其他查找请求都将由缓存元素完全填充。

让我们执行一个小测试，看看我们对缓存和非缓存 Web 元素进行 1000 次连续调用所获得的时间性能差异。此测试将在单个元素、名字文本框上执行。修改后的页面对象将如下所示

```java
public class PracticeFormModifiedPageObject {

	@FindBy(how = How.NAME, using = "firstname")
	public WebElement firsName;

	@FindBy(how = How.NAME, using = "firstname")
	@CacheLookup
	public WebElement firsNameCached;

}
```

我们将修改测试代码以测量缓存和非缓存 Web 元素执行 getText 操作 1000 次所花费的时间。这是测试代码

```java
public static void main(String[] args)
{

	// In order to understand how action on WebElements using PageObjects work,
	// we will save all the logs of chrome driver. Below statement helps us
	// save all the logs in a file called TestLog.txt
	System.setProperty("webdriver.chrome.logfile", "TestLog.txt");

	ChromeDriver driver = new ChromeDriver();
	driver.get("https://toolsqa.com/automation-practice-form/");

	PracticeFormModifiedPageObject pageObject = PageFactory.initElements(driver, PracticeFormModifiedPageObject.class);
	// set some text to fetch it later
	pageObject.firstName.sendKeys("Virender");

	// We will first try to get Text from the WebElement version which is not cached.
	// We will measure the time to perform 1000 getText operations
	long withoutCacheStartTime = System.currentTimeMillis();
	for(int i = 0; i < 1000; i ++)
	{
		pageObject.firstName.getText();
	}
	long withoutCacheEndTime = System.currentTimeMillis();
	System.out.println("Time take in seconds Without cache " + ((withoutCacheEndTime - withoutCacheStartTime)/ 1000));

	// Let us now repeat the same process on the cached element and see
	// the amount of time it takes to perform the same operation 1000 times
	long withCacheStartTime = System.currentTimeMillis();
	for(int i = 0; i < 1000; i ++)
	{
		pageObject.firsNameCached.getText();
	}
	long withCacheEndTime = System.currentTimeMillis();
	System.out.println("Time take in seconds With cache " + ((withCacheEndTime - withCacheStartTime)/ 1000));

	driver.close();
	driver.quit();

}
```

现在让我们运行测试并查看输出。

![PageObjectModel 中的@CacheLookup](https://www.toolsqa.com/gallery/selnium%20webdriver/1.@CacheLookup%20in%20PageObjectModel.png)

在输出中，我们可以清楚地看到，与非缓存版本相比，缓存版本的WebElement执行相同操作所需的时间减少了 50%。你可以看到上面提到的时间上的微小变化，这种变化应该有正/负 2% 的影响。

## 何时使用和何时不使用@Cachelookup 注解

从上面的测试中我们可以清楚地看到使用 WebElement 的缓存版本是有益的，但并非对每个元素都适用。让我们试着理解这两个要点。

### 陈旧元素和陈旧元素异常

尽管对每个元素使用@CacheLookup注释很诱人，但它不适合本质上是动态的元素。动态元素是指经常刷新自身的元素。例如，每秒不断更改值的计时器文本。另一个示例可能是每隔几秒更改一次的股票价格自动收报机。这些元素不是@CacheLookup注释的良好候选者。

原因很简单，由于这些元素在网页上经常变化，它们不适合缓存。因为如果我们缓存一个元素的一个版本并且它在几秒钟后发生变化，那么我们将得到一个Stale 元素异常。

### 静态元素

@CacheLookup对于网页上一旦加载就不会改变的元素非常有用。这些类型的元素构成了网页上的大多数元素。所以对于那些元素，由于它们在测试执行过程中不会发生变化，我们应该使用@Cachelookup注解来提高测试速度。

我希望本教程对你有所帮助。如果对此页面的内容有任何评论或问题，请给我反馈。在我们离开之前，有一个小练习给你。

-   分析日志文件的内容以验证缓存的元素是否正在为 FindElement 调用浏览器的 webdriver