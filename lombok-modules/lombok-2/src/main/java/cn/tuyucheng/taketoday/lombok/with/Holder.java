package cn.tuyucheng.taketoday.lombok.with;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;

@Getter
@AllArgsConstructor
public class Holder {
	@With
	private String variableA;
	@With
	private String _variableB;
	@With
	private String $variableC;
}