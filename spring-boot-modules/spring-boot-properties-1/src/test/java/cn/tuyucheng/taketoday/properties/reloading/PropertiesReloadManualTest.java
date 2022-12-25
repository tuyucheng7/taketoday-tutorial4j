package cn.tuyucheng.taketoday.properties.reloading;

import cn.tuyucheng.taketoday.properties.reloading.beans.ConfigurationPropertiesRefreshConfigBean;
import cn.tuyucheng.taketoday.properties.reloading.beans.EnvironmentConfigBean;
import cn.tuyucheng.taketoday.properties.reloading.beans.PropertiesConfigBean;
import cn.tuyucheng.taketoday.properties.reloading.beans.ValueRefreshConfigBean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringBootPropertiesTestApplication.class)
class PropertiesReloadManualTest {

	protected MockMvc mvc;

	protected long refreshDelay = 3000;

	@Autowired
	WebApplicationContext webApplicationContext;

	@Autowired
	ValueRefreshConfigBean valueRefreshConfigBean;

	@Autowired
	ConfigurationPropertiesRefreshConfigBean configurationPropertiesRefreshConfigBean;

	@Autowired
	EnvironmentConfigBean environmentConfigBean;

	@Autowired
	PropertiesConfigBean propertiesConfigBean;

	@Autowired
	@Qualifier("singletonValueRefreshConfigBean")
	ValueRefreshConfigBean singletonValueRefreshConfigBean;


	@BeforeEach
	void setUp() throws Exception {
		mvc = MockMvcBuilders
			.webAppContextSetup(webApplicationContext)
			.build();
		createConfig("extra.properties", "application.theme.color", "blue");
		createConfig("extra2.properties", "application.theme.background", "red");
		Thread.sleep(refreshDelay);
		callRefresh();
	}

	@AfterEach
	void tearDown() throws Exception {
		createConfig("extra.properties", "application.theme.color", "blue");
		createConfig("extra2.properties", "application.theme.background", "red");
	}

	@Test
	void givenEnvironmentReader_whenColorChanged_thenExpectChangeValue() throws Exception {
		assertEquals("blue", environmentConfigBean.getColor());

		createConfig("extra.properties", "application.theme.color", "red");
		Thread.sleep(refreshDelay);

		assertEquals("red", environmentConfigBean.getColor());
	}

	@Test
	void givenEnvironmentReader_whenBackgroundChanged_thenExpectChangeValue() throws Exception {
		assertEquals("red", environmentConfigBean.getBackgroundColor());

		createConfig("extra2.properties", "application.theme.background", "blue");
		Thread.sleep(refreshDelay);

		assertEquals("blue", environmentConfigBean.getBackgroundColor());
	}

	@Test
	void givenPropertiesReader_whenColorChanged_thenExpectChangeValue() throws Exception {
		assertEquals("blue", propertiesConfigBean.getColor());

		createConfig("extra.properties", "application.theme.color", "red");
		Thread.sleep(refreshDelay);

		assertEquals("red", propertiesConfigBean.getColor());
	}

	@Test
	void givenRefreshScopedValueReader_whenColorChangedAndRefreshCalled_thenExpectChangeValue() throws Exception {
		assertEquals("blue", valueRefreshConfigBean.getColor());

		createConfig("extra.properties", "application.theme.color", "red");
		Thread.sleep(refreshDelay);

		assertEquals("blue", valueRefreshConfigBean.getColor());

		callRefresh();

		assertEquals("red", valueRefreshConfigBean.getColor());
	}

	@Test
	void givenSingletonRefreshScopedValueReader_whenColorChangedAndRefreshCalled_thenExpectOldValue() throws Exception {
		assertEquals("blue", singletonValueRefreshConfigBean.getColor());

		createConfig("extra.properties", "application.theme.color", "red");
		Thread.sleep(refreshDelay);

		assertEquals("blue", singletonValueRefreshConfigBean.getColor());

		callRefresh();

		assertEquals("blue", singletonValueRefreshConfigBean.getColor());
	}

	@Test
	void givenRefreshScopedConfigurationPropertiesReader_whenColorChangedAndRefreshCalled_thenExpectChangeValue() throws Exception {
		assertEquals("blue", configurationPropertiesRefreshConfigBean.getColor());

		createConfig("extra.properties", "application.theme.color", "red");
		Thread.sleep(refreshDelay);

		assertEquals("blue", configurationPropertiesRefreshConfigBean.getColor());

		callRefresh();

		assertEquals("red", configurationPropertiesRefreshConfigBean.getColor());
	}

	void callRefresh() throws Exception {
		MvcResult mvcResult = mvc
			.perform(MockMvcRequestBuilders
				.post("/actuator/refresh")
				.accept(MediaType.APPLICATION_JSON_VALUE))
			.andReturn();
		MockHttpServletResponse response = mvcResult.getResponse();
		assertEquals(200, response.getStatus());
	}

	void createConfig(String file, String key, String value) throws Exception {
		FileOutputStream fo = new FileOutputStream(file);
		fo.write(String
			.format("%s=%s", key, value)
			.getBytes());
		fo.close();
	}
}