在本教程中，我们不会查看使用 AutoIt 工具通过 Selenium 自动下载、上传和处理用户身份验证的代码，而是将学习如何详细使用 AutoIt，以便我们可以为任何 Windows 操作创建自己的脚本.

## Selenium 中需要第三方工具

Web 应用程序并不总是局限于完全在 Web 上运行。有时他们需要与桌面交互以执行下载和上传等操作。在 Selenium 中自动化这些类型的工作流是很棘手的。Selenium 仅限于自动化浏览器，因此桌面窗口超出了范围。如果你希望自动化从浏览器到桌面再回到 Selenium 的工作流，那么一点点 AutoIt 是合适的。

## 什么是AutoIt

AutoIt v3 是一种类似于 BASIC 的免费软件脚本语言，设计用于自动化 Windows GUI 和一般脚本。它使用模拟击键、鼠标移动和窗口/控件操作的组合，以便以其他语言(例如 VBScript 和 SendKeys)不可能或不可靠的方式自动执行任务AutoIt 也非常小巧、独立，可以在所有版本的 Windows 上开箱即用，不需要烦人的“运行时”！

在外行人的术语中，AutoIt 只是另一个自动化工具，如 Selenium，但与 Selenium 不同的是，它用于桌面自动化而不是 Web 自动化。它是一个强大的工具，它不仅可以自动执行桌面窗口、按钮和表单，还可以自动执行鼠标移动和击键。就像 Selenium IDE 一样，它还为你提供了录制功能，可以生成脚本供你在测试中使用相同的脚本。

## AutoIt 功能

1.  易于学习：它只是另一种脚本语言，非常易于使用。在 AutoIt 的帮助菜单下，它为你提供了所有可以使用的功能和方法，并带有详细的解释和示例。
2.  模拟击键：在测试中需要使用击键的地方，你可以使用它，例如在任何对话框上按回车键并在弹出窗口中输入用户名和密码，这是你无法使用 Selenium 模拟的。
3.  模拟鼠标移动：就像击键一样，在某些情况下你需要模拟鼠标移动，这是解决这些情况的最简单方法。

4) 脚本可以编译成独立的可执行文件：这意味着你不需要任何 IDE 来运行你的脚本，你可以轻松地将你的自动化脚本转换为可以独立运行的 .exe 文件。

1.  Windows 管理：你可以期望移动、隐藏、显示、调整大小、激活、关闭以及几乎可以对窗口执行你想要的操作。Windows 可以通过标题、窗口上的文本、大小、位置、类甚至内部 Win32 API 句柄来引用。
2.  Windows 控件：直接获取有关编辑框、复选框、列表框、组合、按钮、状态栏的信息并与之交互，而不会丢失击键的风险。甚至可以在不活动的窗口中使用控件！
3.  详细的帮助文件和基于社区的大型支持论坛：你想到 Windows 上的任何操作，都会在帮助文件中找到它。你遇到任何问题或卡在任何地方，大量用户会在那里为你提供帮助。

简而言之，我们无法用 Selenium 处理的任何窗口、鼠标和击键模拟都可以用 AutoIt 处理。我们需要做的就是使用在 AutoIt 工具的帮助下生成的 Selenium 中的脚本。

## 下载 AutoIt v3 的步骤

