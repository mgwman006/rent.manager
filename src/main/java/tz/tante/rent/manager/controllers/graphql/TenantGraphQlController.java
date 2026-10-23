package tz.tante.rent.manager.controllers.graphql;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import tz.tante.rent.manager.models.dtos.requests.Tenants.TenantContactUpdateDTO;
import tz.tante.rent.manager.models.dtos.responses.TenantDetailsDTO;
import tz.tante.rent.manager.services.TenantService;

@Controller
@AllArgsConstructor
public class TenantGraphQlController
{
  private final TenantService tenantService;

  @MutationMapping(name = "updateTenant")
  public TenantDetailsDTO updateContact(@Argument Long id, @Argument @Valid TenantContactUpdateDTO input)
  {
    return tenantService.updateTenantContact(id, input);
  }
}
