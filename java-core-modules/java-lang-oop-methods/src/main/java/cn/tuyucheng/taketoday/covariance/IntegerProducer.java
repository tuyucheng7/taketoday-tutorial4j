package cn.tuyucheng.taketoday.covariance;

public class IntegerProducer extends Producer {
	@Override
	public Integer produce(String input) {
		return Integer.parseInt(input);
	}
}
