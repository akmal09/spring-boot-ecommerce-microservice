package com.project.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.project.modelpsql.Checkout;

public interface CheckoutRepository extends CrudRepository<Checkout, Long> {
    List<Checkout> findByUserId();
}
