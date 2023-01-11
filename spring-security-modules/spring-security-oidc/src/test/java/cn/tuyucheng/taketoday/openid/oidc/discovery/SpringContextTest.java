package cn.tuyucheng.taketoday.openid.oidc.discovery;

import cn.tuyucheng.taketoday.openid.oidc.utils.YamlLoaderInitializer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

// We'll ignore this test, as we don't want to depend on Google's OIDC-configuration endpoint to be available
@Disabled
@SpringBootTest(classes = SpringOidcDiscoveryApplication.class, properties = "custom.configyaml.file=discovery-application.yml")
@ContextConfiguration(initializers = YamlLoaderInitializer.class)
public class SpringContextTest {

    @Test
    public void whenSpringContextIsBootstrapped_thenNoExceptions() {
    }
}
