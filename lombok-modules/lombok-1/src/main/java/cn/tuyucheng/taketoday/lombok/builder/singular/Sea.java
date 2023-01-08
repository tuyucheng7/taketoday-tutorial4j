package cn.tuyucheng.taketoday.lombok.builder.singular;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Getter
@Builder
public class Sea {

	@Singular
	private final List<String> grasses;
	@Singular("oneFish")
	private final List<String> fish;
}
