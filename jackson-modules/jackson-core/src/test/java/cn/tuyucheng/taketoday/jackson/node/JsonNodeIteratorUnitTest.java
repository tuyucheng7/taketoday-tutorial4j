package cn.tuyucheng.taketoday.jackson.node;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class JsonNodeIteratorUnitTest {

	private JsonNodeIterator onTest = new JsonNodeIterator();
	private static String expectedYaml = "name: \n" +
		"  first: Tatu\n" +
		"  last: Saloranta\n" +
		"title: Jackson founder\n" +
		"company: FasterXML\n" +
		"pets: \n" +
		"- type: dog\n" +
		"  number: 1\n" +
		"- type: fish\n" +
		"  number: 50";

	@Test
	public void givenANodeTree_whenIteratingSubNodes_thenWeFindExpected() throws IOException {
		final JsonNode rootNode = ExampleStructure.getExampleRoot();

		String yaml = onTest.toYaml(rootNode);
		System.out.println(yaml.toString());

		assertEquals(expectedYaml, yaml);

	}


}
