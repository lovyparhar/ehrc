package com.had.backend.hospital.model;

import lombok.*;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataMessage {
    private String sourceId;
    private String destinationId;
    private String department;
    private String address;
    private String diagnosis;
    private String prescription;
    private String aadhar;
    private String doctorId;
    private String patientFirstName;
    private String patientLastName;

    @SneakyThrows
    public DataMessageEncrypted encrypt(DataMessage dataMessage, String entity) {
        return DataMessageEncrypted.builder()
                .sourceId(dataMessage.getSourceId())
                .destinationId(dataMessage.getDestinationId())
                .department(encryptMessage(dataMessage.getDepartment(), entity))
                .address(encryptMessage(dataMessage.getAddress(), entity))
                .diagnosis(encryptMessage(dataMessage.getDiagnosis(), entity))
                .prescription(encryptMessage(dataMessage.getPrescription(), entity))
                .aadhar(encryptMessage(dataMessage.getAadhar(), entity))
                .doctorId(dataMessage.getDoctorId())
                .build();
    }

    @SneakyThrows
    byte[] encryptMessage(String data, String entity) {
        String path = new File(".").getCanonicalPath() + "/./";
        KeyPair keyPair = loadKeyPair(path, "RSA", entity);
        PublicKey publicKey = keyPair.getPublic();
        System.out.println("publicKey = " + publicKey);

        // encrypt the data
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data.getBytes());
    }

    @SneakyThrows
    private KeyPair loadKeyPair(String path, String algorithm, String entity)
            throws IOException, NoSuchAlgorithmException,
            InvalidKeySpecException {
        // Read Public Key.
        File filePublicKey = new File(path + "/" + entity + "_public.key");
        FileInputStream fis = new FileInputStream(path + "/" + entity + "_public.key");
        byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
        fis.read(encodedPublicKey);
        fis.close();

        // Read Private Key.
        File filePrivateKey = new File(path + "/" + entity + "_private.key");
        fis = new FileInputStream(path + "/" + entity + "_private.key");
        byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
        fis.read(encodedPrivateKey);
        fis.close();

        // Generate KeyPair.
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
                encodedPublicKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        return new KeyPair(publicKey, privateKey);
    }
}
