package cn.tuyucheng.taketoday.rxjava.jdbc;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rx.Observable;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsertBlobIntegrationTest {

	private ConnectionProvider connectionProvider = Connector.connectionProvider;
	private Database db = Database.from(connectionProvider);

	private String expectedDocument = null;
	private String actualDocument = null;

	private Observable<Integer> create, insert = null;

	@BeforeEach
	void setup() throws IOException {
		create = db.update("CREATE TABLE IF NOT EXISTS SERVERLOG (id int primary key, document BLOB)")
			.count();

		InputStream actualInputStream = new FileInputStream("src/test/resources/actual_clob");
		this.actualDocument = Utils.getStringFromInputStream(actualInputStream);
		byte[] bytes = this.actualDocument.getBytes(StandardCharsets.UTF_8);

		InputStream expectedInputStream = new FileInputStream("src/test/resources/expected_clob");
		this.expectedDocument = Utils.getStringFromInputStream(expectedInputStream);
		this.insert = db.update("insert into SERVERLOG(id,document) values(?,?)")
			.parameter(1)
			.parameter(Database.toSentinelIfNull(bytes))
			.dependsOn(create)
			.count();
	}

	@Test
	void whenInsertBLOB_thenCorrect() throws IOException {
		db.select("select document from SERVERLOG where id = 1")
			.dependsOn(create)
			.dependsOn(insert)
			.getAs(String.class)
			.toList()
			.toBlocking()
			.single();
		assertEquals(expectedDocument, actualDocument);
	}

	@AfterEach
	void close() {
		db.update("DROP TABLE SERVERLOG")
			.dependsOn(create);
		connectionProvider.close();
	}
}