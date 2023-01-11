package cn.tuyucheng.taketoday.persistence.audit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        EnversFooBarAuditIntegrationTest.class,
        JPABarAuditIntegrationTest.class,
        SpringDataJPABarAuditIntegrationTest.class
})
public class AuditTestSuite {
    //
}