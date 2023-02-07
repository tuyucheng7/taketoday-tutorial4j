package cn.tuyucheng.taketoday.spring.cloud.archaius.dynamosources;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * To run this Live Test we need to:
 * <br/>
 * start a dynamodb instance locally on port 8000(e.g. with the following command `docker run -p 8000:8000 --name bael-dynamodb amazon/dynamodb-local`)
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DynamoSourcesApplication.class)
class SpringContextLiveTest {

	@Test
	void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}