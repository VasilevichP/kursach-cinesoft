package com.example.Cinesoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CinesoftApplication {
	public static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		 context = SpringApplication.run(CinesoftApplication.class, args);
	}

}
