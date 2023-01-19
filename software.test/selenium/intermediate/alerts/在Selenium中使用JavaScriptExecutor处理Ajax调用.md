[在这个设计 Selenium Cucumber 框架](https://www.toolsqa.com/selenium-cucumber-framework/cucumber-automation-framework/)系列中，我们已经取得了很大进展。我们设计了[PageObjectManager](https://toolsqa.com/selenium-cucumber-framework/page-object-manager/)来管理[PageObjects](https://toolsqa.com/selenium-cucumber-framework/page-object-design-pattern-with-selenium-pagefactory-in-cucumber/)，[FileReaderManager](https://toolsqa.com/selenium-cucumber-framework/file-reader-manager-singleton-design-pattern/)来管理 [ConfigFileReader](https://toolsqa.com/selenium-cucumber-framework/read-configurations-from-property-file/)和[JSonFileReader](https://toolsqa.com/selenium-cucumber-framework/data-driven-testing-using-json-with-cucumber/)以及[WebDriverManager](https://toolsqa.com/selenium-cucumber-framework/design-webdriver-manager/)来管理WebDriver。但是所有这些都有助于更好的代码管理和维护。

在本章中，我们将努力从 Selenium 测试执行中获得更好的测试结果。如果你想构建一个成功的测试自动化解决方案，你真正需要的就是让它可靠并快速。在 Web 自动化中影响这两个目标的最重要的因素是每次页面交互后你如何等待。无论你是单击或悬停在 Web 元素上、执行 JavaScript 函数还是模拟按键，都可能导致页面转换或 ajax 操作。你需要等待此操作完成，然后才能执行下一个操作。如果等待时间过长，测试执行速度会比需要的慢。如果你不等待或等待太少，则表现为持续或间歇性故障。

我们都知道 selenium 测试执行有时会间歇性地使某些测试失败。当你尝试解决问题并尝试调试相同的场景时，它运行得非常好。大多数情况下，它是由于元素加载延迟而发生的。意味着 Selenium 尝试对元素执行任何操作，但它尚未在 DOM 中呈现。

我知道你一定在想，那么，在这种情况下，Selenium 已经有一个称为Implicit-wait的特性，它总是等待元素加载并等待指定的时间。那为什么我们在Selenium执行中会出现这样的问题。答案是因为 Ajax Calls 或 JQuery。

### 什么是阿贾克斯？

AJAX是一种从网页到服务器执行 XMLHttpRequest(带外 Http 请求)并发送/检索要在网页上使用的数据的技术。AJAX 代表异步 Javascript 和 XML。

意思是：Ajax 是客户端浏览器与服务器通信(例如：从数据库检索数据)而无需执行页面刷新的方式。

### 什么是 JQuery？

JQuery(网站)是一个 javascript 框架，它通过构建许多可用于搜索 DOM 并与之交互的高级功能，使使用 DOM 变得更加容易。jQuery 的部分功能实现了执行 AJAX 请求的高级接口。

-   JQuery 是一个轻量级的客户端脚本库，而 AJAX 是用于提供异步数据传输的技术组合
-   JQuery 和 AJAX 经常结合使用
-   JQuery 主要用于动态修改屏幕上的数据，它使用 AJAX 来检索它需要的数据而不改变显示页面的当前状态

## 如何在网页上查看Ajax/JQuery？

这对于检查编写测试的应用程序实际运行的内容非常重要。一种简单的方法是与 UI 开发人员交谈并了解用于开发网页的技术。它总是会让你清楚地知道你将在自动化方面处理什么。第二种方法是自己去寻找。

在下面的示例中，我将使用Chrome 浏览器和 shop.demo.com网站来演示 Ajax 调用。要在你这边做同样的事情，请按照以下步骤操作。

1.  转到网站[shop.demoqa.com](https://shop.demoqa.com/)，将任何产品添加到购物篮/购物袋，然后到达结帐页面或输入个人详细信息和付款详细信息的最终页面。
2.  按F12在 chrome 中打开开发者模式，然后从菜单中选择控制台选项卡。
3.  现在在控制台编辑器中键入jQuery.active并按Enter 键。

![在 Selenium 中使用 JavaScriptExecutor 处理 Ajax 调用？](https://www.toolsqa.com/gallery/Cucumber/1.Handle%20Ajax%20call%20Using%20JavaScriptExecutor%20in%20Selenium.png)

注意：你会看到它返回了值 0。零表示当前页面上没有运行 ajax 或 jquery。

1.  让我们看看是否有任何 Ajax 在页面上实际工作。滚动到页面底部，那里有一个单选按钮 SHIP TO A DIFFERENT ADDRESS?。
2.  在Chrome 开发者窗口中选择网络选项卡，然后单击右侧最后一个下拉菜单以选择连接速度。选择慢速 3G。

![阿贾克斯等等 3](https://www.toolsqa.com/gallery/Cucumber/2.Ajax%20Wait%203.png)

注意：选择慢速 3G 会降低连接速度，让你有足够的时间检查页面上的内容。因为有时 Ajax 调用以闪电般的速度发生。

1.  返回到Console选项卡并键入jQuery.active，但不要按Enter。现在接下来的步骤要非常迅速地完成，因为你不会有太多时间去做。你需要单击复选框并立即返回到控制台编辑器并按Enter 进入jQuery.active 语句。

![阿贾克斯等等 3](https://www.toolsqa.com/gallery/Cucumber/3.Ajax%20Wait%203.png)

注意：这次 你会注意到值1 。一种表示 ajax 在页面上仍处于活动状态。

是的很酷，这不是一个很好的找东西的方法吗？很好，现在我们继续使用 JavaScriptExecutor 捕获 Ajax 调用。

## 在 Selenium 中使用 JavaScriptExecutor 处理 Ajax 调用？

由于这些情况，我们的测试将暂时失败。因此，等待 Ajax 调用完成总是明智的想法。这可以使用我们的JavaScriptExecutor接口来完成。这个想法很简单，如果所有的JQuery执行都完成了，那么它将返回jQuery.active == 0我们可以在我们的Wait.Until方法中使用它来等待脚本返回为真。

### 等待 Ajax 调用完成

要理解本章，你必须先了解前面的 [WebDriver Waits](https://toolsqa.com/selenium-webdriver/wait-commands/) 和[Smart Waits in Selenium](https://toolsqa.com/selenium-webdriver/wait-commands/) 一章中讨论的概念。你还应该了解 Java 中的函数和谓词。

下面是一个示例代码，用于展示使用 Selenium Webdriver 处理 AJAX 控件。你可以将它集成到你的测试执行类中。

Boolean isJqueryCallDone = (Boolean)((JavascriptExecutor) driver).executeScript("return jQuery.active==0");

由于上面的脚本将返回True或False。但是我们需要运行这段代码，直到我们得到 true 或指定的时间结束。为此，我们需要有Selenium WebDriver Wait，它会为我们提供直到方法。

```java
private static void until(WebDriver driver, Function<WebDriver, Boolean> waitCondition, Long timeoutInSeconds){
		WebDriverWait webDriverWait = new WebDriverWait(driver, timeoutInSeconds);
		webDriverWait.withTimeout(timeoutInSeconds, TimeUnit.SECONDS);
		try{
			webDriverWait.until(waitCondition);
		}catch (Exception e){
			System.out.println(e.getMessage());
		}          
	}
```

我们将在我们的新 Wait 类中创建一个 until 方法，其中将包含WebDriverWait的逻辑。该函数将WebDriver和Function`<WebDriver, Boolean>` 作为参数。函数`<WebDriver, Boolean> `声明它将接受驱动程序并返回布尔值。 withTimeout将决定等待条件变为真的时间。

让我们编写一个完整的方法，它将从页面对象方法中调用，最终将使用上面创建的 until 方法。

```java
public static void untilJqueryIsDone(WebDriver driver, Long timeoutInSeconds){
	until(driver, (d) ->
		{
		Boolean isJqueryCallDone = (Boolean)((JavascriptExecutor) driver).executeScript("return jQuery.active==0");
		if (!isJqueryCallDone) System.out.println("JQuery call is in Progress");
		return isJqueryCallDone;
		}, timeoutInSeconds);
}
```

在上面，调用了 Until 方法，它将驱动程序和函数作为等待条件作为参数传递。该等待条件实际上是我们的 Ajax 查询。

如果实在看不懂，试着从下面的截图中理解。

![阿贾克斯等待 5](https://www.toolsqa.com/gallery/Cucumber/4.Ajax%20Wait%205.png)

### 在 Selenium 中使用 JavaScriptExecutor 等待页面加载

如果你在软件自动化测试中使用了隐式等待，通常Selenium WebDriver会处理软件应用程序的页面加载或等待页面自行加载。但是许多开发站点都有页面加载问题，并且在每次软件测试迭代中加载页面都需要或多或少的时间。因此，有时你的测试运行时没有任何问题，有时由于页面加载问题而无法找到某些元素。

我们可以在我们的软件自动化测试中使用下面给定的 java 脚本语法来检查(加载)页面的状态。

文档.readyState

当页面完全加载时，它将返回“完成”。

```java
public static void untilPageLoadComplete(WebDriver driver, Long timeoutInSeconds){
		until(driver, (d) ->
			{
				Boolean isPageLoaded = (Boolean)((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
				if (!isPageLoaded) System.out.println("Document is loading");
				return isPageLoaded;
			}, timeoutInSeconds);
	}
```

这也以同样的方式实现。唯一的变化是这个使用document.readyState。

### 在框架中实现等待实用程序

1.  创建一个新包并将其命名为selenium，右键单击src/test/java并选择New >> Package。由于这与 Selenium WebDriverWait相关，我喜欢将其称为 selenium 包。
2.   通过右键单击上面创建的包并选择新建 >> 类，创建一个新类 并将其命名为Wait。
3.  现在将上面创建的方法放入该文件中。

Wait.java

```java
package selenium;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import managers.FileReaderManager;


public class Wait {
	
	public static void untilJqueryIsDone(WebDriver driver){
		untilJqueryIsDone(driver, FileReaderManager.getInstance().getConfigReader().getImplicitlyWait());
	}

	public static void untilJqueryIsDone(WebDriver driver, Long timeoutInSeconds){
		until(driver, (d) ->
			{
			Boolean isJqueryCallDone = (Boolean)((JavascriptExecutor) driver).executeScript("return jQuery.active==0");
			if (!isJqueryCallDone) System.out.println("JQuery call is in Progress");
			return isJqueryCallDone;
			}, timeoutInSeconds);
	}
	
	public static void untilPageLoadComplete(WebDriver driver) {
		untilPageLoadComplete(driver, FileReaderManager.getInstance().getConfigReader().getImplicitlyWait());
	}

	public static void untilPageLoadComplete(WebDriver driver, Long timeoutInSeconds){
		until(driver, (d) ->
			{
				Boolean isPageLoaded = (Boolean)((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
				if (!isPageLoaded) System.out.println("Document is loading");
				return isPageLoaded;
			}, timeoutInSeconds);
	}
	
	public static void until(WebDriver driver, Function<WebDriver, Boolean> waitCondition){
		until(driver, waitCondition, FileReaderManager.getInstance().getConfigReader().getImplicitlyWait());
	}

	
	private static void until(WebDriver driver, Function<WebDriver, Boolean> waitCondition, Long timeoutInSeconds){
		WebDriverWait webDriverWait = new WebDriverWait(driver, timeoutInSeconds);
		webDriverWait.withTimeout(timeoutInSeconds, TimeUnit.SECONDS);
		try{
			webDriverWait.until(waitCondition);
		}catch (Exception e){
			System.out.println(e.getMessage());
		}          
	}
	
	
}
```

注意：你会注意到在上面的类中每个方法名称都使用了两次。在编程中，如果参数不同，你可以在同一个类中使用相同的方法名称。现在，让我们了解为什么要这样做。加载一个元素可能需要 20 秒，而其他元素需要 90 秒才能加载。在这种情况下，最好打开你的方法以接受来自方法用户的时间。但是，如果用户不在意时间，则不应强迫用户提供相同的时间。所以在那种情况下，我们自己默认设置超时，这与我们在属性配置文件中指定为隐式等待的时间相同。

### 将页面对象中的 Thread.Sleep 替换为 Wait.untilJqueryIsDone()

到目前为止，我们已经在代码中使用了很多Thread.sleep，是时候用 Ajax Wait 方法替换所有的了。更改代码后，结帐页面对象类将如下所示：

CheckoutPage.java

```java
package pageObjects;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

import selenium.Wait;
import testDataTypes.Customer;

public class CheckoutPage {
	WebDriver driver;
	
	public CheckoutPage(WebDriver driver) {
		this.driver = driver;
	    PageFactory.initElements(driver, this);
	}
	
	@FindBy(how = How.CSS, using = "#billing_first_name") 
	private WebElement txtbx_FirstName;
	
	@FindBy(how = How.CSS, using = "#billing_last_name") 
	private WebElement txtbx_LastName;
	
	@FindBy(how = How.CSS, using = "#billing_email") 
	private WebElement txtbx_Email;
	
	@FindBy(how = How.CSS, using = "#billing_phone") 
	private WebElement txtbx_Phone;
	
	@FindBy(how = How.CSS, using = "#billing_country_field .select2-arrow") 
	private WebElement drpdwn_CountryDropDownArrow;
	
	@FindBy(how = How.CSS, using = "#billing_state_field .select2-arrow") 
	private WebElement drpdwn_CountyDropDownArrow;
	
	@FindAll(@FindBy(how = How.CSS, using = "#select2-drop ul li"))
	private List<WebElement> country_List;	
	
	@FindBy(how = How.CSS, using = "#billing_city") 
	private WebElement txtbx_City;
	
	@FindBy(how = How.CSS, using = "#billing_address_1") 
	private WebElement txtbx_Address;
	
	@FindBy(how = How.CSS, using = "#billing_postcode") 
	private WebElement txtbx_PostCode;
	
	@FindBy(how = How.CSS, using = "#ship-to-different-address-checkbox") 
	private WebElement chkbx_ShipToDifferetAddress;
	
	@FindAll(@FindBy(how = How.CSS, using = "ul.wc_payment_methods li"))
	private List<WebElement> paymentMethod_List;	
	
	@FindBy(how = How.CSS, using = "#terms.input-checkbox") 
	private WebElement chkbx_AcceptTermsAndCondition;
	
	@FindBy(how = How.CSS, using = "#place_order") 
	private WebElement btn_PlaceOrder;
	
	
	public void enter_Name(String name) {
		txtbx_FirstName.sendKeys(name);
	}
	
	public void enter_LastName(String lastName) {
		txtbx_LastName.sendKeys(lastName);
	}

	public void enter_Email(String email) {
		txtbx_Email.sendKeys(email);
	}
	
	public void enter_Phone(String phone) {
		txtbx_Phone.sendKeys(phone);
	}
	
	public void enter_City(String city) {
		txtbx_City.sendKeys(city);
	}
	
	public void enter_Address(String address) {
		txtbx_Address.sendKeys(address);
	}
	
	public void enter_PostCode(String postCode) {
		txtbx_PostCode.sendKeys(postCode);
	}
	
	public void check_ShipToDifferentAddress(boolean value) {
		if(!value) chkbx_ShipToDifferetAddress.click();
		Wait.untilJqueryIsDone(driver);
	}
	
	public void select_Country(String countryName) {
		drpdwn_CountryDropDownArrow.click();
		Wait.untilJqueryIsDone(driver);

		for(WebElement country : country_List){
			if(country.getText().equals(countryName)) {
				country.click();	
				Wait.untilJqueryIsDone(driver);
				break;
			}
		}

	}
	
	public void select_County(String countyName) {
		drpdwn_CountyDropDownArrow.click();
		Wait.untilJqueryIsDone(driver);
		for(WebElement county : country_List){
			if(county.getText().equals(countyName)) {
				county.click();	
				//Wait.untilJqueryIsDone(driver);
				break;
			}
		}
	}
	
	public void select_PaymentMethod(String paymentMethod) {
		if(paymentMethod.equals("CheckPayment")) {
			paymentMethod_List.get(0).click();
		}else if(paymentMethod.equals("Cash")) {
			paymentMethod_List.get(1).click();
		}else {
			new Exception("Payment Method not recognised : " + paymentMethod);
		}
		Wait.untilJqueryIsDone(driver);
		
	}
	
	public void check_TermsAndCondition(boolean value) {
		if(value) chkbx_AcceptTermsAndCondition.click();
	}
	
	public void clickOn_PlaceOrder() {
		btn_PlaceOrder.submit();
		Wait.untilPageLoadComplete(driver);
	}
	
	
	public void fill_PersonalDetails(Customer customer) {
		enter_Name(customer.firstName);
		enter_LastName(customer.lastName);
		enter_Phone(customer.phoneNumber.mob);
		enter_Email(customer.emailAddress);
		enter_City(customer.address.city);
		enter_Address(customer.address.streetAddress);
		enter_PostCode(customer.address.postCode);
		select_Country(customer.address.country);
		select_County(customer.address.county);		
	}

}
```

### 运行黄瓜测试

作为 JUnit 运行

现在我们都准备好运行 Cucumber 测试了。 右键单击 TestRunner 类 并单击 Run As >> JUnit Test。 Cucumber将以与在Selenium WebDriver 中运行的方式相同的方式运行脚本， 结果将显示在 JUnit 选项卡的左侧项目浏览器窗口中。

你会注意到，在执行完所有步骤后，执行将进入挂钩并执行 quitDriver()。

### 项目浏览器

![阿贾克斯等待 5](https://www.toolsqa.com/gallery/Cucumber/5.Ajax%20Wait%205.png)

