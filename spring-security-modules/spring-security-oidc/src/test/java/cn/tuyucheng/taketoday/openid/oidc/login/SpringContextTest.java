package cn.tuyucheng.taketoday.openid.oidc.login;

import cn.tuyucheng.taketoday.openid.oidc.utils.YamlLoaderInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(classes = SpringOidcLoginApplication.class, properties = "custom.configyaml.file=login-application.yml")
@ContextConfiguration(initializers = YamlLoaderInitializer.class)
public class SpringContextTest {

    @Test
    public void whenSpringContextIsBootstrapped_thenNoExceptions() {
    }
}
