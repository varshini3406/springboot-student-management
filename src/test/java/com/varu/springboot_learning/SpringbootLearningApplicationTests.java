package com.varu.springboot_learning;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@ActiveProfiles("test")
@SpringBootTest("spring.flyway.enabled=false")
class SpringbootLearningApplicationTests {

	@Test
	void contextLoads() {
	}

}
