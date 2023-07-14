package com.had.backend.hospital;

import com.had.backend.hospital.entity.PatientRecord;
import com.had.backend.hospital.repository.PatientRecordRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

@SpringBootTest
class HospitalApplicationTests {



	@Test
	void contextLoads() throws IOException {
//		System.out.println(MessagingConfig.RECEIVE_DATA_QUEUE);
		System.out.println("System.getProperty(\"user.dir\") = " + new File(".").getCanonicalPath());
	}

	@Autowired
	private PatientRecordRepository patientRecordRepository;

	@Test
	void getAllRecords() {
		List<PatientRecord> records = patientRecordRepository.findAll();
		System.out.println("records = " + records);
	}

	@SneakyThrows
	void saveKeyPair(String path, KeyPair keyPair, String entity) {
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();

		// Store Public Key.
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
				publicKey.getEncoded());
		FileOutputStream fos = new FileOutputStream(path + "/" + entity + "_public.key");
		fos.write(x509EncodedKeySpec.getEncoded());
		fos.close();

		// Store Private Key.
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
				privateKey.getEncoded());
		fos = new FileOutputStream(path + "/" + entity + "_private.key");
		fos.write(pkcs8EncodedKeySpec.getEncoded());
		fos.close();
	}

	@SneakyThrows
	public KeyPair loadKeyPair(String path, String algorithm, String entity)
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

	@SneakyThrows
	@Test
	void testRSA() {
		//https://snipplr.com/view/18368/saveload--private-and-public-key-tofrom-a-file
		//https://aws.plainenglish.io/how-to-use-rsa-asymmetric-encryption-and-decryption-in-java-a4d7c3ad8236

		// data to be encrypted
		String data = "This is the data to be encrypted";
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(2048); // initialize with key size 2048
			KeyPair keyPair = keyGen.generateKeyPair();
			PrivateKey privateKey = keyPair.getPrivate();
			System.out.println("privateKey = " + privateKey);
			PublicKey publicKey = keyPair.getPublic();
			System.out.println("publicKey = " + publicKey);

			//save keypair
//			saveKeyPair("/Users/rain/Documents/ehrc/ehrc-backend", keyPair, "P");

			//load keypair
			keyPair = loadKeyPair("/Users/rain/Documents/ehrc/ehrc-backend", "RSA", "H1");
			PrivateKey privateKey1 = keyPair.getPrivate();
			System.out.println("privateKey = " + privateKey);
			PublicKey publicKey1 = keyPair.getPublic();
			System.out.println("publicKey = " + publicKey);

			// encrypt the data
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] encryptedData = cipher.doFinal(data.getBytes());


			// decrypt the data
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] decryptedData = cipher.doFinal(encryptedData);

			// convert the decrypted data back to a string
			String decryptedString = new String(decryptedData);

			assert (decryptedString.equals(data));

			System.out.println("Decrypted data: " + decryptedString);
		}
		catch (BadPaddingException e) {
			System.out.println("Something is wrong: BadPaddingException");
		}
		catch (IllegalBlockSizeException e) {
			System.out.println("Something is wrong: IllegalBlockSizeException");
		}
		catch (InvalidKeyException e) {
			System.out.println("Something is wrong: InvalidKeyException");
		}
		catch (NoSuchPaddingException e) {
			System.out.println("Something is wrong: NoSuchPaddingException");
		}
		catch(NoSuchAlgorithmException e) {
			System.out.println("Something is wrong: NoSuchAlgorithmException");
		}
	}
}
