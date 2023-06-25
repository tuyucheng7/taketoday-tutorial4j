package cn.tuyucheng.taketoday.swaggerexample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
@EnableWebMvc
public class SwaggerConfig {

   @Bean
   public OpenAPI openAPI() {
      return new OpenAPI().info(new Info().title("Products API")
            .description("API to let you add and view product")
            .version("1.0.0")
            .contact(new Contact().name("John Doe")
                  .email("myemail@company.com")
                  .url("www.tuyucheng.com"))
            .license(new License().name("License of API")
                  .url("API license URL")));
   }
}