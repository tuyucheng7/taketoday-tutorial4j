package cn.tuyucheng.taketoday.lombok.builder;

import lombok.Value;

@Value
final class ImmutableClient {

	private int id;
	private String name;

}
