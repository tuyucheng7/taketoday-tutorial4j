package cn.tuyucheng.taketoday.jackson.date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

public class CustomDateTimeSerializer extends StdSerializer<DateTime> {

	private static final long serialVersionUID = -3927232057990121460L;

	public CustomDateTimeSerializer() {
		this(null);
	}

	public CustomDateTimeSerializer(final Class<DateTime> t) {
		super(t);
	}

	private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

	@Override
	public void serialize(final DateTime value, final JsonGenerator gen, final SerializerProvider arg2) throws IOException, JsonProcessingException {
		gen.writeString(formatter.print(value));
	}
}