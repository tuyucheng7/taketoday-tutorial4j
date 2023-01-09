package cn.tuyucheng.taketoday.guice;

import cn.tuyucheng.taketoday.guice.config.DependencyModule;
import cn.tuyucheng.taketoday.guice.service.DataPumpService;
import cn.tuyucheng.taketoday.guice.service.ServiceFactory;

import ratpack.guice.Guice;
import ratpack.server.RatpackServer;

public class Application {

	public static void main(String[] args) throws Exception {

		RatpackServer
			.start(server -> server.registry(Guice.registry(bindings -> bindings.module(DependencyModule.class)))
				.handlers(chain -> chain.get("randomString", ctx -> {
					DataPumpService dataPumpService = ctx.get(DataPumpService.class);
					ctx.render(dataPumpService.generate());
				}).get("factory", ctx -> ctx.render(ServiceFactory.getInstance().generate()))));

//		RatpackServer.start(server -> server
//				.registry(Guice
//						.registry(bindings -> bindings.bindInstance(DataPumpService.class, new DataPumpServiceImpl())))
//				.handlers(chain -> chain.get("randomString", ctx -> {
//					DataPumpService dataPumpService = ctx.get(DataPumpService.class);
//					ctx.render(dataPumpService.generate());
//				}).get("factory", ctx -> ctx.render(ServiceFactory.getInstance().generate()))));

	}

}