今天我们将尝试理解从 Eclipse 通过 Maven 发送电子邮件报告的非常重要的概念。

我们知道[Maven](https://toolsqa.com/maven/maven-introduction/)消除了下载 jar 并将它们添加到构建路径的负担，而是我们现在添加自动从存储库为我们下载 jar 的依赖项。如果Maven对你来说是新手，我建议你阅读以下教程以了解有关 Maven 的更多信息：

-   [Maven简介](https://toolsqa.com/maven/maven-introduction)
-   [在 Eclipse IDE 中安装 Maven](https://toolsqa.com/maven/how-to-install-maven-eclipse-ide/)
-   [如何在 Eclipse 中新建 Maven 项目](https://toolsqa.com/maven/create-new-maven-project-eclipse/)

我们知道为我们执行的测试生成报告并将它们发送给团队或经理是多么重要。在[Selenium](https://toolsqa.com/selenium-webdriver/selenium-tutorial/)中，我们有三个选项来通知测试结果。

1.  使用 java 代码 ( JavaAPI ) 发送邮件
2.  配置 Jenkins (CI) 以发送报告
3.  使用 Maven Post-Man 插件通过 eclipse 发送报告

## 从 Eclipse 使用 Maven 将报告自动发送到电子邮件的步骤

现在让我们关注使用Maven 插件的第三个选项。要实现相同的目的，需要执行以下步骤：

1.  创建一个新的 Maven 项目
2.  创建一个 BasicTest 来对两个数字求和
3.  为 TestNG 和 ExtentReport 添加 Maven 依赖项
4.  为项目创建 TestNg Xml
5.  添加 Surfire 插件
6.  添加 Post-Man 插件并运行 Maven 测试

### 第 1 步：创建一个新的 Maven 项目

1：打开Eclipse点击New->Project

![使用 Eclipse 中的 Maven 将报告自动发送到电子邮件](https://www.toolsqa.com/gallery/Maven/1.Send%20Reports%20Automatically%20to%20Email%20using%20Maven%20from%20Eclipse.png)

2) 选择Maven 项目 ，然后单击下一步按钮

![Maven_Sending_Email_15](https://www.toolsqa.com/gallery/Maven/2.Maven_Sending_Email_15.png)

3)选择创建一个简单的项目 ，然后单击下一步

![Maven_Sending_Email_15](https://www.toolsqa.com/gallery/Maven/3.Maven_Sending_Email_15.png)

4) 输入Group Id和Artifact Id ，然后单击Finish。它只不过是项目的名称，所以这可以由你选择，但为了本教程的简单性，我建议保持不变。

![Maven_Sending_Email_15](https://www.toolsqa.com/gallery/Maven/4.Maven_Sending_Email_15.png)

5)当你点击Eclipse IDE左侧可用的Package Explorer时，它会显示上面创建的SendMavenEmail 项目如下

![Maven_Sending_Email_15](https://www.toolsqa.com/gallery/Maven/5.Maven_Sending_Email_15.png)

### 第 2 步：创建 BasicTest 以对两个数字求和

1)在src/test/java 下创建Package basicTests 。完成后，还要在其中创建一个类文件BasicTest。

![Maven_Sending_Email_15](https://www.toolsqa.com/gallery/Maven/6.Maven_Sending_Email_15.png)

1.  在测试中粘贴以下代码。

```java
package basicTests;

	import org.testng.Assert;
	import org.testng.annotations.Test;
	import com.relevantcodes.extentreports.ExtentReports;
	import com.relevantcodes.extentreports.ExtentTest;
	import com.relevantcodes.extentreports.LogStatus;

public class BasicTest {
	//Creating ExtentReport and ExtentTest  reference values
	ExtentReports report;
	ExtentTest logger;
	
	@Test
	public void verifysum(){
		
		//Create object for Report with filepath
		report=new ExtentReports("./Reports/TestReport.html");
		
		//Start the test
		logger=report.startTest("VerifySum");
	
		//Log the status in report
		logger.log(LogStatus.INFO, "calc started ");
	
		int a=50,b=80,c;
		c=a+b;
		Assert.assertEquals(c, 130);
		
		//Pass the test in report
		logger.log(LogStatus.PASS, "Test Verified");
		
		//End the test
		report.endTest(logger);
		
		//Flush the data to report
		report.flush();
		}
}
```

1.  截至目前，代码将在测试中显示许多错误。这是因为我们在测试中使用了[TestNG](https://toolsqa.com/testng/testng-tutorial/)和ExtentReport，并且在项目中没有引用相同的库。所以下一步是使用 Maven 管理依赖项。

![Maven_Sending_Email_15](https://www.toolsqa.com/gallery/Maven/7.Maven_Sending_Email_15.png)

### 第 3 步：为 TestNG 和 ExtentReport 添加 Maven 依赖项

1.   在Project Explorer中双击pom.xml将其打开。稍后单击底部的pom.xml选项卡以添加依赖项。

![Maven_Sending_Email_15](https://www.toolsqa.com/gallery/Maven/8.Maven_Sending_Email_15.png)

1.  为TestNG和ExtentReport添加依赖项 xml 代码，然后按CTRL + S保存文件，该文件将自动开始下载 jars。

```java
	<dependencies>
	<!-- Extent Report dependency -->
		<dependency>
			<groupId>com.relevantcodes</groupId>
			<artifactId>extentreports</artifactId>
			<version>2.41.1</version>
		</dependency>	
	<!-- TestNG dependency -->
		<dependency>
			<groupId>org.testng</groupId>
			<artifactId>testng</artifactId>
			<version>6.9.4</version>
			<scope>test</scope>
		</dependency> 
	</dependencies>
```

pom文件现在看起来像这样。

![Maven_Sending_Email_15](https://www.toolsqa.com/gallery/Maven/9.Maven_Sending_Email_15.png)

3) 右键单击 BasicTest.java 并单击Run as TestNG。确保你测试通过并且找到相同的报告。

![Maven_Sending_Email_15](https://www.toolsqa.com/gallery/Maven/10.Maven_Sending_Email_15.png)

### 第 4 步：为项目创建 TestNg Xml

1)创建TestNg Xml文件右键单击BasicTest => TestNG => Convert to TestNG

![Maven_Sending_Email_15](https://www.toolsqa.com/gallery/Maven/11.Maven_Sending_Email_15.png)

2) 将 TestNG.xml 编辑为任何名称，如 SendEmail.xml，或者你可以将其保留为 TestNG.xml。单击完成。

![Maven_Sending_Email_15](https://www.toolsqa.com/gallery/Maven/11.Maven_Sending_Email_15.png)

请注意，在 Project Explorer 底部创建了一个名为testng.xml 的新文件。

### 第 5 步：添加 Surfire 插件

Surefire插件在构建生命周期的测试阶段用于执行应用程序的单元测试。它以 2 种不同的文件格式生成报告，如纯文本文件、xml 文件和 html 文件。即使你使用TestNG或Junits框架进行报告，也必须使用此插件，因为它可以帮助 Maven 识别测试。

1.  将以下 XML 代码添加到 pom.xml。

```java
<build> 
		<plugins>
		<!-- Suirefire plugin to run xml files --> 
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-surefire-plugin</artifactId>
				<version>2.18.1</version>
				<configuration>
					<suiteXmlFiles>
				<!-- TestNG suite XML files -->
					<suiteXmlFile>SendEmail.xml</suiteXmlFile>
					</suiteXmlFiles>
				</configuration>
			</plugin>
		</plugins>
</build>
```

我将我的 xml 文件命名为testng.xml。如果你给出了不同的名称，请在`<SuitexmlFile>`标签之间输入。

![Maven_Sending_Email_15](https://www.toolsqa.com/gallery/Maven/12.Maven_Sending_Email_15.png)

2)我们可以通过pom.xml右键单击运行xml文件 并选择 Run as Maven Test。

![Maven_Sending_Email_15](https://www.toolsqa.com/gallery/Maven/13.Maven_Sending_Email_15.png)

一旦构建成功。是时候在 pom.xml 中添加 Post-Man 插件了。

### 第 6 步：添加 Post-Man 插件并运行 Maven 测试

Postman插件允许你从构建中发送电子邮件。如果在构建过程中满足特定条件，则目标可用于发送邮件。或者你可以随时发送邮件。

1.  在 pom.xml 中添加以下 xml 代码以将 PostMan 插件添加到项目中。

```java
<!-- Post-Man plugin -->
		 <plugin>
			<groupId>ch.fortysix</groupId>
			<artifactId>maven-postman-plugin</artifactId>
			<executions>
				<execution>
				
				<id>send a mail</id>
				<phase>test</phase>
				<goals>
				<goal>send-mail</goal>
				</goals>
				<inherited>true</inherited>
				
				<configuration>
					<!-- From Email address -->
					<from>from@gmail.com</from>
					
					<!--  Email subject -->
					<subject>Test Automation Report</subject>
					
					<!-- Fail the build if the mail doesnt reach -->
					<failonerror>true</failonerror>
					
					<!-- host -->
					<mailhost>smtp.gmail.com</mailhost>
					<!-- port of the host -->
					<mailport>465</mailport>
					<mailssl>true</mailssl>
					<mailAltConfig>true</mailAltConfig>
					
					<!-- Email Authentication(USername and Password) -->
					<mailuser>yourEmail@gmail.com</mailuser>
					<mailpassword>yourPassword</mailpassword>
					
					<receivers>
						<!-- To Email address -->
						<receiver>to@gmail.com</receiver>
					</receivers>
					
					<fileSets>
					<fileSet>
						<!-- Report directory Path -->
						<directory>C://workspace//SendMavenEmail//Reports</directory>
						<includes>
							<!-- Report file name -->
							<include>TestReport.html</include>
						</includes>
						<!-- Use Regular Expressions like /.html if you want all the html files to send-->
						</fileSet>
					</fileSets>				
				
				</configuration>
				</execution>
			</executions>
		</plugin>
```

完整的 POM.xml 如下：

```java
<project xmlns="https://maven.apache.org/POM/4.0.0" xmlns:xsi="https://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="https://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>SendMavenEmail</groupId>
  <artifactId>SendMavenEmail</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <dependencies>
	   <dependency>
		    <groupId>com.relevantcodes</groupId>
		    <artifactId>extentreports</artifactId>
		    <version>2.41.1</version>
	  </dependency>
	  <dependency>
		  	<groupId>org.testng</groupId>
		  	<artifactId>testng</artifactId>
		  	<version>6.9.4</version>
		  	<scope>test</scope>
	  </dependency>
  </dependencies>
  <build> 
	 <plugins>
		 <!-- Suirefire plugin to run xml files --> 
		 <plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-surefire-plugin</artifactId>
			<version>2.18.1</version>
			<configuration>
				<suiteXmlFiles>
					<!-- TestNG suite XML files -->
					<suiteXmlFile>testng.xml</suiteXmlFile>
				</suiteXmlFiles>
			</configuration>
		 </plugin>
	<!-- Post-Man plugin -->
		 <plugin>
			<groupId>ch.fortysix</groupId>
			<artifactId>maven-postman-plugin</artifactId>
			<executions>
				<execution>
				
				<id>send a mail</id>
				<phase>test</phase>
				<goals>
				<goal>send-mail</goal>
				</goals>
				<inherited>true</inherited>
				
				<configuration>
					<!-- From Email address -->
					<from>from@gmail.com</from>
					
					<!--  Email subject -->
					<subject>Test Automation Report</subject>
					
					<!-- Fail the build if the mail doesnt reach -->
					<failonerror>true</failonerror>
					
					<!-- host -->
					<mailhost>smtp.gmail.com</mailhost>
					<!-- port of the host -->
					<mailport>465</mailport>
					<mailssl>true</mailssl>
					<mailAltConfig>true</mailAltConfig>
					
					<!-- Email Authentication(USername and Password) -->
					<mailuser>yourEmail@mail.com</mailuser>
					<mailpassword>yourPassword</mailpassword>
					
					<receivers>
						<!-- To Email address -->
						<receiver>to@gmail.com</receiver>
					</receivers>
					
					<fileSets>
					<fileSet>
						<!-- Report directory Path -->
						<directory>C://workspace//SendMavenEmail//Reports</directory>
						<includes>
							<!-- Report file name -->
							<include>TestReport.html</include>
						</includes>
						<!-- Use Regular Expressions like /.html if you want all the html files to send-->
						</fileSet>
					</fileSets>				
				
				</configuration>
				</execution>
			</executions>
		</plugin>
	
	</plugins>		
  </build>			
</project>
```

1.  按CTRL + S 保存文件并确保 POM.xml 中没有任何格式错误。右键单击POM.xml => 以 Maven 测试运行

![Maven_Sending_Email_17](https://www.toolsqa.com/gallery/Maven/14.Maven_Sending_Email_17.png)

测试后。Maven 邮递员会将报告发送到电子邮件。你将收到收件人标签中指定电子邮件的电子邮件。

![Maven_Sending_Email_15](https://www.toolsqa.com/gallery/Maven/15.Maven_Sending_Email_15.png)

### 让我们了解一下Post-Man插件

-   failonerror  => 设置为true。如果我们在构建中遇到错误，它将停止发送电子邮件
-   mailhost => 为 Gmail指定主机名smtp.gmail.com
-   mailport => 端口号为465 和mailssl => 设置为true
-   mailuser => 输入邮件帐户的用户名
-   mailpassword => 输入密码以访问邮件
-   receiver => 提供你要向其发送报告的电子邮件地址
-   directory => 提供Report Directory，它从中获取报告
-   include => 给出报告名称。 我们可以使用正则表达式发送目录“ /.html ”中的所有 html 文件

### 错误及解决方法：

当我使用 Post-Man 插件时，我确实遇到了以下错误。所以我想分享解决方案：

1.  [错误]将电子邮件发送到以下服务器失败：smtp.gmail.com:465: AuthenticationFailedException -> [帮助 1] 解决方案：当用户名或密码或电子邮件格式有问题时，我们会遇到此错误
2.  [错误]将电子邮件发送到以下服务器失败：smtp.gmail.com:465: AuthenticationRequired -> [Help 1] 解决方案：这是一个非常有趣的问题。如果我们必须从外部来源(而不是从gmail 登录)我们需要启用安全性较低的选项“ ON ”。这是我们如何做到这一点

-   转到“我的帐户”中的“不太安全的应用程序”部分。
-   在“访问安全性较低的应用程序”旁边，选择打开。(Google Apps 用户请注意：如果你的管理员锁定了不太安全的应用程序帐户访问权限，则此设置会隐藏。)

1.  [错误]将电子邮件发送到以下服务器失败：smtp.gmail.com:587:530 5.7.0 必须先发出 STARTTLS 命令。y2sm9686661pdm.31 - gsmtp -> 解决方案：启用以下两个选项 <mailssl>true</mailssl> <mailAltConfig>true</mailAltConfig>
2.  [错误]你的邮件帐户收到以下邮件以确认授权应用程序使用：

![Maven_Sending_Email_15](https://www.toolsqa.com/gallery/Maven/16.Maven_Sending_Email_15.png)

我们希望这篇文章能帮助你达到目的，用另一个教程来吸引你们……………………..