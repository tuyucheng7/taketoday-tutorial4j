package cn.tuyucheng.taketoday.reactive.urlmatch;

import cn.tuyucheng.taketoday.reactive.Spring5ReactiveApplication;
import cn.tuyucheng.taketoday.reactive.controller.PathPatternController;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Spring5ReactiveApplication.class)
@WithMockUser
public class PathPatternsUsingHandlerMethodIntegrationTest {

	private static WebTestClient client;

	@BeforeClass
	public static void setUp() {
		client = WebTestClient.bindToController(new PathPatternController())
			.build();
	}

	@Test
	public void givenHandlerMethod_whenMultipleURIVariablePattern_then200() {

		client.get()
			.uri("/spring5/tuyucheng/tutorial")
			.exchange()
			.expectStatus()
			.is2xxSuccessful()
			.expectBody()
			.equals("/tuyucheng/tutorial");

		client.get()
			.uri("/spring5/tuyucheng")
			.exchange()
			.expectStatus()
			.is2xxSuccessful()
			.expectBody()
			.equals("/tuyucheng");
	}

	@Test
	public void givenHandlerMethod_whenURLWithWildcardTakingZeroOrMoreChar_then200() {
		client.get()
			.uri("/spring5/userid")
			.exchange()
			.expectStatus()
			.is2xxSuccessful()
			.expectBody()
			.equals("/spring5/*id");
	}

	@Test
	public void givenHandlerMethod_whenURLWithWildcardTakingExactlyOneChar_then200() {
		client.get()
			.uri("/string5")
			.exchange()
			.expectStatus()
			.is2xxSuccessful()
			.expectBody()
			.equals("/s?ring5");
	}

	@Test
	public void givenHandlerMethod_whenURLWithWildcardTakingZeroOrMorePathSegments_then200() {
		client.get()
			.uri("/resources/tuyucheng")
			.exchange()
			.expectStatus()
			.is2xxSuccessful()
			.expectBody()
			.equals("/resources/**");
	}

	@Test
	public void givenHandlerMethod_whenURLWithRegexInPathVariable_thenExpectedOutput() {
		client.get()
			.uri("/abc")
			.exchange()
			.expectStatus()
			.is2xxSuccessful()
			.expectBody()
			.equals("abc");

		client.get()
			.uri("/123")
			.exchange()
			.expectStatus()
			.is4xxClientError();
	}

	@Test
	public void givenHandlerMethod_whenURLWithMultiplePathVariablesInSameSegment_then200() {
		client.get()
			.uri("/tuyucheng_tutorial")
			.exchange()
			.expectStatus()
			.is2xxSuccessful()
			.expectBody()
			.equals("Two variables are var1=tuyucheng and var2=tutorial");
	}
}