package cn.tuyucheng.taketoday.system;

public class EnvironmentVariables {
	public String getPath() {
		return System.getenv("PATH");
	}
}
