package com.had.backend.hospital;

import com.had.backend.hospital.config.MessagingConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest
class PatientApplicationTests {

	@Test
	void contextLoads() {
		System.out.println(MessagingConfig.RECEIVE_DATA_QUEUE);
	}

}
