问题 1) 我们可以在不使用 sendKeys() 的情况下输入文本吗？

Ans - 是的，我们可以在不使用 sendKeys() 方法的情况下输入文本。我们必须将 javascript 和包装类与 WebDriver 扩展类结合使用，检查以下代码 -

```java
public static void setAttribute(WebElement element, String attributeName, String value)

    {

        WrapsDriver wrappedElement = (WrapsDriver) element;

        JavascriptExecutor driver = (JavascriptExecutor)wrappedElement.getWrappedDriver();

        driver.executeScript("arguments[0].setAttribute(arguments[1],arguments[2])", element, attributeName, value);

    }
```

在测试脚本中调用上面的方法，传入text field属性，传入你要输入的文本。

问题 2) 每当“Assert.assertEquals()”函数自动失败时，就会出现一种情况，它必须截取屏幕截图。你怎么能做到这一点？

Ans- 通过使用 EventFiringWebDriver。

```java
Syntax-EventFiringWebDriver eDriver=new EventFiringWebDriver(driver);

File srcFile = eDriver.getScreenshotAs(OutputType.FILE);

FileUtils.copyFile(srcFile, new File(imgPath));
```

问题 3) 你如何在 selenium 中处理 https 网站？

Ans- 通过更改 FirefoxProfile 的设置。

```java
Syntax-public class HTTPSSecuredConnection {

public static void main(String[] args){

FirefoxProfile profile = new FirefoxProfile();

profile.setAcceptUntrustedCertificates(false);

WebDriver driver = new FirefoxDriver(profile);

driver.get("url");

}

}
```

问题 4) 如果显示用户名和密码的任何身份验证弹出窗口，如何登录到任何站点？

Ans - 通过 url 传递用户名和密码。

```java
Syntax- http://username:password@url

ex- http://creyate:jamesbond007@alpha.creyate.com
```

问题 5) Headless 浏览器的名称是什么。

Ans- HtmlUnitDriver。

Ques 6) Open a browser in memory 意味着每当它试图打开浏览器时，浏览器页面一定不会出现并且可以在内部执行操作。

答- 使用 HtmlUnitDriver。

前任-

```java
public class Memory {

public static void main(String[] args) {

HtmlUnitDriver driver = new HtmlUnitDriver(true);

driver.setJavascriptEnabled(false);

driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

driver.get("https://www.google.co.in/");

System.out.println(driver.getTitle());

}

}
```

问题 7) 使用 TestNG 有什么好处？

年-

a) TestNG 允许我们执行基于组的测试用例。

b) 在 TestNG 中注解很容易理解。

c) 在 TestNG 中并行执行 Selenium 测试用例是可能的。

d) 生成三种报告

e) 可以更改执行顺序

f) 失败的测试用例可以执行

g) 没有 main 函数我们可以执行测试方法。

h) 可以生成一个 xml 文件来执行整个测试套件。在该 xml 文件中，我们可以重新安排执行顺序，也可以跳过特定测试用例的执行。

问题 8) 如何在不使用 EventFiringWebDriver 的情况下进行屏幕截图？

年-

```java
File srcFile = ((TakeScreenshot)driver).getScreenshotAs(OutputType.FILE); //now we can do anything with this screenshot

like copy this to any folder-

FileUtils.copyFile(srcFile,new File(“folder name where u want to copy/file_name.png”));
```

问题 9) 如何在 WebDriver 中发送 ENTER/TAB 键？

Ans- 使用 click() 或 submit() [submit() 只能在 type='submit' 时使用]) ENTER 方法。或者使用 Actions 类来按键。

对于输入-

```java
act.sendKeys(Keys.RETURN);
```

对于选项卡-

```java
act.sendKeys(Keys.ENTER);
```

其中 act 是 Actions 类类型。( Actions act = new Actions(driver); )

问题 10) 什么是数据驱动框架和关键字驱动？

Ans- Datadriven framework - 在此框架中，虽然测试用例逻辑驻留在测试脚本中，但测试数据被分离并保存在测试脚本之外。测试数据从外部文件(Excel 文件)读取并加载到内部变量中测试脚本。变量用于输入值和验证值。

关键字驱动框架——关键字驱动或表驱动框架需要数据表和关键字的开发，独立于用于执行它们的测试自动化工具。可以在有或没有应用程序的情况下设计测试。在关键字驱动的测试中，被测应用程序的功能记录在表格中以及每个测试的分步说明中。

问题 11) 在解释框架时，应该涵盖哪些要点？

年-

a) 什么是框架工作。

b) 你正在使用哪个框架。

c) 为什么这个框架有效。

d) 架构。

e) 框架工作每个组成部分的解释。

f) 框架工作中遵循的过程。

g) 你如何以及何时执行框架工作。

h) 代码(你必须写代码并解释)。

