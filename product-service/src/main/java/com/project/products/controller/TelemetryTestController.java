package com.project.products.controller;

import com.project.products.model.Product;
import com.project.products.services.ProductService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/telemetry-test")
public class TelemetryTestController {

    private final ProductService productService;
    private final MeterRegistry meterRegistry;
    private final Random random = new Random();

    public TelemetryTestController(ProductService productService, MeterRegistry meterRegistry) {
        this.productService = productService;
        this.meterRegistry = meterRegistry;
    }

    @GetMapping("/generate-traffic")
    public String generateTraffic(@RequestParam(defaultValue = "10") int requests) {
        for (int i = 0; i < requests; i++) {
            try {
                // Mix of different operations
                switch (i % 4) {
                    case 0:
                        productService.getAllProducts();
                        break;
                    case 1:
                        productService.getProductById((long) (i % 5 + 1));
                        break;
                    case 2:
                        productService.product();
                        break;
                    case 3:
                        // Simulate some delay
                        Thread.sleep(random.nextInt(100));
                        productService.getAllProducts();
                        break;
                }
                
                // Random delay between requests
                if (random.nextInt(100) < 30) {
                    Thread.sleep(random.nextInt(50));
                }
                
            } catch (Exception e) {
                meterRegistry.counter("telemetry.test.errors").increment();
            }
        }
        
        meterRegistry.counter("telemetry.test.completed").increment();
        return "Generated " + requests + " telemetry events. Check your Datadog dashboard!";
    }

    @GetMapping("/product-stats")
    public String getProductStats() {
        List<Product> products = productService.getAllProducts();
        meterRegistry.gauge("telemetry.test.current.product.count", products.size());
        return "Current product count: " + products.size() + " (metric sent to Datadog)";
    }
}