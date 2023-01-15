## 1. 概述

这个快速教程演示我们**如何使用WireMock测试基于HTTP的有状态API**。如果你对该库不熟悉，请先查看我们的[WireMock简介](WireMock简介.md)教程。

## 2. Maven依赖

为了能够使用[WireMock库](https://search.maven.org/search?q=a:wiremock)，我们需要在POM中包含以下依赖项：

```xml
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock</artifactId>
    <version>2.21.0</version>
    <scope>test</scope>
</dependency>
```

## 3. 我们要Mock的示例API

**Wiremock中的场景概念是为了帮助模拟REST API的不同状态**。这使我们能够创建测试，在这些测试中，我们使用的API的行为会根据其所处的状态而有所不同。

为了说明这一点，我们演示一个实际示例：“Java Tip”服务，每当我们请求它的/java-tip端点时，它都会为我们提供关于Java的不同tip。

如果我们需要响应，我们会以text/plain形式返回：

```
"use composition rather than inheritance"
```

如果我们再次调用它，我们会得到不同的tip。

## 4. 创建场景状态

我们需要让WireMock为“/java-tip”端点创建stub，每个stub都将返回一个特定的文本，该文本对应于模拟API的3种状态之一：

```java
public class WireMockScenarioExampleIntegrationTest {

	private static final String THIRD_STATE = "third";
	private static final String SECOND_STATE = "second";
	private static final String TIP_01 = "finally block is not called when System.exit() is called in the try block";
	private static final String TIP_02 = "keep your code clean";
	private static final String TIP_03 = "use composition rather than inheritance";
	private static final String TEXT_PLAIN = "text/plain";

	static int port = 9999;

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(port);

    @Test
    public void changeStateOnEachCallTest() throws IOException {
        createWireMockStub(Scenario.STARTED, SECOND_STATE, TIP_01);
        createWireMockStub(SECOND_STATE, THIRD_STATE, TIP_02);
        createWireMockStub(THIRD_STATE, Scenario.STARTED, TIP_03);
    }

	private void createWireMockStub(String currentState, String nextState, String responseBody) {
		stubFor(get(urlEqualTo("/java-tip"))
				.inScenario("java tips")
				.whenScenarioStateIs(currentState)
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", TEXT_PLAIN)
						.withBody(responseBody))
				.willSetStateTo(nextState)
		);
	}
}
```

在上面的类中，我们使用了WireMock的JUnit Rule类WireMockRule。这会在运行JUnit测试时设置WireMock服务器。**然后我们使用WireMock的stubFor方法来创建我们之后会使用的stub**。

创建stub时使用的关键方法是：

-   whenScenarioStateIs：定义场景需要处于哪个状态，WireMock才能使用这个stub
-   willSetStateTo：给出WireMock在使用此stub后将状态设置为的值

任何场景的初始状态都是Scenario.STARTED。因此，我们创建了一个stub，当状态为Scenario.STARTED时使用它，这会将状态移动到SECOND_STATE。

我们还添加了stub以从SECOND_STATE移动到THIRD_STATE，最后从THIRD_STATE回到Scenario.STARTED。因此，如果我们继续调用/java-tip端点，状态会发生如下变化：

**Scenario.STARTED -> SECOND_STATE -> THIRD_STATE -> Scenario.STARTED**

## 5. 使用场景

为了使用WireMock场景，我们只需重复调用/java-tip端点。所以我们需要修改我们的测试类，如下所示：

```java
@Test
public void changeStateOnEachCallTest() throws IOException {
	createWireMockStub(Scenario.STARTED, SECOND_STATE, TIP_01);
	createWireMockStub(SECOND_STATE, THIRD_STATE, TIP_02);
	createWireMockStub(THIRD_STATE, Scenario.STARTED, TIP_03);
	assertEquals(TIP_01, nextTip());
	assertEquals(TIP_02, nextTip());
	assertEquals(TIP_03, nextTip());
	assertEquals(TIP_01, nextTip());
}

private String nextTip() throws ClientProtocolException, IOException {
	CloseableHttpClient httpClient = HttpClients.createDefault();
	HttpGet request = new HttpGet(String.format("http://localhost:%s/java-tip", port));
	HttpResponse httpResponse = httpClient.execute(request);
	return firstLineOfResponse(httpResponse);
}
private static String firstLineOfResponse(HttpResponse httpResponse) throws IOException {
	try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()))) {
		return reader.readLine();
	}
}
```

nextTip()方法调用/java-tip端点，然后将响应作为字符串返回。因此我们在每个assertEquals()调用中使用它来检查调用是否确实让场景在不同的状态之间循环。

## 6. 总结

在本文中，我们了解了如何使用WireMock场景来mock API，该API根据其所处的状态更改其响应。