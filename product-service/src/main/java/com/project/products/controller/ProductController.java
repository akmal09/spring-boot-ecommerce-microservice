package com.project.products.controller;

import com.project.products.model.Product;
import com.project.products.services.ProductService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import datadog.trace.api.Trace;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/api/products")
public class ProductController {
    private final ProductService productService;
    private final MeterRegistry meterRegistry;
    private final Counter productRequestCounter;
    private final Counter productCreatedCounter;

    public ProductController(ProductService productService, MeterRegistry meterRegistry) {
        this.productService = productService;
        this.meterRegistry = meterRegistry;
        this.productRequestCounter = Counter.builder("product.requests.total")
                .description("Total product requests")
                .register(meterRegistry);
        this.productCreatedCounter = Counter.builder("product.created.total")
                .description("Total products created")
                .register(meterRegistry);
    }

    @GetMapping
    @Trace(operationName = "get.all.products", resourceName = "ProductController.getAllProducts")
    public ResponseEntity<List<Product>> getAllProducts() {
        Timer.Sample sample = Timer.start(meterRegistry);
        productRequestCounter.increment();
        
        try {
            List<Product> products = productService.getAllProducts();
            meterRegistry.counter("product.requests.success", "endpoint", "getAllProducts").increment();
            meterRegistry.gauge("product.count", products.size());
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            meterRegistry.counter("product.requests.error", "endpoint", "getAllProducts").increment();
            throw e;
        } finally {
            sample.stop(Timer.builder("product.response.time")
                    .tag("endpoint", "getAllProducts")
                    .register(meterRegistry));
        }
    }

    @GetMapping("/{id}")
    @Trace(operationName = "get.product.by.id", resourceName = "ProductController.getProduct")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        Timer.Sample sample = Timer.start(meterRegistry);
        productRequestCounter.increment();
        
        try {
            Product product = productService.getProductById(id);
            if (product != null) {
                meterRegistry.counter("product.requests.success", "endpoint", "getProduct").increment();
                return ResponseEntity.ok(product);
            } else {
                meterRegistry.counter("product.requests.notfound", "endpoint", "getProduct").increment();
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            meterRegistry.counter("product.requests.error", "endpoint", "getProduct").increment();
            throw e;
        } finally {
            sample.stop(Timer.builder("product.response.time")
                    .tag("endpoint", "getProduct")
                    .register(meterRegistry));
        }
    }

    @PostMapping
    @Trace(operationName = "create.product", resourceName = "ProductController.createProduct")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Timer.Sample sample = Timer.start(meterRegistry);
        productRequestCounter.increment();
        
        try {
            Product createdProduct = productService.createProduct(product);
            productCreatedCounter.increment();
            meterRegistry.counter("product.requests.success", "endpoint", "createProduct").increment();
            return ResponseEntity.ok(createdProduct);
        } catch (Exception e) {
            meterRegistry.counter("product.requests.error", "endpoint", "createProduct").increment();
            throw e;
        } finally {
            sample.stop(Timer.builder("product.response.time")
                    .tag("endpoint", "createProduct")
                    .register(meterRegistry));
        }
    }

    @GetMapping("/withresponseObject")
    @Trace(operationName = "get.all.products.response.object", resourceName = "ProductController.getAll")
    public ResponseEntity<?> getAll() {
        Timer.Sample sample = Timer.start(meterRegistry);
        productRequestCounter.increment();
        
        try {
            Object response = productService.product();
            meterRegistry.counter("product.requests.success", "endpoint", "getAll").increment();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            meterRegistry.counter("product.requests.error", "endpoint", "getAll").increment();
            throw e;
        } finally {
            sample.stop(Timer.builder("product.response.time")
                    .tag("endpoint", "getAll")
                    .register(meterRegistry));
        }
    }

}
