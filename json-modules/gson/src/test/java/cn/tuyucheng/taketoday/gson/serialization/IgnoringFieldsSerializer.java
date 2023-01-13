package cn.tuyucheng.taketoday.gson.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class IgnoringFieldsSerializer implements JsonSerializer<SourceClass> {
	@Override
	public JsonElement serialize(final SourceClass src, final Type typeOfSrc, final JsonSerializationContext context) {
		final String intValue = "intValue";
		final JsonObject jObject = new JsonObject();
		jObject.addProperty(intValue, src.getIntValue());

		return jObject;
	}
}
