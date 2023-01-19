有两种创建 Maven 项目的方法。一个是使用命令提示符，另一个是在 eclipse IDE 中使用。让我们一个一个地看。

## 从命令提示符创建一个新的 Maven 项目

1.  转到“运行”并键入“ cmd ”以打开命令提示符。

![项目_1](https://www.toolsqa.com/gallery/Maven/1.Project_1.png)

1.  浏览到要设置项目的文件夹，然后键入以下命令：

mvn 原型：生成 -DgroupId=ToolsQA -DartifactId=DemoMavenProject -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false

![项目_2](https://www.toolsqa.com/gallery/Maven/2.Project_2.png)

注意：'cd..' 用于返回上一个文件夹，'cd foldername' 用于进入文件夹。

这里“ DartifactId ”是你的项目名称，“ DarchetypeArtifactId ”是 Maven 项目的 type 。有不同类型的 Maven 项目，如Web 项目、Java 项目等。你可以在[此处查看](https://maven.apache.org/guides/introduction/introduction-to-archetypes.html)完整列表。

键入上述命令后按Enter后，它将开始创建 Maven 项目。

![项目_3](https://www.toolsqa.com/gallery/Maven/3.Project_3.png)

注意：如果构建失败，请检查pom.xml文件中的 Maven 版本号。它应该与你机器上安装的 Maven 版本相匹配。在 POM“http://maven.apache.org/POM/3.2.3”中寻找它。从 POM 中提到的所有地方更正版本。

1.  到项目所在位置可以看到新建的maven项目。现在打开位于项目文件夹中的pom.xml文件。默认情况下，POM 是这样生成的：

```java
<project xmlns="https://maven.apache.org/POM/3.2.3" xmlns:xsi="https://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="https://maven.apache.org/POM/3.2.3 http://maven.apache.org/maven-v3_2_3.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>ToolsQA</groupId>
  <artifactId>DemoMavenProject</artifactId>
  <packaging>jar</packaging>
  <version>1.0-SNAPSHOT</version>
  <name>DemoMavenProject</name>
  <url>http://maven.apache.org</url>
  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>3.8.1</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
</project>
```

1.  查看 Maven 项目的默认文件夹结构。

![项目_4](https://www.toolsqa.com/gallery/Maven/4.Project_4.png)

注意： 位于src > test > java > ToolsQA下的测试用例只会被 Maven 视为测试，如果将测试用例放在其他文件夹中，其余的将被忽略。

## 与 Eclipse 一起工作的 Maven 项目

1.  打开命令提示符并浏览到你的 Maven 项目并键入此“ mvn eclipse:eclipse ”

![项目_5](https://www.toolsqa.com/gallery/Maven/5.Project_5.png)

你的项目现在与 Eclipse IDE 兼容。

## 将 Maven 项目导入 Eclipse

1.  打开 Eclipse并转到文件 > 导入。

![项目_6](https://www.toolsqa.com/gallery/Maven/6.Project_6.png)

2) 点击Existing Projects into Workspace。

![项目_7](https://www.toolsqa.com/gallery/Maven/7.Project_7.png)

1.  浏览到 Maven 项目文件夹并单击完成。

![项目_8](https://www.toolsqa.com/gallery/Maven/8.Project_8.png)

4) Project Explorer 现在看起来像这样：

![项目_9](https://www.toolsqa.com/gallery/Maven/9.Project_9.png)

1.  修改 POM。默认的pom.xml 太简单了，很多时候你需要添加编译器插件来告诉 Maven 使用哪个 JDK 版本来编译你的项目。

```java
<plugin>
		<groupId>org.apache.maven.plugins</groupId>
		<artifactId>maven-compiler-plugin</artifactId>
		<version>2.3.2</version>
		<configuration>
			<source>1.6</source>
			<target>1.6</target>
		</configuration>
</plugin>
```

将 jUnit 从 3.8.1 更新到最新的 4.11

```java
<dependency>
	<groupId>junit</groupId>
	<artifactId>junit</artifactId>
	<version>4.11</version>
	<scope>test</scope>
</dependency>
```

现在你的 POM 将如下所示：

```java
<project xmlns="https://maven.apache.org/POM/3.2.3" xmlns:xsi="https://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="https://maven.apache.org/POM/3.2.3 http://maven.apache.org/maven-v3_2_3.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>ToolsQA</groupId>
  <artifactId>DemoMavenProject</artifactId>
  <packaging>jar</packaging>
  <version>1.0-SNAPSHOT</version>
  <name>DemoMavenProject</name>
  <url>http://maven.apache.org</url>
  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.11</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
  <build>
	  <plugins>
		<plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-compiler-plugin</artifactId>
			<version>2.3.2</version>
			<configuration>
				<source>1.6</source>
				<target>1.6</target>
			</configuration>
		</plugin>
	  </plugins>
	</build>
</project>
```

修改 POM 后，再次执行Maven Project to work with Eclipse步骤。打开 命令提示符 并浏览到你的 Maven 项目并键入此“ mvn eclipse:eclipse ”

![项目_10](https://www.toolsqa.com/gallery/Maven/10.Project_10.png)

## 运行你的第一个 Maven 测试

1.  右键单击pom.xml并转到运行方式 > Maven 测试。

![项目_11](https://www.toolsqa.com/gallery/Maven/11.Project_11.png)

1.  在 Eclipse 的控制台窗口中，你会看到这样的信息：

![项目_12](https://www.toolsqa.com/gallery/Maven/12.Project_12.png)

1.  转到“ SureFire Reports ”文件夹并打开 xml 文件。

![项目_13](https://www.toolsqa.com/gallery/Maven/13.Project_13.png)

1.  它将显示 JUnit 测试的结果。

![项目_14](https://www.toolsqa.com/gallery/Maven/14.Project_14.png)