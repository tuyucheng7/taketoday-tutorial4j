package cn.tuyucheng.taketoday.openid.oidc.login;

import cn.tuyucheng.taketoday.openid.oidc.utils.YamlLoaderInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringOidcLoginApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SpringOidcLoginApplication.class);
        ApplicationContextInitializer<ConfigurableApplicationContext> yamlInitializer = new YamlLoaderInitializer("login-application.yml");
        application.addInitializers(yamlInitializer);
        application.run(args);
    }

}
