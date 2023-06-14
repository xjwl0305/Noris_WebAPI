package com.project.noris;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan("com.project.noris.entity")
public class NorisApplication {

	public static void main(String[] args) {
		SpringApplication.run(NorisApplication.class, args);
	}

}
