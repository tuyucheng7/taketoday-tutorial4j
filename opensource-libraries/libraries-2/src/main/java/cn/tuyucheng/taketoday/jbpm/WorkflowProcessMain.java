package cn.tuyucheng.taketoday.jbpm;

import cn.tuyucheng.taketoday.jbpm.engine.WorkflowEngine;
import cn.tuyucheng.taketoday.jbpm.engine.WorkflowEngineImpl;
import org.kie.api.runtime.manager.Context;
import org.kie.internal.runtime.manager.context.EmptyContext;

public class WorkflowProcessMain {

	public static void main(String[] args) {
		WorkflowEngine workflowEngine = new WorkflowEngineImpl();
		String processId = "cn.tuyucheng.taketoday.bpmn.helloworld";
		String kbaseId = "kbase";
		String persistenceUnit = "org.jbpm.persistence.jpa";
		Context<String> initialContext = EmptyContext.get();
		workflowEngine.runjBPMEngineForProcess(processId, initialContext, kbaseId, persistenceUnit);
	}
}