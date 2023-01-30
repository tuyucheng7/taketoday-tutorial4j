package cn.tuyucheng.taketoday.spring.serverconfig;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("skipAutoConfig")
class GreetingSkipAutoConfigLiveTest extends GreetingLiveTest {

}