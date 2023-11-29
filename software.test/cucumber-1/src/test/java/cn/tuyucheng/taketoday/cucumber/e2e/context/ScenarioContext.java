package cn.tuyucheng.taketoday.cucumber.e2e.context;

import cn.tuyucheng.taketoday.cucumber.e2e.enums.Context;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {

   private final Map<String, Object> scenarioContext;

   public ScenarioContext() {
      this.scenarioContext = new HashMap<>();
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