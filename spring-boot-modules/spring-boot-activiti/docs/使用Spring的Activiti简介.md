## 1. 概述

简单地说，Activiti 是一个工作流和业务流程管理平台。

我们可以通过创建ProcessEngineConfiguration(通常基于配置文件)快速开始。由此，我们可以获得一个ProcessEngine——通过ProcessEngine，我们可以执行工作流和BPM操作。

API 提供各种可用于访问和管理进程的服务。这些服务可以为我们提供有关进程历史、当前正在运行的进程以及已部署但尚未运行的进程的信息。

服务还可以用来定义流程结构和操纵流程的状态，即运行、暂停、取消等。

如果你是该 API 的新手，请查看我们[的 Activiti API 与Java简介](https://www.baeldung.com/java-activiti)。在本文中，我们将讨论如何在Spring Boot应用程序中设置 Activiti API。

## 2. 使用Spring Boot进行设置

让我们看看如何将 Activiti 设置为Spring BootMaven 应用程序并开始使用它。

### 2.1. 最初设定

像往常一样，我们需要添加 maven 依赖：

```xml
<dependency>
    <groupId>org.activiti</groupId>
    <artifactId>activiti-spring-boot-starter-basic</artifactId>
</dependency>
```

可以在[此处](https://mvnrepository.com/artifact/org.activiti/activiti-engine)找到 API 的最新稳定版本。它与Spring Boot一起工作到 v1.5.4。它不适用于 v2.0.0.M1。

我们还可以使用[https://start.spring.io](https://start.spring.io/)生成一个Spring Boot项目，并选择 Activiti 作为依赖项。

只需将此依赖项和@EnableAutoConfiguration注解添加到Spring Boot应用程序，它就会进行初始设置：

-   创建数据源(API 需要一个数据库来创建ProcessEngine)
-   创建并公开ProcessEngine bean
-   创建并公开 Activiti 服务 beans
-   创建 Spring 作业执行器

### 2.2. 创建和运行进程

让我们构建一个创建和运行业务流程的示例。要定义一个流程，我们需要创建一个 BPMN 文件。

然后，只需下载 BPMN 文件。我们需要将此文件放在src/main/resources/processes文件夹中。默认情况下，Spring Boot 将在此文件夹中查找以部署流程定义。

我们将创建一个包含一个用户任务的演示流程：

[![开始事件结束事件](https://www.baeldung.com/wp-content/uploads/2017/08/image-9-300x111.jpg)](https://www.baeldung.com/wp-content/uploads/2017/08/image-9.jpg)

用户任务的受让人被设置为流程的发起人。此流程定义的 BPMN 文件如下所示：

```xml
 <process id="my-process" name="say-hello-process" isExecutable="true">
     <startEvent id="startEvent" name="startEvent">
     </startEvent>
     <sequenceFlow id="sequence-flow-1" sourceRef="startEvent" targetRef="A">
     </sequenceFlow>     
     <userTask id="A" name="A" activiti:assignee="$INITIATOR">
     </userTask>
     <sequenceFlow id="sequence-flow-2" sourceRef="A" targetRef="endEvent">
     </sequenceFlow>
     <endEvent id="endEvent" name="endEvent">
     </endEvent>
</process>
```

现在，我们将创建一个 REST 控制器来处理启动此过程的请求：

```java
@Autowired
private RuntimeService runtimeService;

@GetMapping("/start-process")
public String startProcess() {
  
    runtimeService.startProcessInstanceByKey("my-process");
    return "Process started. Number of currently running"
      + "process instances = "
      + runtimeService.createProcessInstanceQuery().count();
}
```

这里，runtimeService.startProcessInstanceByKey(“my-process”)开始执行key为“my-process”的进程。runtimeService.createProcessInstanceQuery().count()将为我们获取流程实例的数量。

每次我们点击路径“/start-process” ，都会创建一个新的ProcessInstance ，我们会看到当前正在运行的进程的数量增加。

JUnit 测试用例向我们展示了这种行为：

```java
@Test
public void givenProcess_whenStartProcess_thenIncreaseInProcessInstanceCount() 
  throws Exception {
 
    String responseBody = this.mockMvc
      .perform(MockMvcRequestBuilders.get("/start-process"))
      .andReturn().getResponse().getContentAsString();
 
    assertEquals("Process started. Number of currently running"
      + " process instances = 1", responseBody);
        
    responseBody = this.mockMvc
      .perform(MockMvcRequestBuilders.get("/start-process"))
      .andReturn().getResponse().getContentAsString();
 
    assertEquals("Process started. Number of currently running"
      + " process instances = 2", responseBody);
        
    responseBody = this.mockMvc
      .perform(MockMvcRequestBuilders.get("/start-process"))
      .andReturn().getResponse().getContentAsString();
 
    assertEquals("Process started. Number of currently running"
      + " process instances = 3", responseBody);
}

```

## 3. 玩转流程

现在我们已经使用Spring Boot在 Activiti 中有了一个正在运行的进程，让我们扩展上面的示例来演示我们如何访问和操作该进程。

### 3.1. 获取给定ProcessInstance的任务列表

我们有两个用户任务A和B。当我们启动一个进程时，它会等待第一个任务A完成，然后执行任务B。让我们创建一个处理程序方法，它接受请求以查看与给定processInstance相关的任务。

像Task这样的对象不能直接作为响应发送，因此我们需要创建一个自定义对象并将Task转换为我们的自定义对象。我们称这个类为 TaskRepresentation：

```java
class TaskRepresentation {
    private String id;
    private String name;
    private String processInstanceId;

    // standard constructors
}
```

处理程序方法将如下所示：

```java
@GetMapping("/get-tasks/{processInstanceId}")
public List<TaskRepresentation> getTasks(
  @PathVariable String processInstanceId) {
 
    List<Task> usertasks = taskService.createTaskQuery()
      .processInstanceId(processInstanceId)
      .list();

    return usertasks.stream()
      .map(task -> new TaskRepresentation(
        task.getId(), task.getName(), task.getProcessInstanceId()))
      .collect(Collectors.toList());
}

```

在这里，taskService.createTaskQuery().processInstanceId(processInstanceId).list()使用TaskService并为我们获取与给定processInstanceId相关的任务列表。我们可以看到，当我们开始运行我们创建的进程时，我们将通过向我们刚刚定义的方法发出请求来获取任务A ：

```java
@Test
public void givenProcess_whenProcessInstance_thenReceivedRunningTask() 
  throws Exception {
 
    this.mockMvc.perform(MockMvcRequestBuilders.get("/start-process"))
      .andReturn()
      .getResponse();
    ProcessInstance pi = runtimeService.createProcessInstanceQuery()
      .orderByProcessInstanceId()
      .desc()
      .list()
      .get(0);
    String responseBody = this.mockMvc
      .perform(MockMvcRequestBuilders.get("/get-tasks/" + pi.getId()))
      .andReturn()
      .getResponse()
      .getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    List<TaskRepresentation> tasks = Arrays.asList(mapper
      .readValue(responseBody, TaskRepresentation[].class));
 
    assertEquals(1, tasks.size());
    assertEquals("A", tasks.get(0).getName());
}
```

### 3.2. 完成任务

现在，我们将看看当我们完成任务A时会发生什么。我们创建一个处理程序方法，该方法将处理为给定的processInstance完成任务A的请求：

```java
@GetMapping("/complete-task-A/{processInstanceId}")
public void completeTaskA(@PathVariable String processInstanceId) {
    Task task = taskService.createTaskQuery()
      .processInstanceId(processInstanceId)
      .singleResult();
    taskService.complete(task.getId());
}
```

taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult()在任务服务上创建一个查询，并为我们提供给定processInstance的任务。这是UserTask A。下一行taskService.complete(task.getId)完成这个任务。
因此，现在流程已经结束并且RuntimeService不包含任何ProcessInstances。我们可以使用 JUnit 测试用例看到这一点：

```java
@Test
public void givenProcess_whenCompleteTaskA_thenNoProcessInstance() 
  throws Exception {

    this.mockMvc.perform(MockMvcRequestBuilders.get("/start-process"))
      .andReturn()
      .getResponse();
    ProcessInstance pi = runtimeService.createProcessInstanceQuery()
      .orderByProcessInstanceId()
      .desc()
      .list()
      .get(0);
    this.mockMvc.perform(MockMvcRequestBuilders.get("/complete-task-A/" + pi.getId()))
      .andReturn()
      .getResponse()
      .getContentAsString();
    List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
    assertEquals(0, list.size());
}
```

这就是我们如何使用 Activiti 服务与流程一起工作。

## 4. 总结

在本文中，我们概述了将 Activiti API 与Spring Boot结合使用。有关 API 的更多信息，请参阅[用户指南](https://www.activiti.org/userguide/)。我们还看到了如何使用 Activiti 服务创建流程并对其执行各种操作。

Spring Boot 使其易于使用，因为我们无需担心创建数据库、部署流程或创建ProcessEngine。

请记住，Activiti 与Spring Boot的集成仍处于试验阶段，Spring Boot 2 尚不支持。