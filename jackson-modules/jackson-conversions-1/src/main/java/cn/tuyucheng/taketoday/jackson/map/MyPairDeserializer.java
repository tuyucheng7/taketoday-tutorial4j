package cn.tuyucheng.taketoday.jackson.map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import java.io.IOException;

public class MyPairDeserializer extends KeyDeserializer {

	@Override
	public MyPair deserializeKey(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException {

		return new MyPair(key);
	}
}