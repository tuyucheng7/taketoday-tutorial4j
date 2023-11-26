package cn.tuyucheng.taketoday.swaggerresponses.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class SwaggerConfiguration {

   @Bean
   public OpenAPI openAPI() {
      return new OpenAPI().info(new Info().title("Swagger Application")
            .description("This is a sample application for difference between @Operation & @ApiResponse")
            .version("1.0.0"));
   }
}