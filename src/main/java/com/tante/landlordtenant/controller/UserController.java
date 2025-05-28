package com.tante.landlordtenant.controller;

import com.tante.landlordtenant.models.Enums.UserStatus;
import com.tante.landlordtenant.models.Requests.Users.NewUserRequestDto;
import com.tante.landlordtenant.models.Requests.Users.UpdateUserRequestDto;
import com.tante.landlordtenant.models.Requests.Users.UserLogInRequestDto;
import com.tante.landlordtenant.models.Responses.Users.UpdateUserResponseDto;
import com.tante.landlordtenant.models.Responses.Users.UserLogInResponseDto;
import com.tante.landlordtenant.models.Responses.Users.UserResponseDto;
import com.tante.landlordtenant.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api/v1/users")
@RestController
@CrossOrigin(origins = "*") // Allow only this origin

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
//
//    @PostMapping
//    public ResponseEntity<UserResponseDto> registerUser(@RequestBody NewUserRequestDto userRequest)
//    {
//        UserResponseDto createdUser = userService.registerUser(userRequest);
//        URI location = URI.create("users/"+createdUser.id());
//        return ResponseEntity.created(location).body(createdUser);
//    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers()
    {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId)
    {
        Optional<UserResponseDto> userOptional = userService.getUserById(userId);
        return userOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/logIn")
    public ResponseEntity<UserLogInResponseDto> logIn(@RequestBody UserLogInRequestDto logInRequest)
    {
        UserLogInResponseDto userLogInResponse = userService.logIng(logInRequest);
        return ResponseEntity.ok(userLogInResponse);
    }

//    @PutMapping("/{userId}")
//    public ResponseEntity<UpdateUserResponseDto> updateUser(
//            @PathVariable Long userId,
//            @RequestBody UpdateUserRequestDto updateUserRequest
//            )
//    {
//        UpdateUserResponseDto updateUserResponse = userService.updateUser(userId,updateUserRequest);
//        return ResponseEntity.ok(updateUserResponse);
//    }
}
