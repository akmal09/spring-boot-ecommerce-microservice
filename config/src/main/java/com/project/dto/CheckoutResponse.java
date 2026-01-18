package com.project.dto;

import java.util.List;

import lombok.Data;

@Data
public class CheckoutResponse {
        private List<CheckouttedProduct> listProduct;
}
