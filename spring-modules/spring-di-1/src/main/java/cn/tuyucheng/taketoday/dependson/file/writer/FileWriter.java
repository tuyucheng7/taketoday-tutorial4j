package cn.tuyucheng.taketoday.dependson.file.writer;

import cn.tuyucheng.taketoday.dependson.shared.File;


public class FileWriter {

	public FileWriter(File file) {
		file.setText("write");
	}

	public void writeFile() {
	}
}
