package com.project.operations.controller;

import java.util.ArrayList;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.config.ResponseObject;
import com.project.dto.AddCheckout;
import com.project.operations.services.OperationService;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/external/product")
public class OperationsController {
    private final OperationService operationService;
    private final Tracer tracer;

    public OperationsController(OperationService operationService, Tracer tracer){
        this.operationService = operationService;
        this.tracer = tracer;
    }

    @GetMapping
    public ResponseEntity<?> getProductDirect(){
        Span span = tracer.spanBuilder("get-products-direct")
                .setAttribute("controller", "OperationsController")
                .setAttribute("method", "getProductDirect")
                .startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            log.info("Fetching products directly");
            span.addEvent("Starting product fetch");
            
            ResponseObject getProducts = operationService.getProducts();
            
            span.setAttribute("response.status", getProducts.getStatus());
            span.addEvent("Product fetch completed");
            
            ResponseEntity<?> response = new ResponseEntity<>(getProducts, HttpStatusCode.valueOf(200));
            return response;
        } catch(Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            log.error("Error fetching products", e);
            
            ResponseEntity<?> response = new ResponseEntity<>(new ArrayList<>(), HttpStatusCode.valueOf(500));
            return response;
        } finally {
            span.end();
        }
    }

    @GetMapping("/checkout")
    public ResponseEntity<?> getCheckout(){
        Span span = tracer.spanBuilder("get-checkout")
                .setAttribute("controller", "OperationsController")
                .setAttribute("method", "getCheckout")
                .startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            log.info("Fetching checkout items");
            span.addEvent("Starting checkout fetch");
            
            ResponseObject checkoutItems = operationService.getCheckOut();
            
            span.setAttribute("response.status", checkoutItems.getStatus());
            span.addEvent("Checkout fetch completed");
            
            return new ResponseEntity<>(checkoutItems, HttpStatusCode.valueOf(200));
        } catch(Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            log.error("Error fetching checkout items", e);
            
            return new ResponseEntity<>(new ArrayList<>(), HttpStatusCode.valueOf(500));
        } finally {
            span.end();
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> addCheckout(@RequestBody List<AddCheckout> addedProducts){
        Span span = tracer.spanBuilder("add-checkout")
                .setAttribute("controller", "OperationsController")
                .setAttribute("method", "addCheckout")
                .setAttribute("products.count", addedProducts.size())
                .startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            log.info("Adding {} products to checkout", addedProducts.size());
            span.addEvent("Starting checkout addition");
            
            ResponseObject result = operationService.addCheckOut(addedProducts);
            
            span.setAttribute("response.status", result.getStatus());
            span.addEvent("Checkout addition completed");
            
            return new ResponseEntity<>(result, HttpStatusCode.valueOf(200));
        } catch(Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            log.error("Error adding checkout items", e);
            
            return new ResponseEntity<>(new ArrayList<>(), HttpStatusCode.valueOf(500));
        } finally {
            span.end();
        }
    }
}
