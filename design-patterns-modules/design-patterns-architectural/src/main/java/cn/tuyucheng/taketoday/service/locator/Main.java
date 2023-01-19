package cn.tuyucheng.taketoday.service.locator;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;

@ExcludeFromJacocoGeneratedReport
public class Main {

	public static void main(String[] args) {

		MessagingService service = ServiceLocator.getService("EmailService");
		String email = service.getMessageBody();
		System.out.println(email);

		service = ServiceLocator.getService("SMSService");
		String sms = service.getMessageBody();
		System.out.println(sms);

		service = ServiceLocator.getService("EmailService");
		String newEmail = service.getMessageBody();
		System.out.println(newEmail);
	}
}