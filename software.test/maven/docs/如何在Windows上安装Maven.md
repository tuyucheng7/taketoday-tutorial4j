Maven 是用于基于 Java 的应用程序开发的构建和依赖管理工具。就像其他基于 Java 的开发工具一样，它不是作为 Windows 服务安装的，而是使用 Windows 环境变量配置的。可以从以下位置访问这些变量：

所有控制面板项 > 系统 > 高级系统设置 > 环境变量

我们需要执行某些步骤来设置 Maven，例如在系统上安装 Java、设置环境变量和下载 Eclipse IDE。在本章中，我们将通过这些步骤来在 Windows 中设置 Maven。

## 第 1 步：下载并安装 Java

如果你的系统上尚未安装 Java，请安装它。Java 最新版本可以从[这里](https://www.oracle.com/technetwork/java/javase/downloads/index.html)安装。如果你遇到任何问题，请完成[下载和安装 Java](https://toolsqa.com/selenium-webdriver/install-java/)的步骤。

1.  要检查你机器上安装的 Java 版本，请转到“运行”并键入“ cmd ”以启动命令提示符。
2.  现在键入“ Java -version ”并按Enter。

![JavaVersion_2](https://www.toolsqa.com/gallery/Maven/1.JavaVersion_2.png)

## 第 2 步：设置 Java 环境变量

Java 安装完成后，设置Java 环境变量。要设置Java 环境变量，请打开系统设置。

1.  转到“我的电脑”并通过右键单击“我的电脑”中的空白区域来选择“属性” 。
2.  单击“更改设置”。

![SetUpMaven_1](https://www.toolsqa.com/gallery/Maven/2.SetUpMaven_1.png)

1.  将显示一个弹出窗口，单击“高级”选项卡，然后单击“环境变量”。

![设置Maven_2](https://www.toolsqa.com/gallery/Maven/3.SetUpMaven_2.png)

1.  单击“系统变量”下的新建按钮

![设置Maven_3](https://www.toolsqa.com/gallery/Maven/4.SetUpMaven_3.png)

1.  在变量名框中写入“ JAVA_HOME ”，在变量值框中输入“ C:\Program Files\Java\jdk1.8.0_20 ” JDK路径，然后单击“确定”。

![设置Maven_4](https://www.toolsqa.com/gallery/Maven/5.SetUpMaven_4.png)

1.  新创建的Java变量的条目将显示在“系统变量..”下

![设置Maven_5](https://www.toolsqa.com/gallery/Maven/6.SetUpMaven_5.png)

## 第三步：下载Maven并设置Maven环境变量

1.  下一步是下载 Maven，它可以从这个[位置](https://maven.apache.org/download.cgi)下载。

![设置Maven_9](https://www.toolsqa.com/gallery/Maven/7.SetUpMaven_9.png)

2)将其提取到某个位置。我已将其提取到“ C:/apache-maven-3.2.3 ”。你可以选择自己的位置。

1.  以与上面设置 Java 环境变量相同的方式设置Maven环境变量。

在“变量名”框中写入“ MAVEN_HOME ”，然后在“变量值 ”框中 输入“8C:\apache-maven-3.2.3” Maven 路径， 然后单击 “确定”。

![设置Maven_6](https://www.toolsqa.com/gallery/Maven/8.SetUpMaven_6.png)

新创建的Maven 变量的条目将显示在“系统变量..”下

![SetUpMaven_12](https://www.toolsqa.com/gallery/Maven/9.SetUpMaven_12.png)

## 步骤 4) 更新路径变量

要从命令提示符运行Maven ，必须使用 Maven 的安装“ bin ”目录更新路径变量。

1.  转到“ 我的电脑”并 通过 右键单击“我的电脑 ”中的空白区域来 选择 “属性” 。

2) 单击“更改设置”。

1.  将显示一个弹出窗口，单击 “高级”选项卡，然后 单击 “环境变量”。

4) 单击“ UserName 的用户变量”下的 编辑 按钮，其中UserName 是你的机器名称。

1.  在“变量名”框中写入“ PATH ”，然后在“变量值”框中输入“ C:\apache-maven-3.2.3\bin ”Maven 路径， 然后单击 “确定”。

![SetUpMaven_13](https://www.toolsqa.com/gallery/Maven/10.SetUpMaven_13.png)

## 步骤 5) 测试 Maven 安装

Maven 安装完成。现在，让我们从 Windows 命令提示符下测试它。转到“运行” 并在应用程序位置搜索框中键入“ cmd ”。将打开一个新的命令提示符。 在命令提示符下键入 mvn -version 。

![SetUpMaven_10](https://www.toolsqa.com/gallery/Maven/11.SetUpMaven_10.png)

点击ENTER。

![SetUpMaven_11](https://www.toolsqa.com/gallery/Maven/12.SetUpMaven_11.png)

