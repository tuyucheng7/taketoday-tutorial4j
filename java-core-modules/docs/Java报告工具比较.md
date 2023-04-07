## 一、概述

当我们谈论*报告工具*时，很多软件都涵盖了这个领域。然而，它们中的大多数都是成熟的*商业智能平台*或*云服务*。

但是，如果我们只是想将一些报告功能作为库添加到我们的应用程序中会发生什么？我们将在这里回顾一些非常适合此目的的*Java 报告工具。*

我们将主要关注这些开源工具：

-   *[出版](https://www.eclipse.org/birt/)*
-   *[碧玉报告](https://community.jaspersoft.com/project/jasperreports-library)*
-   *[Pentaho](https://github.com/pentaho/pentaho-reporting)*

此外，我们将简要分析以下商业工具：

-   *[精美报告](https://www.finereport.com/en/)*
-   *[Logi 报告](https://www.logianalytics.com/jreport/)*（以前称为*JReport*）
-   *[报告工厂](http://www.reportmill.com/product/)*

## 2.设计报告

通过本节，我们将回顾如何以可视化方式设计报告和使用我们的数据。请注意，在这一部分中我们将仅提及开源工具。

### 2.1. 视觉编辑器

所有这三个工具都包含一个具有报告预览功能的 WYSIWIG 编辑器。

*BIRT Report Designer*和*Jaspersoft Studio是基于*[Eclipse RCP](https://wiki.eclipse.org/Rich_Client_Platform)构建的工具。对于我们大多数 Java 开发人员来说，这是一个很好的观点，因为我们可能熟悉 Eclipse 环境。与那些不同的是，*Pentaho Report Designer 的***视觉效果很差**。

*此外，关于Jaspersoft Studio*还有一个有趣的功能：我们可以直接在他们的*Jasper Reports Server*（报告管理系统）上发布报告。

### 2.2. 数据集

与所有报告工具一样，我们可以通过查询数据源来检索数据集*（*见下文）。然后，我们可以将它们转换为报表字段、创建计算字段或使用聚合公式。

除此之外，比较**我们如何管理多个数据集也**很有趣，因为如果我们的数据来自不同的查询甚至不同的*数据源*，我们可能需要多个数据集：

-   ***BIRT\*提供了最简单的解决方案，因为我们可以在同一份报告中拥有多个数据集**
-   使用*Jasper Reports*和*Pentaho*，我们每次都需要创建一个单独的子报表，这可能非常棘手

### 2.3. 图表和视觉元素

所有工具都提供简单的元素，如形状和图像，以及每种图表风格：*线条*、*区域*、*饼图*、*雷达*、*圆环*等。它们都支持交叉表。

然而，***Jasper Reports\*提供了最丰富的可视化元素集合**。它在上面的列表中添加了*地图*、*迷你图*、*金字塔*和*甘特图*。

### 2.4. 造型报告

现在，让我们比较一下页面中元素的定位和大小：

-   所有的工具都提供像素定位
-   ***BIRT\*和\*Pentaho\*还提供了类 HTML 定位**（table、block、inline）
-   **它们都不支持类似 CSS 的 flexbox 或网格系统**来控制元素大小

此外，当我们必须管理多个报表时，我们可能希望共享相同的视觉主题：

-   *Jasper Reports*提供具有 XML-CSS 语法的主题文件
-   *BIRT*可以将 CSS 样式表导入设计系统
-   使用*Pentaho*，我们只能在页眉中添加 CSS 样式表。所以很难将它们与内部设计系统混合

## 3.渲染报告

现在，我们已经了解了如何设计报告，让我们比较一下如何以编程方式呈现它们。

### 3.1. 安装

首先，我们要注意，**所有工具都被设计为可以轻松嵌入到 Java 项目中**。

首先，您可以查看我们关于[BIRT](https://baeldung.com/birt-reports-spring-boot#dependencies) 和[Jasper Reports](https://baeldung.com/spring-jasper#maven-dependency)的专门文章。对于 Pentaho，有[帮助页面](https://help.pentaho.com/Documentation/9.0/Developer_center/Embed_reporting_functionality)和免费[代码示例](https://github.com/fcorti/pentaho-8-reporting-for-java-developers)。

接下来，对于这些工具中的每一个，我们都将报表引擎连接到我们的应用程序数据。

### 3.2. 数据源

我们应该问的第一个问题是：我们如何将报表引擎连接到我们的项目数据源？

-   *Jasper Reports ：我们只需将它添加为*[*fillReport*](https://baeldung.com/spring-jasper#populating-reports)[方法](https://baeldung.com/spring-jasper#populating-reports)的参数
-   *BIRT*的解决方案有点复杂：我们应该修改我们的报告以将数据源属性设置为参数
-   **Pentaho**在这里有一个很大的缺点：除非我们购买他们的*PDI*商业软件，否则**我们必须使用 JNDI 数据源**，这更难设置

说到数据源，支持哪些类型？

-   这三个工具都支持最常见的类型：*JDBC*、*JNDI*、*POJOs*、*CSV*、*XML* 和*MongoDB*
-   *REST API*是现代项目的必要条件，然而，它们都没有原生支持它
    -   使用*BIRT*，我们应该编写一个*Groovy 脚本*
    -   *Jasper Reports*需要一个额外的[免费插件](https://community.jaspersoft.com/project/web-service-data-source)
    -   使用*Pentaho*，我们应该编写*Groovy 脚本*或获取*PDI*商业软件
-   ***Jasper Reports\*和\*Pentaho\*****原生支持 JSON 文件**，但*BIRT*将需要外部 Java 解析器库
-   [我们可以在这个矩阵](http://www.innoventsolutions.com/comparison-matrix.html)中找到完整的比较列表

### 3.3. 参数和运行时定制

由于我们已将报告连接到数据源，让我们呈现一些数据！

现在重要的是如何检索我们的最终用户数据。为此，我们可以将参数传递给渲染方法。这些参数应该在我们设计报表的时候就已经定义好了，而不是在运行时。但是，例如，如果我们的数据集基于最终用户上下文的不同查询，我们该怎么办？

使用***Pentaho\*和\*Jasper Reports\*，根本不可能做到这一点**，因为报告文件是二进制的，没有 Java SDK 可以修改它们。相比之下，***BIRT\*报告是纯 XML 文件**。此外，我们可以使用 Java API 来修改它们，因此**很容易在运行时自定义所有内容。**

### 3.4. 输出格式和 Javascript 客户端

值得庆幸的是，所有工具都支持大多数常见格式：***HTML、PDF、Excel、CSV、纯文本*****和*****RTF***。现在，我们可能还会问如何将报告结果直接集成到我们的网页中。不过，我们不会提及 PDF 可视化工具的粗略包含。

-   最好的解决方案是使用*Javascript*客户端将报告直接呈现到 HTML 元素中。对于**BIRT，Javascript 客户端是** ***Actuate JSAPI\*** ***，\*****对于\*Jasper Reports\*，我们应该使用*****JRIO.js***
-   *除了 iFrame 集成， Pentaho*不提供任何东西。此解决方案有效但可能有严重的[缺点](https://www.ostraining.com/blog/webdesign/against-using-iframes/)

### 3.5. 独立渲染工具

除了将我们的报告集成到网页中，我们可能还对拥有一个开箱即用的呈现服务器感兴趣。每个工具都提供自己的解决方案：

-   ***BIRT 查看器\*** **是一个轻量级 Web 应用程序**示例，用于按需执行*BIRT报告。***它是开源的，但不包括报告管理功能**
-   **对于\*Pentaho\*和\*Jasper Report\*，只有商业软件包**

## 4. 项目状态和活动

首先，谈谈许可证。*BIRT*在*EPL*下，*Jasper Reports在**LGPLv3*下，*Pentaho在**LGPLv2.1*下。因此，我们可以将所有这些库嵌入到我们自己的产品中，即使它们是商业产品。

那么，我们可以问问自己，这些开源项目是如何维护的，社区是否还活跃：

-   ***Jasper Reports\*有一个维护良好的[存储库，](https://github.com/TIBCOSoftware/jasperreports)其编辑器 TIBCO 软件 具有稳定的媒体[活动](https://www.openhub.net/p/jasperreports)**
-   ***BIRT\* [存储库](https://github.com/eclipse/birt)**仍在维护，但自 2015 年 OpenText 收购其编辑器 Actuate 以来，**[其活动](https://www.openhub.net/p/birt/commits/summary)非常低**
-   同样，自 2015 年 Hitachi-Vantara 收购以来，***Pentaho\*存储库 [活动](https://www.openhub.net/p/pentaho-reporting/commits/summary)非常低**

我们可以使用 Stackoverflow 趋势来确认这一点。*[BIRT](https://insights.stackoverflow.com/trends?tags=birt)*和*[Pentaho](https://insights.stackoverflow.com/trends?tags=pentaho)*的受欢迎程度最低*，但**[Jasper Reports 的](https://insights.stackoverflow.com/trends?tags=jasper-reports)*受欢迎程度适中。

**在过去 5 年中，**这三种Java 报告工具的受欢迎程度都有所下降，但目前仍保持稳定。**我们可以通过云和 Javascript 产品的出现来解释这一点。**

## 5. 商业 Java 报告工具

除了开源解决方案外，还有一些值得一提的商业选择。

### 5.1. 罚款报告

*Fine Report*最初设计为作为独立服务器执行。幸运的是，如果我们想使用它，我们可以将它作为我们项目的一部分。我们必须手动将所有 JAR 和资源复制到我们的 WAR 中，如[他们的过程](http://doc.fanruan.com/display/VHD/Embedded+Deployment)中所述。

完成此操作后，我们可以在我们的项目中看到*决策平台*工具作为 URL 可用。从这个 URL，我们可以直接在提供的 Web 视图、iFrame*或*使用他们的 Javascript 客户端中执行报告。**但是，我们无法以编程方式生成报告。**

另一个巨大的限制是目标运行时。版本 10仅支持 Java 8 和 Tomcat 8.x。

### 5.2. Logi 报告（以前称为 JReport）

与 Fine Report 一样，Logi Report 被设计为作为独立服务器执行，但[我们可以将其集成](https://www.jinfonet.com/kbase/16.1up2/serverguide/index.htm#t=HTML%2Finteg%2Finteg_way.htm)为现有 WAR 项目的一部分。因此，我们将面临与*Fine Report*相同的限制：**我们无法以编程方式生成报告**。

不同于精细报告。但是，Logi Report 支持几乎所有的 servlet 容器和 Java 8 到 13。

### 5.3. ReportMill 报告

最后*，*值得一提的是 ReportMill，因为**我们可以将它顺利地嵌入到每个 Java 应用程序中**。此外，与 BIRT 一样*，*它非常灵活：**我们可以在运行时自定义报告，因为它们是纯 XML 文件**。

但是，我们可以立即看到 ReportMill 已经老化，并且与其他解决方案相比功能也很差。

## 六，结论

在本文中，我们介绍了一些最著名的 Java 报告工具并比较了它们的功能。

作为结论，我们可以根据我们的要求选择这些 Java 报告工具之一：

**我们将选择 BIRT**：

-   对于一个简单的库来**替换现有的自制解决方案**
-   因其**最大的灵活性和高度的定制潜力**

**我们将选择 Jasper Reports**：

-   如果我们需要一个与**成熟的报告管理系统兼容的报告库**
-   如果我们想赌**最好的长期发展和支持**