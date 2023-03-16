## 1. 概述

FindBugs是一个开源工具，可用于对Java代码执行**静态分析**。

在本文中，我们将介绍如何在Java项目中设置FindBugs并将其集成到IDE和Maven构建中。

注意：FindBugs项目已被弃用，[SpotBugs](https://spotbugs.github.io/)现在作为其继任者得到积极维护，它适用于最新版本的Java。

## 2. FindBugs Maven插件

### 2.1 Maven配置

为了生成静态分析报告，我们首先需要在pom.xml中添加FindBugs插件：

```xml
<reporting>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>findbugs-maven-plugin</artifactId>
            <version>3.0.4</version>
        </plugin>
    </plugins>
</reporting>
```

你可以在Maven Central查看[最新版本](https://central.sonatype.com/artifact/org.codehaus.mojo/findbugs-maven-plugin/3.0.5)的插件。

### 2.2 报告生成

现在我们已经正确配置了Maven插件，让我们使用mvn site命令生成项目文档。

**该报告在文件夹target/site中生成，文件名称为findbugs.html**。

你还可以运行mvn findbugs:gui命令来启动GUI界面来浏览当前项目生成的报告。

FindBugs插件也可以配置为在某些情况下失败，通过将执行目标check添加到我们的配置中：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>findbugs-maven-plugin</artifactId>
    <version>3.0.4</version>
    <configuration>
        <effort>Max</effort>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

当effort指定为Max时，会执行更完整和更精确的分析，揭示代码中的更多错误，但是，它会消耗更多资源并需要更多时间来完成。

你现在可以运行命令mvn verify来检查构建是否成功，具体取决于运行分析时检测到的缺陷。你还可以通过在插件声明中添加一些基本配置来增强报告生成过程并更好地控制分析：

```xml
<configuration>
    <onlyAnalyze>cn.tuyucheng.taketoday.web.controller.*</onlyAnalyze>
    <omitVisitors>FindNullDeref</omitVisitors>
    <visitors>FindReturnRef</visitors>
</configuration>
```

onlyAnalyze参数声明符合分析条件的类/包的逗号分隔值。visitor/omitVisitors参数也是逗号分隔的值，它们用于指定在分析过程中应该/不应该运行哪些检测器。**请注意，visitors和omitVisitors不能同时使用**。检测器由其类名指定，不需要指定类的包名。你可以[通过该链接](http://findbugs.sourceforge.net/api/edu/umd/cs/findbugs/detect/package-summary.html)查找所有可用检测器类名称的详细信息。

## 3. FindBugs Eclipse插件

### 3.1 安装

FindBugs 插件的 IDE 安装非常简单——你只需使用 Eclipse
中的软件更新功能，更新站点如下：http: [//findbugs.cs.umd.edu/eclipse ](http://findbugs.cs.umd.edu/eclipse)。

为了确保 FindBugs 正确安装在你的 Eclipse 环境中，然后，在 Windows -> Preferences -> Java 下查找标记为FindBugs的选项。

### 3.2 报告浏览

为了使用 FindBugs Eclipse 插件对项目启动静态分析，你需要在包资源管理器中右键单击项目，然后单击标记为find bugs的选项。

启动后，Eclipse 在 Bug Explorer 窗口下显示结果，如下面的屏幕截图所示：

[![错误浏览器](https://www.baeldung.com/wp-content/uploads/2016/11/bug_explorer-300x82.png)](https://www.baeldung.com/wp-content/uploads/2016/11/bug_explorer.png)
从版本 2 开始，FindBugs 开始以 1 到 20 的等级对错误进行排名，以衡量缺陷的严重程度：

- 最可怕：排名在 1 和 4 之间。
- 可怕：排名在 5 和 9 之间。
- 麻烦：排名在 10 到 14 之间。
- 关注度：排名在 15 到 20 之间。

虽然错误等级描述了严重性，但置信度因子反映了这些错误被标记为真实错误的可能性。信心最初被称为优先级，但在新版本中被重新命名。

当然，有些缺陷是可以解释的，它们甚至可以存在而不会对软件的预期行为造成任何损害。这就是为什么在实际情况下，我们需要通过选择一组有限的缺陷在特定项目中激活来正确配置静态分析工具。

### 3.3 Eclipse配置

FindBugs 插件通过提供多种过滤警告和限制结果严格性的方法，可以轻松自定义错误分析策略。你可以通过转到 Window ->
Preferences -> Java -> FindBugs 来检查配置界面：

[![脸书偏好 1](https://www.baeldung.com/wp-content/uploads/2016/11/fb_preferences-1-300x246.png)](https://www.baeldung.com/wp-content/uploads/2016/11/fb_preferences-1.png)

你可以随意取消选中不需要的类别、提高要报告的最低排名、指定要报告的最低置信度以及自定义错误排名的标记——警告、信息或错误。

FindBugs 将缺陷分为许多类别：

- 正确性——收集一般错误，例如无限循环、不恰当地使用equals()等
- 不好的做法，例如异常处理、打开的流、字符串比较等
- 性能，例如空闲对象
- 多线程正确性——收集多线程环境中的同步不一致和各种问题
- 国际化——收集与编码和应用程序国际化相关的问题
- 恶意代码漏洞——收集代码中的漏洞，例如可能被潜在攻击者利用的代码片段
- 安全性——收集与特定协议或 SQL 注入相关的安全漏洞
- Dodgy – 收集[代码异味](https://www.baeldung.com/cs/code-smells)，例如无用的比较、空值检查、未使用的变量等

在Detector 配置选项卡下，你可以检查你应该在项目中遵守的规则：

[![fb 偏好检测器 1](https://www.baeldung.com/wp-content/uploads/2016/11/fb_preferences_detector-1-300x240.png)](https://www.baeldung.com/wp-content/uploads/2016/11/fb_preferences_detector-1.png)

速度属性反映了分析的成本。检测器速度越快，执行它所消耗的资源就越少。

[你可以在官方文档页面上](http://findbugs.sourceforge.net/bugDescriptions.html)找到 FindBugs 识别的详尽错误列表。

在过滤器文件面板下，你可以创建自定义文件过滤器，以包含/排除代码库的部分。此功能很有用 - 例如 -
当你想要防止“非托管”或“垃圾”代码、报告中弹出缺陷，或者可能从测试包中排除所有类时。

## 4. FindBugs IntelliJ IDEA插件

### 4.1 安装

如果你更倾向于IntelliJ IDEA，并且想开始使用FindBugs检查Java代码，你只需从[JetBrains官方站点](https://plugins.jetbrains.com/plugin/3847?pr=idea)获取插件安装包，并将其解压缩到Intellij IDEA安装目录的plugins文件夹中；或者直接在Intellij中转到Settings -> Plugins，搜索FindBugs并安装，等待完成最后重新启动你的IDE。

在撰写本文时，IntelliJ IDEA插件的1.0.1版本刚刚发布，

要确保正确安装了FindBugs插件，请检查“分析”->“FindBugs”下标记为“分析项目代码”的选项。

### 4.2 报告浏览

为了在 IDEA 中启动静态分析，单击“分析项目代码”，在分析 -> FindBugs 下，然后查找 FindBugs-IDEA 面板以检查结果：

[![弹簧休息分析1](https://www.baeldung.com/wp-content/uploads/2016/11/Spring-rest-analysis-1-300x139.png)](https://www.baeldung.com/wp-content/uploads/2016/11/Spring-rest-analysis-1.png)

你可以使用屏幕截图左侧的第二列命令，使用不同的因素对缺陷进行分组：

1. 按错误类别分组。
2. 按班级分组。
3. 按包分组。
4. 按错误等级分组。

也可以通过单击命令第四列中的“导出”按钮以 XML/HTML 格式导出报告。

### 4.3 配置

IDEA 中的 FindBugs 插件首选项页面非常不言自明：

[![IntelliJ 首选项 1](https://www.baeldung.com/wp-content/uploads/2016/11/IntelliJ-Preferences-1-1-278x300.png)](https://www.baeldung.com/wp-content/uploads/2016/11/IntelliJ-Preferences-1-1.png)

这个设置窗口与我们在 Eclipse 中看到的非常相似，因此你可以以类似的方式执行各种配置，从分析工作级别、错误排名、置信度、类过滤等开始。

通过单击 FindBugs-IDEA 面板下的“插件首选项”图标，可以在 IDEA 中访问首选项面板。

## 5. Spring-Rest项目的报表分析

在本节中，我们将以[Github上的spring-rest项目]()为例进行静态分析：

[![弹簧休息分析2](https://www.baeldung.com/wp-content/uploads/2016/11/Spring-rest-analysis-2-300x139.png)](https://www.baeldung.com/wp-content/uploads/2016/11/Spring-rest-analysis-2.png)

大多数缺陷都是次要的——值得关注，但让我们看看我们能做些什么来修复其中的一些。

方法忽略异常返回值：

```java
File fileServer = new File(fileName);
fileServer.createNewFile();
```

正如你可能猜到的那样，FindBugs 抱怨我们丢弃了createNewFile()方法的返回值。一种可能的解决方法是将返回值存储在新声明的变量中，然后使用
DEBUG 日志级别记录一些有意义的内容——例如，如果返回值为 true ，则“指定文件不存在并且已成功创建”。

该方法可能无法在出现异常时关闭流：此特定缺陷说明了异常处理的典型用例，它建议始终在finally块中关闭流：

```java
try {
    DateFormat dateFormat 
      = new SimpleDateFormat("yyyy_MM_dd_HH.mm.ss");
    String fileName = dateFormat.format(new Date());
    File fileServer = new File(fileName);
    fileServer.createNewFile();
    byte[] bytes = file.getBytes();
    BufferedOutputStream stream 
      = new BufferedOutputStream(new FileOutputStream(fileServer));
    stream.write(bytes);
    stream.close();
    return "You successfully uploaded " + username;
} catch (Exception e) {
    return "You failed to upload " + e.getMessage();
}
```

当在stream.close()指令之前抛出异常时，流永远不会关闭，这就是为什么最好使用finally{}块来关闭在try / catch例程期间打开的流。

未抛出异常时捕获异常：你可能已经知道，捕获异常是一种不好的编码习惯，FindBugs 认为你必须捕获最具体的异常，以便你可以正确处理它。所以基本上在
Java 类中操作流，捕获IOException比捕获更通用的异常更合适。

字段未在构造函数中初始化，但在没有 null
检查的情况下取消引用：在构造函数中初始化字段总是一个好主意，否则，我们应该忍受代码引发[NPE](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/NullPointerException.html)
的可能性。因此，当我们不确定变量是否正确初始化时，建议执行空检查。

## 六，总结

在本文中，我们介绍了在 Java 项目中使用和自定义 FindBugs 的基本要点。

如你所见，FindBugs 是一个强大而简单的静态分析工具，如果调整和使用正确，它有助于检测系统中潜在的质量漏洞。

最后，值得一提的是，FindBugs 也可以作为单独的连续自动代码审查工具(如[Sputnik](https://sputnik.ci/) )
的一部分运行，这对于提高报告的可见性非常有帮助。