1.  转到 AutoIt 网站并导航到[下载](https://www.autoitscript.com/site/autoit/downloads/) 页面。它将显示最新版本。下载最多的是 AutoIt，点击它。

![汽车](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Auto.jpg)

1.  它将打开一个弹出框，单击“保存文件”按钮。

![自动1](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Auto-1.jpg)

1.  下载完成后，双击.exe 文件，它将要求你同意其条款和条件。单击“我同意”按钮。

![自动2](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Auto-2.jpg)

1.  完成该过程并在安装结束时单击“完成”按钮。

![自动4](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Auto-4.jpg)

1.  转到你的程序菜单并查看 AutoIt 文件夹。如果你有 32 位系统，那么你的文件夹将如下图所示。

![汽车5](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Auto-5.jpg)

如果你有 64 位系统并且在安装过程中你选择了默认的 x86 配置，那么你的文件夹将如下图所示。

![Auto-5.1](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Auto-5.1.jpg)

## AutoIt 脚本的录制

是的，我知道你很惊讶地发现 AutoIt 还具有有助于自动生成脚本的录制功能。但是为此我们需要使用它的名为 AutoIt 脚本编辑器的扩展。虽然 AutoIt 也安装了脚本编辑器，但它的功能非常有限。

## 下载 AutoIt 脚本编辑器的步骤

1) 转到 AutoIt 网站并导航到 [下载](https://www.autoitscript.com/site/autoit/downloads/) 页面。它将显示最新版本。第二个下载是AutoIt Script Editor， 点击 它。

![AutoS](https://www.toolsqa.com/gallery/selnium%20webdriver/7.AutoS.jpg)

1.  将打开一个新页面，单击“ SciTE4AutoIt3.exe ”最顶部的链接，然后按照该过程进行操作，直到安装完成。

![AutoS-1](https://www.toolsqa.com/gallery/selnium%20webdriver/8.AutoS-1.jpg)

## 使用 AutoIt 在 Selenium 中上传文件

上传文件分为四个步骤：

第 1 步：识别 Windows 控件

第 2 步：使用已识别的窗口控件构建 AutoIt 脚本

第 3 步：编译 .au3 脚本并将其转换为 .exe 文件

第 4 步：将 .exe 文件调用到 Selenium 测试用例中

### 第 1 步：识别 Windows 控件

1.  导航到ToolsQA 的[练习表格](https://demoqa.com/automation-practice-form)页面。
2.  单击个人资料图片列的“浏览”按钮。它将打开一个用于“文件上传”的 Windows 框。
3.  现在转到开始 > 所有程序 > AutoIt v3 并打开“ SciTE 脚本编辑器”。它将打开一个编辑器窗口，我们可以在其中编写 AutoIt 的自动化脚本。

在编写脚本之前，我们应该知道我们需要编写什么，因为我们是这个工具的新手，我们甚至不知道它遵循什么语言以及脚本是什么样子。要获得帮助，请从 SciTE 脚本编辑器中打开“帮助”。

由于我们对触发命令感兴趣，只需在帮助的搜索文本字段中键入“命令”。它将提供你可以在测试中使用的所有命令。

![自动7](https://www.toolsqa.com/gallery/selnium%20webdriver/9.Auto-7.jpg)

打开几个命令并查看描述，它通过描述性示例解释了每个方法的语法、参数和详细描述。

1.  现在我们需要执行的第一个任务是单击上传文件窗口框的“编辑”字段。要获取焦点在编辑字段上的命令，请从帮助中打开“ ControlFocus ”。

![自动8](https://www.toolsqa.com/gallery/selnium%20webdriver/10.Auto-8.jpg)

它说我们需要“ title ”、“ text ”和“ ControlID ”来使用ControlFocus方法。要获得这些我们需要帮助识别windows对象。为了识别对象，AutoIt给了我们Windows Info工具，它就像QTP 中的Object Spy和任何浏览器中的 Element Inspector。要打开它，请转到Start > All Program > AutoIt v3 > AutoIt Window Info。

1.  现在将“ Finder Tool ”框拖到你感兴趣的对象上。

![汽车6](https://www.toolsqa.com/gallery/selnium%20webdriver/11.Auto-6.jpg)

你可以看到 Windows Info 工具已经填充了使用该方法所需的所有信息。

### 第 2 步：使用已识别的窗口控件构建 AutoIt 脚本

从“窗口信息”工具中获取信息并填写 ControlFocus 方法`<ControlFocus ( "title", "text", controlID )>`。对于“ title ”，我们可以使用“ Class”、“ hWnd ”或“ title ”，“ text ”是可选的，“ controlId ”是“ Edit1 ”(类名+类实例)，因此我们的最终语句将如下所示：

```java
ControlFocus ( "File Upload", "", "Edit1")

Or

ControlFocus("[CLASS:#32770]", "", "Edit1")

Or 

Local $hWnd = WinWait("[CLASS:#32770]", "", 10)

ControlFocus(hWnd, "", "Edit1")
```

要测试上述命令，你需要先以“ .au3 ”格式保存测试。保存文件后尝试测试所有上述命令。只需按F5或转到SciTE 脚本编辑器上的工具 > 转到。它将在那一刻执行语句 write 并将结果显示在同一个 SciTE 脚本编辑器窗口中。

确保上传文件窗口在后台打开并且控件不在编辑字段上。

![自动9](https://www.toolsqa.com/gallery/selnium%20webdriver/12.Auto-9.jpg)

注意：如果你向下滚动任何命令的帮助窗口，你将获得一个非常易于使用和理解的详细示例。这就是为什么在本教程的开头我说不要只使用 AutoIt 脚本，还要学习如何构建它。

现在让我们完成测试，包括选择文件和按下打开按钮的步骤。

ControlSetText：此命令用于设置编辑字段上的文本。ControlClick： 此命令用于单击操作。

最终脚本将是这样的：

```java
; Wait 10 seconds for the Upload window to appear

  Local$hWnd=WinWait("[CLASS:#32770]","",10)

; Set input focus to the edit control of Upload window using the handle returned by WinWait

  ControlFocus($hWnd,"","Edit1")

; Wait for 2 seconds.

  Sleep(2000)

; Set the File name text on the Edit field

ControlSetText($hWnd, "", "Edit1", "SomeFile.txt")

  Sleep(2000)

; Click on the Open button 

  ControlClick($hWnd, "","Button1");
```

或者

```java
; Wait 10 seconds for the Upload window to appear

  WinWait("[CLASS:#32770]","",10)

; Set input focus to the edit control of Upload window using the handle returned by WinWait

  ControlFocus("File Upload","","Edit1")

  Sleep(2000)

; Set the File name text on the Edit field

  ControlSetText("File Upload", "", "Edit1", "SomeFile.txt")

  Sleep(2000)

; Click on the Open button

  ControlClick("File Upload", "","Button1");
```

### 第 3 步：编译 .au3 脚本并将其转换为 .exe 文件

现在保存上面的脚本，如果你之前没有保存名称，请将其保存为.au3 格式。下一步是将其转换为.exe格式。为此，你需要右键单击 .au3 文件并选择“编译脚本”。

![自动10](https://www.toolsqa.com/gallery/selnium%20webdriver/13.Auto-10.jpg)

注意：确保根据你的机器配置选择“编译脚本”。32位的选择normal version，64位的选择x64或者x86。

### 第 4 步：将 .exe 文件调用到 Selenium 测试用例中

完成编译后，它将在同一文件夹下创建具有相同名称的“ .exe ”文件，并且将使用以下脚本在 Selenium 测试脚本中调用该“ .exe ”文件：

```java
Runtime.getRuntime().exec("D:\AutoIt\AutoItTest.exe");
```

完整的测试将如下所示：

```java
package practiceTestCases;

import java.io.IOException;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;

import org.openqa.selenium.WebDriver;

import org.openqa.selenium.firefox.FirefoxDriver;

public class AutoIt {

	private static WebDriver driver = null;

	public static void main(String[] args) throws IOException, InterruptedException {

	    driver = new FirefoxDriver();

	    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

	    driver.get("https://toolsqa.com/automation-practice-form");

	    driver.findElement(By.id("photo")).click();

	    Runtime.getRuntime().exec("D:\AutoIt\AutoItTest.exe");

	    Thread.sleep(5000);

	    driver.close();

	}

}
```

## 使用 AutoIt 录制的脚本在 Selenium 中上传文件

在使用此功能之前，请确保你已经安装了 AutoIt 脚本编辑器，否则编辑器窗口中不会显示记录器选项。

1.  打开脚本编辑器窗口，将空白文件保存为“ .au3 ”扩展名，然后 在 AutoIt 脚本编辑器上转到工具 > AU3Recorder或Alt + F6 。将打开一个用于录制的窗口。

![AutoIT-3](https://www.toolsqa.com/gallery/selnium%20webdriver/14.AutoIT-3.png)

1.  单击徽标开始录制。你需要确保尽可能避免鼠标交互，并鼓励使用键盘。开始录制后，在已经打开的文件上传窗口中执行你的操作，并通过单击相同的徽标停止录制。你的脚本看起来像这样：

![AutoIT-8](https://www.toolsqa.com/gallery/selnium%20webdriver/15.AutoIT-8.png)

注意：如果上传文件窗口尚未打开并且你在开始录制后打开它，它也会捕获这些步骤，在这种情况下，你需要确保在编译之前删除为这些步骤生成的代码它。

注意：鼠标交互在不同的屏幕分辨率窗口中可能会有所不同，避免使用它并鼓励使用键盘。

1.  现在保存生成的脚本，编译它并相应地在你的 Selenium 测试脚本中使用它。

注意：如果你使用的是 64 位系统并且 WinWaitActivate 报错，请将其更改为 WinWaitActive。

## 使用 AutoIt 在 Selenium 中处理基于 Windows 的身份验证弹出窗口

Selenium 中 AutoIt 脚本的主要用途是处理基于 Windows 的弹出窗口，例如用户名和密码身份验证。大多数时候 AutoIt 可以解决问题，但有时却不行。我很少看到用户说 AutoIt 不工作的讨论，因为 Selenium 脚本卡在弹出窗口中并且在身份验证窗口上执行任何操作之前不会向前移动。在本教程结束时，你还将看到针对这种情况的解决方法。

1.  打开需要认证的Url，打开AutoIt Window Info工具，得到类名和认证窗口的文字。

![AutoIT-1](https://www.toolsqa.com/gallery/selnium%20webdriver/16.AutoIT-1.png)

2) 将“ Finder Tool ”框拖到你感兴趣的对象上，它会向你显示信息。

![AutoIT-2](https://www.toolsqa.com/gallery/selnium%20webdriver/17.AutoIT-2.png)

注意：你一定已经注意到信息工具没有为控件获取任何信息。很可能，在这种情况下，AutoIt 脚本将无法工作，但我们仍然有解决方法。

“基本控制信息”的类信息和实例信息留空。在这种情况下，我们将无法使用“ControlSetText”发送用户名和密码，因为我们没有它的详细信息。但由于它已经识别出主认证窗口，我们可以简单地使用键盘敲击来发送用户名和密码。

1.  让我们记录一下将用户名和密码发送到身份验证窗口的步骤，为此请  在 AutoIt 脚本编辑器上 通过工具 >AU3Recorder 或 Alt + F6激活记录器。![AutoIT-3](https://www.toolsqa.com/gallery/selnium%20webdriver/18.AutoIT-3.png)
2.  左上角的信号表示正在录音。现在输入用户名和密码，不要使用鼠标点击任何字段，只需使用键盘的选项卡按钮即可。

![AutoIT-4](https://www.toolsqa.com/gallery/selnium%20webdriver/19.AutoIT-4.png)

![AutoIT-6](https://www.toolsqa.com/gallery/selnium%20webdriver/20.AutoIT-6.png)

1.  完成录制后，你的代码将如下所示。

![AutoIT-5](https://www.toolsqa.com/gallery/selnium%20webdriver/21.AutoIT-5.png)

注意：我输入的密码是“Test123”，对于大写的“T”，我必须按 Shift 键。

1.  现在保存上面的脚本，如果你之前没有保存名称，请将其保存为.au3 格式。下一步是将其转换为 .exe 格式。为此，你需要右键单击 .au3 文件并选择“编译脚本”。

![自动10](https://www.toolsqa.com/gallery/selnium%20webdriver/22.Auto-10.jpg)

注意：确保根据你的机器配置选择“编译脚本”。如果你是 32 位的，选择普通版本，如果你是 64 位的，选择 x64 或 x86。

1.  完成编译后，它将在同一文件夹下创建具有相同名称的“ .exe ”文件，并且将使用以下脚本在 Selenium 测试脚本中调用该“ .exe ”文件：

```
Runtime.getRuntime().exec("D:\AutoIt\AutoItTest.exe");
```

完整的测试将如下所示：

```java
package practiceTestCases;

import java.io.IOException;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;

import org.openqa.selenium.WebDriver;

import org.openqa.selenium.firefox.FirefoxDriver;

public class AutoIt {

	private static WebDriver driver = null;

	public static void main(String[] args) throws IOException, InterruptedException {

	    driver = new FirefoxDriver();

	    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

	    driver.get("https://www.example.com");

	    Runtime.getRuntime().exec("D:\AutoIt\AutoItTest.exe");

	    Thread.sleep(5000);

	    driver.close();

	}

}
```

## 主要技巧

如果万一你的脚本不起作用，你需要做的就是在打开 Url 之前先调用 AutoIt 脚本。AutoIt 脚本需要几秒钟才能启动，同时你的 Selenium 脚本将打开 url 并显示身份验证窗口。

```java
package practiceTestCases;

import java.io.IOException;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;

import org.openqa.selenium.WebDriver;

import org.openqa.selenium.firefox.FirefoxDriver;

public class AutoIt {

	private static WebDriver driver = null;

	public static void main(String[] args) throws IOException, InterruptedException {

	    driver = new FirefoxDriver();

	    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		Runtime.getRuntime().exec("D:\AutoIt\AutoItTest.exe");

		driver.get("https://www.example.com");

		Thread.sleep(5000); 

		driver.close();

		}

	}
```

它对我有用，希望对你也有用。

身份验证也可以通过浏览器设置来处理，请访问[使用 Selenium Webdriver 的 Http 代理身份验证](https://toolsqa.com/selenium-webdriver/http-proxy-authentication/)