package cn.tuyucheng.taketoday.javaxval.afterdeserialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.io.InputStream;

public class StudentDeserializerWithValidation {

	public static Student readStudent(InputStream inputStream) throws IOException {
		ObjectMapper mapper = getObjectMapperWithValidation();
		return mapper.readValue(inputStream, Student.class);
	}

	private static ObjectMapper getObjectMapperWithValidation() {
		SimpleModule validationModule = new SimpleModule();
		validationModule.setDeserializerModifier(new BeanDeserializerModifierWithValidation());
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(validationModule);
		return mapper;
	}

}
