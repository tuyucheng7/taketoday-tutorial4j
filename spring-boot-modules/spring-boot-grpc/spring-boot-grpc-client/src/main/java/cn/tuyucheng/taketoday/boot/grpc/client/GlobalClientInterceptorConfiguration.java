package cn.tuyucheng.taketoday.boot.grpc.client;

import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Order
@Configuration(proxyBeanMethods = false)
public class GlobalClientInterceptorConfiguration {

	@GrpcGlobalClientInterceptor
	LogGrpcInterceptor logClientInterceptor() {
		return new LogGrpcInterceptor();
	}
}