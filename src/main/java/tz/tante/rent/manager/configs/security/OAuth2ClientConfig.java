package tz.tante.rent.manager.configs.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

@Configuration
public class OAuth2ClientConfig {

  @Bean
  OAuth2AuthorizedClientManager authorizedClientManager(
    ClientRegistrationRepository registrations,
    OAuth2AuthorizedClientService authorizedClientService) {

    var provider = OAuth2AuthorizedClientProviderBuilder.builder()
      .clientCredentials()
      .build();

    var manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
      registrations,
      authorizedClientService
    );

    manager.setAuthorizedClientProvider(provider);
    return manager;
  }

  @Bean
  ClientHttpRequestInterceptor oauth2BearerTokenInterceptor(
    OAuth2AuthorizedClientManager authorizedClientManager) {

    return (request, body, execution) -> {
      var authorizeRequest = OAuth2AuthorizeRequest
        .withClientRegistrationId("property-manager")
        .principal("rent-manager")
        .build();

      OAuth2AuthorizedClient client =
        authorizedClientManager.authorize(authorizeRequest);

      if (client == null || client.getAccessToken() == null) {
        throw new IllegalStateException(
          "Could not obtain property-manager access token"
        );
      }

      request.getHeaders().setBearerAuth(
        client.getAccessToken().getTokenValue()
      );

      return execution.execute(request, body);
    };
  }
}
