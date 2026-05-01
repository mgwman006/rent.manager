package tz.tante.rent.manager.services;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import tz.tante.rent.manager.enums.RoleName;
import tz.tante.rent.manager.exceptions.TanteException;
import tz.tante.rent.manager.exceptions.ResourceNotFoundException;
import tz.tante.rent.manager.models.entities.Role;
import tz.tante.rent.manager.repositories.RoleRepository;

@Getter
@Setter
@AllArgsConstructor
@Service
public class RoleService
{
  private final RoleRepository roleRepository;

  public Role getRoleByName(RoleName roleName)
  {
    try
    {
      return roleRepository.findByName(roleName)
        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
    }
    catch (Exception exception)
    {
      throw new TanteException(exception.getMessage());
    }
  }
}
