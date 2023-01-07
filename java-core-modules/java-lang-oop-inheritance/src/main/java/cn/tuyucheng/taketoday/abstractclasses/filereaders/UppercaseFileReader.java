package cn.tuyucheng.taketoday.abstractclasses.filereaders;

import java.nio.file.Path;

public class UppercaseFileReader extends BaseFileReader {

	public UppercaseFileReader(Path filePath) {
		super(filePath);
	}

	@Override
	public String mapFileLine(String line) {
		return line.toUpperCase();
	}
}
