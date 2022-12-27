package cn.tuyucheng.taketoday.jwt;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
	info = @Info(title = "User API", version = "${api.version}",
		contact = @Contact(name = "Tuyucheng", email = "user-apis@tuyucheng.com", url = "https://www.tuyucheng.com"),
		license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"), termsOfService = "${tos.uri}",
		description = "${api.description}"),
	servers = {
		@Server(url = "http://localhost:8080", description = "Development"),
		@Server(url = "${api.server.url}", description = "Production")})
public class OpenAPI30Configuration {

	/**
	 * Configure the OpenAPI components.
	 *
	 * @return Returns fully configure OpenAPI object
	 * @see OpenAPI
	 */
	@Bean
	public OpenAPI customizeOpenAPI() {
		final String securitySchemeName = "bearerAuth";
		return new OpenAPI()
			.addSecurityItem(new SecurityRequirement()
				.addList(securitySchemeName))
			.components(new Components()
				.addSecuritySchemes(securitySchemeName, new SecurityScheme()
					.name(securitySchemeName)
					.type(SecurityScheme.Type.HTTP)
					.scheme("bearer")
					.description(
						"Provide the JWT token. JWT token can be obtained from the Login API. For testing, use the credentials <strong>john/password</strong>")
					.bearerFormat("JWT")));
	}
}