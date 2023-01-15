package cn.tuyucheng.taketoday.fullstack.restassured.cucumber.steps;

import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.Endpoints;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.context.TestContext;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.scenario.ScenarioContext;

public class BaseStep {

	private final ScenarioContext scenarioContext;
	private final Endpoints endpoints;

	public BaseStep(TestContext testContext) {
		endpoints = testContext.getEndpoints();
		scenarioContext = testContext.getScenarioContext();
	}

	public Endpoints getEndPoints() {
		return endpoints;
	}

	public ScenarioContext getScenarioContext() {
		return scenarioContext;
	}
}