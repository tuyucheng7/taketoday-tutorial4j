问题 1) TestNG 中使用的注释是什么？

答：@Test、@BeforeSuite、@AfterSuite、@BeforeTest、@AfterTest、@BeforeClass、@AfterClass、@BeforeMethod、@AfterMethod。

问题 2) 你如何从 excel 中读取数据？

```java
FileInputStream fis = new FileInputStream(“path of excel file”);

 Workbook wb = WorkbookFactory.create(fis);

 Sheet s = wb.getSheet(“sheetName”);

 String value = s.getRow(rowNum).getCell(cellNum).getStringCellValue();
```

问题 3) xpath 的用途是什么？

Ans-它用于在网页中查找 WebElement。识别动态网页元素非常有用。

问题 4) 有哪些不同类型的定位器？

Ans- 有 8 种类型的定位器，都是 By 类的静态方法。

By.id()、By.name()、By.tagName()、By.className()、By.linkText()、By.partialLinkText()、By.xpath、By.cssSelector()。

问题 5) Assert 和 Verify 有什么区别？

Ans-Assert-用于验证结果。如果测试用例失败，那么它将在那里停止测试用例的执行，并将控制权移至其他测试用例。

验证——它也用于验证结果。如果测试用例失败，那么它不会停止该测试用例的执行。

问题 6) 单击登录按钮的替代方法是什么？

Ans- 使用 submit() 方法，但它只能在属性类型=提交时使用。

问题 7) 如何验证复选框/单选框是否被选中？

Ans- 我们可以使用 isSelected() 方法。

句法 -

```java
driver.findElement(By.xpath("xpath of the checkbox/radio button")).isSelected();
```

如果此方法的返回值为真，则检查它，否则不检查。

问题 8) 你如何处理警报弹出窗口？

Ans- 要处理警报弹出窗口，我们需要首先将控件切换到警报弹出窗口，然后单击“确定”或“取消”，然后将控件移回主页。

句法-

```java
String mainPage = driver.getWindowHandle();

 Alert alt = driver.switchTo().alert(); // to move control to alert popup

 alt.accept(); // to click on ok.

 alt.dismiss(); // to click on cancel.

 //Then move the control back to main web page-

 driver.switchTo().window(mainPage); → to switch back to main page.
```

问题 9) 如何启动 IE/chrome 浏览器？

Ans- 在启动 IE 或 Chrome 浏览器之前，我们需要设置系统属性。

```java
//To open IE browser

System.setProperty(“webdriver.ie.driver”,”path of the iedriver.exe file ”);

 WebDriver driver = new InternetExplorerDriver();
//To open Chrome browser → System.setProperty(“webdriver.chrome.driver”,”path of the chromeDriver.exe file ”);

 WebDriver driver = new ChromeDriver();
```

问题 10) 如何使用 WebDriver 执行右键单击？

Ans-使用动作类

```java
Actions act = new Actions(driver); // where driver is WebDriver type

 act.moveToElement(webElement).perform();

 act.contextClick().perform();
```

问题 11) 如何使用 WebDriver 执行拖放操作？

Ans-使用动作类

```java
Actions act = new Actions(driver);

 WebElement source = driver.findElement(By.xpath(“ -----”)); //source ele which you want to drag

 WebElement target = driver.findElement(By.xpath(“ -----”)); //target where you want to drop

 act.dragAndDrop(source,target).perform();
```

问题 12) 给出 WebDriver 中方法重载的例子。

Ans- frame( string ), frame( int ), frame( WebElement )。

问题 13) 如何上传文件？

Ans- 要上传文件，我们可以使用 sendKeys() 方法。

```java
driver.findElement(By.xpath(“input field”)).sendKeys(“path of the file which u want to upload”);
```

问题 14) 如何单击下拉菜单中的菜单项？

Ans- 如果该菜单是使用 select 标签创建的，那么我们可以使用 selectByValue() 或 selectByIndex() 或 selectByVisibleText() 方法。这些是 Select 类的方法。

如果菜单尚未使用 select 标签创建，那么我们可以简单地找到该元素的 xpath 并单击它进行选择。

问题 15) 你如何模拟浏览器的前后移动？

```java
driver.navigate().back();

 driver.navigate().forward();
```

问题 16)如何获取当前页面的 URL？

```java
driver.getCurrentUrl();
```

问题 17) '/' 和 '//' 有什么区别？

Ans- //- 用于在整个结构中搜索。

/- 用于标识直接子级。

问题 18) findElement 和 findElements 有什么区别？

Ans- 这两种方法都是 WebDriver 接口的抽象方法，用于在网页中查找 WebElement。

findElement() - 它用于查找一个网络元素。它只返回一种 WebElement 类型。

findElements() - 它用于查找多个 Web 元素。它返回 WebElements 列表。

