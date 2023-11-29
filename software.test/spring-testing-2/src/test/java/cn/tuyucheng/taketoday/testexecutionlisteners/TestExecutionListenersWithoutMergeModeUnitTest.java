package cn.tuyucheng.taketoday.testexecutionlisteners;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@TestExecutionListeners(value = {CustomTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
@ContextConfiguration(classes = AdditionService.class)
class TestExecutionListenersWithoutMergeModeUnitTest {

   @Autowired
   private AdditionService additionService;

   @Test
   void whenValidNumbersPassed_thenReturnSum() {
      assertThat(additionService.add(5, 13), Matchers.is(18));
   }
}