这是创建 Maven 项目的第二种方法。但是这里不是在eclipse之外创建一个项目然后导入到eclipse中，而是直接在eclipse中创建一个maven项目。

## 在 Eclipse 中创建一个新的 Maven 项目

1.  打开你的日食并 转到文件>新建>其他。

![EclipseMaven_1](https://www.toolsqa.com/gallery/Maven/1.EclipseMaven_1.png)

1.  选择Maven 项目并单击下一步。

![EclipseMaven_2](https://www.toolsqa.com/gallery/Maven/2.EclipseMaven_2.png)

1.  取消勾选“ Use default Workspace location ”并在Browse按钮的帮助下选择你想要设置Maven 项目的工作区。

![EclipseMaven_3](https://www.toolsqa.com/gallery/Maven/3.EclipseMaven_3.png)

1.  选择原型，现在只需选择“ maven-aechetype-quickstart ”并单击“下一步”。

![EclipseMaven_4](https://www.toolsqa.com/gallery/Maven/4.EclipseMaven_4.png)

1.  指定组 ID和工件 ID，然后单击“完成”。

![EclipseMaven_5](https://www.toolsqa.com/gallery/Maven/5.EclipseMaven_5.png)

注意： 此处的“artifactId”是你的项目名称。

6)到项目所在位置可以看到新建的maven项目。现在打开 位于项目文件夹中的pom.xml文件。默认情况下，POM 是这样生成的：

```java
<project xmlns="https://maven.apache.org/POM/4.0.0" xmlns:xsi="https://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="https://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>ToolsQA</groupId>
  <artifactId>DemoMavenEclipseProject</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <packaging>jar</packaging>

  <name>DemoMavenEclipseProject</name>
  <url>http://maven.apache.org</url>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>

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

7)查看Maven项目的默认文件夹结构。

![EclipseMaven_6](https://www.toolsqa.com/gallery/Maven/6.EclipseMaven_6.png)

注意： 位于src > test > java > PackageName下的测试用例只会被 Maven 视为测试，如果将测试用例放在其他文件夹中，其余的将被忽略。

1.  使用最新的Junit修改XML并保存XML。

```java
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.11</version>
      <scope>test</scope>
    </dependency>
```

注意：在后面的章节中，我们将了解为什么要添加依赖项以及从哪里获取 artifactId & version。

## 运行你的第一个 Maven 测试

1.  右键单击pom.xml 并转到 运行方式 > Maven 测试。

![EclipseMaven_7](https://www.toolsqa.com/gallery/Maven/7.EclipseMaven_7.png)

1.  在 Eclipse 的控制台窗口中，你会看到这样的信息：

![EclipseMaven_8](https://www.toolsqa.com/gallery/Maven/8.EclipseMaven_8.png)

1.  转到“ SureFire Reports ”文件夹并打开 XML 文件。

![EclipseMaven_9](https://www.toolsqa.com/gallery/Maven/9.EclipseMaven_9.png)

1.  它将显示JUnit测试的结果。

![EclipseMaven_10](https://www.toolsqa.com/gallery/Maven/10.EclipseMaven_10.png)