## 1. 简介

在本教程中，我们将讨论业务流程管理 (BPM) 系统及其在Java中作为[jBPM](https://docs.jboss.org/jbpm/release/7.20.0.Final/jbpm-docs/html_single/)系统的实现。

## 2.业务流程管理系统

我们可以将[业务流程管理](https://en.wikipedia.org/wiki/Business_process_management)定义为范围从开发扩展到公司各个方面的领域之一。

BPM 提供了对公司职能流程的可见性。这使我们能够通过迭代改进找到最佳流程，如流程图所示。改进的流程增加了利润并降低了成本。

BPM 定义了它自己的目标、生命周期、实践和所有参与者之间的通用语言，即业务流程。

## 3.jBPM系统_

jBPM 是Java中 BPM 系统的实现。它允许我们创建业务流程、执行它并监控其生命周期。jBPM 的核心是一个用Java编写的工作流引擎，它为我们提供了一个使用最新的业务流程建模符号 (BPMN) 2.0 规范来创建和执行流程的工具。

jBPM 主要关注可执行的业务流程。这些过程有足够的细节，因此它们可以在工作流引擎上执行。

这是我们的 BPMN 流程模型执行顺序的图形流程图示例，以帮助我们理解：

[![过程模型](https://www.baeldung.com/wp-content/uploads/2019/04/processModel.jpg)](https://www.baeldung.com/wp-content/uploads/2019/04/processModel.jpg)

1.  我们开始使用初始上下文执行流程，由绿色开始节点表示
2.  首先，任务1将执行
3.  完成任务 1 后，我们将继续执行任务 2
4.  遇到红端节点时停止执行

## 4. jBPM项目的IDE插件

让我们看看如何在 Eclipse 和 IntelliJ IDEA 中安装插件来创建 jBPM 项目和 BPMN 2.0 流程。

### 4.1. 日蚀插件

我们需要安装一个插件来创建 jBPM 项目。让我们按照以下步骤操作：

1.  在“帮助”部分，单击“安装新软件”
2.  添加[Drools 和 jBPM 更新站点](https://docs.jbpm.org/7.64.0.Final/jbpm-docs/html_single/#jbpmreleasenotes)
3.  接受许可协议条款并完成插件安装
4.  重新启动 Eclipse

Eclipse 重新启动后，我们需要转到Windows -> Preferences -> Drools -> Drools Flow Nodes：

[![流口水配置](https://www.baeldung.com/wp-content/uploads/2019/04/Drools-config.jpg)](https://www.baeldung.com/wp-content/uploads/2019/04/Drools-config.jpg)

选择所有选项后，我们可以单击“应用并关闭”。现在，我们准备创建我们的第一个jBPM 项目。

### 4.2. IntelliJ IDEA 插件

IntelliJ IDEA 默认安装了 jBPM 插件，但它只存在于 Ultimate 而不是 Community 选项中。

我们只需要通过单击 Configure -> Settings -> Plugins -> Installed -> JBoss jBPM来启用它：

[![Drools 配置 IJI](https://www.baeldung.com/wp-content/uploads/2019/04/Drools-config-IJI.jpg)](https://www.baeldung.com/wp-content/uploads/2019/04/Drools-config-IJI.jpg)

目前，这个 IDE 没有 BPMN 2.0 流程设计器，但我们可以从任何其他设计器导入.bpmn文件并运行它们。

## 5. 你好世界示例

让我们亲自动手创建一个简单的 Hello World 项目。

### 5.1. 创建一个jBPM项目

要在 Eclipse 中创建一个新的 jBPM 项目，我们将转到File -> New -> Other -> jBPM Project (Maven)。在提供我们项目的名称后，我们可以点击完成。Eclipse 将为我们完成所有艰苦的工作，并将下载所需的 Maven 依赖项来为我们创建示例 jBPM 项目。

要在 IntelliJ IDEA 中创建相同的文件，我们可以转到File -> New -> Project -> JBoss Drools。IDE 将下载所有需要的依赖项并将它们放在项目的lib文件夹中。

### 5.2. 创建 Hello World 流程模型

让我们创建一个在控制台中打印“Hello World”的小型 BPM 流程模型。

为此，我们需要在src/main/resources下创建一个新的 BPMN 文件：

[![bpmn文件](https://www.baeldung.com/wp-content/uploads/2019/04/bpmn-file.jpg)](https://www.baeldung.com/wp-content/uploads/2019/04/bpmn-file.jpg)

文件扩展名为.bpmn，它在 BPMN 设计器中打开：

[![bpmn设计师](https://www.baeldung.com/wp-content/uploads/2019/04/bpmn-designer.jpg)](https://www.baeldung.com/wp-content/uploads/2019/04/bpmn-designer.jpg)

设计器的左侧面板列出了我们之前在设置 Eclipse 插件时选择的节点。我们将使用这些节点来创建我们的流程模型。中间面板是工作区，我们将在其中创建流程模型。右侧是属性选项卡，我们可以在这里设置进程或节点的属性。

在这个HelloWorld模型中，我们将使用：

-   启动事件——启动流程实例所必需的
-   脚本任务——启用Java片段
-   结束事件——结束流程实例所必需的

如前所述，IntelliJ IDEA 没有BPMN 设计器，但我们可以导入在Eclipse 或网页设计器中设计的.bpmn文件。

### 5.3. 声明并创建知识库 ( kbase )

所有 BPMN 文件都作为进程加载到kbase中。我们需要将各自的流程ID传递给 jBPM 引擎以便执行它们。

我们将 使用我们的kbase和 BPMN 文件包声明在resources/META-INF下 创建kmodule.xml ：

```xml
<kmodule xmlns="http://jboss.org/kie/6.0.0/kmodule">
    <kbase name="kbase" packages="com.baeldung.bpmn.process" />
</kmodule>
```

声明完成后，我们可以使用 KieContainer加载kbase：

```java
KieServices kService = KieServices.Factory.get();
KieContainer kContainer = kService.getKieClasspathContainer();
KieBase kbase = kContainer.getKieBase(kbaseId);
```

### 5.4. 创建jBPM运行时管理器

我们将使用org.jbpm.test 包中的 JBPMHelper来构建示例运行时环境。

我们需要两件事来创建环境：首先，创建EntityManagerFactory的数据源，其次，我们的kbase。

JBPMHelper 有启动内存中的 H2 服务器和设置数据源的方法。使用相同的方法，我们可以创建EntityManagerFactory：

```java
JBPMHelper.startH2Server();
JBPMHelper.setupDataSource();
EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnit);
```

一旦我们准备好了一切，我们就可以创建我们的RuntimeEnvironment：

```java
RuntimeEnvironmentBuilder runtimeEnvironmentBuilder = 
  RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder();
RuntimeEnvironment runtimeEnvironment = runtimeEnvironmentBuilder.
  entityManagerFactory(emf).knowledgeBase(kbase).get();
```

使用 RuntimeEnvironment，我们可以创建我们的 jBPM 运行时管理器：

```java
RuntimeManager runtimeManager = RuntimeManagerFactory.Factory.get()
  .newSingletonRuntimeManager(runtimeEnvironment);
```

### 5.5. 执行流程实例

最后，我们将使用RuntimeManager 来获取RuntimeEngine：

```java
RuntimeEngine engine = manager.getRuntimeEngine(initialContext);
```

使用 RuntimeEngine， 我们将创建一个知识会话并启动该过程：

```java
KieSession ksession = engine.getKieSession();
ksession.startProcess(processId);
```

该进程将启动并在 IDE 控制台上打印 Hello World 。 

## 六. 总结

在本文中，我们介绍了 BPM 系统，使用它的Java实现 —  [jBPM](https://www.jbpm.org/)。

这是启动 jBPM 项目的快速指南。此处演示的示例使用最小流程，以便简要了解执行流程，可以在[GitHub](https://github.com/eugenp/tutorials/tree/master/libraries-2)上找到。

要执行流程，我们只需运行 WorkflowProcessMain 类中的主要方法。