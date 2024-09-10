package com.pard.root;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class RootBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(RootBeApplication.class, args);
	}

}
