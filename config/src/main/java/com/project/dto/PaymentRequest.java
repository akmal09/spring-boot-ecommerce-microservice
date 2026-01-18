package com.project.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PaymentRequest {
    private String orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private String cardNumber;
}
