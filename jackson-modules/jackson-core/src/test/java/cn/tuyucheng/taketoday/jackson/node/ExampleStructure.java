package cn.tuyucheng.taketoday.jackson.node;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class ExampleStructure {
	private static ObjectMapper mapper = new ObjectMapper();

	static JsonNode getExampleRoot() throws IOException {
		InputStream exampleInput = ExampleStructure.class.getClassLoader()
			.getResourceAsStream("node_example.json");
		JsonNode rootNode = mapper.readTree(exampleInput);
		return rootNode;
	}
}