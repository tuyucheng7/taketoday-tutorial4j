package cn.tuyucheng.taketoday.boot.grpc.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LocalGrpcServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocalGrpcServerApplication.class, args);
	}
}