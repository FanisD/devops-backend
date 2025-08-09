package gr.hua.dit.ds.springbootdemo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
public class AppConfig {

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    public OpenAPI openAPI() {
        OpenAPI info = new OpenAPI()
        // Configure JWT authentication for Swagger UI
        .addSecurityItem(new SecurityRequirement().
                        addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes
                        ("Bearer Authentication", createAPIKeyScheme()))
                // Set API documentation information
                .info(new Info().title("DS ASSIGNMENT 2024")
                        .description("This is the API used in ds-assignment-2024 project")
                        .version("1.0").contact(new Contact().name("Maximos Spyridon Diakoumakos")
                                .email("it2021027@hua.gr").url("https://github.com/it2021027"))
                        .license(new License().name("License of API")
                                .url("https://swagger.io/license/")));
        return info;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
