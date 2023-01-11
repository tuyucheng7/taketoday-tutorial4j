package cn.tuyucheng.taketoday.zoneddatetime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import cn.tuyucheng.taketoday.zoneddatetime.config.MongoConfig;
import cn.tuyucheng.taketoday.zoneddatetime.model.Action;
import cn.tuyucheng.taketoday.zoneddatetime.repository.ActionRepository;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This test requires:
 * * mongodb instance running on the environment
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MongoConfig.class)
class ActionRepositoryLiveTest {

	@Autowired
	private MongoOperations mongoOps;

	@Autowired
	private ActionRepository actionRepository;

	@BeforeEach
	void setup() {
		if (!mongoOps.collectionExists(Action.class)) {
			mongoOps.createCollection(Action.class);
		}
	}

	@Test
	void givenSavedAction_TimeIsRetrievedCorrectly() {
		String id = "testId";
		ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);

		actionRepository.save(new Action(id, "click-action", now));
		Action savedAction = actionRepository.findById(id).get();

		assertEquals(now.withNano(0), savedAction.getTime().withNano(0));
	}

	@AfterEach
	void tearDown() {
		mongoOps.dropCollection(Action.class);
	}
}