package cn.tuyucheng.taketoday.serenity.spring;

import cn.tuyucheng.taketoday.serenity.spring.steps.AdderRestSteps;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author tuyucheng
 */
@RunWith(SerenityRunner.class)
public class AdderMockMvcIntegrationTest {

	@Before
	public void init() {
		RestAssuredMockMvc.standaloneSetup(new PlainAdderController());
	}

	@Steps
	AdderRestSteps steps;

	@Test
	public void givenNumber_whenAdd_thenSummedUp() throws Exception {
		steps.givenCurrentNumber();
		steps.whenAddNumber(RandomNumberUtil.randomInt());
		steps.thenSummedUp();
	}

}
