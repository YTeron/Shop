package org.example.server.people;

import org.example.server.system.Product;

import java.util.Map;

public class Order {
    private String orderId;
    private Customer customer;
    private Map<Product, Integer> items; // товар -> количество
    private String status; // NEW, PROCESSED, CANCELLED


}

