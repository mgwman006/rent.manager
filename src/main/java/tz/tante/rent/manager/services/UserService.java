package tz.tante.rent.manager.services;

import lombok.AllArgsConstructor;
import tz.tante.rent.manager.exceptions.TanteException;
import tz.tante.rent.manager.exceptions.ResourceNotFoundException;
import tz.tante.rent.manager.models.entities.User;
import tz.tante.rent.manager.repositories.UserRepository;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;

  public User getUserByUserName(String userName)
  {
    try
    {
      return userRepository.findByUsername(userName)
        .orElseThrow( () -> new ResourceNotFoundException("User not found"));
    }
    catch (Exception exception)
    {
      throw new TanteException(exception.getMessage());
    }
  }


//    public Result<JwtTokenDetails> logIn(String email, String passWord)
//    {
//        try
//        {
//            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, passWord));
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String token = jwtUtils.generateToken(
//              userDetails.getUsername(),
//              userDetails.getAuthorities()
//                .stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toSet())
//            );
//
//            return Result.success(
//              "Login success",
//              new JwtTokenDetails(
//                userDetails.getUsername(),
//                token
//              ));

//        }
//        catch (BadCredentialsException e)
//        {
//            return Result.failure(e.getMessage());
//        }
//    }

//    public UserResponseDto registerUser(NewUserRequestDto userRequest) {
//
//        User newUser = new User(
//                userRequest.firstName(),
//                userRequest.lastName(),
//                userRequest.email(),
//                userRequest.passWord()
//        );
//        User createdUser = userRepository.save(newUser);
//        System.out.println(createdUser.toString());
//        return new UserResponseDto(
//                createdUser.getId(),
//                createdUser.getFirstName(),
//                createdUser.getLastName(),
//                createdUser.getEmail(),
//                Optional.of(createdUser.getPassWord()));
//    }

//    public List<UserResponseDto> getAllUsers() {
//        List<User> users = userRepository.findAll();
//        return  users.stream().map
//                (u -> new
//                    UserResponseDto
//                    (
//                            u.getId(),
//                            u.getEmail(),
//                            u.getPassWord()
//                    )
//                ).toList();
//    }
//


//    public UserLogInResponseDto logIng(UserLogInRequestDto logInRequest) {
//
//        Optional<User> optionalUser = userRepository.findByEmail(logInRequest.email());
//        if (optionalUser.isEmpty())
//            return new UserLogInResponseDto(
//                    UserStatus.LogInFail,
//                    "user not exist",
//                    Optional.empty());
//
//        User user = optionalUser.get();
//
//        if (!user.getPassWord().equals(logInRequest.passWord()))
//            return new UserLogInResponseDto(
//                    UserStatus.LogInFail,
//                    "wrong password",
//                    Optional.empty());
//
//        return new UserLogInResponseDto(
//                UserStatus.LogInSuccess,
//                "success",
//                Optional.of( new UserResponseDto(
//                        user.getId(),
//                        user.getEmail(),
//                        user.getPassWord()
//                        )
//                ));
//    }

//    public UpdateUserResponseDto updateUser(Long userId, UpdateUserRequestDto updateUserRequest)
//    {
//        Optional<User> optionalUser = userRepository.findById(userId);
//        if (optionalUser.isEmpty())
//            return new UpdateUserResponseDto(
//                UserStatus.UpdateFail,
//                    "User Not Exist",
//                    Optional.empty()
//            );
//
//        User user = optionalUser.get();
//        user.update(
//                updateUserRequest.firstName(),
//                updateUserRequest.lastName(),
//                updateUserRequest.email(),
//                updateUserRequest.passWord()
//        );
//
//        User updated = userRepository.save(user);
//
//        return new UpdateUserResponseDto(
//                UserStatus.UpdateSuccess,
//                "update success",
//                Optional.of(new UserResponseDto(
//                        user.getId(),
//                        user.getFirstName(),
//                        user.getLastName(),
//                        user.getEmail(),
//                        Optional.of(user.getPassWord())
//                ))
//        );
//
//
//    }
}
