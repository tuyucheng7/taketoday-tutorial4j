package cn.tuyucheng.taketoday.jackson.enums.deserialization.jsonproperty;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnumDeserializationUsingJsonPropertyUnitTest {

	@Test
	public void givenEnumWithJsonProperty_whenDeserializingJson_thenCorrectRepresentation() throws JsonParseException, IOException {
		String json = "{\"distance\": \"distance-in-km\"}";

		City city = new ObjectMapper().readValue(json, City.class);
		assertEquals(Distance.KILOMETER, city.getDistance());

	}

}
