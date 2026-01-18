package com.project.products.controller;

import com.project.products.services.ProductService;

import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class HealthController {
    private final ProductService productService;

    public HealthController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<?> checkHealth() {
        Logger log = org.slf4j.LoggerFactory.getLogger(HealthController.class);
        log.info("product service is up and running");
        return ResponseEntity.ok("product service is up and running");
    }

    

}
