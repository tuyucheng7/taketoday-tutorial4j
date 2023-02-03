package cn.tuyucheng.taketoday.dynamicproxy;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class DynamicProxyIntegrationTest {

	@Test
	void givenDynamicProxy_thenPutWorks() {
		Map proxyInstance = (Map) Proxy.newProxyInstance(DynamicProxyIntegrationTest.class.getClassLoader(), new Class[]{Map.class}, new DynamicInvocationHandler());

		proxyInstance.put("hello", "world");
	}

	@Test
	void givenInlineDynamicProxy_thenGetWorksOtherMethodsDoNot() {
		Map proxyInstance = (Map) Proxy.newProxyInstance(DynamicProxyIntegrationTest.class.getClassLoader(), new Class[]{Map.class}, (proxy, method, methodArgs) -> {

			if (method.getName().equals("get")) {
				return 42;
			} else {
				throw new UnsupportedOperationException("Unsupported method: " + method.getName());
			}
		});

		int result = (int) proxyInstance.get("hello");

		assertEquals(42, result);

		try {
			proxyInstance.put("hello", "world");
			fail();
		} catch (UnsupportedOperationException e) {
			// expected
		}
	}

	@Test
	void givenTimingDynamicProxy_thenMethodInvokationsProduceTiming() {
		Map mapProxyInstance = (Map) Proxy.newProxyInstance(DynamicProxyIntegrationTest.class.getClassLoader(), new Class[]{Map.class}, new TimingDynamicInvocationHandler(new HashMap<>()));

		mapProxyInstance.put("hello", "world");
		assertEquals("world", mapProxyInstance.get("hello"));

		CharSequence csProxyInstance = (CharSequence) Proxy.newProxyInstance(DynamicProxyIntegrationTest.class.getClassLoader(), new Class[]{CharSequence.class}, new TimingDynamicInvocationHandler("Hello World"));

		assertEquals('l', csProxyInstance.charAt(2));
		assertEquals(11, csProxyInstance.length());
	}
}