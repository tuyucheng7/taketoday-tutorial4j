## 1. 概述

[KivaKit](https://www.kivakit.org/)是一个模块化的 Java 应用程序框架，旨在使开发微服务和应用程序变得更快、更容易。KivaKit 自 2011 年以来一直在[Telenav](https://www.telenav.com/)开发。它现在作为 Apache 许可的开源项目在[GitHub](https://github.com/Telenav/kivakit)上提供。

在本文中，我们将探索 KivaKit 的设计，将其作为一组协同工作的“迷你框架”。此外，我们还将了解每个微型框架的基本功能。

## 2. KivaKit 迷你框架

查看[kivakit](https://github.com/Telenav/kivakit) 和[kivakit-extensions](https://github.com/Telenav/kivakit-extensions)存储库，我们可以看到 KivaKit 1.0 包含 54 个模块。我们会发现这势不可挡。然而，如果我们一步一个脚印地做事，还不错。对于初学者，我们可以挑选我们想要包含在项目中的内容。KivaKit 中的每个模块都设计为可以单独使用。

一些 KivaKit 模块包含迷你框架。迷你框架是一种解决常见问题的简单、抽象的设计。如果我们检查 KivaKit 的迷你框架，我们会发现它们具有直接、广泛适用的接口。因此，它们有点像 Legos™。也就是说，它们是简单的拼接在一起的部分。

在这里，我们可以看到 KivaKit 的迷你框架以及它们之间的关系：

[![k1](https://www.baeldung.com/wp-content/uploads/2021/09/k1.svg)](https://www.baeldung.com/wp-content/uploads/2021/09/k1.svg)

 

 

| 迷你框架   | 模块                                                         | 描述                                                         |
| :--------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| 应用       | [kivakit-应用程序](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-application) | 应用程序和服务器的基础组件                                   |
| 命令行解析 | [kivakit-命令行](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-commandline) | 使用转换和验证迷你框架进行开关和参数解析                     |
| 成分       | [kivakit组件](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-component) | 实现 KivaKit 组件的基本功能，包括应用程序                    |
| 转换       | [kivakit 内核](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-kernel) | 用于实现健壮的模块化类型转换器的抽象                         |
| 萃取       | [kivakit 内核](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-kernel) | 从数据源中提取对象                                           |
| 接口       | [kivakit 内核](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-kernel) | 用作框架之间集成点的通用接口                                 |
| 记录       | [kivakit-内核](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-kernel) [kivakit-日志-](https://github.com/Telenav/kivakit-extensions/tree/1.0.0/kivakit-logs) | 核心日志记录功能、日志服务提供者接口 (SPI) 和日志实现        |
| 讯息       | [kivakit 内核](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-kernel) | 使组件能够传输和接收状态信息                                 |
| 混入       | [kivakit 内核](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-kernel) | [状态特征](https://en.wikipedia.org/wiki/Trait_(computer_programming))的实现 |
| 资源       | [kivakit-资源](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-resource) [kivakit-网络-](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-network) [kivakit-文件系统-](https://github.com/Telenav/kivakit-extensions/tree/1.0.0/kivakit-filesystems) | 文件、文件夹和流式资源的抽象                                 |
| 服务定位器 | [kivakit-配置](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-configuration) | 用于查找组件和设置信息的[服务定位器模式](https://martinfowler.com/articles/injection.html)的实现 |
| 设置       | [kivakit-配置](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-configuration) | 提供对组件配置信息的轻松访问                                 |
| 验证       | [kivakit 内核](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-kernel) | 检查对象一致性的基本功能                                     |

我们可以在kivakit存储库中找到这些框架。另一方面，我们会在[kivakit-extensions](https://github.com/Telenav/kivakit-extensions)中找到不太重要的模块，如服务提供者。

### 2.1. 讯息

如上图所示，消息传递是集成的中心点。在 KivaKit 中，消息传递形式化了状态报告。作为 Java 程序员，我们习惯于记录状态信息。有时，我们也会看到这些信息返回给调用者或作为异常抛出。相比之下，KivaKit 中的状态信息包含在[Message](https://www.kivakit.org/1.0.0/javadoc/kivakit/kivakit.kernel/com/telenav/kivakit/kernel/messaging/Message.html)中。[我们可以编写广播](https://www.kivakit.org/1.0.0/javadoc/kivakit/kivakit.kernel/com/telenav/kivakit/kernel/messaging/Broadcaster.html)这些消息 的组件 。[此外，我们可以编写监听](https://www.kivakit.org/1.0.0/javadoc/kivakit/kivakit.kernel/com/telenav/kivakit/kernel/messaging/Listener.html)它们的组件。

我们可以看到，这种设计让组件能够始终如一地专注于报告状态。对于一个组件来说，状态消息去哪里并不重要。在一种情况下，我们可能会将消息定向到[Logger](https://www.kivakit.org/1.0.0/javadoc/kivakit/kivakit.kernel/com/telenav/kivakit/kernel/logging/Logger.html)。另一方面，我们可能会将它们包括在统计数据中。我们甚至可以将它们展示给最终用户。该组件不关心。它只是向可能感兴趣的人报告问题。

广播消息的 KivaKit 组件可以连接到一个或多个消息[转发器](https://www.kivakit.org/1.0.0/javadoc/kivakit/kivakit.kernel/com/telenav/kivakit/kernel/messaging/Repeater.html)，形成一个监听器链：

 

[![k2](https://www.baeldung.com/wp-content/uploads/2021/09/k2.svg)](https://www.baeldung.com/wp-content/uploads/2021/09/k2.svg)

在 KivaKit 中， [应用程序](https://www.kivakit.org/1.0.0/javadoc/kivakit/kivakit.application/com/telenav/kivakit/application/Application.html)通常是侦听器链的末端。因此， Application类记录它收到的任何消息。在此过程中，侦听器链中的组件可能对这些消息有其他用途。

### 2.2. 混入

KivaKit 的另一个集成功能是 mixins 迷你框架。KivaKit mixins 允许通过接口继承将基类“混合”到类型中。 有时，mixins 被称为“[状态特征](https://en.wikipedia.org/wiki/Trait_(computer_programming))”。

例如， KivaKit 中的[BaseComponent](https://www.kivakit.org/1.0.0/javadoc/kivakit/kivakit.component/com/telenav/kivakit/component/BaseComponent.html)类为构建组件提供了基础。BaseComponent提供了发送消息的便捷方法。此外，它还提供对资源、设置和注册对象的轻松访问。

但是我们很快就遇到了这个设计的问题。正如我们所知，在 Java 中，已经具有基类的类不能再扩展BaseComponent。KivaKit mixins 允许将BaseComponent功能添加到已经具有基类的组件中。例如：

```java
public class MyComponent extends MyBaseClass implements ComponentMixin { [...] }复制
```

我们可以在这里看到接口[ComponentMixin](https://www.kivakit.org/1.0.0/javadoc/kivakit/kivakit.component/com/telenav/kivakit/component/ComponentMixin.html)扩展了 [Mixin](https://www.kivakit.org/1.0.0/javadoc/kivakit/kivakit.kernel/com/telenav/kivakit/kernel/language/mixin/Mixin.html) 和[Component](https://www.kivakit.org/1.0.0/javadoc/kivakit/kivakit.component/com/telenav/kivakit/component/Component.html)：

 

[![k3](https://www.baeldung.com/wp-content/uploads/2021/09/k3.svg)](https://www.baeldung.com/wp-content/uploads/2021/09/k3.svg)

Mixin接口为 ComponentMixin提供了一个 state() 方法 。首先，此方法用于创建 BaseComponent 并将 其与实现ComponentMixin 的对象相关联。其次， ComponentMixin将Component接口 的每个方法实现 为 Java 默认方法。第三，每个默认方法委托给关联的 BaseComponent。 通过这种方式，实现 ComponentMixin提供了与扩展BaseComponent 相同的方法 。

### 2.3. 服务定位器

服务定位器类 Registry 允许我们将组件连接在一起。注册表 提供与依赖注入 (DI) 大致相同的功能[。](https://www.kivakit.org/1.0.0/javadoc/kivakit/kivakit.configuration/com/telenav/kivakit/configuration/lookup/Registry.html)但是，它在一个重要方面不同于 DI 的典型使用。在服务定位器模式中，组件伸出它们需要的接口。另一方面，DI 将接口推入组件。因此，服务定位器方法改进了封装。它还减少了参考范围。例如， 可以在方法中巧妙地使用Registry ：

```java
class MyData extends BaseComponent {

    [...]

    public void save() {
        var database = require(Database.class);
        database.save(this);
    }
}复制
```

基础组件。require(Class)方法在这里查找 注册表中的对象。 当我们的save()方法返回时，数据库引用离开作用域。这样可以确保没有外部代码可以获取我们的引用。

当我们的应用程序启动时，我们可以使用BaseComponent之一注册服务对象。注册对象()方法。稍后，我们应用程序中其他地方的代码可以使用require(Class)查找它们。

### 2.4. 资源和文件系统

kivakit -resource模块为读写流式资源和访问文件系统提供了抽象。 我们可以在这里看到 KivaKit 包含的一些更重要的资源类型：

-   文件(本地、Zip、S3 和 HDFS)
-   包资源
-   网络协议(套接字、HTTP、HTTPS 和 FTP)
-   输入和输出流

我们从这种抽象中获得了两个有价值的好处。我们可以：

-   通过一致的、[面向对象的 API访问任何流式资源](https://www.kivakit.org/1.0.0/lexakai/kivakit/kivakit-resource/documentation/diagrams/diagram-resource.svg)
-   使用 [Resource](https://www.kivakit.org/1.0.0/javadoc/kivakit/kivakit.resource/com/telenav/kivakit/resource/Resource.html)和[WritableResource](https://www.kivakit.org/1.0.0/javadoc/kivakit/kivakit.resource/com/telenav/kivakit/resource/WritableResource.html)接口允许使用未知的资源类型

### 2.5. 成分

kivakit [-component](https://github.com/Telenav/kivakit/blob/1.0.0/kivakit-component/README.md)模块让我们可以随时访问常用功能。我们可以：

-   发送和接收消息
-   访问包和打包资源
-   注册和查找对象、组件和设置

Component接口由BaseComponent和ComponentMixin实现 。 因此，我们可以为任何对象添加“组件性质”。

### 2.6. 记录

由KivaKit组件形成的监听器链通常终止于Logger。 Logger[将](https://www.kivakit.org/1.0.0/javadoc/kivakit/kivakit.kernel/com/telenav/kivakit/kernel/logging/Logger.html) 它接收到的消息写入一个或[多个](https://www.kivakit.org/1.0.0/javadoc/kivakit/kivakit.kernel/com/telenav/kivakit/kernel/logging/Log.html)[Log](https://www.kivakit.org/1.0.0/javadoc/kivakit/kivakit.kernel/com/telenav/kivakit/kernel/logging/Log.html)。此外，[kivakit-kernel](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-kernel)模块提供了一个服务提供者接口(SPI)来实现Log。[我们可以在此处](https://www.kivakit.org/1.0.0/lexakai/kivakit/kivakit-kernel/documentation/diagrams/diagram-logging.svg)查看 UML 中日志记录微型框架的完整设计 。

使用日志记录 SPI， [kivakit-extensions ](https://github.com/Telenav/kivakit-extensions)存储库为我们提供了一些 日志实现：

| 供应商       | 模块                                                         |
| :----------- | :----------------------------------------------------------- |
| 控制台日志   | [kivakit 内核](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-kernel) |
| 文件日志     | [kivakit 日志文件](https://github.com/Telenav/kivakit-extensions/tree/1.0.0/kivakit-logs/file) |
| 电子邮件日志 | [kivakit-日志-电子邮件](https://github.com/Telenav/kivakit-extensions/tree/1.0.0/kivakit-logs/email) |

可以从命令行选择和配置一个或多个日志。这是通过定义[KIVAKIT_LOG](https://github.com/Telenav/kivakit/blob/1.0.0/kivakit-kernel/documentation/logging.md) 系统属性来完成的。

### 2.7. 转换和验证

kivakit -kernel模块包含用于类型转换和对象验证的微型框架。这些框架与 KivaKit 消息传递集成。这使他们能够一致地报告问题。它还简化了使用。要实现像[StringConverter这样的类型](https://www.kivakit.org/1.0.0/javadoc/kivakit/kivakit.kernel/com/telenav/kivakit/kernel/data/conversion/string/StringConverter.html)[转换器](https://www.kivakit.org/1.0.0/javadoc/kivakit/kivakit.kernel/com/telenav/kivakit/kernel/data/conversion/Converter.html)，我们需要编写转换代码。我们不需要担心异常、空字符串或空值。

 

[![k4](https://www.baeldung.com/wp-content/uploads/2021/09/k4.svg)](https://www.baeldung.com/wp-content/uploads/2021/09/k4.svg)

我们可以在 KivaKit 的很多地方看到转换器的使用，包括：

-   开关和参数解析
-   从属性文件加载设置对象
-   将对象格式化为调试字符串
-   从 CSV 文件中读取对象

[Validatable](https://www.kivakit.org/1.0.0/javadoc/kivakit/kivakit.kernel/com/telenav/kivakit/kernel/data/validation/Validatable.html)广播的消息由验证微型框架捕获。随后，对它们进行分析，以便我们轻松访问错误统计信息和验证问题。

 

[![k5](https://www.baeldung.com/wp-content/uploads/2021/09/k5.svg)](https://www.baeldung.com/wp-content/uploads/2021/09/k5.svg)

### 2.8. 应用程序、命令行和设置

kivakit -application、kivakit-configuration和kivakit-commandline模块为开发应用程序提供了一个简单、一致的模型。

kivakit [-application](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-application)项目提供了 [Application](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-application)基类。一个应用程序是一个[组件](https://www.kivakit.org/1.0.0/javadoc/kivakit/kivakit.component/com/telenav/kivakit/component/Component.html)。[它使用kivakit-configuration](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-configuration) 提供设置信息 。此外，它还使用 [kivakit-commandline](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-commandline)提供命令行解析。

kivakit -configuration项目使用[kivakit-resource](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-resource)模块从.properties资源(以及未来的其他来源)加载设置信息。[它使用kivakit-kernel](https://github.com/Telenav/kivakit/tree/1.0.0/kivakit-kernel)转换器将这些资源中的属性转换为对象。然后使用验证迷你框架验证转换后的对象。

应用程序的命令行参数和开关由kivakit 命令行模块使用 KivaKit 转换器和验证器进行解析。我们可以看到应用程序的[ConsoleLog](https://www.kivakit.org/1.0.0/javadoc/kivakit/kivakit.kernel/com/telenav/kivakit/kernel/logging/logs/text/ConsoleLog.html)中出现的问题。

 

[![k6](https://www.baeldung.com/wp-content/uploads/2021/09/k6.svg)](https://www.baeldung.com/wp-content/uploads/2021/09/k6.svg)

### 2.9. 微服务

到目前为止，我们已经讨论了通常对任何应用程序有用的 KivaKit 功能。此外，KivaKit 还在[kivakit-extensions](https://github.com/Telenav/kivakit-extensions)中提供了明确针对微服务的功能。让我们快速浏览一下[kivakit-web](https://github.com/Telenav/kivakit-extensions/tree/1.0.0/kivakit-web)。

kivakit -web项目包含用于快速开发微服务的简单 REST 和 Web 界面的模块。JettyServer类为我们提供了一种以最少的麻烦插入 servlet 和过滤器的方法。我们可以与JettyServer一起使用的插件包括：

| 插入         | 描述                        |
| :----------- | :-------------------------- |
| 泽西码头     | REST 应用程序支持           |
| JettySwagger | Swagger 自动 REST 文档      |
| 码头检票口   | 支持 Apache Wicket Web 框架 |

这些插件可以组合起来提供带有 Swagger 文档和 Web 界面的 RESTful 微服务：

```java
var application = new MyRestApplication();
listenTo(new JettyServer())
    .port(8080)
    .add("/*", new JettyWicket(MyWebApplication.class))
    .add("/open-api/*", new JettySwaggerOpenApi(application))
    .add("/docs/*", new JettySwaggerIndex(port))
    .add("/webapp/*", new JettySwaggerStaticResources())
    .add("/webjar/*", new JettySwaggerWebJar(application))
    .add("/*", new JettyJersey(application))
    .start();复制
```

KivaKit 1.1 将包括一个专用的微服务迷你框架。这将使我们构建微服务变得更加容易。

## 3.文档和Lexakai

KivaKit 的文档由 Lexakai 生成。 [Lexakai](https://www.lexakai.org/)创建 UML 图(在需要时由注释引导)并更新 README.md markdown 文件。在每个项目的自述文件中，Lexakai 更新了一个标准的页眉和页脚。此外，它还维护生成的 UML 图和 Javadoc 文档的索引。Lexakai 是一个在 Apache 许可证下分发的开源项目。

## 4. 构建 KivaKit

KivaKit 面向 Java 11 或更高版本的虚拟机(但可以从 Java 8 源代码使用)。[我们可以在Maven Central](https://search.maven.org/search?q=g:com.telenav.kivakit)上找到 KivaKit 模块的所有工件。但是，我们可能想要修改 KivaKit 或为开源项目做出贡献。在这种情况下，我们需要构建它。

首先，让我们设置 Git、 [Git Flow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow)、[Java 16 JDK](https://adoptopenjdk.net/?variant=openjdk16&jvmVariant=hotspot)和[Maven 3.8.1 或更高版本](https://maven.apache.org/download.cgi)。

首先，我们将kivakit存储库克隆到我们的工作区中：

```bash
mkdir ~/Workspace
cd ~/Workspace
git clone --branch develop https://github.com/Telenav/kivakit.git复制
```

接下来，我们将示例 bash 配置文件复制到我们的主文件夹：

```bash
cp kivakit/setup/profile ~/.profile复制
```

然后我们修改 ~/.profile 以指向我们的工作区，以及我们的 Java 和 Maven 安装：

```bash
export KIVAKIT_WORKSPACE=$HOME/Workspace 
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-16.jdk/Contents/Home 
export M2_HOME=$HOME/Developer/apache-maven-3.8.2复制
```

设置配置文件后，我们确保正在运行bash(在 macOS 上，zsh现在是默认设置)：

```bash
chsh -s /bin/bash复制
```

最后，我们重新启动终端程序并执行命令：

```bash
$KIVAKIT_HOME/setup/setup.sh复制
```

安装脚本将克隆[kivakit-extensions](https://github.com/Telenav/kivakit-extensions)和一些其他相关的存储库。之后，它将初始化 git-flow 并构建我们所有的 KivaKit 项目。

## 5.总结

在本文中，我们简要了解了 KivaKit 的设计。我们还参观了它提供的一些更重要的功能。KivaKit 非常适合开发微服务。它被设计为学习和使用易于理解的独立作品。