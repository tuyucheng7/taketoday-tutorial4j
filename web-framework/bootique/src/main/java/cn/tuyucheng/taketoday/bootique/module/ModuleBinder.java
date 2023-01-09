package cn.tuyucheng.taketoday.bootique.module;

import cn.tuyucheng.taketoday.bootique.service.HelloService;
import cn.tuyucheng.taketoday.bootique.service.impl.HelloServiceImpl;
import com.google.inject.Binder;
import com.google.inject.Module;

public class ModuleBinder implements Module {

	@Override
	public void configure(Binder binder) {
		binder.bind(HelloService.class).to(HelloServiceImpl.class);
	}

}
