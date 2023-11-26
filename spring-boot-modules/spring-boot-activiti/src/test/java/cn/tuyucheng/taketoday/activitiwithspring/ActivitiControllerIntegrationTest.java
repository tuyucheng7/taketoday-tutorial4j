package cn.tuyucheng.taketoday.activitiwithspring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@SpringBootTest
class ActivitiControllerIntegrationTest {
   private static final Logger logger = LoggerFactory.getLogger(ActivitiControllerIntegrationTest.class);
   private MockMvc mockMvc;

   @Autowired
   private WebApplicationContext wac;

   @Autowired
   RuntimeService runtimeService;

   @BeforeEach
   void setUp() {
      this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
            .build();

      for (ProcessInstance instance : runtimeService.createProcessInstanceQuery()
            .list()) {
         runtimeService.deleteProcessInstance(instance.getId(), "Reset Processes");
      }
   }

   @Test
   void givenProcess_whenStartProcess_thenIncreaseInProcessInstanceCount() throws Exception {
      String responseBody = this.mockMvc.perform(MockMvcRequestBuilders.get("/start-process"))
            .andReturn()
            .getResponse()
            .getContentAsString();
      assertEquals("Process started. Number of currently running process instances = 1", responseBody);

      responseBody = this.mockMvc.perform(MockMvcRequestBuilders.get("/start-process"))
            .andReturn()
            .getResponse()
            .getContentAsString();
      assertEquals("Process started. Number of currently running process instances = 2", responseBody);

      responseBody = this.mockMvc.perform(MockMvcRequestBuilders.get("/start-process"))
            .andReturn()
            .getResponse()
            .getContentAsString();
      assertEquals("Process started. Number of currently running process instances = 3", responseBody);
   }

   @Test
   void givenProcess_whenProcessInstance_thenReceivedRunningTask() throws Exception {
      this.mockMvc.perform(MockMvcRequestBuilders.get("/start-process"))
            .andReturn()
            .getResponse();
      ProcessInstance pi = runtimeService.createProcessInstanceQuery()
            .orderByProcessInstanceId()
            .desc()
            .list()
            .get(0);

      logger.info(STR."process instance = \{pi.getId()}");
      String responseBody = this.mockMvc.perform(MockMvcRequestBuilders.get(STR."/get-tasks/\{pi.getId()}"))
            .andReturn()
            .getResponse()
            .getContentAsString();

      ObjectMapper mapper = new ObjectMapper();
      List<TaskRepresentation> tasks = Arrays.asList(mapper.readValue(responseBody, TaskRepresentation[].class));
      assertEquals(1, tasks.size());
      assertEquals("A", tasks.get(0).getName());
   }

   @Test
   void givenProcess_whenCompleteTaskA_thenReceivedNextTask() throws Exception {
      this.mockMvc.perform(MockMvcRequestBuilders.get("/start-process"))
            .andReturn()
            .getResponse();
      ProcessInstance pi = runtimeService.createProcessInstanceQuery()
            .orderByProcessInstanceId()
            .desc()
            .list()
            .get(0);

      logger.info(STR."process instance = \{pi.getId()}");
      this.mockMvc.perform(MockMvcRequestBuilders.get(STR."/complete-task-A/\{pi.getId()}"))
            .andReturn()
            .getResponse()
            .getContentAsString();

      List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
      assertEquals(0, list.size());
   }
}