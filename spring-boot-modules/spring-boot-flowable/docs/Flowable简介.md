## 1. 概述

[Flowable](https://www.flowable.org/)是一个用Java编写的业务流程引擎。在本教程中，我们将详细介绍业务流程并了解我们如何利用Flowable Java API来创建和部署示例业务流程。

## 2. 了解业务流程

简而言之，**业务流程是一组任务，一旦按照定义的顺序完成，就可以实现定义的目标**。业务流程中的每个任务都有明确定义的输入和输出，这些任务可能需要人工干预，也可能是完全自动化的。

**OMG(对象管理组)定义了一个称为[业务流程模型和表示法(BPMN)](http://www.bpmn.org/)的标准，供企业定义和传达其流程**。BPMN在业界得到了广泛的支持和接受，Flowable API完全支持创建和部署BPMN 2.0流程定义。

## 3. 创建流程定义

假设我们有一个在发布前进行文章审核的简单流程。

这个过程的要点是作者提交一篇文章，编辑要么接受要么拒绝它。如果被接受，文章将立即发表；但是，如果被拒绝，作者将通过电子邮件收到通知：

<img src="../assets/img.png">

**我们使用BPMN 2.0 XML标准将流程定义创建为XML文件**。

让我们根据BPMN 2.0标准定义我们的简单流程：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<definitions
        xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:xsd="http://www.w3.org/2001/XMLSchema"
        xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
        xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC"
        xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI"
        xmlns:flowable="http://flowable.org/bpmn"
        typeLanguage="http://www.w3.org/2001/XMLSchema"
        expressionLanguage="http://www.w3.org/1999/XPath"
        targetNamespace="http://www.flowable.org/processdef">
    <process id="articleReview" name="A simple process for article review." isExecutable="true">
        <startEvent id="start"/>
        <sequenceFlow sourceRef="start" targetRef="reviewArticle"/>
        <userTask id="reviewArticle" name="Review the submitted tutorial" flowable:candidateGroups="editors"/>
        <sequenceFlow sourceRef="reviewArticle" targetRef="decision"/>
        <exclusiveGateway id="decision"/>
        <sequenceFlow sourceRef="decision" targetRef="tutorialApproved">
            <conditionExpression xsi:type="tFormalExpression">
                <![CDATA[${approved}]]>
            </conditionExpression>
        </sequenceFlow>
        <sequenceFlow sourceRef="decision" targetRef="tutorialRejected">
            <conditionExpression xsi:type="tFormalExpression">
                <![CDATA[${!approved}]]>
            </conditionExpression>
        </sequenceFlow>
        <serviceTask id="tutorialApproved" name="Publish the approved tutorial."
                     flowable:class="cn.tuyucheng.taketoday.service.PublishArticleService"/>
        <sequenceFlow sourceRef="tutorialApproved" targetRef="end"/>
        <serviceTask id="tutorialRejected" name="Send out rejection email"
                     flowable:class="cn.tuyucheng.taketoday.service.SendMailService"/>
        <sequenceFlow sourceRef="tutorialRejected" targetRef="end"/>
        <endEvent id="end"/>
    </process>
</definitions>
```

现在，这里有相当多的元素是标准的XML内容，而其他元素是BPMN 2.0特有的：

-   整个过程被包装在一个名为“process”的标签中，而这个标签又是一个名为“definitions”的标签的一部分
-   流程由事件、流、任务和网关组成
-   事件是开始事件或结束事件
-   流(在此示例中为序列流)连接其他元素，如事件和任务
-   **任务是完成实际工作的地方；这些可以是“用户任务”或“服务任务”等**
-   用户任务需要人类用户与Flowable API交互并采取行动
-   服务任务代表一个自动任务，它可以是对Java类的调用，甚至是HTTP调用
-   网关根据“approved”属性执行；**这被称为过程变量**，稍后我们会看到如何设置它们

虽然我们可以在任何文本编辑器中创建流程定义文件，但这并不总是最方便的方法。不过，幸运的是，Flowable还带有用户界面选项，可以使用[Eclipse插件](https://www.flowable.org/docs/userguide/index.html#flowableDesigner)或[Web应用程序](https://www.flowable.org/docs/userguide/index.html#flowableApps)来执行此操作。如果你使用的是IntelliJ，也可以使用[IntelliJ插件](https://plugins.jetbrains.com/plugin/14318-flowable-bpmn-visualizer)。

## 4. 使用Flowable API

现在我们已经按照BPMN 2.0标准在XML文件中定义了我们的简单流程，我们需要一种方法来提交和运行它。**Flowable提供Process Engine API来与Flowable Engines交互**，Flowable非常灵活，并提供了多种部署此API的方法。

鉴于Flowable是一个Java API，我们可以通过简单地包含必需的JAR文件来将流程引擎包含在任何Java应用程序中，我们可以很好地利用Maven来管理这些依赖项。

此外，Flowable附带了捆绑的API，可以通过HTTP与Flowable进行交互。我们可以使用这些API来做几乎所有通过Flowable API可能做的事情。

最后，**Flowable对与Spring和Spring Boot的集成有很好的支持！**我们将在教程中使用Flowable和Spring Boot集成。

## 5. 使用流程引擎创建Demo应用程序

现在让我们创建一个简单的应用程序，该应用程序包装了来自Flowable的流程引擎，并提供了一个基于HTTP的API来与Flowable API进行交互。在API之上可能还有一个Web或移动应用程序来提供更好的体验，但在本教程中，我们将跳过这一点。

我们将创建我们的演示作为Spring Boot应用程序。

### 5.1 依赖关系

首先，让我们看看我们需要从Maven中提取的依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.flowable</groupId>
    <artifactId>flowable-spring-boot-starter</artifactId>
    <version>6.4.1</version>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

这些依赖项都可以在Maven Central找到：

-   [Web的Spring Boot Starter](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-web)：这是Spring Boot的标准Starter
-   [Spring Boot的Flowable Starter](https://search.maven.org/search?q=g:org.flowable AND a:flowable-spring-boot-starter)：这是Spring Boot Flowable Engines所必需的
-   [H2数据库](https://search.maven.org/search?q=g:com.h2database AND a:h2)：Flowable需要一个数据库来存储数据，H2是默认的内存数据库

### 5.2 流程定义

**当我们启动Spring Boot应用程序时，它会尝试自动加载文件夹“resources/processes”下存在的所有流程定义**。因此，让我们使用上面创建的流程定义创建一个XML文件，名称为“article-workflow.bpmn20.xml”，并将其放在该文件夹中。

### 5.3 配置

正如我们所知，Spring Boot对应用程序配置采用了一种高度自以为是的方法，这对于作为Spring Boot的一部分的Flowable也是如此。例如，**当将H2检测为类路径上唯一的数据库驱动程序时，Flowable会自动配置它以供使用**。

显然，每个可配置的方面都可以通过[应用程序属性](https://www.flowable.org/docs/userguide/index.html#springBootFlowableProperties)以自定义方式进行配置。但是，对于本教程，我们坚持使用默认值。

### 5.4 Java委托

在我们的流程定义中，我们使用了几个应该作为服务任务的一部分调用的Java类，**这些类实现了JavaDelegate接口，在Flowable中被称为Java委托**。现在，我们将为这些Java委托定义虚拟类：

```java
public class PublishArticleService implements JavaDelegate {
    public void execute(DelegateExecution execution) {
        System.out.println("Publishing the approved article.");
    }
}
```

```java
public class SendMailService implements JavaDelegate {
    public void execute(DelegateExecution execution) {
        System.out.println("Sending rejection mail to author.");
    }
}
```

显然，我们必须用实际的服务替换这些虚拟类才能发表文章或发送电子邮件。

### 5.5 HTTP API

最后，让我们创建一些端点来与流程引擎交互并使用我们定义的流程。

首先我们创建一个公开三个端点的控制器：

```java
@RestController
public class ArticleWorkflowController {
    
    @Autowired
    private ArticleWorkflowService service;

    @PostMapping("/submit")
    public void submit(@RequestBody Article article) {
        service.startProcess(article);
    }

    @GetMapping("/tasks")
    public List<Article> getTasks(@RequestParam String assignee) {
        return service.getTasks(assignee);
    }

    @PostMapping("/review")
    public void review(@RequestBody Approval approval) {
        service.submitReview(approval);
    }
}
```

我们的控制器公开端点以提交一篇文章供审阅，获取要审阅的文章列表，最后提交一篇文章的审阅。Article和Approval是可以在仓库中找到的标准POJO类。

**我们实际上将大部分工作委托给ArticleWorkflowService**：

```java
@Service
public class ArticleWorkflowService {
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Transactional
    public void startProcess(Article article) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("author", article.getAuthor());
        variables.put("url", article.getUrl());
        runtimeService.startProcessInstanceByKey("articleReview", variables);
    }

    @Transactional
    public List<Article> getTasks(String assignee) {
        List<Task> tasks = taskService.createTaskQuery()
              .taskCandidateGroup(assignee)
              .list();
        
        return tasks.stream()
              .map(task -> {
                  Map<String, Object> variables = taskService.getVariables(task.getId());
                  return new Article(task.getId(), (String) variables.get("author"), (String) variables.get("url"));
              })
              .collect(Collectors.toList());
    }

    @Transactional
    public void submitReview(Approval approval) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", approval.isStatus());
        taskService.complete(approval.getId(), variables);
    }
}
```

现在，这里的大部分代码都非常直观，但让我们了解要点：

-   RuntimeService用于实例化特定提交的过程
-   TaskService用于查询和更新任务
-   **将所有数据库调用包装在Spring支持的事务中**
-   将作者和URL等详细信息存储在Map中，并与流程实例一起保存；**这些被称为流程变量，我们可以在流程定义中访问它们**，正如我们之前看到的

现在，我们准备测试我们的应用程序和流程引擎。启动应用程序后，我们可以简单地使用curl或任何REST客户端(如Postman)与我们创建的端点进行交互。

## 6. 单元测试过程

**Flowable支持不同版本的JUnit，包括JUnit 5，用于为业务流程创建单元测试**。Flowable与Spring的集成也对此提供了适当的支持，让我们看一下Spring中一个流程的典型单元测试：

```java
@ExtendWith(FlowableSpringExtension.class)
@ExtendWith(SpringExtension.class)
class ArticleWorkflowUnitTest {
    
