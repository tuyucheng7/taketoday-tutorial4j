package cn.tuyucheng.taketoday.java.io.zip4j;

import net.lingala.zip4j.ZipFile;

import java.io.IOException;

public class ExtractAllFile {

	public static void main(String[] args) throws IOException {
		ZipFile zipFile = new ZipFile("compressed.zip", "password".toCharArray());
		zipFile.extractAll("/destination_directory");
		zipFile.close();
	}
}
