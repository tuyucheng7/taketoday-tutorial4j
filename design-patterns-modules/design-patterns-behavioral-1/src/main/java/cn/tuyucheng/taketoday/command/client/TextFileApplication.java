package cn.tuyucheng.taketoday.command.client;

import cn.tuyucheng.taketoday.command.command.OpenTextFileOperation;
import cn.tuyucheng.taketoday.command.command.SaveTextFileOperation;
import cn.tuyucheng.taketoday.command.command.TextFileOperation;
import cn.tuyucheng.taketoday.command.invoker.TextFileOperationExecutor;
import cn.tuyucheng.taketoday.command.receiver.TextFile;

public class TextFileApplication {

	public static void main(String[] args) {
		TextFileOperation openTextFileOperation = new OpenTextFileOperation(new TextFile("file1.txt"));
		TextFileOperation saveTextFileOperation = new SaveTextFileOperation(new TextFile("file2.txt"));
		TextFileOperationExecutor textFileOperationExecutor = new TextFileOperationExecutor();

		System.out.println(textFileOperationExecutor.executeOperation(openTextFileOperation));
		System.out.println(textFileOperationExecutor.executeOperation(saveTextFileOperation));
	}
}