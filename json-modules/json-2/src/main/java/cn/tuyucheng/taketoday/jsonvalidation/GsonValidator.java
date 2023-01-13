package cn.tuyucheng.taketoday.jsonvalidation;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;

import java.io.IOException;

public class GsonValidator {

	final TypeAdapter<JsonElement> strictAdapter = new Gson().getAdapter(JsonElement.class);

	public boolean isValid(String json) {
		try {
			JsonParser.parseString(json);
		} catch (JsonSyntaxException e) {
			return false;
		}
		return true;
	}

	public boolean isValidStrict(String json) {
		try {
			strictAdapter.fromJson(json);
		} catch (JsonSyntaxException | IOException e) {
			return false;
		}
		return true;
	}
}
