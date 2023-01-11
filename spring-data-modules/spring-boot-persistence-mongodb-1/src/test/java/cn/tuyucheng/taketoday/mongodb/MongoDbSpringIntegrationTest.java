package cn.tuyucheng.taketoday.mongodb;

import cn.tuyucheng.taketoday.SpringBootPersistenceApplication;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = SpringBootPersistenceApplication.class)
@DataMongoTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
class MongoDbSpringIntegrationTest {

	@DisplayName("Given object When save object using MongoDB template Then object can be found")
	@Test
	void test(@Autowired MongoTemplate mongoTemplate) {
		// given
		DBObject objectToSave = BasicDBObjectBuilder.start()
				.add("key", "value")
				.get();

		// when
		mongoTemplate.save(objectToSave, "collection");

		// then
		assertThat(mongoTemplate.findAll(DBObject.class, "collection"))
				.extracting("key").containsOnly("value");
	}
}