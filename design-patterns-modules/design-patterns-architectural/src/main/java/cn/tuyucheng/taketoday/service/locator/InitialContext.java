package cn.tuyucheng.taketoday.service.locator;

public class InitialContext {

	public Object lookup(String serviceName) {

		if (serviceName.equalsIgnoreCase("EmailService")) {
			return new EmailService();
		} else if (serviceName.equalsIgnoreCase("SMSService")) {
			return new SMSService();
		}
		return null;
	}
}