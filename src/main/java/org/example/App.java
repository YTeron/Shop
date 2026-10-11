package org.example;

import org.example.server.Loger;
import org.example.server.system.Product;
import org.example.server.test.Clothing;
import org.example.server.test.Electronics;

import java.util.ArrayList;
import java.util.List;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        List<Product> products = new ArrayList<>();
        products.add(new Clothing("123", "Футболка", 100.0, 20, "M"));
        products.add(new Clothing("12333", "Майка", 1000.0, 10, "X"));
        products.add(new Electronics("12333", "Телефон", 1000.0, 10, 2000));
        Product clothing = new Clothing("1234555", "Футболка", 100.0, 20, "XL")
                .addProperty("color", "Красный")
                .addProperty("material", "Хлопок")
                .addProperty("ширина", 20)
                .addProperty("discountAvailable", true);
        products.add(clothing);
        Loger.jsonWriter(products);
        List<Product> products1 = Loger.jsonReader();
        for (Product p : products) {
            System.out.println(p);
        }
    }
}
