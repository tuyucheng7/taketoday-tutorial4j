package cn.tuyucheng.taketoday.defaultserializercustomserializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class FolderSerializerWithSerializerProvider extends StdSerializer<Folder> {

	public FolderSerializerWithSerializerProvider() {
		super(Folder.class);
	}

	@Override
	public void serialize(Folder value, JsonGenerator gen, SerializerProvider provider) throws IOException {

		gen.writeStartObject();
		gen.writeStringField("name", value.getName());

		// we delegate the File list serialization to its default serializer
		provider.defaultSerializeField("files", value.getFiles(), gen);

		gen.writeEndObject();

	}

}