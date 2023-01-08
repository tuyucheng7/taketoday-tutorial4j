package cn.tuyucheng.taketoday.jackson.mapnull;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class MyDtoNullKeySerializer extends StdSerializer<Object> {

	private static final long serialVersionUID = -4478531309177369056L;

	public MyDtoNullKeySerializer() {
		this(null);
	}

	public MyDtoNullKeySerializer(final Class<Object> t) {
		super(t);
	}

	@Override
	public void serialize(final Object value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeFieldName("");
	}

}
