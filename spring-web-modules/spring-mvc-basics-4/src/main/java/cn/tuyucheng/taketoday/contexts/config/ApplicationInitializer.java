package cn.tuyucheng.taketoday.contexts.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class ApplicationInitializer //implements WebApplicationInitializer
{
    //uncomment to run the multiple contexts example
    //@Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        //Here, we can define a root context and register servlets, among other things.
        //However, since we've later defined other classes to do the same and they would clash,
        //we leave this commented out.

        //Root XML Context
        //XmlWebApplicationContext rootContext = new XmlWebApplicationContext();
        //rootContext.setConfigLocations("/WEB-INF/rootApplicationContext.xml");
        //Annotations Context
        //AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        //rootContext.register(RootApplicationConfig.class);
        //Registration
        //servletContext.addListener(new ContextLoaderListener(rootContext));

        //Dispatcher Servlet
        //XmlWebApplicationContext normalWebAppContext = new XmlWebApplicationContext();
        //normalWebAppContext.setConfigLocation("/WEB-INF/normal-webapp-servlet.xml");
        //ServletRegistration.Dynamic normal = servletContext.addServlet("normal-webapp", new DispatcherServlet(normalWebAppContext));
        //normal.setLoadOnStartup(1);
        //normal.addMapping("/api/*");
    }

}
