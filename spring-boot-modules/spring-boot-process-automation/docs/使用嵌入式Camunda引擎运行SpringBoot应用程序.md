## 1. 概述

工作流引擎在业务流程自动化中起着重要作用，[Camunda](https://camunda.com/)**平台是一个开源工作流和业务流程管理系统(BPMS)，它为业务流程建模提供流程引擎**。Spring Boot与Camunda平台有很好的集成。在本教程中，我们将了解如何**将嵌入式Camunda引擎利用到Spring Boot应用程序中**。

## 2. Camunda工作流引擎

Camunda工作流引擎是[Activiti]()的一个分支，提供基于[业务流程建模符号2.0(BPMN2.0)](https://www.bpmn.org/)标准的工作流和模拟引擎。此外，它还包含用于建模、执行和监控的工具和API。首先，**我们可以使用Modeler对我们的端到端业务流程进行**[建模](https://camunda.com/download/modeler/)，Camunda提供了用于设计BPMN工作流的建模器，Modeler作为桌面应用程序在本地运行，然后，我们将业务流程模型部署到工作流引擎并执行它。我们可以使用REST API和提供的Web应用程序(Cockpit、Tasklist和Admin)以不同的方式执行业务流程，Camunda引擎可以以不同的方式使用：SaaS、自我管理和可嵌入库。**在本教程中，我们重点介绍Spring Boot应用程序中的Camunda嵌入式引擎**。

## 3. 使用嵌入式Camunda引擎创建Spring Boot应用程序

在本节中，我们将使用[Camunda Platform Initializr](https://start.camunda.com/)创建和配置带有嵌入式Camunda引擎的Spring Boot应用程序。

### 3.1 Camunda Platform Initializr

**我们可以使用Camunda Platform Initializr创建一个与Camunda引擎集成的Spring Boot应用程序**，它是Camunda提供的一个Web应用程序工具，类似于[Spring Initializr](https://start.spring.io/)。让我们在Camunda Platform Initializr中使用以下信息创建应用程序：

<img src="../assets/img.png">

该工具允许我们添加项目元数据，包括Group、Artifact、Camunda BPM版本、H2数据库和Java版本。此外，我们可以添加Camunda BPM模块在我们的Spring Boot应用程序中支持Camunda REST API或Camunda Webapps。此外，我们可以添加Spring Boot Web和Security模块，我们拥有的另一个选项是设置管理员用户名和密码，这是在Camunda Web应用程序(例如Cockpit应用程序登录)中使用所必需的。现在，我们单击Generate Project将项目模板下载为.zip文件，最后，我们提取文件并在我们的IDE中打开pom.xml。

### 3.2 Camunda配置

**生成的项目是一个常规的Spring Boot应用程序，具有额外的Camunda依赖项和配置**，目录结构如下图所示：

<img src="../assets/img_1.png">

resources目录中有一个简单的流程图process.bpmn，它使用起始节点开始执行流程，之后，它将继续执行Say hello to demo任务。完成此任务后，执行会在遇到最终节点时停止。Camunda属性存在于application.yaml中，让我们看一下application.yaml中生成的默认Camunda属性：

```yaml
camunda.bpm.admin-user:
    id: demo
    password: demo
```

我们可以使用camunda.bpm.admin-user属性更改管理员用户名和密码。

## 4. 使用Spring Boot创建应用程序

使用嵌入式Camunda引擎创建Spring Boot应用程序的另一种方法是从头开始使用Spring Boot并向其中添加Camunda库。

### 4.1 Maven依赖项

首先我们在pom.xml中声明[camunda-bpm-spring-boot-starter-webapp](https://search.maven.org/search?q=g:org.camunda.bpm.springboot AND a:camunda-bpm-spring-boot-starter-webapp)依赖项：

```xml
<dependency>
    <groupId>org.camunda.bpm.springboot</groupId>
    <artifactId>camunda-bpm-spring-boot-starter-webapp</artifactId>
    <version>7.18.0</version>
</dependency>
```

我们需要一个数据库来存储流程定义、流程实例、历史信息等。在本教程中，我们使用基于文件的H2数据库，因此我们需要添加[h2](https://search.maven.org/search?q=g:com.h2database a:h2)和[spring-boot-starter-jdbc](https://search.maven.org/search?q=a:spring-boot-starter-jdbc g:org.springframework.boot)依赖：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
```

### 4.2 示例流程模型

我们使用Camunda Modeler来定义一个简单的贷款请求工作流程图loanProcess.bpmn，下面是loanProcess.bpmn模型执行顺序的图形流程图，以帮助我们理解：

<img src="../assets/img_2.png">

我们使用起始节点开始执行该流程，之后，执行”Calculate Interest“任务。接下来，我们将继续执行”Approve Loan“任务。完成此任务后，执行会在遇到最终节点时停止。Calculate Interest任务是一个服务任务并调用CalculateInterestService bean：

```java
@Component
public class CalculateInterestService implements JavaDelegate {

	private static final Logger LOGGER = LoggerFactory.getLogger(CalculateInterestService.class);

	@Override
	public void execute(DelegateExecution execution) {
		LOGGER.info("calculating interest of the loan");
	}
}
```

**我们需要实现JavaDelegate接口**，此类可用于服务任务和事件监听器，**它在流程执行期间调用的execute()方法中提供了所需的逻辑**。现在，应用程序已准备好启动。

## 5. 示范

现在让我们运行Spring Boot应用程序，我们打开浏览器并输入网址http://localhost:8080/：

<img src="../assets/img_3.png">

我们输入用户凭据(demo/demo)并访问Camunda Web应用程序Cockpit、Tasklist和Admin：

<img src="../assets/img_4.png">

### 5.1 Cockpit应用程序

**Camunda Cockpit Web应用程序为用户提供了监控已实施流程及其操作的实时视图的设施**，我们可以看到启动Spring Boot应用程序时自动部署的流程数：

<img src="../assets/img_5.png">

正如我们所看到的，有一个已部署的流程(loanProcess.bpmn)，我们可以通过点击已部署的流程来查看已部署的流程示意图：

<img src="../assets/img_6.png">

现在，这个流程还没有开始，我们可以使用Tasklist应用程序启动它。

### 5.2 Tasklist应用程序

**Camunda Tasklist应用程序用于管理用户与其任务的交互**，我们可以通过单击Start process菜单项来启动示例流程：

<img src="../assets/img_7.png">

启动流程后，将执行”Calculate Interest“任务，它记录日志到控制台：

```shell
21:53:34.727 [http-nio-8080-exec-8] INFO  c.t.t.c.t.CalculateInterestService - calculating interest of the loan
```

现在，我们可以在Cockpit应用程序中看到正在运行的流程实例：

<img src="../assets/img_8.png">

请注意，该流程正在等待”Approve Loan“用户任务，在这一步中，我们将任务分配给demo用户。因此，demo用户可以在Tasklist应用程序中看到任务：

<img src="../assets/img_9.png">

我们可以通过单击Complete按钮来完成任务，最后我们可以在Cockpit应用中看到流程运行完成。

### 5.3 Admin应用程序

**Camunda Admin应用程序用于管理用户及其对系统的访问**；此外，我们可以管理租户和组：

<img src="../assets/img_10.png">

## 6. 总结

在本文中，我们讨论了使用嵌入式Camunda引擎设置Spring Boot应用程序的基础知识，我们使用Camunda Platform Initializr工具和Spring Boot从头开始创建应用程序。此外，我们使用Camunda Modeler定义了一个简单的贷款请求流程模型。此外，我们使用Camunda Web应用程序开始并探索这个流程模型。本文中显示的代码的工作版本可在[GitHub]()上找到。