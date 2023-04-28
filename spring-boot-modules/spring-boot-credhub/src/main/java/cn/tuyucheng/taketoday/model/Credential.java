package cn.tuyucheng.taketoday.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Credential {
	Map<String, Object> value;
	String type;
	String name;
}
