package cn.tuyucheng.taketoday.boot.jackson.app;

import cn.tuyucheng.taketoday.boot.jackson.config.CoffeeCustomizerConfig;
import org.springframework.context.annotation.Import;

@Import(CoffeeCustomizerConfig.class)
public class CoffeeCustomizerIntegrationTest extends AbstractCoffeeIntegrationTest {
}
