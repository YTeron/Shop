package org.example.server;

import com.github.cliftonlabs.json_simple.*;
import org.example.server.system.Product;
import org.example.server.test.Clothing;
import org.example.server.test.Electronics;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;


import static javax.swing.UIManager.getInt;
import static javax.swing.UIManager.getString;

public class Loger {
    public static void jsonWriter(List<Product> items){
        Map<String, JsonArray> categories = new HashMap<>();
        int count = 0;
        for (Product p: items){
            if (p != null) {
                count++;
                System.out.println("Найден: " + p.getClass().getName());
                String className = p.getClass().getSimpleName();
                JsonObject itemJson = p.toJson();
                categories
                        .computeIfAbsent(className, k -> new JsonArray())
                        .add(itemJson);
            }
        }
        JsonObject rootJson = new JsonObject();
        for (Map.Entry<String, JsonArray> entry : categories.entrySet()) {
            rootJson.put(entry.getKey(), entry.getValue());
        }
        System.out.println("Всего найдено реализаций: " + count);
        jsonInit(rootJson);
    }
    public static List<Product> jsonReader(){
        List<Product> products =new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        File file = new File("products.json");
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)){
                JsonObject rootJson = (JsonObject) Jsoner.deserialize(reader);
                for (String categoryKey : rootJson.keySet()) {
                    Object value = rootJson.get(categoryKey);
                    if (value instanceof JsonArray jsonArray) {
                        for (Object itemObj : jsonArray) {
                            if (itemObj instanceof JsonObject itemJson) {
                                String code = getString(itemJson.get("code"));
                                String name = getString(itemJson.get("name"));
                                double price = getDouble(itemJson.get("price"));
                                int quantity = getInt(itemJson.get("quantity"));
                                Product product = createProductByCategory(categoryKey, code, name, price, quantity);
                                for (Map.Entry<String, Object> entry : itemJson.entrySet()) {
                                    String propKey = entry.getKey();

                                    if (!isBaseField(propKey)) {
                                        product.addProperty(propKey, entry.getValue());
                                    }
                                    products.add(product);
                                }
                            }
                        }
                    }
                }

            }
            catch (IOException | JsonException e){

            }

        } else {
            System.out.println("Файл не найден");
        }
        return products;
    }
    private static boolean isBaseField(String key) {
        return key.equals("code") || key.equals("name") || key.equals("price") || key.equals("quantity");
    }
    private static Product createProductByCategory(String categoryKey, String code, String name, double price, int quantity) {
        return switch (categoryKey) {
            case "Clothing" -> new Clothing(code, name, price, quantity, "DEFAULT");
            case "Electronics" -> new Electronics(code, name, price, quantity, 0);
            default -> throw new IllegalStateException("Unexpected value: " + categoryKey);
        };
    }

    public static void jsonRemove(List<Product> items){

    }
    private static void jsonInit(JsonObject items){
        File file = new File("products.json");
        try {
            if (!file.exists() && file.createNewFile()) {
                System.out.println("Файл products.json создан.");
            }
            String rawJson = Jsoner.serialize(items);
            String prettyJson = Jsoner.prettyPrint(rawJson);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(prettyJson);
                System.out.println("Данные успешно записаны в файл!");
            }
        } catch (IOException e) {
            System.err.println("Ошибка при работе с файлом: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private static String getString(Object val) {
        return val != null ? val.toString() : "";
    }

    private static double getDouble(Object val) {
        if (val instanceof BigDecimal b) return b.doubleValue();
        if (val instanceof Number n) return n.doubleValue();
        return 0.0;
    }

    private static int getInt(Object val) {
        if (val instanceof BigDecimal b) return b.intValue();
        if (val instanceof Number n) return n.intValue();
        return 0;
    }
}
