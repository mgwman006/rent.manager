package tz.tante.rent.manager.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.tante.rent.manager.models.dtos.ApiResponse;
import tz.tante.rent.manager.models.dtos.requests.account.AccountCreateDto;
import tz.tante.rent.manager.models.dtos.responses.AccountDetailsDto;
import tz.tante.rent.manager.services.AuthService;


@AllArgsConstructor
@RestController
@RequestMapping("/v1/auth")
public class AuthController
{
  private final AuthService authService;

  @GetMapping("/phone")
  public ResponseEntity<ApiResponse<AccountDetailsDto>> getAccountByPhoneNumber(
    @NotBlank @RequestParam String phoneNumber)
  {
    AccountDetailsDto accountDetailsDto = authService.findAccountByPhoneNumber(phoneNumber);
    return ResponseEntity.status(HttpStatus.OK)
      .body(ApiResponse.success(accountDetailsDto,HttpStatus.OK.value()));
  }

  @PostMapping("/account")
  public ResponseEntity<ApiResponse<AccountDetailsDto>> createAccount(
    @Valid @RequestBody AccountCreateDto accountCreateDto)
  {
    AccountDetailsDto accountDetailsDto = authService.createAccount(accountCreateDto);
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(ApiResponse.success(accountDetailsDto,HttpStatus.CREATED.value()));
  }
}
