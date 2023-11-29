package cn.tuyucheng.taketoday.serenity.spring;

import cn.tuyucheng.taketoday.serenity.spring.steps.AdderServiceSteps;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author tuyucheng
 */
@RunWith(SerenityRunner.class)
public class AdderServiceIntegrationTest {

   @Steps
   private AdderServiceSteps adderServiceSteps;

   @Test
   public void givenNumber_whenAdd_thenSummedUp() {
      adderServiceSteps.givenBaseAndAdder(RandomNumberUtil.randomInt(), RandomNumberUtil.randomInt());
      adderServiceSteps.whenAdd();
      adderServiceSteps.summedUp();
   }

}
