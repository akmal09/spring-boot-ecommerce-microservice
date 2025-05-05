package com.project.ecommerce;

import com.project.products.ProductServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = ProductServiceApplication.class)
class ProductServiceApplicationTests {

	@Test
	void contextLoads() {
		assertEquals(true,true);
	}

}
