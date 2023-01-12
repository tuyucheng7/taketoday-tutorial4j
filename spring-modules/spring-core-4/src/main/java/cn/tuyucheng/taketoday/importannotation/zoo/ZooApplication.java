package cn.tuyucheng.taketoday.importannotation.zoo;

import cn.tuyucheng.taketoday.importannotation.animal.AnimalScanConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(AnimalScanConfiguration.class)
class ZooApplication {
}
