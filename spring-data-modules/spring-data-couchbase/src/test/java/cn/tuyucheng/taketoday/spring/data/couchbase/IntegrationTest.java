package cn.tuyucheng.taketoday.spring.data.couchbase;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MyCouchbaseConfig.class, IntegrationTestConfig.class})
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class})
abstract class IntegrationTest {
}