package cn.tuyucheng.taketoday.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class SleepUtils {

	@SneakyThrows
	public static void loadingSimulator(int sec) {
		LOGGER.info("Sleeping for {} sec.", sec);
		String bar = " ".repeat(sec);
		for (int i = 0; i < sec; i++) {
			bar = bar.replaceFirst(" ", "=");
			LOGGER.info("[" + bar + "]");
			TimeUnit.SECONDS.sleep(1);
		}
	}
}