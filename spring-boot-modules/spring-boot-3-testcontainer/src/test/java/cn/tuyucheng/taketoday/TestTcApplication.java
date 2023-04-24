package cn.tuyucheng.taketoday;

import org.springframework.boot.SpringApplication;

public class TestTcApplication {

	public static void main(String[] args) {
		System.out.println("running inside the test `main` method.");
		SpringApplication.from(TcApplication::main).run(args);
	}
}