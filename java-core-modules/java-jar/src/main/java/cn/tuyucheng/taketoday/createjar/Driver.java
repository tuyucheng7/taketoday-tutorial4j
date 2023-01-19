package cn.tuyucheng.taketoday.createjar;

import java.io.IOException;
import java.util.jar.JarOutputStream;

public class Driver {

	public static void main(String[] args) throws IOException {
		JarTool tool = new JarTool();
		tool.startManifest();
		tool.addToManifest("Main-Class", "cn.tuyucheng.taketoday.createjar.HelloWorld");
		JarOutputStream target = tool.openJar("HelloWorld.jar");

		tool.addFile(target, System.getProperty("user.dir") + "\\src\\main\\java", System.getProperty("user.dir") + "\\src\\main\\java\\cn\\tuyucheng\\taketoday\\createjar\\HelloWorld.class");
		target.close();
	}
}