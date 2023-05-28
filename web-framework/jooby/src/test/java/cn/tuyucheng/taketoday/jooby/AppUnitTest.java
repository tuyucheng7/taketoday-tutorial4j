package cn.tuyucheng.taketoday.jooby;

import io.jooby.MockRouter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AppUnitTest {

   @Test
   public void given_defaultUrl_with_mockrouter_expect_fixedString() {
      MockRouter router = new MockRouter(new App());
      assertEquals("Hello World!", router.get("/")
            .value());
   }
}