问题 19) 如何在 WebDriver 中实现同步？

Ans-我们可以使用隐式等待。

语法- driver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);

如果执行驱动程序没有立即在页面中找到元素，它将在这里等待 10 秒。此代码将自动附加到脚本的每一行。不需要每次都写。打开浏览器后写一次即可。

问题 20) 编写通过 Selenium 读写 Excel 的代码？

```java
FileInputStream fis = new FileInputStream(“path of excel file”);

 Workbook wb = WorkbookFactory.create(fis);

 Sheet s = wb.getSheet("sheetName");

 String value = s.getRow(rowNum).getCell(cellNum).getStringCellValue(); // read data

 s.getRow(rowNum).getCell(cellNum).setCellValue("value to be set"); //write data

 FileOutputStream fos = new FileOutputStream(“path of file”);

 wb.write(fos); //save file
```

问题 21) 如何从文本框中获取键入的文本？

Ans-通过将 arg 作为值传递来使用 getAttribute( “value” ) 方法。

```java
String typedText = driver.findElement(By.xpath("xpath of box")).getAttribute("value"));
```

问题 22) 使用 WebDriver 时遇到的不同异常是什么？

Ans- ElementNotVisibleException、ElementNotSelectableException、NoAlertPresentException、NoSuchAttributeException、NoSuchWindowException、TimeoutException、WebDriverException 等。

问题 23) WebDriver 支持哪些语言？

Ans- 开发团队直接支持 Python、Ruby、C# 和 Java。还有用于 PHP 和 Perl 的 webdriver 实现。

问题 24) 如何清除 selenium 中文本框的内容？

Ans- 使用 clear() 方法。

```java
driver.findElement(By.xpath("xpath of box")).clear();
```

问题 25) 什么是框架？

Ans- 框架是一组自动化指南，有助于

保持测试的一致性，改进测试结构，最少使用代码，减少代码维护，提高可重用性，非技术测试人员可以参与代码，可以减少使用工具的培训时间，在适当的地方涉及数据。

软件自动化测试中使用了五种框架：

1-数据驱动的自动化框架

2-方法驱动的自动化框架

3-模块化自动化框架

4 关键字驱动的自动化框架

5-Hybrid Automation Framework，它基本上是不同框架的组合。(1+2+3)。

问题 26) 运行 selenium webdriver 的先决条件是什么？

Ans- JDK、Eclipse、WebDriver(selenium standalone jar file)、浏览器、要测试的应用程序。

问题 27) selenium webdriver 的优点是什么？

Ans- a) 它支持大多数浏览器，如 Firefox、IE、Chrome、Safari、Opera 等。

b) 它支持大多数语言，如 Java、Python、Ruby、C# 等。

b) 在执行测试脚本之前不需要启动服务器。

c) 它有实际的核心 API，可以绑定多种语言。

d) 支持移动鼠标光标。

e) 支持测试iphone/Android应用。

问题 28) 什么是 WebDriverBackedSelenium？

Ans- WebDriverBackedSelenium 是一种类名，我们可以在其中为其创建一个对象，如下所示：

```java
Selenium wbdriver= new WebDriverBackedSelenium(WebDriver object name, "URL path of website")
```

这个的主要用途是当我们想同时使用 WebDriver 和 Selenium RC 编写代码时，我们必须使用上面创建的对象来使用 selenium 命令。

问题 29) 如何在 webdriver 中调用应用程序？

driver.get(“url”); 或 driver.navigate().to(“url”);

问题 30) 什么是 Selenium Grid？

Ans-Selenium-Grid 允许你在不同的机器上针对不同的浏览器并行运行测试。也就是说，同时针对不同的机器、不同的浏览器和操作系统运行多个测试。本质上，Selenium-Grid 支持分布式测试执行。它允许在分布式测试执行环境中运行你的测试。

问题 31) 如何获取页面上的帧数？

```java
List <WebElement> framesList = driver.findElements(By.xpath("//iframe"));

 int numOfFrames = frameList.size();
```

问题 32) 你如何模拟向下滚动动作？

Ans-使用java脚本向下滚动-

```java
JavascriptExecutor jsx = (JavascriptExecutor)driver;

 jsx.executeScript("window.scrollBy(0,4500)", ""); //scroll down, value 4500 you can change as per your req

 jsx.executeScript("window.scrollBy(450,0)", ""); //scroll up

 ex-

 public class ScrollDown {

 public static void main(String[] args) throws InterruptedException {

 WebDriver driver = new FirefoxDriver();

 driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

 driver.get("https://www.flipkart.com/womens-clothing/pr?sid=2oq,c1r&otracker=hp_nmenu_sub_women_1_View%20all");

 driver.manage().window().maximize();

 JavascriptExecutor jsx = (JavascriptExecutor)driver;

 jsx.executeScript("window.scrollBy(0,4500)", ""); //scroll down

 Thread.sleep(3000);

 jsx.executeScript("window.scrollBy(450,0)", ""); //scroll up

 }

 }
```

