package com.project.products.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/stress")
public class StressTestController {

    @Autowired
    private MeterRegistry meterRegistry;
    
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

    @GetMapping("/cpu")
    public String cpuStress(@RequestParam(defaultValue = "1000") int iterations) {
        Timer.Sample sample = Timer.start(meterRegistry);
        stressTestCounter.increment();
        
        try {
            // CPU intensive task
            long result = 0;
            for (int i = 0; i < iterations * 10000; i++) {
                result += Math.sqrt(i) * Math.sin(i);
            }
            return "CPU stress test completed. Result: " + result;
        } finally {
            sample.stop(stressTestTimer);
        }
    }

    @GetMapping("/memory")
    public String memoryStress(@RequestParam(defaultValue = "100") int sizeMB) {
        Timer.Sample sample = Timer.start(meterRegistry);
        stressTestCounter.increment();
        
        try {
            // Memory allocation stress
            byte[][] arrays = new byte[sizeMB][];
            for (int i = 0; i < sizeMB; i++) {
                arrays[i] = new byte[1024 * 1024]; // 1MB each
                // Fill with random data
                random.nextBytes(arrays[i]);
            }
            return "Memory stress test completed. Allocated: " + sizeMB + "MB";
        } finally {
            sample.stop(stressTestTimer);
        }
    }

    @GetMapping("/delay")
    public String delayStress(@RequestParam(defaultValue = "1000") int delayMs) {
        Timer.Sample sample = Timer.start(meterRegistry);
        stressTestCounter.increment();
        
        try {
            Thread.sleep(delayMs);
            return "Delay stress test completed. Delayed: " + delayMs + "ms";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Delay interrupted";
        } finally {
            sample.stop(stressTestTimer);
        }
    }

    @GetMapping("/error")
    public String errorStress(@RequestParam(defaultValue = "50") int errorRate) {
        stressTestCounter.increment();
        
        if (random.nextInt(100) < errorRate) {
            meterRegistry.counter("stress.test.errors").increment();
            throw new RuntimeException("Simulated error for stress testing");
        }
        
        return "No error this time. Error rate: " + errorRate + "%";
    }
}