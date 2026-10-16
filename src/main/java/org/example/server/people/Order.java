package org.example.server.people;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import org.example.server.system.Product;

import java.util.LinkedHashMap;
import java.util.Map;

public class Order {
    private String orderId;
    private Customer customer;
    private Map<Product, Integer> items;
    private String status;

    public enum Status{NEW, PROCESSED, CANCELLED}
    public Order(String orderId, Customer customer,Map<Product, Integer> items,Status status){
        this.orderId = orderId;
        this.customer = customer;
        this.items = new LinkedHashMap<>(items);
        switch (status){
            case NEW -> this.status ="NEW";
            case PROCESSED -> this.status ="PROCESSED";
            case CANCELLED -> this.status ="CANCELLED";
        }
    }
    public void setStatus(String status) { this.status = status; }
    public String getOrderId() { return orderId; }
    public Customer getCustomer() { return customer; }
    public Map<Product, Integer> getItems() { return items; }
    public String getStatus() { return status; }
    @Override
    public String toString() {
        return String.format("ID заказа: %s, почта %s, кол-во %d, статус %s", orderId,customer.getEmail(),items.size(),status);
    }
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("code", this.orderId);
        json.put("customer", this.customer.toJson());
        JsonArray itemsArray = new JsonArray();
        for (Map.Entry<Product, Integer> entry : this.items.entrySet()) {
            JsonObject itemEntry = new JsonObject();
            itemEntry.put("product", entry.getKey().toJson());
            itemEntry.put("quantity", entry.getValue());
            itemsArray.add(itemEntry);
        }
        json.put("items", itemsArray);
        json.put("status", this.status);

        return json;
    }

}

