package cn.tuyucheng.taketoday.jackson.map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

public class ClassWithAMap {

	@JsonProperty("map")
	@JsonDeserialize(keyUsing = MyPairDeserializer.class)
	private final Map<MyPair, String> map;

	@JsonCreator
	public ClassWithAMap(Map<MyPair, String> map) {
		this.map = map;
	}

	public Map<MyPair, String> getMap() {
		return map;
	}
}