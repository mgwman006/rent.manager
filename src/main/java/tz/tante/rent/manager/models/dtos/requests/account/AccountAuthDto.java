package tz.tante.rent.manager.models.dtos.requests.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload to Authenticate account")
public record AccountAuthDto(
  @Schema(example = "+27821234567")
  @NotBlank(message = "Phone number is required")
  @Pattern(
    regexp = "^\\+?[0-9]{10,15}$",
    message = "Invalid phone number"
  )
  String phoneNumber,

  @Schema(example = "123456")
  @NotBlank(message = "Password is required")
  @Size(min = 6, message = "Password must be at least 6 characters")
  String passWord
){ }