问题 33)当我们使用 testng 时，我们必须在 .bat 文件中编写什么命令行来执行 selenium 项目？

Ans- java -cp bin;jars/ org.testng.TestNG testng.xml

问题 34) 使用 WebDriver 时要导入的包是什么？

Ansorg.openqa.selenium

问题 35) 如何检查网页上的元素是否可见？

Ans- 使用 isDisplayed() 方法。该方法的返回类型是布尔值。因此，如果它返回 true，则元素可见，否则不可见。

```java
driver.findElement(By.xpath("xpath of elemnt")).isDisplayed();
```

问题 36) 如何检查页面上的按钮是否启用？

Ans- 使用 isEnabled() 方法。该方法的返回类型是布尔值。因此，如果它返回 true，则按钮已启用，否则未启用。

driver.findElement(By.xpath("按钮的xpath")).isEnabled();

问题 37) 如何检查页面上的文本是否突出显示？

Ans- 识别字段的天气颜色是否不同-

```java
String color = driver.findElement(By.xpath("//a[text()='Shop']")).getCssValue("color");

 String backcolor = driver.findElement(By.xpath("//a[text()='Shop']")).getCssValue("background-color");

 System.out.println(color);

 System.out.println(backcolor);
```

在这里，如果颜色和背景颜色不同，则意味着该元素的颜色不同。

问题 38) 如何检查复选框或单选按钮是否被选中？

Ans- 使用 isSelected() 方法来识别。该方法的返回类型是布尔值。因此，如果它返回 true，则选择按钮，否则不启用。

```java
driver.findElement(By.xpath("xpath of button")).isSelected();
```

### 问题 39) 如何获取页面的标题？

Ans- 使用 getTitle() 方法。

```java
Syntax- driver.getTitle();
```

问题 40) 你如何获得文本框的宽度？

```java
driver.findElement(By.xpath(“xpath of textbox ”)).getSize().getWidth();

driver.findElement(By.xpath(“xpath of textbox ”)).getSize().getHeight();
```

问题 41) 你如何获得 web 元素的属性？

Ans- driver.getElement( By.tagName("img") ).getAttribute( "src" ) 将为你提供此标签的 src 属性。类似地，你可以获取 title、alt 等属性的值。

同样，你可以使用 getCssValue( "some propety name" ) 获取任何标签的 CSS 属性。

问题 42) 如何检查文本是否有下划线？

```java
Ans- Identify by getCssValue(“border-bottom”) or sometime getCssValue(“text-decoration”) method if the

 cssValue is 'underline' for that WebElement or not.

 ex- This is for when moving cursor over element that is going to be underlined or not-

 public class UnderLine {

 public static void main(String[] args) {

 WebDriver driver = new FirefoxDriver();

 driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

 driver.get("https://www.google.co.in/?gfe_rd=ctrl&ei=bXAwU8jYN4W6iAf8zIDgDA&gws_rd=cr");

 String cssValue= driver.findElement(By.xpath("//a[text()='Hindi']")).getCssValue("text-decoration");

 System.out.println("value"+cssValue);

 Actions act = new Actions(driver);

 act.moveToElement(driver.findElement(By.xpath("//a[text()='Hindi']"))).perform();

 String cssValue1= driver.findElement(By.xpath("//a[text()='Hindi']")).getCssValue("text-decoration");

 System.out.println("value over"+cssValue1);

 driver.close();

 }

 }
```

问题 43) 如何使用 selenium 网络驱动程序更改网页上的 URL？

```java
driver.get(“url1”);

driver.get(“url2”);
```

问题 44) 如何将鼠标悬停在元素上？

```java
Actions act = new Actions(driver);

act.moveToElement(webelement); //webelement on which you want to move cursor
```

问题 45) getOptions() 方法有什么用？

Ans- getOptions() 用于从下拉列表中获取选定的选项。

问题 46) deSelectAll() 方法有什么用？

Ans- 用于取消选择所有已从下拉列表中选择的选项。

问题 47) WebElement 是接口还是类？

Ans- WebDriver 是一个接口。

问题 48) FirefoxDriver 是类还是接口，它是从哪里继承的？

Ans- FirefoxDriver 是一个类。它实现了 WebDriver 接口的所有方法。

问题 49) webdriver 的超级接口是哪个？

Ans-SearchContext。

问题 50) b/w close() 和 quit() 有什么区别？

Ans- close() - 它将关闭控件所在的浏览器。

quit() - 它将关闭 WebDriver 打开的所有浏览器。