    @Autowired
    private RuntimeService runtimeService;
 
    @Autowired
    private TaskService taskService;
 
    @Test
    @Deployment(resources = { "processes/article-workflow.bpmn20.xml" })
    void articleApprovalTest() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("author", "test@baeldung.com");
        variables.put("url", "http://baeldung.com/dummy");
 
        runtimeService.startProcessInstanceByKey("articleReview", variables);
        Task task = taskService.createTaskQuery().singleResult();
 
        assertEquals("Review the submitted tutorial", task.getName());
 
        variables.put("approved", true);
        taskService.complete(task.getId(), variables);
 
        assertEquals(0, runtimeService.createProcessInstanceQuery().count());
    }
}
```

这看起来应该很像Spring中的标准单元测试，除了像@Deployment这样的一些注解。现在，@Deployment注解由Flowable提供，用于围绕测试方法创建和删除流程部署。

## 7. 了解流程的部署

虽然我们不会在本教程中介绍流程部署的细节，但值得介绍一些重要的方面。

通常，**流程作为业务归档(BAR)存档并部署在应用程序中**。在部署时，会扫描此存档中的工件(如流程定义)并进行处理，你可能已经注意到以“.bpmn20.xml”结尾的流程定义文件的约定。

虽然我们在教程中使用了默认的内存H2数据库，但这实际上不能在真实应用程序中使用，原因很简单，内存数据库不会在启动时保留任何数据，并且实际上是无法在集群环境中使用！因此，我**们必须使用生产级关系数据库，并在应用程序中提供所需的配置**。

虽然BPMN 2.0本身没有任何版本控制的概念，但**Flowable为流程创建了一个版本属性，该属性部署在数据库中**。如果部署了由属性“id”标识的同一流程的更新版本，则会创建一个新条目，其中版本会增加。当我们尝试通过“id”启动流程时，流程引擎会获取部署的流程定义的最新版本。

如果我们使用之前讨论过的设计器之一来创建流程定义，那么我们已经有了流程的可视化效果，**我们可以将流程图导出为图像并将其与XML流程定义文件放在一起**。如果我们坚持Flowable建议的标准[命名约定](https://www.flowable.org/docs/userguide/index.html#providingProcessDiagram)，则这个图像将与流程本身一起由流程引擎处理。此外，我们还可以通过API获取此图像！

## 8. 流程实例的浏览历史记录

在业务流程的情况下，了解过去发生的事情通常至关重要。我们可能需要它来进行简单的调试或复杂的审计目的。

**Flowable记录了流程执行过程中发生的事情，并将其保存在数据库中**。此外，Flowable通过API使这些历史记录可供查询和分析。Flowable在六个实体下记录了这些内容，并且HistoryService具有查询所有实体的方法。

让我们看一个简单的查询来获取已完成的流程实例：

```java
HistoryService historyService = processEngine.getHistoryService();
List<HistoricActivityInstance> activities = historyService
	  .createHistoricActivityInstanceQuery()
      .processInstanceId(processInstance.getId())
      .finished()
      .orderByHistoricActivityInstanceEndTime()
      .asc()
      .list();
