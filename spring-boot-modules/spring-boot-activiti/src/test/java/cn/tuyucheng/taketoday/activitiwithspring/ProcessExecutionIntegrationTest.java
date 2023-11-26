package cn.tuyucheng.taketoday.activitiwithspring;

import org.activiti.engine.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProcessExecutionIntegrationTest {

   @Test
   void givenBPMN_whenDeployProcess_thenDeployed() {
      ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
      RepositoryService repositoryService = processEngine.getRepositoryService();
      repositoryService.createDeployment()
            .addClasspathResource("org/activiti/test/vacationRequest.bpmn20.xml")
            .deploy();
      Long count = repositoryService.createProcessDefinitionQuery().count();
      assertTrue(count >= 1);
   }

   @Test
   void givenProcessDefinition_whenStartProcessInstance_thenProcessRunning() {
      ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
      RepositoryService repositoryService = processEngine.getRepositoryService();
      repositoryService.createDeployment()
            .addClasspathResource("org/activiti/test/vacationRequest.bpmn20.xml")
            .deploy();

      Map<String, Object> variables = new HashMap<String, Object>();
      variables.put("employeeName", "Kermit");
      variables.put("numberOfDays", new Integer(4));
      variables.put("vacationMotivation", "I'm really tired!");

      RuntimeService runtimeService = processEngine.getRuntimeService();
      ProcessInstance processInstance = runtimeService
            .startProcessInstanceByKey("vacationRequest", variables);

      Long count = runtimeService.createProcessInstanceQuery().count();
      assertTrue(count >= 1);
   }

   @Test
   void givenProcessInstance_whenCompleteTask_thenProcessExecutionContinues() {
      ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
      RepositoryService repositoryService = processEngine.getRepositoryService();
      repositoryService.createDeployment()
            .addClasspathResource("org/activiti/test/vacationRequest.bpmn20.xml")
            .deploy();

      Map<String, Object> variables = new HashMap<String, Object>();
      variables.put("employeeName", "Kermit");
      variables.put("numberOfDays", new Integer(4));
      variables.put("vacationMotivation", "I'm really tired!");

      RuntimeService runtimeService = processEngine.getRuntimeService();
      ProcessInstance processInstance = runtimeService
            .startProcessInstanceByKey("vacationRequest", variables);

      TaskService taskService = processEngine.getTaskService();
      List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("management").list();

      Task task = tasks.get(0);

      Map<String, Object> taskVariables = new HashMap<String, Object>();
      taskVariables.put("vacationApproved", "false");
      taskVariables.put("comments", "We have a tight deadline!");
      taskService.complete(task.getId(), taskVariables);

      Task currentTask = taskService.createTaskQuery().taskName("Modify vacation request").singleResult();
      assertNotNull(currentTask);
   }

   @Test
   void givenProcessDefinition_whenSuspend_thenNoProcessInstanceCreated() {
      ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
      RepositoryService repositoryService = processEngine.getRepositoryService();
      repositoryService.createDeployment()
            .addClasspathResource("org/activiti/test/vacationRequest.bpmn20.xml")
            .deploy();

      RuntimeService runtimeService = processEngine.getRuntimeService();
      repositoryService.suspendProcessDefinitionByKey("vacationRequest");
      assertThrows(ActivitiException.class, () -> runtimeService.startProcessInstanceByKey("vacationRequest"));
   }
}