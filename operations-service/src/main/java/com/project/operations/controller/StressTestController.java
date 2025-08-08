package com.project.operations.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Random;

@RestController
@RequestMapping("/stress")
public class StressTestController {

    @Autowired
    private MeterRegistry meterRegistry;
    
    @Autowired
    private RestTemplate restTemplate;
    
    private final Counter stressTestCounter;
    private final Timer stressTestTimer;
    private final Random random = new Random();

    public StressTestController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.stressTestCounter = Counter.builder("stress.test.requests")
                .description("Number of stress test requests")
                .register(meterRegistry);
        this.stressTestTimer = Timer.builder("stress.test.duration")
                .description("Duration of stress test operations")
                .register(meterRegistry);
    }

    @GetMapping("/checkout")
    public String checkoutStress(@RequestParam(defaultValue = "10") int orders) {
        Timer.Sample sample = Timer.start(meterRegistry);
        stressTestCounter.increment();
        
        try {
            // Simulate multiple checkout operations
            for (int i = 0; i < orders; i++) {
                // Simulate processing time
                Thread.sleep(random.nextInt(100) + 50);
                
                // Call product service (simulate dependency)
                try {
                    String response = restTemplate.getForObject(
                        "http://product-service/products/health", String.class);
                } catch (Exception e) {
                    meterRegistry.counter("stress.test.dependency.errors").increment();
                }
            }
            return "Checkout stress test completed. Processed: " + orders + " orders";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Checkout interrupted";
        } finally {
            sample.stop(stressTestTimer);
        }
    }

    @GetMapping("/payment")
    public String paymentStress(@RequestParam(defaultValue = "5") int payments) {
        Timer.Sample sample = Timer.start(meterRegistry);
        stressTestCounter.increment();
        
        try {
            for (int i = 0; i < payments; i++) {
                // Simulate payment processing delay
                Thread.sleep(random.nextInt(200) + 100);
                
                // Simulate payment failure rate
                if (random.nextInt(100) < 10) { // 10% failure rate
                    meterRegistry.counter("stress.test.payment.failures").increment();
                }
            }
            return "Payment stress test completed. Processed: " + payments + " payments";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Payment interrupted";
        } finally {
            sample.stop(stressTestTimer);
        }
    }

    @GetMapping("/load")
    public String loadStress(@RequestParam(defaultValue = "100") int requests) {
        Timer.Sample sample = Timer.start(meterRegistry);
        stressTestCounter.increment();
        
        try {
            // Simulate high load
            for (int i = 0; i < requests; i++) {
                // CPU work
                double result = Math.pow(random.nextDouble(), 2);
                
                // Memory allocation
                byte[] data = new byte[1024 * 10]; // 10KB
                random.nextBytes(data);
                
                if (i % 10 == 0) {
                    Thread.sleep(1); // Brief pause every 10 iterations
                }
            }
            return "Load stress test completed. Processed: " + requests + " operations";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Load test interrupted";
        } finally {
            sample.stop(stressTestTimer);
        }
    }
}