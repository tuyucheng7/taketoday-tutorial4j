package cn.tuyucheng.taketoday.unused;

import com.google.common.collect.ImmutableList;
import org.apache.http.ssl.SSLContextBuilder;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class UnusedDependencies {

	public static void main(String[] args) {
		System.out.println("Hello world");
		useGuava();
		useHttpCore();
		useHttpClientWithReflection();
	}

	private static void useGuava() {
		List<String> list = ImmutableList.of("Tuyucheng", "is", "cool");
		System.out.println(list.stream()
			.collect(Collectors.joining(" ")));
	}

	private static void useHttpCore() {
		SSLContextBuilder.create();
	}

	private static void useHttpClientWithReflection() {
		try {
			Class<?> httpBuilder = Class.forName("org.apache.http.impl.client.HttpClientBuilder");
			Method create = httpBuilder.getMethod("create", null);
			create.invoke(httpBuilder, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}