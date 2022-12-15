package cn.tuyucheng.taketoday.command.command;

import cn.tuyucheng.taketoday.command.receiver.TextFile;

public class SaveTextFileOperation implements TextFileOperation {

	private final TextFile textFile;

	public SaveTextFileOperation(TextFile textFile) {
		this.textFile = textFile;
	}

	@Override
	public String execute() {
		return textFile.save();
	}
}