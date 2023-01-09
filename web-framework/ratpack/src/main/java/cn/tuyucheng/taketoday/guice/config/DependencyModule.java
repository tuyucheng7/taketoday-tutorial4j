package cn.tuyucheng.taketoday.guice.config;

import cn.tuyucheng.taketoday.guice.service.DataPumpService;
import cn.tuyucheng.taketoday.guice.service.impl.DataPumpServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class DependencyModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(DataPumpService.class).to(DataPumpServiceImpl.class)
			.in(Scopes.SINGLETON);
	}

}
