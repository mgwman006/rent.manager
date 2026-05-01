package tz.tante.rent.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.tante.rent.manager.enums.RoleName;
import tz.tante.rent.manager.models.entities.Role;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>
{
  Optional<Role> findByName(RoleName name);
  boolean existsByName(RoleName name);
}