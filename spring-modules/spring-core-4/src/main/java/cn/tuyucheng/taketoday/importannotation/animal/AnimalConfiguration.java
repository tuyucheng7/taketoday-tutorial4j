package cn.tuyucheng.taketoday.importannotation.animal;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MammalConfiguration.class, BirdConfig.class})
class AnimalConfiguration {
}
