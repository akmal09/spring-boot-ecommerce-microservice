package com.project.operations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OperationsServiceApplication {

	public static void main(String[] args) {
		System.out.println("Operations service run");
		SpringApplication.run(OperationsServiceApplication.class, args);
	}

}
