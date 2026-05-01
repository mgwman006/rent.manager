package tz.tante.rent.manager.bootstraps;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tz.tante.rent.manager.enums.AuthorityRoleName;
import tz.tante.rent.manager.enums.RoleName;
import tz.tante.rent.manager.models.entities.AuthorityRole;
import tz.tante.rent.manager.models.entities.Role;
import tz.tante.rent.manager.repositories.AuthorityRoleRepository;
import tz.tante.rent.manager.repositories.RoleRepository;

@Component
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner
{
  private final RoleRepository roleRepository;
  private final AuthorityRoleRepository authorityRoleRepository;

  @Override
  public void run(String... args)
  {
    for (RoleName roleName : RoleName.values())
    {
      if (!roleRepository.existsByName(roleName))
      {
        Role role = new Role();
        role.setName(roleName);
        roleRepository.save(role);
      }
    }

    for (AuthorityRoleName authorityRoleName : AuthorityRoleName.values())
    {
      if (!authorityRoleRepository.existsByName(authorityRoleName))
      {
        AuthorityRole authorityRole = new AuthorityRole();
        authorityRole.setName(authorityRoleName);
        authorityRoleRepository.save(authorityRole);
      }
    }
  }



}