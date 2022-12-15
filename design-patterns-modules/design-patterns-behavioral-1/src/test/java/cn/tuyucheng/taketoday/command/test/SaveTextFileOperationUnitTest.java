package cn.tuyucheng.taketoday.command.test;

import cn.tuyucheng.taketoday.command.command.SaveTextFileOperation;
import cn.tuyucheng.taketoday.command.command.TextFileOperation;
import cn.tuyucheng.taketoday.command.receiver.TextFile;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SaveTextFileOperationUnitTest {

	@Test
	void givenSaveTextFileOperationIntsance_whenCalledExecuteMethod_thenOneAssertion() {
		TextFileOperation openTextFileOperation = new SaveTextFileOperation(new TextFile("file1.txt"));
		assertThat(openTextFileOperation.execute()).isEqualTo("Saving file file1.txt");
	}
}