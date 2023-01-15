package cn.tuyucheng.taketoday.fullstack.restassured.cucumber.scenario;

import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.enums.Context;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {

	private final Map<String, Object> scenarioContext;

	public ScenarioContext() {
		scenarioContext = new HashMap<>();
	}

	public void setContext(Context key, Object value) {
		scenarioContext.put(key.toString(), value);
	}

	public Object getContext(Context key) {
		return scenarioContext.get(key.toString());
	}

	public boolean isContains(Context key) {
		return scenarioContext.containsKey(key.toString());
	}
}