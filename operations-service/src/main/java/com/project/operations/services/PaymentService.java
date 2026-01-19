package com.project.operations.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.dto.PaymentRequest;
import com.project.dto.PaymentResponse;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentService {
    
    private final RestTemplate restTemplate;
    private final Tracer tracer;
    
    public PaymentService(RestTemplate restTemplate, Tracer tracer) {
        this.restTemplate = restTemplate;
        this.tracer = tracer;
    }
    
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        Span span = tracer.spanBuilder("payment-service.process-payment")
                .setAttribute("service", "PaymentService")
                .setAttribute("method", "processPayment")
                .setAttribute("payment.amount", paymentRequest.getAmount())
                .setAttribute("payment.currency", paymentRequest.getCurrency())
                .startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            log.info("Processing payment for amount: {} {}", 
                    paymentRequest.getAmount(), paymentRequest.getCurrency());
            
            span.addEvent("Starting payment processing");
            
            // Simulate external payment service call
            Span externalSpan = tracer.spanBuilder("external-payment-api-call")
                    .setAttribute("http.method", "POST")
                    .setAttribute("http.url", "http://payment-service/api/payments")
                    .startSpan();
            
            try (Scope externalScope = externalSpan.makeCurrent()) {
                // This would be your actual external service call
                // PaymentResponse response = restTemplate.postForObject(
                //     "http://payment-service/api/payments", 
                //     paymentRequest, 
                //     PaymentResponse.class
                // );
                
                // For demo purposes, creating a mock response
                Thread.sleep(100); // Simulate network delay
                
                PaymentResponse response = new PaymentResponse();
                response.setTransactionId("TXN-" + System.currentTimeMillis());
                response.setStatus("SUCCESS");
                response.setAmount(paymentRequest.getAmount());
                
                externalSpan.setAttribute("payment.transaction_id", response.getTransactionId());
                externalSpan.setAttribute("payment.status", response.getStatus());
                externalSpan.addEvent("Payment API call completed");
                
                span.addEvent("Payment processed successfully");
                log.info("Payment processed successfully with transaction ID: {}", 
                        response.getTransactionId());
                
                return response;
                
            } catch (Exception e) {
                externalSpan.recordException(e);
                externalSpan.setAttribute("error", true);
                throw new RuntimeException("Payment processing failed", e);
            } finally {
                externalSpan.end();
            }
            
        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            log.error("Error processing payment", e);
            throw e;
        } finally {
            span.end();
        }
    }
}
