package cn.tuyucheng.taketoday.dependson;

import cn.tuyucheng.taketoday.dependson.config.Config;
import cn.tuyucheng.taketoday.dependson.file.processor.FileProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class DriverApplication {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
		ctx.getBean(FileProcessor.class);
		ctx.close();
	}
}
