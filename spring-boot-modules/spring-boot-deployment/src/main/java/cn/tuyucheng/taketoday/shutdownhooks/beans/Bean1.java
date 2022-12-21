package cn.tuyucheng.taketoday.shutdownhooks.beans;

import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class Bean1 {

    @PreDestroy
    public void destroy() {
        System.out.println("Shutdown triggered using @PreDestroy.");
    }
}