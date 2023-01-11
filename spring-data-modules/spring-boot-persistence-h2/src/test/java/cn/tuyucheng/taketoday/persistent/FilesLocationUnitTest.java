package cn.tuyucheng.taketoday.persistent;

import cn.tuyucheng.taketoday.h2db.auto.configuration.AutoConfigurationDemo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("persistent-on")
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(classes = AutoConfigurationDemo.class)
class FilesLocationUnitTest {

	@BeforeAll
	static void beforeClass() {

	}

	@Test
	void whenApplicationStarted_thenEmbeddedDbSubfolderCreated() {
		File subdirectory = new File("src/main/resources/db");
		System.out.println(subdirectory.getAbsolutePath());
		assertTrue(subdirectory.exists());
		assertTrue(subdirectory.isDirectory());
	}

	@Test
	void whenApplicationStarted_thenEmbeddedDbFilesCreated() {
		File dbFile = new File("src/main/resources/db/demodb.mv.db");
		System.out.println(dbFile.getAbsolutePath());

		assertTrue(dbFile.exists());
		assertTrue(dbFile.isFile());
	}

	@AfterAll
	static void cleanUp() {
		File dbFile = new File("src/main/resources/db/demodb.mv.db");
		dbFile.deleteOnExit();
	}
}