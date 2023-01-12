package cn.tuyucheng.taketoday.hystrix;

import com.netflix.hystrix.HystrixCommand;


class RemoteServiceTestCommand extends HystrixCommand<String> {

	private final RemoteServiceTestSimulator remoteService;

	RemoteServiceTestCommand(Setter config, RemoteServiceTestSimulator remoteService) {
		super(config);
		this.remoteService = remoteService;
	}

	@Override
	protected String run() throws Exception {
		return remoteService.execute();
	}
}
