package cn.tuyucheng.taketoday.persistence;

import cn.tuyucheng.taketoday.persistence.hibernate.FooPaginationPersistenceIntegrationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import cn.tuyucheng.taketoday.persistence.audit.AuditTestSuite;
import cn.tuyucheng.taketoday.persistence.hibernate.FooSortingPersistenceIntegrationTest;
import cn.tuyucheng.taketoday.persistence.service.FooServiceBasicPersistenceIntegrationTest;
import cn.tuyucheng.taketoday.persistence.service.FooServicePersistenceIntegrationTest;
import cn.tuyucheng.taketoday.persistence.service.ParentServicePersistenceIntegrationTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AuditTestSuite.class
        , FooServiceBasicPersistenceIntegrationTest.class
        , FooPaginationPersistenceIntegrationTest.class
        , FooServicePersistenceIntegrationTest.class
        , ParentServicePersistenceIntegrationTest.class
        , FooSortingPersistenceIntegrationTest.class
})
public class IntegrationTestSuite {
    //
}