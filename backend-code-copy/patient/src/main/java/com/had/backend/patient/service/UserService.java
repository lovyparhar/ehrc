package com.had.backend.patient.service;

import com.had.backend.patient.entity.User;
import com.had.backend.patient.model.UpdatePatientDetailsRequest;
import com.had.backend.patient.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User findUserByAadhar(String aadhar) {
        Optional<User> user = userRepository.findByAadhar(aadhar);
        return user.orElse(null);
    }

    public Boolean updatePatientDetails(UpdatePatientDetailsRequest updatePatientDetailsRequest) {
        try {
            String aadhar = updatePatientDetailsRequest.getAadhar();
            String email = updatePatientDetailsRequest.getEmail();
            String phoneNumber = updatePatientDetailsRequest.getPhoneNumber();

            if(email != null) userRepository.findByAadhar(aadhar).ifPresent(user -> {
                user.setEmail(email);
                userRepository.save(user);
            });
            if(phoneNumber != null) userRepository.findByAadhar(aadhar).ifPresent(user -> {
                user.setPhoneNumber(phoneNumber);
                userRepository.save(user);
            });
        }
        catch (Exception exception) {
            System.out.println("Exception Occurred in Updating Patient Details: " + exception.getMessage());
            return false;
        }
        return true;
    }
}
