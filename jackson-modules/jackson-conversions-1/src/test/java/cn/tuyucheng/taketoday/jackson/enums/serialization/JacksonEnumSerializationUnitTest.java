package cn.tuyucheng.taketoday.jackson.enums.serialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class JacksonEnumSerializationUnitTest {

	@Test
	public final void givenEnum_whenSerializingJson_thenCorrectRepresentation() throws JsonParseException, IOException {
		final String dtoAsString = new ObjectMapper().writeValueAsString(Distance.MILE);

		assertThat(dtoAsString, containsString("1609.34"));
	}

}
