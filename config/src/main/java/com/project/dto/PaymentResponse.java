package com.project.dto;

import java.math.BigDecimal;

import lombok.Data;
    
@Data
public class PaymentResponse {
    private String transactionId;
    private String orderId;
    private BigDecimal amount;
    private String status;
    private String paymentMethod;
}
