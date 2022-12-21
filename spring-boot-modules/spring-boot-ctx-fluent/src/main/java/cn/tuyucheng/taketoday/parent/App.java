package cn.tuyucheng.taketoday.parent;

import cn.tuyucheng.taketoday.ctx1.Ctx1Config;
import cn.tuyucheng.taketoday.ctx2.Ctx2Config;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class App {
    public static void main(String[] args) {
        new SpringApplicationBuilder().parent(ParentConfig.class)
              .web(WebApplicationType.NONE)
              .child(Ctx1Config.class)
              .web(WebApplicationType.SERVLET)
              .sibling(Ctx2Config.class)
              .web(WebApplicationType.SERVLET)
              .run(args);
    }
}