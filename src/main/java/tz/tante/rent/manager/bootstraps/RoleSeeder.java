package tz.tante.rent.manager.bootstraps;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tz.tante.rent.manager.enums.RoleName;
import tz.tante.rent.manager.models.entities.Role;
import tz.tante.rent.manager.repositories.RoleRepository;

@Component
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner
{
  private final RoleRepository roleRepository;

  @Override
  public void run(String... args)
  {
    for (RoleName roleName : RoleName.values())
    {
      if (!roleRepository.existsByName(roleName))
      {
        Role role = new Role();
        role.setName(roleName.toString());
        roleRepository.save(role);
      }
    }
  }
}