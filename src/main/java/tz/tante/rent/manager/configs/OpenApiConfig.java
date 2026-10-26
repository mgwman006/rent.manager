package tz.tante.rent.manager.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static tz.tante.rent.manager.utilities.Constant.BEARER_AUTH;

@Configuration
public class OpenApiConfig {

  @Bean
  public GroupedOpenApi publicApi()
  {
    return GroupedOpenApi.builder()
      .group("rent-manager")
      .packagesToScan("tz.tante.rent.manager.controllers")
      .build();
  }

  @Bean
  public OpenAPI customOpenAPI()
  {
    return new OpenAPI()
      .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
      .components(new Components()
        .addSecuritySchemes(BEARER_AUTH,
          new SecurityScheme()
            .name(BEARER_AUTH)
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
        )
      );
  }
}
