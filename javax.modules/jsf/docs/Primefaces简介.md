## 1. 简介

Primefaces 是用于[Java Server Faces](https://www.baeldung.com/spring-jsf)[ (JSF)](https://www.baeldung.com/spring-jsf) 应用程序的开源 UI 组件套件。

在本教程中，我们将介绍 Primefaces，并演示如何配置它和使用它的一些主要功能。

## 2.概述

### 2.1.Java服务器界面

Java Server Faces 是一个面向组件的框架，用于为JavaWeb 应用程序构建用户界面。JSF 规范通过JavaCommunity Process 正式化，是一种标准化的显示技术。

可以在[此处](https://www.baeldung.com/spring-jsf)找到有关 Spring 环境中的 JSF 的更多信息。

### 2.2. 素颜

Primefaces建立在 JSF 之上，通过提供可添加到任何项目的预构建 UI 组件来支持快速应用程序开发。

除了 Primefaces，还有[Primefaces Extensions](https://primefaces-extensions.github.io/)项目。这个基于社区的开源项目提供了标准组件之外的其他组件。

## 3. 应用设置

为了演示一些 Primefaces 组件，让我们使用 Maven 创建一个简单的 Web 应用程序。

### 3.1. Maven 配置

Primefaces 有一个只有一个 jar 的轻量级配置，所以要开始，让我们将依赖项添加到我们的pom.xml：

```xml
<dependency>
    <groupId>org.primefaces</groupId>
    <artifactId>primefaces</artifactId>
    <version>6.2</version>
</dependency>
```

最新版本可以在[这里](https://search.maven.org/classic/#search|ga|1|g%3A"org.primefaces")找到。

### 3.2. 控制器——托管 Bean

接下来，让我们为我们的组件创建 bean 类：

```java
@ManagedBean(name = "helloPFBean")
public class HelloPFBean {
}
```

我们需要提供一个@ManagedBean 注解来将我们的控制器绑定到视图组件。

### 3.3. 看法

最后，让我们在我们的 . xhtml文件：

```xml
<html xmlns:p="http://primefaces.org/ui">

```

## 4. 示例组件

要呈现页面，请启动服务器并导航至：

```xml
http://localhost:8080/jsf/pf_intro.xhtml
```

### 4.1. 面板网格

让我们使用PanelGrid作为标准 JSF panelGrid的扩展：

```xml
<p:panelGrid columns="2">
    <h:outputText value="#{helloPFBean.firstName}"/>
    <h:outputText value="#{helloPFBean.lastName}" />
</p:panelGrid>

```

在这里，我们定义了一个 包含两列 的panelGrid ，并设置了来自 JSF facelets 的outputText 以显示来自托管 bean 的数据。

每个outputText 中声明的值对应 于我们的@ManagedBean中声明的firstName 和 lastName 属性：

```java
private String firstName;
private String lastName;

```

让我们来看看我们的第一个简单组件：

```xml
<img class=" wp-image-32802 alignnone" style="font-family: Georgia, 'Times New Roman', 'Bitstream Charter', Times, serif; white-space: normal;" src="https://www.baeldung.com/wp-content/uploads/2018/04/panelGridPF-300x68.png" alt="" width="256" height="58" />
```

### 4.2. 选择一个电台

我们可以使用 selectOneRadio 组件来提供单选按钮功能：

```xml
<h:panelGrid columns="2">
    <p:outputLabel for="jsfCompSuite" value="Component Suite" />
    <p:selectOneRadio id="jsfCompSuite" value="#{helloPFBean.componentSuite}">
        <f:selectItem itemLabel="ICEfaces" itemValue="ICEfaces" />
        <f:selectItem itemLabel="RichFaces" itemValue="Richfaces" />
    </p:selectOneRadio>
</h:panelGrid>

```

我们需要在支持 bean 中声明值变量来保存单选按钮值：

```java
private String componentSuite;

```

此设置将产生一个 2 选项单选按钮，该按钮绑定到底层String属性 componentSuite：

[![选择OneRadioPF](https://www.baeldung.com/wp-content/uploads/2018/04/selectOneRadioPF.png)](https://www.baeldung.com/wp-content/uploads/2018/04/selectOneRadioPF.png)

### 4.3. 数据表

接下来，让我们使用 dataTable组件以表格布局显示数据：

```xml
<p:dataTable var="technology" value="#{helloPFBean.technologies}">
    <p:column headerText="Name">
        <h:outputText value="#{technology.name}" />
    </p:column>

    <p:column headerText="Version">
        <h:outputText value="#{technology.currentVersion}" />
    </p:column>
</p:dataTable>
```

同样，我们需要提供一个 Bean 属性来保存我们表的数据：

```java
private List<Technology> technologies;

```

在这里，我们列出了各种技术及其版本号：

[![数据表PF](https://www.baeldung.com/wp-content/uploads/2018/04/datatablePF-1024x119.png)](https://www.baeldung.com/wp-content/uploads/2018/04/datatablePF-1024x119.png)

### 4.4. 带有输入文本的Ajax

我们还可以使用 p:ajax 为我们的组件提供 Ajax 特性。

例如，让我们使用此组件来应用模糊事件：

```xml
<h:panelGrid columns="3">
    <h:outputText value="Blur event " />
    <p:inputText id="inputTextId" value="#{helloPFBean.inputText}}">
        <p:ajax event="blur" update="outputTextId"
	  listener="#{helloPFBean.onBlurEvent}" />
    </p:inputText>
    <h:outputText id="outputTextId" 
      value="#{helloPFBean.outputText}" />
</h:panelGrid>

```

因此，我们需要在 bean 中提供属性：

```java
private String inputText;
private String outputText;

```

此外，我们还需要在我们的 bean 中为我们的 AJAX blur 事件提供一个监听器方法：

```java
public void onBlurEvent() {
    outputText = inputText.toUpperCase();
}
```

在这里，我们只是将文本转换为大写来演示该机制：

[![模糊PF](https://www.baeldung.com/wp-content/uploads/2018/04/blurPF.png)](https://www.baeldung.com/wp-content/uploads/2018/04/blurPF.png)

### 4.5. 按钮

除此之外，我们还可以使用 p:commandButton 作为标准h:commandButton 组件的扩展 。

例如：

```xml
<p:commandButton value="Open Dialog" 
  icon="ui-icon-note" 
  onclick="PF('exDialog').show();">
</p:commandButton>

```

结果，通过此配置，我们有了用于打开对话框的按钮(使用onclick属性)：

[![命令按钮](https://www.baeldung.com/wp-content/uploads/2018/04/commandButton-300x77.png)](https://www.baeldung.com/wp-content/uploads/2018/04/commandButton-300x77.png)

### 4.6. 对话

此外，为了提供对话框的功能，我们可以使用 p:dialog组件。

我们还使用上一个示例中的按钮在单击时打开对话框：

```xml
<p:dialog header="Example dialog" widgetVar="exDialog" minHeight="40">
    <h:outputText value="Hello Baeldung!" />
</p:dialog>
```

在这种情况下，我们有一个包含基本配置的对话框，可以使用上一节中描述的commandButton触发：

[![对话](https://www.baeldung.com/wp-content/uploads/2018/04/dialog-300x137.png)](https://www.baeldung.com/wp-content/uploads/2018/04/dialog-300x137.png)

## 5. Primefaces 手机

Primefaces Mobile (PFM)提供了一个 UI 工具包来为移动设备创建 Primefaces 应用程序。

为此，PFM 支持针对移动设备调整的响应式设计。

### 5.1. 配置

首先，我们需要在faces-config.xml中启用移动导航支持：

```xml
<navigation-handler>
    org.primefaces.mobile.application.MobileNavigationHandler
</navigation-handler>
```

### 5.2. 命名空间

然后，要使用 PFM 组件，我们需要在.xhtml文件中包含 PFM 命名空间：

```xml
xmlns:pm="http://primefaces.org/mobile"
```

除了标准的 Primefaces jar 之外，我们的配置中不需要任何其他库。

### 5.3. 渲染套件

最后，我们需要提供RenderKit，用于渲染移动环境中的组件。

如果应用程序中只有一个 PFM 页面，我们可以在页面中定义一个RenderKit：

```xml
<f:view renderKitId="PRIMEFACES_MOBILE" />
```

使用完整的 PFM 应用程序，我们可以在faces-config.xml中的应用程序范围内定义我们的RenderKit：

```xml
<default-render-kit-id>PRIMEFACES_MOBILE</default-render-kit-id>

```

### 5.4. 示例页面

现在，让我们制作示例 PFM 页面：

```xml
<pm:page id="enter">
    <pm:header>
        <p:outputLabel value="Introduction to PFM"></p:outputLabel>
    </pm:header>
    <pm:content>
        <h:form id="enterForm">
            <pm:field>
	        <p:outputLabel 
                  value="Enter Magic Word">
                </p:outputLabel>
	        <p:inputText id="magicWord" 
                  value="#{helloPFMBean.magicWord}">
                </p:inputText>
	    </pm:field>
            <p:commandButton 
              value="Go!" action="#{helloPFMBean.go}">
            </p:commandButton>
	</h:form>
     </pm:content>
</pm:page>
```

可以看出，我们使用 了 PFM 中的页面、页眉和 内容 组件来构建一个带有页眉的简单表单：

[![pfmIntroBaeldung](https://www.baeldung.com/wp-content/uploads/2018/04/pfmIntroBaeldung-1024x233.png)](https://www.baeldung.com/wp-content/uploads/2018/04/pfmIntroBaeldung-1024x233.png)

此外，我们使用我们的支持 bean 进行用户输入检查和导航：

```java
public String go() {
    if(this.magicWord != null 
      && this.magicWord.toUpperCase().equals("BAELDUNG")) {
	return "pm:success";
     }
    
    return "pm:failure";
}
```

如果单词正确，我们导航到下一页：

```xml
<pm:page id="success">
    <pm:content>
        <p:outputLabel value="Correct!">
        </p:outputLabel>			
	<p:button value="Back" 
          outcome="pm:enter?transition=flow">
        </p:button>
    </pm:content>
</pm:page>
```

此配置导致此布局：

[![正确的页面PFM](https://www.baeldung.com/wp-content/uploads/2018/04/correctPagePFM-1024x138.png)](https://www.baeldung.com/wp-content/uploads/2018/04/correctPagePFM-1024x138.png)

 

如果单词不正确，我们导航到下一页：

```xml
<pm:page id="failure">
    <pm:content>
        <p:outputLabel value="That is not the magic word">
        </p:outputLabel>
	<p:button value="Back" outcome="pm:enter?transition=flow">
        </p:button>
    </pm:content>
</pm:page>
```

此配置将导致此布局：

[![不正确的词PFM](https://www.baeldung.com/wp-content/uploads/2018/04/incorrectWordPFM-1024x137.png)](https://www.baeldung.com/wp-content/uploads/2018/04/incorrectWordPFM-1024x137.png)

请注意，[PFM](https://www.primefaces.org/primefaces-6-2-roadmap/)在 6.2 版中已弃用，并将在 6.3 版中删除，以支持响应式标准套件。

## 六. 总结

在本教程中，我们解释了使用 Primefaces JSF 组件套件的好处，并演示了如何在基于 Maven 的项目中配置和使用 Primefaces。

此外，我们还推出了专为移动设备设计的 UI 工具包 Primefaces Mobile。