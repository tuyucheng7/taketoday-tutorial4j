package cn.tuyucheng.taketoday.bootique;

import cn.tuyucheng.taketoday.bootique.service.HelloService;
import io.bootique.BQRuntime;
import io.bootique.test.junit.BQDaemonTestFactory;
import io.bootique.test.junit.BQTestFactory;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AppUnitTest {

   @Rule
   public BQTestFactory bqTestFactory = new BQTestFactory();

   @Rule
   public BQDaemonTestFactory bqDaemonTestFactory = new BQDaemonTestFactory();

   @Test
   public void givenService_expectBoolean() {
      BQRuntime runtime = bqTestFactory.app("--server").autoLoadModules().createRuntime();
      HelloService service = runtime.getInstance(HelloService.class);
      assertTrue(service.save());
   }
}