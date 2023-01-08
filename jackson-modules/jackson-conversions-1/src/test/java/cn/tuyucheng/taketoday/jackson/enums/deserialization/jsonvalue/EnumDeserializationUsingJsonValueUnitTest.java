package cn.tuyucheng.taketoday.jackson.enums.deserialization.jsonvalue;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnumDeserializationUsingJsonValueUnitTest {

	@Test
	public void givenEnumWithJsonValue_whenDeserializingJson_thenCorrectRepresentation() throws JsonParseException, IOException {
		String json = "{\"distance\": \"0.0254\"}";

		City city = new ObjectMapper().readValue(json, City.class);
		assertEquals(Distance.INCH, city.getDistance());
	}

}
