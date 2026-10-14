package org.example.server.test;

import com.github.cliftonlabs.json_simple.JsonObject;
import org.example.server.system.Discountable;
import org.example.server.system.Product;

public class Clothing extends Product implements Discountable {
    private String size;

    public Clothing(String code, String name, double price, int quantity, String size) {
        super(code, name, price, quantity);
        this.size =size;
    }

    @Override
    public double applyDiscount(double percent) {
        return percent;
    }

    @Override
    public String getInfo() {
        return "";
    }

    public Object getSize() {
        return size;
    }

    @Override
    public String toString() {
        return String.format("Одежда: %s (%s), Цена: %.2f, Кол-во: %d, размер %s", code, name, price, quantity, size);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.put("size", this.size);
        return json;
    }
}
