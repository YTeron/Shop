package org.example.server.system;

import com.github.cliftonlabs.json_simple.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class Product {
    protected String code;
    protected String name;
    protected double price;
    private int quantity;

    public Product(String code, String name, double price,int quantity){
        this.code = code;
        this.name = name;
        this.price = price>=0? price:0;
        this.quantity = quantity>=0? quantity:0;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("code", this.code);
        json.put("name", this.name);
        json.put("price", this.price);
        json.put("quantity", this.quantity);
        return json;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
                sb.append(String.format("предмет: %s (%s), Цена: %.2f, Кол-во: %d",
                        code, name, price, quantity));

        return sb.toString().trim();
    }
    public abstract String getInfo();
    public String getCode() { return code; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
