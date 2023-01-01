package cn.tuyucheng.taketoday.servletinitializer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@SpringBootApplication
public class WarInitializerApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(WarInitializerApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication sa = new SpringApplication(WarInitializerApplication.class);
        sa.setLogStartupInfo(false);
        sa.run(args);
    }

    @RestController
    public static class WarInitializerController {

        @GetMapping("/")
        public String handler(Model model) {
            model.addAttribute("date", LocalDateTime.now());
            return "WarInitializerApplication is up and running!";
        }
    }
}