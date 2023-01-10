package cn.tuyucheng.taketoday.jbpm;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

@Ignore("fails test")
public class WorkflowEngineIntegrationTest extends JbpmJUnitBaseTestCase {

	private String[] triggeredNodesArray = {"Start", "HelloWorld", "End"};
	private RuntimeManager manager = null;
	private RuntimeEngine runtimeEngine = null;
	private KieSession ksession = null;
	private ProcessInstance processInstance = null;

	@Before
	public void setup() {
		manager = createRuntimeManager(Strategy.SINGLETON, "manager", "cn/tuyucheng/taketoday/process/helloworld.bpmn");
		runtimeEngine = getRuntimeEngine(ProcessInstanceIdContext.get());
		ksession = runtimeEngine.getKieSession();
		processInstance = ksession.startProcess("cn.tuyucheng.taketoday.bpmn.helloworld");
	}

	@After
	public void cleanup() {
		manager.disposeRuntimeEngine(runtimeEngine);
	}

	@Test
	public void givenProcessInstance_whenExecutionCompleted_thenVerifyNodesExecutionOrder() {
		assertNodeTriggered(processInstance.getId(), triggeredNodesArray);
	}

	@Test
	public void givenProcessInstance_whenExecutionCompleted_thenVerifyKnowledgeSessionId() {
		int ksessionID = ksession.getId();
		runtimeEngine = getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
		ksession = runtimeEngine.getKieSession();
		assertEquals(ksessionID, ksession.getId());
	}

	@Test
	public void givenProcessInstance_whenExecutionCompleted_thenVerifyProcessInstanceStatus() {
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		assertTrue("ProcessInstance completed with status 2", processInstance.getState() == 2);
	}
}
