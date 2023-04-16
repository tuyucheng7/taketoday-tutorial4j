在上[一个教程](https://toolsqa.com/jmeter/how-to-install-java-on-mac-os/)中，我们在机器上安装了 Java 8，因为要启动 JMeter，我们需要 Java。在本教程中，我们将安装最新版本的 JMeter，即 JMeter 4 (截至 2018 年 7 月 4 日)，以下是我们将在本教程中介绍的主题。

-   安装 JMeter 4.0 的先决条件
-   JMeter 4.0 亮点
-   如何下载JMeter？
-   如何启动 JMeter？

在安装之前先了解一下JMeter 4.0安装的先决条件和JMeter 4.0引入的特性

### 先决条件

软件要求：由于 JMeter 是纯 Java 软件，Java 应该在你的机器上。JMeter 4.0 仅支持 java 8 和 9。它不支持任何以前版本的 java，也不支持 java 10。

-   [在 Mac 上安装 Java](https://toolsqa.com/jmeter/how-to-install-java-on-mac-os/)
-   [在 Windows 上安装 Java](https://toolsqa.com/selenium-webdriver/install-java/)

操作系统要求： 如果你的操作系统支持 java，那么 JMeter 应该可以在你的系统上正确运行。下面提到的操作系统和在相应操作系统上启动 JMeter 所需的文件。

-   Windows：jmeter.bat(在 Windows 上启动 JMeter)
-   MAC：jmeter.sh(在 MAC 上启动 JMeter)
-   Linux：jmeter.sh(在 Linux 上启动 JMeter)

我们将在后面的部分中讨论这些文件。

### JMeter4.0亮点：

JMeter 4.0 是JMeter 3.3 之后的一个主要版本，它是在其上一个主要版本即JMeter 3.0 两年内发布的。JMeter 4.0 的改进主要集中在用户体验上，牢记这一点，它使用户更容易导航和创建测试。以下是 JMeter 4.0 用户认可的一些功能。

-   在 JMeter 4.0 中，Workbench 不再显示在 GUI 上。
-   JMeter4.0的GUI由白色改为黑色。
-   英语现在是 JMeter 4.0 的默认语言。以前版本的 JMeter 使用计算机的语言环境。
-   大多数使用的元素将首先出现在 JMeter 4.0 中。
-   JMeter 4.0 使用 Java 8 或 9 版本。
-   JSON 断言现在是 JMeter 的核心部分。因此，不再需要 JSON 断言的第三方插件。
-   边界提取器是 JMeter 4.0 中的新功能。它是一个后处理器，在请求中的每个样本请求之后执行，并允许用户使用用户提供的左右边界从服务器响应中提取值。
-   精确的吞吐量计时器，此计时器引入了可变暂停，经过计算可使总吞吐量(例如，根据每分钟的样本数)尽可能接近给定数字。

注意：JSON Assertion 用于对 JSON 文档执行断言。

要更深入地了解 JMeter 4.0 中引入的更改，请参阅[JMeter](https://jmeter.apache.org/changes.html)。

## 如何下载JMeter？

在本节中，我们将下载最新版本的 JMeter。下载 JMeter 与 MAC 和 Windows 类似。在下面的步骤中，我将在 MAC 上下载 JMeter 4.0，但你也可以在 Windows 上执行相同的步骤。

1.  到[Apache Jmeter 的网站](https://jmeter.apache.org/download_jmeter.cgi) 下载JMeter。

![JMeter](https://www.toolsqa.com/gallery/Jmeter/1.JMeter.png)

1.  单击 适用于 MAC 和 Windows 的apache-jmeter-4.0.zip
2.  当你单击 apache-jmeter-4.0.zip 时， JMeter 将开始下载。

![JMeter_下载](https://www.toolsqa.com/gallery/Jmeter/3.JMeter_Download.png)

1.  下载完成后导航到你机器的下载文件夹，在那里你将看到 apache-jmeter-4.0.zip文件。

![Jmeter_Zip](https://www.toolsqa.com/gallery/Jmeter/4.Jmeter_Zip.png)

1.  解压缩文件并将其保存到你的首选位置。对于 MAC，我双击文件解压缩；对于 Windows，你可以右键单击它并解压缩。解压后打开文件夹。就我而言，我已将该文件夹复制到桌面，现在我将打开该文件夹。

![Jmeter_Lib_bin](https://www.toolsqa.com/gallery/Jmeter/5.Jmeter_Lib_bin.png)

-   Bin 文件夹包含用于启动 JMeter 的模板、.bat、.sh、.jar 文件。它还包含 User 和 JMeter 的属性文件。
-   Lib 文件夹包含所有必需的 jar 文件。

![JMeter_bat_sh](https://www.toolsqa.com/gallery/Jmeter/6.JMeter_bat_sh.png)

-   在 Windows 上启动 jmeter GUI 需要 jmeter.bat
-   在 MAC 和 Linux 上启动 jmeter GUI 需要 jmeter.sh

因此，现在我们已经下载了 JMeter，现在我们准备在我们的系统上启动这个 JMeter 的 GUI。

## 如何启动 JMeter？

对于 MAC，导航至终端按 命令 + 空格键。只要你按下 command + space Spotlight 就会出现。

![聚光灯](https://www.toolsqa.com/gallery/Jmeter/7.spotlight.png)

对于 Windows 导航到命令提示符，请按 Window + R 或转到 运行。

注意：如果终端已经打开，请关闭并重新打开它

1. 通过切换到bin 目录并键入以下内容来启动 JMeter：

`<file path>`/apache-jmeter-4.0/bin/jmeter.sh

在我的例子中是：

/Users/mohitgarg/Desktop/apache-jmeter-4.0/bin/jmeter.sh

![Jmeter_Command](https://www.toolsqa.com/gallery/Jmeter/8.Jmeter_Command.png)

1.  输入上述命令后按Enter后，你的终端将如下图所示。

![LaunchJMeter](https://www.toolsqa.com/gallery/Jmeter/9.LaunchJMeter.png)

1.  Apache JMeter 将开始加载。

![Jmeter负载](https://www.toolsqa.com/gallery/Jmeter/10.Jmeter%20load.png)

1.  几秒钟后，JMeter GUI将启动。JMeter GUI 如下所示。

![如何安装JMeter？](https://www.toolsqa.com/gallery/Jmeter/11..How%20to%20Install%20JMeter.png)

注意：要在 Windows 上启动JMeter，只需双击 jmeter.bat文件或转到命令提示符并键入 <文件路径>/apache-jmeter-4.0/bin/jmeter.bat并等待几秒钟 JMeter GUI 将启动。

因此，总结本教程。现在，我们知道如何在 MAC OS 和 Windows 上下载和启动 JMeter。在接下来的教程中，我们将熟悉 JMeter 的 GUI，并最终构建我们的第一个测试计划。