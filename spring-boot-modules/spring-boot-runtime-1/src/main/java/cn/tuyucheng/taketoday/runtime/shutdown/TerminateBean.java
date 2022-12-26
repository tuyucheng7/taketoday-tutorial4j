package cn.tuyucheng.taketoday.runtime.shutdown;

import javax.annotation.PreDestroy;

public class TerminateBean {

	@PreDestroy
	public void onDestroy() throws Exception {
		System.out.println("Spring Container is destroyed!");
	}
}