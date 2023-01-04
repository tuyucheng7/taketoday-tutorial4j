package cn.tuyucheng.taketoday.servlet;

import cn.tuyucheng.taketoday.spring.WebFlowConfig;
import cn.tuyucheng.taketoday.spring.WebMvcConfig;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.ServletRegistration.Dynamic;

public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    public WebInitializer() {
        super();
    }

    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{WebMvcConfig.class, WebFlowConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected void customizeRegistration(final Dynamic registration) {
        super.customizeRegistration(registration);
    }
}