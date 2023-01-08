package cn.tuyucheng.taketoday.serialization;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.Test;

import java.io.IOException;

public class CustomSerializationUnitTest {

	@Test
	public final void whenSerializing_thenNoExceptions() throws JsonGenerationException, JsonMappingException, IOException {
		final Item myItem = new Item(1, "theItem", new User(2, "theUser"));
		final String serialized = new ObjectMapper().writeValueAsString(myItem);
	}

	@Test
	public final void whenSerializingWithCustomSerializer_thenNoExceptions() throws JsonGenerationException, JsonMappingException, IOException {
		final Item myItem = new Item(1, "theItem", new User(2, "theUser"));

		final ObjectMapper mapper = new ObjectMapper();

		final SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(Item.class, new ItemSerializer());
		mapper.registerModule(simpleModule);

		final String serialized = mapper.writeValueAsString(myItem);
	}

	@Test
	public final void givenSerializerRegisteredOnClass_whenSerializingWithCustomSerializer_thenNoExceptions() throws JsonGenerationException, JsonMappingException, IOException {
		final ItemWithSerializer myItem = new ItemWithSerializer(1, "theItem", new User(2, "theUser"));

		final String serialized = new ObjectMapper().writeValueAsString(myItem);
	}

}
