package app.config;

import app.services.ArticleServiceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.javalite.activeweb.AppContext;
import org.javalite.activeweb.Bootstrap;

public class AppBootstrap extends Bootstrap {
	public void init(AppContext context) {
	}

	public Injector getInjector() {
		return Guice.createInjector(new ArticleServiceModule());
	}
}
