package com.tante.landlordtenant.service;

import com.tante.landlordtenant.models.Entities.Users.User;
import com.tante.landlordtenant.models.Enums.UserStatus;
import com.tante.landlordtenant.models.Requests.Users.NewUserRequestDto;
import com.tante.landlordtenant.models.Requests.Users.UpdateUserRequestDto;
import com.tante.landlordtenant.models.Requests.Users.UserLogInRequestDto;
import com.tante.landlordtenant.models.Responses.Users.UpdateUserResponseDto;
import com.tante.landlordtenant.models.Responses.Users.UserLogInResponseDto;
import com.tante.landlordtenant.models.Responses.Users.UserResponseDto;
import com.tante.landlordtenant.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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

    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return  users.stream().map
                (u -> new
                    UserResponseDto
                    (
                            u.getId(),
                            u.getEmail(),
                            u.getPassWord()
                    )
                ).toList();
    }

    public Optional<UserResponseDto> getUserById(Long userId)
    {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty())
            return Optional.empty();
        User user = userOptional.get();
        return Optional.of( new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getPassWord()));
    }

    public UserLogInResponseDto logIng(UserLogInRequestDto logInRequest) {

        Optional<User> optionalUser = userRepository.findByEmail(logInRequest.email());
        if (optionalUser.isEmpty())
            return new UserLogInResponseDto(
                    UserStatus.LogInFail,
                    "user not exist",
                    Optional.empty());

        User user = optionalUser.get();

        if (!user.getPassWord().equals(logInRequest.passWord()))
            return new UserLogInResponseDto(
                    UserStatus.LogInFail,
                    "wrong password",
                    Optional.empty());

        return new UserLogInResponseDto(
                UserStatus.LogInSuccess,
                "success",
                Optional.of( new UserResponseDto(
                        user.getId(),
                        user.getEmail(),
                        user.getPassWord()
                        )
                ));
    }

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
