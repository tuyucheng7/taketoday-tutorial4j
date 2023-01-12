package cn.tuyucheng.taketoday.dependson.config;

import cn.tuyucheng.taketoday.dependson.file.processor.FileProcessor;
import cn.tuyucheng.taketoday.dependson.file.reader.FileReader;
import cn.tuyucheng.taketoday.dependson.file.writer.FileWriter;
import cn.tuyucheng.taketoday.dependson.shared.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;

@Configuration
@ComponentScan("cn.tuyucheng.taketoday.dependson")
public class TestConfig {

	@Autowired
	File file;

	@Bean("fileProcessor")
	@DependsOn({"fileReader", "fileWriter"})
	@Lazy
	public FileProcessor fileProcessor() {
		return new FileProcessor(file);
	}

	@Bean("fileReader")
	public FileReader fileReader() {
		return new FileReader(file);
	}

	@Bean("fileWriter")
	public FileWriter fileWriter() {
		return new FileWriter(file);
	}

	@Bean("dummyFileProcessor")
	@DependsOn({"dummyfileWriter"})
	@Lazy
	public FileProcessor dummyFileProcessor() {
		return new FileProcessor(file);
	}

	@Bean("dummyFileProcessorCircular")
	@DependsOn({"dummyFileReaderCircular"})
	@Lazy
	public FileProcessor dummyFileProcessorCircular() {
		return new FileProcessor(file);
	}

	@Bean("dummyFileReaderCircular")
	@DependsOn({"dummyFileProcessorCircular"})
	@Lazy
	public FileReader dummyFileReaderCircular() {
		return new FileReader(file);
	}
}
