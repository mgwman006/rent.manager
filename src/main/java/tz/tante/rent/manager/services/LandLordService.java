package tz.tante.rent.manager.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import tz.tante.rent.manager.enums.RoleName;
import tz.tante.rent.manager.exceptions.ResourceExistException;
import tz.tante.rent.manager.exceptions.TanteException;
import tz.tante.rent.manager.models.dtos.requests.landlord.LandLordRequestDto;
import tz.tante.rent.manager.models.dtos.responses.LandLordResponseDto;
import tz.tante.rent.manager.models.entities.Landlord;
import tz.tante.rent.manager.models.entities.Role;
import tz.tante.rent.manager.models.entities.User;
import org.springframework.stereotype.Service;
import tz.tante.rent.manager.repositories.LandlordRepository;
import tz.tante.rent.manager.repositories.UserRepository;

@Getter
@Setter
@AllArgsConstructor
@Service
public class LandLordService
{
  private final LandlordRepository landlordRepository;
  private final UserRepository userRepository;
  private final RoleService roleService;
  private final PasswordEncoder passwordEncoder;


  @Transactional
  public LandLordResponseDto registerLandLord(LandLordRequestDto requestDto)
  {
    try
    {
      if (landlordRepository.existsByPhoneNumber(requestDto.phoneNumber()))
      {
        throw new ResourceExistException("Landlord with phone number: " + requestDto.phoneNumber() + " already exists");
      }

      if (landlordRepository.existsByEmail(requestDto.email()))
      {
        throw new ResourceExistException("Landlord with email: " + requestDto.email() + " already exists");
      }


      Landlord newLandLord = new Landlord(
        requestDto.firstName(),
        requestDto.lastName(),
        requestDto.phoneNumber(),
        requestDto.email()
      );

      User user = userRepository.findByUsername(requestDto.phoneNumber())
        .orElse(null);

      if (user == null)
      {
        String encodedPassword = passwordEncoder.encode(requestDto.passWord());

        user = new User(requestDto.phoneNumber(), encodedPassword);
        Role landlordRole = roleService.getRoleByName(RoleName.ROLE_LANDLORD.toString());
        user.addRole(landlordRole);

        userRepository.save(user);
      }

      newLandLord.setUser(user);
      user.setLandlordProfile(newLandLord);

      newLandLord = landlordRepository.save(newLandLord);

      return new LandLordResponseDto(
        newLandLord.getId(),
        newLandLord.getFirstName(),
        newLandLord.getLastName(),
        newLandLord.getPhoneNumber(),
        newLandLord.getEmail()
      );
    }
    catch (Exception exception)
    {
      throw new TanteException(exception.getMessage());
    }

  }


}
