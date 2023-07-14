package com.had.backend.hospital.service;

import com.had.backend.hospital.config.TwilioConfig;
import com.had.backend.hospital.entity.User;
import com.had.backend.hospital.model.CheckUserDemographic;
import com.had.backend.hospital.model.RequestConsentObject;
import com.had.backend.hospital.model.UserDemographicResponse;
import com.had.backend.hospital.model.VerifyOTP;
import com.had.backend.hospital.repository.UserRepository;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.apache.http.client.methods.RequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;
import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@Service
public class OTPService {
    @Autowired
    private TwilioConfig twilioConfig;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConsentService consentService;

    Map<String, String> OTPMap = new HashMap<>();
    Map<String, RequestConsentObject> ConsentRequestMap = new HashMap<>();
    @Autowired
    private WebClient webClient;

    // Generate 6 digit OTP
    private String generateOTP() {
        return new DecimalFormat("000000").format(new Random().nextInt(999999));
    }
    private Boolean validateUser(String aadhar) { return userRepository.existsByAadhar(aadhar); }


    public void getConsentRequestPatientOTP(RequestConsentObject requestConsentObject) {
        // TODO - somehow (using demographics) get the phone number to send otp, hardcoding for now
        CheckUserDemographic checkUserDemographic = new CheckUserDemographic();
        checkUserDemographic.setAadhar(requestConsentObject.getPatientId());
        UserDemographicResponse userDemographicResponse = this.webClient
                .post()
                .uri("http://127.0.0.1:7001/user-demographic/find-user")
                .attributes(clientRegistrationId("hospital-client-client-credentials"))
                .body(Mono.just(checkUserDemographic), CheckUserDemographic.class)
                .retrieve()
                .bodyToMono(UserDemographicResponse.class)
                .block();
        String patientPhoneHardcoded = userDemographicResponse.getPhoneNumber();
        System.out.println("patientPhoneHardcoded = " + patientPhoneHardcoded);

//        String patientPhoneHardcoded = "+918840087436";
        getConsentRequestOTP(requestConsentObject, patientPhoneHardcoded);
    }

    public void getConsentRequestGuardianOTP(RequestConsentObject requestConsentObject) {
//        // TODO - somehow (using demographics) get the phone number to send otp, hardcoding for now
        CheckUserDemographic checkUserDemographic = new CheckUserDemographic();
        checkUserDemographic.setAadhar(requestConsentObject.getPatientId());
        UserDemographicResponse userDemographicResponse = this.webClient
                .post()
                .uri("http://127.0.0.1:7001/user-demographic/find-user")
                .attributes(clientRegistrationId("hospital-client-client-credentials"))
                .body(Mono.just(checkUserDemographic), CheckUserDemographic.class)
                .retrieve()
                .bodyToMono(UserDemographicResponse.class)
                .block();
        String guardianPhoneHardcoded = userDemographicResponse.getGodFatherNumber();

//        String guardianPhoneHardcoded = "+916969696969";

        getConsentRequestOTP(requestConsentObject, guardianPhoneHardcoded);
    }

    public void getConsentRequestOTP(RequestConsentObject requestConsentObject, String phoneNumber) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        PhoneNumber toPhoneNumber = new PhoneNumber(phoneNumber);
        PhoneNumber fromPhoneNumber = new PhoneNumber(twilioConfig.getTrialNumber());

        String OTP = generateOTP();
        String body = "OTP for consent approval is " + OTP + ".\n\nThe record requesting hospital is " + requestConsentObject.getRecordRequesterHospital()
                + ", the record sending hospital is " + requestConsentObject.getRecordSenderHospital()
                + ", the department for the requested data is " + requestConsentObject.getDepartment()
                + ". The consent will be valid till " + requestConsentObject.getEndTime()
                + ". Please share this code with doctor if you want to approve this consent."
                + " In case you want to change the details mentioned in the consent, ask the doctor to modify the request."
                + " We strongly recommend using the patient app.";

        Message.creator(toPhoneNumber, fromPhoneNumber, body).create();
        System.out.println("body = " + body);


        OTPMap.put(requestConsentObject.getPatientId(), OTP);
        ConsentRequestMap.put(requestConsentObject.getPatientId(), requestConsentObject);
    }

    public boolean verifyConsentRequestOTP(VerifyOTP verifyOTP) {
        String otp = verifyOTP.getOtp();
        String patientId = verifyOTP.getPatientId();

        if(OTPMap.containsKey(patientId) && otp.equals(OTPMap.get(patientId))) {
            consentService.approveConsentByOTP(ConsentRequestMap.get(patientId));
            OTPMap.remove(patientId);
            ConsentRequestMap.remove(patientId);
            return true;
        }

        return false;
    }


}
