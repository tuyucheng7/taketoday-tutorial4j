package cn.tuyucheng.taketoday.jackson.enums.deserialization.customdeserializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnumCustomDeserializationUnitTest {

	@Test
	public void givenEnumWithCustomDeserializer_whenDeserializingJson_thenCorrectRepresentation() throws JsonParseException, IOException {
		String json = "{\"distance\": {\"unit\":\"miles\",\"meters\":1609.34}}";

		City city = new ObjectMapper().readValue(json, City.class);
		assertEquals(Distance.MILE, city.getDistance());
	}
}
