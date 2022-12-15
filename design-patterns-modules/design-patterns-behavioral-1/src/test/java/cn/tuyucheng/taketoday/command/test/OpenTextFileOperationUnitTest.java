package cn.tuyucheng.taketoday.command.test;

import cn.tuyucheng.taketoday.command.command.OpenTextFileOperation;
import cn.tuyucheng.taketoday.command.command.TextFileOperation;
import cn.tuyucheng.taketoday.command.receiver.TextFile;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenTextFileOperationUnitTest {

	@Test
	void givenOpenTextFileOperationInstance_whenCalledExecuteMethod_thenOneAssertion() {
		TextFileOperation openTextFileOperation = new OpenTextFileOperation(new TextFile("file1.txt"));
		assertThat(openTextFileOperation.execute()).isEqualTo("Opening file file1.txt");
	}
}