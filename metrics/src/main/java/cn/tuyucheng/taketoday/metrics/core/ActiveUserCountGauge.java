package cn.tuyucheng.taketoday.metrics.core;

import com.codahale.metrics.DerivativeGauge;
import com.codahale.metrics.Gauge;

import java.util.List;

public class ActiveUserCountGauge extends DerivativeGauge<List<Long>, Integer> {
	public ActiveUserCountGauge(Gauge<List<Long>> base) {
		super(base);
	}

	@Override
	protected Integer transform(List<Long> value) {
		return value.size();
	}
}
