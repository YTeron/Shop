package org.example.server.test;

import org.example.server.system.Discountable;
import org.example.server.system.Product;


public class Electronics extends Product implements Discountable {
    private int warrantyMonths;

    public Electronics(String code, String name, double price, int quantity, int warrantyMonths) {
        super(code, name, price, quantity);
        this.warrantyMonths = warrantyMonths;
    }

    @Override
    public double applyDiscount(double percent) {
        return 0;
    }

//    @Override
//    public String toString() {
//        return String.format("Электроника: %s (%s), Цена: %.2f, Кол-во: %d", code, name, price, quantity);
//    }

    @Override
    public String getInfo() {
        return "";
    }

}

