package cn.tuyucheng.taketoday.jackson.enums.deserialization.jsoncreator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnumDeserializationUsingJsonCreatorUnitTest {

	@Test
	public void givenEnumWithJsonCreator_whenDeserializingJson_thenCorrectRepresentation() throws JsonParseException, IOException {
		String json = "{\"distance\": {\"unit\":\"miles\",\"meters\":1609.34}}";

		City city = new ObjectMapper().readValue(json, City.class);
		assertEquals(Distance.MILE, city.getDistance());
	}

}
