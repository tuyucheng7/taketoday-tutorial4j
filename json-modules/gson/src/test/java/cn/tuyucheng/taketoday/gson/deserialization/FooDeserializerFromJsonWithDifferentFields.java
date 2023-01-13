package cn.tuyucheng.taketoday.gson.deserialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class FooDeserializerFromJsonWithDifferentFields implements JsonDeserializer<Foo> {

	@Override
	public Foo deserialize(final JsonElement jElement, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
		final JsonObject jObject = jElement.getAsJsonObject();
		final int intValue = jObject.get("valueInt").getAsInt();
		final String stringValue = jObject.get("valueString").getAsString();
		return new Foo(intValue, stringValue);
	}

}
