package com.had.backend.patient.service;

import com.had.backend.patient.config.TwilioConfig;
import com.had.backend.patient.model.SmsRequest;
import com.had.backend.patient.model.updatePasswordRequest;
import com.had.backend.patient.model.verifyOTPRequest;
import com.had.backend.patient.repository.UserRepository;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import static java.util.concurrent.TimeUnit.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

@Service
public class OTPService {
    @Autowired
    private TwilioConfig twilioConfig;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    Map<String, String> OTPDatabase = new HashMap<>();
    Map<String, LocalDateTime> OTPExpiry = new HashMap<>();

    public Boolean sendOTP(SmsRequest smsRequest) {
        //validate Phone Number
        PhoneNumber to = new PhoneNumber(smsRequest.getPhoneNumber());
        String aadhar = smsRequest.getAadhar();

        Boolean userExists = validateUser(aadhar);
        if(!userExists) {
            return false;
        }

        PhoneNumber from = new PhoneNumber(twilioConfig.getTrialNumber());
        String OTP = generateOTP();
        String body = "OTP: ##" + OTP + "## Use this for the Operation.";

        //INFO: Uncomment this to invoke TWILIO SMS Services
        //Message.creator(to, from, body).create();
        System.out.println("body = " + body);
        OTPDatabase.put(smsRequest.getPhoneNumber(), OTP);
        OTPExpiry.put(OTP, LocalDateTime.now());

        return true;
    }

    public Boolean genericSendOTP(SmsRequest smsRequest) {
        //validate Phone Number
        PhoneNumber to = new PhoneNumber(smsRequest.getPhoneNumber());

        PhoneNumber from = new PhoneNumber(twilioConfig.getTrialNumber());
        String OTP = generateOTP();
        String body = "OTP: ##" + OTP + "## Use this for the Operation.";

        //Message.creator(to, from, body).create();
        System.out.println("body = " + body);
        OTPDatabase.put(smsRequest.getPhoneNumber(), OTP);
        OTPExpiry.put(OTP, LocalDateTime.now());

        return true;
    }

    public Boolean genericvVerifyOTP(verifyOTPRequest verifyRequest) {
        String OTP = verifyRequest.getOTP();
        String phoneNumber = verifyRequest.getPhoneNumber();

        if(OTP.isEmpty() || phoneNumber.isEmpty()) return false;

        //TODO: DECRYPT PASSWORD HERE
        if(OTPDatabase.containsKey(phoneNumber) && OTP.equals(OTPDatabase.get(phoneNumber))) {
            long minutes = ChronoUnit.MINUTES.between(OTPExpiry.get(OTP), LocalDateTime.now());
            if(minutes > 10){
                OTPDatabase.remove(phoneNumber);
                OTPExpiry.remove(OTP);
                return false;
            }
            OTPDatabase.remove(phoneNumber);
            OTPExpiry.remove(OTP);
            return true;
            }

        return false;
    }

    public Boolean verifyOTPupdatePassword(updatePasswordRequest verifyRequest) {
        String OTP = verifyRequest.getOTP();
        String phoneNumber = verifyRequest.getPhoneNumber();
        String password = verifyRequest.getPassword();
        String aadhar = verifyRequest.getAadhar();

        if(OTP.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || aadhar.isEmpty()) return false;

        System.out.println("MAI challeya");
        //TODO: DECRYPT PASSWORD HERE
        if(OTPDatabase.containsKey(phoneNumber) && OTP.equals(OTPDatabase.get(phoneNumber))) {
            long minutes = ChronoUnit.MINUTES.between(OTPExpiry.get(OTP), LocalDateTime.now());
            if(minutes > 10){
                OTPDatabase.remove(phoneNumber);
                OTPExpiry.remove(OTP);
                return false;
            }
            OTPDatabase.remove(phoneNumber);
            OTPExpiry.remove(OTP);
            //If OTP exists, then user exists always!
            userRepository.findByAadhar(aadhar).ifPresent(user1 -> {
//                System.out.println("user1.getPassword() = " + user1.getPassword());
                user1.setPassword(passwordEncoder.encode(password));
                userRepository.save(user1);
            });

//            todo: check whether password changes
//            Optional<User> user = userRepository.findByAadhar(aadhar);
//            user.ifPresent(value -> System.out.println("user.get().getPassword() = " + value.getPassword()));
            
            return true;
        }
        return false;
    }

    // Generate 6 digit OTP
    private String generateOTP() {
        return new DecimalFormat("000000").format(new Random().nextInt(999999));
    }
    private Boolean validateUser(String aadhar) { return userRepository.existsByAadhar(aadhar); }
}
