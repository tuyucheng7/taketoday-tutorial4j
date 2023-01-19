## 1. 简介

这篇文章是关于 Structurizr 的，它是一种为基于[C4 模型](https://www.structurizr.com/help/c4)的架构定义和可视化提供编程方法的工具。

Structurizr 打破了 UML 等架构图编辑器的传统拖放方法，允许我们使用我们最了解的工具Java来描述我们的架构工件。

## 2. 开始

首先，让我们将[structurizr-core](https://search.maven.org/search?q=a:structurizr-core)依赖项添加到我们的pom.xml 中：

```xml
<dependency>
    <groupId>com.structurizr</groupId>
    <artifactId>structurizr-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 3.系统

让我们开始为示例架构建模。假设我们正在构建一个具有欺诈检测功能的支付终端，供商家用于清算支付。

首先，我们需要创建一个工作区和一个模型：

```java
Workspace workspace = new Workspace("Payment Gateway", "Payment Gateway");
Model model = workspace.getModel();
```

我们还在该模型中定义了一个用户和两个软件系统：

```java
Person user = model.addPerson("Merchant", "Merchant");
SoftwareSystem paymentTerminal = model.addSoftwareSystem(
  "Payment Terminal", "Payment Terminal");
user.uses(paymentTerminal, "Makes payment");
SoftwareSystem fraudDetector = model.addSoftwareSystem(
  "Fraud Detector", "Fraud Detector");
paymentTerminal.uses(fraudDetector, "Obtains fraud score");

```

现在我们的系统已经定义好了，我们可以创建一个视图：

```java
ViewSet viewSet = workspace.getViews();

SystemContextView contextView = viewSet.createSystemContextView(
  paymentTerminal, "context", "Payment Gateway Diagram");
contextView.addAllSoftwareSystems();
contextView.addAllPeople();
```

这里我们创建了一个包含所有软件系统和人员的视图。现在需要渲染视图。

## 4.通过PlantUML查看

在上一节中，我们创建了一个简单支付网关的视图。

下一步是创建一个人性化的图表。对于已经使用[PlantUML](http://plantuml.com/)的组织来说，最简单的解决方案可能是指示 Structurizr 执行 PlantUML 导出：

```java
StringWriter stringWriter = new StringWriter();
PlantUMLWriter plantUMLWriter = new PlantUMLWriter();
plantUMLWriter.write(workspace, stringWriter);
System.out.println(stringWriter.toString());
```

这里生成的标记被打印到屏幕上，但它也可以很容易地发送到文件中。以这种方式呈现数据会产生下图：

[![尝试](https://www.baeldung.com/wp-content/uploads/2017/06/try-255x300.png)](https://www.baeldung.com/wp-content/uploads/2017/06/try.png)

## 5.通过Structurizr网站查看

存在用于呈现图的另一个选项。可以通过客户端 API 将架构视图发送到 Structurizr 网站。然后将使用其丰富的 UI 生成图表。

让我们创建一个 API 客户端：

```java
StructurizrClient client = new StructurizrClient("key", "secret");
```

密钥和秘密参数是从其网站上的工作区仪表板获取的。然后可以通过以下方式引用工作区：

```java
client.putWorkspace(1337, workspace);
```

显然，我们需要在网站上注册并创建一个工作区。具有单个工作区的基本帐户是免费的。同时，商业计划也是可用的。

## 6.容器

让我们通过添加一些容器来扩展我们的软件系统。在 C4 模型中，容器可以是 Web 应用程序、移动应用程序、桌面应用程序、数据库和文件系统：几乎所有包含代码和/或数据的东西。

首先，我们为支付终端创建一些容器：

```java
Container f5 = paymentTerminal.addContainer(
  "Payment Load Balancer", "Payment Load Balancer", "F5");
Container jvm1 = paymentTerminal.addContainer(
  "JVM-1", "JVM-1", "Java Virtual Machine");
Container jvm2 = paymentTerminal.addContainer(
  "JVM-2", "JVM-2", "Java Virtual Machine");
Container jvm3 = paymentTerminal.addContainer(
  "JVM-3", "JVM-3", "Java Virtual Machine");
Container oracle = paymentTerminal.addContainer(
  "oracleDB", "Oracle Database", "RDBMS");
```

接下来，我们定义这些新创建的元素之间的关系：

```java
f5.uses(jvm1, "route");
f5.uses(jvm2, "route");
f5.uses(jvm3, "route");

jvm1.uses(oracle, "storage");
jvm2.uses(oracle, "storage");
jvm3.uses(oracle, "storage");
```

最后，创建一个可以提供给渲染器的容器视图：

```java
ContainerView view = workspace.getViews()
  .createContainerView(paymentTerminal, "F5", "Container View");
view.addAllContainers();
```

通过 PlantUML 呈现结果图会产生：

[![集装箱](https://www.baeldung.com/wp-content/uploads/2017/06/Containers-300x279.png)](https://www.baeldung.com/wp-content/uploads/2017/06/Containers.png)

## 7. 组件

C4 模型中的下一层详细信息由组件视图提供。创建一个类似于我们之前所做的。

首先，我们在容器中创建一些组件：

```java
Component jaxrs = jvm1.addComponent("jaxrs-jersey", 
  "restful webservice implementation", "rest");
Component gemfire = jvm1.addComponent("gemfire", 
  "Clustered Cache Gemfire", "cache");
Component hibernate = jvm1.addComponent("hibernate", 
  "Data Access Layer", "jpa");
```

接下来，让我们添加一些关系：

```java
jaxrs.uses(gemfire, "");
gemfire.uses(hibernate, "");
```

最后，让我们创建视图：

```java
ComponentView componentView = workspace.getViews()
  .createComponentView(jvm1, JVM_COMPOSITION, "JVM Components");

componentView.addAllComponents();
```

通过 PlantUML 对结果图的再现导致：

[![成分](https://www.baeldung.com/wp-content/uploads/2017/06/Components-292x300.png)](https://www.baeldung.com/wp-content/uploads/2017/06/Components.png)

## 8. 成分提取

对于使用 Spring 框架的现有代码库，Structurizr 提供了一种自动提取 Spring 注解组件并将它们添加到架构工件的方法。

要使用此功能，我们需要添加另一个依赖项：

```xml
<dependency>
    <groupId>com.structurizr</groupId>
    <artifactId>structurizr-spring</artifactId>
    <version>1.0.0-RC5</version>
</dependency>
```

接下来，我们需要创建一个配置有一个或多个解析策略的ComponentFinder 。解析策略会影响诸如哪些组件将被添加到模型、依赖树遍历的深度等。

我们甚至可以插入自定义解析策略：

```java
ComponentFinder componentFinder = new ComponentFinder(
  jvm, "com.baeldung.structurizr",
  new SpringComponentFinderStrategy(
    new ReferencedTypesSupportingTypesStrategy()
  ),
  new SourceCodeComponentFinderStrategy(new File("/path/to/base"), 150));
```

最后，我们启动取景器：

```java
componentFinder.findComponents();
```

上面的代码扫描包com.baeldung.structurizr以查找 Spring 注解的 bean，并将它们作为组件添加到容器 JVM 中。不用说，我们可以自由地实现我们自己的扫描器、JAX-RS 注解资源甚至 Google Guice 活页夹。

下面了示例项目中的简单图表示例：

[![春天](https://www.baeldung.com/wp-content/uploads/2017/06/spring-300x190.png)](https://www.baeldung.com/wp-content/uploads/2017/06/spring.png)

## 9.总结

本快速教程涵盖了 Structurizr forJava项目的基础知识。