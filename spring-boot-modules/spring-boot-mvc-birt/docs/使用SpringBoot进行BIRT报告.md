## 1. 简介

在本教程中，我们将BIRT(Business Intelligence and Reporting Tools-商业智能和报告工具)与Spring Boot MVC集成，以提供HTML和PDF格式的静态和动态报告。

## 2. 什么是BIRT？

**BIRT是一个开源引擎，用于创建可以集成到Java Web应用程序中的数据可视化**。

它是Eclipse基金会中的顶级软件项目，利用了IBM和Innovent Solutions的贡献，它于2004年底由Actuate发起和赞助。

该框架允许创建与各种数据源集成的报告。

## 3. Maven依赖

**BIRT有两个主要组件：一个用于创建报告设计文件的可视化报告设计器，以及一个用于解释和呈现这些设计的运行时组件**。

在我们的示例Web应用程序中，我们将在Spring Boot之上使用两者。

### 3.1 BIRT框架依赖

由于我们习惯于从依赖管理的角度思考，因此第一个选择是在Maven Central中寻找BIRT。

但是，**可用的核心库的最新官方版本是**[2016年的4.6](https://search.maven.org/search?q=a:org.eclipse.birt.runtime)，而在[Eclipse下载页面上](https://download.eclipse.org/birt/downloads/build_list.php)，我们可以找到至少两个更新版本(**当前为4.8**)的链接。

如果我们选择正式版本，启动和运行代码的最简单方法是下载[BIRT Report Engine](https://www.eclipse.org/downloads/download.php?file=/birt/downloads/drops/R-R1-4.8.0-201806261756/birt-runtime-4.8.0-20180626.zip)包，这是一个完整的Web应用程序，对学习也很有用。然后，我们需要将其lib文件夹复制到我们的项目中(大小约为68MB)，并告诉IDE将所有jar包含在其中。

不用说，使用这种方法，**我们将只能通过IDE进行编译**，因为Maven不会找到这些jar，除非我们在本地仓库中手动配置和安装它们(超过100个文件！)。

幸运的是，**Innovent Solutions决定自己动手，并在**[Maven Central](https://search.maven.org/search?q=a: org.eclipse.birt.runtime_4.8.0-20180626)**上发布了自己构建的最新BIRT依赖项**，这很棒，因为它为我们管理了所有需要的依赖项。

通过阅读在线论坛中的评论，目前还不清楚这些工件是否可用于生产，但Innovent Solutions从一开始就与Eclipse团队一起参与该项目，因此我们的项目依赖于它们。

包括BIRT现在非常容易：

```xml
<dependency>
    <groupId>com.innoventsolutions.birt.runtime</groupId>
    <artifactId>org.eclipse.birt.runtime_4.8.0-20180626</artifactId>
    <version>4.8.0</version>
</dependency>
```

### 3.2 Spring Boot依赖项

现在BIRT已导入到我们的项目中，我们只需要在我们的pom文件中添加标准的Spring Boot依赖项。

但是，有一个陷阱，因为BIRT jar包含它自己的Slf4J实现，因此它不能很好地与Logback配合使用，并且会在启动期间抛出冲突异常。

由于我们无法将其从jar中删除，因此为了解决此问题，我们**需要排除Logback**：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-logging</artifactId>
    <exclusions>
        <exclusion>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

现在我们终于准备好开始了！

## 4. BIRT报告

在BIRT框架中，**报告是一个长的XML配置文件**，由扩展名rptdesign标识。

**它告诉引擎要绘制什么以及在哪里绘制**，从标题的样式到连接到数据源所需的属性。

对于基本的动态报告，我们需要配置三样东西：

1.  数据源(在我们的示例中，我们使用本地CSV文件，但它很容易成为数据库表)
2.  我们要显示的元素(图表、表格等)
3.  页面设计

该报告的结构类似于HTML页面，具有页眉、正文、页脚、脚本和样式。

**该框架提供了一组开箱即用的组件供你选择**，包括与主流数据源、布局、图表和表格的集成。而且，我们可以扩展它来添加我们自己的！

有两种生成报告文件的方法：可视化或编程化。

## 5. Eclipse报表设计器

为了简化报告的创建，**Eclipse团队为其流行的IDE构建了一个报告设计工具插件**。

**该工具具有从左侧面板轻松拖放的界面**，它会自动打开我们添加到页面的新组件的设置窗口。我们还可以通过在页面上单击每个组件然后单击“属性编辑器”按钮(在下图中突出显示)来查看每个组件可用的所有自定义项。

要在树视图中可视化整个页面结构，我们只需单击”大纲“按钮。

**Data Explorer选项卡还包含为我们的报告定义的数据源**：

<img src="../assets/img.png">

图像中显示的示例报告可以在路径<project_root>/reports/csv_data_report.rptdesign中找到。

选择可视化设计器的另一个优势是在线文档，它更侧重于此工具而不是编程方法。

**如果我们已经在使用Eclipse，我们只需要安装BIRT Report Design插件**，它包括一个预定义的透视图和可视化编辑器。

**对于那些目前没有使用Eclipse并且不想切换的开发人员，有一个**[Eclipse Report Designer](https://www.eclipse.org/downloads/download.php?file=/birt/downloads/drops/R-R1-4.8.0-201806261756/birt-report-designer-all-in-one-4.8.0-20180626-win32.win32.x86_64.zip)**包**，它包含一个预装了BIRT插件的可移植Eclipse安装。

报告文件完成后，我们可以将其保存在我们的项目中，然后返回到我们首选的环境中进行编码。

## 6. 编程化方法

我们也可以**仅使用代码来设计报告**，但由于可用的文档不多，这种方法要困难得多，因此请准备好深入研究源代码和在线论坛。

同样值得考虑的是，**使用设计器可以更轻松地处理所有繁琐的设计细节**，如尺寸、长度和网格位置。

为了证明这一点，下面是一个如何定义带有图像和文本的简单静态页面的示例：

```java
DesignElementHandle element = factory.newSimpleMasterPage("Page Master");
design.getMasterPages().add(element);

GridHandle grid = factory.newGridItem(null, 2, 1);
design.getBody().add(grid);

grid.setWidth("100%");

RowHandle row0 = (RowHandle) grid.getRows().get(0);

ImageHandle image = factory.newImage(null);
CellHandle cell = (CellHandle) row0.getCells().get(0);
cell.getContent().add(image);
image.setURL("\"https://www.tuyucheng.com/wp-content/themes/tuyucheng/favicon/favicon-96x96.png\"");

LabelHandle label = factory.newLabel(null);
cell = (CellHandle) row0.getCells().get(1);
cell.getContent().add(label);
label.setText("Hello, Tuyucheng world!");
```

此代码将生成一个简单(且丑陋)的报告：

<img src="../assets/img_1.png">

上图中显示的示例报告可在以下路径找到：<project_root>/reports/static_report.rptdesign。

一旦我们对报告的外观和显示的数据进行了编码，我们就可以通过运行ReportDesignApplication类来生成XML文件。

## 7. 附加数据源

我们之前提到过，BIRT支持许多不同的数据源。

对于我们的示例项目，我们使用了一个包含三个条目的简单CSV文件，它可以在reports文件夹中找到，由三行简单的数据以及标题组成：

```csv
Student, Math, Geography, History
Bill, 10,3,8
Tom, 5,6,5
Anne, 7, 4,9
```

### 7.1 配置数据源

为了让BIRT使用我们的文件(或任何其他类型的源)，**我们必须配置一个数据源**。

对于我们的文件，我们使用报表设计器创建了一个平面文件数据源，只需几个步骤：

1.  打开designer视角，查看右侧的outline。
2.  右键单击Data Sources图标。
3.  选择所需的源类型(在我们的例子中是flat file源)。
4.  我们现在可以选择加载整个文件夹或只加载一个文件，我们使用了第二个选项(如果我们的数据文件是CSV格式，我们希望确保使用第一行作为列名指示符)。
5.  测试连接以确保路径正确。

我们附上了一些图片来演示每个步骤：

<img src="../assets/img_2.png">

<img src="../assets/img_3.png">

<img src="../assets/img_4.png">

<img src="../assets/img_5.png">

### 7.2 数据集

数据源已准备就绪，但我们仍然需要定义我们的Data Set，这是我们报告中显示的实际数据：

1.  打开designer视角，查看右侧的outline。
2.  右键单击Data Sets图标。
3.  选择所需的数据源和类型(在我们的例子中只有一种类型)。
4.  下一个屏幕取决于我们选择的数据源和数据集的类型：在我们的例子中，我们看到一个页面，我们可以在其中选择要包含的列。
5.  设置完成后，我们可以随时通过双击我们的数据集来打开配置。
6.  在Output Columns中，我们可以设置显示数据的正确类型。
7.  然后我们可以通过单击Preview Results来查看预览。

再次，列出一些图片来阐明这些步骤：

<img src="../assets/img_6.png">

<img src="../assets/img_7.png">

<img src="../assets/img_8.png">

<img src="../assets/img_9.png">

### 7.3 其他数据源类型

如数据集配置的第4步中所述，可用选项可能会根据引用的数据源而变化。

对于我们的CSV文件，BIRT提供了与显示哪些列、数据类型以及我们是否要加载整个文件相关的选项。另一方面，如果我们有一个JDBC数据源，我们可能必须编写一个SQL查询或一个存储过程。

从Data Sets菜单中，**我们还可以将两个或多个数据集加入一个新的数据集中**。

## 8. 呈现报告

报表文件准备就绪后，我们必须将其传递给引擎进行渲染。为此，需要执行一些操作。

### 8.1 初始化引擎

解释设计文件并生成最终结果的ReportEngine类是BIRT运行时库的一部分。

它使用一堆工具程序和任务来完成这项工作，这使得它非常耗费资源：

图片来源：Eclipse BIRT文档

**与创建引擎实例相关的成本很高，在主要是由于加载扩展的成本**。因此，我们应该**只创建一个ReportEngine实例**并使用它来运行多个报表。

报告引擎是通过平台提供的工厂创建的，在创建引擎之前，我们必须启动Platform，该Platform将加载适当的插件：

```java
@PostConstruct
protected void initialize() throws BirtException {
    EngineConfig config = new EngineConfig();
    config.getAppContext().put("spring", this.context);
    Platform.startup(config);
    IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
    birtEngine = factory.createReportEngine(config);
    imageFolder = System.getProperty("user.dir") + File.separatorChar + reportsPath + imagesPath;
    loadReports();
}
```

当我们不再需要它时，我们可以销毁它：

```java
@Override
public void destroy() {
    birtEngine.destroy();
    Platform.shutdown();
}
```

### 8.2 实现输出格式

**BIRT已经支持多种输出格式：HTML、PDF、PPT和ODT等等**。

对于示例项目，我们使用generatePDFReport和generateHTMLReport方法实现了其中两个。

它们根据所需的特定属性略有不同，例如输出格式和图像处理程序。

事实上，PDF将图像与文本嵌入在一起，而HTML报告需要生成它们和/或链接它们。

因此，**PDF渲染功能非常简单**：

```java
private void generatePDFReport(IReportRunnable report, HttpServletResponse response,HttpServletRequest request) {
    IRunAndRenderTask runAndRenderTask = birtEngine.createRunAndRenderTask(report);
    response.setContentType(birtEngine.getMIMEType("pdf"));
    IRenderOption options = new RenderOption();
    PDFRenderOption pdfRenderOption = new PDFRenderOption(options);
    pdfRenderOption.setOutputFormat("pdf");
    runAndRenderTask.setRenderOption(pdfRenderOption);
    runAndRenderTask.getAppContext().put(EngineConstants.APPCONTEXT_PDF_RENDER_CONTEXT, request);

    try {
        pdfRenderOption.setOutputStream(response.getOutputStream());
        runAndRenderTask.run();
    } catch (Exception e) {
        throw new RuntimeException(e.getMessage(), e);
    } finally {
        runAndRenderTask.close();
    }
}
```

**而HTML渲染功能需要更多的设置**：

```java
private void generateHTMLReport(IReportRunnable report, HttpServletResponse response,HttpServletRequest request) {
    IRunAndRenderTask runAndRenderTask = birtEngine.createRunAndRenderTask(report);
    response.setContentType(birtEngine.getMIMEType("html"));
    IRenderOption options = new RenderOption();
    HTMLRenderOption htmlOptions = new HTMLRenderOption(options);
    htmlOptions.setOutputFormat("html");
    htmlOptions.setBaseImageURL("/" + reportsPath + imagesPath);
    htmlOptions.setImageDirectory(imageFolder);
    htmlOptions.setImageHandler(htmlImageHandler);
    runAndRenderTask.setRenderOption(htmlOptions);
    runAndRenderTask.getAppContext().put(EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, request);

    try {
        htmlOptions.setOutputStream(response.getOutputStream());
        runAndRenderTask.run();
    } catch (Exception e) {
        throw new RuntimeException(e.getMessage(), e);
    } finally {
        runAndRenderTask.close();
    }
}
```

最值得注意的是，我们**设置了HTMLServerImageHandler**而不是保留默认处理程序，这个微小的差异对生成的img标签有很大的影响：

-   **默认处理程序将img标签链接到文件系统路径**，许多浏览器出于安全原因将其阻止
-   **HTMLServerImageHandler链接到服务器URL**

使用setImageDirectory方法，我们指定引擎将保存生成的图像文件的位置。

默认情况下，处理程序在每个请求时都会生成一个新文件，因此**我们可以添加缓存层或删除策略**。

### 8.3 发布图像

在HTML报告案例中，图像文件是外部文件，因此它们需要在服务器路径上可访问。

在上面的代码中，通过setBaseImageURL方法，我们告诉引擎在img标签链接中应该使用什么相对路径，因此我们需要确保这个路径是真实可访问的！

出于这个原因，在我们的ReportEngineApplication中，我们配置了Spring来发布images文件夹：

```java
@SpringBootApplication
@EnableWebMvc
public class ReportEngineApplication implements WebMvcConfigurer {
	@Value("${reports.relative.path}")
	private String reportsPath;
	@Value("${images.relative.path}")
	private String imagesPath;

	// ...

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry
			.addResourceHandler(reportsPath + imagesPath + "/**")
			.addResourceLocations("file:///" + System.getProperty("user.dir") + "/" + reportsPath + imagesPath);
	}
}
```

无论我们选择什么路径，我们都必须确保这里和前面代码段的htmlOptions中使用相同的路径，否则我们的报告将无法显示图像。

## 9. 显示报告

让我们的应用程序准备就绪所需的最后一个组件是返回渲染结果的控制器：

```java
@RequestMapping(method = RequestMethod.GET, value = "/report/{name}")
@ResponseBody
public void generateFullReport(HttpServletResponse response, HttpServletRequest request,
		@PathVariable("name") String name, 
		@RequestParam("output") String output) throws EngineException, IOException {
    OutputType format = OutputType.from(output);
    reportService.generateMainReport(name, format, response, request);
}
```

通过output参数，我们可以让用户选择所需的格式-HTML或PDF。

## 10. 测试报告

我们可以通过运行ReportEngineApplication类来启动应用程序。

在启动期间，BirtReportService类将加载在<project_root>/reports文件夹中找到的所有报告。

要查看我们的报告，我们只需在浏览器中访问：

-   /report/csv_data_report?output=pdf
-   /report/csv_data_report?output=html
-   /report/static_report?output=pdf
-   /report/static_report?output=html

以下是csv_data_report报告的外观：

<img src="../assets/img_10.png">

要在更改设计文件后重新加载报告，我们只需通过浏览器访问/report/reload。

## 11. 总结

在本文中，我们将BIRT与Spring Boot集成在一起，介绍了其中的陷阱和挑战，以及它的强大功能和灵活性。