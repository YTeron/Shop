package org.example.server.test;

import com.github.cliftonlabs.json_simple.JsonObject;
import org.example.server.system.Discountable;
import org.example.server.system.Product;


public class Electronics extends Product implements Discountable {
    private int warrantyMonths;

    public Electronics(String code, String name, double price, int quantity, int warrantyMonths) {
        super(code, name, price, quantity);
        this.warrantyMonths = warrantyMonths;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    @Override
    public double applyDiscount(double percent) {
        return percent;
    }

    @Override
    public String toString() {
        return String.format("Электроника: %s (%s), Цена: %.2f, Кол-во: %d, гарантия %d", code, name, price, quantity,warrantyMonths);
    }

    @Override
    public String getInfo() {
        return "";
    }
    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.put("warrantyMonths", this.warrantyMonths);
        return json;
    }

}

