package tz.tante.rent.manager.utilities;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tz.tante.rent.manager.models.entities.Account;
import tz.tante.rent.manager.repositories.AccountRepository;

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
      .withUsername(account.getPhoneNumber())
      .password(account.getPassword())
      .disabled(!account.isEnabled())
      .authorities(
        account.getAuthorityRoles()
          .stream()
          .map(authorityRole -> new SimpleGrantedAuthority(authorityRole.getName().name()))
          .toList()
      ).build();
  }
}