i) 结果和报告。

j) 你应该能够解释 20 分钟。

问题 12) 如何从一个框架切换回来？

Ans- 使用方法 defaultContent()。

```java
Syntax – driver.switchTo().defaultContent();
```

问题 13) 如何在文本区域内换行输入文本？

Ans- 使用 \n 换行。

```java
ex- webelement.sendKeys(“Sanjay_Line1.\n Sanjay_Line2.”);
```

它将在文本框中键入 -

Sanjay_Line1。

Sanjay_Line2。

问题 14) AutoIt 工具有什么用？

Ans- 有时在使用 selenium 进行测试时，我们会被一些中断卡住，例如基于窗口的弹出窗口。但是 selenium 无法处理这个问题，因为它只支持基于 Web 的应用程序。为了克服这个问题，我们需要结合使用 AutoIT 和 selenium 脚本。AutoIT 是处理基于窗口的应用程序的第三方工具。使用的脚本语言是 VBScript。

问题 15) 如何使用 WebDriver 执行双击？

Ans- 使用 doubleClick() 方法。

```java
Syntax- Actions act = new Actions(driver);

act.doubleClick(webelement);
```

问题 16) 如何按 Shift+Tab 键？

年-

```java
String press = Keys.chord(Keys.SHIFT,Keys.ENTER);

webelement.sendKeys(press);
```

问题 17) contextClick() 的用途是什么？

Ans- 用于右键单击。

问题 18) b/w getWindowHandles() 和 getWindowHandle() 有什么区别？

Ans-getWindowHandles()-用于获取所有打开的浏览器的地址，其返回类型为Iterator<String>。

getWindowHandle()——用于获取conrol所在的当前浏览器的地址，返回类型为String。

问题 19) 你如何在你的框架中容纳项目特定的方法？

Ans- 1st 遍历所有手动测试用例并确定重复的步骤。记下这些步骤并将它们作为方法写入 ProjectSpecificLibrary。

问题 20) 你的框架有哪些不同的组成部分？

Ans- Library- Assertion、ConfigLibrary、GenericLibrary、ProjectSpecificLibrary、模块。

Drivers文件夹，Jars文件夹，excel文件。

问题 21) Selenium IDE 支持哪些浏览器？

Ans- 仅限 Mozilla FireFox。它是一个 Firefox 插件。

问题 22) Selenium IDE 的局限性是什么？

年-

a) 它不支持循环或条件语句。测试人员必须使用本地语言在测试用例中编写逻辑。

b) 它不支持测试报告，你必须使用 selenium RC 和一些外部报告插件，如 TestNG 或 JUint 来获得测试执行报告。

c) 也不支持错误处理，具体取决于本语言。

d) 仅支持 Mozilla FireFox。它是一个 Firefox 插件。

问题 23) 如何选中页面中的所有复选框？

年-

```java
List<webElement> chkBox = driver.findElements(By.xpath(“//htmltag[@attbute='checkbox']”));

for(int i=0; i<=chkBox.size(); i++){

chkBox.get(i).click();

}
```

问题 24) 计算页面中的链接数。

Ans- 使用定位器 By.tagName 并找到标签的元素 //a 然后使用循环来计算找到的元素数。

```java
Syntax- int count = 0;

List<webElement> link = driver.findElements(By.tagName(“a”));

System.out.println(link.size()); // this will print the number of links in a page.
```

问题 25) 你如何识别浏览器上元素的 Xpath？ 并且-为了找到 xpath，我们在 firefox 浏览器上使用 Firebug 插件，并识别我们使用 Firepath 插件编写的 xpath。

```java
Syntax- //htmltag[@attname='attvalue'] or //html[text()='textvalue'] or //htmltag[contains(text(),'textvalue')] or //htmltag[contains(@attname,'attvalue')]
```

问题 26) 什么是 Selenium Webdriver？

Ans-WebDriver 是关键接口的名称，应针对该接口用 Java 编写测试。WebDriver 的所有方法都由 RemoteWebDriver 实现了。

问题 27) 什么是 Selenium IDE？

Ans-Selenium IDE 是一个完整的Selenium 测试集成开发环境(IDE)。它作为 Firefox 附加组件实现，并允许记录、编辑和调试测试。它以前被称为 Selenium Recorder。

脚本可以自动记录和手动编辑，提供自动完成支持和快速移动命令的能力。

脚本记录在 Selenese 中，这是一种用于 Selenium 的特殊测试脚本语言。Selenese 提供用于在浏览器中执行操作(单击链接，选择一个选项)以及从结果页面检索数据的命令。

问题 28) 硒有哪些味道？

Ansselenium IDE、selenium RC、Selenium WebDriver 和 Selenium Grid。

问题 29) 什么是硒？

