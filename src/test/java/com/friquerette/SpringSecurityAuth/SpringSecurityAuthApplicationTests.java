package com.friquerette.SpringSecurityAuth;

import com.friquerette.SpringSecurityAuth.controller.LoginController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class SpringSecurityAuthApplicationTests {
	@Autowired
	private LoginController loginController;

	@Test
	void contextLoads() {
		Assertions.assertThat(loginController).isNotNull();
	}

}
