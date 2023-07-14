package com.had.backend.hospital.model;

import com.had.backend.hospital.config.MessagingConfig;
import lombok.*;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataMessageEncrypted {
    private String sourceId;
    private String destinationId;
    private String doctorId;
    private byte[] department;
    private byte[] address;
    private byte[] diagnosis;
    private byte[] prescription;
    private byte[] aadhar;
    private byte[] patientFirstName;
    private byte[] patientLastName;


    public DataMessage decryptRecord() {
        return DataMessage.builder()
                .sourceId(sourceId)
                .destinationId(destinationId)
                .department(decryptMessage(department))
                .address(decryptMessage(address))
                .diagnosis(decryptMessage(diagnosis))
                .prescription(decryptMessage(prescription))
                .aadhar(decryptMessage(aadhar))
                .patientFirstName(decryptMessage(patientFirstName))
                .patientLastName(decryptMessage(patientLastName))
                .doctorId(doctorId)
                .build();
    }

    @SneakyThrows
    String decryptMessage(byte[] encryptedData) {
        String path = new File(".").getCanonicalPath() + "/./";
        KeyPair keyPair = loadKeyPair(path, "RSA", MessagingConfig.hospitalId);
        PrivateKey privateKey = keyPair.getPrivate();
//        System.out.println("privateKey = " + privateKey);

        Cipher cipher = Cipher.getInstance("RSA");

        // decrypt the data
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedData = cipher.doFinal(encryptedData);

        // convert the decrypted data back to a string
        return new String(decryptedData);
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

