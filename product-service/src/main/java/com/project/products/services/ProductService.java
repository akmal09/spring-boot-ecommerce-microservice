package com.project.products.services;

import com.project.config.ResponseObject;
import com.project.products.model.Product;
import com.project.products.repository.ProductRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import datadog.trace.api.Trace;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final MeterRegistry meterRegistry;
    private final Counter dbOperationCounter;

    public ProductService(ProductRepository productRepository, MeterRegistry meterRegistry) {
        this.productRepository = productRepository;
        this.meterRegistry = meterRegistry;
        this.dbOperationCounter = Counter.builder("product.db.operations.total")
                .description("Total database operations")
                .register(meterRegistry);
    }

    @Trace(operationName = "db.product.findAll", resourceName = "ProductService.getAllProducts")
    public List<Product> getAllProducts() {
        Timer.Sample sample = Timer.start(meterRegistry);
        dbOperationCounter.increment();
        
        try {
            List<Product> products = productRepository.findAll();
            meterRegistry.counter("product.db.operations.success", "operation", "findAll").increment();
            meterRegistry.gauge("product.db.result.size", products.size());
            return products;
        } catch (Exception e) {
            meterRegistry.counter("product.db.operations.error", "operation", "findAll").increment();
            throw e;
        } finally {
            sample.stop(Timer.builder("product.response.time")
                    .tag("endpoint", "getAllProducts")
                    .register(meterRegistry));
        }
    }

    @Trace(operationName = "db.product.save", resourceName = "ProductService.createProduct")
    public Product createProduct(Product product) {
        Timer.Sample sample = Timer.start(meterRegistry);
        dbOperationCounter.increment();
        
        try {
            Product savedProduct = productRepository.save(product);
            meterRegistry.counter("product.db.operations.success", "operation", "save").increment();
            meterRegistry.counter("product.created.success").increment();
            return savedProduct;
        } catch (Exception e) {
            meterRegistry.counter("product.db.operations.error", "operation", "save").increment();
            meterRegistry.counter("product.created.error").increment();
            throw e;
        } finally {
            sample.stop(Timer.builder("product.response.time")
                    .tag("endpoint", "getAllProducts")
                    .register(meterRegistry));
        }
    }

    @Trace(operationName = "db.product.findById", resourceName = "ProductService.getProductById")
    public Product getProductById(Long id) {
        Timer.Sample sample = Timer.start(meterRegistry);
        dbOperationCounter.increment();
        
        try {
            Product product = productRepository.findById(id).orElse(null);
            if (product != null) {
                meterRegistry.counter("product.db.operations.success", "operation", "findById").increment();
                meterRegistry.counter("product.found").increment();
            } else {
                meterRegistry.counter("product.db.operations.success", "operation", "findById").increment();
                meterRegistry.counter("product.notfound").increment();
            }
            return product;
        } catch (Exception e) {
            meterRegistry.counter("product.db.operations.error", "operation", "findById").increment();
            throw e;
        } finally {
            sample.stop(Timer.builder("product.response.time")
                    .tag("endpoint", "getAllProducts")
                    .register(meterRegistry));
        }
    }

    @Trace(operationName = "service.product.response.object", resourceName = "ProductService.product")
    public ResponseObject product(){
        Timer.Sample sample = Timer.start(meterRegistry);
        dbOperationCounter.increment();
        
        try {
            ResponseObject responseObject = new ResponseObject();
            List<Product> products = productRepository.findAll();
            
            responseObject.setStatus("success");
            responseObject.setData(products);
            
            meterRegistry.counter("product.db.operations.success", "operation", "findAllForResponse").increment();
            meterRegistry.counter("product.response.object.created").increment();
            meterRegistry.gauge("product.response.object.size", products.size());
            
            return responseObject;
        } catch (Exception e) {
            meterRegistry.counter("product.db.operations.error", "operation", "findAllForResponse").increment();
            meterRegistry.counter("product.response.object.error").increment();
            throw e;
        } finally {
            sample.stop(Timer.builder("product.response.time")
                    .tag("endpoint", "getAllProducts")
                    .register(meterRegistry));
        }
    }
}
