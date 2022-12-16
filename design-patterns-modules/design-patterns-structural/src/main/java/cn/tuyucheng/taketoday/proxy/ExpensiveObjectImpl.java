package cn.tuyucheng.taketoday.proxy;

import static cn.tuyucheng.taketoday.util.LoggerUtil.LOG;

public class ExpensiveObjectImpl implements ExpensiveObject {

	public ExpensiveObjectImpl() {
		heavyInitialConfiguration();
	}

	@Override
	public void process() {
		LOG.info("processing complete.");
	}

	private void heavyInitialConfiguration() {
		LOG.info("Loading initial configuration..");
	}
}