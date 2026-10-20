package tz.tante.rent.manager.configs.security;

import feign.RequestInterceptor;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import tz.tante.rent.manager.exceptions.TanteException;

@Configuration
public class FeignOAuth2Config
{
  @Bean
  RequestInterceptor feignOAuth2RequestInterceptor(OAuth2AuthorizedClientManager authorizedClientManager)
  {
    return requestTemplate -> {
      var authorizeRequest = OAuth2AuthorizeRequest
        .withClientRegistrationId("rent-manager")
        .principal("rent-manager")
        .build();

      OAuth2AuthorizedClient client = authorizedClientManager.authorize(authorizeRequest);

      if (client == null || client.getAccessToken() == null)
      {
        throw new TanteException("Could not obtain rent-manager access token");
      }

      requestTemplate.header("Authorization", "Bearer " + client.getAccessToken().getTokenValue());
    };
  }

  @Bean
  Retryer propertyManagerRetryer()
  {
    return new Retryer.Default(1000, 4000, 4);
  }

}