```

正如我们所见，**查询记录数据的API是非常可组合的**。在此示例中，我们按ID查询已完成的流程实例，并按结束时间的升序对它们进行排序。

## 9. 监控流程

监控是任何关键业务应用程序的一个关键方面，对于处理组织业务流程的应用程序更是如此，Flowable有几个选项可以让我们实时监控流程。

**Flowable提供了我们可以通过JMX访问的特定MBean**，不仅可以收集数据进行监控，还可以执行许多其他活动。我们可以将它与任何标准的JMX客户端集成，包括jconsole，它与标准Java发行版一起提供。

使用JMX进行监控打开了很多选项，但相对复杂且耗时。但是，由于我们使用的是Spring Boot，因此我们很幸运！

Spring Boot提供了[Actuator端点]()，用于通过HTTP收集应用程序指标，我们可以将其与[Prometheus和Grafana](https://prometheus.io/docs/visualization/grafana/)等工具堆栈无缝集成，以最小的工作量创建生产级监控工具。

**Flowable提供了一个额外的Actuator端点用于公开有关正在运行的进程的信息**，这不如通过JMX收集信息好，但它快速、简单，而且最重要的是，足够了。

## 10. 总结

在本教程中，我们讨论了业务流程以及如何在BPMN 2.0标准中定义它们。然后，我们讨论了Flowable流程引擎和API部署和执行流程的能力，我们看到了如何将其集成到Java应用程序中，特别是在Spring Boot中。

接下来，我们讨论了流程的其他重要方面，例如部署、可视化和监控。不用说，我们只是触及了业务流程和像Flowable这样强大的引擎的皮毛。Flowable有非常丰富的API和足够的可用文档。