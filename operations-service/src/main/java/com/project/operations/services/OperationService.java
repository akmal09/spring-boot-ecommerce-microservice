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

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OperationService {

    private final ProductRepository productRepository;
    private final CheckoutRepository checkoutRepository;
    private final Tracer tracer;

    public OperationService(ProductRepository productRepository, CheckoutRepository checkoutRepository, Tracer tracer){
        this.productRepository = productRepository;
        this.checkoutRepository = checkoutRepository;
        this.tracer = tracer;
    }

    public ResponseObject getProducts(){
        Span span = tracer.spanBuilder("operation-service.get-products")
                .setAttribute("service", "OperationService")
                .setAttribute("method", "getProducts")
                .startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            log.debug("Fetching all products from database");
            span.addEvent("Querying products from database");
            
            List<Product> products = productRepository.findAll();
            
            span.setAttribute("products.count", products.size());
            
            if(products.isEmpty()){
                span.addEvent("No products found");
                log.info("No products found in database");
                return new ResponseObject("EMPTY", new ArrayList<>());
            }
            
            span.addEvent("Products retrieved successfully");
            log.info("Retrieved {} products", products.size());
            return new ResponseObject("SUCCESS", products);
            
        } catch(Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            log.error("Error fetching products", e);
            throw e;
        } finally {
            span.end();
        }
    }

    public ResponseObject getCheckOut(){
        Span span = tracer.spanBuilder("operation-service.get-checkout")
                .setAttribute("service", "OperationService")
                .setAttribute("method", "getCheckOut")
                .startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            log.debug("Fetching checkout items for user");
            span.addEvent("Querying checkout items from database");
            
            List<Checkout> getCheckOut = checkoutRepository.findByUserId();
            
            span.setAttribute("checkout.items.count", getCheckOut.size());
            
            if(getCheckOut.isEmpty()){
                span.addEvent("No checkout items found");
                log.info("No checkout items found for user");
                return new ResponseObject("EMPTY", new ArrayList<>());
            }
            
            span.addEvent("Processing checkout items");
            List<CheckouttedProduct> mappedProduct = new ArrayList<>();
            
            for(Checkout ch : getCheckOut){
                Span productSpan = tracer.spanBuilder("get-product-details")
                        .setAttribute("product.id", ch.getProductId())
                        .startSpan();
                
                try (Scope productScope = productSpan.makeCurrent()) {
                    Optional<Product> getProduct = productRepository.findById(ch.getProductId());
                    
                    if(!getProduct.isPresent()){
                        productSpan.setAttribute("error", true);
                        productSpan.addEvent("Product not found");
                        log.error("Product not found for ID: {}", ch.getProductId());
                        return new ResponseObject("ERROR_GET_PRODUCT", new ArrayList<>());
                    }

                    Product product = getProduct.get();
                    CheckouttedProduct checkouttedProduct = new CheckouttedProduct();
                    checkouttedProduct.setId(product.getId());
                    checkouttedProduct.setName(product.getName());
                    mappedProduct.add(checkouttedProduct);
                    
                    productSpan.addEvent("Product details retrieved");
                } finally {
                    productSpan.end();
                }
            }

            CheckoutResponse checkoutResponse = new CheckoutResponse();
            checkoutResponse.setListProduct(mappedProduct);
            List<CheckoutResponse> successResponses = new ArrayList<>();
            successResponses.add(checkoutResponse);

            span.addEvent("Checkout processing completed");
            log.info("Successfully processed {} checkout items", getCheckOut.size());
            return new ResponseObject("SUCCESS", successResponses);
            
        } catch(Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            log.error("Error processing checkout items", e);
            throw e;
        } finally {
            span.end();
        }
    } 

    public ResponseObject addCheckOut(List<AddCheckout> addedProduct){
        Span span = tracer.spanBuilder("operation-service.add-checkout")
                .setAttribute("service", "OperationService")
                .setAttribute("method", "addCheckOut")
                .setAttribute("products.count", addedProduct.size())
                .startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            log.info("Adding {} products to checkout", addedProduct.size());
            span.addEvent("Starting checkout addition process");

            if(addedProduct.isEmpty()) {
                span.setAttribute("error", true);
                span.addEvent("Empty product list provided");
                log.warn("Attempted to add empty product list to checkout");
                return new ResponseObject("ERROR_ADD_CHECKOUT", new ArrayList<>()); 
            }
            
            span.addEvent("Converting products to checkout entities");
            List<Checkout> checkouts = new ArrayList<>();
            
            for(AddCheckout addCheckout : addedProduct){
                Checkout checkout = new Checkout();
                checkout.setProductId(addCheckout.getProductId());
                checkout.setQty(addCheckout.getQty());
                checkouts.add(checkout);
            }
            
            span.addEvent("Saving checkout items to database");
            checkoutRepository.saveAll(checkouts);
            
            span.addEvent("Checkout items saved successfully");
            log.info("Successfully added {} items to checkout", addedProduct.size());
            return new ResponseObject("SUCCESS", "ADD CHECKOUT SUCCESSFULLY");
            
        } catch(Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            log.error("Error adding checkout items", e);
            return new ResponseObject("ERROR_ADD_CHECKOUT", e.getMessage());
        } finally {
            span.end();
        }
    }
}
