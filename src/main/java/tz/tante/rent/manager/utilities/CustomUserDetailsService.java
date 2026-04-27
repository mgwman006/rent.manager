package tz.tante.rent.manager.utilities;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tz.tante.rent.manager.models.entities.Role;
import tz.tante.rent.manager.models.entities.User;
import tz.tante.rent.manager.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService
{
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
  {
    User user = userRepository.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

    return org.springframework.security.core.userdetails.User
      .withUsername(user.getUsername())
      .password(user.getPassword())
      .disabled(!user.isEnabled())
      .authorities(
        user.getRoles()
          .stream()
          .map(role -> new SimpleGrantedAuthority(role.getName()))
          .toList()
      ).build();
  }
}
