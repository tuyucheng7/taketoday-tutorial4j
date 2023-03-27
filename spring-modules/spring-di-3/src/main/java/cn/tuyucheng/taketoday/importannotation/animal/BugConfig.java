package cn.tuyucheng.taketoday.importannotation.animal;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(Bug.class)
class BugConfig {
}
