package org.example.server.people;

import java.util.Objects;

public class Customer {
    private String name;
    private String email;
    private int cancelledOrders;

    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
    }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getCancelledOrders() { return cancelledOrders; }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Customer customer) {
            return  Objects.equals(email, customer.email);
        }
        return false;
    }
    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
    @Override
    public String toString() {
        return String.format("Имя: %s, почта %s, orders %d", name,email,cancelledOrders);
    }
    public void incrementCancelledOrders() {
        this.cancelledOrders++;
    }
}
