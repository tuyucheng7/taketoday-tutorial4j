package cn.tuyucheng.taketoday.persistence;

import cn.tuyucheng.taketoday.persistence.service.FooServicePersistenceIntegrationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
      FooServicePersistenceIntegrationTest.class
})
public class PersistenceTestSuite {

}