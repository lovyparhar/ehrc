package com.demographic.demographic.service;

import com.demographic.demographic.entity.User;
import com.demographic.demographic.model.AddUserRequest;
import com.demographic.demographic.model.UserResponse;
import com.demographic.demographic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserResponse findUserByAadhar(String aadhar) {
        Optional<User> user = userRepository.findByAadhar(aadhar);
        if(user.isPresent()) {
            User presentUser = user.get();
            return UserResponse.builder()
                    .firstName(presentUser.getFirstName())
                    .lastName(presentUser.getLastName())
                    .aadhar(presentUser.getAadhar())
                    .phoneNumber(presentUser.getPhoneNumber())
                    .dateOfBirth(presentUser.getDateOfBirth())
                    .age(presentUser.getAge())
                    .godFatherName(presentUser.getGodFatherName())
                    .godFatherNumber(presentUser.getGodFatherNumber())
                    .build();
        }
        return UserResponse.builder()
                .message("No User Found with aadhar: " + aadhar)
                .build();
    }

    public UserResponse addUser(AddUserRequest addUserRequest) {
        var user = User.builder()
                .firstName(addUserRequest.getFirstName())
                .lastName(addUserRequest.getLastName())
                .aadhar(addUserRequest.getAadhar())
                .phoneNumber(addUserRequest.getPhoneNumber())
                .dateOfBirth(addUserRequest.getDateOfBirth())
                .godFatherNumber(addUserRequest.getGodFatherNumber())
                .godFatherName(addUserRequest.getGodFatherName())
                .build();
        user.setAge();

        try {
            User userObject = userRepository.save(user);
            return UserResponse.builder()
                    .firstName(userObject.getFirstName())
                    .lastName(userObject.getLastName())
                    .aadhar(userObject.getAadhar())
                    .phoneNumber(userObject.getPhoneNumber())
                    .dateOfBirth(userObject.getDateOfBirth())
                    .godFatherNumber(userObject.getGodFatherNumber())
                    .godFatherName(userObject.getGodFatherName())
                    .age(userObject.getAge())
                    .build();
        } catch (Exception exception) {
            System.out.println("exception = " + exception.toString());
            return UserResponse.builder()
                    .message("No User Found with aadhar: " + addUserRequest.getAadhar())
                    .build();
        }
    }
}
