package tz.tante.rent.manager.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tz.tante.rent.manager.enums.UnitStatus;


@Component
public class PropertyHttpClient {

  private static final Logger logger = LoggerFactory.getLogger(PropertyHttpClient.class);
  private final RestClient restClient;
  
  public PropertyHttpClient(ClientHttpRequestInterceptor oauth2BearerTokenInterceptor, @Value("${property.manager.base.url}") String propertyManagerBaseUrl)
  {
    this.restClient = RestClient.builder()
      .baseUrl(propertyManagerBaseUrl)
      .requestInterceptor(oauth2BearerTokenInterceptor)
      .build();
  }

  public void updateUnitStatus(Long unitId, UnitStatus status)
  {
    try
    {
      RestClient.RequestBodySpec request = restClient
        .patch()
        .uri(uriBuilder -> uriBuilder
          .path("/v1/units/{unitId}/status")
          .queryParam("status", status)
          .build(unitId));


      ResponseEntity<String> response = request
        .retrieve()
        .toEntity(String.class);

      if (response.getStatusCode().is2xxSuccessful())
      {
        logger.info("Successfully updated unit status: {}", response.getBody());
      }
      else
      {
        logger.error(response.getBody());
      }
    }
    catch (Exception exception)
    {
      logger.error("Error updating unit status: {}", exception.getMessage(), exception);
    }
  }

}