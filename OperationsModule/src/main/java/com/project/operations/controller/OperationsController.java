package com.project.operations.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/external/product")
public class OperationsController {
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public void getProduct(){

        ResponseEntity<?> response= restTemplate.exchange(
                "http://product-service/internal/api/products/withresponseObject",
            HttpMethod.GET,
                null,
                Object.class
        );
        System.out.println(response.getBody());
    }
}
