package com.project.operations.services;


import com.project.config.ResponseObject;
import com.project.dto.AddCheckout;
import com.project.dto.CheckoutResponse;
import com.project.dto.CheckouttedProduct;
import com.project.modelpsql.Checkout;
import com.project.modelpsql.Product;
import com.project.repository.CheckoutRepository;
import com.project.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;


@Service
public class OperationService {

    
        private final ProductRepository productRepository;
        private final CheckoutRepository checkoutRepository;

        public OperationService(ProductRepository productRepository, CheckoutRepository checkoutRepository){
            this.productRepository = productRepository;
            this.checkoutRepository = checkoutRepository;
        }

        public ResponseObject getProducts(){
            List<Product> products = productRepository.findAll();

            if(products.isEmpty()){
                return new ResponseObject("EMPTY",new ArrayList<>());
            }
            
            return new ResponseObject("SUCCESS", products);
        }

    
        public ResponseObject getCheckOut(){

            List<Checkout> getCheckOut = checkoutRepository.findByUserId();

            if(getCheckOut.isEmpty()){
                return new ResponseObject("EMPTY",new ArrayList<>());
            }
            
            List<CheckouttedProduct> mappedProduct = new ArrayList<>();
            for(Checkout ch : getCheckOut){
                Optional<Product> getProduct = productRepository.findById(ch.getProductId());
                if(!getProduct.isPresent()){
                    return new ResponseObject("ERROR_GET_PRODUCT", new ArrayList<>());
                }

                Product product = getProduct.get();
                CheckouttedProduct checkouttedProduct = new CheckouttedProduct();
                checkouttedProduct.setId(product.getId());
                checkouttedProduct.setName(product.getName());
                mappedProduct.add(checkouttedProduct);
            }

            CheckoutResponse checkoutResponse = new CheckoutResponse();
            
            checkoutResponse.setListProduct(mappedProduct);
            List<CheckoutResponse> successResponses = new ArrayList<>();
            successResponses.add(checkoutResponse);

            return new ResponseObject("SUCCESS", successResponses);
        } 

        public ResponseObject addCheckOut(List<AddCheckout> addedProduct){
            try{

                if(addedProduct.isEmpty()) return new ResponseObject("ERROR_ADD_CHECKOUT", new ArrayList<>()); 
                
                List<Checkout> checkouts = new ArrayList<>();
                for(AddCheckout addCheckout : addedProduct){

                    Checkout checkout = new Checkout();
                    checkout.setProductId(addCheckout.getProductId());
                    checkout.setQty(addCheckout.getQty());
                    checkouts.add(checkout);
                }
                checkoutRepository.saveAll(checkouts);
                
                return new ResponseObject("SUCCESS", "ADD CHECKOUT SUCCESSFULLY");
            }catch(Exception e){
                return new ResponseObject("ERROR_ADD_CHECKOUT", e.getMessage());
            }
        }
}
