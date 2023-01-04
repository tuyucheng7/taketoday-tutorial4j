## 1. 概述

Spring Web Flow建立在Spring MVC之上，允许在Web应用程序中实现流程。它用于创建引导用户完成流程或某些业务逻辑的步骤序列。

在本快速教程中，我们将通过一个简单的用户激活流程示例。用户会看到一个页面，然后单击“激活”按钮继续或单击“取消”按钮取消激活。

并不是说这里的假设是我们有一个已经设置好的Spring MVC Web应用程序。

## 2. 设置

让我们首先将Spring Web Flow依赖项添加到pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.webflow</groupId>
    <artifactId>spring-webflow</artifactId>
    <version>2.5.0.RELEASE</version>
</dependency>
```

可以在[Central Maven Repository](https://search.maven.org/search?q=a:spring-webflow)中找到最新版本的Spring Web Flow。

## 3. 创建流程

现在让我们创建一个简单的流程。如前所述，流程是指导用户完成流程的一系列步骤。目前，这只能使用基于XML的配置来完成。

流程中的每个步骤都称为状态。

对于这个简单的示例，我们将使用视图状态。视图状态是呈现匹配视图的流程中的一个步骤。view-state指的是应用程序中的一个页面(WEB-INF/view)，view-state的id是它所指的页面的名称。

我们还将使用过渡元素。过渡元素用于处理特定状态中发生的事件。

对于这个示例流程，我们将设置三个视图状态-激活、成功和失败。

此流程的过程非常简单。起点是激活视图。如果激活事件被触发，它应该转换到成功视图。如果取消事件被触发，它应该转换到失败视图。过渡元素处理视图状态中发生的按钮单击事件：

```xml
<view-state id="activation">
    <transition on="activate" to="success"/>
    <transition on="cancel" to="failure"/>
</view-state>

<view-state id="success" />

<view-state id="failure" />
```

初始激活页面(由id activation引用并位于WEB-INF/view/activation.jsp中)是一个简单的页面，它有两个按钮，activate和cancel。单击按钮触发我们的转换，将用户发送到成功视图状态(WEB-INF/view/success.jsp)或失败视图状态(WEB-INF/view/failure.jsp)：

```html
<body>
    <h2>Click to activate account</h2>

    <form method="post" action="${flowExecutionUrl}">
        <input type="submit" name="_eventId_activate" value="activate" />
        <input type="submit" name="_eventId_cancel" value="cancel" />
    </form>
</body>
```

我们正在使用flowExecutionUrl来访问当前流程执行视图状态的上下文相关URI。

## 4. 配置流程

接下来，我们将Spring Web Flow配置到我们的Web环境中，我们将通过设置Flow Registry和Flow Builder Service来做到这一点。

Flow Registry允许我们指定我们的流的位置，如果正在使用，还可以指定Flow Builder Service。

Flow Builder Service帮助我们自定义用于构建流的服务和设置。

我们可以定制的服务之一是view-factory-creator。view-factory-creator允许我们自定义Spring Web Flow使用的ViewFactoryCreator。由于我们使用的是Spring MVC，因此我们可以将Spring Web Flow配置为在我们的Spring MVC配置中使用视图解析器。

以下是我们如何为示例配置Spring Web Flow：

```java
@Configuration
public class WebFlowConfig extends AbstractFlowConfiguration {

    @Autowired
    private WebMvcConfig webMvcConfig;

    @Bean
    public FlowDefinitionRegistry flowRegistry() {
        return getFlowDefinitionRegistryBuilder(flowBuilderServices())
              .addFlowLocation("/WEB-INF/flows/activation-flow.xml", "activationFlow")
              .build();
    }

    @Bean
    public FlowExecutor flowExecutor() {
        return getFlowExecutorBuilder(flowRegistry()).build();
    }

    @Bean
    public FlowBuilderServices flowBuilderServices() {
        return getFlowBuilderServicesBuilder()
              .setViewFactoryCreator(mvcViewFactoryCreator())
              .setDevelopmentMode(true).build();
    }

    @Bean
    public MvcViewFactoryCreator mvcViewFactoryCreator() {
        MvcViewFactoryCreator factoryCreator = new MvcViewFactoryCreator();
        factoryCreator.setViewResolvers(Collections.singletonList(this.webMvcConfig.viewResolver()));
        factoryCreator.setUseSpringBeanBinding(true);
        return factoryCreator;
    }
}
```

我们还可以为该配置使用XML：

```xml
<bean class="org.springframework.webflow.mvc.servlet.FlowHandlerMapping">
    <property name="flowRegistry" ref="activationFlowRegistry"/>
</bean>

<flow:flow-builder-services id="flowBuilderServices"
  view-factory-creator="mvcViewFactoryCreator"/>

<bean id="mvcViewFactoryCreator" 
  class="org.springframework.webflow.mvc.builder.MvcViewFactoryCreator">
    <property name="viewResolvers" ref="jspViewResolver"/>
</bean>

<flow:flow-registry id="activationFlowRegistry" 
  flow-builder-services="flowBuilderServices">
    <flow:flow-location id="activationFlow" path="/WEB-INF/flows/activation-flow.xml"/>
</flow:flow-registry>

<bean class="org.springframework.webflow.mvc.servlet.FlowHandlerAdapter">
    <property name="flowExecutor" ref="activationFlowExecutor"/>
</bean>
<flow:flow-executor id="activationFlowExecutor" 
  flow-registry="activationFlowRegistry"/>
```

## 5. 导航流程

要在流程中导航，请启动Web应用程序并转到http://localhost:8080/{context-path}/activationFlow。要启动该应用程序，请将其部署在应用程序服务器上，例如[Tomcat](https://www.baeldung.com/tomcat-deploy-war)或[Jetty](https://www.baeldung.com/deploy-to-jetty)。

这会将我们带到流程的初始页面，这是我们的流程配置中指定的激活页面：

<img src="../assets/img.png">

你可以点击激活按钮进入成功页面：

<img src="../assets/img_1.png">

或取消按钮转到失败页面：

<img src="../assets/img_2.png">

## 6. 总结

在本文中，我们使用了一个简单的示例来指导如何使用Spring Web Flow。