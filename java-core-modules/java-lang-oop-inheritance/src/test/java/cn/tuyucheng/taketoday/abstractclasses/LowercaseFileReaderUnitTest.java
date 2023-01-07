package cn.tuyucheng.taketoday.abstractclasses;

import cn.tuyucheng.taketoday.abstractclasses.filereaders.BaseFileReader;
import cn.tuyucheng.taketoday.abstractclasses.filereaders.LowercaseFileReader;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LowercaseFileReaderUnitTest {

	@Test
	public void givenLowercaseFileReaderInstance_whenCalledreadFile_thenCorrect() throws Exception {
		Path path = Paths.get(getClass().getClassLoader().getResource("files/test.txt").toURI());
		BaseFileReader lowercaseFileReader = new LowercaseFileReader(path);

		assertThat(lowercaseFileReader.readFile()).isInstanceOf(List.class);
	}
}
