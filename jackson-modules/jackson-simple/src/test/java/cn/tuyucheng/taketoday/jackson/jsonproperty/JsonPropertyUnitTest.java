package cn.tuyucheng.taketoday.jackson.jsonproperty;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class JsonPropertyUnitTest {

	@Test
	public final void whenSerializing_thenCorrect() throws JsonParseException, IOException {
		final ObjectMapper mapper = new ObjectMapper();
		final String dtoAsString = mapper.writeValueAsString(new MyDto());

		assertThat(dtoAsString, containsString("intValue"));
		assertThat(dtoAsString, containsString("stringValue"));
		assertThat(dtoAsString, containsString("booleanValue"));
	}

	@Test
	public final void givenNameOfFieldIsChangedViaAnnotationOnGetter_whenSerializing_thenCorrect() throws JsonParseException, IOException {
		final ObjectMapper mapper = new ObjectMapper();
		final MyDtoFieldNameChanged dtoObject = new MyDtoFieldNameChanged();
		dtoObject.setStringValue("a");

		final String dtoAsString = mapper.writeValueAsString(dtoObject);

		assertThat(dtoAsString, not(containsString("stringValue")));
		assertThat(dtoAsString, containsString("strVal"));
		System.out.println(dtoAsString);
	}

}
