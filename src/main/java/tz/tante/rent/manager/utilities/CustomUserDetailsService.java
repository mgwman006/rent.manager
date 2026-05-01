package tz.tante.rent.manager.utilities;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tz.tante.rent.manager.models.entities.Account;
import tz.tante.rent.manager.models.entities.Role;
import tz.tante.rent.manager.models.entities.User;
import tz.tante.rent.manager.repositories.AccountRepository;
import tz.tante.rent.manager.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService
{
  private final AccountRepository accountRepository;

  @Override
  public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException
  {
    Account account = accountRepository.findByPhoneNumber(phoneNumber)
      .orElseThrow(() -> new UsernameNotFoundException("Account with phoneNumber: " + phoneNumber+" does not exist"));

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
