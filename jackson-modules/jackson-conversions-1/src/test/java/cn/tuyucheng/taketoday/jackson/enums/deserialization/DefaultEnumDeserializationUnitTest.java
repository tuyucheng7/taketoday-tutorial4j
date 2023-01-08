package cn.tuyucheng.taketoday.jackson.enums.deserialization;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultEnumDeserializationUnitTest {

	@Test
	public void givenEnum_whenDeserializingJson_thenCorrectRepresentation() throws IOException {
		String json = "{\"distance\":\"KILOMETER\"}";
		City city = new ObjectMapper().readValue(json, City.class);

		assertEquals(Distance.KILOMETER, city.getDistance());
	}

	@Test
	public void givenEnum_whenDeserializingJsonWithMapperFeature_thenCorrectRepresentation() throws IOException {
		String json = "{\"distance\":\"KiLoMeTeR\"}";
		ObjectMapper objectMapper = JsonMapper.builder()
			.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
			.build();
		City city = objectMapper.readValue(json, City.class);

		assertEquals(Distance.KILOMETER, city.getDistance());
	}
}
