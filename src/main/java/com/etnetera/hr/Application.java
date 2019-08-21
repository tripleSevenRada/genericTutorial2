package com.etnetera.hr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring Boot application class.
 * 
 * @author Etnetera
 *
 */
@SpringBootApplication
public class Application {

	// common application.properties
	// https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html
	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

}
