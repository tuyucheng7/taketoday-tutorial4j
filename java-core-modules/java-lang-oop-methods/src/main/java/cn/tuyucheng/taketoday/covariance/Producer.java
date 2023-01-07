package cn.tuyucheng.taketoday.covariance;

public class Producer {
	public Object produce(String input) {
		Object result = input.toLowerCase();
		return result;
	}
}
