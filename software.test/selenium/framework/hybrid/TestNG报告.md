在上一个教程中，我们[在 TestNG 中编写并执行了一个测试用例](https://www.toolsqa.com/testng/testng-test/)。当我们在 TestNG 或[测试套件](https://www.toolsqa.com/testng/testng-test-suite/)中运行测试用例时，如果这很重要，结果将显示在屏幕的下半部分(我们将仅在 eclipse 方面进行讨论)。这些结果以报告的形式出现，而TestNG 报告需要在本课程中单独一节，因为它们非常详细、明确和复杂。除此之外，TestNG 有它自己的默认方式来开发可传输格式的报告。因此，这篇文章将结合以下主题：

-   Eclipse 中的 TestNG 报告仪表板
    -   此外，TestNG 控制台报告
    -   Eclipse 中的 TestNG“报告”部分
-   如何在 TestNG 中生成和查看可通过电子邮件发送的报告？
-   此外，如何在 TestNG 中生成和查看索引报告？
-   如何使用 Reporter 类生成 TestNG 报告？

在我们尝试生成可以在 Eclipse 外部查看和发送的报告之前，让我们探索 Eclipse 结果仪表板及其提供的有意义的见解。

## Eclipse 中的 TestNG 报告仪表板

重要的是要注意，我们将继续上一个关于[TestNG 测试用例](https://www.toolsqa.com/testng/testng-test/)的教程来完成本教程。在该教程中，我们运行了一个测试用例，该用例将打开浏览器并使用 Selenium 将其关闭。如果你不知道如何在 TestNG 中运行测试，我们建议你先阅读该教程。测试源代码如下所示：

```java
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.openqa.selenium.;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.;

public class TestNG {
    WebDriver driver ;
    @Test
    public void f() {
	   String baseUrl = "https://www.toolsqa.com/";
	   System.out.println("Launching Google Chrome browser"); 
	   driver = new ChromeDriver();
	   driver.get(baseUrl);
	   String testTitle = "Free QA Automation Tools For Everyone";
	   String originalTitle = driver.getTitle();
	   Assert.assertEquals(originalTitle, testTitle);
   }
	
   @BeforeMethod
    public void beforeMethod() {
	System.out.println("Starting Test On Chrome Browser");
    }
	
    @AfterMethod
     public void afterMethod() {
	 driver.close();
	 System.out.println("Finished Test On Chrome Browser");
    }
}
```

当我们运行这个测试时，在 Eclipse 中有两个独立的部分可以看到这些报告。

-   安慰
-   报告科

### TestNG 中的控制台报告

TestNG 中的控制台报告简短而简单，仅表示测试的整体摘要。![测试控制台](https://www.toolsqa.com/gallery/TestNG/1-testng%20console.png)

除了这些简短的统计数据外，控制台还显示了一堆来自 TestNG 的进程命令，我们不需要理会这些命令。如果运行这些测试有任何障碍，错误命令将仅显示在控制台选项卡中。该控制台在每种语言中都像典型的控制台一样工作。

### Eclipse 中的 TestNG 报告部分

除了控制台选项卡，报告选项卡位于 Eclipse 中，它生成比我们在控制台中的视图更深入的视图。![日食中的testng部分](https://www.toolsqa.com/gallery/TestNG/2-testng%20section%20in%20eclipse.png)

报告的顶部包含我们在控制台部分看到的相同摘要。我们已经在上面的截图中标记了它。

在摘要下方，TestNG 提供了作为测试一部分的类名和函数名。![测试报告类日食](https://www.toolsqa.com/gallery/TestNG/3-testng%20report%20class%20eclipse.png)

与此同时，执行“ f() ”测试方法也需要时间。这是“所有测试”部分下的默认视图。

除了“所有测试”之外，还可以通过不同的选项卡看到失败的测试和测试摘要，如下图所示。![testng中失败的测试摘要](https://www.toolsqa.com/gallery/TestNG/4-failed%20test%20summary%20in%20testng.png)

上面的屏幕截图是Failed Tests选项卡，它看起来与All Tests选项卡相同。这是因为我们进行了一项测试，但执行失败了。用户界面与它们两者相似。

TestNG 报告部分的摘要选项卡包含一些不同的内容。单击“摘要”选项卡：![测试报告摘要](https://www.toolsqa.com/gallery/TestNG/5-testng%20report%20summary.png)

摘要将显示我们执行的不同测试的完整摘要。如果你运行一个测试套件，那么就会有多个测试；否则，如果你像我一样执行过单个测试，则会看到单个测试。时间(秒)列将显示执行测试所花费的总时间，另外两列分别显示测试中类和方法的计数。

它是关于我们运行测试时的 Eclipse 报告或摘要。但是，最终，我们还需要将这些报告通过电子邮件发送给其他团队成员。为此，我们需要使用 TestNG 中所谓的“ emailable-report ”。让我们看看如何生成它。

## 如何在 TestNG 中生成可通过电子邮件发送的报告？

在 TestNG 中生成可通过电子邮件发送的报告，让用户可以将他们的测试报告发送给其他团队成员。可通过电子邮件发送的报告不需要测试人员做任何额外的工作，它们是整个测试执行的一部分。要生成可通过电子邮件发送的报告，首先，如果你还没有运行 TestNG 测试类。

运行测试用例后，将在同一目录中生成一个名为test-output的新文件夹。![TestNG 中的测试输出文件夹](https://www.toolsqa.com/gallery/TestNG/6-test%20output%20folder%20in%20TestNG.png)

探索这个文件夹。它将包含多个文件。我们将在本课程的不同点上讨论它们。对于本教程，我们将重点关注emailable-report.html文件。![TestNG 中的可通过电子邮件发送的报告](https://www.toolsqa.com/gallery/TestNG/7-emailable%20report%20in%20TestNG.png)

右键单击该文件。选择打开方式 -> Web 浏览器。![打开可通过电子邮件发送的报告](https://www.toolsqa.com/gallery/TestNG/8-open%20emailable%20reports.png)

它将在 Eclipse 中打开报告。为了更好地查看，从 Eclipse 中的地址栏复制 URL。![电子邮件报告的网址](https://www.toolsqa.com/gallery/TestNG/9-url%20of%20emailable%20report.png)

将此 URL 粘贴到你选择的任何浏览器，以更好地查看可通过电子邮件发送的报告。

位于测试输出文件夹中的另一个重要文件是index.html。让我们来看看。

## TestNG如何生成索引文件？

可通过电子邮件发送的报告是一种摘要报告，可以通过任何媒介将其传输给团队中的其他人。另一方面，索引报告包含报告不同部分的类似索引的结构，例如失败的测试、测试文件、通过的测试等。

要打开index.html文件，请将其放在同一个测试输出文件夹中。![索引 html 文件](https://www.toolsqa.com/gallery/TestNG/10-index%20html%20file.png)

与上一份报告类似，在浏览器中打开这份报告。![testng中的索引报告](https://www.toolsqa.com/gallery/TestNG/11-index%20report%20in%20testng.png)

我们可以将这份报告分为两部分。左边部分包含索引，这就是它被称为索引报告的原因，而右边部分包含该索引的探索内容。![指标测试](https://www.toolsqa.com/gallery/TestNG/12-index%20testng.png)

更直接地说，你在此索引中选择的任何内容都会投影在右侧。在上面的示例中，突出显示的 XML 索引项目位于右侧。操作起来更加得心应手和人性化。

## 如何使用 Reporter 类生成 TestNG 报告？

Reporter 类是 TestNG 中的内置类。TestNG 中的 reporter 类有助于将日志存储在用户生成或系统生成的报告中，以便将来我们查看报告时，可以直接从那里查看日志，而不是重新运行测试用例。

要使用报告类，我们使用以下语法：

`Reporter.log(string);`

简单地说，我们正在调用TestNG 的 Reporter 类的“日志”函数。

从我们在本教程开始时使用的测试用例代码中观察以下代码片段。

```java
public void f() {
	  String baseUrl = "https://www.toolsqa.com/"; 
	  System.out.println("Launching Google Chrome browser"); 
	  driver = new ChromeDriver();
	  driver.get(baseUrl);
	  Reporter.log("We used Google Chrome Ver 80 for this test");
	  String testTitle = "Free QA Automation Tools For Everyone";
	  String originalTitle = driver.getTitle();
	  Assert.assertEquals(originalTitle, testTitle);
  }
```

我们想在报告中记录字符串“ We used Google Chrome Ver 80 for this test ”。按照上述步骤运行测试用例并生成可通过电子邮件发送的报告。

完成后，在浏览器中打开可通过电子邮件发送的报告：![记者班日志](https://www.toolsqa.com/gallery/TestNG/13-reporter%20class%20log.png)

我们开始吧，我们在报告中为每个收到它的团队成员记录了一条消息。伟大的！这一切都是关于生成 TestNG 报告并在 Eclipse 和你的浏览器中查看它们。报告的作用与考试中的成绩单类似。它们帮助我们分析完整的摘要，而无需阅读一行代码。由于报告包含大量元素(包括测试人员在代码中放置的元素)，我们无法在教程或本课程中解释所有内容。尽管随着我们的进步，我们会选择重要的东西。因此，我建议花一些时间阅读这些报告并导航到你看到的每个链接。对以后有很大帮助。

## 关于 TestNG 报告的常见问题

测试人员可以自定义 TestNG 报告吗？
是的，测试人员可以根据自己的意愿自由定制[TestNG报告。](https://testng.org/doc/)为此，我们在 TestNG 中使用了两个接口：

-   ITestListener接口
-   IReporter接口

我们可以在 TestNG 中生成 PDF 报告而不是 HTML 报告吗？
是的，TestNG 允许生成 PDF 报告。测试人员需要为此下载外部 Java API，并阅读有关如何使用它们的文档。此外，它们很容易通过 Internet 获得。

TestNG 报告需要外部代码编写吗？
不，不需要编写任何代码来在 TestNG 中生成报告。换句话说，报告生成是默认发生的。

TestNG 生成报告的两种方式是什么？
我们可以通过两种方式生成 TestNG 报告：

-   可通过电子邮件发送的报告
-   指数报告