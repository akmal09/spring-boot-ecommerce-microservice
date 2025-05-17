package com.project.products;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class ProductServiceApplication {

	public static void main(String[] args) {
		System.out.println("Product Service Initiate to Run 1.1");
		SpringApplication.run(ProductServiceApplication.class, args);
	}

}
