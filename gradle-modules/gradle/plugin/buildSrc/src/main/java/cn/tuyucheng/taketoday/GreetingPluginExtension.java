package cn.tuyucheng.taketoday;

public class GreetingPluginExtension {
	private String greeter = "Tuyucheng";
	private String message = "Message from Plugin!";

	public String getGreeter() {
		return greeter;
	}

	public void setGreeter(String greeter) {
		this.greeter = greeter;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}