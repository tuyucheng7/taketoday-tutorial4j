package cn.tuyucheng.taketoday.enforcer;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

public class MyCustomRule implements EnforcerRule {

	public void execute(EnforcerRuleHelper enforcerRuleHelper) throws EnforcerRuleException {

		try {

			String groupId = (String) enforcerRuleHelper.evaluate("${project.groupId}");

			if (groupId == null || !groupId.startsWith("cn.tuyucheng.taketoday")) {
				throw new EnforcerRuleException("Project group id does not start with cn.tuyucheng.taketoday");
			}

		} catch (ExpressionEvaluationException ex) {
			throw new EnforcerRuleException("Unable to lookup an expression " + ex.getLocalizedMessage(), ex);
		}
	}

	public boolean isCacheable() {
		return false;
	}

	public boolean isResultValid(EnforcerRule enforcerRule) {
		return false;
	}

	public String getCacheId() {
		return null;
	}
}