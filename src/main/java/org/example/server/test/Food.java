package org.example.server.test;

import com.github.cliftonlabs.json_simple.JsonObject;
import org.example.server.system.Product;

public class Food extends Product {
    private int shelfLifeDays;
    public Food(String code, String name, double price, int quantity,int shelfLifeDays) {
        super(code, name, price, quantity);
        this.shelfLifeDays =shelfLifeDays;
    }

    public int getShelfLifeDays() {
        return shelfLifeDays;
    }

    @Override
    public String getInfo() {
        return "";
    }
    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.put("shelfLifeDays", this.shelfLifeDays);
        return json;
    }
    @Override
    public String toString() {
        return String.format("Одежда: %s (%s), Цена: %.2f, Кол-во: %d, время жизни %d", code, name, price, quantity, shelfLifeDays);
    }
}