Ans-它是一个开源的 Web 自动化工具。它支持所有类型的网络浏览器。尽管它是开源的，但它得到了积极的开发和支持。

问题 30) selenium 相对于其他工具的优势？

年-

a)它是免费的，

b) 它支持多种语言，如 Java、C#、Ruby、Python 等，

c) 它允许简单而强大的 DOM 级测试等。

问题 31) RC 和 webdriver 之间的主要区别是什么？

Ans- Selenium RC 在加载网页时将 javascript 函数注入浏览器。

Selenium WebDriver 使用浏览器的内置支持来驱动浏览器。

问题 32)为什么选择 webdriver 而不是 RC？

年-

a) 本机自动化更快，更不容易出错和浏览器配置，

b) 不需要运行 Selenium-RC 服务器

c) 访问无头 HTMLUnitDriver 可以允许真正快速的测试

d) 优秀的 API 等

问题 33) xpath 和 CSS 哪个更好？

答- xpath。

问题 34) 你将如何处理动态元素？

Ans- 通过编写相对 xpath。

问题 35) 你的脚本中使用了哪些不同的断言或检查点？

Ans- 常见的验证类型是：

a) 页面标题是否符合预期，

b)针对页面上的元素进行验证，

c)页面上是否存在文本，

d) javascript 调用是否返回预期值。

```java
method used for validation – Assert.assertEquals();
```

问题 36) WebDriver 中的动作类是什么？

Ans-Actions 类用于控制鼠标的动作。

问题 37) @BeforeMethod 和 @BeforeClass 有什么区别？

Ans- @BeforeMethod- 这将在每个 @Test 方法之前执行。

@BeforeClass- 这将在每节课之前执行。

问题 38) @Test 注解有哪些不同的属性？

Ans- alwaysRun、dataProvider、dependsOnMethods、enabled、expectedExceptions、timeOut 等。

```java
ex- @Test(expectedExceptions = ArithmeticException.class)

@Test(timeOut = 2000)
```

问题 39) 我们可以使用 TestNG 运行一组测试用例吗？

答-是的。

问题 40) 什么是对象存储库？

Ans- 对象存储库是任何 UI 自动化工具中非常重要的实体。存储库允许测试人员将脚本中使用的所有对象存储在一个或多个集中位置，而不是让它们分散在整个测试脚本中。对象存储库的概念并不仅仅与 WET 相关。它可用于任何 UI 测试自动化。事实上，最初引入对象存储库概念的原因是为了QTP需要的框架。

问题 41) oops 的概念是什么？

年-

一)封装，

b)抽象，

c)多态性，

d) 继承。

问题 42) 什么是继承？

Ans-通过在类/接口之间建立某种关系来继承任何类的特性称为继承。

问题 43) overload 和 override 有什么区别？

Ans-通过传递不同参数列表/类型的方法被称为方法重载，而具有不同方法主体的相同方法签名被称为方法覆盖。

问题 44) java 是否支持多重继承？

Ans-接口支持多重继承，类不支持。

问题 45) 编写一个交换两个数字的 Java 程序？

年-

```java
public class Swapping{

public static void main(String[] args){

Scanner in = new Scanner(System.in);

System.out.println(“enter the 1st num”);

int a = in.nextInt();

System.out.println(“enter the 2nd num”);

int b = in.nextInt();

System.out.println(“before swapping a=”+a+” and b= ”+b);

int x = a;

a = b;

b = x;

System.out.println(“After swapping a=”+a+” and b= ”+b);

}

}
```

问题 46) 为给定数字的阶乘编写一个 Java 程序。

年-

```java
public class Factorial{

public static void main(String args[]){

Scanner in = new Scanner(System.in);

System.out.println(“enter the num for which u want the factorial”);

int num = in.nextInt();

for(int i=num-1; i>0; i-- ){

num = numi;

}

System.out.println(num);

}

}
```

问题 47)Java 中有哪些不同的访问说明符？

Ans- private、default、protected 和 public。

问题 48) 为什么我们要进行自动化测试？

答- 原因-

a) 手动测试所有工作流程、所有领域、所有负面场景非常耗时和成本。

b) 很难手动测试多语言站点。

c) 自动化不需要人工干预。我们可以在无人值守的情况下运行自动化测试(一夜之间)。

d) 自动化提高了测试执行的速度。

e) 自动化有助于增加测试覆盖率。

f) 手动测试可能变得乏味，因此容易出错。

问题 49) 什么是测试策略？

Ans- 测试策略文档是高级文档，通常由项目经理开发。本文档定义了“软件测试方法”以实现测试目标。测试策略通常源自业务需求规范文档。

问题 50) ) 如果我的用户名不正确，请编写代码以使用断言。

年-

```java
try{

Assert.assertEquals(expUserName, actUserName);

}catch(Exception e){

Syste.out.println(“name is invalid”);

}
```