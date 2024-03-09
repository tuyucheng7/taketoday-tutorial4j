package cn.tuyucheng.taketoday.persistence.service;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
      FooPaginationPersistenceIntegrationTest.class
      , FooServicePersistenceIntegrationTest.class
      , FooServiceSortingIntegrationTest.class
      , FooServiceSortingWitNullsManualIntegrationTest.class
})
public class PersistenceTestSuite {
   //
}