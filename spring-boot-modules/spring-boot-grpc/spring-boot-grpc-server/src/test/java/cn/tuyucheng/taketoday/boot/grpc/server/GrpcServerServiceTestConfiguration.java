package cn.tuyucheng.taketoday.boot.grpc.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcServerServiceTestConfiguration {

	@Bean
	public GrpcServerService grpcServerService() {
		return new GrpcServerService();
	}
}