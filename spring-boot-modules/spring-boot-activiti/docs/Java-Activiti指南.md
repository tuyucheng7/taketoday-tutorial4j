## 1. 概述

Activiti API是一个工作流和业务流程管理系统，我们可以在其中定义一个流程，执行它，并使用API提供的服务以不同的方式操作它，它需要JDK 7+。

使用API的开发可以在任何IDE中完成，但要使用[Activiti Designer](https://www.activiti.org/userguide/index.html?_ga=2.55182893.76071610.1499064413-1368418377.1499064413#eclipseDesignerInstallation)，我们需要Eclipse。

我们可以使用BPMN 2.0标准在其中定义一个流程。还有另一种不太流行的方法-使用Java类，如StartEvent、EndEvent、UserTask、SequenceFlow等。

如果我们想要运行一个流程或访问任何服务，我们需要创建一个ProcessEngineConfiguration。

我们可以通过某些方式使用ProcessEngineConfiguration获取ProcessEngine，我们将在本文中进一步讨论。通过ProcessEngine我们可以执行Workflow和BPMN操作。

## 2. Maven依赖

要使用此API，我们需要包含Activiti依赖项：

```xml
<dependency>
    <groupId>org.activiti</groupId>
    <artifactId>activiti-engine</artifactId>
</dependency>
```

## 3. 创建流程引擎

Activiti中的ProcessEngine通常使用XML文件activiti.cfg.xml配置，此配置文件的一个示例是：

```xml
<beans xmlns="...">
    <bean id="processEngineConfiguration" class=
            "org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration">
        <property name="jdbcUrl"
                  value="jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000"/>
        <property name="jdbcDriver" value="org.h2.Driver"/>
        <property name="jdbcUsername" value="root"/>
        <property name="jdbcPassword" value=""/>
        <property name="databaseSchemaUpdate" value="true"/>
    </bean>
</beans>
```

现在我们可以使用ProcessEngines类获取ProcessEngine：

```java
ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
```

该语句将在classpath中查找一个activiti.cfg.xml文件，并根据文件中的配置构造一个ProcessEngine。

配置文件的示例代码表明它只是一个基于Spring的配置。但是，这并不意味着我们只能在Spring环境中使用Activiti，Spring的功能仅在内部用于创建ProcessEngine。

让我们编写一个JUnit测试用例，它将使用上面显示的配置文件创建ProcessEngine：

```java
@Test
void givenXMLConfig_whenGetDefault_thenGotProcessEngine() {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    assertNotNull(processEngine);
    assertEquals("root", processEngine.getProcessEngineConfiguration()
        .getJdbcUsername());
}
```

## 4. Activiti Process Engine API和服务

与API交互的入口点是ProcessEngine。通过ProcessEngine，我们可以访问各种提供工作流/BPMN方法的服务。ProcessEngine和所有服务对象都是线程安全的。

<img src="../assets/img.png">

取自：https://www.activiti.org/userguide/images/api.services.png

ProcessEngines类将扫描activiti.cfg.xml和activiti-context.xml文件。如前所述，对于所有activiti.cfg.xml文件，ProcessEngine将以典型方式创建。

然而，对于所有activiti-context.xml文件，它将以Spring方式创建-我将创建Spring Application Context并从中获取ProcessEngine。在流程执行期间，将按照BPMN文件中定义的顺序访问所有步骤。

在流程执行期间，将按照BPMN文件中定义的顺序访问所有步骤。

### 4.1 流程定义和相关术语

ProcessDefinition表示业务流程，它用于定义流程中不同步骤的结构和行为，部署流程定义意味着将流程定义加载到Activiti数据库中。

流程定义主要由BPMN 2.0标准定义，也可以使用Java代码定义它们，本节中定义的所有术语也可用作Java类。

一旦我们开始运行流程定义，它就可以称为流程。

ProcessInstance是ProcessDefinition的一次执行。

StartEvent与每个业务流程相关联，它指示过程的入口点。类似地，还有一个EndEvent表示过程结束。我们可以定义这些事件的条件。

开始和结束之间的所有步骤(或元素)都称为Tasks，任务可以有多种类型，最常用的任务是UserTasks和ServiceTasks。

顾名思义，UserTasks需要由用户手动执行。

另一方面，ServiceTasks是用一段代码配置的，每当执行到达他们时，他们的代码块将被执行。

SequenceFlows连接任务，我们可以通过它们将连接的源和目标元素来定义SequenceFlow。同样，我们还可以在SequenceFlow上定义条件以在流程中创建条件路径。

### 4.2 服务

我们将简要讨论Activiti提供的服务：

-   RepositoryService帮助我们操作流程定义的部署，该服务处理与流程定义相关的静态数据
-   RuntimeService管理ProcessInstances(当前运行的过程)以及过程变量
-   TaskService跟踪UserTasks，需要用户手动执行的任务是 ActivitiAPI的核心。我们可以使用此服务创建任务、声明并完成任务、操纵任务的受让人等
-   FormService是一项可选服务，API可以在没有它的情况下使用，并且不会牺牲它的任何功能。用于定义流程中的启动表单和任务表单
-   IdentityService管理用户和组
-   HistoryService跟踪Activiti引擎的历史，我们还可以设置不同的历史级别
-   ManagementService与元数据相关，创建应用时通常不需要
-   DynamicBpmnService帮助我们改变流程中的任何东西而无需重新部署它

## 5. 使用Activiti服务

要了解我们如何使用不同的服务并运行一个流程，让我们以“员工休假请求”的流程为例：

<img src="../assets/img_1.png">

该流程的 BPMN 2.0文件VacationRequest.bpmn20.xml将开始事件定义为：

```xml
<startEvent id="startEvent" name="request"
            activiti:initiator="employeeName">
    <extensionElements>
        <activiti:formProperty id="numberOfDays"
                               name="Number of days" type="long" required="true"/>
        <activiti:formProperty id="startDate"
                               name="Vacation start date (MM-dd-yyyy)" type="date"
                               datePattern="MM-dd-yyyy hh:mm" required="true"/>
        <activiti:formProperty id="reason" name="Reason for leave"
                               type="string"/>
    </extensionElements>
</startEvent>
```

同样，分配给用户组“management”的第一个用户任务将如下所示：

```xml
<userTask id="handle_vacation_request" name=
        "Handle Request for Vacation">
    <documentation>${employeeName} would like to take ${numberOfDays} day(s)
        of vacation (Motivation: ${reason}).
    </documentation>
    <extensionElements>
        <activiti:formProperty id="vacationApproved" name="Do you approve
          this vacation request?" type="enum" required="true"/>
        <activiti:formProperty id="comments" name="Comments from Manager"
                               type="string"/>
    </extensionElements>
    <potentialOwner>
        <resourceAssignmentExpression>
            <formalExpression>management</formalExpression>
        </resourceAssignmentExpression>
    </potentialOwner>
</userTask>
```

使用ServiceTask，我们需要定义要执行的代码段。我们将这段代码作为Java类：

```xml
<serviceTask id="send-email-confirmation" name="Send email confirmation"
             activiti:class="com.example.activiti.servicetasks.SendEmailServiceTask.java">
</serviceTask>
```

条件流将通过在“sequenceFlow”中添加“conditionExpression”标签来显示：

```xml
<sequenceFlow id="flow3" name="approved"
              sourceRef="sid-12A577AE-5227-4918-8DE1-DC077D70967C"
              targetRef="send-email-confirmation">
    <conditionExpression xsi:type="tFormalExpression">
        <![CDATA[${vacationApproved == 'true'}]]>
    </conditionExpression>
</sequenceFlow>
```

在这里，vacationApproved是上面显示的UserTask的formProperty。

正如我们在图中看到的，这是一个非常简单的流程。员工提出休假申请，提供休假天数和开始日期，请求转到经理，他们可以批准/不批准该请求。

如果获得批准，则会定义一个服务任务来发送确认电子邮件。如果未获批准，员工可以选择修改并重新发送请求，也可以什么都不做。

服务任务提供了一些要执行的代码(这里是Java类)，我们已经给出了SendEmailServiceTask.java类。

这些类型的类应该扩展JavaDelegate。另外，我们需要重写它的execute()方法，当流程执行到这一步时就会执行。

### 5.1 部署流程

为了让Activiti引擎知道我们的流程，我们需要部署流程。我们可以使用RepositoryService以编程方式完成此操作，让我们编写一个JUnit测试来展示这一点：

```java
@Test 
void givenBPMN_whenDeployProcess_thenDeployed() {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    RepositoryService repositoryService = processEngine.getRepositoryService();
    repositoryService.createDeployment()
        .addClasspathResource("org/activiti/test/vacationRequest.bpmn20.xml")
        .deploy();
    Long count=repositoryService.createProcessDefinitionQuery().count();
    assertEquals("1", count.toString());
}
```

部署意味着引擎将解析BPMN文件并将其转换为可执行文件。此外，每次部署都会在Repository表中添加一条记录。

因此，之后，我们可以查询RepositoryService以获取已部署的流程；ProcessDefinitions。

### 5.2 启动流程实例

将ProcessDefinition部署到Activiti Engine后，我们可以通过创建ProcessInstances来执行流程。ProcessDefinition是蓝图，ProcessInstance是它的运行时执行。

对于单个ProcessDefinition，可以有多个ProcessInstances。

可以通过RuntimeService访问与ProcessInstances相关的所有详细信息。

在我们的示例中，在开始事件中，我们需要传递休假天数、开始日期和原因。我们将使用流程变量，并在创建ProcessInstance时传递它们。

让我们编写一个JUnit测试用例以获得更好的想法：

```java
@Test
void givenDeployedProcess_whenStartProcessInstance_thenRunning() {
    //deploy the process definition    
    Map<String, Object> variables = new HashMap>();
    variables.put("employeeName", "John");
    variables.put("numberOfDays", 4);
    variables.put("vacationMotivation", "I need a break!");
    
    RuntimeService runtimeService = processEngine.getRuntimeService();
    ProcessInstance processInstance = runtimeService
        .startProcessInstanceByKey("vacationRequest", variables);
    Long count=runtimeService.createProcessInstanceQuery().count();
 
    assertEquals("1", count.toString());
}
```

单个流程定义的多个实例将因流程变量而异。

有多种方法可以启动流程实例。在这里，我们使用流程的key。启动流程实例后，我们可以通过查询RuntimeService来获取有关它的信息。

### 5.3 完成任务

当我们的流程实例开始运行时，第一步是一个用户任务，分配给用户组“management”。

用户可能有一个收件箱，其中包含要由他们完成的任务列表。现在，如果我们想继续流程执行，用户需要完成这个任务。对于Activiti Engine来说，这叫做“完成任务”。

我们可以查询TaskService，获取任务对象，然后完成它。

我们需要为此编写的代码如下所示：

```java
@Test 
void givenProcessInstance_whenCompleteTask_thenGotNextTask() {
    // deploy process and start process instance   
    TaskService taskService = processEngine.getTaskService();
    List<Task> tasks = taskService.createTaskQuery()
        .taskCandidateGroup("management").list();
    Task task = tasks.get(0);
    
    Map<String, Object> taskVariables = new HashMap<>();
    taskVariables.put("vacationApproved", "false");
    taskVariables.put("comments", "We have a tight deadline!");
    taskService.complete(task.getId(), taskVariables);

    Task currentTask = taskService.createTaskQuery()
        .taskName("Modify vacation request").singleResult();
    assertNotNull(currentTask);
}
```

请注意，TaskService的complete()方法也接收所需的流程变量，我们把经理的回复传进去。

在此之后，流程引擎将继续下一步。在这里，下一步询问员工是否要重新发送休假请求。

因此，我们的ProcessInstance现在正在等待这个名为“修改休假请求”的UserTask。

### 5.4 挂起和激活流程

我们可以暂停ProcessDefinition和ProcessInstance，如果我们挂起一个ProcessDefinition，我们就不能在它挂起时创建它的实例，我们可以使用RepositoryService来做到这一点：

```java
@Test(expected = ActivitiException.class)
public void givenDeployedProcess_whenSuspend_thenNoProcessInstance() {
    // deploy the process definition
    repositoryService.suspendProcessDefinitionByKey("vacationRequest");
    runtimeService.startProcessInstanceByKey("vacationRequest");
}
```

要再次激活它，我们只需要调用repositoryService.activateProcessDefinitionXXX方法之一。

同样，我们可以使用RuntimeService暂停ProcessInstance。

## 6. 总结

在本文中，我们了解了如何将Activiti与Java结合使用。我们创建了一个示例ProcessEngineConfiguration文件，它帮助我们创建ProcessEngine。

使用它，我们访问了API提供的各种服务。这些服务帮助我们管理和跟踪ProcessDefinitions、ProcessInstances、UserTasks等。