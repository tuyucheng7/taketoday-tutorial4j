在 Safari 浏览器中运行 Selenium 测试与在其他浏览器中运行完全相同。但是，你必须准备好 Safari 浏览器才能理解 Selenium WebDriver 命令。让我们学习如何设置你的 Safari 浏览器以及如何编写你的第一个测试。

## 如何在 Safari 浏览器中运行 Selenium 测试？

### 第 1 步 - 为 Safari 浏览器设置 WebDriver 扩展

1.  下载 Safari 浏览器扩展- 下载最新版本的 Safari 浏览器扩展。
2.  安装 Safari 浏览器扩展 - 转到下载文件的文件夹并双击它。你会得到一个提示，如下图所示，选择“安装”

![下载文件夹](https://www.toolsqa.com/gallery/selnium%20webdriver/1.DownloadFolder.jpg)

下载文件夹图片

1.  启用 WebDriver 浏览器扩展 -现在打开Safari浏览器上的首选项面板。转到Safari >> 首选项并 打开首选项窗口。

在首选项窗口中选择扩展。你会在扩展列表中找到 Selenium Web 驱动程序，选中该复选框。如下图所示

![下载文件夹](https://www.toolsqa.com/gallery/selnium%20webdriver/2.DownloadFolder.jpg)

Safari 扩展窗口

注意：确保你启用了“启用 WebDriver ”复选框。

1.  重新启动你的浏览器 - 你在这里所要做的就是重新启动你的浏览器。

### 编写 Selenium WebDriver 代码来启动 Safari

正如我之前所说，在 Safari 中运行 selenium 测试与使用 Firefox 或 IE 完全相似。Safari 浏览器由org.openqa.selenium.safari 包中名为SafariDriver的类表示。 我们所要做的就是创建一个 SafariDriver 类的实例。下面是一个示例代码来做到这一点

```java
package Usage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;

public class SafariUsage {

	public static void main(String[] args)
	{
		WebDriver driver = new SafariDriver();
		driver.get("https://store.demoqa.com");

		//Find some element on DemoQa.com
		WebElement element = driver.findElement(By.id("login"));
		element.click();

	}

}
```

在这里你可以看到，我们所要做的就是创建一个SafariDriver实例，并像我们一直在其他浏览器中使用它的常规WebDriver一样使用它。

问题：

1.  Safari仅支持http://和https://协议。
2.  Safari 无法处理警报，因此我们必须在 Safari 的情况下抑制警报。我们将在另一章中学习这一点。