package cn.tuyucheng.taketoday;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import cn.tuyucheng.taketoday.spring.data.es.config.Config;

/**
 * This Manual test requires: Elasticsearch instance running on localhost:9200.
 * <p>
 * The following docker command can be used: docker run -d --name es762 -p
 * 9200:9200 -e "discovery.type=single-node" elasticsearch:7.6.2
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = Config.class)
class SpringContextManualTest {

    @Test
    void whenSpringContextIsBootstrapped_thenNoExceptions() {
    }
}