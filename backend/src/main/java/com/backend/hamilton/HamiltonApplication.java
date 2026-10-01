package com.backend.hamilton;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan("com.backend.hamilton.configuration.properties")
@SpringBootApplication
public class HamiltonApplication {

	public static void main(String[] args) {
		SpringApplication.run(HamiltonApplication.class, args);
	}

}
