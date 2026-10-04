package tz.tante.rent.manager.clients;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tz.tante.rent.manager.enums.UnitStatus;
import tz.tante.rent.manager.exceptions.TanteException;

import java.util.Map;

@Component
public class PropertyHttpClient {

  private final RestClient restClient;

  public PropertyHttpClient() {
    this.restClient = RestClient.builder()
      .baseUrl(getBaseUrl())
      .build();
  }

  public String post(
    String jsonBody,
    String path,
    Map<String, String> additionalHeaders
  ) {
    try {
      RestClient.RequestBodySpec request = restClient
        .post()
        .uri(path)
        .contentType(MediaType.APPLICATION_JSON);

      if (additionalHeaders != null) {
        request.headers(headers ->
          additionalHeaders.forEach(headers::set)
        );
      }

      ResponseEntity<String> response = request
        .body(jsonBody)
        .retrieve()
        .toEntity(String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      }

      throw new TanteException(
        "Cannot POST request. HTTP status: "
          + response.getStatusCode().value()
          + ", response: "
          + response.getBody()
      );

    } catch (TanteException e) {
      throw e;

    } catch (Exception e) {
      throw new TanteException(
        "Cannot POST request: " + e.getMessage()
      );
    }
  }

  public String patch(
    String jsonBody,
    String path,
    Map<String, String> additionalHeaders
  ) {
    try {
      RestClient.RequestBodySpec request = restClient
        .patch()
        .uri(path)
        .contentType(MediaType.APPLICATION_JSON);

      if (additionalHeaders != null) {
        request.headers(headers ->
          additionalHeaders.forEach(headers::set)
        );
      }

      ResponseEntity<String> response = request
        .body(jsonBody)
        .retrieve()
        .toEntity(String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      }

      throw new TanteException(
        "Cannot PATCH request. HTTP status: "
          + response.getStatusCode().value()
          + ", response: "
          + response.getBody()
      );

    } catch (TanteException e) {
      throw e;

    } catch (Exception e) {
      throw new TanteException(
        "Cannot PATCH request: " + e.getMessage()
      );
    }
  }
  public String updateUnitStatus(
    Long unitId,
    UnitStatus status,
    Map<String, String> additionalHeaders
  ) {
    try {
      RestClient.RequestBodySpec request = restClient
        .patch()
        .uri(uriBuilder -> uriBuilder
          .path("/v1/units/{unitId}/status")
          .queryParam("status", status)
          .build(unitId)
        );

      if (additionalHeaders != null) {
        request.headers(headers ->
          additionalHeaders.forEach(headers::set)
        );
      }

      ResponseEntity<String> response = request
        .retrieve()
        .toEntity(String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      }

      throw new TanteException(
        "Cannot update unit status. HTTP status: "
          + response.getStatusCode().value()
          + ", response: "
          + response.getBody()
      );

    } catch (TanteException e) {
      throw e;

    } catch (Exception e) {
      throw new TanteException(
        "Cannot update unit status: " + e.getMessage()
      );
    }
  }

  private String getBaseUrl() {
    return "http://localhost:8082/property-manager";
  }

}