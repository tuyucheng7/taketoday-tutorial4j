package cn.tuyucheng.taketoday.javaee.security;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;


@CustomFormAuthenticationMechanismDefinition(
	loginToContinue = @LoginToContinue(
		loginPage = "/login.xhtml",
		errorPage = "/login-error.html"
	)
)
@ApplicationScoped
public class AppConfig {
}
