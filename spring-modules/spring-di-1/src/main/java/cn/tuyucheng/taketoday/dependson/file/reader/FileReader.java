package cn.tuyucheng.taketoday.dependson.file.reader;

import cn.tuyucheng.taketoday.dependson.shared.File;

public class FileReader {

	public FileReader(File file) {
		file.setText("read");
	}

	public void readFile() {
	}
}
