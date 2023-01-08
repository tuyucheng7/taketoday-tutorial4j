package cn.tuyucheng.taketoday.jackson.ignorenullfields;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class IgnoreNullFieldsUnitTest {

	@Test
	public final void givenNullsIgnoredOnClass_whenWritingObjectWithNullField_thenIgnored() throws JsonProcessingException {
		final ObjectMapper mapper = new ObjectMapper();
		final MyDtoIgnoreNull dtoObject = new MyDtoIgnoreNull();

		final String dtoAsString = mapper.writeValueAsString(dtoObject);

		assertThat(dtoAsString, containsString("intValue"));
		assertThat(dtoAsString, containsString("booleanValue"));
		assertThat(dtoAsString, not(containsString("stringValue")));
		System.out.println(dtoAsString);
	}

	@Test
	public final void givenNullsIgnoredGlobally_whenWritingObjectWithNullField_thenIgnored() throws JsonProcessingException {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		final MyDto dtoObject = new MyDto();

		final String dtoAsString = mapper.writeValueAsString(dtoObject);

		assertThat(dtoAsString, containsString("intValue"));
		assertThat(dtoAsString, containsString("booleanValue"));
		assertThat(dtoAsString, not(containsString("stringValue")));
		System.out.println(dtoAsString);
	}

}
