package cn.tuyucheng.taketoday.fullstack.restassured.cucumber.context;

import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.Endpoints;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.config.ConfigFileReader;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.enums.Context;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.scenario.ScenarioContext;

public class TestContext {

	private final Endpoints endpoints = new Endpoints(ConfigFileReader.getInstance().getBaseUrl());
	private final ScenarioContext scenarioContext;

	public TestContext() {
		scenarioContext = new ScenarioContext();
		scenarioContext.setContext(Context.USER_ID, ConfigFileReader.getInstance().getUserID());
	}

	public Endpoints getEndpoints() {
		return endpoints;
	}

	public ScenarioContext getScenarioContext() {
		return scenarioContext;
	}
}