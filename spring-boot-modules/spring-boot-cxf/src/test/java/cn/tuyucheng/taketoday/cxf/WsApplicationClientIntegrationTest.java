package cn.tuyucheng.taketoday.cxf;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.system.OutputCaptureRule;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WsApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class WsApplicationClientIntegrationTest {

	@Rule
	public OutputCaptureRule output = new OutputCaptureRule();

	private final WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

	@LocalServerPort
	private int port;

	@Before
	public void setUp() {
		this.webServiceTemplate.setDefaultUri("http://localhost:" + this.port + "/Service/Hello");
	}

	@Test
	public void whenHelloServiceIsCalled_thenShouldBeCorrect() {
		// final String request = "<q0:sayHello xmlns:q0=\"http://service.cxf.taketoday.tuyucheng.cn\">Elan</q0:sayHello>";
		String request = "<q0:sayHello xmlns:q0=\"http://service.cxf.taketoday.tuyucheng.cn/\"><myname>Elan</myname></q0:sayHello>";

		StreamSource source = new StreamSource(new StringReader(request));
		StreamResult result = new StreamResult(System.out);

		this.webServiceTemplate.sendSourceAndReceiveToResult(source, result);
		assertThat(this.output.toString(),
			containsString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<ns2:sayHelloResponse xmlns:ns2=\"http://service.cxf.taketoday.tuyucheng.cn/\">"
				+ "<return>Hello, Welcome to CXF Spring boot Elan!!!</return>"
				+ "</ns2:sayHelloResponse>"));
	}
}