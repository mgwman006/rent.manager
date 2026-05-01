package tz.tante.rent.manager.services;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tz.tante.rent.manager.exceptions.ResourceExistException;
import tz.tante.rent.manager.exceptions.ResourceNotFoundException;
import tz.tante.rent.manager.exceptions.TanteException;
import tz.tante.rent.manager.models.dtos.requests.account.AccountCreateDto;
import tz.tante.rent.manager.models.dtos.responses.AccountDetailsDto;
import tz.tante.rent.manager.models.entities.Account;
import tz.tante.rent.manager.repositories.AccountRepository;

@AllArgsConstructor
@Service
public class AuthService
{
  private final PasswordEncoder passwordEncoder;
  private final AccountRepository accountRepository;

  public AccountDetailsDto findAccountByPhoneNumber(String phoneNumber)
  {
    try
    {
      Account account = accountRepository.findByPhoneNumber(phoneNumber)
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

  public AccountDetailsDto findAccountByEmail(String email)
  {
    try
    {
      Account account = accountRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("Account with email " + email + " not found."));

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

  public AccountDetailsDto createAccount(AccountCreateDto accountCreateDto)
  {
    try
    {
      if (accountRepository.existByPhoneNumber(accountCreateDto.phoneNumber()))
      {
        throw new ResourceExistException("Account with phone number " + accountCreateDto.phoneNumber() + " already exists.");
      }

      String encodedPassword = passwordEncoder.encode(accountCreateDto.passWord());
      Account account = new Account(accountCreateDto.phoneNumber(), accountCreateDto.email(), encodedPassword);
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

//  public AccountDetailsDto authenticate(AccountAuthDto accountAuthDto)
//  {
//    try
//    {
//      if (accountRepository.existByPhoneNumber(accountRequestDto.phoneNumber()))
//      {
//        throw new ResourceExistException("Account with phone number " + accountRequestDto.phoneNumber() + " already exists.");
//      }
//
//      Account account = new Account(accountRequestDto.phoneNumber(), accountRequestDto.email(), accountRequestDto.passWord());
//      accountRepository.save(account);
//      return new AccountDetailsDto(
//        account.getId(),
//        account.getPhoneNumber(),
//        account.getEmail(),
//        account.isEnabled()
//      );
//    }
//    catch (Exception exception)
//    {
//      throw new TanteException(exception.getMessage());
//    }
//
//
//  }
}
