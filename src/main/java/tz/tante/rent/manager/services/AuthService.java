package tz.tante.rent.manager.services;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tz.tante.rent.manager.exceptions.AuthException;
import tz.tante.rent.manager.exceptions.ResourceExistException;
import tz.tante.rent.manager.exceptions.ResourceNotFoundException;
import tz.tante.rent.manager.exceptions.TanteException;
import tz.tante.rent.manager.models.dtos.requests.account.AccountAuthRequestDTO;
import tz.tante.rent.manager.models.dtos.requests.account.AccountCreateDto;
import tz.tante.rent.manager.models.dtos.responses.AccountDetailsDto;
import tz.tante.rent.manager.models.dtos.responses.AccountAuthResponseDTO;
import tz.tante.rent.manager.models.entities.Account;
import tz.tante.rent.manager.repositories.AccountRepository;
import tz.tante.rent.manager.utilities.JwtUtils;
import tz.tante.rent.manager.utilities.Utils;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class AuthService
{
  private final PasswordEncoder passwordEncoder;
  private final AccountRepository accountRepository;
  private final JwtUtils jwtUtils;
  private final AuthenticationManager authenticationManager;

  public AccountDetailsDto findAccountByPhoneNumber(String phoneNumber)
  {
    try
    {
      String normalizedPhoneNumber = Utils.normalizePhone(phoneNumber);
      Account account = accountRepository.findByPhoneNumber(normalizedPhoneNumber)
        .orElseThrow(() -> new ResourceNotFoundException("Account with phone number " + phoneNumber + " not found."));

      return new AccountDetailsDto(
        account.getId(),
        account.getPhoneNumber(),
        account.getEmail(),
        account.isEnabled()
      );
    }
    catch (Exception exception)
    {
      throw new TanteException(exception.getMessage());
    }
  }

  public AccountDetailsDto createAccount(AccountCreateDto request)
  {
    try
    {
      String normalizedPhoneNumber = Utils.normalizePhone(request.phoneNumber());
      if (accountRepository.existsByPhoneNumber(normalizedPhoneNumber))
      {
        throw new ResourceExistException("Account with phone number " + normalizedPhoneNumber + " already exists.");
      }

      String encodedPassword = passwordEncoder.encode(request.passWord());
      Account account = new Account(normalizedPhoneNumber, encodedPassword);
      accountRepository.save(account);
      return new AccountDetailsDto(
        account.getId(),
        account.getPhoneNumber(),
        account.getEmail(),
        account.isEnabled()
      );
    }
    catch (Exception exception)
    {
      throw new TanteException(exception.getMessage());
    }
  }

  public AccountAuthResponseDTO authenticate(AccountAuthRequestDTO request)
  {
    try
    {
      String normalizedPhoneNumber = Utils.normalizePhone(request.phoneNumber());
      Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(normalizedPhoneNumber, request.passWord()));

      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      String token = jwtUtils.generateToken(
        userDetails.getUsername(),
        userDetails.getAuthorities()
          .stream()
          .map(GrantedAuthority::getAuthority)
          .collect(Collectors.toSet())
      );

      return new AccountAuthResponseDTO(
        userDetails.getUsername(),
        token
      );
    }
    catch (Exception exception)
    {
      throw new AuthException(exception.getMessage());
    }
  }
}
