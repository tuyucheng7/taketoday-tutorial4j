package cn.tuyucheng.taketoday.anonymousclass;

public class EmailSenderService implements SenderService {

	@Override
	public String callSender(Sender sender) {
		return sender.send("Email Notification");
	}
}