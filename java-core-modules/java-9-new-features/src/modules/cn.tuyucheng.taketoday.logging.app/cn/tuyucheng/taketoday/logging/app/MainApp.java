package cn.tuyucheng.taketoday.logging.app;

import java.lang.System.Logger.Level;

public class MainApp {

	private static final System.Logger LOGGER = System.getLogger("MainApp");

	public static void main(String[] args) {
		LOGGER.log(Level.ERROR, "error test");
		LOGGER.log(Level.INFO, "info test");
	}
}