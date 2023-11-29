package cn.tuyucheng.taketoday.springcloudtaskfinal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;
import org.springframework.cloud.deployer.spi.task.TaskLauncher;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.task.launcher.TaskLaunchRequest;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringCloudTaskSinkApplication.class)
class SpringCloudTaskSinkApplicationIntegrationTest {

   @Autowired
   ApplicationContext context;

   @Autowired
   private Sink sink;

   @Test
   void testTaskLaunch() {
      TaskLauncher taskLauncher = context.getBean(TaskLauncher.class);

      Map<String, String> prop = new HashMap<>();
      prop.put("server.port", "0");
      TaskLaunchRequest request = new TaskLaunchRequest("maven://org.springframework.cloud.task.app:" + "timestamp-task:jar:1.0.1.RELEASE", null,
            prop, null, null);
      GenericMessage<TaskLaunchRequest> message = new GenericMessage<>(request);
      this.sink.input().send(message);

      ArgumentCaptor<AppDeploymentRequest> deploymentRequest = ArgumentCaptor.forClass(AppDeploymentRequest.class);

      verify(taskLauncher).launch(deploymentRequest.capture());

      AppDeploymentRequest actualRequest = deploymentRequest.getValue();

      // Verifying the co-ordinate of launched Task here.
      assertTrue(actualRequest.getCommandlineArguments().isEmpty());
      assertEquals("0", actualRequest.getDefinition().getProperties().get("server.port"));
      assertTrue(actualRequest
            .getResource()
            .toString()
            .contains("org.springframework.cloud.task.app:timestamp-task:jar:1.0.1.RELEASE"));
   }
}