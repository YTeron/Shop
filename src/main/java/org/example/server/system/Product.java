package org.example.server.system;

import com.github.cliftonlabs.json_simple.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class Product {
    protected String code;
    protected String name;
    protected double price;
    protected int quantity;
    protected Map<String, Object> extraProperties = new HashMap<>();


    public Product(String code, String name, double price,int quantity){
        this.code = code;
        this.name = name;
        this.price = price>=0? price:0;
        this.quantity = quantity>=0? quantity:0;
    }
    public Product addProperty(String key,Object value) {
        this.extraProperties.put(key, value);
        return this;
    }
    public abstract String getInfo();

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("code", this.code);
        json.put("name", this.name);
        json.put("price", this.price);
        json.put("quantity", this.quantity);
        for (Map.Entry<String, Object> entry : extraProperties.entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }
        return json;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
                sb.append(String.format("предмет: %s (%s), Цена: %.2f, Кол-во: %d",
                        code, name, price, quantity));

        if (extraProperties != null && !extraProperties.isEmpty()) {
            sb.append(" | Доп. свойства: ");
            for (Map.Entry<String, Object> entry : extraProperties.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(" ");
            }
        }

        return sb.toString().trim();
    }
}
