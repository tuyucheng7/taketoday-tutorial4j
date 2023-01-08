package cn.tuyucheng.taketoday.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class ClassGetResourceExample {

	private static Logger logger = LoggerFactory.getLogger(ClassGetResourceExample.class);

	public static void main(String[] args) {
		URL resourceAbsolutePath = ClassGetResourceExample.class.getResource("/cn/tuyucheng/taketoday/resource/example.txt");
		logger.info("Resource with absolute path = {}", resourceAbsolutePath);

		URL resourceRelativePath = ClassGetResourceExample.class.getResource("example.txt");
		logger.info("Resource with relative path = {}", resourceRelativePath);
	}
}