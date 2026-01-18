package com.project.operations.controller;

import java.util.ArrayList;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.config.ResponseObject;
import com.project.operations.services.OperationService;

@RestController
@RequestMapping("/external/product")
public class OperationsController {
    private final OperationService operationService;

    public OperationsController(OperationService operationService){
        this.operationService = operationService;
    }

    @GetMapping
    public ResponseEntity<?> getProductDirect(){
        try{
            ResponseObject getProducts = operationService.getProducts();
            ResponseEntity<?> response = new ResponseEntity<>(getProducts, HttpStatusCode.valueOf(200));
            return response;
        }catch(Exception e){
            ResponseEntity<?> response = new ResponseEntity<>(new ArrayList<>(), HttpStatusCode.valueOf(500));

            return response;
        }
    }

    // # Add controller in every operations service

    // # add open telemetry agent in every controller and service
}
