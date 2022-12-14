package cn.tuyucheng.taketoday.reduceIfelse;

import cn.tuyucheng.taketoday.reducingifelse.Expression;
import cn.tuyucheng.taketoday.reducingifelse.Operator;
import cn.tuyucheng.taketoday.reducingifelse.Result;
import cn.tuyucheng.taketoday.reducingifelse.RuleEngine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RuleEngineUnitTest {

	@Test
	void whenNumbersGivenToRuleEngine_thenReturnCorrectResult() {
		Expression expression = new Expression(5, 5, Operator.ADD);
		RuleEngine engine = new RuleEngine();
		Result result = engine.process(expression);

		assertNotNull(result);
		assertEquals(10, result.getValue());
	}
}